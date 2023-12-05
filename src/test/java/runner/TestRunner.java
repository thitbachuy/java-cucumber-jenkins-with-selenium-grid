import io.cucumber.testng.CucumberOptions;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import org.testng.annotations.Test;

@CucumberOptions(
        features = "src/test/java/features/test",
        glue = {"steps"},
        tags = "@Tiki",
        plugin = {"pretty", "html:target/cucumber-report.html", "json:target/cucumber-report/cucumber.json", "junit:target/cucumber-report/cucumber.xml"}
)
@Test
public class TestRunner extends AbstractTestNGCucumberTests {

}
