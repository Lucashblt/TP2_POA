@echo off
echo ==========================================
echo Compilation des microservices
echo ==========================================

if not exist "bin" mkdir bin

echo.
echo [1/3] Compilation des classes de modele...
javac -d bin src\User.java src\Product.java src\Order.java
if %ERRORLEVEL% NEQ 0 (
    echo ERREUR: Echec de la compilation des modeles
    pause
    exit /b 1
)
echo OK

echo.
echo [2/3] Compilation de UserService et ProductService...
javac -d bin -cp bin src\UserService.java src\ProductService.java
if %ERRORLEVEL% NEQ 0 (
    echo ERREUR: Echec de la compilation de UserService/ProductService
    pause
    exit /b 1
)
echo OK

echo.
echo [3/3] Compilation de OrderService...
javac -d bin -cp bin src\OrderService.java
if %ERRORLEVEL% NEQ 0 (
    echo ERREUR: Echec de la compilation de OrderService
    pause
    exit /b 1
)
echo OK

echo.
echo ==========================================
echo Compilation terminee avec succes!
echo ==========================================
echo Prochaine etape: Lancez les services avec:
echo   - run-user-service.bat
echo   - run-product-service.bat
echo   - run-order-service.bat
echo ==========================================
pause
