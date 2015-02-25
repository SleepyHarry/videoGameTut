import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class SpaceShip extends GameObject{
	
	public static int[] arrBasePolyX = {-14, 13, -14, -6};
	public static int[] arrBasePolyY = {-15, 0, 15, 0, -15};
	
	double acceleration = 0.1;
	double dOmega = 0.0025;
	double dTheta = 0.1;
	double dThetaMax;
	
	public SpaceShip(int xLim, int yLim, double size){//, Point[] arrPoly) {
		
		super(arrBasePolyX, arrBasePolyY, size, new Point(xLim, yLim));
		
		//move ourselves to the centre of the screen
//		this.translate(this.limit.x/2, this.limit.y/2);
		this.setPos(this.limit.x/2, this.limit.y/2);
		
		this.setCollidable(false);
	}
	
	public void handleKeyPress(ArrayList<Integer> keyPressStack){
		//decides what to do when given input from keyboard keys pressed
		
		for(int k : keyPressStack){
			//It's called a stack, but really we just iterate through it in order, then clear it
			switch(k){
			case KeyEvent.VK_BACK_QUOTE:
				this.setCollidable(!this.isCollidable());
			}
		}
		
		//does this propogate? Yes
		keyPressStack.clear();
	}
	
	public void handleKeysHeld(ArrayList<Integer> keysDown){
		//decides what to do when given input from keyboard keys held down
		
		if(keysDown.contains(KeyEvent.VK_W)){
			this.addRelativeVelocity( this.acceleration,  0);
		}
		
		if(keysDown.contains(KeyEvent.VK_A)){
			this.setTheta(this.getTheta()-dTheta);
//			this.setOmega(this.getOmega()-dOmega);
		}
		
		if(keysDown.contains(KeyEvent.VK_S)){
			this.addRelativeVelocity(-this.acceleration,  0);
		}
		
		if(keysDown.contains(KeyEvent.VK_D)){
//			this.setTheta(this.getTheta()+dTheta);
			this.setOmega(dTheta);
		}
	}
	
	public void tick(){
		
		super.tick();
		
		//damp rotation
		this.omega *= 0.9;
	}
}
