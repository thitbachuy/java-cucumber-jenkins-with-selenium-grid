FROM maven:3.8.6-jdk-11

ARG app_name=selenium_java_25022024
RUN apt-get update

WORKDIR /opt/${app_name}
RUN chmod -R 777 /opt/${app_name}

# Install tools.
RUN apt update -y & apt install -y wget unzip
ARG DEBIAN_FRONTEND=noninteractive
RUN apt-get install -y tzdata

# Install Google Chrome:
RUN wget -q -O - https://dl-ssl.google.com/linux/linux_signing_key.pub | apt-key add -
RUN sh -c 'echo "deb http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google-chrome.list'
RUN apt-get update
RUN apt-get install -y google-chrome-stable

# Install ChromeDriver.
RUN wget -N https://chromedriver.storage.googleapis.com/105.0.5195.19/chromedriver_linux64.zip -P ~/
RUN unzip ~/chromedriver_linux64.zip -d ~/
RUN rm ~/chromedriver_linux64.zip
RUN mv -f ~/chromedriver /usr/local/bin/chromedriver
RUN chmod +x /usr/local/bin/chromedriver

#Copy source code and pom file.
COPY ./src /opt/${app_name}/src
COPY ./pom.xml /opt/${app_name}

ENV HUB_ENDPOINT localhost:4444
ENV TAGS Tiki

ENTRYPOINT mvn test -Dcucumber.filter.tags=@${TAGS} -Dcucumber.filter -Dbrowser=chromeGCP -DexecutingEnv=test -DtestedEnv=uat -Dplatform=desktop
