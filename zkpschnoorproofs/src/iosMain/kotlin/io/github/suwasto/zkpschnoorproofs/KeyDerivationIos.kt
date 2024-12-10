package io.github.suwasto.zkpschnoorproofs

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.usePinned
import platform.CoreCrypto.CCKeyDerivationPBKDF
import platform.CoreCrypto.CC_SHA256
import platform.CoreCrypto.CC_SHA256_DIGEST_LENGTH
import platform.CoreCrypto.kCCPRFHmacAlgSHA256
import kotlin.math.abs


class KeyDerivationIos : KeyDerivation {


    @OptIn(ExperimentalForeignApi::class)
    override fun hashSHA256(username: String, password: String): ByteArray {
        val salt = (password + username).encodeToByteArray()
        val passwordBytes = (username + password).encodeToByteArray()

        val keyLength = 32 // 256 bits
        val derivedKey = ByteArray(keyLength)

        derivedKey.usePinned { pinnedKey ->
            salt.usePinned { pinnedSalt ->
                passwordBytes.usePinned { pinnedPassword ->
                    // Calling the actual CCKeyDerivationPBKDF function
                    val result = CCKeyDerivationPBKDF(
                        kCCPRFHmacAlgSHA256.convert(),
                        passwordBytes.decodeToString(),
                        passwordBytes.size.convert(),
                        pinnedSalt.addressOf(0).reinterpret(),
                        salt.size.convert(),
                        kCCPRFHmacAlgSHA256.convert(),
                        10000u.convert(),
                        pinnedKey.addressOf(0).reinterpret(),
                        keyLength.convert()
                    )

                    if (result != 0) {
                        throw IllegalStateException("PBKDF2 key derivation failed with error code: $result")
                    }

                }
            }
        }

        return derivedKey
    }

    @OptIn(ExperimentalForeignApi::class)
    override fun hashSHA256(data: String): ByteArray {
        val input = data.encodeToByteArray()
        val hash = ByteArray(CC_SHA256_DIGEST_LENGTH)

        memScoped {
            input.usePinned { pinnedData ->
                hash.usePinned { pinnedHash ->
                    CC_SHA256(pinnedData.addressOf(0), input.size.toULong().convert(), pinnedHash.addressOf(0).reinterpret())
                }
            }
        }

        return hash
    }
}