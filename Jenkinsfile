pipeline {
    agent any

    tools {
        jdk 'JDK17'
        maven 'Maven'
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'ci/jenkins', url: 'https://github.com/WissamDouskary/Smart-Delivery-v2'
            }
        }

        stage('Build') {
            steps {
                bat 'mvn clean install -DskipTests'
            }
        }

        stage('Docker Build & Run') {
            steps {
                bat 'docker-compose up -d --build'
            }
        }
    }

    post {
        success {
            echo 'Pipeline Success'
        }
        failure {
            echo 'Pipeline Failed'
        }
    }
}
