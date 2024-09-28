package com.example.domain.model

import android.content.Context
import androidx.annotation.StringRes
import com.example.domain.Logger
import com.example.example2023.R

enum class UserTitle(val entityValue: String, @StringRes val stringRes: Int) {
    UNKNOWN("UNKNOWN", R.string.user_title_unknown),
    MS("Ms", R.string.user_title_ms),
    MISS("Miss", R.string.user_title_miss),
    MR("Mr", R.string.user_title_mr),
    MRS("Mrs", R.string.user_title_mrs);

    fun getString(context: Context) = context.getString(stringRes)

    companion object {
        fun fromEntity(entityValue: String?, logger: Logger? = null) =
            entries.find { it.entityValue == entityValue } ?: let {
                logger?.e("UserTitle - Error parsing $entityValue")
                UNKNOWN
            }
    }
}
