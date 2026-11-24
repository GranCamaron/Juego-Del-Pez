
package peces;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class JuegoPez extends JFrame
{
    private static final long serialVersionUID = 1L;

    public JuegoPez()
    {
        PanelPez panel = new PanelPez();

        setTitle("El pez más grande");
        setContentPane(panel);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                JuegoPez ventana = new JuegoPez();
                ventana.setVisible(true);
                ventana.getContentPane().requestFocusInWindow();
            }
        });
    }
}
