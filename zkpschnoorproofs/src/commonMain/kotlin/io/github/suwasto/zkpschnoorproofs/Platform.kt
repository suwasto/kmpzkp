package io.github.suwasto.zkpschnoorproofs

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform