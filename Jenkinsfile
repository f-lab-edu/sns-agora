pipeline {
    agent any

    environment {
        dockerImage = ''
    }

    tools {
        maven 'mvn3.6.3'
    }

    parameters {

        booleanParam defaultValue: true, description: 'Parameter to check whether to run the build', name: 'RUN_BUILD'
        booleanParam defaultValue: false, description: 'Parameter to check whether to run the build docker image', name: 'RUN_BUILD_IMAGE'
        booleanParam defaultValue: false, description: 'Parameter to check whether to run the push image to dockerhub', name: 'RUN_PUSH_IMAGE'
        booleanParam defaultValue: false, description: 'Parameter to check whether to run the deploy', name: 'RUN_DEPLOY'
    }

    stages {
        stage('Poll') {
            steps {
                checkout scm
            }
        }

        stage('Build') {

            when { expression { params.RUN_BUILD } }
            steps{
                script {

                    sh 'mvn clean package -DskipTests=true'
                }
            }
        }

        stage('Build image') {

            when { expression { params.RUN_BUILD_IMAGE } }
            steps {
                script {

                    dockerImage = docker.build("tax1116/agora")
                }
            }
        }

        stage('Push image') {

            when { expression { params.RUN_PUSH_IMAGE } }
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

            when { expression { params.RUN_BUILD_IMAGE } }
            steps {
                script {
                    sh "docker rmi tax1116/agora:latest"
                    sh "docker rmi registry.hub.docker.com/tax1116/agora:$BUILD_NUMBER"
                    sh "docker rmi registry.hub.docker.com/tax1116/agora:latest"

                }
            }
        }

        stage('Deploy') {

            when { expression { params.RUN_DEPLOY } }

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
}