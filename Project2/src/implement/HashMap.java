package implement;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.List;

/**
 * Your implementation of a Linear Probing HashMap. Must implement {@link Iterable}.
 */
public class HashMap<K, V> implements Iterable<K> {


    /**
     * The initial capacity of the LinearProbingHashMap when created with the
     * default constructor.
     *
     * DO NOT MODIFY THIS VARIABLE!
     */
    public static final int INITIAL_CAPACITY = 13;

    /**
     * The max load factor of the LinearProbingHashMap
     *
     * DO NOT MODIFY THIS VARIABLE!
     */
    public static final double MAX_LOAD_FACTOR = 0.67;

    private MapEntry<K, V>[] table;
    private int size;

    /**
     * Constructs a new Linear Probing HashMap.
     *
     * The backing array should have an initial capacity of {@code INITIAL_CAPACITY}.
     *
     * Use constructor chaining.
     */
    public HashMap() {
        this(INITIAL_CAPACITY);
    }

    /**
     * Constructs a new LinearProbingHashMap.
     *
     * The backing array should have an initial capacity of initialCapacity.
     *
     * You may assume initialCapacity will always be positive.
     *
     * @param initialCapacity the initial capacity of the backing array
     */
    public HashMap(int initialCapacity) {
        this.table = new MapEntry[initialCapacity];

        this.size = 0;
    }

    /**
     * Adds the given key-value pair to the map. If an entry in the map
     * already has this key, replace the entry's value with the new one
     * passed in.
     *
     * In the case of a collision, use linear probing as your resolution
     * strategy. See the PDF for more instructions on edge cases and resizing.
     *
     * If a value was updated, return the old value; otherwise, return null.
     *
     * @param key   the key to add
     * @param value the value to add
     * @return null if the key was not already in the map. If it was in the
     * map, return the old value associated with it
     * @throws IllegalArgumentException if key or value is null
     */
    public V put(K key, V value) {
        return putH(key, value, false);
    }

    /**
     * Private helper method for implementation of put;
     * @param key   the key to add
     * @param value the value to add
     * @return null if the key was not already in the map. If it was in the
     * map, return the old value associated with it
     * @throws IllegalArgumentException if key or value is null
     * @param ignoreResize whether to ignore resizew call
     */
    private V putH(K key, V value, boolean ignoreResize) {
        if (key == null || value == null)   {
            throw new IllegalArgumentException("Cannot put entry with null Key or Value");
        }

        // resize check
        if (!ignoreResize)   {
            if (((double) (this.size + 1) / this.table.length) > MAX_LOAD_FACTOR)   {
                resizeBackingTable(2 * this.table.length + 1);
            }
        }

        int index = Math.abs((key.hashCode()) % this.table.length); // absolute value bc hashCode can be negative

        int firstDelLocation = -1;
        for (int i = 0; i < this.table.length; i++) {
            // calculate where we're probing at
            int probeAt = (index + i) % this.table.length;
            if (this.table[probeAt] == null) { // Case 1: encounter null, time to put!
                if (firstDelLocation == -1) { // check that we didn't previously encounter a DEL flag
                    this.table[probeAt] = new MapEntry<>(key, value);  // insert
                } else {
                    this.table[firstDelLocation] = new MapEntry<>(key, value); // put at first del location
                }
                this.size++;
                return null;

            } else if (this.table[probeAt].getKey().equals(key)) { // Case 2: Found matching key
                if (this.table[probeAt].isRemoved())    { // 2a - matching but DEL flag
                    if (firstDelLocation == -1) { // resurrect "dead" spot
                        this.table[probeAt].setValue(value); // update
                        this.table[probeAt].setRemoved(false); // update
                    } else {
                        this.table[firstDelLocation] = new MapEntry<>(key, value);
                    }
                    this.size++;
                    return null;
                } else { // matching & entry active -> update
                    V prevValue = this.table[probeAt].getValue();
                    this.table[probeAt].setValue(value);
                    return prevValue;
                }
            } else if (this.table[probeAt].isRemoved()) { // Case 3: DEL encountered
                if (firstDelLocation == -1) {
                    firstDelLocation = probeAt;
                }
            }
        }

        // Fallback Case: Looped through entire table but null not found
        if (firstDelLocation != -1) {
            this.table[firstDelLocation] = new MapEntry<>(key, value);
        } else {
            System.out.println("Something seriously went wrong");
        }
        this.size++;
        return null;
    }

    /**
     * Removes the entry with a matching key from map by marking the entry as
     * removed.
     *
     * @param key the key to remove
     * @return the value previously associated with the key
     * @throws IllegalArgumentException if key is null
     * @throws NoSuchElementException   if the key is not in the map
     */
    public V remove(K key) {
        if (key == null)    {
            throw new IllegalArgumentException("Cannot remove with null key");
        }

        int index = Math.abs((key.hashCode()) % this.table.length); // absolute value bc hashCode can be negative
        for (int i = 0; i < this.table.length; i++) {
            int probeAt = (index + i) % this.table.length;
            if (this.table[probeAt] == null)    {
                break;
            } else if (this.table[probeAt].isRemoved()) {
                continue;
            } else if (this.table[probeAt].getKey().equals(key))   {
                this.table[probeAt].setRemoved(true);
                this.size--;
                return this.table[probeAt].getValue();
            }
        }

        throw new NoSuchElementException("Key to remove not found in map");
    }

    /**
     * Gets the value associated with the given key.
     *
     * @param key the key to search for in the map
     * @return the value associated with the given key
     * @throws IllegalArgumentException if key is null
     * @throws NoSuchElementException   if the key is not in the map
     */
    public V get(K key) {
        if (key == null)    {
            throw new IllegalArgumentException("Cannot get with null key");
        }

        int index = Math.abs((key.hashCode()) % this.table.length); // absolute value bc hashCode can be negative
        for (int i = 0; i < this.table.length; i++) {
            int probeAt = (index + i) % this.table.length;
            if (this.table[probeAt] == null)    {
                break;
            } else if (this.table[probeAt].isRemoved()) {
                continue;
            } else if (this.table[probeAt].getKey().equals(key))   {
                return this.table[probeAt].getValue();
            }
        }

        throw new NoSuchElementException("Key to get not found in map");
    }

    /**
     * Gets the value associated with the given key, or returns a provided default value.
     * 
     * @param key the key to search for in the map
     * @param defaultValue the value to return if key not found
     * @return the value associated with the given key if found,
     * {@code defaultValue} otherwise
     * @throws IllegalArgumentException if key is null
     */
    public V getOrDefault(K key, V defaultValue) {
        try {
            return this.get(key);
        } catch (NoSuchElementException e)    {
            return defaultValue;
        }
    }

    /**
     * Returns whether or not the key is in the map.
     *
     * @param key the key to search for in the map
     * @return true if the key is contained within the map, false
     * otherwise
     * @throws IllegalArgumentException if key is null
     */
    public boolean containsKey(K key) {
        try {
            this.get(key);
            return true;
        } catch (NoSuchElementException e)    {
            return false;
        }
    }

    /**
     * Returns a Set view of the keys contained in this map.
     *
     * Use java.util.HashSet.
     *
     * @return the set of keys in this map
     */
    public Set<K> keySet() {
        Set<K> keySet = new HashSet<>();
        for (MapEntry<K, V> curEntry: this.table)    {
            if (curEntry != null && !curEntry.isRemoved())  {
                keySet.add(curEntry.getKey());
            }
        }
        return keySet;
    }

    /**
     * Returns a List view of the values contained in this map.
     *
     * Use java.util.ArrayList or java.util.LinkedList.
     *
     * You should iterate over the table in order of increasing index and add
     * entries to the List in the order in which they are traversed.
     *
     * @return list of values in this map
     */
    public List<V> values() {
        ArrayList<V> valueList = new ArrayList<V>();
        for (MapEntry<K, V> curEntry: this.table)    {
            if (curEntry != null && !curEntry.isRemoved())  {
                valueList.add(curEntry.getValue());
            }
        }
        return valueList;
    }

    /**
     * Resize the backing table to length.
     *
     * Disregard the load factor for this method. So, if the passed in length is
     * smaller than the current capacity, and this new length causes the table's
     * load factor to exceed MAX_LOAD_FACTOR, you should still resize the table
     * to the specified length.
     *
     * See the PDF for more details.
     *
     * @param length new length of the backing table
     * @throws IllegalArgumentException if length is less than the
     *                                            number of items in the hash
     *                                            map
     */
    public void resizeBackingTable(int length) {
        if (length < this.size) {
            throw new IllegalArgumentException("Cannot resize backingTable to size less than the number of elements");
        }

        MapEntry<K, V>[] oldBackingTable = this.table;
        this.table = new MapEntry[length];
        this.size = 0;

        for (MapEntry<K, V> curEntry: oldBackingTable)    {
            if (curEntry != null && !curEntry.isRemoved())   {
                this.putH(curEntry.getKey(), curEntry.getValue(), true);
            }
        }
    }

    /**
     * Clears the map.
     *
     * Resets the table to a new array of the INITIAL_CAPACITY and resets the
     * size.
     *
     * Must be O(1).
     */
    public void clear() {
        this.table = new MapEntry[INITIAL_CAPACITY];
        this.size = 0;
    }

    /**
     * Returns the table of the map.
     *
     * For grading purposes only. You shouldn't need to use this method since
     * you have direct access to the variable.
     *
     * @return the table of the map
     */
    public MapEntry<K, V>[] getTable() {
        // DO NOT MODIFY THIS METHOD!
        return table;
    }

    /**
     * Returns the size of the map.
     *
     * For grading purposes only. You shouldn't need to use this method since
     * you have direct access to the variable.
     *
     * @return the size of the map
     */
    public int size() {
        // DO NOT MODIFY THIS METHOD!
        return size;
    }

    /**
     * Returns an iterator of all keys in the HashMap. The order should be the
     * order they appear in the array from left to right.
     * <p>
     * Must be done in O(1) auxiliary space.
     *
     * @return an iterator of all keys in the HashMap
     * @implNote you may create a private inner class to implement {@link Iterator}
     */
    public Iterator<K> iterator() {
        class HashMapIterator implements Iterator<K> {
            private int index;
            private int nextIndex;

            /**
             * Default constructor
             */
            public HashMapIterator()    {
                this.index = -1; // temp value until we find
                this.nextIndex = this.findNextIndex();
            }

            /**
             * Checks if iterator has next element.
             * @return true if yes, false if not
             */
            @Override
            public boolean hasNext() {
                if (this.nextIndex == -1)    {
                    return false;
                }
                return true;
            }

            /**
             * Iterates to next element.
             * @return Next element
             * @throws NoSuchElementException if element not found
             */
            @Override
            public K next() {
                if (this.hasNext()) {
                    this.index = this.nextIndex;
                    this.nextIndex = this.findNextIndex();
                    return table[this.index].getKey();
                } else {
                    throw new NoSuchElementException("No next element in HashMap");
                }
            }

            /**
             * Private helper method to find index of next element in hashMap.
             * @return Index of next element in the backingTable, -1 if not found
             */
            private int findNextIndex() {
                for (int i = this.index + 1; i < table.length; i++)  {
                    if (table[i] != null && !table[i].isRemoved())  {
                        return i;
                    }
                }
                return -1;
            }
        }

        return new HashMapIterator();
    }
    
}
