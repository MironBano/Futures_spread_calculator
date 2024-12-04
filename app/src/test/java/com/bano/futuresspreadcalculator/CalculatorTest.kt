package com.bano.futuresspreadcalculator

import org.junit.Assert.assertEquals
import org.junit.Test

class CalculatorTest {
    private val calculator = Calculator()

    @Test
    fun calculateActivPriceNoDiv_isCorrect() {
        val activ = 1000.0
        val dividendTrue = 100.0
        val dividendFalse = 0.0

        assertEquals(1000.0, calculator.calculateActivPriceNoDiv(activ, dividendFalse), 0.0)
        assertEquals(913.0, calculator.calculateActivPriceNoDiv(activ, dividendTrue), 0.0)
    }

    @Test
    fun calculateFuture_isCorrect(){
        val activPriceNoDiv1 = 1000.0
        val kcRate1 = 0.21
        val daysToExpire1 = 31L

        val activPriceNoDiv2 = 10000.0
        val kcRate2 = 0.2
        val daysToExpire2 = 1L

        assertEquals(1017.84, calculator.calculateFuture(activPriceNoDiv1,kcRate1,daysToExpire1), 0.005)
        assertEquals(10005.48, calculator.calculateFuture(activPriceNoDiv2,kcRate2,daysToExpire2), 0.005)
    }

    @Test
    fun calculateBackFuture_isCorrect(){
        val futurePrice = 1000.0
        val div = 100.0
        val kcRate = 0.21
        val daysToExpire = 31L

        assertEquals( 1067.95, calculator.calculateBackFuture(futurePrice, div, kcRate, daysToExpire), 0.005)
    }

}