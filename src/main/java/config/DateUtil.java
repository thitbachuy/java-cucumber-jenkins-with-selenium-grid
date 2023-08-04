package config;

import io.cucumber.core.exception.CucumberException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateUtil {

  private static final Logger LOG = LogManager.getLogger(DateUtil.class);

  public static String getTestDataDateValue(String key, String value) {
    if (key.startsWith("@date:")) {
      if (key.contains("today")) {
        value = calculateTimestampWithFormat("today", key.substring(11));
      } else {
        value = calculateTimestampWithFormat(key.split("/")[0].substring(6), key.split("/")[1]);
      }
      LOG.info("value of Date : {}", value);
      LOG.info("Loading \"{}\" as \"{}\" from Test Data Set", value, key);
    } else if (key.equals("@DateAsValue")) {
      String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(
          new Timestamp(System.currentTimeMillis()));
      value = timeStamp.replace("\\.", "");
    } else if (key.equals("@DateAsValue10")) {
      String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(
          new Timestamp(System.currentTimeMillis()));
      value = timeStamp.replace("\\.", "");
      value = value.substring(value.length() - 10);
    } else if (key.equals("@NameDate")) {
      String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(
          new Timestamp(System.currentTimeMillis()));
      value = "NAME " + timeStamp.replace("\\.", "");
    } else if (key.equals("randomNumberUsingDateTime")) {
      LocalDateTime localDateTime = LocalDateTime.now(ZoneId.of("Europe/Paris"));
      String localDateTimeString = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")
          .format(localDateTime);
      TestDataLoader.setTestData("randomNumberUsingDateTime", localDateTimeString);
      value = localDateTimeString;
    }
    return value;
  }

  public static String calculateTimestampWithFormat(String originalDate, String calculation) {
    String[] dateTimeInfoArray = calculation.split("_FORMAT");
    String differenceDateTime = dateTimeInfoArray[0];
    String dateTimeFormat = dateTimeInfoArray[1];

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern(dateTimeFormat);
    LocalDate newDate = calculateDateTime(originalDate, differenceDateTime);

    return dtf.format(newDate);
  }

  public static LocalDate calculateDateTime(String startDate, String value) {
    LocalDate newDate;
    if (startDate.equals("today")) {
      newDate = LocalDate.now(ZoneId.of("Europe/Paris"));
    } else {
      String[] newDateInfo = startDate.split("_ofFORMAT");
      newDate = LocalDate.parse(TestDataLoader.getTestData(newDateInfo[0]),
          DateTimeFormatter.ofPattern(newDateInfo[1]));
    }
    if (!value.equals("")) {
      String[] dateTimeDifferences = value.split("_");
      for (String difference : dateTimeDifferences) {
        long calc = Long.parseLong(difference.replaceAll("[a-zA-Z]", ""));
        if (difference.contains("day")) {
          newDate = newDate.plusDays(calc);
        } else if (difference.contains("week")) {
          newDate = newDate.plusWeeks(calc);
        } else if (difference.contains("month")) {
          newDate = newDate.plusMonths(calc);
        } else if (difference.contains("year")) {
          newDate = newDate.plusYears(calc);
        } else {
          throw new CucumberException("please choose a valid format like day, week, month or year");
        }
      }
    }
    return newDate;
  }
}
