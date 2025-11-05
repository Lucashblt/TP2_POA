public class Order {
    private int id;
    private int userId;
    private int productId;
    private int quantity;
    private String status;

    public Order(int id, int userId, int productId, int quantity) {
        this.id = id;
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
        this.status = "En attente";
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public int getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String toJson() {
        return String.format("{\"id\":%d,\"userId\":%d,\"productId\":%d,\"quantity\":%d,\"status\":\"%s\"}", 
            id, userId, productId, quantity, status);
    }
}
