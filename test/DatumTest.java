


import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.time.LocalDate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class DatumTest {

    private static final String[] MONTHS = {"Januar", "Februar", "März", "April", "Mai", "Juni", "Juli",
            "August", "September", "Oktober", "November", "Dezember"};
    private static final String[] DAYS = {"Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag", "Sonntag"};

    private static Stream<Arguments> dayProvider() {
        return IntStream.range(5, 32).mapToObj(Arguments::of);
    }

    @Test
    public void constructorNoArgs_systemDateobjectCreated() {
        LocalDate today = LocalDate.now();
        Datum expected = new Datum(today.getDayOfMonth(), today.getMonthValue(), today.getYear());

        Datum shouldBeSystemDate = new Datum();

        assertEquals(expected, shouldBeSystemDate);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "text", "01012000", "01.13.2000", "32.01.2000", "31.12.1899", "29.02.2001"})
    public void constructorString_invalid_exception(String date) {
        assertThrows(IllegalArgumentException.class, () -> new Datum(date));
    }

    @ParameterizedTest
    @ValueSource(strings = {"01.02.2003", "31.12.2000", "29.02.2004", "28.02.2000"})
    public void constructorString_valid_objectCreated(String date) {
        Datum d = new Datum(date);
        assertEquals(date, d.toString());
    }

    @Test
    public void constructorDays_daysNegative_exception() {
        assertThrows(IllegalArgumentException.class, () -> new Datum(-1));
    }

    @Test
    public void constructorDays_0_objectCreated() {
        Datum d = new Datum(0);

        assertEquals(new Datum(1, 1, 1900), d);
    }

    @Test
    public void constructorDays_31_objectCreated() {
        Datum d = new Datum(31);

        assertEquals(new Datum(1, 2, 1900), d);
    }

    @Test
    public void constructorDays_365_objectCreated() {
        Datum d = new Datum(365);

        assertEquals(new Datum(1, 1, 1901), d);
    }

    @Test
    public void constructorDays_40000_objectCreated() {
        Datum d = new Datum(40_000);

        assertEquals(new Datum(8, 7, 2009), d);
    }

    @ParameterizedTest(name = "{0}.12.2016")
    @MethodSource("dayProvider")
    public void wochentag_correctDayOfWeek(int day) {
        Datum d = new Datum(day, 12, 2016);
        assertEquals(DAYS[(day - 5) % DAYS.length], d.wochentag());
    }

    @ParameterizedTest
    @MethodSource("dayProvider")
    public void wochentagNummer_correctDayOfWeek(int day) {
        Datum d = new Datum(day, 12, 1988);

        assertEquals((day - 5) % DAYS.length, d.wochentagNummer());
    }

    @Test
    public void addiereTage_0_noChange() {
        Datum d = new Datum(1, 1, 2000);
        Datum expected = new Datum(1, 1, 2000);

        d.addiereTage(0);

        assertEquals(expected, d);
    }

    @Test
    public void addiereTage_daysNegativeResultBefore1900_exception() {
        Datum d = new Datum(1, 1, 1900);

        assertThrows(IllegalArgumentException.class, () -> d.addiereTage(-1));
    }

    @Test
    public void addiereTage_daysNegativeResultBefore1900complex_exception() {
        Datum d = new Datum(20, 2, 1982);

        assertThrows(IllegalArgumentException.class, () -> d.addiereTage(-30_001));
    }

    @Test
    public void addiereTage_daysNegativeResultExactly1900_daysSubtractedFromDate() {
        Datum d = new Datum(20, 2, 1982);
        Datum expected = new Datum(1, 1, 1900);

        d.addiereTage(-30_000);

        assertEquals(expected, d);
    }

    @Test
    public void addiereTage_daysBig_daysAddedToDate() {
        Datum d = new Datum(1, 1, 1900);
        Datum expected = new Datum(1, 1, 2300);

        d.addiereTage(146_097);

        assertEquals(expected, d);
    }

    @Test
    public void addiereTage_anyBeginDate_daysAddedToDate() {
        Datum d = new Datum(18, 8, 1983);
        Datum expected = new Datum(7, 9, 2016);

        d.addiereTage(12_074);

        assertEquals(expected, d);
    }

    @Test
    public void tageZwischen_firstEqualSecond_result0() {
        Datum from = new Datum(1, 1, 2003);
        Datum to = new Datum(1, 1, 2003);

        assertEquals(0, Datum.tageZwischen(from, to));
    }

    @Test
    public void tageZwischen_oneYearBetween_365() {
        Datum from = new Datum(1, 1, 2003);
        Datum to = new Datum(1, 1, 2004);

        assertEquals(365, Datum.tageZwischen(from, to));
    }

    @Test
    public void tageZwischen_oneYearBetweenLaterFirst_negative365() {
        Datum from = new Datum(1, 1, 2004);
        Datum to = new Datum(1, 1, 2003);

        assertEquals(-365, Datum.tageZwischen(from, to));
    }

    @Test
    public void tageZwischen_leapYear_daysBetween() {
        Datum from = new Datum(31, 12, 2003);
        Datum to = new Datum(1, 3, 2004);

        assertEquals(61, Datum.tageZwischen(from, to));
    }

    @Test
    public void tageZwischen_largeTimespanLaterLast_daysBetween() {
        Datum from = new Datum(1, 1, 1900);
        Datum to = new Datum(1, 1, 2300);

        assertEquals(146_097, Datum.tageZwischen(from, to));
    }

    @Test
    public void tageZwischen_largeTimespanLaterFirst_negativeDaysBetween() {
        Datum from = new Datum(1, 1, 2300);
        Datum to = new Datum(1, 1, 1900);

        assertEquals(-146_097, Datum.tageZwischen(from, to));
    }

    @Test
    public void toString_formatShort_correctResult() {
        Datum d = new Datum(14, 4, 2008);

        assertEquals("14.04.08", d.toString(Datum.FORMAT_SHORT));
    }

    @Test
    public void toString_formatNormal_correctResult() {
        Datum d = new Datum(14, 4, 2008);

        assertEquals("14.04.2008", d.toString(Datum.FORMAT_NORMAL));
    }

    @Test
    public void toString_formatLong_correctResult() {
        Datum d = new Datum(14, 4, 2008);

        assertEquals("14.April 2008, Montag", d.toString(Datum.FORMAT_LONG));
    }

    @Test
    public void toString_formatUS_correctResult() {
        Datum d = new Datum(14, 4, 2008);

        assertEquals("2008/14/04", d.toString(Datum.FORMAT_US));
    }

    @Test
    public void toString_formatIllegal_exception() {
        Datum d = new Datum(14, 4, 2008);

        assertThrows(IllegalArgumentException.class, () -> d.toString(42));
    }

    @Test
    public void toString_noArgs_correctResult() {
        Datum d = new Datum(14, 4, 2008);

        assertEquals("14.04.2008", d.toString());
    }

    @Test
    public void compareTo_firstBeforeSecond_negativeResult() {
        Datum from = new Datum(12, 2, 2007);
        Datum to = new Datum(13, 2, 2007);

        assertTrue(from.compareTo(to) < 0);
    }

    @Test
    public void compareTo_firstAfterSecond_positiveResult() {
        Datum from = new Datum(13, 2, 2007);
        Datum to = new Datum(12, 2, 2007);

        assertTrue(from.compareTo(to) > 0);
    }

    @Test
    public void compareTo_firstEqualToSecond_result0() {
        Datum from = new Datum(13, 2, 2007);
        Datum to = new Datum(13, 2, 2007);

        assertEquals(0, from.compareTo(to));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12})
    public void getMonatAsString_firstDay_correctMonth(int month) {
        Datum d = new Datum(1, month, 2000);

        assertEquals(MONTHS[month - 1], d.getMonatAsString());
    }

    @ParameterizedTest
    @ValueSource(ints = {1900, 1907, 1901, 1999})
    public void isSchaltjahr_noLeapYear_false(int year) {
        assertFalse(Datum.isSchaltjahr(year));
    }

    @ParameterizedTest
    @ValueSource(ints = {2000, 1600, 2004})
    public void isSchaltjahr_leapYear_true(int year) {
        assertTrue(Datum.isSchaltjahr(year));
    }

    @Nested
    class ConstructorDayMonthYear {

        @Test
        public void _29022004_objectCreated() {
            assertDoesNotThrow(() -> new Datum(29, 2, 2004));
        }

        @Test
        public void _29022000_objectCreated() {
            assertDoesNotThrow(() -> new Datum(29, 2, 2000));
        }

        @Test
        public void _29021900_exception() {
            assertThrows(IllegalArgumentException.class, () -> new Datum(29, 2, 1900));
        }

        @Test
        public void _29022001_exception() {
            assertThrows(IllegalArgumentException.class, () -> new Datum(29, 2, 2001));
        }

        @Test
        public void dayNegative_exception() {
            assertThrows(IllegalArgumentException.class, () -> new Datum(-1, 3, 2010));
        }

        @Test
        public void monthTooLarge_exception() {
            assertThrows(IllegalArgumentException.class, () -> new Datum(4, 13, 2010));
        }

        @Test
        public void month0_exception() {
            assertThrows(IllegalArgumentException.class, () -> new Datum(4, 0, 2010));
        }

        @Test
        public void yearBefore1900_exception() {
            assertThrows(IllegalArgumentException.class, () -> new Datum(31, 12, 1899));
        }

        @ParameterizedTest(name = "{0}.{1}.{2}")
        @CsvSource({
                "31, 1, 2000",
                "29, 2, 2000",
                "28, 2, 2001",
                "28, 2, 1900",
                "31, 3, 2000",
                "30, 4, 2000",
                "31, 5, 2000",
                "30, 6, 2000",
                "31, 7, 2000",
                "31, 8, 2000",
                "30, 9, 2000",
                "31, 10, 2000",
                "30, 11, 2000",
                "31, 12, 2000"})
        public void lastDayOfMonth_objectCreated(int day, int month, int year) {
            assertDoesNotThrow(() -> new Datum(day, month, year));
        }

        @ParameterizedTest(name = "{0}.{1}.{2}")
        @CsvSource({
                "32, 1, 2000",
                "30, 2, 2000",
                "29, 2, 2001",
                "29, 2, 1900",
                "32, 3, 2000",
                "31, 4, 2000",
                "32, 5, 2000",
                "31, 6, 2000",
                "32, 7, 2000",
                "32, 8, 2000",
                "31, 9, 2000",
                "32, 10, 200",
                "31, 11, 200",
                "32, 12, 200"})
        public void dayTooLarge_exception(int day, int month, int year) {
            assertThrows(IllegalArgumentException.class, () -> new Datum(day, month, year));
        }

        @ParameterizedTest(name = "{0}.{1}.{2}")
        @CsvSource({
                "1, 1, 1900",
                "17, 12, 1937",
                "31, 8, 2020"})
        public void objectCreated(int day, int month, int year) {
            assertDoesNotThrow(() -> new Datum(17, 12, 2017));
        }
    }
}