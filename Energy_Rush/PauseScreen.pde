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
      "\nNum Players: " + numPlayersRequested + "\n----------------------\n[R] Restart\n[+] More Players (4 max)\n[-] Less Players (2 min)\n[H] Help\n[Q] Quit", width*0.2, height*0.5);
  }
}
