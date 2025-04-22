import java.awt.*;
import java.util.*;

// class for enemies in the platforming section (how for it can move, position)
class EnemyPlatforming{
  private int startX, startY, x, y, width, height, finalX, finalY, changeX, changeY; // startX for how far left it can go, startY for how far up it can go, finalX for how far right it can go, finalY for how far down it can go, changeX for change in x and changeY for change in y
  private String type; // varibale for the type of enemy
  private double hypotenuse, angle, vx, vy; // variables for moving
  
  public EnemyPlatforming(int sx, int sy, int xx, int yy, int w, int h, int fx, int fy, String ty){ // creates an enemy with all the variables needed for their position and how they move
    startX = sx;
    startY = sy;
    x = xx;
    y = yy;
    width = w;
    height = h;
    finalX = fx;
    finalY = fy;
    changeX = finalX - startX;
    changeY = finalY - startY;
    hypotenuse = Math.hypot(changeX, changeY);
    if (hypotenuse != 0) {
      angle = Math.acos(Math.min(1, Math.max(-1, changeX / hypotenuse)));
    }
    else {
      angle = 0;
    }
    vx = Math.round(3 * Math.cos(angle));
    vy = Math.round(3 * Math.sin(angle));
    type = ty;
  }
  
  public int getStartX(){return startX;}
  
  public int getStartY(){return startY;}
  
  public int getX(){return x;}
  
  public int getY(){return y;}
  
  public int getWidth(){return width;}
  
  public int getHeight(){return height;}
  
  public int getFinalX(){return finalX;}
  
  public int getFinalY(){return finalY;}
  
  public static ArrayList<EnemyPlatforming> make(){ // returns a list of enemies for the platforming section
    ArrayList<EnemyPlatforming> enemiesPlat = new ArrayList<EnemyPlatforming>(Arrays.asList(
                                                                                            new EnemyPlatforming(765,350, 765,500,75,75, 765,550, Enemy.GHOST), new EnemyPlatforming(1200,325, 1200,300,75,75, 1325,300, Enemy.GHOST),
                                                                                            new EnemyPlatforming(1600,100, 1600,100,75,75, 2000,100, Enemy.GHOST), new EnemyPlatforming(1750,450, 1750,450,75,75, 1950,450, Enemy.GHOST),
                                                                                            new EnemyPlatforming(2770,200, 2770,300,75,75, 2770,500, Enemy.GHOST), new EnemyPlatforming(2860,100, 3000,100,75,75, 2995,100, Enemy.GHOST),
                                                                                            new EnemyPlatforming(3525,450, 3525,450,75,75, 3775,450, Enemy.GHOST), new EnemyPlatforming(4700,380, 4700,380,75,108, 4700,380, Enemy.GHOST_KING)));
    return enemiesPlat;
  }
  
  public void move(PlayerPlatforming playerPlat){ // method for moving enemies
    if (startX - playerPlat.getDist() < 800 && finalX + width - playerPlat.getDist() > 0){ // checks if start or final position are on screen
      vx = x + vx > finalX || x + vx < startX ? vx * -1 : vx; // if past startX or finalX, inverts vx
      vy = y + vy > finalY || y + vy < startY ? vy * -1 : vy; // if past startY or finalY, inverts vy
      x += vx;
      y += vy;
    }
  }
  
  public void draw(Graphics g, PlayerPlatforming playerPlat){ // drawing the enemy in the platforming section
    if (type == Enemy.GHOST){
      g.drawImage(Enemy.enemyImages[0], x - playerPlat.getDist(), y, null);
    }
    else{
      g.drawImage(Enemy.enemyImages[1], x - playerPlat.getDist(), y, null);
    }
  }
}