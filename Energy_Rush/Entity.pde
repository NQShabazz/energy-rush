/*
  This is the Entity class. Entitys (yes, Entitys) will be used for all interactive game objects. This is a base class for other objects to build off of.
  Entitys blink and are immobile while initializing. Entitys become mobile, and fade down from white when spawned
*/

public class Entity{
  color hexColor; //← The Entity's assigned color
  color trueHexColor; //← The true color the Entity will be displayed with
  float opacity; //← Opacity of Entity
  protected float positionX, positionY, velocityX, velocityY, size, rotation; //← X position, Y position, X velocity, Y velocity, rotation and size of Entity
  protected float initTime; //← Time passed since starting Entity
  protected float spawnTime; //← Time passed since initializing Entity
  protected boolean initialized, spawned; //← The initialized and spawned states of the Entity. Entitys cannot be spawned without being initialized.
  protected float blinkTime, initBlinkTime, spawnFadeTime; //← How long it takes to blink when initializing, how long it takes for initialization of an Entity, and how long it takes for an Entity to truly spawn
  protected boolean dead; //← Whether or not an Entity is dead. Not much use in base class
  

/*
  The contructor will initialize Entity with an X position, Y position, X velocity, Y velocity, and rotation of 0
  As well, all timer variables are set to millis(), the time at the start of the Entity's creation
  trueHexColor and hexColor are initialized to white, and opacity is initialized to 255 (fully opaque)
  initBlinkTime is set to 1500 by default (1.5 seconds of blinking)
  spawnFadeTime is set to 500 by default (0.5 seconds of fading down from white)
  Also, give it a chance at life. dead = false by default
*/
  public Entity(){
    positionX = positionY = velocityX = velocityY = rotation = 0;
    initTime = spawnTime = millis();
    blinkTime = initBlinkTime = spawnFadeTime = millis();
    initialized = spawned = false;
    trueHexColor = #ffffff;
    hexColor = #ffffff;
    opacity = 255;
    initBlinkTime = 1500;
    spawnFadeTime = 500;
    dead = false;
  }
  
  //↓ The step function calls update and render, and should be called every frame
  public void step(){
    update();
    render();
  }
  //↓ The base update method of the Entity. Does the blinking and fade in of the Entity, which is optional.
  void baseUpdate(){
    //↓ If the Entity is not initialized, it cannot have been spawned
    if(!initialized)
      spawned = false;
      
    //↓ If the Entity is not yet initialized, and there's a 250 millisecond difference between the time now and blinkTime, then blink
    if(!initialized && millis() - blinkTime > 250){
      //↓ Set trueHexColor to hexColor
      trueHexColor = hexColor;
      
      //↓ Switch opacity from 0 to 255 or vice versa
      if(opacity == 255)
        opacity = 0;
      else
        opacity = 255;
        
      //↓ Set blinkTime to now so that another 250 milliseconds will have to pass to blink again
      blinkTime = millis();
    }
    
    //↓ If the Entity is not yet initialized and the prerequisite amount of time has passed for the object to initialize, then initialize it
    if(!initialized && millis() - initTime > initBlinkTime){
      initialized = true; //← The Entity is now initialized
      
      //↓ Set opacity to fully opaque just in case
      opacity = 255;
      
      //↓ Set spawnTime to now
      spawnTime = millis();
    }
    
    //↓ If the Entity is initialized, but not yet spawned, then linearly interpolate the true color of the Entity from white to it's preset color, based on how much time has passed since initialization
    if(initialized && !spawned)
      trueHexColor = lerpColor(#ffffff, hexColor, (millis() - spawnTime)/spawnFadeTime);
    
    //↓ If the Entity is initialized, and enough time has passed to spawn, but it is not yet spawned, then spawn it
    if(initialized && millis() - spawnTime > spawnFadeTime && !spawned){
      //↓ Set true color of Entity to it's preset color
      trueHexColor = hexColor;
      spawned = true; //← The Entity is now officially spawned
    }
      
  }
  //↓ This is the template update method to be overriden in the child classes. Use for updating variables and the like
  void update(){
    
  }
  //↓ This is the template render method to be overriden in the child classes. Use for draw calls and the like
  void render(){
    
  }
  
  //↓ Getter for positionX
  public float getPositionX(){
    return positionX;
  }
  //↓ Getter for positionY
  public float getPositionY(){
    return positionY;
  }
  //↓ Getter for velocityX
  public float getVelocityX(){
    return velocityX;
  }
  //↓ Getter for velocityY
  public float getVelocityY(){
    return velocityY;
  }
  //↓ Getter for size
  public float getSize(){
    return size;
  }
  //↓ Getter for 'initialized' state
  public boolean getInitialized(){
    return initialized;
  }
  //↓ Getter for 'spawned' state
  public boolean getSpawned(){
    return spawned;
  }
  //↓ Getter for color
  public color getHexColor(){
    return hexColor;
  }
  //↓ Getter for true color
  public color getTrueHexColor(){
    return trueHexColor;
  }
  //↓ Getter for opacity
  public float getOpacity(){
    return opacity;
  }
  //↓ Getter for 'dead' state
  public boolean getDead(){
    return dead;
  }
  
  //↓ Setter for 'initialized' state
  public void setInitialized(boolean isInitialized){
    initialized = isInitialized;
  }
  //↓ Setter for 'spawned' state
  public void setSpawned(boolean isSpawned){
    spawned = isSpawned;
  }
  //↓ Setter for 'dead' state
  public void setDead(boolean dead){
    this.dead = dead;
  }
  //↓ Setter for rotation value
  public void setRotation(float rotation){
    this.rotation = rotation;
  }
  
  //↓ Public method for Entity-Entity collision
  public boolean collidesWith(Entity e){
    return pow(e.getPositionX() - positionX, 2) + pow(e.getPositionY() - positionY, 2) < pow(e.getSize()/2 + size, 2);
  }
}
