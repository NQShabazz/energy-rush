//TODO - List:
//add sound T_T
//CircleExplosion on player death

ArrayList<Player> players;
Player p0, p1, p2, p3;
int numPlayersRequested, numPlayersAlive;
float[] timeSinceLastEnergySpawn = {
  1000, 1000, 1000, 1000
};
float timeToWaitForNextEnergySpawn = 500;

UserInterface UI;

PauseScreen PS;
HelpScreen HS;
ExplanationScreen ES;
ControlsScreen CS;
AboutScreen AS;
ResultScreen RS;
boolean showPaused, showHelp, showExplanation, showControls, showAbout, muted;
float timeSinceLastKill; //← Time since last kill. Used for the slow motion thing

void setup() {
  size(800, 600);
  surface.setTitle("Energy Rush");

  players = new ArrayList<Player>();

  p0 = new Player(0);
  players.add(p0);
  p1 = new Player(1);
  players.add(p1);
  p2 = new Player(2);
  players.add(p2);
  p3 = new Player(3);
  players.add(p3);

  UI = new UserInterface();
  PS = new PauseScreen();
  HS = new HelpScreen();
  ES = new ExplanationScreen();
  CS = new ControlsScreen();
  AS = new AboutScreen();
  RS = new ResultScreen();
  
  showPaused = true;
  showHelp = showExplanation = showControls = showAbout = muted = false;

  numPlayersAlive = numPlayersRequested = players.size();
  timeSinceLastKill = millis() - 1000;
}

void draw() {
  numPlayersAlive = 0;
  for (Player p : players)
    if (p.getLives() > 0)
      numPlayersAlive++;

  if (numPlayersAlive < 2) {
    RS.render();
  } else if (showPaused) {
    background(0);
    for (Player p : players)
      p.render();

    UI.render();

    if (showHelp) {
      if (showExplanation)
        ES.render();
      else if (showControls)
        CS.render();
      else if (showAbout)
        AS.render();
      else  
        HS.render();
    } else
      PS.render();
  } else {
    update();

    background(0);

    for (Player p : players)
      p.step();

    UI.render();
  }
}

void update() {
  for (int i = 0; i < players.size (); i++) {
    for (int ii = 0; ii < players.size (); ii++) {
      if (ii != i && players.get(ii).getCanLive() && players.get(i).getCanLive()) {
        if (players.get(i).collidesWith(players.get(ii))) {
          players.get(i).addKill(players.get(ii).getEnergy());
          players.get(ii).addKill(players.get(i).getEnergy());
          players.get(i).die();
          players.get(ii).die();
          timeSinceLastKill = millis();
          break;
        }
        for (AfterImage aI : players.get (ii).getAfterImages())
          if (players.get(i).collidesWith(aI)) {
            players.get(ii).addKill(players.get(i).getEnergy());
            players.get(i).die();
            timeSinceLastKill = millis();
            break;
          }
      }
    }
  }
  
  if(millis() - timeSinceLastKill < 1000)
    frameRate(20);
  else if(frameRate < 58)
    frameRate(60);
    
}

void restart() {
  players = new ArrayList<Player>();

  for (int i = 0; i < numPlayersRequested; i++)
    players.add(new Player(i));

  UI.reset();
  showPaused = showHelp = showExplanation = showControls = showAbout = false;
  numPlayersAlive = players.size();
}

void keyPressed() {
  if (key == ESC) {
    key = 0;
  } else if (numPlayersAlive < 2) {
    if (key == 'R' || key == 'r')
      restart();
  } else if (key == 'P' || key == 'p') {
    showPaused = !showPaused;

    if (!showPaused) {
      showHelp = showExplanation = showControls = showAbout = false;
      if (numPlayersRequested != players.size())
        restart();
    }
  } else if (showPaused) {
    if (showHelp) {
      if (!showControls && !showAbout && (key == 'E' || key == 'e')) {
        showExplanation = !showExplanation;
      } else if (!showAbout && !showExplanation && (key == 'C' || key == 'c')) {
        showControls = !showControls;
      } else if (!showExplanation && !showControls && (key == 'A' || key == 'a')) {
        showAbout = !showAbout;
      } else if (key == 'H' || key=='h')
        showHelp = showExplanation = showControls = showAbout = false;
    } else {
      if (key == 'R' || key == 'r') {
        restart();
      } else if (key == '-' || key == '_') {
        numPlayersRequested = max(2, numPlayersRequested - 1);
      } else if (key == '+' || key == '=') {
        numPlayersRequested = min(4, numPlayersRequested + 1);
      } else if (key == 'H' || key == 'h') {
        showHelp = !showHelp;
      } else if (key == 'M' || key == 'm') {
        muted = !muted;
      } else if (key == 'Q' || key == 'q') {
        exit();
      }
    }
  } else {
    if (players.size() >= 0) {
      //↓ Player 0 Controls
      if (key == 'W' || key == 'w')players.get(0).setRotation(0);
      else if (key == 'A' || key == 'a')players.get(0).setRotation(-PI/2);
      else if (key == 'S' || key == 's')players.get(0).setRotation(PI);
      else if (key == 'D' || key == 'd')players.get(0).setRotation(PI/2);
    }

    if (players.size() > 1 && key == CODED) {
      //↓ Player 1 Controls
      if (keyCode == UP)players.get(1).setRotation(0);
      else if (keyCode == LEFT)players.get(1).setRotation(-PI/2);
      else if (keyCode == DOWN)players.get(1).setRotation(PI);
      else if (keyCode == RIGHT)players.get(1).setRotation(PI/2);
    }

    if (players.size() > 2) {
      //↓ Player 2 Controls
      if (key == 'I' || key == 'i')players.get(2).setRotation(0);
      else if (key == 'J' || key == 'j')players.get(2).setRotation(-PI/2);
      else if (key == 'K' || key == 'k')players.get(2).setRotation(PI);
      else if (key == 'L' || key == 'l')players.get(2).setRotation(PI/2);
    }
    if (players.size() > 3) {
      //↓ Player 3 Controls
      if (key == '8')players.get(3).setRotation(0);
      else if (key == '4')players.get(3).setRotation(-PI/2);
      else if (key == '5')players.get(3).setRotation(PI);
      else if (key == '6')players.get(3).setRotation(PI/2);
    }
  }
}