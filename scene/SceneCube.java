package pipeline.scene;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLDrawable;

import pipeline.Pipeline;
import pipeline.misc.Geometry;

/**
 * This subclass of scene renders a simple cube on screen.
 * 
 * @author ags
 */
public class SceneCube extends Scene {

    /**
     * @see Scene#render(GLDrawable)
     */
    public void render(GLAutoDrawable d) {

        d.getGL().glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGB, texture.nx, texture.ny, 0, GL.GL_RGB, GL.GL_UNSIGNED_BYTE, texture.cBuf);
        texture.cBuf.rewind();
        Geometry.cube(d);
    }

    /**
     * @see Scene#render(Pipeline)
     */
    public void render(Pipeline pipe) {

        pipe.setTexture(texture);
        Geometry.cube();
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {

        return "Cube";
    }

}
