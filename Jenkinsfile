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
            defaultValue: '',
            multiSelectDelimiter: ',',
            quoteValue: false
        )
        extendedChoice(
            name: 'TAGGING',
            type: 'PT_CHECKBOX',
            value: 'Tiki,Shopee,Google',
            description: 'Please select the tagging that you want to run',
            visibleItemCount: 3,
            defaultValue: '',
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
                script {
                    echo 'Creating containers...'
                    echo "BROWSER: ${params.BROWSER}"
                    echo "TAGGING: ${params.TAGGING}"
                    def selectedOptions = params.TAGGING.split(',')
                    selectedOptions = selectedOptions.collect {
                        "@${it}"
                    }
                    def tagging = selectedOptions.join(',')
                    echo "Selected options with '@': ${selectedOptions.join(',')}"
                    echo "tagging: ${tagging}"
                    sh 'docker-compose up --build --abort-on-container-exit'
                    sh 'ls -al'
                }
            }
        }
        stage('Export result') {
            steps {
                echo 'exporting...'
                sh 'docker cp testing:./target .'
                sh 'ls -al /target'
            }
        }
        stage('Tear down') {
            steps {
                echo 'Tear down...'
                params.TAGGING = ''
                params.BROWSER = ''
                sh 'docker-compose down'
            }
        }
    }
}
