package config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.cucumber.core.exception.CucumberException;
import org.apache.commons.exec.*;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.ProfilesIni;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.Browser;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;
import pages.android.AndroidBasicPage;
import steps.Hook;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import static org.openqa.selenium.chrome.ChromeOptions.LOGGING_PREFS;
import static org.openqa.selenium.remote.Browser.EDGE;
import static org.openqa.selenium.remote.Browser.FIREFOX;
import static org.openqa.selenium.remote.CapabilityType.*;

public class DriverUtil {
    public static final String PATH_TO_DOWNLOAD_DIR = System.getProperty("user.home") + System.getProperty("file.separator") + "Downloads";
    private static RemoteWebDriver driver;
    private static AndroidDriver androidDriver;
    private static IOSDriver iosDriver;
    private static DesiredCapabilities dc;
    private static final Logger LOG = LogManager.getLogger(DriverUtil.class);
    public static final ThreadLocal<Map<String, RemoteWebDriver>> threadLocalActiveBrowsers = new ThreadLocal<>();
    private static final String APPIUM_SERVER_LOCAL_HOST = "127.0.0.1";
    private static String proxyHost;
    private static String proxyPort;
    private static final String HUB_ENDPOINT = System.getenv("HUB_ENDPOINT");
    public static ThreadLocal<RemoteWebDriver> threadLocalDriver = new ThreadLocal<>();
    public static final String FILE_SEPARATOR = System.getProperty("file.separator");
    static Path modHeaderExtension = Paths.get(System.getProperty("user.dir") + FILE_SEPARATOR + "resources" + FILE_SEPARATOR + "chrome_extension" + FILE_SEPARATOR + "modheader.crx");

    private DriverUtil() {
    }

    private static Executor executor;

    public static RemoteWebDriver getDriver() {
        return threadLocalActiveBrowsers.get().get("current");
    }

    public static AndroidDriver getAndroidDriver() {
        return androidDriver;
    }

    public static IOSDriver getIosDriver() {
        return iosDriver;
    }

    /* DOCKER DRIVER */

    public static RemoteWebDriver initDockerChrome(boolean incognito) {
        RemoteWebDriver driver;
        dc = new DesiredCapabilities();
        dc.setCapability(BROWSER_NAME, Browser.CHROME);
        dc.setBrowserName(Browser.CHROME.browserName());
        dc.setPlatform(Platform.LINUX);

        try {
            dc.setBrowserName("chrome");
            LoggingPreferences logPrefsCHROME = new LoggingPreferences();
            logPrefsCHROME.enable(LogType.PERFORMANCE, Level.ALL);

            ChromeOptions chromeOptions = new ChromeOptions();
            Map<String, Object> preferences = new HashMap<>();
            preferences.put("profile.default_content_setting_values.notifications", 2);
            chromeOptions.setExperimentalOption("prefs", preferences);
            chromeOptions.addArguments("--disable-gpu");
            if (System.getProperty("os.name").toLowerCase().contains("linux")) {
                // in Linux, we need these config to work
                LOG.info("Working in Linux OS");
                chromeOptions.addArguments("--no-sandbox");
                chromeOptions.addArguments("--disable-dev-shm-usage");
                chromeOptions.addArguments("--disable-shm-usage");
                chromeOptions.addArguments("--disable-setuid-sandbox");
                chromeOptions.addArguments("--disable-dev-shm-using");
                chromeOptions.addArguments("start-maximized");
                chromeOptions.addArguments("disable-infobars");
                chromeOptions.addArguments("--headless=new");
            }

            chromeOptions.addArguments("--lang=en");
            chromeOptions.addArguments("--enable-javascript");
            chromeOptions.addArguments("--window-size=1920,1080");
            chromeOptions.setExperimentalOption("w3c", true);
            chromeOptions.setCapability("browserName", "chrome");
            chromeOptions.setAcceptInsecureCerts(true);
            chromeOptions.setCapability(LOGGING_PREFS, logPrefsCHROME);

            if (incognito) {
                LOG.info("adding incognito to Browser");
                chromeOptions.addArguments("--incognito");
            }
            dc.merge(chromeOptions);
            LOG.info("Hub endpoint is: {}", HUB_ENDPOINT);
            driver = new RemoteWebDriver(new URL("http://" + HUB_ENDPOINT + "/wd/hub"), dc);
            LOG.info("Initialized driver is: {}", driver);
        } catch (Exception e) {
          throw new CucumberException("Failed to initialize ChromeDriver. Error: " + e.getMessage());
        }
        return driver;
    }

    public static RemoteWebDriver initDockerFirefox(boolean incognito) {
        dc = new DesiredCapabilities();
        dc.setCapability(BROWSER_NAME, FIREFOX);
        dc.setBrowserName(FIREFOX.browserName());
        dc.setPlatform(Platform.LINUX);

        FirefoxProfile testProfile = new FirefoxProfile();
        testProfile.setAcceptUntrustedCertificates(true);
        testProfile.setPreference("dom.webnotifications.enabled", false);
        testProfile.setPreference("javascript.enabled", true);

        FirefoxOptions options = new FirefoxOptions();
        options.setCapability(ACCEPT_INSECURE_CERTS, true);
//        options.setCapability("acceptSslCerts", true);
//        options.setCapability("marionette", true);
        options.setAcceptInsecureCerts(true);
        options.setProfile(testProfile);

        if (incognito) {
            LOG.info("adding incognito to Browser");
            testProfile.setPreference("browser.privatebrowsing.autostart", true);
        }

        try {
            dc.setBrowserName("firefox");
            dc.merge(options);
            LOG.info("Hub endpoint is: {}", HUB_ENDPOINT);
            driver = new RemoteWebDriver(new URL("http://" + HUB_ENDPOINT + "/wd/hub"), dc);
            LOG.info("Initialized driver is: {}", driver);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return driver;
    }

    public static RemoteWebDriver initDockerEdge(boolean incognito) {
        dc = new DesiredCapabilities();
        dc.setCapability(BROWSER_NAME, EDGE);
        dc.setBrowserName(EDGE.browserName());
        dc.setPlatform(Platform.LINUX);

        HashMap<String, Object> edgePrefs = new HashMap<>();
        edgePrefs.put("profile.default_content_settings.popups", 2);
        edgePrefs.put("profile.default_content_setting_values.notifications", 2);

        EdgeOptions edgeOptions = new EdgeOptions();
        edgeOptions.setCapability("prefs", edgePrefs);
        edgeOptions.setCapability(ACCEPT_INSECURE_CERTS, true);
        edgeOptions.setCapability("acceptInsecureCerts", true);
        edgeOptions.setCapability("acceptSslCerts", true);
        edgeOptions.setPageLoadStrategy(PageLoadStrategy.NORMAL);

        if (incognito) {
            LOG.info("adding incognito to Browser");
            edgeOptions.setCapability("ms:inPrivate", true);
        }

        try {
            dc.setBrowserName("edge");
            dc.merge(edgeOptions);
            LOG.info("Hub endpoint is: {}", HUB_ENDPOINT);
            driver = new RemoteWebDriver(new URL("http://" + HUB_ENDPOINT + "/wd/hub"), dc);
            LOG.info("Initialized driver is: {}", driver);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return driver;
    }

    /* LOCAL DRIVER */

    public static RemoteWebDriver initChrome(boolean incognito, boolean proxy) {
        RemoteWebDriver driver;
        LoggingPreferences logPrefsCHROME = new LoggingPreferences();
        logPrefsCHROME.enable(LogType.PERFORMANCE, Level.ALL);
        ChromeOptions chromeOptions = new ChromeOptions();
        Map<String, Object> preferences = new HashMap<>();
        preferences.put("profile.default_content_setting_values.notifications", 2);
        preferences.put("safebrowsing.enabled", "true");
        preferences.put("plugins.plugins_disabled", new String[]{"Chrome PDF Viewer"});
        preferences.put("plugins.always_open_pdf_externally", true);
        preferences.put("profile.default_content_settings.popups", 0);
        preferences.put("download.default_directory", PATH_TO_DOWNLOAD_DIR);

        chromeOptions.setExperimentalOption("prefs", preferences);
        chromeOptions.addArguments("--remote-allow-origins=*");
        chromeOptions.addArguments("--disable-gpu");
        if (System.getProperty("os.name").toLowerCase().contains("linux")) {
            // in Linux, we need these config to work
            LOG.info("Working in Linux OS");
            chromeOptions.addArguments("--no-sandbox");
            chromeOptions.addArguments("--disable-shm-usage");
            chromeOptions.addArguments("--disable-dev-shm-usage");
            chromeOptions.addArguments("--disable-setuid-sandbox");
            chromeOptions.addArguments("--disable-dev-shm-using");
            chromeOptions.addArguments("--disable-extensions");
            chromeOptions.addArguments("disable-infobars");
        }
        chromeOptions.addArguments("--remote-allow-origins=*");
        chromeOptions.addArguments("--lang=en");
        chromeOptions.addArguments("--enable-javascript");
        chromeOptions.setExperimentalOption("w3c", true);
        chromeOptions.setCapability("browserName", "chrome");
        chromeOptions.setAcceptInsecureCerts(true);
        chromeOptions.setCapability(LOGGING_PREFS, logPrefsCHROME);

         /*
        Ensure that your chrome browser has proxy enabled.
        Settings - Advanced - System : Open your computer proxy settings should be able to open the dialog
         */
        if (proxy) {
            Proxy proxyBrowser = new Proxy();
            getProxyServer();
            proxyBrowser.setSslProxy(proxyHost + ":" + proxyPort);
            chromeOptions.setCapability("proxy", proxyBrowser);
        }

        if (incognito) {
            LOG.info("adding incognito to Browser");
            chromeOptions.addArguments("--incognito");
        }
        driver = new ChromeDriver(chromeOptions);
        return driver;
    }

    public static RemoteWebDriver initChromeHeadless(boolean incognito, boolean proxy) {
        RemoteWebDriver driver;
        LoggingPreferences logPrefsCHROME = new LoggingPreferences();
        logPrefsCHROME.enable(LogType.PERFORMANCE, Level.ALL);

        ChromeOptions chromeOptions = new ChromeOptions();
        Map<String, Object> preferences = new HashMap<>();
        preferences.put("profile.default_content_setting_values.notifications", 2);
        preferences.put("profile.default_content_settings.popups", 0);
        chromeOptions.setExperimentalOption("prefs", preferences);
        chromeOptions.addArguments("--disable-gpu");
        chromeOptions.addArguments("--remote-allow-origins=*");

        chromeOptions.addArguments("--no-sandbox");
        chromeOptions.addArguments("--disable-shm-usage");
        chromeOptions.addArguments("--disable-dev-shm-usage");
        chromeOptions.addArguments("--disable-setuid-sandbox");
        chromeOptions.addArguments("--disable-dev-shm-using");
        chromeOptions.addArguments("window-size=1920,1080");
        chromeOptions.addArguments("disable-infobars");
        chromeOptions.addArguments("--headless=new");
        if(TestDataLoader.getTestData("@TD:IsTrialPromo").equals("Yes")) {
          chromeOptions.addExtensions(new File(modHeaderExtension.toAbsolutePath().toString()));
        }
        chromeOptions.addArguments("--lang=en");
        chromeOptions.addArguments("--enable-javascript");
        chromeOptions.setExperimentalOption("w3c", true);
        chromeOptions.setCapability("browserName", "chrome");
        chromeOptions.setAcceptInsecureCerts(true);
        chromeOptions.setCapability(LOGGING_PREFS, logPrefsCHROME);

         /*
        Ensure that your chrome browser has proxy enabled.
        Settings - Advanced - System : Open your computer proxy settings should be able to open the dialog
         */
        if (proxy) {
            Proxy proxyBrowser = new Proxy();
            getProxyServer();
            proxyBrowser.setSslProxy(proxyHost + ":" + proxyPort);
            chromeOptions.setCapability("proxy", proxyBrowser);
        }

        if (incognito) {
            LOG.info("adding incognito to Browser");
            chromeOptions.addArguments("--incognito");
        }
        ChromeDriverService driverService = ChromeDriverService.createDefaultService();
        driver = new ChromeDriver(driverService, chromeOptions);
        //Setup to enable download file in headless
        enableDownloadFileHeadless(driverService.getUrl().toString(), driver);
        return driver;
    }

    public static RemoteWebDriver initChromeExtension(boolean incognito, boolean proxy) {
        if (System.getProperty("executingEnv").contains("GCP")) {
            dc = new DesiredCapabilities();
            dc.setCapability(BROWSER_NAME, Browser.CHROME);
            dc.setBrowserName(Browser.CHROME.browserName());
            dc.setPlatform(Platform.LINUX);
            dc.setBrowserName("chrome");
        }
        //Setup chrome options
        LoggingPreferences logPrefsCHROME = new LoggingPreferences();
        logPrefsCHROME.enable(LogType.PERFORMANCE, Level.ALL);

        ChromeOptions chromeOptions = new ChromeOptions();
        Map<String, Object> preferences = new HashMap<>();
        preferences.put("profile.default_content_setting_values.notifications", 2);
        LOG.info("Add chrome extension");
        String extensionPath = "src/test/java/config/chromeExtensions/feahianecghpnipmhphmfgmpdodhcapi.crx";
        chromeOptions.addExtensions(new File(extensionPath));
        LOG.info("Absolute Extension Path is: {}", new File(extensionPath).getAbsolutePath());
        LOG.info("Relative Extension Path is: {}", Paths.get(extensionPath));
        chromeOptions.setExperimentalOption("prefs", preferences);
        chromeOptions.addArguments("--disable-gpu");
        if (System.getProperty("os.name").toLowerCase().contains("linux")) {
            // in Linux, we need these config to work
            LOG.info("Working in Linux OS");
            chromeOptions.addArguments("--no-sandbox");
            chromeOptions.addArguments("--disable-shm-usage");
            chromeOptions.addArguments("--disable-dev-shm-usage");
            chromeOptions.addArguments("--disable-setuid-sandbox");
            chromeOptions.addArguments("--disable-dev-shm-using");
            chromeOptions.addArguments("start-maximized");
            chromeOptions.addArguments("disable-infobars");
            chromeOptions.addArguments("--headless");
        }
        chromeOptions.addArguments("--lang=en");
        chromeOptions.addArguments("--enable-javascript");
        chromeOptions.setExperimentalOption("w3c", true);
        chromeOptions.setCapability("browserName", "chrome");
        chromeOptions.setAcceptInsecureCerts(true);
        chromeOptions.setCapability(LOGGING_PREFS, logPrefsCHROME);
        if (incognito) {
            LOG.info("adding incognito to Browser");
            chromeOptions.addArguments("--incognito");
        }
        if (System.getProperty("executingEnv").contains("GCP")) {
            dc.merge(chromeOptions);
            LOG.info("Hub endpoint is: {}", HUB_ENDPOINT);
            try {
                driver = new RemoteWebDriver(new URL("http://" + HUB_ENDPOINT + "/wd/hub"), dc);
                LOG.info("Initialized driver is: {}", driver);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        } else {
        /*
        Ensure that your chrome browser has proxy enabled.
        Settings - Advanced - System : Open your computer proxy settings should be able to open the dialog
         */
            if (proxy) {
                Proxy proxyBrowser = new Proxy();
                getProxyServer();
                proxyBrowser.setSslProxy(proxyHost + ":" + proxyPort);
                chromeOptions.setCapability("proxy", proxyBrowser);
            }
            driver = new ChromeDriver(chromeOptions);
        }
        return driver;
    }


    public static RemoteWebDriver initChromeSSH(boolean incognito, boolean proxy) {

        try {
            LoggingPreferences logPrefsCHROME = new LoggingPreferences();
            logPrefsCHROME.enable(LogType.PERFORMANCE, Level.ALL);

            DesiredCapabilities dcSSH = new DesiredCapabilities();
            dcSSH.setCapability(BROWSER_NAME, Browser.CHROME);
            dcSSH.setBrowserName(Browser.CHROME.browserName());
            dcSSH.setPlatform(Platform.MAC);

            ChromeOptions chromeOptions = new ChromeOptions();
            Map<String, Object> preferences = new HashMap<>();
            preferences.put("profile.default_content_setting_values.notifications", 2);
            chromeOptions.setExperimentalOption("prefs", preferences);
            chromeOptions.addArguments("--disable-gpu");
            chromeOptions.addArguments("--lang=en");
            chromeOptions.addArguments("--enable-javascript");
            chromeOptions.setExperimentalOption("w3c", true);
            chromeOptions.setCapability("browserName", "chrome");
            chromeOptions.setAcceptInsecureCerts(true);
            chromeOptions.setCapability(LOGGING_PREFS, logPrefsCHROME);

         /*
        Ensure that your chrome browser has proxy enabled.
        Settings - Advanced - System : Open your computer proxy settings should be able to open the dialog
         */
            if (proxy) {
                Proxy proxyBrowser = new Proxy();
                getProxyServer();
                proxyBrowser.setSslProxy(proxyHost + ":" + proxyPort);
                chromeOptions.setCapability("proxy", proxyBrowser);
            }

            if (incognito) {
                LOG.info("adding incognito to Browser");
                chromeOptions.addArguments("--incognito");
            }
            dcSSH.merge(chromeOptions);
            driver = new RemoteWebDriver(new URL("http://192.168.178.39:4723/wd/hub"), dcSSH);
            driver.manage().window().setSize(new Dimension(1920, 1080));
            LOG.info("running driver on IP Adress 192.168.178.39:4723");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return driver;
    }

    public static RemoteWebDriver initFirefox(boolean incognito, boolean proxy) {

        LoggingPreferences logPrefsFOX = new LoggingPreferences();
        logPrefsFOX.enable(LogType.PERFORMANCE, Level.ALL);

        FirefoxOptions options = new FirefoxOptions();
        options.setCapability(ACCEPT_INSECURE_CERTS, true);
//        options.setCapability("acceptSslCerts", true);
//        options.setCapability("marionette", true);
        options.setAcceptInsecureCerts(true);

        // input type of firefox profile folder
        FirefoxProfile testProfile = (new ProfilesIni()).getProfile("automation");
        if (testProfile == null) {
            testProfile = new FirefoxProfile();
        }
        testProfile.setAcceptUntrustedCertificates(true);
        testProfile.setPreference("dom.webnotifications.enabled", false);
        testProfile.setPreference("javascript.enabled", true);

        /*
        Ensure that your firefox browser has proxy enabled.
        Options - Network Settings - Settings : Use System Proxy Settings
         */
        if (proxy) {
            getProxyServer();
            testProfile.setPreference("network.proxy.type", 1);
            testProfile.setPreference("network.proxy.ssl", proxyHost);
            testProfile.setPreference("network.proxy.ssl_port", Integer.parseInt(proxyPort));
        }

        if (incognito) {
            LOG.info("adding incognito to Browser");
            testProfile.setPreference("browser.privatebrowsing.autostart", true);
        }

        options.setProfile(testProfile);
        driver = new FirefoxDriver(options);
        return driver;
    }

    public static RemoteWebDriver initEdge(boolean incognito) {

        HashMap<String, Object> edgePrefs = new HashMap<>();
        edgePrefs.put("profile.default_content_settings.popups", 2);
        edgePrefs.put("profile.default_content_setting_values.notifications", 2);

        EdgeOptions edgeOptions = new EdgeOptions();
        edgeOptions.setCapability("prefs", edgePrefs);
        edgeOptions.setCapability(ACCEPT_INSECURE_CERTS, true);
        edgeOptions.setCapability("acceptInsecureCerts", true);
        edgeOptions.setCapability("acceptSslCerts", true);
        edgeOptions.setPageLoadStrategy(PageLoadStrategy.NORMAL);

        if (incognito) {
            LOG.info("adding incognito to Browser");
            edgeOptions.setCapability("ms:inPrivate", true);
        }

        edgeOptions.merge(edgeOptions);
        driver = new EdgeDriver(edgeOptions);
        return driver;
    }

    public static RemoteWebDriver initSafari() {
        SafariOptions safariOptions = new SafariOptions();
        safariOptions.setCapability("acceptSslCerts", true);
        driver = new SafariDriver(safariOptions);
        return driver;
    }

    public static RemoteWebDriver initDriver(String browser, boolean incognito, boolean proxy) {
        RemoteWebDriver driver = null;
        switch (browser) {
            case "chromeGCP":
                driver = initDockerChrome(incognito);
                setUpUserAgentOfDriver(driver);
                driver = initChromeDockerUserAgent(TestDataLoader.getTestData("@TD:user-agent"), incognito);
                break;
            case "chromeHeadless":
                driver = initChromeHeadless(incognito, proxy);
                setUpUserAgentOfDriver(driver);
                driver = initChromeHeadlessUserAgent(TestDataLoader.getTestData("@TD:user-agent"), proxy, incognito);
                break;
            case "firefoxGCP":
                driver = initDockerFirefox(incognito);
                break;
            case "edgeGCP":
                driver = initDockerEdge(incognito);
                setUpUserAgentOfDriver(driver);
                driver = initEdgeDockerUserAgent(TestDataLoader.getTestData("@TD:user-agent"), incognito);
                break;
            case "chrome":
                driver = initChrome(incognito, proxy);
                break;
            case "firefox":
                driver = initFirefox(incognito, proxy);
                break;
            case "edge":
                driver = initEdge(incognito);
                break;
            case "safari":
                driver = initSafari();
                break;
            case "chrome-ssh":
                driver = initChromeSSH(incognito, proxy);
                break;
            case "chromeextension":
                driver = initChromeExtension(incognito, proxy);
                break;
            default:
                LOG.error("Browser {} is not supported by test automation framework!", browser);
                System.exit(0);
        }
        setCurrentDriver(driver);
        LOG.info("The current thread id '{}' has the following active browsers: {}", Thread.currentThread().getName(), threadLocalActiveBrowsers.get());
        return driver;
    }

    private static void setUpUserAgentOfDriver(RemoteWebDriver driver) {
        //Get user-agent in chrome headless
        String userAgent = driver.executeScript("return navigator.userAgent;").toString();
        LOG.info("Driver \"{}\" has user-agent \"{}\"", driver, userAgent);
        //Convert to user-agent chrome and set in test data
        TestDataLoader.setTestData("user-agent", userAgent.replace("HeadlessChrome", "Chrome"));
        LOG.info("Unused driver \"{}\" will be closed", driver);
        driver.quit();
        LOG.info("Close unused driver successfully!");
    }

  public static int adbCommand(String command) {

        CommandLine cmd = CommandLine.parse("ssh pascalkallenborn@" + APPIUM_SERVER_LOCAL_HOST + " \"" + command + "\"");
        LOG.info("used string to connect: {}", cmd);

        DefaultExecutor shell = new DefaultExecutor();
        int exitValue = 9999;
        //Delete app cache and data
        try {
            exitValue = shell.execute(cmd);
            return exitValue;
        } catch (IOException ignored) {
        }
        return exitValue;
    }

    public static void initAndroidDriver() {
        String startADB;
        String appiumSettingsStop;
        String appiumSettingsClear;
        switch (Hook.platform) {
            case "android-webApp":
                break;
            case "android-nativeApp":
                startADB = "adb devices";
                appiumSettingsClear = "adb -s " + System.getProperty("deviceUDID") + " shell pm clear io.appium.settings";
                appiumSettingsStop = "adb -s " + System.getProperty("deviceUDID") + " shell am force-stop io.appium.settings";
                executeSettingAdbAndAppium(startADB, appiumSettingsStop, appiumSettingsClear);
                break;
            case "android-ssh":
                String formatSSH = "ssh pascalkallenborn@%s " + APPIUM_SERVER_LOCAL_HOST + "\"%s\"";
                startADB = String.format(formatSSH, "adb devices");
                appiumSettingsStop = String.format(formatSSH, "adb -s " + System.getProperty("deviceUDID") + " shell pm clear io.appium.settings");
                appiumSettingsClear = String.format(formatSSH, "adb -s " + System.getProperty("deviceUDID") + " shell am force-stop io.appium.settings");
                executeSettingAdbAndAppium(startADB, appiumSettingsStop, appiumSettingsClear);
                break;
            default:
                throw new CucumberException("please choose a valid android platform");
        }
        //Set capabilities
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("udid", System.getProperty("deviceUDID"));
        caps.setCapability("deviceName", System.getProperty("deviceName"));
        caps.setCapability("platformName", "Android");
        caps.setCapability("platformVersion", System.getProperty("platformVersion"));
        caps.setCapability("automationName", "UiAutomator2");
        caps.setCapability("noReset", "true");
        caps.setCapability("newCommandTimeout", 10000);
        caps.setCapability("adbExecTimeout", 100000);
        if (Hook.platform.equals("android-webApp")) {
            if (System.getProperty("platformVersion").contains("13")) {
                AdbShellCommand.clearApplicationDataOfDevice(System.getProperty("deviceUDID"), "com.android.chrome");
                startAndroidDriver(Hook.appiumServer.getRemoteAppiumUrl(), caps);
                //Open url by adb shell command to load the web view
                AdbShellCommand.openUrl(System.getProperty("deviceUDID"), "https://www.google.com");
                //Handle popup if it appears
                AndroidBasicPage androidBasicPage = new AndroidBasicPage(androidDriver);
                androidBasicPage.declineChromeNotificationPopup();
                //Wait and switch to web view context
                androidBasicPage.waitForBothNativeAndWebViewContextAvailable(10, 1);
                androidBasicPage.switchToAnotherContext("WEBVIEW");
            } else {
                caps.setCapability("browserName", Hook.browser);
                startAndroidDriver(Hook.appiumServer.getRemoteAppiumUrl(), caps);
            }
        } else {
            startAndroidDriver(Hook.appiumServer.getRemoteAppiumUrl(), caps);
        }
        setCurrentDriver(androidDriver);
        androidDriver.rotate(ScreenOrientation.PORTRAIT);
    }

    private static void startAndroidDriver(URL remoteAppiumUrl, DesiredCapabilities caps) {
        try {
            androidDriver = new AndroidDriver(remoteAppiumUrl, caps);
        } catch (Exception e) {
            throw new CucumberException("Fail to initialize android driver due to: " + e.getMessage());
        }
    }

    private static void executeSettingAdbAndAppium(String startADB, String appiumSettingsStop, String appiumSettingsClear) {
        //Before actually firing up the AndroidDriver, let's make sure ADB daemon is running
        LOG.info("Making sure ADB daemon is started...");
        DefaultExecutor shell = new DefaultExecutor();
        int exitValue = 9999;

        try {
            exitValue = shell.execute(CommandLine.parse(startADB));
        } catch (IOException ignored) {
        }
        if (exitValue > 0) throw new CucumberException("Cannot start ADB daemon!");

        //Re-starting appium settings on the device (it can hang up sometimes)
        try {
            exitValue = shell.execute(CommandLine.parse(appiumSettingsStop));
        } catch (IOException ignored) {
        }
        if (exitValue > 0)
            throw new CucumberException("Cannot stop Appium settings on the device!");

        try {
            exitValue = shell.execute(CommandLine.parse(appiumSettingsClear));
        } catch (IOException ignored) {
        }
        if (exitValue > 0)
            throw new CucumberException("Cannot clear Appium settings data on the device!");
    }

    public static void initIosDriver() {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("udid", System.getProperty("deviceUDID"));
        caps.setCapability("deviceName", System.getProperty("deviceName"));
        caps.setCapability("platformName", "iOS");
        caps.setCapability("platformVersion", System.getProperty("platformVersion"));
        caps.setCapability("automationName", "XCUITest");
        caps.setCapability("rotatable", true);
        caps.setCapability("newCommandTimeout", 10000);
        if (Hook.platform.equals("ios-webApp")) {
            caps.setCapability("browserName", Hook.browser);
            caps.setCapability("startIWDP", true);
        }
        try {
            iosDriver = new IOSDriver(Hook.appiumServer.getRemoteAppiumUrl(), caps);
        } catch (Exception e) {
            throw new CucumberException("Fail to initialize ios driver due to: " + e.getMessage());
        }
        setCurrentDriver(iosDriver);
        iosDriver.rotate(ScreenOrientation.PORTRAIT);
    }

    public static void setCurrentDriver(RemoteWebDriver remoteWebDriver) {
      threadLocalActiveBrowsers.get().put("current", remoteWebDriver);
    }

    public static void closeDriver() {
      LOG.info("remaining open Browsers: {}", threadLocalActiveBrowsers.get().keySet());
      threadLocalActiveBrowsers.get().keySet().forEach(driverAlias -> {
        threadLocalActiveBrowsers.get().get(driverAlias).quit();
        LOG.info("Driver: {} closed as expected :)", driverAlias);
      });
      driver = null;
    }

    public static void closeAndroidDriver() {
        if (getAndroidDriver() == null) {
            LOG.info("Android driver already closed");
        } else {
            try {
                androidDriver.quit();
                androidDriver = null;
            } catch (Exception ignored) {
            }
        }
    }

    public static void closeIosDriver() {
        if (getIosDriver() == null) {
            LOG.info("iOS driver already closed");
        } else {
            try {
                iosDriver.close();
                iosDriver.quit();
                iosDriver = null;
            } catch (Exception ignored) {
            }
        }
    }

    public static void clearCache() {
      threadLocalDriver.get().get("chrome://settings/clearBrowserData");
      threadLocalDriver.get().switchTo().activeElement();
      threadLocalDriver.get().findElement(By.cssSelector("* /deep/ #clearBrowsingDataConfirm")).click();
    }

    public static RemoteWebDriver getBrowser(String browserName) {
        return threadLocalActiveBrowsers.get().get(browserName);
    }

    public static RemoteWebDriver initNewDriver(String browserName, boolean incognito, boolean proxy) {
        if (System.getProperty("executingEnv").contains("GCP") && !browserName.contains("GCP") && !browserName.contains("Extension")) {
            browserName = browserName.toLowerCase() + "GCP";
        } else {
            if (!browserName.equals("chromeHeadless") && !browserName.contains("GCP")) {
                browserName = browserName.toLowerCase();
            }
        }
        RemoteWebDriver newDriver = DriverUtil.initDriver(browserName, incognito, proxy);
        if (browserName.contains("GCP") || browserName.contains("Headless")) {
            newDriver.manage().window().setSize(new Dimension(1920, 1080));
        } else if (System.getProperty("executingEnv").contains("GCP") && browserName.contains("extension")) {
            newDriver.manage().window().setSize(new Dimension(1920, 1080));
        } else {
            newDriver.manage().window().maximize();
        }
        newDriver.manage().deleteAllCookies();
        return newDriver;
    }
    public static void switchToBrowser(String browserName) {
        threadLocalActiveBrowsers.get().put("current", getBrowser(browserName));
        BasePage.threadLocalDriverBasePage.set(threadLocalActiveBrowsers.get().get("current"));
    }

    public static void closeBrowser(String browserName) {
        getBrowser(browserName).quit();
        threadLocalActiveBrowsers.get().remove(browserName);
    }

    public static boolean startAppiumServer() throws IOException {
        CommandLine cmd = null;
        CommandLine cmdAPPIUM = null;
        LOG.info(Hook.platform);
        if (Hook.platform.equals("android")) {
            if (System.getProperty("executingEnv").contains("jenkins"))
                cmd = CommandLine.parse("appium.cmd -a 127.0.0.1 -p 4723");
            else if (System.getProperty("executingEnv").contains("local"))
                cmd = CommandLine.parse("appium.cmd -a 0.0.0.0 -p 4723");
        } else if (Hook.platform.equals("ios") && (System.getProperty("executingEnv").contains("jenkins"))) {
            cmd = CommandLine.parse("appium -a 127.0.0.1 -p 4723");
        }
        if (Hook.platform.contains("ssh")) {
            LOG.info("Appium server already running");
        } else {
            LOG.info("running command: {}", cmd);
            DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
            ExecuteWatchdog watchdog = new ExecuteWatchdog(60 * 1000L);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
            executor = new DefaultExecutor();
            executor.setStreamHandler(streamHandler);
            executor.setExitValue(1);
            executor.setWatchdog(watchdog);
            executor.execute(cmd, resultHandler);
            int count = 0;
            while (!outputStream.toString().contains("Appium REST http interface listener started")) {
                LOG.info(outputStream.toByteArray());
                LOG.info("waiting for Appium Server to start ({} second(s))", count);
                count++;
                if (count >= 31) {
                    LOG.info("Appium Server could not been started, trying to restart Appium server");
                    return false;
                }
            }
        }
        return true;
    }

    public static void closeAppiumServer() {
        CommandLine cmd = null;
        if (Hook.platform.contains("android")) {
            cmd = CommandLine.parse("taskkill /IM node.exe /F");
        } else if (Hook.platform.contains("ios")) {
            cmd = CommandLine.parse("pkill -f appium");
        }
        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
        ExecuteWatchdog watchdog = new ExecuteWatchdog(60 * 1000L);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
        executor = new DefaultExecutor();
        executor.setStreamHandler(streamHandler);
        executor.setExitValue(1);
        executor.setWatchdog(watchdog);
        try {
            executor.execute(cmd, resultHandler);
        } catch (Exception ignored) {
        }
        LOG.info("shutting down appium Server");
    }

  public static void getProxyServer() {
        proxyHost = TestDataLoader.getTestData("@TD:proxyHost");
        proxyPort = TestDataLoader.getTestData("@TD:proxyPort");
    }

    public static RemoteWebDriver initDriverWithUserAgent(String browserName, String userAgent) {
      RemoteWebDriver driver;
        switch (browserName) {
            case "chromeHeadless":
                driver = initChromeHeadlessUserAgent(userAgent, false, false);
                break;
            case "chromeGCP":
                driver = initChromeDockerUserAgent(userAgent, false);
                break;
            case "chrome":
                driver = initChrome(false, false);
                break;
            case "edgeGCP":
                driver = initEdgeDockerUserAgent(userAgent, false);
                break;
            case "edge":
                driver = initEdge(false);
                break;
            case "firefoxGCP":
                driver = initDockerFirefox(false);
                break;
            case "firefox":
                driver = initFirefox(false, false);
                break;
            default:
                throw new CucumberException("Browser name should be one of the following values: chromeHeadless, chromeGCP, chrome, edgeGCP, edge, firefoxGCP, firefox");
        }
        setCurrentDriver(driver);
        LOG.info("The current thread id '{}' has the following active browsers: {}", Thread.currentThread().getName(), threadLocalActiveBrowsers.get());
        return driver;
    }

    private static RemoteWebDriver initEdgeDockerUserAgent(String userAgent, boolean incognito) {
        dc = new DesiredCapabilities();
        dc.setCapability(BROWSER_NAME, EDGE);
        dc.setBrowserName(EDGE.browserName());
        dc.setPlatform(Platform.LINUX);

        HashMap<String, Object> edgePrefs = new HashMap<>();
        edgePrefs.put("profile.default_content_settings.popups", 2);
        edgePrefs.put("profile.default_content_setting_values.notifications", 2);

        EdgeOptions edgeOptions = new EdgeOptions();
        edgeOptions.setCapability("prefs", edgePrefs);
        edgeOptions.setCapability(ACCEPT_INSECURE_CERTS, true);
        edgeOptions.setCapability("acceptInsecureCerts", true);
        edgeOptions.setCapability("acceptSslCerts", true);
        edgeOptions.setPageLoadStrategy(PageLoadStrategy.NORMAL);
        edgeOptions.addArguments("--headless");
        edgeOptions.addArguments("--user-agent=" + userAgent);

        if (incognito) {
            LOG.info("adding incognito to Browser");
            edgeOptions.setCapability("ms:inPrivate", true);
        }

        try {
            dc.setBrowserName("edge");
            dc.merge(edgeOptions);
            LOG.info("Hub endpoint is: {}", HUB_ENDPOINT);
            driver = new RemoteWebDriver(new URL("http://" + HUB_ENDPOINT + "/wd/hub"), dc);
            LOG.info("Initialized driver is: {}", driver);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return driver;
    }

    private static RemoteWebDriver initChromeDockerUserAgent(String userAgent, boolean incognito) {
        RemoteWebDriver driver;
        dc = new DesiredCapabilities();
        dc.setCapability(BROWSER_NAME, Browser.CHROME);
        dc.setBrowserName(Browser.CHROME.browserName());
        dc.setPlatform(Platform.LINUX);

        try {
            dc.setBrowserName("chrome");
            LoggingPreferences logPrefsCHROME = new LoggingPreferences();
            logPrefsCHROME.enable(LogType.PERFORMANCE, Level.ALL);

            ChromeOptions chromeOptions = new ChromeOptions();
            Map<String, Object> preferences = new HashMap<>();
            preferences.put("profile.default_content_setting_values.notifications", 2);
            preferences.put("plugins.plugins_disabled", new String[] { "Chrome PDF Viewer" });
            preferences.put("download.default_directory", PATH_TO_DOWNLOAD_DIR);
            preferences.put("plugins.always_open_pdf_externally", true);
            preferences.put("profile.default_content_settings.popups", 0);
            chromeOptions.setExperimentalOption("prefs", preferences);
            chromeOptions.addArguments("--disable-gpu");
            if (System.getProperty("os.name").toLowerCase().contains("linux")) {
                // in Linux, we need these config to work
                LOG.info("Working in Linux OS");
                chromeOptions.addArguments("--no-sandbox");
                chromeOptions.addArguments("--disable-shm-usage");
                chromeOptions.addArguments("--disable-dev-shm-usage");
                chromeOptions.addArguments("--disable-setuid-sandbox");
                chromeOptions.addArguments("--disable-dev-shm-using");
                chromeOptions.addArguments("start-maximized");
                chromeOptions.addArguments("disable-infobars");
                chromeOptions.addArguments("--headless=new");
            }
            chromeOptions.addArguments("--user-agent=" + userAgent);
            LOG.info("Add user-agent \"{}\" to chrome", userAgent);
            chromeOptions.addArguments("--lang=en");
            chromeOptions.addArguments("--enable-javascript");
            chromeOptions.addArguments("--window-size=1920,1080");
            chromeOptions.setExperimentalOption("w3c", true);
            chromeOptions.setCapability("browserName", "chrome");
            chromeOptions.setAcceptInsecureCerts(true);
            chromeOptions.setCapability(LOGGING_PREFS, logPrefsCHROME);

            if (incognito) {
                LOG.info("adding incognito to Browser");
                chromeOptions.addArguments("--incognito");
            }
            dc.merge(chromeOptions);
            LOG.info("Hub endpoint is: {}", HUB_ENDPOINT);
            driver = new RemoteWebDriver(new URL("http://" + HUB_ENDPOINT + "/wd/hub"), dc);
            LOG.info("Initialized driver is: {}", driver);
        } catch (Exception e) {
            throw new CucumberException("Failed to initialize ChromeDriver. Error: " + e.getMessage());
        }
        return driver;
    }

    private static RemoteWebDriver initChromeHeadlessUserAgent(String userAgent, boolean proxy, boolean incognito) {
        RemoteWebDriver driver;
        LoggingPreferences logPrefsCHROME = new LoggingPreferences();
        logPrefsCHROME.enable(LogType.PERFORMANCE, Level.ALL);

        ChromeOptions chromeOptions = new ChromeOptions();
        Map<String, Object> preferences = new HashMap<>();
        preferences.put("profile.default_content_setting_values.notifications", 2);
        preferences.put("plugins.plugins_disabled", new String[] { "Chrome PDF Viewer" });
        preferences.put("download.default_directory", PATH_TO_DOWNLOAD_DIR);
        preferences.put("plugins.always_open_pdf_externally", true);
        preferences.put("profile.default_content_settings.popups", 0);
        chromeOptions.setExperimentalOption("prefs", preferences);
        chromeOptions.addArguments("--disable-gpu");
        chromeOptions.addArguments("--remote-allow-origins=*");

        chromeOptions.addArguments("--no-sandbox");
        chromeOptions.addArguments("--disable-shm-usage");
        chromeOptions.addArguments("--disable-dev-shm-usage");
        chromeOptions.addArguments("--disable-setuid-sandbox");
        chromeOptions.addArguments("--disable-dev-shm-using");
        chromeOptions.addArguments("window-size=1920,1080");
        chromeOptions.addArguments("disable-infobars");
        chromeOptions.addArguments("--headless=new");
        chromeOptions.addArguments("--user-agent=" + userAgent);
        chromeOptions.addArguments("--lang=en");
        chromeOptions.addArguments("--enable-javascript");
        if(TestDataLoader.getTestData("@TD:IsTrialPromo").equals("Yes")) {
          chromeOptions.addExtensions(new File(modHeaderExtension.toAbsolutePath().toString()));
        }
        chromeOptions.setExperimentalOption("w3c", true);
        chromeOptions.setCapability("browserName", "chrome");
        chromeOptions.setAcceptInsecureCerts(true);
        chromeOptions.setCapability(LOGGING_PREFS, logPrefsCHROME);

         /*
        Ensure that your chrome browser has proxy enabled.
        Settings - Advanced - System : Open your computer proxy settings should be able to open the dialog
         */
        if (proxy) {
            Proxy proxyBrowser = new Proxy();
            getProxyServer();
            proxyBrowser.setSslProxy(proxyHost + ":" + proxyPort);
            chromeOptions.setCapability("proxy", proxyBrowser);
        }

        if (incognito) {
            LOG.info("adding incognito to Browser");
            chromeOptions.addArguments("--incognito");
        }
        driver = new ChromeDriver(chromeOptions);
        return driver;
    }

    public static void getDeviceCapabilities(String device) {
        String deviceInfo = TestDataLoader.getTestData(device);
        String[] infos = deviceInfo.split("[\\|]");
        System.setProperty("deviceUDID", infos[0].trim());
        System.setProperty("deviceName", infos[1].trim());
        System.setProperty("platformVersion", infos[2].trim());
        LOG.info("deviceUDID: {}", System.getProperty("deviceUDID"));
        LOG.info("deviceName: {}", System.getProperty("deviceName"));
        LOG.info("platformVersion: {}", System.getProperty("platformVersion"));
    }

    //Setup to enable download file in headless
    private static void enableDownloadFileHeadless(String driverServiceUrl, RemoteWebDriver driver) {
        Map<String, Object> commandParams = new HashMap<>();
        commandParams.put("cmd", "Page.setDownloadBehavior");
        Map<String, String> params = new HashMap<>();
        params.put("behavior", "allow");
        params.put("downloadPath", PATH_TO_DOWNLOAD_DIR);
        commandParams.put("params", params);
        ObjectMapper objectMapper = new ObjectMapper();
        HttpClient httpClient = HttpClientBuilder.create().build();
        try {
            String command = objectMapper.writeValueAsString(commandParams);
            String url = driverServiceUrl + "/session/" + driver.getSessionId() + "/chromium/send_command";
            LOG.info("Driver service url is: {}", driverServiceUrl);
            LOG.info("Driver session id is: {}", driver.getSessionId());
            HttpPost request = new HttpPost(url);
            request.addHeader("content-type", "application/json");
            request.setEntity(new StringEntity(command));
            httpClient.execute(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

  public static void initializeBrowserWithSessionAlias(String browserName, String sessionAlias) {
    if (threadLocalActiveBrowsers.get().size() == 1)
      threadLocalActiveBrowsers.get().put("initial", DriverUtil.getBrowser("current"));
    if (browserName.contains("Headless") || browserName.contains("GCP"))
      threadLocalActiveBrowsers.get().put(sessionAlias, DriverUtil.initDriverWithUserAgent(browserName, TestDataLoader.getTestData("@TD:user-agent")));
    else
      threadLocalActiveBrowsers.get().put(sessionAlias, DriverUtil.initNewDriver(browserName, browserName.contains("incognito"), false));
    BasePage.threadLocalDriverBasePage.set(threadLocalActiveBrowsers.get().get(sessionAlias));
  }
}
