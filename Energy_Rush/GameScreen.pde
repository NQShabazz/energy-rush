/*
  This is the base class for screens in the game. Use this whenever you are making a new screen
*/

public class GameScreen{
  private String title; //← The title of the GameScreen. Displayed at the top of the GameScreen in large noticeable letters
  
  /*
    When initialized, will take in the title of the screen as a paremeter
    Titles are displayed at the top of the GameScreen
  */
  public GameScreen(String title){
    this.title= title;
  }
  
  //↓ Make a transparent black box, then make an opaque grey box above, then write the title text at the top, followed by whatever custom text the subclass may have
  public void render(){
    rectMode(CORNER);
    
    fill(0, 100);
    rect(0, 0, width, height);
    
    rectMode(CENTER);
    
    fill(150, 255);
    strokeWeight(5);
    stroke(255);
    rect(width*0.5, height*0.5, width*0.8, height*0.8);
    
    textAlign(LEFT, TOP);
    
    textSize(50);
    fill(255);
    text(title, width*0.15, height*0.15);
    
    customRender();
  }
  
  //↓ To be overrridden by child classes
  protected void customRender(){
    
  }
}
