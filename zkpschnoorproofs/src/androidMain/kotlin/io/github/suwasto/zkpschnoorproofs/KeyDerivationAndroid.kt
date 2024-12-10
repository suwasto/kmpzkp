package io.github.suwasto.zkpschnoorproofs

import java.security.MessageDigest
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

class KeyDerivationAndroid: KeyDerivation {

    override fun hashSHA256(username: String, password: String): ByteArray {
        val combined = (username + password).toByteArray(Charsets.UTF_8)
        val salt = (password + username).toByteArray(Charsets.UTF_8)

        // Use PBKDF2 to derive a key
        val spec = PBEKeySpec(combined.map { it.toInt().toChar() }.toCharArray(), salt, 10000, 256)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val hash = factory.generateSecret(spec).encoded
        return hash
    }

    override fun hashSHA256(data: String): ByteArray {
        val digest = MessageDigest.getInstance("SHA-256")
        return digest.digest(data.toByteArray())
    }
}