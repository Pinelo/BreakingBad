/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package breakingBad;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URL;
import java.util.LinkedList;
import javax.swing.JFrame;

/**
 *
 * @author Casa
 */


public final class Juego extends JFrame implements Runnable, KeyListener{

    /**
     * @param args the command line arguments
     */
    
    /* objetos para manejar el buffer del Applet y este no parpadee */
    private Image    imaImagenApplet;   // Imagen a proyectar en Applet	
    private Graphics graGraficaApplet;  // Objeto grafico de la Imagen
    private Entidad entBarra;          //Se crea la barra
    private LinkedList encBloques;  //lista de bloques
    private Entidad entBloque;     //objeto para obtener bloques indiviualmente
    private Entidad entProyectil;   //objeto para el proyectil
    private int iScore;                 //score del juego
    private SoundClip aucSonidoBloque; //sonido de impacto con bloque
    private SoundClip aucSonidoSuelo;  //sonido de impacto con suelo
    private int dirBarra;     //booleano indicativo de la direccion de la barra
    private Boolean dirProyectilY;   //indica si el proyectil sube o baja
    private Boolean dirProyectilX;   //indica si el proyectil se mueve a la derecha o izq
    private int iChoque;       //prevenir que se registre mas de un choque al mismo tiempo
    private Boolean bPausa;     //juego se pausa cuando este boolean sea verdadero
    private Animacion aniBloqueDestruido; //animacion que se corre cuando se destruye un bloque
    //longs para medir el tiempo entre las imagenes de la animacion
    private long tiempoActual;            
    private long tiempoInicial;
    //posicion donde se correra la animacion
    private int aniPosX;
    private int aniPosY;
    private Boolean bBloqueDestruido; //se destruyo un bloque y esta pendiente correr la animacion
    private int iContador;            //cuenta las imagenes despliegadas por la
                                      //animacion para saber cuando acaba
    
     public Juego() {
        init();
        start();
    }
     
    public void init() {
        // hago el applet de un tamaño 500,500
        setSize(1200, 800);
        
        iChoque = 0; //el es cero por defecto
        //por defecto score empieza en 0
        iScore =0;
        
        //no se ha corrido ninguna imagen de la animacion
        iContador = 0;
        
        //juego no esta pausado por defecto
        bPausa = false;
        
        //no se ha destruido ningun bloque
        bBloqueDestruido = false;
        
        //proyectil empieza moviendose a la derecha y arriba
        dirProyectilX = true;
        dirProyectilY = true;
        
        //la barra no se mueve al principio
        dirBarra = 0;

        //se crea sonido de impacto con bloque
        aucSonidoBloque = new SoundClip("wooho.wav");
        
        //se crea sonido de impacto con suelo
        aucSonidoSuelo = new SoundClip("d_oh.wav");
        
        
        // se crea imagen de la barra
        URL urlImagenBarra = this.getClass().getResource("barra.png");
        // se crea la barra
	entBarra = new Entidad(getWidth() / 2, getHeight(),
                Toolkit.getDefaultToolkit().getImage(urlImagenBarra));
        entBarra.setY(getHeight()-entBarra.getAlto());
        entBarra.setX(getWidth()/2-entBarra.getAncho());

         //velocidad de barra es 3
        entBarra.setVelocidad(17);
        
        //
        // se obtiene las imagenes para los bloques    
        URL urlImagenBloque = this.getClass().getResource("bloque.png");
        URL urlImagenBloque2 = this.getClass().getResource("bloque2.png");
        URL urlImagenBloque3 = this.getClass().getResource("bloque3.png");
        URL urlImagenBloque4 = this.getClass().getResource("bloque4.png");
        URL urlImagenBloque5 = this.getClass().getResource("bloque5.png");

        
        aniBloqueDestruido = new Animacion();
        aniBloqueDestruido.sumaCuadro(urlImagenBloque2, 100);
        aniBloqueDestruido.sumaCuadro(urlImagenBloque3, 100);
        aniBloqueDestruido.sumaCuadro(urlImagenBloque4, 100);
        aniBloqueDestruido.sumaCuadro(urlImagenBloque5, 100);
                   


        

        // se crea el arreglo de bloques
        encBloques = new LinkedList();
        int cantbloques = 39;
        int numFilas = 1; //contador de filas
        int numColumna = 0; //contador de columnas
        while(cantbloques >= 0) {
            cantbloques -=1;
            int posX = 1;    
            int posY = 1;
            entBloque = new Entidad(posX,posY,Toolkit.getDefaultToolkit().getImage(urlImagenBloque));
            //cada caminador tiene una velocidad al azar

            /*
                PosX y PosY dependen del tama;o del sprite, 
                esto va a cambiar despues.
            */
            entBloque.setX(numColumna*entBloque.getAncho());
            entBloque.setY(entBloque.getAlto()*numFilas);

            if(numColumna >= 9) {
                numColumna =0;
                numFilas++;
            }
            else {
                numColumna++;
            }
            encBloques.add(entBloque);
        }
        //
        
        // se obtiene la imagen para el proyectil    
        URL urlImagenProyectil = this.getClass().getResource("proyectil.png");
    // se establece la posicion inicial, su velocidad y se crea el objeto
    int posX = entBarra.getX();
    int posY = entBarra.getY();
    entProyectil = new Entidad(posX, posY, Toolkit.getDefaultToolkit().getImage(urlImagenProyectil));
    entProyectil.setY(entProyectil.getY() - entProyectil.getAlto());
    entProyectil.setVelocidad(7);
    //se establece la posicion del proyectil
        
        //se agrega keylistener para poder detectar el teclado
        addKeyListener(this);
    }
	
    public void start () {
        // Declaras un hilo
        Thread th = new Thread ((Runnable) this);
        // Empieza el hilo
        th.start ();
    }
    
   
    
    public void actualiza(){
        // instrucciones para actualizar Entidads
        
        
        //Determina el tiempo que ha transcurrido desde que el Applet 
        //inicio su ejecución
        long tiempoTranscurrido=System.currentTimeMillis() - tiempoActual;
        //Guarda el tiempo actual
        tiempoActual += tiempoTranscurrido;
        //Actualiza la animación en base al tiempo transcurrido
        aniBloqueDestruido.actualiza(tiempoTranscurrido);
        
        
        //se cambia la posicion de la barra dependiendo del booleano 'spacebar'
        if (dirBarra == 1) {
            entBarra.setX(entBarra.getX() + entBarra.getVelocidad());
        }
        else if(dirBarra == 2){
            entBarra.setX(entBarra.getX() - entBarra.getVelocidad());
        }

        //dirProyectilY determina si el proyectil sube o baka
       if (dirProyectilY) {
            entProyectil.setY(entProyectil.getY() + entProyectil.getVelocidad());
        }
        else {
            entProyectil.setY(entProyectil.getY() - entProyectil.getVelocidad());
        } 

        //dirProyectilX determina si el proyectil se mueve la derecha o izquierda
       if (dirProyectilX) {
            entProyectil.setX(entProyectil.getX() + entProyectil.getVelocidad());
        }
        else {
            entProyectil.setX(entProyectil.getX() - entProyectil.getVelocidad());
        } 

    }
    
    public void run () {
        // se realiza el ciclo del juego en este caso nunca termina
        
        
        //Guarda el tiempo actual del sistema 
        tiempoActual = System.currentTimeMillis();
            
        while (true) {
            /* mientras dure el juego, se actualizan posiciones de jugadores
               se checa si hubo colisiones para desaparecer jugadores o corregir
               movimientos y se vuelve a pintar todo
            */ 
                   
            //actualiza no se corre cuando se pausa el juego
            if(!bPausa) {
                actualiza();
            }
            iChoque++;        //se incrementa el contador
            checaColision();
            repaint();
            try	{
                // El thread se duerme.
                Thread.sleep (20);
            }
            catch (InterruptedException iexError)	{
                System.out.println("Hubo un error en el juego " + 
                        iexError.toString());
            }
	}
    }
    
    public void checaColision(){
        // instrucciones para checar colision y reacomodar Entidads si 
        // es necesario
        //la barra no se puede salir del cuadro
        if(entBarra.getX()+entBarra.getAncho()>=getWidth()) {
            entBarra.setX(getWidth()-entBarra.getAncho());
        }
        else if(entBarra.getY()+entBarra.getAlto()>getHeight()) {
            entBarra.setY(getHeight()-entBarra.getAlto());
        }
        else if(entBarra.getX() < 0) {
            entBarra.setX(0);
        }
        if(entBarra.getY() < 0) {
            entBarra.setY(0);
        }
       
        
        //no se puede salir el proyectil
        //se cambia la direccion si se sale del margen
        if (entProyectil.getX()<=0 || entProyectil.getX() + entProyectil.getAncho() > getWidth()) {
            dirProyectilX = !dirProyectilX;
        }

        if (entProyectil.getY()<=0 || entProyectil.getY() + entProyectil.getAlto() > getHeight()) {
            dirProyectilY = !dirProyectilY;
            iChoque = 0;
        }

        //si Bloque choca con Nena se aumenta el score
        //Contador de tiempo para que no se registren dos choques al mismo tiempo
        
        for (int i = 0; i < encBloques.size(); i++) {
                Entidad Bloque = (Entidad)encBloques.get(i);
                if(entProyectil.colisiona(Bloque) && iChoque >= 3) {
                    iChoque = 0;
                    iScore++;   //se aumenta el score
                    //se correra la animacion en la posicion del bloque destruido
                    aniPosX = Bloque.getX();
                    aniPosY = Bloque.getY();
                    iContador = 0;
                    
                    encBloques.remove(Bloque);
                    if(entProyectil.getY()<=entBloque.getY()+entBloque.getAlto() || 
                                entProyectil.getY() + entProyectil.getAlto() >= Bloque.getY()) {
                        dirProyectilY = !dirProyectilY;
                    }
                    else {
                        dirProyectilX = !dirProyectilX;
                    }
                        
                    //encBloques.remove(Bloque);   //se elimina el bloque con el que se colisiono                    
                    aucSonidoBloque.play();      //emite sonido
                }
        }
        
        if(entBarra.colisiona(entProyectil) && iChoque >= 10) {
            iChoque = 0;
             if( entProyectil.getY() + entProyectil.getAlto() >= entBarra.getY()) {
                dirProyectilY = !dirProyectilY;
             }
             else {
                dirProyectilX = !dirProyectilX;
             }
        }
    }
    
    public void keyReleased(KeyEvent e) {
        // si se suelta spacebar, barra se mueve hacia al izquierda
        if(e.getKeyCode() == KeyEvent.VK_RIGHT) {    
            dirBarra = 0;
        }
        else if(e.getKeyCode() == KeyEvent.VK_LEFT) {
            dirBarra = 0;
        }
    }

    public void keyPressed(KeyEvent e) {
        // si se presiona spacebar, barra se mueve hacia la derecha
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            dirBarra = 1;
        }
        else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            dirBarra = 2;
        }
        //si se acaba el juego, se reinicia al teclear 'n'
        else if(e.getKeyCode() == KeyEvent.VK_N && encBloques.size() == 0){
            init();
        }
        else if(e.getKeyCode() == KeyEvent.VK_P) {
            bPausa = !bPausa;
        
        }
    }
    
    public void paint (Graphics graGrafico){
        // Inicializan el DoubleBuffer
        if (imaImagenApplet == null){
                imaImagenApplet = createImage (this.getSize().width, 
                        this.getSize().height);
                graGraficaApplet = imaImagenApplet.getGraphics ();
        }
        
        
        //crea imagen para el background
        URL urlImagenFondo;
        Image imaImagenFondo;
        if(encBloques.size() > 0){
        urlImagenFondo = this.getClass().getResource("fondo.png");
        }
        else {
        urlImagenFondo = this.getClass().getResource("game_over.jpg");
        }
        
        
        
        
        imaImagenFondo = Toolkit.getDefaultToolkit().getImage(urlImagenFondo);

        //despliega la imagen
        graGraficaApplet.drawImage(imaImagenFondo, 0, 0, 
                getWidth(), getHeight(), this);
        
       // Actualiza el Foreground.
        graGraficaApplet.setColor (getForeground());
        paintAux(graGraficaApplet);

        // Dibuja la imagen actualizada
        graGrafico.drawImage (imaImagenApplet, 0, 0, this);
        
        
    }

    
    
    public void paintAux(Graphics g) {
        //se despliegan el score en la esquina superior izq
        g.setColor(Color.RED);
        g.drawString("Score: " + iScore, 20, 35);
        if (entBarra != null && entBloque != null && entProyectil != null && encBloques.size() > 0) {
                //Dibuja la imagen de la barra en la posicion actualizada
                g.drawImage(entBarra.getImagen(), entBarra.getX(),
                        entBarra.getY(), this);
                
                g.drawImage(entProyectil.getImagen(), entProyectil.getX(),
                        entProyectil.getY(), this);
                //se corre la animacion si es verdadero el boolean
                if(iContador < 3) {
                    iContador++;
                    g.drawImage(Toolkit.getDefaultToolkit().getImage(aniBloqueDestruido.getImagen()), aniPosX, aniPosY, this);
                }
                //Dibuja los bloques
                for (Object encBloque : encBloques) {
                    Entidad bloque = (Entidad)encBloque;
                    g.drawImage(bloque.getImagen(), bloque.getX(),
                            bloque.getY(), this);
                }
        }

    }

   

    @Override
    public void keyTyped(KeyEvent e) {
    }
    
}

/**/
