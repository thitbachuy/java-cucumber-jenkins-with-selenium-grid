pipeline {
    agent any  // This specifies that the pipeline can run on any available agent

    stages {
        stage('Build') {
            steps {
                echo 'Building...'
                // Insert your build commands here, e.g., 'mvn clean install'
            }
        }

        stage('Test') {
            steps {
                echo 'Testing...'
                // Insert your test commands here, e.g., 'mvn test'
            }
        }

        stage('Deploy') {
            steps {
                echo 'Deploying...'
                // Insert your deployment commands here, e.g., script to deploy to a server
            }
        }
    }
}
