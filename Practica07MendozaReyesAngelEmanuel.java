import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Practica07MendozaReyesAngelEmanuel extends JFrame {
    private BufferedImage lienzo;
    private BufferedImage imagenFondo;
    private JPanel panelDibujo;

    public Practica07MendozaReyesAngelEmanuel() {
        setTitle("Practica07MendozaReyesAngelEmanuel");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        try {
            imagenFondo = ImageIO.read(new File("fondo.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        lienzo = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        panelDibujo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), null);
                g.drawImage(lienzo, 0, 0, null);
            }
        };
        panelDibujo.setPreferredSize(new Dimension(800, 600));
        panelDibujo.setLayout(new BorderLayout());
        add(panelDibujo, BorderLayout.CENTER);
        setVisible(true);

        Graphics2D g2d = lienzo.createGraphics();
        g2d.setComposite(AlphaComposite.Clear);
        g2d.fillRect(0, 0, lienzo.getWidth(), lienzo.getHeight());
        g2d.setComposite(AlphaComposite.SrcOver);
        g2d.dispose();
    }

    private void putPixel(int x, int y, Color c) {
        if (x >= 0 && x < lienzo.getWidth() && y >= 0 && y < lienzo.getHeight()) {
            lienzo.setRGB(x, y, c.getRGB());
        }
    }

    private void dibujarLinea(int x1, int y1, int x2, int y2, Color color) {
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;

        while (true) {
            putPixel(x1, y1, color);
            if (x1 == x2 && y1 == y2) break;
            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x1 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y1 += sy;
            }
        }
    }

    private void dibujarCirculo(int x0, int y0, int radio, Color color) {
        int x = radio;
        int y = 0;
        int err = 0;

        while (x >= y) {
            putPixel(x0 + x, y0 + y, color);
            putPixel(x0 + y, y0 + x, color);
            putPixel(x0 - y, y0 + x, color);
            putPixel(x0 - x, y0 + y, color);
            putPixel(x0 - x, y0 - y, color);
            putPixel(x0 - y, y0 - x, color);
            putPixel(x0 + y, y0 - x, color);
            putPixel(x0 + x, y0 - y, color);

            if (err <= 0) {
                y += 1;
                err += 2 * y + 1;
            }
            if (err > 0) {
                x -= 1;
                err -= 2 * x + 1;
            }
        }
    }

    private void dibujarOvalo(int x0, int y0, int radioX, int radioY, Color color) {
        int x = radioX;
        int y = 0;
        int err = 0;
        int dosCuadradoA = 2 * radioX * radioX;
        int dosCuadradoB = 2 * radioY * radioY;
        int cambioX = radioY * radioY * (1 - 2 * radioX);
        int cambioY = radioX * radioX;
        int errorElipse = 0;
        int detencionX = dosCuadradoB * radioX;
        int detencionY = 0;

        while (detencionX >= detencionY) {
            putPixel(x0 + x, y0 + y, color);
            putPixel(x0 - x, y0 + y, color);
            putPixel(x0 - x, y0 - y, color);
            putPixel(x0 + x, y0 - y, color);
            y++;
            detencionY += dosCuadradoA;
            errorElipse += cambioY;
            cambioY += dosCuadradoA;
            if ((2 * errorElipse + cambioX) > 0) {
                x--;
                detencionX -= dosCuadradoB;
                errorElipse += cambioX;
                cambioX += dosCuadradoB;
            }
        }

        x = 0;
        y = radioY;
        cambioX = radioY * radioY;
        cambioY = radioX * radioX * (1 - 2 * radioY);
        errorElipse = 0;
        detencionX = 0;
        detencionY = dosCuadradoA * radioY;

        while (detencionX <= detencionY) {
            putPixel(x0 + x, y0 + y, color);
            putPixel(x0 - x, y0 + y, color);
            putPixel(x0 - x, y0 - y, color);
            putPixel(x0 + x, y0 - y, color);
            x++;
            detencionX += dosCuadradoB;
            errorElipse += cambioX;
            cambioX += dosCuadradoB;
            if ((2 * errorElipse + cambioY) > 0) {
                y--;
                detencionY -= dosCuadradoA;
                errorElipse += cambioY;
                cambioY += dosCuadradoA;
            }
        }
    }

    private void dibujarFlor(int desplazamientoX, Color colorPetalo) {
        Color colorCentro = Color.YELLOW;
        Color colorTallo = Color.GREEN;
        Color colorMaceta = Color.ORANGE;

        int desplazamientoY = 150;

        for (int radio = 20; radio > 0; radio -= 5) {
            dibujarCirculo(400 + desplazamientoX, 100 + desplazamientoY, radio, colorCentro);
        }

        for (int i = 0; i < 360; i += 45) {
            double rad = Math.toRadians(i);
            int x = (int) (400 + desplazamientoX + 40 * Math.cos(rad));
            int y = (int) (100 + desplazamientoY + 40 * Math.sin(rad));
            for (int radio = 20; radio > 0; radio -= 5) {
                dibujarOvalo(x, y, radio, radio * 2, colorPetalo);
            }
        }

        for (int i = 0; i < 360; i += 15) {
            double rad = Math.toRadians(i);
            int x1 = (int) (400 + desplazamientoX + 20 * Math.cos(rad));
            int y1 = (int) (100 + desplazamientoY + 20 * Math.sin(rad));
            int x2 = (int) (400 + desplazamientoX + 60 * Math.cos(rad));
            int y2 = (int) (100 + desplazamientoY + 60 * Math.sin(rad));
            dibujarLinea(x1, y1, x2, y2, colorPetalo);
        }

        for (int i = -4; i <= 4; i += 2) {
            dibujarLinea(400 + desplazamientoX + i, 120 + desplazamientoY, 400 + desplazamientoX + i, 300 + desplazamientoY, colorTallo);
        }

        for (int i = 200 + desplazamientoY; i <= 300 + desplazamientoY; i += 5) {
            dibujarOvalo(400 + desplazamientoX, i, 20, 10, colorMaceta);
        }
    }

    public static void main(String[] args) {
        Practica07MendozaReyesAngelEmanuel ventana = new Practica07MendozaReyesAngelEmanuel();
        ventana.dibujarFlor(0, Color.RED); 
        ventana.dibujarFlor(200, Color.BLUE); 
        ventana.dibujarFlor(-200, Color.MAGENTA);
        ventana.panelDibujo.repaint();
    }
}