package implement;

import refactor.StaticEndlessLinkedList;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class EndlessLinkedList<T> extends StaticEndlessLinkedList<T> {

    private Node<T> tail;
    /**
     * Constructs a new {@link EndlessLinkedList}.
     */
    public EndlessLinkedList()   {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    /**
     * Adds the element to the front of the list.
     * @param data the data to add to the front of the list
     * @throws IllegalArgumentException if data is null
     * @implSpec {@code O(1)} runtime
     */
    @Override
    public void addFirst(T data) {
        if (data == null)   {
            throw new IllegalArgumentException("Cannot add null data to an EndlessLinkedList");
        }

        this.head = new Node<>(data, this.head);

        // edge case list empty -> first node is head and tail
        if (this.size == 0) {
            this.tail = this.head;
        }
        this.size++;
    }


    /**
     * Adds the element to the back of the list.
     *
     * @param data the data to add to the back of the list
     * @throws IllegalArgumentException if data is null
     * @implSpec {@code O(1)} runtime
     */
    @Override
    public void addLast(T data) {
        if (data == null)   {
            throw new IllegalArgumentException("Cannot add null data to an EndlessLinkedList");
        }

        Node<T> newNode = new Node<>(data, null);

        // edge case list empty -> first node is head and tail
        if (this.size == 0) {
            this.tail = newNode;
            this.head = this.tail;
        } else {
            this.tail.setNext(newNode);
            this.tail = newNode;
        }
        this.size++;
    }

    /**
     * Removes and returns the first element of the list.
     *
     * @return the data formerly located at the front of the list
     * @throws NoSuchElementException if the list is empty
     * @implSpec {@code O(1)} runtime
     */
    @Override
    public T removeFirst()  {
        if (this.size == 0) {
            throw new NoSuchElementException("Cannot remove from empty EndlessLinkedList");
        }

        T data = this.head.getData();
        this.head = this.head.getNext();

        // edge case: array empty after removal
        if (this.size == 1) {
            this.tail = null;
        }
        this.size--;
        return data;
    }

    /**
     * Removes and returns the last element of the list.
     *
     * @return the data formerly located at the back of the list
     * @throws NoSuchElementException if the list is empty
     * @implSpec {@code O(n)} runtime
     */
    @Override
    public T removeLast()  {
        if (this.size == 0) {
            throw new NoSuchElementException("Cannot remove from empty EndlessLinkedList");
        }

        T data;

        if (this.size == 1) { // edge case: array empty after removal
            data = this.head.getData();
            this.head = null;
            this.tail = null;
        } else  {
            data = this.tail.getData();
            Node<T> curNode = this.head;
            for (int i = 0; i < this.size - 2; i++) { // iterate to second to last node
                curNode = curNode.getNext();
            }
            curNode.setNext(null);
            this.tail = curNode;
        }

        this.size--;
        return data;
    }

    /**
     * Locates the first occurrence of the specified data and removes it.
     *
     * @param data the data to remove
     * @return the index of the data that was removed
     * @throws IllegalArgumentException if {@code data} is null
     * @throws NoSuchElementException if {@code data} is not in the list
     * @implSpec {@code O(n)} runtime
     */
    public int removeValue(T data)  {
        if (data == null)   {
            throw new IllegalArgumentException("Cannot remove null data");
        }
        if (this.size == 0) {
            throw new NoSuchElementException("Data to remove is not in EndlessLinkedList");
        }

        int curIndex = 0;

        if (this.head.getData().equals(data))    {
            this.removeFirst(); // also handles edge case this.size == 1
            return curIndex;
        }

        curIndex = 1; // refers to index of curNode.getNext() at the top of while loop
        Node<T> curNode = this.head;

        while (curIndex < this.size)    {
            if (curNode.getNext().getData().equals(data))  {
                if (curNode.getNext() == this.tail)   { //if removing tail
                    this.tail = curNode;
                }

                curNode.setNext(curNode.getNext().getNext());

                this.size--;
                return curIndex;
            }
            curNode = curNode.getNext();
            curIndex++;
        }

        throw new NoSuchElementException("Data to remove is not in EndlessLinkedList");
    }

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
    public T get(int index) {
        if (index < 0 || index >= this.size)    {
            throw new IndexOutOfBoundsException("Invalid index passed to get");
        }

        if (index == 0) {
            return this.head.getData();
        } else if (index == this.size - 1) {
            return this.tail.getData();
        }

        Node<T> curNode = this.head;
        for (int i = 0; i < index; i++) {
            curNode = curNode.getNext();
        }
        return curNode.getData();

    }

    /**
     * Reverses the linked list.
     * <p>
     * This should modify the linked list in place, such that iterating
     * through it from the head would be traversed in reversed.
     * @implSpec
     * <p> {@code O(n)} runtime
     * <p> {@code O(1)} auxiliary space
     */
    public void reverse()   {
        if (this.size <= 1) {
            return;
        }
        Node<T> prev = null;
        Node<T> cur = this.head;
        Node<T> next;

        for (int i = 0; i < this.size; i++) {
            next = cur.getNext();
            cur.setNext(prev);

            prev = cur;
            cur = next;
        }
        Node<T> temp = this.head;
        this.head = this.tail;
        this.tail = temp;
    }

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
    public Iterator<T> iterator()   {
        return new EndlessLinkedListIterator();
    }

    /**
     * Helper class for iterator(), defines Iterator for EndlessLinkedList
     */
    private class EndlessLinkedListIterator implements Iterator<T> {
        private Node<T> curNode;

        /**
         * Constructor for ELL
         */
        public EndlessLinkedListIterator() {
            this.curNode = head;
        }

        /**
         * Checks if the Iterator can progress
         * @return if Iterator has a next element
         */
        @Override
        public boolean hasNext()    {
            return size > 0;
        }

        /**
         * Progresses the iterator if possible
         * @return data at cur position
         */
        @Override
        public T next()   {
            if (this.hasNext()) {
                T data = curNode.getData();
                if (this.curNode == tail)   {
                    curNode = head;
                } else {
                    curNode = curNode.getNext();
                }
                return data;
            }
            throw new NoSuchElementException("EndlessLinkedList is empty, cannot iterate further");
        }
    }

}
