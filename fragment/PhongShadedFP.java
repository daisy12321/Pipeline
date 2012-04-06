package pipeline.fragment;

import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import pipeline.Pipeline;
import pipeline.PointLight;
import pipeline.misc.Fragment;
import pipeline.misc.FrameBuffer;

/**
 * This is the fragment program which actually uses a shading model to compute
 * the color on a per fragment basis.
 * 
 * @author ags
 */
public class PhongShadedFP extends FragmentProcessor {

    // The number of fragment attributes expected from the vertex processor
    public int nAttr() {
        return 9;   // diffuse color (r,g,b), normal (x,y,z), fragment position (x,y,z)
    }

    /**
     * @see FragmentProcessor#fragment(Fragment, FrameBuffer)
     */
    public void fragment(Fragment f, FrameBuffer fb) {
        // TODO 2
    }
}
