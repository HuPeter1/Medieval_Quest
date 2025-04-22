import java.awt.*;
import javax.swing.*;
import java.util.*;
import javax.imageio.ImageIO;

// class for the player in the combat section (stats and actions)
class PlayerCombat{
  private int level, experience, maxExperience, maxHealth, health, maxMana, mana, statPoints, left, right, up, down, space; // stats and controls
  private int[] statValues, statIncreases; // statValues to store each stat and statIncreases to store how much the player is increasing each stat using stat points
  public static final int ENDURANCE = 0, ENERGY = 1, POWER = 2, DEFENSE = 3, SPEED = 4; // stats
  private static final Image playerImage;
  public static final int ATTACK = 0, CHARGED_ATTACK = 1, SHIELD = 2, HEAL = 3, MISSED = -1, NOT_ENOUGH_MANA = -2; // combat actions
  public static final int NOTHING = -1, LEFT = 0, RIGHT = 1, UP = 2, DOWN = 3, SELECT = 4; // controls
  private boolean []keysHeld; // list to check which keys are being held

  static {
    Image temp = null;
    try {
      temp = ImageIO.read(PlayerCombat.class.getResourceAsStream("Knight/Fighting.png"));
    } catch (Exception e) {
      e.printStackTrace();
    }
    playerImage = temp;
  }
  
  public PlayerCombat(int l, int r, int u, int d, int s){ // l for a key, r for d key, u for w key, d for s key, s for space
    statValues = new int[5];
    statIncreases = new int[5];
    level = statValues[ENDURANCE] = statValues[ENERGY] = statValues[POWER] = statValues[DEFENSE] = statValues[SPEED] = 5;
    experience = 0;
    maxExperience = level * 4;
    maxHealth = health = statValues[ENDURANCE] * 3;
    maxMana = mana = statValues[ENERGY] * 2;
    statPoints = 0;
    left = l;
    right = r;
    up = u;
    down = d;
    space = s;
    keysHeld = new boolean[5];
  }
  
  public int getLvl(){return level;}
  
  public int getMaxExp(){return maxExperience;}
  
  public int getExp(){return experience;}
  
  public int getStatPoints(){return statPoints;}
  
  public int getMaxHp(){return maxHealth;}
  
  public int getHp(){return health;}
  
  public int getMana(){return mana;}
  
  public int getStatValue(int i){return statValues[i];}
  
  public int getStatIncrease(int i){return statIncreases[i];}
  
  public int getEnd(){return statValues[ENDURANCE];}
  
  public int getEnrgy(){return statValues[ENERGY];}
  
  public int getPw(){return statValues[POWER];}
  
  public int getDf(){return statValues[DEFENSE];}
  
  public int getSp(){return statValues[SPEED];}
  
  public void increaseHealth(int heal){
    health = health + heal > maxHealth ? maxHealth : health + heal;
  }
  
  public void decreaseHealth(int damage){
    health = health - damage < 0 ? 0 : health - damage;
  }
  
  public void setHealth(int h){
    health = h;
  }
  
  public void decreaseMana(int cost){
    mana -= cost;
  }
  
  public void resetMana(){
    mana = maxMana;
  }
  
  public void increaseExperience(int exp){
    experience += exp;
    if (experience >= maxExperience){
      levelUp();
    }
  }
  
  public void levelUp(){ // method for leveling up
    level ++;
    experience -= maxExperience;
    maxExperience = level * 4; // gets new experience amount needed to level up
    statPoints += 5;
  }
  
  public void increaseStat(int i){ // adds one to the list of how much you want to increase each stat for the specific stat
    if (statPoints > 0){ // checks if there is a stat point available
      statPoints --;
      statIncreases[i] ++;
    }
  }
  
  public void decreaseStat(int i){ // minuses one from the list of how much you want to increase each stat for the specific stat
    if (statIncreases[i] > 0){ // can't go negative
      statIncreases[i] --;
      statPoints ++;
    }
  }
  
  public void updateStats(){ // adds the amount wanted to each stat
    maxHealth += statIncreases[ENDURANCE] * 3;
    health += statIncreases[ENDURANCE] * 3;
    maxMana += statIncreases[ENERGY] * 2;
    mana += statIncreases[ENERGY] * 2;
    for (int i = 0; i < 5; i ++){
      statValues[i] += statIncreases[i];
    }
    Arrays.fill(statIncreases, 0);
  }
  
  public int action(boolean []keys){ // returns player's actions during combat
    if (keys[left] && !keysHeld[LEFT]){
      keysHeld[LEFT] = true;
      return LEFT;
    }
    else if (keys[right] && !keysHeld[RIGHT]){
      keysHeld[RIGHT] = true;
      return RIGHT;
    }
    else if (keys[space] && !keysHeld[SELECT]){
      keysHeld[SELECT] = true;
      return SELECT;
    }
		
    checkKeysHeld(keys);
    
    return NOTHING;
  }
  
  public int menu(boolean []keys){ // returns player's actions on the pause menu
    if (keys[left] && !keysHeld[LEFT]){
      keysHeld[LEFT] = true;
      return LEFT;
    }
    else if (keys[right] && !keysHeld[RIGHT]){
      keysHeld[RIGHT] = true;
      return RIGHT;
    }
    else if (keys[up] && !keysHeld[UP]){
      keysHeld[UP] = true;
      return UP;
    }
    else if (keys[down] && !keysHeld[DOWN]){
      keysHeld[DOWN] = true;
      return DOWN;
    }
		
    checkKeysHeld(keys);
    
    return NOTHING;
  }
  
  public void checkKeysHeld(boolean []keys){ // checks if each key is being held
    if (keysHeld[LEFT]){
      if (!keys[left]){
        keysHeld[LEFT] = false;
      }
    }
    if (keysHeld[RIGHT]){
      if (!keys[right]){
        keysHeld[RIGHT] = false;
      }
    }
    if (keysHeld[UP]){
      if (!keys[up]){
        keysHeld[UP] = false;
      }
    }
    if (keysHeld[DOWN]){
      if (!keys[down]){
        keysHeld[DOWN] = false;
      }
    }
    if (keysHeld[SELECT]){
      if (!keys[space]){
        keysHeld[SELECT] = false;
      }
    }
  }
  
  public void draw(Graphics g){ // drawing the player and player's box in the combat section
    g.drawImage(playerImage, 185, 350, null);
    g.setColor(Color.GRAY);
    g.fillRect(485, 520, 250, 105);
    g.setColor(Color.WHITE);
    g.fillRect(500, 560, 201, 16);
    g.fillRect(500, 590, 201, 16);
    g.fillRect(500, 614, 201, 7);
    g.setColor(Color.GREEN);
    g.fillRect(501, 561, health * 200 / maxHealth - 1, 14);
    g.setColor(Color.CYAN);
    g.fillRect(501, 591, mana * 200 / maxMana - 1, 14);
    g.setColor(Color.YELLOW);
    g.fillRect(501, 615, experience * 200 / maxExperience - 1, 5);
    g.setColor(Color.BLACK);
    g.setFont(new Font("Times New Roman", Font.BOLD, 15));
    g.drawString(health + " / " + maxHealth, 501, 574);
    g.drawString(mana + " / " + maxMana, 501, 604);
    g.drawString("Health", 575, 574);
    g.drawString("Mana", 578, 604);
    g.setFont(new Font("Times New Roman", Font.BOLD, 25));
    g.drawString("Player", 495, 550);
    g.setFont(new Font("Times New Roman", Font.BOLD, 15));
    g.drawString("Lvl: " + level, 675, 550);
  }
}