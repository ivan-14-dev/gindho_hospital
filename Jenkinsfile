pipeline {
    agent any
    tools {
        maven 'Maven 3.9.x'
        jdk 'JDK 21'
        docker 'Docker'
    }
    environment {
        REGISTRY = 'registry.example.com'
        IMAGE_NAME = 'gindho-hospital'
        POSTGRES_IMAGE = 'postgres:16-alpine'
    }
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Clean') {
            steps {
                sh './mvnw clean -f hospital/pom.xml || mvn clean -f hospital/pom.xml'
            }
        }
        stage('Build') {
            steps {
                sh './mvnw package -DskipTests -f hospital/pom.xml || mvn package -DskipTests -f hospital/pom.xml'
            }
        }
        stage('Test') {
            steps {
                sh './mvnw test -f hospital/pom.xml || mvn test -f hospital/pom.xml'
            }
            post {
                always {
                    junit '**/hospital/**/target/surefire-reports/*.xml'
                }
            }
        }
        stage('Code Quality') {
            steps {
                sh '''
                    ./mvnw checkstyle:check -f hospital/pom.xml || mvn checkstyle:check -f hospital/pom.xml
                '''
            }
        }
        stage('Docker Compose Build') {
            steps {
                sh '''
                    cd hospital/docker
                    docker-compose build
                '''
            }
        }
        stage('Push Images') {
            steps {
                script {
                    def services = ['backend', 'patient-service', 'appointment-service', 'medical-record-service', 
                                   'admission-service', 'emergency-service', 'ward-service', 'bed-service',
                                   'round-service', 'surgery-service', 'prescription-service', 'pharmacy-service',
                                   'laboratory-service', 'billing-service', 'insurance-service', 'payment-service',
                                   'inventory-service', 'procurement-service', 'asset-service', 'ambulance-service',
                                   'notification-service', 'audit-service', 'reporting-service', 'authorization-service',
                                   'identity-service', 'api-gateway']
                    for (service in services) {
                        def img = docker.build("${REGISTRY}/${service}:${BUILD_NUMBER}", "hospital/services/${service}")
                        docker.withRegistry("https://${REGISTRY}", 'registry-credentials') {
                            img.push()
                            if (env.GIT_BRANCH == 'main') {
                                img.push('latest')
                            }
                        }
                    }
                }
            }
        }
        stage('Deploy') {
            when {
                branch 'main'
            }
            steps {
                sh '''
                    echo "Deploying hospital microservices to production k8s"
                    cd hospital/k8s
                    # kubectl apply -f namespaces.yaml
                    # kubectl apply -f overlays/prod
                '''
            }
        }
    }
    post {
        success {
            echo 'Hospital pipeline completed successfully!'
        }
        failure {
            echo 'Hospital pipeline failed!'
            mail to: 'devops@bercore.com', subject: 'Gindho-hospital build failed', body: "Build ${BUILD_NUMBER} failed"
        }
    }
}