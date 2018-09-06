package com.jonnycaley.cryptomanager.ui.splash

class Converter {

    fun convert(): String {

        val test = "HelloahahaahahhaMe"

        return test.replace(("hello" + ".*?" + "me").toRegex(), "It worked!")
    }
}
