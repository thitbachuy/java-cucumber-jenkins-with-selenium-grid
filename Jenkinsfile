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
              sh 'cp /opt/target /target'
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
