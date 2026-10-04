package com.example.data.model

enum class AppLanguage {
    BN, EN
}

object Localization {
    fun toBanglaDigits(number: String): String {
        val banglaDigits = mapOf(
            '0' to '০', '1' to '১', '2' to '২', '3' to '৩', '4' to '৪',
            '5' to '৫', '6' to '৬', '7' to '৭', '8' to '৮', '9' to '৯'
        )
        return number.map { banglaDigits[it] ?: it }.joinToString("")
    }

    fun formatPrice(amount: Double, language: AppLanguage): String {
        val intAmount = amount.toInt().toString()
        return if (language == AppLanguage.BN) {
            "৳ ${toBanglaDigits(intAmount)}"
        } else {
            "৳ $intAmount"
        }
    }

    fun formatNumber(number: Int, language: AppLanguage): String {
        val str = number.toString()
        return if (language == AppLanguage.BN) toBanglaDigits(str) else str
    }
}
