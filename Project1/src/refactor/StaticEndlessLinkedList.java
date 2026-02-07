package refactor;
// ######################################
// #####  DO NOT MODIFY THIS FILE!  #####
// ######################################

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Abstract class for your custom {@code LinkedList} implementation.
 * <p>
 * You should create a new class that overrides all abstract methods.
 *
 * @apiNote DO NOT MODIFY THIS FILE!
 * @implSpec
 * <p> Take your {@code LinkedList} class from your HW and refactor it
 * into your class that extends {@link StaticEndlessLinkedList}
 * <p> The iterator MUST be {@code O(1)} auxiliary space. You do not
 * have to override the default methods.
 * When it reaches the end of the list, it must wrap around.
 * @version 1.0
 * @author CS 1332 TAs
 * @param <T> the type of data in the linked list
 */
public abstract class StaticEndlessLinkedList<T> implements Iterable<T> {

    protected Node<T> head;
    protected int size;

    /**
     * Adds the element to the front of the list.
     *
     * @param data the data to add to the front of the list
     * @throws IllegalArgumentException if data is null
     * @implSpec {@code O(1)} runtime
     */
    public abstract void addFirst(T data);

    /**
     * Adds the element to the back of the list.
     *
     * @param data the data to add to the back of the list
     * @throws IllegalArgumentException if data is null
     * @implSpec {@code O(1)} runtime
     */
    public abstract void addLast(T data);

    /**
     * Removes and returns the first element of the list.
     *
     * @return the data formerly located at the front of the list
     * @throws NoSuchElementException if the list is empty
     * @implSpec {@code O(1)} runtime
     */
    public abstract T removeFirst();

    /**
     * Removes and returns the last element of the list.
     *
     * @return the data formerly located at the back of the list
     * @throws NoSuchElementException if the list is empty
     * @implSpec {@code O(n)} runtime
     */
    public abstract T removeLast();

    /**
     * Locates the first occurrence of the specified data and removes it.
     *
     * @param data the data to remove
     * @return the index of the data that was removed
     * @throws IllegalArgumentException if {@code data} is null
     * @throws NoSuchElementException if {@code data} is not in the list
     * @implSpec {@code O(n)} runtime
     */
    public abstract int removeValue(T data);

    /**
     * Returns the element at the specified index.
     *
     * @param index the index of the element to get
     * @return the data stored at the index in the list
     * @throws IndexOutOfBoundsException if index < 0 or index >= size
     * @implSpec
     * <p> {@code O(1)} runtime for index 0
     * <p> {@code O(n)} runtime for all other cases
     */
    public abstract T get(int index);

    /**
     * Reverses the linked list.
     * <p>
     * This should modify the linked list in place, such that iterating
     * through it from the head would be traversed in reversed.
     * @implSpec
     * <p> {@code O(n)} runtime
     * <p> {@code O(1)} auxiliary space
     */
    public abstract void reverse();

    /**
     * Retrieves your implementation of an iterator for
     * {@link StaticEndlessLinkedList}.
     * <p>
     * Unless the list is empty, your implementation should iterate
     * through the list in the order of front to back, then wrap
     * around to the beginning of the list. Follow {@link Iterator}
     * documentation for exception handling.
     *
     * @return an implementation of iterator
     * @implSpec
     * <p> All methods – {@code O(1)} auxiliary space
     * <p> {@link Iterator#next() next()} – {@code O(1)} runtime
     * <p> {@link Iterator#hasNext() hasNext()} – {@code O(1)} runtime
     * <p> Default methods – do not override
     */
    public abstract Iterator<T> iterator();

    // #########################################################################################

   
    /**
     * Adds the song to the specified index.
     * Must be O(1) for indices 0 and size, and O(n) for all other cases.
     *
     * @param index the index at which to add the new name
     * @param data  the name to add at the specified index
     * @throws IndexOutOfBoundsException if index < 0 or index > size
     * @throws IllegalArgumentException  if name is null
     * @implSpec
     * <p> {@code O(1)} runtime for indices 0 and size
     * <p> {@code O(n)} runtime for all other cases
     */
    public void addAtIndex(int index, T data) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index out of bounds");
        } else if (data == null) {
            throw new IllegalArgumentException("name null");
        }
        if (index == 0) {
            addFirst(data);
        } else if (index == size) {
            addLast(data);
        } else {
            Node<T> cur = head;
            for (int i = 1; i < index; i++) {
                cur = cur.next;
            }
            cur.setNext(new Node<>(data, cur.getNext()));
            size++;
        }
    }

    /**
     * Removes and returns the data at the specified index.
     * 
     * @param index the index of the data to remove
     * @return the data formerly located at the specified index
     * @throws IndexOutOfBoundsException if index < 0 or index >= size
     * @implSpec
     * <p> {@code O(1)} runtime for index 0
     * <p> {@code O(n)} runtime for all other cases
     */
    public T removeAtIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index out of bounds");
        }
        if (index == 0) {
            return removeFirst();
        } else if (index == size - 1) {
            return removeLast();
        } else {
            Node<T> cur = head;
            for (int i = 0; i < index - 1; i++) {
                cur = cur.getNext();
            }
            T result = cur.getNext().getData();
            cur.setNext(cur.getNext().getNext());
            --size;
            return result;
        }
    }

    /**
     * Returns the size of the list.
     *
     * @return the size of the list
     * @apiNote Do not override this method.
     */
    public int size() {
        return size;
    }

    /**
     * Returns the head of the list.
     *
     * @return the head of the list
     * @apiNote Do not override this method.
     */
    public Node<T> getHead() {
        return head;
    }

    /**
     * Inner node class storing the data in the linked list.
     *
     * @apiNote Do not override this class.
     * @param <T> the type of data in the linked list
     */
    public static class Node<T> {
        private T data;
        private Node<T> next;

        /**
         * Constructs a new empty {@link Node Node}.
         */
        public Node() {
            this(null);
        }

        /**
         * Constructs a new {@link Node Node} with only the given data.
         *
         * @param data the data stored in the new node
         */
        public Node(T data) {
            this(data, null);
        }

        /**
         * Constructs a new {@link Node Node} with the given data and
         * next reference.
         *
         * @param data     the data stored in the new node
         * @param next     the next node in the list
         */
        public Node(T data, Node<T> next) {
            this.data = data;
            this.next = next;
        }

        /**
         * Gets the next node.
         *
         * @return the next node
         */
        public Node<T> getNext() {
            return next;
        }

        /**
         * Sets the next node.
         *
         * @param next the new next node
         */
        public void setNext(Node<T> next) {
            this.next = next;
        }

        /**
         * Gets the data.
         *
         * @return the data
         */
        public T getData() {
            return data;
        }

        /**
         * Sets the data.
         * 
         * @param data the new data
         */
        public void setData(T data) {
            this.data = data;
        }

        /**
         * Returns a string of this node's data, and the data of its next node.
         *
         * @return string representation of this node
         */
        public String toString() {
            String dataStr = data.toString();
            String nextStr = next != null ? next.data.toString() : "null";
            return "Node{data:" + dataStr + ",next:" + nextStr + "}";
        }
    }

}
