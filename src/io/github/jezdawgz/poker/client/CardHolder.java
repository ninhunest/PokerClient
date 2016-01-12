/*
 * Copyright (C) 2015-2016 Jeremy Collette
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


package io.github.jezdawgz.poker.client;

import io.github.jezdawgz.poker.server.Card;
import java.awt.FlowLayout;
import javax.swing.JLabel;

/**
 * Used to display a set of {@link Card}s  
 * 
 * @author Jeremy Collette
 */
 public class CardHolder extends javax.swing.JPanel {
    
    private Card.CardSet cards;
    public JLabel lbl;

    public CardHolder()
    {
        cards = new Card.CardSet();
        this.setLayout(new FlowLayout());
        lbl = new JLabel("");
        this.add(lbl);        
    }

    public void addCard(Card c)
    {
        cards.addCard(c);
        refreshComponent();
    }
      
    public void setCards(Card[] newCards)
    {
        System.out.println(newCards[0].toString());
        this.cards = new Card.CardSet(newCards);
        refreshComponent();
    }
    
    public void clearCards()
    {
        cards.clearCards();
        refreshComponent();
    }
    
    public Card[] getCards()
    {
        return cards.getCards();
    }

    private void refreshComponent()
    {
        if (cards.getNumCards() > 0)
            lbl.setText(cards.toString());        
        else
            lbl.setText("");
       
    }    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
