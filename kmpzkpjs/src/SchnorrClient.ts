import bigInt, { BigInteger } from "big-integer";
import { SafePrimeGenerator } from "./SafePrimeGenerator";
import { ZKPUtils } from "./ZKPUtils";

export interface Keys {
  privateKey: string;
  publicKey: string;
}

export class SchnorrClient {
  /**
   * Derive keys using a username and password.
   */
  static deriveKeys(username: string, password: string): Keys {
    const privateKey = this.derivePrivateKey(username, password);
    const publicKey = this.derivePublicKey(privateKey);
    return { privateKey: privateKey.toString(), publicKey: publicKey.toString() };
  }

  private static derivePrivateKey(username: string, password: string): BigInteger {
    const hash = this.hashSHA256(username + password); // Implement or use a library for hashing
    const privateKey = bigInt(hash, 16).mod(SafePrimeGenerator.PRIME_2048);
    return privateKey;
  }

  private static derivePublicKey(privateKey: BigInteger): BigInteger {
    return ZKPUtils.modularExponentiation(
      SafePrimeGenerator.GENERATOR,
      privateKey,
      SafePrimeGenerator.PRIME_2048
    );
  }

  /**
   * Generate a proof (nonce and commitment) for authentication.
   */
  static generateProof(privateKey: string, challenge: string): [string, string] {
    const privateKeyBigInt = bigInt(privateKey);
    const challengeBigInt = bigInt(challenge);

    const nonce = SafePrimeGenerator.generateRandomNonce();

    const commitment = ZKPUtils.modularExponentiation(
      SafePrimeGenerator.GENERATOR,
      nonce,
      SafePrimeGenerator.PRIME_2048
    );

    const response = nonce
      .add(privateKeyBigInt.multiply(challengeBigInt))
      .mod(SafePrimeGenerator.PRIME_2048.minus(1));

    return [commitment.toString(), response.toString()];
  }

  private static hashSHA256(data: string): string {
    const crypto = require("crypto");
    return crypto.createHash("sha256").update(data).digest("hex");
  }
}

module.exports = SchnorrClient;
