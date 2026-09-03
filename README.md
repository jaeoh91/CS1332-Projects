# CS 1332 — Data Structures & Algorithms: Projects

> **Georgia Tech** · CS 1332 · Spring 2026  
> Three software projects that apply data structures knowledge to real-world scenarios

---

# Overview

This repository contains three projects from **CS 1332: Data Structures and Algorithms** at Georgia Tech. Each project builds a **complete system** by composing multiple data structures to solve an application problem. The data structures underneath each system (linked lists, deques, hash maps, AVL trees, graphs, etc.) were all written from scratch.

Each project is organized into three source packages:
- **`implement/`** — core data structures and application logic (student-written)
- **`apply/`** — static interfaces / data classes (provided by course staff)
- **`refactor/`** — pre-existing implementations to be integrated or extended (provided, sometimes modified)

---

# Project Breakdown


## Project 1 — Quackify

> A music playlist manager built on a circular linked list and array-backed deque.

**Key Files:**
- [`Quackify.java`](Project1/src/implement/Quackify.java) — main application
- [`EndlessLinkedList.java`](Project1/src/implement/EndlessLinkedList.java) — circular doubly-linked list
- [`ArrayDeque.java`](Project1/src/implement/ArrayDeque.java) — circular array-backed deque

### Data Structures Implemented

| Structure | Description |
|---|---|
| `EndlessLinkedList<T>` | Circular doubly-linked list with a full `Iterator` implementation enabling infinite looping through a playlist |
| `ArrayDeque<T>` | Circular array-backed double-ended queue; used as undo/redo stacks |

### Application — `Quackify`

`Quackify` is a stateful music player backed by the above structures:

| Feature | How it works |
|---|---|
| `play()` / `stop()` | State machine using the circular iterator to track current song |
| `next()` / `prev()` | Advance or rewind through the playlist via the `EndlessLinkedList` iterator |
| `addSong(song, index)` | Insert a song at an arbitrary position in the playlist |
| `removeSong(index)` | Remove a song from the playlist |
| `shuffle()` | Randomizes playlist order using a Fisher-Yates-style swap via `Random` |
| `undo()` / `redo()` | Full undo/redo system backed by two `ArrayDeque` stacks of `PlaylistOperations` |

**Complexity highlights:** All standard playlist operations are O(1) or O(n) with no unnecessary copies; undo/redo is O(1) amortized.

---

## Project 2 — DaleDB

> A two-tier, LRU-evicting time-series database backed by a custom HashMap and AVL tree.

**Key Files:**
- [`DaleDB.java`](Project2/src/implement/DaleDB.java) — main database
- [`HashMap.java`](Project2/src/implement/HashMap.java) — open-addressing hash map
- [`TreeMap.java`](Project2/src/implement/TreeMap.java) — AVL-tree-backed sorted map
- [`AVL.java`](Project2/src/refactor/AVL.java) — self-balancing AVL tree

### Data Structures Implemented

| Structure | Description |
|---|---|
| `HashMap<K, V>` | Open-addressing hash map with double-hashing for collision resolution |
| `TreeMap<K, V>` | Sorted map backed by an AVL tree for O(log n) ordered operations |
| `AVL<T>` | Self-balancing BST; implements left/right rotations and height/balance-factor updates |

### Application — `DaleDB`

`DaleDB` is a **two-level key-value store** for time-series wildlife observation data:

- **Level 1 (Pond):** records are grouped by a pond ID string, stored in `HashMap<String, Pond>`
- **Level 2 (Record):** within each pond, records are indexed by a `long` timestamp in a `TreeMap` (AVL-backed), enabling O(log r) lookup, insertion, and deletion

| Feature | Complexity | How it works |
|---|---|---|
| `putRecord(record)` | O(log r) | Hashes pond ID → finds/creates pond → AVL insert by timestamp |
| `deleteRecord(pond, ts)` | O(log r) | AVL delete; removes empty pond automatically |
| `getRecord(pond, ts)` | O(log r) | AVL get |
| `getRecordRange(pond, start, end)` | O(r) | In-order AVL traversal filtered to timestamp range |
| `evict(k)` | O(kr) | Evicts k least-recently-accessed ponds via a **doubly-linked list** used as an LRU queue |

**LRU eviction design:** `DaleDB` maintains ponds in both the `HashMap` *and* a doubly-linked list (intrusive — each `Pond` object carries `prev`/`next` pointers). Every pond access moves it to the head. Eviction simply pops from the tail — O(1) per eviction step, O(r) to collect records.

---

## Project 3 — WaddleWorks

> A city planning simulation using BFS, DFS, Dijkstra's, Prim's, Kruskal's, and a Disjoint Set.

**Key Files:**
- [`GraphAlgorithms.java`](Project3/src/implement/GraphAlgorithms.java) — graph algorithm library
- [`WaddleWorks.java`](Project3/src/implement/WaddleWorks.java) — city planning application
- [`MyGraph.java`](Project3/src/implement/MyGraph.java) — adjacency-list graph
- [`DisjointSet.java`](Project3/src/refactor/DisjointSet.java) — Union-Find with path compression

### Algorithms Implemented

| Algorithm | File | Complexity | Use Case |
|---|---|---|---|
| BFS | `GraphAlgorithms.java` | O(V + E) | Road network traversal |
| DFS (recursive) | `GraphAlgorithms.java` | O(V + E) | Connectivity checking |
| Dijkstra's | `GraphAlgorithms.java` | O((V + E) log V) | Shortest path routing |
| Prim's MST | `GraphAlgorithms.java` | O((V + E) log V) | Minimum spanning electrical grid |
| Kruskal's MST | `GraphAlgorithms.java` | O(E log E) | Alternative MST via DisjointSet |
| Union-Find | `DisjointSet.java` | O(α(n)) amortized | Cycle detection in Kruskal's |

### Application — `WaddleWorks`

`WaddleWorks` models a penguin city with two separate graphs:
- **Road graph** — `MyGraph<Intersection>`: intersections as vertices, roads as weighted edges
- **Grid graph** — `MyGraph<Building>`: buildings as vertices, wire segments as weighted edges

| Feature | Algorithm Used | Description |
|---|---|---|
| `addRoad` / `removeRoad` | — | Mutate road graph; update DisjointSet for neighborhood tracking |
| `getNeighborhoods()` | DisjointSet | Returns count of connected components in road graph |
| `isAccessible(b1, b2)` | BFS/DFS | Checks if two buildings are reachable via roads |
| `shortestRoute(b1, b2)` | Dijkstra's | Returns minimum-cost path between buildings |
| `minPowerGrid()` | Prim's | Returns MST of the electrical grid |
| `mergeReports(reports)` | Kruskal's | Collates and sorts inter-neighborhood reports by road cost |

---

# Repo Structure

```
CS1332-Projects/
├── Project1/src/
│   ├── implement/
│   │   ├── ArrayDeque.java
│   │   ├── EndlessLinkedList.java
│   │   └── Quackify.java
│   ├── apply/
│   │   └── StaticQuackify.java
│   └── refactor/
│       └── StaticEndlessLinkedList.java
├── Project2/src/
│   ├── implement/
│   │   ├── AVL.java  (via refactor/)
│   │   ├── DaleDB.java
│   │   ├── HashMap.java
│   │   └── TreeMap.java
│   ├── apply/
│   │   ├── DaleRecord.java
│   │   └── StaticDaleDB.java
│   └── refactor/
│       ├── AVL.java
│       ├── AVLNode.java
│       └── StaticTreeMap.java
└── Project3/src/
    ├── implement/
    │   ├── GraphAlgorithms.java
    │   ├── MyGraph.java
    │   └── WaddleWorks.java
    ├── apply/
    │   ├── Building.java
    │   ├── Intersection.java
    │   └── StaticWaddleWorks.java
    └── refactor/
        ├── DisjointSet.java
        ├── DisjointSetNode.java
        ├── Edge.java
        ├── MutableGraph.java
        ├── StaticGraph.java
        ├── Vertex.java
        └── VertexDistance.java
```
