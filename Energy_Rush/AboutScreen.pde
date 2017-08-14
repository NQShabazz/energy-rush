/*
  The about screen for the game. Displays why the game was made
*/

public class AboutScreen extends GameScreen{
  public AboutScreen(){
    super("[A] About");
  }
  
  protected void customRender(){
    textSize(25);
    rectMode(CORNER);
    textAlign(LEFT, TOP);
    text("Energy Rush\nby Nazaire Shabazz\n----------------------\nThis game was made in Processing for my first-year Intro to Algebra and Programming class." +
      " The project was to just 'make anything', but instead of just churning out a crap program, I opted to make a game!\n", width*0.2, height*0.25, width*0.6, height*2);
  }
}
