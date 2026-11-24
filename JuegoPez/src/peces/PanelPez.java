
package peces;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Random;

import javax.swing.JPanel;

public class PanelPez extends JPanel implements Runnable, KeyListener
{
    private static final long serialVersionUID = 1L;

    private final int INICIO = 0;
    private final int JUGANDO = 1;
    private final int GANADO = 2;
    private final int PERDIDO = 3;

    private int estado = INICIO;

    private int tamanioJugador = 5;
    private int jugadorX = 100;
    private int jugadorY = 250;
    private int direccionJugador = 1;

    private boolean arriba = false;
    private boolean abajo = false;
    private boolean izquierda = false;
    private boolean derecha = false;

    // Posicion del raton
    private int ratonX = 100;
    private int ratonY = 250;
    private boolean seguirRaton = false;

    private final int NUM_PECES = 6;
    private Pez[] peces = new Pez[NUM_PECES];

    private final int NUM_COMIDAS = 25;
    private int[] comidaX = new int[NUM_COMIDAS];
    private int[] comidaY = new int[NUM_COMIDAS];

    private Random aleatorio = new Random();

    private Thread hiloJuego;

    public PanelPez()
    {
        setPreferredSize(new Dimension(900, 600));
        setBackground(new Color(0, 110, 145));
        setFocusable(true);

        addKeyListener(this);

        // Movimiento con el raton
        addMouseMotionListener(new MouseMotionAdapter()
        {
            public void mouseMoved(MouseEvent e)
            {
                moverConRaton(e.getX(), e.getY());
            }

            public void mouseDragged(MouseEvent e)
            {
                moverConRaton(e.getX(), e.getY());
            }
        });

        addMouseListener(new MouseAdapter()
        {
            public void mousePressed(MouseEvent e)
            {
                requestFocusInWindow();
            }
        });

        Sonidos.cargar();
    }

    // Guardamos la posicion del raton
    private synchronized void moverConRaton(int x, int y)
    {
        if (estado == JUGANDO)
        {
            int radio = 10 + tamanioJugador;

            ratonX = Math.max(radio,
                    Math.min(getWidth() - radio, x));

            ratonY = Math.max(55 + radio,
                    Math.min(getHeight() - radio - 25, y));

            seguirRaton = true;
        }
    }

    public synchronized boolean estaJugando()
    {
        return estado == JUGANDO;
    }

    private synchronized void iniciarPartida()
    {
        estado = INICIO;

        if (hiloJuego != null)
        {
            hiloJuego.interrupt();
        }

        detenerPeces();

        tamanioJugador = 5;

        jugadorX = 100;
        jugadorY = getHeight() / 2;
        direccionJugador = 1;

        ratonX = jugadorX;
        ratonY = jugadorY;
        seguirRaton = false;

        arriba = false;
        abajo = false;
        izquierda = false;
        derecha = false;

        crearComida();

        estado = JUGANDO;

        for (int i = 0; i < NUM_PECES; i++)
        {
            crearPez(i);
        }

        Sonidos.iniciarMusica();

        hiloJuego = new Thread(this, "Hilo del juego");
        hiloJuego.start();

        requestFocusInWindow();
        repaint();
    }

    private synchronized void crearComida()
    {
        for (int i = 0; i < NUM_COMIDAS; i++)
        {
            comidaX[i] = 20 + aleatorio.nextInt(getWidth() - 40);
            comidaY[i] = 60 + aleatorio.nextInt(getHeight() - 100);
        }
    }

    private synchronized void crearPez(int posicion)
    {
        int direccion;
        int x;

        if (aleatorio.nextInt(2) == 0)
        {
            direccion = 1;
            x = -60;
        }
        else
        {
            direccion = -1;
            x = getWidth() + 60;
        }

        int y = 60 + aleatorio.nextInt(getHeight() - 120);
        int tamanio = 2 + aleatorio.nextInt(19);

        Color[] colores = {
            Color.RED, Color.ORANGE, Color.MAGENTA,
            Color.GREEN, Color.PINK, Color.CYAN
        };

        Color color = colores[aleatorio.nextInt(colores.length)];

        peces[posicion] = new Pez(this, x, y,
                tamanio, direccion, color);

        peces[posicion].start();
    }

    public synchronized void recolocarPez(Pez pez)
    {
        if (estado != JUGANDO || !pez.estaActivo())
        {
            return;
        }

        int direccion;
        int x;

        if (aleatorio.nextInt(2) == 0)
        {
            direccion = 1;
            x = -60;
        }
        else
        {
            direccion = -1;
            x = getWidth() + 60;
        }

        int y = 60 + aleatorio.nextInt(getHeight() - 120);
        int tamanio = 2 + aleatorio.nextInt(19);

        Color[] colores = {
            Color.RED, Color.ORANGE, Color.MAGENTA,
            Color.GREEN, Color.PINK, Color.CYAN
        };

        Color color = colores[aleatorio.nextInt(colores.length)];

        pez.recolocar(x, y, tamanio, direccion, color);
    }

    // Movimiento del jugador con teclado o raton
    private synchronized void moverJugador()
    {
        int velocidad = 4;

        if (seguirRaton)
        {
            // Movimiento horizontal
            if (jugadorX < ratonX)
            {
                jugadorX += Math.min(velocidad, ratonX - jugadorX);
                direccionJugador = 1;
            }
            else if (jugadorX > ratonX)
            {
                jugadorX -= Math.min(velocidad, jugadorX - ratonX);
                direccionJugador = -1;
            }

            // Movimiento vertical
            if (jugadorY < ratonY)
            {
                jugadorY += Math.min(velocidad, ratonY - jugadorY);
            }
            else if (jugadorY > ratonY)
            {
                jugadorY -= Math.min(velocidad, jugadorY - ratonY);
            }

            // Comprobamos si ha llegado al destino
            if (jugadorX == ratonX && jugadorY == ratonY)
            {
                seguirRaton = false;
            }
        }
        else
        {
            // Movimiento con las flechas
            if (arriba) jugadorY -= velocidad;
            if (abajo) jugadorY += velocidad;

            if (izquierda)
            {
                jugadorX -= velocidad;
                direccionJugador = -1;
            }

            if (derecha)
            {
                jugadorX += velocidad;
                direccionJugador = 1;
            }
        }

        int radio = 10 + tamanioJugador;

        if (jugadorX < radio)
        {
            jugadorX = radio;
        }

        if (jugadorX > getWidth() - radio)
        {
            jugadorX = getWidth() - radio;
        }

        if (jugadorY < 55 + radio)
        {
            jugadorY = 55 + radio;
        }

        if (jugadorY > getHeight() - radio - 25)
        {
            jugadorY = getHeight() - radio - 25;
        }
    }

    private synchronized void comprobarComida()
    {
        for (int i = 0; i < NUM_COMIDAS; i++)
        {
            int dx = jugadorX - comidaX[i];
            int dy = jugadorY - comidaY[i];

            int radio = tamanioJugador + 10;

            if (dx * dx + dy * dy < radio * radio)
            {
                tamanioJugador++;

                comidaX[i] = 20 + aleatorio.nextInt(getWidth() - 40);
                comidaY[i] = 60 + aleatorio.nextInt(getHeight() - 100);

                Sonidos.sonidoComer();
            }
        }
    }

    private synchronized void comprobarPeces()
    {
        for (int i = 0; i < NUM_PECES && estado == JUGANDO; i++)
        {
            if (peces[i] != null && peces[i].estaActivo())
            {
                int dx = jugadorX - peces[i].getX();
                int dy = jugadorY - peces[i].getY();

                int radio = 20 + tamanioJugador
                        + peces[i].getTamanio();

                if (dx * dx + dy * dy < radio * radio)
                {
                    if (tamanioJugador > peces[i].getTamanio())
                    {
                        tamanioJugador += peces[i].getTamanio();

                        Sonidos.sonidoComer();

                        recolocarPez(peces[i]);
                    }
                    else
                    {
                        estado = PERDIDO;

                        Sonidos.detenerMusica();
                        Sonidos.sonidoPerder();

                        detenerPeces();
                    }
                }
            }
        }
    }

    private synchronized void detenerPeces()
    {
        for (int i = 0; i < NUM_PECES; i++)
        {
            if (peces[i] != null)
            {
                peces[i].detener();
            }
        }
    }

    private synchronized void terminarVictoria()
    {
        estado = GANADO;

        Sonidos.detenerMusica();

        detenerPeces();
    }

    public void run()
    {
        while (estaJugando() && Thread.currentThread() == hiloJuego)
        {
            moverJugador();

            comprobarComida();

            comprobarPeces();

            if (tamanioJugador >= 50 && estaJugando())
            {
                terminarVictoria();
            }

            repaint();

            try
            {
                Thread.sleep(30);
            }
            catch (InterruptedException e)
            {
                break;
            }
        }
    }

    private void dibujarPez(Graphics g, int x, int y,
            int tamanio, Color color, int direccion)
    {
        int ancho = 20 + tamanio * 3;
        int alto = 12 + tamanio * 2;

        Polygon cola = new Polygon();

        if (direccion == 1)
        {
            cola.addPoint(x - ancho / 2, y);
            cola.addPoint(x - ancho / 2 - 12, y - alto / 2);
            cola.addPoint(x - ancho / 2 - 12, y + alto / 2);
        }
        else
        {
            cola.addPoint(x + ancho / 2, y);
            cola.addPoint(x + ancho / 2 + 12, y - alto / 2);
            cola.addPoint(x + ancho / 2 + 12, y + alto / 2);
        }

        g.setColor(color.darker());
        g.fillPolygon(cola);

        g.setColor(color);
        g.fillOval(x - ancho / 2, y - alto / 2, ancho, alto);

        int ojoX;

        if (direccion == 1)
        {
            ojoX = x + ancho / 4;
        }
        else
        {
            ojoX = x - ancho / 4;
        }

        g.setColor(Color.WHITE);
        g.fillOval(ojoX - 4, y - 4, 8, 8);

        g.setColor(Color.BLACK);
        g.fillOval(ojoX - 2, y - 2, 4, 4);
    }

    public synchronized void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        g.setColor(new Color(190, 240, 240));

        for (int i = 0; i < 15; i++)
        {
            int x = (i * 67 + 20) % getWidth();
            int y = (i * 43 + 30) % (getHeight() - 30);

            g.drawOval(x, y, 8, 8);
        }

        g.setColor(new Color(220, 180, 110));
        g.fillRect(0, getHeight() - 25, getWidth(), 25);

        if (estado == INICIO)
        {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 38));

            g.drawString("EL PEZ MAS GRANDE", 220, 150);

            g.setFont(new Font("Arial", Font.PLAIN, 20));

            g.drawString("Empiezas con tamaño 5.", 300, 230);
            g.drawString("Come comida para sumar 1 punto.", 260, 270);
            g.drawString("Come peces mas pequeños para crecer.", 230, 310);
            g.drawString("Evita los peces iguales o mas grandes.", 225, 350);
            g.drawString("Llega al tamaño 50 para ganar.", 270, 390);

            g.setFont(new Font("Arial", Font.BOLD, 22));
            g.setColor(Color.YELLOW);

            g.drawString("Pulsa ESPACIO para comenzar", 275, 470);
        }
        else
        {
            for (int i = 0; i < NUM_COMIDAS; i++)
            {
                g.setColor(Color.ORANGE);
                g.fillOval(comidaX[i] - 5, comidaY[i] - 5, 10, 10);
            }

            for (int i = 0; i < NUM_PECES; i++)
            {
                if (peces[i] != null && peces[i].estaActivo())
                {
                    dibujarPez(g,
                            peces[i].getX(),
                            peces[i].getY(),
                            peces[i].getTamanio(),
                            peces[i].getColor(),
                            peces[i].getDireccion());
                }
            }

            dibujarPez(g, jugadorX, jugadorY,
                    tamanioJugador, Color.YELLOW, direccionJugador);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 20));

            g.drawString("Tamaño: " + tamanioJugador + " / 50", 20, 30);

            if (estado == GANADO)
            {
                g.setColor(Color.YELLOW);
                g.setFont(new Font("Arial", Font.BOLD, 40));

                g.drawString("HAS GANADO", 330, 260);

                g.setFont(new Font("Arial", Font.PLAIN, 20));

                g.drawString("Pulsa ESPACIO para volver a jugar", 260, 310);
            }
            else if (estado == PERDIDO)
            {
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 40));

                g.drawString("FIN DE LA PARTIDA", 250, 260);

                g.setFont(new Font("Arial", Font.PLAIN, 20));

                g.drawString("Pulsa ESPACIO para volver a jugar", 260, 310);
            }
        }
    }

    public synchronized void keyPressed(KeyEvent e)
    {
        if (estado != JUGANDO && e.getKeyCode() == KeyEvent.VK_SPACE)
        {
            iniciarPartida();
        }
        else if (estado == JUGANDO)
        {
            seguirRaton = false;

            if (e.getKeyCode() == KeyEvent.VK_UP) arriba = true;
            if (e.getKeyCode() == KeyEvent.VK_DOWN) abajo = true;
            if (e.getKeyCode() == KeyEvent.VK_LEFT) izquierda = true;
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) derecha = true;
        }
    }

    public synchronized void keyReleased(KeyEvent e)
    {
        if (e.getKeyCode() == KeyEvent.VK_UP) arriba = false;
        if (e.getKeyCode() == KeyEvent.VK_DOWN) abajo = false;
        if (e.getKeyCode() == KeyEvent.VK_LEFT) izquierda = false;
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) derecha = false;
    }

    public void keyTyped(KeyEvent e)
    {
    }
}
