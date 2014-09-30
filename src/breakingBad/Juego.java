/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package examenjframe;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyListener;
import java.util.LinkedList;
import javax.swing.JFrame;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;

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
    private int dirNena;                //direccion de Nena;
    private LinkedList encBloques;  //lista de bloques
    private Entidad entBloque;     //objeto para obtener bloques indiviualmente
    private int iScore;                 //score del juego
    private SoundClip aucSonidoBloque; //sonido de impacto con bloque
    private SoundClip aucSonidoSuelo;  //sonido de impacto con suelo
    
     public Juego() {
        init();
        start();
    }
     
    public void init() {
        // hago el applet de un tamaño 500,500
        setSize(800, 600);
        
        //por defecto score empieza en 0
        iScore =0;
        
        //se crea sonido de impacto con bloque
        aucSonidoBloque = new SoundClip("wooho.wav");
        
        //se crea sonido de impacto con suelo
        aucSonidoSuelo = new SoundClip("d_oh.wav");
        
        
        // se crea imagen de la barra
        Image imaImagenBarra = Toolkit.getDefaultToolkit().getImage("nena.gif");
        
        // se crea la barra
	entBarra = new Entidad(getWidth() / 2, getHeight(),
                imaImagenNena);
        entBarra.setY(getHeight()-entBarra.getAlto());
        entBarra.setX(getWidth()/2-entBarra.getAncho());

         //velocidad de nena es 3
        entBarra.setVelocidad(3);
        
        //
        // se obtiene la imagen para los bloques    
	Image imaImagenBloque = Toolkit.getDefaultToolkit().getImage("alien1Camina.gif");

        // se crea el arreglo de bloques con 8-10 bloques
        encBloques = new LinkedList();
        int cantbloques = 20;
        while(cantbloques != 0) {
            cantbloques -=1;
            int posX = (int) (Math.random() *(0 + 600) - 600);    
            int posY = (int) (Math.random() *(getHeight()));
            entBloque = new Entidad(posX,posY,imaImagenBloque);
            //cada caminador tiene una velocidad al azar
            entBloque.setVelocidad((int) (Math.random() * (5 -3) + 3));
            encBloques.add(entBloque);
        }
        //
        
        // se obtiene la imagen para los corredores    
	Image imaImagenCorredor = Toolkit.getDefaultToolkit().getImage("alien2Corre.gif");

        // se crea el arreglo de corredores con 10-15 corredores
        encCorredores = new LinkedList();
        int cantCorredores = (int) (Math.random() * (15 - 10) + 10);
        while(cantCorredores != 0) {
            cantCorredores -=1;
            int posX = (int) (Math.random() *(getWidth()));    
            int posY = (int) (Math.random() *(0 + 1000) - 1000);
            perCorredor = new Entidad(posX,posY,imaImagenCorredor);
            encCorredores.add(perCorredor);
        }
        
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
        
        //se cambia la posicion de la nena dependiendo de su direccion
        if(dirNena == 1) {
            entBarra.setY(entBarra.getY()-entBarra.getVelocidad());
        }
        else if(dirNena == 2) {
            entBarra.setY(entBarra.getY()+entBarra.getVelocidad());
        }
        else if(dirNena == 3) {
            entBarra.setX(entBarra.getX()-entBarra.getVelocidad());
        }
        else if(dirNena == 4) {
            entBarra.setX(entBarra.getX()+entBarra.getVelocidad());
        }
        
        //se mueve a cada caminador
        for (Object encCaminador : encBloques) {
                Entidad Caminador = (Entidad)encCaminador;
                Caminador.setX(Caminador.getX()+Caminador.getVelocidad());
        }
        
        //se mueve a cada corredor
        for (Object encCorredor : encCorredores) {
                Entidad Corredor = (Entidad)encCorredor;
                Corredor.setY(Corredor.getY()+Corredor.getVelocidad()-iVidas+5); //mientras menos vidas, mas rapido
        }
    }
    
    public void run () {
        // se realiza el ciclo del juego en este caso nunca termina
        while (iVidas>0) {//mientras no se haya perdido
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
        //la nena no se puede salir del cuadro
        if(entBarra.getX()+entBarra.getAncho()>getWidth()) {
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
        
        //si caminador choca con Nena se aumenta el score
        for (Object encCaminador : encBloques) {
                Entidad Caminador = (Entidad)encCaminador;
                if(entBarra.colisiona(Caminador)) {
                    iScore++;   //se aumenta el score
                    //se reposiciona al caminador
                    Caminador.setX((int) (Math.random() *(0 + 600) - 600)); 
                    Caminador.setY((int) (Math.random() *(getHeight())));
                    aucSonidoBloque.play();      //emite sonido
                }
                //se reposiciona al caminador cuando se sale del applet
                else if(Caminador.getX()+Caminador.getAncho()>getWidth()) {
                    Caminador.setX((int) (Math.random() *(0 + 600) - 600)); 
                    Caminador.setY((int) (Math.random() *(getHeight())));
                }
        }
        
        //si corredor choca con Nena se disminuye las vidas
        for (Object encCorredor : encCorredores) {
                Entidad Corredor = (Entidad)encCorredor;
                if(entBarra.colisiona(Corredor)) {
                    iColisiones++;  //se aumenta el contador de colisiones
                    if(iColisiones >=5) { //si llega a cinco colisiones, se disminuye una vida
                        iVidas--;
                        iColisiones = 0;    //se reinicia el contador
                    }
                    //se reposiciona al corredor
                    Corredor.setX((int) (Math.random() *(getWidth()))); 
                    Corredor.setY((int) (Math.random() *(0 + 1000) - 1000));
                    aucSonidoSuelo.play();   //emite sonido
                }
                //se reposiciona al corredor cuando se sale del applet
                else if(Corredor.getY()+Corredor.getAlto()>getHeight()) {
                    Corredor.setX((int) (Math.random() *(getWidth()))); 
                    Corredor.setY((int) (Math.random() *(0 + 1000) - 1000));
                }
        }
    }
    
    public void keyReleased(KeyEvent e) {
        // si presiono flecha para arriba
        if(e.getKeyCode() == KeyEvent.VK_W) {    
                dirNena = 1;  // cambio la dirección arriba
        }
        // si presiono flecha para abajo
        else if(e.getKeyCode() == KeyEvent.VK_S) {    
                dirNena = 2;   // cambio la direccion para abajo
        }
        else if(e.getKeyCode() == KeyEvent.VK_A) {    
                dirNena = 3;   // cambio la direccion para izquierda
        }
        else if(e.getKeyCode() == KeyEvent.VK_D) {    
                dirNena = 4;   // cambio la direccion para derecha
        }
    }
    
    public void paint(Graphics graGrafico){
        // Inicializan el DoubleBuffer
        if (imaImagenApplet == null){
                imaImagenApplet = createImage (this.getSize().width, 
                        this.getSize().height);
                graGraficaApplet = imaImagenApplet.getGraphics ();
        }
        
        
        //crea imagen para el background
        Image imaImagenFondo;
        if(iVidas>0){
            imaImagenFondo = Toolkit.getDefaultToolkit().getImage("espacio.jpg");
        }
        else{   //imagen de game over cuando se pierde el juego
            imaImagenFondo = Toolkit.getDefaultToolkit().getImage("gameOver.jpg");
        }
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
        //se despliegan las vidas en la esquina superior izq
        g.setColor(Color.RED);
        g.drawString("Vidas: "+ iVidas, 20, 20);
        g.drawString("Score: " + iScore, 20, 35);
        if (entBarra != null && entBloque != null && perCorredor != null && iVidas >0) {
                //Dibuja la imagen de la nena en la posicion actualizada
                g.drawImage(entBarra.getImagen(), entBarra.getX(),
                        entBarra.getY(), this);
                
                //Dibuja los bloques en la posicion actualizada
                for (Object encCaminador : encBloques) {
                    Entidad Caminador = (Entidad)encCaminador;
                    g.drawImage(Caminador.getImagen(), Caminador.getX(),
                            Caminador.getY(), this);
                }
                
                //Dibuja lso corredores en la posicion actualizada
                for (Object encCorredor : encCorredores) {
                    Entidad Corredor = (Entidad)encCorredor;
                    g.drawImage(Corredor.getImagen(), Corredor.getX(),
                            Corredor.getY(), this);
                }
        }

    }

   

    @Override
    public void keyTyped(KeyEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void keyPressed(KeyEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
    
}

/**/
