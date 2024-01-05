package com.example.data

import com.example.data.api.randomuser.RandomUserMapper
import com.example.data.api.randomuser.RandomUserService
import com.example.data.api.randomuser.model.GetRandomUsersDTO
import com.example.data.storage.user.UserDAO
import com.example.data.storage.user.model.UserEntity
import com.example.domain.user.UserRepository
import kotlinx.coroutines.flow.Flow
import org.junit.Before
import retrofit2.Response

class UserRepositoryTest {

//    @MockK
//    val movieAPI = mockk<MovieAPI>()
    private val randomUserService = object: RandomUserService {
        override suspend fun getRandomUsers(numberOfUsers: Int): Response<GetRandomUsersDTO> {
            TODO("Not yet implemented")
        }
    }
    private val userDAO = object : UserDAO {
        override fun getAll(): Flow<List<UserEntity>?> {
            TODO("Not yet implemented")
        }

        override fun get(title: String, firstName: String, lastName: String): UserEntity? {
            TODO("Not yet implemented")
        }

        override fun add(user: UserEntity): Long {
            TODO("Not yet implemented")
        }

        override fun addAll(users: List<UserEntity>): List<Long> {
            TODO("Not yet implemented")
        }

        override fun delete(user: UserEntity): Int {
            TODO("Not yet implemented")
        }

        override fun deleteAll(users: List<UserEntity>): Int {
            TODO("Not yet implemented")
        }

    }
    private val randomUserMapper = object: RandomUserMapper {

    }

    private lateinit var repository: UserRepository

    @Before
    fun setUp() {
        repository = UserRepositoryImp(
            randomUserService = randomUserService,
            userDAO = userDAO,
            randomUserMapper = ,
            json = ,
            logger = ,
            ioDispatcher = ,
        )
    }
}
