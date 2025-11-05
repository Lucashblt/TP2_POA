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
 * Microservice pour la gestion des produits
 * Port: 8082
 * 
 * Endpoints:
 * - GET  /products      : Liste tous les produits
 * - GET  /products/{id} : Récupère un produit par ID
 * - POST /products      : Crée un nouveau produit
 */
public class ProductService {
    private static Map<Integer, Product> products = new HashMap<>();
    private static AtomicInteger idCounter = new AtomicInteger(1);

    public static void main(String[] args) throws IOException {
        // Initialiser quelques produits pour les tests
        products.put(1, new Product(1, "Ordinateur portable", 999.99, 10));
        products.put(2, new Product(2, "Souris sans fil", 29.99, 50));
        products.put(3, new Product(3, "Clavier mécanique", 89.99, 25));
        idCounter.set(4);

        // Créer le serveur HTTP sur le port 8082
        HttpServer server = HttpServer.create(new InetSocketAddress(8082), 0);
        
        // Définir les routes
        server.createContext("/products", new ProductHandler());
        
        server.setExecutor(null);
        server.start();
        
        System.out.println("===========================================");
        System.out.println("Service Produit démarré sur le port 8082");
        System.out.println("===========================================");
        System.out.println("Endpoints disponibles:");
        System.out.println("  GET  http://localhost:8082/products");
        System.out.println("  GET  http://localhost:8082/products/{id}");
        System.out.println("  POST http://localhost:8082/products");
        System.out.println("===========================================");
    }

    static class ProductHandler implements HttpHandler {
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
                } else if (method.equals("GET") && path.equals("/products")) {
                    handleGetAllProducts(exchange);
                } else if (method.equals("GET") && path.matches("/products/\\d+")) {
                    handleGetProductById(exchange, path);
                } else if (method.equals("POST") && path.equals("/products")) {
                    handleCreateProduct(exchange);
                } else {
                    sendResponse(exchange, 404, "{\"error\":\"Endpoint non trouvé\"}");
                }
            } catch (Exception e) {
                e.printStackTrace();
                sendResponse(exchange, 500, "{\"error\":\"" + e.getMessage() + "\"}");
            }
        }

        private void handleGetAllProducts(HttpExchange exchange) throws IOException {
            StringBuilder json = new StringBuilder("[");
            boolean first = true;
            for (Product product : products.values()) {
                if (!first) json.append(",");
                json.append(product.toJson());
                first = false;
            }
            json.append("]");
            
            sendResponse(exchange, 200, json.toString());
        }

        private void handleGetProductById(HttpExchange exchange, String path) throws IOException {
            String[] parts = path.split("/");
            int productId = Integer.parseInt(parts[2]);
            
            Product product = products.get(productId);
            if (product != null) {
                sendResponse(exchange, 200, product.toJson());
            } else {
                sendResponse(exchange, 404, "{\"error\":\"Produit non trouvé\"}");
            }
        }

        private void handleCreateProduct(HttpExchange exchange) throws IOException {
            // Lire le corps de la requête
            InputStream is = exchange.getRequestBody();
            String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            
            // Parser le JSON
            String nom = extractJsonValue(body, "nom");
            double prix = Double.parseDouble(extractJsonValue(body, "prix"));
            int stock = Integer.parseInt(extractJsonValue(body, "stock"));
            
            // Créer un nouveau produit
            int id = idCounter.getAndIncrement();
            Product product = new Product(id, nom, prix, stock);
            products.put(id, product);
            
            System.out.println("Produit créé: " + product.toJson());
            
            sendResponse(exchange, 201, product.toJson());
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
