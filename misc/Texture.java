package pipeline.misc;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.*;

import javax.imageio.ImageIO;
import javax.vecmath.Color3f;
import javax.vecmath.Vector2f;

/**
 * This class holds all of the information necessary to describe texture data.
 * 
 * @author ags
 */
public class Texture {

    /** The name of the file from where the data was loaded */
    protected String filename;

    /** The width of the texture. */
    public int nx;

    /** The height of the texture. */
    public int ny;

    /** The RGB data for each pixel. */
    private byte[] cData;

    public Buffer cBuf;

    /**
     * Reads in a texture from the given file and stores the data. The image file
     * given must be stored in a format recognizable by javax.imageio.ImageIO.
     * 
     * @param imageFile The file where the texture is stored.
     */
    public Texture(File imageFile) {

        try {
            BufferedImage loadedImage = ImageIO.read(imageFile);

            nx = loadedImage.getWidth();
            ny = loadedImage.getHeight();
            cData = new byte[nx * ny * 3];

            System.out.print("loading " + nx + " x " + ny + " texture...");
            filename = imageFile.getName();

            int offset = 0;
            for (int iy = 0; iy < ny; iy++) {
                for (int ix = 0; ix < nx; ix++) {
                    int pixelValue = loadedImage.getRGB(ix, ny - 1 - iy);

                    cData[offset + 0] = (byte) (0xff & (pixelValue >> 16));
                    cData[offset + 1] = (byte) (0xff & (pixelValue >> 8));
                    cData[offset + 2] = (byte) (0xff & (pixelValue >> 0));

                    offset += 3;
                }
            }
            cBuf = ByteBuffer.wrap(cData);

            System.out.println("done.");
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Samples this texture for a given 2D location. Places the result in the
     * color parameter.
     * 
     * @param p The 2D texture coordinate.
     * @param cOut The result of sampling the texture.
     */
    public void sample(Vector2f p, Color3f cOut) {

        int ix = (int) (p.x * nx + 0.5);
        int iy = (int) (p.y * ny + 0.5);

        ix = Math.min(Math.max(ix, 0), nx - 1);
        iy = Math.min(Math.max(iy, 0), ny - 1);

        int offset = 3 * (nx * iy + ix);
        cOut.set((cData[offset + 0] & 0xff) / 255.0f, (cData[offset + 1] & 0xff) / 255.0f, (cData[offset + 2] & 0xff) / 255.0f);
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {

        return filename;
    }

}
