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



package io.github.jezdawgz.poker.test;

import io.github.jezdawgz.poker.client.PokerClient;
import io.github.jezdawgz.poker.server.PokerServer;
import java.net.Socket;

/**
 * Used to test the {@link io.github.jezdawgz.poker.client} and {@link io.github.jezdawgz.poker.server} packages
 * @author Jeremy Collette
 */
public class PokerTest {
    
    private static final int NUM_CLIENTS = 3;
    
    public static void main(String[] args)
    {    
        try
        {                       
            PokerServer ps = new PokerServer(NUM_CLIENTS, 3000, 5, 10);
            Thread t = new Thread(ps);
            t.start();
                        
            for (int i = 0; i < NUM_CLIENTS; i++)
            {
                Socket s = new Socket("localhost", ps.getPort());            
                PokerClient pc = new PokerClient(s, "Player "+i);
                t = new Thread(pc);
                t.start();
            }            
                       
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
    }
    
    
}
