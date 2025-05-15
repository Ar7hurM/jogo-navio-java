package src;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class DatabaseUtil {
    private static final String URL = "jdbc:mysql://IP:3306/jogo?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USER = "user";
    private static final String PASSWORD = "pass";

    public static void salvarPontuacao(String nome, int score) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            String sql = "INSERT INTO ranking (nome, score) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, nome);
            stmt.setInt(2, score);
            stmt.executeUpdate();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
