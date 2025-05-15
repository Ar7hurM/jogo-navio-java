package src;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.Random;

public class Lixo {
    private int x;
    private int y;
    private int largura;
    private int altura;
    private BufferedImage imagem;
    private String nomeImagem;
    private boolean coletado = false;
    private boolean pontuado = false;
    public long tempoInicioColeta;
    public static final long DURACAO_ANIMACAO = 30;

    private static final String[] IMAGENS_LIXO = {"garrafa.png", "lata.png", "pneu.png", "sacola.png", "metal.png"};
    private static final Random random = new Random();
    static final int VELOCIDADE_BASE = 2; 

    public static final int LARGURA_LIXO = 40;
    public static final int ALTURA_LIXO = 40;

    public Lixo(int x, int y, int largura, int altura, String nomeImagem) {
        this.x = x;
        this.y = y;
        this.largura = largura;
        this.altura = altura;
        this.nomeImagem = (nomeImagem != null && !nomeImagem.isEmpty()) ? nomeImagem : selecionarImagemAleatoria();
        carregarImagem();
    }

    private String selecionarImagemAleatoria() {
        int indice = random.nextInt(IMAGENS_LIXO.length);
        return IMAGENS_LIXO[indice];
    }

    private void carregarImagem() {
        try {
            if (nomeImagem != null) {
                imagem = ImageIO.read(getClass().getResource("imagens/lixos/" + nomeImagem));
                if (imagem == null) {
                    System.err.println("Erro: Não foi possível carregar a imagem do lixo: " + nomeImagem);
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar a imagem do lixo: " + nomeImagem + " - " + e.getMessage());
        }
    }

    public void iniciarAnimacaoColeta() {
        coletado = true;
        tempoInicioColeta = System.currentTimeMillis();
    }

    public void setPontuado(boolean pontuado) {
        this.pontuado = pontuado;
    }

    public boolean jaFoiPontuado() {
        return pontuado;
    }

    public void paint(Graphics g, TelaJogo telaJogo) {
        if (coletado) {
            long tempoDecorrido = System.currentTimeMillis() - tempoInicioColeta;
            if (tempoDecorrido < DURACAO_ANIMACAO) {
                double fator = 1.0 - (double) tempoDecorrido / DURACAO_ANIMACAO;
                int larguraAnimada = (int) (largura * fator);
                int alturaAnimada = (int) (altura * fator);
                int xAnimado = x + (largura - larguraAnimada) / 2;
                int yAnimado = y + (altura - alturaAnimada) / 2;

                if (imagem != null) {
                    g.drawImage(imagem, xAnimado, yAnimado, larguraAnimada, alturaAnimada, telaJogo);
                } else {
                    g.setColor(Color.GREEN);
                    g.fillRect(xAnimado, yAnimado, larguraAnimada, alturaAnimada);
                }
            }
        } else {
            if (imagem != null) {
                g.drawImage(imagem, x, y, largura, altura, telaJogo);
            } else {
                g.setColor(Color.GREEN);
                g.fillRect(x, y, largura, altura);
            }
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getLargura() {
        return largura;
    }

    public int getAltura() {
        return altura;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, largura, altura);
    }

    public boolean isColetado() {
        return coletado;
    }
}