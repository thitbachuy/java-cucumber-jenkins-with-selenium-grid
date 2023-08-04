# Import libraries
apt install zip
# Declare variables
project_home=$1
# Replace spaces appear in directory name with wildcard characters in order to cd
project_dir=$(echo "$project_home" | sed "s/ /*/g")
features_dir=$project_dir"/src/test/java/features/regression"
token=$2
echo -e "\nProject directory: "$project_dir
echo -e "\nFeatures directory: "$features_dir

# Generate test set and test execution json files
cd $project_dir || exit
# Import Cucumber test using xray api...
echo -e "\nImport Cucumber test using xray api..."
cd $features_dir || exit
all_directories=$(find -mindepth 1 -maxdepth 1 -type d)
if [ -z "$all_directories" ]
then
  echo -e "\nNo directories exist. Exiting..."
  exit 1
fi
for directoryPath in $all_directories
do
	echo -e "\nZip feature files..." $directoryPath
	directory=$(echo $directoryPath  | awk -F"/" '{print$2}')
	zip -r $directory.zip $directory
	echo "Importing..."
	curl -H "Content-Type: multipart/form-data" \
	-X POST -H "Authorization: Bearer $token" \
	-F "file=@$directory.zip" \
	https://xray.cloud.getxray.app/api/v2/import/feature?projectKey=TEST
done
# Console all files in current directory
ls -1
# Remove & Add Test To TestSets
echo -e  "\nRemove & Add Test To TestSets..."
search_dir_testset=$project_dir"/src/test/resources/jiraxray/testset/active"
echo -e "\nTest Set Directory: "$search_dir_testset
cd $search_dir_testset || exit
# Console all files in current directory
ls -1
testSets=$(find . -name "*.json")
if [ -z "$testSets" ]
then
  echo "No test set json file available. Exiting..."
  exit 1
fi
for testSet in $testSets
do
	testSetFile=$(echo $testSet  | awk -F"/" '{print$2}')
	echo -e "\nWorking on test set: "$testSetFile
	curl -g \
	-X POST \
	-H "Content-Type: application/json" \
	-H "Authorization: Bearer $token" \
	-d "@$testSetFile" \
	https://xray.cloud.getxray.app/api/v2/graphql
done

# Remove & Add Test To Test Execution
echo -e "\nRemove & Add Test To Test Execution..."
search_dir_testexecution=$project_dir"/src/test/resources/jiraxray/testexecution/active"
echo "Test Execution Directory: "$search_dir_testexecution
cd $search_dir_testexecution || exit
# Console all files in current directory
ls -1
testExecutions=$(find . -name "*.json")
if [ -z "$testExecutions" ]
then
  echo -e "\nNo test execution json file available. Exiting..."
  exit 1
fi
for testExecution in $testExecutions
do
	testExecutionFile=$(echo $testExecution  | awk -F"/" '{print$2}')
	echo -e "\nWorking on test execution: "$testExecutionFile
	curl -g \
	-X POST \
	-H "Content-Type: application/json" \
	-H "Authorization: Bearer $token" \
	-d "@$testExecutionFile" \
	https://xray.cloud.getxray.app/api/v2/graphql
done
cd $project_dir || exit
echo -e "\nWorking Directory: "$project_dir
