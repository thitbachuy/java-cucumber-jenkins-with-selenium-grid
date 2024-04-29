FROM maven:3.8.6-jdk-11

ARG app_name=automation_testing

WORKDIR /apps/${app_name}
RUN chmod -R 777 /apps/${app_name}

#Copy source code and pom file.
COPY src /apps/${app_name}/src
COPY pom.xml /apps/${app_name}
# ENV Tagging Tiki
# ENV Browser chromeGCP
RUN echo "----Tagging: " $Tagging

ENTRYPOINT mvn test -Dcucumber.options="--tags ${Tagging}" -Dcucumber.filter -Dbrowser=${Browser} -DexecutingEnv=test -DtestedEnv=uat -Dplatform=desktop
