import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;


public class Bullet extends Polygon{
	
	final static double length = 5;
	
	double[] pos = {0, 0};				//Position of the head of the bullet
	double[] velocity;
	
	boolean alive = true;
	
	boolean collidable = true;
	
	Color color = Color.WHITE;

	public Bullet(Point startPos, double[] velocity){
		
		this.pos[0] = startPos.x;
		this.pos[1] = startPos.y;
		
		this.velocity = velocity;
		
		double velocityVectorLength = Point.distance(0, 0, velocity[0], velocity[1]);
		double[] normalVelocity = {
				velocity[0] / velocityVectorLength,
				velocity[1] / velocityVectorLength
		};
		
		//create ourselves with the tail point behind us in the direction of our initial velocity
		this.addPoint(startPos.x, startPos.y);
		this.addPoint((int) (startPos.x - normalVelocity[0]*Bullet.length), (int) (startPos.y - normalVelocity[1]*Bullet.length));
	}
	
	public GameObject[] checkHits(GameObject[] objs){
		
		if(!this.isCollidable() || !this.isAlive()){
			return new GameObject[0];
		}
		
		ArrayList<GameObject> hits = new ArrayList<GameObject>();
		
		for(GameObject obj : objs){
			if(this.intersects(obj.getBounds2D())){
//				System.out.println("HIT");
				hits.add(obj);
			}
		}
		
		return hits.toArray(new GameObject[hits.size()]);
	}
	
	public boolean isAlive() {
		return alive;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}
	
	public boolean isCollidable() {
		return collidable;
	}

	public void setCollidable(boolean collidable) {
		this.collidable = collidable;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public void draw(Graphics2D g2){
//		System.out.printf("pos: (%d, %d)\tvelocity: (%f, %f)\txpoints: [%d, %d]\typoints: [%d, %d]\n",
//				this.getPos().x, this.getPos().y,
//				velocity[0], velocity[1],
//				xpoints[0], xpoints[1],                               
//				ypoints[0], ypoints[1]                                
//						);                                            
		
		Color oldColor = g2.getColor();
		g2.setColor(this.getColor());
		                                                              
		g2.draw(this);
		
		g2.setColor(oldColor);
	}                                                                 
	                                                                  
	public Point getPos(){                                            
		return new Point((int)this.pos[0], (int)this.pos[1]);         
	}                                                                 
	                                                                  
	public double[] getVelocity(){                                    
		return this.velocity;                                         
	}                                                                 
	                                                                  
	private void move(){
		
		this.pos[0] += this.velocity[0];
		this.pos[1] += this.velocity[1];
		
		super.translate((int)(this.pos[0]-this.xpoints[0]), (int)(this.pos[1]-this.ypoints[0]));
	}
	
	public void tick(){
		
		this.move();
	}
}
