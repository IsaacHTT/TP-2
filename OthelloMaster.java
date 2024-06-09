import java.awt.image.BufferedImage;
import java.awt.Color;
import javax.swing.JFrame;
import java.io.*;
import javax.swing.JOptionPane;
import java.awt.*;

public class OthelloMaster {
    public static void main(String[] args) {

        //creamos la interfaz inicial donde se escoge alguno de los dos modos indicados, hot seat o IA vs Humano, lo hacemos mediante botones y JOption
        String[] opciones = {"Hot Seat", "IA vs Humano"};
        int escogencia = JOptionPane.showOptionDialog(null,"Selecciona el modo de juego:","Othello",JOptionPane.DEFAULT_OPTION,JOptionPane.INFORMATION_MESSAGE,null,opciones,opciones[0]);

        if (escogencia == 0) {
            System.out.println("Modo de juego seleccionado: Hot Seat");
            ModoHotSeat(); //llamamos a la funcion que ejecuta el Modo Hot Seat
        } else if (escogencia == 1) {
            System.out.println("Modo de juego seleccionado: IA vs Human");
            modoIA();
        } else {
            System.out.println("No se selecciono un modo de juego.");
        }
    }

    public static final int ampli = 25; //factor por el que se multiplica el tamano del tablero para dar la imagen
    public static final Color verdeClaro = new Color(54,105,43);
    public static final int verdeClaroRGB = verdeClaro.getRGB();
    public static final Color verdeOscuro = new Color(92,190,72);
    public static final int verdeOscuroRGB = verdeOscuro.getRGB();

    private static void ModoHotSeat() {

        //iniciamos el modo de juego preguntando por el tamaño del tablero deseado, tambien con JOption 

        String[] tamanoTablero = {"8x8", "10x10", "10x14", "14x10"};
        String tamanoEscogido = (String) JOptionPane.showInputDialog(null,"Selecciona el tamaño del tablero:","Modo Hot Seat",JOptionPane.QUESTION_MESSAGE,null,tamanoTablero,tamanoTablero[0]);

        //Creamos un prueba de errores por si la persona no escoge ninguna opcion

        if (tamanoEscogido == null) {
            return;
        }
        //Creamos el tablero con la funcion crearTablero hecha mas adelante, segun el tamano seleccionado por el usuario anteriormente

        int[][] tablero = crearTablero(tamanoEscogido);


        //Ahora preguntamos por la escogencia del color para los jugadores, le preguntamos a uno y le asignamos al otro el color restante

        String[] opcionesColor = {"Blanco", "Negro"};
        int p1ColorEscogencia = JOptionPane.showOptionDialog(null,"Jugador 1, elige tu color:","Modo Hot Seat",JOptionPane.DEFAULT_OPTION,JOptionPane.INFORMATION_MESSAGE,null,opcionesColor,opcionesColor[0]);

        int p1Color = p1ColorEscogencia == 0 ? 1 : 0;
        int p2Color = p1Color == 1 ? 0 : 1;

        // Dasmos un mensaje para que sepan que se determino el color a corde a como se selecciono

        JOptionPane.showMessageDialog(null, "Jugador 1 ha elegido " + opcionesColor[p1ColorEscogencia] + ".\nJugador 2 será " + opcionesColor[1 - p1ColorEscogencia]);

        //Una vez asignadas los colores, inicializamos el tablero, con la funcion iniciarTablero, declarada mas adelante

        iniciarTablero(tablero);


        //Ahora imprimimos el tablero con la funcion imprimirTablero tambien declarada mas adelante

        imprimirTablero(tablero);
        interfaz(tablero);

        // Ahora sí se comienza el juego, declarando el fin de juego como falso y determinando que el jugador con color negro empieza siempre 
        boolean gameOver = false;
        int jugadorActual = (p1Color == 0) ? p1Color : p2Color; // Jugador con color negro empienza siempre

        //Creamos un contador para el while que determina si el juego a terminado o no, el cual es el contador de turnos jugados

        int turnoCont = 0;

        while (!gameOver) {
            boolean movimientoValido = false; 

            // Si el juego no ha terminado siginifica que aun se pueden mover piezas de algun jugador


            while (!movimientoValido) {

                //Como hay piezas que se pueden mover, preguntamos sobre que posicion quiere poner las piezas al jugador que le toca

                String posicion = JOptionPane.showInputDialog(null, "Jugador " + (jugadorActual == p1Color ? "1" : "2") + " (" + (jugadorActual == 1 ? "Blanco" : "Negro") + "), ingresa tu movimiento (fila,columna) o 500,500 para terminar:");

                //En los siguientes condicionales se hace prueba de que el dato proporcionado sea correcto o cumpla con los requisitos, es decir, a prueba de error

                if (posicion == null || posicion.isEmpty()) {
                    continue;
                }

                if (posicion.equals("500,500")) {
                    //
                    movimientoValido = true;
                    jugadorActual = (jugadorActual == 1) ? 0 : 1;
                    break;
                }

                //convertimos la entrada en un array separado por coma

                String[] pos = posicion.split(",");

                //Condicional anti errores por parte del usuario, si no mete un dato con dos digitos, sale mensaje de error

                if (pos.length != 2) {
                    JOptionPane.showMessageDialog(null, "Entrada inválida. Por favor, ingresa la posición en el formato fila,columna.");
                    continue;
                }

                //Ahora vamos a verificar si la posicion dada es valida para poner la ficha, o si dio un comando distinto a lo esperado
                //Como usamos matrices, creamos filas y columnas para verificar dichos datos.

                try {
                    int row = Integer.parseInt(pos[0].trim()) - 1;
                    int col = Integer.parseInt(pos[1].trim()) - 1;
                    movimientoValido = colocarFicha(tablero, row, col, jugadorActual, 0); //usamos la funcion colocarFciha, nos da un valor boleano y con ello sabemos si la posicion es valida

                    //cremos los condicionales anti errores para los datos dados

                    if (!movimientoValido) {
                        JOptionPane.showMessageDialog(null, "Movimiento inválido, intenta de nuevo.");
                    } else {
                        imprimirTablero(tablero);
                        jugadorActual = (jugadorActual == 1) ? 0 : 1;
                        interfaz(tablero);
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Entrada inválida, intenta de nuevo.");
                }
            }

            gameOver = endGameOver(tablero);
            turnoCont++;
        }

        //hacemos un conteo de los puntos cuando ya se haya acabado el juego

        int p1puntos = contadorPuntos(tablero, p1Color);
        int p2puntos = contadorPuntos(tablero, p2Color);
        String mensajeWinner = "Juego terminado.\nPuntos Jugador 1 (" + (p1Color == 1 ? "Blanco" : "Negro") + "): " + p1puntos +"\nPuntos Jugador 2 (" + (p2Color == 1 ? "Blanco" : "Negro") + "): " + p2puntos;JOptionPane.showMessageDialog(null, mensajeWinner);
    }

    //modo IA

    public static void modoIA(){
        String[] tamanoTablero = {"8x8", "10x10", "10x14", "14x10"};
        String tamanoEscogido = (String) JOptionPane.showInputDialog(null,"Selecciona el tamaño del tablero:","Modo Hot Seat",JOptionPane.QUESTION_MESSAGE,null,tamanoTablero,tamanoTablero[0]);

        //Creamos un prueba de errores por si la persona no escoge ninguna opcion

        if (tamanoEscogido == null) {
            return;
        }
        //Creamos el tablero con la funcion crearTablero hecha mas adelante, segun el tamano seleccionado por el usuario anteriormente

        int[][] tablero = crearTablero(tamanoEscogido);

        //Ahora preguntamos por la escogencia del color para los jugadores, le preguntamos a uno y le asignamos al otro el color restante

        String[] opcionesColor = {"Blanco", "Negro"};
        int p1ColorEscogencia = JOptionPane.showOptionDialog(null,"Jugador, elige tu color:","Modo Humano vs IA",JOptionPane.DEFAULT_OPTION,JOptionPane.INFORMATION_MESSAGE,null,opcionesColor,opcionesColor[0]);

        int p1Color = p1ColorEscogencia == 0 ? 1 : 0;
        int p2Color = p1Color == 1 ? 0 : 1;

        // Dasmos un mensaje para que sepan que se determino el color a corde a como se selecciono

        JOptionPane.showMessageDialog(null, "Jugador ha elegido " + opcionesColor[p1ColorEscogencia] + ".\nLa IA será " + opcionesColor[1 - p1ColorEscogencia]);

        //Una vez asignadas los colores, inicializamos el tablero, con la funcion iniciarTablero, declarada mas adelante

        iniciarTablero(tablero);

        //Ahora imprimimos el tablero con la funcion imprimirTablero tambien declarada mas adelante

        imprimirTablero(tablero);
        interfaz(tablero);

        // Ahora sí se comienza el juego, declarando el fin de juego como falso y determinando que el jugador con color negro empieza siempre 
        boolean gameOver = false;
        //int jugadorActual = (p1Color == 0) ? p1Color : p2Color; // Jugador con color negro empienza siempre
        //System.out.println(p1Color); //SI ESCOGE BLANCO, P1COLOR ES 1

        int jugadorActual;
        if(p1Color == 1){
            jugadorActual = 1; //0 usuario, 1 ia
        }
        else{
            jugadorActual = 0;
        }

        //Creamos un contador para el while que determina si el juego a terminado o no, el cual es el contador de turnos jugados

        int turnoCont = 0;

        while (!gameOver) {
            boolean movimientoValido = false; 

            // Si el juego no ha terminado siginifica que aun se pueden mover piezas de algun jugador


            while (!movimientoValido) {

                //Como hay piezas que se pueden mover, preguntamos sobre que posicion quiere poner las piezas al jugador que le toca
                if(jugadorActual == 0){

                String posicion = JOptionPane.showInputDialog(null, "Jugador " + " (" + (p1Color == 1 ? "Blanco" : "Negro") + "), ingresa tu movimiento (fila,columna) o 500,500 para terminar:");

                //En los siguientes condicionales se hace prueba de que el dato proporcionado sea correcto o cumpla con los requisitos, es decir, a prueba de error

                if (posicion == null || posicion.isEmpty()) {
                    continue;
                }

                if (posicion.equals("500,500")) {
                    movimientoValido = true;
                    jugadorActual = (jugadorActual == 1) ? 0 : 1;
                    break;
                }

                //convertimos la entrada en un array separado por coma

                String[] pos = posicion.split(",");

                //Condicional anti errores por parte del usuario, si no mete un dato con dos digitos, sale mensaje de error

                if (pos.length != 2) {
                    JOptionPane.showMessageDialog(null, "Entrada inválida. Por favor, ingresa la posición en el formato fila,columna.");
                    continue;
                }

                //Ahora vamos a verificar si la posicion dada es valida para poner la ficha, o si dio un comando distinto a lo esperado
                //Como usamos matrices, creamos filas y columnas para verificar dichos datos.

                try {
                    int row = Integer.parseInt(pos[0].trim()) - 1;
                    int col = Integer.parseInt(pos[1].trim()) - 1;
                    movimientoValido = colocarFicha(tablero, row, col, p1Color, 0); //usamos la funcion colocarFciha, nos da un valor boleano y con ello sabemos si la posicion es valida

                    //cremos los condicionales anti errores para los datos dados

                    if (!movimientoValido) {
                        JOptionPane.showMessageDialog(null, "Movimiento inválido, intenta de nuevo.");
                    } else {
                        imprimirTablero(tablero);
                        jugadorActual = (jugadorActual == 1) ? 0 : 1;
                        interfaz(tablero);
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Entrada inválida, intenta de nuevo.");
                }
                }

                else{
                    int[][] opciones = new int[tablero.length*tablero[0].length][2];
                    int n = 0;
                    for(int i = 1; i <= 14; i++){
                        for(int j = 1; j <= 14; j++){
                            if(colocarFicha(tablero, j-1, i-1, 1 - p1Color, 1)){
                                opciones[n][0] = j-1;
                                opciones[n][1] = i-1;
                                n += 1;
                            }
                        }
                    }
                     if(opciones != null){
                    int[] opcion = opciones[(int)(Math.random()*n)];
                    colocarFicha(tablero, opcion[0], opcion[1], 1-p1Color, 0);
                    imprimirTablero(tablero);
                    interfaz(tablero);
                    jugadorActual = (jugadorActual == 1) ? 0 : 1;
                    }
                    else{
                   
                        JOptionPane.showMessageDialog(null, "La IA no puede poner fichas");
                        jugadorActual = (jugadorActual == 1) ? 0 : 1;
                    }
                    
                }
                
            }

            gameOver = endGameOver(tablero);
            turnoCont++;
        }

        //hacemos un conteo de los puntos cuando ya se haya acabado el juego

        int p1puntos = contadorPuntos(tablero, p1Color);
        int p2puntos = contadorPuntos(tablero, p2Color);
        String mensajeWinner = "Juego terminado.\nPuntos Jugador 1 (" + (p1Color == 1 ? "Blanco" : "Negro") + "): " + p1puntos +"\nPuntos Jugador 2 (" + (p2Color == 1 ? "Blanco" : "Negro") + "): " + p2puntos;JOptionPane.showMessageDialog(null, mensajeWinner);
    }


    //funcion para crear tablero, usada en el main, recibe lo seleccionado por el usuario para crear el tablero.

    private static int[][] crearTablero(String size) {
        int rows = 8;
        int cols = 8;

        switch (size) {
            case "8x8":
                rows = 8;
                cols = 8;
                break;
            case "10x10":
                rows = 10;
                cols = 10;
                break;
            case "10x14":
                rows = 10;
                cols = 14;
                break;
            case "14x10":
                rows = 14;
                cols = 10;
                break;
            default:
                break;
        }

        //Asignamos el valor de 2 a todas las colunmans lo cual significa que es vacia

        int[][] tablero = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                tablero[i][j] = 2;
            }
        }

        return tablero;
    }


    //funcion para dar la posicion inicial en el juego, dado en las especificaciones

    private static void iniciarTablero(int[][] tablero) {
        int rows = tablero.length;
        int cols = tablero[0].length;

        tablero[rows / 2 - 1][cols / 2 - 1] = 1; // Blanco
        tablero[rows / 2][cols / 2] = 1; // Blanco
        tablero[rows / 2 - 1][cols / 2] = 0; // Negro
        tablero[rows / 2][cols / 2 - 1] = 0; // Negro
    }

    //funcion para imprimir una matriz dada, vamos a estar imprimiendo en cada turno por lo que es mejor crearlo como metodo y no estarlo programando en cada momento

    private static void imprimirTablero(int[][] tablero) {
        for (int[] row : tablero) {
            for (int entrada : row) {
                System.out.print((entrada == 2 ? "." : (entrada == 1 ? "B" : "N")) + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    //funcion colocarFicha, recibe como datos el tablero en general para obtener toda la info de las fichas ya puestas, recibe las cordenadas de la nueva ficha que se va a analizar y el color de la misma

    private static boolean colocarFicha(int[][] tablero, int row, int col, int color, int prueba) {

        //condicional anti errores por datos dados por el usuario

        if (row < 0 || row >= tablero.length || col < 0 || col >= tablero[0].length || tablero[row][col] != 2) {
            return false;
        }

        // Creamos la variable movimientoValido, la cual se retornara como la validadcion de que la ficha sí puede estar en la posicion dada, la inicializamos
        //en falso, ademas damos una lista de la direccion de validacion que haremos a cada casilla

        boolean movimientoValido = false;
        int colorOponente = (color == 1) ? 0 : 1;
        int[][] direccionesValidacion = {{-1, -1}, {-1, 0}, {-1, 1},{0, -1},{0, 1},{1, -1}, {1, 0}, {1, 1}};

        //Comenzamos a alternar en todas direcciones de validacion para saber si encontramos fichas del otro jugador/oponente

        for (int[] dir : direccionesValidacion) {
            int r = row + dir[0];
            int c = col + dir[1];
            boolean oponenteEncontrado = false;

            while (r >= 0 && r < tablero.length && c >= 0 && c < tablero[0].length && tablero[r][c] == colorOponente) {
                r += dir[0];
                c += dir[1];
                oponenteEncontrado = true;
            }

            //Si tenemos al prinicio y al final fichas nuestras, y en medio fichas enemigas entonces hacemos cambio de color, con la variable flip, que cambia unicamente el color de la ficha

            if (oponenteEncontrado && r >= 0 && r < tablero.length && c >= 0 && c < tablero[0].length && tablero[r][c] == color) {
                movimientoValido = true;
                if(prueba == 0){
                int flipR = row + dir[0];
                int flipC = col + dir[1];
                while (flipR != r || flipC != c) {
                    tablero[flipR][flipC] = color;
                    flipR += dir[0];
                    flipC += dir[1];
                }
                }
            }
        }

        if (movimientoValido && prueba == 0) {
            tablero[row][col] = color;
        }

        //retornamos el valor de true o false si el campo solicitado es valido para la ficha

        return movimientoValido;
    }


    //determina si las entradas del tablero estan vacias, si lo estan el juego termina

    private static boolean endGameOver(int[][] tablero) {
        for (int[] row : tablero) {
            for (int entrada : row) {
                if (entrada == 2) {
                    return false;
                }
            }
        }
        return true;
    }
    //Creamos la funcion contadorPuntos, para hacer un conteo entrada por entrada hacindo sumas de cuantos puntos tiene cada jugador, en este caso solo de uno, el resto se saca con el complemento
    private static int contadorPuntos(int[][] tablero, int color) {
        int puntos = 0;
        for (int[] row : tablero) {
            for (int entrada : row) {
                if (entrada == color) {
                    puntos++;
                }
            }
        }
        return puntos;
    }

    private static void interfaz(int[][] tablero){
        


       
        BufferedImage tablon = new BufferedImage(tablero[0].length*ampli,(tablero.length+1)*ampli,BufferedImage.TYPE_INT_RGB);
       for(int i=0; i<tablero[0].length; i++){
            for(int j=0; j<tablero.length; j++){
                switch(tablero[j][i]){
                        case 0:
                        for(int k=0; k<=ampli-1; k++){
                            for(int p=0; p<=ampli-1; p++){
                                tablon.setRGB(i*ampli+k, (j+1)*ampli+p, Color.BLACK.getRGB());
                            }
                        }
                        break;
                        case 1:
                        for(int k=0; k<=ampli-1; k++){
                            for(int p=0; p<=ampli-1; p++){
                                tablon.setRGB(i*ampli+k, (j+1)*ampli+p, Color.WHITE.getRGB());
                            }
                        }
                        break;
                        default:
                        for(int k=0; k<=ampli-1; k++){
                            for(int p=0; p<=ampli-1; p++){
                                if(i%2==j%2){
                                    tablon.setRGB(i*ampli+k, (j+1)*ampli+p, verdeOscuroRGB);
                                }
                                else{
                                    tablon.setRGB(i*ampli+k, (j+1)*ampli+p, verdeClaroRGB);
                                }                            
                            }
                        }
                        }  
                    }
                }
            
            
       

    JFrame f = new JFrame("Othello"){
      public void paint(java.awt.Graphics g){
        g.drawImage(tablon,0,0,null);
      } 
    };
    Image img = Toolkit.getDefaultToolkit().getImage("logo.png");
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    f.setSize(tablero[0].length*ampli,tablero.length*ampli + 30);
    f.repaint();
    f.setVisible(true);
    f.setIconImage(img);
    f.toFront();
    }
    
}
