package com.bano.futuresspreadcalculator

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class Calculator {
    fun calculateActivPriceNoDiv(activPrice: Double, dividend: Double) =
        activPrice - dividend * 0.87

    fun calculateFuture(
        activPriceNoDiv: Double, kcRate: Double, daysToExpire: Long
    ) = activPriceNoDiv * (1 + kcRate * (daysToExpire.toDouble() / 365))

    fun calculateBackFuture(
        futurePrice: Double, dividend: Double, kcRate: Double, daysToExpire: Long
    ) = (futurePrice + dividend * 0.87) / (1 + kcRate * (daysToExpire.toDouble() / 365))

    fun calculateDaysUntil(inputDate: String): Long {
        val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())

        return try {
            val targetDate: Date = dateFormat.parse(inputDate)
                ?: throw IllegalArgumentException("Неправильный формат даты")

            val currentDate = Date()
            val differenceInMillis = targetDate.time - currentDate.time
            TimeUnit.DAYS.convert(differenceInMillis, TimeUnit.MILLISECONDS) + 1

        } catch (e: Exception) {
            -1
        }
    }
}