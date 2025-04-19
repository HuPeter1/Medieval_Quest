import java.awt.*;

//class for buttons that are interacted with by using the keyboard (position, name, and cost if applicable)
class Button{
  private int x, y, width, height, cost;
  private String name;
  
  public Button(int xx, int yy, int w, int h, String n, int c){ //creates a button with dimensions, a name and a cost for using it
    x = xx;
    y = yy;
    width = w;
    height = h;
    name = n;
    cost = c;
  }

 public Button(int xx, int yy, int w, int h, String n){ //creates a button with dimensions and a name
    x = xx;
    y = yy;
    width = w;
    height = h;
    name = n;
  }
  
  public static Button[] makeCombat(){ //returns a list of buttons for the combat section
    Button []combatButtons = {new Button(95, 650, 145, 75, "Attack", 0), new Button(250, 650, 145, 75, "Charged Attack", 2),
                              new Button(405, 650, 145, 75, "Shield", 1), new Button(560, 650, 145, 75, "Heal", 3)};
    return combatButtons;
  }

 public static Button[] makeStat(){ //returns a list of buttons for the pause menu
  Button []statButtons = {new Button(250, 255, 200, 50, "Endurance"), new Button(250, 315, 200, 50, "Energy"), new Button(250, 375, 200, 50, "Power"), new Button(250, 435, 200, 50, "Defense"), new Button(250, 495, 200, 50, "Speed")};
  return statButtons;
 }
  
  public void drawCombat(Graphics g, boolean hover){ //drawing the button in the combat section
    if (hover){ //if being hovered
      g.setColor(Color.WHITE);
      g.fillRect(x - 5, y - 5, width + 10, height + 10);
    }
    g.setColor(Color.LIGHT_GRAY);
    g.fillRect(x, y, width, height);
    g.setColor(Color.BLACK);
    g.setFont(new Font("Times New Roman", Font.BOLD, 15));
    g.drawString(name, x + 5, y + 15);
    if(cost > 0){
      g.drawString("Cost: " + cost + " Mana", x + 5, y + height);
    }
  }

 public void drawStat(Graphics g, int i, boolean hover, PlayerCombat playerCom){ //drawing the button in the pause menu
	 if (hover){ //if being hovered
      g.setColor(Color.WHITE);
      g.fillRect(x - 5, y - 5, width + 10, height + 10);
    }
	 if (i == PlayerCombat.ENDURANCE){
		 g.setColor(Color.GREEN);
	 }
	 else if (i == PlayerCombat.ENERGY){
		 g.setColor(Color.CYAN);
	 }
	 else if (i == PlayerCombat.POWER){
		 g.setColor(Color.RED);
	 }
	 else if (i == PlayerCombat.DEFENSE){
		 g.setColor(Color.BLUE);
	 }
	 else{
		 g.setColor(Color.YELLOW);
	 }
	 g.fillRect(x, y, width, height);
	 g.setColor(Color.BLACK);
	 g.setFont(new Font("Times New Roman", Font.BOLD, 20));
	 g.drawString(name + ":", x + 5, y + 30);
	 g.drawString(playerCom.getStatValue(i) + " +" + playerCom.getStatIncrease(i), x + 140, y + 30);
 }
}