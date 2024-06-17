package com.secure.jnet.wallet.presentation.base

import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts

class BetterActivityResult<Input, Result>(
    caller: ActivityResultCaller,
    contract: ActivityResultContract<Input, Result>,
    onActivityResult: OnActivityResult<Result>?
) {
    private var launcher: ActivityResultLauncher<Input>? = null
    private var onActivityResult: OnActivityResult<Result>? = null

    init {
        this.onActivityResult = onActivityResult
        launcher =
            caller.registerForActivityResult(contract) { result -> callOnActivityResult(result) }
    }

    companion object {
        /**
         * Register activity result using a [ActivityResultContract] and an in-place activity result callback like
         * the default approach. You can still customise callback using [.launch]. Special for permissions
         */
        fun registerActivityForPermissionsResult(
            caller: ActivityResultCaller,
            onActivityResult: OnActivityResult<Map<String, Boolean>>? = null
        ): BetterActivityResult<Array<String>, Map<String, Boolean>> {
            return BetterActivityResult(
                caller,
                ActivityResultContracts.RequestMultiplePermissions(),
                onActivityResult
            )
        }

        /**
         * Register activity result using a [ActivityResultContract] and an in-place activity result callback like
         * the default approach. You can still customise callback using [.launch].
         */
        private fun <Input, Result> registerForActivityResult(
            caller: ActivityResultCaller,
            contract: ActivityResultContract<Input, Result>,
            onActivityResult: OnActivityResult<Result>?
        ): BetterActivityResult<Input, Result> {
            return BetterActivityResult(caller, contract, onActivityResult)
        }

        /**
         * Same as [.registerForActivityResult] except
         * the last argument is set to `null`.
         */
        private fun <Input, Result> registerForActivityResult(
            caller: ActivityResultCaller,
            contract: ActivityResultContract<Input, Result>
        ): BetterActivityResult<Input, Result> {
            return registerForActivityResult(caller, contract, null)
        }

        /**
         * Specialised method for launching new activities.
         */
        fun registerActivityForResult(
            caller: ActivityResultCaller
        ): BetterActivityResult<Intent, ActivityResult> {
            return registerForActivityResult(
                caller,
                ActivityResultContracts.StartActivityForResult()
            )
        }
    }

    /**
     * Callback interface
     */
    interface OnActivityResult<O> {
        /**
         * Called after receiving a result from the target activity
         */
        fun onActivityResult(result: O)
    }

    /**
     * Launch activity, same as [ActivityResultLauncher.launch] except that it allows a callback
     * executed after receiving a result from the target activity.
     */
    fun launch(input: Input, onActivityResult: OnActivityResult<Result>?) {
        if (onActivityResult != null) {
            this.onActivityResult = onActivityResult
        }
        launcher!!.launch(input)
    }

    /**
     * Same as [.launch] with last parameter set to `null`.
     */
    fun launch(input: Input) {
        launch(input, onActivityResult)
    }

    private fun callOnActivityResult(result: Result) {
        if (onActivityResult != null) onActivityResult!!.onActivityResult(result)
    }
}
