# Declare variables
project_dir=$1
features_dir=$project_dir"/src/test/java/features/regression"
cloud_auth_file_dir=$2
echo "Project directory:" $project_dir
echo "Features directory:" $features_dir
echo "Cloud authentication file directory:" $cloud_auth_file_dir

# Generate cloud token
echo "Generate cloud token..."
xrayToken=$(curl -H "Content-Type: application/json" -X POST --data "@$cloud_auth_file_dir" https://xray.cloud.getxray.app/api/v2/authenticate)
token=$(echo $xrayToken  | awk -F"\"" '{print$2}')
echo "Cloud token: "$token

# Generate test set and test execution json files
cd $project_dir || exit
echo "Generate test set and test execution json files..."
mvn clean verify -Dcucumber.filter.tags=@GenerateTestSetAndTestExecutionJsonFile -Dbrowser=chromeHeadless -DexecutingEnv=local -DtestedEnv=uat -Dplatform=desktop
# Import Cucumber test using xray api...
echo "Import Cucumber test using xray api..."
cd $features_dir || exit
all_directories=$(find -mindepth 1 -maxdepth 1 -type d)
for directoryPath in $all_directories
do
	echo "Zip feature files..."
	directory=$(echo $directoryPath  | awk -F"/" '{print$2}')
	zip -r $directory.zip $directory
	echo "Importing..."
	curl -H "Content-Type: multipart/form-data" \
	-X POST -H "Authorization: Bearer $token" \
	-F "file=@$directory.zip" \
	https://xray.cloud.getxray.app/api/v2/import/feature?projectKey=TEST
done

# Remove & Add Test To TestSets
echo "Remove & Add Test To TestSets..."
search_dir_testset=$project_dir"/src/test/resources/jiraxray/testset/active"
cd $search_dir_testset || exit
testSets=$(find . -name "*.json")
for testSet in $testSets
do
	testSetFile=$(echo $testSet  | awk -F"/" '{print$2}')
	echo $testSetFile
	curl -g \
	-X POST \
	-H "Content-Type: application/json" \
	-H "Authorization: Bearer $token" \
	-d "@$testSetFile" \
	https://xray.cloud.getxray.app/api/v2/graphql
done

# Remove & Add Test To Test Execution
echo "Remove & Add Test To Test Execution..."
search_dir_testexecution=$project_dir"/src/test/resources/jiraxray/testexecution/active"
cd $search_dir_testexecution || exit
testExecutions=$(find . -name "*.json")
for testExecution in $testExecutions
do
	testExecutionFile=$(echo $testExecution  | awk -F"/" '{print$2}')
	echo $testExecutionFile
	curl -g \
	-X POST \
	-H "Content-Type: application/json" \
	-H "Authorization: Bearer $token" \
	-d "@$testExecutionFile" \
	https://xray.cloud.getxray.app/api/v2/graphql
done
