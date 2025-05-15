package src;

import javax.sound.sampled.*;
import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class TelaJogo extends JFrame implements KeyListener {

    private String nomeJogador;
    private Navio navio;
    private List<Lixo> lixos;
    private int pontuacao;
    private JLabel pontuacaoLabel;
    private Random random = new Random();
    private Timer timerGeracaoLixo;
    private Image offScreenImage;
    private Graphics offScreenGraphics;
    private Image fundoAnimado;
    private int nivel = 1;
    private JLabel nivelLabel;
    private int velocidadeNavio = 10;
    private Clip somColeta;
    private Clip musicaDeFundoMar;
    private FloatControl volumeControlMusicaFundo;
    private FloatControl volumeControlSomColeta;
    private JLabel avisoLixoLabel;
    private Image imagemTubarãoEsquerda;
    private Image imagemTubarãoDireita;
    private Image imagemTubarãoCima;
    private Image imagemTubarãoBaixo;
    private Image imagemTubarãoAtual;
    private int tubarãoX;
    private int tubarãoY;
    private int velocidadeTubarãoInicial = 3;
    private int velocidadeTubarão;
    private boolean tubarãoAtivo = false;
    private boolean jogoAtivo = true;
    private static final int MAX_LIXO_PERMITIDO = 12;
    private static final int INTERVALO_INICIAL_GERACAO_LIXO = 4000;
    private static final int LARGURA_TELA_ORIGINAL = 1366;
    private static final int ALTURA_TELA_ORIGINAL = 768;
    private static final int TAMANHO_NAVIO = 80;
    public static final int TAMANHO_LIXO = 40;
    private static final int TEMPO_DE_VIDA_LIXO = 6000;
    private static final int OBJETIVO_PONTOS_FASE1 = 50;
    private static final int BORDA_SUPERIOR = 50;
    private static final int PONTOS_NIVEL_2 = 100;
    private static final int PONTOS_NIVEL_3 = 200;
    private static final int PONTOS_NIVEL_4 = 350;
    private static final int PONTOS_NIVEL_5 = 550;
    private static final int PONTOS_NIVEL_6 = 700;
    private static final int PONTOS_NIVEL_7 = 1000;
    private static final int PONTOS_NIVEL_8 = 1200;
    private static final int PONTOS_NIVEL_9 = 1550;
    private static final int PONTOS_NIVEL_10 = 2000;

    private long startTime = 0;
    private Timer gameTimer;
    private String tempoFormatado = "00:00";

    public TelaJogo(String nomeJogador) {
        this.nomeJogador = nomeJogador;

        setTitle("Jogo de Limpeza Oceânica - Jogador: " + nomeJogador);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(LARGURA_TELA_ORIGINAL, ALTURA_TELA_ORIGINAL);
        setPreferredSize(new Dimension(LARGURA_TELA_ORIGINAL, ALTURA_TELA_ORIGINAL));
        setMaximumSize(new Dimension(LARGURA_TELA_ORIGINAL, ALTURA_TELA_ORIGINAL));
        setMinimumSize(new Dimension(LARGURA_TELA_ORIGINAL, ALTURA_TELA_ORIGINAL));
        setResizable(true);
        setLayout(null);

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        fundoAnimado = toolkit.getImage(getClass().getResource("imagens/fundo_animado/oceano.gif"));
        imagemTubarãoEsquerda = toolkit.getImage(getClass().getResource("imagens/tubarao_esquerda.png"));
        imagemTubarãoDireita = toolkit.getImage(getClass().getResource("imagens/tubarao_direita.png"));
        imagemTubarãoCima = toolkit.getImage(getClass().getResource("imagens/tubarao_cima.png"));
        imagemTubarãoBaixo = toolkit.getImage(getClass().getResource("imagens/tubarao_baixo.png"));
        imagemTubarãoAtual = imagemTubarãoEsquerda;

        inicializarJogo();

        pontuacaoLabel = new JLabel("Pontuação: " + pontuacao);
        pontuacaoLabel.setForeground(Color.BLACK);
        nivelLabel = new JLabel("Nível: " + nivel);
        nivelLabel.setForeground(Color.BLACK);
        avisoLixoLabel = new JLabel("Lixo: 0/" + MAX_LIXO_PERMITIDO);
        avisoLixoLabel.setForeground(Color.RED);

        add(pontuacaoLabel);
        add(nivelLabel);
        add(avisoLixoLabel);

        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);

        timerGeracaoLixo = new Timer(INTERVALO_INICIAL_GERACAO_LIXO, e -> {
            if (jogoAtivo) {
                gerarLixo();
                if (!tubarãoAtivo && !lixos.isEmpty()) {
                    tubarãoAtivo = true;
                    tubarãoX = navio.getX() - 200;
                    tubarãoY = navio.getY();
                    startTime = System.currentTimeMillis();
                    startGameTimer();
                }
                atualizarAvisoLixo();
            }
        });
        timerGeracaoLixo.start();

        Timer timerTubarão = new Timer(50, e -> {
            if (jogoAtivo && tubarãoAtivo) {
                atualizarPosiçãoTubarão();
                verificarColisãoTubarão();
            }
            repaint();
        });
        timerTubarão.start();

        carregarSomColeta();
        carregarMusicaDeFundoMar();

        setLocationRelativeTo(null);
        setVisible(true);

        SwingUtilities.invokeLater(() -> updateUI());

        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent evt) {
                updateUI();
            }
        });
    }

    public TelaJogo() {
        this("Jogador Padrão");
    }

    private void inicializarJogo() {
        navio = new Navio(LARGURA_TELA_ORIGINAL / 2 - TAMANHO_NAVIO / 2, ALTURA_TELA_ORIGINAL - TAMANHO_NAVIO - BORDA_SUPERIOR - 10, TAMANHO_NAVIO, TAMANHO_NAVIO);
        lixos = new ArrayList<>();
        pontuacao = 0;
        nivel = 1;
        velocidadeNavio = 10;
        velocidadeTubarão = velocidadeTubarãoInicial;
        tubarãoAtivo = false;
        jogoAtivo = true;
        tempoFormatado = "Tempo: 00:00";
        startTime = 0;
        if (gameTimer != null) {
            gameTimer.stop();
            gameTimer = null;
        }
        if (timerGeracaoLixo != null) {
            timerGeracaoLixo.start();
            timerGeracaoLixo.setDelay(INTERVALO_INICIAL_GERACAO_LIXO);
        }
        if (pontuacaoLabel != null) pontuacaoLabel.setText("Pontuação: " + pontuacao);
        if (nivelLabel != null) nivelLabel.setText("Nível: " + nivel);
        atualizarAvisoLixo();
    }

    private void startGameTimer() {
        gameTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (jogoAtivo && tubarãoAtivo) {
                    long elapsedTime = System.currentTimeMillis() - startTime;
                    long minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedTime);
                    long seconds = TimeUnit.MILLISECONDS.toSeconds(elapsedTime) - TimeUnit.MINUTES.toSeconds(minutes);
                    tempoFormatado = String.format("Tempo: %02d:%02d", minutes, seconds);
                    TelaJogo.this.repaint();
                } else {
                    if (gameTimer != null) gameTimer.stop();
                }
            }
        });
        gameTimer.start();
    }

private void mostrarOpcoesFimDeJogo(String mensagem) {
    jogoAtivo = false;
    timerGeracaoLixo.stop();
    if (gameTimer != null) gameTimer.stop();

    DatabaseUtil.salvarPontuacao(nomeJogador, pontuacao);

    Object[] options = {"Jogar Novamente", "Menu"};
    int escolha = JOptionPane.showOptionDialog(this,
            mensagem,
            "Fim de Jogo",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]);

    if (escolha == JOptionPane.YES_OPTION) {
        inicializarJogo();
        setTitle("Jogo de Limpeza Oceânica - Jogador: " + nomeJogador);
        jogoAtivo = true;
        timerGeracaoLixo.start();
        if (tubarãoAtivo && startTime > 0) {
            startGameTimer();
        } else if (!tubarãoAtivo && !lixos.isEmpty()) {
            startTime = System.currentTimeMillis();
            startGameTimer();
        }
    } else if (escolha == JOptionPane.NO_OPTION) {
        SwingUtilities.invokeLater(() -> {
            TelaMenu telaMenu = new TelaMenu();
            telaMenu.setVisible(true);
        });
        dispose();
    }
}

    private void verificarColisãoTubarão() {
        if (imagemTubarãoAtual != null && tubarãoAtivo) {
            Rectangle tubarãoRetangulo = new Rectangle(tubarãoX, tubarãoY, 70, 60);
            Rectangle navioRetangulo = new Rectangle(navio.getX(), navio.getY(), navio.getLargura(), navio.getAltura());

            if (tubarãoRetangulo.intersects(navioRetangulo) && jogoAtivo) {
                mostrarOpcoesFimDeJogo("O tubarão te pegou! Fim de Jogo.");
            }
        }
    }

    private void atualizarPosiçãoTubarão() {
        if (imagemTubarãoAtual != null && jogoAtivo && tubarãoAtivo) {
            int deltaX = navio.getX() - tubarãoX;
            int deltaY = navio.getY() - tubarãoY;

            if (Math.abs(deltaX) > Math.abs(deltaY)) {
                if (deltaX > 0) {
                    imagemTubarãoAtual = imagemTubarãoDireita;
                } else {
                    imagemTubarãoAtual = imagemTubarãoEsquerda;
                }
                tubarãoX += velocidadeTubarão * Math.signum(deltaX);
            } else if (Math.abs(deltaY) > 0) {
                if (deltaY > 0) {
                    imagemTubarãoAtual = imagemTubarãoBaixo;
                } else {
                    imagemTubarãoAtual = imagemTubarãoCima;
                }
                tubarãoY += velocidadeTubarão * Math.signum(deltaY);
            }
        }
    }

    private void carregarSomColeta() {
        try {
            URL urlSom = getClass().getResource("sons/som_coleta.wav");
            if (urlSom != null) {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(urlSom);
                somColeta = AudioSystem.getClip();
                somColeta.open(audioIn);
                volumeControlSomColeta = (FloatControl) somColeta.getControl(FloatControl.Type.MASTER_GAIN);
                if (volumeControlSomColeta != null) {
                    float volume = -30.0f;
                    volumeControlSomColeta.setValue(volume);
                }
            } else {
                System.err.println("Arquivo de som de coleta não encontrado.");
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Erro ao carregar som de coleta: " + e.getMessage());
        }
    }

    private void tocarSomColeta() {
        if (somColeta != null) {
            somColeta.setFramePosition(0);
            somColeta.start();
        }
    }

    private void carregarMusicaDeFundoMar() {
        try {
            URL urlMusica = getClass().getResource("sons/som_mar.wav");
            if (urlMusica != null) {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(urlMusica);
                musicaDeFundoMar = AudioSystem.getClip();
                musicaDeFundoMar.open(audioIn);
                volumeControlMusicaFundo = (FloatControl) musicaDeFundoMar.getControl(FloatControl.Type.MASTER_GAIN);
                float volume = -25.0f;
                if (volumeControlMusicaFundo != null) {
                    volumeControlMusicaFundo.setValue(volume);
                }
                musicaDeFundoMar.loop(Clip.LOOP_CONTINUOUSLY);
            } else {
                System.err.println("Arquivo de música de fundo do mar não encontrado.");
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Erro ao carregar música de fundo do mar: " + e.getMessage());
        }
    }

    private void updateUI() {
        int alturaBarraTitulo = getInsets().top;
        pontuacaoLabel.setBounds(10, 10 + alturaBarraTitulo, 150, 20);
        nivelLabel.setBounds(10, 40 + alturaBarraTitulo, 100, 20);
        avisoLixoLabel.setBounds(getWidth() - 150, 10 + alturaBarraTitulo, 140, 20);
    }

    private void gerarLixo() {
        if (jogoAtivo) {
            int larguraJanela = getWidth();
            int alturaJanela = getHeight();
            int alturaBarraTitulo = getInsets().top;

            int x = random.nextInt(larguraJanela - Lixo.LARGURA_LIXO);
            int y = random.nextInt(alturaJanela - alturaBarraTitulo - Lixo.ALTURA_LIXO) + alturaBarraTitulo;

            final Lixo novoLixo = new Lixo(x, y, Lixo.LARGURA_LIXO, Lixo.ALTURA_LIXO, null);
            lixos.add(novoLixo);
            atualizarAvisoLixo();
            repaint();

            final Lixo lixoParaRemover = novoLixo;
            Timer timerRemoverLixo = new Timer(TEMPO_DE_VIDA_LIXO, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    lixos.remove(lixoParaRemover);
                    atualizarAvisoLixo();
                    repaint();
                }
            });
            timerRemoverLixo.setRepeats(false);
            timerRemoverLixo.start();

            int novoDelay = (int) (INTERVALO_INICIAL_GERACAO_LIXO * Math.pow(0.9, pontuacao / 10.0));
            if (novoDelay < 500) {
                novoDelay = 500;
            }
            timerGeracaoLixo.setDelay(novoDelay);
        }
    }

    private void atualizarAvisoLixo() {
        if (avisoLixoLabel != null) {
            avisoLixoLabel.setText("Lixo: " + lixos.size() + "/" + MAX_LIXO_PERMITIDO);
        }
    }

    private void verificarColisoes() {
        if (jogoAtivo) {
            Rectangle navioRect = new Rectangle(navio.getX(), navio.getY(), navio.getLargura(), navio.getAltura());

            Iterator<Lixo> iterator = lixos.iterator();
            while (iterator.hasNext()) {
                Lixo lixo = iterator.next();
                Rectangle lixoRect = lixo.getBounds();
                if (navioRect.intersects(lixoRect)) {
                    lixo.iniciarAnimacaoColeta();
                    if (!lixo.jaFoiPontuado()) {
                        pontuacao += 5;
                        pontuacaoLabel.setText("Pontuação: " + pontuacao);
                        lixo.setPontuado(true);
                        verificarVitoria();
                        tocarSomColeta();
                        iterator.remove();
                        atualizarAvisoLixo();

                        int novoNivel = calcularNivel(pontuacao);
                        if (novoNivel > nivel) {
                            nivel = novoNivel;
                            nivelLabel.setText("Nível: " + nivel);
                            JOptionPane.showMessageDialog(this, "Você alcançou o Nível " + nivel + "!", "Novo Nível!", JOptionPane.INFORMATION_MESSAGE);
                            velocidadeNavio = Math.min(20, velocidadeNavio + 1);
                            aumentarVelocidadeTubarão();
                        }
                    }
                }
            }
            if (lixos.size() >= MAX_LIXO_PERMITIDO) {
                mostrarOpcoesFimDeJogo("Muitos detritos! O oceano está sobrecarregado. Fim de Jogo.");
            }
            repaint();
        }
    }

    private void aumentarVelocidadeTubarão() {
        velocidadeTubarão = velocidadeTubarãoInicial + (nivel - 1);
    }

    private int calcularNivel(int pontos) {
        if (pontos >= PONTOS_NIVEL_10) return 10;
        if (pontos >= PONTOS_NIVEL_9) return 9;
        if (pontos >= PONTOS_NIVEL_8) return 8;
        if (pontos >= PONTOS_NIVEL_7) return 7;
        if (pontos >= PONTOS_NIVEL_6) return 6;
        if (pontos >= PONTOS_NIVEL_5) return 5;
        if (pontos >= PONTOS_NIVEL_4) return 4;
        if (pontos >= PONTOS_NIVEL_3) return 3;
        if (pontos >= PONTOS_NIVEL_2) return 2;
        return 1;
    }

    private void verificarVitoria() {
        if (jogoAtivo) {
            int objetivoProximaFase = 0;
            switch (nivel) {
                case 1:
                    objetivoProximaFase = PONTOS_NIVEL_2;
                    break;
                case 2:
                    objetivoProximaFase = PONTOS_NIVEL_3;
                    break;
                case 3:
                    objetivoProximaFase = PONTOS_NIVEL_4;
                    break;
                case 4:
                    objetivoProximaFase = PONTOS_NIVEL_5;
                    break;
                case 5:
                    objetivoProximaFase = PONTOS_NIVEL_6;
                    break;
                case 6:
                    objetivoProximaFase = PONTOS_NIVEL_7;
                    break;
                case 7:
                    objetivoProximaFase = PONTOS_NIVEL_8;
                    break;
                case 8:
                    objetivoProximaFase = PONTOS_NIVEL_9;
                    break;
                case 9:
                    objetivoProximaFase = PONTOS_NIVEL_10;
                    break;
                case 10:
                    objetivoProximaFase = Integer.MAX_VALUE;
                    break;
                default:
                    objetivoProximaFase = nivel * 250;
                    break;
            }

            if (pontuacao >= objetivoProximaFase) {
                if (nivel < 10) {
                    nivel++;
                    nivelLabel.setText("Nível: " + nivel);
                    JOptionPane.showMessageDialog(this, "Você alcançou o Nível " + nivel + "!", "Novo Nível!", JOptionPane.INFORMATION_MESSAGE);
                    timerGeracaoLixo.setDelay(Math.max(500, (int) (INTERVALO_INICIAL_GERACAO_LIXO * Math.pow(0.9, nivel))));
                    velocidadeNavio = Math.min(20, velocidadeNavio + 1);
                    aumentarVelocidadeTubarão();
                } else if (nivel == 10) {
                    mostrarOpcoesFimDeJogo("Parabéns! Você limpou o oceano e superou todos os desafios!");
                }
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        int larguraJanela = getWidth();
        int alturaJanela = getHeight();
        double proporcaoJanela = (double) larguraJanela / alturaJanela;
        double proporcaoOriginal = (double) LARGURA_TELA_ORIGINAL / ALTURA_TELA_ORIGINAL;

        int larguraDesenho;
        int alturaDesenho;
        int xOffset = 0;
        int yOffset = 0;

        if (proporcaoJanela > proporcaoOriginal) {
            alturaDesenho = alturaJanela;
            larguraDesenho = (int) (alturaJanela * proporcaoOriginal);
            xOffset = (larguraJanela - larguraDesenho) / 2;
        } else {
            larguraDesenho = larguraJanela;
            alturaDesenho = (int) (larguraJanela / proporcaoOriginal);
            yOffset = (alturaJanela - alturaDesenho) / 2;
        }

        if (offScreenImage == null || offScreenImage.getWidth(this) != larguraDesenho || offScreenImage.getHeight(this) != alturaDesenho) {
            offScreenImage = createImage(larguraDesenho, alturaDesenho);
            offScreenGraphics = offScreenImage.getGraphics();
            ((Graphics2D) offScreenGraphics).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            ((Graphics2D) offScreenGraphics).scale((double) larguraDesenho / LARGURA_TELA_ORIGINAL, (double) alturaDesenho / ALTURA_TELA_ORIGINAL);
        }

        offScreenGraphics.clearRect(0, 0, LARGURA_TELA_ORIGINAL, ALTURA_TELA_ORIGINAL);

        if (fundoAnimado != null) {
            offScreenGraphics.drawImage(fundoAnimado, 0, 0, LARGURA_TELA_ORIGINAL, ALTURA_TELA_ORIGINAL, this);
        } else {
            offScreenGraphics.setColor(Color.CYAN);
            offScreenGraphics.fillRect(0, 0, LARGURA_TELA_ORIGINAL, ALTURA_TELA_ORIGINAL);
            offScreenGraphics.setColor(Color.CYAN);
            offScreenGraphics.fillRect(0, BORDA_SUPERIOR, LARGURA_TELA_ORIGINAL, ALTURA_TELA_ORIGINAL - BORDA_SUPERIOR);
        }

        if (navio.getImagem() != null) {
            offScreenGraphics.drawImage(navio.getImagem(), navio.getX(), navio.getY(), navio.getLargura(), navio.getAltura(), this);
        } else {
            offScreenGraphics.setColor(navio.getCorPadrao());
            offScreenGraphics.fillRect(navio.getX(), navio.getY(), navio.getLargura(), navio.getAltura());
        }

        if (imagemTubarãoAtual != null && tubarãoAtivo) {
            offScreenGraphics.drawImage(imagemTubarãoAtual, tubarãoX, tubarãoY, 100, 60, this);
        }

        Iterator<Lixo> iteratorLixoPaint = lixos.iterator();
        while (iteratorLixoPaint.hasNext()) {
            Lixo lixo = iteratorLixoPaint.next();
            lixo.paint(offScreenGraphics, this);
            if (lixo.isColetado() && (System.currentTimeMillis() - lixo.tempoInicioColeta > Lixo.DURACAO_ANIMACAO)) {
                iteratorLixoPaint.remove();
            }
            if (lixo.getY() > getHeight()) {
                iteratorLixoPaint.remove();
            }
        }

        Graphics2D g2d = (Graphics2D) offScreenGraphics;
        g2d.setFont(new Font("Arial", Font.BOLD, 24));

        g2d.setColor(Color.BLACK);
        g2d.drawString("Nível: " + nivel, 11, 61);
        g2d.setColor(Color.YELLOW);
        g2d.drawString("Nível: " + nivel, 10, 60);

        g2d.setColor(Color.BLACK);
        g2d.drawString("Score: " + pontuacao, 11, 91);
        g2d.setColor(new Color(0, 200, 0));
        g2d.drawString("Score: " + pontuacao, 10, 90);

        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        //g.drawString(tempoFormatado, getWidth() / 2 - g.getFontMetrics().stringWidth(tempoFormatado) / 2, 30 + getInsets().top);

        g.setColor(Color.RED);
        //g.drawString(avisoLixoLabel.getText(), getWidth() - 160, 30 + getInsets().top);

        g.drawImage(offScreenImage, xOffset, yOffset, larguraDesenho, alturaDesenho, this);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (jogoAtivo) {
            int keyCode = e.getKeyCode();
            int velocidade = velocidadeNavio;

            int novoX = navio.getX();
            int novoY = navio.getY();

            switch (keyCode) {
                case KeyEvent.VK_W:
                    novoY = Math.max(BORDA_SUPERIOR, navio.getY() - velocidade);
                    navio.setAnguloRotacao(0);
                    break;
                case KeyEvent.VK_S:
                    novoY = Math.min(ALTURA_TELA_ORIGINAL - navio.getAltura(), navio.getY() + velocidade);
                    navio.setAnguloRotacao(180);
                    break;
                case KeyEvent.VK_D:
                    novoX = Math.min(LARGURA_TELA_ORIGINAL - navio.getLargura(), navio.getX() + velocidade);
                    navio.setAnguloRotacao(90);
                    break;
                case KeyEvent.VK_A:
                    novoX = Math.max(0, navio.getX() - velocidade);
                    navio.setAnguloRotacao(-90);
                    break;
            }
            navio.setX(novoX);
            navio.setY(novoY);
            verificarColisoes();
            repaint();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}