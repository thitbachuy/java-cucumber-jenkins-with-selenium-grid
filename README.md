# New CRM E2E Test Automation Framework

This project is for testing the E2E Flow of New CRM and its interfacing components - Sales Force, Marketting Cloud, Social Studio. It also includes automated tests for the mobile interface - both Andriod and IOS!

## Prerequisites and Setup

* JAVA 11
* MAVEN 3.6.3
* Your favourite IDE :) 

* Clone the repo and Import as Maven project. 

* Request for keyset.json file from one of the developers and place it in the following directory : 
  /Users/{your_user}/sky-TA-keyset.json

## Running Tests
mvn clean verify -Dcucumber.filter.tags=@testTag -Dbrowser=chrome(or firefox) -DexecutingEnv=test -Dplatform=desktop(or macbook) -DtestedEnv=sit(or uat)

## Project Structure

| Files and folders  | Description |
| ------------- | ------------- |
| src/test/config | Contains driver, base page & database configuration. Also test data helper methods to store and reuse test data during runtime. |
| src/test/features | All the test scenarios are defined as cucumber features. Sorted by PI or functionality. |
| src/test/locators | Contains the xpath locators of objects on the pages. The structure is OS - Application - Pages. Locators are stored as a hashmap.|
| src/test/pages | Contains page classes with actions (methods) that are performed on each page. |
| src/test/steps | Contains step definition classes - wiring classes between the feature file and page class. |
| src/test/testdata | Contains test data which is stored in a json. Passwords are encrypted. (Request for password encrypter from developers) |


**Note**: **Set the device name same as account name used for testing.**
Imagine that the account name for test is *e2e.test.username* => then the device name must be *e2e.test.username*

## Mobile
**1.Setup:**

* JAVA 11
* Android SDK
* Node
* Appium

**2. Pre-requisites:**
Appium server is running

`appium -a [host] -p [port] --allow-insecure=chromedriver_autodownload`

**3. Running Tests:**

- In case testing native app in mobile

mvn clean verify -Dcucumber.filter.tags=@testAge -Dbrowser=chrome -DexecutingEnv=test -DtestedEnv=uat -Dplatform=android-nativeApp

mvn clean verify -Dcucumber.filter.tags=@testAge -Dbrowser=chrome -DexecutingEnv=test -DtestedEnv=uat -Dplatform=ios-nativeApp

- In case testing web app in mobile

mvn clean verify -Dcucumber.filter.tags=@testAge -Dbrowser=chrome -DexecutingEnv=test -DtestedEnv=uat -Dplatform=android-webApp

mvn clean verify -Dcucumber.filter.tags=@testAge -Dbrowser=safari -DexecutingEnv=test -DtestedEnv=uat -Dplatform=ios-webApp

## Code Analytics
### Using Sonarqube tool

- Install: https://docs.sonarqube.org/latest/setup/overview/
- Scanner for Maven: https://docs.sonarqube.org/latest/analysis/scan/sonarscanner-for-maven/

    - mvn clean install
    - mvn sonar:sonar -Dsonar.login=authenticationToken -Dsonar.java.binaries=src/main/java
  
- Note: Add  *nonProxyHost* with value *localhost* in m2/settings.xml

### How to display code coverage in SonarQube using Jacoco

#### 1. Merge all individual jacoco.exec file into one file

- java -jar jacoco_cli.jar merge [<exec_file_1> <exec_file_2> ...] --destfile <path\to\output\file>

  Example: java -jar org.jacoco.cli.jar merge C:\Users\tqbao\Desktop\jacoo0\jacoco.exec C:
  \Users\tqbao\Desktop\jacoco1\jacoco.exec --destfile C:\Users\tqbao\Desktop\jacoco

#### 2. Generate jacoco report in xml format

- java -jar jacoco_cli.jar report [<execfiles> ...] --classfiles <path\to\classfiles.jar>
  [--csv <file>] [--encoding <charset>] [--help] [--html <dir>] [--name <name>] [--sourcefiles <path>] [--tabwith <n>] [--xml <file>]

  Example(html): java -jar org.jacoco.cli-0.8.9-20220405.092301-1-nodeps.jar report C:
  \Users\tqbao\Desktop\jacoco\jacoco.exec --classfiles E:\ProgrammingStudy\MavenTest\target\MavenTest-1.0-SNAPSHOT.jar
  --html C:\Users\tqbao\Desktop\jacoco\index.html

  Example(xml): java -jar org.jacoco.cli-0.8.9-20220405.092301-1-nodeps.jar report C:
  \Users\tqbao\Desktop\jacoco\jacoco.exec --classfiles E:\ProgrammingStudy\MavenTest\target\MavenTest-1.0-SNAPSHOT.jar
  --xml C:\Users\tqbao\Desktop\jacoco\jacoco.xml

#### 3. Import jacoco xml report to SonarQube

Using Maven -D switch:

-Dsonar.coverage.jacoco.xmlReportPaths=C:\Users\tqbao\Desktop\jacoco\jacoco.xml

Example: mvn sonar:sonar -Dsonar.login=<loginToken> -Dsonar.coverage.jacoco.xmlReportPaths=C:\Users\tqbao\Desktop\jacoco\jacoco.xml

### References
https://www.jacoco.org/jacoco/trunk/doc/cli.html

https://docs.sonarqube.org/latest/analysis/test-coverage/java-test-coverage/

## Jira Cucumber Test Operation Automation

#### Generate test set and test execution json file dynamically

Run command: 

mvn -Dtest=FilesUtilsTest#testCreateTestSetAndTestExecutionJsonFile clean test

=>*Test Set json file will be located in: src/test/resources/jiraxray/testset/active*

=>*Test Execution json file will be located in: src/test/resources/jiraxray/testexecution/active*

#### Using xray api to import Cucumber test from feature files

- Feature file scenario name must be equal Cucumber test summary

- Zip all feature folders

- Run command "curl" to update Cucumber test

curl -H "Content-Type: multipart/form-data" -X POST -H "Authorization: Bearer $token"  -F "file=@features.zip" https://xray.cloud.getxray.app/api/v2/import/feature?projectKey=TEST

#### Using xray GraphQL api to update TestSets

- Prepare .json file for each Test Set

- Run command "curl" to execute

  - Remove tests from test set

  - Add tests to test set

  curl -g 
  -X POST 
  -H "Content-Type: application/json" 
  -H "Authorization: Bearer $Token" 
  -d '@testset.json' 
  https://xray.cloud.getxray.app/api/v2/graphql

#### Using xray GraphQL api to update Test Execution

- Prepare .json file for each Test Execution

- Run command "curl" to execute

  - Remove tests from test execution
  - Add tests to test execution

  curl -g
  -X POST
  -H "Content-Type: application/json"
  -H "Authorization: Bearer $Token"
  -d '@testexecution.json'
  https://xray.cloud.getxray.app/api/v2/graphql

###  Operation in local

- Install zip command
- Run cmd (git bash if window os)

  updateTestSetAndTestExecution.sh <path/to/project/directory> <path/to/cloudAuthen.json>