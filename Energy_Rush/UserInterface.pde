/*
  This is the interface displayed while playing the game.
  Handles drawing the player energy as well as the player's remaining lives
  Also draws player kills and their total energy collected at the bottom of the screen
*/

public class UserInterface {
  float lifeSize; //← How big the display of the Player's remaining lives will be
  boolean[] topPlayers = {false, false, false, false}; //← The top-player statuses of each Player
  
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
  
  //↓ Draws the Players' current lives and energy above and below them.
  void drawPlayerStuff() {
    textAlign(CENTER, CENTER);
    for (Player p : players) {
      if (p.getLives() > 0) {
        fill(p.getHexColor(), 255);
        textSize(20);
        for (int i = 0; i < p.getLives (); i++) {
          ellipse((lifeSize + 20)*((float)i/p.getLives()) + (p.getPositionX() - p.getSize()*0.5), p.getPositionY() -  p.getSize()*2, lifeSize, lifeSize);
        }
        text("(" + p.getEnergy() + ")", p.getPositionX(), p.getPositionY() + p.getSize()*2);
      }
    }
  }
  
  void drawPlayerStats() {
    int topScore = players.get(0).getTotalEnergy();
    
    //↓ Go through each PPlayer to find the top score so far
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
  
  //↓ Resets the lifeSize and topPlayer statuses
  void reset(){
    lifeSize = (players.get(0).getSize() / players.get(0).getLives()); //← Just to be sure
    
    //↓ Set the status of each player to not-a-top-player by default
    for(boolean b:topPlayers)
      b = false;
  }
}

