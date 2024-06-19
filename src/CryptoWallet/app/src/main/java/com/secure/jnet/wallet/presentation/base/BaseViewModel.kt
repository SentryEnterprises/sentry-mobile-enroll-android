//package com.secure.jnet.wallet.presentation.base
//
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.ViewModel
//import com.secure.jnet.wallet.util.SingleLiveEvent
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.SupervisorJob
//
//@Suppress("PropertyName")
//abstract class BaseViewModel : ViewModel() {
//
//    private var supervisorJob = SupervisorJob()
//        get() {
//            if (field.isCancelled) field = SupervisorJob()
//            return field
//        }
//
//    private val coroutineContext = Dispatchers.Main + supervisorJob
//
//    protected val viewModelScope = CoroutineScope(coroutineContext)
//
//    protected val _showProgress = SingleLiveEvent<Boolean>()
//    val showProgress: LiveData<Boolean> get() = _showProgress
//
//    protected val _showError = SingleLiveEvent<String>()
//    val showError: LiveData<String> get() = _showError
//}