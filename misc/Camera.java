package pipeline.misc;

import javax.vecmath.Matrix3f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import javax.media.opengl.*;
import javax.media.opengl.glu.GLU;

import pipeline.Pipeline;

/**
 * @author ags
 */
public class Camera {
    protected GLU myGLU;

    static final float THETA_LIMIT = (float) (89 * Math.PI / 180);

    protected float near = 1.0f;

    protected float far = 10f;

    protected float ht = 0.6f;

    protected float aspect = 1.0f;

    protected Vector3f eye = new Vector3f();

    protected Vector3f target = new Vector3f();

    protected Vector3f up = new Vector3f();

    /**
     * Constructs a new camera. Requires all
     * 
     * @param newEye
     * @param newTarget
     * @param newUp
     * @param newNear
     * @param newFar
     * @param ht
     */
    public Camera(Vector3f newEye, Vector3f newTarget, Vector3f newUp, float newNear, float newFar, float ht) {

        eye.set(newEye);
        target.set(newTarget);
        up.set(newUp);
        near = newNear;
        far = newFar;
        this.ht = ht;
        // GL.

        myGLU = new GLU();
    }

    /*
     * Returns 0 if a is smallest, 1 if b is smallest, 2 if c is smallest.
     */
    static private int argmin(double a, double b, double c) {

        return a < b ? (a < c ? 0 : 2) : (b < c ? 1 : 2);
    }

    /* Returns a vector that is not nearly parallel to v. */
    static private Vector3f nonParallelVector(Vector3f v) {

        int i = argmin(Math.abs(v.x), Math.abs(v.y), Math.abs(v.z));
        Vector3f u = new Vector3f();
        if (i == 0)
            u.x = 1;
        else if (i == 1)
            u.y = 1;
        else if (i == 2)
            u.z = 1;
        return u;
    }

    /**
     * @param mouseDelta
     */
    public void orbit(Vector2f mouseDelta) {

        // Build arbitrary frame at target point with w = up
        Vector3f u = new Vector3f(), v = new Vector3f(), w = new Vector3f();
        w.set(up);
        w.normalize();
        u.set(nonParallelVector(w));
        v.cross(w, u);
        v.normalize();
        u.cross(v, w);
        Matrix3f basis = new Matrix3f();
        basis.setColumn(0, u);
        basis.setColumn(1, v);
        basis.setColumn(2, w);
        Matrix3f basisInv = new Matrix3f();
        basisInv.invert(basis);

        // write eye in that frame
        Vector3f e = new Vector3f(eye);
        e.sub(target);
        basisInv.transform(e);

        // write e in spherical coordinates
        double r = e.length();
        double phi = Math.atan2(e.y, e.x);
        double theta = Math.asin(e.z / r);

        // increment phi and theta by mouse motion
        phi += -Math.PI / 2 * mouseDelta.x;
        theta += -Math.PI / 2 * mouseDelta.y;
        if (theta > THETA_LIMIT)
            theta = THETA_LIMIT;
        if (theta < -THETA_LIMIT)
            theta = -THETA_LIMIT;

        // write e back in cartesian world coords
        e.set((float) (r * Math.cos(phi) * Math.cos(theta)), (float) (r * Math.sin(phi) * Math.cos(theta)), (float) (r * Math.sin(theta)));
        basis.transform(e, eye);
    }

    /**
     * @param mouseDelta
     * @param dolly
     */
    public void panDolly(Vector2f mouseDelta, boolean dolly) {

        // Build frame at eye point with w up-ish and u toward target
        Vector3f u = new Vector3f(), v = new Vector3f(), w = new Vector3f();
        u.set(target);
        u.sub(eye);
        Vector3f t = new Vector3f(u);
        u.normalize();
        w.set(up);
        w.normalize();
        v.cross(w, u);
        v.normalize();
        w.cross(u, v);
        Matrix3f basis = new Matrix3f();
        basis.setColumn(0, u);
        basis.setColumn(1, v);
        basis.setColumn(2, w);
        Matrix3f basisInv = new Matrix3f();
        basisInv.invert(basis);

        // drive eye forward if dollying
        if (dolly) {
            eye.scaleAdd(0.1f, u, eye);
        }

        // write target in that frame
        basisInv.transform(t);

        // write t in spherical coordinates
        double r = t.length();
        double phi = Math.atan2(t.y, t.x);
        double theta = Math.asin(t.z / r);

        // increment phi and theta by mouse motion
        phi += -Math.PI / 2 * mouseDelta.x;
        theta += Math.PI / 2 * mouseDelta.y;
        if (theta > THETA_LIMIT)
            theta = THETA_LIMIT;
        if (theta < -THETA_LIMIT)
            theta = -THETA_LIMIT;

        // write t back in cartesian world coords
        t.set((float) (r * Math.cos(phi) * Math.cos(theta)), (float) (r * Math.sin(phi) * Math.cos(theta)), (float) (r * Math.sin(theta)));
        basis.transform(t, target);
        target.normalize();
        target.add(eye);
    }

    /**
     * @param d
     */
    public void dolly(float d) {

        eye.scaleAdd(-d, eye, eye);
    }

    /**
     * @param pipe
     */
    public void setProjection(Pipeline pipe) {

        pipe.frustum(-ht * aspect, ht * aspect, -ht, ht, near, far);
    }

    /**
     * @param d
     */
    public void setProjection(GLAutoDrawable d) {
        GL2 gl = d.getGL().getGL2();
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glFrustum(-ht * aspect, ht * aspect, -ht, ht, near, far);
    }

    /**
     * @param d
     */
    public void setAspect(float d) {

        aspect = d;
    }

    /**
     * @param pipe
     */
    public void setup(Pipeline pipe) {

        pipe.lookAt(eye, target, up);
    }

    /**
     * @param d
     */
    public void setup(GLAutoDrawable d) {
        GL2 gl = d.getGL().getGL2();
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
        myGLU.gluLookAt(eye.x, eye.y, eye.z, target.x, target.y, target.z, up.x, up.y, up.z);
    }

}
