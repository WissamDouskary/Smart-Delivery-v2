pipeline {
    agent any

    environment {
        SONAR_HOST_URL = 'http://sonarqube:9000'
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/WissamDouskary/Smart-Delivery-v2'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean compile'
            }
        }

        stage('Tests') {
            steps {
                sh 'mvn test'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                sh '''
                mvn sonar:sonar \
                  -Dsonar.projectKey=sdms \
                  -Dsonar.host.url=$SONAR_HOST_URL
                '''
            }
        }

        stage('Docker Build') {
            steps {
                sh 'docker build -t sdms-app .'
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
