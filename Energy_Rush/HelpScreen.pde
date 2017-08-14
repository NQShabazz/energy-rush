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
    text("\n[E] Explanation\n[C] Controls\n[A] About", width*0.2, height*0.25, width*0.6, height*2);
  }
}
