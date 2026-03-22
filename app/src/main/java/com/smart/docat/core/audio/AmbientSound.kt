package com.smart.docat.core.audio

import androidx.annotation.RawRes
import androidx.annotation.StringRes
import com.smart.docat.R

enum class AmbientSound(
    @RawRes val resId: Int,
    @StringRes val nameResId: Int
) {
    LLUVIA(R.raw.sonido_ambiental_1, R.string.sound_lluvia),
    RIO(R.raw.sonido_ambiental_2, R.string.sound_rio),
    BOSQUE(R.raw.sonido_ambiental_3, R.string.sound_bosque),
    CAFE(R.raw.sonido_ambiental_4, R.string.sound_cafe),
    OLAS(R.raw.sonido_ambiental_5, R.string.sound_olas),
    VIENTO(R.raw.sonido_ambiental_6, R.string.sound_viento),
    FUEGO(R.raw.sonido_ambiental_7, R.string.sound_fuego),
    NOCHE(R.raw.sonido_ambiental_8, R.string.sound_noche),
    CIUDAD(R.raw.sonido_ambiental_9, R.string.sound_ciudad),
    PAJAROS(R.raw.sonido_ambiental_10, R.string.sound_pajaros)
}
