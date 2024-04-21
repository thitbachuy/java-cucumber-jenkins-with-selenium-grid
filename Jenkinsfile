pipeline {
    agent any  // This specifies that the pipeline can run on any available agent

    stages {
         stage('Checkout') {
              steps {
                  echo 'Checkout...'
                  checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'jenkins-user-github', url: 'https://github.com/thitbachuy/selenium-java-02082023.git']]])
                  sh "ls -lart ./*"
              }
          }

        stage('Create containers') {
            steps {
                echo 'Creating containers...'
                sh 'docker-compose -f docker-compose.yml up'
                // Insert your build commands here, e.g., 'mvn clean install'
            }
        }

        stage('Test') {
            steps {
                echo 'Testing...'
                // Insert your test commands here, e.g., 'mvn test'
            }
        }

        stage('Tear down') {
            steps {
                echo 'Tear down...'
                sh 'docker-compose -f docker-compose.yml down'
                // Insert your build commands here, e.g., 'mvn clean install'
            }
        }
    }
}