package src;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Navio {
    private int x;
    private int y;
    private int largura;
    private int altura;
    private BufferedImage imagemOriginal;
    private Image imagemRotacionada;
    private double anguloRotacao = 0;
    private Color corPadrao = Color.GREEN;

    public Navio(int x, int y, int largura, int altura) {
        this.x = x;
        this.y = y;
        this.largura = largura;
        this.altura = altura;
        try {
            imagemOriginal = ImageIO.read(getClass().getResource("imagens/navio.png"));
            if (imagemOriginal != null) {
                this.imagemRotacionada = imagemOriginal.getScaledInstance(this.largura, this.altura, Image.SCALE_SMOOTH);
                atualizarImagemRotacionada();
            } else {
                this.imagemRotacionada = null;
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar a imagem do navio: " + e.getMessage());
            this.imagemRotacionada = null;
        }
    }

    public void setAnguloRotacao(double anguloRotacao) {
        this.anguloRotacao = anguloRotacao % 360;
        atualizarImagemRotacionada();
    }

    private void atualizarImagemRotacionada() {
        if (imagemOriginal != null) {
            double radianos = Math.toRadians(anguloRotacao);
            int w = imagemOriginal.getWidth();
            int h = imagemOriginal.getHeight();
            BufferedImage rotatedImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = rotatedImage.createGraphics();
            AffineTransform transform = new AffineTransform();
            transform.rotate(radianos, w / 2.0, h / 2.0);
            g2d.drawImage(imagemOriginal, transform, null);
            g2d.dispose();
            imagemRotacionada = rotatedImage.getScaledInstance(largura, altura, Image.SCALE_SMOOTH);
        } else {
            imagemRotacionada = null;
        }
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getLargura() {
        return largura;
    }

    public int getAltura() {
        return altura;
    }

    public Image getImagem() {
        return imagemRotacionada;
    }

    public Color getCorPadrao() {
        return corPadrao;
    }
}