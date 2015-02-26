import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

public class SpaceShip extends GameObject{
	
	static int[] arrBasePolyX = {-14, 13, -14, -6};
	static int[] arrBasePolyY = {-15, 0, 15, 0, -15};
	
	double acceleration = 0.1;
	double dOmega = 0.0025;
	double dTheta = 0.1;
	double dThetaMax;
	
//	ArrayList<Bullet> bullets;
	
	//TODO: Weapon class, which will have things like bulletSpeed set in it
	double bulletSpeed = 10;
	
	private int explosionFragments = 10;		//How many bullets are generated when this ship explodes
	
	public SpaceShip(int xLim, int yLim, double size, GameBoard game){//, Point[] arrPoly) {
		
		super(arrBasePolyX, arrBasePolyY, size, game);
		
		//move ourselves to the centre of the screen
//		this.translate(this.limit.x/2, this.limit.y/2);
		this.setPos(this.limit.x/2, this.limit.y/2);
		
		this.setCollidable(false);
		
//		this.bullets = new ArrayList<Bullet>();
	}
	
	private void fireBullet(){
		//fires a bullet out of the nose of the ship
//		//returns the fired Bullet for convenience
		double[] bulletRelativeVelocity = {Math.cos(this.theta)*this.bulletSpeed, Math.sin(this.theta)*this.bulletSpeed};
		double[] bulletAbsoluteVelocity = {bulletRelativeVelocity[0]+this.velocity[0], bulletRelativeVelocity[1]+this.velocity[1]};
		
		Bullet newBullet = new Bullet(this.getPos(), bulletAbsoluteVelocity);
		newBullet.setCollidable(this.isCollidable());
		newBullet.setColor(newBullet.isCollidable()?Color.WHITE:Color.GREEN);
		
		//TODO: Check all bullets for hits when we tick, remove any that do
		this.game.bullets.add(newBullet);
		
		//return newBullet;
	}
	
	public int getExplosionFragments() {
		return explosionFragments;
	}

	public void setExplosionFragments(int explosionFragments) {
		this.explosionFragments = explosionFragments;
	}

	public void handleKeyPress(){
		//decides what to do when given input from keyboard keys pressed
		
		for(int k : this.game.keyPressStack){
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
		
		this.game.keyPressStack.clear();
	}
	
	public void handleKeysHeld(){
		//decides what to do when given input from keyboard keys held down
		
		//movement keys
		for(int keyCode : this.game.keysDown){
			switch(keyCode){
			case KeyEvent.VK_W:
				this.addRelativeVelocity( this.acceleration,  0);
				break;
				
			case KeyEvent.VK_A:
				this.setTheta(this.getTheta()-dTheta);
	//			this.setOmega(this.getOmega()-dOmega);
				break;
				
			case KeyEvent.VK_S:
				this.addRelativeVelocity(-this.acceleration,  0);
				break;
				
			case KeyEvent.VK_D:
	//			this.setTheta(this.getTheta()+dTheta);
				this.setOmega(dTheta);
				break;
			}
		}
	}
	
	public void draw(Graphics2D g2){
		
		super.draw(g2);
		
//		for(Bullet bullet : this.game.bullets){
//			bullet.tick();
//			Point bPos = bullet.getPos();
//			
//			if(bPos.x <= 0 || this.limit.x <= bPos.x ||
//			   bPos.y <= 0 || this.limit.y <= bPos.y){
////				this.bullets.remove(bullet);
//				bullet.setAlive(false);
////				System.out.println("bullet is kil");
//			}else if(bullet.isAlive()){
//				bullet.draw(g2);
//			}
//			
////			GameObject[] hits = bullet.checkHits();
//		}
//		
//		//clean-up
//		for(Object bullet : this.game.bullets.toArray()){
//			if(!((Bullet)bullet).isAlive()){
//				this.game.bullets.remove(((Bullet)bullet));
//			}
//		}
	}
	
	public void tick(){
		
		super.tick();
		
		//damp rotation
		this.omega *= 0.9;
	}
}
