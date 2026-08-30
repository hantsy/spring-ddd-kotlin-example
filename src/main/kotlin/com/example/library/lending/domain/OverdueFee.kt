package com.example.library.lending.domain

import java.math.BigDecimal

/**
 * Dummy overdue fee schedule for demonstration purposes. In a real-world system
 * this would live in a database table so library staff can adjust rates without
 * redeploying.
 */
enum class OverdueFee(val maxDays: Int, val amount: BigDecimal) {
    WITHIN_A_WEEK(7, BigDecimal("5.00")),
    WITHIN_TWO_WEEKS(14, BigDecimal("10.00")),
    WITHIN_A_MONTH(30, BigDecimal("20.00")),
    BEYOND_A_MONTH(Int.MAX_VALUE, BigDecimal("50.00"));

    companion object {
        /**
         * Resolves the fee for the given number of overdue days.
         *
         * @return the matching fee tier, or `null` if not overdue
         */
        fun forDays(daysOverdue: Long): OverdueFee? {
            if (daysOverdue <= 0) return null
            for (tier in entries) {
                if (daysOverdue <= tier.maxDays) return tier
            }
            return BEYOND_A_MONTH
        }
    }
}
