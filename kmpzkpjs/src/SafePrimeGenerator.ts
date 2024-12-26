import bigInt, { BigInteger } from "big-integer";

export class SafePrimeGenerator {
  static PRIME_2048: BigInteger = bigInt(
    "FFFFFFFFFFFFFFFFC90FDAA22168C234C4C6628B80DC1CD129024E088A67CC74020BBEA63B139B22514A08798E3404DDEF9519B3CD3A431B302B0A6DF25F14374FE1356D6D51C245E485B576625E7EC6F44C42E9A63A36210000000000090563",
    16
  );

  static GENERATOR: BigInteger = bigInt(2);

  static generateRandomNonce(): BigInteger {
    const lowerBound = bigInt(1);
    const upperBound = SafePrimeGenerator.PRIME_2048.minus(1);

    let nonce: BigInteger;
    do {
      // Generate a random BigInteger in the range [1, PRIME_2048 - 1]
      nonce = bigInt.randBetween(lowerBound, upperBound);
    } while (nonce.lt(lowerBound) || nonce.geq(upperBound));

    return nonce;
  }
}
