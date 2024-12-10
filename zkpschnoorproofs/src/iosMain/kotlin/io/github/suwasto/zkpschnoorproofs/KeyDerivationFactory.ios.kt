package io.github.suwasto.zkpschnoorproofs

actual object KeyDerivationFactory {
    actual fun create(): KeyDerivation = KeyDerivationIos()
}