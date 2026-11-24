
package peces;

import java.awt.Color;

public class Pez extends Thread
{
    private PanelPez panel;

    private int x;
    private int y;
    private int tamanio;
    private int direccion;

    private Color color;
    private boolean activo;

    public Pez(PanelPez panel, int x, int y, int tamanio,
            int direccion, Color color)
    {
        this.panel = panel;
        this.x = x;
        this.y = y;
        this.tamanio = tamanio;
        this.direccion = direccion;
        this.color = color;
        activo = true;
    }

    public void run()
    {
        while (estaActivo() && panel.estaJugando())
        {
            mover();

            if (getX() < -80 || getX() > panel.getWidth() + 80)
            {
                panel.recolocarPez(this);
            }

            panel.repaint();

            try
            {
                Thread.sleep(40);
            }
            catch (InterruptedException e)
            {
                detener();
            }
        }
    }

    public synchronized void mover()
    {
        int velocidad = 1 + tamanio / 5;

        x = x + direccion * velocidad;
    }

    public synchronized void recolocar(int nuevoX, int nuevoY,
            int nuevoTamanio, int nuevaDireccion, Color nuevoColor)
    {
        x = nuevoX;
        y = nuevoY;
        tamanio = nuevoTamanio;
        direccion = nuevaDireccion;
        color = nuevoColor;
        activo = true;
    }

    public synchronized int getX()
    {
        return x;
    }

    public synchronized int getY()
    {
        return y;
    }

    public synchronized int getTamanio()
    {
        return tamanio;
    }

    public synchronized int getRadio()
    {
        return 10 + tamanio;
    }

    public synchronized int getDireccion()
    {
        return direccion;
    }

    public synchronized Color getColor()
    {
        return color;
    }

    public synchronized boolean estaActivo()
    {
        return activo;
    }

    public synchronized void detener()
    {
        activo = false;
        interrupt();
    }
}
