/*
 * Copyright (C) 2015 Jeremy Collette
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
import io.github.jezdawgz.poker.server.ClientCommand;
import io.github.jezdawgz.poker.server.Card;
import io.github.jezdawgz.poker.server.Event;
import io.github.jezdawgz.poker.server.Event.*;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.text.DefaultCaret;

/**
 * The main GUI of the {@link PokerClient} - represents a poker table showing each {@link Player} in the {@link Game} 
 * 
 * @author Jeremy Collette
 */
public class GameScreen extends javax.swing.JFrame {

    private final PlayerPanel[] PLAYER_PANELS;
    private final JButton[] PLAYER_BUTTONS;
    private int playerIndex;
    private int toActIndex;
    private final LinkedList<Event> eventsToSend;
    private static final SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
    
        
    public GameScreen()
    {
        initComponents();
        setLocationRelativeTo(null);
        
        PLAYER_PANELS = new PlayerPanel[]{playerPanel1, playerPanel2, playerPanel3, playerPanel4, playerPanel5, playerPanel6, playerPanel7, playerPanel8};
        PLAYER_BUTTONS = new JButton[]{btnFold, btnCheck, btnCall, btnBet, btnAllIn};
        setButtonsEnabled(false);        
        toActIndex = -1;        
        eventsToSend = new LinkedList<>();
       
        /* set log to auto-scroll */
        DefaultCaret caret = (DefaultCaret) txtLog.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
    }
   
    
    public Event getNextClientEvent()
    {
        return eventsToSend.poll();
    }
    
    private void setButtonsEnabled(boolean enabled)
    {
        for (JButton b : PLAYER_BUTTONS)
        {
            b.setEnabled(enabled);
        }
    }
          
    private void playerFolded(int index)
    {
        PLAYER_PANELS[index].setTitle("FOLDED");
        addToLog(PLAYER_PANELS[index].getPlayerName()+" folded!");
    }
    
    private void playerDisconnected(int index)
    {
        PLAYER_PANELS[index].setTitle("DISCONNECTED");
        PLAYER_PANELS[index].setChipsInStack(0);
        addToLog(PLAYER_PANELS[index].getPlayerName()+" disconnected!");
    }
    
    private void sendCommandToServer(ClientCommand c)
    {
        eventsToSend.add(c);
    }
    
    private void playerClickedFoldButton()
    {
        sendCommandToServer(new ClientCommand.ClientFoldCommand());
    }
    
    private void playerClickedCheckButton()
    {
        sendCommandToServer(new ClientCommand.ClientCheckCommand());
    }
    
    private void playerClickedBetButton()
    {
        sendCommandToServer(new ClientCommand.ClientBetCommand((int)spnBet.getValue()));
    }
    
    private void playerClickedCallButton()
    {
        sendCommandToServer(new ClientCommand.ClientCallCommand());
    }
    
   
    private void newPlayerTurn(int index)
    {
        if (toActIndex >= 0)
        {
            PLAYER_PANELS[toActIndex].setRaisePlayer(false);
        }
        toActIndex = index;
        PLAYER_PANELS[toActIndex].setRaisePlayer(true);
        
        addToLog("The action is on "+PLAYER_PANELS[toActIndex].getPlayerName());
        
        if(toActIndex == playerIndex)
        {
            setButtonsEnabled(true);
            java.awt.Toolkit.getDefaultToolkit().beep();
        }
        else
        {
            setButtonsEnabled(false);
        }
    }
    
    private void setPotAmount(int pot)
    {
        this.lblPot.setText("Pot: "+pot);
    }
    
    private void addToLog(String toLog)
    {
        txtLog.append("["+df.format(new Date())+"]\t"+toLog+"\n");
    }
    
    public void receiveEvent(Event e)
    {
        if (NewGameEvent.class.isInstance(e))        
        {
            NewGameEvent evt = (NewGameEvent)e;       
            playerIndex = evt.clientIndex;
            
            for (int i = 0; i < PLAYER_PANELS.length && i < evt.playerNames.length; i++)
            {
                PLAYER_PANELS[i].reset();
                PLAYER_PANELS[i].setPlayerName(evt.playerNames[i]);
                PLAYER_PANELS[i].setChipsInStack(evt.startingStack);
            }
            PLAYER_PANELS[playerIndex].setTitle("(You)");
            PLAYER_PANELS[playerIndex].setHighlightPlayer(true);
            
            communityCards.clearCards();
            
            lblStartingStack.setText(lblStartingStack.getText()+" "+evt.startingStack);
            lblBlinds.setText(lblBlinds.getText() + evt.smallBlind+" / "+evt.bigBlind);
        }
        
        else if (NewMessageEvent.class.isInstance(e))
        {
            System.out.println("Recieved from server: "+((NewMessageEvent)e).message);
            addToLog(((NewMessageEvent)e).message);
        }
        
        else if (NewRoundEvent.class.isInstance(e))
        {            
            addToLog("** New round! **");
            
            NewRoundEvent evt = (NewRoundEvent)e;
            for (PlayerPanel p : PLAYER_PANELS)
            {
                p.reset();
            }
            
            communityCards.clearCards();
            
            PLAYER_PANELS[evt.dealerIndex].setTitle("(D)");
            java.awt.Toolkit.getDefaultToolkit().beep();
        }

        else if (NewPlayerCardEvent.class.isInstance(e))
        {          
            Card c = ((NewPlayerCardEvent)e).newCard;
            PLAYER_PANELS[playerIndex].addCard(c);
            addToLog("You were dealt the card "+c.toString());
        }
        
        else if (NewCommunityCardEvent.class.isInstance(e))
        {
            Card c = ((Event.NewCommunityCardEvent)e).newCard;
            communityCards.addCard(c);
                        
            for (PlayerPanel p : PLAYER_PANELS)
            {
                p.setChipsOnTable(0);
            }
            
            addToLog("The community card "+c.toString()+" was dealt.");
        }

        else if (PlayerTurnEvent.class.isInstance(e))
        {
            newPlayerTurn(((PlayerTurnEvent)e).playerIndex);
        }
        
        else if (PlayerFoldEvent.class.isInstance(e))
        {
            playerFolded(((PlayerFoldEvent)e).playerIndex);
        }        
        
        else if (PlayerChipsOnTableEvent.class.isInstance(e))
        {
            PlayerChipsOnTableEvent evt = (PlayerChipsOnTableEvent)e;
            PLAYER_PANELS[evt.playerIndex].setChipsOnTable(evt.amount);
            PLAYER_PANELS[evt.playerIndex].setChipsInStack(evt.chipsRemaining);
            addToLog(PLAYER_PANELS[evt.playerIndex].getPlayerName() +" put "+evt.amount+" chips on the table!");
            setPotAmount(evt.totalPot);
            
            
            int allIn = PLAYER_PANELS[playerIndex].getTotalChips();
            int newBet = (int)spnBet.getValue();
            
            if (PlayerPostBlindsEvent.class.isInstance(e))
            {
                /* If the player has posted blinds - the min bet is double the blind */
                newBet = evt.amount*2;
            }
            else if (PlayerBetEvent.class.isInstance(e))
            {
                /* If the player has made a bet - the min total bet is calculated server side */
                newBet = ((PlayerBetEvent)e).minTotalBet;
            }
            
            /* Set the min bet to either the new minimum bet or the player's total chips (if the bet is greater than their number of chips) */
            spnBet.setValue(newBet < allIn ? newBet : allIn);
        }
        
        else if (PlayerCheckEvent.class.isInstance(e))
        {
            addToLog(PLAYER_PANELS[((PlayerCheckEvent)e).playerIndex].getPlayerName()+" checked.");
        }
        
        else if (PlayerWinsChipsEvent.class.isInstance(e))
        {
            PlayerWinsChipsEvent evt = (PlayerWinsChipsEvent)e;
            PLAYER_PANELS[evt.playerIndex].addChipsToStack(evt.amountWon);
            setPotAmount(evt.remainingPot);
            
            addToLog(PLAYER_PANELS[evt.playerIndex].getPlayerName()+" wins "+evt.amountWon+" chips.");
            
            if (evt.remainingPot == 0)
            {
                addToLog("Hand complete.");
            }
        }
                
        else if (GameOverEvent.class.isInstance(e))
        {
            GameOverEvent evt = (GameOverEvent)e;
            addToLog("Game over!");
            addToLog(PLAYER_PANELS[evt.winnerIndex].getPlayerName()+" wins!");
            JOptionPane.showMessageDialog(null, "Game over!\n"+PLAYER_PANELS[evt.winnerIndex].getPlayerName()+" wins!");
            dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        }
        
        else if (PlayerDisconnectEvent.class.isInstance(e))
        {
            PlayerDisconnectEvent evt = (PlayerDisconnectEvent)e;
            playerDisconnected(evt.playerIndex);
        }
               
        
        else 
        {
            System.out.println("Unhandled event: "+e.getClass().getCanonicalName());
        }
            
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        playerPanel2 = new io.github.jezdawgz.poker.client.PlayerPanel();
        communityCards = new io.github.jezdawgz.poker.client.CardHolder();
        lblBlinds = new javax.swing.JLabel();
        lblStartingStack = new javax.swing.JLabel();
        playerPanel3 = new io.github.jezdawgz.poker.client.PlayerPanel();
        playerPanel4 = new io.github.jezdawgz.poker.client.PlayerPanel();
        playerPanel5 = new io.github.jezdawgz.poker.client.PlayerPanel();
        playerPanel6 = new io.github.jezdawgz.poker.client.PlayerPanel();
        playerPanel7 = new io.github.jezdawgz.poker.client.PlayerPanel();
        playerPanel8 = new io.github.jezdawgz.poker.client.PlayerPanel();
        btnFold = new javax.swing.JButton();
        btnCheck = new javax.swing.JButton();
        btnBet = new javax.swing.JButton();
        lblPot = new javax.swing.JLabel();
        spnBet = new javax.swing.JSpinner();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtLog = new javax.swing.JTextArea();
        btnCall = new javax.swing.JButton();
        btnAllIn = new javax.swing.JButton();
        lblTitle = new javax.swing.JLabel();
        playerPanel1 = new io.github.jezdawgz.poker.client.PlayerPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Poker");

        communityCards.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        lblBlinds.setText("Blinds: ");

        lblStartingStack.setText("Starting stack:");

        btnFold.setText("Fold");
        btnFold.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFoldActionPerformed(evt);
            }
        });

        btnCheck.setText("Check");
        btnCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCheckActionPerformed(evt);
            }
        });

        btnBet.setText("Bet");
        btnBet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBetActionPerformed(evt);
            }
        });

        lblPot.setText("Pot: 0");

        spnBet.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(0), Integer.valueOf(0), null, Integer.valueOf(1)));

        txtLog.setEditable(false);
        txtLog.setColumns(20);
        txtLog.setRows(5);
        jScrollPane1.setViewportView(txtLog);

        btnCall.setText("Call");
        btnCall.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCallActionPerformed(evt);
            }
        });

        btnAllIn.setText("Max Bet");
        btnAllIn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAllInActionPerformed(evt);
            }
        });

        lblTitle.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        lblTitle.setText("Poker Game");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(59, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 636, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(43, 43, 43))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblStartingStack)
                .addGap(68, 68, 68)
                .addComponent(lblBlinds)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnFold)
                .addGap(5, 5, 5)
                .addComponent(btnCheck)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnCall)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnBet)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(spnBet, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnAllIn)
                .addGap(54, 54, 54))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(128, 128, 128)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(playerPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(playerPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(116, 116, 116)
                                        .addComponent(communityCards, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(164, 164, 164)
                                        .addComponent(lblPot)))
                                .addGap(111, 111, 111)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(playerPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(playerPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(77, 77, 77)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(playerPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(129, 129, 129)
                                        .addComponent(playerPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(playerPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(121, 121, 121)
                                        .addComponent(playerPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(264, 264, 264)
                        .addComponent(lblTitle)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(lblTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 37, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(playerPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(playerPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(13, 13, 13)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(communityCards, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(playerPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(playerPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(19, 19, 19)
                                .addComponent(lblPot))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(playerPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(46, 46, 46)
                        .addComponent(playerPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(48, 48, 48)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(playerPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(playerPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(62, 62, 62)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblBlinds)
                    .addComponent(lblStartingStack)
                    .addComponent(btnFold, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCheck, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBet, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCall, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spnBet, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAllIn, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnFoldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFoldActionPerformed
        playerClickedFoldButton();
    }//GEN-LAST:event_btnFoldActionPerformed

    private void btnCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCheckActionPerformed
        playerClickedCheckButton();
    }//GEN-LAST:event_btnCheckActionPerformed

    private void btnBetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBetActionPerformed
        playerClickedBetButton();
    }//GEN-LAST:event_btnBetActionPerformed

    private void btnCallActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCallActionPerformed
        playerClickedCallButton();
    }//GEN-LAST:event_btnCallActionPerformed

    private void btnAllInActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAllInActionPerformed
       spnBet.setValue(PLAYER_PANELS[playerIndex].getTotalChips());
        
    }//GEN-LAST:event_btnAllInActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAllIn;
    private javax.swing.JButton btnBet;
    private javax.swing.JButton btnCall;
    private javax.swing.JButton btnCheck;
    private javax.swing.JButton btnFold;
    private io.github.jezdawgz.poker.client.CardHolder communityCards;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblBlinds;
    private javax.swing.JLabel lblPot;
    private javax.swing.JLabel lblStartingStack;
    private javax.swing.JLabel lblTitle;
    private io.github.jezdawgz.poker.client.PlayerPanel playerPanel1;
    private io.github.jezdawgz.poker.client.PlayerPanel playerPanel2;
    private io.github.jezdawgz.poker.client.PlayerPanel playerPanel3;
    private io.github.jezdawgz.poker.client.PlayerPanel playerPanel4;
    private io.github.jezdawgz.poker.client.PlayerPanel playerPanel5;
    private io.github.jezdawgz.poker.client.PlayerPanel playerPanel6;
    private io.github.jezdawgz.poker.client.PlayerPanel playerPanel7;
    private io.github.jezdawgz.poker.client.PlayerPanel playerPanel8;
    private javax.swing.JSpinner spnBet;
    private javax.swing.JTextArea txtLog;
    // End of variables declaration//GEN-END:variables
}
