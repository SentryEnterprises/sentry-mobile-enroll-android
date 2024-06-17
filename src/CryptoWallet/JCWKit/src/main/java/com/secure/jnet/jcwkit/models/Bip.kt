package com.secure.jnet.jcwkit.models

enum class Bip(val value: Int) {
    BIP_32(0),
    BIP_44(44),
    BIP_84(84);

    companion object {
        fun getByValue(value: Int) = entries.firstOrNull { it.value == value }
    }
}