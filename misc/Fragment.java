package pipeline.misc;

/**
 * @author Beowulf
 */
public class Fragment {

    /** The screen space x coordinate of this fragment. */
    public int x;

    /** The screen space y coordinate of this fragment. */
    public int y;

    /** The attributes associated with this fragment. */
    public float[] attrs;

    /**
     * Creates an empty fragment
     */
    public Fragment(int na) {
        attrs = new float[na];
    }

}
