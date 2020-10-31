pipeline {
    agent any

    environment {
        dockerImage = ''
    }

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

        stage('Build image') {
            steps {
                script {
                    dockerImage = docker.build("tax1116/agora")
                }
            }
        }

        stage('Push image') {
            steps {
                script {
                    docker.withRegistry('https://registry.hub.docker.com', 'docker-hub-tax1116-credential') {
                        dockerImage.push("${env.BUILD_NUMBER}")
                        dockerImage.push("latest")
                    }
                }
            }
        }

        stage('Remove image in jenkins server') {
            steps {
                script {
                    sh "docker rmi registry.hub.docker.com/tax1116/agora:$BUILD_NUMBER"
                    sh "docker rmi registry.hub.docker.com/tax1116/agora:latest"
                }
            }
        }

        stage('Deploy') {
            steps([$class: 'BapSshPromotionPublisherPlugin']) {
                sshPublisher(
                        continueOnError: false, failOnError: true,
                        publishers: [
                                sshPublisherDesc(
                                        configName: "agora-was",
                                        verbose: true,
                                        transfers: [
                                                sshTransfer(
                                                        sourceFiles: "", // 전송파일
                                                        removePrefix: "", //파일에서 삭제할 경로
                                                        remoteDirectory: "", // 배포위치
                                                        execCommand: "bash run.sh" //원격지 실행 커맨드
                                                )
                                        ]
                                )
                        ]
                )
            }
        }
    }

    post {

        always {
            script {
                if(env.CHANGE_ID) {
                    pullRequest.comment("Check build result: ${currentBuild.absoluteUrl}")
                }
            }
        }

        failure {
            mail to: 'tax941116@gmail.com',
            subject: "Failed Pipeline: ${currentBuild.fullDisplayName}",
            body: "Something is wrong with ${env.BUILD_URL}"
        }
    }
}