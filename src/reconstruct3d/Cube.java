/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reconstruct3d;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mai
 */
public class Cube {
    float cube_size = 1.0f; //render size
    int cubeSize = 0;       //integer size
    int tex_index = cubeSize-2;
    int tex_start_index;
    float zAxisD = 0;
    Texture []mid_texture;
    float render_depth = 1;
    float start_depth;
    float structDepth = 0.0f;
    
    float scale_x = 0;
    float scale_y = 0;
    float scale_z = 0;
    
    Cube(int cubeSize, float scaledx, float scaledy, float scaledz){
        this.cubeSize = cubeSize;
        this.scale_x = scaledx;
        this.scale_y = scaledy;
        this.scale_z = scaledz;
    }
    
    void setIndex(int index){
        this.tex_index = index;
        this.tex_start_index = tex_index;
    }
    
    void setRenderDepth(float sizeZ){
        render_depth = sizeZ;
        this.start_depth = render_depth;
    }
    
    void setStructDepth(float structDepth){
        this.structDepth = structDepth;
    }
    
    void loadMidTextures(){
        File tmp;
        mid_texture = new Texture[cubeSize];
        for(int i = 0; i < cubeSize; i++){  //tutaj
            //System.out.println(i);
            if(new File("Regen/VolumeSplices/"+""+i+".png").exists()){
                tmp = new File("Regen/VolumeSplices/"+""+i+".png");
                try {
                    mid_texture[i] = TextureIO.newTexture(tmp, false);
                } catch (IOException ex) {
                    Logger.getLogger(Cube.class.getName()).log(Level.SEVERE, null, ex);
                } catch (GLException ex) {
                    Logger.getLogger(Cube.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    public void renderAll(GL2 gl, float displaySizeZ){
        
        System.out.println(scale_x);
        System.out.println(scale_y);
        System.out.println(scale_z);
        gl.glScalef(scale_x, scale_y, scale_z); //tutaj skalowanie
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        //gl.glBlendFunc(GL.GL_AND_REVERSE, GL.GL_ONE_MINUS_SRC_ALPHA);
        
        for(int i = 0; i < cubeSize; i++){
            gl.glEnable(GL2.GL_TEXTURE_2D);
            if(mid_texture[tex_index] != null){
                    mid_texture[tex_index].setTexParameteri(gl, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
                    mid_texture[tex_index].setTexParameteri(gl, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
                    mid_texture[tex_index].setTexParameteri(gl, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
                    mid_texture[tex_index].setTexParameteri(gl, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
                    mid_texture[tex_index].bind(gl);
            }
            gl.glBegin(gl.GL_QUADS);
            // Front Face
            gl.glTexCoord2f(0.0f, 0.0f);
            gl.glVertex3f(-cube_size, -cube_size, render_depth);
            gl.glTexCoord2f(cube_size, 0.0f);
            gl.glVertex3f(cube_size, -cube_size, render_depth);
            gl.glTexCoord2f(cube_size, cube_size);
            gl.glVertex3f(cube_size, cube_size, render_depth);
            gl.glTexCoord2f(0.0f, cube_size);
            gl.glVertex3f(-cube_size, cube_size, render_depth);
            gl.glEnd();
            gl.glDisable(GL2.GL_TEXTURE_2D);
            
            tex_index--;
            render_depth -= displaySizeZ;
            
        }
        //gl.glScalef(0.5f, 0.5f, 0.5f);
        //System.out.println(tex_index);
        //render_depth = start_depth;
        //tex_index = tex_start_index;
        
        //System.out.println(tex_index);
        
        GLUT glut = new GLUT();
        glut.glutWireCube((float) 2.0);
        
        gl.glFlush();
        
    }
    
}
