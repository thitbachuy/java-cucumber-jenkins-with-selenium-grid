package config;

/*
Set the filename for INPUT Data as SystemProperty when executing the test via Maven
to get TestData via Execution use method getTestData, to write testdata in File to reuse later again use method setTestData
*/

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.crypto.tink.Aead;
import com.google.crypto.tink.CleartextKeysetHandle;
import com.google.crypto.tink.JsonKeysetReader;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.aead.AeadConfig;
import io.cucumber.core.exception.CucumberException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import steps.Hook;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Map;
import java.util.Random;

public class TestDataLoader {

  private static final Logger LOG = LogManager.getLogger(TestDataLoader.class);
  private static final Map<String, String> commonTestData = readJsonFile("COMMON_DATA.json");
  private static final Map<String, String> envSpecificTestData = readJsonFile(
      "INPUT_" + Hook.testedEnv.toUpperCase() + ".json");
  private static final Map<String, String> testDataRuntime;
  private static Random random = new Random();

  static {
    assert commonTestData != null;
    assert envSpecificTestData != null;
    testDataRuntime = mergeTestData(commonTestData, envSpecificTestData);
  }


  public static Map<String, String> readJsonFile(String dataFileToLoad) {
    ObjectMapper mapper = new ObjectMapper();
    try {
      return mapper.readValue(new File("src/main/java/testdata/input/" + dataFileToLoad),
          new TypeReference<Map<String, String>>() {
          });

    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public static Map<String, String> mergeTestData(Map<String, String> common,
      Map<String, String> envSpecific) {
    if (common == null) {
      throw new CucumberException("common data must not null");
    }
    if (envSpecific == null) {
      throw new CucumberException("envSpecific data must not null");
    }
    common.forEach((key, value) -> {
      envSpecific.forEach((key2, value2) -> {
        if (key.equals(key2)) {
          LOG.info("found dublicate key \"{}\"", key);
        }
      });
    });
    Map<String, String> environmentVariables = System.getenv();
    environmentVariables.forEach((key, value) ->
        LOG.info("Key {} has value {}", key, value)
    );
    common.putAll(envSpecific);
    common.putAll(environmentVariables);
    return common;
  }

  public static void testDataRuntimeEndOfExecution() {

    ObjectMapper mapper = new ObjectMapper();
    try {
      mapper.writeValue(new File(
              "src/main/java/testdata/output/OUTPUT_" + Hook.testedEnv.toUpperCase() + ".json"),
          testDataRuntime);
      LOG.info("Post Execution Test Data saved in OUTPUT{}.json file.", Hook.testedEnv);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  public static String getTestData(String key) {
    assert testDataRuntime != null;
    String value = "";

    if (key.startsWith("@TD:")) {
      int end = key.length();
      value = testDataRuntime.get(key.substring(4, end));
      checkTestData(key, value);
    } else if (key.startsWith("@date:") || key.equals("@DateAsValue") || key.equals(
        "randomNumberUsingDateTime")) {
      value = DateUtil.getTestDataDateValue(key, value);
    } else if (key.equals("@RandomFirstName") || key.equals("@RandomLastName")) {
      value = getTestDataNameValue(key, value);
    } else if (key.equals("@BusinessHoursTest") || key.equals("@BusinessHoursPast")) {
      String timeZone = "Europe/Berlin";
      LocalTime now = LocalTime.now(ZoneId.of(timeZone));
      DateTimeFormatter tf = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
      value = key.equals("@BusinessHoursTest") ? tf.format(
          now.plusMinutes(Integer.parseInt(TestDataLoader.getTestData("@TD:businessHoursExtra"))))
          + "Z" : tf.format(now.minusMinutes(2)) + "Z";
    } else {
      value = key;
    }
    return value;
  }

  public static void checkTestData(String key, String value) {
    if (value == null) {
      throw new CucumberException("your key " + key + " is not pointing to a Value!");
    } else {
      String lowercaseKey = key.toLowerCase();
      if (lowercaseKey.contains("password") || lowercaseKey.contains("pw") || lowercaseKey.contains(
          "pin") || lowercaseKey.contains("clientid") || lowercaseKey.contains("clientsecret")
          || lowercaseKey.contains("credentials") || lowercaseKey.contains("mailosaur_apikey")) {
        LOG.info("Loading \"{}\" as \"*********\" from Test Data Set", key);
      } else {
        LOG.info("Loading \"{}\" as \"{}\" from Test Data Set", value, key);
      }
    }
  }

  public static String getTestDataNameValue(String key, String value) {
    if (key.equals("@RandomFirstName")) {
      value = "SkyTest";
      setTestData("currentRandomFirstName", value);
    } else if (key.equals("@RandomLastName")) {
      value = "Deutsch " + StringUtils.capitalize(generateString(6));
      setTestData("currentRandomLastName", value);
    }
    return value;
  }

  public static String generateString(Integer lenght) {
    char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    StringBuilder sb = new StringBuilder(lenght);
    for (int i = 0; i < lenght; i++) {
      char c = chars[random.nextInt(chars.length)];
      sb.append(c);
    }
    String output = sb.toString();
    LOG.info("created string: {}", output);
    return output;
  }

  private static String decrypt(String encryptedText, String keysetPath) throws
      GeneralSecurityException, IOException {
    // Initialization
    AeadConfig.register();
    // Read keyset from file
    KeysetHandle keysetHandle = CleartextKeysetHandle.read(
        JsonKeysetReader.withFile(new File(keysetPath)));
    // Get AEAD (Authenticated Encryption with Associated Data) primitive
    Aead aead = keysetHandle.getPrimitive(Aead.class);
    // Decode base64 string to byte array
    byte[] cipherData = Base64.getDecoder().decode(encryptedText);
    // Decrypt cipher text
    String associatedData = "TestAutomation@Sky";
    byte[] plainData = aead.decrypt(cipherData, associatedData.getBytes(StandardCharsets.UTF_8));
    // Return decrypted string
    return new String(plainData, StandardCharsets.UTF_8);
  }

  public static void setTestData(String key, String value) {
    assert testDataRuntime != null;
    testDataRuntime.put(key, value);
    LOG.info("Saving \"{}\" as \"{}\" in Test Data Set", value, key);
  }


}
