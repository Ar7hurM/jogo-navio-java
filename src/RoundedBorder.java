package src;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.BorderFactory;

@SuppressWarnings("unused")
public class RoundedBorder extends AbstractBorder {

    private int radius;
    private Border internalBorder;

    public RoundedBorder(int radius) {
        this.radius = radius;
        this.internalBorder = null;
    }

    public RoundedBorder(int radius, Border internalBorder) { 
        this.radius = radius;
        this.internalBorder = internalBorder;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.WHITE); 
        g2d.setStroke(new BasicStroke(4)); 
        RoundRectangle2D roundRect = new RoundRectangle2D.Double(x, y, width - 1, height - 1, radius, radius);
        g2d.draw(roundRect);
        g2d.dispose();
        
        if (internalBorder != null) {
            internalBorder.paintBorder(c, g, x, y, width, height);
        }
    }

    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        insets.left = insets.right = insets.top = insets.bottom = radius / 2;
        if (internalBorder != null) {
            Insets internalInsets = internalBorder.getBorderInsets(c);
            insets.left += internalInsets.left;
            insets.right += internalInsets.right;
            insets.top += internalInsets.top;
            insets.bottom += internalInsets.bottom;
        }
        return insets;
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }
}
