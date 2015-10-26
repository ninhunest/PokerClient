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

import io.github.jezdawgz.poker.client.PokerClient.PokerClientInitException;
import java.io.IOException;
import java.net.Socket;
import javax.swing.JOptionPane;
import javax.swing.JSpinner.NumberEditor;

/**
 * Used to get connection properties for connecting to a {@link PokerServer}
 * @author Jeremy Collette
 */
public class ClientLauncher extends javax.swing.JFrame {    
    
    /**
     * Creates new form ConnectionDialog
     */
    public ClientLauncher() {
        super("Poker Launcher");
        initComponents();
        setLocationRelativeTo(null);
        
        getRootPane().setDefaultButton(btnConnect);
        spnPort.setEditor(new NumberEditor(spnPort, "0"));
    }
    
    public String getName()
    {
        return txtName.getText();
    }
    
    public String getIpAddress()
    {
        return txtHost.getText();
    }
    
    public int getPort()
    {
        return (int)spnPort.getValue();
    }
    
    public static void main(String args[])
    {
        ClientLauncher cl = new ClientLauncher();
        cl.setVisible(true);
    }
    
   
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        spnPort = new javax.swing.JSpinner();
        btnConnect = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        txtHost = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel1.setText("Server Details");

        jLabel2.setText("Enter the details of the server to connect to and click \"Connect\".");

        jLabel3.setText("IP Address:");

        txtName.setText("Unnamed");

        jLabel4.setText("Port:");
        jLabel4.setToolTipText("");

        spnPort.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(3232), Integer.valueOf(0), null, Integer.valueOf(1)));
        spnPort.setValue(3232);

        btnConnect.setText("Connect");
        btnConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConnectActionPerformed(evt);
            }
        });

        jLabel5.setText("User Nickname:");

        txtHost.setText("localhost");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(163, 163, 163)
                        .addComponent(btnConnect))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(117, 117, 117)
                        .addComponent(jLabel1))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel2))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(46, 46, 46)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtHost, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel5))
                                .addGap(61, 61, 61)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(spnPort, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap(36, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtHost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(spnPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(38, 38, 38)
                .addComponent(btnConnect)
                .addGap(25, 25, 25))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConnectActionPerformed

        int len = txtName.getText().length();
        if (len < 3 || len > 10)
        {
            JOptionPane.showMessageDialog(this, "Enter a name between 3 and 10 characters long.", "Invalid Name", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (txtHost.getText().length() == 0)
        {
            JOptionPane.showMessageDialog(this, "Enter a valid host (IP address)", "Invalid Host", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!(spnPort.getValue() instanceof Integer) || (int)spnPort.getValue() < 0)
        {
            JOptionPane.showMessageDialog(this, "Enter a valid port (default 3232)", "Invalid Port", JOptionPane.ERROR_MESSAGE);
            return;
        }        

        try
        {
            //System.out.println("Attempting to connect to "+host+" on "+port+"...");            
            Socket s = new Socket(getIpAddress(), getPort());
            PokerClient client = new PokerClient(s, getName());
            Thread t = new Thread(client);
            t.start();           
        }
        catch(IOException e)
        {
            JOptionPane.showMessageDialog(null, "Connection failed!", "Error", JOptionPane.ERROR_MESSAGE);
        }        
        catch(PokerClientInitException e)
        {
            JOptionPane.showMessageDialog(null, "Error connecting: "+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }        
        this.setVisible(false);

    }//GEN-LAST:event_btnConnectActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnConnect;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JSpinner spnPort;
    private javax.swing.JTextField txtHost;
    private javax.swing.JTextField txtName;
    // End of variables declaration//GEN-END:variables
}
