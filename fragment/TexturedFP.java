package pipeline.fragment;

import javax.vecmath.Color3f;
import javax.vecmath.Vector2f;

import pipeline.misc.Fragment;
import pipeline.misc.FrameBuffer;

/**
 * This FP does a texture lookup rather to determine the color of a fragment. It
 * also uses the z-buffering technique to draw the correct fragment.
 * 
 * @author ags
 */
public class TexturedFP extends FragmentProcessor {

    // The number of fragment attributes expected from the vertex processor
    public int nAttr() {
        return 5;   // surface color (r,g,b), texture coordinates (u,v)
    }

    /**
     * @see FragmentProcessor#fragment(Fragment, FrameBuffer)
     */
    public void fragment(Fragment f, FrameBuffer fb) {
        // similar to ColorZBuffer
        // but also samples texture (use texture.sample(inCoords, outColor))
        // and then multiplies surface color with texture color
        // should also clamp to [0, 1]
    	Vector2f p = new Vector2f(f.attrs[4], f.attrs[5]);
    	Color3f colorTmp = new Color3f();
    	texture.sample(p, colorTmp);
    	if (f.attrs[0] < fb.getZ(f.x, f.y)) {
    		fb.set(f.x, f.y, f.attrs[1] * colorTmp.x, f.attrs[2] * colorTmp.y, f.attrs[3] * colorTmp.z, f.attrs[0]);
    	}
    }
}