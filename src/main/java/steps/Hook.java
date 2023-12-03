package steps;

import static config.DriverUtil.closeDriver;
import static config.DriverUtil.threadLocalActiveBrowsers;
import static config.FilesUtils.saveExecutionGeneralInfoToExcelFile;
import static config.FilesUtils.updateTestResultsSummaryToExcelFile;

import com.assertthat.selenium_shutterbug.core.Capture;
import com.assertthat.selenium_shutterbug.core.Shutterbug;
import config.AppiumServer;
import config.BasePage;
import config.DriverUtil;
import config.TestDataLoader;
import io.cucumber.core.backend.TestCaseState;
import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.Scenario;
import io.cucumber.plugin.event.DataTableArgument;
import io.cucumber.plugin.event.PickleStepTestStep;
import io.cucumber.plugin.event.Result;
import io.cucumber.plugin.event.Step;
import io.cucumber.plugin.event.TestCase;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assumptions;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
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
  private static boolean SKIP_TEST_ON_ERROR = false;
  private static boolean isPartializedScenario = false;
  public static final ThreadLocal<Map<String, String>> threadLocalDataSetInExecution = new ThreadLocal<>();
  public static List<Scenario> listOfScenariosInExecution = new ArrayList<>();
  public static List<Map<String, String>> listOfImportantDataInEachScenario = new ArrayList<>();
  public static final ThreadLocal<Integer> threadLocalCurrentStepNumber = new ThreadLocal<>();
  public static List<String> importantTestCaseKey = Arrays.asList("Contact_Account_Customer_Id__c",
    "api_email", "api_pass", "SkyMpp", "randomEmail", "initialPurchase_ParentOrderId");

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
  public static void beginScenario(Scenario scenario) {
    Collection<String> tags = scenario.getSourceTagNames();
    LOG.info("-------------------------------------------");
    LOG.info("Running Tests in \"{}\" browser", browser);
    if (tags.contains("@FIRST_CONDITIONAL_SCENARIO")) {
      SKIP_TEST_ON_ERROR = false;
    }
    if (isPartializedScenario && !tags.contains("@FIRST_CONDITIONAL_SCENARIO")) {
      Assumptions.assumeFalse(SKIP_TEST_ON_ERROR,
        "The Scenario '" + scenario.getName() + "' is aborted as the previous scenario with tags '"
          + failedScenarios + "' ran failed or being skipped");
      LOG.info(
        "Running Tests using the current opening browser '{}' without initializing a new Remote Web Driver",
        threadLocalActiveBrowsers.get().get("current"));
    } else {
      threadLocalCookieAccepted.set(false);
      new TestDataLoader();
      importantTestCaseKey.forEach(dataKey -> {
        TestDataLoader.setTestData(dataKey, "");
      });
      if (tags.contains("@FIRST_CONDITIONAL_SCENARIO")) {
        isPartializedScenario = true;
      }
      if (tags.contains("@TrialPromo")) {
        TestDataLoader.setTestData("IsTrialPromo", "Yes");
      } else {
        TestDataLoader.setTestData("IsTrialPromo", "No");
      }
      //initialize the Map<String, RemoteWebDriver> for storing all active browsers sessions
      threadLocalActiveBrowsers.set(new HashMap<>());
      RemoteWebDriver driver = DriverUtil.initDriver(browser, false, false);
      if (browser.contains("GCP") || browser.contains("Headless")) {
        driver.manage().window().setSize(new Dimension(1920, 1080));
      } else {
        driver.manage().window().maximize();
      }
      driver.manage().deleteAllCookies();
    }
    //Check if the test requires running with Trial Promo or not, if yes then add some required HTTP headers to browser
    if (TestDataLoader.getTestData("@TD:IsTrialPromo").equals("Yes")) {
      addHttpHeaders();
    }
    LOG.info("START SCENARIO '{}'", scenario.getName());
    LOG.info("With Tags: {}", tags);
    LOG.info("-------------------------------------------");
    threadLocalCurrentStepNumber.set(0);
  }

  @After(order = 1)
  public static void saveExecutionResultToExcel(Scenario scenario)
    throws NoSuchFieldException, IllegalAccessException {
    List<String> importantTestCaseKey = Arrays.asList("api_email", "api_pass", "SkyMpp",
      "randomEmail", "initialPurchase_ParentOrderId");
    //get general failure result
    Map<String, String> importantDataInEachScenario = new HashMap<>(
      getFailureResultFromScenario(scenario));
    importantTestCaseKey.forEach(dataKey -> importantDataInEachScenario.put(dataKey,
      threadLocalDataSetInExecution.get().getOrDefault(TestDataLoader.getTestData(dataKey), "")));
    listOfImportantDataInEachScenario.add(importantDataInEachScenario);
    listOfScenariosInExecution.add(scenario);
  }

  @After(order = 1)
  public static void endScenario(Scenario scenario) {
    Collection<String> tags = scenario.getSourceTagNames();
    LOG.info("-------------------------------------------");
    String scenarioName = scenario.getName().toUpperCase();
    LOG.info("END SCENARIO {}", scenarioName);
    LOG.info("-------------------------------------------");
    Collection<String> scenarioTags = scenario.getSourceTagNames();
    if (scenario.isFailed()) {
      failedScenarios.add(scenarioTags.toString());
      LOG.info("Scenario with the following tags '{}' failed", scenarioTags);
      if (DriverUtil.getDriver() != null) {
        captureFullScreenShot(scenario);
      }
      if (isPartializedScenario) {
        SKIP_TEST_ON_ERROR = true;
        closeDriver();
        LOG.info(
          "The scenario '{}' is in Conditional tests. Set 'SKIP_TEST_ON_ERROR' to true to abort the remaining Scenarios",
          scenarioName);
      }
    } else if (SKIP_TEST_ON_ERROR) {
      failedScenarios.add(scenarioTags.toString());
      LOG.info(
        "Scenario '{}' with the following tags '{}' is skipped due to previous Scenario failed / is aborted.",
        scenarioName, scenarioTags);
    } else {
      passedScenarios.add(scenario.getSourceTagNames().toString());
      LOG.info("Scenario with the following tags '{}' passed", scenarioTags);
    }
    if (isPartializedScenario && !scenarioTags.contains("@LAST_CONDITIONAL_SCENARIO")) {
      LOG.info(
        "Next scenario using the current opening browser '{}' without initializing a new Remote Web Driver",
        threadLocalActiveBrowsers.get().get("current"));
    } else {
      closeDriver();
    }
    LOG.info("-------------------------------------------");
  }

  @After(order = 3)
  public void printTestCaseData(Scenario scenario) {
    String dataSetInExecutionString = "**********************************************************\n";
    //add scenario data into the dataSetInExecutionString
    Map<String, String> scenarioInfo = new HashMap<>();
    scenarioInfo.put("Scenario", scenario.getName());
    scenarioInfo.put("Tags", scenario.getSourceTagNames().toString());
    scenarioInfo.put("Executing env", System.getProperty("executingEnv"));
    scenarioInfo.put("Tested env", testedEnv);
    dataSetInExecutionString += formatPrintData(scenarioInfo);
    //add important data into the dataSetInExecutionString
    Map<String, String> importantTestData = new HashMap<>();
    importantTestCaseKey.forEach(dataKey -> {
      if (threadLocalDataSetInExecution.get().containsKey(dataKey)) {
        importantTestData.put(dataKey, threadLocalDataSetInExecution.get().get(dataKey));
      }
    });
    dataSetInExecutionString += formatPrintData(importantTestData);
    //embedded dataSetInExecutionString to a txt file
    byte[] testDataMap = dataSetInExecutionString.getBytes(StandardCharsets.UTF_8);
    scenario.attach(testDataMap, "text/plain",
      scenario.getName().toLowerCase().replace(" ", "_") + "_collected_information");
  }

  private String formatPrintData(Map<String, String> dataMap) {
    String dataMapString = "";
    for (Map.Entry<String, String> data : dataMap.entrySet()) {
      dataMapString += String.format("%s: %s%n", StringUtils.rightPad(data.getKey(), 30),
        data.getValue());
    }
    dataMapString += "**********************************************************\n";
    return dataMapString;
  }

  @AfterAll
  public static void afterFeature() {
    if (platform.contains("android") || platform.contains("ios")) {
      LOG.info("-------------------------------------------");
      LOG.info("STOP APPIUM SERVER");
      LOG.info("-------------------------------------------");
      LOG.info(threadLocalDataSetInExecution.get().toString());
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
      if (testExecutionID.contains("@TEST") || testExecutionID.contains("@E2ED")) {
        saveExecutionGeneralInfoToExcelFile(listOfScenariosInExecution);
        updateTestResultsSummaryToExcelFile(testExecutionID, passedScenarios.size(),
          failedScenarios.size());
      }
    }
  }

  @AfterStep
  public static void afterStep(Scenario message) {
    if (!message.isFailed()) {
      threadLocalCurrentStepNumber.set(threadLocalCurrentStepNumber.get() + 1);
    }
    if (TestDataLoader.getTestData("@TD:take screenshot").equals("yes")) {
//      captureFullScreenShot(message);
    }
  }

  public static void captureFullScreenShot(Scenario message) {
    byte[] screenshot;
    try {
      screenshot = Shutterbug.shootPage(DriverUtil.getDriver(), Capture.FULL).getBytes();
      message.attach(screenshot, "image/png",
        message.getName().replace(" ", "_") + "_full_error_screenshot");
    } catch (IOException e) {
      LOG.info("Can not capture full screenshot: ", e);
    }
  }

  public static void addHttpHeaders() {
    DriverUtil.getDriver().get(
      "https://webdriver.modheader.com/load?profile={\"headers\":[{\"enabled\":true,\"name\":\"Referer\",\"value\":\"https://trial.sky.de\"},{\"enabled\":true,\"name\":\"x-sky-trials\",\"value\":\"true\"}],\"shortTitle\":\"1\",\"title\":\"Profile 1\",\"urlFilters\":[{\"enabled\":true,\"urlRegex\":\"https://api.uat.id.sky.de/*\"}],\"version\":2}");
    if (browser.contains("firefox")) {
      FirefoxDriver driver = (FirefoxDriver) DriverUtil.getDriver();
      if (System.getProperty("platform").contains("macbook")) {
        driver.installExtension(
          Paths.get(System.getProperty("user.dir") + "/resources/firefox_extension/modheader.xpi"));
      } else {
        driver.installExtension(Paths.get(
          System.getProperty("user.dir") + "\\resources\\firefox_extension\\modheader.xpi"));
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

  private static Map<String, String> getFailureResultFromScenario(Scenario scenario)
    throws NoSuchFieldException, IllegalAccessException {
    //initialize the value of information
    Map<String, String> info = new HashMap<>();
    info.put("failed step", "");
    info.put("failure message", "");
    //access to scenario delegate
    Field delegate = scenario.getClass().getDeclaredField("delegate");
    delegate.setAccessible(true);
    //get step results
    TestCaseState testCaseState = (TestCaseState) delegate.get(scenario);
    Field stepResults = testCaseState.getClass().getDeclaredField("stepResults");
    stepResults.setAccessible(true);
    List<Result> results = (List<Result>) stepResults.get(testCaseState);
    //get failure message
    Result failResult;
    String failureStackTrace = "";
    for (Result result : results) {
      if (result.getStatus().name().equalsIgnoreCase("FAILED")) {
        failResult = result;
        for (StackTraceElement stackTrace : failResult.getError().getStackTrace()) {
          failureStackTrace = failureStackTrace + stackTrace.toString() + "\n";
        }
        info.put("failure message", failResult.getError().getMessage() + "\n" + failureStackTrace);
        break;
      }
    }
    //get test case info
    Field testCaseField = testCaseState.getClass().getDeclaredField("testCase");
    testCaseField.setAccessible(true);
    TestCase testCase = (TestCase) testCaseField.get(testCaseState);
    //get test step list
    List<PickleStepTestStep> testStepTitles = testCase.getTestSteps()
      .stream()
      .filter(PickleStepTestStep.class::isInstance)
      .map(PickleStepTestStep.class::cast)
      .collect(Collectors.toList());
    if (scenario.isFailed() && !failureStackTrace.contains("Hook")) {
      //get failed step
      Step failedStep = testStepTitles.get(threadLocalCurrentStepNumber.get()).getStep();
      //get test step argument
      String dataTableString = "";
      try {
        DataTableArgument dataTableArgument = (DataTableArgument) failedStep.getArgument();
        for (List<String> cell : dataTableArgument.cells()) {
          dataTableString = dataTableString + cell.toString() + "\n";
        }
      } catch (NullPointerException e) {
        LOG.info("No datatable present in the step");
      }
      String stepFailed = String.format("Step #%d: %s %n %s",
        (threadLocalCurrentStepNumber.get() + 1),
        testStepTitles.get(threadLocalCurrentStepNumber.get()).getStep().getText(),
        dataTableString);
      info.put("failed step", stepFailed);
    }
    return info;
  }
}
