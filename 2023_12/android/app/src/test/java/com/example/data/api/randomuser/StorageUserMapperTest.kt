package com.example.data.api.randomuser

import com.example.data.api.randomuser.UserTestUtility.checkUser
import com.example.data.api.randomuser.UserTestUtility.getDomainUser
import com.example.data.api.randomuser.UserTestUtility.getMapperResult
import com.example.data.api.randomuser.UserTestUtility.getRoomUsers
import com.example.data.storage.user.RoomUserMapperImp
import com.example.data.storage.user.StorageUserMapper
import com.example.domain.model.UserGender
import com.example.domain.model.UserTitle
import org.junit.Before
import org.junit.Test

class StorageUserMapperTest {

    private lateinit var storageMapper: StorageUserMapper

    @Before
    fun setUp() {
        storageMapper = RoomUserMapperImp()
    }

    // TESTS

    @Test
    fun `GIVEN a valid User ENTITY model WHEN mapping to DOMAIN model THEN valid`() {
        val userEntities = getRoomUsers()
        val user = getMapperResult(storageMapper.map(userEntities)) { "$it: $userEntities" }

        checkUser(user?.firstOrNull())
    }

    @Test
    fun `GIVEN a valid User DOMAIN model WHEN mapping to ENTITY model THEN valid`() {
        val user = getDomainUser()
        val user1 = getDomainUser(
            title = UserTitle.MISS,
            gender = UserGender.FEMALE,
            email = null,
            birthDate = null,
            age = null,
            picLargeUrl = null,
            picMediumUrl = null,
            picSmallUrl = null,
        )
        val userEntity = storageMapper.map(user)
        val userEntity1 = storageMapper.map(user1)

        assert(user.equals(userEntity)) { "user NOT EQUALS: $user != $userEntity" }
        assert(user1.equals(userEntity1)) { "user1 NOT EQUALS: $user1 != $userEntity1" }
    }
}
