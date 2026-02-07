package implement;

import refactor.StaticEndlessLinkedList;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Your implementation of an {@link ArrayDeque}.
 *
 */
public class ArrayDeque<T> {

    /**
     * The initial capacity of the ArrayDeque.
     * <p>
     * DO NOT MODIFY THIS VARIABLE.
     */
    public static final int INITIAL_CAPACITY = 11;

    // Do not add new instance variables or modify existing ones.
    private T[] backingArray;
    private int front;
    private int size;

    /**
     * Constructs a new {@link ArrayDeque}.
     */
    public ArrayDeque() {
        this.clear();
    }

    /**
     * Constructs a new {@link ArrayDeque}, given a linked list.
     * <p>
     * You should add all of the elements from the linked list to the deque
     * in the same order as they appear in the list. 
     * 
     * Start by initializing the backing array to 2 * linkedList.size() + 1.
     *
     * @param linkedList the iterable object
     * @throws IllegalArgumentException if linkedlist is null
     * @implNote this constructor will work once you have completed part 2
     */
    public ArrayDeque(StaticEndlessLinkedList<T> linkedList) {
        if (linkedList == null) {
            throw new IllegalArgumentException("Cannot initialize ArrayDeque from null EndlessLinkedList");
        }

        this.backingArray = (T[]) new Object[2 * linkedList.size() + 1];

        Iterator<T> iterator = linkedList.iterator();
        for (int i = 0; i < linkedList.size(); i++)    {
            this.backingArray[i] = iterator.next();
        }

        this.front = 0;
        this.size = linkedList.size();
    }

    /**
     * Adds the element to the front of the deque.
     *
     * If sufficient space is not available in the backing array, resize it to
     * double the current capacity. When resizing, copy elements to the
     * beginning of the new array and reset front to 0. After the resize and add
     * operation, the new data should be at index 0 of the array. Consider how 
     * to do this efficiently.
     *
     * Must be amortized O(1).
     *
     * @param data the data to add to the front of the deque
     * @throws IllegalArgumentException if data is null
     */
    public void addFirst(T data) {
        if (data == null)   {
            throw new IllegalArgumentException("Cannot add null data");
        }

        // resize logic
        if (this.size >= this.backingArray.length)  {
            T[] newBackingArray = (T[]) new Object[this.backingArray.length * 2];
            for (int i = 0; i < this.size; i++) {
                newBackingArray[i + 1] = this.backingArray[mod(this.front + i, this.backingArray.length)];
                // i + 1 to make addFirst more efficient
            }
            this.backingArray = newBackingArray;
            this.front = 1;
        }

        this.front = mod(this.front - 1, this.backingArray.length);
        this.backingArray[front] = data;
        this.size++;
    }

    /**
     * Adds the element to the back of the deque.
     *
     * If sufficient space is not available in the backing array, resize it to
     * double the current capacity. When resizing, copy elements to the
     * beginning of the new array and reset front to 0.
     *
     * Must be amortized O(1).
     *
     * @param data the data to add to the back of the deque
     * @throws IllegalArgumentException if data is null
     */
    public void addLast(T data) {
        if (data == null)   {
            throw new IllegalArgumentException("Cannot add null data");
        }

        // resize logic
        if (this.size >= this.backingArray.length)  {
            T[] newBackingArray = (T[]) new Object[this.backingArray.length * 2];
            for (int i = 0; i < this.size; i++) {
                newBackingArray[i] = this.backingArray[mod(this.front + i, this.backingArray.length)];
            }
            this.backingArray = newBackingArray;
            this.front = 0;
        }

        this.backingArray[mod(this.front + this.size, this.backingArray.length)] = data;
        this.size++;
    }

    /**
     * Removes and returns the first element of the deque.
     *
     * Do not grow or shrink the backing array.
     *
     * If the deque becomes empty as a result of this call, do not reset
     * front to 0. Rather, modify the front index as if the deque did not become
     * empty as a result of this call.
     *
     * Replace any spots that you remove from with null. Failure to do so can
     * result in loss of points.
     *
     * Must be O(1).
     *
     * @return the data formerly located at the front of the deque
     * @throws java.util.NoSuchElementException if the deque is empty
     */
    public T removeFirst() {
        if (this.size == 0) {
            throw new NoSuchElementException("Cannot remove from empty deque");
        }

        T data = this.backingArray[this.front];
        this.backingArray[this.front] = null;
        this.front = mod(this.front + 1, this.backingArray.length);
        this.size--;
        return data;
    }

    /**
     * Removes and returns the last element of the deque.
     *
     * Do not grow or shrink the backing array.
     *
     * If the deque becomes empty as a result of this call, do not reset
     * front to 0. 
     *
     * Replace any spots that you remove from with null. Failure to do so can
     * result in loss of points.
     *
     * Must be O(1).
     *
     * @return the data formerly located at the back of the deque
     * @throws java.util.NoSuchElementException if the deque is empty
     */
    public T removeLast() {
        if (this.size == 0) {
            throw new NoSuchElementException("Cannot remove from empty deque");
        }

        int index = mod(front + size - 1, this.backingArray.length);
        T data = this.backingArray[index];
        this.backingArray[index] = null;
        this.size--;

        return data;
    }

    /**
     * Returns the first data of the deque without removing it.
     *
     * Must be O(1).
     *
     * @return the first data
     * @throws java.util.NoSuchElementException if the deque is empty
     */
    public T getFirst() {
        if (this.size == 0) {
            throw new NoSuchElementException("Cannot get elements from empty deque");
        }

        return this.backingArray[this.front];
    }

    /**
     * Returns the last data of the deque without removing it.
     *
     * Must be O(1).
     *
     * @return the last data
     * @throws java.util.NoSuchElementException if the deque is empty
     */
    public T getLast() {
        if (this.size == 0) {
            throw new NoSuchElementException("Cannot get elements from empty deque");
        }

        return this.backingArray[mod(this.front + this.size - 1, this.backingArray.length)];
    }

    /**
     * Clears the ArrayDeque, resetting the array to length {@link ArrayDeque#INITIAL_CAPACITY}
     */
    public void clear() {
        this.backingArray = (T[]) new Object[INITIAL_CAPACITY];
        this.front = 0;
        this.size = 0;
    }

    /**
     * Returns the backing array of the deque.
     *
     * For grading purposes only. You shouldn't need to use this method since
     * you have direct access to the variable.
     *
     * @return the backing array of the deque
     */
    public T[] getBackingArray() {
        // DO NOT MODIFY THIS METHOD!
        return backingArray;
    }

    /**
     * Returns the size of the deque.
     *
     * For grading purposes only. You shouldn't need to use this method since
     * you have direct access to the variable.
     *
     * @return the size of the deque
     */
    public int size() {
        // DO NOT MODIFY THIS METHOD!
        return size;
    }

    /**
     * Returns the smallest non-negative remainder when dividing index by
     * modulo. So, for example, if modulo is 5, then this method will return
     * either 0, 1, 2, 3, or 4, depending on what the remainder is.
     *
     * This differs from using the % operator in that the % operator returns
     * the smallest answer with the same sign as the dividend. So, for example,
     * (-5) % 6 => -5, but with this method, mod(-5, 6) = 1.
     *
     * Examples:
     * mod(-3, 5) => 2
     * mod(11, 6) => 5
     * mod(-7, 7) => 0
     *
     * This helper method is here to make the math part of the circular
     * behavior easier to work with.
     *
     * @param index  the number to take the remainder of
     * @param modulo the divisor to divide by
     * @return the remainder in its smallest non-negative form
     * @throws IllegalArgumentException if the modulo is non-positive
     */
    private static int mod(int index, int modulo) {
        // DO NOT MODIFY THIS METHOD!
        if (modulo <= 0) {
            throw new IllegalArgumentException("The modulo must be positive");
        }
        int newIndex = index % modulo;
        return newIndex >= 0 ? newIndex : newIndex + modulo;
    }
}
