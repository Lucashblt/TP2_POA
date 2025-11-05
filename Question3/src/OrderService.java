import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Microservice pour la gestion des commandes
 * Port: 8083
 * 
 * Ce service communique avec UserService et ProductService
 * pour enrichir les informations des commandes.
 * 
 * Endpoints:
 * - GET  /orders      : Liste toutes les commandes avec détails
 * - GET  /orders/{id} : Récupère une commande par ID avec détails
 * - POST /orders      : Crée une nouvelle commande
 */
public class OrderService {
    private static Map<Integer, Order> orders = new HashMap<>();
    private static AtomicInteger idCounter = new AtomicInteger(1);
    
    // URLs des autres microservices
    private static final String USER_SERVICE_URL = "http://localhost:8081";
    private static final String PRODUCT_SERVICE_URL = "http://localhost:8082";

    public static void main(String[] args) throws IOException {
        // Créer le serveur HTTP sur le port 8083
        HttpServer server = HttpServer.create(new InetSocketAddress(8083), 0);
        
        // Définir les routes
        server.createContext("/orders", new OrderHandler());
        
        server.setExecutor(null);
        server.start();
        
        System.out.println("===========================================");
        System.out.println("Service Commande démarré sur le port 8083");
        System.out.println("===========================================");
        System.out.println("Endpoints disponibles:");
        System.out.println("  GET  http://localhost:8083/orders");
        System.out.println("  GET  http://localhost:8083/orders/{id}");
        System.out.println("  POST http://localhost:8083/orders");
        System.out.println("===========================================");
        System.out.println("Note: Ce service communique avec:");
        System.out.println("  - UserService (port 8081)");
        System.out.println("  - ProductService (port 8082)");
        System.out.println("===========================================");
    }

    static class OrderHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Activer CORS
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            System.out.println("[" + method + "] " + path);

            try {
                if (method.equals("OPTIONS")) {
                    sendResponse(exchange, 200, "OK");
                } else if (method.equals("GET") && path.equals("/orders")) {
                    handleGetAllOrders(exchange);
                } else if (method.equals("GET") && path.matches("/orders/\\d+")) {
                    handleGetOrderById(exchange, path);
                } else if (method.equals("POST") && path.equals("/orders")) {
                    handleCreateOrder(exchange);
                } else {
                    sendResponse(exchange, 404, "{\"error\":\"Endpoint non trouvé\"}");
                }
            } catch (Exception e) {
                e.printStackTrace();
                sendResponse(exchange, 500, "{\"error\":\"" + e.getMessage() + "\"}");
            }
        }

        private void handleGetAllOrders(HttpExchange exchange) throws IOException {
            System.out.println("Récupération de toutes les commandes avec enrichissement...");
            
            StringBuilder json = new StringBuilder("[");
            boolean first = true;
            for (Order order : orders.values()) {
                if (!first) json.append(",");
                json.append(enrichOrderWithDetails(order));
                first = false;
            }
            json.append("]");
            
            sendResponse(exchange, 200, json.toString());
        }

        private void handleGetOrderById(HttpExchange exchange, String path) throws IOException {
            String[] parts = path.split("/");
            int orderId = Integer.parseInt(parts[2]);
            
            Order order = orders.get(orderId);
            if (order != null) {
                String enrichedOrder = enrichOrderWithDetails(order);
                sendResponse(exchange, 200, enrichedOrder);
            } else {
                sendResponse(exchange, 404, "{\"error\":\"Commande non trouvée\"}");
            }
        }

        private void handleCreateOrder(HttpExchange exchange) throws IOException {
            // Lire le corps de la requête
            InputStream is = exchange.getRequestBody();
            String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            
            // Parser le JSON
            int userId = Integer.parseInt(extractJsonValue(body, "userId"));
            int productId = Integer.parseInt(extractJsonValue(body, "productId"));
            int quantity = Integer.parseInt(extractJsonValue(body, "quantity"));
            
            System.out.println("Création de commande: userId=" + userId + ", productId=" + productId + ", quantity=" + quantity);
            
            // Vérifier que l'utilisateur existe
            String userJson = callExternalService(USER_SERVICE_URL + "/users/" + userId);
            if (userJson.contains("error")) {
                sendResponse(exchange, 404, "{\"error\":\"Utilisateur non trouvé\"}");
                return;
            }
            
            // Vérifier que le produit existe
            String productJson = callExternalService(PRODUCT_SERVICE_URL + "/products/" + productId);
            if (productJson.contains("error")) {
                sendResponse(exchange, 404, "{\"error\":\"Produit non trouvé\"}");
                return;
            }
            
            // Créer la commande
            int id = idCounter.getAndIncrement();
            Order order = new Order(id, userId, productId, quantity);
            order.setStatus("Confirmée");
            orders.put(id, order);
            
            System.out.println("Commande créée: " + order.toJson());
            
            // Retourner la commande enrichie
            String enrichedOrder = enrichOrderWithDetails(order);
            sendResponse(exchange, 201, enrichedOrder);
        }

        /**
         * Enrichit une commande avec les détails de l'utilisateur et du produit
         * en appelant les autres microservices
         */
        private String enrichOrderWithDetails(Order order) {
            try {
                // Appeler UserService pour obtenir les détails de l'utilisateur
                System.out.println("  -> Appel à UserService pour userId=" + order.getUserId());
                String userJson = callExternalService(USER_SERVICE_URL + "/users/" + order.getUserId());
                
                // Appeler ProductService pour obtenir les détails du produit
                System.out.println("  -> Appel à ProductService pour productId=" + order.getProductId());
                String productJson = callExternalService(PRODUCT_SERVICE_URL + "/products/" + order.getProductId());
                
                // Construire la réponse enrichie
                return String.format(
                    "{\"id\":%d,\"userId\":%d,\"productId\":%d,\"quantity\":%d,\"status\":\"%s\",\"user\":%s,\"product\":%s}",
                    order.getId(), order.getUserId(), order.getProductId(), 
                    order.getQuantity(), order.getStatus(), userJson, productJson
                );
            } catch (Exception e) {
                System.err.println("Erreur lors de l'enrichissement: " + e.getMessage());
                return order.toJson();
            }
        }

        /**
         * Appelle un service externe via HTTP GET
         */
        private String callExternalService(String urlString) throws IOException {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                
                return response.toString();
            } else {
                return "{\"error\":\"Service non disponible\"}";
            }
        }

        private String extractJsonValue(String json, String key) {
            String searchKey = "\"" + key + "\"";
            int startIndex = json.indexOf(searchKey);
            if (startIndex == -1) return "";
            
            startIndex = json.indexOf(":", startIndex) + 1;
            
            // Chercher le début de la valeur (skip whitespace)
            while (startIndex < json.length() && Character.isWhitespace(json.charAt(startIndex))) {
                startIndex++;
            }
            
            // Si c'est une chaîne (commence par ")
            if (json.charAt(startIndex) == '"') {
                startIndex++;
                int endIndex = json.indexOf("\"", startIndex);
                return json.substring(startIndex, endIndex);
            } else {
                // C'est un nombre
                int endIndex = startIndex;
                while (endIndex < json.length() && 
                       (Character.isDigit(json.charAt(endIndex)) || json.charAt(endIndex) == '.')) {
                    endIndex++;
                }
                return json.substring(startIndex, endIndex);
            }
        }

        private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(statusCode, bytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(bytes);
            os.close();
        }
    }
}
