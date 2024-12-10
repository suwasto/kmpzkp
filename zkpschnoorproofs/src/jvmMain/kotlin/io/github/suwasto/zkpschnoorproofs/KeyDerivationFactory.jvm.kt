package io.github.suwasto.zkpschnoorproofs

actual object KeyDerivationFactory {
    actual fun create(): KeyDerivation = object : KeyDerivation {
        override fun hashSHA256(username: String, password: String): ByteArray {
            return ByteArray(0)
        }

        override fun hashSHA256(data: String): ByteArray {
            return ByteArray(0)
        }
    }
}