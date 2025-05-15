package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

public class TelaNomeJogador extends JFrame {

    private JTextField nomeTextField;
    private JButton jogarButton;
    private BufferedImage imagemFundo;

    public TelaNomeJogador() {
        setTitle("Digite seu nome");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        setSize(800, 600);
        setPreferredSize(new Dimension(800, 600));
        setMinimumSize(new Dimension(800, 600));
        setMaximumSize(new Dimension(800, 600));
        setLocationRelativeTo(null); 
        setLayout(new BorderLayout());

        try {
            imagemFundo = ImageIO.read(getClass().getResource("imagens/nome.png")); 
        } catch (IOException e) {
            System.err.println("Erro ao carregar a imagem de fundo: " + e.getMessage());
            imagemFundo = null;
        }

        JPanel painelPrincipal = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (imagemFundo != null) {
                    g.drawImage(imagemFundo, 0, 0, getWidth(), getHeight(), this);
                } else {
                    g.setColor(new Color(100, 149, 237)); 
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        painelPrincipal.setLayout(new GridBagLayout()); 
        painelPrincipal.setOpaque(false); 

        JPanel container = new JPanel();
        container.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20)); 
        container.setPreferredSize(new Dimension(300, 200)); 
        container.setBackground(new Color(0, 0, 0, 100)); 
        Border roundedWhiteBorder = BorderFactory.createCompoundBorder(
                new LineBorder(Color.WHITE, 3),
                BorderFactory.createEmptyBorder(10, 10, 10, 10) 
        );
        container.setBorder(new RoundedBorder(20, roundedWhiteBorder));
        container.setOpaque(false); 

        JLabel nomeLabel = new JLabel("Nome:");
        nomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        nomeLabel.setForeground(Color.WHITE);

        nomeTextField = new JTextField(15); 
        nomeTextField.setFont(new Font("Arial", Font.PLAIN, 18));
        nomeTextField.setPreferredSize(new Dimension(150, 30));

        jogarButton = new JButton("Jogar");
        jogarButton.setFont(new Font("Arial", Font.BOLD, 20));
        jogarButton.setForeground(Color.WHITE);
        jogarButton.setBackground(new Color(65, 105, 225)); 
        jogarButton.setFocusPainted(false);
        jogarButton.setBorder(new RoundedBorder(10));
        jogarButton.setPreferredSize(new Dimension(120, 40));

        jogarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nomeJogador = nomeTextField.getText().trim();
                if (!nomeJogador.isEmpty()) {

                    TelaJogo telaJogo = new TelaJogo(nomeJogador);
                    telaJogo.setVisible(true);
                    TelaNomeJogador.this.dispose(); 
                } else {
                    JOptionPane.showMessageDialog(TelaNomeJogador.this, "Por favor, digite seu nome.", "Nome Inválido", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        container.add(nomeLabel);
        container.add(nomeTextField);
        container.add(jogarButton);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER; 

        painelPrincipal.add(container, gbc);

        add(painelPrincipal, BorderLayout.CENTER);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaNomeJogador());
    }
}

