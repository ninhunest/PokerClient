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
import io.github.jezdawgz.poker.server.ClientCommand;
import io.github.jezdawgz.poker.server.Card;
import io.github.jezdawgz.poker.server.Event;
import io.github.jezdawgz.poker.server.Event.*;
import java.awt.Component;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultCaret;

/**
 * The main GUI of the {@link PokerClient} - represents a poker table showing each {@link Player} in the {@link Game} 
 * 
 * @author Jeremy Collette
 */
public class GameScreen extends javax.swing.JFrame {

    private final PlayerPanel[] PLAYER_PANELS;
    private final JButton[] PLAYER_BUTTONS;
    private final Component[] RAISE_COMPONENTS;
    private int playerIndex;
    private int toActIndex;
    private final LinkedList<Event> eventsToSend;
    private static final SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
    
    private int ourChips;    
    private int minBet;
    private int currentBet;
    private int ourChipsOnTable;
    private int smallBlind;
    private int bigBlind;
    private int pot;
    
    /**
     * The default constructor
     */
    public GameScreen()
    {
        initComponents();
        setLocationRelativeTo(null);
              
        /* initialise variables */
        ourChips = 0;
        minBet = 0;
        currentBet = 0;
        ourChipsOnTable = 0;
        smallBlind = 0;
        bigBlind = 0;
        
        /* add a listener to update bet amount */
        sldBetAmt.addChangeListener(new ChangeListener()
        {
            @Override
            public void stateChanged(ChangeEvent e) {
                setRaiseAmount(sldBetAmt.getValue());
            }           
        });
        
                
        /* various preparations */
        PLAYER_PANELS = new PlayerPanel[]{playerPanel1, playerPanel2, playerPanel3, playerPanel4, playerPanel5, playerPanel6, playerPanel7, playerPanel8};
        PLAYER_BUTTONS = new JButton[]{btnFold, btnCheck, btnBet, btnCall};
        RAISE_COMPONENTS = new Component[]{btnMinRaise, btnHalfPot, btnWholePot, btnAllIn, sldBetAmt};
        setButtonsEnabled(false);        
        toActIndex = -1;        
        eventsToSend = new LinkedList<>();
       
        /* set log to auto-scroll */
        DefaultCaret caret = (DefaultCaret) txtLog.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        
    }
   
    /**
     * Gets the next Client event to sent to the {@link PokerServer} we are connected to.
     * @return Next Client event
     */
    public Event getNextClientEvent()
    {
        return eventsToSend.poll();
    }
    
    /**
     * Sets client buttons to be enabled/disabled
     * @param enabled If true enables buttons, false disables buttons.
     */
    private void setButtonsEnabled(boolean enabled)
    {
        for (JButton b : PLAYER_BUTTONS)
        {
            b.setEnabled(enabled);
        }             
        
        for (Component c : RAISE_COMPONENTS)
        {
            c.setEnabled(enabled);
        }
        
        /* We don't want to be able to 'Call' if we already have! */
        if (currentBet-ourChipsOnTable == 0)
        {
            btnCall.setEnabled(false);
        }
        /* We don't want to be able to 'Check' if there are chips on the table! */
        else
        {
            btnCheck.setEnabled(false);
        }
        
        
    }
          
    /**
     * Updates client to reflect that a {@link Player} has folded
     * @param index The index of the folded Player
     */
    private void playerFolded(int index)
    {
        PLAYER_PANELS[index].setTitle("FOLDED");
        addToLog(PLAYER_PANELS[index].getPlayerName()+" folded!");
    }
    
    /**
     * Updates client to reflect that a {@link Player} has disconnected
     * @param index The index of the disconnected Player
     */
    private void playerDisconnected(int index)
    {
        PLAYER_PANELS[index].setTitle("DISCONNECTED");
        PLAYER_PANELS[index].setChipsInStack(0);
        addToLog(PLAYER_PANELS[index].getPlayerName()+" disconnected!");
    }
    
    /**
     * Determines if it is our turn
     * @return True if it is our turn. False if it is not.
     */
    private boolean isItOurTurn()
    {
        return (toActIndex == playerIndex);        
    }
    
    /**
     * Set the player's raise amount to display on the GUI.
     * @param newBet The new raise / bet
     */
    private void setRaiseAmount(int newBet)
    {                
        if (newBet < minBet)
        {
            setRaiseAmount(minBet);
        }
        else
        {        
            sldBetAmt.setValue(newBet);

            if (newBet == 0)
            {
                //btnCheck.setEnabled(btnCheck.isEnabled());
                btnBet.setEnabled(false);
            }
            else
            {
                
                //btnBet.setEnabled(btnBet.isEnabled());

                if (newBet < ourChips)
                {
                    String caption = "";
                    if (currentBet > 0)
                    {
                        caption += "Raise";
                    }
                    else
                    {
                        caption += "Bet";
                    }
                    
                    caption += " ("+newBet+")";
                    btnBet.setText(caption);
                }
                else
                {
                    btnBet.setText("Bet (All-In!)");
                }
            }
        }
                
    }
    
    /**
     * Sets the minimum bet
     * @param newMinBet The new minimum bet
     */
    private void setMinBetAmount(int newMinBet)
    {
        minBet = newMinBet;
        
        lblSlideLower.setText(minBet+"");
        lblSlideUpper.setText(ourChips+"");
        
        sldBetAmt.setMinimum(minBet);
        sldBetAmt.setMaximum(ourChips);
        
        setRaiseAmount(minBet);
    }
    
    
    /**
     * Get the current raise/bet amount
     * @return The raise/bet amount
     */
    private int getBetAmount()
    {
        return sldBetAmt.getValue();
    }
    
    /**
     * Sends a command to the server
     * @param c The command to send
     */
    private void sendCommandToServer(ClientCommand c)
    {
        setButtonsEnabled(false);
        eventsToSend.add(c);
    }
    
    /**
     * Sends a "Fold" command to the server
     */
    private void playerClickedFoldButton()
    {
        sendCommandToServer(new ClientCommand.ClientFoldCommand());
    }
    
    /**
     * Sends a "Check" command to the server
     */
    private void playerClickedCheckButton()
    {
        sendCommandToServer(new ClientCommand.ClientCheckCommand());
    }
    
    /**
     * Sends a "Bet" command to the server
     */
    private void playerClickedBetButton()
    {
        sendCommandToServer(new ClientCommand.ClientBetCommand(getBetAmount()));
    }
    
    /**
     * Sends a "Call" command to the server
     */
    private void playerClickedCallButton()
    {
        sendCommandToServer(new ClientCommand.ClientCallCommand());
    }
    
   
    /** 
     * Reflect change in player turn
     * @param index Index of the player whose turn it is
     */
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
            setMinBetAmount(minBet);
            setButtonsEnabled(true);
            java.awt.Toolkit.getDefaultToolkit().beep();
        }
        else
        {
            setButtonsEnabled(false);
        }
    }
    
    /**
     * Reflect new pot amount
     * @param pot New pot amount
     */
    private void setPotAmount(int pot)
    {
        this.lblPot.setText("Pot: "+pot);
        this.pot = pot;
    }
    
    /**
     * Add message to chat log
     * @param toLog Message to add
     */
    private void addToLog(String toLog)
    {
        txtLog.append("["+df.format(new Date())+"]   "+toLog+"\n");
    }
    
    /**
     * Exit client due to error
     * @param e Error which caused exit
     */
    public void exitDueToError(Exception e)
    {
        JOptionPane.showMessageDialog(null, "An error has occured communicating with the server:\n" + e.getMessage());
        
        /* This will close our frame and actually end the process */
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }
    
    /**
     * Receive Event from server
     * @param e Event to receive
     */
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
            
            smallBlind = evt.smallBlind;
            bigBlind = evt.bigBlind;
        }
        
        else if (NewMessageEvent.class.isInstance(e))
        {
            //System.out.println("Recieved from server: "+((NewMessageEvent)e).message);
            addToLog(((NewMessageEvent)e).message);
        }
        
        else if (NewRoundEvent.class.isInstance(e))
        {            
            addToLog("** New round! **");
            
            ourChips = 0;
            minBet = 0;
            currentBet = 0;
            ourChipsOnTable = 0;        
            setPotAmount(0);
                                   
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
            /* Reset some stuff... */
            ourChipsOnTable = 0;
            currentBet = 0;
            setMinBetAmount(bigBlind);
            
            /* Get that card! */
            Card c = ((Event.NewCommunityCardEvent)e).newCard;
            communityCards.addCard(c);
                        
            for (PlayerPanel p : PLAYER_PANELS)
            {
                p.setChipsOnTable(0);
            }
            
            ourChips = PLAYER_PANELS[playerIndex].getTotalChips();
            
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
                     
            currentBet = evt.amount;
            ourChips = PLAYER_PANELS[playerIndex].getTotalChips();
            setMinBetAmount(minBet);
            
            
            /* If we put the chips on the table... */
            if (evt.playerIndex == playerIndex)
            {
                ourChipsOnTable = evt.amount;
                btnCall.setText("Call");
            }
            /* Or if someone else put the chips on the table... */
            else
            {
                /* How much do we have to call? */
                int amountToCall = evt.amount - ourChipsOnTable;
                if (amountToCall > 0)
                {
                    btnCall.setText("Call ("+(amountToCall)+")");
                    btnCall.setEnabled(true);
                }
                else
                {
                    btnCall.setText("Call");
                    btnCall.setEnabled(false);
                }
                
            }
            
            int newBet = getBetAmount();            
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
            setMinBetAmount(newBet < ourChips ? newBet : ourChips);
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
        jScrollPane1 = new javax.swing.JScrollPane();
        txtLog = new javax.swing.JTextArea();
        lblTitle = new javax.swing.JLabel();
        playerPanel1 = new io.github.jezdawgz.poker.client.PlayerPanel();
        lblSlideUpper = new javax.swing.JLabel();
        sldBetAmt = new javax.swing.JSlider();
        lblSlideLower = new javax.swing.JLabel();
        btnCall = new javax.swing.JButton();
        btnAllIn = new javax.swing.JButton();
        btnMinRaise = new javax.swing.JButton();
        btnHalfPot = new javax.swing.JButton();
        btnWholePot = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        mnuMain = new javax.swing.JMenuBar();
        mnbMain = new javax.swing.JMenu();
        mnuAbout = new javax.swing.JMenuItem();
        mnuLicense = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Poker");
        setMinimumSize(new java.awt.Dimension(735, 750));
        setPreferredSize(new java.awt.Dimension(735, 685));
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        getContentPane().add(playerPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 320, -1, -1));

        communityCards.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        getContentPane().add(communityCards, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 180, 129, 31));

        lblBlinds.setText("Blinds: ");
        getContentPane().add(lblBlinds, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 640, -1, -1));

        lblStartingStack.setText("Starting stack:");
        getContentPane().add(lblStartingStack, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 640, -1, -1));
        getContentPane().add(playerPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 230, -1, -1));
        getContentPane().add(playerPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 140, -1, -1));
        getContentPane().add(playerPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 80, -1, -1));
        getContentPane().add(playerPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 80, -1, -1));
        getContentPane().add(playerPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 140, -1, -1));
        getContentPane().add(playerPanel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 230, -1, -1));

        btnFold.setText("Fold");
        btnFold.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFoldActionPerformed(evt);
            }
        });
        getContentPane().add(btnFold, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 470, 70, 31));

        btnCheck.setText("Check");
        btnCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCheckActionPerformed(evt);
            }
        });
        getContentPane().add(btnCheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 470, 70, 31));

        btnBet.setText("Bet");
        btnBet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBetActionPerformed(evt);
            }
        });
        getContentPane().add(btnBet, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 470, 120, 31));

        lblPot.setText("Pot: 0");
        getContentPane().add(lblPot, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 250, -1, -1));

        txtLog.setEditable(false);
        txtLog.setColumns(20);
        txtLog.setLineWrap(true);
        txtLog.setRows(5);
        jScrollPane1.setViewportView(txtLog);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 520, 390, 100));

        lblTitle.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        lblTitle.setText("Poker Game");
        getContentPane().add(lblTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 20, -1, -1));
        getContentPane().add(playerPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 320, -1, -1));

        lblSlideUpper.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblSlideUpper.setText("100");
        getContentPane().add(lblSlideUpper, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 490, 70, -1));
        getContentPane().add(sldBetAmt, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 460, 230, 30));

        lblSlideLower.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblSlideLower.setText("0");
        getContentPane().add(lblSlideLower, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 490, 30, -1));

        btnCall.setText("Call");
        btnCall.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCallActionPerformed(evt);
            }
        });
        getContentPane().add(btnCall, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 470, 100, 31));

        btnAllIn.setText("All-In");
        btnAllIn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAllInActionPerformed(evt);
            }
        });
        getContentPane().add(btnAllIn, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 570, 90, 30));

        btnMinRaise.setText("Min Raise");
        btnMinRaise.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMinRaiseActionPerformed(evt);
            }
        });
        getContentPane().add(btnMinRaise, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 530, 90, 30));

        btnHalfPot.setText("1/2 Pot");
        btnHalfPot.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHalfPotActionPerformed(evt);
            }
        });
        getContentPane().add(btnHalfPot, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 530, 90, 30));

        btnWholePot.setText("Pot-Size");
        btnWholePot.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnWholePotActionPerformed(evt);
            }
        });
        getContentPane().add(btnWholePot, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 570, 90, 30));

        jLabel1.setText("Bet amount");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 500, -1, -1));

        mnbMain.setText("Help");

        mnuAbout.setText("About");
        mnuAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAboutActionPerformed(evt);
            }
        });
        mnbMain.add(mnuAbout);

        mnuLicense.setText("License");
        mnuLicense.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuLicenseActionPerformed(evt);
            }
        });
        mnbMain.add(mnuLicense);

        mnuMain.add(mnbMain);

        setJMenuBar(mnuMain);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /** 
     * Player clicks the "Fold" button
     * @param evt Button click event
     */
    private void btnFoldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFoldActionPerformed
        playerClickedFoldButton();
    }//GEN-LAST:event_btnFoldActionPerformed

    /**
     * Player clicks the "Check" button
     * @param evt Button click event
     */
    private void btnCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCheckActionPerformed
        playerClickedCheckButton();
    }//GEN-LAST:event_btnCheckActionPerformed

    /**
     * Player clicks the "Bet" button
     * @param evt Button click event
     */
    private void btnBetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBetActionPerformed
        playerClickedBetButton();
    }//GEN-LAST:event_btnBetActionPerformed
    
    /**
     * Player clicks the "Call" button
     * @param evt Button click event
     */
    private void btnCallActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCallActionPerformed
        playerClickedCallButton();
    }//GEN-LAST:event_btnCallActionPerformed

    /**
     *  Player sets their bet to minimum bet
     *  @param evt Button click event
     */
    private void btnMinRaiseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMinRaiseActionPerformed
        setRaiseAmount(minBet);
    }//GEN-LAST:event_btnMinRaiseActionPerformed

    /**
     * Player sets their bet to half-pot amount
     * @param evt Button click event
     */
    private void btnHalfPotActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHalfPotActionPerformed
        setRaiseAmount((int)Math.ceil(pot/2.));
    }//GEN-LAST:event_btnHalfPotActionPerformed

    /**
     * Player sets their bet to the whole pot amount
     * @param evt Button click event
     */
    private void btnWholePotActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnWholePotActionPerformed
        setRaiseAmount(pot);
    }//GEN-LAST:event_btnWholePotActionPerformed

    /**
     * Player sets their bet to be all-in!
     * @param evt Button click event
     */
    private void btnAllInActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAllInActionPerformed
        setRaiseAmount(ourChips);
    }//GEN-LAST:event_btnAllInActionPerformed

    /**
     * Player clicks the "About" menu button
     * @param evt Mouse click event
     */
    private void mnuAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAboutActionPerformed
        JOptionPane.showMessageDialog(this, 
                "PokerClient "+PokerClient.VERSION+"\n\n"
                        + "Copyright (C) 2015-2016 Jeremy Collette"
                        + "",
                "About",
                JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_mnuAboutActionPerformed

    
    /**
     * Player clicks the "License" menu button
     * @param evt Menu click event
     */
    private void mnuLicenseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuLicenseActionPerformed
        JOptionPane.showMessageDialog(this, 
            "Copyright (C) 2015-2016 Jeremy Collette." +
            "\n\n"+
                    
            "This program is free software: you can redistribute it and/or modify\n" +
            "it under the terms of the GNU General Public License as published by\n" +
            "the Free Software Foundation, either version 3 of the License, or\n" +
            "(at your option) any later version.\n" +
            "\n" +
            "This program is distributed in the hope that it will be useful,\n" +
            "but WITHOUT ANY WARRANTY; without even the implied warranty of\n" +
            "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the\n" +
            "GNU General Public License for more details.\n" +
            "\n" +
            "You should have received a copy of the GNU General Public License\n" +
            "along with this program.  If not, see <http://www.gnu.org/licenses/>.",
            "License",
            JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_mnuLicenseActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAllIn;
    private javax.swing.JButton btnBet;
    private javax.swing.JButton btnCall;
    private javax.swing.JButton btnCheck;
    private javax.swing.JButton btnFold;
    private javax.swing.JButton btnHalfPot;
    private javax.swing.JButton btnMinRaise;
    private javax.swing.JButton btnWholePot;
    private io.github.jezdawgz.poker.client.CardHolder communityCards;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblBlinds;
    private javax.swing.JLabel lblPot;
    private javax.swing.JLabel lblSlideLower;
    private javax.swing.JLabel lblSlideUpper;
    private javax.swing.JLabel lblStartingStack;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JMenu mnbMain;
    private javax.swing.JMenuItem mnuAbout;
    private javax.swing.JMenuItem mnuLicense;
    private javax.swing.JMenuBar mnuMain;
    private io.github.jezdawgz.poker.client.PlayerPanel playerPanel1;
    private io.github.jezdawgz.poker.client.PlayerPanel playerPanel2;
    private io.github.jezdawgz.poker.client.PlayerPanel playerPanel3;
    private io.github.jezdawgz.poker.client.PlayerPanel playerPanel4;
    private io.github.jezdawgz.poker.client.PlayerPanel playerPanel5;
    private io.github.jezdawgz.poker.client.PlayerPanel playerPanel6;
    private io.github.jezdawgz.poker.client.PlayerPanel playerPanel7;
    private io.github.jezdawgz.poker.client.PlayerPanel playerPanel8;
    private javax.swing.JSlider sldBetAmt;
    private javax.swing.JTextArea txtLog;
    // End of variables declaration//GEN-END:variables
}
