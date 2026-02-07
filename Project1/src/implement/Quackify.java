package implement;

import apply.StaticQuackify;
import refactor.StaticEndlessLinkedList;

import java.util.Iterator;
import java.util.Random;

/**
 * joh426's implementation of Quackify
 */
public class Quackify implements StaticQuackify {

    private boolean isPlaying;
    private Random rand;
    private EndlessLinkedList<String> playlist;
    private Iterator<String> iterator;
    private String curSong;
    private ArrayDeque<PlaylistOperations> undoStack;
    private ArrayDeque<PlaylistOperations> redoStack;

    /**
     * No-args constructor for Quackify
     */
    public Quackify()   {
        this(new Random());
    }

    /**
     * Constructor for Quackify allowing users to use their own random
     * @param cutomRandom Random to use
     */
    public Quackify(Random cutomRandom)  {
        this.isPlaying = false;
        this.rand = cutomRandom;
        this.undoStack = new ArrayDeque<>();
        this.redoStack = new ArrayDeque<>();
        this.playlist = new EndlessLinkedList<>();
    }

    /**
     * Signifies a state change to start music playback.
     * <p>
     * This should start the playlist from the beginning, regardless of when it
     * was last stopped.
     *
     * @throws IllegalStateException if the playlist is empty, or already playing
     * @implSpec {@code O(1)} runtime
     */
    public void play() {
        if (this.isPlaying) {
            throw new IllegalStateException("Music is already playing.");
        }
        if (this.playlist.size() == 0) {
            throw new IllegalStateException("Cannot play music from an empty playlist.");
        }

        this.iterator = this.playlist.iterator();
        this.isPlaying = true;
        this.curSong = this.iterator.next();
    }

    /**
     * Signifies a state change to stop music playback.
     *
     * @throws IllegalStateException if the playlist is empty or already stopped
     * @implSpec {@code O(1)} runtime
     */
    public void stop() {
        if (!this.isPlaying) {
            throw new IllegalStateException("Music is already stopped.");
        }
        if (this.playlist.size() == 0) {
            throw new IllegalStateException("Cannot stop music from an empty playlist.");
        }
        this.isPlaying = false;
    }

    /**
     * Gets the current playing status.
     *
     * @return {@code true} if music is playing, {@code false} otherwise
     * @implSpec {@code O(1)} runtime
     */
    public boolean isPlaying() {
        return this.isPlaying;
    }

    /**
     * Returns a snapshot of the playlist.
     *
     * @return the playlist, as an in-order EndlessLinkedList
     * @implSpec {@code O(1)} runtime
     */
    public StaticEndlessLinkedList<String> getPlaylist()   {
        return this.playlist;
    }

    /**
     * Retrieves the size of the playlist.
     *
     * @return the number of songs in the playlist.
     */
    public int size()  {
        return this.playlist.size();
    }

    /**
     * Retrieves the current song being played in the playlist.
     *
     * @return the current song playing in the playlist
     * @throws IllegalStateException if the playlist is in a stopped state
     * @implSpec {@code O(1)} runtime
     */
    public String currentSong()    {
        if (!this.isPlaying) {
            throw new IllegalStateException("Cannot get current song from stopped playlist.");
        }

        return this.curSong;
    }

    /**
     * Iterates to the next song to play in the playlist.
     *
     * @return the next song in the playlist
     * @throws IllegalStateException if the playlist is in a stopped state
     * @implSpec {@code O(1)} runtime
     */
    public String nextSong()    {
        if (!this.isPlaying) {
            throw new IllegalStateException("Cannot get current song from stopped playlist.");
        }

        this.curSong = this.iterator.next();
        return this.curSong;
    }

    /**
     * Adds a song to the top of the playlist.
     *
     * @param song the song to add to the playlist
     * @throws IllegalStateException if the playlist is in a playing state
     * @throws IllegalArgumentException if {@code song} is {@code null} or an empty string
     * @implSpec {@code O(1)} runtime
     */
    public void addSong(String song)   {
        if (this.isPlaying) {
            throw new IllegalStateException("Song cannot be added to currently playing list");
        }
        if (song == null || song.isEmpty()) {
            throw new IllegalArgumentException("Invalid song name");
        }
        this.playlist.addFirst(song);
        this.undoStack.addFirst(new PlaylistOperations("ADD", song, 0));
        this.redoStack = new ArrayDeque<>();
    }

    /**
     * Removes the first occurrence of a song from the playlist by song.
     *
     * @param song the song to remove from the playlist
     * @throws IllegalStateException if the playlist is in a playing state
     * @throws IllegalArgumentException if {@code song} is {@code null}
     * @throws IllegalArgumentException if {@code song} is {@code null} or an empty string
     * @implSpec {@code O(n)} runtime
     */
    public void removeSong(String song) {
        if (this.isPlaying) {
            throw new IllegalStateException("Cannot remove from currently playing playlist.");
        }
        if (song == null || song.isEmpty()) {
            throw new IllegalArgumentException("Invalid song name to remove.");
        }
        int index = this.playlist.removeValue(song);
        this.undoStack.addFirst(new PlaylistOperations("REMOVE", song, index));
        this.redoStack = new ArrayDeque<>();
    }

    /**
     * Reverses the playlist.
     *
     * @throws IllegalStateException if the playlist is empty or playing music
     * @implSpec
     * <p> {@code O(n)} runtime
     * <p> {@code O(1)} auxiliary space
     */
    public void reverse()  {
        if (this.isPlaying) {
            throw new IllegalStateException("Cannot reverse currently playing playlist.");
        }
        if (this.playlist.size() == 0) {
            throw new IllegalStateException("Cannot reverse empty playlist");
        }
        this.playlist.reverse();
        this.undoStack.addFirst(new PlaylistOperations("REVERSE", null, -1));
        this.redoStack = new ArrayDeque<>();
    }

    /**
     * Uses {@link Random#nextInt(int)} to iterate to a random song in the playlist.
     * Randomly calculate an index of a song to jump to, and then start playing that song.
     * Optionally, consider what optimization can lower the number of {@code .next()} calls.
     * <p>
     * Note that this is just a one-time randomization; calling {@link StaticQuackify#nextSong()}
     * after going to a random song should return the song that comes next after the song we jump to.
     * <p>
     * You should use the range [0, n) for generating your random index, using the Random
     * from the constructor. You must use Random.randInt(b) to pick a random number.
     * You must use the one-argument method, where b is the exclusive upper bound.
     *
     * @return the song that we randomly randomSong to
     * @throws IllegalStateException if the playlist is in a stopped state
     * @implSpec {@code O(n)} runtime
     */
    public String randomSong() {
        if (!this.isPlaying)    {
            throw new IllegalStateException("Cannot call randomSong on stopped playlist.");
        }
        int index = this.rand.nextInt(this.playlist.size());
        this.iterator = this.playlist.iterator();
        for (int i = 0; i < index; i++) {
            this.iterator.next();
        }
        this.curSong = this.iterator.next();
        return this.curSong;
    }

    /**
     * Determines whether the playlist itself (i.e., the list of song names) is a
     * palindrome.
     * <p>
     * Compare song names directly to each other, as the comparison is case-sensitive.
     *
     * @return whether the playlist itself is a palindrome
     * @throws IllegalStateException if the playlist is empty
     * @implNote do not use an array directly. You must use your ArrayDeque.
     * @implSpec {@code O(n)} runtime
     */
    public boolean isPalindrome()  {
        if (this.playlist.size() == 0)  {
            throw new IllegalStateException("Cannot check palindrome on empty playlist.");
        }

        this.playlist.reverse();
        ArrayDeque<String> reversed = new ArrayDeque<>(this.playlist);
        this.playlist.reverse();
        ArrayDeque<String> inOrder = new ArrayDeque<>(this.playlist);

        for (int i = 0; i < this.playlist.size(); i++) {
            if (!reversed.removeFirst().equals(inOrder.removeFirst())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Undoes the most recent structural change from the playlist.
     * <p>
     * Structural changes include those made in {@link StaticQuackify#addSong(String)}
     * and {@link StaticQuackify#removeSong(String)}.
     * <p>
     * The operations should include the index in mind such that it recovers the order
     * properly in addition to the elements' existence.
     *
     * @throws IllegalStateException if the playlist is in a playing state,
     *                               or if there are no operations to undo
     * @implSpec {@code O(n)} runtime
     */
    public void undo() {
        if (isPlaying)  {
            throw new IllegalStateException("Cannot undo on currently playing playlist.");
        }
        if (undoStack.size() == 0) {
            throw new IllegalStateException("No operations to undo.");
        }

        PlaylistOperations lastOperation = this.undoStack.removeFirst();
        String lastOperationName = lastOperation.getOperation();
        if (lastOperationName.equals("ADD")) {
            this.playlist.removeAtIndex(lastOperation.getIndex());
        } else if (lastOperationName.equals("REMOVE")) {
            this.playlist.addAtIndex(lastOperation.getIndex(), lastOperation.getSong());
        } else if (lastOperationName.equals("REVERSE")) {
            this.playlist.reverse();
        }

        this.redoStack.addFirst(lastOperation);
    }

    /**
     * Redoes the most recent structural change undone from the playlist.
     * <p>
     * Structural changes include those made in {@link StaticQuackify#addSong(String)}
     * and {@link StaticQuackify#removeSong(String)}.
     * <p>
     * The operations should include the index in mind such that it recovers the order
     * properly in addition to the elements' existence. After an undo, if any new
     * structural modification occurs, the redo history should be cleared, since
     * redoing and undoing would no longer reconstruct the playlist correctly.
     *
     * @throws IllegalStateException if the playlist is in a playing state,
     *                               or if there are no operations to redo
     * @implSpec {@code O(n)} runtime
     */
    public void redo() {
        if (isPlaying)  {
            throw new IllegalStateException("Cannot redo on currently playing playlist.");
        }
        if (redoStack.size() == 0) {
            throw new IllegalStateException("No operations to redo.");
        }

        PlaylistOperations lastOperation = this.redoStack.removeFirst();
        String lastOperationName = lastOperation.getOperation();
        if (lastOperationName.equals("ADD")) {
            this.playlist.addAtIndex(lastOperation.getIndex(), lastOperation.getSong());
        } else if (lastOperationName.equals("REMOVE")) {
            this.playlist.removeAtIndex(lastOperation.getIndex());
        } else if (lastOperationName.equals("REVERSE")) {
            this.playlist.reverse();
        }

        this.undoStack.addFirst(lastOperation);
    }

    /**
     * Defines playlist operations for undo/redo.
     */
    private class PlaylistOperations {
        private String operation;
        private String song;
        private int index;

        /**
         * Constructor for PlaylistOperations
         * @param operation Operation type
         * @param song Song Name
         * @param index Index of operation
         */
        public PlaylistOperations(String operation, String song, int index) {
            this.operation = operation;
            this.song = song;
            this.index = index;
        }

        /**
         * Getter for operation type
         * @return operation type
         */
        public String getOperation()   {
            return this.operation;
        }

        /**
         * Getter for song name
         * @return song name
         */
        public String getSong()   {
            return this.song;
        }

        /**
         * Getter for index
         * @return index
         */
        public int getIndex()   {
            return this.index;
        }



    }
}
