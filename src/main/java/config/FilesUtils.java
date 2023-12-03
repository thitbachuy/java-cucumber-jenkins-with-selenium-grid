package config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.cucumber.core.exception.CucumberException;
import io.cucumber.java.Scenario;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static steps.Hook.listOfImportantDataInEachScenario;
import static steps.Hook.testStartDateTime;

public class FilesUtils {
    private static final Logger LOG = LogManager.getLogger(FilesUtils.class);

    public static Sheet readExcelFile(String filePath, String sheetName) {
      String fileExtension = filePath.substring(filePath.indexOf("."));
      try (FileInputStream file = new FileInputStream(filePath);
           Workbook workbook = fileExtension.equals(".xlsx") ? new XSSFWorkbook(file) : new HSSFWorkbook(file)){
        return workbook.getSheet(sheetName);
      } catch (IOException e) {
        throw new CucumberException(e.getMessage());
      }
    }

    public static String readTextFile(String filePath) {
        try (FileInputStream fis = new FileInputStream(filePath)){
            return IOUtils.toString(fis, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new CucumberException(e.getMessage());
        }
    }

  public static String readDocFile(String filePath) {
    StringBuilder data = new StringBuilder();
    try (FileInputStream fis = new FileInputStream(filePath)) {
      XWPFDocument document = new XWPFDocument(fis);
      List<XWPFParagraph> paragraphs = document.getParagraphs();
      for (XWPFParagraph para : paragraphs) {
        data.append(para.getText());
      }
    } catch (IOException e) {
      throw new CucumberException(e.getMessage());
    }
    LOG.info("The texts retrieved from the document: {}", data);
    return data.toString();
  }

    public static String readPdfFile(String filePath) {
        try (PDDocument pdfDocument = PDDocument.load(new File(filePath))){
          return (new PDFTextStripper()).getText(pdfDocument);
        } catch (IOException e) {
            throw new CucumberException("Fail to read pdf file: " + filePath + " due to: " + e.getMessage());
        }
    }

  public void createTestExecutionJsonFile() {
        String testExecutionDirectoryPath = "src/test/resources/jiraxray/testexecution/active";
        //Get all available file name in test set (only support .txt)
        FilenameFilter textFiler = (dir, name) -> name.toLowerCase().endsWith(".txt");
        List<File> testExecutionFileList = Arrays.asList(Objects.requireNonNull(new File(testExecutionDirectoryPath).listFiles(textFiler)));
        List<String> testExecutionFilePathList = new ArrayList<>();
        testExecutionFileList.forEach(file -> testExecutionFilePathList.add(file.getAbsolutePath()));
        /* Due to test execution need to reset previous result =>Remove all previous test, then add them again.
           In this case, will remove the code all available test which are stored in src/test/resources/jiraxray/AllTest.txt
         */
        /* Extract test set key and test set id
           Build removed test list
         */
        String testExecutionFileName = "";
        String testExecutionId = "";
        StringBuilder addedTestList;
        String lineContent;
        String[] testKeyIdArray;
        //Build added test list
        for (String testSetFilePath : testExecutionFilePathList) {
            addedTestList = new StringBuilder();
            try {
                Scanner documentScanner = new Scanner(new File(testSetFilePath));
                while (documentScanner.hasNextLine()) {
                    lineContent = documentScanner.nextLine();
                    testKeyIdArray = lineContent.split(" = ");
                    if (lineContent.startsWith("TestExecution")) {
                        testExecutionId = String.format("\"%s\"", testKeyIdArray[1]);
                        testExecutionFileName = testKeyIdArray[0];
                    } else if (lineContent.startsWith("Add")) {
                        addedTestList.append("\"").append(testKeyIdArray[1]).append("\", ");
                    }
                }
                documentScanner.close();
            } catch (FileNotFoundException e) {
                throw new CucumberException(e.getMessage());
            }
            if (addedTestList.length() > 0) addedTestList.delete(addedTestList.length() - 2, addedTestList.length());
            //Write test set json file
            LOG.info("*******************************************************************");
            LOG.info("Creating test execution json file which has Id \"{}\"", testExecutionId);
            LOG.info("Add tests: {} to test execution {}", addedTestList, testExecutionId);
            String queryValue = String.format("mutation{addTestsToTestExecution(issueId: %s,testIssueIds: [%s]){addedTests warning}}", testExecutionId, addedTestList);
            Map<String, String> testSet = new HashMap<>();
            testSet.put("query", queryValue);
            ObjectMapper mapper = new ObjectMapper();
            try {
                String fileOutput = String.format("%s/%s.json", testExecutionDirectoryPath, testExecutionFileName);
                mapper.writeValue(new File(fileOutput), testSet);
                LOG.info("Json file for {} successfully created!", testExecutionFileName);
            } catch (IOException e) {
                throw new CucumberException(e.getMessage());
            }
        }
    }

  public static void updateTestResultsSummaryToExcelFile(String testId, int totalPasses, int totalFails) {
        String filePath = Paths.get("").toAbsolutePath() + "/src/main/java/templates/testResult.xlsx";
        int totalScenarios = totalPasses + totalFails;
        LOG.info("Total scenarios are '{}'.\nTotal pass scenarios are '{}'.\nTotal fail scenarios are '{}'", totalScenarios, totalPasses, totalFails);
        try (
          FileInputStream file = new FileInputStream(filePath);
          Workbook workbook = WorkbookFactory.create(file);
          FileOutputStream outFile = new FileOutputStream(filePath)
          ) {
            CellStyle cellStyle = workbook.createCellStyle();
            Sheet sheet = workbook.getSheetAt(0);
            int currentRowNumbers = sheet.getLastRowNum();
            int newRowNumber = currentRowNumbers + 1;
            sheet.createRow(newRowNumber);
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
          for (int i = 0; i <= 6; i++) {
                Row row = sheet.getRow(newRowNumber);
                Cell cell = row.createCell(i);
              switch (i) {
                case 1:
                  sheet.getRow(newRowNumber).createCell(i).setCellValue(testId);
                  LOG.info("Writing row '{}' at column 'ID' with value '{}'", newRowNumber, testId);
                  break;
                case 2:
                  sheet.getRow(newRowNumber).createCell(i).setCellValue(totalPasses);
                  LOG.info("Writing row '{}' at column 'Total Passed' with value '{}'", newRowNumber, totalPasses);
                  break;
                case 3:
                  sheet.getRow(newRowNumber).createCell(i).setCellValue(totalFails);
                  LOG.info("Writing row '{}' at column 'Total Failed' with value '{}'", newRowNumber, totalFails);
                  break;
                case 4:
                  sheet.getRow(newRowNumber).createCell(i).setCellValue(totalScenarios);
                  LOG.info("Writing row '{}' at column 'Total Scenario' with value '{}'", newRowNumber, totalFails);
                  break;
                case 5: {
                  String formula = "C" + (newRowNumber + 1) + "/" + "E" + (newRowNumber + 1);
                  cell.setCellFormula(formula);
                  cellStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00%"));
                  cell.setCellStyle(cellStyle);
                  evaluator.evaluateFormulaCell(cell);
                  LOG.info("Writing row '{}' at column 'Passed Ratio' with value '{}'", newRowNumber + 1, cell.getNumericCellValue() * 100 + "%");
                  break;
                }
                case 6: {
                  String formula = "D" + (newRowNumber + 1) + "/" + "E" + (newRowNumber + 1);
                  cell.setCellFormula(formula);
                  cellStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00%"));
                  cell.setCellStyle(cellStyle);
                  evaluator.evaluateFormulaCell(cell);
                  LOG.info("Writing row '{}' at column 'Failed Ratio' with value '{}'", newRowNumber + 1, cell.getNumericCellValue() * 100 + "%");
                  break;
                }
                default:
                  sheet.getRow(newRowNumber).createCell(i).setCellValue(testStartDateTime.toString());
                  LOG.info("Writing row '{}' at column 'Date' with value '{}'", newRowNumber, testStartDateTime);
                  break;
              }
            }
            workbook.write(outFile);
            LOG.info("Test Results for '{}' are updated successfully into file '{}'", testId, filePath);
        } catch (IOException e) {
            LOG.info("Failed to update Test Results to file {} due to:\n{}", filePath, e.getMessage());
        }
    }

    public void updateCucumberJsonFile(){
      String testResultCucumberDirectoryPath = Paths.get("").toAbsolutePath().toString();
      LOG.info("Current working directory is: {}", testResultCucumberDirectoryPath);
      //Get all available file name in test set (only support .txt)
      FilenameFilter textFiler = (dir, name) -> name.toLowerCase().matches("^cucumber.*\\.json$");
      File[] testExecutionFileList = Objects.requireNonNull(new File(testResultCucumberDirectoryPath).listFiles(textFiler));
      for (File file: testExecutionFileList) {
        LOG.info("Found cucumber json file: {}", file.getName());
        ObjectMapper objectMapper = new ObjectMapper();
        try {
          // Update the JSON data
          JsonNode jsonNode = objectMapper.readTree(file);
          JsonNode tagsNode = jsonNode.get(0).get("tags");
          ((ObjectNode) jsonNode.get(0)).put("description", "@E2EAutomationTestResults");
          LOG.info("The 'Description' is updated to '@E2EAutomationTestResults'");
          for (JsonNode tagNode : tagsNode) {
            LOG.info("Put the 'name' field to empty from the 'tags' node '{}'", tagNode);
            ((ObjectNode) tagNode).put("name", "");
          }
          // Write the updated JSON string back to the file
          objectMapper.writeValue(file, jsonNode);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

  public static void saveExecutionGeneralInfoToExcelFile(List<Scenario> scenarioList) {
    String filePath = Paths.get("").toAbsolutePath() + "/src/main/java/templates/executionGeneralInfo.xlsx";
    try (
      FileInputStream file = new FileInputStream(filePath);
      Workbook workbook = WorkbookFactory.create(file);
      FileOutputStream outFile = new FileOutputStream(filePath)
    ) {
      Sheet sheet = workbook.getSheetAt(0);
      // Remove all existing information in the sheet
      for (int existingRow = 1; existingRow <= sheet.getLastRowNum(); existingRow++) {
        sheet.removeRow(sheet.getRow(existingRow));
      }
      for (int i = 0; i < scenarioList.size(); i++) {
       // Add information of each scenario to sheet
        sheet.createRow(i + 1);
        addInformationOfEachScenarioToSheet(sheet, i + 1, scenarioList.get(i));
      }
      workbook.write(outFile);
      LOG.info("Execution General Information for '{}' are updated successfully into file '{}'", System.getProperty("cucumber.filter.tags"), filePath);
    } catch (IOException e) {
      LOG.info("Failed to update Execution General Information to file {} due to:\n{}", filePath, e.getMessage());
    }
  }

  private static void addInformationOfEachScenarioToSheet(Sheet sheet, int rowNumber, Scenario scenario) {
    for (int i = 0; i <= 13; i++) {
      String valueToAdd;
      String columnName;
      switch (i) {
        case 1:
          valueToAdd = getFeatureFileRelatedInfo(scenario, rowNumber).get("feature name");
          columnName = "Flow";
          break;
        case 2:
          valueToAdd = scenario.getName();
          columnName = "Scenario";
            break;
        case 3:
          valueToAdd = String.valueOf(scenario.getStatus());
          columnName = "Status";
          break;
        case 4:
          valueToAdd = getFeatureFileRelatedInfo(scenario, rowNumber).get("test run tags");
          columnName = "TC / TE tags";
          break;
        case 5:
          valueToAdd = getFeatureFileRelatedInfo(scenario, rowNumber).get("other tags");
          columnName = "Other tags";
          break;
        case 6:
          valueToAdd = listOfImportantDataInEachScenario.get(rowNumber - 1).get("api_email");
          columnName = "Api email";
          break;
        case 7:
          valueToAdd = listOfImportantDataInEachScenario.get(rowNumber - 1).get("randomEmail");
          columnName = "Generated email";
          break;
        case 8:
          valueToAdd = listOfImportantDataInEachScenario.get(rowNumber - 1).get("api_pass");
          columnName = "Api password";
          break;
        case 9:
          valueToAdd = listOfImportantDataInEachScenario.get(rowNumber - 1).get("initialPurchase_ParentOrderId");
          columnName = "Initial purchase parent Order ID";
          break;
        case 10:
          valueToAdd = listOfImportantDataInEachScenario.get(rowNumber - 1).get("SkyMpp");
          columnName = "Initial Sky Pin / MP Pin";
          break;
        case 11:
          valueToAdd = listOfImportantDataInEachScenario.get(rowNumber - 1).get("failed step");
          columnName = "Failed step";
          break;
        case 12:
          valueToAdd = listOfImportantDataInEachScenario.get(rowNumber - 1).get("failure message");
          columnName = "Failure message";
          break;
        case 13:
          valueToAdd = getFeatureFileRelatedInfo(scenario, rowNumber).get("feature file");
          columnName = "Feature file";
          break;
        default:
          valueToAdd = String.valueOf(rowNumber);
          columnName = "No";
          break;
      }
      if (Arrays.asList("No", "Failed step line in feature file").contains(columnName) && !valueToAdd.isEmpty())
        sheet.getRow(rowNumber).createCell(i).setCellValue(Integer.parseInt(valueToAdd));
      else
        sheet.getRow(rowNumber).createCell(i).setCellValue(valueToAdd);
      LOG.info("Writing row '{}' at column '{}' with value '{}'", rowNumber, columnName, valueToAdd);
    }
  }

  private static Map<String, String> getFeatureFileRelatedInfo(Scenario scenario, int rowNumber) {
    List<String> featureLine;
    Map<String, String> info = new HashMap<>();
    try {
      featureLine = Files.readAllLines(Paths.get(scenario.getUri()));
    } catch (IOException e) {
      throw new CucumberException("Get error when reading feature file: " + e);
    }
    String testRunTags = "";
    String otherTags = "";
    for (String tag : scenario.getSourceTagNames()) {
      testRunTags = tag.contains("@TEST") || tag.contains("@E2ED") ? testRunTags + tag : testRunTags;
      otherTags = !tag.contains("@TEST") && !tag.contains("@E2ED") ? otherTags + tag : otherTags;
    }
    info.put("test run tags", testRunTags);
    info.put("other tags", otherTags);
    info.put("feature name", featureLine.get(0).replace("Feature: ", "").replace("Feature:", ""));
    info.put("feature file", scenario.getUri().getPath().split("/")[scenario.getUri().getPath().split("/").length - 1]);
    return info;
  }
}
