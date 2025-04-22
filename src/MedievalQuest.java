import javax.swing.*;

// Main class

public class MedievalQuest extends JFrame{
  MedievalQuestPanel game = new MedievalQuestPanel(); // panel
  
  public MedievalQuest(){ // managing the window
    super("MedievalQuest");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    add(game);
    pack();
    setVisible(true);
  }
  
  public static void main(String []a){
    MedievalQuest frame = new MedievalQuest();
  }
}