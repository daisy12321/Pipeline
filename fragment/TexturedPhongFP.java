package pipeline.fragment;

import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import pipeline.misc.Fragment;

/**
 * This FP works just like the PhongShadedFP, but also incorporates a texture.
 * 
 * @author ags
 */
public class TexturedPhongFP extends PhongShadedFP {

    // The number of fragment attributes expected from the vertex processor
    public int nAttr() {
        return 8;   // normal (x,y,z), texture coordinates (u,v), fragment position (x,y,z)
    }

    // TODO 2
}
