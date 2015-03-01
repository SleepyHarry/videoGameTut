
public abstract class Weapon {
	
	GameObject weilder = null;
	
	abstract void fire();

	public GameObject getWeilder() {
		return weilder;
	}

	public void setWeilder(GameObject weilder) {
		this.weilder = weilder;
	}
}
