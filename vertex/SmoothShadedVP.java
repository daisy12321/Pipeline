package pipeline.vertex;

import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import pipeline.PointLight;
import pipeline.Pipeline;
import pipeline.math.Matrix4f;
import pipeline.misc.Vertex;

/**
 * This triangle processor smoothly interpolates the color across the face of
 * the triangle.
 * 
 * @author ags
 */
public class SmoothShadedVP extends VertexProcessor {
    protected Matrix4f modelView = new Matrix4f();
    protected Matrix4f composition = new Matrix4f();
    // The number of fragment attributes provided to the fragment processor
    public int nAttr() {
        return 3;   // surface color (r,g,b)
    }

    /**
     * @see VertexProcessor#updateTransforms(Pipeline)
     */
    public void updateTransforms(Pipeline pipe) {
        modelView.set(pipe.modelviewMatrix);
        
        composition.set(pipe.projectionMatrix);
        composition.leftCompose(pipe.viewportMatrix);
    }
   
    /**
     * @see VertexProcessor#vertex(Vector3f, Color3f, Vector3f, Vector2f, Vertex)
     */
    public void vertex(Vector3f v, Color3f c, Vector3f n, Vector2f t, Vertex output) {
        // multiply v by modelview matrix, this gives vertex pos in "eye space"
        output.v.set(v.x, v.y, v.z, 1);
    	modelView.rightMultiply(output.v);
    	
        // do the same with the normal
    	Vector4f nTmp = new Vector4f();
        nTmp.set(n.x, n.y, n.z, 0);
        modelView.rightMultiply(nTmp);
        Vector3f nModelView = new Vector3f(nTmp.x, nTmp.y, nTmp.z);
        
        // compute color at vertex:
        // start with c scaled by Pipeline.ambientIntensity
        Color3f colorTmp = new Color3f();
        colorTmp.set(c);
        colorTmp.scale(Pipeline.ambientIntensity);
        
        // for each light, add diffuse and specular term, using normal, light direction, and eye direction
        // note that light positions are given in eye space, and the camera is at the origin
		
		// diffuse term uses vertex color c and light color
        
        // specular term uses Pipeline.specularColor and assumes light color is full white
        // also uses Pipeline.specularExponent
        
        Vector3f eyeDir = new Vector3f();
        Vector3f vTmp = new Vector3f(output.v.x, output.v.y, output.v.z);
        eyeDir.set(vTmp);
        eyeDir.scale(-1);
        eyeDir.normalize();
        
        Vector3f lightDir = new Vector3f();
		Vector3f h = new Vector3f();
        for (PointLight light : Pipeline.lights) {
        	lightDir.set(light.getPosition().x, light.getPosition().y, light.getPosition().z);
        	lightDir.sub(vTmp);
        	lightDir.normalize();
        	
			h.add(lightDir, eyeDir);
			h.normalize();
			
			double x = Math.max(0, nModelView.dot(lightDir));
        	double y = Math.pow(Math.max(0, nModelView.dot(h)), Pipeline.specularExponent);
			colorTmp.x += light.getIntensity().x * c.x * x + Pipeline.specularColor.x * y;
			colorTmp.y += light.getIntensity().y * c.y * x + Pipeline.specularColor.y * y;
			colorTmp.z += light.getIntensity().z * c.z * x + Pipeline.specularColor.z * y;
		}

        
        // in the end, clamp the resulting color to [0, 1] and store in attributes
        colorTmp.clamp(0, 1);
        output.setAttrs(nAttr());
        output.attrs[0] = colorTmp.x;
        output.attrs[1] = colorTmp.y;
        output.attrs[2] = colorTmp.z;
        
        // also project vertex position to screen space
        composition.rightMultiply(output.v);
    }

}