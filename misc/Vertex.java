package pipeline.misc;

import javax.vecmath.Vector4f;

/**
 * @author Beowulf
 */
public class Vertex {

    /** The 4D homogenous coordinate location. */
    public Vector4f v = new Vector4f();

    /** The attribute values associated with this vertex. */
    public float[] attrs;

    /**
     * Sets the number of attributes for this vertex.
     * 
     * @param n The new number of attributes for this vertex.
     */
    public void setAttrs(int n) {

        if (attrs != null && n == attrs.length)
            return;
        attrs = new float[n];
    }

}
