package org.yourorghere;

import com.sun.opengl.util.Animator;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;

/**
 * SimpleJOGL.java <BR>
 * author: Brian Paul (converted to Java by Ron Cemer and Sven Goethel)
 * <P>
 *
 * This version is equal to Brian Paul's version 1.2 1999/10/21
 */
public class SimpleJOGL implements GLEventListener {

    //statyczne pola okre?laj?ce rotacj? wokó? osi X i Y
    private static float xrot = 0.0f, yrot = 0.0f;
    public static float ambientLight[] = {0.3f, 0.3f, 0.3f, 1.0f};//swiat?o otaczajšce
    public static float diffuseLight[] = {0.7f, 0.7f, 0.7f, 1.0f};//?wiat?o rozproszone
    public static float specular[] = {1.0f, 1.0f, 1.0f, 1.0f}; //?wiat?o odbite
    public static float lightPos[] = {0.0f, 150.0f, 150.0f, 1.0f};//pozycja ?wiat?a

    public static void main(String[] args) {
        Frame frame = new Frame("Simple JOGL Application");
        GLCanvas canvas = new GLCanvas();

        canvas.addGLEventListener(new SimpleJOGL());
        frame.add(canvas);
        frame.setSize(640, 480);
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
                    xrot -= 1.0f;
                }
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    xrot += 1.0f;
                }
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    yrot += 1.0f;
                }
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    yrot -= 1.0f;
                }
                if (e.getKeyChar() == 'q') {
                    ambientLight = new float[]{ambientLight[0] - 0.1f, ambientLight[0] - 0.1f, ambientLight[0] - 0.1f, 1};
                }
                if (e.getKeyChar() == 'w') {
                    ambientLight = new float[]{ambientLight[0] + 0.1f, ambientLight[0] + 0.1f, ambientLight[0] + 0.1f, 1};
                }
                if (e.getKeyChar() == 'a') {
                    diffuseLight = new float[]{diffuseLight[0] - 0.1f, diffuseLight[0] - 0.1f, diffuseLight[0] - 0.1f, 1};
                }
                if (e.getKeyChar() == 's') {
                    diffuseLight = new float[]{diffuseLight[0] + 0.1f, diffuseLight[0] + 0.1f, diffuseLight[0] + 0.1f, 1};
                }
                if (e.getKeyChar() == 'z') {
                    specular = new float[]{specular[0] - 0.1f, specular[0] - 0.1f, specular[0] - 0.1f, 1};
                }
                if (e.getKeyChar() == 'x') {
                    specular = new float[]{specular[0] + 0.1f, specular[0] + 0.1f, specular[0] + 0.1f, 1};
                }
                if (e.getKeyChar() == 'n') {
                    lightPos[3] = 0;
                }
                if (e.getKeyChar() == 'm') {
                    lightPos[3] = 1;
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
        // Use debug pipeline
        // drawable.setGL(new DebugGL(drawable.getGL()));

        GL gl = drawable.getGL();
        System.err.println("INIT GL IS: " + gl.getClass().getName());

        // Enable VSync
        gl.setSwapInterval(1);

        //warto?ci sk?adowe o?wietlenia i koordynaty ?ród?a ?wiat?a
        //(czwarty parametr okre?la odleg?o?? ?ród?a:
        //0.0f-niesko?czona; 1.0f-okre?lona przez pozosta?e parametry)
        gl.glEnable(GL.GL_LIGHTING); //uaktywnienie o?wietlenia
        //ustawienie parametrów ?ród?a ?wiat?a nr. 0
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_AMBIENT, ambientLight, 0); //swiat?o otaczajšce
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, diffuseLight, 0); //?wiat?o rozproszone
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_SPECULAR, specular, 0); //?wiat?o odbite
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, lightPos, 0); //pozycja ?wiat?a
        gl.glEnable(GL.GL_LIGHT0); //uaktywnienie ?ród?a ?wiat?a nr. 0
        gl.glEnable(GL.GL_COLOR_MATERIAL); //uaktywnienie ?ledzenia kolorów
        //kolory b?dš ustalane za pomocš glColor
        gl.glColorMaterial(GL.GL_FRONT, GL.GL_AMBIENT_AND_DIFFUSE);
        //Ustawienie jasno?ci i odblaskowo?ci obiektów
        float specref[] = {1.0f, 1.0f, 1.0f, 1.0f}; //parametry odblaskowo?ci
        gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, specref, 0);

        gl.glMateriali(GL.GL_FRONT, GL.GL_SHININESS, 128);

        gl.glEnable(GL.GL_DEPTH_TEST);
        // Setup the drawing area and shading mode
        gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        gl.glShadeModel(GL.GL_SMOOTH); // try setting this to GL_FLAT and see what happens.

    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
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
        
        gl.glLightfv(GL.GL_LIGHT0,GL.GL_AMBIENT,ambientLight,0);
        gl.glLightfv(GL.GL_LIGHT0,GL.GL_DIFFUSE,diffuseLight,0);
        gl.glLightfv(GL.GL_LIGHT0,GL.GL_SPECULAR,specular,0);
        gl.glLightfv(GL.GL_LIGHT0,GL.GL_POSITION,lightPos,0);

        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();
        gl.glTranslatef(0.0f, 0.0f, -6.0f); //przesuni?cie o 6 jednostek
        gl.glRotatef(xrot, 1.0f, 0.0f, 0.0f); //rotacja wokó? osi X
        gl.glRotatef(yrot, 0.0f, 1.0f, 0.0f); //rotacja wokó? osi Y

        gl.glFlush();
        float x, y, kat;

//        gl.glBegin(GL.GL_TRIANGLE_FAN);
//        gl.glColor3f(1.0f, 0.0f, 0.0f);
//        gl.glVertex3f(0.0f, 0.0f, -1.0f); //?rodek
//        //for (kat = (float) (2.0f * Math.PI); kat > 0.0; kat -= (Math.PI / 32.0f)) {
//        for (kat = 0.0f; kat < (2.0f * Math.PI); kat += (Math.PI / 32.0f)) {
//            x = 1.5f * (float) Math.sin(kat);
//            y = 1.5f * (float) Math.cos(kat);
//            gl.glVertex3f(x, y, -1.0f); //kolejne punkty
//        }
//        gl.glEnd();
//
//        gl.glBegin(GL.GL_TRIANGLE_FAN);
//        gl.glColor3f(0.5f, 1.0f, 0.0f);
//        gl.glVertex3f(0.0f, 0.0f, 1.0f); //?rodek
//        for (kat = (float) (2.0f * Math.PI); kat > 0.0; kat -= (Math.PI / 32.0f)) {
//            //for (kat = 0.0f; kat < (2.0f * Math.PI); kat += (Math.PI / 32.0f)) {
//            x = 1.5f * (float) Math.sin(kat);
//            y = 1.5f * (float) Math.cos(kat);
//            gl.glVertex3f(x, y, 1.0f); //kolejne punkty
//        }
//        gl.glEnd();
//
//        gl.glBegin(GL.GL_QUAD_STRIP);
//        gl.glColor3f(1.0f, 1.0f, 0.0f);
//
//        //?rodek
//        //for (kat = 0.0f; kat < (2.0f * Math.PI); kat += (Math.PI / 32.0f)) {
//        for (kat = 0.0f; kat < (2.0f * Math.PI); kat += (Math.PI / 32.0f)) {
//            x = 1.5f * (float) Math.sin(kat);
//            y = 1.5f * (float) Math.cos(kat);
//            gl.glVertex3f(x, y, -1.0f);
//            gl.glVertex3f(x, y, 1.0f);//kolejne punkty
//        }
//        gl.glEnd();

        gl.glBegin(GL.GL_QUADS);
//?ciana przednia
        gl.glColor3f(0.7f, 0.0f, 0.0f);
        gl.glNormal3f(0.0f,0.0f,1.0f);
        gl.glVertex3f(-1.0f, -1.0f, 1.0f);
        gl.glVertex3f(1.0f, -1.0f, 1.0f);
        gl.glVertex3f(1.0f, 1.0f, 1.0f);
        gl.glVertex3f(-1.0f, 1.0f, 1.0f);
//sciana tylniaa
        gl.glColor3f(0.7f, 0.0f, 0.0f);
        gl.glNormal3f(0.0f,0.0f,-1.0f);
        gl.glVertex3f(-1.0f, 1.0f, -1.0f);
        gl.glVertex3f(1.0f, 1.0f, -1.0f);
        gl.glVertex3f(1.0f, -1.0f, -1.0f);
        gl.glVertex3f(-1.0f, -1.0f, -1.0f);
//?ciana lewa
        gl.glColor3f(0.7f, 0.0f, 0.0f);
        gl.glNormal3f(-1.0f,0.0f,0.0f);
        gl.glVertex3f(-1.0f, -1.0f, -1.0f);
        gl.glVertex3f(-1.0f, -1.0f, 1.0f);
        gl.glVertex3f(-1.0f, 1.0f, 1.0f);
        gl.glVertex3f(-1.0f, 1.0f, -1.0f);
//?ciana prawa
        gl.glColor3f(0.7f, 0.0f, 0.0f);
        gl.glNormal3f(1.0f,0.0f,0.0f);
        gl.glVertex3f(1.0f, 1.0f, -1.0f);
        gl.glVertex3f(1.0f, 1.0f, 1.0f);
        gl.glVertex3f(1.0f, -1.0f, 1.0f);
        gl.glVertex3f(1.0f, -1.0f, -1.0f);
//?ciana dolna
        gl.glColor3f(0.7f, 0.0f, 0.0f);
        gl.glNormal3f(0.0f,-1.0f,0.0f);
        gl.glVertex3f(-1.0f, -1.0f, 1.0f);
        gl.glVertex3f(-1.0f, -1.0f, -1.0f);
        gl.glVertex3f(1.0f, -1.0f, -1.0f);
        gl.glVertex3f(1.0f, -1.0f, 1.0f);
//sciana gorna
        gl.glColor3f(0.7f, 0.0f, 0.0f);
        gl.glNormal3f(0.0f,1.0f,0.0f);
        gl.glVertex3f(-1.0f, 1.0f, -1.0f);
        gl.glVertex3f(-1.0f, 1.0f, 1.0f);
        gl.glVertex3f(1.0f, 1.0f, 1.0f);
        gl.glVertex3f(1.0f, 1.0f, -1.0f);
        gl.glEnd();
//        gl.glBegin(GL.GL_TRIANGLES);
//        gl.glColor3f(1.0f, 0.0f, 0.0f);
//        gl.glVertex3f(-1.0f, 1.0f, -1.0f);
//        gl.glVertex3f(-1.0f, -1.0f, -1.0f);
//        gl.glVertex3f(0.0f, 0.0f, 1.0f);
//
//        gl.glColor3f(0.0f, 0.0f, 1.0f);
//        gl.glVertex3f(-1.0f, -1.0f, -1.0f);
//        gl.glVertex3f(1.0f, -1.0f, -1.0f);
//        gl.glVertex3f(0.0f, 0.0f, 1.0f);
//
//        gl.glColor3f(1.0f, 1.0f, 1.0f);
//        gl.glVertex3f(1.0f, -1.0f, -1.0f);
//        gl.glVertex3f(1.0f, 1.0f, -1.0f);
//
//        gl.glVertex3f(0.0f, 0.0f, 1.0f);
//
//        gl.glColor3f(1.0f, 0.0f, 1.0f);
//        gl.glVertex3f(1.0f, 1.0f, -1.0f);
//        gl.glVertex3f(-1.0f, 1.0f, -1.0f);
//        gl.glVertex3f(0.0f, 0.0f, 1.0f);
//        gl.glEnd();
//        for (x = -10.0f; x < 10.0f; x += 0.1f) {
//            gl.glBegin(GL.GL_QUADS);
//            double iks = (double) x;
//            gl.glColor3f(0.2f, 0.2f, 0.8f);
//            gl.glVertex3f(x, (float) Math.sin(iks), (float) Math.cos(iks) - 6.0f);
//            gl.glVertex3f(x + 0.1f, (float) Math.sin(iks + 0.1), (float) Math.cos(iks + 0.1) - 6.0f);
//            gl.glVertex3f(x + 0.1f, 0.0f, -6.0f);
//            gl.glVertex3f(x, 0.0f, -6.0f);
//            gl.glEnd();
//        }
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
    }
}