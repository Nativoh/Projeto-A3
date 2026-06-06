package com.controleFinanceiro.view;

import javax.swing.*;
import java.awt.*;


public class CardKPI extends JPanel {

    private final Color corFundo;
    private final Color corBorda;


    public CardKPI(Color corFundo, Color corBorda) {
        this.corFundo = corFundo;
        this.corBorda = corBorda;

        setOpaque(false);  // obrigatório para o paintComponent funcionar
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        // suaviza as bordas
        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // fundo arredondado
        g2.setColor(corFundo);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);

        // borda arredondada
        g2.setColor(corBorda);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 16, 16);

        g2.dispose();
        super.paintComponent(g);
    }
}