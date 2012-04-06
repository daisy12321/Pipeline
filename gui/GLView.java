package pipeline.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Vector;

import javax.media.opengl.*;
import javax.media.opengl.awt.GLJPanel;
import javax.swing.JPanel;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;

import pipeline.Pipeline;
import pipeline.PointLight;
import pipeline.fragment.TexturedFP;
import pipeline.fragment.TexturedPhongFP;
import pipeline.fragment.TrivialColorFP;
import pipeline.misc.Camera;
import pipeline.scene.Scene;
import pipeline.vertex.ConstColorVP;
import pipeline.vertex.TexturedFragmentShadedVP;

/**
 * This class is a swing widget that will display the OpenGL implementation of
 * what is going on in the pipeline.
 * 
 * @author ags
 */
public class GLView extends JPanel implements GLEventListener {

    private static final long serialVersionUID = 1L;

    private static final float[] WHITE = new float[] {1.0f, 1.0f, 1.0f};

    /** The "image" where OpenGL will draw its results */
    protected GLJPanel canvas;

    /** The camera being used to render the scene */
    protected Camera camera;

    /** The scene being rendered */
    protected Scene scene;

    /** lights */
    protected Vector<PointLight> lights;

    public void setLights(Vector<PointLight> lights) {
        this.lights = lights;
    }

    /**
     * Default constructor - sets the height and width of the OpenGL canvas with
     * the same parameter.
     * 
     * @param sizei The height and width of the new canvas.
     */
    public GLView(int sizei, Vector<PointLight> lights) {

        GLCapabilities capabilities = new GLCapabilities(GLProfile.getDefault());
        canvas = new GLJPanel(capabilities);
        canvas.addGLEventListener(this);

        setSize(sizei, sizei);
        setPreferredSize(new Dimension(sizei, sizei));

        setLayout(new BorderLayout());
        add(canvas, BorderLayout.CENTER);

        this.lights = lights;
    }

    /**
     * @return Returns the canvas.
     */
    public GLJPanel getCanvas() {

        return this.canvas;
    }

    /**
     * Sets the camera for the OpenGL view to use.
     * 
     * @param c The new OpenGL camera.
     */
    public void setCamera(Camera c) {

        camera = c;
    }

    /**
     * Sets the scene for the OpenGL view to render.
     * 
     * @param s The scene to render.
     */
    public void setScene(Scene s) {

        scene = s;
    }

    /** Used to update the GL settings during display. */
    protected Class<?> fpClass;

    /** Used to update the GL settings during display. */
    protected Class<?> tpClass;

    /** Used to update the GL settings during display. */
    protected boolean classesChanged = false;

    /**
     * Configures the OpenGL view to use the given fragment and triangle
     * processors. Actually, since OpenGL won't use them at all, it just attempts
     * to match their indicated capabilities as best it can.
     * 
     * @param fp The fragment processor class.
     * @param tp The triangle processor class.
     */
    public void configure(Class<?> fp, Class<?> tp) {

        classesChanged = true;
        fpClass = fp;
        tpClass = tp;
    }

    /**
     * @see GLEventListener#init(GLDrawable)
     */
    public void init(GLAutoDrawable d) {

        GL2 gl = d.getGL().getGL2();
        gl.glDepthFunc(GL2.GL_LESS);
        gl.glDisable(GL2.GL_DEPTH_TEST);
        gl.glCullFace(GL2.GL_BACK);
        gl.glEnable(GL2.GL_CULL_FACE);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
        gl.glEnable(GL2.GL_NORMALIZE);
        gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_FILL);
        gl.glShadeModel(GL2.GL_SMOOTH);

        gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);

        gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
        gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);
        gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
        gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);

        // removed this, so the specular component is modulated with the texture - not added on top. 
        //    gl.glLightModeli(GL.GL_LIGHT_MODEL_COLOR_CONTROL, GL.GL_SEPARATE_SPECULAR_COLOR);

        float a = Pipeline.ambientIntensity;
        gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, new float[] {a, a, a}, 0);

        // This disables the infinite-viewer assumption
        gl.glLightModeli(GL2.GL_LIGHT_MODEL_LOCAL_VIEWER, GL.GL_TRUE);

        setupGlLights(gl);

        gl.glMateriali(GL2.GL_FRONT, GL2.GL_SHININESS, Pipeline.specularExponent);
        Color3f scol = Pipeline.specularColor;
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, new float[] {scol.x, scol.y, scol.z},0);
        gl.glClearColor(0, 0, 0, 1);

        gl.glDisable(GL2.GL_LIGHTING);
        gl.glDisable(GL2.GL_COLOR_MATERIAL);
        gl.glDisable(GL2.GL_DEPTH_TEST);
        gl.glDisable(GL2.GL_TEXTURE_2D);
    }

    private void setupGlLights(GL2 gl) {
        int[] glLights = { GL2.GL_LIGHT0, GL2.GL_LIGHT1, GL2.GL_LIGHT2, GL2.GL_LIGHT3, GL2.GL_LIGHT4, GL2.GL_LIGHT5, GL2.GL_LIGHT6, GL2.GL_LIGHT7 };

        if( lights.size() > glLights.length ) {
            System.out.println("WARNING: More lights than OGL allows. OGL will only render " + glLights.length +"  lights.");
        }

        System.out.println("setupGlLights for " + lights.size() + " lights");

        for( int i = 0; i < Math.min(glLights.length, lights.size()); i++) {
            Color3f d = lights.get(i).getIntensity();
            Point3f p = lights.get(i).getPosition();
            gl.glEnable(glLights[i]);
            gl.glLightfv(glLights[i], GL2.GL_DIFFUSE, new float[] { d.x, d.y, d.z, 1.0f }, 0);
            gl.glLightfv(glLights[i], GL2.GL_SPECULAR, new float[] { 1f, 1f, 1f, 1f }, 0);
            gl.glLightfv(glLights[i], GL2.GL_POSITION, new float[] { p.x, p.y, p.z, 1.0f }, 0);
        }
    }

    /**
     * @see GLEventListener#display(GLDrawable)
     */
    public void display(GLAutoDrawable d) {

        GL2 gl = d.getGL().getGL2();

        if (classesChanged) {
            if (tpClass == ConstColorVP.class) {
                gl.glDisable(GL2.GL_LIGHTING);
                gl.glDisable(GL2.GL_COLOR_MATERIAL);
                gl.glShadeModel(GL2.GL_SMOOTH);
            }
            else if (tpClass == TexturedFragmentShadedVP.class ) {
                gl.glEnable(GL2.GL_LIGHTING);
                gl.glDisable(GL2.GL_COLOR_MATERIAL);	// ignore vertex colors
                gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, WHITE,0);
                gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, WHITE,0);
                gl.glShadeModel(GL2.GL_SMOOTH);
            }
            else {
                gl.glEnable(GL2.GL_LIGHTING);
                gl.glColorMaterial(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE);
                gl.glEnable(GL2.GL_COLOR_MATERIAL);
                gl.glShadeModel(GL2.GL_SMOOTH);
            }

            if (fpClass == TrivialColorFP.class) {
                gl.glDisable(GL2.GL_DEPTH_TEST);
                gl.glDisable(GL2.GL_TEXTURE_2D);
            }
            else if (fpClass == TexturedFP.class || fpClass == TexturedPhongFP.class) {
                gl.glEnable(GL2.GL_DEPTH_TEST);
                gl.glEnable(GL2.GL_TEXTURE_2D);
            }
            else {
                gl.glEnable(GL.GL_DEPTH_TEST);
                gl.glDisable(GL.GL_TEXTURE_2D);
            }

            classesChanged = false;
        }

        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        camera.setProjection(d);
        gl.glViewport(0, 0, canvas.getWidth(), canvas.getHeight());
        camera.setup(d);
        scene.render(d);
    }

    /**
     * @see GLEventListener#displayChanged(GLDrawable, boolean, boolean)
     */
    public void displayChanged(GLAutoDrawable arg0, boolean arg1, boolean arg2) {

        canvas.repaint();
    }

    /**
     * Here so that the MainFrame can manually refresh the canvas.
     */
    public void refresh() {

        canvas.repaint();
    }

    // Unused interface bits

    /**
     * @see GLEventListener#reshape(GLDrawable, int, int, int, int)
     */
    public void reshape(GLAutoDrawable arg0, int x, int y, int width, int height) {

    }

    public void dispose(GLAutoDrawable d) {

    }

}
