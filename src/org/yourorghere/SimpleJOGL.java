package org.yourorghere;

import com.sun.opengl.util.Animator;
import java.awt.Frame;
import java.awt.event.*;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureIO;
import java.io.File;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

/**
 * SimpleJOGL.java <BR>
 * author: Brian Paul (converted to Java by Ron Cemer and Sven Goethel)
 * <P>
 *
 * This version is equal to Brian Paul's version 1.2 1999/10/21
 */
public class SimpleJOGL implements GLEventListener {

    static BufferedImage image1 = null, image2 = null;
    static Texture t1 = null, t2 = null;

    //statyczne pola okre?laj?ce rotacj? wokó? osi X i Y
    private static float xrot = 0.0f, yrot = 0.0f;

    public static void main(String[] args) {
        Frame frame = new Frame("Simple JOGL Application");
        GLCanvas canvas = new GLCanvas();
        canvas.addGLEventListener(new SimpleJOGL());
        frame.add(canvas);
        frame.setSize(1024, 880);
        final Animator animator = new Animator(canvas);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Run this on another thread than the AWT event queue to
                // make sure the call to Animator.stop() completes before
                // exiting
                new Thread(new Runnable() {
                    public void run() {
                        animator.stop();
                        System.exit(0);
                    }
                }).start();
            }
        });

        //Obs?uga klawiszy strza?ek
        frame.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    xrot -= 5.0f;
                }
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    xrot += 5.0f;
                }
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    yrot += 5.0f;
                }
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    yrot -= 5.0f;
                }
            }

            public void keyReleased(KeyEvent e) {
            }

            public void keyTyped(KeyEvent e) {
            }
        });

        // Center frame
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        animator.start();
    }

    public void init(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        System.err.println("INIT GL IS: " + gl.getClass().getName());
        gl.setSwapInterval(1);

        //wartoœci sk³adowe oœwietlenia i koordynaty Ÿród³a œwiat³a
        float ambientLight[] = {0.3f, 0.3f, 0.3f, 1.0f};//swiat³o otaczaj¹ce
        float diffuseLight[] = {0.7f, 0.7f, 0.7f, 1.0f};//œwiat³o rozproszone
        float specular[] = {1.0f, 1.0f, 1.0f, 1.0f}; //œwiat³o odbite
        float lightPos[] = {0.0f, 150.0f, 150.0f, 1.0f};//pozycja œwiat³a
        //(czwarty parametr okreœla odleg³oœæ Ÿród³a:
        //0.0f-nieskoñczona; 1.0f-okreœlona przez pozosta³e parametry)
        gl.glEnable(GL.GL_LIGHTING); //uaktywnienie oœwietlenia
        //ustawienie parametrów Ÿród³a œwiat³a nr. 0
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_AMBIENT, ambientLight, 0); //swiat³o otaczaj¹ce
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, diffuseLight, 0); //œwiat³o rozproszone
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_SPECULAR, specular, 0); //œwiat³o odbite
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, lightPos, 0); //pozycja œwiat³a
        gl.glEnable(GL.GL_LIGHT0); //uaktywnienie Ÿród³a œwiat³a nr. 0
        gl.glEnable(GL.GL_COLOR_MATERIAL); //uaktywnienie œledzenia kolorów
        //kolory bêd¹ ustalane za pomoc¹ glColor
        gl.glColorMaterial(GL.GL_FRONT, GL.GL_AMBIENT_AND_DIFFUSE);
        gl.glEnable(GL.GL_DEPTH_TEST);
        // Setup the drawing area and shading mode
        gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        gl.glShadeModel(GL.GL_SMOOTH);
        try {
           image1 = ImageIO.read(getClass().getResourceAsStream("/img1.bmp"));
           image1 = ImageIO.read(getClass().getResourceAsStream("/img2.bmp"));
        } catch (Exception exc) {
            JOptionPane.showMessageDialog(null, exc.toString());
            return;
        }

        t1 = TextureIO.newTexture(image1, false);
        t2 = TextureIO.newTexture(image2, false);

        gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_BLEND | GL.GL_MODULATE);
        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width,
            int height) {
        GL gl = drawable.getGL();
        GLU glu = new GLU();
        if (height <= 0) { // avoid a divide by zero error!

            height = 1;
        }
        final float h = (float) width / (float) height;
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(45.0f, h, 1.0, 20.0);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    public void display(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        // Clear the drawing area
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        // Reset the current matrix to the "identity"
        gl.glLoadIdentity();
        gl.glTranslatef(0.0f, 0.0f, -6.0f); //przesuni?cie o 6 jednostek
        gl.glRotatef(xrot, 1.0f, 0.0f, 0.0f); //rotacja wokó? osi X
        gl.glRotatef(yrot, 0.0f, 1.0f, 0.0f); //rotacja wokó? osi Y
        //Tu piszemy kod tworz?cy obiekty 3D
        gl.glBegin(GL.GL_QUADS);
//?ciana przednia
        gl.glColor3f(1.0f, 0.0f, 0.0f);
        gl.glVertex3f(-1.0f, -1.0f, 1.0f);
        gl.glVertex3f(1.0f, -1.0f, 1.0f);
        gl.glVertex3f(1.0f, 1.0f, 1.0f);
        gl.glVertex3f(-1.0f, 1.0f, 1.0f);
//sciana tylnia
        gl.glColor3f(0.0f, 1.0f, 0.0f);
        gl.glVertex3f(-1.0f, 1.0f, -1.0f);
        gl.glVertex3f(1.0f, 1.0f, -1.0f);
        gl.glVertex3f(1.0f, -1.0f, -1.0f);
        gl.glVertex3f(-1.0f, -1.0f, -1.0f);
//?ciana lewa
        gl.glColor3f(0.0f, 0.0f, 1.0f);
        gl.glVertex3f(-1.0f, -1.0f, -1.0f);
        gl.glVertex3f(-1.0f, -1.0f, 1.0f);
        gl.glVertex3f(-1.0f, 1.0f, 1.0f);
        gl.glVertex3f(-1.0f, 1.0f, -1.0f);
//?ciana prawa
        gl.glColor3f(1.0f, 1.0f, 0.0f);
        gl.glVertex3f(1.0f, 1.0f, -1.0f);
        gl.glVertex3f(1.0f, 1.0f, 1.0f);
        gl.glVertex3f(1.0f, -1.0f, 1.0f);
        gl.glVertex3f(1.0f, -1.0f, -1.0f);
//?ciana dolna
        gl.glColor3f(1.0f, 0.0f, 1.0f);
        gl.glVertex3f(-1.0f, -1.0f, 1.0f);
        gl.glVertex3f(-1.0f, -1.0f, -1.0f);
        gl.glVertex3f(1.0f, -1.0f, -1.0f);
        gl.glVertex3f(1.0f, -1.0f, 1.0f);
//górna
        gl.glColor3f(1.0f, 1.0f, 1.0f);
        gl.glVertex3f(-1.0f, 1.0f, -1.0f);
        gl.glVertex3f(-1.0f, 1.0f, 1.0f);
        gl.glVertex3f(1.0f, 1.0f, 1.0f);
        gl.glVertex3f(1.0f, 1.0f, -1.0f);;
        gl.glEnd();
        // Flush all drawing operations to the graphics card
        gl.glFlush();
    }
    

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
            boolean deviceChanged) {
    }
}
