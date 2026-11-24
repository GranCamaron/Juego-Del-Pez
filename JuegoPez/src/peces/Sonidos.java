
package peces;

import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Sonidos
{
    private static Clip musica;
    private static Clip comer;
    private static Clip perder;

    public static void cargar()
    {
        musica = cargarClip("sonidos/musica.wav");
        comer = cargarClip("sonidos/comer.wav");
        perder = cargarClip("sonidos/perder.wav");
    }

    private static Clip cargarClip(String ruta)
    {
        try
        {
            AudioInputStream audio =
                    AudioSystem.getAudioInputStream(new File(ruta));

            Clip clip = AudioSystem.getClip();
            clip.open(audio);
            audio.close();

            return clip;
        }
        catch (Exception e)
        {
            System.out.println("No se pudo cargar " + ruta
                    + ": " + e.getMessage());

            return null;
        }
    }

    public static void iniciarMusica()
    {
        if (musica != null)
        {
            musica.stop();
            musica.setFramePosition(0);
            musica.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public static void detenerMusica()
    {
        if (musica != null)
        {
            musica.stop();
        }
    }

    public static void sonidoComer()
    {
        reproducir(comer);
    }

    public static void sonidoPerder()
    {
        reproducir(perder);
    }

    private static void reproducir(Clip clip)
    {
        if (clip != null)
        {
            clip.stop();
            clip.setFramePosition(0);
            clip.start();
        }
    }
}
