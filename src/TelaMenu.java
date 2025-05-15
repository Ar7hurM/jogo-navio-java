package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import javax.sound.sampled.*;
import java.net.URL;

public class TelaMenu extends JFrame {

    private JButton iniciarJogoButton;
    private JButton comoJogarButton;
    private JButton sairButton;
    private JButton opcoesButton;
    private JButton rankingButton;
    private BufferedImage imagemFundo;
    private JLabel tituloLabel;
    private Clip musicaDeFundo;
    private FloatControl volumeControl;

    public TelaMenu() {
        setTitle("Menu Principal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 768);
        setPreferredSize(new Dimension(1024, 768));
        setMaximumSize(new Dimension(1024, 768));
        setMinimumSize(new Dimension(1024, 768));
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        carregarMusicaDeFundo();
        if (musicaDeFundo != null) {
            float volumeDB = -30.0f;
            if (volumeControl != null) {
                volumeControl.setValue(volumeDB);
            }
            musicaDeFundo.loop(Clip.LOOP_CONTINUOUSLY);
        }

        try {
            imagemFundo = ImageIO.read(getClass().getResource("imagens/menu.png"));
        } catch (IOException e) {
            System.err.println("Erro ao carregar a imagem de fundo: " + e.getMessage());
            imagemFundo = null;
        }

        JPanel painelFundo = new JPanel() {
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
        painelFundo.setLayout(new BoxLayout(painelFundo, BoxLayout.Y_AXIS));
        painelFundo.setOpaque(false);
        add(painelFundo, BorderLayout.CENTER);

        tituloLabel = new JLabel("Resgate Azul - Mar Limpo");
        tituloLabel.setFont(new Font("Arial", Font.BOLD, 50));
        tituloLabel.setForeground(new Color(0, 139, 139));
        tituloLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sombraTitulo = new JLabel("Resgate Azul - Mar Limpo");
        sombraTitulo.setFont(new Font("Arial", Font.BOLD, 50));
        sombraTitulo.setForeground(Color.WHITE.darker());
        sombraTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel painelTitulo = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        painelTitulo.setOpaque(false);
        painelTitulo.add(sombraTitulo);
        painelTitulo.add(tituloLabel);

        painelFundo.add(Box.createRigidArea(new Dimension(0, 100)));
        painelFundo.add(painelTitulo);
        painelFundo.add(Box.createRigidArea(new Dimension(0, 80)));

        iniciarJogoButton = criarBotaoEstilizado("Iniciar Jogo");
        rankingButton = criarBotaoEstilizado("Ranking"); // Inicializa o botão Ranking
        comoJogarButton = criarBotaoEstilizado("Como Jogar");
        opcoesButton = criarBotaoEstilizado("Opções");
        sairButton = criarBotaoEstilizado("Sair");

        iniciarJogoButton.setPreferredSize(new Dimension(280, 70));
        iniciarJogoButton.setMaximumSize(new Dimension(280, 70));
        rankingButton.setPreferredSize(new Dimension(280, 70)); // Define o tamanho do botão Ranking
        rankingButton.setMaximumSize(new Dimension(280, 70));
        comoJogarButton.setPreferredSize(new Dimension(280, 70));
        comoJogarButton.setMaximumSize(new Dimension(280, 70));
        opcoesButton.setPreferredSize(new Dimension(280, 70));
        opcoesButton.setMaximumSize(new Dimension(280, 70));
        sairButton.setPreferredSize(new Dimension(280, 70));
        sairButton.setMaximumSize(new Dimension(280, 70));

        painelFundo.add(iniciarJogoButton);
        painelFundo.add(Box.createRigidArea(new Dimension(0, 30)));
        painelFundo.add(rankingButton); // Adiciona o botão Ranking ao painel
        painelFundo.add(Box.createRigidArea(new Dimension(0, 30)));
        painelFundo.add(comoJogarButton);
        painelFundo.add(Box.createRigidArea(new Dimension(0, 30)));
        painelFundo.add(opcoesButton);
        painelFundo.add(Box.createRigidArea(new Dimension(0, 30)));
        painelFundo.add(sairButton);
        painelFundo.add(Box.createVerticalGlue());


        iniciarJogoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (musicaDeFundo != null && musicaDeFundo.isRunning()) {
                    musicaDeFundo.stop();
                    musicaDeFundo.close();
                }
                SwingUtilities.invokeLater(() -> {
                    TelaNomeJogador telaNomeJogador = new TelaNomeJogador();
                    telaNomeJogador.setVisible(true);
                    dispose();
                });
            }
        });

        comoJogarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new TelaComoJogar(TelaMenu.this);
            }
        });

        opcoesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new TelaOpcoes(TelaMenu.this);
            }
        });

        sairButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        rankingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new TelaRanking();
        }
        });


        setVisible(true);
    }

    private void carregarMusicaDeFundo() {
        try {
            URL urlMusica = getClass().getResource("sons/musica_menu.wav");
            if (urlMusica != null) {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(urlMusica);
                musicaDeFundo = AudioSystem.getClip();
                musicaDeFundo.open(audioIn);
                volumeControl = (FloatControl) musicaDeFundo.getControl(FloatControl.Type.MASTER_GAIN);
                if (volumeControl == null) {
                    System.err.println("Controle de volume não suportado para este formato de áudio.");
                }
            } else {
                System.err.println("Arquivo de música não encontrado: /sons/musica_menu.wav");
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Erro ao carregar música de fundo: " + e.getMessage());
        }
    }

    public void setVolume(float volumeDB) {
        if (volumeControl != null) {
            volumeControl.setValue(volumeDB);
        }
    }

    public float getVolume() {
        if (volumeControl != null) {
            return volumeControl.getValue();
        }
        return -15.0f;
    }

    private JButton criarBotaoEstilizado(String texto) {
        JButton botao = new JButton(texto);
        botao.setFont(new Font("Arial", Font.BOLD, 28));
        botao.setForeground(Color.WHITE);
        botao.setFocusPainted(false);
        botao.setBorder(new RoundedBorder(20));
        botao.setContentAreaFilled(true);
        botao.setOpaque(true);
        botao.setAlignmentX(Component.CENTER_ALIGNMENT);
        botao.setPreferredSize(new Dimension(280, 70));
        botao.setMaximumSize(new Dimension(280, 70));

        Color corPrimaria = new Color(65, 105, 225);
        Color corHover = new Color(100, 149, 237);
        Color corPressionado = corPrimaria.darker();

        botao.setBackground(corPrimaria);

        botao.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                botao.setBackground(corHover);
                botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            public void mouseExited(MouseEvent evt) {
                botao.setBackground(corPrimaria);
                botao.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }

            @Override
            public void mousePressed(MouseEvent e) {
                botao.setBackground(corPressionado);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                botao.setBackground(corPrimaria);
            }
        });

        return botao;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new TelaMenu();
            }
        });
    }
}