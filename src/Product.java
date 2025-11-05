public class Product {
    private int id;
    private String nom;
    private double prix;
    private int stock;

    public Product(int id, String nom, double prix, int stock) {
        this.id = id;
        this.nom = nom;
        this.prix = prix;
        this.stock = stock;
    }

    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public double getPrix() {
        return prix;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String toJson() {
        return String.format("{\"id\":%d,\"nom\":\"%s\",\"prix\":%.2f,\"stock\":%d}", 
            id, nom, prix, stock);
    }
}
