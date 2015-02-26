import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class SpaceShip extends GameObject{
	
	static int[] arrBasePolyX = {-14, 13, -14, -6};
	static int[] arrBasePolyY = {-15, 0, 15, 0, -15};
	
	double acceleration = 0.1;
	double dOmega = 0.0025;
	double dTheta = 0.1;
	double dThetaMax;
	
	ArrayList<Bullet> bullets;
	double bulletSpeed = 10;
	
	public SpaceShip(int xLim, int yLim, double size){//, Point[] arrPoly) {
		
		super(arrBasePolyX, arrBasePolyY, size, new Point(xLim, yLim));
		
		//move ourselves to the centre of the screen
//		this.translate(this.limit.x/2, this.limit.y/2);
		this.setPos(this.limit.x/2, this.limit.y/2);
		
		this.setCollidable(false);
		
		this.bullets = new ArrayList<Bullet>();
	}
	
	private Bullet fireBullet(){
		//fires a bullet out of the nose of the ship
		//returns the fired Bullet for convenience
		double[] bulletVelocity = {Math.cos(this.theta)*this.bulletSpeed, Math.sin(this.theta)*this.bulletSpeed};
		
		Bullet newBullet = new Bullet(this.getPos(), bulletVelocity);
		newBullet.setCollidable(this.isCollidable());
		newBullet.setColor(newBullet.isCollidable()?Color.WHITE:Color.GREEN);
		
		//TODO: Check all bullets for hits when we tick, remove any that do
		this.bullets.add(newBullet);
		
		return newBullet;
	}
	
	public void handleKeyPress(ArrayList<Integer> keyPressStack){
		//decides what to do when given input from keyboard keys pressed
		
		for(int k : keyPressStack){
			//It's called a stack, but really we just iterate through it in order, then clear it
			switch(k){
			case KeyEvent.VK_BACK_QUOTE:
				this.setCollidable(!this.isCollidable());
				break;
			case KeyEvent.VK_SPACE:
				this.fireBullet();
				break;
			}
		}
		
		//does this propogate? Yes
		keyPressStack.clear();
	}
	
	public void handleKeysHeld(ArrayList<Integer> keysDown){
		//decides what to do when given input from keyboard keys held down
		
		//movement keys
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
	
	public void draw(Graphics2D g2){
		
		super.draw(g2);
		
		for(Bullet bullet : this.bullets){
			bullet.tick();
			Point bPos = bullet.getPos();
			
			if(bPos.x <= 0 || this.limit.x <= bPos.x ||
			   bPos.y <= 0 || this.limit.y <= bPos.y){
//				this.bullets.remove(bullet);
				bullet.setAlive(false);
//				System.out.println("bullet is kil");
			}else if(bullet.isAlive()){
				bullet.draw(g2);
			}
		}
		
		//clean-up
		for(Object bullet : this.bullets.toArray()){
			if(!((Bullet)bullet).isAlive()){
				bullets.remove(((Bullet)bullet));
			}
		}
	}
	
	public void tick(){
		
		super.tick();
		
		//damp rotation
		this.omega *= 0.9;
	}
}
