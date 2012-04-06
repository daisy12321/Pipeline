package pipeline;

import java.lang.reflect.Constructor;
import java.util.Vector;

import javax.vecmath.Color3f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import pipeline.fragment.FragmentProcessor;
import pipeline.fragment.TrivialColorFP;
import pipeline.math.Matrix4f;
import pipeline.misc.FrameBuffer;
import pipeline.misc.Texture;
import pipeline.misc.Vertex;
import pipeline.vertex.ConstColorVP;
import pipeline.vertex.VertexProcessor;

/**
 * This class directs the "pipeline process". Think of this class as describing
 * the hardware architecture.
 * 
 * @author ags
 */
public class Pipeline {

    private static final Class<?>[] EMPTY_CLASS_ARRAY = new Class[0];

    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

    private static final int MODE_NONE = 0;
    public static final int TRIANGLES = 1;
    public static final int TRIANGLE_STRIP = 2;
    public static final int TRIANGLE_FAN = 3;
    public static final int QUADS = 4;
    public static final int QUAD_STRIP = 5;
    private int mode = MODE_NONE;

    /** The triangle processor. Sets up the vertices for rasterization. */
    private VertexProcessor vp;

    /** The clipper. Clips triangles against the near plane. */
    private Clipper clipper;

    /** The rasterizer. Rasterizes triangles into fragments. */
    private Rasterizer rasterizer;

    /**
     * The fragment processor. Writes the fragments from the rasterizer into the
     * framebuffer.
     */
    private FragmentProcessor fp;

    /** The framebuffer. Holds the current image to be displayed. */
    private FrameBuffer framebuffer;

    /** Holds the premultiplied modelling and viewing matrix. */
    public Matrix4f modelviewMatrix = new Matrix4f();

    /** Holds the projection matrix. */
    public Matrix4f projectionMatrix = new Matrix4f();

    /** Holds the viewport matrix. */
    public Matrix4f viewportMatrix = new Matrix4f();

    /** Array of all the lights. */
    public static Vector<PointLight> lights;

    /** This is the intensity of ambient light. */
    public static final float ambientIntensity = 0.1f;

    /** This is the specular surface color. */
    public static final Color3f specularColor = new Color3f(0.4f, 0.4f, 0.4f);

    /** This is the phong exponent. */
    public static final int specularExponent = 40;

    /**
     * The default constructor needs to know what the dimensions are for its
     * framebuffer.
     * 
     * @param nx The width of the frame buffer.
     * @param ny The height of the frame buffer.
     */
    public Pipeline(int nx, int ny, Vector<PointLight> lights) {
        framebuffer = new FrameBuffer(nx, ny);
        configure(TrivialColorFP.class, ConstColorVP.class);
        Pipeline.lights = lights;
    }

    /**
     * Configures the pipeline so that the triangle and fragment processors are
     * now up to date. Forces some reinitialization in order to set up things like
     * the clipper and the rasterizer.
     * 
     * @param fpClass The class of the new fragment shader.
     * @param vpClass The class of the new triangle shader.
     */
    public void configure(Class<?> fpClass, Class<?> vpClass) {

        try {
            Constructor<?> c = fpClass.getConstructor(EMPTY_CLASS_ARRAY);
            fp = (FragmentProcessor) c.newInstance(EMPTY_OBJECT_ARRAY);
            rasterizer = new Rasterizer(fp.nAttr(), framebuffer.getWidth(), framebuffer.getHeight());
            clipper = new Clipper(fp.nAttr());
            c = vpClass.getConstructor(EMPTY_CLASS_ARRAY);
            vp = (VertexProcessor) c.newInstance(EMPTY_OBJECT_ARRAY);
            vp.updateTransforms(this);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns true as long as the triangle and fragment processors expect the
     * same number of attributes.
     * 
     * @return True as long as the triangle and fragment processors expect the
     *         same number of attributes.
     */
    public boolean validConfiguration() {

        return fp.nAttr() == vp.nAttr();
    }

    /**
     * Returns the current fragment program class
     * @return the class of the triangle processor
     */
    public Class<?> getTriangleClass() {

        return vp.getClass();

    }

    /**
     * Sets the texture for the underlying FP.
     * 
     * @param texture The new texture to use.
     */
    public void setTexture(Texture texture) {

        fp.setTexture(texture);
    }

    /**
     * Clears the underlying framebuffer.
     */
    public void clearFrameBuffer() {

        framebuffer.clear(0, 0, 0, 1);
    }

    /**
     * Returns the data in the framebuffer.
     * 
     * @return The data in the framebuffer.
     */
    public byte[] getFrameData() {

        return framebuffer.getData();
    }

    /**
     * Sets the modelview matrix to I, and notifies the VP of the change.
     */
    public void loadIdentity() {

        modelviewMatrix.setIdentity();
        recomputeMatrix();
    }

    /**
     * Right multiplies the model view matrix by a rotation for the given axis and
     * angle, and notifies the VP of the change.
     * 
     * @param angle The amount to rotate (in radians).
     * @param axis The axis about which to rotate.
     */
    public void rotate(float angle, Vector3f axis) {

        Matrix4f T = new Matrix4f();
        T.setRotate(angle, axis);
        modelviewMatrix.rightCompose(T);
        recomputeMatrix();
    }

    /**
     * Right multiplies the model view matrix by a translation for the given
     * values, and notifies the VP of the change.
     * 
     * @param v The translation amount.
     */
    public void translate(Vector3f v) {

        Matrix4f T = new Matrix4f();
        T.setTranslate(v);
        modelviewMatrix.rightCompose(T);
        recomputeMatrix();
    }

    /**
     * Right multiplies the model view matrix by a scale for the given values, and
     * notifies the VP of the change.
     * 
     * @param v The amount to scale by.
     */
    public void scale(Vector3f v) {

        Matrix4f T = new Matrix4f();
        T.setScale(v);
        modelviewMatrix.rightCompose(T);
        recomputeMatrix();
    }

    /**
     * Notifies the VP of any changes to the modelview, projection, or viewing
     * matrices.
     */
    protected void recomputeMatrix() {

        vp.updateTransforms(this);
    }

    /**
     * Sets the modelview matrix to be equal to the indicated viewing matrix, and
     * notifies the VP of the change.
     * 
     * @param eye The location of the eye.
     * @param target The target at which the eye is looking.
     * @param up A vector that is not parallel to (target - eye) so as to indicate
     *          which direction is up.
     */
    public void lookAt(Vector3f eye, Vector3f target, Vector3f up) {

        Matrix4f T = new Matrix4f();
        Vector3f w = new Vector3f();
        w.sub(eye, target);
        w.normalize();
        Vector3f u = new Vector3f();
        u.cross(up, w);
        u.normalize();
        Vector3f v = new Vector3f();
        v.cross(w, u);
        T.setCtoF(u, v, w, eye);
        modelviewMatrix.rightCompose(T);
        recomputeMatrix();
    }

    /**
     * Sets the projection matrix to represent the indicated viewing volume, and
     * notifies the VP of the change.
     * 
     * @param l The left extent of the view volume.
     * @param r The right extent of the view volume.
     * @param b The bottom extent of the view volume.
     * @param t The top extent of the view volume.
     * @param n The near plane of the view volume.
     * @param f The far plane of the view volume.
     */
    public void frustum(float l, float r, float b, float t, float n, float f) {

        projectionMatrix.setIdentity();
        projectionMatrix.m[0][0] = 2 * n / (r - l);
        projectionMatrix.m[0][2] = (r + l) / (r - l);
        projectionMatrix.m[1][1] = 2 * n / (t - b);
        projectionMatrix.m[1][3] = (t + b) / (t - b);
        projectionMatrix.m[2][2] = -(f + n) / (f - n);
        projectionMatrix.m[2][3] = -2 * f * n / (f - n);
        projectionMatrix.m[3][2] = -1;
        projectionMatrix.m[3][3] = 0;
        recomputeMatrix();
    }

    /**
     * Sets the viewport matrix to the indicated window on screen, and notifies
     * the VP of the change.
     * 
     * @param x The x location of the window.
     * @param y The y location of the window.
     * @param w The width of the window.
     * @param h The height of the window.
     */
    public void viewport(int x, int y, int w, int h) {

        float cx = x + 0.5f * w, cy = y + 0.5f * h;
        viewportMatrix.setIdentity();
        viewportMatrix.m[0][0] = 0.5f * w;
        viewportMatrix.m[0][3] = cx;
        viewportMatrix.m[1][1] = 0.5f * h;
        viewportMatrix.m[1][3] = cy;
        viewportMatrix.m[2][2] = 0.5f;
        viewportMatrix.m[2][3] = 0.5f;
        recomputeMatrix();

    }

    /** Cache of processed vertices for primitive assembly. */
    private final Vertex[] vertexCache = { new Vertex(), new Vertex(), new Vertex(), new Vertex() };

    /** A static variable to save allocation costs. */
    private final Vertex[] triangle1 = { new Vertex(), new Vertex(), new Vertex() };
    private final Vertex[] triangle2 = { new Vertex(), new Vertex(), new Vertex() };

    // 
    int vertexIndex, stripParity;

    /**
     * Sets the pipeline mode to render a particular type of primitive.
     */
    public void begin(int primType) {
        mode = primType;
        vertexIndex = 0;
        stripParity = 0;
    }

    public void vertex(Vector3f v, Color3f c, Vector3f n, Vector2f t) {
        vp.vertex(v, c, n, t, vertexCache[vertexIndex]);
        switch (mode) {
        case TRIANGLES:
            if (vertexIndex == 2) {
                renderTriangle(vertexCache);
                vertexIndex =0;
            } else
                vertexIndex++;
            break;
        case TRIANGLE_STRIP:
            if (vertexIndex == 2) {
                renderTriangle(vertexCache);
                swap(vertexCache, stripParity, 2);
                stripParity ^= 1;
            } else
                vertexIndex++;
            break;
        case TRIANGLE_FAN:
            if (vertexIndex == 2) {
                renderTriangle(vertexCache);
                swap(vertexCache, 1, 2);
            } else
                vertexIndex++;
            break;
        case QUADS:
            if (vertexIndex == 3) {
                renderTriangle(vertexCache);
                swap(vertexCache, 1, 2);
                swap(vertexCache, 2, 3);
                renderTriangle(vertexCache);
                vertexIndex = 0;
            } else
                vertexIndex++;
            break;
        case QUAD_STRIP:
            if (vertexIndex == 3) {
                swap(vertexCache, 2, 3);
                renderTriangle(vertexCache);
                swap(vertexCache, 1, 2);
                swap(vertexCache, 2, 3);
                renderTriangle(vertexCache);
                swap(vertexCache, 0, 2);
                vertexIndex = 2;
            } else
                vertexIndex++;
            break;
        }
    }

    public void end() {
        mode = MODE_NONE;
    }

    private static void swap(Vertex[] va, int i, int j) {
        Vertex temp = va[i];
        va[i] = va[j];
        va[j] = temp;
    }

    /**
     * Renders a triangle to the software pipeline.
     * 
     * @param v The 3 vertices of the triangle.
     * @param c The 3 colors of the triangle - one for each vertex.
     * @param n The 3 normals of the triangle - one for each vertex.
     * @param t The 3 texture coordinates of the triangle - one for each vertex.
     */
    public void renderTriangle(Vector3f[] v, Color3f[] c, Vector3f[] n, Vector2f[] t) {

        // Send to VP, get back attributes to interpolate
        vp.triangle(v, c, n, t, vertexCache);

        renderTriangle(vertexCache);
    }

    /**
     * Renders a triangle from already-processed vertices.
     * 
     * @param v The 3 vertices of the triangle.
     * @param c The 3 colors of the triangle - one for each vertex.
     * @param n The 3 normals of the triangle - one for each vertex.
     * @param t The 3 texture coordinates of the triangle - one for each vertex.
     */
    private void renderTriangle(Vertex[] vertices) {

        // See how many "unclipped" triangles we have
        int numberOfTriangles = clipper.clip(vertices, triangle1, triangle2);

        // If we have none, just stop
        if (numberOfTriangles == 0)
            return;

        // If we have two...render the second one
        if (numberOfTriangles == 2) {
            // Rasterize triangle, sending results to fp
            rasterizer.rasterize(triangle2, fp, framebuffer);
        }

        // And if we have 1 or 2, render the first one

        //Rasterize triangle, sending results to fp
        rasterizer.rasterize(triangle1, fp, framebuffer);
    }

}
