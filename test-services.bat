@echo off
chcp 65001 >nul
echo ==========================================
echo Test des Microservices
echo ==========================================
echo.
echo Ce script teste les 3 microservices:
echo   - UserService (8081)
echo   - ProductService (8082)
echo   - OrderService (8083)
echo.
echo ==========================================
echo.
pause

echo.
echo ==========================================
echo [TEST 1] Liste des utilisateurs
echo ==========================================
powershell -Command "try { $response = Invoke-WebRequest -Uri 'http://localhost:8081/users' -UseBasicParsing; Write-Host 'Status:' $response.StatusCode; Write-Host 'Reponse:'; $response.Content } catch { Write-Host 'ERREUR: Service non disponible' -ForegroundColor Red }"

echo.
echo ==========================================
echo [TEST 2] Creation d'un utilisateur
echo ==========================================
powershell -Command "try { $body = '{\"nom\":\"Alice Durand\",\"email\":\"alice.durand@email.com\"}'; $response = Invoke-WebRequest -Uri 'http://localhost:8081/users' -Method POST -Body $body -ContentType 'application/json' -UseBasicParsing; Write-Host 'Status:' $response.StatusCode; Write-Host 'Reponse:'; $response.Content } catch { Write-Host 'ERREUR:' $_.Exception.Message -ForegroundColor Red }"

echo.
echo ==========================================
echo [TEST 3] Recuperation utilisateur ID=1
echo ==========================================
powershell -Command "try { $response = Invoke-WebRequest -Uri 'http://localhost:8081/users/1' -UseBasicParsing; Write-Host 'Status:' $response.StatusCode; Write-Host 'Reponse:'; $response.Content } catch { Write-Host 'ERREUR: Service non disponible' -ForegroundColor Red }"

echo.
echo ==========================================
echo [TEST 4] Liste des produits
echo ==========================================
powershell -Command "try { $response = Invoke-WebRequest -Uri 'http://localhost:8082/products' -UseBasicParsing; Write-Host 'Status:' $response.StatusCode; Write-Host 'Reponse:'; $response.Content } catch { Write-Host 'ERREUR: Service non disponible' -ForegroundColor Red }"

echo.
echo ==========================================
echo [TEST 5] Creation d'un produit
echo ==========================================
powershell -Command "try { $body = '{\"nom\":\"Webcam HD\",\"prix\":\"49.99\",\"stock\":\"30\"}'; $response = Invoke-WebRequest -Uri 'http://localhost:8082/products' -Method POST -Body $body -ContentType 'application/json' -UseBasicParsing; Write-Host 'Status:' $response.StatusCode; Write-Host 'Reponse:'; $response.Content } catch { Write-Host 'ERREUR:' $_.Exception.Message -ForegroundColor Red }"

echo.
echo ==========================================
echo [TEST 6] Recuperation produit ID=1
echo ==========================================
powershell -Command "try { $response = Invoke-WebRequest -Uri 'http://localhost:8082/products/1' -UseBasicParsing; Write-Host 'Status:' $response.StatusCode; Write-Host 'Reponse:'; $response.Content } catch { Write-Host 'ERREUR: Service non disponible' -ForegroundColor Red }"

echo.
echo ==========================================
echo [TEST 7] Creation d'une commande
echo (Ce test communique avec les 2 autres services!)
echo ==========================================
powershell -Command "try { $body = '{\"userId\":\"1\",\"productId\":\"1\",\"quantity\":\"2\"}'; $response = Invoke-WebRequest -Uri 'http://localhost:8083/orders' -Method POST -Body $body -ContentType 'application/json' -UseBasicParsing; Write-Host 'Status:' $response.StatusCode; Write-Host 'Reponse:'; $response.Content } catch { Write-Host 'ERREUR:' $_.Exception.Message -ForegroundColor Red }"

echo.
echo ==========================================
echo [TEST 8] Liste des commandes avec details enrichis
echo (Les donnees utilisateur et produit sont recuperees)
echo ==========================================
powershell -Command "try { $response = Invoke-WebRequest -Uri 'http://localhost:8083/orders' -UseBasicParsing; Write-Host 'Status:' $response.StatusCode; Write-Host 'Reponse:'; $response.Content } catch { Write-Host 'ERREUR: Service non disponible' -ForegroundColor Red }"

echo.
echo ==========================================
echo [TEST 9] Creation d'une 2eme commande
echo ==========================================
powershell -Command "try { $body = '{\"userId\":\"2\",\"productId\":\"3\",\"quantity\":\"1\"}'; $response = Invoke-WebRequest -Uri 'http://localhost:8083/orders' -Method POST -Body $body -ContentType 'application/json' -UseBasicParsing; Write-Host 'Status:' $response.StatusCode; Write-Host 'Reponse:'; $response.Content } catch { Write-Host 'ERREUR:' $_.Exception.Message -ForegroundColor Red }"

echo.
echo ==========================================
echo [TEST 10] Recuperation commande ID=1
echo ==========================================
powershell -Command "try { $response = Invoke-WebRequest -Uri 'http://localhost:8083/orders/1' -UseBasicParsing; Write-Host 'Status:' $response.StatusCode; Write-Host 'Reponse:'; $response.Content } catch { Write-Host 'ERREUR: Service non disponible' -ForegroundColor Red }"

echo.
echo ==========================================
echo Tests termines!
echo ==========================================
pause
