package modal;

import config.TestDataLoader;
import io.cucumber.core.exception.CucumberException;

public class TableInformation {

  private String listColumnLocator;
  private String listRowLocator;
  private String cellValueLocator;
  private String uniqueColumn;
  private String cellValueCheckboxLocator;
  private String checkboxTagAttribute;
  private String checkboxAttributeActiveValue;
  private String checkboxAttributeInactiveValue;
  private boolean isTableLocatedInsideElement = false;

  public void setTableLocatedInsideElement(boolean tableLocatedInsideElement) {
    isTableLocatedInsideElement = tableLocatedInsideElement;
  }

  public void setListColumnLocator(String listColumnLocator) {
    this.listColumnLocator = listColumnLocator;
  }

  public void setListRowLocator(String listRowLocator) {
    this.listRowLocator = listRowLocator;
  }

  public void setCellValueLocator(String cellValueLocator) {
    this.cellValueLocator = cellValueLocator;
  }

  public void setUniqueColumn(String uniqueColumn) {
    this.uniqueColumn = uniqueColumn;
  }

  public void setCellValueCheckboxLocator(String cellValueCheckboxLocator) {
    this.cellValueCheckboxLocator = cellValueCheckboxLocator;
  }

  public void setCheckboxTagAttribute(String checkboxTagAttribute) {
    this.checkboxTagAttribute = checkboxTagAttribute;
  }

  public void setCheckboxAttributeActiveValue(String checkboxAttributeActiveValue) {
    this.checkboxAttributeActiveValue = checkboxAttributeActiveValue;
  }

  public void setCheckboxAttributeInactiveValue(String checkboxAttributeInactiveValue) {
    this.checkboxAttributeInactiveValue = checkboxAttributeInactiveValue;
  }

  public void defineTable(String context, String tableName) {
    String commonSalesforceTableLocator = "//*[contains(text(),'" + tableName + "')]";
    isTableLocatedInsideElement = false;
    //define error message
    TestDataLoader.setTestData("notSupportedTableErrorMessage",
        String.format("Table [%s] in context [%s] is not in support list", tableName, context));
    switch (context) {
      case "salesforce account detail page":
        tableDefinitionSFAccountDetailsPage(tableName);
        break;
      case "salesforce contact detail page":
        tableDefinitionSFContactDetailsPage(tableName);
        break;
      case "salesforce manage data privacy page":
        listColumnLocator =
            "//table[@aria-label='" + tableName + "']//thead//th//span[@title and text()]";
        listRowLocator = "//table[@aria-label='" + tableName + "']//tbody/tr//th//a//span";
        cellValueLocator = "(//table[@aria-label='" + tableName
            + "']//tbody//tr[%d]//*[self::lightning-formatted-date-time or self::a or self::lst-formatted-text])[%d]";
        uniqueColumn = "Name";
        break;
      case "salesforce reports page":
        tableDefinitionSFReportPage(tableName);
        break;
      case "salesforce quick text page":
        tableDefinitionSFQuickTextPage(tableName);
        break;
      case "salesforce hardware return detail page":
        if (tableName.equals("HW Returns")) {
          listColumnLocator = "(//*[contains(text(),'HW Returns')]//ancestor::article//table)[last()]//thead//a/span[2]";
          listRowLocator = "(//*[contains(text(),'HW Returns')]//ancestor::article//table)[last()]/tbody//tr//th//a//span";
          cellValueLocator = "(//*[contains(text(),'HW Returns')]//ancestor::article//table)[last()]//tbody//tr[%s]/*[self::td or self::th][@aria-readonly='true'][%s]//*[self::lst-formatted-text or self::span[@force-lookup_lookup] or self::lightning-formatted-date-time]";
          uniqueColumn = "HW Return Number";
        } else if (tableName.equals("HW Return Items")) {
          listColumnLocator = "(//*[contains(text(),'HW Return Items')]//ancestor::article//table)[last()]//thead//th//span[@class='slds-truncate' and not(@title='Action')]";
          listRowLocator = "(//*[contains(text(),'HW Return Items')]//ancestor::article//table)[last()]/tbody//tr//th//a//span";
          cellValueLocator = "(//*[contains(text(),'HW Return Items')]//ancestor::article//table)[last()]//tbody//tr[%s]/*[self::td or self::th][@aria-readonly='true'][%s]//*[self::lst-formatted-text or self::a[@force-lookup_lookup] or self::span[@force-lookup_lookup] or self::lightning-formatted-date-time]";
          uniqueColumn = "HW Return Item Number";
        } else {
          throw new CucumberException(TestDataLoader.getTestData("notSupportedTableErrorMessage"));
        }
        break;
      case "salesforce permission center page":
        if (tableName.equals("Deactivate Communication Subscription Consents")) {
          listColumnLocator = commonSalesforceTableLocator + "//ancestor::article//table/thead//th";
          listRowLocator =
              commonSalesforceTableLocator + "//ancestor::article//table//tbody//tr//td[1]";
          cellValueLocator = commonSalesforceTableLocator
              + "//ancestor::article//table//tbody//tr[%d]/*[self::td or self::th][%d]";
          uniqueColumn = "Name";
          checkboxTagAttribute = "";
          checkboxAttributeActiveValue = "";
          checkboxAttributeInactiveValue = "";
        } else {
          throw new CucumberException(TestDataLoader.getTestData("notSupportedTableErrorMessage"));
        }
        break;
      case "salesforce order overview page":
        if (tableName.equals("Orders")) {
          listColumnLocator = "(//*[contains(text(),'Orders')]//ancestor::article//table)[last()]//thead//a/span[2]";
          listRowLocator = "(//*[contains(text(),'Orders')]//ancestor::article//table)[last()]/tbody//tr//th//a//span";
          cellValueLocator = "(//*[contains(text(),'Orders')]//ancestor::article//table)[last()]//tbody//tr[%s]/*[self::td or self::th][@aria-readonly='true'][%s]//*[self::lst-formatted-text or self::span[@force-lookup_lookup] or self::lightning-formatted-date-time]";
          uniqueColumn = "Order Number";
          break;
        } else {
          throw new CucumberException(TestDataLoader.getTestData("notSupportedTableErrorMessage"));
        }
      case "salesforce payback entities page":
        if (tableName.equals("Payback Entities")) {
          listColumnLocator = "//*[contains(@aria-label,'PAYBACKEntities') and @data-aura-class='forceListViewManager']//table/thead//th[position()>2]//*[self::span[@class='slds-truncate']]";
          listRowLocator = "//*[contains(@aria-label,'PAYBACKEntities') and @data-aura-class='forceListViewManager']//table//tbody//tr//th";
          cellValueLocator = "//*[contains(@aria-label,'PAYBACKEntities') and @data-aura-class='forceListViewManager']//table//tbody//tr[%s]/*[self::td or self::th][@data-aura-class='forceInlineEditCell'][%s]";
          uniqueColumn = "PAYBACKEntity Name";
          break;
        } else {
          throw new CucumberException("Context " + context + " is not in support list");
        }
      case "salesforce incident details page":
        tableDefinitionSFIncidentDetailsPage(tableName);
        break;
      case "partner portal page":
        tableDefinitionPartnerPortalPage(tableName);
        break;
      default:
        throw new CucumberException("Context has not been defined yet. Pls add it!");
    }
    setListColumnLocator(listColumnLocator);
    setListRowLocator(listRowLocator);
    setCellValueLocator(cellValueLocator);
    setUniqueColumn(uniqueColumn);
    setCellValueCheckboxLocator(cellValueCheckboxLocator);
    setCheckboxTagAttribute(checkboxTagAttribute);
    setCheckboxAttributeActiveValue(checkboxAttributeActiveValue);
    setCheckboxAttributeInactiveValue(checkboxAttributeInactiveValue);
    setTableLocatedInsideElement(isTableLocatedInsideElement);
    TestDataLoader.setTestData("listColumnLocator", listColumnLocator);
    TestDataLoader.setTestData("listRowLocator", listRowLocator);
    TestDataLoader.setTestData("cellValueLocator", cellValueLocator);
    TestDataLoader.setTestData("uniqueColumn", uniqueColumn);
    TestDataLoader.setTestData("uniqueColumn", uniqueColumn);
    TestDataLoader.setTestData("cellValueCheckboxLocator", cellValueCheckboxLocator);
    TestDataLoader.setTestData("checkboxTagAttribute", checkboxTagAttribute);
    TestDataLoader.setTestData("checkboxAttributeActiveValue", checkboxAttributeActiveValue);
    TestDataLoader.setTestData("checkboxAttributeInactiveValue", checkboxAttributeInactiveValue);
    TestDataLoader.setTestData("isTableLocatedInsideElement",
        String.valueOf(isTableLocatedInsideElement));
  }

  private void tableDefinitionPartnerPortalPage(String tableName) {
    if (tableName.equals("Kundendaten")) {
      listColumnLocator = "//*[contains(text(),'Kundendaten')]//ancestor::article//table/thead//th//span/span[@title]";
      listRowLocator = "//*[contains(text(),'Kundendaten')]//ancestor::article//table//tbody//tr//th";
      cellValueLocator = "(//*[contains(text(),'Kundendaten')]//ancestor::article//table//tbody//tr[%s]//span)[%s]";
      uniqueColumn = "Kundennummer";
    } else {
      throw new CucumberException(TestDataLoader.getTestData("notSupportedTableErrorMessage"));
    }
  }

  private void tableDefinitionSFQuickTextPage(String tableName) {
    String commonSalesforceQuickTextTableLocator =
        "//div[contains(@class,'PageHost')][.//span[@title='" + tableName + "']]";
    switch (tableName) {
      case "All Quick Text":
        listColumnLocator = commonSalesforceQuickTextTableLocator
            + "//table[contains(@class,'table_header-fixed')]//th[position()>0]//span[@class='slds-truncate' and text()]";
        listRowLocator = commonSalesforceQuickTextTableLocator
            + "//table//tbody//tr//th//*[self::lightning-base-formatted-text[text()] or self::lightning-formatted-url]";
        cellValueLocator = commonSalesforceQuickTextTableLocator
            + "//table//tbody//tr[@class='slds-hint-parent'][%d]//*[self::td or self::th][%d]//*[self::span[contains(@class,'lds-grid_align-spread')]]";
        uniqueColumn = "Quick Text Name";
        break;
      case "All Folders":
        listColumnLocator = commonSalesforceQuickTextTableLocator
            + "//table[contains(@class,'table_header-fixed')]//th[position()>0]//span[@class='slds-truncate' and text()]";
        listRowLocator = commonSalesforceQuickTextTableLocator
            + "//table//tbody//tr//th//*[self::lightning-base-formatted-text[text()] or self::lightning-formatted-url]";
        cellValueLocator = commonSalesforceQuickTextTableLocator
            + "//table//tbody//tr[@class='slds-hint-parent'][%d]//*[self::td or self::th][%d]//*[self::lightning-formatted-url or self::lightning-formatted-date-time]";
        uniqueColumn = "Name";
        break;
      default:
        throw new CucumberException(TestDataLoader.getTestData("notSupportedTableErrorMessage"));
    }
  }

  private void tableDefinitionSFReportPage(String tableName) {
    switch (tableName) {
      case "PB_Sessiondetails Bot ab 03.2022":
      case "PB_Sessiondetails Agent ab 03.2022":
      case "Survey last 7 days Agent ab 03.2022":
        listColumnLocator = "//table[contains(@class,'data-grid-full-table')]//tbody//span[contains(@class,'lightning-table-cell-measure-header-value')]";
        listRowLocator = "//table[@class='data-grid-table data-grid-full-table']//tr[position()>1]//td[1]";
        cellValueLocator = "//table[@class='data-grid-table data-grid-full-table']//tr[position()>1][%s]//*[self::th or self::td][%s]";
        uniqueColumn = "Messaging Session Name";
        break;
      case "Survey last 7 days Bot ab 03.2022":
        listColumnLocator = "//table[contains(@class,'data-grid-full-table')]//tbody//span[contains(@class,'lightning-table-cell-measure-header-value')]";
        listRowLocator = "//table[@class='data-grid-table data-grid-full-table']//tr[position()>2]//td[1]";
        cellValueLocator = "//table[@class='data-grid-table data-grid-full-table']//tr[position()>2][%s]//*[self::th or self::td][%s]";
        uniqueColumn = "Messaging Session Name";
        break;
      case "Webchat Transfer Queue Information":
        listColumnLocator = "//div[@class='data-grid-table-ctr' and not(@aria-hidden='true')]//table//th[contains(@class,'lightning-table-action-cell')]//span[contains(@class,'lightning-table-cell-measure-header-value')]";
        listRowLocator = "//table[@class='data-grid-table data-grid-full-table']//tr[position()>1]//td[1]";
        cellValueLocator = "(//table[@class='data-grid-table data-grid-full-table']//tr[position()>1])[%s]//*[self::td][%s]//*[self::a or self::span[@class]]";
        uniqueColumn = "Chat Transcript Name";
        break;
      case "Forecast Document Report":
      case "Forecast Telephony Report":
        listColumnLocator = "//table[@class='data-grid-table data-grid-full-table']//th[position()>1]//span[contains(@class,'wave-table-cell-measure-header-text')]/span";
        listRowLocator = "//table[@class='data-grid-table data-grid-full-table']//tr[position()>1]/td[4]";
        cellValueLocator = "//table[@class='data-grid-table data-grid-full-table']//tr[position()>1][%s]//*[self::td][%s]";
        uniqueColumn = "Case Number";
        break;
      case "Report on LiveChatTranscript":
        listColumnLocator = "//table[contains(@class,'data-grid-full-table')]//tbody//span[contains(@class,'lightning-table-cell-measure-header-value')]";
        listRowLocator = "//table[@class='data-grid-table data-grid-full-table']//tr[position()>1]//td[1]";
        cellValueLocator = "//table[@class='data-grid-table data-grid-full-table']//tr[position()>1][%s]//*[self::th or self::td][%s]";
        uniqueColumn = "Chat Transcript Name";
        break;
      case "Report: Incidents":
        listColumnLocator = "//table//th[contains(@class,'lightning-table-action-cell')]//span[contains(@class,'lightning-table-cell-measure-header-value')]";
        listRowLocator = "//table[@class='data-grid-table data-grid-full-table']//tr[position()>1]";
        cellValueLocator = "((//table[@class='data-grid-table data-grid-full-table']//tr[position()>1])[%d]//*[self::th or self::td])[%d]/div/div";
        uniqueColumn = "Incident Number";
        break;
      default:
        throw new CucumberException(TestDataLoader.getTestData("notSupportedTableErrorMessage"));
    }
  }

  private void tableDefinitionSFContactDetailsPage(String tableName) {
    String commonSalesforceTableLocator = "//*[contains(text(),'" + tableName + "')]";
    switch (tableName) {
      case "Payment Methods for Parent Account":
        listColumnLocator =
            commonSalesforceTableLocator + "//ancestor::article//table//thead//tr//th//span/span";
        listRowLocator =
            commonSalesforceTableLocator + "//ancestor::article//table//tbody//tr/th//a//span";
        cellValueLocator = commonSalesforceTableLocator
            + "//ancestor::article//table//tbody//tr[%d]/*[self::td or self::th][%d]//*[self::lst-formatted-text or self::a or self::input]";
        cellValueCheckboxLocator = commonSalesforceTableLocator
            + "//ancestor::article//table//tbody//tr[1]/*[self::td or self::th][2]//input";
        uniqueColumn = "Payment Method Name";
        checkboxTagAttribute = "";
        checkboxAttributeActiveValue = "";
        checkboxAttributeInactiveValue = "";
        break;
      case "Marketing Permission Info":
      case "Product Settings & Analysis":
        listColumnLocator = commonSalesforceTableLocator
            + "//ancestor::article//table/thead//th//div//span[@class='slds-th__action']/span";
        listRowLocator = commonSalesforceTableLocator
            + "//ancestor::article//table//tbody//tr/*[self::th or self::td]//c-custom-navigation";
        cellValueLocator = commonSalesforceTableLocator
            + "//ancestor::article//table//tbody//tr[%d]/*[self::td or self::th][%d]//*[self::lightning-base-formatted-text or self::span/ancestor::a or self::a/ancestor::c-custom-navigation or self::lst-formatted-text]";
        uniqueColumn = "Name";
        break;
      case "Communication Subscription Consents for Parent Manage Data Privacy":
      case "Contact Point Type Consents for Parent Manage Data Privacy":
        listColumnLocator = commonSalesforceTableLocator
            + "//ancestor::article//table/thead//th//div//span[@class='slds-th__action']/span";
        listRowLocator = commonSalesforceTableLocator
            + "//ancestor::article//table//tbody//tr/*[self::th or self::td]//a//span";
        cellValueLocator = commonSalesforceTableLocator
            + "//ancestor::article//table//tbody//tr[%d]/*[self::td or self::th][%d]//*[self::lst-formatted-text or self::lightning-formatted-date-time or self::span/ancestor::a]";
        uniqueColumn = "Name";
        break;
      case "Cases":
        listColumnLocator =
            commonSalesforceTableLocator + "/ancestor::article//table/thead//th//div//span/span | "
                + commonSalesforceTableLocator + "/ancestor::article//table/thead//th//a/span[2]";
        listRowLocator = commonSalesforceTableLocator
            + "//ancestor::article//table//tbody//tr/*[self::th or self::td]//a//span";
        cellValueLocator = commonSalesforceTableLocator
            + "//ancestor::article//table//tbody//tr[%d]/*[self::td or self::th][%d]//*[self::span/ancestor::a or self::lst-formatted-text or self::slot[not(./*)]]";
        uniqueColumn = "Case";
        break;
      case "Orders":
        listColumnLocator =
            commonSalesforceTableLocator + "/ancestor::article//table/thead//th//a/span[2]";
        listRowLocator = commonSalesforceTableLocator
            + "//ancestor::article//table//tbody//tr/*[self::th or self::td]//a//span";
        cellValueLocator = commonSalesforceTableLocator
            + "//ancestor::article//table//tbody//tr[%s]/*[self::td or self::th][position()>1][%s]//*[self::a//span or self::div/span or self::lst-formatted-text or self::lightning-formatted-date-time]";
        uniqueColumn = "Order Number";
        break;
      case "Active Packages & Add Ons":
      case "Active Hardware":
        listColumnLocator = commonSalesforceTableLocator
            + "/ancestor::article//table//thead//th/lightning-primitive-header-factory/div/span/span";
        listRowLocator = commonSalesforceTableLocator
            + "/ancestor::article//table//tbody//tr/th//lightning-base-formatted-text";
        cellValueLocator = commonSalesforceTableLocator
            + "//ancestor::article//table//tbody//tr[%d]/*[self::td or self::th][%d]//lightning-base-formatted-text";
        uniqueColumn = "Product Name";
        break;
      case "Orders for Parent Account":
        listColumnLocator =
            commonSalesforceTableLocator + "//ancestor::article//table/thead//th//span[@title]";
        listRowLocator =
            commonSalesforceTableLocator + "/ancestor::article//table//tbody//tr/th//a//span";
        cellValueLocator = commonSalesforceTableLocator
            + "//ancestor::article//table//tbody//tr[%d]/*[self::td or self::th][%d]//*[self::a or self::span/slot[text()] or self::lst-formatted-text or self::lightning-formatted-date-time]";
        uniqueColumn = "Order Number";
        break;
      case "skypin Lock Status":
      case "mpp Lock Status":
        listColumnLocator = commonSalesforceTableLocator + "//ancestor::article//table//thead//th";
        listRowLocator = commonSalesforceTableLocator + "//ancestor::article//table//tbody//tr";
        cellValueLocator = commonSalesforceTableLocator
            + "//ancestor::article//table//tbody//tr[%d]/*[self::td or self::th][%d]";
        uniqueColumn = "Pin Type";
        break;
      case "Customer Login Locked Status":
        listColumnLocator = commonSalesforceTableLocator + "//ancestor::article//table//thead//th";
        listRowLocator = commonSalesforceTableLocator + "//ancestor::article//table//tbody//tr";
        cellValueLocator = commonSalesforceTableLocator
            + "//ancestor::article//table//tbody//tr[%d]/*[self::td or self::th][%d]";
        uniqueColumn = "Is Password Set?";
        break;
      case "Dunning actions":
        listColumnLocator = commonSalesforceTableLocator
            + "//ancestor::c-current-dunning-information//table/thead//th//span/span[@title]";
        listRowLocator = commonSalesforceTableLocator
            + "//ancestor::c-current-dunning-information//table//tbody//tr//td";
        cellValueLocator = commonSalesforceTableLocator
            + "//ancestor::c-current-dunning-information//table//tbody//tr[%s]//*[local-name()='td' or local-name()='th'][%s]";
        uniqueColumn = "Dunning action";
        break;
      default:
        throw new CucumberException(TestDataLoader.getTestData("notSupportedTableErrorMessage"));
    }
  }

  public void tableDefinitionSFAccountDetailsPage(String tableName) {
    String commonSalesforceTableLocator = "//*[contains(text(),'" + tableName + "')]";
    switch (tableName) {
      case "Transactional Email Archives":
        listColumnLocator =
            commonSalesforceTableLocator + "//ancestor::article//table/thead//th//span/span";
        listRowLocator = commonSalesforceTableLocator
            + "//ancestor::article//table//tbody//tr//th//*[self::span/ancestor::a]";
        cellValueLocator = commonSalesforceTableLocator
            + "//ancestor::article//table//tbody//tr[%d]/*[self::td or self::th][%d]//*[self::lst-formatted-text or self::a//span or self::lightning-formatted-url/a]";
        uniqueColumn = "Transactional Email Archive Name";
        break;
      case "Orders":
        listColumnLocator =
            commonSalesforceTableLocator + "//ancestor::article//table/thead//th//span/span";
        listRowLocator =
            commonSalesforceTableLocator + "//ancestor::article//table//tbody//tr/th//a//span";
        cellValueLocator = commonSalesforceTableLocator
            + "//ancestor::article//table//tbody//tr[%d]/*[self::td or self::th][%d]//*[self::span or self::a]";
        uniqueColumn = "Order Number";
        break;
      case "Contracts":
        listColumnLocator = commonSalesforceTableLocator + "//ancestor::article//table/thead//th";
        listRowLocator =
            commonSalesforceTableLocator + "//ancestor::article//table//tbody//tr/th//a//span";
        cellValueLocator = commonSalesforceTableLocator
            + "//ancestor::article//table//tbody//tr[%d]/*[self::td or self::th][%d]//*[self::span or self::a]";
        uniqueColumn = "Contract Number";
        break;
      case "Contacts":
        listColumnLocator =
            commonSalesforceTableLocator + "//ancestor::article//table/thead//th//span/span";
        listRowLocator =
            commonSalesforceTableLocator + "//ancestor::article//table//tbody//tr/th//a//span";
        cellValueLocator = commonSalesforceTableLocator
            + "//ancestor::article//table//tbody//tr[%d]/*[self::td or self::th][%d]//*[self::lst-formatted-text or self::a]";
        uniqueColumn = "Contact Name";
        break;
      case "Assets":
        listColumnLocator =
            commonSalesforceTableLocator + "//ancestor::article//table/thead//th//span/span";
        listRowLocator =
            commonSalesforceTableLocator + "//ancestor::article//table//tbody//tr/th//a//span";
        cellValueLocator = commonSalesforceTableLocator
            + "//ancestor::article//table//tbody//tr[%d]/*[self::th or self::td][%d]//*[self::lightning-formatted-number or self::lightning-formatted-date-time or self::a//slot or self::a//span[not(./*)]]";
        uniqueColumn = "Asset Name";
        break;
      case "Shipping Orders":
        listColumnLocator =
            commonSalesforceTableLocator + "//ancestor::article//table/thead//th[1]//span/span";
        listRowLocator =
            commonSalesforceTableLocator + "//ancestor::article//table//thead//tr//th//span/span";
        cellValueLocator = commonSalesforceTableLocator
            + "//ancestor::article//table//tbody//tr[%d]/*[self::td or self::th][%d]//a//span";
        uniqueColumn = "Shipping Order Number";
        break;
      case "Archived Documents in Saperion":
        listColumnLocator = commonSalesforceTableLocator
            + "//ancestor::flexipage-component2[1]//table//thead//div[contains(@class,'slds-cell-fixed')]/span/span";
        listRowLocator = commonSalesforceTableLocator
            + "//ancestor::flexipage-component2[1]//table//tbody//tr//td[2]//lightning-base-formatted-text";
        cellValueLocator = commonSalesforceTableLocator
            + "//ancestor::flexipage-component2[1]//table//tbody//tr[%s]/*[self::td or self::th][%s]//div";
        uniqueColumn = "Document Id";
        break;
      case "Payment Methods":
        listColumnLocator =
            commonSalesforceTableLocator + "//ancestor::article//table//thead//tr//th//span/span";
        listRowLocator = commonSalesforceTableLocator
            + "//ancestor::article//table//tbody//tr//th//*[self::span/ancestor::a]";
        cellValueLocator = commonSalesforceTableLocator
            + "//ancestor::article//table//tbody//tr[%d]/*[self::td or self::th][%d]//*[self::lst-formatted-text or self::a or self::input]";
        uniqueColumn = "Payment Method Name";
        checkboxTagAttribute = "";
        checkboxAttributeActiveValue = "";
        checkboxAttributeInactiveValue = "";
        break;
      default:
        throw new CucumberException(TestDataLoader.getTestData("notSupportedTableErrorMessage"));
    }
  }

  private void tableDefinitionSFIncidentDetailsPage(String tableName) {
    switch (tableName) {
      case "Case Related Issues":
        listColumnLocator = "//article[.//text()='Case Related Issues']//thead//th//span[@title and text()]";
        listRowLocator = "//article[.//text()='Case Related Issues']//table//tbody//tr//th";
        cellValueLocator = "(//article[.//text()='Case Related Issues']//tbody//tr[%d]//span//*[self::a or self::lst-formatted-text])[%d]";
        uniqueColumn = "Case";
        break;
      case "Articles":
        listColumnLocator = "//article[.//text()='Articles']//thead//th//span[@title and text()]";
        listRowLocator = "//article[.//text()='Articles']//table//tbody//tr//th";
        cellValueLocator = "(//article[.//text()='Articles']//tbody//tr[%d]//span//*[self::a or self::lst-formatted-text])[%d]";
        uniqueColumn = "Article Title";
        break;
      default:
        throw new CucumberException(TestDataLoader.getTestData("notSupportedTableErrorMessage"));
    }
  }
}
