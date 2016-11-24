package DurakGame;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by igor on 11.10.16.
 */
public abstract class Player {
    String name;
    static int count;
    ArrayList<Card> hand=new ArrayList<>();
    ArrayList<Card> outOfTheGame=new ArrayList<>();
    abstract ArrayList<Card> attack(ArrayList<Card> cardsOnTable,char trumpSuit) throws SQLException;
    abstract ArrayList<Card> defend(ArrayList<Card> attackCards, char trumpSuit) throws SQLException;
    abstract boolean canAttack(ArrayList<Card> cardsOnTable);
    abstract boolean canDefend(ArrayList<Card> attack,char trumpSuit);
    abstract void takeCard(Card card);
    abstract int getCardValue(Card card);
}
