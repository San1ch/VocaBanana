package com.san1ch.vocabanana.core.ui.theme

import androidx.compose.ui.graphics.Color

val BananaPrimary = Color(0xFF6D5E00)
val BananaOnPrimary = Color(0xFFF6F3ED)
val BananaContainer = Color(0xFFFFE97B)

// Dark Mode Colors (Soft & Glowing)
val BananaPrimaryDark = Color(0xFFFFE97B)
val BananaOnPrimaryDark = Color(0xFF352D00)
val BananaBackgroundDark = Color(0xFF1D1B16)
val BananaSurfaceDark = Color(0xFF25231E)

object AppColor {
    val NotKnow = Color(0xFFE35858)
    val Learn = Color(0xFFFFE97B)
    val Known = Color(0xFFA8E4AD)
    val Ignore = Color(0xFF696969)
}

object MileStoneColor {
    val Novice = Color(0xFFF53844)
    val Learner = Color(0xFFEC9F05)
    val Fluent = Color(0xFF20BF55)
    val Professional = Color(0xFF5F0A87)
    val Mastery = Color(0xFFFFD700)
}

interface ReadingColor {
    val new: Color
    val learning: Color
    val known: Color
    val notKnown: Color
    val ignored: Color
}

object LightReadingColor : ReadingColor {
    override val new = Color(0xFF173746)
    override val learning = Color(0xFF696128)
    override val known = Color(0xFF406744)
    override val notKnown = Color(0xFF752D2D)
    override val ignored = BananaBackgroundDark
}

object DarkReadingColor : ReadingColor {
    override val new = Color(0xFF58B7E3)
    override val learning = Color(0xFFFFE97B)
    override val known = Color(0xFFA8E4AD)
    override val notKnown = Color(0xFFE35858)
    override val ignored = BananaOnPrimary
}
