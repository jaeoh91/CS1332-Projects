
import apply.StaticQuackify;
import implement.EndlessLinkedList;
import implement.Quackify;
import refactor.StaticEndlessLinkedList;

import java.util.Random;

/**
 * Entry point for accessing your project 1 files.
 *
 * @author Jaesang Oh
 * @version 1.0
 * @userid joh426
 * @GTID 904170848
 * <br>
 * <p>
 * Collaborators: LIST ALL COLLABORATORS YOU WORKED WITH HERE
 * <p>
 * Resources: LIST ALL NON-COURSE RESOURCES YOU CONSULTED HERE
 * <p>
 * <br>
 * By typing 'I agree' below, you are agreeing that this is your
 * own work and that you are responsible for the contents of all
 * submitted files. If this is left blank, this project will lose
 * points.
 *<p>
 *<br>
 * Agree Here: I agree
 */
public class Main {

    /**
     * Creates and returns a new instance of your class implementing
     * {@link StaticEndlessLinkedList}.
     *
     * @param <T> the type of data
     * @return a new {@link StaticEndlessLinkedList} instance
     * @apiNote This method must be implemented for unit tests to run
     */
    public static <T> StaticEndlessLinkedList<T> getEndlessLinkedListInstance() {
        return new EndlessLinkedList<>();
    }

    /**
     * Creates and returns a new instance of your class implementing
     * {@link StaticQuackify}.
     *
     * @return a new {@link StaticQuackify} instance
     * @apiNote This method must be implemented for unit tests to run
     */
    public static StaticQuackify getQuackifyInstance() {
        return new Quackify();
    }

    /**
     * Creates and returns a new instance of your class implementing
     * {@link StaticQuackify} with a set seed.
     *
     * @param rand the random seed
     * @return a new {@link StaticQuackify} instance with a set seed
     * @apiNote This method must be implemented for unit tests to run
     */
    public static StaticQuackify getQuackifyInstance(Random rand) {
        return new Quackify(rand);
    }
}
