package com.example.data.api.randomuser

import com.example.data.api.randomuser.model.ErrorRandomUserDTO
import com.example.domain.randomuser.RandomUserRepository
import com.example.domain.state.DataState
import com.example.domain.state.DataState.ReasonCode.API_REQUEST
import com.example.domain.state.DataState.ReasonCode.BODY_NULL
import com.example.domain.state.DataState.ReasonCode.ERROR_BODY_DESERIALIZATION
import com.example.domain.state.DataState.ReasonCode.ERROR_BODY_NULL
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response
import javax.inject.Inject

class RepositoryImp @Inject constructor(
    private val endpoints: Endpoints,
    private val mapper: Mapper,
    private val json: Json,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : RandomUserRepository {

    override suspend fun fetchRandomUsers(numberOfUser: Int) = withContext(ioDispatcher) {
        try {
            processResponse(endpoints.getRandomUsers(numberOfUser)) { dto -> mapper.map(dto) }
        } catch (e: HttpException) {
            handleHttpError(e)
        } catch (e: Exception) {
            DataState.Error(reason = API_REQUEST, ex = e)
        }
    }

    private fun <T, U> processResponse(
        response: Response<T>,
        map: (dto: T) -> DataState<U>,
    ) = response.run {
        if (isSuccessful) {
            body()?.let { dto -> map(dto) }
                ?: DataState.Error(reason = BODY_NULL, httpCode = code())
        } else {
            errorBody()?.let { errorBody -> deserializedErrorBody(code(), errorBody) }
                ?: DataState.Error(reason = ERROR_BODY_NULL, httpCode = code())
        }
    }

    private fun deserializedErrorBody(code: Int, errorBody: ResponseBody) = try {
        json.decodeFromString<ErrorRandomUserDTO>(errorBody.string())
            .run { DataState.Error(httpCode = code, msg = error) }
    } catch (e: Exception) {
        DataState.Error(reason = ERROR_BODY_DESERIALIZATION, httpCode = code, ex = e)
    }

    private fun handleHttpError(e: HttpException) = DataState.Error(
        reason = API_REQUEST,
        httpCode = e.code(),
        msg = e.message(),
        ex = e,
    )
}
