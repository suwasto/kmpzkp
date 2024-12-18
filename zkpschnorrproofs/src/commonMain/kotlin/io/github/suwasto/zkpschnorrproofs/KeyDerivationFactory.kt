package io.github.suwasto.zkpschnorrproofs

expect object KeyDerivationFactory {
    fun create(): KeyDerivation
}