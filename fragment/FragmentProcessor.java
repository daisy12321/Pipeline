package pipeline.fragment;

import pipeline.misc.Fragment;
import pipeline.misc.FrameBuffer;
import pipeline.misc.Texture;

/**
 * A fragment processor describes what happens in order to render fragments of a
 * triangle on screen. Things that happen here include shading calculations, z
 * buffering, texturing, etc.
 * 
 * @author ags
 */
public abstract class FragmentProcessor {

    /** A list of valid Fragment Processors */
    static public Class<?>[] classes = {
        TrivialColorFP.class,
        ColorZBufferFP.class,
        TexturedFP.class,
        PhongShadedFP.class,
        TexturedPhongFP.class,
        ReflectionMapFP.class
    };

    /** A reference to the currently loaded texture. */
    protected Texture texture;

    /**
     * Returns the number of attributes this fragment processor will use.
     * 
     * @return The number of attributes expected from the vertex processor.
     */
    public abstract int nAttr();

    /**
     * This is the main function of this class, which is called once for every
     * fragment f in the scene. f.attrs[0] is the Z value, and
     * f.attrs[1]...f.attrs[n] are the custom attributes provided by the
     * different vertex processors, where n = nAttr(). This method should update
     * the values in the frame buffer using fb.set(int, int, float, float,
     * float, float). The two ints are the (x, y) coordinate on screen. The next
     * three floats form the (r, g, b) triple. The last float is the z value of
     * the fragment (for use in z-buffering).
     *
     * @param f The fragment to render.
     * @param fb The framebuffer in which to render the fragment.
     */
    public abstract void fragment(Fragment f, FrameBuffer fb);

    /**
     * This sets the texture that the fragment processor should use.
     * 
     * @param newTexture The new texture to use.
     */
    public void setTexture(Texture newTexture) {

        texture = newTexture;
    }
}