package com.secure.jnet.jcwkit.models

data class PublicKeyDTO(
    val key: String,
    val chainCode: String,
    val parentKey: String,
)