package apply;

import java.util.Random;
import refactor.StaticEndlessLinkedList;

/**
 * Interface for your custom {@code Quackify} implementation.
 * <p>
 * You are implementing a player application that uses your EndlessLinkedList for the playlist
 * management, and ArrayDeque for undo/redo functionality and string processing operations.
 * <p>
 * To get started, create a concrete class that implements {@link StaticQuackify}.
 * You will have to write an implementation for each of the methods below. Additionally,
 * you will need to have constructors that allow for a custom {@link Random} to be passed
 * into the class for {@link StaticQuackify#randomSong()}.
 *
 * @apiNote DO NOT MODIFY THIS FILE!
 * @implSpec You must use your own linear structures to solve all methods.
 * Importing any data structure implementations from {@link java.util} are banned.
 * @implNote {@code n} refers to the number of songs in the playlist
 * @version 1.0
 * @author CS 1332 TAs
 */
public interface StaticQuackify {

    /**
     * Signifies a state change to start music playback.
     * <p>
     * This should start the playlist from the beginning, regardless of when it
     * was last stopped.
     *
     * @throws IllegalStateException if the playlist is empty, or already playing
     * @implSpec {@code O(1)} runtime
     */
    void play();

    /**
     * Signifies a state change to stop music playback.
     *
     * @throws IllegalStateException if the playlist is empty or already stopped
     * @implSpec {@code O(1)} runtime
     */
    void stop();

    /**
     * Gets the current playing status.
     *
     * @return {@code true} if music is playing, {@code false} otherwise
     * @implSpec {@code O(1)} runtime
     */
    boolean isPlaying();

    /**
     * Returns a snapshot of the playlist.
     *
     * @return the playlist, as an in-order EndlessLinkedList
     * @implSpec {@code O(1)} runtime
     */
    StaticEndlessLinkedList<String> getPlaylist();

    /**
     * Retrieves the size of the playlist.
     *
     * @return the number of songs in the playlist.
     */
    int size();

    /**
     * Retrieves the current song being played in the playlist.
     *
     * @return the current song playing in the playlist
     * @throws IllegalStateException if the playlist is in a stopped state
     * @implSpec {@code O(1)} runtime
     */
    String currentSong();

    /**
     * Iterates to the next song to play in the playlist.
     *
     * @return the next song in the playlist
     * @throws IllegalStateException if the playlist is in a stopped state
     * @implSpec {@code O(1)} runtime
     */
    String nextSong();

    /**
     * Adds a song to the top of the playlist.
     *
     * @param song the song to add to the playlist
     * @throws IllegalStateException if the playlist is in a playing state
     * @throws IllegalArgumentException if {@code song} is {@code null} or an empty string
     * @implSpec {@code O(1)} runtime
     */
    void addSong(String song);

    /**
     * Removes the first occurrence of a song from the playlist by song.
     *
     * @param song the song to remove from the playlist
     * @throws IllegalStateException if the playlist is in a playing state
     * @throws IllegalArgumentException if {@code song} is {@code null}
     * @throws IllegalArgumentException if {@code song} is {@code null} or an empty string
     * @implSpec {@code O(n)} runtime
     */
    void removeSong(String song);

    /**
     * Reverses the playlist.
     *
     * @throws IllegalStateException if the playlist is empty or playing music
     * @implSpec
     * <p> {@code O(n)} runtime
     * <p> {@code O(1)} auxiliary space
     */
    void reverse();

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
    String randomSong();

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
    boolean isPalindrome();

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
    void undo();

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
    void redo();
}
