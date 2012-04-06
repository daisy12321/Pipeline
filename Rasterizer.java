package pipeline;

import javax.vecmath.Vector4f;

import pipeline.fragment.FragmentProcessor;
import pipeline.misc.Fragment;
import pipeline.misc.FrameBuffer;
import pipeline.misc.Vertex;

/**
 * This class is responsible for interpolating the attributes given to it across
 * the triangle, and handing off the correctly interpolated values to the
 * fragment processor. Clipping also happens within this class.
 * 
 * @author ags
 */
public class Rasterizer {

    /** Number of user-supplied attributes */
    protected int na;

    /** Width of the image */
    protected int nx;

    /** Height of the image */
    protected int ny;


    // All the following arrays are preallocated for efficiency.

    /** Vertex data for triangle setup */
    protected float[][] vData;

    /** State arrays for rasterization.
     * Data is [e0, e1, e2, z', a0/w, a1/w, ..., 1/w]. */
    protected float[] xInc;
    protected float[] yInc;
    protected float[] rowData;
    protected float[] pixData;

    /** A pre-allocated fragment */
    Fragment frag;

    /** Scratch space for post-perspective vertex positions */
    Vector4f[] posn = { new Vector4f(), new Vector4f(), new Vector4f() };


    /**
     * The only constructor.
     * 
     * @param newNa The number of user defined attributes.
     * @param newNx The width of the image.
     * @param newNy The height of the image.
     */
    public Rasterizer(int newNa, int newNx, int newNy) {

        na = newNa;
        nx = newNx;
        ny = newNy;

        vData = new float[3][5 + na];
        xInc = new float[5 + na];
        yInc = new float[5 + na];
        rowData = new float[5 + na];
        pixData = new float[5 + na];

        frag = new Fragment(1 + na);
    }


    protected void rasterize(Vertex[] vs, FragmentProcessor fp, FrameBuffer fb) {

        // Assemble the vertex data.  Entries 0--2 are barycentric
        // coordinates; entry 3 is the screen-space depth; entries
        // 4 through 4 + (na-1) are the attributes provided in the
        // vertices; and entry 4 + na is the inverse w coordinate.
        // The caller-provided attributes are all interpolated with
        // perspective correction.
        for (int iv = 0; iv < 3; iv++) {
            float invW = 1.0f / vs[iv].v.w;
            posn[iv].scale(invW, vs[iv].v);
            for (int k = 0; k < 3; k++)
                vData[iv][k] = (k == iv ? 1 : 0);
            vData[iv][3] = posn[iv].z;
            for (int ia = 0; ia < na; ia++)
                vData[iv][4 + ia] = invW * vs[iv].attrs[ia];
            vData[iv][4 + na] = invW;
        }

        // Compute the bounding box of the triangle; bail out if it is empty.
        int ixMin = Math.max(0, ceil(min(posn[0].x, posn[1].x, posn[2].x)));
        int ixMax = Math.min(nx - 1, floor(max(posn[0].x, posn[1].x, posn[2].x)));
        int iyMin = Math.max(0, ceil(min(posn[0].y, posn[1].y, posn[2].y)));
        int iyMax = Math.min(ny - 1, floor(max(posn[0].y, posn[1].y, posn[2].y)));
        if (ixMin > ixMax || iyMin > iyMax)
            return;

        // Compute the determinant for triangle setup.  If it is negative, the
        // triangle is back-facing and we cull it.
        float dx1 = posn[1].x - posn[0].x, dy1 = posn[1].y - posn[0].y;
        float dx2 = posn[2].x - posn[0].x, dy2 = posn[2].y - posn[0].y;
        float det = dx1 * dy2 - dx2 * dy1;
        if (det < 0)
            return;

        // Triangle setup: compute the initial values and the x and y increments
        // for each attribute.
        for (int k = 0; k < 5 + na; k++) {
            float da1 = vData[1][k] - vData[0][k];
            float da2 = vData[2][k] - vData[0][k];
            xInc[k] = (da1 * dy2 - da2 * dy1) / det;
            yInc[k] = (da2 * dx1 - da1 * dx2) / det;
            rowData[k] = vData[0][k] + (ixMin - posn[0].x) * xInc[k] + (iyMin - posn[0].y) * yInc[k];
        }

        // Rasterize: loop over the bounding box, updating the attribute values.
        // For each pixel where the barycentric coordinates are in range, emit 
        // a fragment.  In our case this means calling the fragment processor to
        // process it immediately.
        for (frag.y = iyMin; frag.y <= iyMax; frag.y++) {
            for (int k = 0; k < 5 + na; k++)
                pixData[k] = rowData[k];
            for (frag.x = ixMin; frag.x <= ixMax; frag.x++) {
                if (pixData[0] >= 0 && pixData[1] >= 0 && pixData[2] >= 0) {
                    frag.attrs[0] = pixData[3];
                    float w = 1.0f / pixData[4 + na];
                    for (int ia = 0; ia < na; ia++)
                        frag.attrs[1 + ia] = pixData[4 + ia] * w;
                    fp.fragment(frag, fb);
                }
                for (int k = 0; k < 5 + na; k++)
                    pixData[k] += xInc[k];
            }
            for (int k = 0; k < 5 + na; k++)
                rowData[k] += yInc[k];
        }
    }


    // Utility routines for clarity

    protected static int ceil(float x) {

        return (int) Math.ceil(x);
    }

    protected static int floor(float x) {

        return (int) Math.floor(x);
    }

    protected static float min(float a, float b, float c) {

        return Math.min(Math.min(a, b), c);
    }

    protected static float max(float a, float b, float c) {

        return Math.max(Math.max(a, b), c);
    }

}
