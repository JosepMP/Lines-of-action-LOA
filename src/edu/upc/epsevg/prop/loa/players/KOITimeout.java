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
public class KOITimeout implements IPlayer, IAuto {

    String name;
    private CellType _mi_color;
    //private int profundidad;
    private Move _movElegido, _moveAux;
    int nodosExp;
    boolean _time;
    
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
    
    public KOITimeout(String name ) {
        this.name = name;
        
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
        nodosExp = 0;
        _time = true;
        int profundidad = 1;
        System.out.println("Nosotros somos " + _mi_color + " enemigo es " + opposite(_mi_color));
        //minmax(aux, profundidad, _mi_color , true , Integer.MIN_VALUE, Integer.MAX_VALUE);
        while(_time) {
            minimax(aux, _mi_color, profundidad);
            if(_time) {
                _movElegido = _moveAux;
            }
            profundidad++;
        }
        System.out.println(nodosExp);
        long nodos = (long) nodosExp; 
        _movElegido.setNumerOfNodesExplored(nodos);
        return _movElegido;
    }
   
     
    
     public int minimax(GameStatus tauler, CellType color, int profundidad){
         
        //valoracionMaxima guarda la mejor valoracion obtenida hasta el momento
        int valoracionMaxima = Integer.MIN_VALUE;
        
         
        Move moveAux;
        ArrayList<Point> fichasAliadas = new ArrayList<>();
        int numAliadas = tauler.getNumberOfPiecesPerColor(color);
        for(int i=0; i<numAliadas; i++) {
            fichasAliadas.add(tauler.getPiece(color, i));
            //System.out.println("Fichas enemigas: " + tauler.getPiece(color,i) + " " + i);
        }
        
        ArrayList<Move> movimientos = new ArrayList<>();       
        
        for(int i = 0; i < fichasAliadas.size(); i++) {
            //System.out.println("Movimiento posible: " + fichasAliadas.get(i) + " " + aux.getMoves(fichasAliadas.get(i)));
            ArrayList<Point> movimientosFicha = tauler.getMoves(fichasAliadas.get(i));       
        
            for(int j = 0; j < movimientosFicha.size(); j++) {
                //System.out.println(aux.getPiece(color,i) + " " +  movimientosFicha.get(j));        
                moveAux = new Move(tauler.getPiece(color,i), movimientosFicha.get(j), nodosExp, profundidad, SearchType.MINIMAX);
                movimientos.add(moveAux);             
                           
           }    
        }


        for(Move m  : movimientos){
            //Creamos un tablero auxiliar y añadimos el movimiento actual
            GameStatus aux = new GameStatus(tauler);
            aux.movePiece(m.getFrom(), m.getTo());
            nodosExp++;
            
            if(aux.GetWinner() == color ) {
                _moveAux = m;
                return valoracionMaxima;
            }
           
            //Obtenemos la valoración obtenida al ejecutar el algoritmo min
            int valor = min(aux, opposite(color), Integer.MIN_VALUE, Integer.MAX_VALUE, profundidad-1, valoracionMaxima);

            //Si la valoración obtenida supera la valoración máxima guardada, substituimos la nueva valoración máxima
            if(_time == false) {
                break;
            }
            if(valor >= valoracionMaxima){
                valoracionMaxima = valor;
                _moveAux = m;
            }
        }
        
        //Retornamos la mejor columna obtenida tras ejecutar el algoritmo
        return valoracionMaxima;
    }

    /**
     * Ejecución del max del algoritmo minimax
     * 
     * @param tauler Tablero de juego
     * @param alpha Alpha del algoritmo minimax
     * @param beta Beta del algoritmo minimax
     * @param depth Profundidad
     * @param valoracionMaxima Mejor valoracion obtenida hasta el momento
     * @return El valor máximo
     */
    private int max(GameStatus tauler, CellType color, int alpha, int beta, int profundidad, int valoracionMaxima){
         int mejorTirada = Integer.MIN_VALUE; 
        Move moveAux;  
        ArrayList<Point> fichasAliadas = new ArrayList<>();
        int numAliadas = tauler.getNumberOfPiecesPerColor(color);
        for(int i=0; i<numAliadas; i++) {
            fichasAliadas.add(tauler.getPiece(color, i));
            //System.out.println("Fichas enemigas: " + tauler.getPiece(color,i) + " " + i);
        }
        
        ArrayList<Move> movimientos = new ArrayList<>();       
        
        for(int i = 0; i < fichasAliadas.size(); i++) {
            //System.out.println("Movimiento posible: " + fichasAliadas.get(i) + " " + aux.getMoves(fichasAliadas.get(i)));
            ArrayList<Point> movimientosFicha = tauler.getMoves(fichasAliadas.get(i));       
        
            for(int j = 0; j < movimientosFicha.size(); j++) {
                //System.out.println(aux.getPiece(color,i) + " " +  movimientosFicha.get(j));        
                moveAux = new Move(tauler.getPiece(color,i), movimientosFicha.get(j), nodosExp, profundidad, SearchType.MINIMAX);
                movimientos.add(moveAux);             
                           
           }    
        }
        //Comprobamos si es el final de la profundidad o si no podemos mover ficha
        if(tauler.GetWinner() == color ) {
                     //System.out.println("Aqui ganamos");
            return Integer.MAX_VALUE;
                    
        }  else if(profundidad == 0 ){
            return evaluar(tauler);
            
        } else{
            for(Move m : movimientos){
                //Creamos un tablero auxiliar y añadimos el movimiento actual
                GameStatus aux = new GameStatus(tauler);
                aux.movePiece(m.getFrom(), m.getTo());
                nodosExp++;
                //Si la jugada actual es ganadora, retornamos un +∞
                if(tauler.GetWinner() == color) {
    
                   return Integer.MAX_VALUE;
                }  

                //Llama la función min para analizar la siguiente jugada del rival
                int valor = min(aux, opposite(color), alpha, beta, profundidad - 1, valoracionMaxima);
                if(valor >= mejorTirada) {
                   mejorTirada = valor;
                   alpha = Math.max(alpha, valor);

                   if(beta <= alpha ) {

                       break;
                   }
                   if(_time == false) {
                       break;
                   }
               }
                /*alpha = Math.max(alpha, valor);
                
                //Aplicamos la poda alpha beta
                if(alpha >= beta){
                    break;
                }*/
            }
            
            return alpha;
        }
    }

    /**
     * Ejecución del min del algoritmo minimax
     * 
     * @param tauler Tablero de juego
     * @param alpha Alpha del algoritmo minimax
     * @param beta Beta del algoritmo minimax
     * @param depth Profundidad
     * @param valoracionMaxima Mejor valoracion obtenida hasta el momento
     * @return El valor mínimo
     */
    private int min(GameStatus tauler, CellType color , int alpha, int beta, int profundidad, int valoracionMaxima) { 
        
        int mejorTirada = Integer.MAX_VALUE; 
        Move moveAux;
        ArrayList<Point> fichasEnemigas = new ArrayList<>();
        int numEnemigas = tauler.getNumberOfPiecesPerColor(color);
        for(int i=0; i<numEnemigas; i++) {
            fichasEnemigas.add(tauler.getPiece(color, i));
            //System.out.println("Fichas enemigas: " + tauler.getPiece(color,i) + " " + i);
        }
        
        ArrayList<Move> movimientos = new ArrayList<>();       
        
        for(int i = 0; i < fichasEnemigas.size(); i++) {
            //System.out.println("Movimiento posible: " + fichasEnemigas.get(i) + " " + aux.getMoves(fichasEnemigas.get(i)));
            ArrayList<Point> movimientosFicha = tauler.getMoves(fichasEnemigas.get(i));       
        
            for(int j = 0; j < movimientosFicha.size(); j++) {
                            
                moveAux = new Move(tauler.getPiece(color,i), movimientosFicha.get(j), nodosExp, profundidad, SearchType.MINIMAX);
                movimientos.add(moveAux);             
                           
           }    
        }
        //Comprobamos si es el final de la profundidad o si no podemos mover ficha
       
        if(tauler.GetWinner() == color ) {
                     //System.out.println("Aqui ganamos");
            return Integer.MIN_VALUE;
                    
        }  else if(profundidad == 0 ){
            return evaluar(tauler);
            
        } else{
            for(Move m : movimientos){
                //Creamos un tablero auxiliar y añadimos el movimiento actual
                GameStatus aux = new GameStatus(tauler);
                aux.movePiece(m.getFrom(), m.getTo());
                nodosExp++;
                //Si la jugada actual es ganadora, retornamos un -∞          
                if(tauler.GetWinner() == color) { 
     
                   return Integer.MIN_VALUE;
                   
                }              
                
                //Llama la función max para analizar la siguiente jugada
                int valor = max(aux, opposite(color), alpha, beta, profundidad - 1, valoracionMaxima);
                if(valor <= mejorTirada) {
                  mejorTirada = valor;             
                  beta = Math.min(beta, valor);

                  if(beta <= alpha ) {                    
                      break;
                  }
                  
                   if(_time == false) {
                       break;
                   }                  
              }

            }
        }
        
        return beta;
    }
    

    
    public int evaluar(GameStatus tauler) {
        //System.out.println("Analizando heuristica de:");
        //System.out.println(tauler.toString());
        
        CellType color = _mi_color;
        
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
        
        int heuAliada = -distanciaPiezas(fichasAliadas) + valorTauler(fichasAliadas) + movimientos(fichasAliadas, tauler);
        int heuEnemiga = -distanciaPiezas(fichasEnemigas) + valorTauler(fichasEnemigas) + movimientos(fichasEnemigas, tauler) ;
        int heuristicaTotal = heuAliada - heuEnemiga;
        
        
        return heuristicaTotal;
    }
    
   public int movimientos(ArrayList<Point> fichasPos, GameStatus tauler) {
       int numMovimientos = 0;
       for(Point i : fichasPos) {
           
           ArrayList<Point> movimientosFicha = tauler.getMoves(i);  
        
            for(int j = 0; j < movimientosFicha.size(); j++) {                            
                numMovimientos++;            
                           
           } 
       }
       return numMovimientos * 3;
   }
   public int valorTauler(ArrayList<Point> fichasPos) {
       int valor = 0;
       for(Point i : fichasPos) {
           int x1 = i.x;
           int x2 = i.y;
           valor += tableroP[x1][x2];
           
       }
       valor = valor *2 ;
       //System.out.println("Valor " + valor);       
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
        
        //System.out.println("Distancia " + distancia);
        return distancia;
    }
    /**
     * Ens avisa que hem de parar la cerca en curs perquè s'ha exhaurit el temps
     * de joc.
     */
    @Override
    public void timeout() {
        // Bah! Humans do not enjoy timeouts, oh, poor beasts !
        _time = false;
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
    
    
    
    
    //Zobrist
    /*public int ProbeHash(int profundidad, int alpha, int beta) {
        HASHE * phase = &hash_table[ZobristKey() % TableSize()];
        if(phase->key== ZobristKey() ) {
            if (phase->profundidad >= profundidad) {
                if(phase->flags == hashfEXACT){
                    return phashe->val;
                }
                if((phase->flags == hashfALPHA) && (phase->val <= alpha)) {
                    return alpha;
                }
                if((phase->flags == hashfBETA) && (phashe->val >= beta)) {
                    return beta;
                }
            }
            RememberBestMove();
        }
        return valUNKNOWN;
    }
    
    void RecordHash(int profundidad, int val, int hashf) {
        HASHE * phase = &hash_table[ZobristKey() % TableSize()];
        
        phashe->key = ZobristKey();
        phashe->best = BestMove();
        phase->val= val;
        phashe->hashf = hashf;
        phashe->profundidad = profundidad;
    }*/
    
    


}




