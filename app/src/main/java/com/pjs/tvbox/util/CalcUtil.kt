package com.pjs.tvbox.util

object CalcUtil {
    fun formatQian(
        num: Long = 0L
    ): String {
        val a =
            num / 1000
        val b =
            num % 1000
        val c =
            (b + 99) / 100
        val total =
            a * 10 + c

        if (total == 0L || (a == 0L && b <= 100)) {
            return "0.1千"
        }

        return "${total / 10}.${total % 10}千"
    }

    fun formatWan(
        n: Long = 0L
    ): String {
        val a =
            n / 10000
        val b =
            n % 10000
        val c =
            (b + 999) / 1000
        var total =
            a * 10 + c

        if (total == 0L || (a == 0L && b <= 1000)) {
            return "0.1万"
        }

        return "${total / 10}.${total % 10}万"
    }

}