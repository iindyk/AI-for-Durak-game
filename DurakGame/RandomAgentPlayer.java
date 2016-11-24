package DurakGame;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by igor.indyk on 11/17/2016.
 */
public class RandomAgentPlayer extends Player {
    public RandomAgentPlayer(){
        RandomAgentPlayer.count++;
        this.name="RandomAgent"+count;
    }
    @Override
    ArrayList<Card> attack(ArrayList<Card> cardsOnTable, char trumpSuit) throws SQLException {
        ArrayList<Card> retainedHand=new ArrayList<>();
        retainedHand.addAll(this.hand);
        int in=0;
        if (!(cardsOnTable.size()==0)) {
            for (Card cardOnHand :
                    this.hand) {
                for (Card cardOnTable :
                        cardsOnTable) {
                    if (cardOnHand.value == cardOnTable.value) in = 1;
                }
                if (in == 0) retainedHand.remove(cardOnHand);
                else in = 0;
            }
        }
        Random r=new Random();
        ArrayList<Card> result=new ArrayList<>();
        result.add(retainedHand.get(r.nextInt(retainedHand.size())));
        ArrayList<Card> handDuplicate=new ArrayList<>();
        handDuplicate.addAll(hand);
        for (Card att:
                result) {
            for (Card card:
                    handDuplicate) {
                if (att.value==card.value&&att.suit==card.suit) this.hand.remove(card);
            }
        }
        return result;
    }

    @Override
    ArrayList<Card> defend(ArrayList<Card> attack, char trump) throws SQLException {
        ArrayList<CardVector> possibleDefences = new ArrayList<>();
        ArrayList<Card> availableHand0=new ArrayList<>();
        ArrayList<Card> availableHand1=new ArrayList<>();
        ArrayList<Card> availableHand2=new ArrayList<>();
        ArrayList<Card> tmp=new ArrayList<>();
        for (int i = 0; i <this.hand.size() ; i++) {
            tmp.clear();
            availableHand0.clear();
            availableHand0.addAll(hand);
            if (hand.get(i).beats(attack.get(0),trump)) {
                tmp.add(hand.get(i));
                availableHand0.remove(hand.get(i));
                if (attack.size() > 1) {
                    availableHand1.clear();
                    availableHand1.addAll(availableHand0);
                    for (Card card :
                            availableHand0) {
                        if (card.beats(attack.get(1), trump)) {
                            availableHand1.remove(card);
                            tmp.add(card);
                            if (attack.size() > 2) {
                                availableHand2.clear();
                                availableHand2.addAll(availableHand1);
                                for (Card card1 :
                                        availableHand1) {
                                    if (card1.beats(attack.get(2), trump)) {
                                        availableHand2.remove(card1);
                                        tmp.add(card1);
                                        if (attack.size() > 3) {
                                            for (Card card2 :
                                                    availableHand2) {
                                                if (card2.beats(attack.get(3), trump)) {
                                                    tmp.add(card2);
                                                    possibleDefences.add(new CardVector(tmp,trump));
                                                    tmp.clear();
                                                }
                                            }
                                        }
                                        else {
                                            possibleDefences.add(new CardVector(tmp,trump));
                                            tmp.remove(2);
                                        }
                                    }
                                }
                            }
                            else {
                                possibleDefences.add(new CardVector(tmp,trump));
                                tmp.remove(1);
                            }

                        }
                    }

                }
                else {
                    possibleDefences.add(new CardVector(tmp,trump));
                    tmp.remove(0);
                }


            }

        }
        Random r=new Random();
        CardVector bestDefence=possibleDefences.get(r.nextInt(possibleDefences.size()));
        ArrayList<Card> handDuplicate=new ArrayList<>();
        handDuplicate.addAll(hand);
        for (Card att:
                bestDefence.toArrayList()) {
            for (Card card:
                    handDuplicate) {
                if (att.value==card.value&&att.suit==card.suit) this.hand.remove(card);
            }
        }
        return bestDefence.toArrayList();
    }

    @Override
    boolean canAttack(ArrayList<Card> cardsOnTable) {
        if (cardsOnTable.size()==0) return true;
        else {
            for (Card cardTable: cardsOnTable
                    ) {
                for (Card cardHand: this.hand
                        ) {
                    if (cardHand.value==cardTable.value) return true;
                }
            }
        }
        return false;
    }

    @Override
    boolean canDefend(ArrayList<Card> attack, char trump) {
        ArrayList<CardVector> possibleDefences = new ArrayList<>();
        ArrayList<Card> availableHand0=new ArrayList<>();
        ArrayList<Card> availableHand1=new ArrayList<>();
        ArrayList<Card> availableHand2=new ArrayList<>();
        ArrayList<Card> tmp=new ArrayList<>();
        for (int i = 0; i <this.hand.size() ; i++) {
            tmp.clear();
            availableHand0.clear();
            availableHand0.addAll(hand);
            if (hand.get(i).beats(attack.get(0),trump)) {
                tmp.add(hand.get(i));
                availableHand0.remove(hand.get(i));
                if (attack.size() > 1) {
                    availableHand1.clear();
                    availableHand1.addAll(availableHand0);
                    for (Card card :
                            availableHand0) {
                        if (card.beats(attack.get(1), trump)) {
                            availableHand1.remove(card);
                            tmp.add(card);
                            if (attack.size() > 2) {
                                availableHand2.clear();
                                availableHand2.addAll(availableHand1);
                                for (Card card1 :
                                        availableHand1) {
                                    if (card1.beats(attack.get(2), trump)) {
                                        availableHand2.remove(card1);
                                        tmp.add(card1);
                                        if (attack.size() > 3) {
                                            for (Card card2 :
                                                    availableHand2) {
                                                if (card2.beats(attack.get(3), trump)) {
                                                    tmp.add(card2);
                                                    possibleDefences.add(new CardVector(tmp,trump));
                                                    tmp.clear();
                                                }
                                            }
                                        }
                                        else {
                                            possibleDefences.add(new CardVector(tmp,trump));
                                            tmp.remove(2);
                                        }
                                    }
                                }
                            }
                            else {
                                possibleDefences.add(new CardVector(tmp,trump));
                                tmp.remove(1);
                            }

                        }
                    }

                }
                else {
                    possibleDefences.add(new CardVector(tmp,trump));
                    tmp.remove(0);
                }


            }

        }
        return !(possibleDefences.size()==0);
    }

    @Override
    void takeCard(Card card) {
        this.hand.add(card);
    }

    @Override
    int getCardValue(Card card) {
        if (card==null) return 100;
        else return card.valueInt;
    }
}
