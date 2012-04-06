package pipeline.vertex;

import javax.vecmath.Color3f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import pipeline.misc.Vertex;

/**
 * This is the same vertex processor as FragmentShadedVP, but it also
 * interpolates texture coordinates, allowing for texturing to be done in the
 * fragment stage.
 * 
 * @author ags
 */
public class TexturedFragmentShadedVP extends FragmentShadedVP {

    // The number of fragment attributes provided to the fragment processor
    public int nAttr() {
        return 8;   // normal (x,y,z), texture coordinates (u,v), fragment position (x,y,z)
    }


    /**
     * @see VertexProcessor#vertex(Vector3f, Color3f, Vector3f, Vector2f, Vertex)
     */
    public void vertex(Vector3f v, Color3f c, Vector3f n, Vector2f t, Vertex output) {
        // TODO 2
        
        output.setAttrs(nAttr()); // placeholder

    }

}