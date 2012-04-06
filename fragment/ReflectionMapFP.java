package pipeline.fragment;

import javax.vecmath.Color3f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import pipeline.misc.Fragment;
import pipeline.misc.FrameBuffer;

public class ReflectionMapFP extends FragmentProcessor {

    // The number of fragment attributes expected from the vertex processor
    public int nAttr() {
        return 9;   // diffuse color (r,g,b), normal (x,y,z), fragment position (x,y,z)
    }

    public void fragment(Fragment f, FrameBuffer fb) {
        // TODO 2
    }
}
