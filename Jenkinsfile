pipeline {
    agent any

    environment {
        SONAR_HOST_URL = 'http://sonarqube:9000'
    }
    tools {
            maven 'Maven 3.8.9'
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/WissamDouskary/Smart-Delivery-v2'
            }
        }

        stage('Build') {
            steps {
                bat 'mvn clean compile'
            }
        }

        stage('Tests') {
            steps {
                bat 'mvn test'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                bat """
                mvn sonar:sonar ^
                  -Dsonar.projectKey=sdms ^
                  -Dsonar.host.url=%SONAR_HOST_URL%
                """
            }
        }

        stage('Docker Build') {
            steps {
                bat 'docker build -t sdms-app .'
            }
        }
    }

    post {
        failure {
            echo 'Pipeline Failed'
        }
        success {
            echo 'Pipeline Success'
        }
    }
}
