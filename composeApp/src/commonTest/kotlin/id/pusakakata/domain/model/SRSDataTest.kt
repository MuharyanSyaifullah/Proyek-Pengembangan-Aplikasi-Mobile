package id.pusakakata.domain.model

import kotlinx.datetime.Instant
import kotlin.test.*

class SRSDataTest {

    @Test
    fun testSRSData_initialState() {
        val srs = SRSData()
        assertEquals(0, srs.level)
        assertEquals(2.5, srs.easeFactor)
        assertEquals(0, srs.intervalDays)
        assertNull(srs.nextReview)
    }

    @Test
    fun testSRSData_calculateNextReview_lowQuality() {
        val srs = SRSData()
        val now = Instant.fromEpochMilliseconds(0)
        val next = srs.calculateNextReview(2, now) // Quality < 3
        
        assertEquals(0, next.level)
        assertEquals(1, next.intervalDays)
        assertTrue(next.easeFactor < srs.easeFactor)
    }

    @Test
    fun testSRSData_calculateNextReview_highQuality_firstReview() {
        val srs = SRSData()
        val now = Instant.fromEpochMilliseconds(0)
        val next = srs.calculateNextReview(5, now) // Perfect
        
        assertEquals(1, next.level)
        assertEquals(1, next.intervalDays)
        assertEquals(2.6, next.easeFactor)
    }

    @Test
    fun testSRSData_calculateNextReview_highQuality_secondReview() {
        val srs = SRSData(intervalDays = 1, level = 1, easeFactor = 2.6)
        val now = Instant.fromEpochMilliseconds(0)
        val next = srs.calculateNextReview(4, now)
        
        assertEquals(2, next.level)
        assertEquals(6, next.intervalDays)
    }

    @Test
    fun testSRSData_calculateNextReview_highQuality_multipleReviews() {
        val srs = SRSData(intervalDays = 6, level = 2, easeFactor = 2.6)
        val now = Instant.fromEpochMilliseconds(0)
        val next = srs.calculateNextReview(4, now)
        
        assertEquals(3, next.level)
        assertEquals((6 * 2.6).toInt(), next.intervalDays)
    }
}

