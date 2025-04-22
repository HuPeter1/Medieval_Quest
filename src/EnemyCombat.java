import java.awt.*;
import java.util.*;

// class for enemies in combat (stats and actions in combat)
class EnemyCombat{
  Random rand = new Random();
  private int level, maxHealth, health, sumOfStats, action;
  private int []statValues;
  private static final int ENDURANCE = 0, POWER = 1, DEFENSE = 2, SPEED = 3; // stats
  private String type; // variable for the type of enemy
  public static final int NOTHING = -2, MISSED = -1, ATTACK = 0, DELAY = 1, HAUNT = 2, STRONG_ATTACK = 3; // actions
  private boolean charging; // boolean for when enemy is preparing for strong attack
  
  public EnemyCombat(int lv, int en, int pw, int sp, int de, String ty){ // creates a new enemy with starting stats as well as a type
    level = lv;
    statValues = new int[4];
    statValues[ENDURANCE] = en;
    statValues[POWER] = pw;
    statValues[SPEED] = sp;
    statValues[DEFENSE] = de;
    maxHealth = health = statValues[ENDURANCE] * 3;
    sumOfStats = statValues[ENDURANCE] + statValues[POWER] + statValues[SPEED] + statValues[DEFENSE];
    type = ty;
    charging = false;
  }
  
  public int getHp(){return health;}
  
  public int getPw(){return statValues[POWER];}
  
  public int getSp(){return statValues[SPEED];}
  
  public int getDf(){return statValues[DEFENSE];}
  
  public int getSumOfStats(){return sumOfStats;}
  
  public String getTy(){return type;}
  
  public void charge(){
    charging = true;
  }
  
  public static ArrayList<EnemyCombat> make(){ // returns a list of enemies for the combat section
    ArrayList<EnemyCombat> enemiesCom = new ArrayList<EnemyCombat>(Arrays.asList(new EnemyCombat(3, 3, 3, 3, 3, Enemy.GHOST), new EnemyCombat(4, 4, 4, 4, 4, Enemy.GHOST), new EnemyCombat(5, 5, 5, 5, 5, Enemy.GHOST), new EnemyCombat(6, 6, 6, 6, 6, Enemy.GHOST), new EnemyCombat(7, 7, 7, 7, 7, Enemy.GHOST), new EnemyCombat(8, 8, 8, 8, 8, Enemy.GHOST), new EnemyCombat(8, 8, 8, 8, 8, Enemy.GHOST), new EnemyCombat(10, 10, 10, 10, 10, Enemy.GHOST_KING)));
    return enemiesCom;
  }
  
  public int action(){ // returns enemy's actions during combat
    if (charging){ // if enemy is charging they will always perform STRONGER_ATTACK
      charging = false;
      return STRONG_ATTACK;
    }
    if (type == Enemy.GHOST){ // normal ghost only has two options
      action = rand.nextInt(2);
    }
    else{ // ghost king has 1 more option (HAUNT)
      action = rand.nextInt(3);
    }
    return action;
  }
  
  public void decreaseHealth(int damage){
    health = health - damage < 0 ? 0 : health - damage;
  }
  
  public void draw(Graphics g){ // drawing the enemy and enemy's box in the combat section
    if (health > 0){
      if (type == Enemy.GHOST){
        g.drawImage(Enemy.enemyImages[0], 490, 95, 150, 180, null);
      }
      else{
        g.drawImage(Enemy.enemyImages[1], 490, 59, 150, 216, null);
      }
    }
    g.setColor(Color.GRAY);
    g.fillRect(10, 10, 250, 80);
    g.setColor(Color.WHITE);
    g.fillRect(25, 25, 201, 16);
    g.setColor(Color.GREEN);
    g.fillRect(26, 26, health * 200 / maxHealth - 1, 14);
    g.setColor(Color.BLACK);
    g.setFont(new Font("Times New Roman", Font.BOLD, 15));
    g.drawString("Health", 100, 40);
    g.setFont(new Font("Times New Roman", Font.BOLD, 25));
    g.drawString(type, 20, 80);
    g.setFont(new Font("Times New Roman", Font.BOLD, 15));
    g.drawString("Lvl: " + level, 200, 80);
  }
}