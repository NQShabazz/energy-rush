import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.Collections; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Energy_Rush extends PApplet {

//TODO - List:
//add sound T_T
//CircleExplosion on player death

ArrayList<Player> players;
Player p0, p1, p2, p3;
int numPlayersRequested, numPlayersAlive;
float[] timeSinceLastEnergySpawn = {
  1000, 1000, 1000, 1000
};
float timeToWaitForNextEnergySpawn = 500;

UserInterface UI;

PauseScreen PS;
HelpScreen HS;
ExplanationScreen ES;
ControlsScreen CS;
AboutScreen AS;
ResultScreen RS;
boolean showPaused, showHelp, showExplanation, showControls, showAbout, muted;
float timeSinceLastKill; //\u2190 Time since last kill. Used for the slow motion thing

public void setup() {
  
  surface.setTitle("Energy Rush");

  players = new ArrayList<Player>();

  p0 = new Player(0);
  players.add(p0);
  p1 = new Player(1);
  players.add(p1);
  p2 = new Player(2);
  players.add(p2);
  p3 = new Player(3);
  players.add(p3);

  UI = new UserInterface();
  PS = new PauseScreen();
  HS = new HelpScreen();
  ES = new ExplanationScreen();
  CS = new ControlsScreen();
  AS = new AboutScreen();
  RS = new ResultScreen();
  
  showPaused = true;
  showHelp = showExplanation = showControls = showAbout = muted = false;

  numPlayersAlive = numPlayersRequested = players.size();
  timeSinceLastKill = millis() - 1000;
}

public void draw() {
  numPlayersAlive = 0;
  for (Player p : players)
    if (p.getLives() > 0)
      numPlayersAlive++;

  if (numPlayersAlive < 2) {
    RS.render();
  } else if (showPaused) {
    background(0);
    for (Player p : players)
      p.render();

    UI.render();

    if (showHelp) {
      if (showExplanation)
        ES.render();
      else if (showControls)
        CS.render();
      else if (showAbout)
        AS.render();
      else  
        HS.render();
    } else
      PS.render();
  } else {
    update();

    background(0);

    for (Player p : players)
      p.step();

    UI.render();
  }
}

public void update() {
  for (int i = 0; i < players.size (); i++) {
    for (int ii = 0; ii < players.size (); ii++) {
      if (ii != i && players.get(ii).getCanLive() && players.get(i).getCanLive()) {
        if (players.get(i).collidesWith(players.get(ii))) {
          players.get(i).addKill(players.get(ii).getEnergy());
          players.get(ii).addKill(players.get(i).getEnergy());
          players.get(i).die();
          players.get(ii).die();
          timeSinceLastKill = millis();
          break;
        }
        for (AfterImage aI : players.get (ii).getAfterImages())
          if (players.get(i).collidesWith(aI)) {
            players.get(ii).addKill(players.get(i).getEnergy());
            players.get(i).die();
            timeSinceLastKill = millis();
            break;
          }
      }
    }
  }
  
  if(millis() - timeSinceLastKill < 1000)
    frameRate(20);
  else if(frameRate < 58)
    frameRate(60);
    
}

public void restart() {
  players = new ArrayList<Player>();

  for (int i = 0; i < numPlayersRequested; i++)
    players.add(new Player(i));

  UI.reset();
  showPaused = showHelp = showExplanation = showControls = showAbout = false;
  numPlayersAlive = players.size();
}

public void keyPressed() {
  if (key == ESC) {
    key = 0;
  } else if (numPlayersAlive < 2) {
    if (key == 'R' || key == 'r')
      restart();
  } else if (key == 'P' || key == 'p') {
    showPaused = !showPaused;

    if (!showPaused) {
      showHelp = showExplanation = showControls = showAbout = false;
      if (numPlayersRequested != players.size())
        restart();
    }
  } else if (showPaused) {
    if (showHelp) {
      if (!showControls && !showAbout && (key == 'E' || key == 'e')) {
        showExplanation = !showExplanation;
      } else if (!showAbout && !showExplanation && (key == 'C' || key == 'c')) {
        showControls = !showControls;
      } else if (!showExplanation && !showControls && (key == 'A' || key == 'a')) {
        showAbout = !showAbout;
      } else if (key == 'H' || key=='h')
        showHelp = showExplanation = showControls = showAbout = false;
    } else {
      if (key == 'R' || key == 'r') {
        restart();
      } else if (key == '-' || key == '_') {
        numPlayersRequested = max(2, numPlayersRequested - 1);
      } else if (key == '+' || key == '=') {
        numPlayersRequested = min(4, numPlayersRequested + 1);
      } else if (key == 'H' || key == 'h') {
        showHelp = !showHelp;
      } else if (key == 'M' || key == 'm') {
        muted = !muted;
      } else if (key == 'Q' || key == 'q') {
        exit();
      }
    }
  } else {
    if (players.size() >= 0) {
      //\u2193 Player 0 Controls
      if (key == 'W' || key == 'w')players.get(0).setRotation(0);
      else if (key == 'A' || key == 'a')players.get(0).setRotation(-PI/2);
      else if (key == 'S' || key == 's')players.get(0).setRotation(PI);
      else if (key == 'D' || key == 'd')players.get(0).setRotation(PI/2);
    }

    if (players.size() > 1 && key == CODED) {
      //\u2193 Player 1 Controls
      if (keyCode == UP)players.get(1).setRotation(0);
      else if (keyCode == LEFT)players.get(1).setRotation(-PI/2);
      else if (keyCode == DOWN)players.get(1).setRotation(PI);
      else if (keyCode == RIGHT)players.get(1).setRotation(PI/2);
    }

    if (players.size() > 2) {
      //\u2193 Player 2 Controls
      if (key == 'I' || key == 'i')players.get(2).setRotation(0);
      else if (key == 'J' || key == 'j')players.get(2).setRotation(-PI/2);
      else if (key == 'K' || key == 'k')players.get(2).setRotation(PI);
      else if (key == 'L' || key == 'l')players.get(2).setRotation(PI/2);
    }
    if (players.size() > 3) {
      //\u2193 Player 3 Controls
      if (key == '8')players.get(3).setRotation(0);
      else if (key == '4')players.get(3).setRotation(-PI/2);
      else if (key == '5')players.get(3).setRotation(PI);
      else if (key == '6')players.get(3).setRotation(PI/2);
    }
  }
}
/*
  The about screen for the game. Displays why the game was made
*/

public class AboutScreen extends GameScreen{
  public AboutScreen(){
    super("[A] About");
  }
  
  protected void customRender(){
    textSize(25);
    rectMode(CORNER);
    textAlign(LEFT, TOP);
    text("Energy Rush\nby Nazaire Shabazz\n----------------------\nThis game was made in Processing for my first-year Intro to Algebra and Programming class." +
      " The project was to just 'make anything', but instead of just churning out a crap program, I opted to make a game!\n", width*0.2f, height*0.25f, width*0.6f, height*2);
  }
}
/*
  This is the AfterImage class. AfterImages are Entities created by Players.
  They gives the illusion of following the player by fading with time, then being revived at the players position.
  The effect is most apparent with more AfterImages
*/

public class AfterImage extends Entity {

  /*
    When initialized, an AfterImage needs to know it's position, rotation, size, and color.
    Of course, when the AfterImage is first born, it is not dead. So isDead is set to false by default
  */
  public AfterImage(float x, float y, float rotation, float size, int hexColor) {
    this.positionX = x;
    this.positionY = y;
    this.rotation = rotation;
    this.size = size;
    this.trueHexColor = this.hexColor = lerpColor(hexColor, 0xffffffff, 0.25f);
    this.opacity = 200;
    dead = false;
  }
  
  //\u2193 This update method overrides the Entity update method
  public void update() {
    //\u2193 Lower the opacity by 2.5 (an arbitrary value)
    opacity -= 2.5f;
    
    //\u2193 If the opacity is 0, then the AfterImage is dead
    if (opacity < 0)
      dead = true; //\u2190 This AfterImage is dead
  }

  //\u2193 This render method overloads the Entity render method
  public void render() {
    //\u2193 Start a matrix - kind of like opening a new grid paper to draw on. Everything between the pushMatrix() and popMatrix() will be on the new grid
    pushMatrix();
      noStroke();
      translate(positionX, positionY); //\u2190 Move the whole grid over by positionX and positionY
      rotate(rotation); //\u2190 Rotate the whole grid by rotation (which is in rads)
      fill(trueHexColor, min(100, opacity));
      triangle(0, -size, size, size, -size, size);
    popMatrix();
  }

  //\u2193 getter method for 'dead' state
  public boolean isDead() {
    return dead;
  }

  //\u2193 This method "revives" the AfterImage. In order to revive, it needs to know it's position and rotation
  public void revive(float x, float y, float rotation) {
    this.positionX = x;
    this.positionY = y;
    this.rotation = rotation;

    opacity = 200; //\u2190 Reset opacity to 200
    dead = false; //\u2190 Reset dead state to false
  }
}
/*
  The controls screen for the game. Displays the game's controls (which are static)
*/

public class ControlsScreen extends GameScreen{
  public ControlsScreen(){
    super("[C] Controls");
  }
  
  protected void customRender(){
    textSize(30);
    rectMode(CORNER);
    textAlign(LEFT, TOP);
    text("\nP1: W/A/S/D\nP2: \u2191/\u2193/\u2190/\u2192 (Use Arrow Keys)\nP3: I/J/K/L\nP4: 8/4/5/6 (Use NumPad)", width*0.2f, height*0.25f, width*0.6f, height*2);
  }
}
/*
  This is the DyingCircle class. When a player dies, a circle is made that explodes outwards. Doesn't do anything, just shows death
*/
public class DyingCircle extends Entity{
  
  /*
    Needs a passed size rather than an arbitrary value. Also needs color of it's Player
  */
  public DyingCircle(float size, int hexColor){
    super();
    this.size = size;
    this.hexColor = hexColor;
  }
  
  //\u2193 This update method overloads the Entity update method
  public void update(){
    size += 5;
    if(opacity > 0)
      opacity -= 10;
  }
  //\u2193 This render method overloads the Entity render method
  public void render(){
    ellipseMode(CENTER);
    fill(lerpColor(hexColor, 0xff000000, 0.1f), opacity);
    ellipse(positionX, positionY, size, size);
  }
  //\u2193 Needs a float X, float Y, and radius
  public void show(float x, float y, float r){
    positionX = x;
    positionY = y;
    size = r;
    opacity = 255;
  }
}
/*
  This is the Energy class. Energy can be 'taken' by players.
  In reality, when a player collides with an Energy, the Energy moves to a random location and reinitializes
*/

public class Energy extends Entity {
  PlusOne plusOne;
  boolean taken; //\u2190 The taken state of the Energy. If an Energy is taken, it will be hidden until shown() is run

  /*
    When initialized, Energy will take in the hexColor and the player number of the player it belongs to
    Positions are set to a ranfom value with a 50px padding from the edges of the screen
    Since it is just created, the Energy will not have been taken
    Size is set to 20, arbitrarily
  */
  public Energy(int hexColor) {
    super();
    this.hexColor = hexColor;
    
    plusOne = new PlusOne(hexColor);
    plusOne.hide();
    positionX = random(50, width - 50);
    positionY = random(50, height - 50);
    taken = false;
    size = 20;
  }
  
  public void update(){
    baseUpdate();
    plusOne.update();
  }
  //\u2193 This render method overloads the Entity render method
  public void render() {
    fill(trueHexColor, opacity);
    ellipseMode(CENTER);
    ellipse(positionX, positionY, size, size);
    
    plusOne.render();
  }

  //\u2193 Hide function to be used whenever this Energy is 'taken' by it's Player
  public void hide() {
    plusOne.show(positionX, positionY);
    positionX = positionY = -100; //\u2190 Just set the position to somewhere off-screen to hide it.
    taken = true; //\u2190 This Energy is now taken
  }

  //\u2193 Show method will reveal the Energy. Use when energy is taken and needs to be revitalized
  public void show() {
    positionX = random(50, width - 50); //\u2190 Set position X to a random place (with 50px padding from border
    positionY = random(50, height - 50); //\u2190 Set position Y to a random place (with 50px padding from border
    initialized = spawned = taken = false; //\u2190 This Energy is no longer initialized or spawned or taken
    initTime = spawnTime = millis(); //\u2190 Set the init time and spawn time to now
    plusOne.hide();
  }

  //\u2193 Setter method for 'taken' state
  public void setTaken(boolean isTaken) {
    this.taken = isTaken;
  }

  //\u2193 Getter method for 'taken' state
  public boolean getTaken() {
    return taken;
  }
}
/*
  This is the Entity class. Entitys (yes, Entitys) will be used for all interactive game objects. This is a base class for other objects to build off of.
  Entitys blink and are immobile while initializing. Entitys become mobile, and fade down from white when spawned
*/

public class Entity{
  int hexColor; //\u2190 The Entity's assigned color
  int trueHexColor; //\u2190 The true color the Entity will be displayed with
  float opacity; //\u2190 Opacity of Entity
  protected float positionX, positionY, velocityX, velocityY, size, rotation; //\u2190 X position, Y position, X velocity, Y velocity, rotation and size of Entity
  protected float initTime; //\u2190 Time passed since starting Entity
  protected float spawnTime; //\u2190 Time passed since initializing Entity
  protected boolean initialized, spawned; //\u2190 The initialized and spawned states of the Entity. Entitys cannot be spawned without being initialized.
  protected float blinkTime, initBlinkTime, spawnFadeTime; //\u2190 How long it takes to blink when initializing, how long it takes for initialization of an Entity, and how long it takes for an Entity to truly spawn
  protected boolean dead; //\u2190 Whether or not an Entity is dead. Not much use in base class
  

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
    trueHexColor = 0xffffffff;
    hexColor = 0xffffffff;
    opacity = 255;
    initBlinkTime = 1500;
    spawnFadeTime = 500;
    dead = false;
  }
  
  //\u2193 The step function calls update and render, and should be called every frame
  public void step(){
    update();
    render();
  }
  //\u2193 The base update method of the Entity. Does the blinking and fade in of the Entity, which is optional.
  public void baseUpdate(){
    //\u2193 If the Entity is not initialized, it cannot have been spawned
    if(!initialized)
      spawned = false;
      
    //\u2193 If the Entity is not yet initialized, and there's a 250 millisecond difference between the time now and blinkTime, then blink
    if(!initialized && millis() - blinkTime > 250){
      //\u2193 Set trueHexColor to hexColor
      trueHexColor = hexColor;
      
      //\u2193 Switch opacity from 0 to 255 or vice versa
      if(opacity == 255)
        opacity = 0;
      else
        opacity = 255;
        
      //\u2193 Set blinkTime to now so that another 250 milliseconds will have to pass to blink again
      blinkTime = millis();
    }
    
    //\u2193 If the Entity is not yet initialized and the prerequisite amount of time has passed for the object to initialize, then initialize it
    if(!initialized && millis() - initTime > initBlinkTime){
      initialized = true; //\u2190 The Entity is now initialized
      
      //\u2193 Set opacity to fully opaque just in case
      opacity = 255;
      
      //\u2193 Set spawnTime to now
      spawnTime = millis();
    }
    
    //\u2193 If the Entity is initialized, but not yet spawned, then linearly interpolate the true color of the Entity from white to it's preset color, based on how much time has passed since initialization
    if(initialized && !spawned)
      trueHexColor = lerpColor(0xffffffff, hexColor, (millis() - spawnTime)/spawnFadeTime);
    
    //\u2193 If the Entity is initialized, and enough time has passed to spawn, but it is not yet spawned, then spawn it
    if(initialized && millis() - spawnTime > spawnFadeTime && !spawned){
      //\u2193 Set true color of Entity to it's preset color
      trueHexColor = hexColor;
      spawned = true; //\u2190 The Entity is now officially spawned
    }
      
  }
  //\u2193 This is the template update method to be overriden in the child classes. Use for updating variables and the like
  public void update(){
    
  }
  //\u2193 This is the template render method to be overriden in the child classes. Use for draw calls and the like
  public void render(){
    
  }
  
  //\u2193 Getter for positionX
  public float getPositionX(){
    return positionX;
  }
  //\u2193 Getter for positionY
  public float getPositionY(){
    return positionY;
  }
  //\u2193 Getter for velocityX
  public float getVelocityX(){
    return velocityX;
  }
  //\u2193 Getter for velocityY
  public float getVelocityY(){
    return velocityY;
  }
  //\u2193 Getter for size
  public float getSize(){
    return size;
  }
  //\u2193 Getter for 'initialized' state
  public boolean getInitialized(){
    return initialized;
  }
  //\u2193 Getter for 'spawned' state
  public boolean getSpawned(){
    return spawned;
  }
  //\u2193 Getter for color
  public int getHexColor(){
    return hexColor;
  }
  //\u2193 Getter for true color
  public int getTrueHexColor(){
    return trueHexColor;
  }
  //\u2193 Getter for opacity
  public float getOpacity(){
    return opacity;
  }
  //\u2193 Getter for 'dead' state
  public boolean getDead(){
    return dead;
  }
  
  //\u2193 Setter for 'initialized' state
  public void setInitialized(boolean isInitialized){
    initialized = isInitialized;
  }
  //\u2193 Setter for 'spawned' state
  public void setSpawned(boolean isSpawned){
    spawned = isSpawned;
  }
  //\u2193 Setter for 'dead' state
  public void setDead(boolean dead){
    this.dead = dead;
  }
  //\u2193 Setter for rotation value
  public void setRotation(float rotation){
    this.rotation = rotation;
  }
  
  //\u2193 Public method for Entity-Entity collision
  public boolean collidesWith(Entity e){
    return pow(e.getPositionX() - positionX, 2) + pow(e.getPositionY() - positionY, 2) < pow(e.getSize()/2 + size, 2);
  }
}
/*
  The explanation screen for the game. Displays how to play
*/

public class ExplanationScreen extends GameScreen{
  public ExplanationScreen(){
    super("[E] Explanation");
  }
  
  protected void customRender(){
    textSize(25);
    rectMode(CORNER);
    textAlign(LEFT, TOP);
    text("Welcome to Energy Rush!\n Win by getting the most Energy. Gain Energy by killing other Players or collecting Energy drops." +
      " Colliding with a Player or their AfterImages usually means death, but in a Player-Player collision, if you have more energy, you live!" +
      " Also, you lose energy over time, so keep up the pace!", width*0.2f, height*0.25f, width*0.6f, height*2);
  }
}
/*
  This is the base class for screens in the game. Use this whenever you are making a new screen
*/

public class GameScreen{
  private String title; //\u2190 The title of the GameScreen. Displayed at the top of the GameScreen in large noticeable letters
  
  /*
    When initialized, will take in the title of the screen as a paremeter
    Titles are displayed at the top of the GameScreen
  */
  public GameScreen(String title){
    this.title= title;
  }
  
  //\u2193 Make a transparent black box, then make an opaque grey box above, then write the title text at the top, followed by whatever custom text the subclass may have
  public void render(){
    rectMode(CORNER);
    
    fill(0, 100);
    rect(0, 0, width, height);
    
    rectMode(CENTER);
    
    fill(150, 255);
    strokeWeight(5);
    stroke(255);
    rect(width*0.5f, height*0.5f, width*0.8f, height*0.8f);
    
    textAlign(LEFT, TOP);
    
    textSize(50);
    fill(255);
    text(title, width*0.15f, height*0.15f);
    
    customRender();
  }
  
  //\u2193 To be overrridden by child classes
  protected void customRender(){
    
  }
}
/*
  The help screen for the game. Leads to other helpful screens
*/

public class HelpScreen extends GameScreen{
  public HelpScreen(){
    super("[H] Help");
  }
  
  protected void customRender(){
    textSize(30);
    rectMode(CORNER);
    textAlign(LEFT, TOP);
    text("\n[E] Explanation\n[C] Controls\n[A] About", width*0.2f, height*0.25f, width*0.6f, height*2);
  }
}
/*
  The pause screen for the game. Gives basic game options, and leads to the Help Page
*/

public class PauseScreen extends GameScreen{
  public PauseScreen(){
    super("[P] Pause Menu");
  }
  
  protected void customRender(){
    textSize(30);
    textAlign(LEFT, CENTER);
    text(muted ? "\nNum Players: " + numPlayersRequested + "\n[R] Restart\n[-] More Players (4 max)\n[+] Less Players (2 min)\n[M] unMute\n[Q] Quit":
      "\nNum Players: " + numPlayersRequested + "\n----------------------\n[R] Restart\n[+] More Players (4 max)\n[-] Less Players (2 min)\n[H] Help\n[Q] Quit", width*0.2f, height*0.5f);
  }
}
public class Player extends Entity {
  Energy energy;
  int numKills, totalEnergyCollected;
  float originalX, originalY;
  int playerNum; //\u2190 The number of this Player
  int numLives; //\u2190 How many times numLives the Player currently has
  int energyCollected; //\u2190 Energy Collected. Affects speed and score
  int direction; //\u2190 Up(1), Right(2), Down(3), Left(4)
  int speedLimit;
  float timeSinceLastAfterImage, timeSinceLastEnergyCollection, timeSinceLastEnergyLoss; //\u2190 time since last AfterImage was made, time since last Energy collection, and time since last Energy loss
  ArrayList<AfterImage> afterImages;
  DyingCircle dCircle;

  public Player(int playerNum) {
    super();
    this.playerNum = playerNum;
    timeSinceLastAfterImage = millis()*1.2f;
    timeSinceLastEnergyCollection = timeSinceLastEnergyLoss = millis();
    numLives = 3;
    energyCollected = 3;
    direction = playerNum;
    rotation = 0;
    positionX = width*0.5f;
    positionY = height*0.5f;
    size = 15;
    dead = false;

    switch(playerNum) {
    case 0:
      originalX = width*0.5f;
      originalY = height * 0.85f;
      hexColor = 0xff0066ff;
      break;
    case 1:
      originalX = width * 0.15f;
      originalY = height*0.5f;
      hexColor = 0xffff66ff;
      break;
    case 2:
      originalX = width*0.5f;
      originalY = height * 0.15f;
      hexColor = 0xffff6600;
      break;
    case 3:
      originalX = width * 0.85f;
      originalY = height*0.5f;
      hexColor = 0xffdddd00;
      break;
    }
    energy = new Energy(hexColor);

    afterImages = new ArrayList<AfterImage>();
    dCircle = new DyingCircle(size, hexColor);

    numLives++;
    die();
    energy.show();
    
  }

  //\u2193 This step method overrides the Entity step method
  public void step() {
    if(!initialized && dCircle.getOpacity() > 0)
      dCircle.step();
    //\u2193 Will only render if the Player has a life. Sorry if you're an outcast, Player.
    if (numLives > 0) {
      update();
      render();
    }
  }

  //\u2193 This render method overrides the Entity render method
  public void update() {
    baseUpdate();

    if (initialized) {
      float speed = min(10, 1 + energyCollected) + energyCollected/25f;

      if (millis() - timeSinceLastAfterImage > 150/speed) {
        //\u2193 Just add a new AfterImage if there's a difference betwen the number of AfterImages the player has, and the number of Energy the player has collected
        if (afterImages.size() < energyCollected)
          afterImages.add(new AfterImage(positionX, positionY, rotation, size, hexColor));
        else {
          AfterImage aI = afterImages.get(0);

          for (AfterImage a : afterImages)
            if (a.getOpacity() < aI.getOpacity())
              aI = a;

          aI.revive(positionX, positionY, rotation);
        }

        timeSinceLastAfterImage = millis();
      }

      //\u2193 If the Player hasn't picked up Energy in 4 seconds and has not lost energy in two (2) seconds, lose one energy
      if (energyCollected > 3 && afterImages.size() > 0 && millis() - timeSinceLastEnergyCollection > 4000 && millis() - timeSinceLastEnergyLoss > 2000) {
        afterImages.remove(afterImages.size()-1);
        energyCollected--;
        timeSinceLastEnergyLoss = millis();
      }

      //\u2193 Move based on the Player is facing; (I could do this in one line with cos or sin, but no)
      if (rotation < -0.5f)
        positionX -= speed;
      else if (rotation > -0.5f && rotation < 0.5f)
        positionY -= speed;
      else if (rotation < 2)
        positionX += speed;
      else
        positionY += speed;

      //\u2193 Handles wrapping of Player around screen
      if (positionX < -(size- 10))
        positionX = width + (size- 10);
      if (positionX > width + (size- 10))
        positionX = -(size- 10);
      if (positionY < -(size- 10))
        positionY = height + (size- 10);
      if (positionY > height + (size- 10))
        positionY = -(size- 10);

      //\u2193 Update each AfterImage the Player has
      for (int i = afterImages.size () - 1; i >= 0; i--) {
        AfterImage a = afterImages.get(i);
        a.update();
      }

      energy.update();

      if (energy.getInitialized() && collidesWith(energy)) {
        addEnergy();
        energy.hide();
        energy.show();
      }
    }
  }

  //\u2193 This render method overrides the Entity render method
  public void render() {
    energy.render();

    //\u2193Render each AfterImage first
    for (AfterImage a : afterImages)
      a.render();

    //\u2193 Since this is a complex object (made of more than one shape), we'll be using a matrix.
    pushMatrix();
    noStroke();
    translate(positionX, positionY);
    rotate(rotation);
    fill(trueHexColor, opacity);
    triangle(0, -size, size, size, -size, size);
    fill(0xffffffff, opacity);
    triangle(0, -size + size/3, size/3, size - 3*size/3, -size/3, size - 3*size/3);
    popMatrix();
  }

  //\u2193 Dying just means you lose a life and get your everything reset to it's original state. Good luck in the next life! ^_^
  public void die() {
    dCircle.show(positionX, positionY, size);
    numLives--; //\u2190 This Player has lost a life

    //\u2193 Reset the positions to the original positions
    positionX = originalX;
    positionY = originalY;

    //\u2193 Reset spawned and initialized to false, and reset the timers to now;
    initialized = spawned = false;
    initTime = spawnTime = millis();

    //\u2193 Set the correct rotation based on the player number of the Player
    switch(playerNum) {
    case 0:
      rotation = 0;
      break;
    case 1:
      rotation = PI/2;
      break;
    case 2:
      rotation = PI;
      break;
    case 3:
      rotation = -PI/2;
      break;
    }

    energyCollected = 3; //\u2190 reset energyCollected to 3
    //\u2193 Remove all but 3(three) afterImages
    timeSinceLastEnergyCollection = timeSinceLastEnergyLoss = millis();
    for (int i = afterImages.size () - 1; i >= 3; i--)
      afterImages.remove(i);
    
  }

  //\u2193 Returns current Energy held by Player
  public int getEnergy() {
    return energyCollected;
  }
  //\u2193 Returns total Energy Player has
  public int getTotalEnergy() {
    return totalEnergyCollected;
  }
  //\u2193 Returns number of numLives Player has left
  public int getLives() {
    return numLives;
  }
  //\u2193 Return number of kills Player has earned
  public int getKills() {
    return numKills;
  }
  //\u2193 Returns number of this Player
  public int getPlayerNum() {
    return playerNum;
  }
  //\u2193 Returns the array of AfterImages this Player has
  public ArrayList<AfterImage> getAfterImages() {
    return afterImages;
  }
  //\u2193 Returns whether or not the Player can live
  public boolean getCanLive() {
    return numLives > 0 && spawned;
  }
  //\u2193 Returns this Player's 'dead' state
  public boolean getDead() {
    return dead;
  }

  //\u2193 Increments the kill score of the Player by one, and adds energy gained to totalEnergy
  public void addKill(int energyGained) {
    numKills++;
    totalEnergyCollected += energyGained;
  }
  //\u2193 Adds one (1) Energy to the Player's count
  public void addEnergy() {
    energyCollected++;
    totalEnergyCollected++;
    timeSinceLastEnergyCollection = millis();
  }

  //\u2193 Returns whether or not another Player collides with this Player's AfterImages
  public boolean collidesWithAfterImages(Player p) {
    for (AfterImage afterImage : afterImages)
      if (afterImage.collidesWith(p))
        return true;

    return false;
  }
}
/*
  PlusOnes appear whenever an Energy is taken. Each Energy should have a PlusOne object
*/

public class PlusOne extends Entity{
  
  /*
    The constructor takes in the color of it's Player (from the Energy). That's all that's needed.
    Since no plusOnes are taken at the beginning of the game, just hide it off-screen first
  */
  public PlusOne(int hexColor){
    super();
    this.trueHexColor = this.hexColor = hexColor;
    opacity = 255;
    positionX = positionY = -100;
  }
  
  //\u2193 This update method overloads the Entity update method
  public void update(){
    opacity = max(0, opacity - 5); //\u2190 Reduce opacity by 5 (arbitrary)
    positionY -= 1; //\u2190 Decrease (raise) the Y position of the plusone
    //\u2191 All this in total gives the PlusOne the effect of fading up and away
  }
  
  //\u2193 This render method overloads the Entity render method
  public void render(){
    //\u2193 Only render if there's anything to display in the first place
    if(opacity > 0){
      fill(trueHexColor, opacity);
      textAlign(CENTER, CENTER);
      textSize(25);
      text("+1", positionX, positionY);
    }
  }
  
  //\u2193 To be called whenever the repective Player takes their Energy.
  public void show(float x, float y){
    this.positionX = x;
    this.positionY = y;
    opacity = 255;
  }
  
  //\u2193 Hides the PlusOne
  public void hide(){
    this.positionX = this.positionY = -100;
  }
}


/*
  The result screen for the game. Displays the winning players in order of highest to lowest total energy collected
*/

public class ResultScreen extends GameScreen{
  public ResultScreen(){
    super("VICTORY");
  }
  
  protected void customRender(){
    //\u2193 Make a new player array from the current player array
    ArrayList<Player> sortArray = new ArrayList<Player>(players);
    
    /*
      Sort the player array by searching through each item while already searching through each item, comparing the second search's item's value to the first search's item's value
      If the second search's item's value is greater, then swap the items' indices
    */
    for(int i = 0; i < sortArray.size(); i++)
      for(int ii = i + 1; ii < sortArray.size(); ii++)
        if(sortArray.get(i).getTotalEnergy() < sortArray.get(ii).getTotalEnergy())
          Collections.swap(sortArray, i, ii);
    
    rectMode(CENTER);
    textAlign(CENTER, CENTER);
    
   /*
     Print each item in the sorted array, one below the other, with font size decreasing to further show heirarchy
   */
  for(int i = 0; i < sortArray.size(); i++){
  textSize(65 - 10*i);   
    fill(sortArray.get(i).getHexColor(), 255);
    text("P" + (i + 1) + ": " + sortArray.get(i).getTotalEnergy(), width*0.5f, (i+1f)/(sortArray.size() + 1)*(height*0.6f) + height*0.25f);
  }
    
    textSize(20);
    fill(255, 150);
    textAlign(RIGHT, TOP);
    text("Press [R] to Restart", width - 15, 15);
  }
}
/*
  This is the interface displayed while playing the game.
  Handles drawing the player energy as well as the player's remaining lives
  Also draws player kills and their total energy collected at the bottom of the screen
*/

public class UserInterface {
  float lifeSize; //\u2190 How big the display of the Player's remaining lives will be
  boolean[] topPlayers = {false, false, false, false}; //\u2190 The top-player statuses of each Player
  
  public UserInterface() {
    lifeSize = (players.get(0).getSize() / players.get(0).getLives());
  }
  
  public void render() {
    drawPlayerStuff();
    drawPlayerStats();
  
    textSize(20);
    fill(255, 150);
    textAlign(RIGHT, TOP);
    text("Press [P] to Pause", width - 15, 15);
  }
  
  //\u2193 Draws the Players' current lives and energy above and below them.
  public void drawPlayerStuff() {
    textAlign(CENTER, CENTER);
    for (Player p : players) {
      if (p.getLives() > 0) {
        fill(p.getHexColor(), 255);
        textSize(20);
        for (int i = 0; i < p.getLives (); i++) {
          ellipse((lifeSize + 20)*((float)i/p.getLives()) + (p.getPositionX() - p.getSize()*0.5f), p.getPositionY() -  p.getSize()*2, lifeSize, lifeSize);
        }
        text("(" + p.getEnergy() + ")", p.getPositionX(), p.getPositionY() + p.getSize()*2);
      }
    }
  }
  
  public void drawPlayerStats() {
    int topScore = players.get(0).getTotalEnergy();
    
    //\u2193 Go through each PPlayer to find the top score so far
    for (int i = 1; i < players.size(); i++) {
      if (topScore < players.get(i).getTotalEnergy())
        topScore = players.get(i).getTotalEnergy();
    }
    
    /*
      Compare each player to see if others share the top score.
      Set the top-player status of each player accordingly.
    */
    for(int i = 0; i < players.size(); i++){
      if(players.get(i).getTotalEnergy() == topScore)
        topPlayers[i] = true;
      else
        topPlayers[i] = false;
    }
  
    textAlign(CENTER, BOTTOM);
    
    /*
      Go through each Player, printing their score and kills side by side.
      If a player is a top scorer, then make their text's font size a bit bigger
    */
    for (int i = 0; i < players.size (); i++) {
      fill(players.get(i).getHexColor());
      
      if (topPlayers[i])
        textSize(35);
      else
        textSize(30);
  
      text("P" + (i + 1) + "\n" + players.get(i).getKills() + " (" + players.get(i).getTotalEnergy() + ")", (((float)i + 1)/(players.size() + 1))*width, height);
    }
  }
  
  //\u2193 Resets the lifeSize and topPlayer statuses
  public void reset(){
    lifeSize = (players.get(0).getSize() / players.get(0).getLives()); //\u2190 Just to be sure
    
    //\u2193 Set the status of each player to not-a-top-player by default
    for(boolean b:topPlayers)
      b = false;
  }
}
  public void settings() {  size(800, 600); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Energy_Rush" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
