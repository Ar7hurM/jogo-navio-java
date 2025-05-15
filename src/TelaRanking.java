package src;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.sql.*;

public class TelaRanking extends JFrame {

    public TelaRanking() {
        setTitle("🏆 Ranking de Jogadores");
        setSize(400, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Painel principal
        JPanel painel = new JPanel();
        painel.setBackground(new Color(240, 248, 255));
        painel.setLayout(new BorderLayout(10, 10));
        painel.setBorder(new EmptyBorder(20, 20, 20, 20));
        add(painel);

        // Título
        JLabel titulo = new JLabel("Ranking de Jogadores", JLabel.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(new Color(30, 60, 90));
        painel.add(titulo, BorderLayout.NORTH);

        // Painel para a tabela
        JPanel painelTabela = new JPanel();
        painelTabela.setLayout(new GridLayout(0, 1, 5, 5));
        painelTabela.setBackground(new Color(240, 248, 255));

        JScrollPane scrollPane = new JScrollPane(painelTabela);
        scrollPane.setBorder(null);
        painel.add(scrollPane, BorderLayout.CENTER);

        // Conexão com o banco
        try (Connection conexao = DriverManager.getConnection("jdbc:mysql://IP:3306/jogo", "user", "pass");
             Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT nome, score FROM ranking ORDER BY score DESC")) {

            int posicao = 1;
            while (rs.next()) {
                String nome = rs.getString("nome");
                int score = rs.getInt("score");

                JLabel linha = new JLabel(posicao + "º - " + nome + " - " + score + " pontos");
                linha.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                linha.setOpaque(true);
                linha.setBackground(posicao == 1 ? new Color(255, 250, 200) : Color.WHITE);
                linha.setBorder(new CompoundBorder(
                        new LineBorder(new Color(200, 200, 200)),
                        new EmptyBorder(8, 12, 8, 12)
                ));

                painelTabela.add(linha);
                posicao++;
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao acessar o banco de dados:\n" + e.getMessage());
        }

        // Botão voltar
        JButton voltar = new JButton("Voltar");
        voltar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        voltar.setBackground(new Color(200, 230, 201));
        voltar.setFocusPainted(false);
        voltar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        voltar.addActionListener(e -> dispose());

        JPanel painelBotao = new JPanel();
        painelBotao.setBackground(new Color(240, 248, 255));
        painelBotao.add(voltar);
        painel.add(painelBotao, BorderLayout.SOUTH);

        setVisible(true);
    }
}
