package com.emmanual.swing;
import com.emmanual.model.StatusType;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JLabel;

public class TableStatus extends JLabel {

    public StatusType getType() {
        return type;
    }
    
    public TableStatus() {
        setForeground(Color.WHITE);
    }

    private StatusType type;

    public void setType(StatusType type) {
        this.type = type;
        setText(type.toString());
        repaint();
    }

    @Override
    protected void paintComponent(Graphics grphcs) {
        if (type != null) {
            Graphics2D g2 = (Graphics2D) grphcs;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color c;
            if (type == StatusType.In_Progress || type.toString().equals("IN PROCESS")) {
                c = new Color(182,117,245,255);//set color to blueish color
            } else if (type == StatusType.Completed) {
                c = new Color(232,201,62,255);//set color to orangish color
            } else {
                c = new Color(135,135,248,255);//set color to purple-ish color
            }
            g2.setPaint(c);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 1, 1);
        }
        super.paintComponent(grphcs);
    }
}
