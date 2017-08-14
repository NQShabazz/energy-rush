public class Player extends Entity {
  Energy energy;
  int numKills, totalEnergyCollected;
  float originalX, originalY;
  int playerNum; //← The number of this Player
  int numLives; //← How many times numLives the Player currently has
  int energyCollected; //← Energy Collected. Affects speed and score
  int direction; //← Up(1), Right(2), Down(3), Left(4)
  int speedLimit;
  float timeSinceLastAfterImage, timeSinceLastEnergyCollection, timeSinceLastEnergyLoss; //← time since last AfterImage was made, time since last Energy collection, and time since last Energy loss
  ArrayList<AfterImage> afterImages;
  DyingCircle dCircle;

  public Player(int playerNum) {
    super();
    this.playerNum = playerNum;
    timeSinceLastAfterImage = millis()*1.2;
    timeSinceLastEnergyCollection = timeSinceLastEnergyLoss = millis();
    numLives = 3;
    energyCollected = 3;
    direction = playerNum;
    rotation = 0;
    positionX = width*0.5;
    positionY = height*0.5;
    size = 15;
    dead = false;

    switch(playerNum) {
    case 0:
      originalX = width*0.5;
      originalY = height * 0.85;
      hexColor = #0066ff;
      break;
    case 1:
      originalX = width * 0.15;
      originalY = height*0.5;
      hexColor = #ff66ff;
      break;
    case 2:
      originalX = width*0.5;
      originalY = height * 0.15;
      hexColor = #ff6600;
      break;
    case 3:
      originalX = width * 0.85;
      originalY = height*0.5;
      hexColor = #dddd00;
      break;
    }
    energy = new Energy(hexColor);

    afterImages = new ArrayList<AfterImage>();
    dCircle = new DyingCircle(size, hexColor);

    numLives++;
    die();
    energy.show();
    
  }

  //↓ This step method overrides the Entity step method
  public void step() {
    if(!initialized && dCircle.getOpacity() > 0)
      dCircle.step();
    //↓ Will only render if the Player has a life. Sorry if you're an outcast, Player.
    if (numLives > 0) {
      update();
      render();
    }
  }

  //↓ This render method overrides the Entity render method
  void update() {
    baseUpdate();

    if (initialized) {
      float speed = min(10, 1 + energyCollected) + energyCollected/25f;

      if (millis() - timeSinceLastAfterImage > 150/speed) {
        //↓ Just add a new AfterImage if there's a difference betwen the number of AfterImages the player has, and the number of Energy the player has collected
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

      //↓ If the Player hasn't picked up Energy in 4 seconds and has not lost energy in two (2) seconds, lose one energy
      if (energyCollected > 3 && afterImages.size() > 0 && millis() - timeSinceLastEnergyCollection > 4000 && millis() - timeSinceLastEnergyLoss > 2000) {
        afterImages.remove(afterImages.size()-1);
        energyCollected--;
        timeSinceLastEnergyLoss = millis();
      }

      //↓ Move based on the Player is facing; (I could do this in one line with cos or sin, but no)
      if (rotation < -0.5)
        positionX -= speed;
      else if (rotation > -0.5 && rotation < 0.5)
        positionY -= speed;
      else if (rotation < 2)
        positionX += speed;
      else
        positionY += speed;

      //↓ Handles wrapping of Player around screen
      if (positionX < -(size- 10))
        positionX = width + (size- 10);
      if (positionX > width + (size- 10))
        positionX = -(size- 10);
      if (positionY < -(size- 10))
        positionY = height + (size- 10);
      if (positionY > height + (size- 10))
        positionY = -(size- 10);

      //↓ Update each AfterImage the Player has
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

  //↓ This render method overrides the Entity render method
  void render() {
    energy.render();

    //↓Render each AfterImage first
    for (AfterImage a : afterImages)
      a.render();

    //↓ Since this is a complex object (made of more than one shape), we'll be using a matrix.
    pushMatrix();
    noStroke();
    translate(positionX, positionY);
    rotate(rotation);
    fill(trueHexColor, opacity);
    triangle(0, -size, size, size, -size, size);
    fill(#ffffff, opacity);
    triangle(0, -size + size/3, size/3, size - 3*size/3, -size/3, size - 3*size/3);
    popMatrix();
  }

  //↓ Dying just means you lose a life and get your everything reset to it's original state. Good luck in the next life! ^_^
  public void die() {
    dCircle.show(positionX, positionY, size);
    numLives--; //← This Player has lost a life

    //↓ Reset the positions to the original positions
    positionX = originalX;
    positionY = originalY;

    //↓ Reset spawned and initialized to false, and reset the timers to now;
    initialized = spawned = false;
    initTime = spawnTime = millis();

    //↓ Set the correct rotation based on the player number of the Player
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

    energyCollected = 3; //← reset energyCollected to 3
    //↓ Remove all but 3(three) afterImages
    timeSinceLastEnergyCollection = timeSinceLastEnergyLoss = millis();
    for (int i = afterImages.size () - 1; i >= 3; i--)
      afterImages.remove(i);
    
  }

  //↓ Returns current Energy held by Player
  public int getEnergy() {
    return energyCollected;
  }
  //↓ Returns total Energy Player has
  public int getTotalEnergy() {
    return totalEnergyCollected;
  }
  //↓ Returns number of numLives Player has left
  public int getLives() {
    return numLives;
  }
  //↓ Return number of kills Player has earned
  public int getKills() {
    return numKills;
  }
  //↓ Returns number of this Player
  public int getPlayerNum() {
    return playerNum;
  }
  //↓ Returns the array of AfterImages this Player has
  public ArrayList<AfterImage> getAfterImages() {
    return afterImages;
  }
  //↓ Returns whether or not the Player can live
  public boolean getCanLive() {
    return numLives > 0 && spawned;
  }
  //↓ Returns this Player's 'dead' state
  public boolean getDead() {
    return dead;
  }

  //↓ Increments the kill score of the Player by one, and adds energy gained to totalEnergy
  public void addKill(int energyGained) {
    numKills++;
    totalEnergyCollected += energyGained;
  }
  //↓ Adds one (1) Energy to the Player's count
  public void addEnergy() {
    energyCollected++;
    totalEnergyCollected++;
    timeSinceLastEnergyCollection = millis();
  }

  //↓ Returns whether or not another Player collides with this Player's AfterImages
  public boolean collidesWithAfterImages(Player p) {
    for (AfterImage afterImage : afterImages)
      if (afterImage.collidesWith(p))
        return true;

    return false;
  }
}