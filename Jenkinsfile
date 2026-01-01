pipeline {
    agent any

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
            steps {
                withSonarQubeEnv('SonarQubeLocal') {
                    bat '''
                    mvn org.sonarsource.scanner.maven:sonar-maven-plugin:5.2.0.4988:sonar ^
                        -Dsonar.projectKey=sdms
                    '''
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
        success {
            echo 'Pipeline Success'
        }
        failure {
            echo 'Pipeline Failed'
        }
    }
}
