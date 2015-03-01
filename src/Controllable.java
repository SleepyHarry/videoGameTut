import java.util.ArrayList;


public interface Controllable {
	
	ArrayList<Integer> getKeysDown();
	ArrayList<Integer> getKeyPressStack();
	
	void handleKeyPress();
	void handleKeysHeld();
}
