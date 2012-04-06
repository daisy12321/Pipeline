package pipeline;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Tuple2f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import javax.media.opengl.awt.GLJPanel;
import pipeline.fragment.FragmentProcessor;
import pipeline.gui.GLView;
import pipeline.gui.PipeView;
import pipeline.misc.Camera;
import pipeline.misc.Texture;
import pipeline.scene.Scene;
import pipeline.scene.SceneSmoothMesh;
import pipeline.scene.SceneBalls;
import pipeline.scene.SceneCube;
import pipeline.scene.SceneMaze;
import pipeline.scene.SceneMesh;
import pipeline.scene.SceneTriangle;
import pipeline.vertex.VertexProcessor;

/**
 * The class that starts the whole process and ties it together.
 * 
 * @author ags
 * some modifications to many of the files by Daniel Scharstein, Spring 2012
 */
public class MainFrame extends JFrame implements MouseListener, MouseMotionListener, ActionListener {

    private static final long serialVersionUID = 1L;

    /** The path to append to all image and data files */
    public final static String PATH = "data/";

    /** The width of the canvases */
    public final static int XSIZE = 600;

    /** The height of the canvases */
    public final static int YSIZE = XSIZE;

    /** The currently selected camera */
    protected Camera camera;

    /** A camera designed to orbit the scene at a fixed radius */
    protected Camera cameraOrbit;

    /** A camera designed to fly around the scene */
    protected Camera cameraFly;

    /** The scene currently being rendered */
    protected Scene scene;

    /** The GUI widget rendering the results of our pipeline process */
    protected PipeView pv;

    /** The GUI widget rendering the results of OpenGL's process */
    protected GLView gv;

    /** A timer firing off timed events */
    protected Timer timer;

    /** Selector for the triangle processor */
    protected JComboBox vertexCombo;

    /** Selector for the fragment processor */
    protected JComboBox fragmentCombo;

    /** Selector for the current scene */
    protected JComboBox sceneCombo;

    /** Selector for the current texture */
    protected JComboBox textureCombo;

    /** Selector for the current camera */
    protected JComboBox cameraCombo;

    /** Default constructor */
    public MainFrame() {

        super("Pipeline");

        // Handle closing the frame
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Fix the window size cus we are cheap for now
        //setResizable(false);

        // setup lights
        Vector<PointLight> lights = new Vector<PointLight>();
        lights.add(new PointLight(new Point3f(-2, -2, 0), new Color3f(0.8f, 0.7f, 0.7f)));
        lights.add(new PointLight(new Point3f(2, 2, 0), new Color3f(0.5f, 0.5f, 0.6f)));

        // Set up the views
        pv = new PipeView(XSIZE, lights);
        GLJPanel canvas = pv.getCanvas();
        canvas.addMouseListener(this);
        canvas.addMouseMotionListener(this);

        gv = new GLView(XSIZE, lights);
        canvas = gv.getCanvas();
        canvas.addMouseListener(this);
        canvas.addMouseMotionListener(this);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(pv, BorderLayout.WEST);
        getContentPane().add(new JLabel(" "), BorderLayout.CENTER);
        getContentPane().add(gv, BorderLayout.EAST);

        // Do some initialization
        timer = new Timer((int) (1.0 / 15 * 1000), this);

        cameraOrbit = new Camera(new Vector3f(3.0f, 4.0f, 5.0f), new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(0.0f, 1.0f, 0.0f), 1.0f, 10.0f, 0.6f);
        cameraFly = new Camera(new Vector3f(-1.5f, 0.0f, -5.0f), new Vector3f(-1.5f, 0.0f, 0.0f), new Vector3f(0.0f, 1.0f, 0.0f), 0.25f, 20.0f, 0.3f);
        pv.setCamera(cameraOrbit);
        gv.setCamera(cameraOrbit);
        camera = cameraOrbit;

        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new GridLayout(1, 2));
        labelPanel.add(new JLabel("Software", JLabel.CENTER));
        labelPanel.add(new JLabel("OpenGL", JLabel.CENTER));
        getContentPane().add(labelPanel, BorderLayout.NORTH);

        sceneCombo = new JComboBox(new Scene[] {
                new SceneTriangle(),
                new SceneBalls(),
                new SceneCube(),
                new SceneMesh(new File(PATH + "ship1.msh")),
                new SceneMesh(new File(PATH + "ship2.msh")),
                new SceneSmoothMesh(new File(PATH + "ship1.msh")),
                new SceneSmoothMesh(new File(PATH + "ship2.msh")),
                new SceneSmoothMesh(new File(PATH + "bunny500.msh")),
                new SceneSmoothMesh(new File(PATH + "bunny10k.msh")),
                new SceneMaze() });

        textureCombo = new JComboBox(new Texture[] {
                new Texture(new File(PATH + "chessboard.png")), 
                new Texture(new File(PATH + "chessboard-R.png")), 
                new Texture(new File(PATH + "ship1.png")),
                new Texture(new File(PATH + "ship2.png")), 
                new Texture(new File(PATH + "brick.png")),
                new Texture(new File(PATH + "quartz.png")),
                new Texture(new File(PATH + "cafe.png")),
                new Texture(new File(PATH + "trees.png")),
                new Texture(new File(PATH + "stpeters.png")),
        });

        cameraCombo = new JComboBox(new String[] { "Orbit Camera", "Flythrough" });
        vertexCombo = new JComboBox(VertexProcessor.classes);
        fragmentCombo = new JComboBox(FragmentProcessor.classes);

        vertexCombo.addActionListener(this);
        fragmentCombo.addActionListener(this);
        sceneCombo.addActionListener(this);
        textureCombo.addActionListener(this);
        cameraCombo.addActionListener(this);

        sceneCombo.setSelectedIndex(0);
        textureCombo.setSelectedIndex(0);

        JPanel controlPanel = new JPanel();
        controlPanel.add(vertexCombo);
        controlPanel.add(fragmentCombo);
        controlPanel.add(sceneCombo);
        controlPanel.add(textureCombo);
        controlPanel.add(cameraCombo);

        getContentPane().add(controlPanel, BorderLayout.SOUTH);
        scene = (Scene) sceneCombo.getSelectedItem();
        scene.setTexture((Texture) textureCombo.getSelectedItem());
        pv.setScene(scene);
        gv.setScene(scene);

        pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((int) (screenSize.getWidth() / 2 - getWidth() / 2), (int) (screenSize.getHeight() / 2 - getHeight() / 2));
    }

    protected boolean listening = true;

    /**
     * @see ActionListener#actionPerformed(ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {

        if (listening == false)
            return;

        Object src = e.getSource();

        if (button1Pressed && (src == timer) && (camera == cameraFly)) {
            listening = false;
            mouseDelta.set(currentMousePoint);
            mouseDelta.sub(lastMousePoint);
            mouseDelta.scale(0.08f);
            camera.panDolly(mouseDelta, button3Pressed);
            // System.out.println("panDollying: " + mouseDelta);
            pv.refresh();
            gv.refresh();
            listening = true;
            return;
        }
        if (src == timer)
            return;

        if (src == textureCombo || src == sceneCombo) {
            scene = (Scene) sceneCombo.getSelectedItem();
            scene.setTexture((Texture) textureCombo.getSelectedItem());
            pv.setScene(scene);
            gv.setScene(scene);
        }

        if (src == cameraCombo) {
            Object srcItem = ((JComboBox) src).getSelectedItem();
            if (srcItem.equals("Orbit Camera")) {
                pv.setCamera(cameraOrbit);
                gv.setCamera(cameraOrbit);
                camera = cameraOrbit;
                timer.stop();
            }
            else {
                pv.setCamera(cameraFly);
                gv.setCamera(cameraFly);
                camera = cameraFly;
                timer.start();
            }
        }

        Class<?> fpClass = (Class<?>) fragmentCombo.getSelectedItem();
        Class<?> tpClass = (Class<?>) vertexCombo.getSelectedItem();

        pv.configure(fpClass, tpClass);
        gv.configure(fpClass, tpClass);
        pv.refresh();
        gv.refresh();
    }

    // MouseListener interface

    protected Vector2f lastMousePoint = new Vector2f();

    protected Vector2f currentMousePoint = new Vector2f();

    protected boolean button1Pressed = false;

    protected boolean button3Pressed = false;

    protected Vector2f mouseDelta = new Vector2f();

    /**
     * @see MouseListener#mousePressed(MouseEvent)
     */
    public void mousePressed(MouseEvent e) {

        lastMousePoint.set(e.getX(), e.getY());
        windowToViewport(lastMousePoint);
        button1Pressed = flagSet(e, MouseEvent.BUTTON1_DOWN_MASK);
        button3Pressed = flagSet(e, MouseEvent.BUTTON3_DOWN_MASK) || flagSet(e, MouseEvent.BUTTON1_DOWN_MASK | MouseEvent.SHIFT_DOWN_MASK);

    }

    /**
     * @see MouseListener#mouseReleased(MouseEvent)
     */
    public void mouseReleased(MouseEvent e) {

        button1Pressed = flagSet(e, MouseEvent.BUTTON1_DOWN_MASK);
        button3Pressed = flagSet(e, MouseEvent.BUTTON3_DOWN_MASK) || flagSet(e, MouseEvent.BUTTON1_DOWN_MASK | MouseEvent.SHIFT_DOWN_MASK);
    }

    private static void windowToViewport(Tuple2f p) {

        p.set((2.0f * p.x - XSIZE) / YSIZE, (2.0f * (YSIZE - p.y - 1) - YSIZE) / YSIZE);
    }

    // MouseMotionListener interface

    static boolean flagSet(MouseEvent e, int flag) {

        return (e.getModifiersEx() & flag) == flag;
    }

    /**
     * @see MouseMotionListener#mouseDragged(MouseEvent)
     */
    public void mouseDragged(MouseEvent e) {

        if (camera == cameraOrbit) {
            mouseDelta.set(e.getX(), e.getY());
            windowToViewport(mouseDelta);

            mouseDelta.sub(lastMousePoint);

            if (flagSet(e, MouseEvent.BUTTON1_DOWN_MASK) && !flagSet(e, MouseEvent.SHIFT_DOWN_MASK))
                camera.orbit(mouseDelta);
            else if (flagSet(e, MouseEvent.BUTTON3_DOWN_MASK) || flagSet(e, MouseEvent.BUTTON1_DOWN_MASK | MouseEvent.SHIFT_DOWN_MASK))
                camera.dolly(mouseDelta.y);

            lastMousePoint.set(e.getX(), e.getY());
            windowToViewport(lastMousePoint);
            pv.refresh();
            gv.refresh();
        }
        else {
            currentMousePoint.set(e.getX(), e.getY());
            windowToViewport(currentMousePoint);
        }
    }

    // unused bits of MouseListener interface
    /**
     * @see MouseListener#mouseClicked(MouseEvent)
     */
    public void mouseClicked(MouseEvent e) {

    }

    /**
     * @see MouseListener#mouseEntered(MouseEvent)
     */
    public void mouseEntered(MouseEvent e) {

    }

    /**
     * @see MouseListener#mouseExited(MouseEvent)
     */
    public void mouseExited(MouseEvent e) {

    }

    // unused bits of MouseMotionListener interface
    /**
     * @see MouseMotionListener#mouseMoved(MouseEvent)
     */
    public void mouseMoved(MouseEvent e) {

    }

    /**
     * The main method. Just creates a MainFrame and shows it.
     * 
     * @param args
     */
    public static void main(String[] args) {

        try {
            (new MainFrame()).setVisible(true);
        }
        catch (UnsatisfiedLinkError ule) {
            System.out.println("Unsatisfied Link Error.");
            System.out.print("java.library.path=");
            System.out.println(System.getProperty("java.library.path"));
            System.out.println(ule);
            ule.printStackTrace();
        }
    }

}