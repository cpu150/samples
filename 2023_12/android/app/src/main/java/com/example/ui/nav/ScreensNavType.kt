package com.example.ui.nav

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.example.domain.model.User
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.reflect.typeOf

@Serializable
object UserListScreen

@Serializable
data class UserDetailsScreen(val user: User) {

    companion object {
        fun getTypeMap(json: Json = Json) =
            mapOf(typeOf<User>() to serializableType<User>(json = json))

        fun from(savedStateHandle: SavedStateHandle, json: Json = Json) =
            savedStateHandle.toRoute<UserDetailsScreen>(typeMap = getTypeMap(json))
    }
}
