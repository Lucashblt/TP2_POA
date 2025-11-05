public class User {
    private int id;
    private String nom;
    private String email;

    public User(int id, String nom, String email) {
        this.id = id;
        this.nom = nom;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getEmail() {
        return email;
    }

    public String toJson() {
        return String.format("{\"id\":%d,\"nom\":\"%s\",\"email\":\"%s\"}", 
            id, nom, email);
    }
}
