import java.awt.Point;
import java.awt.Rectangle;

public class Rock extends GameObject{
	
	public static double sizeThreshold = 1/4.0;
	
	public static int[] arrBasePolyX = {-8,-1,8,16,9,18,8,-4,-10,-17,-13,-17};//,10};
	public static int[] arrBasePolyY = {-15,-10,-14,-7,-2,5,16,13,16,7,1,-8};//,0};
	
	double maxSpeed = 5;	//not actually a max speed, max speed is sqrt(50)

	public Rock(int xLim, int yLim, double size, GameBoard game){//, Point[] arrPoly) {
		
		super(arrBasePolyX, arrBasePolyY, size, game);
		
		Rectangle bBox = this.getBounds();
		
		this.setPos(
				(int)(Math.random()*(limit.x-bBox.width*2)+bBox.width),
				(int)(Math.random()*(limit.y-bBox.height*2)+bBox.height)
				);
		
//		System.out.println(this.getBounds());
		
		this.setVelocity((Math.random()-0.5)*maxSpeed, (Math.random()-0.5)*maxSpeed);
		
		this.theta = Math.random()*Math.PI*2;
		this.omega = (Math.random()-0.5)*0.1;
		
//		this.arrPoly = arrPoly;
	}
	
	//TODO: make it possible to split into more than 2
	public Rock[] split(){
		//splits this Rock into two Rocks with half (TODO: random?) this Rock's size each
		//also gives them appropriate velocities
		
		Rock[] splits = {
				new Rock(this.limit.x, this.limit.y, this.size/2, this.game),
				new Rock(this.limit.x, this.limit.y, this.size/2, this.game)
		};
		
		Point oldPos = this.getPos();
		double[] oldVel = this.getVelocity();
		
		splits[0].setPos((int) (oldPos.x + Math.cos(0.5)*oldVel[0]), (int) (oldPos.y + Math.sin(0.5)*oldVel[1]));
//		splits[0].setVelocity(this.getVelocity());
		
		splits[1].setPos((int) (oldPos.x + Math.cos(-0.5)*oldVel[0]), (int) (oldPos.y + Math.sin(-0.5)*oldVel[1]));
//		splits[1].setVelocity(this.getVelocity());
		
		return splits;
	}
}
