pipeline {
    agent any

    parameters {
        booleanParam (name: 'RUN_BUILD', defaultValue: true, description: 'Run build stage')
        booleanParam (name: 'RUN_BUILD_IMAGE', defaultValue: false, description: 'Run docker image build stage')
        booleanParam (name: 'RUN_PUSH_IMAGE', defaultValue: false, description: 'Run image push dockerhub stage')
        booleanParam (name: 'RUN_DEPLOY', defaultValue: false, description: 'Run deploy stage')
    }

    environment {
        dockerImage = ''
    }

    tools {
        maven 'mvn3.6.3'
    }

    stages {

        stage('Parameter check') {
            steps {
                println "BRANCH_NAME = ${env.BRANCH_NAME}"
                println "CHANGE_BRANCH = ${env.CHANGE_BRANCH}"
            }
        }
        stage('Poll') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps{
                script {

                    if(${params.RUN_BUILD}) {

                        println "Build start!"
                        sh 'mvn clean package -DskipTests=true'
                        println "Build end!"
                    } else {

                        println "Build skipped!"
                    }
                }
            }
        }

        stage('Build image') {
            steps {
                script {

                    if(${params.RUN_BUILD_IMAGE}) {

                        println "Builds docker image start!"
                        dockerImage = docker.build("tax1116/agora")
                        println "Build end!"
                    } else {

                        println "Build skipped!"
                    }
                }
            }
        }

        stage('Push image') {
            steps {
                script {

                    if(${params.RUN_PUSH_IMAGE}) {

                        println "Push image to dockerhub start!"
                        docker.withRegistry('https://registry.hub.docker.com', 'docker-hub-tax1116-credential') {
                            dockerImage.push("${env.BUILD_NUMBER}")
                            dockerImage.push("latest")
                        }
                        println "Push image end!"
                    } else {

                        println "Push image skipped!"
                    }
                }
            }
        }

        stage('Remove image in jenkins server') {
            steps {
                script {

                    if(${params.RUN_PUSH_IMAGE}) {

                        println "Remove old images start!"
                        sh "docker rmi tax1116/agora:latest"
                        sh "docker rmi registry.hub.docker.com/tax1116/agora:$BUILD_NUMBER"
                        sh "docker rmi registry.hub.docker.com/tax1116/agora:latest"
                        println "Remove image end!"
                    } else {

                        println "Remove images skipped"
                    }
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
            emailext (
                subject: "Failed Pipeline: '${currentBuild.fullDisplayName}''",
                body: "Something is wrong with '${env.BUILD_URL}'",
                to: "tax941116@gmail.com",
                from: "tax941116@gmail.com"
            )
        }
    }
}