package mygame;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.TextureKey;
import com.jme3.audio.AudioData.DataType;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.BulletAppState;
import static com.jme3.bullet.PhysicsSpace.getPhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.joints.HingeJoint;
import com.jme3.collision.CollisionResults;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.control.CameraControl;
import static com.jme3.scene.plugins.fbx.mesh.FbxLayerElement.Type.Texture;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.scene.shape.Sphere.TextureMode;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import java.util.Random;

/**
 * This is a shooting fish game
 * Student number: 8759122
 * @author Fanyu Ran 
 */
public class Main extends SimpleApplication 
                   implements AnimEventListener, PhysicsCollisionListener {

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }
     private Node basejointObject;
    private AudioNode audio_bgm, audio_click, audio_blub, audio_ball, audio_shoot;
    String name_NodeB,collisionObject;
    int collision_counter = 0;
    int counter;
    int soundFlag = 0; //1 for playing
    Node blub;
    CameraNode camNode;
    private HingeJoint joint,joint2,joint3;
    Geometry tray1_geo;
    Geometry Rtray1_geo,Ltray1_geo;
    private boolean left = false, right = false, forward = false, backward = false, up = false, down = false;
    
    private AnimChannel channel_swim, channel_idle, channel_turn, channel_turnFins;
    private AnimControl control;
    int swimFlag = 0;
    boolean swimS = false;
    int idleFlag = 0;
    int turnFlag = 0; //1 for stop
    int controlObject = 0; //0 is for blub, 1 is for ball
    String [] ballNum = new String [200];
    RigidBodyControl [] Ballphy = new RigidBodyControl [200];
    int Ballphy_Index = 0;
    int ball_Index =0;
    String ballNam, Num,  blubName;
    
    GhostControl ghost;
    
    Geometry target = new Geometry();
    /** Prepare the Physics Application State (jBullet) */
    private BulletAppState bulletAppState;
    
    /** Prepare Materials */
    Material floor_mat, floor2_mat, floor3_mat, t_mat;
    Material ball_mat;
    Material tray_mat;
    
    /** Prepare geometries and physical nodes. */
    private RigidBodyControl    floor1_phy,floor2_phy,floor3_phy,floor4_phy,floor5_phy, floor6_phy ;
    private static final Box    floor1,floor2,floor3,floor4,floor5, floor6;
    private RigidBodyControl    ball_phy, blub_phy, Joint0_phy;
    private static final Sphere sphere;
    private RigidBodyControl    tray1_phy, trayright_phy, trayleft_phy, trayback_phy,trayfront_phy;
    private static final Box    tray1, tray2, tray3;
    private RigidBodyControl    Rtray1_phy, Rtrayright_phy, Rtrayleft_phy, Rtrayback_phy,Rtrayfront_phy;
    private RigidBodyControl    Ltray1_phy, Ltrayright_phy, Ltrayleft_phy, Ltrayback_phy,Ltrayfront_phy;
    
    static {
    /** Initialize the box geometry */
    floor1 = new Box(5f,0.01f, 5f);
    floor1.scaleTextureCoordinates(new Vector2f(1, 1));
    floor2 = new Box(5f,0.01f, 6f);
    floor2.scaleTextureCoordinates(new Vector2f(3, 3));
    floor3 = new Box(5f,0.01f, 6f);
    floor3.scaleTextureCoordinates(new Vector2f(3, 3));
    floor4 = new Box(6f,0.01f, 5f);
    floor4.scaleTextureCoordinates(new Vector2f(2, 2));
    floor5 = new Box(5f,0.01f, 5f);
    floor5.scaleTextureCoordinates(new Vector2f(1, 1));
    floor6 = new Box(6f,0.01f, 5f);
    
    /** Initialize the ball geometry */
    sphere = new Sphere(32, 32, 0.4f, true, false);
    sphere.setTextureMode(TextureMode.Projected);
    
    /** Initialize the tray geometry */
    tray1 = new Box(1f,0.1f, 2f); 
    tray2 = new Box(1f,1f, 0.1f);
    tray3 = new Box(0.1f,1f, 2f);
  }
    
    @Override
    public void simpleInitApp() {
        
        /** Configure cam to look at scene */
        
        flyCam.setEnabled(false);
        camNode = new CameraNode("Camera Node", cam);
        camNode.setControlDir(CameraControl.ControlDirection.SpatialToCamera);
        rootNode.attachChild(camNode);
        camNode.setLocalTranslation(new Vector3f(18f, 5.5f, 0f));
        camNode.lookAt(new Vector3f(0f, 5.5f, 0f), Vector3f.UNIT_Y);
        
        /** Set up Physics Game */
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        bulletAppState.getPhysicsSpace().addCollisionListener(this);
        
//        // Creat a blub joint
//        basejointObject = new Node();
//        Geometry Joint0 = new Geometry("Joint", new Sphere(6, 12, 0.3f));
//        Joint0.setLocalTranslation(1.0f, 5.0f, 2.0f);
//        Material matJoint = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
//        matJoint.setColor("Color", new ColorRGBA( 1.7f, 0.7f, 0.7f, 0.5f)); // silver'ish
//        //matJoint.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
//        Joint0.setMaterial(matJoint);
//        
//        
////        Joint0_phy = new RigidBodyControl(1.0f);
//        //Joint0_phy.setGravity(new Vector3f(0f,0f,0f));
////        Joint0.addControl(Joint0_phy);
////        bulletAppState.getPhysicsSpace().add(Joint0_phy);
//        basejointObject.attachChild(Joint0);
//        
//        rootNode.attachChild(basejointObject);
        
        
        
     
        blub = (Node) assetManager.loadModel("Models/Blub_a5/blub3_a5.j3o");
        rootNode.attachChild(blub);      
        blub.setLocalTranslation(0f, 5f, 2f);
        blub.rotate(0.0f, FastMath.DEG_TO_RAD*90.0f, 0.0f);
        blub.scale(0.8f,0.8f,0.8f);                       
        blub_phy = new RigidBodyControl(3.0f);
        //blub_phy.setAngularVelocity(new Vector3f(0f,0f,1.0f));
        blub.setShadowMode(ShadowMode.CastAndReceive);
        
        blub.addControl(blub_phy);
        bulletAppState.getPhysicsSpace().add(blub_phy);
        blub_phy.setGravity(new Vector3f(0f,-0.08f,0f));
        
        blubName = blub.getName();
        System.out.println(blubName);
        target.setName("blub_quadrangulated1");
        
        
             
//        ghost = new GhostControl(new BoxCollisionShape(new Vector3f(1,1,1)));  // a box-shaped ghost
//        Node node = new Node("a ghost-controlled thing");
//         node.addControl(ghost);     
//         node.attachChild(blub);
//        rootNode.attachChild(node);
//        getPhysicsSpace().add(ghost);
        
        
        /** Add animations */
        control = blub.getChild("blub_quadrangulated").getControl(AnimControl.class);
        control.addListener(this);
        
        channel_swim = control.createChannel();
        channel_swim.addFromRootBone("Bone.001");
        channel_swim.setAnim("swim_stop");
        
        channel_idle = control.createChannel();   
        channel_idle.addFromRootBone("Bone.016");
        channel_idle.addFromRootBone("Bone.019");
        channel_idle.addFromRootBone("Bone.014");
        channel_idle.setAnim("stationary_stop");
        
        channel_turn = control.createChannel();
        
        
        
        initKeys();
        initMaterials(); 
        initFloor();
        initTray();
        setupJoint();
        initAudio();
        

            
        //add light source and shadows
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(0.0f, -3.0f, -3.50f));
        rootNode.addLight(sun);
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(1.0f));
        rootNode.addLight(al);
        
        final int SHADOWMAP_SIZE=1024;
        DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(assetManager, SHADOWMAP_SIZE, 3);
        dlsr.setLight(sun);
        viewPort.addProcessor(dlsr);
        
        
        inputManager.setCursorVisible(true);
    }
    
    private void initKeys() {  
        inputManager.addMapping("shoot", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(actionListener, "shoot");
        
        inputManager.addMapping("Forward",  new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping("Left",   new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping("Right",  new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping("Backward", new KeyTrigger(KeyInput.KEY_DOWN));
        inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_PGUP));
        inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_PGDN));

        inputManager.addListener(actionListener, "Left");
        inputManager.addListener(actionListener, "Right");
        inputManager.addListener(actionListener, "Forward");
        inputManager.addListener(actionListener, "Backward");
        inputManager.addListener(actionListener, "Up");
        inputManager.addListener(actionListener, "Down");
        
        inputManager.addMapping("swimming", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addListener(actionListener, "swimming");

        inputManager.addMapping("idle", new KeyTrigger(KeyInput.KEY_I));
        inputManager.addListener(actionListener, "idle");

        inputManager.addMapping("turn", new KeyTrigger(KeyInput.KEY_T));
        inputManager.addListener(actionListener, "turn");
        
        inputManager.addMapping("pick target", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(actionListener, "pick target");
        }
    
    private ActionListener actionListener = new ActionListener() {
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("shoot") && !keyPressed) {
                //makeCannonBall();              
                Ballphy [Ballphy_Index] = makeCannonBall();
                //ball_Index = Ballphy_Index;
                System.out.println(Ballphy_Index);
                System.out.println(ball_Index);
                Ballphy_Index ++;
                ball_Index ++;
                audio_shoot.setVolume(2);
                audio_shoot.play();
            }
            
            if (name.equals("Left")) {
              if (keyPressed) { left = true; } else { left = false; }
            } else if (name.equals("Right")) {
              if (keyPressed) { right = true; } else { right = false; }
            } else if (name.equals("Forward")) {
              if (keyPressed) { forward = true; } else { forward = false; }
            } else if (name.equals("Backward")) {
              if (keyPressed) { backward = true; } else { backward = false; }
            } else if (name.equals("Up")) {
              if (keyPressed) { up = true; } else { up = false; }
            } else if (name.equals("Down")) {
              if (keyPressed) { down = true; } else { down = false; }
            }
            
            if (name.equals("swimming") && keyPressed) {
            if(channel_swim.getAnimationName().equals("ArmatureAction")){
                channel_swim.setAnim("swim_stop");
                swimS = false;
                channel_swim.setLoopMode(LoopMode.Loop);}

            else if(channel_swim.getAnimationName().equals("swim_stop")) {
                channel_swim.setAnim("ArmatureAction");
                swimS = true;
                channel_swim.setLoopMode(LoopMode.Loop);} 
            } 
            
            if (name.equals("idle") && keyPressed) {
            if(channel_idle.getAnimationName().equals("stationary_stop")){
                  channel_idle.setAnim("stationaryIdle");
                  channel_idle.setLoopMode(LoopMode.Loop);}

            else if(channel_idle.getAnimationName().equals("stationaryIdle")) {
              channel_idle.setAnim("stationary_stop");
              channel_idle.setLoopMode(LoopMode.Loop);}
            } 
 
            if (name.equals("turn") && keyPressed) { 
                
              //channel_turn.setAnim("turn");
              channel_turn.setAnim("turn_O");
              channel_turn.setLoopMode(LoopMode.DontLoop);
//            //Joint0_phy.setAngularVelocity(new Vector3f(0f,0f,1f));
//            //Joint0_phy.applyTorque(new Vector3f(0f,0f,1f));
//             Joint0_phy.applyForce(new Vector3f(0f, 0f, 5f),new Vector3f(0f, 0f, 0f));
             System.out.println("turn !");
            }
            
        if (name.equals("pick target")&& keyPressed) {
         audio_click.playInstance(); 
        // Reset results list.
        CollisionResults results = new CollisionResults();
        // Convert screen click to 3d position
        Vector2f click2d = inputManager.getCursorPosition();
        Vector3f click3d = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
        Vector3f dir = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d).normalizeLocal();
        // Aim the ray from the clicked spot forwards.
        Ray ray = new Ray(click3d, dir);
        // Collect intersections between ray and all nodes in results list.
        rootNode.collideWith(ray, results);
        // (Print the results so we see what is going on:)
        for (int i = 0; i < results.size(); i++) {
          // (For each “hit”, we know distance, impact point, geometry.)
          float dist = results.getCollision(i).getDistance();
          Vector3f pt = results.getCollision(i).getContactPoint();
          String target = results.getCollision(i).getGeometry().getName();
          System.out.println("Selection #" + i + ": " + target + " at " + pt + ", " + dist + " WU away.");
             
          } 
        
         //Use the results -- we rotate the selected geometry.
           if (results.size() > 0) {
          // The closest result is the target that the player picked:
          
          for (int j = 0; j< results.size(); j++){
            if (results.getCollision(j).getGeometry().getName().matches("Floor")){
                // j += 1;
                //System.out.println(j);
            }
            else{
                target = results.getCollision(j).getGeometry();
                System.out.println(target.getName()+ " is selected");
                break;
            }
          }
              
              // Here comes the action:
              if (("blub_quadrangulated1").equals(target.getName())){ 
                  controlObject = 0;
                  System.out.println("Blub is being controlled");        
              } 
              else if(("Tray").equals(target.getName())){
                  controlObject = controlObject;
              }
              else {
                 
                  ballNam = target.getName().substring(0, 11);
                  Num = target.getName().substring(11);
                    if (("cannon ball").equals(ballNam)) {
                    controlObject =1;
                     System.out.println("A ball is being controlled");
                    }
                    
                 }  
              
          }
        } // else if ...   
            
                  
            
        }

    };
    
    public void simpleUpdate(float tpf) {
//        if (ghost.getOverlappingObjects().contains(floor5_phy)){
//            System.out.println("Blub stop!");
//        }    
    
    
     
     if (controlObject == 0){    
          if (left){  
              blub_phy.applyForce(new Vector3f(0f, 0f, 2f),new Vector3f(0f, 0f, 0f));
          }
          if (right) {

              blub_phy.applyForce(new Vector3f(0f, 0f, -2f),new Vector3f(0f, 0f, 0f));

          }
          if (forward) {

              blub_phy.applyForce(new Vector3f(-2f, 0f, 0f),new Vector3f(0f, 0f, 0f));

          }
          if (backward) {

              blub_phy.applyForce(new Vector3f(2f, 0f, 0f),new Vector3f(0f, 0f, 0f));

          }
          if (up) {

              blub_phy.applyForce(new Vector3f(0f, 2f, 0f),new Vector3f(0f, 0f, 0f));

          }
          if (down) {

              blub_phy.applyForce(new Vector3f(0f, -2f, 0f),new Vector3f(0f, 0f, 0f));

          }
     }
          if (controlObject == 1){
           RigidBodyControl selBall_phy = Ballphy [Integer.parseInt(Num)];
           
          if (left){  
              selBall_phy.applyForce(new Vector3f(0f, 0f, 2f),new Vector3f(0f, 0f, 0f));
          }
          if (right) {

              selBall_phy.applyForce(new Vector3f(0f, 0f, -2f),new Vector3f(0f, 0f, 0f));

          }
          if (forward) {

              selBall_phy.applyForce(new Vector3f(-2f, 0f, 0f),new Vector3f(0f, 0f, 0f));

          }
          if (backward) {

              selBall_phy.applyForce(new Vector3f(2f, 0f, 0f),new Vector3f(0f, 0f, 0f));

          }
          if (up) {

              selBall_phy.applyForce(new Vector3f(0f, 11f, 0f),new Vector3f(0f, 0f, 0f));

          }
          if (down) {

              selBall_phy.applyForce(new Vector3f(0f, -2f, 0f),new Vector3f(0f, 0f, 0f));

          }  
     }
     
    }
     
    /** Initialize the materials used in this scene. */
     public void initMaterials() {
         
        floor_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        TextureKey key1 = new TextureKey("Textures/beach.jpg");
        key1.setGenerateMips(true);
        Texture tex1 = assetManager.loadTexture(key1);
        tex1.setWrap(WrapMode.Repeat);
        floor_mat.setTexture("ColorMap", tex1);
        
        floor2_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        TextureKey key2 = new TextureKey("Textures/sea3.jpg");
        key2.setGenerateMips(true);
        Texture tex2 = assetManager.loadTexture(key2);
        tex2.setWrap(WrapMode.Repeat);
        floor2_mat.setTexture("ColorMap", tex2);
        
        floor3_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        TextureKey key4 = new TextureKey("Textures/topwater.jpg");
        key4.setGenerateMips(true);
        Texture tex4 = assetManager.loadTexture(key4);
        tex4.setWrap(WrapMode.Repeat);
        floor3_mat.setTexture("ColorMap", tex4);
        
        ball_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        TextureKey key3 = new TextureKey("Textures/Terrain/Rock/Rock.PNG");
        key3.setGenerateMips(true);
        Texture tex3 = assetManager.loadTexture(key3);
        ball_mat.setTexture("ColorMap", tex3);
        
        
        t_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        // Use transparency - just to make sure we can always see the target
        t_mat.setColor("Color", new ColorRGBA( 0.7f, 0.7f, 0.7f, 0.5f)); // silver'ish
        t_mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        
        tray_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        // Use transparency - just to make sure we can always see the target
        //tray_mat.setTexture("ColorMap", assetManager.loadTexture("Textures/wood.jpg"));
        tray_mat.setColor("Color", new ColorRGBA( 0.7f, 0.7f, 0.7f, 0.5f)); // silver'ish
        tray_mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        
         
     }
     
    public void initTray(){ 
        
        //Top tray
         tray1_geo = new Geometry("Tray", tray1);
         tray1_geo.setMaterial(tray_mat);
         tray1_geo.setQueueBucket(RenderQueue.Bucket.Transparent);
         tray1_geo.setLocalTranslation(0, 8f, 0);
         tray1_geo.rotate(0f, 0f, 0f);
        this.rootNode.attachChild(tray1_geo);
        /* Make the floor physical with mass 0.0f! */
        tray1_phy = new RigidBodyControl(0.5f);
        tray1_geo.addControl(tray1_phy);
        bulletAppState.getPhysicsSpace().add(tray1_phy);
        
         Geometry tray2_geo = new Geometry("Tray", tray2);
         tray2_geo.setMaterial(tray_mat);
         tray2_geo.setQueueBucket(RenderQueue.Bucket.Transparent);
         tray2_geo.setLocalTranslation(0, 9f, -1.9f);
         tray2_geo.rotate(0f, 0f, 0f);
        this.rootNode.attachChild(tray2_geo);
        /* Make the floor physical with mass 0.0f! */
        trayright_phy = new RigidBodyControl(0.0f);
        tray2_geo.addControl(trayright_phy);
        bulletAppState.getPhysicsSpace().add(trayright_phy);
        
        Geometry tray3_geo = new Geometry("Tray", tray2);
         tray3_geo.setMaterial(tray_mat);
         tray3_geo.setQueueBucket(RenderQueue.Bucket.Transparent);
         tray3_geo.setLocalTranslation(0, 9f,1.9f);
         tray3_geo.rotate(0f, 0f, 0f);
        this.rootNode.attachChild(tray3_geo);
        /* Make the floor physical with mass 0.0f! */
        trayleft_phy = new RigidBodyControl(0.0f);
        tray3_geo.addControl(trayleft_phy);
        bulletAppState.getPhysicsSpace().add(trayleft_phy);
        
        Geometry tray4_geo = new Geometry("Tray", tray3);
         tray4_geo.setMaterial(tray_mat);
         tray4_geo.setQueueBucket(RenderQueue.Bucket.Transparent);
         tray4_geo.setLocalTranslation(-0.9f, 9f,0f);
         tray4_geo.rotate(0f, 0f, 0f);
        this.rootNode.attachChild(tray4_geo);
        /* Make the floor physical with mass 0.0f! */
        trayfront_phy = new RigidBodyControl(0.0f);
        tray4_geo.addControl(trayfront_phy);
        bulletAppState.getPhysicsSpace().add(trayfront_phy);
        
         Geometry tray5_geo = new Geometry("Tray", tray3);
         tray5_geo.setMaterial(tray_mat);
         tray5_geo.setQueueBucket(RenderQueue.Bucket.Transparent);
         tray5_geo.setLocalTranslation(0.9f, 9f,0f);
         tray5_geo.rotate(0f, 0f, 0f);
        this.rootNode.attachChild(tray5_geo);
        /* Make the floor physical with mass 0.0f! */
        trayback_phy = new RigidBodyControl(0.0f);
        tray5_geo.addControl(trayback_phy);
        bulletAppState.getPhysicsSpace().add(trayback_phy);
        
        //Right tray
         Rtray1_geo = new Geometry("Tray", tray1);
         Rtray1_geo.setMaterial(tray_mat);
         Rtray1_geo.setQueueBucket(RenderQueue.Bucket.Transparent);
         Rtray1_geo.setLocalTranslation(1, 5f, -2f);
         Rtray1_geo.rotate(0f, 0f, 0f);
        this.rootNode.attachChild(Rtray1_geo);
        /* Make the floor physical with mass 0.0f! */
        Rtray1_phy = new RigidBodyControl(0.5f);
        Rtray1_geo.addControl(Rtray1_phy);
        bulletAppState.getPhysicsSpace().add(Rtray1_phy);
        
         Geometry Rtray2_geo = new Geometry("Tray", tray2);
         Rtray2_geo.setMaterial(tray_mat);
         Rtray2_geo.setQueueBucket(RenderQueue.Bucket.Transparent);
         Rtray2_geo.setLocalTranslation(1, 6f, -3.9f);
         Rtray2_geo.rotate(0f, 0f, 0f);
        this.rootNode.attachChild(Rtray2_geo);
        /* Make the floor physical with mass 0.0f! */
        Rtrayright_phy = new RigidBodyControl(0.0f);
        Rtray2_geo.addControl(Rtrayright_phy);
        bulletAppState.getPhysicsSpace().add(Rtrayright_phy);
        
        Geometry Rtray3_geo = new Geometry("Tray", tray2);
         Rtray3_geo.setMaterial(tray_mat);
         Rtray3_geo.setQueueBucket(RenderQueue.Bucket.Transparent);
         Rtray3_geo.setLocalTranslation(1, 6f,-0.1f);
         Rtray3_geo.rotate(0f, 0f, 0f);
        this.rootNode.attachChild(Rtray3_geo);
        /* Make the floor physical with mass 0.0f! */
        Rtrayleft_phy = new RigidBodyControl(0.0f);
        Rtray3_geo.addControl(Rtrayleft_phy);
        bulletAppState.getPhysicsSpace().add(Rtrayleft_phy);
        
        Geometry Rtray4_geo = new Geometry("Tray", tray3);
         Rtray4_geo.setMaterial(tray_mat);
         Rtray4_geo.setQueueBucket(RenderQueue.Bucket.Transparent);
         Rtray4_geo.setLocalTranslation(0.1f, 6f,-2f);
         Rtray4_geo.rotate(0f, 0f, 0f);
        this.rootNode.attachChild(Rtray4_geo);
        /* Make the floor physical with mass 0.0f! */
        Rtrayfront_phy = new RigidBodyControl(0.0f);
        Rtray4_geo.addControl(Rtrayfront_phy);
        bulletAppState.getPhysicsSpace().add(Rtrayfront_phy);
        
         Geometry Rtray5_geo = new Geometry("Tray", tray3);
         Rtray5_geo.setMaterial(tray_mat);
         Rtray5_geo.setQueueBucket(RenderQueue.Bucket.Transparent);
         Rtray5_geo.setLocalTranslation(1.9f, 6f,-2f);
         Rtray5_geo.rotate(0f, 0f, 0f);
        this.rootNode.attachChild(Rtray5_geo);
        /* Make the floor physical with mass 0.0f! */
        Rtrayback_phy = new RigidBodyControl(0.0f);
        Rtray5_geo.addControl(Rtrayback_phy);
        bulletAppState.getPhysicsSpace().add(Rtrayback_phy);
        
        //Left tray
         Ltray1_geo = new Geometry("Tray", tray1);
         Ltray1_geo.setMaterial(tray_mat);
         Ltray1_geo.setQueueBucket(RenderQueue.Bucket.Transparent);
         Ltray1_geo.setLocalTranslation(3, 2.5f, 1.5f);
         Ltray1_geo.rotate(0f, 0f, 0f);
        this.rootNode.attachChild(Ltray1_geo);
        /* Make the floor physical with mass 0.0f! */
        Ltray1_phy = new RigidBodyControl(0.5f);
        Ltray1_geo.addControl(Ltray1_phy);
        bulletAppState.getPhysicsSpace().add(Ltray1_phy);
        
         Geometry Ltray2_geo = new Geometry("Tray", tray2);
         Ltray2_geo.setMaterial(tray_mat);
         Ltray2_geo.setQueueBucket(RenderQueue.Bucket.Transparent);
         Ltray2_geo.setLocalTranslation(3, 3.5f, -0.4f);
         Ltray2_geo.rotate(0f, 0f, 0f);
        this.rootNode.attachChild(Ltray2_geo);
        /* Make the floor physical with mass 0.0f! */
        Ltrayright_phy = new RigidBodyControl(0.0f);
        Ltray2_geo.addControl(Ltrayright_phy);
        bulletAppState.getPhysicsSpace().add(Ltrayright_phy);
        
        Geometry Ltray3_geo = new Geometry("Tray", tray2);
         Ltray3_geo.setMaterial(tray_mat);
         Ltray3_geo.setQueueBucket(RenderQueue.Bucket.Transparent);
         Ltray3_geo.setLocalTranslation(3, 3.5f,3.4f);
         Ltray3_geo.rotate(0f, 0f, 0f);
        this.rootNode.attachChild(Ltray3_geo);
        /* Make the floor physical with mass 0.0f! */
        Ltrayleft_phy = new RigidBodyControl(0.0f);
        Ltray3_geo.addControl(Ltrayleft_phy);
        bulletAppState.getPhysicsSpace().add(Ltrayleft_phy);
        
        Geometry Ltray4_geo = new Geometry("Tray", tray3);
         Ltray4_geo.setMaterial(tray_mat);
         Ltray4_geo.setQueueBucket(RenderQueue.Bucket.Transparent);
         Ltray4_geo.setLocalTranslation(2.1f, 3.5f,1.5f);
         Ltray4_geo.rotate(0f, 0f, 0f);
        this.rootNode.attachChild(Ltray4_geo);
        /* Make the floor physical with mass 0.0f! */
        Ltrayfront_phy = new RigidBodyControl(0.0f);
        Ltray4_geo.addControl(Ltrayfront_phy);
        bulletAppState.getPhysicsSpace().add(Ltrayfront_phy);
        
         Geometry Ltray5_geo = new Geometry("Tray", tray3);
         Ltray5_geo.setMaterial(tray_mat);
         Ltray5_geo.setQueueBucket(RenderQueue.Bucket.Transparent);
         Ltray5_geo.setLocalTranslation(3.9f, 3.5f,1.5f);
         Ltray5_geo.rotate(0f, 0f, 0f);
        this.rootNode.attachChild(Ltray5_geo);
        /* Make the floor physical with mass 0.0f! */
        Ltrayback_phy = new RigidBodyControl(0.0f);
        Ltray5_geo.addControl(Ltrayback_phy);
        bulletAppState.getPhysicsSpace().add(Ltrayback_phy);
    
    }
     
    public RigidBodyControl makeCannonBall() {
        /** Create a cannon ball geometry and attach to scene graph. */
        Geometry ball_geo = new Geometry("cannon ball" +  Integer.toString(ball_Index), sphere);
        System.out.println(ball_geo.getName());
        //Geometry ball_geo = new Geometry("cannon ball", sphere);
        ball_geo.setMaterial(ball_mat);
        rootNode.attachChild(ball_geo);
        
        /** Position the cannon ball  */
        //Random rand = new Random();
        int rand_x = FastMath.nextRandomInt(-4, 4);
        int rand_z = FastMath.nextRandomInt(-4, 4);
        ball_geo.setLocalTranslation(new Vector3f(rand_x, 11f, rand_z));
        /** Make the ball physcial with a mass > 0.0f */
        //float rand_mass = FastMath.nextRandomFloat()*2;
        ball_phy = new RigidBodyControl(1.0f);
        /** Add physical ball to physics space. */
        ball_geo.addControl(ball_phy);
        bulletAppState.getPhysicsSpace().add(ball_phy);
        /** Accelerate the physcial ball to shoot it. */
        float rand_vx = FastMath.nextRandomFloat()*2 - 1;
        float rand_vy = FastMath.nextRandomFloat()*2 - 1;
        float rand_vz = FastMath.nextRandomFloat()*2 - 1;
        float rand_speed = FastMath.nextRandomFloat()*8;
        Vector3f v = new  Vector3f (rand_vx, rand_vy, rand_vz);
        ball_phy.setLinearVelocity(v.mult(rand_speed));
        ball_phy.setGravity(new Vector3f(0f,-9.81f,0f));
        
         ball_geo.setShadowMode(ShadowMode.CastAndReceive);
        return  ball_phy;
  }
    public void setupJoint() {
        //Top joint
        Node hookNode = new Node("hookNode");
        rootNode.attachChild(hookNode);    
        hookNode.setLocalTranslation(0, 9f, -1.9f);
        hookNode.rotate(0f, 0f, 0f);
        hookNode.addControl(trayright_phy);
        bulletAppState.getPhysicsSpace().add(hookNode);

        Node pendulumNode = new Node("pendulumNode");
        rootNode.attachChild(pendulumNode);    
        pendulumNode.setLocalTranslation(0f, 8f, 0f);
        pendulumNode.rotate(0f, 0f, 0f);
        pendulumNode.addControl(tray1_phy);
        bulletAppState.getPhysicsSpace().add(pendulumNode);
       
        tray1_geo.addControl(tray1_phy);
        bulletAppState.getPhysicsSpace().add(tray1_phy);

         joint=new HingeJoint(hookNode.getControl(RigidBodyControl.class), // A
                     pendulumNode.getControl(RigidBodyControl.class), // B
                     new Vector3f(0f, -1f, 0f),  // pivot point local to A
                     new Vector3f(0f, 0f, -1.9f),  // pivot point local to B
                     Vector3f.UNIT_X,           // DoF Axis of A (Z axis)
                     Vector3f.UNIT_X);        // DoF Axis of B (Z axis)
         bulletAppState.getPhysicsSpace().add(joint);
         joint.enableMotor(true, 1, 0.6f); 
         joint.setLimit(FastMath.DEG_TO_RAD *-40, FastMath.DEG_TO_RAD *40);
        
        //Right joint
        Node hookNode2 = new Node("hookNode2");
        rootNode.attachChild(hookNode2);    
        hookNode2.setLocalTranslation(1, 6f, -3.9f);
        hookNode2.rotate(0f, 0f, 0f);
        hookNode2.addControl(Rtrayright_phy);
        bulletAppState.getPhysicsSpace().add(hookNode2);

        Node pendulumNode2 = new Node("pendulumNode2");
        rootNode.attachChild(pendulumNode2);    
        pendulumNode2.setLocalTranslation(1, 5f, -2f);
        pendulumNode2.rotate(0f, 0f, 0f);
        pendulumNode2.addControl(Rtray1_phy);
        bulletAppState.getPhysicsSpace().add(pendulumNode2);
        
        Rtray1_geo.addControl(Rtray1_phy);
        bulletAppState.getPhysicsSpace().add(Rtray1_phy);

         joint2=new HingeJoint(hookNode2.getControl(RigidBodyControl.class), // A
                     pendulumNode2.getControl(RigidBodyControl.class), // B
                     new Vector3f(0f, -1f, 0f),  // pivot point local to A
                     new Vector3f(0f, 0f, -1.9f),  // pivot point local to B
                     Vector3f.UNIT_X,           // DoF Axis of A (Z axis)
                     Vector3f.UNIT_X);        // DoF Axis of B (Z axis)
         bulletAppState.getPhysicsSpace().add(joint2);
         joint2.enableMotor(true, 1, 0.6f); 
         joint2.setLimit(FastMath.DEG_TO_RAD *-40, FastMath.DEG_TO_RAD *40);
         
        //Left joint
        Node hookNode3 = new Node("hookNode3");
        rootNode.attachChild(hookNode3);    
        hookNode3.setLocalTranslation(3, 3.5f, -0.4f);
        hookNode3.rotate(0f, 0f, 0f);
        hookNode3.addControl(Ltrayright_phy);
        bulletAppState.getPhysicsSpace().add(hookNode3);

        Node pendulumNode3 = new Node("pendulumNode3");
        rootNode.attachChild(pendulumNode3);    
        pendulumNode3.setLocalTranslation(3, 2.5f, 1.5f);
        pendulumNode3.rotate(0f, 0f, 0f);
        pendulumNode3.addControl(Ltray1_phy);
        bulletAppState.getPhysicsSpace().add(pendulumNode3);
        Ltray1_geo.addControl(Ltray1_phy);
        bulletAppState.getPhysicsSpace().add(Ltray1_phy);

         joint3=new HingeJoint(hookNode3.getControl(RigidBodyControl.class), // A
                     pendulumNode3.getControl(RigidBodyControl.class), // B
                     new Vector3f(0f, -1f, 0f),  // pivot point local to A
                     new Vector3f(0f, 0f, -1.9f),  // pivot point local to B
                     Vector3f.UNIT_X,           // DoF Axis of A (Z axis)
                     Vector3f.UNIT_X);        // DoF Axis of B (Z axis)
         bulletAppState.getPhysicsSpace().add(joint3);
         joint3.enableMotor(true, 1, 0.6f); 
         joint3.setLimit(FastMath.DEG_TO_RAD *-40, FastMath.DEG_TO_RAD *40);
 
    }
     
   public void initFloor() {
        //top
        Geometry floor1_geo = new Geometry("Floor", floor1);
        floor1_geo.setMaterial(floor3_mat);
        floor1_geo.setLocalTranslation(0, 12f, 0);
        this.rootNode.attachChild(floor1_geo);
        /* Make the floor physical with mass 0.0f! */
        floor1_phy = new RigidBodyControl(0.0f);
        floor1_geo.addControl(floor1_phy);
        bulletAppState.getPhysicsSpace().add(floor1_phy);
        floor1_geo.setShadowMode(ShadowMode.Receive);
        
        //right
        Geometry floor2_geo = new Geometry("Floor", floor2);
        floor2_geo.setMaterial(floor2_mat);
        floor2_geo.setLocalTranslation(0, 6f, -5f);
        floor2_geo.rotate(1.57f, 0f, 0f);
        this.rootNode.attachChild(floor2_geo);
        /* Make the floor physical with mass 0.0f! */
        floor2_phy = new RigidBodyControl(0.0f);
        floor2_geo.addControl(floor2_phy);
        bulletAppState.getPhysicsSpace().add(floor2_phy);
        floor2_geo.setShadowMode(ShadowMode.Receive);
        
        //left
        Geometry floor3_geo = new Geometry("Floor", floor3);
        floor3_geo.setMaterial(floor2_mat);
        floor3_geo.setLocalTranslation(0, 6f, 5f);
        floor3_geo.rotate(1.57f, 0f, 0f);
        this.rootNode.attachChild(floor3_geo);
        /* Make the floor physical with mass 0.0f! */
        floor3_phy = new RigidBodyControl(0.0f);
        floor3_geo.addControl(floor3_phy);
        bulletAppState.getPhysicsSpace().add(floor3_phy);
        floor3_geo.setShadowMode(ShadowMode.Receive);
        
        //back
        Geometry floor4_geo = new Geometry("Floor", floor4);
        floor4_geo.setMaterial(floor2_mat);
        floor4_geo.setLocalTranslation(-5f, 6f, 0);
        floor4_geo.rotate(0f, 0f, 1.57f);
        this.rootNode.attachChild(floor4_geo);
        /* Make the floor physical with mass 0.0f! */
        floor4_phy = new RigidBodyControl(0.0f);
        floor4_geo.addControl(floor4_phy);
        bulletAppState.getPhysicsSpace().add(floor4_phy);
        floor4_geo.setShadowMode(ShadowMode.Receive);
        
        //bottom
        Geometry floor5_geo = new Geometry("Floor", floor5);
        floor5_geo.setMaterial(floor_mat);
        floor5_geo.setLocalTranslation(0, 0f, 0);
        this.rootNode.attachChild(floor5_geo);
        /* Make the floor physical with mass 0.0f! */
        floor5_phy = new RigidBodyControl(0.0f);
        floor5_geo.addControl(floor5_phy);
        bulletAppState.getPhysicsSpace().add(floor5_phy);
        floor5_geo.setShadowMode(ShadowMode.Receive);
        
        Geometry floor6_geo = new Geometry("Floor", floor6);
        floor6_geo.setMaterial(t_mat);
        floor6_geo.setQueueBucket(RenderQueue.Bucket.Transparent);
        floor6_geo.setLocalTranslation(5, 6f, 0);
        floor6_geo.rotate(0f, 0f, 1.57f);
        floor6_geo.setCullHint(CullHint.Always);
        this.rootNode.attachChild(floor6_geo);
        /* Make the floor physical with mass 0.0f! */
        floor6_phy = new RigidBodyControl(0.0f);
        floor6_geo.addControl(floor6_phy);
        bulletAppState.getPhysicsSpace().add(floor6_phy);
        
        System.out.println(floor6_geo.getName());
  }



    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
    // test for a condition you are interested in, e.g. ...
    if (animName.equals("turn_O")) {
      // respond to the event here, e.g. ...    
       swimFlag = 0;
       channel_swim.setAnim("swim_stop");
       channel_idle.setAnim("stationary_stop");
    }
  }
    public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
    // test for a condition you are interested in, e.g. ...
    if (animName.equals("turn_O")) {
      // respond to the event here, e.g. ...
      swimFlag = 1;
      channel_swim.setAnim("ArmatureAction");
      channel_idle.setAnim("stationaryIdle");
    }
  }

    @Override
    public void collision(PhysicsCollisionEvent event) {

          if (  blubName.equals(event.getNodeA().getName()) || blubName.equals(event.getNodeB().getName())) {
                    System.out.println("Blub is collided!");
                    if (event.getAppliedImpulse()>0.3){
                      audio_blub.setVolume(event.getAppliedImpulse());
                      audio_blub.play();
                    }

            } 

             if (  ("Floor").equals(event.getNodeA().getName()) && !(blubName.equals(event.getNodeB().getName()))) {
                System.out.println("Ball is collided!");
                if (event.getAppliedImpulse()>0.3){
                  audio_ball.setVolume(3);
                  audio_ball.play();
                }
            }
             
            if (  ("Floor").equals(event.getNodeB().getName()) && !(blubName.equals(event.getNodeA().getName()))) {
                System.out.println("Ball is collided!");
                if (event.getAppliedImpulse()>0.3){
                  audio_ball.setVolume(3);
                  audio_ball.play();
                }
            }
             
    }
    
     private void initAudio() {
        audio_bgm = new AudioNode(assetManager, "Sounds/ocean.wav",  DataType.Buffer);  
        audio_bgm.setLooping(true);  // activate continuous playing
        audio_bgm.setPositional(false);
        audio_bgm.setVolume(3);
        rootNode.attachChild(audio_bgm);
        audio_bgm.play(); // play continuously!
        
       audio_click = new AudioNode(assetManager, "Sounds/click.wav", DataType.Buffer);
       audio_click.setPositional(false);
       audio_click.setLooping(false);
       audio_click.setVolume(2);
       rootNode.attachChild(audio_click);
       
       audio_blub = new AudioNode(assetManager, "Sounds/collision_blub.wav", DataType.Buffer);
       audio_blub .setPositional(false);
       audio_blub .setLooping(false);
       //audio_blub .setVolume(2);
       rootNode.attachChild(audio_blub);
       
       audio_ball = new AudioNode(assetManager, "Sounds/collision_ball.WAV", DataType.Buffer);
       audio_ball .setPositional(false);
       audio_ball .setLooping(false);
       rootNode.attachChild(audio_ball);
       
       audio_shoot = new AudioNode(assetManager, "Sounds/shoot.wav", DataType.Buffer);
       audio_shoot.setPositional(false);
       audio_shoot.setLooping(false);
       rootNode.attachChild(audio_shoot);
       
     }

  
}
