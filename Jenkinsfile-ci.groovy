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
                        sh 'mvn surefire:test'
                        junit '**/target/surefire-reports/TEST-*.xml'
                }
            }
        }

        stage('Integration Tests') {
            steps {
                script {

                    sh 'mvn failsafe:integration-test'
                    junit allowEmptyResults: true, testResults: '**/target/failsafe-reports/TEST-*.xml'
                }
            }
        }
    }
}