package io.github.suwasto.zkpschnoorproofs

interface KeyDerivation {
    fun hashSHA256(username: String, password: String): ByteArray
    fun hashSHA256(data: String): ByteArray
}