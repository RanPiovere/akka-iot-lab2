pipeline {
    agent any

    parameters {
        string(
            name: 'DOCKER_IMAGE',
            defaultValue: 'your-dockerhub-user/akka-iot-service',
            description: 'Docker registry image name'
        )
        string(
            name: 'TEAM_EMAIL',
            defaultValue: '',
            description: 'Optional build failure recipient'
        )
    }

    options {
        timestamps()
        skipDefaultCheckout(true)
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '20'))
    }

    environment {
        SBT_OPTS = '-Xms256m -Xmx1g -XX:MaxMetaspaceSize=512m'
        AKKA_TEST_TIMEFACTOR = '2'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Compile') {
            steps {
                script {
                    runSbt('clean compile')
                }
            }
        }

        stage('Static Analysis') {
            steps {
                script {
                    runSbt('scalastyle test:scalastyle')
                }
            }
        }

        stage('Unit Tests') {
            steps {
                script {
                    runSbt('test')
                }
            }
        }

        stage('Matrix Testing') {
            matrix {
                axes {
                    axis {
                        name 'JAVA_VERSION'
                        values 'JDK11', 'JDK17', 'JDK21'
                    }

                    axis {
                        name 'PROFILE'
                        values 'Standard', 'Cluster'
                    }
                }

                tools {
                    jdk "${JAVA_VERSION}"
                }

                stages {
                    stage('Build and Test') {
                        steps {
                            echo "Testing Akka IoT service on ${JAVA_VERSION} with ${PROFILE} profile"

                            ws("${env.WORKSPACE}@matrix-${JAVA_VERSION}-${PROFILE}") {
                                deleteDir()
                                checkout scm

                                script {
                                    runSbt("-Diot.profile=${PROFILE.toLowerCase()} test")
                                }
                            }
                        }
                    }
                }
            }
        }

        stage('Package and Push Docker Image') {
            when {
                branch 'main'
            }

            steps {
                script {
                    runSbt('assembly')

                    def iotImage = docker.build(
                        "${params.DOCKER_IMAGE}:${env.BUILD_NUMBER}"
                    )

                    docker.withRegistry(
                        'https://index.docker.io/v1/',
                        'dockerhub-credentials-id'
                    ) {
                        iotImage.push()
                        iotImage.push('latest')
                    }
                }
            }
        }
    }

    post {
        always {
            junit(
                testResults: '**/target/test-reports/*.xml',
                allowEmptyResults: true
            )

            archiveArtifacts(
                artifacts: 'target/scala-2.13/*.jar',
                allowEmptyArchive: true,
                fingerprint: true
            )
        }

        failure {
            echo 'Build failed. Review the failed stage and Console Output.'

            script {
                if (params.TEAM_EMAIL?.trim()) {
                    mail(
                        to: params.TEAM_EMAIL,
                        subject: "FAILED: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                        body: "Pipeline failed: ${env.BUILD_URL}"
                    )
                }
            }
        }
    }
}

def runSbt(String arguments) {
    if (isUnix()) {
        sh """
            sbt \
            -Dsbt.server.autostart=false \
            -Dsbt.supershell=false \
            -Dakka.test.timefactor=${env.AKKA_TEST_TIMEFACTOR} \
            ${arguments}
        """
    } else {
        bat """
            sbt ^
            -Dsbt.server.autostart=false ^
            -Dsbt.supershell=false ^
            -Dakka.test.timefactor=${env.AKKA_TEST_TIMEFACTOR} ^
            ${arguments}
        """
    }
}