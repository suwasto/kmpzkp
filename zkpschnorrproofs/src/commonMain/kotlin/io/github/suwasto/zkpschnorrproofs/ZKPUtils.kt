package io.github.suwasto.zkpschnorrproofs

import com.ionspin.kotlin.bignum.integer.BigInteger

object ZKPUtils {

    internal fun modularExponentiation(base: BigInteger, exponent: BigInteger, mod: BigInteger): BigInteger {
        var result = BigInteger.ONE
        var newBase = base % mod
        var newExponent = exponent

        while (newExponent > BigInteger.ZERO) {
            if (newExponent % BigInteger.TWO == BigInteger.ONE) {
                result = (result * newBase) % mod
            }
            newExponent /= BigInteger.TWO
            newBase = (newBase * newBase) % mod
        }
        return result
    }

}