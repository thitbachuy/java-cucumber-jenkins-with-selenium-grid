pipeline {
  agent {
    docker {
      image 'alpinelinux/docker-cli'
    }
  }
  parameters {
    extendedChoice(
      name: 'BROWSER',
      type: 'PT_SINGLE_SELECT',
      value: 'chromeGCP,chrome,firefox',
      description: 'Please select the browser that you want to run',
      visibleItemCount: 3,
      multiSelectDelimiter: ',',
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
        echo "BROWSER: ${params.BROWSER}"
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
