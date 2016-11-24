package DurakGame;

import java.util.ArrayList;

/**
 * Created by igor on 09.10.16.
 */

//attacks and defends with smallest possible cards
public class SimpleAgentPlayer extends Player {
    public SimpleAgentPlayer(){
        SimpleAgentPlayer.count++;
        this.name="SimpleAgent"+count;
    }
    @Override
    public ArrayList<Card> attack(ArrayList<Card> cardsOnTable,char trump) {
        Card result=null;
        if (cardsOnTable.size()==0) {
            for (Card card:this.hand
                 ) {
                if (getCardValue(result)>getCardValue(card)) result=card;
            }
        }
        else {
            for (Card cardTable: cardsOnTable
                 ) {
                for (Card cardHand: this.hand
                     ) {
                    if (cardTable.value==cardHand.value && getCardValue(cardHand)<getCardValue(result)) result=cardHand;
                }
            }
        }
        this.hand.remove(result);
        ArrayList<Card> res=new ArrayList<>();
        res.add(result);
        return res;
    }

    @Override
    public ArrayList<Card> defend(ArrayList<Card> attack,char trump) {
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
        CardVector bestDefence=null;
        int bestDefenceValue=1000;
        for (int i = 0; i <possibleDefences.size(); i++) {
            int sumDefenceValue=0;
            for (Card card:
                 possibleDefences.get(i).toArrayList()) {
                sumDefenceValue+=card.valueIntWithTrump;
            }
            if (sumDefenceValue <bestDefenceValue) {
                bestDefenceValue=sumDefenceValue;
                bestDefence=possibleDefences.get(i);
            }
        }
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
    public boolean canAttack(ArrayList<Card> cardsOnTable) {
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
    boolean canDefend(ArrayList<Card> attack, char trump){
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
    public void takeCard(Card card) {
        this.hand.add(card);
    }

    @Override
    public int getCardValue(Card card) {
        if (card==null) return 100;
        else return card.valueInt;
    }

}
