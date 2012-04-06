package pipeline.misc;

import java.io.FileOutputStream;

/**
 * This class holds all of the data for a frame. A frame is usually constructed
 * or rendered off screen, then transferred onto the screen. This transfer
 * process is called blitting. The z buffer is also held in this frame buffer
 * class.
 * 
 * @author ags
 */
public class FrameBuffer {

    /** The width of the image in the frame buffer. */
    protected int nx;

    /** The height of the image in the frame buffer. */
    protected int ny;

    /** The rgb data that forms the image. */
    protected byte[] cData;

    /** The z buffer - holds the z value of the current fragment. */
    protected float[] zData;

    /**
     * Constructs a new frame buffer with the given dimensions.
     * 
     * @param newNx The width of the new frame buffer.
     * @param newNy The height of the new frame buffer.
     */
    public FrameBuffer(int newNx, int newNy) {

        nx = newNx;
        ny = newNy;
        cData = new byte[nx * ny * 3];
        zData = new float[nx * ny];
    }

    /**
     * Returns the z value of the currently stored fragment for the given (x, y)
     * coordinate.
     * 
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @return The z value of the fragment stored at that point.
     */
    public float getZ(int x, int y) {

        return zData[x + nx * y];
    }

    /**
     * @return Returns the frame buffere data array
     */
    public byte[] getData() {

        return this.cData;
    }

    /**
     * @return Returns the width of the buffer
     */
    public int getWidth() {

        return this.nx;
    }

    /**
     * @return Returns the height of the buffer
     */
    public int getHeight() {

        return this.ny;
    }

    /**
     * Sets the color (r, g, b) and z value for a given (x, y) location.
     * 
     * @param ix The x coordinate.
     * @param iy The y coordinate.
     * @param r The red color channel (in the range [0, 1].
     * @param g The green color channel (in the range [0, 1].
     * @param b The blue color channel (in the range [0, 1].
     * @param z The z value of the new fragment.
     */
    public void set(int ix, int iy, float r, float g, float b, float z) {

        int offset = 3 * (ix + nx * iy);

        cData[offset + 0] = (byte) ((int) (255 * r) & 0xff);
        cData[offset + 1] = (byte) ((int) (255 * g) & 0xff);
        cData[offset + 2] = (byte) ((int) (255 * b) & 0xff);

        zData[ix + nx * iy] = z;
    }

    /**
     * Sets all data in the frame buffer to be the same color triple and depth
     * value.
     * 
     * @param r The red color channel (in the range [0, 1].
     * @param g The green color channel (in the range [0, 1].
     * @param b The blue color channel (in the range [0, 1].
     * @param z The new z value.
     */
    public void clear(float r, float g, float b, float z) {

        byte ir = (byte) ((int) (255 * r) & 0xff);
        byte ig = (byte) ((int) (255 * g) & 0xff);
        byte ib = (byte) ((int) (255 * b) & 0xff);

        for (int k = 0; k < nx * ny; k++) {
            cData[3 * k + 0] = ir;
            cData[3 * k + 1] = ig;
            cData[3 * k + 2] = ib;
            zData[k] = z;
        }
    }

    /**
     * Attempts to write the current framebuffer out to a PPM file. In case of an
     * error, this method will throw a new RuntimeException.
     * 
     * @param fname The name of the output file.
     */
    public void write(String fname) {

        try {
            FileOutputStream stream = new FileOutputStream(fname);
            String hdr = new String("P6 " + nx + " " + ny + " 255\n");
            stream.write(hdr.getBytes());
            for (int iy = ny - 1; iy >= 0; iy--)
                stream.write(cData, 3 * nx * iy, 3 * nx);
        }
        catch (java.io.FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
    }

}
