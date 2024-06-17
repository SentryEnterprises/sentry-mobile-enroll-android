package com.secure.jnet.jcwkit

import java.io.IOException

class JCWIOException constructor(
    val errorCode: Int
) : IOException()