package DurakGame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Created by igor on 28.09.16.
 */
/* suits:
'h' - heart
'd' - diamond
'c' - club
's' - spade
 */
public class Card {
    char value;
    char suit;
    int valueInt;
    int valueIntWithTrump;
    private Card(){

    }
    public Card(char value,char suit){
        this.value=value;
        this.suit=suit;
        if (Character.isDigit(value)) {
            this.valueInt=Character.getNumericValue(value);
        }
        else{
            switch (value){
                case 't':
                    this.valueInt=10;
                    break;
                case 'j':
                    this.valueInt=11;
                    break;
                case 'q':
                    this.valueInt=12;
                    break;
                case 'k':
                    this.valueInt=13;
                    break;
                case 'a':
                    this.valueInt=14;
                    break;
            }
        }
        this.valueIntWithTrump=this.valueInt;
    }
    public boolean beats(Card card2,char trumpSuit){
        if ((this.suit==card2.suit)&&(this.valueInt>card2.valueInt)) return true;
        else if ((this.suit!=card2.suit)&&(this.suit==trumpSuit)) return true;
        else return false;
    }

    public static ArrayList<Card> createDeck(){
        ArrayList<Card> result=new ArrayList<>();
        Card[] sortedDeck={new Card('6','h'),new Card('7','h'),new Card('8','h'),new Card('9','h'),new Card('t','h'),new Card('j','h'),
                new Card('q','h'), new Card('k','h'), new Card('a','h'),
                new Card('6','d'),new Card('7','d'),new Card('8','d'),new Card('9','d'),new Card('t','d'),new Card('j','d'),
                new Card('q','d'), new Card('k','d'), new Card('a','d'),
                new Card('6','c'),new Card('7','c'),new Card('8','c'),new Card('9','c'),new Card('t','c'),new Card('j','c'),
                new Card('q','c'), new Card('k','c'), new Card('a','c'),
                new Card('6','s'),new Card('7','s'),new Card('8','s'),new Card('9','s'),new Card('t','s'),new Card('j','s'),
                new Card('q','s'), new Card('k','s'), new Card('a','s')};
        Collections.addAll(result,sortedDeck);
        Random random=new Random();
        for (int i = 2; i <result.size() ; i++) {
            int j=random.nextInt(i);
            Card temp;
            temp=result.get(i);
            result.set(i,result.get(j));
            result.set(j,temp);
        }
        return result;
    }
    public static Card nextCard(ArrayList<Card> deck){
        Card result =deck.get(deck.size()-1);
        deck.remove(deck.size()-1);
        return result;
    }
    public String toString(){
        return this.value+" "+this.suit;
    }
    public boolean equals(Card card){
      if (this.value==card.value && this.suit==card.suit) return true;
      else return false;
    }
}
