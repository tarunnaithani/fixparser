package com.parser.utils;

import java.util.Arrays;

/**
 * A specialized map implementation for storing FIX field metadata.
 * This map uses open addressing with linear probing for collision resolution.
 * It stores tags, offsets, and lengths of FIX fields, along with their states.
 */
public class FieldLocationsMap {
    private final int[] tags;
    private final int[] offsets;
    private final int[] lengths;
    private final byte[] states; // 0 = empty, 1 = occupied
    private int size;
    private final int maxNumberOfFieldsExpected;
    private static final int EMPTY_RETURN = -1;
    private static final int DEFAULT_CAPACITY = 100;

    /**
     * Constructs a new FieldLocationsMap with the default capacity.
     */
    public FieldLocationsMap() {
        this(DEFAULT_CAPACITY);
    }

    /**
     * Constructs a new FieldLocationsMap with the specified initial capacity.
     *
     * @param maxNumberOfFieldsExpected The maximum number of fields the map can store.
     */
    public FieldLocationsMap(int maxNumberOfFieldsExpected) {
        this.maxNumberOfFieldsExpected =  maxNumberOfFieldsExpected;
        this.tags = new int[this.maxNumberOfFieldsExpected];
        this.offsets = new int[this.maxNumberOfFieldsExpected];
        this.lengths = new int[this.maxNumberOfFieldsExpected];
        this.states = new byte[this.maxNumberOfFieldsExpected];
        this.size = 0;
    }

    /**
     * Computes the hash index for a given key.
     *
     * @param key The key to hash.
     * @return The hash index.
     */
    private int hash(int key) {
        return key % maxNumberOfFieldsExpected;
    }

    /**
     * Inserts or updates a tag with its offset and length.
     * If the tag already exists, its offset and length are updated.
     *
     * @param tag The FIX tag to insert or update.
     * @param offset The offset of the tag's value.
     * @param length The length of the tag's value.
     * @throws RuntimeException if the map exceeds its maximum capacity.
     */
    public void put(int tag, int offset, int length) {
        if ((size + 1) > maxNumberOfFieldsExpected) {
            throw new RuntimeException("Cannot store more fields, please consider increasing initial capacity");
        }

        int index = hash(tag);
        int firstTombstone = -1;

        while (true) {
            byte state = states[index];
            if (state == 0) { // empty
                int insertIndex = (firstTombstone != -1) ? firstTombstone : index;
                tags[insertIndex] = tag;
                offsets[insertIndex] = offset;
                lengths[insertIndex] = length;
                states[insertIndex] = 1;
                size++;
                return;
            } else if (state == 1) { // occupied
                if (tags[index] == tag) {
                    offsets[index] = offset; // update
                    lengths[index] = length;
                    return;
                }
            } else { // tombstone
                if (firstTombstone == -1) firstTombstone = index;
            }
            index = (index + 1) % maxNumberOfFieldsExpected;
        }
    }

    /**
     * Retrieves the index for a given tag.
     *
     * @param tag The FIX tag to look up.
     * @return The index of the tag, or -1 if the tag is not found.
     */
    public int getIndex(int tag) {
        int index = hash(tag);
        int start = index;

        while (states[index] != 0) { // stop only at an empty slot
            if (states[index] == 1 && tags[index] == tag) {
                return index;
            }
            index = (index + 1) % maxNumberOfFieldsExpected;
            if (index == start) break;
        }
        return EMPTY_RETURN;
    }

    /**
     * Retrieves the offset for a given index.
     *
     * @param index The index to look up.
     * @return The offset of the tag's value.
     */
    public int getOffset(int index) {
        return offsets[index];
    }

    /**
     * Retrieves the length for a given index.
     *
     * @param index The index to look up.
     * @return The length of the tag's value.
     */
    public int getLength(int index) {
        return lengths[index];
    }

    /**
     * Checks if a tag exists in the map.
     *
     * @param tag The FIX tag to check.
     * @return True if the tag exists, false otherwise.
     */
    public boolean containsKey(int tag) {
        int index = hash(tag);
        int start = index;

        while (states[index] != 0) {
            if (states[index] == 1 && tags[index] == tag) return true;
            index = (index + 1) % maxNumberOfFieldsExpected;
            if (index == start) break;
        }
        return false;
    }

    /**
     * Returns the number of entries in the map.
     *
     * @return The size of the map.
     */
    public int size() {
        return size;
    }

    /**
     * Clears all entries in the map.
     */
    public void clear() {
        Arrays.fill(states, (byte)0);
        Arrays.fill(offsets, -1);
        Arrays.fill(lengths, -1);
        size = 0;
    }

}