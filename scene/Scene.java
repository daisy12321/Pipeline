package pipeline.scene;

import javax.media.opengl.GLAutoDrawable;

import java.io.File;

import pipeline.Pipeline;
import pipeline.misc.Texture;

/**
 * A Scene object represents a collection of geometry and texture data. A Scene
 * object should know how to render itself into both a GL context, as well as
 * the custom built context of this project framework.
 */
public abstract class Scene {

    /** The texture data for a scene. Might be null. */
    Texture texture;

    /**
     * Sets the texture for this scene. The file passed in must be stored in a
     * format readable by javax.imageio.ImageIO.
     * 
     * @param texFile The file to read the image from.
     */
    public void setTexture(File texFile) {

        if (texFile != null) {
            setTexture(new Texture(texFile));
        }
    }

    /**
     * Sets the texture for this scene.
     * 
     * @param newTexture The texture object to use.
     */
    public void setTexture(Texture newTexture) {

        texture = newTexture;
    }

    /**
     * This method renders the scene onto a GL drawing region.
     * 
     * @param d The reference to the GL drawing area.
     */
    public abstract void render(GLAutoDrawable d);

    /**
     * This method renders the scene onto our custom built pipeline.
     * 
     * @param pipeline The custom pipeline.
     */
    public abstract void render(Pipeline pipeline);

}
