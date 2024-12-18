package io.github.suwasto.zkpschnorrproofs

import SafePrimeGenerator
import SafePrimeGenerator.generateRandomNonce
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.Sign
import com.ionspin.kotlin.bignum.integer.toBigInteger

object SchnorrMobileClient {

    data class Keys(val privateKey: String, val publicKey: String)

    /**
     * Derive keys using platform-specific key derivation.
     */
    fun deriveKeys(username: String, password: String): Keys {
        val privateKey = derivePrivateKey(username, password)
        val publicKey = derivePublicKey(privateKey)
        return Keys(privateKey.toString(), publicKey.toString())
    }

    private fun derivePrivateKey(username: String, password: String): BigInteger {
        val keyDerivation = KeyDerivationFactory.create()
        val hash = keyDerivation.hashSHA256("$username$password")
        return BigInteger.fromByteArray(hash, Sign.POSITIVE).mod(SafePrimeGenerator.PRIME_2048)
    }

    private fun derivePublicKey(privateKey: BigInteger): BigInteger {
        return ZKPUtils.modularExponentiation(SafePrimeGenerator.GENERATOR, privateKey, SafePrimeGenerator.PRIME_2048)
    }

    /**
     * Generate a proof (nonce and commitment) for authentication.
     */
    fun generateProof(privateKey: String, challenge: String): Pair<String, String> {
        val privateKeyBigInteger = privateKey.toBigInteger()
        val challengeBigInteger = challenge.toBigInteger()
        // Step 1: Generate a random nonce
        val nonce = generateRandomNonce()

        // Step 2: Calculate commitment as g^r % p
        val commitment = ZKPUtils.modularExponentiation(SafePrimeGenerator.GENERATOR, nonce, SafePrimeGenerator.PRIME_2048)

        // Step 3: Calculate response as (r + privateKey * challenge) % (p - 1)
        val response = (nonce + privateKeyBigInteger * challengeBigInteger) % (SafePrimeGenerator.PRIME_2048 - 1.toBigInteger())

        // Return commitment and response as the proof
        return Pair(commitment.toString(), response.toString())
    }

}