package DurakGame;

import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static DurakGame.conn.conn;
import static DurakGame.conn.resSet;
import static DurakGame.conn.statmt;

/**
 * Created by igor on 09.11.16.
 */
public class TrainedAgentPlayer extends Player {
    public TrainedAgentPlayer(){
        TrainedAgentPlayer.count++;
        Game.count=0;
        this.name="TrainedAgentPlayer"+TrainedAgentPlayer.count;
        try{
            DurakGame.conn.Conn();
            DurakGame.conn.statmt = conn.createStatement();
            //TrainedAgentPlayer.writeTxdIntoDB("/home/igor/Downloads/MyJavaTest/games.txd");
            //TrainedAgentPlayer.makeClustering();
           /* ArrayList<Card> attack=new ArrayList<>();
            attack.add(new Card('6','h'));
            attack.add(new Card('6','d'));
            this.hand.add(new Card('7','h'));
            this.hand.add(new Card('7','d'));
            this.hand.add(new Card('7','c'));
            this.hand.add(new Card('8','h'));
            this.hand.add(new Card('8','s'));
            this.hand.add(new Card('a','h'));
            ArrayList<Card> csdgsd=new ArrayList<>();
            csdgsd.add(new Card('7','s'));
            csdgsd.add(new Card('a','d'));
            ArrayList<Card> attack1=this.attack(csdgsd,'s');
            for (Card card:
                 attack1) {
                System.out.println(card);
            }*/
            //DurakGame.conn.CloseDB();
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    ArrayList<Card> attack(ArrayList<Card> cardsOnTable, char trump) throws SQLException{
        //defining possible attacks list
        ArrayList<CardVector> possibleAttacks = new ArrayList<>();
        ArrayList<Card> tmp=new ArrayList<>();
        ArrayList<Card> retainedHand=new ArrayList<>();
        retainedHand.addAll(this.hand);
        int in=0;
        if (!(cardsOnTable.size()==0)){
            for (Card cardOnHand:
                 this.hand) {
                for (Card cardOnTable:
                     cardsOnTable) {
                    if (cardOnHand.value==cardOnTable.value) in=1;
                }
                if (in==0) retainedHand.remove(cardOnHand);
                else in=0;
            }
        }


            for (int i = 0; i <retainedHand.size() ; i++) {
                tmp.add(retainedHand.get(i));
                possibleAttacks.add(new CardVector(tmp,trump));
                for (int j = i+1; j < retainedHand.size(); j++) {
                    if (retainedHand.get(i).value==retainedHand.get(j).value) {
                        tmp.add(retainedHand.get(j));
                        possibleAttacks.add(new CardVector(tmp, trump));
                        for (int k = j+1; k <retainedHand.size(); k++) {
                            if (retainedHand.get(j).value==retainedHand.get(k).value){
                                tmp.add(retainedHand.get(k));
                                possibleAttacks.add(new CardVector(tmp,trump));
                                for (int l = k+1; l <retainedHand.size() ; l++) {
                                    if (retainedHand.get(k).value==retainedHand.get(l).value) {
                                        tmp.add(retainedHand.get(l));
                                        possibleAttacks.add(new CardVector(tmp, trump));
                                    }
                                }
                                tmp.clear();
                            }
                        }
                        tmp.clear();
                    }
                }
                tmp.clear();
            }

        //reading cluster centres
        ArrayList<CardVector> attackClusterCentres = new ArrayList<>();
        ArrayList<Integer> attackClusterCentresIDs=new ArrayList<>();
        ArrayList<Double> avgRewards=new ArrayList<>();
        resSet = statmt.executeQuery("SELECT * FROM clusterCentresAttacks");
        while(resSet.next())
        {
            attackClusterCentresIDs.add(resSet.getInt("attackClusterid"));
            ArrayList<Card> hand0=new ArrayList<>();
            String card1 = resSet.getString("card1");
            if (card1!=null) hand0.add(new Card(card1.charAt(1),card1.charAt(0)));
            String card2 = resSet.getString("card2");
            if (card2!=null) hand0.add(new Card(card2.charAt(1),card2.charAt(0)));
            String card3 = resSet.getString("card3");
            if (card3!=null) hand0.add(new Card(card3.charAt(1),card3.charAt(0)));
            String card4 = resSet.getString("card4");
            if (card4!=null) hand0.add(new Card(card4.charAt(1),card4.charAt(0)));
            attackClusterCentres.add(new CardVector(hand0,trump));
            avgRewards.add(resSet.getDouble("avgreward"));
        }



        CardVector bestAttack=null;
        double bestAttackExpextedReward=-100;
        CardVector nearestClusterCentre=null;
        double distToNearestCentre=10000;

        for (CardVector possibleAttack:
             possibleAttacks) {
            //find nearest centre & define max avgreward
            for (CardVector attackClusterCentre:
                 attackClusterCentres) {
                if (possibleAttack.distTo(attackClusterCentre)<distToNearestCentre) {
                    nearestClusterCentre=attackClusterCentre;
                    distToNearestCentre=possibleAttack.distTo(attackClusterCentre);
                }
            }
            if (avgRewards.get(attackClusterCentres.indexOf(nearestClusterCentre))>bestAttackExpextedReward){
                bestAttack=possibleAttack;
                bestAttackExpextedReward=avgRewards.get(attackClusterCentres.indexOf(nearestClusterCentre));
            }
        }
        ArrayList<Card> handDuplicate=new ArrayList<>();
        handDuplicate.addAll(hand);
        for (Card att:
             bestAttack.toArrayList()) {
            for (Card card:
                 handDuplicate) {
                if (att.value==card.value&&att.suit==card.suit) this.hand.remove(card);
            }
        }
        return bestAttack.toArrayList();
    }

    @Override
    ArrayList<Card> defend(ArrayList<Card> attack, char trump) throws SQLException{
        //defining possible defences list
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

        if (possibleDefences.size()==0) return null;
        //reading cluster centres
        ArrayList<CardVector> defenceClusterCentres = new ArrayList<>();
        ArrayList<Integer> defenceClusterCentresIDs=new ArrayList<>();
        ArrayList<Double> avgRewards=new ArrayList<>();
        resSet = statmt.executeQuery("SELECT * FROM clusterCentresDefences");
        while(resSet.next())
        {
            defenceClusterCentresIDs.add(resSet.getInt("defenceClusterid"));
            ArrayList<Card> hand0=new ArrayList<>();
            String card1 = resSet.getString("card1");
            if (card1!=null) hand0.add(new Card(card1.charAt(1),card1.charAt(0)));
            String card2 = resSet.getString("card2");
            if (card2!=null) hand0.add(new Card(card2.charAt(1),card2.charAt(0)));
            String card3 = resSet.getString("card3");
            if (card3!=null) hand0.add(new Card(card3.charAt(1),card3.charAt(0)));
            String card4 = resSet.getString("card4");
            if (card4!=null) hand0.add(new Card(card4.charAt(1),card4.charAt(0)));
            defenceClusterCentres.add(new CardVector(hand0,trump));
            avgRewards.add(resSet.getDouble("avgreward"));
        }


        CardVector bestDefence=null;
        double bestDefenceExpextedReward=-100;
        CardVector nearestClusterCentre=null;
        double distToNearestCentre=10000;

        for (CardVector possibleDefence:
                possibleDefences) {
            //find nearest centre & define max avgreward
            for (CardVector defenceClusterCentre:
                    defenceClusterCentres) {
                if (possibleDefence.distTo(defenceClusterCentre)<distToNearestCentre) {
                    nearestClusterCentre=defenceClusterCentre;
                    distToNearestCentre=possibleDefence.distTo(defenceClusterCentre);
                }
            }
            if (avgRewards.get(defenceClusterCentres.indexOf(nearestClusterCentre))>bestDefenceExpextedReward){
                bestDefence=possibleDefence;
                bestDefenceExpextedReward=avgRewards.get(defenceClusterCentres.indexOf(nearestClusterCentre));
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
    void takeCard(Card card) {
        this.hand.add(card);
    }

    @Override
    int getCardValue(Card card) {
        if (card==null) return 100;
        else return card.valueInt;
    }
    private static void writeTxdIntoDB(String path) throws SQLException{
        int linesCount=0;
        String prevLine="     ";
        char trump='1';
        String line;


        ArrayList<String> hand0=new ArrayList<>();
        ArrayList<String> hand1=new ArrayList<>();
        ArrayList<String> attack0=new ArrayList<>();
        ArrayList<String> attack1=new ArrayList<>();
        ArrayList<String> defence0=new ArrayList<>();
        ArrayList<String> defence1=new ArrayList<>();
        ArrayList<String> cardsOnTable=new ArrayList<>();
        conn con=new conn();
        int attacker=0;
        try {
            InputStream is = new FileInputStream(path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            DurakGame.conn.Conn();
            DurakGame.conn.statmt = conn.createStatement();
            resSet=statmt.executeQuery("select seq from sqlite_sequence where name='attacks'");
            resSet.next();
            int attackid=resSet.getInt("seq");
            int startAttackid=attackid;
            ArrayList<String> oldHandAttack=new ArrayList<>();
            resSet=statmt.executeQuery("select seq from sqlite_sequence where name='defences'");
            resSet.next();
            int defenceid=resSet.getInt("seq");
            int startDefenceid=defenceid;
            ArrayList<String> oldHandDefence=new ArrayList<>();


            while ((line = reader.readLine()) != null/* && linesCount<1000*/) {
                if (line.substring(0,4).equals("hand")&&line.charAt(4)=='0') {
                    hand0.clear();
                    hand0.add(line.substring(6,8));
                    hand0.add(line.substring(8,10));
                    hand0.add(line.substring(10,12));
                    hand0.add(line.substring(12,14));
                    hand0.add(line.substring(14,16));
                    hand0.add(line.substring(16));

                }
                else if (line.substring(0,4).equals("hand")&&line.charAt(4)=='1') {
                    hand1.clear();
                    hand1.add(line.substring(6,8));
                    hand1.add(line.substring(8,10));
                    hand1.add(line.substring(10,12));
                    hand1.add(line.substring(12,14));
                    hand1.add(line.substring(14,16));
                    hand1.add(line.substring(16));
                }
                else if (line.substring(0,4).equals("trum")) {
                    trump=line.charAt(6);
                    attack0.clear();
                    attack1.clear();
                    defence0.clear();
                    defence1.clear();
                }
                else if (((line.charAt(2)!='+'&& prevLine.charAt(2)=='+')||prevLine.substring(0,3).equals("tru") )&& line.charAt(0)=='0') {
                    attacker=0;
                    startAttackid=attackid;
                    attack0.add(line.substring(2,4));
                    cardsOnTable.add(line.substring(2,4));
                }
                else if (((line.charAt(2)!='+'&& prevLine.charAt(2)=='+')||prevLine.substring(0,3).equals("tru") ) && line.charAt(0)=='1') {
                    attacker=1;
                    startAttackid=attackid;
                    attack1.add(line.substring(2,4));
                    cardsOnTable.add(line.substring(2,4));

                }
                else if (line.substring(2,4).equals("hv") && line.charAt(0)=='0'){
                    if (!prevLine.substring(2,4).equals("be")) {attacker=1;startAttackid=attackid;}
                    //write attack & hand for 0 & change hand for 0 & set attack0=null
                    attackid++;
                    oldHandAttack.clear();
                    oldHandAttack.addAll(hand0);
                    writeAttackIntoDB(hand0,attack0,0,trump);
                    System.out.println(linesCount);
                    System.out.println("hand0(trump is "+trump+"):");//
                    for (String s:
                         hand0) {
                        System.out.print(s+" ");
                    }
                    System.out.println();
                    System.out.println("attack0:");
                    for (String s:
                         attack0) {
                        System.out.print(s+" ");
                    }
                    System.out.println();//
                    hand0.removeAll(attack0);
                    attack0.clear();
                    //write defence & hand for 1 & change hand for 1 & set defence1=null
                    defenceid++;
                    oldHandDefence.clear();
                    oldHandDefence.addAll(hand1);
                    writeDefenceIntoDB(hand1,defence1,0,trump);
                    System.out.println(linesCount);
                    System.out.println("hand1(trump is "+trump+"):");//
                    for (String s:
                            hand1) {
                        System.out.print(s+" ");
                    }
                    System.out.println();
                    System.out.println("defence1:");
                    for (String s:
                            defence1) {
                        System.out.print(s+" ");
                    }
                    System.out.println();//
                    defence1.clear();
                    cardsOnTable.clear();
                }
                else if (line.substring(2,4).equals("hv") && line.charAt(0)=='1'){
                    if (!prevLine.substring(2,4).equals("be")) {attacker=0;startAttackid=attackid;}
                    //write attack & hand for 1 & change hand for 1 & set attack1=null
                    attackid++;
                    oldHandAttack.clear();
                    oldHandAttack.addAll(hand1);
                    writeAttackIntoDB(hand1,attack1,0,trump);
                    System.out.println(linesCount);
                    System.out.println("hand1(trump is "+trump+"):");//
                    for (String s:
                            hand1) {
                        System.out.print(s+" ");
                    }
                    System.out.println();
                    System.out.println("attack1:");
                    for (String s:
                            attack1) {
                        System.out.print(s+" ");
                    }
                    System.out.println();//
                    hand1.removeAll(attack1);
                    attack1.clear();
                    //write defence & hand for 0 & change hand for 0 & set attack0=null
                    defenceid++;
                    oldHandDefence.clear();
                    oldHandDefence.addAll(hand0);
                    writeDefenceIntoDB(hand0,defence0,0,trump);
                    System.out.println(linesCount);
                    System.out.println("hand0(trump is "+trump+"):");//
                    for (String s:
                            hand0) {
                        System.out.print(s+" ");
                    }
                    System.out.println();
                    System.out.println("defence0:");
                    for (String s:
                            defence0) {
                        System.out.print(s+" ");
                    }
                    System.out.println();//
                    hand0.removeAll(defence0);
                    defence0.clear();
                    cardsOnTable.clear();
                }
                else if ((prevLine.length()==4||prevLine.substring(2,4).equals("be") ||prevLine.substring(2,4).equals("hv"))&& line.charAt(0)=='0' && attacker==0 && line.length()==4){
                    if (attack0.size()==0 || attack0.get(attack0.size()-1).charAt(1)==line.charAt(3)) attack0.add(line.substring(2,4));
                    else {//write attack & hand for 0 & change hand for 0 & set attack0=null & .add(line.substring(2,3))
                        attackid++;
                        oldHandAttack.clear();
                        oldHandAttack.addAll(hand0);
                        writeAttackIntoDB(hand0,attack0,0,trump);
                        System.out.println(linesCount);
                        System.out.println("hand0(trump is "+trump+"):");//
                        for (String s:
                                hand0) {
                            System.out.print(s+" ");
                        }
                        System.out.println();
                        System.out.println("attack0:");
                        for (String s:
                                attack0) {
                            System.out.print(s+" ");
                        }
                        System.out.println();//
                        hand0.removeAll(attack0);
                        attack0.clear();
                        attack0.add(line.substring(2,4));
                    }
                }
                else if ((prevLine.length()==4||prevLine.substring(2,4).equals("be") ||prevLine.substring(2,4).equals("hv"))&& line.charAt(0)=='1' && attacker==1 && line.length()==4){
                    if (attack1.size()==0 || attack1.get(attack1.size()-1).charAt(1)==line.charAt(3)) attack1.add(line.substring(2,4));
                    else {//write attack & hand for 1 & change hand for 1 & set attack1=null & .add(line.substring(2,3))
                        attackid++;
                        oldHandAttack.clear();
                        oldHandAttack.addAll(hand1);
                        writeAttackIntoDB(hand1,attack1,0,trump);
                        System.out.println(linesCount);
                        System.out.println("hand1(trump is "+trump+"):");//
                        for (String s:
                                hand1) {
                            System.out.print(s+" ");
                        }
                        System.out.println();
                        System.out.println("attack1:");
                        for (String s:
                                attack1) {
                            System.out.print(s+" ");
                        }
                        System.out.println();//
                        hand1.removeAll(attack1);
                        attack1.clear();
                        attack1.add(line.substring(2,4));
                        cardsOnTable.add(line.substring(2,4));
                    }
                }
                else if (line.substring(2,4).equals("be")&& line.charAt(0)=='0') {
                    defence0.clear();
                    hand0.addAll(cardsOnTable);
                }
                else if (line.substring(2,4).equals("be")&& line.charAt(0)=='1') {
                    defence1.clear();
                    hand1.addAll(cardsOnTable);
                }
                else if (line.charAt(2)=='+'&& line.charAt(0)=='0'){
                    for (int i = 2; i <line.length()-1 ; i++) {
                        hand0.add(line.substring(i+1,i+3));
                        i+=2;
                    }
                    if (prevLine.charAt(2)=='+') {
                        updateReward(startAttackid,attackid,0,0,
                                HandInfo.getAttackReward(new HandInfo(oldHandAttack,trump),new HandInfo(hand1,trump),
                                        new HandInfo(oldHandDefence,trump), new HandInfo(hand0,trump)),false);
                        updateReward(0,0,startDefenceid,defenceid,
                                HandInfo.getDefenceReward(new HandInfo(oldHandAttack,trump),new HandInfo(hand1,trump),
                                        new HandInfo(oldHandDefence,trump), new HandInfo(hand0,trump)),false);
                    }
                }
                else if (line.charAt(2)=='+'&& line.charAt(0)=='1'){
                    for (int i = 2; i <line.length()-1 ; i++) {
                        hand1.add(line.substring(i+1,i+3));
                        i+=2;
                    }
                    if (prevLine.charAt(2)=='+') {
                        updateReward(startAttackid,attackid,0,0,
                                HandInfo.getAttackReward(new HandInfo(oldHandAttack,trump),new HandInfo(hand0,trump),
                                        new HandInfo(oldHandDefence,trump), new HandInfo(hand1,trump)),false);
                        updateReward(0,0,startDefenceid,defenceid,
                                HandInfo.getDefenceReward(new HandInfo(oldHandDefence,trump),new HandInfo(hand0,trump),
                                        new HandInfo(oldHandDefence,trump), new HandInfo(hand1,trump)),false);
                    }
                }
                else if (prevLine.length()==4 && line.length()==4 && line.charAt(0)=='0' && attacker==1){
                    if (defence0.size()==0|| defence0.get(defence0.size()-1).charAt(1)==line.charAt(3)) defence0.add(line.substring(2,4));
                    else {
                        defenceid++;
                        oldHandDefence.clear();
                        oldHandDefence.addAll(hand0);
                        writeDefenceIntoDB(hand0,defence0,0,trump);
                        hand0.removeAll(defence0);
                        defence0.clear();
                        defence0.add(line.substring(2,4));
                        cardsOnTable.add(line.substring(2,4));
                    }
                }
                else if (prevLine.length()==4 && line.length()==4 && line.charAt(0)=='1' && attacker==0){
                    if (defence1.size()==0|| defence1.get(defence1.size()-1).charAt(1)==line.charAt(3)) defence1.add(line.substring(2,4));
                    else {
                        defenceid++;
                        oldHandDefence.clear();
                        oldHandDefence.addAll(hand1);
                        writeDefenceIntoDB(hand1,defence1,0,trump);
                        hand0.removeAll(defence1);
                        defence1.clear();
                        defence1.add(line.substring(2,4));
                        cardsOnTable.add(line.substring(2,4));
                    }
                }


                prevLine=line;
                linesCount++;
            }
//            DurakGame.conn.CloseDB();
        }
        catch (Exception e) {
            //System.out.println(e);
            System.out.println(linesCount+"  "+prevLine+" "+trump);
            e.printStackTrace();
        }
    }
    private static void writeAttackIntoDB(ArrayList<String> hand,ArrayList<String> attack, int reward, char trump) throws SQLException {
        try{
            String sqlAttack="INSERT INTO 'attacks' ('trump',";
            String sqlAttack1=") VALUES ('"+trump+"',";
            for (int i = 0; (i <attack.size()&&i<4) ; i++) {
                sqlAttack+="'Card"+(i+1)+"',";
                sqlAttack1+="'"+attack.get(i)+"',";
            }
            sqlAttack+="'reward'";
            sqlAttack1+="'"+reward+"')";
            //DurakGame.conn.Conn();
            //DurakGame.conn.statmt = conn.createStatement();
            DurakGame.conn.statmt.execute(sqlAttack+sqlAttack1);

            resSet = statmt.executeQuery("SELECT max(attackid) id FROM attacks");
            resSet.next();
            int id=resSet.getInt("id");

            String sqlHand="INSERT INTO 'hands' ('attackid'";
            String sqlHand1=") VALUES ('"+id+"'";
            for (int i = 0; (i <hand.size()&&i<6) ; i++) {
                sqlHand+=",'Card"+(i+1)+"'";
                sqlHand1+=",'"+hand.get(i)+"'";
            }
            sqlHand1+=")";
            DurakGame.conn.statmt.execute(sqlHand+sqlHand1);
            //DurakGame.conn.CloseDB();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void writeDefenceIntoDB(ArrayList<String> hand,ArrayList<String> defence, int reward,char trump) throws SQLException {
        try{
            String sqlAttack="INSERT INTO 'defences' ('trump',";
            String sqlAttack1=") VALUES ('"+trump+"',";
            for (int i = 0; (i <defence.size()&&i<4) ; i++) {
                sqlAttack+="'Card"+(i+1)+"',";
                sqlAttack1+="'"+defence.get(i)+"',";
            }
            sqlAttack+="'reward'";
            sqlAttack1+="'"+reward+"')";
            //DurakGame.conn.Conn();
            //DurakGame.conn.statmt = conn.createStatement();
            DurakGame.conn.statmt.execute(sqlAttack+sqlAttack1);

            resSet = statmt.executeQuery("SELECT max(defenceid) id FROM defences");
            resSet.next();
            int id=resSet.getInt("id");

            String sqlHand="INSERT INTO 'hands' ('defenceid'";
            String sqlHand1=") VALUES ('"+id+"'";
            for (int i = 0; (i <hand.size()&&i<6) ; i++) {
                sqlHand+=",'Card"+(i+1)+"'";
                sqlHand1+=",'"+hand.get(i)+"'";
            }
            sqlHand1+=")";
            DurakGame.conn.statmt.execute(sqlHand+sqlHand1);
            //DurakGame.conn.CloseDB();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void makeClustering() throws SQLException,ClassNotFoundException{
        DurakGame.conn.Conn();
        DurakGame.conn.statmt = conn.createStatement();
        ArrayList<CardVector> allAttackVectors = new ArrayList<>();
        ArrayList<CardVector> allDefenceVectors = new ArrayList<>();
        ArrayList<Integer> allAttackIDs=new ArrayList<>();
        ArrayList<Integer> allDefenceIDs=new ArrayList<>();
        ArrayList<CardVector> attacksClusterCentres =new ArrayList<>();
        ArrayList<CardVector> defencesClusterCentres =new ArrayList<>();
        ArrayList<Integer> attacksClusterIDs=new ArrayList<>();
        ArrayList<Integer> defencesClusterIDs=new ArrayList<>();
        resSet = statmt.executeQuery("SELECT * FROM attacks");
        int numberOfAttacks=0;
        int numberOfClusters=30;
        while(resSet.next())
        {
            allAttackIDs.add(resSet.getInt("attackid"));
            char trump=resSet.getString("trump").charAt(0);
            ArrayList<Card> hand=new ArrayList<>();
            String card1 = resSet.getString("card1");
            if (card1!=null) hand.add(new Card(card1.charAt(1),card1.charAt(0)));
            String card2 = resSet.getString("card2");
            if (card2!=null) hand.add(new Card(card2.charAt(1),card2.charAt(0)));
            String card3 = resSet.getString("card3");
            if (card3!=null) hand.add(new Card(card3.charAt(1),card3.charAt(0)));
            String card4 = resSet.getString("card4");
            if (card4!=null) hand.add(new Card(card4.charAt(1),card4.charAt(0)));
            allAttackVectors.add(new CardVector(hand,trump));
            numberOfAttacks++;
        }
        resSet = statmt.executeQuery("SELECT * FROM defences");
        int numberOfDefences=0;
        while(resSet.next())
        {
            allDefenceIDs.add(resSet.getInt("defenceid"));
            char trump=resSet.getString("trump").charAt(0);
            ArrayList<Card> hand=new ArrayList<>();
            String card1 = resSet.getString("card1");
            if (card1!=null) hand.add(new Card(card1.charAt(1),card1.charAt(0)));
            String card2 = resSet.getString("card2");
            if (card2!=null) hand.add(new Card(card2.charAt(1),card2.charAt(0)));
            String card3 = resSet.getString("card3");
            if (card3!=null) hand.add(new Card(card3.charAt(1),card3.charAt(0)));
            String card4 = resSet.getString("card4");
            if (card4!=null) hand.add(new Card(card4.charAt(1),card4.charAt(0)));
            allDefenceVectors.add(new CardVector(hand,trump));
            numberOfDefences++;
        }


        Random random=new Random();
        /*char[] suits={'h','d','c','s'};
        for (int i = 0; i <numberOfAttacks ; i++) {                   //generating random hands
            ArrayList<Card> deck=Card.createDeck();
            ArrayList<Card> hand=new ArrayList<>();
            for (int j = 0; j <6 ; j++) {
                hand.add(Card.nextCard(deck));
            }
            CardVector cardVector=new CardVector(hand,suits[random.nextInt(4)]);
            allAttackVectors.add(cardVector);
        }*/
        Set<Integer> attackCentersIndexes=new HashSet<>();
        while (attackCentersIndexes.size() < numberOfClusters) {             //generating random centers
            attackCentersIndexes.add(random.nextInt(numberOfAttacks) );
        }
        for (Integer index:
                attackCentersIndexes) {
            allAttackVectors.get(index).clusterNumber=allAttackIDs.get(index);
            attacksClusterCentres.add(allAttackVectors.get(index));
            attacksClusterIDs.add(allAttackIDs.get(index));
        }

        Set<Integer> defenceCentersIndexes=new HashSet<>();
        while (defenceCentersIndexes.size() < numberOfClusters) {             //generating random centers
            defenceCentersIndexes.add(random.nextInt(numberOfDefences) );
        }
        for (Integer index:
                defenceCentersIndexes) {
            allDefenceVectors.get(index).clusterNumber=allDefenceIDs.get(index);
            defencesClusterCentres.add(allDefenceVectors.get(index));
            defencesClusterIDs.add(allDefenceIDs.get(index));
        }
        int i=0;
        do {
            for (int j = 0; j < numberOfAttacks; j++) {  //assigning centreIndexes for all hands as nearest centers indexes
                if (!attacksClusterCentres.contains(allAttackVectors.get(j))){
                    int centreIndex=0;
                    for (int k = 0; k < numberOfClusters; k++) {
                        if (allAttackVectors.get(j).distTo(attacksClusterCentres.get(centreIndex))>allAttackVectors.get(j).distTo(attacksClusterCentres.get(k)))
                            centreIndex=k;
                    }
                    allAttackVectors.get(j).clusterNumber=allAttackIDs.get(centreIndex);
                }
            }
            for (int j = 0; j < numberOfClusters; j++) { //calculating sum of distance to all in centre & defining new centre
                int minDistToAllInCluster=3186;
                for (int k = 0; k < numberOfAttacks; k++) {
                    if (allAttackVectors.get(k).clusterNumber==attacksClusterIDs.get(j)) {
                        for (int l = 0; l <numberOfAttacks; l++) {
                            if (allAttackVectors.get(l).clusterNumber==attacksClusterIDs.get(j))
                                allAttackVectors.get(k).distToAllInCluster+=Math.pow(allAttackVectors.get(k).distTo(allAttackVectors.get(l)),2);
                        }
                        if (minDistToAllInCluster>allAttackVectors.get(k).distToAllInCluster) {
                            minDistToAllInCluster=allAttackVectors.get(k).distToAllInCluster;
                            attacksClusterCentres.set(j,allAttackVectors.get(k));
                            attacksClusterIDs.set(j,allAttackIDs.get(k));
                        }
                    }
                }
            }
            i++;
        } while (i<200);

        do {
            for (int j = 0; j < numberOfDefences; j++) {  //assigning centreIndexes for all hands as nearest centers indexes
                if (!defencesClusterCentres.contains(allDefenceVectors.get(j))){
                    int centreIndex=0;
                    for (int k = 0; k < numberOfClusters; k++) {
                        if (allDefenceVectors.get(j).distTo(defencesClusterCentres.get(centreIndex))>allDefenceVectors.get(j).distTo(defencesClusterCentres.get(k)))
                            centreIndex=k;
                    }
                    allDefenceVectors.get(j).clusterNumber=allDefenceIDs.get(centreIndex);
                }
            }
            for (int j = 0; j < numberOfClusters; j++) { //calculating sum of distance to all in centre & defining new centre
                int minDistToAllInCluster=3186;
                for (int k = 0; k < numberOfDefences; k++) {
                    if (allDefenceVectors.get(k).clusterNumber==allDefenceIDs.get(j)) {
                        for (int l = 0; l <numberOfDefences; l++) {
                            if (allDefenceVectors.get(l).clusterNumber==allDefenceIDs.get(j))
                                allDefenceVectors.get(k).distToAllInCluster+=Math.pow(allDefenceVectors.get(k).distTo(allDefenceVectors.get(l)),2);
                        }
                        if (minDistToAllInCluster>allDefenceVectors.get(k).distToAllInCluster) {
                            minDistToAllInCluster=allDefenceVectors.get(k).distToAllInCluster;
                            defencesClusterCentres.set(j,allDefenceVectors.get(k));
                            defencesClusterIDs.set(j,allDefenceIDs.get(k));
                        }
                    }
                }
            }
            i++;
        } while (i<200);

        //adding cluster centres into DB
        statmt.execute("delete from clusterCentresAttacks");
        for(int j=0;j<numberOfAttacks;j++) {
            statmt.execute("update attacks set attackClusterid="+
                    allAttackVectors.get(j).clusterNumber+" where attackid="+allAttackIDs.get(j));
        }
        for (Integer attacksClusterId:
             attacksClusterIDs) {
            statmt.execute("insert into clusterCentresAttacks('attackClusterid','card1','card2','card3','card4','trump') " +
                    "select attackid, card1, card2, card3, card4, trump " +
                    "from attacks where attacks.attackid="+attacksClusterId);
        }
        statmt.execute("update clusterCentresAttacks " +
                "set avgreward=(select avg(reward) from attacks where attacks.attackClusterid=clusterCentresAttacks.attackClusterid)");





        statmt.execute("delete from clusterCentresDefences");

        for(int j=0;j<numberOfDefences;j++) {
            statmt.execute("update defences set defenceClusterid="+
                    allDefenceVectors.get(j).clusterNumber+" where defenceid="+allDefenceIDs.get(j));
        }
        for (Integer defencesClusterId:
                defencesClusterIDs) {
            statmt.execute("insert into clusterCentresDefences('defenceClusterid','card1','card2','card3','card4','trump') " +
                    "select defenceid, card1, card2, card3, card4, trump " +
                    "from defences where defences.defenceid="+defencesClusterId);
        }
        statmt.execute("update clusterCentresDefences " +
                "set avgreward=(select avg(reward) from defences where defences.defenceClusterid=clusterCentresDefences.defenceClusterid)");

        /*
        for (int j = 0; j <numberOfClusters ; j++) {
            System.out.println("Cluster "+j+". Centre is "+attacksClusterCentres.get(j)+"."+"Contains :");
            for (int k = 0; k <numberOfAttacks ; k++) {
                if (allAttackVectors.get(k).clusterNumber==j) System.out.println(allAttackVectors.get(k));
            }
            System.out.println();

        }*/


        //DurakGame.conn.CloseDB();
    }
    private static void updateReward(int startAttackid,int attackid,int startDefenceid,int defenceid, int newReward,boolean addToOldReward) throws SQLException{
        int newRewardforDB;
        if (attackid!=0){
            if (addToOldReward) {
                resSet = statmt.executeQuery("select reward from attacks where attackid=" + attackid);
                resSet.next();
                newRewardforDB=resSet.getInt("reward")+newReward;
            }
            else newRewardforDB=newReward;
            String attacks=new String();
            for (int i = startAttackid; i <attackid+1 ; i++) attacks+=","+i;
            statmt.execute("update attacks set reward="+newRewardforDB+" where attackid in ("+attacks.substring(2)+")");

        }
        else {
            if (addToOldReward){
                resSet = statmt.executeQuery("select reward from defences where defenceid=" + defenceid);
                resSet.next();
                newRewardforDB=resSet.getInt("reward")+newReward;
            }
            else newRewardforDB=newReward;
            String defences=new String();
            for (int i = startDefenceid; i <defenceid+1 ; i++) defences+=", "+i;
            statmt.execute("update defences set reward="+newRewardforDB+" where defenceid in ("+defences.substring(2)+")");
        }


    }
}
