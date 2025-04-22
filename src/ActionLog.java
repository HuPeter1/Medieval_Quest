import java.awt.*;

// class for the action log (actions performed in battle) using a queue structure (copied from code given by Mr. McKenzie)
class ActionLog{
  private LNode head;
  private LNode tail;
  
  public ActionLog(){
    tail = null;
    head = null;
  }
  
  public void enqueue(String a){ // adds to the back
    LNode tmp = new LNode(tail, a, null);
    if (head == null){ // no elements
      head = tmp;
    }
    else{ // there are elements
      tail.setNext(tmp); // setting the next of the previous tail
    }
    tail = tmp; // setting the new tail
  }
  
  public void dequeue(){ // removes from the front
    if (head.getNext() != null){ // checking if there is more than one element
      head = head.getNext();
      head.setPrev(null);
    }
    else{ // only one element
      head = null;
    }
  }
  
  public int getLength(){ // returns the length of the action log
    LNode tmp = head;
    int len = 0;
    
    while (tmp != null){ // while tmp is an existing element
      len ++;
      tmp = tmp.getNext();
    }
    
    return len;
  }
  
  public void reset(){ // resets the action log
    head = null;
    tail = null;
  }
  
  public void draw(Graphics g){ // drawing the action log
    LNode tmp = head;
    int i = 0;
		
    g.setColor(Color.BLACK);
    g.setFont(new Font("Times New Roman", Font.BOLD, 15));
    while (tmp != null){
      g.drawString(tmp.getAction(), 280, 30 + i * 20);
      i ++;
      tmp = tmp.getNext();
    }
  }
}

// class for the nodes to store where each element is located (copied from code given by Mr. McKenzie)
class LNode{
  private String action;
  private LNode next;
  private LNode prev;
  
  public LNode(LNode p, String a, LNode n){
    prev = p;
    action = a;
    next = n;
  }
  
  public String getAction(){return action;}
  public LNode getNext(){return next;}
  public LNode getPrev(){return prev;}
  
  public void setPrev(LNode tmp){
    prev = tmp;
  }
  
  public void setNext(LNode tmp){
    next = tmp;
  }
}