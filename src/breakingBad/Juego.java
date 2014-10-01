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
    private Boolean spacePress;     //booleano indicativo de la tecla spacebar
    private Boolean dirProyectilY;   //indica si el proyectil sube o baja
    private Boolean dirProyectilX;   //indica si el proyectil se mueve a la derecha o izq

    
     public Juego() {
        init();
        start();
    }
     
    public void init() {
        // hago el applet de un tamaño 500,500
        setSize(1200, 800);
        
        //por defecto score empieza en 0
        iScore =0;
        
        //proyectil empieza moviendose a la derecha y arriba
        dirProyectilX = true;
        dirProyectilY = true;
        
        //la tecla spacebar no esta presionada
        spacePress = false;

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
        // se obtiene la imagen para los bloques    
        URL urlImagenBloque = this.getClass().getResource("bloque.png");

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
    entProyectil.setVelocidad(1);
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
        
        //se cambia la posicion de la barra dependiendo del booleano 'spacebar'
        if (spacePress) {
            entBarra.setX(entBarra.getX() + entBarra.getVelocidad());
        }
        else {
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
            entProyectil.setY(entProyectil.getX() + entProyectil.getVelocidad());
        }
        else {
            entProyectil.setY(entProyectil.getX() - entProyectil.getVelocidad());
        } 

    }
    
    public void run () {
        // se realiza el ciclo del juego en este caso nunca termina
        while (true) {
            /* mientras dure el juego, se actualizan posiciones de jugadores
               se checa si hubo colisiones para desaparecer jugadores o corregir
               movimientos y se vuelve a pintar todo
            */ 
            actualiza();
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
        }

        //si Bloque choca con Nena se aumenta el score
        for (Object encBloque : encBloques) {
                Entidad Bloque = (Entidad)encBloque;
                if(entProyectil.colisiona(Bloque)) {
                    iScore++;   //se aumenta el score
                    //se reposiciona al Bloque
                    //Bloque.setX(-400);
                    dirProyectilX = !dirProyectilX;
                    dirProyectilY = !dirProyectilY;
                    //encBloques.remove(Bloque);   //se elimina el bloque con el que se colisiono                    
                    aucSonidoBloque.play();      //emite sonido
                }
        }
    }
    
    public void keyReleased(KeyEvent e) {
        // si se suelta spacebar, barra se mueve hacia al izquierda
        if(e.getKeyCode() == KeyEvent.VK_SPACE) {    
            spacePress = false;
        }
    }

    public void keyPressed(KeyEvent e) {
        // si se presiona spacebar, barra se mueve hacia la derecha
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            spacePress = true;
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
        urlImagenFondo = this.getClass().getResource("fondo.png");
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
        if (entBarra != null && entBloque != null && entProyectil != null) {
                //Dibuja la imagen de la barra en la posicion actualizada
                g.drawImage(entBarra.getImagen(), entBarra.getX(),
                        entBarra.getY(), this);
                
                g.drawImage(entProyectil.getImagen(), entProyectil.getX(),
                        entProyectil.getY(), this);
                
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
