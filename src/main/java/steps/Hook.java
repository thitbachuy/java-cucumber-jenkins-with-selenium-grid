package steps;

import static config.DriverUtil.threadLocalActiveBrowsers;
import static config.FilesUtils.updateTestResultsSummaryToExcelFile;

import config.AppiumServer;
import config.BasePage;
import config.DriverUtil;
import config.TestDataLoader;
import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.Scenario;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

public class Hook {

  private static final Logger LOG = LogManager.getLogger(Hook.class);
  public static String browser = System.getProperty("browser");
  public static String testedEnv = System.getProperty("testedEnv");
  public static String platform = System.getProperty("platform");
  public static AppiumServer appiumServer;
  //    public static int chatIndex;
  public static Date testStartDateTime = new Date();
  public static List<String> failedScenarios = new ArrayList<>();
  public static List<String> passedScenarios = new ArrayList<>();
  public static ThreadLocal<Boolean> threadLocalCookieAccepted = new ThreadLocal<>();

  @BeforeAll
  public static void initializeAppiumServer() {
    if (platform.contains("android") || platform.contains("ios")) {
      LOG.info("-------------------------------------------");
      LOG.info("START APPIUM SERVER");
      LOG.info("-------------------------------------------");
      appiumServer = appiumServer == null ? new AppiumServer() : appiumServer;
      appiumServer.start();
    }
  }

  @Before
  public void beginScenario(Scenario scenario) {
    threadLocalCookieAccepted.set(false);
    Collection<String> tags = scenario.getSourceTagNames();
    if (tags.contains("@Extension")) {
      TestDataLoader.setTestData("IsExtension", "Yes");
    }
    LOG.info("-------------------------------------------");
    LOG.info("Running Tests in \"{}\" browser", browser);
    //initialize the Map<String, RemoteWebDriver> for storing all active browsers sessions
    threadLocalActiveBrowsers.set(new HashMap<>());
    RemoteWebDriver driver = DriverUtil.initDriver(browser, false, false);
    if (browser.contains("GCP") || browser.contains("Headless")) {
      driver.manage().window().setSize(new Dimension(1920, 1080));
    } else {
      driver.manage().window().maximize();
    }
    driver.manage().deleteAllCookies();
    if (TestDataLoader.getTestData("@TD:IsExtension").equals("Yes")) {
      addHttpHeaders();
    }
    LOG.info("START SCENARIO '{}'", scenario.getName());
    LOG.info("With Tags: {}", tags);
    LOG.info("-------------------------------------------");
  }

  @After(order = 1)
  public void endScenario(Scenario scenario) {
    LOG.info("-------------------------------------------");
    String scenarioName = scenario.getName().toUpperCase();
    LOG.info("END SCENARIO {}", scenarioName);
    LOG.info("-------------------------------------------");
    TestDataLoader.testDataRuntimeEndOfExecution();
    Collection<String> scenarioTags = scenario.getSourceTagNames();
    if (scenario.isFailed()) {
      try {
        failedScenarios.add(scenarioTags.toString());
        LOG.info("Scenario with the following tags '{}' failed", scenarioTags);
        if (DriverUtil.getDriver() != null) {
          byte[] screenshot = ((TakesScreenshot) DriverUtil.getDriver()).getScreenshotAs(
              OutputType.BYTES);
          scenario.attach(screenshot, "image/png", scenario.getName().replace(" ", "_")
              + "_error_screenshot");  // Stick it in the report
        }
      } catch (Exception ignored) {
      }
    } else {
      passedScenarios.add(scenario.getSourceTagNames().toString());
      LOG.info("Scenario with the following tags '{}' passed", scenarioTags);
    }
//    closeDriver();
    LOG.info("-------------------------------------------");
  }

  @After(order = 2, value = "@MessageConfiguration")
  public void resetMessageConfiguration() {
    LOG.info("The message configuration name");
  }

  @After(order = 2, value = "@DefectSwap")
  public void deleteDefectSwapOrder() {
  }

  @AfterAll
  public static void afterFeature() {
    if (platform.contains("android") || platform.contains("ios")) {
      LOG.info("-------------------------------------------");
      LOG.info("STOP APPIUM SERVER");
      LOG.info("-------------------------------------------");
      appiumServer = appiumServer == null ? new AppiumServer() : appiumServer;
      appiumServer.stop();
    }
    LOG.info("Total {} scenario(s) failed:", failedScenarios.size());
    failedScenarios.forEach(LOG::info);
    LOG.info("-------------------------------------------");
    LOG.info("Total {} scenario(s) passed:", passedScenarios.size());
    passedScenarios.forEach(LOG::info);
    if (System.getProperty("executingEnv") != null && System.getProperty("browser")
        .equalsIgnoreCase("chromeGCP")) {
      String testExecutionID = System.getProperty("cucumber.filter.tags");
      if (testExecutionID.contains("@TEST")) {
        updateTestResultsSummaryToExcelFile(testExecutionID, passedScenarios.size(),
            failedScenarios.size());
      }
    }
  }

  @AfterStep
  public void afterStep(Scenario message) {
    if (TestDataLoader.getTestData("@TD:take screenshot").equals("yes")) {
      byte[] screenshot = ((TakesScreenshot) DriverUtil.getDriver()).getScreenshotAs(
          OutputType.BYTES);
      message.attach(screenshot, "image/png",
          message.getName().replace(" ", "_") + "_error_screenshot");
    }
  }

  public void addHttpHeaders() {
    DriverUtil.getDriver().get(
        "https://webdriver.modheader.com/load?profile={\"headers\":[{\"enabled\":true,\"name\":\"Referer\",\"value\":\"https://google.com\"},{\"enabled\":true,\"name\":\"x-lyly-name\",\"value\":\"true\"}],\"shortTitle\":\"1\",\"title\":\"Profile 1\",\"urlFilters\":[{\"enabled\":true,\"urlRegex\":\"https://google.com/*\"}],\"version\":2}");
    if (browser.contains("firefox")) {
      FirefoxDriver driver = (FirefoxDriver) DriverUtil.getDriver();
      if (System.getProperty("platform").contains("macbook")) {
        driver.installExtension(Paths.get(
            System.getProperty("user.dir") + "/resources/extension/firefox_modheader.xpi"));
      } else {
        driver.installExtension(Paths.get(
            System.getProperty("user.dir") + "/resources/extension/firefox_modheader.xpi"));
      }
    } else {
      LOG.info(
          "Browser is not firefox hence no need to install Mod Header addon in the firefox driver");
    }
    BasePage basePage = new BasePage(DriverUtil.getDriver());
    String modifyHttpHeadersPageLocator = "//main";
    basePage.waitUntilElementContainsTexts(By.xpath(modifyHttpHeadersPageLocator), "Loaded profile",
        10);
  }
}
