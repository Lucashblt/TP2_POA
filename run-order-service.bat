@echo off
echo ==========================================
echo Demarrage du Service Commande
echo Port: 8083
echo ==========================================
echo.
echo ATTENTION: Ce service a besoin de:
echo   - UserService (port 8081)
echo   - ProductService (port 8082)
echo.
echo Assurez-vous qu'ils sont demarres!
echo ==========================================
echo.

cd /d "%~dp0"

if not exist "bin" (
    echo ERREUR: Le dossier 'bin' n'existe pas!
    echo Veuillez d'abord compiler avec compile.bat
    pause
    exit /b 1
)

java -cp bin OrderService

pause
