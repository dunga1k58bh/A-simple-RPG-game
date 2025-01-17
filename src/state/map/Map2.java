package state.map;

import java.io.FileInputStream;
import java.util.ArrayList;
import Audio.Music;
import application.Main;
import entity.enemies.Enemy;
import entity.enemies.Fly;
import entity.somethings.Dropping;
import entity.somethings.Gate;
import entity.somethings.HUD;
import entity.Player;
import entity.enemies.Snail;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import state.GameState;
import state.GameStateManager;
import tilemap.TileMap;

public class Map2 extends GameState {

    //OVERALL DESCRIPTION:
    //this class owns tilemap obj and stores a reference to player object which playstate owns. Playstate will pass player obj to this class.
    //this class also owns player's on-map cord
    //this class owns camera position

    private  Image bg;

    //only reference
    private Player player; //Both player and tilemap can move, map can move then player stays, map can't move and player will move
    private ArrayList<Enemy> enemies;
    private ArrayList<Dropping> droppings;
    private  TileMap tilemap;
    private final Gate gatetoNextMap;
    private final Gate gateToPreviousMap;
    private HUD hud;
    private int hardLevel;

    //Music BackGround
    private  Music bgMusic;

    //starting position of player on map (On-map coord)
    public final double playerStartingPosX = 100;//TODO
    public final double playerStartingPosY = 200; //TODO
    //position of player on map if return the old Map
    private final double playerReturnPosX =  2800;
    private final double playerReturnPosY =  100;
    //Is this map clear ?
    public boolean isclear;

    //Camera position (On-map coord)
    private double camPosX = 0;
    private double camPosY = 0;

    public Map2(GameStateManager gsm){
        super(gsm);
        try {
            bg= new Image(new FileInputStream("res/bg/bgMap2.png"));
            bgMusic = new Music("res/Audio/bgMusic0.wav");
            tilemap = new TileMap(48);
            tilemap.loadTileSet("Map/TileSet.png");
            tilemap.loadMap("res/Map/Map2.map");
        }catch (Exception e){
            e.printStackTrace();
        }
         tilemap.setPos(camPosX,camPosY);
        hardLevel = gsm.getHardLevel();

        //Set Cycle music background and Play
        bgMusic.setCycle();
        bgMusic.setVolume(0.1);
        //the gate
        gateToPreviousMap = new Gate(tilemap);
        gateToPreviousMap.setPos(24,336);
        gatetoNextMap = new Gate(tilemap);
        gatetoNextMap.setPos(2856,336);
    }
    @Override
    public void setPlayer(Player player) {
        this.player = player;
        //System.out.println("playerSet: " + player);
        if(gsm.getNextMap()== true) {
            player.setPos(playerStartingPosX,playerStartingPosY);
        }else{
            player.setPos(playerReturnPosX,playerReturnPosY);
        }
        //Vá»©t TileMap cho player
        player.setTileMap(tilemap);
        hud = new HUD(player);
        generateEnemies();
        bgMusic.startMusic();
    }

    private void generateEnemies() {
        enemies = new ArrayList<>();
        droppings = new ArrayList<>();
        Snail s;
        Fly f;
        Point2D[] points = new Point2D[] {
                new Point2D(200, 900),
                new Point2D(860, 200),
                new Point2D(1525, 200),
                new Point2D(1680, 200),
                new Point2D(1800, 200)
        };
        Fly fly = new Fly(tilemap, player, hardLevel);
        fly.setPos(700, 1000);
        enemies.add(fly);

        Fly fly2 = new Fly(tilemap, player, hardLevel);
        fly2.setPos(500, 700);
        enemies.add(fly2);

        for (Point2D point : points) {
            s = new Snail(tilemap, hardLevel);
            f = new Fly(tilemap, player, hardLevel);
            f.setPos(point.getX(), point.getY() - 100);
            s.setPosition(point.getX(), point.getY());
            enemies.add(s);
            enemies.add(f);
        }

    }


    @Override
    public void tick(){
        if (player.getFacing() == 1) {
            camPosX = player.getPosX() - Main.width*1/3;
        }
        else {
            camPosX = player.getPosX() - Main.width*2/3;
        }
        camPosY = player.getPosY() - Main.height*2/3;
        tilemap.setPos(camPosX,camPosY);

        tilemap.tick();                                   //Map update each frame
        for(int i = 0; i < enemies.size(); i++) {
            Enemy e = enemies.get(i);
            if (player.intersects(e)) player.getHit(e.getDamage());;
            if (player.getKey().skill1 == 1) {
            	if (player.getSkill1().intersects(e)) {
            		e.getHit(player.getSkill1().getDamage());
            	}
            }
            if (player.getKey().skill2 == 1) {
            	if (player.getSkill2().intersects(e)) {
            		e.getHit(player.getSkill2().getDamage());
            	}
            }
            if (player.getKey().attack == 1) {
            	if (player.getBox().intersects(e)) {
            		e.getHit(player.getBox().getDamage());
            	}
            }
            e.tick();

            if(e.isDead()) {
            	player.curEXP += e.getEXP();
            	if(player.curEXP >= player.curMaxEXP) {
            		player.level++;
            		if(player.level == 2) {
            			player.curMaxEXP = player.level2EXP;
            			player.curEXP -= player.level1EXP;
            			player.setMaxHP(600);
            			player.setHP(600);
            			player.setMP(player.maxMP);
            		}
            		if(player.level == 3) {
            			player.curMaxEXP = player.level3EXP;
            			player.curEXP -= player.level2EXP;
            			player.setMaxHP(800);
            			player.setHP(800);
            			player.setMP(player.maxMP);
            		}
            	}
                Dropping d = new Dropping(tilemap, e);
                droppings.add(d);
                enemies.remove(i);
                i--;
            }
        }
        player.tick();                                    //player upda
        if(player.isDead()){                    //if player dead revival him in the pos begin and resumoner enemy
            player.setDead(false);
            gsm.setNextMap(true);
            setPlayer(player);
            player.setHP(player.maxHP);
            player.setMP(player.maxMP);
            gsm.setNextMap(false);
        }
		for(int i = 0; i < droppings.size(); i++) {
			Dropping d = droppings.get(i);
			d.tick();
			if (d.intersects(player)) {
				if (d.type == d.HPpot) player.HPpotNum++;
				else if (d.type == d.MPpot) player.MPpotNum++;
				droppings.remove(i);
				i--;
			}
		}
		
        //Check to open gate
        tilemap.OpenNextMap(enemies.size());
        changeMap();
    }
    public void changeMap(){
        if(enemies.size()==0) isclear = true;    ///if there no enemy the map is clear
        //Check to move to next map
        if(isclear&&player.intersects(gatetoNextMap)){   //Move to nextMap
            bgMusic.pauseMusic();
            gsm.nextMap();            // move to next map
            gsm.setNextMap(true);     //It mean the player is being move to the NEXT map
            gsm.setPlayer(player);    //set player to next map
            gsm.setNextMap(false);
        }
        if(player.intersects(gateToPreviousMap)){
            bgMusic.pauseMusic();
            gsm.previousMap();     //gsm.getNextMap is false in defalt so setPlayer will set player to returnPos
            gsm.setPlayer(player);
        }
    }

    @Override
    public void render(GraphicsContext g) {
        g.drawImage(bg,0,0, Main.width, Main.height);
        tilemap.draw(g);
        for (Enemy enemy : enemies) {
//		    System.out.println(enemies.get(0).getPosX()+" "+ enemies.get(0).getPosY());
            enemy.render(g);
        }
        player.render(g);
        hud.render(g);
		for(int i = 0; i < droppings.size(); i++) {
			Dropping d = droppings.get(i);
			d.render(g);
		}
        if(isclear) gatetoNextMap.render(g);
        gateToPreviousMap.render(g);
    }

    @Override
    public void keyPressed(KeyEvent k) {
        player.keyIn(k);
    }

    @Override
    public void keyTyped(KeyEvent k) {
//
        player.keyIn(k);
    }

    @Override
    public void keyReleased(KeyEvent k) {
        player.keyIn(k);
    }
}
