@echo off
echo ==========================================
echo Lancement de tous les microservices
echo ==========================================
echo.
echo Ce script va ouvrir 3 fenetres de terminal:
echo   1. UserService (Port 8081)
echo   2. ProductService (Port 8082)
echo   3. OrderService (Port 8083)
echo.
echo Attendez quelques secondes entre chaque lancement...
echo ==========================================
pause

echo.
echo [1/3] Demarrage de UserService...
start "UserService (Port 8081)" cmd /k "run-user-service.bat"
timeout /t 3 /nobreak >nul

echo [2/3] Demarrage de ProductService...
start "ProductService (Port 8082)" cmd /k "run-product-service.bat"
timeout /t 3 /nobreak >nul

echo [3/3] Demarrage de OrderService...
start "OrderService (Port 8083)" cmd /k "run-order-service.bat"
timeout /t 3 /nobreak >nul

echo.
echo ==========================================
echo Tous les services sont en cours de demarrage!
echo ==========================================
echo.
echo Attendez 5 secondes puis vous pouvez lancer:
echo   test-services.bat
echo.
echo Pour arreter les services, fermez les fenetres
echo ou appuyez sur Ctrl+C dans chaque fenetre.
echo ==========================================
pause
