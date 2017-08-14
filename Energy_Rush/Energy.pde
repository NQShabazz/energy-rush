/*
  This is the Energy class. Energy can be 'taken' by players.
  In reality, when a player collides with an Energy, the Energy moves to a random location and reinitializes
*/

public class Energy extends Entity {
  PlusOne plusOne;
  boolean taken; //← The taken state of the Energy. If an Energy is taken, it will be hidden until shown() is run

  /*
    When initialized, Energy will take in the hexColor and the player number of the player it belongs to
    Positions are set to a ranfom value with a 50px padding from the edges of the screen
    Since it is just created, the Energy will not have been taken
    Size is set to 20, arbitrarily
  */
  public Energy(color hexColor) {
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
  //↓ This render method overloads the Entity render method
  public void render() {
    fill(trueHexColor, opacity);
    ellipseMode(CENTER);
    ellipse(positionX, positionY, size, size);
    
    plusOne.render();
  }

  //↓ Hide function to be used whenever this Energy is 'taken' by it's Player
  public void hide() {
    plusOne.show(positionX, positionY);
    positionX = positionY = -100; //← Just set the position to somewhere off-screen to hide it.
    taken = true; //← This Energy is now taken
  }

  //↓ Show method will reveal the Energy. Use when energy is taken and needs to be revitalized
  public void show() {
    positionX = random(50, width - 50); //← Set position X to a random place (with 50px padding from border
    positionY = random(50, height - 50); //← Set position Y to a random place (with 50px padding from border
    initialized = spawned = taken = false; //← This Energy is no longer initialized or spawned or taken
    initTime = spawnTime = millis(); //← Set the init time and spawn time to now
    plusOne.hide();
  }

  //↓ Setter method for 'taken' state
  public void setTaken(boolean isTaken) {
    this.taken = isTaken;
  }

  //↓ Getter method for 'taken' state
  public boolean getTaken() {
    return taken;
  }
}

