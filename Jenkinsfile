pipeline {
    agent any

    environment {
        APP_NAME     = 'student-attendance-devops'
        IMAGE_TAG    = "${env.BUILD_NUMBER ?: 'latest'}"
        PORT         = '3000'
        DOCKER_IMAGE = "${APP_NAME}:${IMAGE_TAG}"
    }

    stages {
        stage('1. Checkout SCM') {
            steps {
                echo '=== Checking out repository from Git ==='
                checkout scm
            }
        }

        stage('2. Install Dependencies') {
            steps {
                script {
                    def targetDir = fileExists('Student-Attendance-DevOps/package.json') ? 'Student-Attendance-DevOps' : '.'
                    dir(targetDir) {
                        echo "=== Installing Node.js Dependencies in ${targetDir} ==="
                        bat 'npm.cmd install'
                    }
                }
            }
        }

        stage('3. Code Syntax & Database Verification') {
            steps {
                script {
                    def targetDir = fileExists('Student-Attendance-DevOps/package.json') ? 'Student-Attendance-DevOps' : '.'
                    dir(targetDir) {
                        echo "=== Checking Node syntax and database initialization in ${targetDir} ==="
                        bat 'node -c server.js'
                        bat 'node -c database/db.js'
                        bat 'node seed.js'
                    }
                }
            }
        }

        stage('4. Docker Build') {
            steps {
                script {
                    def targetDir = fileExists('Student-Attendance-DevOps/package.json') ? 'Student-Attendance-DevOps' : '.'
                    dir(targetDir) {
                        echo "=== Building Docker Image: ${DOCKER_IMAGE} in ${targetDir} ==="
                        bat "docker build -t ${DOCKER_IMAGE} -t ${APP_NAME}:latest ."
                    }
                }
            }
        }

        stage('5. Container Health Check') {
            steps {
                echo '=== Running container in isolated test environment ==='
                script {
                    bat 'docker rm -f attendance-test-container || exit 0'
                    bat "docker run -d --name attendance-test-container -p 3001:3000 ${APP_NAME}:latest"
                    sleep(time: 6, unit: 'SECONDS')
                    bat 'curl -f http://localhost:3001/api/dashboard'
                    bat 'docker rm -f attendance-test-container'
                }
            }
        }

        stage('6. Automated Testing') {
            steps {
                script {
                    def targetDir = fileExists('Student-Attendance-DevOps/package.json') ? 'Student-Attendance-DevOps' : '.'
                    dir(targetDir) {
                        echo "=== Executing Automated Verification Tests in ${targetDir} ==="
                        bat 'npm.cmd run test:api'
                    }
                }
            }
        }
    }

    post {
        always {
            echo '=== Pipeline Execution Completed ==='
            bat 'docker rm -f attendance-test-container || exit 0'
        }
        success {
            echo "✅ Build #${env.BUILD_NUMBER} Succeeded! Application is ready for Kubernetes deployment."
        }
        failure {
            echo "❌ Build #${env.BUILD_NUMBER} Failed! Check console log for details."
        }
    }
}
