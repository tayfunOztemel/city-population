package city;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Source;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class GraphsTest {

    private ActorMaterializer mat;

    @Before
    public void setUp() throws Exception {
        final ActorSystem system = ActorSystem.create("test-city-population");
        mat = ActorMaterializer.create(system);

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testBirthFlow() {
        final NotUsed notUsed = Graphs.birthFlow.runWith(Source.single("Birth-1"), mat);

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}