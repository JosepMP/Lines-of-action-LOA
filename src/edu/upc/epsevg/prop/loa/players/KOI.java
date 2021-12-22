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
    private CellType _mi_color;
    private int profundidad;
    private Move _movElegido;
    int nodosExp;
    
    int[][] tableroP = new int[][]{
                    {-80, -25, -20, -20, -20, -20, -25, -80},
                    {-25,  10,  10,  10,  10,  10,  10,  -25},
                    {-20,  10,  25,  25,  25,  25,  10,  -20},
                    {-20,  10,  25,  50,  50,  25,  10,  -20},
                    {-20,  10,  25,  50,  50,  25,  10,  -20},
                    {-20,  10,  25,  25,  25,  25,  10,  -20},
                    {-25,  10,  10,  10,  10,  10,  10,  -25},
                    {-80, -25, -20, -20, -20, -20, -25, -80}
            };
    
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
        _mi_color = s.getCurrentPlayer();
        System.out.println("Nosotros somos " + _mi_color + " enemigo es " + opposite(_mi_color));
        minmax(aux, profundidad, _mi_color , true , -Integer.MAX_VALUE, Integer.MAX_VALUE);
        System.out.println(nodosExp);           
        return _movElegido;
    }
    
    public int minmax(GameStatus tauler, int profundidad, CellType color, boolean smove, int alpha, int beta) {
        System.out.println("Tablero actual con profunidad: " + profundidad );
        System.out.println(tauler.toString());
        if(tauler.isGameOver()) {
            System.out.println("Juego acabado");
        }
        if(tauler.GetWinner() != null) {
            if(tauler.GetWinner() == _mi_color ) {
                     System.out.println("Aqui ganamos");
                    return Integer.MAX_VALUE;
            } else if(tauler.GetWinner() ==  opposite(_mi_color)) {
                     System.out.println("Aqui ganan ellos");
                    return -Integer.MAX_VALUE;
            }
        } else if (profundidad == 0) {
            return evaluar(tauler);
        }
        
        if(color == _mi_color) {
            return max(tauler, profundidad, color, smove, alpha, beta );
        } else {
           
            return min(tauler, profundidad, color, smove, alpha, beta );
        }              
    }
    
   
    
    public int max(GameStatus tauler, int profundidad, CellType color, boolean smove, int alpha, int beta) {
        System.out.println("Entro max " + color);
        Move moveAux;
        int mejorTirada = Integer.MIN_VALUE;    
        GameStatus aux = new GameStatus(tauler);
        
        ArrayList<Point> fichasAliadas = new ArrayList<>();
        int numAliadas = tauler.getNumberOfPiecesPerColor(color);
        for(int i=0; i<numAliadas; i++) {
            fichasAliadas.add(tauler.getPiece(color, i));
            //System.out.println("Fichas enemigas: " + tauler.getPiece(color,i) + " " + i);
        }
        
        ArrayList<Move> movimientos = new ArrayList<>();       
        
        for(int i = 0; i < fichasAliadas.size(); i++) {
            //System.out.println("Movimiento posible: " + fichasAliadas.get(i) + " " + aux.getMoves(fichasAliadas.get(i)));
            ArrayList<Point> movimientosFicha = aux.getMoves(fichasAliadas.get(i));       
        
            for(int j = 0; j < movimientosFicha.size(); j++) {
                //System.out.println(aux.getPiece(color,i) + " " +  movimientosFicha.get(j));        
                moveAux = new Move(aux.getPiece(color,i), movimientosFicha.get(j), nodosExp, profundidad, SearchType.MINIMAX);
                movimientos.add(moveAux);             
                           
           }    
        }
        
        //System.out.println("Movimientos creados " );
       
        for(Move m : movimientos) {
            GameStatus aux2 = new GameStatus(aux);
            //System.out.println(m.getFrom() + " " + m.getTo() );
            aux.movePiece(m.getFrom(), m.getTo());
            nodosExp++;
            //System.out.println(aux.toString());
            int valor = minmax(aux, profundidad-1, opposite(color), false, alpha,beta);
            aux = aux2;
            if(valor >= mejorTirada) {
                mejorTirada = valor;
                alpha = Math.max(alpha, valor);
                if(smove) {
                    System.out.println("Mejor heuristica " + mejorTirada );
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
        System.out.println("Entro min " + color);
         
        Move moveAux;
        int mejorTirada = Integer.MAX_VALUE;    
        GameStatus aux = new GameStatus(tauler);
        
        ArrayList<Point> fichasEnemigas = new ArrayList<>();
        int numEnemigas = tauler.getNumberOfPiecesPerColor(color);
        for(int i=0; i<numEnemigas; i++) {
            fichasEnemigas.add(tauler.getPiece(color, i));
            //System.out.println("Fichas enemigas: " + tauler.getPiece(color,i) + " " + i);
        }
        
        ArrayList<Move> movimientos = new ArrayList<>();       
        
        for(int i = 0; i < fichasEnemigas.size(); i++) {
            //System.out.println("Movimiento posible: " + fichasEnemigas.get(i) + " " + aux.getMoves(fichasEnemigas.get(i)));
            ArrayList<Point> movimientosFicha = aux.getMoves(fichasEnemigas.get(i));       
        
            for(int j = 0; j < movimientosFicha.size(); j++) {
                            
                moveAux = new Move(aux.getPiece(color,i), movimientosFicha.get(j), nodosExp, profundidad, SearchType.MINIMAX);
                movimientos.add(moveAux);             
                           
           }    
        }
       
        for(Move m : movimientos) {
            System.out.println("Antes " );
            GameStatus aux2 = new GameStatus(aux);
            aux.movePiece(m.getFrom(), m.getTo());
            
            nodosExp++;
            //System.out.println(aux.toString());
            int valor = minmax(aux,profundidad-1, opposite(color) , false ,alpha,beta);
            System.out.println("Jugada enemiga con heuristica: " + valor );
            aux = aux2;
            if(valor <= mejorTirada) {
                mejorTirada = valor;
                beta = Math.min(beta, valor);
                
                if(smove) {
                    
                    System.out.println("Mejor heuristica " + mejorTirada );
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
        //System.out.println("Analizando heuristica de:");
        //System.out.println(tauler.toString());
        
        CellType color = tauler.getCurrentPlayer();
        
        ArrayList<Point> fichasEnemigas = new ArrayList<>();
        int numEnemigas = tauler.getNumberOfPiecesPerColor(opposite(color));
        for(int i=0; i<numEnemigas; i++) {
            fichasEnemigas.add(tauler.getPiece(opposite(color), i));
            
        }
        
        ArrayList<Point> fichasAliadas = new ArrayList<>();
        int numAliadas = tauler.getNumberOfPiecesPerColor(color);
        for(int i=0; i<numAliadas; i++) {
            fichasAliadas.add(tauler.getPiece(color, i));
            
        }
        
        int heuAliada = -distanciaPiezas(fichasAliadas) + valorTauler(fichasAliadas);
        int heuEnemiga = -distanciaPiezas(fichasEnemigas) + valorTauler(fichasEnemigas);
        int heuristicaTotal = heuAliada - heuEnemiga;
        
        
        return heuristicaTotal;
    }
    
   public int valorTauler(ArrayList<Point> fichasPos) {
       int valor = 0;
       for(Point i : fichasPos) {
           int x1 = i.x;
           int x2 = i.y;
           valor += tableroP[x1][x2];
           
       }
       valor = valor *2 ;
       System.out.println("Valor " + valor);       
       return valor;
   }
    public int cantidadFichas() {
       return 1; 
    }
    public int distanciaPiezas(ArrayList<Point> fichasPos) {
        int distancia = 0;
       
        for(Point i : fichasPos) {
            for(Point j : fichasPos) {
                if(i != j) {
                    int x1 = i.x;
                    int y1 = i.y;
                    int x2 = j.x;
                    int y2 = j.y;
                    double dist = Math.sqrt(Math.pow((y2 - y1),2) + Math.pow((x2 - x1), 2));                    
                    distancia += (int) dist;                    
                }
            }
        }
        
        System.out.println("Distancia " + distancia);
        return distancia;
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




