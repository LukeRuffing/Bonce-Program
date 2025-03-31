// Description: A simple bouncing ball application that demonstrates the use of threads, scrollbars, and buttons in Java.
//Author: Luke Ruffing
//Class: CMSC-3080-001
//Date: 3/12/2025

// Importing necessary packages
import java.awt.*;
import java.awt.event.*;
import java.io.*;

//public class Bounce extends Frame 
public class Bounce extends Frame 
    implements WindowListener, 
               ComponentListener, 
               ActionListener, 
               AdjustmentListener, 
               Runnable {

    private static final long serialVersionUID = 10L; // Serial version ID for serialization

    // Constants
    private final int WIDTH = 640; // Frame width
    private final int HEIGHT = 400; // Frame height
    private final int BUTTONH=20; // Button height
    private final int BUTTONHS = 5; // Button spacing
    private final int MAXObj=100;// Max object size
    private final int MINObj=10;// Min object size
    private final int SPEED=50;// Initial speed
    private final int SBvisible=10;// Scrollbar visibility
    private final int SBunit=1;// Scrollbar unit step size
    private final int SBblock=10;// Scrollbar block step size
    private final int SCROLLBARH=BUTTONH;// Scrollbar height
    private final int SOBJ=21;// Initial object width

    // Variables for frame dimensions and object properties
    private int WinWidth=WIDTH;// Initial frame width
    private int WinHeight=HEIGHT;// Initial frame height
    private int ScreenWidth;// Drawing screen width
    private int ScreenHeight;// Drawing screen height
    private int WinTop=10;// Top of frame
    private int WinLeft=10;// Left side of frame
    private int BUTTONW=50;// Initial button width
    private int CENTER=(WIDTH/2);// Initial screen center
    private int BUTTONS=BUTTONW/4;// Initial button spacing

    private final double DELAY = 10; // Delay for thread sleep
    private int SObj=SOBJ;// Initial object width
    private int SpeedSBmin=1;// Speed scrollbar min value
    private int SpeedSBmax=100+SBvisible;// Speed scrollbar max with visible offset
    private int SpeedSBinit=SPEED;// Initial speed scrollbar value
    private int ScrollBarW;// Scrollbar width
    
    private Insets I; // Insets for frame padding

    // Buttons
    Button Start, Shape,Clear,Tail,Quit;

    // Object for drawing
    private Objc Obj;
    private Label SPEEDL = new Label("Speed",Label.CENTER);
    private Label SIZEL = new Label("Size",Label.CENTER);
    Scrollbar SpeedScrollBar, ObjSizeScrollBar;
    private Thread thethread;// Thread for timer delay
   
    boolean run; // Flag to control running state
    boolean TimePause; // Flag to control pause state
    boolean started; // Flag to check if started
    int speed; // Variable for speed
    int delay; // Variable for delay

    boolean tail; // Flag to control tail drawing

    // Constructor to initialize the frame
    Bounce() {
        setTitle("Bouncing Ball"); // Set frame title
        setLayout(null); // Set layout to null
        setVisible(true); // Make frame visible
        MakeSheet(); // Initialize frame dimensions
        started = false; // Set started flag to false
        try{
            initComponents(); // Initialize components
        }catch (Exception e){
            e.printStackTrace();}
        SizeScreen(); // Set screen size
        start(); // Start the thread
    }

    // Method to initialize components
    public void initComponents() throws Exception, IOException{
        Obj = new Objc(SObj, ScreenWidth, ScreenHeight); // Create drawing object
        TimePause = true; // Set pause flag
        run = true; // Set running flag
        delay = (int) DELAY; // Set delay
        Start = new Button("Run"); // Create buttons
        Shape = new Button("Circle");
        Clear = new Button("Clear");
        Tail = new Button("No Tail");
        Quit = new Button("Quit");
        add("Center",Start); // Add buttons to frame
        add("Center",Shape);
        add("Center",Tail);
        add("Center",Clear);
        add("Center",Quit);
        Start.addActionListener(this); // Add action listeners to buttons
        Shape.addActionListener(this);
        Tail.addActionListener(this);
        Clear.addActionListener(this);
        Quit.addActionListener(this);
        this.addComponentListener(this); // Add component listener to frame
        this.addWindowListener(this); // Add window listener to frame
        SpeedScrollBar=new Scrollbar(Scrollbar.HORIZONTAL); // Create scrollbars
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
        add(SpeedScrollBar); // Add scrollbars to frame
        add(ObjSizeScrollBar);
        add(SPEEDL);
        add(SIZEL);
        add(Obj);
        SpeedScrollBar.addAdjustmentListener(this); // Add adjustment listeners to scrollbars
        ObjSizeScrollBar.addAdjustmentListener(this);
        setPreferredSize(new Dimension(WIDTH, HEIGHT)); // Set preferred size of frame
        setMinimumSize(getPreferredSize()); // Set minimum size of frame
        setBounds(WinLeft, WinTop, WIDTH, HEIGHT); // Size and position the frame
        validate();// Validate the layout
    }

    // Method to initialize frame dimensions
    private void MakeSheet(){
        I=getInsets(); // Get insets
        ScreenWidth=WinWidth-I.left-I.right; // Calculate screen width
        ScreenHeight=WinHeight-I.top-2*(BUTTONH+BUTTONHS)-I.bottom; // Calculate screen height
        setSize(WinWidth,WinHeight); // Set frame size
        CENTER=(ScreenWidth/2); // Calculate center
        BUTTONW=ScreenWidth/11; // Calculate button width
        BUTTONS=BUTTONW/4; // Calculate button spacing
        setBackground(Color.lightGray); // Set background color
        ScrollBarW = 2*BUTTONW; // Calculate scrollbar width
    }

    // Method to set screen size and position buttons
    private void SizeScreen(){
        Start.setLocation(CENTER-2*(BUTTONW+BUTTONS)-BUTTONW/2,ScreenHeight+BUTTONHS+I.top); // Position buttons
        Shape.setLocation(CENTER-BUTTONW-BUTTONS-BUTTONW/2,ScreenHeight+BUTTONHS+I.top);
        Tail.setLocation(CENTER-BUTTONW/2,ScreenHeight+BUTTONHS+I.top);
        Clear.setLocation(CENTER+BUTTONS+BUTTONW/2,ScreenHeight+BUTTONHS+I.top);
        Quit.setLocation(CENTER+BUTTONW+2*BUTTONS+BUTTONW/2,ScreenHeight+BUTTONHS+I.top);
        Start.setSize(BUTTONW,BUTTONH); // Size buttons
        Shape.setSize(BUTTONW,BUTTONH);
        Tail.setSize(BUTTONW,BUTTONH);
        Clear.setSize(BUTTONW,BUTTONH);
        Quit.setSize(BUTTONW,BUTTONH);
        SpeedScrollBar.setLocation(I.left+BUTTONS,ScreenHeight+BUTTONHS+I.top); // Position scrollbars
        ObjSizeScrollBar.setLocation(WinWidth-ScrollBarW-I.right-BUTTONS,ScreenHeight+BUTTONHS+I.top);
        SPEEDL.setLocation(I.left+BUTTONS,ScreenHeight+BUTTONHS+BUTTONH+I.top); // Position labels
        SIZEL.setLocation(WinWidth-ScrollBarW-I.right,ScreenHeight+BUTTONHS+BUTTONH+I.top);
        SpeedScrollBar.setSize(ScrollBarW,SCROLLBARH); // Size scrollbars
        ObjSizeScrollBar.setSize(ScrollBarW,SCROLLBARH);
        SPEEDL.setSize(ScrollBarW,BUTTONH); // Size labels
        SIZEL.setSize(ScrollBarW,SCROLLBARH);
        Obj.setBounds(I.left,I.top,ScreenWidth,ScreenHeight); // Set drawing object bounds
    }

    // Method to stop the application
    public void stop(){
        run = false; // Set running flag to false
        Start.removeActionListener(this); // Remove action listeners from buttons
        Shape.removeActionListener(this);
        Clear.removeActionListener(this);
        Tail.removeActionListener(this);
        Quit.removeActionListener(this);
        this.removeComponentListener(this); // Remove component listener from frame
        this.removeWindowListener(this); // Remove window listener from frame
        SpeedScrollBar.removeAdjustmentListener(this); // Remove adjustment listeners from scrollbars
        ObjSizeScrollBar.removeAdjustmentListener(this);
        dispose(); // Dispose the frame
        thethread.interrupt(); // Interrupt the thread
        System.exit(0); // Exit the application
    }

    // Method to start the application
    public void start(){
        Obj.repaint(); // Repaint the drawing object
        if(thethread == null){ // Create a thread if it doesn't exist
            thethread = new Thread(this); // Create a new thread
            thethread.start(); // Start the thread
        }
    }

    // Method to run the thread
    public void run(){
        while(run){ // While running
            started = true; // Set started flag to true
            try{
                Thread.sleep(delay); // Sleep for delay duration
            }
            catch (InterruptedException e){}
            if(!TimePause){   
                Obj.move(); // Move the object
                Obj.repaint(); // Repaint the object
            }
        }
        stop(); // Stop the application
    }

    // Method to handle button actions
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource(); 
        if(source==Start){
            if(Start.getLabel().equals("Run")){ 
                Start.setLabel("Pause");  
                TimePause = false;  
                started = true;
                        
            }
            else{
                Start.setLabel("Run");  
                TimePause = true;
                started = false;
                             
            }
        }
        if(source==Shape){
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

    // Method to handle scrollbar adjustments
    public void adjustmentValueChanged(AdjustmentEvent e) {
        int TS;
        Scrollbar sb = (Scrollbar)e.getSource(); // Get the scrollbar that triggered the event
        if(sb == SpeedScrollBar) {
            int validatespeed = SpeedScrollBar.getValue();
            delay = 100 - validatespeed + 1;
            if (thethread != null) {
                thethread.interrupt();
            }
        }
        if(sb==ObjSizeScrollBar){
            TS = e.getValue();// Get the value
            TS = ( TS/2)*2 + 1;// Make odd to account for center position
            if(TS <= ScreenHeight && TS <= ScreenWidth){// Validate size
                Obj.update(TS);// Change the size in the drawing object  
                Obj.stayInBounds(ScreenWidth, ScreenHeight); // Ensure the object stays within bounds     
            }
            else{
                ObjSizeScrollBar.setValue(SObj); 
            }
            if(!Obj.gettail()){
                Obj.Clear();
            }
        }
        Obj.repaint();// Force a repaint
    }

    // Window listener methods
    public void windowClosing(WindowEvent e){
        stop();
    }
    public void windowClosed(WindowEvent e){}
    public void windowOpened(WindowEvent e){}
    public void windowActivated(WindowEvent e){}
    public void windowDeactivated(WindowEvent e){}
    public void windowIconified(WindowEvent e){}
    public void windowDeiconified(WindowEvent e){}

    // Method to check object size within bounds
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

    // Component listener methods
    public void componentResized(ComponentEvent e) {
        WinWidth = getWidth();
        WinHeight = getHeight();
        MakeSheet();
        Obj.reSize(ScreenWidth, ScreenHeight);
        CheckSize();
        SizeScreen();
    }
    public void componentHidden(ComponentEvent e) {}
    public void componentShown(ComponentEvent e) {}
    public void componentMoved(ComponentEvent e){}

    // Main method to create the frame
    public static void main(String[] args){
       new Bounce(); // Create an object
    }
}

// Class for drawing object
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

    // Constructor to initialize object
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

    // Method to calculate minimum bounds
    public void calculatemins(){
        int offset = (SObj - 1)/2;
        xmin = offset;
        xmax = ScreenWidth - offset;
        ymin = offset;
        ymax =  ScreenHeight - offset;
    }

    // Method to check x bounds
    public boolean checkxin(){
        calculatemins();
        return (x <= xmin || x>= xmax);
    }

    // Method to check y bounds
    public boolean checkyin(){
        calculatemins();
        return (y <= ymin || y>= ymax);
    }

    // Method to set shape to rectangle
    public void rectangle(boolean r){
        rect = r; 
    }

    // Method to update object size
    public void update(int NS){
        SObj = NS; 
    }

    public void stayInBounds(int newScreenWidth, int newScreenHeight) {
        int half = SObj / 2;
    
        // Ensure the object stays inside the right and bottom edges
        if (x + half >= newScreenWidth) {
            x = newScreenWidth - half - 2; // Adjust to stay fully within the right edge
            right = !right; // Reverse direction
        }
        if (y + half >= newScreenHeight) {
            y = newScreenHeight - half - 2; // Adjust to stay fully within the bottom edge
            down = !down; // Reverse direction
        }
    
        // Ensure the object doesn't go beyond the left and top edges
        if (x - half <= 0) {
            x = half; // Adjust to stay fully within the left edge
            right = !right; // Reverse direction
        }
        if (y - half <= 0) {
            y = half; // Adjust to stay fully within the top edge
            down = !down; // Reverse direction
        }
    }

    // Method to resize object
    public void reSize(int w, int h){
        ScreenWidth = w;
        ScreenHeight = h;
        y = ScreenHeight/2;
        x = ScreenWidth/2;
        stayInBounds(w,h); // Ensure the object stays within new bounds
        calculatemins();
    }

    // Method to clear object
    public void Clear(){
        clear = true; 
    }

    // Getter and setter methods
    public int getx(){return x;}
    public int gety(){return y;}
    public void setx(int a){x=a;}
    public void sety(int a){y=a;}
    public int getobjs(){return SObj;}
    public void setobjs(int a){SObj=a;}
    public boolean gettail(){return tail;}
    public void settail(boolean a){tail=a;}

    // Method to paint object
    public void paint(Graphics g){      
        update(g);
    }

    // Method to update object
    public void update(Graphics g){    
        if(clear){
            super.paint(g);
            clear = false;
            g.setColor(Color.red);
            g.drawRect(0,0,ScreenWidth-1,ScreenHeight-1);
        }
        if(!tail){
            g.setColor(getBackground());
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

    // Method to move object
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