package pipeline.fragment;

import pipeline.misc.Fragment;
import pipeline.misc.FrameBuffer;

/**
 * This fragment processor will place the indicated color into the framebuffer
 * only if the fragment passes the z buffer test (ie - it isn't occluded by
 * another fragment).
 * 
 * @author ags
 */
public class ColorZBufferFP extends FragmentProcessor {

    // The number of fragment attributes expected from the vertex processor
    public int nAttr() {
        return 3;   // surface color (r,g,b)
    }

    /**
     * @see FragmentProcessor#fragment(Fragment, FrameBuffer)
     */
    public void fragment(Fragment f, FrameBuffer fb) {
    	if (f.attrs[0] < fb.getZ(f.x, f.y)){
    		fb.set(f.x, f.y, f.attrs[1], f.attrs[2], f.attrs[3], f.attrs[0]);
    	}
    }

}