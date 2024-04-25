pipeline {
  agent any
  stages {
    stage('Unit Test') {
      steps {
        sh 'mvn clean test'
      }
    }
    stage('Deploy Standalone') {
      steps {
        sh 'mvn deploy -P standalone'
      }
    }
    stage('Deploy AnyPoint') {
      environment {
        ANYPOINT_CREDENTIALS = credentials('anypoint.credentials')
      }
      steps {
        sh 'mvn deploy -P arm -Darm.target.name=local-4.0.0-ee -Danypoint.username=${ANYPOINT_CREDENTIALS_USR}  -Danypoint.password=${ANYPOINT_CREDENTIALS_PSW}'
      }
    }
    stage('Deploy CloudHub') {
      environment {
        ANYPOINT_CREDENTIALS = credentials('anypoint.credentials')
      }
      steps {
        sh 'mvn deploy -P cloudhub -Dmule.version=4.0.0 -Danypoint.username=${ANYPOINT_CREDENTIALS_USR} -Danypoint.password=${ANYPOINT_CREDENTIALS_PSW}'
      }
    }
  }
}
pipeline {
  agent {
    docker {
      image 'alpinelinux/docker-cli'
    }
  }
  parameters {
    extendedChoice(
      name: 'environment',
      type: 'PT_SINGLE_SELECT',
      value: 'DEV,UAT,PROD',
      description: 'Please select the environment that you want to run',
      visibleItemCount: 3,
      multiSelectionDelimiter: ',',
      quoteValue: false
    )
  }

  stages {
    stage('Checkout') {
      steps {
        echo 'Checkout...'
        checkout([$class: 'GitSCM', branches: [
          [name: '*/master']
        ], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [
          [credentialsId: 'jenkins-user-github', url: 'https://github.com/thitbachuy/selenium-java-02082023.git']
        ]])
        sh "ls -lart ./*"
      }
    }

    stage('Create containers and run test') {
      steps {
        echo 'Creating containers...'
        sh 'docker-compose up --build --abort-on-container-exit'
        // Insert your build commands here, e.g., 'mvn clean install'
      }
    }

    stage('Export result') {
      steps {
        echo 'exporting...'
        // Insert your test commands here, e.g., 'mvn test'
      }
    }

    // stage('Tear down') {
    //     steps {
    //         echo 'Tear down...'
    //         sh 'docker-compose down'
    //         // Insert your build commands here, e.g., 'mvn clean install'
    //     }
    // }
  }
}
