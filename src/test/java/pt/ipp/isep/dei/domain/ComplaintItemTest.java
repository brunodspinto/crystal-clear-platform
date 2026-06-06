package pt.ipp.isep.dei.domain;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class ComplaintItemTest {

    private static final Date PAST_DATE = new Date(0); // 1 Jan 1970 — always in the past

    @Test
    void ensureGrievanceCreationWorks() {
        ComplaintItem item = new ComplaintItem("Behaviour described", PAST_DATE, PoliticalFunction.MAYOR);
        assertNotNull(item);
    }

    @Test
    void ensureGettersReturnCorrectValues() {
        ComplaintItem item = new ComplaintItem("Behaviour described", PAST_DATE, PoliticalFunction.MINISTER);
        assertEquals("Behaviour described", item.getDescription());
        assertEquals(PAST_DATE, item.getComplaintDate());
        assertEquals(PoliticalFunction.MINISTER, item.getPoliticalFunction());
    }

    @Test
    void ensureGrievanceFailsWithNullDescription() {
        assertThrows(IllegalArgumentException.class, () ->
                new ComplaintItem(null, PAST_DATE, PoliticalFunction.MAYOR));
    }

    @Test
    void ensureGrievanceFailsWithBlankDescription() {
        assertThrows(IllegalArgumentException.class, () ->
                new ComplaintItem("   ", PAST_DATE, PoliticalFunction.MAYOR));
    }

    @Test
    void ensureGrievanceFailsWithNullDate() {
        assertThrows(IllegalArgumentException.class, () ->
                new ComplaintItem("Description", null, PoliticalFunction.MAYOR));
    }

    @Test
    void ensureGrievanceFailsWithFutureDate() {
        Date futureDate = new Date(Long.MAX_VALUE);
        assertThrows(IllegalArgumentException.class, () ->
                new ComplaintItem("Description", futureDate, PoliticalFunction.MAYOR));
    }

    @Test
    void ensureGrievanceFailsWithNullFunction() {
        assertThrows(IllegalArgumentException.class, () ->
                new ComplaintItem("Description", PAST_DATE, null));
    }

    @Test
    void ensureTodayDateWorks() {
        ComplaintItem item = new ComplaintItem("Description", new Date(), PoliticalFunction.DEPUTY);
        assertNotNull(item);
    }
}
