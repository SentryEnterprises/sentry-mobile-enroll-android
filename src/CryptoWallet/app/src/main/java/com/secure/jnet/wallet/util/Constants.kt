@file:Suppress("KotlinConstantConditions")

package com.secure.jnet.wallet.util

import com.secure.jnet.wallet.BuildConfig

// delay for splash screen
const val START_DELAY = 1000L

// skip nfc flow, so app can be run on emulator
const val WORK_WITHOUT_CARD = false

// use testnet for testing
var TESTNET: Boolean = BuildConfig.TESTNET

// work only with biometric cards
const val BIOMETRIC_MODE = true

// static pin for biometric cards
// fix, do we need this PIN anymore
const val PIN = "111111"
const val PIN_BIOMETRIC = "111111"

const val DEFAULT_WORDS_COUNT = 24
