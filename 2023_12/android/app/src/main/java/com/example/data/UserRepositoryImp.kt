package com.example.data

import com.example.data.api.randomuser.RandomUserApi
import com.example.data.api.randomuser.RandomUserMapper
import com.example.data.api.randomuser.model.ErrorRandomUserDTO
import com.example.data.storage.user.UserDAO
import com.example.data.storage.user.map
import com.example.domain.Logger
import com.example.domain.di.AppDispatchers
import com.example.domain.di.Dispatcher
import com.example.domain.model.User
import com.example.domain.state.LocalRequestState
import com.example.domain.state.RemoteRequestState
import com.example.domain.state.RemoteRequestState.ReasonCode.API_REQUEST
import com.example.domain.state.RemoteRequestState.ReasonCode.BODY_NULL
import com.example.domain.state.RemoteRequestState.ReasonCode.ERROR_BODY_DESERIALIZATION
import com.example.domain.state.RemoteRequestState.ReasonCode.ERROR_BODY_NULL
import com.example.domain.user.UserRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response
import javax.inject.Inject

class UserRepositoryImp @Inject constructor(
    private val randomUserApi: RandomUserApi,
    private val userDAO: UserDAO,
    private val randomUserMapper: RandomUserMapper,
    private val json: Json,
    private val logger: Logger?,
    @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : UserRepository {

    override suspend fun fetchRemoteRandomUsers(numberOfUser: Int) = withContext(ioDispatcher) {
        try {
            processResponse(randomUserApi.getRandomUsers(numberOfUser))
            { dto -> randomUserMapper.map(dto, logger) }
        } catch (e: HttpException) {
            handleHttpError(e)
        } catch (e: Exception) {
            ensureActive()
            RemoteRequestState.Error(reason = API_REQUEST, ex = e)
        }
    }

    override suspend fun saveLocalUser(user: User) = withContext(ioDispatcher) {
        user.run { userDAO.get(title.entityValue, firstName, lastName) == null }.let { notExists ->
            try {
                userDAO.add(user.map())

                if (notExists) {
                    LocalRequestState.Create(user)
                } else {
                    LocalRequestState.Update(user)
                }
            } catch (e: Exception) {
                ensureActive()
                var debugMsg = "UserRepositoryImp - saveLocalUser - Error "
                if (notExists) {
                    debugMsg += "CREATING"
                    LocalRequestState.ErrorCreate(user, e)
                } else {
                    debugMsg += "INSERTING"
                    LocalRequestState.ErrorUpdate(user, e)
                }.also { logger?.e("$debugMsg $user", e) }
            }
        }
    }

    override suspend fun deleteLocalUser(user: User): LocalRequestState<User> = withContext(ioDispatcher) {
        user.run { userDAO.get(title.entityValue, firstName, lastName) == null }.let { notExists ->
            val debugMsg = "UserRepositoryImp - deleteLocalUser - Error DELETING"
            try {
                if (notExists) {
                    logger?.e("$debugMsg no user saved: $user")
                    LocalRequestState.ErrorDelete(user)
                } else {
                    val nbUserDeleted = userDAO.delete(user.map())
                    if (nbUserDeleted == 1) {
                        LocalRequestState.Delete(user)
                    } else {
                        logger?.e("$debugMsg wrong number of user deleted ($nbUserDeleted) $user")
                        LocalRequestState.ErrorDelete(user)
                    }
                }
            } catch (e: Exception) {
                ensureActive()
                logger?.e("$debugMsg $user", e)
                LocalRequestState.ErrorDelete(user, e)
            }
        }
    }

    override suspend fun getLocalUsers(logger: Logger?) = withContext(ioDispatcher) {
        try {
            userDAO.getAll().map { userEntities ->
                val users =
                    userEntities?.map { userEntity -> userEntity.map(logger) } ?: emptyList()
                LocalRequestState.Read(users)
            }
        } catch (e: Exception) {
            ensureActive()
            logger?.e("UserRepositoryImp - getLocalUsers - Error", e)
            flowOf(LocalRequestState.ErrorRead(e))
        }
    }

    private fun <T, U> processResponse(
        response: Response<T>,
        map: (dto: T) -> RemoteRequestState<U>,
    ) = response.run {
        if (isSuccessful) {
            body()?.let { dto -> map(dto) }
                ?: RemoteRequestState.Error(reason = BODY_NULL, httpCode = code())
        } else {
            errorBody()?.let { errorBody -> deserializedErrorBody(code(), errorBody) }
                ?: RemoteRequestState.Error(reason = ERROR_BODY_NULL, httpCode = code())
        }
    }

    private fun deserializedErrorBody(code: Int, errorBody: ResponseBody) = try {
        json.decodeFromString<ErrorRandomUserDTO>(errorBody.string())
            .run { RemoteRequestState.Error(httpCode = code, msg = error) }
    } catch (e: SerializationException) { // in case of any decoding-specific error
        RemoteRequestState.Error(reason = ERROR_BODY_DESERIALIZATION, httpCode = code, ex = e)
    } catch (e: IllegalArgumentException) { // if the decoded input is not a valid instance of T
        RemoteRequestState.Error(reason = ERROR_BODY_DESERIALIZATION, httpCode = code, ex = e)
    }

    private fun handleHttpError(e: HttpException) = RemoteRequestState.Error(
        reason = API_REQUEST,
        httpCode = e.code(),
        msg = e.message(),
        ex = e,
    )
}
