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
                    for (int i = 0; i < selectedOptions.size(); i++) {
                        if (i > 0) {
                            tagging += " or "
                        }
                        selectedOptions[i] = "@" +  selectedOptions[i]
                        tagging += selectedOptions[i]
                    }
                    echo "tagging: ${tagging}"                  
                    sh 'docker-compose up --build --abort-on-container-exit'
                    sh 'ls -al'
                }
            }
        }
        stage('Tear down') {
            steps {
                echo 'Tear down...'
                sh 'docker-compose down'
            }
        }
    }
}
