package io.github.suwasto.zkpschnoorproofs

interface KeyDerivation {
    fun hashSHA256(data: String): ByteArray
}