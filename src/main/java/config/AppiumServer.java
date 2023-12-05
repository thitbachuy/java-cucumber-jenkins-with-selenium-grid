package config;

import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

public class AppiumServer {

  private final Logger LOG = LogManager.getLogger(AppiumServer.class);
  private static final String APPIUM_SERVER_LOCAL_HOST = "127.0.0.1";
  private static final String APPIUM_SERVER_LOCAL_PORT = "4723";
  private static final String APPIUM_SERVER_REMOTE_HOST = "10.160.63.6";
  private static final String APPIUM_SERVER_REMOTE_PORT = "4723";
  private static String appiumJsPath, nodeJsPath;
  private static AppiumDriverLocalService appiumService;
  private final String appiumServerHost, appiumServerPort, appiumServerBasePath;
  private URL remoteAppiumUrl;

  public AppiumServer() {
    appiumServerHost = getAppiumServerCapabilities().get("host");
    appiumServerPort = getAppiumServerCapabilities().get("port");
    appiumServerBasePath = getAppiumServerCapabilities().get("base path");
    String systemOs = System.getProperty("os.name");
    Map<String, String> environmentVariables = System.getenv();
    if (systemOs.toLowerCase().contains("windows")) {
      appiumJsPath =
        environmentVariables.get("APPDATA") + "\\npm\\node_modules\\appium\\build\\lib\\main.js";
      //When running with mvn
      nodeJsPath = environmentVariables.get("PROGRAMFILES") + "\\nodejs\\node.exe";
      //When running by IDE
    } else if (systemOs.toLowerCase().contains("mac os")) {
      appiumJsPath = "/usr/local/lib/node_modules/appium/build/lib/main.js";
      nodeJsPath = "/usr/local/bin/node";
    }
    LOG.info("Appium js path is: {}", appiumJsPath);
    LOG.info("Node js path is: {}", nodeJsPath);
    Assert.assertNotNull("Appium js path is null", appiumJsPath);
    Assert.assertNotNull("Node js path is null", nodeJsPath);
    appiumService = buildAppiumService(appiumJsPath, nodeJsPath, appiumServerHost, appiumServerPort,
      appiumServerBasePath);
  }

  public void start() {
    if (!appiumService.isRunning()) {
      appiumService.start();
      LOG.info("Appium server starts successfully!");
    } else {
      LOG.info("Appium server already started at host: {}:{}{}", appiumServerHost, appiumServerPort,
        appiumServerBasePath);
    }
    URL appiumUrl = appiumService.getUrl();
    LOG.info("Appium server  is: {}", appiumUrl);
    setRemoteAppiumUrl(appiumUrl);
  }

  public URL getRemoteAppiumUrl() {
    return remoteAppiumUrl;
  }

  public void setRemoteAppiumUrl(URL remoteAppiumUrl) {
    this.remoteAppiumUrl = remoteAppiumUrl;
  }

  public void stop() {
    if (appiumService.isRunning()) {
      appiumService.stop();
      LOG.info("Appium server is stopped successfully!");
    } else {
      LOG.info("Appium server is not running at host: {}:{}{}", appiumServerHost, appiumServerPort,
        appiumServerBasePath);
    }
  }

  private static Map<String, String> getAppiumServerCapabilities() {
    Map<String, String> appiumServerCapability = new HashMap<>();
    String appiumServerHost, appiumServerPort;
    if (System.getProperty("executingEnv").contains("jenkins")) {
      appiumServerHost = APPIUM_SERVER_REMOTE_HOST;
      appiumServerPort = APPIUM_SERVER_REMOTE_PORT;
    } else {
      appiumServerHost = APPIUM_SERVER_LOCAL_HOST;
      appiumServerPort = APPIUM_SERVER_LOCAL_PORT;
    }
    appiumServerCapability.put("host", appiumServerHost);
    appiumServerCapability.put("port", appiumServerPort);
    appiumServerCapability.put("base path", "/wd/hub/");
    return appiumServerCapability;
  }

  private AppiumDriverLocalService buildAppiumService(String appiumJsPath, String nodeJsPath,
    String appiumServerHost, String appiumServerPort, String appiumServerBasePath) {
    AppiumServiceBuilder builder = new AppiumServiceBuilder();
    builder
      .withAppiumJS(new File(appiumJsPath))
      .usingDriverExecutable(new File(nodeJsPath))
      .withIPAddress(appiumServerHost)
      .usingPort(Integer.parseInt(appiumServerPort))
      .withLogFile(new File("target/appium.log"))
      .withArgument(GeneralServerFlag.LOG_LEVEL, "info")
      .withArgument(GeneralServerFlag.ALLOW_INSECURE, "chromedriver_autodownload")
      .withArgument(GeneralServerFlag.BASEPATH, appiumServerBasePath);
    return AppiumDriverLocalService.buildService(builder);
  }
}
