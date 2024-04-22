pipeline {
    agent {
         docker { image 'alpinelinux/docker-cli' }
         }

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
                sh 'docker-compose up --build'
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
                sh 'docker-compose down'
                // Insert your build commands here, e.g., 'mvn clean install'
            }
        }
    }
}
