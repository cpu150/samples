package com.example.data

import com.example.data.api.randomuser.RandomUserMapper
import com.example.data.api.randomuser.RandomUserService
import com.example.data.api.randomuser.model.ErrorRandomUserDTO
import com.example.data.storage.user.UserDAO
import com.example.data.storage.user.map
import com.example.domain.Logger
import com.example.domain.model.User
import com.example.domain.randomuser.UserRepository
import com.example.domain.state.LocalRequestState
import com.example.domain.state.RemoteRequestState
import com.example.domain.state.RemoteRequestState.ReasonCode.API_REQUEST
import com.example.domain.state.RemoteRequestState.ReasonCode.BODY_NULL
import com.example.domain.state.RemoteRequestState.ReasonCode.ERROR_BODY_DESERIALIZATION
import com.example.domain.state.RemoteRequestState.ReasonCode.ERROR_BODY_NULL
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response
import javax.inject.Inject

class UserRepositoryImp @Inject constructor(
    private val randomUserService: RandomUserService,
    private val userDAO: UserDAO,
    private val randomUserMapper: RandomUserMapper,
    private val json: Json,
    private val logger: Logger,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : UserRepository {

    override suspend fun fetchRemoteRandomUsers(numberOfUser: Int) = withContext(ioDispatcher) {
        try {
            processResponse(randomUserService.getRandomUsers(numberOfUser))
            { dto -> randomUserMapper.map(dto) }
        } catch (e: HttpException) {
            handleHttpError(e)
        } catch (e: Exception) {
            RemoteRequestState.Error(reason = API_REQUEST, ex = e)
        }
    }

    override suspend fun saveLocalUser(user: User) = user
        .run { userDAO.getUser(title.value, firstName, lastName) == null }
        .let { created ->
            try {
                userDAO.addUser(user.map())

                if (created) {
                    LocalRequestState.Create(user)
                } else {
                    LocalRequestState.Update(user)
                }
            } catch (e: Exception) {
                var debugMsg = "UserRepositoryImp - saveLocalUser - Error "
                if (created) {
                    debugMsg += "CREATING"
                    LocalRequestState.ErrorCreate(user, e)
                } else {
                    debugMsg += "INSERTING"
                    LocalRequestState.ErrorUpdate(user, e)
                }.also { logger.e("$debugMsg $user", e) }
            }
        }

    override suspend fun getLocalUsers() = try {
        userDAO.getAllUsers().map { userEntities ->
            val users = userEntities?.map { userEntity -> userEntity.map() } ?: emptyList()
            LocalRequestState.Read(users)
        }
    } catch (e: Exception) {
        logger.e("UserRepositoryImp - getLocalUsers - Error", e)
        flowOf(LocalRequestState.ErrorRead(e))
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
    } catch (e: Exception) {
        RemoteRequestState.Error(reason = ERROR_BODY_DESERIALIZATION, httpCode = code, ex = e)
    }

    private fun handleHttpError(e: HttpException) = RemoteRequestState.Error(
        reason = API_REQUEST,
        httpCode = e.code(),
        msg = e.message(),
        ex = e,
    )
}
