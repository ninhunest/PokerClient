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




package io.github.jezdawgz.poker.test;

import io.github.jezdawgz.poker.server.Card;
import io.github.jezdawgz.poker.server.Card.CardSet;
import io.github.jezdawgz.poker.server.Card.Suit;
import io.github.jezdawgz.poker.server.Card.Value;
import io.github.jezdawgz.poker.server.Hand;
import io.github.jezdawgz.poker.server.HandAnalyser;


/**
 * Used to test the {@link io.github.jezdawgz.poker.client} and {@link io.github.jezdawgz.poker.server} packages
 * @author Jeremy Collette
 */
public class PokerCustomTest {
    
    public static void main(String[] args)
    {
        Card[] cards = new Card[]{new Card(Suit.SPADES, Value.ACE), new Card(Suit.SPADES, Value.FIVE), new Card(Suit.CLUBS, Value.TWO), new Card(Suit.CLUBS, Value.THREE), new Card(Suit.CLUBS, Value.FOUR)};
        Hand h1 = HandAnalyser.getBestHand(null, new CardSet(cards), new CardSet(new Card[]{new Card(Suit.DIAMONDS, Value.ACE), new Card(Suit.HEARTS, Value.ACE)}));
        Hand h2 = HandAnalyser.getBestHand(null, new CardSet(cards), new CardSet(new Card[]{new Card(Suit.SPADES, Value.JACK), new Card(Suit.DIAMONDS, Value.JACK)}));
        System.out.println(h1.getType()+": "+h1.toString());
        System.out.println(h2.getType()+": "+h2.toString());
        
        Hand.HandComparator comp = new Hand.HandComparator();
        System.out.println(comp.compare(h1, h2));
    }
    
}
