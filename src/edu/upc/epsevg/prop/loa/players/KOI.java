package edu.upc.epsevg.prop.loa.players;

import edu.upc.epsevg.prop.loa.CellType;
import static edu.upc.epsevg.prop.loa.CellType.*;
import edu.upc.epsevg.prop.loa.GameStatus;
import edu.upc.epsevg.prop.loa.IAuto;
import edu.upc.epsevg.prop.loa.IPlayer;
import edu.upc.epsevg.prop.loa.Move;
import edu.upc.epsevg.prop.loa.SearchType;
import java.awt.Point;
import java.util.Random;

import java.util.ArrayList;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Jugador humà de LOA
 * @author bernat
 */
public class KOI implements IPlayer, IAuto {

    String name;
    private CellType color;
    private int profundidad;
    private Move _movElegido;
    int nodosExp;
    
    
    public KOI(String name, int prof ) {
        this.name = name;
        this.profundidad = prof;
    }

    /**
     * Decideix el moviment del jugador donat un tauler i un color de peça que
     * ha de posar.
     *
     * @param s Tauler i estat actual de joc.
     * @return el moviment que fa el jugador.
     */
    @Override
    public Move move(GameStatus s) {
        GameStatus aux = new GameStatus(s);
        CellType color = s.getCurrentPlayer();
        minmax(aux, profundidad, color , true , -Integer.MAX_VALUE, Integer.MAX_VALUE);        
        return _movElegido;
    }
    
    public int minmax(GameStatus tauler, int profundidad, CellType color, boolean smove, int alpha, int beta) {
        System.out.println("Entra al minmax");
        if(tauler.GetWinner() != null) {
            if(tauler.GetWinner() == color ) {
                    return Integer.MAX_VALUE;
            } else if(tauler.GetWinner() == opposite(color)) {
                    return -Integer.MAX_VALUE;
            }
        } else if (profundidad == 0) {
            return evaluar(tauler);
        }
        
        if(color != this.color) {
            return max(tauler, profundidad, color, smove, alpha, beta );
        } else {
            return min(tauler, profundidad, opposite(color), smove, alpha, beta );
        }              
    }
    
   
    
    public int max(GameStatus tauler, int profundidad, CellType color, boolean smove, int alpha, int beta) {
        System.out.println("Entro max " );
        Move moveAux;
        int mejorTirada = Integer.MIN_VALUE;    
        GameStatus aux = new GameStatus(tauler);
        
        ArrayList<Point> fichasAliadas = new ArrayList<>();
        int numAliadas = tauler.getNumberOfPiecesPerColor(color);
        for(int i=0; i<numAliadas; i++) {
            fichasAliadas.add(tauler.getPiece(color, i));
            System.out.println("Fichas enemigas: " + tauler.getPiece(color,i) + " " + i);
        }
        
        ArrayList<Move> movimientos = new ArrayList<>();       
        
        for(int i = 0; i < fichasAliadas.size(); i++) {
            System.out.println("Movimiento posible: " + fichasAliadas.get(i) + " " + aux.getMoves(fichasAliadas.get(i)));
            ArrayList<Point> movimientosFicha = aux.getMoves(fichasAliadas.get(i));       
        
            for(int j = 0; j < movimientosFicha.size(); j++) {
                System.out.println(aux.getPiece(color,i) + " " +  movimientosFicha.get(j));        
                moveAux = new Move(aux.getPiece(color,i), movimientosFicha.get(j), nodosExp, profundidad, SearchType.MINIMAX);
                movimientos.add(moveAux);             
                           
           }    
        }
        
        System.out.println("Movimientos creados " );
       
        for(Move m : movimientos) {
            GameStatus aux2 = new GameStatus(aux);
            System.out.println(m.getFrom() + " " + m.getTo() );
            aux.movePiece(m.getFrom(), m.getTo());
            System.out.println(aux.toString());
            int valor = minmax(aux,profundidad-1,opposite(color),false,alpha,beta);
            aux = aux2;
            if(valor >= mejorTirada) {
                mejorTirada = valor;
                alpha = Math.max(alpha, valor);
                if(smove) {
                    _movElegido = m;
                }
                
                if(beta <= alpha ) {
                    break;
                }
            }
            
        }   
        
        return mejorTirada;
    }
    
     public int min(GameStatus tauler, int profundidad, CellType color, boolean smove, int alpha, int beta) {
        System.out.println("Entro min " );
        Move moveAux;
        int mejorTirada = Integer.MAX_VALUE;    
        GameStatus aux = new GameStatus(tauler);
        
        ArrayList<Point> fichasEnemigas = new ArrayList<>();
        int numEnemigas = tauler.getNumberOfPiecesPerColor(color);
        for(int i=0; i<numEnemigas; i++) {
            fichasEnemigas.add(tauler.getPiece(color, i));
            System.out.println("Fichas enemigas: " + tauler.getPiece(color,i) + " " + i);
        }
        
        ArrayList<Move> movimientos = new ArrayList<>();       
        
        for(int i = 0; i < fichasEnemigas.size(); i++) {
            System.out.println("Movimiento posible: " + fichasEnemigas.get(i) + " " + aux.getMoves(fichasEnemigas.get(i)));
            ArrayList<Point> movimientosFicha = aux.getMoves(fichasEnemigas.get(i));       
        
            for(int j = 0; j < movimientosFicha.size(); j++) {
                            
                moveAux = new Move(aux.getPiece(color,i), movimientosFicha.get(j), nodosExp, profundidad, SearchType.MINIMAX);
                movimientos.add(moveAux);             
                           
           }    
        }
       
        for(Move m : movimientos) {
            GameStatus aux2 = new GameStatus(aux);
            aux.movePiece(m.getFrom(), m.getTo());
            System.out.println(aux.toString());
            int valor = minmax(aux,profundidad-1,opposite(color),false,alpha,beta);
            aux = aux2;
            if(valor <= mejorTirada) {
                mejorTirada = valor;
                beta = Math.min(beta, valor);
                if(smove) {
                    _movElegido = m;
                }
                
                if(beta <= alpha ) {
                    break;
                }
            }
            
        }   
        
        return mejorTirada;
    }
    
    public int evaluar(GameStatus tauler) {
        return 1;
    }
    /**
     * Ens avisa que hem de parar la cerca en curs perquè s'ha exhaurit el temps
     * de joc.
     */
    @Override
    public void timeout() {
        // Bah! Humans do not enjoy timeouts, oh, poor beasts !
        System.out.println("Bah! You are so slow...");
    }

    /**
     * Retorna el nom del jugador que s'utlilitza per visualització a la UI
     *
     * @return Nom del jugador
     */
    @Override
    public String getName() {
        return "KOI(" + name + ")";
    }
}




