import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.imageio.ImageIO;

// class for the player in the platforming section (moving, jumping, position/distance and animations)
class PlayerPlatforming{
  private int x, y, width, height, distance, vy, groundX, groundY, frame, aCount, animation, left, right, up; // distance tracks how far the player traveled, vy for jump velocity, groundX and groundY for where the player was last standing on a block, frame, aCount and animation to manage player animations
  private boolean collideRight, collideLeft, jumped, aFreeze, leftBool;
  private static final Image[][] playerImages = new Image[6][8];
  private Image playerImage; // variable for current image
  public static final int IDLE = 0, LIDLE = 1, RUN = 2, LRUN = 3, JUMP = 4, LJUMP = 5; // player states for animations

  static {
    for (int i = 0; i < 8; ++i) {
      try {
        playerImages[IDLE][i] = ImageIO.read(PlayerPlatforming.class.getResourceAsStream("Knight/Idle/Idle" + i + ".png"));
        playerImages[LIDLE][i] = ImageIO.read(PlayerPlatforming.class.getResourceAsStream("Knight/Idle/LIdle" + i + ".png"));
        playerImages[RUN][i] = ImageIO.read(PlayerPlatforming.class.getResourceAsStream("Knight/Run/Run" + i + ".png"));
        playerImages[LRUN][i] = ImageIO.read(PlayerPlatforming.class.getResourceAsStream("Knight/Run/LRun" + i + ".png"));
        playerImages[JUMP][i] = ImageIO.read(PlayerPlatforming.class.getResourceAsStream("Knight/Jump/Jump" + i + ".png"));
        playerImages[LJUMP][i] = ImageIO.read(PlayerPlatforming.class.getResourceAsStream("Knight/Jump/LJump" + i + ".png"));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
  
  public PlayerPlatforming(int l, int r, int u){ // l for a key, r for d key, u for w key
    x = 200;
    y = 650;
    width = 50;
    height = 78;
    distance = 0;
    vy = 0;
    frame = 0;
    aCount = 0;
    animation = IDLE;
    left = l;
    right = r;
    up = u;
  }
  
  public Rectangle getRect(){return new Rectangle(x, y, width, height);}
  
  public int getX(){return x;}
  
  public int getY(){return y;}
  
  public int getWidth(){return width;}
  
  public int getHeight(){return height;}
  
  public int getDist(){return distance;}
  
  public void move(boolean keys[], Block []blocks){ // method for movement and animations for the player
    frame ++;
    collideRight = collideRightBlock(blocks);
    collideLeft = collideLeftBlock(blocks);
    
    jumped = collideUpBlock(blocks) ? false : true; // checks if the player is in the air
    
    
    if (frame % 15 == 0 && !jumped){ // every 15 frames, display a new idle animation sprite
      frame = 0;
      aCount = aCount + 1 == 8 ? 0 : aCount + 1;
      animation = !leftBool ? IDLE : LIDLE;
    }
		
    if (keys[up]){
      jump(blocks);
    }
    
    gravity(blocks);
    
    if (keys[left] && !keys[right]){
      leftBool = true; // facing left
      
      if (!collideRight){ // if not colliding against the right side of a block
        distance = Math.max(0, distance - 5);
      }
      if (frame % 5 == 0 && !jumped){ // every 5 frames, display a new left run animation sprite
        frame = 0;
        aCount = aCount + 1 == 8 ? 0 : aCount + 1;
        animation = LRUN;
      }
    }
    if (keys[right] && !keys[left]){
      leftBool = false; // not facing left
      
      if (!collideLeft){ // if not colliding against the left side of a block
        distance += 5;
      }
      if (frame % 5 == 0 && !jumped){ // every 5 frames, display a new run animation sprite
        frame = 0;
        aCount = aCount + 1 == 8 ? 0 : aCount + 1;
        animation = RUN;
      }
    }
    
    if (jumped){
      if (!aFreeze){ // reset the animation only once while in the air
        aCount = 0;
        aFreeze = true;
      }
      if (frame % 5 == 0){ // every 5 frames, display a new jump animation sprite
        frame = 0;
        aCount = aCount + 1 == 8 ? 0 : aCount + 1;
        animation = !leftBool ? JUMP : LJUMP;
      }
    }
    else{
      aFreeze = false;
    }
    
    playerImage = playerImages[animation][aCount]; // set the displayed sprite based off the animation type and sprite number
  }
  
  public boolean collideLeftBlock(Block []blocks){ // checks collision with the left side of blocks
    for (Block b : blocks){
      if (b.getX() - distance <= x + width && b.getX() + b.getWidth() - distance >= x){ // this checks if the block can touch the player
        if (y < b.getY() + b.getHeight() && y + height > b.getY() && x + width >= b.getX() - distance && x < b.getX() + b.getWidth() - distance){
          return true;
        }
      }
    }
    return false;
  }
  
  public boolean collideRightBlock(Block []blocks){ // checks collision with the right side of blocks
    for (Block b : blocks){
      if (b.getX() - distance <= x + width && b.getX() + b.getWidth() - distance >= x){ // this checks if the block can touch the player
        if (y < b.getY() + b.getHeight() && y + height > b.getY() && x <= b.getX() + b.getWidth() - distance && x + width > b.getX() - distance){
          return true;
        }
      }
    }
    return false;
  }
  
  public boolean collideUpBlock(Block []blocks){ // checks collision with the top side of blocks
    for (Block b : blocks){
      if (b.getX() - distance <= x + width && b.getX() + b.getWidth() - distance >= x){ // this checks if the block can touch the player
        if (x + width > b.getX() - distance && x < b.getX() + b.getWidth() - distance && y + height >= b.getY() && y < b.getY()){ // checks if the player is above the block
          y = b.getY() - height; // keeps the player above the block
          return true;
        }
      }
    }
    return false;
  }
  
  public boolean collideDownBlock(Block []blocks){ // checks collision with the bottom side of blocks
    for (Block b : blocks){
      if (b.getX() - distance <= x + width && b.getX() + b.getWidth() - distance >= x){ // this checks if the block can touch the player
        if (x + width > b.getX() - distance && x < b.getX() + b.getWidth() - distance && y <= b.getY() + b.getHeight() - 1 && y + height > b.getY()){ // checks if the player is below the block
          y = b.getY() + b.getHeight(); // keeps the player below the block
          return true;
        }
      }
    }
    return false;
  }
  
  public void jump(Block[] blocks){ // method for jumping
    if(collideUpBlock(blocks) && vy == 0){ // allows the player to jump if they are on the ground
      vy = -20;
    }
  }
  
  public void gravity(Block []blocks){ // method for gravity
    y += vy;
    
    if (collideUpBlock(blocks)){ // set vy to zero if player stands on a block
      vy = 0;
    }
    else{
      vy += 1;
    }
    if (collideDownBlock(blocks)){ // set vy to zero if player hits their head
      vy = 0;
    }
  }
  
  public boolean checkFall(Block []blocks){ // checks if player falls below the screen and moves them back
    if (collideUpBlock(blocks)){
      groundX = distance;
      groundY = y;
    }
    if (y > 1000){
      distance = groundX;
      y = groundY;
      vy = 0;
      return true;
    }
    return false;
  }
  
  public int collideEnemy(ArrayList<EnemyPlatforming> enemiesPlat){ // checks if player collides with an enemy
    for (int i = 0; i < enemiesPlat.size(); i ++){
      if (enemiesPlat.get(i).getX() - distance <= x + width && (enemiesPlat.get(i).getX() + enemiesPlat.get(i).getWidth()) - distance >= x){
        if (enemiesPlat.get(i).getX() + enemiesPlat.get(i).getWidth() - distance >= x && enemiesPlat.get(i).getX() - distance <= x + width && enemiesPlat.get(i).getY() + enemiesPlat.get(i).getHeight() >= y && enemiesPlat.get(i).getY() <= y + height){
          return i;
        }
      }
    }
    return -1;
  }
  
  public void draw(Graphics g){ // drawing the player in the platforming section
    g.drawImage(playerImage, x + 3, y, 45, height, null);
  }
}