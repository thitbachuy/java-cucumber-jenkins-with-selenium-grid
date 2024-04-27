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
        //         extendedChoice(
        //             name: 'TAGGING',
        //             type: 'PT_CHECKBOX',
        //             value: 'Tiki,Shopee,Google',
        //             description: 'Please select the tagging that you want to run',
        //             visibleItemCount: 3,
        //             multiSelectDelimiter: ',',
        //             quoteValue: false
        //         )
    }
    stages {
        stage('Select TAGGING') {
            script {
                properties([
                    parameters([
                        multiselect(
                            decisionTree: [
                                variableDescriptions: [
                                    [
                                        label: 'Tiki',
                                        variableName: '@Tiki'
                                    ],
[
                                        label: 'Shopee',
                                        variableName: '@Shopee'
                                    ]
                                ]
                            ],
                            description: 'Please select the tagging you want to run',
                            name: 'TAGGING'
                        )
                    ])
                ])
            }
        }
    }
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
                sh 'docker-compose up --build --abort-on-container-exit'
                sh 'ls -al'
                // Insert your build commands here, e.g., 'mvn clean install'
            }
        }
    }
    stage('Export result') {
        steps {
            echo 'exporting...'
            //         sh 'docker cp testing:/target /target'
            //         sh 'ls -al /target'
            // Insert your test commands here, e.g., 'mvn test'
        }
    }
    stage ('email') {
        steps {
            emailext mimeType: 'text/html',
            body: 'Hi',
            subject: "Selenium: Job '${env.JOB_NAME}' Status: currentBuild.resul",
            to: 'noikhongvoitrai@gmail.com'
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
