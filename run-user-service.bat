@echo off
echo ==========================================
echo Demarrage du Service Utilisateur
echo Port: 8081
echo ==========================================
echo.

cd /d "%~dp0"

if not exist "bin" (
    echo ERREUR: Le dossier 'bin' n'existe pas!
    echo Veuillez d'abord compiler avec compile.bat
    pause
    exit /b 1
)

java -cp bin UserService

pause
