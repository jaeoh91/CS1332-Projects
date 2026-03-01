package implement;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

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
        // Remove this line when you implement the method
        throw new UnsupportedOperationException("Unimplemented");
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
        // Remove this line when you implement the method
        throw new UnsupportedOperationException("Unimplemented");
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
        // Remove this line when you implement the method
        throw new UnsupportedOperationException("Unimplemented");
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
        // Remove this line when you implement the method
        throw new UnsupportedOperationException("Unimplemented");
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
        // Remove this line when you implement the method
        throw new UnsupportedOperationException("Unimplemented");
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
        // Remove this line when you implement the method
        throw new UnsupportedOperationException("Unimplemented");
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
        // Remove this line when you implement the method
        throw new UnsupportedOperationException("Unimplemented");
    }

    /**
     * Returns a Set view of the keys contained in this map.
     *
     * Use java.util.HashSet.
     *
     * @return the set of keys in this map
     */
    public Set<K> keySet() {
        // Remove this line when you implement the method
        throw new UnsupportedOperationException("Unimplemented");
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
        // Remove this line when you implement the method
        throw new UnsupportedOperationException("Unimplemented");
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
        // Remove this line when you implement the method
        throw new UnsupportedOperationException("Unimplemented");
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
        // Remove this line when you implement the method
        throw new UnsupportedOperationException("Unimplemented");
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
        // Remove this line when you implement the method
        throw new UnsupportedOperationException("Unimplemented");
    }
    
}
