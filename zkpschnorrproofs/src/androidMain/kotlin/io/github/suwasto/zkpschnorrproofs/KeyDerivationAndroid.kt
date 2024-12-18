package io.github.suwasto.zkpschnorrproofs

import java.security.MessageDigest

class KeyDerivationAndroid: KeyDerivation {
    override fun hashSHA256(data: String): ByteArray {
        val digest = MessageDigest.getInstance("SHA-256")
        return digest.digest(data.toByteArray())
    }
}