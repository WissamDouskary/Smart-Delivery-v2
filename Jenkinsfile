pipeline {
    agent any

    environment {
        SONAR_HOST_URL = 'http://sonarqube:9000'
    }
    tools {
        jdk 'JDK17'
        maven 'Maven'
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/WissamDouskary/Smart-Delivery-v2'
            }
        }

        stage('Build') {
            steps {
                bat 'mvn clean install -DskipTests'
            }
        }

        stage('SonarQube Analysis') {
            environment {
                SCANNER_HOME = tool 'sonarQube'
            }
            steps {
                withSonarQubeEnv('SonarQubeLocal') {
                    bat "${SCANNER_HOME}\\bin\\sonar-scanner.bat -Dsonar.projectKey=sdms -Dsonar.sources=src/main/java"
                }
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
