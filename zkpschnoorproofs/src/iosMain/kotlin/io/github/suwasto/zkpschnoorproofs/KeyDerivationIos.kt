package io.github.suwasto.zkpschnoorproofs

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.usePinned
import platform.CoreCrypto.CC_SHA256
import platform.CoreCrypto.CC_SHA256_DIGEST_LENGTH


class KeyDerivationIos : KeyDerivation {

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