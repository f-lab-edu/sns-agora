pipeline {
    agent any

    tools {
        maven 'mvn3.6.3'
    }

    stages {
        stage('Poll') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps{
                script {

                    sh 'mvn clean package -DskipTests=true'
                }
            }
        }

        stage('Unit Tests') {
            steps {
                script {
                    try {
                        sh 'mvn surefire:test'
                        junit '**/target/surefire-reports/TEST-*.xml'

                        if (env.CHANGE_ID) {
                            pullRequest.createStatus(
                                    status: 'success',
                                    context: 'JUnit Test',
                                    description: 'success',
                                    targetUrl: "${currentBuild.absoluteUrl}testReport/")
                        }
                    } catch (exc) {
                        junit '**/target/surefire-reports/TEST-*.xml'

                        if (env.CHANGE_ID) {
                            pullRequest.createStatus(
                                    status: 'failure',
                                    context: 'JUnit Test',
                                    description: 'Unit test failed.',
                                    targetUrl: "${currentBuild.absoluteUrl}testReport/")
                        }
                    }
                }
            }
        }

        stage('Integration Tests') {
            steps {
                script {
                    try {

                        sh 'mvn failsafe:integration-test'
                        junit '**/target/failsafe-reports/TEST-*.xml'

                        if (env.CHANGE_ID) {
                            pullRequest.createStatus(
                                    status: 'success',
                                    context: 'Integration Test',
                                    description: 'success',
                                    targetUrl: "${currentBuild.absoluteUrl}testReport/")
                        }
                    } catch (exc){
                        junit '**/target/failsafe-reports/TEST-*.xml'

                        if (env.CHANGE_ID) {
                            pullRequest.createStatus(
                                    status: 'failure',
                                    context: 'JUnit Test',
                                    description: 'Unit test failed.',
                                    targetUrl: "${currentBuild.absoluteUrl}testReport/")
                        }
                    }

                }
            }
        }
    }
}