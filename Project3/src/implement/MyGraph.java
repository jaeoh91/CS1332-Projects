package implement;

import refactor.Edge;
import refactor.MutableGraph;
import refactor.Vertex;
import refactor.VertexDistance;

import java.util.*;

/**
 * Adjacency List based implementation of MutableGraph
 *
 * @apiNote DO NOT MODIFY THIS FILE!
 * @version 1.0
 * @author joh426
 *
 * @param <T> the type of data held within the vertices
 */
public class MyGraph<T> extends MutableGraph<T> {
    private Map<Vertex<T>, List<VertexDistance<T>>> adjacencyList;

    /**
     * Constructor for a MutableGraph
     * Takes in a set of vertices and edges and initializes the graph.
     */
    public MyGraph(Set<Vertex<T>> vertices, Set<Edge<T>> edges) {
        // use constructor in MutableGraph, handles null edge cases and also initializes vertices & edges
        super(vertices, edges);

        // initialize & set up the aedj list
        this.adjacencyList = new HashMap<>();
        for (Vertex<T> vertex : this.vertices)   {
            if (vertex == null)   {
                throw new IllegalArgumentException("Cannot add null vertex");
            }
            adjacencyList.put(vertex, new ArrayList<>());
        }

        for (Edge<T> edge : this.edges) {
            if (edge == null)   {
                throw new IllegalArgumentException("Cannot add null edge");
            }

            Vertex<T> u = edge.u();
            Vertex<T> v = edge.v();
            int weight = edge.weight();

            // check for self loop
            if (v.equals(u)) { // this should? work as Vertex is a record
                throw new IllegalArgumentException("No self loops permitted in my graph!");
            }

            // validate that vertex set used to construct the graph contains every vertex referenced in edges
            // i.e that the edges set does not have any edges that do not exist in vertices
            if (!(this.containsVertex(u) && this.containsVertex(v)))   {
                throw new IllegalArgumentException("Edges reference a vertex not found in vertices");
            }

            // add both ways to adj list
            this.adjacencyList.get(u).add(new VertexDistance<>(v, weight));
            this.adjacencyList.get(v).add(new VertexDistance<>(u, weight));
        }
    }

    /**
     * Adds a vertex to the graph.
     * See PDF for exception handling.
     *
     * @param vertex the vertex to add
     */
    public void addVertex(Vertex<T> vertex) {
        // check if vertex already exist in graph or if vertex is null
        if (this.containsVertex(vertex)) { // performs null check on vertex
            throw new IllegalArgumentException("Vertex already exists in graph");
        }

        this.vertices.add(vertex);
        this.adjacencyList.put(vertex, new ArrayList<>());
    }

    /**
     * Removes a vertex from the graph. It should also remove all adjacent edges.
     * See PDF for exception handling.
     *
     * @param vertex the vertex to remove
     */
    public void removeVertex(Vertex<T> vertex) {
        if (!this.containsVertex(vertex))   { // performs null check btw
            throw new IllegalArgumentException("Cannot remove vertex that doesn't exist in the graph");
        }

        List<VertexDistance<T>> distancesList = this.adjacencyList.get(vertex);
        for (VertexDistance<T> cur : distancesList) {
            Vertex<T> neighbor = cur.vertex();
            int distance = cur.distance();
            // entry that the neighbor will have in its adjacencyList
            // i.e. entry to remove from neighbor's adj list
            VertexDistance<T> neighborEntry = new VertexDistance<>(vertex, distance);

            // remove entry from neighbor's adj list
            List<VertexDistance<T>> neighborAdjList = this.adjacencyList.get(neighbor);
            neighborAdjList.remove(neighborEntry);

            // remove from edges
            // order of arg1 & arg2 doesn't matter bc of Edge's equals function implementation
            edges.remove(new Edge<>(vertex, neighbor, distance));
        }

        this.vertices.remove(vertex);
        // remove entry in adjlist corresponding to the vertex to remove
        this.adjacencyList.remove(vertex);
    }

    /**
     * Adds an edge to the graph. It should also add the vertices if needed.
     * See PDF for exception handling.
     *
     * @param edge the edge to add
     */
    public void addEdge(Edge<T> edge)   {
        if (this.containsEdge(edge))    { // also does null check
            throw new IllegalArgumentException("Cannot add an edge that already exists in the graph");
        }

        Vertex<T> u = edge.u();
        Vertex<T> v = edge.v();
        int distance = edge.weight();

        // self loop check
        if (u.equals(v))    {
            throw new IllegalArgumentException("No self loops allowed!");
        }

        // add vertices in the edge if they don't already exist
        // performs null checks on the vertices too
        if (!this.containsVertex(u))    {
            this.addVertex(u);
        }
        if (!this.containsVertex(v))    {
            this.addVertex(v);
        }

        // add to adj list
        List<VertexDistance<T>> uAdjList = this.adjacencyList.get(u);
        List<VertexDistance<T>> vAdjList = this.adjacencyList.get(v);
        uAdjList.add(new VertexDistance<>(v, distance));
        vAdjList.add(new VertexDistance<>(u, distance));

        // add to edges
        this.edges.add(edge);
    }

    /**
     * Removes an edge from the graph.
     * See PDF for exception handling.
     *
     * @param edge the edge to remove
     */
    public void removeEdge(Edge<T> edge)    {
        if (!this.containsEdge(edge))   { // null check performed
            throw new IllegalArgumentException("Cannot remove a non-existent edge");
        }

        Vertex<T> u = edge.u();
        Vertex<T> v = edge.v();
        int distance = edge.weight();

        if (!(this.containsVertex(u) && this.containsVertex(v)))   {
            throw new IllegalArgumentException("Cannot remove edges with vertices that don't exist on graph");
        }

        // remove from adj list
        List<VertexDistance<T>> uAdjList = this.adjacencyList.get(u);
        List<VertexDistance<T>> vAdjList = this.adjacencyList.get(v);
        uAdjList.remove(new VertexDistance<>(v, distance));
        vAdjList.remove(new VertexDistance<>(u, distance));

        this.edges.remove(edge);
    }

    /**
     * Gets the adjacency list of this graph.
     *
     * @return the adjacency list of this graph
     */
    public Map<Vertex<T>, List<VertexDistance<T>>> getAdjList()    {
        return this.adjacencyList;
    }

    /**
     * Gets all adjacent vertices of and their distances from a given vertex.
     * See PDF for exception handling.
     *
     * @param vertex the vertex to get neighbors of
     * @return a list of all neighboring vertices and their distances
     */
    public List<VertexDistance<T>> getNeighbors(Vertex<T> vertex)   {
        if(!this.containsVertex(vertex))    {
            throw new IllegalArgumentException("Cannot get neighbors for nonexistent vertex");
        }
        return this.adjacencyList.get(vertex);
    }
}
