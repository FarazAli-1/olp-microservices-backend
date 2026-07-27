Write-Host "Starting Online Learning Platform..."

Write-Host "`n1. Deploying Global Secrets..."
kubectl apply -f k8s/platform-secrets.yml

Write-Host "`n2. Deploying Databases & Managers..."
kubectl apply -f k8s/config-server/deployment.yml
kubectl apply -f k8s/service-discovery/deployment.yml
kubectl apply -f k8s/postgres/deployment.yml
kubectl apply -f k8s/redis/deployment.yml

Write-Host "`nWaiting 30 seconds for infrastructure to initialize..."
Start-Sleep -Seconds 30

Write-Host "`n3. Deploying Microservices & Gateway..."
kubectl apply -f k8s/user-service/deployment.yml
kubectl apply -f k8s/course-service/deployment.yml
kubectl apply -f k8s/enrollment-service/deployment.yml
kubectl apply -f k8s/notification-service/deployment.yml
kubectl apply -f k8s/api-gateway/deployment.yml

Write-Host "`nDeployment Complete! Opening Pod Watcher...`n"

Write-Host "Opening API Gateway tunnel on port 8090..." -ForegroundColor Green
kubectl port-forward svc/api-gateway 8090:8090

kubectl get pods -w