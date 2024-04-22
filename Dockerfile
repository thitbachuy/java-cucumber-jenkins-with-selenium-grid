FROM maven:3.8.6-jdk-11

ARG app_name=automation_testing

WORKDIR /apps/${app_name}
RUN chmod -R 777 /apps/${app_name}
RUN curl -fsSLO https://get.docker.com/builds/Linux/x86_64/docker-17.04.0-ce.tgz \
  && tar xzvf docker-17.04.0-ce.tgz \
  && mv docker/docker /usr/local/bin \
  && rm -r docker docker-17.04.0-ce.tgz

ENV env_browser_param chromeGCP

#Copy source code and pom file.
COPY src /apps/${app_name}/src
COPY pom.xml /apps/${app_name}

ENTRYPOINT mvn test -Dcucumber.filter.tags=@Tiki -Dcucumber.filter -Dbrowser=${env_browser_param} -DexecutingEnv=test -DtestedEnv=uat -Dplatform=desktop
