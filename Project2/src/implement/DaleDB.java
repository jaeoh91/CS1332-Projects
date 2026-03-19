package implement;

import apply.DaleRecord;
import apply.StaticDaleDB;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Implementation of DaleDB
 */
public class DaleDB implements StaticDaleDB {
    private final HashMap<String, Pond> pondMap;
    // Ponds will also be stored in a doubly-linked-list
    private Pond head; // points to most recently accessed Node
    private Pond tail; // points to least recently accessed Node

    /**
     *  Creates a new empty DaleDB
     */
    public DaleDB() {
        this.pondMap = new HashMap<>();
        this.head = null;
        this.tail = null;
    }

    /**
     * Inserts or updates a record in the database.
     *
     * @param daleRecord the {@link DaleRecord} to insert or update
     * @return the old record if replaced; {@code null} otherwise
     * @throws IllegalArgumentException if {@code record} is {@code null}
     * @implSpec {@code O(log r)} runtime
     */
    public DaleRecord putRecord(DaleRecord daleRecord) {
        if (daleRecord == null) {
            throw new IllegalArgumentException("Cannot put null record in DaleDB");
        }

        // Step 1: Query Pond
        // O(1) average
        Pond curPond = queryPond(daleRecord);

        // Step 2: put daleRecord in pond
        // O(log n)
        return curPond.put(daleRecord);
    }

    /**
     * Deletes a record from the database, given its pond and timestamp,
     * if it exists.
     *
     * @param pond the pond key (pond ID)
     * @param timestamp the sort key (event time)
     * @return the associated record
     * @throws IllegalArgumentException if {@code pond} is {@code null}
     * @throws NoSuchElementException if a matching record doesn't exist
     * @implSpec {@code O(log r)} runtime
     */
    public DaleRecord deleteRecord(String pond, long timestamp)    {
        if (pond == null)   {
            throw new IllegalArgumentException("Cannot delete from a null pond");
        }

        // Step 1: Find Pond
        // O(1)
        Pond curPond = accessPond(pond);

        // Step 2: Delete record
        // O(log n)
        DaleRecord removed = curPond.delete(timestamp);

        // if pond is now empty, remove the pond
        // O(log n)
        if (curPond.size() == 0)    {
            this.pondMap.remove(curPond.getName()); // remove from pondMap
            unlinkPond(curPond); // remove from DLL
        }
        return removed;
    }

    /**
     * Retrieves a record from the database, given its pond and timestamp.
     *
     * @param pond the pond key (pond ID)
     * @param timestamp the sort key (event time)
     * @return the associated record
     * @throws IllegalArgumentException if {@code pond} is {@code null}
     * @throws NoSuchElementException if a matching record doesn't exist
     * @implSpec {@code O(log r)} runtime
     */
    public DaleRecord getRecord(String pond, long timestamp)    {
        // O(1)
        Pond curPond = accessPond(pond); // throws IllegalArg & NoSuchElement (if pond doesn't exist)

        // O(log r)
        return curPond.get(timestamp); // throws NoSuchElement (if matching record not found in pond)
    }

    /**
     * Retrieves all records belonging to a pond.
     * <p>
     * The returned list must be sorted in ascending order of timestamp.
     *
     * @param pond the pond key
     * @return a sorted list of all records belonging to {@code pond}
     * @throws IllegalArgumentException if {@code pond} is {@code null}
     * @implSpec {@code O(r)} runtime
     */
    public List<DaleRecord> getPond(String pond)    {
        Pond curPond;
        try {
            curPond = accessPond(pond); // throws IllegalArgumentException
        } catch (NoSuchElementException _)  {  // if pond not found, need to catch exception
            return new ArrayList<>(); // if pond not found, we need to return empty list
        }
        return curPond.returnAllRecords();
    }

    /**
     * Retrieves the count of how many ponds are in the database.
     *
     * @return the number of ponds in DaleDB
     * @implSpec {@code O(1)} runtime
     */
    public int getPondCount()   {
        return this.pondMap.size();
    }

    /**
     * Retrieves all records within a timestamp range (inclusive) belonging
     * to a pond.
     * <p>
     * The returned list must be sorted in ascending order of timestamp.
     *
     * @param pond the pond key
     * @param start the start timestamp (inclusive)
     * @param end the end timestamp (inclusive)
     * @return a sorted list of records in the {@code pond}
     * @throws IllegalArgumentException if {@code pond} is {@code null}
     * @throws NoSuchElementException if the {@code pond} doesn't exist
     * @implSpec
     * <p>{@code O(r)} runtime
     * <p>{@code O(log r)} auxiliary space
     */
    public List<DaleRecord> getRecordRange(String pond, long start, long end)   {
        Pond curPond = accessPond(pond); // throws IllegalArg & NoSuchElement
        return curPond.returnAllInRange(start, end);
    }

    /**
     * Evicts the {@code k} least recently accessed ponds from the database.
     * <p>
     * The removed records should be returned as a list, sorted first by
     * ascending order of eviction, and second by ascending order of
     * timestamp.
     *
     * @param k the number of ponds to evict
     * @return a list of all records removed as a result of eviction
     * @throws IllegalArgumentException if {@code k} is nonpositive
     * @throws NoSuchElementException if there are less than {@code k} ponds
     * @implSpec {@code O(kr)} runtime
     */
    public List<DaleRecord> evict(int k)    {
        // Overall approach:
        // have DaleDB store ponds in two data structures (kinda)
        // main data struct is the HashMap<String, Pond> pondMap
        // second data structure is a Doubly-Linked List of Pond objects
        // daleDB has a head & tail, each pond has next & prev
        // DLL allows us to evict Ponds efficiently, and without having to search every Pond
        // head points to most recently used pond, tail points to least recently used
        // after each pond access, updateAccessOrder() is called to move the accessed Pond to head

        // edge cases
        if (k <= 0)  {
            throw new IllegalArgumentException("Cannot evict a negative number of Ponds");
        }
        if (this.pondMap.size() < k)    {
            throw new NoSuchElementException("Cannot evict more ponds than the total number of ponds");
        }

        ArrayList<DaleRecord> evictedRecords = new ArrayList<>();

        // iterate to k + 1 least recently accessed ponds
        // O(k)
        Pond curPond = this.tail;
        for (int i = 0; i < k; i++) {
            evictedRecords.addAll(curPond.returnAllRecords()); // O(r), technically O(2r)
            this.pondMap.remove(curPond.getName()); // O(1) average, remove from HashMap
            curPond = curPond.getPrev();
        }

        this.tail = curPond;
        if (curPond != null)    {
            curPond.setNext(null);
        } else { // Need to check for edge case k == this.pondMap.size()  (i.e. all ponds evicted)
            // curPond will be null, therefore tail will have already been set to null
            this.head = null;
        }


        return evictedRecords;
    }

    /**
     * Evicts the least recently accessed pond from the database.
     * <p>
     * The removed records should be returned as a list, sorted by
     * ascending order of timestamp.
     *
     * @return a list of all records removed as a result of eviction
     * @throws NoSuchElementException if the database is empty
     * @implSpec {@code O(r)} runtime
     */
    public List<DaleRecord> evict() {
        return evict(1);
    }

    /**
     * Computes the maximum concurrent ducks in a pond.
     * <p>
     * This method should examine {@link DaleRecord.BoundaryEvent}
     * records over time, taking the number of ducks and cows at each
     * event occurrence into account.
     *
     * @param pond the pond key
     * @return the maximum number of ducks in a pond at one time
     * @throws IllegalArgumentException if {@code pond} is {@code null}
     * @throws NoSuchElementException if {@code pond} not in database
     * @implSpec {@code O(r)} runtime
     * <p> {@code O(log r)} auxiliary space
     */
    public int getPeakConcurrentOccupancy(String pond)  {
        Pond curPond = accessPond(pond); // throws IllegalArg & NoSuchElement
        int max = 0;
        int currentOccupancy = 0;

        // r iterations, O(1) each iteration -> O(r)
        for (DaleRecord curRecord : curPond.returnAllRecords()) {
            // pattern variable, kinda cool, automatically casts
            if (curRecord instanceof DaleRecord.BoundaryEvent curBoundaryRecord)  {
                // calculate # of ducks
                int numAnimals = curBoundaryRecord.names().size();
                int numCows = curBoundaryRecord.numCows();
                int numDucks = numAnimals - numCows;

                // += or -= check
                currentOccupancy =
                        (curBoundaryRecord.type() == DaleRecord.BoundaryEvent.TransitionType.ENTRANCE)
                                ? currentOccupancy + numDucks
                                : currentOccupancy - numDucks;

                // update max
                if (currentOccupancy > max) {
                    max = currentOccupancy;
                }
            }
        }

        return max;
    }

    /**
     * Retrieves the most frequent visitor to the specified pond.
     * <p>
     * This method should examine {@link DaleRecord.BoundaryEvent}
     * records over time, taking into account who is visiting the
     * pond the most times.
     *
     * @param pond the pond key
     * @return the name of the most frequent visitor; {@code null} otherwise
     * @throws IllegalArgumentException if {@code pond} is {@code null}
     * @throws NoSuchElementException if {@code pond} not in database
     * @implSpec
     * <p> {@code O(r)} runtime

     */
    public String getMostFrequentVisitor(String pond)   {
        Pond curPond = accessPond(pond); // throws IllegalArg & NoSuchElement
        HashMap<String, Integer> visitorLog = new HashMap<>();


        String mostFrequent = null;
        int max = -1;

        // O(kr)
        for (DaleRecord curRecord : curPond.returnAllRecords()) { // iterates r times
            if (curRecord instanceof DaleRecord.BoundaryEvent curBoundaryRecord)  {
                for (String curName : curBoundaryRecord.names())    { // iterates k times
                    int curVisitors = 0;
                    try {
                        curVisitors = visitorLog.get(curName); // O(1)
                    } catch (NoSuchElementException _) {

                    }

                    curVisitors++;
                    visitorLog.put(curName, curVisitors); // O(1)

                    if (curVisitors > max)  {
                        max = curVisitors;
                        mostFrequent = curName;
                    }
                }
            }
        }

        return mostFrequent;
    }

    /**
     * Merges fishing reports for each duck within the specified pond.
     * <p>
     * For each duck, this consolidates all readings into a single record
     * at the latest timestamp, pruning older records. The latest timestamp
     * for each duck's fish report will have all lists concatenated to one
     * another in ascending order of timestamp. The result maps each latest
     * timestamp to the list of pruned timestamps for that sensor, also
     * sorted in ascending order.
     *
     * @param pond the pond key
     * @return a map where each key is the latest timestamp and each value
     *         is the list of timestamps that were merged into it, in
     *         ascending order
     * @throws IllegalArgumentException if {@code pond} is {@code null}
     * @throws NoSuchElementException if {@code pond} not in database
     * @implSpec {@code O(r + k log r)} runtime, where {@code k << r}
     * k - # of fishReports
     * r - # of records
     */
    public HashMap<Long, List<Long>> mergeReports(String pond)  {
        Pond curPond = accessPond(pond); // throws IllegalArg & NoSuchElement

        HashMap<String, ArrayList<DaleRecord.FishReport>> reportsForEachDuck = new HashMap<>();
        HashMap<Long, List<Long>> outMap = new HashMap<>();

        // Part 1: collate reports for each duck & remove all fishReports from Pond

        // ! traverses in ascending timestamp order
        // iterates r times
        for (DaleRecord curRecord : curPond.returnAllRecords())    { // O(1) fetch, returnAllRecords is also O(r)
            // iterates k times
            if (curRecord instanceof DaleRecord.FishReport curFishReport)   {
                String duckName = curFishReport.duck();
                ArrayList<DaleRecord.FishReport> curList;
                try {
                    curList = reportsForEachDuck.get(duckName);
                } catch (NoSuchElementException _)  {
                    curList = new ArrayList<>();
                    reportsForEachDuck.put(duckName, curList);
                }
                curList.add(curFishReport);

                curPond.delete(curFishReport.timestamp()); // O(log r)
            }
        }
        // Part 1 time complexity:
        // returnAllRecords is O(r)
        // outer loop runs O(r) times, O(1) each
        // inner if statement runs k times,  O(log r) each
        // O(r) + r * O(1) + (k * O(log r)) -> O(r) + O(k log r)


        // Part 2: combine into one report / list of timestamps per duck,
        // loops for (# of ducks) times, which is <= k
        // let # of ducks = d
        for (String curDuckName : reportsForEachDuck.keySet())  {
            ArrayList<DaleRecord.FishReport> curList = reportsForEachDuck.get(curDuckName);
            ArrayList<Double> weights = new ArrayList<>();
            ArrayList<Long> timestamps = new ArrayList<>();

            Long latestTimeStamp = null;
            // in total the two for loops will call the inner code k times, since there are k fish reports
            for (int i = 0; i < curList.size(); i++)    {
                DaleRecord.FishReport curFishReport = curList.get(i); // O(1)
                weights.addAll(curFishReport.weights()); // O(1)*

                // since ascending order of timestamp, last record will have the greatest timestamp
                if (i == curList.size() - 1)    { // if last record, i.e. record with the latest timestamp
                    latestTimeStamp = curFishReport.timestamp();
                } else {
                    timestamps.add(curFishReport.timestamp());
                }
            }

            DaleRecord.FishReport newFishReport = new DaleRecord.FishReport(
                    pond,
                    latestTimeStamp,
                    curDuckName,
                    weights
            );
            curPond.put(newFishReport); // log(r)
            outMap.put(latestTimeStamp, timestamps);
        }

        // Part 2: time complexity
        // remember d = # of ducks <= k
        // inner for loop: k iterations * O(1)
        // outer for loop: d iterations * O(log r)
        // O(k) + O(d log r)


        // Overall time complexity:
        // part 1 - O(r) + O(k log r)
        // part 2 - O(k) + O(d log r)
        // since r >= k, k >= d
        // total - O(r) + O(k log r)
        return outMap;
    }

    // // // // // // // // // // // // // // // // // // // // // // // // // // // // // //
    // HELPER METHODS & CLASSES
    // // // // // // // // // // // // // // // // // // // // // // // // // // // // // //

    /**
     * Private helper method to find the correct Pond or create a new Pond
     * @param curRecord Record whose pondName will be the search field
     * @return The matching / newly created Pond
     * O(1) average
     */
    private Pond queryPond(DaleRecord curRecord)   {
        return this.queryPond(curRecord.pond());
    }

    /**
     * Private helper method to find the correct Pond or create a new Pond
     * @param pondName Name of the pond
     * @return The matching / newly created Pond
     * O(1) average
     */
    private Pond queryPond(String pondName) {
        Pond curPond;
        try {
            curPond = accessPond(pondName);
        } catch (NoSuchElementException _) { // if pond doesn't exist
            curPond = new Pond(pondName); // create the pond
            this.pondMap.put(pondName, curPond); // put the new pond in pondMap
            updateAccessOrder(curPond); // access the newly created Pond
        }
        return curPond;
    }

    /**
     * Finds Pond in DaleDB matching a pondName
     * Calls updateAccessOrder if pond found
     * @param pondName Unique name of pond
     * @return Pond matching the pondName
     * @throws NoSuchElementException If Pond Matching the pondName is not found
     * @throws IllegalArgumentException If pondName is null
     */
    private Pond accessPond(String pondName)    {
        Pond curPond = this.pondMap.get(pondName);
        updateAccessOrder(curPond);
        return curPond;
    }

    /**
     * Updates the access order of the DaleDB with a newly accessed Pond
     * @param mostRecentlyAccessed The newly accessed pond
     */
    private void updateAccessOrder(Pond mostRecentlyAccessed)   {
        // if already most recent Pond, nothing to do
        if (mostRecentlyAccessed == this.head)  {
            return;
        }

        // edge case: DLL empty / no Ponds exist yet
        if (this.tail == null)  {
            this.tail = mostRecentlyAccessed;
        } else {
            // (1) Remove node from position & fill gap in DLL
            unlinkPond(mostRecentlyAccessed);

            // (2) reassign the node's prev and next
            mostRecentlyAccessed.setPrev(null);
            mostRecentlyAccessed.setNext(this.head);
            this.head.setPrev(mostRecentlyAccessed);
        }
        // (1 or 3) Add node to head of DLL
        this.head = mostRecentlyAccessed;
    }

    /**
     * Removes a Pond from the Doubly-Linked List of ponds
     * @param remove Pond to remove
     */
    private void unlinkPond(Pond remove)   {
        if (remove == null) {
            return;
        }
        Pond prev = remove.getPrev();
        Pond next = remove.getNext();

        if (prev != null)   {
            prev.setNext(next);
        } else if (this.head == remove) { // edge case: Pond was head, need to reassign head
            // needed else if because prev == null doesn't necessarily guarantee remove == head
            this.head = next;
        }
        if (next != null)   {
            next.setPrev(prev);
        } else if (this.tail == remove) { // edge case: Pond was tail, need to reassign tail
            this.tail = prev;
        }
    }


    /**
     * Private inner class representing each pond in the DaleDB.
     * Ponds will be doubly-linked for a DLL-backed implementation of the evict() method
     */
    private static class Pond   {
        private final String name;
        private final TreeMap<Long, DaleRecord> recordsMap;
        private Pond prev;
        private Pond next;

        /**
         * Creates a new pond.
         * @param name The name of the Pond
         */
        public Pond(String name)  {
            this.name = name;
            this.recordsMap = new TreeMap<>();
        }

        /**
         * Puts a record into the Pond
         * @param curRecord The record to put in the pond
         * @return The previous record if replaced, null if newly inserted
         */
        public DaleRecord put(DaleRecord curRecord)    {
            return this.recordsMap.put(curRecord.timestamp(), curRecord);
        }

        /**
         * Retrieves a record from the Pond matching a timestamp
         * @param timestamp Timestamp to look for
         * @return Record matching provided timestamp
         * @throws NoSuchElementException If a matching record was not found
         * @throws IllegalArgumentException If timestamp is null
         */
        public DaleRecord get(Long timestamp)   {
            return this.recordsMap.get(timestamp);
        }

        /**
         * Removes a record from the Pond.
         * @param timestamp Timestamp to identify record to remove
         * @return The removed record
         * @throws NoSuchElementException If matching element not found
         */
        public DaleRecord delete(Long timestamp)   {
            return this.recordsMap.remove(timestamp);
        }

        /**
         * Returns List (specifically an ArrayList) containing all records in the Pond
         * @return List containing all records in the Pond, in ascending order
         */
        public List<DaleRecord> returnAllRecords() {
            return this.recordsMap.values();
        }

        /**
         * Return all records whose timestamps are in the range [start, end]
         * @param start Lower bound of the range, inclusive
         * @param end Upper bound of the range, inclusive
         * @return List of all records in range
         */
        public List<DaleRecord> returnAllInRange(long start, long end)  {
            return this.recordsMap.getRange(start, end);
        }

        /**
         * Gets the size of the Pond
         * @return The size of the Pond
         */
        public int size()   {
            return recordsMap.size();
        }

        /**
         * Getter for name.
         * @return The pond's name
         */
        public String getName() {
            return this.name;
        }

        /**
         * Getter for previous Pond
         * @return The previous Pond
         */
        public Pond getPrev() {
            return this.prev;
        }

        /**
         * Getter for the next Pond
         * @return The next Pond
         */
        public Pond getNext()   {
            return this.next;
        }

        /**
         * Setter for the previous pond
         * @param newPrev The new previous pond
         */
        public void setPrev(Pond newPrev)   {
            this.prev = newPrev;
        }

        /**
         * Setter for the next pond
         * @param newNext The new next pond
         */
        public void setNext(Pond newNext)   {
            this.next = newNext;
        }
    }
}
