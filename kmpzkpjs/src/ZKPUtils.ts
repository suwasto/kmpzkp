import bigInt, { BigInteger } from "big-integer";

export class ZKPUtils {
  static modularExponentiation(base: BigInteger, exponent: BigInteger, mod: BigInteger): BigInteger {
    let result = bigInt.one;
    let newBase = base.mod(mod);
    let newExponent = exponent;

    while (newExponent.greater(0)) {
      if (newExponent.mod(2).equals(1)) {
        result = result.multiply(newBase).mod(mod);
      }
      newExponent = newExponent.divide(2);
      newBase = newBase.multiply(newBase).mod(mod);
    }

    return result;
  }
}
