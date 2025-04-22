import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;

// class for the panel and game elements (mouse, keyboard, screen, offset of background, index of the enemy being fought, hovered button, player's actions in combat and in the pause menu, enemy's actions in combat and time)
class MedievalQuestPanel extends JPanel implements MouseMotionListener, MouseListener, KeyListener, ActionListener{
  Random rand = new Random();
  private int mx, my, mb, screen, offset, enemyInCombat, turn, hover, playerCombatAction, playerCombatActionAmount, playerMenuAction, enemyAction, enemyActionAmount;
  private double playerEndTurnTime, endCombatTime; // playerEndTurnTime for when the player ended their turn and endCombatTime for when combat ended
  private boolean []keys; // keyboard keys;
  private boolean []keysHeld; // list to check which keys are being held
  private static final int ESCAPE = 0, MENU = 1, INSTRUCTIONS_BUTTON = 2; // escape for escape key, menu for p key, instructions button for i key
  private Timer timer;
  private static final int GAME_OVER = -2, INSTRUCTIONS = -1, INTRO = 0, PLATFORMING = 1, COMBAT = 2, END = 3; // different screens
  private boolean pause, winCombat; // pause for when the game is paused and winCombat for when the player defeated the enemy
  private static final int playerTurn = 1, enemyTurn = -1; // player's turn and enemy's turn
  private PlayerPlatforming playerPlat;
  private PlayerCombat playerCom;
  private ArrayList<EnemyPlatforming> enemiesPlat;
  private ArrayList<EnemyCombat> enemiesCom;
  private Block []blocks;
  private Button []combatButtons, statButtons;
  private static final Image platformingBack, combatBack, instructionsBack, princess;
  private ActionLog aLog;

  static {
    Image pBack = null, cBack = null, iBack = null, prImg = null;
    try {
      pBack = ImageIO.read(MedievalQuestPanel.class.getResourceAsStream("Backgrounds/PlatformingBackground.png"));
      cBack = ImageIO.read(MedievalQuestPanel.class.getResourceAsStream("Backgrounds/CombatBackground.png"));
      iBack = ImageIO.read(MedievalQuestPanel.class.getResourceAsStream("Backgrounds/Instructions.png"));
      prImg = ImageIO.read(MedievalQuestPanel.class.getResourceAsStream("Princess.png"));
    } catch (Exception e) {
      e.printStackTrace();
    }
    platformingBack = pBack;
    combatBack = cBack;
    instructionsBack = iBack;
    princess = prImg;
  }
  
  public MedievalQuestPanel(){
    keys = new boolean[KeyEvent.KEY_LAST + 1];
    keysHeld = new boolean[3];
    setPreferredSize(new Dimension(800, 800));
    
    playerCombatAction = PlayerCombat.NOTHING;
    playerMenuAction = PlayerCombat.NOTHING;
    enemyAction = EnemyCombat.NOTHING;
    screen = INTRO;
    blocks = Block.make();
    combatButtons = Button.makeCombat();
    statButtons = Button.makeStat();
    pause = false;
    aLog = new ActionLog();
    
    setFocusable(true);
    requestFocus();
    addKeyListener(this);
    addMouseListener(this);
    timer = new Timer(10, this);
    timer.start();
  }
  
  public void start(){ // method for when a new game is started
    playerPlat = new PlayerPlatforming(KeyEvent.VK_A, KeyEvent.VK_D, KeyEvent.VK_W);
    playerCom = new PlayerCombat(KeyEvent.VK_A, KeyEvent.VK_D, KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_SPACE);
    enemiesPlat = EnemyPlatforming.make();
    enemiesCom = EnemyCombat.make();
  }
  
  public void move(){ // method for moving everything
    playerPlat.move(keys, blocks);
    for (EnemyPlatforming ep : enemiesPlat){
      ep.move(playerPlat);
    }
    if (playerPlat.checkFall(blocks)){ // checks if the player fell below the screen
      playerCom.decreaseHealth(1); // decreases the player's health by 1
      if (playerCom.getHp() == 0){ // checks if their health was reduced to 0
        playerCom.setHealth(1); // sets it to 1
      }
    }
    
    screen = playerPlat.getDist() > 5300 ? END : PLATFORMING; // if distance is more than 5300, sets the screen to END
    
    enemyInCombat = playerPlat.collideEnemy(enemiesPlat); // gets the index of the enemy collided with (-1 if none)
    if (enemyInCombat != -1){ // if colliding with an enemy
      if (playerPlat.getY() + playerPlat.getHeight() < enemiesPlat.get(enemyInCombat).getY() + 20){ // if player is above a certain part of the enemy the player gets an advantage
        playerCombatActionAmount = playerCom.getPw() - enemiesCom.get(enemyInCombat).getDf(); // ambush
        if (playerCombatActionAmount > 0){
          enemiesCom.get(enemyInCombat).decreaseHealth(playerCombatActionAmount);
          aLog.enqueue("You ambushed " + enemiesCom.get(enemyInCombat).getTy() + ", dealing " + playerCombatActionAmount + " damage!"); // adds action to the Action Log
        }
        else {
          aLog.enqueue("You ambushed " + enemiesCom.get(enemyInCombat).getTy() + ", but dealt no damage!"); // adds action to the Action Log
          
        }
        turn = playerTurn; // player always starts if they jumped on the enemy
      }
      else if (playerCom.getSp() == enemiesCom.get(enemyInCombat).getSp()){ // if speeds are equal
        turn = rand.nextInt(1);
      }
      else{ // speeds are not equal
        turn = playerCom.getSp() > enemiesCom.get(enemyInCombat).getSp() ? playerTurn : enemyTurn; // whoever has the highest speed starts
      }
      hover = 0;
      screen = COMBAT;
    }
  }
  
  public void combat(){ // method for fighting
    if (turn == playerTurn){
      playerCombatAction = playerCom.action(keys); // gets the player's action in combat
      
      if (playerCombatAction == PlayerCombat.LEFT){ // a key was pressed
        hover = hover - 1 < 0 ? 3 : hover - 1; // new hovered button is left
      }
      else if (playerCombatAction == PlayerCombat.RIGHT){ // d key was pressed
        hover = hover + 1 > 3 ? 0 : hover + 1; // new hovered button is right
      }
      else if (playerCombatAction == PlayerCombat.SELECT){ // space was pressed
        if (hover == PlayerCombat.ATTACK){ // if ATTACK was hovered
          if (rand.nextInt(100) > enemiesCom.get(enemyInCombat).getSp()){ // if random number is higher than enemy's speed
            playerCombatAction = PlayerCombat.ATTACK;
            playerCombatActionAmount = playerCom.getPw() * 2 / enemiesCom.get(enemyInCombat).getDf() * (100 + rand.nextInt(11) - 5) / 100; // damage calculation
            enemiesCom.get(enemyInCombat).decreaseHealth(playerCombatActionAmount);
            aLog.enqueue("You attacked, dealing " + playerCombatActionAmount + " damage!"); // adds action to the Action Log
          }
          else{ // random number was lower therefore missed
            playerCombatAction = PlayerCombat.MISSED;
          }
        }
        else if (hover == PlayerCombat.CHARGED_ATTACK){ // if CHARGED_ATTACK was hovered
          if (playerCom.getMana() >= 2){ // checks if player has enough mana
            playerCom.decreaseMana(2);
            if (rand.nextInt(100) > enemiesCom.get(enemyInCombat).getSp()){ // if random number is higher than enemy's speed
              playerCombatAction = PlayerCombat.CHARGED_ATTACK;
              playerCombatActionAmount = playerCom.getPw() * 5 / enemiesCom.get(enemyInCombat).getDf() * (100 + rand.nextInt(11) - 5) / 100; // damage calculation
              enemiesCom.get(enemyInCombat).decreaseHealth(playerCombatActionAmount);
              aLog.enqueue("You charged your attack with magic, dealing " + playerCombatActionAmount + " damage!"); // adds action to the Action Log
            }
            else{ // random number was lower therefore missed
              playerCombatAction = PlayerCombat.MISSED;
            }
          }
          else{ // not enough mana
            playerCombatAction = PlayerCombat.NOT_ENOUGH_MANA;
            aLog.enqueue("You didn't have enough mana!"); // adds action to the Action Log
          }
        }
        else if (hover == PlayerCombat.SHIELD){ // if SHIELD was hovered
          if (playerCom.getMana() >= 1){ // checks if player has enough mana
            playerCom.decreaseMana(1);
            playerCombatAction = PlayerCombat.SHIELD;
            aLog.enqueue("You braced yourself!"); // adds action to the Action Log
          }
          else{ // not enough mana
            playerCombatAction = PlayerCombat.NOT_ENOUGH_MANA;
            aLog.enqueue("You didn't have enough mana!"); // adds action to the Action Log
          }
        }
        else{ // HEAL was hovered
          if (playerCom.getMana() >= 3){ // checks if player has enough mana
            playerCom.decreaseMana(3);
            playerCombatAction = PlayerCombat.HEAL;
            playerCombatActionAmount = playerCom.getPw() * 2; // heal calculation
            playerCombatActionAmount = playerCom.getMaxHp() - playerCom.getHp() < playerCombatActionAmount ? playerCom.getMaxHp() - playerCom.getHp() : playerCombatActionAmount; // amount shouldn't be more than the player can actually heal
            playerCom.increaseHealth(playerCombatActionAmount);
            aLog.enqueue("You healed for " + playerCombatActionAmount + " health!"); // adds action to the Action Log
          }
          else{ // not enough mana
            playerCombatAction = PlayerCombat.NOT_ENOUGH_MANA;
            aLog.enqueue("You didn't have enough mana!"); // adds action to the Action Log
          }
        }
        if (playerCombatAction == PlayerCombat.MISSED){
          aLog.enqueue("You missed!"); // adds action to the Action Log
        }
        turn = enemyTurn;
        playerEndTurnTime = System.currentTimeMillis(); // gets the time the player ended their turn at
      }
    }
    else{
      while (System.currentTimeMillis() < playerEndTurnTime + 2500){} // waits for 2.5 seconds to pass
      
      enemyAction = enemiesCom.get(enemyInCombat).action(); // gets the enemy's action
      
      if (enemyAction == EnemyCombat.ATTACK){
        if (playerCombatAction == PlayerCombat.SHIELD || rand.nextInt(100) > playerCom.getSp()){ // if player was shielding or if random number is higher than player's speed
          enemyActionAmount = enemiesCom.get(enemyInCombat).getPw() * 2 / playerCom.getDf() * (100 + rand.nextInt(11) - 5) / 100; // damage calculation
          enemyActionAmount = playerCombatAction == PlayerCombat.SHIELD ? enemyActionAmount / 2 : enemyActionAmount;
          playerCom.decreaseHealth(enemyActionAmount); // if player shielded, divides damage by 2
          aLog.enqueue(enemiesCom.get(enemyInCombat).getTy() + " attacked, dealing " + enemyActionAmount + " damage!"); // adds action to the Action Log
        }
        else{ // random number was lower therefore missed
          enemyAction = EnemyCombat.MISSED;
        }
      }
      else if (enemyAction == EnemyCombat.DELAY){
        enemiesCom.get(enemyInCombat).charge(); // enemy starts charging
        aLog.enqueue(enemiesCom.get(enemyInCombat).getTy() + " is getting ready to attack!"); // adds action to the Action Log
      }
      else if (enemyAction == EnemyCombat.STRONG_ATTACK){
        if (playerCombatAction == PlayerCombat.SHIELD || rand.nextInt(100) > playerCom.getSp()){ // if player was shielding or if random number is higher than player's speed
          enemyActionAmount = enemiesCom.get(enemyInCombat).getPw() * 5 / playerCom.getDf() * (100 + rand.nextInt(11) - 5) / 100; // damage calculation
          enemyActionAmount = playerCombatAction == PlayerCombat.SHIELD ? enemyActionAmount / 2 : enemyActionAmount; // if player shielded, divides damage by 2
          playerCom.decreaseHealth(enemyActionAmount);
          aLog.enqueue(enemiesCom.get(enemyInCombat).getTy() + " let out a strong attack, dealing " + enemyActionAmount + " damage!"); // adds action to the Action Log
        }
        else{ // random number was lower therefore missed
          enemyAction = EnemyCombat.MISSED;
        }
      }
      else{ // enemyAction == EnemyCombat.HAUNT
        if (playerCom.getMana() >= 2){ // if player's mana is at least 2
          enemyActionAmount = 2;
        }
        else if (playerCom.getMana() == 1){ // if player's mana is 1
          enemyActionAmount = 1;
        }
        else{ // player's mana is 0
          enemyActionAmount = 0;
        }
        playerCom.decreaseMana(enemyActionAmount);
        aLog.enqueue(enemiesCom.get(enemyInCombat).getTy() + " haunted you, your mana was reduced by " + enemyActionAmount + "!"); // adds action to the Action Log
      }
      if (enemyAction == EnemyCombat.MISSED){
        aLog.enqueue(enemiesCom.get(enemyInCombat).getTy() + " missed!"); // adds action to the Action Log
      }
      turn = playerTurn;
    }
    
    if (enemiesCom.get(enemyInCombat).getHp() == 0){ // checks if the enemy is dead
      aLog.enqueue("You gained " + enemiesCom.get(enemyInCombat).getSumOfStats() + " experience points!"); // adds action to the Action Log
      playerCom.increaseExperience(enemiesCom.get(enemyInCombat).getSumOfStats());
      endCombatTime = System.currentTimeMillis(); // gets the time the enemy was defeated at
      winCombat = true; // sets it to true because the fight was won
    }
    
    if (playerCom.getHp() == 0){ // checks if the player is dead
      screen = GAME_OVER;
    }
    
    while (aLog.getLength() > 3){ // while the Action Log has more than 3 entries
      aLog.dequeue(); // removes the oldest entry
    }
  }
  
  public void wonCombat(){ // method for when combat is won
    if (System.currentTimeMillis() > endCombatTime + 1500){ // checks if 1.5 seconds have passed
      enemiesPlat.remove(enemyInCombat); // removes the dead enemy from enemiesPlat
      enemiesCom.remove(enemyInCombat); // removes the dead enemy from enemiesCom
      playerCom.resetMana();
      playerCombatAction = PlayerCombat.NOTHING;
      enemyAction = EnemyCombat.NOTHING;
      aLog.reset();
      screen = PLATFORMING;
      winCombat = false;
    }
  }
  
  public void menu(){ // method for navigating the pause menu
    playerMenuAction = playerCom.menu(keys); // gets the player's action in the pause menu
    
    if (playerMenuAction == PlayerCombat.LEFT){ // a key was pressed
      playerCom.decreaseStat(hover); // minuses one stat point from the stat points applied for the specific stat
    }
    else if (playerMenuAction == PlayerCombat.RIGHT){ // d key was pressed
      playerCom.increaseStat(hover); // adds one stat point from the stat points applied for the specific stat
    }
    else if (playerMenuAction == PlayerCombat.UP){ // w key was pressed
      hover = hover - 1 < 0 ? 4 : hover - 1; // new hovered button is up
    }
    else if (playerMenuAction == PlayerCombat.DOWN){ // s key was pressed
      hover = hover + 1 > 4 ? 0 : hover + 1; // new hovered button is down
    }
  }
  
  @Override
  public void actionPerformed(ActionEvent e){
    if (screen == INTRO){
      if (mb == MouseEvent.BUTTON1){ // starting a new game
        screen = PLATFORMING;
        start();
      }
      if (keys[KeyEvent.VK_I] && !keysHeld[INSTRUCTIONS_BUTTON]){ // opening instructions
        keysHeld[INSTRUCTIONS_BUTTON] = true;
        screen = INSTRUCTIONS;
      }
    }
    else if (screen == INSTRUCTIONS){
      if (keys[KeyEvent.VK_I] && !keysHeld[INSTRUCTIONS_BUTTON]){ // exiting instructions
        keysHeld[INSTRUCTIONS_BUTTON] = true;
        screen = INTRO;
      }
    }
    else if (screen == PLATFORMING && !pause){
      move();
      
      if (keys[KeyEvent.VK_P] && !keysHeld[MENU]){ // pausing
        keysHeld[MENU] = true;
        pause = true;
        hover = 0;
      }
    }
    else if (screen == COMBAT && !pause){
      if (!winCombat){
        combat();
      }
      else{ // fight was won
        wonCombat();
      }
      
      if (keys[KeyEvent.VK_P] && !keysHeld[MENU] && !winCombat){ // pausing
        keysHeld[MENU] = true;
        pause = true;
        hover = 0;
      }
    }
    else if (pause){
      menu();
      
      if (keys[KeyEvent.VK_ESCAPE] && !keysHeld[ESCAPE]){ // exiting to main menu
        keysHeld[ESCAPE] = true;
        pause = false;
        screen = INTRO;
      }
      if (keys[KeyEvent.VK_P] && !keysHeld[MENU]){ // unpausing
        keysHeld[MENU] = true;
        pause = false;
        hover = 0;
        playerCom.updateStats(); // applies stat upgrades, if any
      }
    }
    else if (screen == GAME_OVER){
      if (keys[KeyEvent.VK_ESCAPE] && !keysHeld[ESCAPE]){ // returns to main menu
        keysHeld[ESCAPE] = true;
        screen = INTRO;
      }
    }
    else{ // screen == END
      if (keys[KeyEvent.VK_ESCAPE] && !keysHeld[ESCAPE]){ // returns to main menu
        keysHeld[ESCAPE] = true;
        screen = INTRO;
      }
    }
		
    checkKeysHeld();
    
    repaint();
  }
  
  private void updateMouse(MouseEvent e){
    mx = e.getX();
    my = e.getY();
  }
  
  @Override
  public void mouseEntered(MouseEvent e){}
  
  @Override
  public void mouseExited(MouseEvent e){}
  
  @Override
  public void mouseReleased(MouseEvent e){
    updateMouse(e);      
    mb = 0;
  }
  
  @Override
  public void mouseClicked(MouseEvent e){
    updateMouse(e);
    mb = 0;
  }
  
  @Override
  public void mouseDragged(MouseEvent e){
    updateMouse(e);
  }
  
  @Override
  public void mouseMoved(MouseEvent e){
    updateMouse(e);
  }
  
  @Override
  public void mousePressed(MouseEvent e){
    updateMouse(e);
    mb = e.getButton();
  }
  
  @Override
  public void keyReleased(KeyEvent e){
    keys[e.getKeyCode()] = false;
  }
  
  @Override
  public void keyPressed(KeyEvent e){
    keys[e.getKeyCode()] = true;
  }
  
  @Override
  public void keyTyped(KeyEvent e){}
  
  public void checkKeysHeld(){ // checks if each key is being held
    if (keysHeld[ESCAPE]){
      if (!keys[KeyEvent.VK_ESCAPE]){
        keysHeld[ESCAPE] = false;
      }
    }
    if (keysHeld[MENU]){
      if (!keys[KeyEvent.VK_P]){
        keysHeld[MENU] = false;
      }
    }
    if (keysHeld[INSTRUCTIONS_BUTTON]){
      if (!keys[KeyEvent.VK_I]){
        keysHeld[INSTRUCTIONS_BUTTON] = false;
      }
    }
  }
  
  @Override
  public void paint(Graphics g){ // drawing everything
    if (pause){ // pause menu
      g.setColor(Color.BLACK);
      g.fillRect(150, 150, 400, 600);
      for (int i = 0; i < statButtons.length; i ++){
        statButtons[i].drawStat(g, i, i == hover, playerCom);
      }
      g.setColor(Color.WHITE);
      g.fillRect(250, 560, 201, 7);
      g.setColor(Color.YELLOW);
      g.fillRect(251, 561, playerCom.getExp() * 200 / playerCom.getMaxExp() - 1, 5);
      g.setColor(Color.WHITE);
      g.setFont(new Font("Times New Roman", Font.BOLD, 20));
      g.drawString("Level: " + playerCom.getLvl(), 160, 180);
      g.drawString("Available Stat Points: " + playerCom.getStatPoints(), 160, 210);
      g.setFont(new Font("Times New Roman", Font.BOLD, 15));
      g.drawString("Press Escape to Return to the Main Menu", 200, 720);
    }
    else if (screen == INTRO){ // main menu
      g.drawImage(platformingBack, 0, 0, null);
      g.setColor(Color.WHITE);
      g.setFont(new Font("Times New Roman", Font.BOLD, 100));
      g.drawString("Medieval Quest", 60, 150);
      g.setFont(new Font("Times New Roman", Font.BOLD, 40));
      g.drawString("Click to Begin", 250, 500);
      g.setFont(new Font("Times New Roman", Font.BOLD, 20));
      g.drawString("Press I for Instructions", 265, 700);
    }
    else if (screen == INSTRUCTIONS){ // instructions screen
      g.drawImage(instructionsBack, 0, 0, null);
    }
    else if (screen == PLATFORMING){ // platforming section
      offset = -playerPlat.getDist() % -2732;
      g.drawImage(platformingBack, offset, 0, null);
      g.drawImage(platformingBack, offset + 2732, 0, -1366, 800, null);
      g.drawImage(platformingBack, offset + 2732, 0, null);
      g.drawImage(platformingBack, offset + 5464, 0, -1366, 800, null);
      playerPlat.draw(g);
      for (EnemyPlatforming ep : enemiesPlat){
        ep.draw(g, playerPlat);
      }
      for (Block b : blocks){
        b.draw(g, playerPlat);
      }
      g.setColor(Color.WHITE);
      g.fillRect(10, 10, 201, 16);
      g.setColor(Color.GREEN);
      g.fillRect(11, 11, playerCom.getHp() * 200 / playerCom.getMaxHp() - 1, 14);
      g.setColor(Color.BLACK);
      g.setFont(new Font("Times New Roman", Font.BOLD, 15));
      g.drawString(playerCom.getHp() + " / " + playerCom.getMaxHp(), 11, 23);
      g.drawString("Health", 85, 23);
      
      g.drawImage(princess, 5670 - playerPlat.getDist(), 455, null); // objective
    }
    else if (screen == COMBAT){ // combat section
      g.drawImage(combatBack, 0, 0, null);
      playerCom.draw(g);
      enemiesCom.get(enemyInCombat).draw(g);
      if (turn == playerTurn){
        for (int i = 0; i < combatButtons.length; i ++){
          combatButtons[i].drawCombat(g, i == hover);
        }
      }
      aLog.draw(g);
    }
    else if (screen == GAME_OVER){ // game over screen
      g.setColor(Color.BLACK);
      g.fillRect(0,0,800,800);
      g.setColor(Color.WHITE);
      g.setFont(new Font("Times New Roman", Font.BOLD, 50));
      g.drawString("Game Over!", 250, 450);
      g.setFont(new Font("Times New Roman", Font.BOLD, 25));
      g.drawString("Press ESCAPE to Continue", 235, 525);
    }
    else{ // credits screen
      g.setColor(Color.BLACK);
      g.fillRect(0,0,800,800);
      g.setColor(Color.WHITE);
      g.setFont(new Font("Times New Roman", Font.BOLD, 35));
      g.drawString("You Saved the Princess!", 200, 450);
      g.setFont(new Font("Times New Roman", Font.BOLD, 15));
      g.drawString("Thanks for Playing!", 335, 525);
      g.drawString("Made by Ethan Becker & Peter Hu", 275, 570);
      g.setFont(new Font("Times New Roman", Font.BOLD, 25));
      g.drawString("Press ESCAPE to Continue", 235, 725);
    }
  }
}