package ass1;

import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL2;




/**
 * A ass1.GameObject is an object that can move around in the game world.
 *
 * GameObjects form a scene tree. The root of the tree is the special ROOT object.
 *
 * Each ass1.GameObject is offset from its parent by a rotation, a translation and a scale factor.
 *
 * @author malcolmr
 */
public class GameObject {
  
  // the list of all GameObjects in the scene tree
  public final static List<GameObject> ALL_OBJECTS = new ArrayList<GameObject>();
  
  // the root of the scene tree
  public final static GameObject ROOT = new GameObject();
  
  // the links in the scene tree
  private GameObject myParent;
  private List<GameObject> myChildren;
  
  // the local transformation
  //myRotation should be normalised to the range (-180..180)
  private double myRotation;
  private double myScale;
  private double[] myTranslation;
  
  // is this part of the tree showing?
  private boolean amShowing;
  
  /**
   * Special private constructor for creating the root node. Do not use otherwise.
   */
  private GameObject() {
    myParent = null;
    myChildren = new ArrayList<GameObject>();
    
    myRotation = 0;
    myScale = 1;
    myTranslation = new double[2];
    myTranslation[0] = 0;
    myTranslation[1] = 0;
    
    amShowing = true;
    
    ALL_OBJECTS.add(this);
  }
  
  /**
   * Public constructor for creating GameObjects, connected to a parent (possibly the ROOT).
   *
   * New objects are created at the same location, orientation and scale as the parent.
   *
   * @param parent parent GameObject to assign to gameobject
   */
  public GameObject(GameObject parent) {
    myParent = parent;
    myChildren = new ArrayList<GameObject>();
    
    parent.myChildren.add(this);
    
    myRotation = 0;
    myScale = 1;
    myTranslation = new double[2];
    myTranslation[0] = 0;
    myTranslation[1] = 0;
    
    // initially showing
    amShowing = true;
    
    ALL_OBJECTS.add(this);
  }
  
  /**
   * Remove an object and all its children from the scene tree.
   */
  public void destroy() {
    for (GameObject child : myChildren) {
      child.destroy();
    }
    
    myParent.myChildren.remove(this);
    ALL_OBJECTS.remove(this);
  }
  
  /**
   * Get the parent of this game object
   *
   * @return parent of this game object
   */
  public GameObject getParent() {
    return myParent;
  }
  
  /**
   * Get the children of this object
   *
   * @return list of children of this object
   */
  public List<GameObject> getChildren() {
    return myChildren;
  }
  
  /**
   * Get the local rotation (in degrees)
   *
   * @return local rotation (in degrees)
   */
  public double getRotation() {
    return myRotation;
  }
  
  /**
   * Set the local rotation (in degrees)
   */
  public void setRotation(double rotation) {
    myRotation = MathUtil.normaliseAngle(rotation);
  }
  
  /**
   * Rotate the object by the given angle (in degrees)
   *
   * @param angle angle to rotate object by (in degrees)
   */
  public void rotate(double angle) {
    myRotation += angle;
    myRotation = MathUtil.normaliseAngle(myRotation);
  }
  
  /**
   * Get the local scale
   *
   * @return local scale
   */
  public double getScale() {
    return myScale;
  }
  
  /**
   * Set the local scale
   *
   * @param scale scale to set
   */
  public void setScale(double scale) {
    myScale = scale;
  }
  
  /**
   * Multiply the scale of the object by the given factor
   *
   * @param factor factor to multiply object scale with
   */
  public void scale(double factor) {
    myScale *= factor;
  }
  
  /**
   * Get the local position of the object
   *
   * @return local position of object
   */
  public double[] getPosition() {
    double[] t = new double[2];
    t[0] = myTranslation[0];
    t[1] = myTranslation[1];
    
    return t;
  }
  
  /**
   * Set the local position of the object
   *
   * @param x x coord to set for local position
   * @param y y coord to set for local position
   */
  public void setPosition(double x, double y) {
    myTranslation[0] = x;
    myTranslation[1] = y;
  }
  
  /**
   * Move the object by the specified offset in local coordinates
   *
   * @param dx change in x to offset position
   * @param dy change in y to offset position
   */
  public void translate(double dx, double dy) {
    myTranslation[0] += dx;
    myTranslation[1] += dy;
  }
  
  /**
   * Test if the object is visible
   *
   * @return true if object is visible
   */
  public boolean isShowing() {
    return amShowing;
  }
  
  /**
   * Set the showing flag to make the object visible (true) or invisible (false).
   * This flag should also apply to all descendents of this object.
   *
   * @param showing set to true if object should be visible
   */
  public void show(boolean showing) {
    amShowing = showing;
  }
  
  /**
   * Update the object. This method is called once per frame.
   *
   * This does nothing in the base ass1.GameObject class. Override this in subclasses.
   *
   * @param dt The amount of time since the last update (in seconds)
   */
  public void update(double dt) {
    // do nothing
  }
  
  /**
   * Draw the object (but not any descendants)
   *
   * This does nothing in the base ass1.GameObject class. Override this in subclasses.
   *
   * @param gl
   */
  public void drawSelf(GL2 gl) {
    // do nothing
  }
  
  
  /**
   * Checks if point collides with game object
   *
   * Override this in subclasses.
   *
   * @param gl
   */
  public boolean collision(double[] point) {
    return false;
  }
  
  // ===========================================
  // COMPLETE THE METHODS BELOW
  // ===========================================
  
  /**
   * Draw the object and all of its descendants recursively.
   *
   * @param gl
   */
  public void draw(GL2 gl) {
    
    // don't draw if it is not showing
    if (!amShowing) {
      return;
    }
    
    //Setting the model transform appropriately
    gl.glMatrixMode(GL2.GL_MODELVIEW);
    gl.glPushMatrix();
    double[] translationPos = getPosition();
    gl.glTranslated(translationPos[0], translationPos[1], 0);
    gl.glRotated(getRotation(), 0, 0, 1);
    gl.glScaled(getScale(), getScale(), 1);
    
    // draw the object (Call drawSelf() to draw the object itself)
    drawSelf(gl);
    
    // draw all its children
    for (GameObject gameObject : getChildren()) {
      gameObject.draw(gl);
    }
    
    gl.glPopMatrix();
  }
  
  
  /**
   *
   * Compute the TRS model view matrix for this given GameObject
   *
   * @return a 3x3 model view matrix
   */
  public double[][] computeModelViewMatrix() {
    double[][] translation = MathUtil.translationMatrix(myTranslation);
    double[][] rotation = MathUtil.rotationMatrix(myRotation);
    double[][] scale = MathUtil.scaleMatrix(myScale);
    
    return MathUtil.multiply(MathUtil.multiply(translation, rotation), scale); //(T*R)*S
  }
  
  /**
   *
   * Compute the SRT model view matrix for this given GameObject.
   * Where the order of changes are reversed and changes are inversed.
   *
   * @return a 3x3 model view matrix
   */
  public double[][] computeInverseModelViewMatrix() {
    double[][] invRotation = MathUtil.rotationMatrix(-myRotation);
    double[][] invScale = MathUtil.scaleMatrix(1/myScale);
    double[][] invTranslation = MathUtil.translationMatrix(new double[] {-myTranslation[0], -myTranslation[1]});
    
    return MathUtil.multiply(MathUtil.multiply(invScale, invRotation), invTranslation); //(S*R)*T
  }
  
  
  /**
   * Compute the object's position in world coordinates
   *
   * @return a point in world coordinats in [x,y] form
   */
  public double[] getGlobalPosition() {
    double[] p = new double[2];
    double[][] m = computeModelViewMatrix();
    
    if (myParent != null)
      m = MathUtil.multiply(myParent.computeModelViewMatrix(), m);
    
    p[0] = m[0][2];
    p[1] = m[1][2];
    
    return p;
  }
  
  /**
   * Compute the object's rotation in the global coordinate frame
   *
   * @return the global rotation of the object (in degrees) and
   * normalized to the range (-180, 180) degrees.
   */
  public double getGlobalRotation() {
    double[][] m = computeModelViewMatrix();
    
    if (myParent != null)
      m = MathUtil.multiply(myParent.computeModelViewMatrix(), m);
    
    return MathUtil.normaliseAngle(Math.toDegrees(Math.atan2(m[1][0], m[0][0])));
  }
  
  /**
   * Compute the object's scale in global terms
   *
   * @return the global scale of the object
   */
  public double getGlobalScale() {
    double[][] m = computeModelViewMatrix();
    
    if (myParent != null)
      m = MathUtil.multiply(myParent.computeModelViewMatrix(), m);
    
    return Math.sqrt(Math.pow(m[0][0], 2) + Math.pow(m[1][0], 2) + Math.pow(m[2][0], 2));
  }
  
  /**
   * Change the parent of a game object.
   *
   * Ensure the object does not change its global position, rotation or scale
   * when it is reparented.
   *
   * @param parent new parent for game object
   */
  public void setParent(GameObject parent) {
    //Get global TRS values for current GameObject
    double[] globalPosition = getGlobalPosition();
    double globalRotation = getGlobalRotation();
    double globalScale = getGlobalScale();
    
    //Get global TRS values for NEW parent GameObject
    double[] parentGlobalPosition = parent.getGlobalPosition();
    double parentGlobalRotation = parent.getGlobalRotation();
    double parentGlobalScale = parent.getGlobalScale();
    
    //Get parent matrices for inverse values
    double[][] parentInverseModelViewMatrix = parent.computeInverseModelViewMatrix();
    
    double[] globalPositionPoint = new double[] {globalPosition[0], globalPosition[1], 1}; //add missing 1
    double[] finalPositionVector = MathUtil.multiply(parentInverseModelViewMatrix, globalPositionPoint);
    
    //Update the global position, rotation and scale
    setPosition(finalPositionVector[0], finalPositionVector[1]);
    setRotation(globalRotation - parentGlobalRotation);
    setScale(globalScale / parentGlobalScale);
    
    //Remove child from current parent and add to new parent
    myParent.myChildren.remove(this);
    myParent = parent;
    myParent.myChildren.add(this);
    
  }
}
