import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.junit.runners.model.TestTimedOutException;

import static org.junit.Assert.*;

public class PetRockTest {
    //define this so that you can use it in all of your tests without redefining it each time
    private PetRock rocky; // = new PetRock("Rocky");

    /*
        Each test instantiates then destroys the PetRockTest so it is new for each test
     */

    @Rule
    public Timeout globalTimeout = Timeout.seconds(15);  //10 seconds max

    @Before
    public void setUp() throws Exception {
        rocky = new PetRock("Rocky");
    }

    @Test
    public void getName() throws Exception{
        PetRock rocky = new PetRock("Rocky");
        assertEquals("Rocky", rocky.getName());
    }

    @Test
    public void testUnhappyToStart() throws Exception{
        assertFalse(rocky.isHappy());
    }

    @Test
    public void testHappyAfterPlay() throws Exception{
        rocky.playWithRock();
        assertTrue(rocky.isHappy());
    }

    @Ignore("Exception throwing not yet defined")
    @Test (expected=IllegalStateException.class)
    public void nameFail() throws Exception {
        rocky.getHappyMessage();
    }

    @Test
    public void name() throws Exception {
        rocky.playWithRock();
        String msg = rocky.getHappyMessage();
        assertEquals("I'm happy!", msg);
    }

    @Test
    public void testFavNum() throws Exception {
        assertEquals(42, rocky.getFavNumber());
    }

    @Test (expected = IllegalArgumentException.class)
    public void emptyNameFail() throws Exception{
        new PetRock("");
    }

    @Ignore ("This will throw an error because the loop is infinite!")
    @Test (expected = TestTimedOutException.class) //, timeout = 100)  //in ms
    public void waitForHappyTimeout() throws Exception{
        rocky.waitTilHappy();
    }
}