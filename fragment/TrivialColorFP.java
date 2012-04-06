package pipeline.fragment;

import pipeline.misc.Fragment;
import pipeline.misc.FrameBuffer;

/**
 * This trivial fragment processor will render the fragment's color into the framebuffer
 * regardless of whether it is in front of an earlier fragment.
 * 
 * @author ags
 */
public class TrivialColorFP extends FragmentProcessor {

    // The number of fragment attributes expected from the vertex processor
    public int nAttr() {
        return 3;   // surface color (r,g,b)
    }

    /**
     * @see FragmentProcessor#fragment(Fragment, FrameBuffer)
     */
    public void fragment(Fragment f, FrameBuffer fb) {

        fb.set(f.x, f.y, f.attrs[1], f.attrs[2], f.attrs[3], 0);
    }

}