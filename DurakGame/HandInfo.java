package DurakGame;

import java.util.ArrayList;

/**
 * Created by igor.indyk on 11/18/2016.
 */
//created to define which hand is better
public class HandInfo {
    public double averageCardValue;
    public int handSize;
    public HandInfo(){

    }
    private HandInfo(double averageCardValue,int handSize){
        this.averageCardValue=averageCardValue;
        this.handSize=handSize;
    }
    public HandInfo(ArrayList<String> hand,char trump){
        this.handSize=hand.size();
        int sumValue=0;
        for (String card:
             hand) {
            int value=new Card(card.charAt(1),card.charAt(0)).valueInt;
            if (card.charAt(0)==trump) value+=9;
            sumValue+=value;
        }
        this.averageCardValue=sumValue/this.handSize;

    }
    public boolean isBetterThan(HandInfo handInfo){
        if(this.averageCardValue>handInfo.averageCardValue && this.handSize<=6) return true;
        else if (this.averageCardValue>1.5*handInfo.averageCardValue && this.handSize<1.5*handInfo.handSize) return true;
        else if (this.averageCardValue>2*handInfo.averageCardValue && this.handSize<2*handInfo.handSize) return true;
        else return false;
    }
    public static int getAttackReward(HandInfo oldAttackerHandInfo, HandInfo newAttckerHandInfo,
                               HandInfo oldDefenderHandInfo, HandInfo newDefenderHandInfo){
        int result=0;
        if (newAttckerHandInfo.isBetterThan(oldAttackerHandInfo)) result+=3;
        if (newDefenderHandInfo.isBetterThan(oldDefenderHandInfo)) result-=2;
        return result;
    }
    public static int getDefenceReward(HandInfo oldAttackerHandInfo, HandInfo newAttckerHandInfo,
                                HandInfo oldDefenderHandInfo, HandInfo newDefenderHandInfo){
        int result=0;
        if (newDefenderHandInfo.isBetterThan(oldDefenderHandInfo)) result+=3;
        if (newAttckerHandInfo.isBetterThan(oldAttackerHandInfo)) result-=2;
        return result;
    }
}
