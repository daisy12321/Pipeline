package pipeline.vertex;

import javax.vecmath.Color3f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import pipeline.misc.Vertex;

/**
 * This is the same vertex processor as SmoothShadedVP, but allows for
 * texturing in the fragment stage by interpolating texture coordinates.
 * 
 * @author ags
 */
public class TexturedSmoothShadedVP extends SmoothShadedVP {

    // The number of fragment attributes provided to the fragment processor
    public int nAttr() {
        return 5;   // surface color (r,g,b), texture coordinates (u,v)
    }

    /**
     * @see VertexProcessor#vertex(Vector3f, Color3f, Vector3f, Vector2f, Vertex)
     */
    public void vertex(Vector3f v, Color3f c, Vector3f n, Vector2f t, Vertex output) {
        // simply call super.vertex(...)
        // then store texture coords in the additional two attributes
        super.vertex(v, c, n, t, output);
        output.setAttrs(nAttr()); // placeholder
        output.attrs[3] = t.x;
        output.attrs[4] = t.y;
    }

}