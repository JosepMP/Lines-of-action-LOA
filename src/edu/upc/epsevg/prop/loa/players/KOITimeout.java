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

    String _name;               //Variable global con el nombre de nuestro jugador.      
    private CellType _mi_color; //Variable global con el color de nuestro jugador. 
    private Move _movElegido, _moveAux;   //Variable global con el movimiento que elegiremos para nuestro juego.
    int _nodosExp;              //Variable global con el contador de nodos que exploramos.
    boolean _time;              //Variable global que controla si estamos dentro del tiempo de juego o no.
    

    
    int[][] tableroP = new int[][]{ //8 x 8
                    {-80, -25, -20, -20, -20, -20, -25, -80},
                    {-25,  10,  10,  10,  10,  10,  10,  -25},
                    {-20,  10,  25,  25,  25,  25,  10,  -20},
                    {-20,  10,  25,  50,  50,  25,  10,  -20},
                    {-20,  10,  25,  50,  50,  25,  10,  -20},
                    {-20,  10,  25,  25,  25,  25,  10,  -20},
                    {-25,  10,  10,  10,  10,  10,  10,  -25},
                    {-80, -25, -20, -20, -20, -20, -25, -80}
            };
    
    int[][] tableroP10 = new int[][]{ //10 x 10
                    {-80, -25, -20, -20, -20, -20, -20, -20 , -25, -80},
                    {-25,  10,  10,  10,  10,  10,  10, 10 , 10, -25},
                    {-20,  10,  25,  25,  25,  25, 25, 25, 10, -20},
                    {-20,  10,  25,  50,  50,  50,  25, 25 , 10, -20},
                    {-20,  10,  25,  50,  50,  50,  25, 25 , 10, -20},
                    {-20,  10,  25,  50,  50,  50, 2525 , 10, -20},
                     {-20,  10,  25,  50,  50,  50,  25, 25 , 10, -20},
                    {-20,  10,  25,  25,  25,  25, 25, 25, 10, -20},
                    {-25,  10,  10,  10,  10,  10,  10, 10 , 10, -25},
                    {-80, -25, -20, -20, -20, -20, -20, -20 , -25, -80},
            };
    
    public KOITimeout(String name ) {
        this._name = name;
        
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
        _nodosExp = 0;
        _time = true;
        
        int profundidad = 1;        
        while(_time) {      //Si estamos dentro del tiempode juego volvemos a hacer otra llamada al algorimto minimax con mas profundidad.
            minimax(aux, _mi_color, profundidad);   //Llama a la funcion minimax
            if(_time) {     //Si acabamos la busqueda dentro del tiempo damos por valido el mejor movimiento.   
                _movElegido = _moveAux;
            }
            profundidad++;
        }
     
        long nodos = (long) _nodosExp; 
        _movElegido.setNumerOfNodesExplored(nodos);
        return _movElegido;
    }
   
     
    
    /**
     * Inicia el algoritmo minmax.
     * 
     *
     * @param tauler Tablero y estado actual del juego.
     * @param color El color de nuestro jugador.
     * @param profundidad Profundidad a la que a de llegar el algoritmo.
     * @return el mejor valor heuristico para nuestro jugador.
     */    
    public int minimax(GameStatus tauler, CellType color, int profundidad){
         
        
        int mejorVal = Integer.MIN_VALUE;       //mejorVal guarda la mejor valoracion obtenida hasta el momento
         
        Move moveAux;
        ArrayList<Point> fichasAliadas = new ArrayList<>();
        int numAliadas = tauler.getNumberOfPiecesPerColor(color);
        for(int i=0; i<numAliadas; i++) {       //Guardamos en una lista de Points las posiciones de las fichas aliadas.    
            fichasAliadas.add(tauler.getPiece(color, i));
            
        }
        
        ArrayList<Move> movimientos = new ArrayList<>();       
        
        for(int i = 0; i < fichasAliadas.size(); i++) {     //Para cada ficha de la lista de fichas Aliadas cogeremos sus movimientos      
            ArrayList<Point> movimientosFicha = tauler.getMoves(fichasAliadas.get(i));    
        
            for(int j = 0; j < movimientosFicha.size(); j++) {    //Guardamos en una lista de Move cada movimiento de cada ficha aliada                   
                moveAux = new Move(tauler.getPiece(color,i), movimientosFicha.get(j), _nodosExp, profundidad, SearchType.MINIMAX);
                movimientos.add(moveAux);             
                           
           }    
        }
        
        for(Move m  : movimientos){             //Bucle para cada Move de cada ficha Aliada
            
            GameStatus aux = new GameStatus(tauler);        //Creamos un tablero auxiliar y añadimos el movimiento actual
            aux.movePiece(m.getFrom(), m.getTo());
            _nodosExp++;
            
            if(aux.GetWinner() == color ) {
                _movElegido = m;
                return mejorVal;
            }
           
          
            int valor = min(aux, opposite(color), Integer.MIN_VALUE, 
                                  Integer.MAX_VALUE, profundidad-1);     //Obtenemos la valoración obtenida al ejecutar el algoritmo min

            if(_time == false) {
                break;
            }
            
            if(valor >= mejorVal){  //Si la valoracion "valor" supera la mejorValoracion la substituimos por esta
                mejorVal = valor;
                _moveAux = m;
            }
        }
        
        
        return mejorVal;    //Return de la mejor valoracion para cada jugada.
    }
    
     /**
     * Ejecución del max del algoritmo minimax
     * 
     * @param tauler Tablero de juego
     * @param color El color de nuestro jugador
     * @param alpha Alpha del algoritmo minimax
     * @param beta  Beta del algoritmo minmax
     * @param profundidad Profundidad a la que nos encontramos en este punto del algoritmo.
     * @return valor heuristico Maximo.
     */
    private int max(GameStatus tauler, CellType color, int alpha, int beta, int profundidad){
        int mejorVal = Integer.MIN_VALUE; //mejorVal guarda la mejor valoracion obtenida hasta el momento
        
        Move moveAux;  
        ArrayList<Point> fichasAliadas = new ArrayList<>();
        int numAliadas = tauler.getNumberOfPiecesPerColor(color);
        for(int i=0; i<numAliadas; i++) {          //Guardamos en una lista de Points las posiciones de las fichas aliadas.   
            fichasAliadas.add(tauler.getPiece(color, i));
            
        }
        
        ArrayList<Move> movimientos = new ArrayList<>();       
        
        for(int i = 0; i < fichasAliadas.size(); i++) {   //Para cada ficha de la lista de fichas Aliadas cogeremos sus movimientos        
            ArrayList<Point> movimientosFicha = tauler.getMoves(fichasAliadas.get(i));       
        
            for(int j = 0; j < movimientosFicha.size(); j++) {    //Guardamos en una lista de Move cada movimiento de cada ficha aliada                   
                moveAux = new Move(tauler.getPiece(color,i), movimientosFicha.get(j), _nodosExp, profundidad, SearchType.MINIMAX);
                movimientos.add(moveAux);             
                           
           }    
        }
        
        if(tauler.GetWinner() == color ) {      //Comprobamos si tenemos tablero ganador           
            return Integer.MAX_VALUE;
                    
        }  else if(profundidad == 0 ){          //Comprobamos si estamos en profundidad 0 para calcular heuristica
            return evaluar(tauler);
            
        } else{
            for(Move m : movimientos){      //Bucle para cada Move de cada ficha Aliada
                
                GameStatus aux = new GameStatus(tauler);    //Creamos un tablero auxiliar y añadimos el movimiento actual
                aux.movePiece(m.getFrom(), m.getTo());
                _nodosExp++;
                
                if(tauler.GetWinner() == color) {   //Comprobamos si el ultimo movimiento añadido es ganador.    
                   return Integer.MAX_VALUE;
                }  

                
                int valor = min(aux, opposite(color), alpha, beta, profundidad - 1);    //Obtenemos la valoración obtenida al ejecutar el algoritmo min
               
                if(valor >= mejorVal) { //Si la valoracion "valor" supera la mejorValoracion la substituimos por esta
                   mejorVal = valor;
                   alpha = Math.max(alpha, valor);

                   if(beta <= alpha ) {     //Hacemos la poda alpha-beta 

                       break;
                   }
                   
                  if(_time == false) {
                      break;
                  }
               }
            }
            
            return alpha;
        }
    }

    /**
     * Ejecución del min del algoritmo minimax
     * 
     * @param tauler Tablero de juego
     * @param color El color de nuestro jugador
     * @param alpha Alpha del algoritmo minimax
     * @param beta  Beta del algoritmo minmax
     * @param profundidad Profundidad a la que nos encontramos en este punto del algoritmo.
     * @return valor heuristico Minimo.
     */
    private int min(GameStatus tauler, CellType color , int alpha, int beta, int profundidad) { 
        
        int mejorVal = Integer.MAX_VALUE;       //mejorVal guarda la peor valoracion obtenida hasta el momento
        
        Move moveAux;
        ArrayList<Point> fichasEnemigas = new ArrayList<>();
        int numEnemigas = tauler.getNumberOfPiecesPerColor(color);
        for(int i=0; i<numEnemigas; i++) {      //Guardamos en una lista de Points las posiciones de las fichas enemigas.   
            fichasEnemigas.add(tauler.getPiece(color, i));
            
        }
        
        ArrayList<Move> movimientos = new ArrayList<>();       
        
        for(int i = 0; i < fichasEnemigas.size(); i++) {        //Para cada ficha de la lista de fichas enemiga cogeremos sus movimientos         
            ArrayList<Point> movimientosFicha = tauler.getMoves(fichasEnemigas.get(i));       
        
            for(int j = 0; j < movimientosFicha.size(); j++) {      //Guardamos en una lista de Move cada movimiento de cada ficha enemiga                             
                moveAux = new Move(tauler.getPiece(color,i), movimientosFicha.get(j), _nodosExp, profundidad, SearchType.MINIMAX);
                movimientos.add(moveAux);             
                           
           }    
        }
     
       
        if(tauler.GetWinner() == color ) {  //Comprobamos si tienen tablero ganador                       
            return Integer.MIN_VALUE;
                    
        }  else if(profundidad == 0 ){      //Comprobamos si estamos en profundidad 0 para calcular heuristica
            return evaluar(tauler);
            
        } else{
            for(Move m : movimientos){      //Bucle para cada Move de cada ficha Aliada
                
                GameStatus aux = new GameStatus(tauler);    //Creamos un tablero auxiliar y añadimos el movimiento actual
                aux.movePiece(m.getFrom(), m.getTo());
                _nodosExp++;
                        
                if(tauler.GetWinner() == color) {   //Comprobamos si el ultimo movimiento añadido es ganador. 
     
                   return Integer.MIN_VALUE;
                   
                }              
                
                
                int valor = max(aux, opposite(color), alpha, beta, profundidad - 1);    //Obtenemos la valoración obtenida al ejecutar el algoritmo max
                
                if(valor <= mejorVal) {     //Si la valoracion "valor" es peor que la mejorValoracion la substituimos por esta
                  mejorVal = valor;             
                  beta = Math.min(beta, valor);

                  if(beta <= alpha ) {     //Hacemos la poda alpha-beta                  
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
    

    
      /**
     * Funcion para evaluar la heuristica de un tablero en su estado.
     * 
     * @param tauler Tablero de juego con un estado de juego
     * @return valor heuristico del tablero en base al analisis de fichas aliadas y enemigas.
     */
    public int evaluar(GameStatus tauler) {
        
        CellType color = _mi_color; //Guardamos el color de las piezas aliadas
        
        ArrayList<Point> fichasEnemigas = new ArrayList<>();
        int numEnemigas = tauler.getNumberOfPiecesPerColor(opposite(color));
        for(int i=0; i<numEnemigas; i++) {  //Guardamos en una lista de Points las posiciones de las fichas enemigas.
            fichasEnemigas.add(tauler.getPiece(opposite(color), i));
            
        }
        
        ArrayList<Point> fichasAliadas = new ArrayList<>();
        int numAliadas = tauler.getNumberOfPiecesPerColor(color);
        for(int i=0; i<numAliadas; i++) {   //Guardamos en una lista de Points las posiciones de las fichas aliadas.
            fichasAliadas.add(tauler.getPiece(color, i));
            
        }
        
        
        int heuAliada = -distanciaPiezas(fichasAliadas) //LLamamos a la funcion distanciaPiezas para las fichas aliadas
                            + valorTablero(fichasAliadas)    //Llamamos a la funcion valorTablero para las fichas aliadas
                                + movimientos(fichasAliadas, tauler);   //Llamamos a la funcion de movimientos para las fichas aliadas.
        
        int heuEnemiga = -distanciaPiezas(fichasEnemigas) //LLamamos a la funcion distanciaPiezas para las fichas enemigas
                            + valorTablero(fichasEnemigas)      //Llamamos a la funcion valorTablero para las fichas enemigas
                                + movimientos(fichasEnemigas, tauler) ;     //Llamamos a la funcion de movimientos para las fichas enemigas.
        
        
        int heuristicaTotal = heuAliada - heuEnemiga;   //Sacamos una heuristica restando la heuristica aliada con la enemiga calculada anteriormente.
        
        
        return heuristicaTotal;
    }
 

   /**
   * Funcion que nos da valor cuantos mas movimientos puede realizar nuestras fichas.
   * 
   * @param fichasPos Lista de la posicion de las fichas de un equipo
   * @param tauler Tablero de juego con un estado de juego
   * @return valor segun el numero de movimientos de un equipo.
   */
   public int movimientos(ArrayList<Point> fichasPos, GameStatus tauler) {
       int numMovimientos = 0;
       for(Point i : fichasPos) { //Recorremos cada ficha
           
           ArrayList<Point> movimientosFicha = tauler.getMoves(i);  
        
            for(int j = 0; j < movimientosFicha.size(); j++) {   //Añadimos un movimiento por cada movimiento de una ficha                         
                numMovimientos++;            
                           
           } 
       }
       return numMovimientos * 3;   
   }
   
   /**
   * Funcion que nos da valor segun la posicion donde tiene las fichas un equipo.
   * 
   * @param fichasPos Lista de la posicion de las fichas de un equipo
   * @return valor segun la posicion de nuestras fichas en el tablero.
   */
   public int valorTablero(ArrayList<Point> fichasPos) {
       int valor = 0;
       for(Point i : fichasPos) {   //Recorremos para cada ficha
           int x1 = i.x;
           int x2 = i.y;
           valor += tableroP[x1][x2];   //Valor de cada celda del tablero siguiendo tableroP 
           
       }
       valor = valor *2 ;
            
       return valor;
   }
   
   
   /**
   * Funcion que nos da valor segun las distancias que hay entre las diferentes piezas de un equipo.
   * 
   * @param fichasPos Lista de la posicion de las fichas de un equipo
   * @return valor segun la distancias de las diferentes piezas de un equipo.
   */
    public int distanciaPiezas(ArrayList<Point> fichasPos) {
        int distancia = 0;
       
        for(Point i : fichasPos) {  //Recorremos para cada pieza de un equipo
            for(Point j : fichasPos) {  //Comparamos cada pieza con todas las demas piezas del tablero de un equipo
                if(i != j) {
                    int x1 = i.x;   //Cogemos las posiciones x , y de ambas fichas de comparamos
                    int y1 = i.y;
                    int x2 = j.x;
                    int y2 = j.y;
                    double dist = Math.sqrt(Math.pow((y2 - y1),2) 
                                                + Math.pow((x2 - x1), 2));  //Calculamos la distancia restando las mismas coordenadas de cada pieza                
                                                                            //y hacemos la suma de las potencias de estos valores y relizamos
                                                                            //la raiz para obtener un valor para la diferencia de distancia entre piezas
                    
                    distancia += (int) dist;  //Sumamos estos valores en una misma variable                   
                                                                                    
                }
            }
        }        
        
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
        return "KOI(" + _name + ")";
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




