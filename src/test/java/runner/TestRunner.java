package runner;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

@CucumberOptions(
  features = "src/test/java/features/test",
  glue = {"steps"},
//        tags = "Google",
  plugin = {"pretty", "html:target/cucumber-report.html", "timeline:target/cucumber-report", "json:target/cucumber-report/cucumber.json", "junit:target/cucumber-report/cucumber.xml"}
)
@RunWith(Cucumber.class)

public class TestRunner {
  //
//  private static boolean setUpIsDone = false;
//
//  @Before
//  public void setUp() {
//    if (setUpIsDone) {
//      return;
//    }
//    // do the setup
//    setUpIsDone = true;
  @BeforeClass
  public static void setup() {
    System.out.println("xjghkjxhkjh");
  }

}




