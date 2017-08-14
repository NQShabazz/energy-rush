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
    text("\nP1: W/A/S/D\nP2: ↑/↓/←/→ (Use Arrow Keys)\nP3: I/J/K/L\nP4: 8/4/5/6 (Use NumPad)", width*0.2, height*0.25, width*0.6, height*2);
  }
}
