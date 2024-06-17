package com.secure.jnet.jcwkit.models

enum class AccountStatus(val value: Int) {
    NO_INIT_ACC (0x00),
    INIT_ACC (0x01),
    BLK_INIT_ACC (0x11),
    ACTIVE_ACC (0x03),
    INACTIVE_ACC (0x13);

    companion object {
        fun getByValue(value: Int) = entries.firstOrNull { it.value == value }
    }
}