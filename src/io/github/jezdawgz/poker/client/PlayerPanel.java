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
import java.awt.Color;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;

/**
 * Used to show a {@link Player} in the {@link PokerClient} GUI
 * 
 * @author Jeremy Collette
 */
public class PlayerPanel extends javax.swing.JPanel {
    
    private String name;
    private String title;
    private int chipsInStack;
    private int chipsOnTable;
   
    private JLabel lblChipsOnTable;    
    private JPanel foreground;    
    private JLabel nameLabel;
    private CardHolder cards;
    private JLabel titleLabel;
    private JLabel chipsLabel;
    
    
    private final Color defaultBackground;
    
    public PlayerPanel()
    {
        this("Empty");
    }
    
    public PlayerPanel(String playerName)
    {
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        
        chipsOnTable = 0;
        lblChipsOnTable = new JLabel(chipsOnTable+"");
        this.add(lblChipsOnTable);
        
        foreground = new JPanel();
        foreground.setLayout(new BoxLayout(foreground, BoxLayout.PAGE_AXIS));
        foreground.setBorder(new EtchedBorder(EtchedBorder.RAISED));
        this.add(foreground);
        
        name = playerName;
        nameLabel = new JLabel(name);
        nameLabel.setAlignmentX(0.5f);
        nameLabel.setEnabled(false);
        foreground.add(nameLabel);
        
        chipsLabel = new JLabel("");
        foreground.add(chipsLabel);
        
        titleLabel = new JLabel("");
        foreground.add(titleLabel);
                
        cards = new CardHolder();
        foreground.add(cards);

        defaultBackground = getBackground();
    }
    
    public void reset()
    {
        cards.clearCards();
        setChipsOnTable(0);
        setTitle("");
    }
    
    public void setRaisePlayer(boolean raise)
    {
        if (raise)
        {
            foreground.setBorder(new BevelBorder(BevelBorder.RAISED, Color.white, Color.black));
        }
        else
        {
            foreground.setBorder(new EtchedBorder(EtchedBorder.RAISED));
        }
    }
    
    public void setHighlightPlayer(boolean highlight)
    {
        if (highlight)
        {
           foreground.setBackground(Color.red);
           nameLabel.setForeground(Color.white);
           chipsLabel.setForeground(Color.white);
           titleLabel.setForeground(Color.white);
        }
        else
        {
            foreground.setBackground(defaultBackground);
            nameLabel.setForeground(Color.black);
            chipsLabel.setForeground(Color.black);
            titleLabel.setForeground(Color.black);
        }
        
    }
    
    public void setTitle(String title)
    {
        this.title = title;
        titleLabel.setText(title);
    }
    
    public void addChipsToStack(int chips)
    {
        setChipsInStack(this.chipsInStack+chips);
    }
    
    public int getChipsInStack()
    {
        return chipsInStack;
    }
    
    public void setChipsInStack(int chips)
    {
        this.chipsInStack = chips;
        chipsLabel.setText(chips+"");
    }
    
    public void setChipsOnTable(int chips)
    {
        chipsOnTable = chips;
        lblChipsOnTable.setText(chipsOnTable > 0 ? (chipsOnTable+"") : "");
    }
    
    public int getChipsOnTable()
    {
        return chipsOnTable;
    }
    
    public int getTotalChips()
    {
        return chipsInStack + chipsOnTable;
    }
    
    public void setPlayerName(String newName)
    {
        name = newName;
        nameLabel.setEnabled(true);
        nameLabel.setText(name);
    }
    
    public String getPlayerName()
    {
        return name;
    }
    
    public void addCard(Card c)
    {
        cards.addCard(c);
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
