import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class SpaceShip extends GameObject implements Controllable{
	
	static int[] arrBasePolyX = {-14, 13, -14, -6};
	static int[] arrBasePolyY = {-15, 0, 15, 0, -15};
	
	double acceleration = 0.1;
	double dOmega = 0.0025;
	double dTheta = 0.1;
	double dThetaMax;
	
//	ArrayList<Bullet> bullets;
	
	Weapon wep;
	ArrayList<Weapon> weps;
	int curWep = 0;
	
	private int explosionFragments = 50;		//How many Bullets are generated when this ship explodes
	double explosionForce = 7;					//How fast the Bullets travel away from the dead ship
	
	public SpaceShip(int xLim, int yLim, double size, GameBoard game){//, Point[] arrPoly) {
		
		super(arrBasePolyX, arrBasePolyY, size, game);
		
		//move ourselves to the centre of the screen
//		this.translate(this.limit.x/2, this.limit.y/2);
		this.setPos(this.limit.x/2, this.limit.y/2);
		
		this.setCollidable(false);
		
//		this.bullets = new ArrayList<Bullet>();
		
//		this.wep = new BulletWeapon(1, 10, Math.PI/72, this);
		
		this.weps = new ArrayList<Weapon>();
		weps.add(0, new BulletWeapon(1, 10, Math.PI/72, false, 0, this));
		weps.add(1, new BulletWeapon(3, 10, Math.PI/72, true, 60, this));
		weps.add(2, new BulletWeapon(4, 10, Math.PI/2, true, 100, this));
	}
	
	//Controllable's methods
	@Override
	public ArrayList<Integer> getKeyPressStack() {
		return this.game.keyPressStack;
	}
	
	@Override
	public ArrayList<Integer> getKeysDown() {
		return this.game.keysDown;
	}

	public void handleKeyPress(){
		//decides what to do when given input from keyboard keys pressed
		
		ArrayList<Integer> kp = this.getKeyPressStack();
		
		for(int k : kp){
			//It's called a stack, but really we just iterate through it in order, then clear it
			switch(k){
			case KeyEvent.VK_BACK_QUOTE:
			case KeyEvent.VK_ENTER:
				this.setCollidable(!this.isCollidable());
				break;
			case KeyEvent.VK_SPACE:
			case KeyEvent.VK_SHIFT:
//				this.wep.fire();
				this.weps.get(this.curWep).fire();
				break;
			case KeyEvent.VK_1:
			case KeyEvent.VK_2:
			case KeyEvent.VK_3:
			case KeyEvent.VK_4:
			case KeyEvent.VK_5:
			case KeyEvent.VK_6:
			case KeyEvent.VK_7:
			case KeyEvent.VK_8:
			case KeyEvent.VK_9:
				int choice = k - KeyEvent.VK_1;		//Do we like this? I think it may assume too much
				if(choice < this.weps.size()){
					this.curWep = choice;
				}
				break;
			}
		}
		
		kp.clear();
	}
	
	public void handleKeysHeld(){
		//decides what to do when given input from keyboard keys held down
		
		ArrayList<Integer> kd = this.getKeysDown();
		
		//movement keys
		for(int keyCode : kd){
			switch(keyCode){
			case KeyEvent.VK_W:
			case KeyEvent.VK_UP:
				this.addRelativeVelocity( this.acceleration,  0);
				break;
				
			case KeyEvent.VK_A:
			case KeyEvent.VK_LEFT:
				this.setTheta(this.getTheta()-dTheta);
//				this.setOmega(-dTheta);
				break;
				
			case KeyEvent.VK_S:
			case KeyEvent.VK_DOWN:
				this.addRelativeVelocity(-this.acceleration,  0);
				break;
				
			case KeyEvent.VK_D:
			case KeyEvent.VK_RIGHT:
				this.setTheta(this.getTheta()+dTheta);
//				this.setOmega(dTheta);
				break;
			}
		}
	}
	////
	
	public void die() {
		this.setAlive(false);
		
		int n = this.explosionFragments;
		double deltaTheta = (Math.PI*2)/n;		//each bullet is evenly distributed
		
		Point startPos = this.getPos();
		
		for(int i=0; i<n; i++){
			double[] newVelocity = {Math.cos(deltaTheta*i)*this.explosionForce, Math.sin(deltaTheta*i)*this.explosionForce};
			
			Bullet fragment = new Bullet(startPos, newVelocity);
			fragment.setCollidable(false);
			
			this.game.bullets.add(fragment);
		}
	}
	
	public void tick(){
		
		this.handleKeyPress();
		this.handleKeysHeld();
		
		super.tick();
		
		//damp rotation
		this.omega *= 0.9;
	}
}
