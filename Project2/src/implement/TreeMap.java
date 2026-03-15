package implement;
import refactor.AVLNode;
import refactor.StaticTreeMap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Stack;


public class TreeMap<K extends Comparable<? super K>, V> implements StaticTreeMap<K, V> {
    private AVLNode<TreeMapEntry<K, V>> root;
    private int size;

    /**
     * Updates a node's height and balance factor, assuming that the node's
     * children were both already properly updated.
     *
     * @param node the node to update (assume non-null)
     */
    private void update(AVLNode<TreeMapEntry<K, V>> node) {
        int leftHeight = (node.getLeft() == null) ? -1 : node.getLeft().getHeight();
        int rightHeight = (node.getRight() == null) ? -1 : node.getRight().getHeight();
        node.setHeight(1 + (Math.max(leftHeight, rightHeight)));
        node.setBalanceFactor(leftHeight - rightHeight);
    }

    /**
     * @param node the root of the current subtree (assumed to be unbalanced)
     * @return the new root of the balanced subtree
     * @implNote <b>Fill in this method with your code.</b>
     */
    private AVLNode<TreeMapEntry<K, V>> leftRotate(AVLNode<TreeMapEntry<K, V>> node) {
        AVLNode<TreeMapEntry<K, V>> a = node;
        AVLNode<TreeMapEntry<K, V>> b = a.getRight();

        a.setRight(b.getLeft()); // prev, A.right == B
        b.setLeft(a);
        update(a);
        update(b);
        return b;
    }

    /**
     * @param node the root of the current subtree (assumed to be unbalanced)
     * @return the new root of the balanced subtree
     * @implNote <b>Fill in this method with your code.</b>
     */
    private AVLNode<TreeMapEntry<K, V>> rightRotate(AVLNode<TreeMapEntry<K, V>> node) {
        AVLNode<TreeMapEntry<K, V>> a = node;
        AVLNode<TreeMapEntry<K, V>> b = a.getLeft();

        a.setLeft(b.getRight());
        b.setRight(a);
        update(a);
        update(b);
        return b;
    }

    /**
     * Checks if the passed in node needs to be balanced and calls
     * the relevant rotation operations, if necessary.
     *
     * @param node the root of the current subtree
     * @return the new root of the balanced subtree
     * @implNote <b>Fill in this method with your code.</b>
     */
    private AVLNode<TreeMapEntry<K, V>> balance(AVLNode<TreeMapEntry<K, V>> node) {
        if (node.getBalanceFactor() < -1)    {
            if (node.getRight().getBalanceFactor() < 1) {
                return this.leftRotate(node); // left
            } else if (node.getRight().getBalanceFactor() >= 1)  {
                // right-left
                AVLNode<TreeMapEntry<K, V>> a = node;
                AVLNode<TreeMapEntry<K, V>> b = a.getRight();
                a.setRight(this.rightRotate(b)); // internal rotation
                return this.leftRotate(a);
            }
        } else if (node.getBalanceFactor() > 1) {
            if (node.getLeft().getBalanceFactor() > -1) {
                // right
                return this.rightRotate(node);
            } else if (node.getLeft().getBalanceFactor() <= -1) {
                // left-right
                AVLNode<TreeMapEntry<K, V>> a = node;
                AVLNode<TreeMapEntry<K, V>> b = a.getLeft();

                a.setLeft(this.leftRotate(b));
                return this.rightRotate(a);
            }
        }
        return node;
    }


    // TREEMAP-UNIQUE IMPLEMENTATION BEGINS HERE

    /**
     * Adds the given key-value pair to the map. If an entry in the map
     * already has this key, replace the entry's value with the new one
     * passed in.
     *
     * @param key the key to add
     * @param value the value to add
     * @return null if the key was not already in the map. If it was in the
     * map, return the old value associated with it
     * @throws IllegalArgumentException if key or value is null
     * @implSpec {@code O(log n)} runtime
     */
    @Override
    public V put(K key, V value)   {
        if (key == null || value == null)    {
            throw new IllegalArgumentException("Cannot put null key or value to TreeMap");
        }

        TreeMapEntry<K, V> dummy = new TreeMapEntry<>();
        this.root = putH(this.root, new TreeMapEntry<>(key, value), dummy);
        return dummy.getValue();
    }

    /**
     * Helper method for recursive, pointer reinforcement-based implementation of put
     * @param curr The root of the current subtree
     * @param data The data to put
     * @param dummy Temporary node to store previous value
     * @return The new root of the current subtree
     */
    private AVLNode<TreeMapEntry<K, V>> putH(AVLNode<TreeMapEntry<K, V>> curr,
                                             TreeMapEntry<K, V> data,
                                             TreeMapEntry<K, V> dummy)    {
        if (curr == null) {
            this.size++;
            return new AVLNode<>(data);
        }
        int comp = data.compareTo(curr.getData());
        if (comp > 0) {
            curr.setRight(putH(curr.getRight(), data, dummy));
        } else if (comp < 0) {
            curr.setLeft(putH(curr.getLeft(), data, dummy));
        } else  {
            V replacedValue = curr.getData().getValue();
            curr.getData().setValue(data.getValue());
            dummy.setValue(replacedValue);
        }

        update(curr);
        return balance(curr);
    }

    /**
     * Removes the entry with a matching key from map.
     *
     * Use the predecessor when removing a node with two children.
     *
     * @param key the key to remove
     * @return the value previously associated with the key
     * @throws IllegalArgumentException if key is null
     * @throws java.util.NoSuchElementException   if the key is not in the map
     * @implSpec {@code O(log n)} runtime
     */
    @Override
    public V remove(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Cannot remove with null key");
        }

        TreeMapEntry<K, V> dummy = new TreeMapEntry<>();
        root = removeH(root, dummy, new TreeMapEntry<>(key, null));
        return dummy.getValue();
    }

    /**
     * Helper method for recursive, pointer reinforcement-based implementation of remove().
     *
     * @param curr the root of the current subtree
     * @param dummy extra node to store removed data
     * @param data the data to remove
     * @return the new root of the subtree
     */
    private AVLNode<TreeMapEntry<K, V>> removeH(AVLNode<TreeMapEntry<K, V>> curr,
                                                TreeMapEntry<K, V> dummy,
                                                TreeMapEntry<K, V> data) {
        if (curr == null) {
            throw new NoSuchElementException("Could not find element to remove in TreeMap");
        }
        int comp = data.compareTo(curr.getData());
        if (comp > 0) {
            curr.setRight(removeH(curr.getRight(), dummy, data));
        } else if (comp < 0) {
            curr.setLeft(removeH(curr.getLeft(), dummy, data));
        } else {
            dummy.setValue(curr.getData().getValue());
            this.size--;
            if (curr.getRight() == null) {
                return curr.getLeft();
            } else if (curr.getLeft() == null) {
                return curr.getRight();
            } else {
                AVLNode<TreeMapEntry<K, V>> predecessor = new AVLNode<>(null);
                curr.setLeft(removePredecessor(curr.getLeft(), predecessor));
                curr.setData(predecessor.getData());
            }
        }

        update(curr);
        return balance(curr);
    }


    /**
     * Recursive helper method for removing the predecessor.
     *
     * @param curr the root of the current subtree
     * @param dummy extra node to store the predecessor
     * @return the new root of the subtree
     */
    private AVLNode<TreeMapEntry<K, V>> removePredecessor(AVLNode<TreeMapEntry<K, V>> curr,
                                                          AVLNode<TreeMapEntry<K, V>> dummy) {
        if (curr.getRight() == null) {
            dummy.setData(curr.getData());
            return curr.getLeft();
        } else {
            curr.setRight(removePredecessor(curr.getRight(), dummy));
            update(curr);
            return balance(curr);
        }

    }

    /**
     * Gets the value associated with the given key.
     *
     * @param key the key to search for in the map
     * @return the value associated with the given key
     * @throws IllegalArgumentException if key is {@code null}
     * @throws java.util.NoSuchElementException   if the key is not in the map
     * @implSpec {@code O(log n)} runtime
     */
    @Override
    public V get(K key) {
        if (key == null)    {
            throw new IllegalArgumentException("Cannot call get() with null key");
        }

        AVLNode<TreeMapEntry<K, V>> result = getH(this.root, new TreeMapEntry<>(key, null));
        if (result == null) {
            throw new java.util.NoSuchElementException("Element not found in TreeMap");
        } else {
            return result.getData().getValue();
        }
    }


    /**
     * Recursive helper method for retrieving the data.
     *
     * @param curr the root of the current subtree
     * @param data the data to find in the tree
     * @return the data found in the tree
     */
    private AVLNode<TreeMapEntry<K, V>> getH(AVLNode<TreeMapEntry<K, V>> curr, TreeMapEntry<K, V> data) {
        if (curr == null) {
            return null;
        }
        int comp = data.compareTo(curr.getData());
        if (comp > 0) {
            return getH(curr.getRight(), data);
        } else if (comp < 0) {
            return getH(curr.getLeft(), data);
        } else {
            return curr;
        }
    }


    /**
     * Returns a list of all values within an inclusive key range.
     *
     * @param lower the lower key bound (inclusive)
     * @param upper the upper key bound (inclusive)
     * @return a list of all values within the range {@code [lower, upper]}.
     * @throws IllegalArgumentException if any argument is {@code null}
     * @implSpec
     * <p> {@code O(log(n) + k)} runtime
     * <p> {@code O(1)} auxiliary space (excluding recursive stack)
     * @implNote consider {@code k} to be the number of elements within the range,
     * and is practically less than {@code n}
     */
    @Override
    public List<V> getRange(K lower, K upper)   {
        if (lower == null || upper == null) {
            throw new IllegalArgumentException("Lower / Upper bound to getRange cannot be null");
        }

        ArrayList<V> valuesInRange = new ArrayList<>();

        if (lower.compareTo(upper) > 0) {
            return valuesInRange;
        }

        getRangeH(this.root, new TreeMapEntry<K, V>(lower, null), new TreeMapEntry<K, V>(upper, null), valuesInRange);
        return valuesInRange;
    }

    /**
     * Recursive helper method for getRange()
     *
     * @param curr the root of the current subtree
     * @param lowerBound The lower bound for the data to find in the tree
     * @param upperBound The upper bound for the data to find in the tree
     * @param valuesInRange List containing all values in range
     */
    private void getRangeH(AVLNode<TreeMapEntry<K, V>> curr,
                           TreeMapEntry<K, V> lowerBound,
                           TreeMapEntry<K, V> upperBound,
                           List<V> valuesInRange) {
        if (curr == null) {
            return;
        }
        int compToLowerBound =  curr.getData().compareTo(lowerBound);
        int compToUpperBound =  curr.getData().compareTo(upperBound);

        // ORDER OF IF STATEMENTS MATTERS, must visit Left->Cur->Right to get inorder traversal
        if (compToLowerBound > 0) {
            getRangeH(curr.getLeft(), lowerBound, upperBound, valuesInRange);
        }
        if (compToLowerBound >= 0 && compToUpperBound <= 0) {
            valuesInRange.add(curr.getData().getValue());
        }
        if (compToUpperBound < 0) {
            getRangeH(curr.getRight(), lowerBound, upperBound, valuesInRange);
        }

    }

    /**
     * Returns whether or not the key is in the map.
     *
     * @param key the key to search for in the map
     * @return true if the key is contained within the map, false
     * otherwise
     * @throws IllegalArgumentException if key is null
     * @implSpec {@code O(log n)} runtime
     */
    @Override
    public boolean containsKey(K key)  {
        if (key == null)    {
            throw new IllegalArgumentException("Cannot call containsKey with null key");
        }
        return getH(this.root, new TreeMapEntry<>(key, null)) != null;
    }

    /**
     * Returns a Set view of the keys contained in this map.
     *
     * @return the set of keys in this map
     * @implSpec
     * <p> {@code O(n)} runtime
     * <p> Use {@link java.util.HashSet}
     */
    @Override
    public Set<K> keySet() {
        HashSet<K> keySet = new HashSet<>();
        inOrderKeySetH(this.root, keySet);
        return keySet;
    }

    /**
     * Private helper method for recursive implementation of keySet
     * Adds the keys from an inorder traversal of the treemap to a given set
     * @param cur Current subtree to recurse on
     * @param keySet Set to add traversal too
     */
    private void inOrderKeySetH(AVLNode<TreeMapEntry<K, V>> cur, Set<K> keySet) {
        if (cur == null)    {
            return;
        }
        inOrderKeySetH(cur.getLeft(), keySet);
        keySet.add(cur.getData().getKey());
        inOrderKeySetH(cur.getRight(), keySet);
    }

    /**
     * Returns a List view of the values contained in this map.
     *
     * @return list of values in this map
     * @implSpec
     * <p> {@code O(n)} runtime
     * <p> Use {@link java.util.ArrayList} or {@link java.util.LinkedList}
     */
    @Override
    public List<V> values()    {
        ArrayList<V> values = new ArrayList<>();
        inOrderValuesH(this.root, values);
        return values;
    }

    /**
     * Private helper method returning the keys in TreeMap through an inorder traversal
     * @param cur The root of the current subtree inOrderValuesH is recursing on
     * @param values List of Values, in ascending order
     */
    private void inOrderValuesH(AVLNode<TreeMapEntry<K, V>> cur, List<V> values)    {
        if (cur == null)    {
            return;
        }
        inOrderValuesH(cur.getLeft(), values);
        values.add(cur.getData().getValue());
        inOrderValuesH(cur.getRight(), values);
    }

    /**
     * Clears the map.
     *
     * @implSpec {@code O(1)} runtime
     */
    @Override
    public void clear()    {
        this.root = null;
        size = 0;
    }

    /**
     * Returns the height of the root of the tree.
     *
     * @return the height of the root of the tree, -1 if the tree is empty
     * @implSpec {@code O(1)} runtime
     */
    @Override
    public int height()    {
        return root == null ? -1 : root.getHeight();
    }

    /**
     * Returns the number of entries in the map.
     *
     * @return the size of the map
     * @implSpec {@code O(1)} runtime
     */
    @Override
    public int size()  {
        return this.size;
    }

    /**
     * Returns an iterator over the values of the map.
     * Iterates over the TreeMap through an inorder traversal
     * i.e. Left -> Parent -> Right
     * @return an iterator over the values of the map
     * @implSpec
     * <p> {@code O(log n)} auxiliary space. You cannot simply call
     * {@link #values()}.
     */
    @Override
    public Iterator<V> iterator()  {
        /**
         *** Inner class defining the Iterator to return
         */

        class TreeMapIterator implements Iterator<V> {
            // stack containing nodes that have not yet been iterated over and searched for right nodes
            private Stack<AVLNode<TreeMapEntry<K, V>>> nodeStack;

            /**
             * Default constructor, initializes the nodeStack and begins by pushing all left nodes
             */
            public TreeMapIterator()   {
                this.nodeStack = new Stack<>();
                pushAllLeftNodes(root);
            }


            /**
             * Goes left, pushing all nodes it encounters, until it can't anymore
             * @param cur Node to start from
             */
            private void pushAllLeftNodes(AVLNode<TreeMapEntry<K, V>> cur)  {
                while (cur != null) {
                    nodeStack.push(cur);
                    cur = cur.getLeft();
                }

            }

            /**
             * Checks if the iterator can advance further
             * @return True if it can, False if it can't
             */
            @Override
            public boolean hasNext() {
                return !this.nodeStack.isEmpty();
            }

            /**
            * Advances the iterator.
            * Logical order
            * ! previously, pushAllLeftNodes() will have been called on root / last node that was iterated over
            * (1) Pop the node at the top of the nodeStack
            * (2) call pushAllLeftNodes() on node that was just popped
            * (3) Visit the popped node
            * @return The value corresponding to the popped node
            */
            @Override
            public V next() {
                if (!hasNext())  {
                    throw new NoSuchElementException("No next item in TreeMap");
                }

                AVLNode<TreeMapEntry<K, V>> popped = this.nodeStack.pop();
                pushAllLeftNodes(popped.getRight());
                return popped.getData().getValue();
            }
        }

        return new TreeMapIterator();
    }

    /**
     * Helper class for implementation of TreeMap.
     * Basically a MapEntry that implements comparable such that AVLNode can store it in this.data
     * @param <K> The key for the entry
     * @param <V> The value for the entry
     */
    private static class TreeMapEntry<K extends Comparable<? super K>, V> implements Comparable<TreeMapEntry<K, V>> {
        private K key;
        private V value;

        /**
         * Constructor for TreeMapEntry
         * @param key The key for the entry
         * @param value The value for the entry
         */
        public TreeMapEntry(K key, V value)   {
            this.key = key;
            this.value = value;
        }

        /**
         * Default constructor, sets key & value to null
         */
        public TreeMapEntry()   {
            this(null, null);
        }

        /**
         * Getter for key
         * @return The key for the entry
         */
        public K getKey() {
            return this.key;
        }

        /**
         * Getter for value
         * @return The value for the entry
         */
        public V getValue() {
            return this.value;
        }

        /**
         * Setter for key
         * @param newKey The new key for the entry
         */
        public void setKey(K newKey)    {
            this.key = newKey;
        }

        /**
         * Setter for value
         * @param newValue The new value for the entry
         */
        public void setValue(V newValue)  {
            this.value = newValue;
        }

        /**
         * Implementation of compareTo
         * @param other the object to be compared.
         * @return < 0 if less than, 0 if equal, > 0 if greater than
         */
        @Override
        public int compareTo(TreeMapEntry<K, V> other)    {
            return this.key.compareTo(other.key);
        }
    }
}
