package com.gas.flexapp.local

interface CipherService {
    fun encrypt(data: String): String

    fun decrypt(data: String): String
}