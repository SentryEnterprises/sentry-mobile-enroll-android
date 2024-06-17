package com.secure.jnet.wallet.presentation.base

import androidx.appcompat.app.AppCompatActivity

@Suppress("LeakingThis")
abstract class BaseActivity : AppCompatActivity() {

    val activityLauncher = BetterActivityResult.registerActivityForResult(this)

}