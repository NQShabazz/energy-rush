/*
  This is the DyingCircle class. When a player dies, a circle is made that explodes outwards. Doesn't do anything, just shows death
*/
public class DyingCircle extends Entity{
  
  /*
    Needs a passed size rather than an arbitrary value. Also needs color of it's Player
  */
  public DyingCircle(float size, color hexColor){
    super();
    this.size = size;
    this.hexColor = hexColor;
  }
  
  //↓ This update method overloads the Entity update method
  public void update(){
    size += 5;
    if(opacity > 0)
      opacity -= 10;
  }
  //↓ This render method overloads the Entity render method
  public void render(){
    ellipseMode(CENTER);
    fill(lerpColor(hexColor, #000000, 0.1), opacity);
    ellipse(positionX, positionY, size, size);
  }
  //↓ Needs a float X, float Y, and radius
  public void show(float x, float y, float r){
    positionX = x;
    positionY = y;
    size = r;
    opacity = 255;
  }
}
