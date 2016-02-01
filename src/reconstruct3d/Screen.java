/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reconstruct3d;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFrame;

/**
 *
 * @author Mai
 */
public class Screen implements GLEventListener{

    public static DisplayMode dm, dm_old;
    private GLU glu = new GLU();
    private float rquad=0.0f;
    float zoomFactor = 0.1f;
    float x_pitch = 0f;
    float y_pitch = 0f;
    float rotating_x = 0;
    float rotating_y = 0;
    float rotation = 0;
    int CubeSize = 0;
    float displaySize_z = 0;    //single iteration of depth
    float displayStructSize = 0;    //side textures scaling
    Cube cub;
    
    float x_scale = 0;
    float y_scale = 0;
    float z_scale = 0;
    
    float scaleFactor = 0.01f;
    
    int tex_index = 0;
    float render_depth = 1.0f;  //rendering depth of a cube
    float struct_depth = 0.0f;
    
    Screen(int CubeSize, int x_size, int y_size, float z_size){
        this.CubeSize = CubeSize;
        this.x_scale = (x_size)*scaleFactor;
        this.y_scale = (y_size)*scaleFactor;
        this.z_scale = (z_size)*scaleFactor;
    }
    
    JFrame window;
    //int bigCubeSize = 100;
    
    GLProfile profile;
    GLCapabilities capabilities;
    // The canvas 
    GLCanvas glcanvas;
   
    
    @Override
    public void init(GLAutoDrawable drawable) {
        final GL2 gl = drawable.getGL().getGL2();
        
        gl.glShadeModel( GL2.GL_SMOOTH );
        gl.glClearColor( 0f, 0f, 0f, 0f );
        gl.glClearDepth( 4.0f );
        //gl.glEnable( GL2.GL_DEPTH_TEST );
        gl.glDepthFunc( GL2.GL_LEQUAL );
        gl.glHint( GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST );
        //gl.glOrtho(-400.0, 400.0, -300.0, 300.0, -400.0, 400.0);
        tex_index = CubeSize-1;
        displaySize_z = 2.0f/CubeSize;  //spacing zAxis
        displayStructSize = 1.0f/CubeSize-1;  //spacing sides
        cub = new Cube(CubeSize, x_scale, y_scale, z_scale);
        
        //cub.loadTextures(); //load side textures
        cub.loadMidTextures();  //load volume textures
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        //nothing
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        final GL2 gl = drawable.getGL().getGL2();
//        float width = window.getWidth();
//        float height = window.getHeight();
//        final float h = ( float ) width / ( float ) height;
        
        gl.glClear( GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT );     
        gl.glLoadIdentity();
        gl.glTranslatef( 0f, 0f, -5.0f ); //8
        gl.glRotatef( rquad+x_pitch, 0, 1, 0.0f ); // Rotate The Cube On X, Y & Z
        gl.glRotatef( rquad+y_pitch, 1, 0, 0.0f );
        
        cub.setIndex(tex_index);
        cub.setRenderDepth(render_depth);
        cub.setStructDepth(struct_depth);
        //cub.render(gl);
        cub.renderAll(gl, displaySize_z);
        
        //rquad -=0.15f;
        gl.glTranslatef(0, 0, 0);
        
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        final GL2 gl = drawable.getGL().getGL2();
      if( height <=0 )
         height =1;
        final float h = ( float ) width / ( float ) height;
        gl.glViewport( 0, 0, width, height );
        gl.glMatrixMode( GL2.GL_PROJECTION );
        gl.glLoadIdentity();
        glu.gluPerspective( 45.0f, h, 0.1, 100.0 );
        gl.glMatrixMode( GL2.GL_MODELVIEW );
        gl.glLoadIdentity();
    }
    
    void enableZoom(){
        window.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent ke) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void keyPressed(KeyEvent ke) {
                if(ke.getKeyCode() == KeyEvent.VK_LEFT){
                    rotating_y = 0;
                    rotating_x = 1;
                    x_pitch += 2.5f;
                    rotation = x_pitch;
                }
                
                if(ke.getKeyCode() == KeyEvent.VK_RIGHT){
                    rotating_y = 0;
                    rotating_x = 1;
                    x_pitch -= 2.5f;
                    rotation = x_pitch;
                }
                
                if(ke.getKeyCode() == KeyEvent.VK_UP){
                    rotating_y = 1;
                    rotating_x = 0;
                    y_pitch += 2.5f;
                    rotation = y_pitch;
                }
                
                if(ke.getKeyCode() == KeyEvent.VK_DOWN){
                    rotating_y = 1;
                    rotating_x = 0;
                    y_pitch -= 2.5f;
                    rotation = y_pitch;
                }
                
                if(ke.getKeyCode() == KeyEvent.VK_D){
                    if(tex_index > 0){
                        tex_index -= 1;
                        render_depth -= displaySize_z;
                        struct_depth -= displayStructSize;
                        //System.out.println(displaySize_z);
                    }
                }
                
                if(ke.getKeyCode() == KeyEvent.VK_A){
                    if(tex_index < CubeSize-1){
                        tex_index += 1;
                        render_depth += displaySize_z;
                        struct_depth += displayStructSize;
                        //System.out.println(displaySize_z);
                        System.out.println(CubeSize);
                    }
                }
                
            }

            @Override
            public void keyReleased(KeyEvent ke) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
    }
    
    public void setUp(){
        window = new JFrame("Widok 3D");
        window.setVisible(true);
        window.setSize(new Dimension(600, 600));
        
        profile = GLProfile.get(GLProfile.GL2);
        capabilities = new GLCapabilities(profile);
        glcanvas = new GLCanvas(capabilities);
        glcanvas.setSize( 400, 400 );
        
        glcanvas.addGLEventListener(this);
        
        window.getContentPane().add(glcanvas);
        
        enableZoom();
        //window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //window.setVisible(true);
        //window.setLocationRelativeTo(null);
        final FPSAnimator animator = new FPSAnimator(glcanvas, 300, true);
        animator.setFPS(30);
        animator.start();
    }
    
}