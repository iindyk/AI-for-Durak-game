package DurakGame;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by igor on 03.10.16.
 */
public class Game {
    public static int count;
    public Card trumpCard;
    public ArrayList<Player> players=new ArrayList<>();
    public ArrayList<Card> deck=new ArrayList<>();
    public ArrayList<Card> cardsOnTable=new ArrayList<>();
    private Game(){
    }
    public Game(ArrayList<Player> players) throws SQLException{
        this.players=players;
        this.deck=Card.createDeck();
        this.trumpCard=deck.get(0);
        System.out.println("Trump card is "+this.trumpCard);
        for (Player player: players
             ) {
            System.out.print(player.name + "'s hand is ");
            for (int i = 0; i <6 ; i++) {
                Card card=Card.nextCard(this.deck);
                player.takeCard(card);
                System.out.print(card+"  ");
            }
            System.out.println();
        }

        //for 2 players only
        Player attacker=players.get(0);
        Player defender=players.get(1);
        Player transitPlayer;
        int roundNumber=1;
        while (attacker.hand.size()!=0 && defender.hand.size()!=0 && roundNumber<1000) {
            //
            System.out.print(attacker.name + "'s hand is ");
            for(Card card: attacker.hand) System.out.print(card+"  ");
            System.out.println();
            System.out.print(defender.name + "'s hand is ");
            for(Card card: defender.hand) System.out.print(card+"  ");
            System.out.println();
            //
            ArrayList<Card> attackCards;
            System.out.println("Round " +roundNumber);
            while (attacker.canAttack(this.cardsOnTable)) {
                attackCards=attacker.attack(this.cardsOnTable,this.trumpCard.suit);
                this.cardsOnTable.addAll(attackCards);
                System.out.println(attacker.name + " attacks with " + attackCards);//
                if (defender.canDefend(attackCards,this.trumpCard.suit)) {
                    ArrayList<Card> defendCards=defender.defend(attackCards,this.trumpCard.suit);
                    this.cardsOnTable.addAll(defendCards);
                    System.out.println(defender.name +" defends with "+ defendCards);//
                    if (!attacker.canAttack(this.cardsOnTable)) {
                        transitPlayer=attacker;
                        attacker=defender;
                        defender=transitPlayer;
                        break;
                    }
                }
                else {
                    for (Card card: this.cardsOnTable) defender.takeCard(card);
                    System.out.println(defender.name + " takes cards");
                    break;
                }
            }
            this.cardsOnTable.clear();
            while (attacker.hand.size()<6&&deck.size()!=0) {
                attacker.takeCard(Card.nextCard(this.deck));

            }
            while (defender.hand.size()<6&&deck.size()!=0) {
                defender.takeCard(Card.nextCard(this.deck));
            }
            roundNumber++;
        }
        if (attacker.hand.size()==0) System.out.println(attacker.name+" wins!");
        else if (roundNumber==999) System.out.println("Timeout!");
        else    System.out.println(defender.name+ " wins!");
        if (attacker.name.charAt(0)=='T' && attacker.hand.size()==0) Game.count++;
        if (defender.name.charAt(0)=='T' && defender.hand.size()==0) Game.count++;
    }
}
