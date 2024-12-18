package io.github.suwasto.zkpschnorrproofs

interface KeyDerivation {
    fun hashSHA256(data: String): ByteArray
}