import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public abstract class GameObject extends Polygon{
	
	final GameBoard game;
	final Point limit;
	
	boolean collidable = true;
	
	double[] actualPos = {0, 0};
	
	double theta;	//angle in radians we are rotated from +ve x-axis CCW
	
	double[] velocity = {0, 0};
	double omega = 0;	//angular velocity
	
	final double size;
	
	public static int[] arrBasePolyX;
	public static int[] arrBasePolyY;
	
	Color color = Color.WHITE;
	
	boolean alive = true;

	public GameObject(int[] arrPolyX, int[] arrPolyY, double size, GameBoard game) {
		
//		super(arrSPolyX, arrSPolyY, length);
		this.size = size;
		
		for(int i=0; i<arrPolyX.length; i++){
			this.addPoint((int)(arrPolyX[i]*size), (int)(arrPolyY[i]*size));
		}
		
		this.game = game;
		this.limit = new Point(game.width, game.height);
	}
	
	public boolean isAlive() {
		return alive;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public boolean isCollidable() {
		return collidable;
	}

	public void setCollidable(boolean collidable) {
		this.collidable = collidable;
	}
	
	public void setPos(int x, int y){
		
		this.actualPos[0] = (double)x;
		this.actualPos[1] = (double)y;
		
		Rectangle r = this.getBounds();
		
		this.translate((int)(x-r.getCenterX()), (int)(y-r.getCenterY()));
	}
	
	public Point getPos(){
		
		return new Point((int)this.actualPos[0], (int)this.actualPos[1]);
	}

	public double[] getVelocity() {
		return velocity;
	}

	public void setVelocity(double velocityX, double velocityY) {
		this.velocity[0] = velocityX;
		this.velocity[1] = velocityY;
	}
	
	public void setVelocity(double[] newVelocity) {
		this.velocity = newVelocity;
	}
	
	public void addVelocity(double deltaX, double deltaY) {
		
		this.velocity[0] += deltaX;
		this.velocity[1] += deltaY;
	}
	
	public void addRelativeVelocity(double deltaX, double deltaY){
		//adds velocity to the body adjusted for rotation
		AffineTransform aT = new AffineTransform();
		aT.rotate(this.theta);
		
		double[] deltaV = {deltaX, deltaY};
		double[] deltaVnew = {0, 0};
		aT.deltaTransform(deltaV, 0, deltaVnew, 0, 1);
//		System.out.println(newVelocity[0]+" "+newVelocity[1]);
		
		this.velocity[0] += deltaVnew[0];
		this.velocity[1] += deltaVnew[1];
	}
	
	public double getTheta() {
		return theta;
	}

	public void setTheta(double theta) {
		this.theta = theta;
	}

	public double getOmega() {
		return omega;
	}

	public void setOmega(double omega) {
		this.omega = omega;
	}
	
	public void addOmega(double dOmega){
		this.omega += dOmega;
	}

	public void move(){
		
		this.rotate();
		
		Rectangle r = this.getBounds();
		
		double[] oldPos = {r.getCenterX(), r.getCenterY()};
		
		this.actualPos[0] += this.velocity[0];
		this.actualPos[1] += this.velocity[1];
		
//		System.out.printf("Vel: (%f, %f)\toldPos: (%f, %f)\taPos: (%f, %f)\n",
//				this.velocity[0], this.velocity[1],
//				oldPos[0], oldPos[1],
//				this.actualPos[0], this.actualPos[1]);
		
		//doing it this way means that we hold on to double precision for as long as possible
		this.translate((int)(this.actualPos[0]-oldPos[0]), (int)(this.actualPos[1]-oldPos[1]));
		
		if(this.actualPos[0] <= 0-r.width/2 || this.limit.x <= this.actualPos[0]+r.width/2){
			//obj is outside bounds, loop it
			
			//add this.limit.x first to deal with negative r.x
			double newX = (this.actualPos[0] + this.limit.x) % this.limit.x;
			
			this.translate((int) (newX - this.actualPos[0]), 0);
			this.actualPos[0] = newX;
			
//			System.out.printf("x: %d\tnewX: %d\n", r.x, newX);
		}
		
		if(actualPos[1] <= 0-r.height/2 || this.limit.y <= actualPos[1]+r.height/2){
			//ship is outside bounds, loop it

			//add this.limit.x first to deal with negative r.x
			double newY = (this.actualPos[1] + this.limit.y) % this.limit.y;
			
			this.translate(0, (int) (newY - this.actualPos[1]));
			this.actualPos[1] = newY;
			
//			System.out.printf("y: %d\tnewY: %d\n", r.y, newY);
		}
	}
	
	public void rotate(){
		
		this.theta += this.omega;
	}
	
	public GameObject[] checkCollisions(GameObject[] objs){
		if(!this.isCollidable()){
			//if we're not collidable, then there aren't any collisions to test for
			return new GameObject[0];
		}
		//test against all objects in objs for collisions with this, and return array of objects that we collided with
//		System.out.println("Checking "+objs.length+" objects");
		ArrayList<GameObject> collisions = new ArrayList<GameObject>();
		
		for(GameObject obj : objs){
			if(this != obj && this.intersects(obj.getBounds2D())){
//				System.out.println("hit");
//				double[] temp = this.getVelocity();
//				this.setVelocity(obj.getVelocity());
//				obj.setVelocity(temp);
				
				collisions.add(obj);
			}
		}
		
		return collisions.toArray(new GameObject[collisions.size()]);
	}
	
	//TODO: Make this work with ArrayList<X>, where X extends GameObject
	public GameObject[] checkCollisions(ArrayList<GameObject> objs){
		//overload to make calls to checkCollisions more readable
		return this.checkCollisions(objs.toArray(new GameObject[objs.size()]));
	}

	public void handleKeyPress(){
		//decides what to do when given input from keyboard keys pressed
		//keysDown is currently only {w, a, s, d} bools
		
		//checks this.game.keysHeld
		
		//OVERRIDE THIS IN SUBCLASSES
	}
	
	public void handleKeysHeld(){
		//decides what to do when given input from keyboard keys held down
		//keysDown is currently only {w, a, s, d} bools
		
		//checks this.game.keysDown
		
		//OVERRIDE THIS IN SUBCLASSES
	}
	
	public void tick(){
		
		this.handleKeyPress();
		this.handleKeysHeld();
		
		this.move();
	}
	
	public void draw(Graphics2D g2){
		
		AffineTransform oldT = g2.getTransform();
		
		Rectangle2D r = this.getBounds2D();
		double centerX = r.getCenterX();
		double centerY = r.getCenterY();
		
		g2.translate( centerX,  centerY);
		g2.rotate(this.theta);
		g2.translate(-centerX, -centerY);
		
		Color oldColor = g2.getColor();
		g2.setColor(this.getColor());
		
		g2.draw(this);
		
		g2.setTransform(oldT);
		g2.setColor(oldColor);
	}
}
