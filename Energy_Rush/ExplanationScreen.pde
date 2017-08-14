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
      " Also, you lose energy over time, so keep up the pace!", width*0.2, height*0.25, width*0.6, height*2);
  }
}
