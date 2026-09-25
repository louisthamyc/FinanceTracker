package com.louis.tham.financetracker.core.nav

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class NavDestinationTest {

    @Test
    fun homeDestination_isSingleton() {
        val dest1 = HomeDestination
        val dest2 = HomeDestination
        assertEquals(dest1, dest2)
    }

    @Test
    fun addTransactionDestination_isSingleton() {
        val dest1 = AddTransactionDestination
        val dest2 = AddTransactionDestination
        assertEquals(dest1, dest2)
    }

    @Test
    fun transactionDetailDestination_holdsCorrectTransactionId() {
        val dest = TransactionDetailDestination(transactionId = "12345")
        assertEquals("12345", dest.transactionId)

        val copy = dest.copy(transactionId = "67890")
        assertEquals("67890", copy.transactionId)
        assertNotEquals(dest, copy)
    }

    @Test
    fun transactionDetailDestination_equalsAndHashCodeWorkCorrectly() {
        val destA = TransactionDetailDestination(transactionId = "42")
        val destB = TransactionDetailDestination(transactionId = "42")
        val destC = TransactionDetailDestination(transactionId = "99")

        assertEquals(destA, destB)
        assertEquals(destA.hashCode(), destB.hashCode())
        assertNotEquals(destA, destC)
    }
}
