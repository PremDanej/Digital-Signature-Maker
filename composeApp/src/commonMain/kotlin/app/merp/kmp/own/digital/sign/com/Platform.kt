package app.merp.kmp.own.digital.sign.com

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform