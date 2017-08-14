/*
  PlusOnes appear whenever an Energy is taken. Each Energy should have a PlusOne object
*/

public class PlusOne extends Entity{
  
  /*
    The constructor takes in the color of it's Player (from the Energy). That's all that's needed.
    Since no plusOnes are taken at the beginning of the game, just hide it off-screen first
  */
  public PlusOne(color hexColor){
    super();
    this.trueHexColor = this.hexColor = hexColor;
    opacity = 255;
    positionX = positionY = -100;
  }
  
  //↓ This update method overloads the Entity update method
  public void update(){
    opacity = max(0, opacity - 5); //← Reduce opacity by 5 (arbitrary)
    positionY -= 1; //← Decrease (raise) the Y position of the plusone
    //↑ All this in total gives the PlusOne the effect of fading up and away
  }
  
  //↓ This render method overloads the Entity render method
  public void render(){
    //↓ Only render if there's anything to display in the first place
    if(opacity > 0){
      fill(trueHexColor, opacity);
      textAlign(CENTER, CENTER);
      textSize(25);
      text("+1", positionX, positionY);
    }
  }
  
  //↓ To be called whenever the repective Player takes their Energy.
  public void show(float x, float y){
    this.positionX = x;
    this.positionY = y;
    opacity = 255;
  }
  
  //↓ Hides the PlusOne
  public void hide(){
    this.positionX = this.positionY = -100;
  }
}
