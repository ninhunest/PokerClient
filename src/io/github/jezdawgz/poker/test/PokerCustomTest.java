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

import io.github.jezdawgz.poker.server.Card;
import io.github.jezdawgz.poker.server.Card.CardSet;
import io.github.jezdawgz.poker.server.Card.Suit;
import io.github.jezdawgz.poker.server.Card.Value;
import io.github.jezdawgz.poker.server.Hand;
import io.github.jezdawgz.poker.server.HandAnalyser;
import io.github.jezdawgz.poker.server.Pot;
import io.github.jezdawgz.poker.server.Player;
import java.util.ArrayList;


/**
 * Used to test the {@link io.github.jezdawgz.poker.client} and {@link io.github.jezdawgz.poker.server} packages
 * @author Jeremy Collette
 */
public class PokerCustomTest {
    
    public static void main(String[] args)
    {
        /*
        Card[] cards = new Card[]{new Card(Suit.SPADES, Value.ACE), new Card(Suit.SPADES, Value.FIVE), new Card(Suit.CLUBS, Value.TWO), new Card(Suit.CLUBS, Value.THREE), new Card(Suit.CLUBS, Value.FOUR)};
        Hand h1 = HandAnalyser.getBestHand(null, new CardSet(cards), new CardSet(new Card[]{new Card(Suit.DIAMONDS, Value.ACE), new Card(Suit.HEARTS, Value.ACE)}));
        Hand h2 = HandAnalyser.getBestHand(null, new CardSet(cards), new CardSet(new Card[]{new Card(Suit.SPADES, Value.JACK), new Card(Suit.DIAMONDS, Value.JACK)}));
        Hand h3 = HandAnalyser.getBestHand(null, new CardSet(cards), new CardSet(new Card[]{new Card(Card.Suit.DIAMONDS, Card.Value.SIX), new Card(Card.Suit.DIAMONDS, Card.Value.SEVEN)}));
        Hand h4 = HandAnalyser.getBestHand(null, new CardSet(cards), new CardSet(new Card[]{new Card(Card.Suit.SPADES, Card.Value.SIX), new Card(Card.Suit.HEARTS, Card.Value.SEVEN)}));
        
        System.out.println(h1.getType()+": "+h1.toString());
        System.out.println(h2.getType()+": "+h2.toString());
        System.out.println(h3.getType()+": "+h3.toString());
        System.out.println(h4.getType()+": "+h4.toString());        
        
        Hand.HandComparator comp = new Hand.HandComparator();
        System.out.println(comp.compare(h1, h4));
        */
        
        Player[] players = new Player[]{new Player("Jeremy", null, null), new Player("Justin", null, null), new Player("Baker", null, null), new Player("Andy", null, null)};
        int JEREMY = 0, JUSTIN = 1, BAKER = 2, ANDY = 3;
        Pot np = new Pot(players);

        np.setBet(players[JEREMY], 500);
        np.setBet(players[JUSTIN], 400);
        np.setBet(players[BAKER], 300);
        
        /*
        np.setBet(players[ANDY], 400);
        np.setBet(players[JEREMY], 200);
        np.setBet(players[JUSTIN], 400);
        np.setBet(players[BAKER], 350);
        np.setBet(players[JUSTIN], 1000);
        np.setBet(players[ANDY], 1000);
        */
        
        int t = np.getTotalAmount();
        System.out.println("Total pot: "+np.getTotalAmount());
        //for (int i = JEREMY; i <= ANDY; i++)
        /*
        for (int i = BAKER; i >= JEREMY; i--)
        {
            int c = np.getContribution(players[i]);
            int w = np.getWinnings(players[i]);
            System.out.println("i: "+i+", Contrib: "+c+", Winnings: "+w+", Remaining: "+np.getTotalAmount());
        }
        */
        
        int splitsies; 
        ArrayList<Player> p = new ArrayList<>();
        p.add(players[JEREMY]);
        p.add(players[JUSTIN]);
        p.add(players[BAKER]);
        
        while(p.size() > 0)
        {
            for (Player ply : p)
            {
                System.out.print(ply.getName()+" ");
            }
            splitsies = np.getTotalSharedWinnings(p.toArray(new Player[0]));
            System.out.println("\nSharing: "+splitsies +" ("+splitsies/p.size()+" each)");
            ArrayList<Player> rem = new ArrayList<>();
            for (Player ply : p)
            {
                if (np.getContribution(ply) == 0)
                {
                    rem.add(ply);
                }
            }
            for (Player ply : rem)
            {
                p.remove(ply);
            }
        }
        
        
    }
    
}
