package pipeline.misc;

import javax.vecmath.Color3f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import pipeline.Pipeline;

import javax.media.opengl.*;

/**
 * Created by IntelliJ IDEA. User: ajb64 Date: Nov 10, 2003 Time: 6:37:37 PM To
 * change this template use Options | File Templates.
 */
public class Geometry {

    /* The reference to the software pipeline */
    protected static Pipeline pipe = null;

    /* Statically allocated variables to reduce memory allocation */
    /* These three arrays are used by both cube and sphere methods */
    private static final Vector3f[] normals = new Vector3f[3];

    private static final Color3f[] colors = new Color3f[3];

    private static final Vector3f[] vertices = new Vector3f[3];

    /*-------------------------------------*\
     * Begin Cube Drawing Methods and Data *
   \*-------------------------------------*/

    /* The four texture coordinates used on all quadrilaterals */
    private static final Vector2f t0 = new Vector2f(0.0f, 0.0f);

    private static final Vector2f t1 = new Vector2f(1.0f, 0.0f);

    private static final Vector2f t2 = new Vector2f(1.0f, 1.0f);

    private static final Vector2f t3 = new Vector2f(0.0f, 1.0f);

    /* The eight vertices of the cube. n = negative, p = positive */
    private static final Vector3f nnn = new Vector3f(-1.0f, -1.0f, -1.0f);

    private static final Vector3f nnp = new Vector3f(-1.0f, -1.0f, +1.0f);

    private static final Vector3f npn = new Vector3f(-1.0f, +1.0f, -1.0f);

    private static final Vector3f npp = new Vector3f(-1.0f, +1.0f, +1.0f);

    private static final Vector3f pnn = new Vector3f(+1.0f, -1.0f, -1.0f);

    private static final Vector3f pnp = new Vector3f(+1.0f, -1.0f, +1.0f);

    private static final Vector3f ppn = new Vector3f(+1.0f, +1.0f, -1.0f);

    private static final Vector3f ppp = new Vector3f(+1.0f, +1.0f, +1.0f);

    /* Normals for the different faces of the cube */
    private static final Vector3f lNormal = new Vector3f(-1, 0, 0);

    private static final Vector3f rNormal = new Vector3f(+1, 0, 0);

    private static final Vector3f dNormal = new Vector3f(0, -1, 0);

    private static final Vector3f uNormal = new Vector3f(0, +1, 0);

    private static final Vector3f bNormal = new Vector3f(0, 0, -1);

    private static final Vector3f fNormal = new Vector3f(0, 0, +1);

    /* Colors for the different faces of the cube */
    private static final Color3f lColor = new Color3f(0.4f, 0.4f, 0.8f);

    private static final Color3f rColor = new Color3f(0.8f, 0.8f, 0.4f);

    private static final Color3f dColor = new Color3f(0.8f, 0.4f, 0.4f);

    private static final Color3f uColor = new Color3f(0.4f, 0.8f, 0.8f);

    private static final Color3f bColor = new Color3f(0.8f, 0.4f, 0.8f);

    private static final Color3f fColor = new Color3f(0.4f, 0.8f, 0.4f);
    
    private static final boolean isFlatShaded = false;

    /**
     * Sets the pipe object.
     */
    public static void setPipeline(Pipeline inPipe) {

        if(pipe == null)
            pipe = inPipe;
        else throw new Error("Should only set the pipeline in geometry once.");

    }

    /*
     * Sends a quadrilateral to the software pipeline in the form of two
     * triangles. The methods takes in 4 vertices v0 - v3, the normal, and the
     * color.
     */
    public static void quad(Vector3f v0, Vector3f v1, Vector3f v2, Vector3f v3, Vector3f n, Color3f c) {

        pipe.begin(Pipeline.TRIANGLES);
        pipe.vertex(v0, c, n, t0);
        pipe.vertex(v1, c, n, t1);
        pipe.vertex(v2, c, n, t2);
        pipe.vertex(v0, c, n, t0);
        pipe.vertex(v2, c, n, t2);
        pipe.vertex(v3, c, n, t3);
        pipe.end();
    }

    /*
     * Sends a quadrilateral to the OpenGL pipeline. Mimics the other quad
     * function.
     */
    public static void quad(Vector3f v0, Vector3f v1, Vector3f v2, Vector3f v3, Vector3f n, Color3f c, GLAutoDrawable d) {

        GL2 gl = d.getGL().getGL2();

        gl.glBegin(GL2.GL_QUADS);

        gl.glColor3f(c.x, c.y, c.z);
        gl.glNormal3f(n.x, n.y, n.z);

        gl.glTexCoord2f(t0.x, t0.y);
        gl.glVertex3f(v0.x, v0.y, v0.z);

        gl.glTexCoord2f(t1.x, t1.y);
        gl.glVertex3f(v1.x, v1.y, v1.z);

        gl.glTexCoord2f(t2.x, t2.y);
        gl.glVertex3f(v2.x, v2.y, v2.z);

        gl.glTexCoord2f(t3.x, t3.y);
        gl.glVertex3f(v3.x, v3.y, v3.z);

        gl.glEnd();
    }

    /*
     * Sends two quadrilaterals in the form of four triangles to the software
     * pipeline. Both quads will occupy the same location, but will be back to
     * back, giving the effect of a 2 sided polygon.
     */
    public static void quadPair(Vector3f v0, Vector3f v1, Vector3f v2, Vector3f v3, Vector3f n, Color3f c1, Color3f c2, GLAutoDrawable d) {

        quad(v0, v1, v2, v3, n, c1, d);
        n.negate();

        quad(v3, v2, v1, v0, n, c2, d);
        n.negate();
    }

    /*
     * Sends two quadrilaterals to the OpenGL pipeline. Mimics the other quadPair
     * function.
     */
    public static void quadPair(Vector3f v0, Vector3f v1, Vector3f v2, Vector3f v3, Vector3f n, Color3f c1, Color3f c2) {

        quad(v0, v1, v2, v3, n, c1);
        n.negate();

        quad(v3, v2, v1, v0, n, c2);
        n.negate();
    }

    /*
     * Draws a unit cube (2x2x2) at the origin using the software pipeline. The
     * colors are fixed above.
     */
    public static void cube() {

        quad(nnn, nnp, npp, npn, lNormal, lColor);
        quad(pnn, ppn, ppp, pnp, rNormal, rColor);
        quad(nnn, pnn, pnp, nnp, dNormal, dColor);
        quad(npn, npp, ppp, ppn, uNormal, uColor);
        quad(nnn, npn, ppn, pnn, bNormal, bColor);
        quad(nnp, pnp, ppp, npp, fNormal, fColor);
    }

    /*
     * Draws a unit cube (2x2x2) at the origin using the OpenGL pipeline. Mimics
     * the other cube function.
     */
    public static void cube(GLAutoDrawable d) {

        quad(nnn, nnp, npp, npn, lNormal, lColor, d);
        quad(pnn, ppn, ppp, pnp, rNormal, rColor, d);
        quad(nnn, pnn, pnp, nnp, dNormal, dColor, d);
        quad(npn, npp, ppp, ppn, uNormal, uColor, d);
        quad(nnn, npn, ppn, pnn, bNormal, bColor, d);
        quad(nnp, pnp, ppp, npp, fNormal, fColor, d);
    }

    /*---------------------------------------*\
     * Begin Sphere Drawing Methods and Data *
   \*---------------------------------------*/

    /* Statically allocated arrays to reduce memory allocation overhead. */
    /* These arrays are used by only the sphere methods. */
    private static final Vector3f nrml = new Vector3f();

    private static final Vector2f[] texs = new Vector2f[] { new Vector2f(), new Vector2f(), new Vector2f() };

    /* The eight initial vertices for the sphere approximation */
    private static final Vector3f v_p00 = new Vector3f(+1.0f, 0.0f, 0.0f);

    private static final Vector3f v_n00 = new Vector3f(-1.0f, 0.0f, 0.0f);

    private static final Vector3f v_0p0 = new Vector3f(0.0f, +1.0f, 0.0f);

    private static final Vector3f v_0n0 = new Vector3f(0.0f, -1.0f, 0.0f);

    private static final Vector3f v_00p = new Vector3f(0.0f, 0.0f, +1.0f);

    private static final Vector3f v_00n = new Vector3f(0.0f, 0.0f, -1.0f);

    /*
     * Takes in a 3D location and spits out its texture coordinate. This version
     * simply returns 1/2 the x and y coordinate, offset by 0.5 This will ensure
     * that all texture coordinates are valid, based on assumption about the
     * incoming 3D location. This is valid because we know this method will only
     * be called from our spheretri methods.
     */
    private static void xyTex(Vector3f v, Vector2f tex) {

        tex.x = v.x / 2 + 0.5f;
        tex.y = v.y / 2 + 0.5f;
    }

    /*
     * Recursively generates a sphere using triangles and puts the resulting
     * polygons into the software pipeline.
     */
    private static void spheretri(int n, Vector3f v0, Vector3f v1, Vector3f v2, Color3f c) {

        if (n == 0) {
            vertices[0] = v0;
            vertices[1] = v1;
            vertices[2] = v2;
            colors[0] = colors[1] = colors[2] = c;
            if (isFlatShaded) {
                nrml.add(v0, v1);
                nrml.add(v2);
                nrml.normalize();

                normals[0] = normals[1] = normals[2] = nrml;
                // pipe.tp.triangle(vertices, colors, normals, null);
                pipe.renderTriangle(vertices, colors, normals, null);
            }
            else {
                xyTex(v0, texs[0]);
                xyTex(v1, texs[1]);
                xyTex(v2, texs[2]);
                // pipe.tp.triangle(vertices, colors, vertices, texs);
                pipe.renderTriangle(vertices, colors, vertices, texs);
            }
        }
        else {
            Vector3f v01 = new Vector3f();
            Vector3f v12 = new Vector3f();
            Vector3f v20 = new Vector3f();

            v01.add(v0, v1);
            v01.normalize();
            v12.add(v1, v2);
            v12.normalize();
            v20.add(v2, v0);
            v20.normalize();

            spheretri(n - 1, v01, v12, v20, c);
            spheretri(n - 1, v0, v01, v20, c);
            spheretri(n - 1, v1, v12, v01, c);
            spheretri(n - 1, v2, v20, v12, c);
        }
    }

    /*
     * Recursively generates a sphere using triangles and puts the resulting
     * polygons into the OpenGL pipeline. Mimics the other spheretri function.
     */
    private static void spheretri(int n, Vector3f v0, Vector3f v1, Vector3f v2, Color3f c, GLAutoDrawable d) {

        GL2 gl = d.getGL().getGL2();

        if (n == 0) {
            if (isFlatShaded) {
                nrml.add(v0, v1);
                nrml.add(v2);
                nrml.normalize();

                gl.glBegin(GL2.GL_TRIANGLES);

                gl.glColor3f(c.x, c.y, c.z);
                gl.glNormal3f(nrml.x, nrml.y, nrml.z);

                gl.glVertex3f(v0.x, v0.y, v0.z);
                gl.glVertex3f(v1.x, v1.y, v1.z);
                gl.glVertex3f(v2.x, v2.y, v2.z);

                gl.glEnd();
            }
            else {
                gl.glBegin(GL2.GL_TRIANGLES);

                gl.glColor3f(c.x, c.y, c.z);
                xyTex(v0, texs[0]);
                xyTex(v1, texs[1]);
                xyTex(v2, texs[2]);

                gl.glNormal3f(v0.x, v0.y, v0.z);
                gl.glTexCoord2f(texs[0].x, texs[0].y);
                gl.glVertex3f(v0.x, v0.y, v0.z);

                gl.glNormal3f(v1.x, v1.y, v1.z);
                gl.glTexCoord2f(texs[1].x, texs[1].y);
                gl.glVertex3f(v1.x, v1.y, v1.z);

                gl.glNormal3f(v2.x, v2.y, v2.z);
                gl.glTexCoord2f(texs[2].x, texs[2].y);
                gl.glVertex3f(v2.x, v2.y, v2.z);

                gl.glEnd();
            }
        }
        else {
            Vector3f v01 = new Vector3f();
            Vector3f v12 = new Vector3f();
            Vector3f v20 = new Vector3f();

            v01.add(v0, v1);
            v01.normalize();
            v12.add(v1, v2);
            v12.normalize();
            v20.add(v2, v0);
            v20.normalize();

            spheretri(n - 1, v01, v12, v20, c, d);
            spheretri(n - 1, v0, v01, v20, c, d);
            spheretri(n - 1, v1, v12, v01, c, d);
            spheretri(n - 1, v2, v20, v12, c, d);
        }
    }

    /*
     * Draws a sphere out of triangles, using the spheretri function. The sphere
     * is rendered to the software pipeline.
     */
    public static void sphere(int n, Color3f c) {

        spheretri(n, v_p00, v_0p0, v_00p, c);
        spheretri(n, v_00n, v_0p0, v_p00, c);
        spheretri(n, v_n00, v_0p0, v_00n, c);
        spheretri(n, v_00p, v_0p0, v_n00, c);
        spheretri(n, v_00p, v_0n0, v_p00, c);
        spheretri(n, v_p00, v_0n0, v_00n, c);
        spheretri(n, v_00n, v_0n0, v_n00, c);
        spheretri(n, v_n00, v_0n0, v_00p, c);
    }

    /*
     * Draws a sphere out of triangles, using the spheretri function. The sphere
     * is rendered to the OpenGL pipeline. Mimics the other sphere function.
     */
    public static void sphere(int n, Color3f c, GLAutoDrawable d) {

        spheretri(n, v_p00, v_0p0, v_00p, c, d);
        spheretri(n, v_00n, v_0p0, v_p00, c, d);
        spheretri(n, v_n00, v_0p0, v_00n, c, d);
        spheretri(n, v_00p, v_0p0, v_n00, c, d);
        spheretri(n, v_00p, v_0n0, v_p00, c, d);
        spheretri(n, v_p00, v_0n0, v_00n, c, d);
        spheretri(n, v_00n, v_0n0, v_n00, c, d);
        spheretri(n, v_n00, v_0n0, v_00p, c, d);
    }

}
