package com.example.library.lending.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class OverdueFeeTest {

    @Test
    fun `should return null when not overdue`() {
        assertThat(OverdueFee.forDays(0)).isNull()
        assertThat(OverdueFee.forDays(-1)).isNull()
    }

    @Test
    fun `should resolve within a week`() {
        var fee = OverdueFee.forDays(1)
        assertThat(fee).isEqualTo(OverdueFee.WITHIN_A_WEEK)
        assertThat(fee!!.amount).isEqualTo(BigDecimal("5.00"))

        fee = OverdueFee.forDays(7)
        assertThat(fee).isEqualTo(OverdueFee.WITHIN_A_WEEK)
    }

    @Test
    fun `should resolve within two weeks`() {
        var fee = OverdueFee.forDays(8)
        assertThat(fee).isEqualTo(OverdueFee.WITHIN_TWO_WEEKS)
        assertThat(fee!!.amount).isEqualTo(BigDecimal("10.00"))

        fee = OverdueFee.forDays(14)
        assertThat(fee).isEqualTo(OverdueFee.WITHIN_TWO_WEEKS)
    }

    @Test
    fun `should resolve within a month`() {
        var fee = OverdueFee.forDays(15)
        assertThat(fee).isEqualTo(OverdueFee.WITHIN_A_MONTH)
        assertThat(fee!!.amount).isEqualTo(BigDecimal("20.00"))

        fee = OverdueFee.forDays(30)
        assertThat(fee).isEqualTo(OverdueFee.WITHIN_A_MONTH)
    }

    @Test
    fun `should resolve beyond a month`() {
        var fee = OverdueFee.forDays(31)
        assertThat(fee).isEqualTo(OverdueFee.BEYOND_A_MONTH)
        assertThat(fee!!.amount).isEqualTo(BigDecimal("50.00"))

        fee = OverdueFee.forDays(365)
        assertThat(fee).isEqualTo(OverdueFee.BEYOND_A_MONTH)
    }
}
