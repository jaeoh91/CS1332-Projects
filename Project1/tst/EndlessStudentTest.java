import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import refactor.StaticEndlessLinkedList;

import java.util.Iterator;

import static org.junit.Assert.assertEquals;
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EndlessStudentTest {
    private static final int TIMEOUT = 200;
    
    private StaticEndlessLinkedList<String> endlessLinkedList;

    @Before
    public void setup() {
        endlessLinkedList = Main.getEndlessLinkedListInstance();
    }

    @Test(timeout = TIMEOUT)
    public void testAdd() {
        String[] expected = new String[]{"A", "B", "C", "D"};
        endlessLinkedList.addFirst(expected[1]);
        endlessLinkedList.addFirst(expected[0]);
        endlessLinkedList.addLast(expected[2]);
        endlessLinkedList.addLast(expected[3]);

        assertEquals("Size is incorrect", 4, endlessLinkedList.size());

        for (int i = 0; i < expected.length; ++i) {
            assertEquals("Elements at index " + i + " do not match", expected[i], endlessLinkedList.get(i));
        }
    }

    @Test(timeout = TIMEOUT)
    public void testRemove() {
        endlessLinkedList.addLast("A");
        endlessLinkedList.addLast("B");
        endlessLinkedList.addLast("C");

        assertEquals("Size is incorrect", 3, endlessLinkedList.size());
        assertEquals("A", endlessLinkedList.removeAtIndex(0));
        assertEquals("C", endlessLinkedList.removeLast());
        assertEquals("B", endlessLinkedList.removeLast());
        assertEquals("Size is incorrect", 0, endlessLinkedList.size());
    }

    @Test(timeout = TIMEOUT)
    public void testGet() {
        endlessLinkedList.addLast("A");
        endlessLinkedList.addLast("B");
        endlessLinkedList.addLast("C");

        assertEquals("Size is incorrect", 3, endlessLinkedList.size());
        assertEquals("A", endlessLinkedList.get(0));
        assertEquals("B", endlessLinkedList.get(1));
        assertEquals("C", endlessLinkedList.get(2));
    }

    @Test(timeout = TIMEOUT)
    public void testRemoveValue() {
        endlessLinkedList.addLast("A");
        endlessLinkedList.addLast("B");
        endlessLinkedList.addLast("C");

        assertEquals(1, endlessLinkedList.removeValue("B"));
        assertEquals(1, endlessLinkedList.removeValue("C"));
        assertEquals(0, endlessLinkedList.removeValue("A"));
    }

    @Test(timeout = TIMEOUT)
    public void testReverse() {
        String[] expected = new String[]{"A", "B", "C", "D"};
        endlessLinkedList.addFirst(expected[0]);
        endlessLinkedList.addFirst(expected[1]);
        endlessLinkedList.addFirst(expected[2]);
        endlessLinkedList.addFirst(expected[3]);
        endlessLinkedList.reverse();

        assertEquals("Size is incorrect", 4, endlessLinkedList.size());

        for (int i = 0; i < expected.length; ++i) {
            assertEquals("Elements at index " + i + " do not match", expected[i], endlessLinkedList.get(i));
        }
    }

    @Test(timeout = TIMEOUT)
    public void testIterator() {
        String[] expected = new String[]{"A", "B", "C", "D"};
        endlessLinkedList.addFirst(expected[1]);
        endlessLinkedList.addFirst(expected[0]);
        endlessLinkedList.addLast(expected[2]);
        endlessLinkedList.addLast(expected[3]);

        Iterator<String> iterator = endlessLinkedList.iterator();;
        for (int i = 0; i < expected.length; ++i) {
            assertEquals("Elements at index " + i + " do not match", expected[i], iterator.next());
        }

        // 2nd iteration
        for (int i = 0; i < expected.length; ++i) {
            assertEquals("Elements at index " + i + " do not match on second iteration", expected[i], iterator.next());
        }
    }

}
