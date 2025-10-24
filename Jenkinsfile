   pipeline {
    agent any
    
    environment {
        JAVA_HOME = 'C:\\Program Files\\Java\\jdk-24'
        PATH = "${env.JAVA_HOME}\\bin;${env.PATH}"
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out code from GitHub...'
                checkout scm
            }
        }
        
        stage('Build') {
            steps {
                echo 'Compiling the project...'
                script {
                    // Use Maven wrapper if available, otherwise use mvn
                    if (fileExists('mvnw.cmd')) {
                        bat 'mvnw.cmd clean compile'
                    } else {
                        bat 'mvn clean compile'
                    }
                }
            }
        }
        
        stage('Run API Tests') {
            steps {
                echo 'Running API tests...'
                script {
                    if (fileExists('mvnw.cmd')) {
                        bat 'mvnw.cmd test'
                    } else {
                        bat 'mvn test'
                    }
                }
            }
        }
        
        stage('Generate Allure Report') {
            steps {
                echo 'Generating Allure report...'
                bat 'allure generate allure-results -o allure-report --clean'
            }
        }
        
        stage('Publish Reports') {
            steps {
                echo 'Publishing test reports...'
                
                allure includeProperties: false,
                       jdk: '',
                       results: [[path: 'allure-results']]
                
                publishHTML([
                    allowMissing: true,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'reports',
                    reportFiles: '*.html',
                    reportName: 'Extent Report',
                    reportTitles: 'API Test Report'
                ])
            }
        }
    }
    
    post {
        always {
            echo 'Archiving artifacts...'
            archiveArtifacts artifacts: 'allure-report/**/*', allowEmptyArchive: true
            archiveArtifacts artifacts: 'reports/**/*', allowEmptyArchive: true
        }
        success {
            echo ' Build succeeded!'
        }
        failure {
            echo 'Build failed!'
        }
    }
}
