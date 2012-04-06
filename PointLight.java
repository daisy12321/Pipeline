package pipeline;

import javax.vecmath.Color3f;
import javax.vecmath.Point3f;

public class PointLight {
    Point3f position;
    Color3f intensity;

    public PointLight(Point3f position, Color3f intensity) {
        super();
        this.position = position;
        this.intensity = intensity;
    }

    public Point3f getPosition() {
        return position;
    }

    public Color3f getIntensity() {
        return intensity;
    }

    public void setIntensity(Color3f intensity) {
        this.intensity = intensity;
    }
}
