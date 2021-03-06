package edu.upc.epsevg.prop.loa;

import edu.upc.epsevg.prop.loa.Level;
import edu.upc.epsevg.prop.loa.IPlayer;
import edu.upc.epsevg.prop.loa.players.*;

import javax.swing.SwingUtilities;

/**
 * Lines Of Action: el joc de taula.
 * @author bernat
 */
public class Game {
        /**
     * @param args
     */
    public static void main(String[] args) {
        
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
            
                //IPlayer player1 = new KOI("Messi",2);
                //IPlayer player1 = new KOITimeout("Dembele");
                IPlayer player1 = new KOIOptimizado("CR7",2);    
                IPlayer player2 = new MCCloudPlayer();
                //IPlayer player2 = new RandomPlayer("Roger");
                new Board(player1 , player2, 3, Level.DIFFICULT);
             }
        });
    }
}
