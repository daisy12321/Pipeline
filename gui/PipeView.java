package pipeline.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.nio.ByteBuffer;
import java.util.Vector;

import javax.swing.JPanel;

import pipeline.PointLight;
import pipeline.Pipeline;
import pipeline.misc.Camera;
import pipeline.misc.Geometry;
import pipeline.scene.Scene;

import javax.media.opengl.*;
import javax.media.opengl.awt.*;

import com.jogamp.opengl.util.gl2.GLUT;

/**
 * This class is a swing widget that will display the homegrown implementation
 * of what is going on in the pipeline.
 * 
 * @author ags
 */
public class PipeView extends JPanel implements GLEventListener {

    private static final long serialVersionUID = 1L;

    /** The "image" where the pipeline will display the results */
    protected GLJPanel canvas;

    /** The width of the canvas */
    protected int xsize;

    /** The height of the canvas */
    protected int ysize;

    /** A reference to the pipeline */
    protected Pipeline pipe;

    /** The camera currently being used to view the scene */
    protected Camera camera;

    /** The scene currently being rendered */
    protected Scene scene;

    /**
     * @return Returns the canvas.
     */
    public GLJPanel getCanvas() {

        return this.canvas;
    }

    /**
     * The default constructor takes in a size. This size is the size of the
     * canvas (both dimensions).
     * 
     * @param sizei The height and width of the canvas.
     */
    public PipeView(int sizei, Vector<PointLight> lights) {

        // For now use squares cus we are cheap
        xsize = sizei;
        ysize = sizei;
        setSize(xsize, ysize);
        setPreferredSize(new Dimension(xsize, ysize));

        // create canvas
        //GLCapabilities capabilities = new GLCapabilities();
        GLCapabilities capabilities = new GLCapabilities(GLProfile.getDefault());
        // Necessary, as far as I know, to make GLJPanel work. ags
        capabilities.setDoubleBuffered(false);
        canvas = new GLJPanel(capabilities);
        canvas.addGLEventListener(this);

        // Add it to the layout
        setLayout(new BorderLayout());
        add(canvas, BorderLayout.CENTER);

        pipe = new Pipeline(xsize, ysize, lights);
        Geometry.setPipeline(pipe);
    }

    /**
     * This simply configures the underlying pipeline with the appropriate
     * fragment and triangle processors.
     * 
     * @param fp The new fragment processor class.
     * @param tp The new triangle processor class.
     */
    public void configure(Class<?> fp, Class<?> tp) {

        pipe.configure(fp, tp);
    }

    /**
     * Sets the current camera.
     * 
     * @param c The new camera.
     */
    public void setCamera(Camera c) {

        camera = c;
    }

    /**
     * Sets the current scene.
     * 
     * @param s The new scene.
     */
    public void setScene(Scene s) {

        scene = s;
    }

    /**
     * @see GLEventListener#display(GLDrawable)
     */
    public void display(GLAutoDrawable d) {

        GL2 gl = d.getGL().getGL2();

        if (pipe.validConfiguration()) {
            pipe.clearFrameBuffer();
            camera.setProjection(pipe);
            pipe.viewport(0, 0, xsize, ysize);
            pipe.loadIdentity();
            camera.setup(pipe);
            scene.render(pipe);
            gl.glRasterPos2i(0, 0);
            gl.glDrawPixels(xsize, ysize, GL.GL_RGB, GL.GL_UNSIGNED_BYTE, ByteBuffer.wrap(pipe.getFrameData()));
        }
        else {
            gl.glClearColor(0, 0, 0, 0);
            gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
            GLUT glut = new GLUT();
            String msg = "Incompatible triangle and fragment processors selected.";
            int stringWidth = glut.glutBitmapLength(GLUT.BITMAP_HELVETICA_12, msg);
            gl.glRasterPos2i((xsize - stringWidth) / 2, ysize / 2);
            glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, msg);
        }
    }

    /**
     * This method exists so that the canvas can be repainted by the MainFrame
     * class from time to time, mostly in response to events that occur in the
     * MainFrame.
     */
    public void refresh() {

        canvas.repaint();
    }

    // Unused interface bits

    /**
     * @see GLEventListener#init(GLDrawable)
     */
    public void init(GLAutoDrawable d) {

    }

    /**
     * @see GLEventListener#reshape(GLDrawable, int, int, int, int)
     */
    public void reshape(GLAutoDrawable d, int x, int y, int width, int height) {

        GL2 gl = d.getGL().getGL2();
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(0, width, 0, height, -1, 1);
        gl.glViewport(0, 0, width, height);
        float aspect = (float) width / height;
        camera.setAspect(aspect);
    }

    /**
     * @see GLEventListener#displayChanged(GLDrawable, boolean, boolean)
     */
    public void displayChanged(GLAutoDrawable d, boolean arg1, boolean arg2) {

    }

    public void dispose(GLAutoDrawable d) {

    }
}