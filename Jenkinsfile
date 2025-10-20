pipeline {
    agent any

    environment {
        // Update this path to your actual IntelliJ Maven location
        MAVEN_PATH = 'C:\\Users\\manis\\.m2\\wrapper\\dists\\apache-maven-3.9.6\\bin\\mvn.cmd'
        JAVA_HOME = 'C:\\Program Files\\Java\\jdk-24'
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

                allure includeProperties: false,
                       jdk: '',
                       results: [[path: 'allure-results']]

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
            echo '✅ API tests passed! Build successful.'
        }
        failure {
            echo '❌ API tests failed! Check the reports for details.'
        }
    }
}
