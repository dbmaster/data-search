 
import org.testng.Assert;
import org.testng.annotations.Test;
 
public class Calc1TestIT {
 
    @Test
    public void calc1() {
        Assert.assertEquals(10+20, 30);
        
        println System.getProperties().get("output.bundle");
    }
 
}