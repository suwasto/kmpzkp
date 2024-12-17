package io.github.suwasto.zkpschnoorproofs

import SafePrimeGenerator
import SafePrimeGenerator.generateRandomNonce
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.Sign
import com.ionspin.kotlin.bignum.integer.toBigInteger

object SchnorrServer {

    data class Keys(val privateKey: Int, val publicKey: Int)

    // Generate the challenge on the server
    fun generateChallenge(username: String): String {
        val keyDerivation = KeyDerivationFactory.create()
        val randomNonce = generateRandomNonce()
        val hash = keyDerivation.hashSHA256("$username$randomNonce")
        return BigInteger.fromByteArray(hash, Sign.POSITIVE).mod(SafePrimeGenerator.PRIME_2048).toString()
    }

    // Verify the client's response
    fun verifyProof(
        commitment: String,
        publicKey: String,
        challenge: String,
        response: String
    ): Boolean {
        val commitmentBigInteger = commitment.toBigInteger()
        val publicKeyBigInteger = publicKey.toBigInteger()
        val challengeBigInteger = challenge.toBigInteger()
        val responseBigInteger = response.toBigInteger()
        val lhs = ZKPUtils.modularExponentiation(SafePrimeGenerator.GENERATOR, responseBigInteger, SafePrimeGenerator.PRIME_2048)
        val rhs = (commitmentBigInteger * ZKPUtils.modularExponentiation(publicKeyBigInteger, challengeBigInteger, SafePrimeGenerator.PRIME_2048)) % SafePrimeGenerator.PRIME_2048
        return lhs == rhs
    }

}