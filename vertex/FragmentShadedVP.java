package pipeline.vertex;

import javax.vecmath.Color3f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import pipeline.Pipeline;
import pipeline.math.Matrix4f;
import pipeline.misc.Vertex;

/**
 * Passes the normals and the colors of each vertex on to be rasterized, and
 * later shaded during the fragment stage of the pipeline. This results in the
 * highest quality images, but results in costly computation.
 * 
 * @author ags
 */
public class FragmentShadedVP extends VertexProcessor {

    // The number of fragment attributes provided to the fragment processor
    public int nAttr() {
        return 9;   // diffuse color (r,g,b), normal (x,y,z), fragment position (x,y,z)
    }

    /**
     * @see VertexProcessor#updateTransforms(Pipeline)
     */
    public void updateTransforms(Pipeline pipe) {
        // TODO 2
    }

    /**
     * @see VertexProcessor#vertex(Vector3f, Color3f, Vector3f, Vector2f, Vertex)
     */
    public void vertex(Vector3f v, Color3f c, Vector3f n, Vector2f t, Vertex output) {
        // TODO 2
        
        output.setAttrs(nAttr()); // placeholder

    }

}