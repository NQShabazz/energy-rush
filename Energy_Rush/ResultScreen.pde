import java.util.Collections;

/*
  The result screen for the game. Displays the winning players in order of highest to lowest total energy collected
*/

public class ResultScreen extends GameScreen{
  public ResultScreen(){
    super("VICTORY");
  }
  
  protected void customRender(){
    //↓ Make a new player array from the current player array
    ArrayList<Player> sortArray = new ArrayList<Player>(players);
    
    /*
      Sort the player array by searching through each item while already searching through each item, comparing the second search's item's value to the first search's item's value
      If the second search's item's value is greater, then swap the items' indices
    */
    for(int i = 0; i < sortArray.size(); i++)
      for(int ii = i + 1; ii < sortArray.size(); ii++)
        if(sortArray.get(i).getTotalEnergy() < sortArray.get(ii).getTotalEnergy())
          Collections.swap(sortArray, i, ii);
    
    rectMode(CENTER);
    textAlign(CENTER, CENTER);
    
   /*
     Print each item in the sorted array, one below the other, with font size decreasing to further show heirarchy
   */
  for(int i = 0; i < sortArray.size(); i++){
  textSize(65 - 10*i);   
    fill(sortArray.get(i).getHexColor(), 255);
    text("P" + (i + 1) + ": " + sortArray.get(i).getTotalEnergy(), width*0.5, (i+1f)/(sortArray.size() + 1)*(height*0.6) + height*0.25);
  }
    
    textSize(20);
    fill(255, 150);
    textAlign(RIGHT, TOP);
    text("Press [R] to Restart", width - 15, 15);
  }
}
