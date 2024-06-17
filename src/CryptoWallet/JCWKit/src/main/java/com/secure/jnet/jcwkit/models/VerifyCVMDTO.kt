package com.secure.jnet.jcwkit.models

data class VerifyCVMDTO(
    val cvmAvailable: Boolean,
    val fingerVerified: Boolean,
    val wssm: WSSM,
)
