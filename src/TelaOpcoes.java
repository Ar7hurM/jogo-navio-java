package src;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class TelaOpcoes extends JFrame {

    private JSlider volumeSlider;
    private TelaMenu telaMenu;
    private JLabel volumeLabelText;
    private JLabel volumeValueLabel;
    private BufferedImage imagemFundo;
    private JPanel painelPrincipal;

    public TelaOpcoes(TelaMenu telaMenu) {
        setTitle("Opções");
        setSize(400, 200);
        setResizable(false);
        setLocationRelativeTo(telaMenu);

        this.telaMenu = telaMenu;

        try {
            imagemFundo = ImageIO.read(getClass().getResource("imagens/opcoes.png"));
        } catch (IOException e) {
            System.err.println("Erro ao carregar a imagem de fundo das opções: " + e.getMessage());
            imagemFundo = null;
        }

        painelPrincipal = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (imagemFundo != null) {
                    g.drawImage(imagemFundo, 0, 0, getWidth(), getHeight(), this);
                } else {
                    g.setColor(new Color(200, 200, 200));
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        painelPrincipal.setLayout(new BoxLayout(painelPrincipal, BoxLayout.Y_AXIS));
        painelPrincipal.setOpaque(false);

        volumeLabelText = new JLabel("Volume da Música:");
        volumeLabelText.setFont(new Font("Arial", Font.BOLD, 16));
        volumeLabelText.setForeground(Color.BLACK);
        volumeLabelText.setAlignmentX(Component.CENTER_ALIGNMENT);

        volumeSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, (int) mapVolumeToSlider(telaMenu.getVolume()));
        volumeSlider.setMajorTickSpacing(25);
        volumeSlider.setMinorTickSpacing(5);
        volumeSlider.setPaintTicks(true);
        volumeSlider.setPaintLabels(true);
        volumeSlider.setMaximumSize(new Dimension(300, 50));
        volumeSlider.setAlignmentX(Component.CENTER_ALIGNMENT);
        volumeSlider.setOpaque(false);

        volumeValueLabel = new JLabel(String.format("%.0f %%", (float) volumeSlider.getValue())); 
        volumeValueLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        volumeValueLabel.setForeground(Color.BLACK);
        volumeValueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        volumeValueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        painelPrincipal.add(Box.createVerticalGlue());
        painelPrincipal.add(volumeLabelText);
        painelPrincipal.add(Box.createRigidArea(new Dimension(0, 10)));
        painelPrincipal.add(volumeSlider);
        painelPrincipal.add(volumeValueLabel);
        painelPrincipal.add(Box.createVerticalGlue());

        add(painelPrincipal);

        volumeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int sliderValue = volumeSlider.getValue();
                float volumeDB = mapSliderToVolume(sliderValue);
                telaMenu.setVolume(volumeDB);
                volumeValueLabel.setText(String.format("%.0f %%", (float) sliderValue));
            }
        });

        setVisible(true);
    }

    private float mapSliderToVolume(int sliderValue) {
        float minDB = -40.0f;
        float maxDB = 6.0f;
        float ratio = (float) sliderValue / 100.0f;
        return minDB + (maxDB - minDB) * ratio;
    }

    private float mapVolumeToSlider(float volumeDB) {
        float minDB = -40.0f;
        float maxDB = 6.0f;
        if (volumeDB <= minDB) return 0;
        if (volumeDB >= maxDB) return 100;
        float ratio = (volumeDB - minDB) / (maxDB - minDB);
        return ratio * 100.0f;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new TelaMenu();
            }
        });
    }
}