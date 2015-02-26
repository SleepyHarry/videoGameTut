import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.swing.JComponent;
import javax.swing.JFrame;


public class Lesson50 extends JFrame{

	static int width = 1280, height = 720;
	
	public static void main(String[] args) {
		
		new Lesson50();
	}
	
	public Lesson50() {
		
		this.setSize(width, height);
		this.setTitle("Java Asteroids");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		GameBoard panel = new GameBoard();
		
		this.add(panel, BorderLayout.CENTER);
		
		addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e) {
				
				if(!panel.keysDown.contains((Integer)e.getKeyCode())){
					panel.keysDown.add(e.getKeyCode());
				}
				
				panel.keyPressStack.add(e.getKeyCode());
			}

			@Override
			public void keyReleased(KeyEvent e) {
				
				panel.keysDown.remove((Integer)e.getKeyCode());
			}

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(5);
		
		executor.scheduleAtFixedRate(new RepaintTheBoard(this), 0L, 20L, TimeUnit.MILLISECONDS);
		
		this.setVisible(true);
	}
}

class RepaintTheBoard implements Runnable{
	
	Lesson50 board;

	public RepaintTheBoard(Lesson50 board) {
		// TODO Auto-generated constructor stub
		this.board = board;
	}

	@Override
	public void run() {
		
		this.board.repaint();
	}
}

class GameBoard extends JComponent {
	
	int width = Lesson50.width;
	int height = Lesson50.height;
	
	static int numRocks = 5;
	
	public ArrayList<Rock> rocks = new ArrayList<Rock>();
	
	public SpaceShip player;
	
	ArrayList<Bullet> bullets = new ArrayList<Bullet>();
	
	ArrayList<Integer> keysDown = new ArrayList<Integer>();				//keys removed when released
	ArrayList<Integer> keyPressStack = new ArrayList<Integer>();		//keys removed when we've acted on it
	
	public GameBoard(){
		
//			this.setDoubleBuffered(true);
		
		player = new SpaceShip(width, height, 1, this);
		
		for(int i=0; i<numRocks; i++){
			Rock newRock;
			
			do{
				newRock = new Rock(width, height, Math.random()+0.5, this);
			}while(newRock.checkCollisions(rocks.toArray(new GameObject[rocks.size()])).length > 0);	//make sure we haven't made a rock inside an existing one
			
			rocks.add(newRock);
		}
	}
	
	@Override
	public void paint(Graphics g) {
		
		Graphics2D g2 = (Graphics2D)g;
		
		g2.setColor(Color.BLACK);
		g2.fillRect(0, 0, width, height);
		
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g2.setPaint(Color.WHITE);
		
		//tick everything before checking for collisions or whatever else
		for(Rock rock : rocks){
			rock.tick();
		}
		
		player.tick();
		
		for(Bullet bullet : bullets){
			bullet.tick();
		}
		
		
		//Now check for collisions and other interactions
		for(Bullet bullet : bullets){
			Point bPos = bullet.getPos();
			
			if(bPos.x <= 0 || this.width <= bPos.x ||
			   bPos.y <= 0 || this.height <= bPos.y){
				bullet.setAlive(false);
				continue;
			}
			
			//See if we've hit a rock
			GameObject[] hits = bullet.checkHits(rocks.toArray(new GameObject[rocks.size()]));
			for(GameObject hit : hits){
				if(bullet.isAlive()){
					//we only get here if we've hit something
					bullet.setAlive(false);
				}
				
//				hit.setColor(Color.RED);
				
				//hit is definitely a Rock, so casting to Rock is not a problem here
				rocks.remove(hit);
				
				Rock[] splits = ((Rock) hit).split();
				for(Rock split : splits){
	//				System.out.println(split.size+"\t"+Rock.sizeThreshold);
					//if(!split.getBounds().isEmpty()){
					if(!(split.size < Rock.sizeThreshold)){
						rocks.add(split);
					}
				}
			}
			
			if(bullet.isAlive()){
				bullet.draw(g2);
			}
		}

		for(Rock rock : rocks){
			GameObject[] collisions = rock.checkCollisions(rocks.toArray(new GameObject[rocks.size()]));
			
			for(GameObject obj : collisions){
				if(obj.getClass() == Rock.class){
					//Rock-on-Rock action
					if(rocks.indexOf(obj)<rocks.indexOf(rock)){	//arbitrary way of making sure we don't double-handle every colliding rock
						double[] temp = rock.getVelocity();
						rock.setVelocity(obj.getVelocity());
						obj.setVelocity(temp);
						
						rock.setOmega(-rock.getOmega());
						obj.setOmega(-obj.getOmega());
						
						//move the rocks until they're not stuck on each other any more
						while(rock.intersects(obj.getBounds())){
							rock.tick();
							obj.tick();
						}
					}
				}
			}
			
			rock.draw(g2);
//			g2.draw(rock.getBounds());
		}
		
		GameObject[] pCollisions = player.checkCollisions(rocks.toArray(new GameObject[rocks.size()]));
		if(pCollisions.length > 0){
			player.setColor(Color.RED);
		}else{
			player.setColor(player.isCollidable()?Color.WHITE:Color.GREEN);
		}
		
		for(GameObject obj : pCollisions){
			double[] temp = player.getVelocity();
			player.setVelocity(obj.getVelocity());
			obj.setVelocity(temp);
			
			player.setOmega(-obj.getOmega());
//			obj.setOmega(-player.getOmega());
			
			if(obj.getClass() == Rock.class){/*
				rocks.remove(obj);
			
				Rock[] splits = ((Rock) obj).split();
				for(Rock split : splits){
	//				System.out.println(split.size+"\t"+Rock.sizeThreshold);
					//if(!split.getBounds().isEmpty()){
					if(!(split.size < Rock.sizeThreshold)){
						rocks.add(split);
					}
				}
			
				//move the rocks until they're not stuck on each other any more
				while(player.checkCollisions(splits).length>0){
					//checks collisions against both fragments simultaneously
					player.tick();
					
					for(Rock split : splits){
						split.tick();
					}
				}
			*/}
		}
		
		player.draw(g2);
		
		//clean-up
		for(Bullet bullet : bullets.toArray(new Bullet[bullets.size()])){		//convoluted, but avoids concurrency errors
			if(!bullet.isAlive()){
				bullets.remove(bullet);
			}
		}
		
//		rocks.get(0).checkCollisions(rocks.toArray(new GameObject[rocks.size()]));
	}
}
