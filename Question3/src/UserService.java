import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Microservice pour la gestion des utilisateurs
 * Port: 8081
 * 
 * Endpoints:
 * - GET  /users      : Liste tous les utilisateurs
 * - GET  /users/{id} : Récupère un utilisateur par ID
 * - POST /users      : Crée un nouvel utilisateur
 */
public class UserService {
    private static Map<Integer, User> users = new HashMap<>();
    private static AtomicInteger idCounter = new AtomicInteger(1);

    public static void main(String[] args) throws IOException {
        // Initialiser quelques utilisateurs pour les tests
        users.put(1, new User(1, "Jean Dupont", "jean.dupont@email.com"));
        users.put(2, new User(2, "Marie Martin", "marie.martin@email.com"));
        idCounter.set(3);

        // Créer le serveur HTTP sur le port 8081
        HttpServer server = HttpServer.create(new InetSocketAddress(8081), 0);
        
        // Définir les routes
        server.createContext("/users", new UserHandler());
        
        server.setExecutor(null); // Utilise l'exécuteur par défaut
        server.start();
        
        System.out.println("===========================================");
        System.out.println("Service Utilisateur démarré sur le port 8081");
        System.out.println("===========================================");
        System.out.println("Endpoints disponibles:");
        System.out.println("  GET  http://localhost:8081/users");
        System.out.println("  GET  http://localhost:8081/users/{id}");
        System.out.println("  POST http://localhost:8081/users");
        System.out.println("===========================================");
    }

    static class UserHandler implements HttpHandler {
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
                } else if (method.equals("GET") && path.equals("/users")) {
                    handleGetAllUsers(exchange);
                } else if (method.equals("GET") && path.matches("/users/\\d+")) {
                    handleGetUserById(exchange, path);
                } else if (method.equals("POST") && path.equals("/users")) {
                    handleCreateUser(exchange);
                } else {
                    sendResponse(exchange, 404, "{\"error\":\"Endpoint non trouvé\"}");
                }
            } catch (Exception e) {
                e.printStackTrace();
                sendResponse(exchange, 500, "{\"error\":\"" + e.getMessage() + "\"}");
            }
        }

        private void handleGetAllUsers(HttpExchange exchange) throws IOException {
            StringBuilder json = new StringBuilder("[");
            boolean first = true;
            for (User user : users.values()) {
                if (!first) json.append(",");
                json.append(user.toJson());
                first = false;
            }
            json.append("]");
            
            sendResponse(exchange, 200, json.toString());
        }

        private void handleGetUserById(HttpExchange exchange, String path) throws IOException {
            String[] parts = path.split("/");
            int userId = Integer.parseInt(parts[2]);
            
            User user = users.get(userId);
            if (user != null) {
                sendResponse(exchange, 200, user.toJson());
            } else {
                sendResponse(exchange, 404, "{\"error\":\"Utilisateur non trouvé\"}");
            }
        }

        private void handleCreateUser(HttpExchange exchange) throws IOException {
            // Lire le corps de la requête
            InputStream is = exchange.getRequestBody();
            String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            
            // Parser le JSON manuellement (simple parsing)
            String nom = extractJsonValue(body, "nom");
            String email = extractJsonValue(body, "email");
            
            // Créer un nouvel utilisateur
            int id = idCounter.getAndIncrement();
            User user = new User(id, nom, email);
            users.put(id, user);
            
            System.out.println("Utilisateur créé: " + user.toJson());
            
            sendResponse(exchange, 201, user.toJson());
        }

        private String extractJsonValue(String json, String key) {
            String searchKey = "\"" + key + "\"";
            int startIndex = json.indexOf(searchKey);
            if (startIndex == -1) return "";
            
            startIndex = json.indexOf(":", startIndex) + 1;
            startIndex = json.indexOf("\"", startIndex) + 1;
            int endIndex = json.indexOf("\"", startIndex);
            
            return json.substring(startIndex, endIndex);
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
