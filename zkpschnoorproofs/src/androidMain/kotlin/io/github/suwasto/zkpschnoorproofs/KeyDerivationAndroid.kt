package io.github.suwasto.zkpschnoorproofs

import java.security.MessageDigest
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

class KeyDerivationAndroid: KeyDerivation {
    override fun hashSHA256(data: String): ByteArray {
        val digest = MessageDigest.getInstance("SHA-256")
        return digest.digest(data.toByteArray())
    }
}