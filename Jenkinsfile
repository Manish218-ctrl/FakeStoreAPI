      pipeline {
    agent any

    environment {
        MAVEN_PATH = 'C:\\Program Files\\JetBrains\\IntelliJ IDEA Community Edition 2025.2\\plugins\\maven\\lib\\maven3\\bin\\mvn.cmd'
        JAVA_HOME = 'C:\\Users\\manis\\.jdks\\graalvm-jdk-21.0.8'
        ALLURE_HOME = 'C:\\Program Files\\allure-2.35.1'
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
                bat "\"${MAVEN_PATH}\" clean compile"
            }
        }

        stage('Run API Tests') {
            steps {
                echo 'Running API tests...'
                bat "\"${MAVEN_PATH}\" test"
            }
        }

        stage('Generate Allure Report') {
            steps {
                echo 'Generating Allure report...'
                bat "\"${ALLURE_HOME}\\bin\\allure.bat\" generate allure-results -o allure-report --clean"
            }
        }

        stage('Publish Reports') {
            steps {
                echo 'Publishing test reports...'

                // Publish Allure report
                allure includeProperties: false,
                       jdk: '',
                       results: [[path: 'allure-results']]

                // Publish HTML reports (if you have ExtentReports)
                publishHTML([
                    allowMissing: true,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'reports',
                    reportFiles: 'ExtentReport*.html',
                    reportName: 'Extent Report',
                    reportTitles: 'API Test Execution Report'
                ])
            }
        }
    }

    post {
        always {
            echo 'Archiving test artifacts...'
            archiveArtifacts artifacts: 'allure-report/**/*', allowEmptyArchive: true
            archiveArtifacts artifacts: 'reports/**/*', allowEmptyArchive: true
        }
        success {
            echo 'API tests passed! Build successful.'
        }
        failure {
            echo 'API tests failed! Check the reports for details.'
        }
        unstable {
            echo 'Build is unstable. Some tests may have failed.'
        }
    }
}