import java.awt.*;
import javax.swing.*;
import javax.imageio.ImageIO;

// class for enemies in general (enemy types and enemy images)
class Enemy{
  public static final String GHOST = "Ghost", GHOST_KING = "Ghost King";
  public static final Image[] enemyImages = new Image[2];

  static {
    try {
      enemyImages[0] = ImageIO.read(Enemy.class.getResourceAsStream("Enemies/NormalEnemy.png"));
      enemyImages[1] = ImageIO.read(Enemy.class.getResourceAsStream("Enemies/FinalBoss.png"));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}