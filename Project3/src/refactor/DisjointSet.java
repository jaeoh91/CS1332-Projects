package refactor;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Class to store a DisjointSet data structure. This data structure has two
 * main functions: find and union. find will look for the root (parent) of a
 * DisjointSet. Calling find on two different T data will check if those two are
 * part of the same set. union will join two sets together if not already.
 * <p>
 * See the PDF for more information on Disjoint Sets.
 * <p>
 * @version 2.0
 * @author CS 1332 TAs
 */
public class DisjointSet<T> {

    private final Map<T, DisjointSetNode<T>> disjointSet;
    private Set<T> rootSet;

    /**
     * Initializes the disjoint sets by instantiating a HashMap
     */
    public DisjointSet() {
        disjointSet = new HashMap<>();
        this.rootSet = new HashSet<>();
    }

    /**
     * Finds the root node of the disjoint set containing {@code data}.
     * Puts the data in the disjoint sets if it does not already exist.
     *
     * @param data the data to search for
     * @return the disjoint set's root data
     * @implNote think about how you can modify this method to ensure
     * the roots set is properly updated.
     */
    public T find(T data) {
        if (!disjointSet.containsKey(data)) {
            disjointSet.put(data, new DisjointSetNode<>(data));
            // for getRoots(), finding a node not in the disjointset makes it root of its own set
            // only time for find() that we have to add to roots, since otherwise the node has alrdy been accounted for
            this.rootSet.add(data);
        }
        return find(disjointSet.get(data)).getData();
    }

    /**
     * Recursively finds the root of the DisjointSetNode. Performs path
     * compression such that all DisjointSetNodes along the path to the root
     * will all directly point to the root.
     *
     * @param curr the current DisjointSetNode to find the root of
     * @return the root of the current node
     */
    private DisjointSetNode<T> find(DisjointSetNode<T> curr) {
        DisjointSetNode<T> parent = curr.getParent();
        if (parent == curr) {
            return curr;
        } else {
            parent = find(curr.getParent());
            curr.setParent(parent);
            return parent;
        }
    }

    /**
     * Attempts to join the two data into the same set by pointing the parent
     * of one set to the parent of another set.
     *
     * @param first The first data to find the parent of
     * @param second The second data to find the parent of
     */
    public void union(T first, T second) {
        union(disjointSet.get(first), disjointSet.get(second));
    }

    /**
     * This is where the work is done for union(). This method finds the
     * roots of both passed in nodes and checks if they are the same root.
     * If not the same root, then the root with the least rank will point
     * to the node with higher rank using merge by rank.
     * ! for getRoots implementation, no new roots are added thru call to union(), but roots can be removed
     *
     * @param first The first DisjointSetNode to find the parent of
     * @param second The second DisjointSetNode to find the parent of
     * @implNote think about how you can modify this method to ensure
     * the roots set is properly updated.
     */
    private void union(DisjointSetNode<T> first, DisjointSetNode<T> second) {
        // Finds parents
        DisjointSetNode<T> firstParent = find(first);
        DisjointSetNode<T> secondParent = find(second);

        // If parents are different (different sets)
        if (firstParent != secondParent) {
            if (firstParent.getRank() < secondParent.getRank()) {
                firstParent.setParent(secondParent);
                // firstParent is no longer a root -> need to remove from rootSet
                rootSet.remove(firstParent.getData());
            } else {
                secondParent.setParent(firstParent);
                // secondParent is no longer a root -> need to remove from rootSet
                rootSet.remove(secondParent.getData());
                if (firstParent.getRank() == secondParent.getRank()) {
                    firstParent.setRank(firstParent.getRank() + 1);
                }
            }
        }
    }

    /**
     * Gets the set of all representative vertices (roots) in the disjoint set.
     * @return the set of all roots in the disjoint set.
     * @implSpec
     * <p> {@code O(1)} runtime
     * <p> The returned set must not allow modifications to the underlying graph.
     */
    public Set<T> getRoots() {
        // strategy: use Set backing structure to keep track of roots
        // this allows O(1) runtime (we js need to return the set)
        // update roots in find (new roots can be added), union (roots can be removed)

        return Collections.unmodifiableSet(this.rootSet); // this should be O(1)?
        // need to do this to prevent modifications to graph
    }
}