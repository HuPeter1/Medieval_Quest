import java.awt.*;
import javax.swing.*;
import javax.imageio.ImageIO;

// class for blocks that the player can't go through in the platforming section (position and type)
public class Block{
  private int x, y, width, height, type;
  private static final Image[] textures = new Image[2];
  private static final int STONE = 0, BRICK = 1; // types

  static {
    try {
      textures[STONE] = ImageIO.read(Block.class.getResourceAsStream("Textures/Stone.png"));
      textures[BRICK] = ImageIO.read(Block.class.getResourceAsStream("Textures/Brick.png"));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  public Block(int xx, int yy, int w, int h, int ty){ // creates a new block with dimensions and type for texture
	x = xx;
    y = yy;
    width = w;
    height = h;
    type = ty;
  }
  
  public int getX(){return x;}
  
  public int getY(){return y;}
  
  public int getWidth(){return width;}
  
  public int getHeight(){return height;}
  
  public static Block[] make(){ // returns a list of blocks
    // Safe jump range: 200x100 100x200
    Block []blocks =
    {new Block(0,725,600,75,STONE),new Block(600,600,100,200,STONE), // screen 1
      new Block(850,725,800,75,STONE),new Block(900,500,100,50,STONE),new Block(1200,400,200,350,STONE), // screen 2
      new Block(1500,200,100,50,STONE),new Block(1600,-100,300,200,STONE),new Block(1600,300,400,100,STONE),new Block(1600,200,100,100,STONE),new Block(1775,200,225,100,STONE),new Block(1750,600,350,200,STONE),new Block(1950,550,50,50,STONE), // screen 3
      new Block(2200,400,200,400,STONE),new Block(2600,300,50,200,STONE),new Block(2650,450,50,50,STONE),new Block(2600,700,50,100,STONE),new Block(2710,700,50,100,STONE),new Block(2860,550,50,250,STONE),new Block(2860,200,200,100,STONE), // screen 4
      new Block(3350,725,50,75,STONE),new Block(3450,550,400,250,STONE),new Block(3450,350,75,75,STONE),new Block(3450,0,400,200,STONE),new Block(3525,350,195,50,STONE),new Block(3775,350,75,75,STONE), // screen 5
      new Block(4150,725,200,75,STONE),new Block(4350,625,200,175,STONE),new Block(4550,525,200,275,STONE),new Block(4550,0,200,380,BRICK),new Block(4750,525,1600,275,BRICK),new Block(5750,0,600,525,BRICK)}; // screen 6
    return blocks;
  }
  
  public void draw(Graphics g, PlayerPlatforming playerPlat){ // drawing the block
    g.drawImage(textures[type], x - playerPlat.getDist(), y, width, height, null);
  }
}