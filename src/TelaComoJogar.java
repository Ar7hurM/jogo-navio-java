package src;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class TelaComoJogar extends JFrame {

    private BufferedImage imagemFundo;
    private JPanel painelPrincipal;
    private JTextArea textoComoJogar;
    private JScrollPane scrollPane;

    public TelaComoJogar(TelaMenu telaMenu) {
        setTitle("Como Jogar");
        setSize(600, 400);
        setResizable(false);
        setLocationRelativeTo(telaMenu);

        try {
            imagemFundo = ImageIO.read(getClass().getResource("imagens/como.png"));
        } catch (IOException e) {
            System.err.println("Erro ao carregar a imagem de fundo de Como Jogar: " + e.getMessage());
            imagemFundo = null;
        }

        painelPrincipal = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (imagemFundo != null) {
                    g.drawImage(imagemFundo, 0, 0, getWidth(), getHeight(), this);
                } else {
                    g.setColor(new Color(255, 255, 255));
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        painelPrincipal.setLayout(new BorderLayout());
        painelPrincipal.setOpaque(false);

        textoComoJogar = new JTextArea(gerarTextoComoJogar());
        textoComoJogar.setFont(new Font("Arial", Font.PLAIN, 16));
        textoComoJogar.setForeground(Color.WHITE);
        textoComoJogar.setLineWrap(true);
        textoComoJogar.setWrapStyleWord(true);
        textoComoJogar.setEditable(false);
        textoComoJogar.setOpaque(false);
        textoComoJogar.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        scrollPane = new JScrollPane(textoComoJogar);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        painelPrincipal.add(scrollPane, BorderLayout.CENTER);
        add(painelPrincipal);

        setVisible(true);
    }

    private String gerarTextoComoJogar() {
        StringBuilder texto = new StringBuilder();
        texto.append("Navegue com o navio:\n" + //
                        "  [W] Cima\n" + //
                        "  [A] Esquerda\n" + //
                        "  [S] Baixo\n" + //
                        "  [D] Direita\n" + //
                        "\n" + //
                        "Colete o lixo:\n" + //
                        "  Passe por cima para pontuar\n" + //
                        "  e avançar de nível.\n" + //
                        "\n" + //
                        "Cuidado com o tubarão!\n" + //
                        "  Evite a colisão.\n" + //
                        "\n" + //
                        "Não sature o oceano:\n" + //
                        "  Limite de lixo(12) = Fim de Jogo.\n" + //
                        "\n" + //
                        "Seu objetivo:\n" + //
                        "  Limpar o máximo!");
        texto.append("\nBoa sorte na sua missão de limpeza oceânica!");
        return texto.toString();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaMenu()); 
    }
}