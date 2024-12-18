import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.BigInteger.Companion.ONE
import com.ionspin.kotlin.bignum.integer.Sign
import com.ionspin.kotlin.bignum.integer.toBigInteger
import kotlin.random.Random

object SafePrimeGenerator {

    val PRIME_2048 =
        "FFFFFFFFFFFFFFFFC90FDAA22168C234C4C6628B80DC1CD129024E088A67CC74020BBEA63B139B22514A08798E3404DDEF9519B3CD3A431B302B0A6DF25F14374FE1356D6D51C245E485B576625E7EC6F44C42E9A63A36210000000000090563".toBigInteger(16)
    val GENERATOR = BigInteger.TWO

    fun generateRandomNonce(): BigInteger {
        // Ensure the range is from 1 to prime - 1
        val lowerBound = ONE
        val upperBound = SafePrimeGenerator.PRIME_2048 - ONE

        var nonce: BigInteger
        do {
            // Generate a random byte array
            val randomBytes = ByteArray(upperBound.toByteArray().size)
            Random.nextBytes(randomBytes)

            // Convert the random bytes to a BigInteger
            nonce = BigInteger.fromByteArray(randomBytes, Sign.POSITIVE)

            // Ensure nonce is within the desired range
            nonce = nonce.mod(upperBound - lowerBound) + lowerBound
        } while (nonce < lowerBound || nonce >= upperBound)

        return nonce
    }

}