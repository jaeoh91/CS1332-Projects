import apply.StaticQuackify;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.Random;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class QuackifyStudentTest {
    private static final int TIMEOUT = 200;

    private StaticQuackify quackify;

    @Before
    public void setup() {
        quackify = Main.getQuackifyInstance();
    }

    @Test(timeout = TIMEOUT)
    public void testBasicOperations1() {
        quackify.addSong("Y");
        quackify.addSong("X");
        quackify.play();

        assertTrue(quackify.isPlaying());
        assertEquals("X", quackify.currentSong());
        assertEquals("Y", quackify.nextSong());
        assertEquals("X", quackify.nextSong());

        quackify.stop();

        String[] expected = {"X", "Y"};
        assertArrayEquals(expected, toArray(quackify));
    }

    @Test(timeout = TIMEOUT)
    public void testRandomSong() {
        Random rand = new Random(123L);
        quackify = Main.getQuackifyInstance(rand);
        quackify.addSong("E");
        quackify.addSong("D");
        quackify.addSong("C");
        quackify.addSong("B");
        quackify.addSong("A");

        quackify.play();
        assertEquals("A", quackify.currentSong());
        assertEquals("C", quackify.randomSong()); // index 2
        assertEquals("A", quackify.randomSong()); // index 0
        assertEquals("B", quackify.randomSong()); // index 1
        assertEquals("B", quackify.currentSong());
        assertEquals("C", quackify.nextSong());
    }

    @Test(timeout = TIMEOUT)
    public void testReverse() {
        quackify.addSong("Y");
        quackify.addSong("X");
        quackify.reverse();
        quackify.play();

        assertEquals("Y", quackify.currentSong());
        assertEquals("X", quackify.nextSong());
        assertEquals("Y", quackify.nextSong());

        quackify.stop();

        String[] expected = {"Y", "X"};
        assertArrayEquals(expected, toArray(quackify));
    }

    @Test(timeout = TIMEOUT)
    public void testPalindrome() {
        quackify.addSong("X");
        quackify.addSong("Y");
        quackify.addSong("X");
        assertTrue("XYX is a palindrome", quackify.isPalindrome());

        quackify.addSong("Y");
        assertFalse("XYXY is not a palindrome", quackify.isPalindrome());
    }

    @Test(timeout = TIMEOUT)
    public void testUndoRedo() {
        quackify.addSong("Z");
        quackify.addSong("Y");
        quackify.addSong("X");
        quackify.removeSong("Y");

        quackify.undo();

        quackify.play();

        assertArrayEquals(new String[]{"X", "Y", "Z"}, toArray(quackify));

        quackify.stop();

        quackify.redo();

        quackify.play();

        assertArrayEquals(new String[]{"X", "Z"}, toArray(quackify));
    }

    private String[] toArray(StaticQuackify q) {
        String[] out = new String[q.size()];
        int i = 0;
        for (String s : q.getPlaylist()) {
            if (i >= out.length) break;
            out[i++] = s;
        }
        return out;
    }
}
