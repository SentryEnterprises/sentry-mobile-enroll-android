package com.secure.jnet.jcwkit.models

data class SignedHashDTO(
    val r: String,
    val s: String,
    val v: String,
)
