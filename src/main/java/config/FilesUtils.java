package config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.cucumber.core.exception.CucumberException;
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
import steps.Hook;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static steps.Hook.testStartDateTime;

public class FilesUtils {

  private static final Logger LOG = LogManager.getLogger(FilesUtils.class);

  public static Sheet readExcelFile(String filePath, String sheetName) {
    Workbook workbook = null;
    try (FileInputStream file = new FileInputStream(filePath)) {
      String fileExtension = filePath.substring(filePath.indexOf("."));
      if (fileExtension.equals(".xlsx")) {
        workbook = new XSSFWorkbook(file);
      } else if (fileExtension.equals(".xls")) {
        workbook = new HSSFWorkbook(file);
      }
      assert workbook != null;
      return workbook.getSheet(sheetName);
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    } finally {
      if (workbook != null) {
        try {
          workbook.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  public static WriteDataToExcelBuilder writeData(String data) {
    return new WriteDataToExcelBuilder(data);
  }

  public static class WriteDataToExcelBuilder {

    private final String data;
    private Workbook workbook;
    private File file;
    private FileInputStream inputStream;
    private Sheet sheet;

    public WriteDataToExcelBuilder(String data) {
      this.data = data;
    }

    public WriteDataToExcelBuilder toExcel(String filePath, String sheetName) {
      try {
        file = new File(filePath);
        inputStream = new FileInputStream(file);
        workbook = WorkbookFactory.create(inputStream);
        assert this.workbook != null;
        sheet = workbook.getSheet(sheetName);
        return this;
      } catch (FileNotFoundException e) {
        throw new CucumberException(
            "File '" + filePath + "' does not exist or being opened by another application.");
      } catch (IOException e) {
        throw new CucumberException(
            "Exception while updating an existing excel file: " + e.getMessage());
      }
    }

    public void atCell(int cellColumn, int cellRow) {
      try {
        Row row = sheet.getRow(cellRow);
        if (row == null) {
          row = sheet.createRow(cellRow);
        }
        Cell cell = row.createCell(cellColumn);
        cell.setCellValue(data);
        inputStream.close();
        LOG.info("Writing data to the excel file '{}'", file.getAbsolutePath());
        FileOutputStream outputStream = new FileOutputStream(file);
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
        LOG.info("Data '{}' is written to cell column '{}' and cell row '{}'.", data, cellColumn,
            cellRow);
      } catch (IOException e) {
        throw new CucumberException(
            "Exception while updating an existing excel file: " + e.getMessage());
      }
    }
  }

  public static String readTextFile(String filePath) {
    FileInputStream fis;
    String data;
    try {
      fis = new FileInputStream(filePath);
      data = IOUtils.toString(fis, StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new CucumberException(e.getMessage());
    }
    return data;
  }

  public static String readDocFile(String filePath) {
    FileInputStream fis;
    String data = "";
    try {
      fis = new FileInputStream(filePath);
      XWPFDocument document = new XWPFDocument(fis);
      List<XWPFParagraph> paragraphs = document.getParagraphs();
      for (XWPFParagraph para : paragraphs) {
        System.out.println(para.getText());
        data += para.getText();
      }
      fis.close();
    } catch (IOException e) {
      throw new CucumberException(e.getMessage());
    }
    return data;
  }

  public static String readPdfFile(String filePath) {
    String pdfContent;
    try {
      PDDocument pdfDocument = PDDocument.load(new File(filePath));
      pdfContent = (new PDFTextStripper()).getText(pdfDocument);
    } catch (IOException e) {
      throw new CucumberException(
          "Fail to read pdf file: " + filePath + " due to: " + e.getMessage());
    }
    LOG.info("Pdf file has content:\n {}", pdfContent);
    return pdfContent;
  }

  public void createTestSetAndTestExecutionJsonFile() {
    createTestSetJsonFile();
    createTestExecutionJsonFile();
  }

  private void createTestExecutionJsonFile() {
    String testExecutionDirectoryPath = "src/test/resources/jiraxray/testexecution/active";
    //Get all available file name in test set (only support .txt)
    FilenameFilter textFiler = (dir, name) -> name.toLowerCase().endsWith(".txt");
    List<File> testExecutionFileList = Arrays.asList(
        Objects.requireNonNull(new File(testExecutionDirectoryPath).listFiles(textFiler)));
    List<String> testExecutionFilePathList = new ArrayList<>();
    testExecutionFileList.forEach(file -> testExecutionFilePathList.add(file.getAbsolutePath()));
        /* Due to test execution need to reset previous result =>Remove all previous test, then add them again.
           In this case, will remove all available test which are stored in src/test/resources/jiraxray/AllTest.txt
         */
        /* Extract test set key and test set id
           Build removed test list
         */
    String testExecutionFileName = "";
    String testExecutionId = "";
    StringBuilder removedTestList = new StringBuilder();
    StringBuilder addedTestList;
    String lineContent;
    String[] testKeyIdArray;
    try {
      Scanner allTestFileScanner = new Scanner(new File("src/test/resources/jiraxray/AllTest.txt"));
      while (allTestFileScanner.hasNextLine()) {
        lineContent = allTestFileScanner.nextLine();
        testKeyIdArray = lineContent.split(" = ");
        removedTestList.append("\"").append(testKeyIdArray[1]).append("\", ");
      }
      allTestFileScanner.close();
    } catch (Exception e) {
      throw new CucumberException(e.getMessage());
    }
    removedTestList.delete(removedTestList.length() - 2, removedTestList.length());
    LOG.info("Remove tests: {} from test execution {}", removedTestList, testExecutionId);
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
      if (addedTestList.length() > 0) {
        addedTestList.delete(addedTestList.length() - 2, addedTestList.length());
      }
      //Write test set json file
      LOG.info("*******************************************************************");
      LOG.info("Creating test execution json file which has Id \"{}\"", testExecutionId);
      LOG.info("Add tests: {} to test execution {}", addedTestList, testExecutionId);
      String queryValue = String.format(
          "mutation{removeTestsFromTestExecution(issueId: %s, testIssueIds: [%s]) addTestsToTestExecution(issueId: %s,testIssueIds: [%s]){addedTests warning}}",
          testExecutionId, removedTestList, testExecutionId, addedTestList);
      Map<String, String> testSet = new HashMap<>();
      testSet.put("query", queryValue);
      ObjectMapper mapper = new ObjectMapper();
      try {
        String fileOutput = String.format("%s/%s.json", testExecutionDirectoryPath,
            testExecutionFileName);
        mapper.writeValue(new File(fileOutput), testSet);
        LOG.info("Json file for {} successfully created!", testExecutionFileName);
      } catch (IOException e) {
        throw new CucumberException(e.getMessage());
      }
    }
  }

  private void createTestSetJsonFile() {
    String testSetDirectoryPath = "src/test/resources/jiraxray/testset/active";
    //Get all available file name in test set (only support .txt)
    FilenameFilter textFiler = (dir, name) -> name.toLowerCase().endsWith(".txt");
    List<File> testSetFileList = Arrays.asList(
        Objects.requireNonNull(new File(testSetDirectoryPath).listFiles(textFiler)));
    List<String> testSetFilePathList = new ArrayList<>();
    testSetFileList.forEach(file -> testSetFilePathList.add(file.getAbsolutePath()));
        /* Extract test set key and test set id
           Build removed test and added test list
        */
    String testSetFileName = "";
    String testSetId = "";
    StringBuilder removedTestList;
    StringBuilder addedTestList;
    for (String testSetFilePath : testSetFilePathList) {
      removedTestList = new StringBuilder();
      addedTestList = new StringBuilder();
      try {
        Scanner documentScanner = new Scanner(new File(testSetFilePath));
        while (documentScanner.hasNextLine()) {
          String lineContent = documentScanner.nextLine();
          String[] testKeyIdArray = lineContent.split(" = ");
          if (lineContent.startsWith("TestSet")) {
            testSetId = String.format("\"%s\"", testKeyIdArray[1]);
            testSetFileName = testKeyIdArray[0];
          } else if (lineContent.startsWith("Remove")) {
            removedTestList.append("\"").append(testKeyIdArray[1]).append("\", ");
          } else if (lineContent.startsWith("Add")) {
            addedTestList.append("\"").append(testKeyIdArray[1]).append("\", ");
          }
        }
        documentScanner.close();
      } catch (FileNotFoundException e) {
        throw new CucumberException(e.getMessage());
      }
      if (removedTestList.length() > 0) {
        removedTestList.delete(removedTestList.length() - 2, removedTestList.length());
      }
      if (addedTestList.length() > 0) {
        addedTestList.delete(addedTestList.length() - 2, addedTestList.length());
      }
      //Write test set json file
      LOG.info("*******************************************************************");
      LOG.info("Creating test set json file which has Id \"{}\"", testSetId);
      LOG.info("Remove tests: {} from test set {}", removedTestList, testSetId);
      LOG.info("Add tests: {} to test set {}", addedTestList, testSetId);
      String queryValue = String.format(
          "mutation{removeTestsFromTestSet(issueId: %s, testIssueIds: [%s]) addTestsToTestSet(issueId: %s,testIssueIds: [%s]){addedTests warning}}",
          testSetId, removedTestList, testSetId, addedTestList);
      Map<String, String> testSet = new HashMap<>();
      testSet.put("query", queryValue);
      writeOutput(testSetDirectoryPath, testSetFileName, testSet);
    }
  }

  public void writeOutput(String testSetDirectoryPath, String testSetFileName,
      Map<String, String> testSet) {
    ObjectMapper mapper = new ObjectMapper();
    try {
      String fileOutputPath = String.format("%s/%s.json", testSetDirectoryPath, testSetFileName);
      mapper.writeValue(new File(fileOutputPath), testSet);
      LOG.info("Json file for {} successfully created!", testSetFileName);
    } catch (IOException e) {
      throw new CucumberException(e.getMessage());
    }
  }

  public static void updateTestResultsSummaryToExcelFile(String testId, int totalPasses,
      int totalFails) {
    String filePath = Paths.get("").toAbsolutePath() + "/src/main/java/templates/testResult.xlsx";
    int totalScenarios = totalPasses + totalFails;
    LOG.info(
        "Total scenarios are '{}'.\nTotal pass scenarios are '{}'.\nTotal fail scenarios are '{}'",
        totalScenarios, totalPasses, totalFails);
    try (
        FileInputStream file = new FileInputStream(filePath);
        Workbook workbook = WorkbookFactory.create(file);
        FileOutputStream outFile = new FileOutputStream(filePath);
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
            LOG.info("Writing row '{}' at column 'Total Passed' with value '{}'", newRowNumber,
                totalPasses);
            break;
          case 3:
            sheet.getRow(newRowNumber).createCell(i).setCellValue(totalFails);
            LOG.info("Writing row '{}' at column 'Total Failed' with value '{}'", newRowNumber,
                totalFails);
            break;
          case 4:
            sheet.getRow(newRowNumber).createCell(i).setCellValue(totalScenarios);
            LOG.info("Writing row '{}' at column 'Total Scenario' with value '{}'", newRowNumber,
                totalFails);
            break;
          case 5: {
            String formula = "C" + (newRowNumber + 1) + "/" + "E" + (newRowNumber + 1);
            cell.setCellFormula(formula);
            cellStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00%"));
            cell.setCellStyle(cellStyle);
            evaluator.evaluateFormulaCell(cell);
            LOG.info("Writing row '{}' at column 'Passed Ratio' with value '{}'", newRowNumber + 1,
                cell.getNumericCellValue() * 100 + "%");
            break;
          }
          case 6: {
            String formula = "D" + (newRowNumber + 1) + "/" + "E" + (newRowNumber + 1);
            cell.setCellFormula(formula);
            cellStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00%"));
            cell.setCellStyle(cellStyle);
            evaluator.evaluateFormulaCell(cell);
            LOG.info("Writing row '{}' at column 'Failed Ratio' with value '{}'", newRowNumber + 1,
                cell.getNumericCellValue() * 100 + "%");
            break;
          }
          default:
            sheet.getRow(newRowNumber).createCell(i).setCellValue(testStartDateTime.toString());
            LOG.info("Writing row '{}' at column 'Date' with value '{}'", newRowNumber,
                testStartDateTime);
            break;
        }
      }
      workbook.write(outFile);
      LOG.info("Test Results for '{}' are updated successfully into file '{}'", testId, filePath);
    } catch (IOException e) {
      LOG.info("Failed to update Test Results to file {} due to:\n{}", filePath, e.getMessage());
    }
  }

  public void updateCucumberJsonFile() {
    String testResultCucumberDirectoryPath = Paths.get("").toAbsolutePath().toString();
    LOG.info("Current working directory is: {}", testResultCucumberDirectoryPath);
    //Get all available file name in test set (only support .txt)
    FilenameFilter textFiler = (dir, name) -> name.toLowerCase().matches("^cucumber.*\\.json$");
    File[] testExecutionFileList = Objects.requireNonNull(
        new File(testResultCucumberDirectoryPath).listFiles(textFiler));
    for (File file : testExecutionFileList) {
      LOG.info("Found cucumber json file: {}", file.getName());
      ObjectMapper objectMapper = new ObjectMapper();
      try {
        JsonNode jsonNode = objectMapper.readTree(file);
        JsonNode tagsNode = jsonNode.get(0).get("tags");
        // Update the JSON data
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
}
