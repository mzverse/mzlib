package mz.mzlib.util;

import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class OptionTest {

    @Test
    void testSome() {
        // Test happy path
        Option<String> option = Option.some("test");
        assertTrue(option.isSome());
        assertEquals("test", option.toNullable());
        assertEquals("test", option.unwrap());
        assertEquals(Optional.of("test"), option.toOptional());

        // Test edge case: null input should throw NullPointerException
        //noinspection DataFlowIssue
        assertThrows(NullPointerException.class, () -> Option.some(null));
    }

    @Test
    void testNone() {
        // Test happy path
        Option<String> option = Option.none();
        assertTrue(option.isNone());
        assertNull(option.toNullable());
        assertEquals(Optional.empty(), option.toOptional());

        // Test edge case: should not throw any exception
        assertDoesNotThrow(() -> Option.none().unwrapOr("default"));
    }

    @Test
    void testFromNullable() {
        // Test happy path with non-null value
        Option<String> option = Option.fromNullable("test");
        assertTrue(option.isSome());
        assertEquals("test", option.toNullable());
        assertEquals("test", option.unwrap());
        assertEquals(Optional.of("test"), option.toOptional());

        // Test edge case with null value
        Option<String> noneOption = Option.fromNullable(null);
        assertTrue(noneOption.isNone());
        assertNull(noneOption.toNullable());
        assertEquals(Optional.empty(), noneOption.toOptional());
    }

    @Test
    void testFromOptional() {
        // Test happy path with non-empty optional
        Optional<String> optional = Optional.of("test");
        Option<String> option = Option.fromOptional(optional);
        assertTrue(option.isSome());
        assertEquals("test", option.toNullable());
        assertEquals("test", option.unwrap());
        assertEquals(Optional.of("test"), option.toOptional());

        // Test edge case with empty optional
        Optional<String> emptyOptional = Optional.empty();
        Option<String> noneOption = Option.fromOptional(emptyOptional);
        assertTrue(noneOption.isNone());
        assertNull(noneOption.toNullable());
        assertEquals(Optional.empty(), noneOption.toOptional());
    }

    @Test
    void testIsSome() {
        // Test happy path with some value
        Option<String> option = Option.some("test");
        assertTrue(option.isSome());
        assertTrue(option.isSome("test"));
        assertFalse(option.isSome("another"));

        // Test edge case with none value
        Option<String> noneOption = Option.none();
        assertFalse(noneOption.isSome());
        assertFalse(noneOption.isSome("test"));
    }

    @Test
    void testIsNone() {
        // Test happy path with some value
        Option<String> option = Option.some("test");
        assertFalse(option.isNone());

        // Test edge case with none value
        Option<String> noneOption = Option.none();
        assertTrue(noneOption.isNone());
    }

    @Test
    void testUnwrap() {
        // Test happy path with some value
        Option<String> option = Option.some("test");
        assertEquals("test", option.unwrap());

        // Test edge case with none value
        Option<String> noneOption = Option.none();
        assertThrows(NoSuchElementException.class, noneOption::unwrap);
    }

    @Test
    void testUnwrapOr() {
        // Test happy path with some value
        Option<String> option = Option.some("test");
        assertEquals("test", option.unwrapOr("default"));

        // Test edge case with none value
        Option<String> noneOption = Option.none();
        assertEquals("default", noneOption.unwrapOr("default"));
    }

    @Test
    void testUnwrapOrGet() {
        // Test happy path with some value
        Option<String> option = Option.some("test");
        assertEquals("test", option.unwrapOrGet(() -> { throw new RuntimeException(); }));

        // Test edge case with none value
        Option<String> noneOption = Option.none();
        assertEquals("default", noneOption.unwrapOrGet(() -> "default"));
    }

    @Test
    void testAnd() {
        // Test happy path with some value
        Option<String> option = Option.some("test");
        Option<Integer> noneOption = Option.none();
        Option<Integer> someOption = Option.some(1);

        assertTrue(option.and(someOption).isSome());
        assertFalse(option.and(noneOption).isSome());

        // Test edge case with none value
        assertTrue(noneOption.and(someOption).isNone());
        assertTrue(noneOption.and(noneOption).isNone());
    }

    @Test
    void testOr() {
        // Test happy path with some value
        Option<String> option = Option.some("test");
        Option<String> noneOption = Option.none();
        Option<String> anotherOption = Option.some("another");

        assertTrue(option.or(noneOption).isSome());
        assertTrue(option.or(anotherOption).isSome());
        assertEquals("test", option.or(noneOption).unwrap());

        // Test edge case with none value
        assertTrue(noneOption.or(anotherOption).isSome());
        assertEquals("another", noneOption.or(anotherOption).unwrap());
        assertTrue(noneOption.or(noneOption).isNone());
    }

    @Test
    void testFlatMap() {
        // Test happy path with some value
        Option<String> option = Option.some("test");
        Function<String, Option<Integer>> mapper = s -> Option.some(s.length());

        assertTrue(option.flatMap(mapper).isSome());
        assertEquals(4, option.flatMap(mapper).unwrap());

        // Test edge case with none value
        Option<String> noneOption = Option.none();
        assertTrue(noneOption.flatMap(mapper).isNone());
    }

    @Test
    void testMap() {
        // Test happy path with some value
        Option<String> option = Option.some("test");
        Function<String, Integer> mapper = String::length;

        assertTrue(option.map(mapper).isSome());
        assertEquals(4, option.map(mapper).unwrap());

        // Test edge case with none value
        Option<String> noneOption = Option.none();
        assertTrue(noneOption.map(mapper).isNone());
    }

    @Test
    void testFilter() {
        // Test happy path with some value and true predicate
        Option<String> option = Option.some("test");
        Predicate<String> predicate = s -> s.startsWith("t");

        assertTrue(option.filter(predicate).isSome());
        assertEquals("test", option.filter(predicate).unwrap());

        // Test edge case with some value and false predicate
        Predicate<String> falsePredicate = s -> s.startsWith("a");
        assertTrue(option.filter(falsePredicate).isNone());

        // Test edge case with none value
        Option<String> noneOption = Option.none();
        assertTrue(noneOption.filter(predicate).isNone());
    }

    @Test
    void testFilterClass() {
        // Test happy path with some value and matching class
        Option<Object> option = Option.some("test");

        assertTrue(option.filter(String.class).isSome());
        assertEquals("test", option.filter(String.class).unwrap());

        // Test edge case with some value and non-matching class
        assertTrue(option.filter(Integer.class).isNone());

        // Test edge case with none value
        Option<Object> noneOption = Option.none();
        assertTrue(noneOption.filter(String.class).isNone());
    }

    @Test
    void testStream() {
        // Test happy path with some value
        Option<String> option = Option.some("test");
        Stream<String> stream = option.stream();

        assertTrue(stream.anyMatch("test"::equals));

        // Test edge case with none value
        Option<String> noneOption = Option.none();
        Stream<String> emptyStream = noneOption.stream();

        assertFalse(emptyStream.anyMatch("test"::equals));
    }

    @Test
    void testIterator() {
        // Test happy path with some value
        Option<String> option = Option.some("test");
        Iterator<String> iterator = option.iterator();

        assertTrue(iterator.hasNext());
        assertEquals("test", iterator.next());
        assertFalse(iterator.hasNext());

        // Test edge case with none value
        Option<String> noneOption = Option.none();
        Iterator<String> emptyIterator = noneOption.iterator();

        assertFalse(emptyIterator.hasNext());
    }

    @Test
    void testEqualsAndHashCode() {
        // Test happy path with some value
        Option<String> option1 = Option.some("test");
        Option<String> option2 = Option.some("test");
        Option<String> option3 = Option.some("another");

        assertEquals(option1, option2);
        assertEquals(option1.hashCode(), option2.hashCode());

        // Test edge case with different some values
        assertNotEquals(option1, option3);
        assertNotEquals(option1.hashCode(), option3.hashCode());

        // Test edge case with none value
        Option<String> noneOption1 = Option.none();
        Option<String> noneOption2 = Option.none();

        assertEquals(noneOption1, noneOption2);
        assertEquals(noneOption1.hashCode(), noneOption2.hashCode());
        assertNotEquals(option1, noneOption1);
    }
}
