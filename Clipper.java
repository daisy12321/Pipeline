package pipeline;

import pipeline.misc.Vertex;

/**
 * @author Beowulf
 */
public class Clipper {

    /** Number of user-supplied attributes */
    protected int na;

    /** Temporaries for clipping */
    protected Vertex[] fClip = { new Vertex(), new Vertex(), new Vertex() };

    /**
     * Initializes a new clipper with a given number of attributes.
     * 
     * @param newNa The number of attributes.
     */
    public Clipper(int newNa) {

        na = newNa;

        fClip[0].setAttrs(newNa);
        fClip[1].setAttrs(newNa);
        fClip[2].setAttrs(newNa);
    }

    /**
     * The interface for the clipper. Each triangle will be clipped against the
     * near plane, resulting in either 0, 1, or 2 triangles. The number of
     * triangles will be returned, and the resulting vertices will be stored into
     * fOut1 if only one triangle results, or in both fOut1 and fOut2 if two triangles
     * result.
     * 
     * @param f The vertices of the triangle to be clipped.
     * @param fOut1 The vertices of the first resulting triangle, if any.
     * @param fOut2 The vertices of the second resulting triangle, if any.
     * @return The number of resulting triangles.
     */
    public int clip(Vertex[] f, Vertex[] fOut1, Vertex[] fOut2) {

        // Clip the triangle against the near plane, which is z == 0 in homogeneous
        // screen space.

        int code = ((f[0].v.z > 0) ? 1 : 0) | ((f[1].v.z > 0) ? 2 : 0) | ((f[2].v.z > 0) ? 4 : 0);

        if (code == 0) // all three out
            return 0;

        else if (code == 1 || code == 2 || code == 4) { // one in, two out
            int kIn, kOut1, kOut2;

            if (code == 1) { // only v[0] in
                kIn = 0;
                kOut1 = 1;
                kOut2 = 2;
            }
            else if (code == 2) { // only v[1] in
                kIn = 1;
                kOut1 = 2;
                kOut2 = 0;
            }
            else if (code == 4) { // only v[2] in
                kIn = 2;
                kOut1 = 0;
                kOut2 = 1;
            }
            else { // error
                return -1;
            }

            float a1 = -f[kIn].v.z / (f[kOut1].v.z - f[kIn].v.z);
            float a2 = -f[kIn].v.z / (f[kOut2].v.z - f[kIn].v.z);

            fClip[kIn].v.set(f[kIn].v);
            fClip[kOut1].v.set((1 - a1) * f[kIn].v.x + a1 * f[kOut1].v.x, (1 - a1) * f[kIn].v.y + a1 * f[kOut1].v.y, 0.0f, (1 - a1) * f[kIn].v.w + a1 * f[kOut1].v.w);
            fClip[kOut2].v.set((1 - a2) * f[kIn].v.x + a2 * f[kOut2].v.x, (1 - a2) * f[kIn].v.y + a2 * f[kOut2].v.y, 0.0f, (1 - a2) * f[kIn].v.w + a2 * f[kOut2].v.w);
            for (int ia = 0; ia < na; ia++) {
                fClip[kIn].attrs[ia] = f[kIn].attrs[ia];
                fClip[kOut1].attrs[ia] = (1 - a1) * f[kIn].attrs[ia] + a1 * f[kOut1].attrs[ia];
                fClip[kOut2].attrs[ia] = (1 - a2) * f[kIn].attrs[ia] + a2 * f[kOut2].attrs[ia];
            }

            fOut1[kIn].v.set(fClip[kIn].v);
            fOut1[kOut1].v.set(fClip[kOut1].v);
            fOut1[kOut2].v.set(fClip[kOut2].v);

            fOut1[kIn].setAttrs(na);
            fOut1[kOut1].setAttrs(na);
            fOut1[kOut2].setAttrs(na);

            for (int ia = 0; ia < na; ia++) {
                fOut1[kIn].attrs[ia] = fClip[kIn].attrs[ia];
                fOut1[kOut1].attrs[ia] = fClip[kOut1].attrs[ia];
                fOut1[kOut2].attrs[ia] = fClip[kOut2].attrs[ia];
            }

            return 1;
        }

        else if (code == 6 || code == 5 || code == 3) { // two in, one out
            int kOut, kIn1, kIn2;
            if (code == 6) { // only v[0] out
                kOut = 0;
                kIn1 = 1;
                kIn2 = 2;
            }
            else if (code == 5) { // only v[1] out
                kOut = 1;
                kIn1 = 2;
                kIn2 = 0;
            }
            else if (code == 3) { // only v[2] out
                kOut = 2;
                kIn1 = 0;
                kIn2 = 1;
            }
            else { // error
                return -1;
            }

            float a1 = -f[kOut].v.z / (f[kIn1].v.z - f[kOut].v.z);
            float a2 = -f[kOut].v.z / (f[kIn2].v.z - f[kOut].v.z);

            fClip[kOut].v.set((1 - a1) * f[kOut].v.x + a1 * f[kIn1].v.x, (1 - a1) * f[kOut].v.y + a1 * f[kIn1].v.y, 0.0f, (1 - a1) * f[kOut].v.w + a1 * f[kIn1].v.w);
            fClip[kIn1].v.set(f[kIn1].v);
            fClip[kIn2].v.set(f[kIn2].v);
            for (int ia = 0; ia < na; ia++) {
                fClip[kOut].attrs[ia] = (1 - a1) * f[kOut].attrs[ia] + a1 * f[kIn1].attrs[ia];
                fClip[kIn1].attrs[ia] = f[kIn1].attrs[ia];
                fClip[kIn2].attrs[ia] = f[kIn2].attrs[ia];
            }

            // Set up the first triangle
            fOut1[kIn1].v.set(fClip[kIn1].v);
            fOut1[kIn2].v.set(fClip[kIn2].v);
            fOut1[kOut].v.set(fClip[kOut].v);

            fOut1[kIn1].setAttrs(na);
            fOut1[kIn2].setAttrs(na);
            fOut1[kOut].setAttrs(na);

            for (int ia = 0; ia < na; ia++) {
                fOut1[kIn1].attrs[ia] = fClip[kIn1].attrs[ia];
                fOut1[kIn2].attrs[ia] = fClip[kIn2].attrs[ia];
                fOut1[kOut].attrs[ia] = fClip[kOut].attrs[ia];
            }

            fClip[kOut].v.set((1 - a1) * f[kOut].v.x + a1 * f[kIn1].v.x, (1 - a1) * f[kOut].v.y + a1 * f[kIn1].v.y, 0.0f, (1 - a1) * f[kOut].v.w + a1 * f[kIn1].v.w);
            fClip[kIn1].v.set(f[kIn2].v);
            fClip[kIn2].v.set((1 - a2) * f[kOut].v.x + a2 * f[kIn2].v.x, (1 - a2) * f[kOut].v.y + a2 * f[kIn2].v.y, 0.0f, (1 - a2) * f[kOut].v.w + a2 * f[kIn2].v.w);
            for (int ia = 0; ia < na; ia++) {
                fClip[kOut].attrs[ia] = (1 - a1) * f[kOut].attrs[ia] + a1 * f[kIn1].attrs[ia];
                fClip[kIn1].attrs[ia] = f[kIn2].attrs[ia];
                fClip[kIn2].attrs[ia] = (1 - a2) * f[kOut].attrs[ia] + a2 * f[kIn2].attrs[ia];
            }

            // Set up the other triangle
            fOut2[kIn1].v.set(fClip[kIn1].v);
            fOut2[kIn2].v.set(fClip[kIn2].v);
            fOut2[kOut].v.set(fClip[kOut].v);

            fOut2[kIn1].setAttrs(na);
            fOut2[kIn2].setAttrs(na);
            fOut2[kOut].setAttrs(na);

            for (int ia = 0; ia < na; ia++) {
                fOut2[kIn1].attrs[ia] = fClip[kIn1].attrs[ia];
                fOut2[kIn2].attrs[ia] = fClip[kIn2].attrs[ia];
                fOut2[kOut].attrs[ia] = fClip[kOut].attrs[ia];
            }

            return 2;
        }

        else { // code == 7 => all three in

            // Copy straight to output
            fOut1[0].v.set(f[0].v);
            fOut1[1].v.set(f[1].v);
            fOut1[2].v.set(f[2].v);

            fOut1[0].setAttrs(na);
            fOut1[1].setAttrs(na);
            fOut1[2].setAttrs(na);

            for (int ia = 0; ia < na; ia++) {
                fOut1[0].attrs[ia] = f[0].attrs[ia];
                fOut1[1].attrs[ia] = f[1].attrs[ia];
                fOut1[2].attrs[ia] = f[2].attrs[ia];
            }

            return 1;
        }
    }

}
