package DurakGame;

import java.util.ArrayList;

import static java.lang.Math.sqrt;

/**
 * Created by igor on 07.11.16.
 */
public class CardVector {
    private ArrayList<Integer> vector=new ArrayList<>();
    public int clusterNumber;
    public int distToAllInCluster;
    char trumpSuit;
    private CardVector(){

    }
    public CardVector(ArrayList<Card> hand1, char trumpSuit){
        this.clusterNumber=0;
        this.distToAllInCluster=0;
        this.trumpSuit=trumpSuit;
        ArrayList<Card> hand=new ArrayList<>();
        hand.addAll(hand1);
        //adding valueInt to trump cards
        for (Card card:
                hand) {
            if (card.suit==trumpSuit) card.valueIntWithTrump=card.valueInt+9;
        }
        //hand sorting
        Card temp;
        for (int i = 0; i <hand.size() ; i++) {
            for (int j = i+1; j <hand.size() ; j++) {
                if (hand.get(i).valueIntWithTrump>hand.get(j).valueIntWithTrump) {
                    temp=hand.get(i);
                    hand.set(i,hand.get(j));
                    hand.set(j,temp);
                }
            }
        }
        //hand->vector: card.value->valueInt; card.suit: h->(1,0,0,0); d->(0,1,0,0); c->(0,0,1,0); s->(0,0,0,1)
        for (Card card:
             hand) {
            this.vector.add(card.valueIntWithTrump);
            if (card.suit=='h') this.vector.add(1); else this.vector.add(0);
            if (card.suit=='d') this.vector.add(1); else this.vector.add(0);
            if (card.suit=='c') this.vector.add(1); else this.vector.add(0);
            if (card.suit=='s') this.vector.add(1); else this.vector.add(0);
        }
    }
    public double distTo(CardVector cardVector){
        double result=0;
        for (int i = 0; i <Math.min(this.vector.size(),cardVector.vector.size()) ; i++) {
            result+=Math.pow(this.vector.get(i)-cardVector.vector.get(i),2);
        }
        return sqrt(result) ;
    }
    public double norm(){
        double result=0;
        for (int i = 0; i <this.vector.size() ; i++) {
            result+=(this.vector.get(i))^2;
        }
        return sqrt(result) ;
    }
    public String toString(){
        if (this.vector.size()==0) return "";
        String result=new String();
        for (int i = 0; i <this.vector.size() ; i++) {
            int valueInt;
            if (this.vector.get(i)>14) {
                valueInt=this.vector.get(i)-9;
            }
            else valueInt=this.vector.get(i);
            if (valueInt>5&& valueInt<10)
                result+=valueInt;
            else if (valueInt==10) result+="t";
            else if (valueInt==11) result+="j";
            else if (valueInt==12) result+="q";
            else if (valueInt==13) result+="k";
            else if (valueInt==14) result+="a";
            else if (valueInt==1 && i%5==1) result+="h ";
            else if (valueInt==1 && i%5==2) result+="d ";
            else if (valueInt==1 && i%5==3) result+="c ";
            else if (valueInt==1 && i%5==4) result+="s ";
        }
        result+="; trump suit is "+this.trumpSuit;
        //result+="; distance to all in cluster "+(int)sqrt(this.distToAllInCluster);
        return result;
    }
    public ArrayList<Card> toArrayList(){
        ArrayList<Card> vectorArrayList=new ArrayList<>();
        int i=0;
        while (i<this.vector.size()) {
            char value;
            int val;
            char suit;
            if (vector.get(i)>14) val=vector.get(i)-9;
            else val=vector.get(i);
            if (val==10) value='t';
            else if (val==11) value='j';
            else if (val==12) value='q';
            else if (val==13) value='k';
            else if (val==14) value='a';
            else value=Character.forDigit(val,10);
            if (vector.get(i+1)==1) suit='h';
            else if (vector.get(i+2)==1) suit='d';
            else if (vector.get(i+3)==1) suit='c';
            else suit='s';
            vectorArrayList.add(new Card(value,suit));
            i+=5;
        }
        return vectorArrayList;
    }
}
