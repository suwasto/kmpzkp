package io.github.suwasto.zkpsample

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform