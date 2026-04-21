package implement;

import refactor.DisjointSet;
import refactor.Edge;
import refactor.Vertex;
import refactor.VertexDistance;
import refactor.StaticGraph;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashSet;
import java.util.Queue;
import java.util.HashMap;
import java.util.PriorityQueue;


/**
 * Your implementation of various different graph algorithms.
 */
public class GraphAlgorithms {
    /**
     * Performs a breadth-first search (BFS) on the input graph, starting at
     * the parameterized starting vertex.
     * <p>
     * When exploring a vertex, explore in the order of neighbors returned by
     * the adjacency list. Failure to do so may cause you to lose points.
     *
     * @param <T>   the generic typing of the data
     * @param start the vertex to begin the BFS on
     * @param graph the graph to search through
     * @return list of vertices in visited order
     * @throws IllegalArgumentException if any input is null, or if start
     *                                  doesn't exist in the graph
     */
    public static <T> List<Vertex<T>> bfs(Vertex<T> start, StaticGraph<T> graph) {
        if (start == null || graph == null) {
            throw new IllegalArgumentException("Cannot run BFS with null starting vertex or null graph");
        }

        if (!graph.containsVertex(start))   {
            throw new IllegalArgumentException("Cannot run BFS with start vertex that doesn't exist in graph");
        }

        // initialize required data structures
        Set<Vertex<T>> visitedSet = new HashSet<>();
        Queue<Vertex<T>> queue = new LinkedList<>();
        List<Vertex<T>> traversalList = new ArrayList<>();

        // enque start and add it to visited
        queue.add(start);
        visitedSet.add(start);

        // keep going until queue is empty
        while (!queue.isEmpty())    {
            // deque & actually visit
            Vertex<T> curVertex = queue.remove();
            traversalList.addLast(curVertex); // actually visit

            // find adj. vertices
            // this should return neighbors in order they are stored in the adj. list in graph
            List<VertexDistance<T>> adjacentVertices = graph.getNeighbors(curVertex);
            for (VertexDistance<T> e : adjacentVertices)   {
                Vertex<T> adjacentVertex = e.vertex();
                // if vertex not already traversed, add it to back of queue and mark as "visited"
                if (!visitedSet.contains(adjacentVertex))    {
                    queue.add(adjacentVertex);
                    visitedSet.add(adjacentVertex);
                }
            }
        }

        return traversalList;
    }

    /**
     * Performs a depth-first search (DFS) on the input graph, starting at
     * the parameterized starting vertex.
     * <p>
     * When exploring a vertex, explore in the order of neighbors returned by
     * the adjacency list. Failure to do so may cause you to lose points.
     *
     * @param <T>   the generic typing of the data
     * @param start the vertex to begin the DFS on
     * @param graph the graph to search through
     * @return list of vertices in visited order
     * @throws IllegalArgumentException if any input is null, or if start
     *                                  doesn't exist in the graph
     * @implSpec You MUST implement this method recursively, or else you will
     * lose all points for this method.
     */
    public static <T> List<Vertex<T>> dfs(Vertex<T> start, StaticGraph<T> graph) {
        if (start == null || graph == null) {
            throw new IllegalArgumentException("Cannot run DFS with null starting vertex or null graph");
        }

        if (!graph.containsVertex(start))   {
            throw new IllegalArgumentException("Cannot run DFS with start vertex that doesn't exist in graph");
        }

        // initialize data structures
        Set<Vertex<T>> visitedSet = new HashSet<>();
        List<Vertex<T>> traversalList = new ArrayList<>();

        // start chain of recursive calls
        dfs(start, graph, visitedSet, traversalList);

        return traversalList;
    }

    /**
     * Recursive helper method for DFS.
     * 
     * @param <T>   the generic typing of the data
     * @param curr  the current vertex being explored
     * @param g     the graph being searched through
     * @param vSet  the set of vertices that have already been visited
     * @param list  the list of vertices in visited order
     */
    private static <T> void dfs(Vertex<T> curr, StaticGraph<T> g, Set<Vertex<T>> vSet, List<Vertex<T>> list) {
        vSet.add(curr);
        list.add(curr); // visit first, then explore

        List<VertexDistance<T>> adjacentVertices = g.getNeighbors(curr);
        for (VertexDistance<T> e : adjacentVertices)    {
            Vertex<T> adjacentVertex = e.vertex();
            if (!vSet.contains(adjacentVertex)) {
                dfs(adjacentVertex, g, vSet, list); // recursive call if not visited
            }
        }
    }

    /**
     * Finds the single-source shortest distance between the start vertex and
     * all vertices given a weighted graph (you may assume non-negative edge
     * weights).
     * <p>
     * Return a map of the shortest distances such that the key of each entry
     * is a node in the graph and the value for the key is the shortest distance
     * to that node from start, or {@link Integer#MAX_VALUE} (representing
     * infinity) if no path exists.
     *
     * @param <T>   the generic typing of the data
     * @param start the vertex to begin the Dijkstra's on (source)
     * @param graph the graph we are applying Dijkstra's to
     * @return a map of the shortest distances from start to every
     * other node in the graph
     * @throws IllegalArgumentException if any input is null, or if start
     *                                  doesn't exist in the graph.
     */
    public static <T> Map<Vertex<T>, Integer> dijkstras(Vertex<T> start,
                                                        StaticGraph<T> graph) {
        if (start == null || graph == null) {
            throw new IllegalArgumentException("Cannot run dijkstras with null start or graph");
        }

        if (!graph.containsVertex(start))   {
            throw new IllegalArgumentException("Cannot run dijkstras with start vertex that doesn't exist in graph");
        }

        // initialize required data structures
        Set<Vertex<T>> visitedSet = new HashSet<>(); // tracks vertices whose distances have been finalized
        Map<Vertex<T>, Integer> distanceMap = new HashMap<>();

        // bc min prioQeue, will give us the vertex with smallest cumulative distance
        // key to greedy implementation, we always want to take the smallest path at each step
        PriorityQueue<VertexDistance<T>> priorityQueue = new PriorityQueue<>(); // need VertexDistance bc its comparable

        // add start node to kick it off
        priorityQueue.add(new VertexDistance<>(start, 0));
        for (Vertex<T> vertex: graph.getVertices()) {
            distanceMap.put(vertex, Integer.MAX_VALUE);
            // default to this so if no path exists MAX_VALUE is returned
        }
        distanceMap.put(start, 0);

        // Keep looping until we still have vertices in prioQueue to explore & we have vertices left
        // check if visitedSet not full w/ size comparison to total number of vertices in graph
        while (!priorityQueue.isEmpty() && visitedSet.size() < graph.getVertexCount()) {
            VertexDistance<T> temp = priorityQueue.remove();
            Vertex<T> vertex = temp.vertex();
            int distance = temp.distance();

            if (!visitedSet.contains(vertex))   {
                visitedSet.add(vertex);
                distanceMap.put(vertex, distance);

                List<VertexDistance<T>> adjacentVertices = graph.getNeighbors(vertex);
                for (VertexDistance<T> e: adjacentVertices) {
                    Vertex<T> adjacentVertex = e.vertex();

                    if (!visitedSet.contains(adjacentVertex))   {
                        int distanceToAdjacent = e.distance();
                        int culumativeDistance = distance + distanceToAdjacent;
                        // add this path to the prioQueue
                        // atp, no guarantee that this is the best path,
                        // but after the for loop ends shortest path will be at start of prioQueue & we will use that
                        priorityQueue.add(new VertexDistance<>(adjacentVertex, culumativeDistance));
                    }
                }
            }
        }
        return distanceMap;
    }

    /**
     * Runs Prim's algorithm on the given graph and returns the Minimum
     * Spanning Tree (mst) in the form of a set of Edges. If the graph is
     * disconnected and therefore no valid mst exists, return null.
     * <p>
     * You may assume that the passed in graph is undirected.
     * <p>
     * @param <T> the generic typing of the data
     * @param start the vertex to begin Prims on
     * @param graph the graph we are applying Prims to
     * @return the mst of the graph or null if there is no valid mst
     * @throws IllegalArgumentException if any input is null, or if start
     *                                  doesn't exist in the graph.
     */
    public static <T> Set<Edge<T>> prims(Vertex<T> start, StaticGraph<T> graph) {
        if (start == null || graph == null) {
            throw new IllegalArgumentException("Cannot run dijkstras with null start or graph");
        }

        if (!graph.containsVertex(start))   {
            throw new IllegalArgumentException("Cannot run dijkstras with start vertex that doesn't exist in graph");
        }

        Set<Vertex<T>> visitedSet = new HashSet<>(); // will contain vertices currently covered by mst
        Set<Edge<T>> mst = new HashSet<>();
        // our frontier
        PriorityQueue<Edge<T>> priorityQueue = new PriorityQueue<>(); // Edge is comparable so we're good
        // minheap so .remove() will give shortest path

        // kickstart prims w/ start node
        visitedSet.add(start);
        List<VertexDistance<T>> adjacentToStart = graph.getNeighbors(start);
        for (VertexDistance<T> temp: adjacentToStart)    {
            Vertex<T> vertex = temp.vertex();
            int distance = temp.distance();

            // need to build edge ourselves bc no function to return neighboring edges
            Edge<T> curEdge = new Edge<>(start, vertex, distance);

            priorityQueue.add(curEdge);
        }

        // same loop continuation logic as djiskras
        while (!priorityQueue.isEmpty() && visitedSet.size() < graph.getVertexCount())  {
            Edge<T> curEdge = priorityQueue.remove();

            Vertex<T> v = curEdge.u(); // u is the known one / one alrdy in visitedSet
            Vertex<T> w = curEdge.v(); // v is the one to explore

            if (!visitedSet.contains(w))    { // check avoids cycle
                mst.add(curEdge);
                visitedSet.add(w);

                // add all edges to prioQueue, shortest will be at start
                List<VertexDistance<T>> adjacentsToW = graph.getNeighbors(w);
                for (VertexDistance<T> temp: adjacentsToW)   {
                    Vertex<T> adjacentToW = temp.vertex();
                    int distance = temp.distance();

                    if (!visitedSet.contains(adjacentToW))  {
                        Edge<T> newEdge = new Edge<>(w, adjacentToW, distance);
                        priorityQueue.add(newEdge);
                    }
                }
            }
        }

        // check if disconnected graph
        int v = graph.getVertexCount();
        // mst must have V-1 edges!!
        if (mst.size() != v - 1)    {
            return null;
        } else  {
            return mst;
        }
    }

    /**
     * Runs Kruskal's algorithm on the given graph and returns the Minimal
     * Spanning Tree (mst) in the form of a set of Edges. If the graph is
     * disconnected and therefore no valid mst exists, return null.
     * <p>
     * You may assume that the passed in graph is undirected.
     * <p>
     * @param <T>   the generic typing of the data
     * @param graph the graph we are applying Kruskals to
     * @return the mst of the graph or null if there is no valid mst
     * @throws IllegalArgumentException if any input is null
     */
    public static <T> Set<Edge<T>> kruskals(StaticGraph<T> graph) {
        if (graph == null) {
            throw new IllegalArgumentException("Cannot run kruskals with null graph");
        }

        DisjointSet<Vertex<T>> disjointSet = new DisjointSet<>();
        Set<Edge<T>> mst = new HashSet<>();

        // initialize prioQueue w/ all edges in graph
        // basically sorts all edges in graph from shortest to longest
        PriorityQueue<Edge<T>> priorityQueue = new PriorityQueue<>(graph.getEdges());

        int capitalV = graph.getVertexCount(); // come on checkstyle js let me use uppercase...

        // is the second check redundant.?
        while (!priorityQueue.isEmpty() && mst.size() < capitalV - 1)  {
            Edge<T> curEdge = priorityQueue.remove();

            Vertex<T> v = curEdge.u();
            Vertex<T> e = curEdge.v();

            // if v and e are in different set
            // rmbr find() on disjointset returns shared parent
            // if not alrdy in disjointset, find() sets the vertex as new parent
            if (!disjointSet.find(v).equals(disjointSet.find(e))) { // prevents cycle
                mst.add(curEdge);
                disjointSet.union(v, e);
            }
        }

        // check if disconnected graph
        // mst must have V-1 edges!!
        if (mst.size() != capitalV - 1)    {
            return null;
        } else  {
            return mst;
        }
    }

}
