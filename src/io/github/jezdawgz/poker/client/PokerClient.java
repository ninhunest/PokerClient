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

import io.github.jezdawgz.poker.server.Event.*;
import io.github.jezdawgz.poker.server.Event;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


/**
 * The client - communicates with a {@link PokerServer} using a GUI ({@link GameScreen}) as the intermediate for displaying server {@link Event}s and creating client {@link Event}s
 * 
 * @author Jeremy Collette
 */
public class PokerClient implements Runnable {
    
    public static final String VERSION = "0.9.1";
   
    public static class PokerClientInitException extends Exception
    {

        public PokerClientInitException() {
        }

        public PokerClientInitException(String message) {
            super(message);
        }

        public PokerClientInitException(Throwable cause) {
            super(cause);
        }

        public PokerClientInitException(String message, Throwable cause) {
            super(message, cause);
        }
        
    }
    
    public static class PokerClientHandshakeFailureException extends Exception
    {

        public PokerClientHandshakeFailureException() {
        }

        public PokerClientHandshakeFailureException(String message) {
            super(message);
        }

        public PokerClientHandshakeFailureException(Throwable cause) {
            super(cause);
        }

        public PokerClientHandshakeFailureException(String message, Throwable cause) {
            super(message, cause);
        }
        
    }
        
    
    private GameScreen screen;
    private ObjectOutputStream dataOut;
    private ObjectInputStream dataIn;
    
    public PokerClient(Socket s, String name) throws PokerClientInitException
    {
        try
        {
            dataOut = new ObjectOutputStream(s.getOutputStream());
            dataIn = new ObjectInputStream(s.getInputStream());
        }
        catch(IOException e)
        {
            throw new PokerClientInitException("Client initialisation failed!", e);
        }
        
        try
        {
            doHandshake();
            dataOut.writeObject(new Event.NewMessageEvent(name));
        }
        catch(Exception e)
        {
            throw new PokerClientInitException("Handshake failed: "+e.getMessage(), e);
        }
        
        screen = null;       
    }
    
    private void doHandshake() throws PokerClientHandshakeFailureException
    {
        try
        {
            dataOut.writeUTF("Hello from PokerClient "+VERSION);
            dataOut.flush();
            
            String s = dataIn.readUTF();
            if (!s.startsWith("Hello from PokerServer"))
            {
                throw new PokerClientHandshakeFailureException("Expected \"Hello from PokerServer\" but got \""+ s+"\"");
            }
            
            dataOut.writeUTF("Can I join?");
            dataOut.flush();
            
            s = dataIn.readUTF();
            if (!s.startsWith("Yes"))
            {
                /* Client rejected! */
                if (s.startsWith("No: ")) 
                    s = s.replace("No: ", "");
                               
                throw new PokerClientHandshakeFailureException("Connection rejected: "+s);
            }                        
        }
        catch(IOException e)
        {
            /* Handshake failed! */
            throw new PokerClientHandshakeFailureException("Handshake failed", e);
        }
    }
    
    @Override
    public void run()
    {
        screen = new GameScreen();
        screen.setVisible(true);   

        ClientEventPoller poller = new ClientEventPoller(screen, dataOut);
        Thread t = new Thread(poller);
        t.start();

        while (true) 
        {
            Event e = null;

            try 
            {
                e = (Event) dataIn.readObject();
            } 
            catch (Exception ex) 
            {
                break;
                //throw new RuntimeException("Error reading event!");
            }

           screen.receiveEvent(e);
           if (GameOverEvent.class.isInstance(e))
           {
               System.out.println("Game over!");
               break;
           }

        }       
        
        try
        {
            dataOut.close();
            dataIn.close();
        }
        catch(Exception e)
        {
            /* We don't care! */
        }
        
    }
    
    
    private static class ClientEventPoller implements Runnable
    {
        private final GameScreen client;
        private final ObjectOutputStream eventStream;
        
        public ClientEventPoller(GameScreen game, ObjectOutputStream oos)
        {
            client = game;
            eventStream = oos;
        }
        
        @Override
        public void run()
        {        
            while(true)
            {
                Event e = client.getNextClientEvent();
                if (e != null)
                {
                    System.out.println("Sending event to server: "+e.toString());
                    try
                    {
                        eventStream.writeObject(e);
                    }
                    catch(IOException ex)
                    {
                        ex.printStackTrace();
                        throw new RuntimeException("Error sending data!");
                    }
                }
                
                try
                {
                    Thread.sleep(10);
                }
                catch(Exception ex)
                {
                    break;
                }
            }   
        }      
    }
}
