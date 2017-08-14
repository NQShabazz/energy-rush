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
  public AfterImage(float x, float y, float rotation, float size, color hexColor) {
    this.positionX = x;
    this.positionY = y;
    this.rotation = rotation;
    this.size = size;
    this.trueHexColor = this.hexColor = lerpColor(hexColor, #ffffff, 0.25);
    this.opacity = 200;
    dead = false;
  }
  
  //↓ This update method overrides the Entity update method
  void update() {
    //↓ Lower the opacity by 2.5 (an arbitrary value)
    opacity -= 2.5;
    
    //↓ If the opacity is 0, then the AfterImage is dead
    if (opacity < 0)
      dead = true; //← This AfterImage is dead
  }

  //↓ This render method overloads the Entity render method
  public void render() {
    //↓ Start a matrix - kind of like opening a new grid paper to draw on. Everything between the pushMatrix() and popMatrix() will be on the new grid
    pushMatrix();
      noStroke();
      translate(positionX, positionY); //← Move the whole grid over by positionX and positionY
      rotate(rotation); //← Rotate the whole grid by rotation (which is in rads)
      fill(trueHexColor, min(100, opacity));
      triangle(0, -size, size, size, -size, size);
    popMatrix();
  }

  //↓ getter method for 'dead' state
  public boolean isDead() {
    return dead;
  }

  //↓ This method "revives" the AfterImage. In order to revive, it needs to know it's position and rotation
  public void revive(float x, float y, float rotation) {
    this.positionX = x;
    this.positionY = y;
    this.rotation = rotation;

    opacity = 200; //← Reset opacity to 200
    dead = false; //← Reset dead state to false
  }
}

