import java.awt.Color;
import java.awt.Point;


public class BulletWeapon extends Weapon {
	//Weapon that fires bulletsPerShot Bullets out in an even spread in front of the firer
	
	double bulletSpeed;
	int bulletsPerShot;
	double spreadTheta;
	
	public BulletWeapon(int bulletsPerShot, double bulletSpeed, double spreadTheta) {
		
		this.bulletsPerShot = bulletsPerShot;
		this.bulletSpeed = bulletSpeed;
		this.spreadTheta = spreadTheta;
	}
	
	public BulletWeapon(int bulletsPerShot, double bulletSpeed, double spreadTheta, GameObject weilder){
		
		this(bulletsPerShot, bulletSpeed, spreadTheta);
		
		this.setWeilder(weilder);
	}

	@Override
	void fire() {
//		System.out.println("fire");
		GameObject weilder = this.getWeilder();
		
		if(!(weilder== null)){
//			System.out.println("owned");
			//Someone owns us
			Point startPos = this.weilder.getPos();
			double[] wVel = this.weilder.getVelocity();
			double wTheta = this.weilder.getTheta();
			
			double initialTheta = wTheta - this.bulletsPerShot*this.spreadTheta/2;
			
			for(int i=0; i<this.bulletsPerShot; i++){
				this.fireBullet(startPos, wVel, this.bulletSpeed, initialTheta + i*this.spreadTheta);
			}
		}
	}
	
	private void fireBullet(Point origin, double[] baseVel, double speed, double theta){
		//fires an individual bullet from origin with speed at angle theta and base velocity baseVel
		double[] vRelative = {Math.cos(theta)*speed, Math.sin(theta)*speed};
		double[] vAbsolute = {baseVel[0] + vRelative[0], baseVel[1] + vRelative[1]};
		
		Bullet newBullet = new Bullet(origin, vAbsolute);
		newBullet.setCollidable(this.weilder.isCollidable());
		newBullet.setColor(newBullet.isCollidable()?Color.WHITE:Color.GREEN);
		
		this.weilder.game.bullets.add(newBullet);
	}
}
