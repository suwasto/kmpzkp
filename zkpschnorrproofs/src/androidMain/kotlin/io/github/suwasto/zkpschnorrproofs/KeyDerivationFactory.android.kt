package io.github.suwasto.zkpschnorrproofs

actual object KeyDerivationFactory {
    actual fun create(): KeyDerivation = KeyDerivationAndroid()
}