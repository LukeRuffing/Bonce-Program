//package Bounce;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

//import BouncingBall.Objc;




public class Bounce extends Frame implements WindowListener, ComponentListener, ActionListener, AdjustmentListener, Runnable{

    private static final long serialVersionUID = 10L;

    private final int WIDTH = 640;
    private final int HEIGHT = 400;
    private final int BUTTONH=20;
    private final int BUTTONHS = 5;

    private final int MAXObj=100;//max object size
    private final int MINObj=10;//min object size
    private final int SPEED=50;//initial speed
    private final int SBvisible=10;//visible Scroll bar
    private final int SBunit=1;//unit step size
    private final int SBblock=10;//block step size
    private final int SCROLLBARH=BUTTONH;//scrollbar height
    private final int SOBJ=21;//initial object width
    
    
    private int WinWidth=WIDTH;//initial frame width
    private int WinHeight=HEIGHT;//initial frame height
    private int ScreenWidth;//drawing screen width
    private int ScreenHeight;//drawing screen height
    private int WinTop=10;//top of frame
    private int WinLeft=10;//left side of frame
    private int BUTTONW=50;//initial button width
    private int CENTER=(WIDTH/2);//initial screen center
    private int BUTTONS=BUTTONW/4;//initial button spacing


    private final double DELAY = 10;
    private int SObj=SOBJ;//initial object width
    private int SpeedSBmin=1;//speed scrollbar min value
    private int SpeedSBmax=100+SBvisible;//speed scrollbar max with visible offset
    private int SpeedSBinit=SPEED;//initial speed scrollbar value
    private int ScrollBarW;//Scrollbar width
    
    private Insets I;

    Button Start, Shape,Clear,Tail,Quit;


    private Objc Obj;
    private Label SPEEDL = new Label("Speed",Label.CENTER);
    private Label SIZEL = new Label("Size",Label.CENTER);
    Scrollbar SpeedScrollBar, ObjSizeScrollBar;
    private Thread thethread;//thread for timer delay
   

    boolean run;
    boolean TimePause;
    boolean started;
    int speed;
    int delay;

    boolean tail;

   

    



    Bounce() {
       
        setTitle("Bouncing Ball");
        setLayout(null);

        setVisible(true);

        MakeSheet();

        started = false;
        //TimePause = false;

        try{
            initComponents();
        }catch (Exception e){
            e.printStackTrace();}


        SizeScreen();
        start();
    }

  
    public void initComponents() throws Exception, IOException{
 
        Obj = new Objc(SObj, ScreenWidth, ScreenHeight);

        TimePause = true;
        run = true;
        delay = (int) DELAY;

        Start = new Button("Run");
        Shape = new Button("Circle");
        Clear = new Button("Clear");
        Tail = new Button("No Tail");
        Quit = new Button("Quit");

        add("Center",Start);
        add("Center",Shape);
        add("Center",Tail);
        add("Center",Clear);
        add("Center",Quit);

        Start.addActionListener(this);
        Shape.addActionListener(this);
        Tail.addActionListener(this);
        Clear.addActionListener(this);
        Quit.addActionListener(this);
        this.addComponentListener(this);
        this.addWindowListener(this);

        SpeedScrollBar=new Scrollbar(Scrollbar.HORIZONTAL);
        SpeedScrollBar.setMaximum(SpeedSBmax);
        SpeedScrollBar.setMinimum(SpeedSBmin);
        SpeedScrollBar.setUnitIncrement(SBunit);
        SpeedScrollBar.setBlockIncrement(SBblock);
        SpeedScrollBar.setValue(SpeedSBinit);
        SpeedScrollBar.setVisibleAmount(SBvisible);
        SpeedScrollBar.setBackground(Color.gray);

        ObjSizeScrollBar=new Scrollbar(Scrollbar.HORIZONTAL);
        ObjSizeScrollBar.setMaximum(MAXObj);
        ObjSizeScrollBar.setMinimum(MINObj);
        ObjSizeScrollBar.setUnitIncrement(SBunit);
        ObjSizeScrollBar.setBlockIncrement(SBblock);
        ObjSizeScrollBar.setValue(SOBJ);
        ObjSizeScrollBar.setVisibleAmount(SBvisible);
        ObjSizeScrollBar.setBackground(Color.gray);

       

        Obj.setBackground(Color.white);


        add(SpeedScrollBar);
        add(ObjSizeScrollBar);
        add(SPEEDL);
        add(SIZEL);
        add(Obj);

        
        SpeedScrollBar.addAdjustmentListener(this);
        ObjSizeScrollBar.addAdjustmentListener(this);

        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setMinimumSize(getPreferredSize());
        setBounds(WinLeft, WinTop, WIDTH, HEIGHT); //size and position the frame
        validate();//validate the layout

    }

    private void MakeSheet(){

        I=getInsets();
        ScreenWidth=WinWidth-I.left-I.right;
        ScreenHeight=WinHeight-I.top-2*(BUTTONH+BUTTONHS)-I.bottom;
        
        setSize(WinWidth,WinHeight);
        CENTER=(ScreenWidth/2);
        BUTTONW=ScreenWidth/11;
        BUTTONS=BUTTONW/4;
        setBackground(Color.lightGray);

        ScrollBarW = 2*BUTTONW;

    }

    private void SizeScreen(){

        //position the buttons
        Start.setLocation(CENTER-2*(BUTTONW+BUTTONS)-BUTTONW/2,ScreenHeight+BUTTONHS+I.top);
        Shape.setLocation(CENTER-BUTTONW-BUTTONS-BUTTONW/2,ScreenHeight+BUTTONHS+I.top);
        Tail.setLocation(CENTER-BUTTONW/2,ScreenHeight+BUTTONHS+I.top);
        Clear.setLocation(CENTER+BUTTONS+BUTTONW/2,ScreenHeight+BUTTONHS+I.top);
        Quit.setLocation(CENTER+BUTTONW+2*BUTTONS+BUTTONW/2,ScreenHeight+BUTTONHS+I.top);

        //size the buttons
        Start.setSize(BUTTONW,BUTTONH);
        Shape.setSize(BUTTONW,BUTTONH);
        Tail.setSize(BUTTONW,BUTTONH);
        Clear.setSize(BUTTONW,BUTTONH);
        Quit.setSize(BUTTONW,BUTTONH);

        SpeedScrollBar.setLocation(I.left+BUTTONS,ScreenHeight+BUTTONHS+I.top);
        ObjSizeScrollBar.setLocation(WinWidth-ScrollBarW-I.right-BUTTONS,ScreenHeight+BUTTONHS+I.top);
        SPEEDL.setLocation(I.left+BUTTONS,ScreenHeight+BUTTONHS+BUTTONH+I.top);
        SIZEL.setLocation(WinWidth-ScrollBarW-I.right,ScreenHeight+BUTTONHS+BUTTONH+I.top);
        SpeedScrollBar.setSize(ScrollBarW,SCROLLBARH);
        ObjSizeScrollBar.setSize(ScrollBarW,SCROLLBARH);
        SPEEDL.setSize(ScrollBarW,BUTTONH);
        SIZEL.setSize(ScrollBarW,SCROLLBARH);
        Obj.setBounds(I.left,I.top,ScreenWidth,ScreenHeight);
    }

    public void stop(){

        run = false;
       
        Start.removeActionListener(this);
        Shape.removeActionListener(this);
        Clear.removeActionListener(this);
        Tail.removeActionListener(this);
        Quit.removeActionListener(this);

       
        this.removeComponentListener(this);
        this.removeWindowListener(this);

        SpeedScrollBar.removeAdjustmentListener(this);
        ObjSizeScrollBar.removeAdjustmentListener(this);

       
       
        dispose();
        
        thethread.interrupt();

        
        System.exit(0);
    }

    public void start(){

        Obj.repaint();

        if(thethread == null){ //create a thread if it doesnt exist

            thethread = new Thread(this);//create a new thread
            thethread.start();//start the thread

        }
    }

 
    public void run(){

        while(run){
            
           

                started = true;
                try{
                    Thread.sleep(delay);
                }
                catch (InterruptedException e){}

              if(!TimePause){   
                Obj.move();//MOVE method
                Obj.repaint();
               
               
            }

           // Obj.setxold(Obj.getx());
           // Obj.setyold(Obj.gety());
        }

        stop();

    }

    public void actionPerformed(ActionEvent e) {


        Object source = e.getSource(); 

        if(source==Start){
            if(Start.getLabel().equals("Run")){ 

                Start.setLabel("Pause");  
                TimePause = false;  
                started = true;
                //thethread.start(); //not sure if needed          
            }
            else{

                Start.setLabel("Run");  
                TimePause = true;
                started = false;
                //thethread.interrupt();               
            }
        }

        if(source==Shape){

           // if(TimePause){ 
           //     Obj.Clear();
           // }
        
            if(Shape.getLabel()=="Circle"){

                Shape.setLabel("Square"); 
                Obj.rectangle(false);   
               
            }
            else{

                Shape.setLabel("Circle"); 
                Obj.rectangle(true);
            }
            Obj.repaint();
        }

        if(source==Tail){
            
            if(Tail.getLabel()=="Tail"){

                Tail.setLabel("No Tail"); 
                started = true;
                Obj.settail(true);
            }
            else{

                Tail.setLabel("Tail"); 
                started = false;
                Obj.settail(false);
            }

        }

        if(source==Clear){  

            Obj.Clear();
            Obj.repaint();
        }

        if(source==Quit){  

            stop();
        }


        

    }

    public void adjustmentValueChanged(AdjustmentEvent e) {

        int TS;
        Scrollbar sb = (Scrollbar)e.getSource(); //get the scrollbar that triggered the event


        if(sb == SpeedScrollBar) {

            int validatespeed = SpeedScrollBar.getValue();

            delay = 100 - validatespeed + 1;


           if (thethread != null) {
                thethread.interrupt();
            }
        }
        if(sb==ObjSizeScrollBar){

            TS = e.getValue();//get the value
            TS = ( TS/2)*2 + 1;//Make odd to account for center position i.e. +
            

            //int validatesize = sb.getValue();

            if(TS <= ScreenHeight && TS <= ScreenWidth){// might be < ijnsted of <=

                  Obj.update(TS);//change the size in the drawing object              
            }
            else{
                ObjSizeScrollBar.setValue(SObj); 
                          
            }
            
            if(!Obj.gettail()){
                Obj.Clear();
            }

            
            
        }
        Obj.repaint();//force a repaint






    }


    


    public void windowClosing(WindowEvent e){
        stop();
        
    }

    public void windowClosed(WindowEvent e){}
    public void windowOpened(WindowEvent e){}
    public void windowActivated(WindowEvent e){}
    public void windowDeactivated(WindowEvent e){}
    public void windowIconified(WindowEvent e){}
    public void windowDeiconified(WindowEvent e){}

    public void CheckSize(){
        int x = Obj.getx();
        int y = Obj.gety();
        
        int size = Obj.getobjs();

        int right = x + (size - 1)/2;
        int bottom = y + (size - 1)/2;

        int left = x - (size - 1)/2;
        int top = y - (size - 1)/2;

        if(right > ScreenWidth){

            x = ScreenWidth - (size - 1)/2;
            //Obj.update(obj.getSizeObj);

        }
        if(bottom > ScreenHeight){

            y = ScreenWidth - (size - 1)/2;

        }
        if(left < 0){

            x = (size - 1)/2;

        }
        if(top < 0){

            y = (size - 1)/2;

        }

        Obj.setx(x);
        Obj.sety(y);

       
    }

    public void componentResized(ComponentEvent e) {
        WinWidth = getWidth();
        WinHeight = getHeight();

        MakeSheet();

        Obj.reSize(ScreenWidth, ScreenHeight);

        

        CheckSize();//NOT SURE

        SizeScreen();

    
        
    }
    public void componentHidden(ComponentEvent e) {}
    public void componentShown(ComponentEvent e) {}
    public void componentMoved(ComponentEvent e){}


   
 
 
    public static void main(String[] args){
       new Bounce();//create an object
      
    }
   

 

}

class Objc extends Canvas{

    private static final long serialVersionUID = 11L;

    private int ScreenWidth;
    private int ScreenHeight;
    private int SObj;
    private int x, y;
    private boolean rect = true;
    private boolean clear = false;
    private boolean tail;

    private int ymin;
    private int ymax;
    private int xmin;
    private int xmax;
    private int xold;
    private int yold;

     boolean right;
     boolean down;
    //private boolean left;
    //private boolean up;



    public Objc(int SB, int w, int h){
        ScreenWidth = w;
        ScreenHeight = h;
        SObj = SB;
        rect = true;
        clear = false;
        y = ScreenHeight/2;
        x = ScreenWidth/2;

      
        right = true;
        down = true;

        ymin = 0;
        ymax = ScreenHeight;
        xmin = 0;
        xmax = ScreenWidth;

        
    }

    

   // public int getxold(){return xold;}
   // public void setxold(int a){xold = a;}
   // public int getyold(){return yold;}
   // public void setyold(int a){yold = a;}

    public void calculatemins(){

        int offset = (SObj - 1)/2;
        xmin = offset;
        xmax = ScreenWidth - offset;
        ymin = offset;
        ymax =  ScreenHeight - offset;
    }

    public boolean checkxin(){
        calculatemins();
        return (x <= xmin || x>= xmax);
    }

    public boolean checkyin(){
        calculatemins();
        return (y <= ymin || y>= ymax);
    }

    public void rectangle(boolean r){
        rect = r; 
    }

    public void update(int NS){
        SObj = NS; 
    }

    public void reSize(int w, int h){
        ScreenWidth = w;
        ScreenHeight = h;
        y = ScreenHeight/2;
        x = ScreenWidth/2;

        calculatemins();
    }


    public void Clear(){
        clear = true; 
    }

    public int getx(){return x;}
    public int gety(){return y;}
    public void setx(int a){x=a;}
    public void sety(int a){y=a;}

    public int getobjs(){return SObj;}
    public void setobjs(int a){SObj=a;}

    public boolean gettail(){return tail;}
    public void settail(boolean a){tail=a;}


    public void paint(Graphics g){      
        update(g);
    }

    public void update(Graphics g){    

        
        if(clear){

            super.paint(g);
            clear = false;
            g.setColor(Color.red);
            g.drawRect(0,0,ScreenWidth-1,ScreenHeight-1);
        }

        if(!tail){
            g.setColor(getBackground());
           // g.setColor(Color.lightGray);
             if(rect){
                 g.fillRect(xold-(SObj-1)/2 - 1,yold-(SObj-1)/2-1,SObj+2,SObj+2);
             }
             else{
                 g.fillOval(xold-(SObj-1)/2 - 1,yold-(SObj-1)/2-1,SObj+2,SObj+2);
             }  
         }

       

        if(rect){
            g.setColor(Color.lightGray);
            g.fillRect(x-(SObj-1)/2,y-(SObj-1)/2,SObj,SObj);
            g.setColor(Color.black);
            g.drawRect(x-(SObj-1)/2,y-(SObj-1)/2,SObj-1,SObj-1);
        }
        else
        {
            g.setColor(Color.lightGray);
            g.fillOval(x-(SObj-1)/2,y-(SObj-1)/2,SObj,SObj);
            g.setColor(Color.black);
            g.drawOval(x-(SObj-1)/2,y-(SObj-1)/2,SObj-1,SObj-1);
        }
        g.setColor(Color.red);
        g.drawRect(0,0,ScreenWidth-1,ScreenHeight-1);
        

        xold = x;
        yold = y;
        

    }

    public void move() {
    

        xold = x;
        yold = y;

        if (checkxin()) {
            right = !right;
        }
        if (checkyin()) {
            down = !down;
        }

        x += (right ? 1 : -1);
        y += (down ? 1 : -1);
        repaint();
    
    }



  

}
