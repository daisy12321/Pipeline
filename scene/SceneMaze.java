package pipeline.scene;

import java.util.Random;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLDrawable;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

import pipeline.Pipeline;
import pipeline.misc.Geometry;

/**
 * This subclass of Scene renders a maze on screen. It has textured walls, which
 * may use different textures.
 * 
 * @author ags
 */
public class SceneMaze extends Scene {

    static final int MAZE_SIZE = 6;

    static final int ENTER = 2;

    static final int EXIT = 4;

    Color3f cpx = new Color3f(1.0f, 1.0f, 1.0f);

    Color3f cpz = new Color3f(0.6f, 0.6f, 0.6f);

    Color3f cmz = new Color3f(0.5f, 0.5f, 0.5f);

    Color3f cmx = new Color3f(0.3f, 0.3f, 0.3f);

    boolean[][] xConn = new boolean[MAZE_SIZE + 1][MAZE_SIZE + 1];

    boolean[][] zConn = new boolean[MAZE_SIZE + 1][MAZE_SIZE + 1];

    /**
     * The default for this scene constructs the standard maze.
     */
    public SceneMaze() {

        boolean[][] visited = new boolean[MAZE_SIZE + 2][MAZE_SIZE + 2];
        for (int i = 0; i < MAZE_SIZE + 2; i++) {
            visited[0][i] = true;
            visited[MAZE_SIZE + 1][i] = true;
            visited[i][0] = true;
            visited[i][MAZE_SIZE + 1] = true;
        }
        buildMaze(visited, 4, 1, new Random());
        zConn[ENTER][0] = true;
        zConn[EXIT][MAZE_SIZE] = true;
    }

    static final int offx[] = { 1, 0, -1, 0 };

    static final int offz[] = { 0, 1, 0, -1 };

    void buildMaze(boolean[][] visited, int ix, int iz, Random r) {

        visited[ix][iz] = true;
        int dir = r.nextInt(4);
        for (int i = 0; i < 4; i++) {
            int ox = offx[(dir + i) % 4], oz = offz[(dir + i) % 4];
            if (!visited[ix + ox][iz + oz]) {
                if (ox > 0)
                    xConn[ix][iz] = true;
                if (ox < 0)
                    xConn[ix - 1][iz] = true;
                if (oz > 0)
                    zConn[ix][iz] = true;
                if (oz < 0)
                    zConn[ix][iz - 1] = true;
                buildMaze(visited, ix + ox, iz + oz, r);
            }
        }

    }

    private static final Vector3f v0 = new Vector3f();

    private static final Vector3f v1 = new Vector3f();

    private static final Vector3f v2 = new Vector3f();

    private static final Vector3f v3 = new Vector3f();

    private static final Vector3f v_n00 = new Vector3f(-1, 0, 0);

    private static final Vector3f v_00n = new Vector3f(0, 0, -1);

    /**
     * @see Scene#render(GLDrawable)
     */
    public void render(GLAutoDrawable d) {

        GL2 gl = d.getGL().getGL2();


        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGB, texture.nx, texture.ny, 0, GL.GL_RGB, GL.GL_UNSIGNED_BYTE, texture.cBuf);
        texture.cBuf.rewind();
        gl.glTranslatef(-(MAZE_SIZE + 2) / 2.0f, -0.5f, -(MAZE_SIZE + 2) / 2.0f);

        for (int ix = 1; ix <= MAZE_SIZE; ix++) {
            for (int iz = 1; iz <= MAZE_SIZE; iz++) {
                if (!xConn[ix][iz]) {
                    v0.set(ix + 1, 0, iz);
                    v1.set(ix + 1, 0, iz + 1);
                    v3.set(ix + 1, 1, iz);
                    v2.set(ix + 1, 1, iz + 1);
                    Geometry.quadPair(v0, v1, v2, v3, v_n00, cmx, cpx, d);
                }
                if (!zConn[ix][iz]) {
                    v0.set(ix, 0, iz + 1);
                    v1.set(ix + 1, 0, iz + 1);
                    v3.set(ix, 1, iz + 1);
                    v2.set(ix + 1, 1, iz + 1);
                    Geometry.quadPair(v0, v1, v2, v3, v_00n, cmz, cpz, d);
                }
                if (ix == 1 && !xConn[0][iz]) {
                    v0.set(ix, 0, iz);
                    v1.set(ix, 0, iz + 1);
                    v3.set(ix, 1, iz);
                    v2.set(ix, 1, iz + 1);
                    Geometry.quadPair(v0, v1, v2, v3, v_n00, cmx, cpx, d);
                }
                if (iz == 1 && !zConn[ix][0]) {
                    v0.set(ix, 0, iz);
                    v1.set(ix + 1, 0, iz);
                    v3.set(ix, 1, iz);
                    v2.set(ix + 1, 1, iz);
                    Geometry.quadPair(v0, v1, v2, v3, v_00n, cmz, cpz, d);
                }
            }
        }
    }

    /**
     * @see Scene#render(Pipeline)
     */
    public void render(Pipeline pipe) {

        pipe.setTexture(texture);

        pipe.translate(new Vector3f(-(MAZE_SIZE + 2) / 2.0f, -0.5f, -(MAZE_SIZE + 2) / 2.0f));

        for (int ix = 1; ix <= MAZE_SIZE; ix++) {
            for (int iz = 1; iz <= MAZE_SIZE; iz++) {
                if (!xConn[ix][iz]) {
                    v0.set(ix + 1, 0, iz);
                    v1.set(ix + 1, 0, iz + 1);
                    v3.set(ix + 1, 1, iz);
                    v2.set(ix + 1, 1, iz + 1);
                    Geometry.quadPair(v0, v1, v2, v3, v_n00, cmx, cpx);
                }
                if (!zConn[ix][iz]) {
                    v0.set(ix, 0, iz + 1);
                    v1.set(ix + 1, 0, iz + 1);
                    v3.set(ix, 1, iz + 1);
                    v2.set(ix + 1, 1, iz + 1);
                    Geometry.quadPair(v0, v1, v2, v3, v_00n, cmz, cpz);
                }
                if (ix == 1 && !xConn[0][iz]) {
                    v0.set(ix, 0, iz);
                    v1.set(ix, 0, iz + 1);
                    v3.set(ix, 1, iz);
                    v2.set(ix, 1, iz + 1);
                    Geometry.quadPair(v0, v1, v2, v3, v_n00, cmx, cpx);
                }
                if (iz == 1 && !zConn[ix][0]) {
                    v0.set(ix, 0, iz);
                    v1.set(ix + 1, 0, iz);
                    v3.set(ix, 1, iz);
                    v2.set(ix + 1, 1, iz);
                    Geometry.quadPair(v0, v1, v2, v3, v_00n, cmz, cpz);
                }
            }
        }
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {

        return "Maze";
    }

}
