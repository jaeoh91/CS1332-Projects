import implement.ArrayDeque;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import refactor.StaticEndlessLinkedList;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DequeStudentTest {

    private static final int TIMEOUT = 200;
    private ArrayDeque<String> array;

    @Before
    public void setup() {
        array = new ArrayDeque<>();
    }

    @Test(timeout = TIMEOUT)
    public void testInitialization() {
        assertEquals(0, array.size());
        assertArrayEquals(new Object[ArrayDeque.INITIAL_CAPACITY],
            array.getBackingArray());
    }

    @Test(timeout = TIMEOUT)
    public void testInitialization2() {
        StaticEndlessLinkedList<String> list = Main.getEndlessLinkedListInstance();
        list.addLast("A");
        list.addLast("B");
        list.addLast("C");
        array = new ArrayDeque<>(list);
        assertEquals(3, array.size());
        Object[] expected = new Object[list.size() * 2 + 1];
        expected[0] = "A";
        expected[1] = "B";
        expected[2] = "C";
        assertArrayEquals(expected, array.getBackingArray());
        array.clear();
        assertEquals(0, array.size());
        assertArrayEquals(new Object[ArrayDeque.INITIAL_CAPACITY],
            array.getBackingArray());
    }

    @Test(timeout = TIMEOUT)
    public void testArrayDequeNoWrapAround() {
        array.addLast("0a"); // 0a, _, _, _, _, _, _, _, _, _, _
        array.addLast("1a"); // 0a, 1a, _, _, _, _, _, _, _, _, _
        array.addLast("2a"); // 0a, 1a, 2a,  _, _, _, _, _, _, _, _
        array.addLast("3a"); // 0a, 1a, 2a, 3a, _, _, _, _, _, _, _
        array.addLast("4a"); // 0a, 1a, 2a, 3a, 4a, _, _, _, _, _, _

        assertEquals(5, array.size());
        String[] expected = new String[ArrayDeque.INITIAL_CAPACITY];
        expected[0] = "0a";
        expected[1] = "1a";
        expected[2] = "2a";
        expected[3] = "3a";
        expected[4] = "4a";
        assertArrayEquals(expected, array.getBackingArray());
        assertEquals("0a", array.getFirst());
        assertEquals("4a", array.getLast());

        // _, 1a, 2a, 3a, 4a, _, _, _, _, _, _
        assertEquals("0a", array.removeFirst());
        // _, _, 2a, 3a, 4a, _, _, _, _, _, _
        assertEquals("1a", array.removeFirst());
        // _, _, 2a, 3a, _, _, _, _, _, _, _
        assertEquals("4a", array.removeLast());
        // _, _, 2a, _, _, _, _, _, _, _, _
        assertEquals("3a", array.removeLast());

        assertEquals(1, array.size());
        expected[0] = null;
        expected[1] = null;
        expected[3] = null;
        expected[4] = null;
        assertArrayEquals(expected, array.getBackingArray());
        assertEquals("2a", array.getFirst());
        assertEquals("2a", array.getLast());
    }

    @Test(timeout = TIMEOUT)
    public void testArrayDequeWrapAround() {
        array.addFirst("4a"); // _, _, _, _, _, _, _, _, _, _, 4a
        array.addFirst("3a"); // _, _, _, _, _, _, _, _, _, 3a, 4a
        array.addFirst("2a"); // _, _, _, _, _, _, _, _, 2a, 3a, 4a
        array.addFirst("1a"); // _, _, _, _, _, _, _, 1a, 2a, 3a, 4a
        array.addFirst("0a"); // _, _, _, _, _, _, 0a, 1a, 2a, 3a, 4a

        assertEquals(5, array.size());
        String[] expected = new String[ArrayDeque.INITIAL_CAPACITY];
        expected[10] = "4a";
        expected[9] = "3a";
        expected[8] = "2a";
        expected[7] = "1a";
        expected[6] = "0a";
        assertArrayEquals(expected, array.getBackingArray());
        assertEquals("0a", array.getFirst());
        assertEquals("4a", array.getLast());

        // _, _, _, _, _, _, 0a, 1a, 2a, 3a, _
        assertEquals("4a", array.removeLast());

        assertEquals(4, array.size());
        expected[10] = null;
        assertArrayEquals(expected, array.getBackingArray());
        assertEquals("0a", array.getFirst());
        assertEquals("3a", array.getLast());

        array.addLast("5a"); // _, _, _, _, _, _, 0a, 1a, 2a, 3a, 5a
        array.addLast("6a"); // 6a, _, _, _, _, _, 0a, 1a, 2a, 3a, 5a

        assertEquals(6, array.size());
        expected[10] = "5a";
        expected[0] = "6a";
        assertArrayEquals(expected, array.getBackingArray());
        assertEquals("0a", array.getFirst());
        assertEquals("6a", array.getLast());
    }
}
