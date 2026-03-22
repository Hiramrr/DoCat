package com.smart.docat.core.audio

import androidx.annotation.RawRes

data class AmbientSound(
    val id: Int,
    val name: String,
    @RawRes val resourceId: Int
)