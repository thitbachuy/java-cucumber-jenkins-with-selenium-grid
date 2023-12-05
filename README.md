run CMD:
mvn clean verify -Dcucumber.filter.tags=@%1 -Dbrowser=chrome -DexecutingEnv=test -DtestedEnv=uat -Dplatform=desktop
