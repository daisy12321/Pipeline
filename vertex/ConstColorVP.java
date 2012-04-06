package pipeline.vertex;

import javax.vecmath.Color3f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import pipeline.Pipeline;
import pipeline.math.Matrix4f;
import pipeline.misc.Vertex;

/**
 * Sets each vertex up with a constant color, no matter what the viewing
 * conditions.
 * 
 * @author ags
 */
public class ConstColorVP extends VertexProcessor {

    /** This is the composed modelview, projection, and viewport matrix. */
    protected Matrix4f m = new Matrix4f();

    // The number of fragment attributes provided to the fragment processor
    public int nAttr() {
        return 3;   // surface color (r,g,b)
    }

    /**
     * @see VertexProcessor#updateTransforms(Pipeline)
     * 
     * In this simple VP we can multiply all matrices together.  In most other VPs
     * we'll need to keep the modelview matrix around separately.
     */
    public void updateTransforms(Pipeline pipe) {
        m.set(pipe.modelviewMatrix);
        m.leftCompose(pipe.projectionMatrix);
        m.leftCompose(pipe.viewportMatrix);
    }

    /**
     * @see VertexProcessor#vertex(Vector3f, Color3f, Vector3f, Vector2f, Vertex)
     */
    public void vertex(Vector3f v, Color3f c, Vector3f n, Vector2f t, Vertex output) {
        output.v.set(v.x, v.y, v.z, 1);
        m.rightMultiply(output.v);

        output.setAttrs(nAttr());
        output.attrs[0] = c.x;
        output.attrs[1] = c.y;
        output.attrs[2] = c.z;
    }

}