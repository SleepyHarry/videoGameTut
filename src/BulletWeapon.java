import java.awt.Color;
import java.awt.Point;


public class BulletWeapon extends Weapon {
	//Weapon that fires bulletsPerShot Bullets out in an even spread in front of the firer
	
	protected int ammo;
	protected boolean usesAmmo = false;
	
	protected double bulletSpeed;
	protected int bulletsPerShot;
	protected double spreadTheta;
	
	public BulletWeapon(int bulletsPerShot, double bulletSpeed, double spreadTheta, boolean usesAmmo, int initialAmmo) {
		
		this.bulletsPerShot = bulletsPerShot;
		this.bulletSpeed = bulletSpeed;
		this.spreadTheta = spreadTheta;
		this.usesAmmo = usesAmmo;
		this.ammo = initialAmmo;
	}
	public BulletWeapon(int bulletsPerShot, double bulletSpeed, double spreadTheta, boolean usesAmmo, int initialAmmo, GameObject weilder){
		
		this(bulletsPerShot, bulletSpeed, spreadTheta, usesAmmo, initialAmmo);
		
		this.setWeilder(weilder);
	}

	public boolean usesAmmo() {
		return usesAmmo;
	}
	
	public void setUsesAmmo(boolean usesAmmo) {
		this.usesAmmo = usesAmmo;
	}
	
	public int getAmmo() {
		return ammo;
	}

	public void setAmmo(int ammo) {
		this.ammo = ammo;
		
		if(ammo > 0){
			this.usesAmmo = true;
		}
	}

	@Override
	void fire() {
		if(!this.usesAmmo || (this.usesAmmo && this.ammo > 0)){
			this.ammo--;
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
