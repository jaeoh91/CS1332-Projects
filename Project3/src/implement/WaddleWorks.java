package implement;

import apply.StaticWaddleWorks;
import apply.Building;
import apply.Intersection;


import refactor.StaticGraph;
import refactor.DisjointSet;
import refactor.Edge;
import refactor.MutableGraph;
import refactor.Vertex;
import refactor.VertexDistance;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Queue;
import java.util.PriorityQueue;
import java.util.HashSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ArrayList;

import static implement.GraphAlgorithms.bfs;
import static implement.GraphAlgorithms.dijkstras;
import static implement.GraphAlgorithms.kruskals;

/**
 * Interface for your custom {@code WaddleWorks} implementation.
 * <p>
 * This API will function as a city planning app, performing
 * simulations of accessible road networks and an electrical grid.
 * More details are provided in the PDF.
 *
 * @apiNote DO NOT MODIFY THIS FILE!
 * @implNote {@code B} refers to the number of Buildings, {@code W}
 * refers to the number of wire segments, {@code I} refers to the
 * number of Intersections, and {@code R} refers to the number of
 * roads.
 * @version 1.0
 * @author CS 1332 TAs
 */
public class WaddleWorks implements StaticWaddleWorks {
    private MyGraph<Intersection> roadGraph;
    private MyGraph<Building> gridGraph;
    private DisjointSet<Intersection> disjointSet;
    private int neighborhoodsCount;

    /**
     * Constructor for WaddleWorks.
     * @param tempRoadGraph Roads graph
     * @param tempGridGraph Grid graph
     */
    public WaddleWorks(MutableGraph<Intersection> tempRoadGraph, MutableGraph<Building> tempGridGraph)   {
        if (tempRoadGraph == null || tempGridGraph == null) {
            throw new IllegalArgumentException("Cannot initialize new WaddleWorks with null road or grid graph");
        }

        // validate vertices & edges
        this.roadGraph = new MyGraph<>(tempRoadGraph.getVertices(), tempRoadGraph.getEdges());
        this.gridGraph = new MyGraph<>(tempGridGraph.getVertices(), tempGridGraph.getEdges());

        Set<Vertex<Building>> gridGraphVertices = this.gridGraph.getVertices();

        // Case: a building's .closest() not found in road graph
        for (Vertex<Building> vertex: gridGraphVertices) {
            Intersection closestIntersection = vertex.data().closest();
            if (closestIntersection != null)    {
                if (!roadGraph.containsVertex(new Vertex<Intersection>(closestIntersection)))    {
                    throw new IllegalArgumentException("Each building's closest Intersection must exist in roads");
                }
            }
        }

        // Case: gridGraph not connected
        // check connectedness w/ length of BFS/DFS traversalList
        if (this.gridGraph.getVertexCount() > 0)    {
            Vertex<Building> start = (Vertex<Building>) this.gridGraph.getVertices().toArray()[0]; // get random element
            List<Vertex<Building>> visitedList = bfs(start, this.gridGraph);
            if (visitedList.size() != this.gridGraph.getVertexCount())  {
                throw new IllegalArgumentException("Grid must be a fully connected graph!");
            }
        }

        // need to set up disjointSet + neighborhood counting
        this.neighborhoodsCount = 0;
        this.disjointSet = new DisjointSet<>();
        // we need to start by adding all to disjointSet bc no roads doesn't need to be a connected graph
        for (Vertex<Intersection> vertex: this.roadGraph.getVertices())   {
            this.disjointSet.find(vertex.data()); // put Intersection in disjointSet
            neighborhoodsCount++;
        }

        // connect the disjointSet
        for (Edge<Intersection> edge: this.roadGraph.getEdges())    {
            Intersection u = edge.u().data();
            Intersection v = edge.v().data();

            if (!this.disjointSet.find(u).equals(this.disjointSet.find(v))) { // i.e. if not alrdy connected
                this.disjointSet.union(u, v);
                neighborhoodsCount--; // ! need to do this always bc individual nodes were added as neighbors
            }
        }
    }
    /**
     * Adds a road to the road network.
     * <p>
     * If the added road connects two neighborhoods,
     * then return a true. Otherwise, return false.
     *
     * @param a the Intersection at one end of the road
     * @param b the Intersection at the other end of the road
     * @param duration the time it takes to traverse the road
     * @return whether the edge connects two neighborhoods
     * @implSpec {@code O(1)} runtime
     */
    public boolean addRoad(Intersection a, Intersection b, int duration)    {
        if (a == null || b == null) {
            throw new IllegalArgumentException("Cannot add a Road with null intersections");
        }
        if (duration < 0)   {
            throw new IllegalArgumentException("Road cannot have a negative duration");
        }

        Vertex<Intersection> aVertex = new Vertex<>(a);
        Vertex<Intersection> bVertex = new Vertex<>(b);

        // pre-increment neighborhoodscount because calling .find() later will create new neighborhoods
        // NEED TO CHECK THIS BEFORE ADDING EDGES BC OTHERWISE .adddEdge() will add the vertices
        if (!this.roadGraph.containsVertex(aVertex))    {
            neighborhoodsCount++;
        }
        if (!this.roadGraph.containsVertex(bVertex)) {
            neighborhoodsCount++;
        }

        this.roadGraph.addEdge(new Edge<>(
                aVertex,
                bVertex,
                duration)); // should add to vertices if necessary



        // check if we're connecting separate neighborhoods
        if (!this.disjointSet.find(a).equals(this.disjointSet.find(b)))  { 
            disjointSet.union(a, b);
            neighborhoodsCount--;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Adds a wire to the electrical grid.
     *
     * @param a the Building at one end of the wire
     * @param b the Building at the other end of the wire
     * @param length the length of the wire
     * @implSpec {@code O(1)} runtime
     */
    public void addWire(Building a, Building b, int length) {
        if (a == null || b == null) {
            throw new IllegalArgumentException("Cannot add a wire with null intersections");
        }
        if (length < 0)   {
            throw new IllegalArgumentException("wire cannot have a negative duration");
        }

        // check if Buildings
        Intersection closestToA = a.closest();
        Intersection closestToB = b.closest();
        if (closestToA != null && !roadGraph.containsVertex(new Vertex<>(closestToA)))  {
            throw new IllegalArgumentException("Each building's closest Intersection must exist in roads");
        }
        if (closestToB != null && !roadGraph.containsVertex(new Vertex<>(closestToB)))  {
            throw new IllegalArgumentException("Each building's closest Intersection must exist in roads");
        }

        // check that adding wont create a disconnected graph
        Vertex<Building> vertexA = new Vertex<>(a);
        Vertex<Building> vertexB = new Vertex<>(b);

        // only way it would disconnect graph is if neither vertex currently in vertices
        if (this.gridGraph.getVertexCount() > 0)    {
            if (!(this.gridGraph.containsVertex(vertexA) || this.gridGraph.containsVertex(vertexB)))  {
                throw new IllegalArgumentException("Can't add Wire that would cause grid to disconnect");
            }
        }

        gridGraph.addEdge(new Edge<>(vertexA, vertexB, length));
    }

    /**
     * Gets the number of neighborhoods in the road network.
     * <p>
     * A group of Intersections is considered to be in the same
     * neighborhood if each Intersection can reach every other
     * Intersection.
     *
     * @return the number of neighborhoods
     * @implSpec {@code O(1)} runtime
     */
    public int getNeighborhoodCount()   {
        return this.neighborhoodsCount; // had to use instance variable bc needs to be O(1)
    }

    /**
     * Gets a view of certain Intersections organized by neighborhood,
     * specifically Intersections with a number of adjacent roads in an
     * inclusive range [i, j].
     * <p>
     * This should return a map where the keys are the neighborhoods'
     * representative Intersections, and the values are a set of all
     * Intersections in the same neighborhood as the representative
     * Intersection that have a number of adjacent roads in the range.
     *
     * @param i the lower bound of the number adjacent of roads to consider
     * @param j the upper bound of the number adjacent of roads to consider
     * @return a curated map of the road network organized by neighborhoods
     * @implSpec
     * <p> {@code O(|R|+|I|)} runtime
     */
    public Map<Intersection, Set<Intersection>> getNeighborhoodsByConnections(int i, int j) {
        if (i < 0 || j < 0 || i > j)    {
            throw new IllegalArgumentException("Invalid bounds to getNeighborhoodsByConnections");
        }


        Map<Intersection, Set<Intersection>> neighborhoods = new HashMap<>(); // dont zero out
        // loop iterates I times
        for (Vertex<Intersection> vertex : this.roadGraph.getVertices())    {
            int curDegree = this.roadGraph.getNeighbors(vertex).size(); // degree is no. neighbors

            // if within bound, add to corect place in neigborhoods HashMap
            if (curDegree >= i && curDegree <= j)   { // remeber bounds r inclusive
                Intersection value = vertex.data();
                Intersection key = this.disjointSet.find(value);
                // [FIXED AFTER AG]:
                neighborhoods.get(key).add(value); // add to corresponding Set in neighborhoods
            }
        }
        return neighborhoods;
    }

    /**
     * Gets the minimum number of roads to travel between two Buildings.
     * If no path exists between the two Buildings, return -1.
     *
     * @param from the start Building
     * @param to the end Building
     * @return the minimum number of roads between the Buildings
     * @implSpec {@code O(|I|+|R|)} runtime
     */
    public int getMinBetween(Building from, Building to)    {
        // can't js use bfs, use modified implementation that keep track of # of hops & stops when we hit building To
        // bfs ideal bc since it finds path w/ smallest # of edges, compared to dfs that doesn't have such guarantee
        // therefore for bfs, first time that we "find" path from->to is the shortest one
        // remember bfs visits all vertices distance 1, distance 2, .... dist n

        if (from == null || to == null) {
            throw new IllegalArgumentException("Cannot find MinBetween null building(s)");
        }

        // check if buildings actually exist
        if (!this.gridGraph.containsVertex(new Vertex<>(from)) || !this.gridGraph.containsVertex(new Vertex<>(to))) {
            throw new IllegalArgumentException("Buildings don't actually exist in grid graph");
        }


        // Case: building(s) w/o closest intersection
        Intersection start = from.closest();
        Intersection finish = to.closest();
        if (start == null || finish == null)    {
            throw new IllegalArgumentException("Buildings must have closest Intersection");
        }


        // Case: closest intersection(S) dont actually exist in graph
        Vertex<Intersection> startVertex = new Vertex<>(start);
        Vertex<Intersection> finishVertex = new Vertex<>(finish);
        if (!this.roadGraph.containsVertex(startVertex) || !this.roadGraph.containsVertex(finishVertex))   {
            throw new IllegalArgumentException("Intersections must exist in roadGraph");
        }

        // edgecase: 0 distance
        if (startVertex.equals(finishVertex))   {
            return 0;
        }

        // initialize required data structures
        Set<Vertex<Intersection>> visitedSet = new HashSet<>();
        Queue<Vertex<Intersection>> queue = new LinkedList<>();
        // map storing vertices and their corresponding distances from start
        // need this to return minbtween
        HashMap<Vertex<Intersection>, Integer> hopsMap = new HashMap<>();


        // enque start and add it to visited
        queue.add(startVertex);
        visitedSet.add(startVertex);
        hopsMap.put(startVertex, 0); // start off w 0 hops

        // keep going until queue is empty
        while (!queue.isEmpty())    {
            // deque & actually visit
            Vertex<Intersection> curVertex = queue.remove();
            int hopsToCurVertex = hopsMap.get(curVertex); // query hop count to current vertex from hopsMap

            // find adj. vertices
            // this should return neighbors in order they are stored in the adj. list in graph
            List<VertexDistance<Intersection>> adjacentVertices = this.roadGraph.getNeighbors(curVertex);
            for (VertexDistance<Intersection> e : adjacentVertices)   {
                Vertex<Intersection> adjacentVertex = e.vertex();
                // if vertex not already traversed, add it to back of queue and mark as "visited"
                if (!visitedSet.contains(adjacentVertex))    {
                    int hopsToCurNeighborVertex = hopsToCurVertex + 1; // one more hop now, bc we solidify that path

                    if (adjacentVertex.equals(finishVertex))    { // if we reached the end, we found minBetween!
                        return hopsToCurNeighborVertex;
                    }

                    queue.add(adjacentVertex);
                    visitedSet.add(adjacentVertex);
                    hopsMap.put(adjacentVertex, hopsToCurNeighborVertex);
                }
            }
        }
        // if we never found finishVertex, no path exists
        return -1;
    }


    /**
     * Calculates the shortest duration path between two Buildings that
     * avoids certain types of Intersections. Prioritize avoiding the
     * undesirable Intersections, even if that means taking a longer path.
     * <p>
     * See {@link Intersection.Type} for details on what can be
     * constituted an avoided type of Intersection. If a path avoiding
     * these types is not possible, the path should include the minimum
     * number of avoided types.
     * <p>
     * The route should be constructed from source to destination,
     * including all Intersections along the way.
     *
     * @param from the start Building
     * @param to the end Building
     * @param avoid the set of Intersection types to avoid when possible
     * @return the list of Intersections to travel to, in order
     * @implSpec
     * <li> {@code O(|R|log(|R|))} runtime
     * <li> <b>No space complexity requirement</b>
     */
    public List<Intersection> calculateRoute(Building from, Building to, Set<Intersection.Type> avoid)  {
        if (from == null || to == null || avoid == null) {
            throw new IllegalArgumentException("Cannot calcualteRoute between null building(s) / avoid");
        }

        // check if buildings acc exist
        if (!this.gridGraph.containsVertex(new Vertex<>(from)) || !this.gridGraph.containsVertex(new Vertex<>(to))) {
            throw new IllegalArgumentException("Buildings don't actually exist in grid graph");
        }

        // Case: building(s) w/o closest intersection
        Intersection start = from.closest();
        Intersection finish = to.closest();
        if (start == null || finish == null)    {
            throw new IllegalArgumentException("Buildings must have closest Intersection");
        }

        // Case: closest intersection(S) dont actually exist in graph
        Vertex<Intersection> startVertex = new Vertex<>(start);
        Vertex<Intersection> finishVertex = new Vertex<>(finish);
        if (!this.roadGraph.containsVertex(startVertex) || !this.roadGraph.containsVertex(finishVertex))   {
            throw new IllegalArgumentException("Intersections must exist in roadGraph");
        }

        // edgecase: 0 distance
        if (startVertex.equals(finishVertex))   {
            List<Intersection> result = new ArrayList<Intersection>();
            result.add(start);
            return result;
        }

        // initialize required data structures (2)
        Set<Vertex<Intersection>> visitedSet = new HashSet<>(); // tracks vertices whose distances have been finalized

        // btw, no need for distanceMap datastruct because we don't care about outputting final distance

        // ! unique to calcRoute's implementation, we store RouteRecord to js use its custom compareTo()
        // bc min prioQeue, will give us the vertex with smallest cumulative distance
        // key to greedy implementation, we always want to take the smallest path at each step
        PriorityQueue<RouteRecord> priorityQueue = new PriorityQueue<>(); // need VertexDistance bc its comparable

        RouteRecord startRecord;
        // add start node to kick it off
        if (avoid.contains(start.type()))  { // apparently have to use .type()?
            startRecord = new RouteRecord(1, 0, startVertex, null);
        } else {
            startRecord = new RouteRecord(0, 0, startVertex, null);
        }

        priorityQueue.add(startRecord);
        RouteRecord finalRecord = null; // variable to hold last RouteRecord in long chain of finalrecords
        // kinda like a linkedlist ig

        // Keep looping until we still have vertices in prioQueue to explore & we have vertices left
        // check if visitedSet not full w/ size comparison to total number of vertices in graph
        // max of R iterations
        while (!priorityQueue.isEmpty() && visitedSet.size() < this.roadGraph.getVertexCount()) {
            // bc priorityqueue, O(log(n)) -> O(log(R)) since max of R elements
            RouteRecord curRecord = priorityQueue.remove();
            Vertex<Intersection> curVertex = curRecord.vertex();

            if (!visitedSet.contains(curVertex))   {
                visitedSet.add(curVertex);

                // check if we reached end yet, we can terminate then
                if (curVertex.equals(finishVertex)) {
                    finalRecord = curRecord;
                    break;
                }

                List<VertexDistance<Intersection>> adjacentVertices = this.roadGraph.getNeighbors(curVertex);
                for (VertexDistance<Intersection> e: adjacentVertices) {
                    Vertex<Intersection> adjacentVertex = e.vertex();
                    int distance = e.distance();

                    if (!visitedSet.contains(adjacentVertex))   {
                        // simple ternary to check if adjacent vertex has bad intersection
                        int newBadIntersections = avoid.contains(adjacentVertex.data().type()) ? 1 : 0;

                        // sum up values for the current adjacentVertex
                        int cumulativeBadIntersectionCount = curRecord.badIntersectionsCount() + newBadIntersections;
                        int cumulativeBadDuration = curRecord.totalDuration() + e.distance();

                        // create new RouteRecord object for the adjacentVertex
                        // remember that in reference to adjacentVertex curVertex is the predecessor in the path
                        RouteRecord newRecord = new RouteRecord(
                                cumulativeBadIntersectionCount, cumulativeBadDuration, adjacentVertex, curRecord);

                        // add this path to the prioQueue
                        // atp, no guarantee that this is the best path,
                        // but after the for loop ends shortest path will be at start of prioQueue & we will use that
                        priorityQueue.add(newRecord);
                    }
                }
            }
        }

        // we now have finalRecord and a list of prev records pointing to each other in their prev field
        LinkedList<Intersection> traversalList = new LinkedList<>();
        RouteRecord curRecord = finalRecord;

        // perform basically a linkedlist traversal
        while (curRecord != null)   {
            traversalList.addFirst(curRecord.vertex().data()); // remember they're in reverse order
            curRecord = curRecord.prevRecord();
        }
        return traversalList;
    }

    /**
     * Given a list of {@code k} candidate Buildings, select the one
     * that would be most "central" to the grid as a power site.
     * <p>
     * The most central power site is one that minimizes the average
     * distance to any arbitrary Building in the electrical grid graph.
     *
     * @param candidates the list of Building candidates
     * @return the best candidate to be a power site
     * @implSpec {@code O(k(|W|log(|W|)))} runtime
     */
    public Building getBestPowerSite(List<Building> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            throw new IllegalArgumentException("Invalid list of candidates");
        }

        Building candidateWBestPS = null;
        double minAverage = Double.MAX_VALUE; // should be big enough right

        // runs k times, each iteration is Wlog(W) because of dijkstras
        for (Building candidate: candidates)    {
            if (candidate == null)  {
                throw new IllegalArgumentException("Candidate cannot be null");
            }

            Vertex<Building> candidateVertex = new Vertex<>(candidate);

            if (!this.gridGraph.containsVertex(candidateVertex))    {
                throw new IllegalArgumentException("Candidate doesn't actually exist in graph");
            }

            // O(W log W)
            Map<Vertex<Building>, Integer> dijkstrasResult = dijkstras(candidateVertex, this.gridGraph);

            // sum up distances
            int curTotal = 0;
            for (Vertex<Building> curKey : dijkstrasResult.keySet()) {
                int curDist = dijkstrasResult.get(curKey);
                curTotal += curDist;
            }

            double curAverage = (double) curTotal / dijkstrasResult.size();

            if (curAverage < minAverage)    {
                minAverage = curAverage;
                candidateWBestPS = candidate;
            }
        }
        return candidateWBestPS;
    }

    /**
     * Consolidates the electrical grid to the minimum total length of
     * wire in the grid by removing all unnecessary wiring.
     *
     * @return the fraction of wire that was removed
     * @implSpec {@code O(|W|log(|W|))} runtime
     */
    public double consolidateGrid() {
        int prevTotalWeight = 0;
        for (Edge<Building> edge : this.gridGraph.getEdges())   {
            prevTotalWeight += edge.weight();
        }

        // edgecase no edges
        if (prevTotalWeight == 0)   {
            return (double) 0;
        }

        Set<Edge<Building>> mst = kruskals(this.gridGraph);

        int afterTotalWeight = 0;
        // need to cast to hashset bc we returned unmoifiable set in getEdges...
        // find edges we need to remove in the same loop
        Set<Edge<Building>> edgesToRemove = new HashSet<>(this.gridGraph.getEdges()); 
        for (Edge<Building> edge : mst)   {
            afterTotalWeight += edge.weight();
            edgesToRemove.remove(edge);
        }
        
        // remove edges from graph
        for (Edge<Building> edge : edgesToRemove)   {
            this.gridGraph.removeEdge(edge);
        }

        // return % saved, removed / o.g. total
        return  ((double) (prevTotalWeight - afterTotalWeight) / prevTotalWeight);
    }

    /**
     * Gets the current state of the road network.
     *
     * @return the current state of the road network
     */
    public StaticGraph<Intersection> getRoads() {
        return this.roadGraph;
    }

    /**
     * Gets the current state of the electrical grid.
     *
     * @return the current state of the electrical grid
     */
    public StaticGraph<Building> getGrid()  {
        return this.gridGraph;
    }

    /**
     * Private helper method for implementation of calculateRoute.
     * @param badIntersectionsCount Number of bad intersections encountered
     * @param totalDuration Duration from start vertex to this vertex, sum of edge weights
     * @param vertex Vertex to reference
     * @param prevRecord Stores RouteRecord object corresponding to the previous vertex in the path current vertex is in
     */
    private record RouteRecord(
            int badIntersectionsCount,
            int totalDuration,
            Vertex<Intersection> vertex,
            RouteRecord prevRecord) implements Comparable<RouteRecord>    {
        @Override
        public int compareTo(RouteRecord other)  {
            // compare by num of bad intersections encountered first
            int compareToRes = Integer.valueOf(this.badIntersectionsCount).compareTo(other.badIntersectionsCount);
            if (compareToRes == 0)  {
                // tiebreaker w/ shorter duration
                return Integer.valueOf(this.totalDuration).compareTo(other.totalDuration());
            } else {
                return compareToRes;
            }
        }
    }

}
