package com.parser.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class FieldsLocationMapTest {

    @Test
    public void putAddsNewEntry() {
        FieldLocationMap map = new FieldLocationMap(10);
        map.put(1, 100, 10);
        assertTrue(map.containsKey(1));
        assertEquals(100, map.getOffset(map.getIndex(1)));
        assertEquals(10, map.getLength(map.getIndex(1)));
    }

    @Test
    public void putUpdatesExistingEntry() {
        FieldLocationMap map = new FieldLocationMap(10);
        map.put(1, 100, 10);
        map.put(1, 200, 20);
        assertTrue(map.containsKey(1));
        assertEquals(200, map.getOffset(map.getIndex(1)));
        assertEquals(20, map.getLength(map.getIndex(1)));
    }

    @Test
    public void getIndexReturnsEmptyForNonExistentKey() {
        FieldLocationMap map = new FieldLocationMap(10);
        assertEquals(-1, map.getIndex(99));
    }

    @Test
    public void containsKeyReturnsFalseForNonExistentKey() {
        FieldLocationMap map = new FieldLocationMap(10);
        assertFalse(map.containsKey(99));
    }

    @Test
    public void clearRemovesAllEntries() {
        FieldLocationMap map = new FieldLocationMap(10);
        map.put(1, 100, 10);
        map.put(2, 200, 20);
        map.clear();
        assertEquals(0, map.size());
        assertFalse(map.containsKey(1));
        assertFalse(map.containsKey(2));
    }

    @Test
    public void putThrowsWhenCapacityExceeded() {
        FieldLocationMap map = new FieldLocationMap(2);
        map.put(1, 100, 10);
        map.put(2, 200, 20);
        assertThrows(RuntimeException.class, () -> map.put(3, 300, 30));
    }

    @Test
    public void sizeReturnsCorrectCount() {
        FieldLocationMap map = new FieldLocationMap(10);
        map.put(1, 100, 10);
        map.put(2, 200, 20);
        assertEquals(2, map.size());
    }

    @Test
    public void getOffsetAndLengthWorkForValidIndex() {
        FieldLocationMap map = new FieldLocationMap(10);
        map.put(1, 100, 10);
        int index = map.getIndex(1);
        assertEquals(100, map.getOffset(index));
        assertEquals(10, map.getLength(index));
    }
}
