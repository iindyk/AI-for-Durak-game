package DurakGame;

import sun.text.normalizer.UTF16;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Created by igor on 28.09.16.
 */
public class Main {
    public static void main(String[] args){
        //SingleAgentPlayer Game test
        Player player1=new TrainedAgentPlayer();
        Player player2=new SimpleAgentPlayer();
        ArrayList<Player> players=new ArrayList<>();
        players.add(player1);
        players.add(player2);
        try{
            for (int i = 0; i <1000 ; i++) {
                Game game=new Game(players);
            }
            DurakGame.conn.CloseDB();
            System.out.println(Game.count);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        //DB connection test
        /*try{
            conn.Conn();
            conn.CreateDB();
            conn.WriteDB();
            conn.ReadDB();
            conn.CloseDB();
        }
        catch (Exception e) {
            System.out.println(e);
        }*/
        //Clustering test
        /**/
        //Player player=new TrainedAgentPlayer();
        /*try {
            InputStream is = new FileInputStream("/home/igor/Downloads/games.txd");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line =reader.readLine().substring(1,2);
            System.out.println(line.equals("ga"));
            System.out.println(line);
            System.out.println(reader.readLine());
        }
        catch (Exception e){
            e.printStackTrace();
        }*/





    }
}
