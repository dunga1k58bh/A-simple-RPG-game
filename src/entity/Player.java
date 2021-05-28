package entity;

import entity.skills.Skill1;
import entity.skills.Skill2;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import utils.Key;

import java.util.ArrayList;

public class Player extends Entity{
    private Image standLowerBody = new Image("char/Small23-resources.assets-11235.png");
    private Image standUpperBody = new Image("char/Small5-resources.assets-1687.png");
    private ArrayList<Image> head = new ArrayList<Image>();
    private ArrayList<Image> runLowerBody = new ArrayList<Image>();
    private ArrayList<Image> runUpperBody = new ArrayList<Image>();
    private ArrayList<Image> jumpLowerBody = new ArrayList<Image>();
    private ArrayList<Image> jumpUpperBody = new ArrayList<Image>();
    private Image jumpHead;
    private int animationStep = 0;
    private int animationStep2 = 0;
    private int count1 = 0;
    private int count2 = 0;
    private int offset = 0;

    //Skills
    private Skill1 skill1;
    private Skill2 skill2;
    private boolean USESKILL1;
    private boolean USESKILL2;



    private int jump = 0;  //this variable is set to 1 whenever player is on-air
    private double dt = 1; //pseudo time between frames

    //velocity of player when moving
    private final double velocityX = 5;
    private final double velocityY = -15;
    private double currentVelocityX = 0;
    private double currentVelocityY = 0;
    private final double accelerationX = 0.4; //TODO
    private final double accelerationY = 0.7;
    private int runningDirection = 0; //0 = not running, 1 = right, -1 = left
    private int lastRunningDirection = 0;
    private int facing = 1;
    //private double zzY = 0;
    //private double zzX = 0;
    private boolean lock = false;
    private Key key= new Key();
    public Player() {
        setPosX(500);
        setPosY(300);
        setEntityBoxSize(30,50);// Dòng này thêm kích cỡ nhân vật (E mới ước chừng thôi) ENGLISH PLS? stupid
        //add default texture

        head.add(0,new Image("char/Small32-resources.assets-4440.png")); //standing
        head.add(1,new Image("char/Small33-resources.assets-14326.png")); //running
        runUpperBody.add (0,new Image("char/Small6-resources.assets-12601.png"));
        runUpperBody.add (1,new Image("char/Small7-resources.assets-12452.png"));
        runUpperBody.add (2,new Image("char/Small8-resources.assets-1354.png"));
        runUpperBody.add (3,new Image("char/Small9-resources.assets-7446.png"));
        runUpperBody.add (4,new Image("char/Small10-resources.assets-8065.png")); //Small11-resources.assets-7160.png

        runLowerBody.add(0,new Image("char/Small24-resources.assets-12840.png"));
        runLowerBody.add(1,new Image("char/Small25-resources.assets-6123.png"));
        runLowerBody.add(2,new Image("char/Small26-resources.assets-6747.png"));
        runLowerBody.add(3,new Image("char/Small27-resources.assets-6734.png"));
        runLowerBody.add(4,new Image("char/Small28-resources.assets-5528.png"));

        //jump animation contains 3 phases:
        //Phase 1: from gound to highest-air (Example: https://drive.google.com/file/d/12mnGkDOkYaG6wl46t_2hhXUM60TrX_NP/view?usp=sharing)
        //Phase 2: air-rolling (Example: https://drive.google.com/file/d/19lhi_kn1RBmVkM7ksC5QPMG8mFIGK23E/view?usp=sharing)
        //Phase 3: falling (Example: https://drive.google.com/file/d/1HrBG9Pz_xFWDgIKE4c9VnVCrhyeICt2Y/view?usp=sharing)
        jumpHead = new Image("char/Small32-resources.assets-4440.png");

        jumpUpperBody.add(0, new Image("char/Small11-resources.assets-7160.png")); //Phase 1
        jumpUpperBody.add(1, new Image("char/Small1-resources.assets-7704.png")); //*
        jumpUpperBody.add(2, new Image("char/Small2-resources.assets-6576.png")); //* Phase 2
        jumpUpperBody.add(3, new Image("char/Small3-resources.assets-11828.png")); //*
        jumpUpperBody.add(4, new Image("char/Small4-resources.assets-5934.png")); //*
        jumpUpperBody.add(5, new Image("char/Small12-resources.assets-4133.png")); //Phase 3

        jumpLowerBody.add(0,new Image("char/Small29-resources.assets-15129.png")); //Phase 1
        jumpLowerBody.add(1,new Image("char/Small30-resources.assets-11864.png")); //Phase 3
    }

    public Player(int x, int y) {
        this();
        setPosX(x);
        setPosY(y);
    }


    public void initSkill(){
        skill1 = new Skill1(tileMap);
        skill1.setPos(posX,posY);

        skill2 = new Skill2(tileMap);
        skill2.setPos(posX,posY);
    }

    @Override
    public void tick() {
        //tick

        if (!onGround && !lock) { //falling
            falling = true;
        }

        if (onGround) {
            falling = false;
            lock = false;
            currentVelocityY = 0;
            //System.out.println("YOYO");
        }

        if (onRoof) {
            //System.out.println(onRoof);
            currentVelocityY = 0;
        }

        if (lock) { //lock = true -> start jump animation
            if (dy < 0) {  //from ground to top phs = 0 -> animation phase 1
                phs = 0;
            }
            else if ((currentVelocityY + dt * accelerationY * 1000) > 0 && !lock2) { //on top
                lock2 = true;
                phs = 1;
            }
            if (phs == 1) {
                count3 ++;
                //animationStep3++;//++;
                //dy = 0;
            }
            if (count3 % 2 == 1) {
                animationStep3 ++;
            }
            if (animationStep3 % 6 == 0 && animationStep3 != 0) {
                animationStep3 = 0;
                count3 = 0;
                phs = 2; //phs = 2 when complete phase 2 animation, phs = 2 -> animation phase 3
            }
        }

        if (key.up == 1 && currentVelocityY >= 0 && onGround && !lock) {
            lock = true;
            currentVelocityY = velocityY;
        }

        runningDirection = key.right - key.left;
        if (runningDirection == 1) {
            skill1.facingRight = true;
            skill2.facingRight = true;
            if (animationStep == -1) {
                animationStep = 0;
            }
            facing = runningDirection;
            lastRunningDirection = runningDirection;
            currentVelocityX = velocityX;
            dx= dt * Math.max(velocityX,0);
            count1++;
            count2++;
        } else if (runningDirection == -1) {
            skill1.facingRight = false;
            skill2.facingRight = false;
            facing = runningDirection;
            if (animationStep == -1) {
                animationStep = 0;
            }
            lastRunningDirection = runningDirection;
            currentVelocityX = velocityX;
            dx =- dt * Math.max(velocityX,0);
            count1++;
            count2++;
        }

        else if (runningDirection == 0) {
            currentVelocityX -= accelerationX *dt;
            dx = lastRunningDirection * dt * Math.max(currentVelocityX,0);
            animationStep = -1;
            count2++;
        }

        currentVelocityY += dt * accelerationY;
        lastdy = dy;
        dy = dt * currentVelocityY;
        CheckTileMapCollision();
        //System.out.println(posX+" "+posY);
        posX+=dx;
        posY+=dy;


        //for the running animation
        if (count1 % 5 == 0 && count1!=0) {
            count1 = 0;
            animationStep ++;
        }

        //for the head shaking animation
        if (count2 % 13 == 0 && count2!=0){
            count2 = 0;
            animationStep2 ++;
            offset = animationStep2%2*3;
        }
        //Update Skill
        if(USESKILL2 ==false){
            skill2.setPos(posX,posY);
        }else{
            skill2.tick();
        }
        skill1.setPos(posX,posY);
        skill1.tick();

    }


    //private "default skin"
    @Override
    public void render(GraphicsContext graphicsContext) {
        //Đoạn này hiểu đơn giản chỉ cần lấy pos Draw như bên dưới thì nó sẽ nằm trong màn hình
        double posXTemp = posX;
        double posYTemp = posY;
        posX = posX - tileMap.getCameraPosX();
        posY = posY - tileMap.getCameraPosY();


        //offset = 0;
        //animationStep ++;

        //render
        //Image image1 = new Image("char/Small33-resources.assets-14326.png"); //head
        //animationStep = 4;

        if (falling) {
            if (facing == 1) {
                graphicsContext.drawImage(jumpHead,0,0,jumpHead.getWidth(),jumpHead.getHeight(),posX+-24.0,posY+-76.0,jumpHead.getWidth()*facing,jumpHead.getHeight());
                graphicsContext.drawImage(jumpLowerBody.get(1),0,0,jumpLowerBody.get(1).getWidth(),jumpLowerBody.get(1).getHeight(),posX-jumpLowerBody.get(1).getWidth()/2*facing,posY-jumpLowerBody.get(1).getHeight(),jumpLowerBody.get(1).getWidth()*facing,jumpLowerBody.get(1).getHeight());
                graphicsContext.drawImage(jumpUpperBody.get(5),0,0,jumpUpperBody.get(5).getWidth(),jumpUpperBody.get(5).getHeight(),posX+-31.0,posY+-59.0,jumpUpperBody.get(5).getWidth()*facing,jumpUpperBody.get(5).getHeight());
            }
            else {
                graphicsContext.drawImage(jumpHead,0,0,jumpHead.getWidth(),jumpHead.getHeight(),posX+25,posY+-75.5,jumpHead.getWidth()*facing,jumpHead.getHeight());
                graphicsContext.drawImage(jumpLowerBody.get(1),0,0,jumpLowerBody.get(1).getWidth(),jumpLowerBody.get(1).getHeight(),posX-jumpLowerBody.get(1).getWidth()/2*facing,posY-jumpLowerBody.get(1).getHeight(),jumpLowerBody.get(1).getWidth()*facing,jumpLowerBody.get(1).getHeight());
                graphicsContext.drawImage(jumpUpperBody.get(5),0,0,jumpUpperBody.get(5).getWidth(),jumpUpperBody.get(5).getHeight(),posX+31,posY+-59.0,jumpUpperBody.get(5).getWidth()*facing,jumpUpperBody.get(5).getHeight());
            }
        }
        else if (lock) {
            if (phs == 0) {
                if (facing == 1) {
                    graphicsContext.drawImage(jumpHead,0,0,jumpHead.getWidth(),jumpHead.getHeight(),posX + -20.0,posY +-69.5,jumpHead.getWidth()*facing,jumpHead.getHeight());
                    graphicsContext.drawImage(jumpLowerBody.get(0),0,0,jumpLowerBody.get(0).getWidth(),jumpLowerBody.get(0).getHeight(),posX-jumpLowerBody.get(0).getWidth()/2,posY-jumpLowerBody.get(0).getHeight(),jumpLowerBody.get(0).getWidth()*facing,jumpLowerBody.get(0).getHeight());
                    graphicsContext.drawImage(jumpUpperBody.get(0),0,0,jumpUpperBody.get(0).getWidth(),jumpUpperBody.get(0).getHeight(),posX + -20.5,posY + -41.0,jumpUpperBody.get(0).getWidth()*facing,jumpUpperBody.get(0).getHeight());
                }
                else {
                    graphicsContext.drawImage(jumpHead,0,0,jumpHead.getWidth(),jumpHead.getHeight(), posX+20.0,posY+-69.5,jumpHead.getWidth()*facing,jumpHead.getHeight());
                    graphicsContext.drawImage(jumpLowerBody.get(0),0,0,jumpLowerBody.get(0).getWidth(),jumpLowerBody.get(0).getHeight(),posX+jumpLowerBody.get(0).getWidth()/2,posY-jumpLowerBody.get(0).getHeight(),jumpLowerBody.get(0).getWidth()*facing,jumpLowerBody.get(0).getHeight());
                    graphicsContext.drawImage(jumpUpperBody.get(0),0,0,jumpUpperBody.get(0).getWidth(),jumpUpperBody.get(0).getHeight(),posX + 21.0,posY + -41.0,jumpUpperBody.get(0).getWidth()*facing,jumpUpperBody.get(0).getHeight());
                }
            }
            if (phs == 1) {
                System.out.println("AnimationStep3 = " + animationStep3);
                System.out.println("Onground = " + onGround);
                System.out.println("Lock = " + lock);
                if (animationStep3 == 4) {
                    graphicsContext.drawImage(jumpUpperBody.get(animationStep3),0,0,jumpUpperBody.get(animationStep3).getWidth(),jumpUpperBody.get(animationStep3).getHeight(),posX-jumpUpperBody.get(animationStep3).getWidth()/2*facing,posY-jumpUpperBody.get(animationStep3).getHeight(),jumpUpperBody.get(animationStep3).getWidth()*facing,jumpUpperBody.get(animationStep3).getHeight());
                }
                else {
                    graphicsContext.drawImage(jumpUpperBody.get(animationStep3%4), 0, 0, jumpUpperBody.get(animationStep3%4).getWidth(), jumpUpperBody.get(animationStep3%4).getHeight(), posX-jumpUpperBody.get(animationStep3%4).getWidth()/2*facing, posY-jumpUpperBody.get(animationStep3%4).getHeight(), jumpUpperBody.get(animationStep3%4).getWidth() * facing, jumpUpperBody.get(animationStep3%4).getHeight());
                }
            }
            if (phs == 2) {
                if (facing == 1) {
                    graphicsContext.drawImage(jumpHead,0,0,jumpHead.getWidth(),jumpHead.getHeight(),posX+-24.0,posY+-76.0,jumpHead.getWidth()*facing,jumpHead.getHeight());
                    graphicsContext.drawImage(jumpLowerBody.get(1),0,0,jumpLowerBody.get(1).getWidth(),jumpLowerBody.get(1).getHeight(),posX-jumpLowerBody.get(1).getWidth()/2*facing,posY-jumpLowerBody.get(1).getHeight(),jumpLowerBody.get(1).getWidth()*facing,jumpLowerBody.get(1).getHeight());
                    graphicsContext.drawImage(jumpUpperBody.get(5),0,0,jumpUpperBody.get(5).getWidth(),jumpUpperBody.get(5).getHeight(),posX+-31.0,posY+-59.0,jumpUpperBody.get(5).getWidth()*facing,jumpUpperBody.get(5).getHeight());
                }
                else {
                    graphicsContext.drawImage(jumpHead,0,0,jumpHead.getWidth(),jumpHead.getHeight(),posX+25,posY+-75.5,jumpHead.getWidth()*facing,jumpHead.getHeight());
                    graphicsContext.drawImage(jumpLowerBody.get(1),0,0,jumpLowerBody.get(1).getWidth(),jumpLowerBody.get(1).getHeight(),posX-jumpLowerBody.get(1).getWidth()/2*facing,posY-jumpLowerBody.get(1).getHeight(),jumpLowerBody.get(1).getWidth()*facing,jumpLowerBody.get(1).getHeight());
                    graphicsContext.drawImage(jumpUpperBody.get(5),0,0,jumpUpperBody.get(5).getWidth(),jumpUpperBody.get(5).getHeight(),posX+31,posY+-59.0,jumpUpperBody.get(5).getWidth()*facing,jumpUpperBody.get(5).getHeight());
                }
            }
        }
        else {
            switch (animationStep % 5) {
                case -1 -> { //Draw stand animation
                    if (facing == 1) {
                        graphicsContext.drawImage(standLowerBody, 0, 0, standLowerBody.getWidth(), standLowerBody.getHeight(), posX - 7, posY - 17, standLowerBody.getWidth() * facing, standLowerBody.getHeight());
                        graphicsContext.drawImage(standUpperBody, 0, 0, standUpperBody.getWidth(), standUpperBody.getHeight(), posX - 14, posY - 35 + offset, standUpperBody.getWidth() * facing, standUpperBody.getHeight());
                        graphicsContext.drawImage(head.get(0), 0, 0, head.get(0).getWidth(), head.get(0).getHeight(), posX - 15, posY - 66 + offset, head.get(0).getWidth() * facing, head.get(0).getHeight()); //head
                    } else if (facing == -1) {
                        graphicsContext.drawImage(standLowerBody, 0, 0, standLowerBody.getWidth(), standLowerBody.getHeight(), posX + 20, posY - 17, standLowerBody.getWidth() * facing, standLowerBody.getHeight());
                        graphicsContext.drawImage(standUpperBody, 0, 0, standUpperBody.getWidth(), standUpperBody.getHeight(), posX + 26, posY - 35 + offset, standUpperBody.getWidth() * facing, standUpperBody.getHeight());
                        graphicsContext.drawImage(head.get(0), 0, 0, head.get(0).getWidth(), head.get(0).getHeight(), posX + 27, posY - 66 + offset, head.get(0).getWidth() * facing, head.get(0).getHeight()); //head
                    }
                break;
                }
                case 0 -> { //Draw
                    if (facing == 1) {
                        graphicsContext.drawImage(head.get(1), 0, 0, head.get(1).getWidth(), head.get(1).getHeight(), posX - 15, posY - 68 + offset, head.get(1).getWidth() * facing, head.get(1).getHeight());

                        //Image image2 = new Image("char/Small24-resources.assets-12840.png"); //down
                        graphicsContext.drawImage(runLowerBody.get(animationStep % runLowerBody.size()), 0, 0, runLowerBody.get(animationStep % runLowerBody.size()).getWidth(), runLowerBody.get(animationStep % runLowerBody.size()).getHeight(), posX - runLowerBody.get(animationStep % runUpperBody.size()).getWidth() / 2, posY - runLowerBody.get(animationStep % runLowerBody.size()).getHeight(), runLowerBody.get(animationStep % runLowerBody.size()).getWidth() * facing, runLowerBody.get(animationStep % runLowerBody.size()).getHeight());

                        //Image image3 = new Image("char/Small6-resources.assets-12601.png"); //up
                        graphicsContext.drawImage(runUpperBody.get(animationStep % runUpperBody.size()), 0, 0, runUpperBody.get(animationStep % runUpperBody.size()).getWidth(), runUpperBody.get(animationStep % runUpperBody.size()).getHeight(), posX - 14, posY - 35 + offset, runUpperBody.get(animationStep % runUpperBody.size()).getWidth() * facing, runUpperBody.get(animationStep % runUpperBody.size()).getHeight());
                    } else if (facing == -1) {
                        graphicsContext.drawImage(head.get(1), 0, 0, head.get(1).getWidth(), head.get(1).getHeight(), posX - 15 + 25, posY - 68 + offset, head.get(1).getWidth() * facing, head.get(1).getHeight());

                        //Image image2 = new Image("char/Small24-resources.assets-12840.png"); //down
                        graphicsContext.drawImage(runLowerBody.get(animationStep % runLowerBody.size()), 0, 0, runLowerBody.get(animationStep % runLowerBody.size()).getWidth(), runLowerBody.get(animationStep % runLowerBody.size()).getHeight(), posX - runLowerBody.get(animationStep % runUpperBody.size()).getWidth() / 2 + 25, posY - runLowerBody.get(animationStep % runLowerBody.size()).getHeight(), runLowerBody.get(animationStep % runLowerBody.size()).getWidth() * facing, runLowerBody.get(animationStep % runLowerBody.size()).getHeight());

                        //Image image3 = new Image("char/Small6-resources.assets-12601.png"); //up
                        graphicsContext.drawImage(runUpperBody.get(animationStep % runUpperBody.size()), 0, 0, runUpperBody.get(animationStep % runUpperBody.size()).getWidth(), runUpperBody.get(animationStep % runUpperBody.size()).getHeight(), posX - 14 + 25, posY - 35 + offset, runUpperBody.get(animationStep % runUpperBody.size()).getWidth() * facing, runUpperBody.get(animationStep % runUpperBody.size()).getHeight());
                    }


                break;
            }
            case 1 -> {
                if (facing == 1) {
                    graphicsContext.drawImage(head.get(1), 0, 0, head.get(1).getWidth(), head.get(1).getHeight(), posX - 15, posY - 68 + offset, head.get(1).getWidth()*facing, head.get(1).getHeight());
                    graphicsContext.drawImage(runLowerBody.get(animationStep % runLowerBody.size()), 0,0, runLowerBody.get(animationStep % runLowerBody.size()).getWidth(), runLowerBody.get(animationStep % runLowerBody.size()).getHeight(), posX - runLowerBody.get(animationStep % runUpperBody.size()).getWidth() / 2 + 3, posY - runLowerBody.get(animationStep % runLowerBody.size()).getHeight(), runLowerBody.get(animationStep % runLowerBody.size()).getWidth()*facing, runLowerBody.get(animationStep % runLowerBody.size()).getHeight());
                    graphicsContext.drawImage(runUpperBody.get(animationStep % runUpperBody.size()), 0,0,runUpperBody.get(animationStep % runUpperBody.size()).getWidth(),runUpperBody.get(animationStep % runUpperBody.size()).getHeight(),posX - 19, posY - 35 + offset, runUpperBody.get(animationStep % runUpperBody.size()).getWidth(),runUpperBody.get(animationStep % runUpperBody.size()).getHeight());
                } else if (facing == -1) {
                    graphicsContext.drawImage(head.get(1), 0, 0, head.get(1).getWidth(), head.get(1).getHeight(), posX - 15 + 25, posY - 68 + offset, head.get(1).getWidth()*facing, head.get(1).getHeight());
                    graphicsContext.drawImage(runLowerBody.get(animationStep % runLowerBody.size()), 0,0, runLowerBody.get(animationStep % runLowerBody.size()).getWidth(), runLowerBody.get(animationStep % runLowerBody.size()).getHeight(), posX - runLowerBody.get(animationStep % runUpperBody.size()).getWidth() / 2 + 3 + 30, posY - runLowerBody.get(animationStep % runLowerBody.size()).getHeight(), runLowerBody.get(animationStep % runLowerBody.size()).getWidth()*facing, runLowerBody.get(animationStep % runLowerBody.size()).getHeight());
                    graphicsContext.drawImage(runUpperBody.get(animationStep % runUpperBody.size()), 0,0,runUpperBody.get(animationStep % runUpperBody.size()).getWidth(),runUpperBody.get(animationStep % runUpperBody.size()).getHeight(),posX - 19 + 33, posY - 35 + offset, runUpperBody.get(animationStep % runUpperBody.size()).getWidth()*facing,runUpperBody.get(animationStep % runUpperBody.size()).getHeight());
                }
                case 1 -> {
                    if (facing == 1) {
                        graphicsContext.drawImage(head.get(1), 0, 0, head.get(1).getWidth(), head.get(1).getHeight(), posX - 15, posY - 68 + offset, head.get(1).getWidth() * facing, head.get(1).getHeight());
                        graphicsContext.drawImage(runLowerBody.get(animationStep % runLowerBody.size()), 0, 0, runLowerBody.get(animationStep % runLowerBody.size()).getWidth(), runLowerBody.get(animationStep % runLowerBody.size()).getHeight(), posX - runLowerBody.get(animationStep % runUpperBody.size()).getWidth() / 2 + 3, posY - runLowerBody.get(animationStep % runLowerBody.size()).getHeight(), runLowerBody.get(animationStep % runLowerBody.size()).getWidth() * facing, runLowerBody.get(animationStep % runLowerBody.size()).getHeight());
                        graphicsContext.drawImage(runUpperBody.get(animationStep % runUpperBody.size()), 0, 0, runUpperBody.get(animationStep % runUpperBody.size()).getWidth(), runUpperBody.get(animationStep % runUpperBody.size()).getHeight(), posX - 19, posY - 35 + offset, runUpperBody.get(animationStep % runUpperBody.size()).getWidth(), runUpperBody.get(animationStep % runUpperBody.size()).getHeight());
                    } else if (facing == -1) {
                        graphicsContext.drawImage(head.get(1), 0, 0, head.get(1).getWidth(), head.get(1).getHeight(), posX - 15 + 25, posY - 68 + offset, head.get(1).getWidth() * facing, head.get(1).getHeight());
                        graphicsContext.drawImage(runLowerBody.get(animationStep % runLowerBody.size()), 0, 0, runLowerBody.get(animationStep % runLowerBody.size()).getWidth(), runLowerBody.get(animationStep % runLowerBody.size()).getHeight(), posX - runLowerBody.get(animationStep % runUpperBody.size()).getWidth() / 2 + 3 + 30, posY - runLowerBody.get(animationStep % runLowerBody.size()).getHeight(), runLowerBody.get(animationStep % runLowerBody.size()).getWidth() * facing, runLowerBody.get(animationStep % runLowerBody.size()).getHeight());
                        graphicsContext.drawImage(runUpperBody.get(animationStep % runUpperBody.size()), 0, 0, runUpperBody.get(animationStep % runUpperBody.size()).getWidth(), runUpperBody.get(animationStep % runUpperBody.size()).getHeight(), posX - 19 + 33, posY - 35 + offset, runUpperBody.get(animationStep % runUpperBody.size()).getWidth() * facing, runUpperBody.get(animationStep % runUpperBody.size()).getHeight());
                    }
                    break;
                }
                case 2 -> {
                    if (facing == 1) {
                        graphicsContext.drawImage(head.get(1), 0, 0, head.get(1).getWidth(), head.get(1).getHeight(), posX - 15, posY - 68 + offset, head.get(1).getWidth() * facing, head.get(1).getHeight());
                        graphicsContext.drawImage(runLowerBody.get(animationStep % runLowerBody.size()), 0, 0, runLowerBody.get(animationStep % runLowerBody.size()).getWidth(), runLowerBody.get(animationStep % runLowerBody.size()).getHeight(), posX - runLowerBody.get(animationStep % runUpperBody.size()).getWidth() / 2 + 2, posY - runLowerBody.get(animationStep % runLowerBody.size()).getHeight(), runLowerBody.get(animationStep % runLowerBody.size()).getWidth() * facing, runLowerBody.get(animationStep % runLowerBody.size()).getHeight());
                        graphicsContext.drawImage(runUpperBody.get(animationStep % runUpperBody.size()), 0, 0, runUpperBody.get(animationStep % runUpperBody.size()).getWidth(), runUpperBody.get(animationStep % runUpperBody.size()).getHeight(), posX - 10, posY - 33 + offset, runUpperBody.get(animationStep % runUpperBody.size()).getWidth() * facing, runUpperBody.get(animationStep % runUpperBody.size()).getHeight());
                    } else if (facing == -1) {
                        graphicsContext.drawImage(head.get(1), 0, 0, head.get(1).getWidth(), head.get(1).getHeight(), posX - 15 + 25, posY - 68 + offset, head.get(1).getWidth() * facing, head.get(1).getHeight());
                        graphicsContext.drawImage(runLowerBody.get(animationStep % runLowerBody.size()), 0, 0, runLowerBody.get(animationStep % runLowerBody.size()).getWidth(), runLowerBody.get(animationStep % runLowerBody.size()).getHeight(), posX - runLowerBody.get(animationStep % runUpperBody.size()).getWidth() / 2 + 2 + 23, posY - runLowerBody.get(animationStep % runLowerBody.size()).getHeight(), runLowerBody.get(animationStep % runLowerBody.size()).getWidth() * facing, runLowerBody.get(animationStep % runLowerBody.size()).getHeight());
                        graphicsContext.drawImage(runUpperBody.get(animationStep % runUpperBody.size()), 0, 0, runUpperBody.get(animationStep % runUpperBody.size()).getWidth(), runUpperBody.get(animationStep % runUpperBody.size()).getHeight(), posX - 10 + 16, posY - 33 + offset, runUpperBody.get(animationStep % runUpperBody.size()).getWidth() * facing, runUpperBody.get(animationStep % runUpperBody.size()).getHeight());
                    }
                    break;
                }
                case 3 -> {
                    if (facing == 1) {
                        graphicsContext.drawImage(head.get(1), 0, 0, head.get(1).getWidth(), head.get(1).getHeight(), posX - 15, posY - 68 + offset, head.get(1).getWidth() * facing, head.get(1).getHeight());
                        graphicsContext.drawImage(runLowerBody.get(animationStep % runLowerBody.size()), 0, 0, runLowerBody.get(animationStep % runLowerBody.size()).getWidth(), runLowerBody.get(animationStep % runLowerBody.size()).getHeight(), posX - runLowerBody.get(animationStep % runLowerBody.size()).getWidth() / 2 + 4, posY - runLowerBody.get(animationStep % runLowerBody.size()).getHeight(), runLowerBody.get(animationStep % runLowerBody.size()).getWidth() * facing, runLowerBody.get(animationStep % runLowerBody.size()).getHeight());
                        graphicsContext.drawImage(runUpperBody.get(animationStep % runUpperBody.size()), 0, 0, runUpperBody.get(animationStep % runUpperBody.size()).getWidth(), runUpperBody.get(animationStep % runUpperBody.size()).getHeight(), posX - 13, posY - 33 + offset, runUpperBody.get(animationStep % runUpperBody.size()).getWidth() * facing, runUpperBody.get(animationStep % runUpperBody.size()).getHeight());
                    } else if (facing == -1) {
                        graphicsContext.drawImage(head.get(1), 0, 0, head.get(1).getWidth(), head.get(1).getHeight(), posX - 15 + 25, posY - 68 + offset, head.get(1).getWidth() * facing, head.get(1).getHeight());
                        graphicsContext.drawImage(runLowerBody.get(animationStep % runLowerBody.size()), 0, 0, runLowerBody.get(animationStep % runLowerBody.size()).getWidth(), runLowerBody.get(animationStep % runLowerBody.size()).getHeight(), posX - runLowerBody.get(animationStep % runLowerBody.size()).getWidth() / 2 + 4 + 33, posY - runLowerBody.get(animationStep % runLowerBody.size()).getHeight(), runLowerBody.get(animationStep % runLowerBody.size()).getWidth() * facing, runLowerBody.get(animationStep % runLowerBody.size()).getHeight());
                        graphicsContext.drawImage(runUpperBody.get(animationStep % runUpperBody.size()), 0, 0, runUpperBody.get(animationStep % runUpperBody.size()).getWidth(), runUpperBody.get(animationStep % runUpperBody.size()).getHeight(), posX - 13 + 23, posY - 33 + offset, runUpperBody.get(animationStep % runUpperBody.size()).getWidth() * facing, runUpperBody.get(animationStep % runUpperBody.size()).getHeight());
                    }
                    break;
                }
                case 4 -> {
                    if (facing == 1) {
                        graphicsContext.drawImage(head.get(1), 0, 0, head.get(1).getWidth(), head.get(1).getHeight(), posX - 15, posY - 68 + offset, head.get(1).getWidth() * facing, head.get(1).getHeight());
                        graphicsContext.drawImage(runLowerBody.get(animationStep % runLowerBody.size()), 0, 0, runLowerBody.get(animationStep % runLowerBody.size()).getWidth(), runLowerBody.get(animationStep % runLowerBody.size()).getHeight(), posX - runLowerBody.get(animationStep % runUpperBody.size()).getWidth() / 2 + 4, posY - runLowerBody.get(animationStep % runLowerBody.size()).getHeight(), runLowerBody.get(animationStep % runLowerBody.size()).getWidth() * facing, runLowerBody.get(animationStep % runLowerBody.size()).getHeight());
                        graphicsContext.drawImage(runUpperBody.get(animationStep % runUpperBody.size()), 0, 0, runUpperBody.get(animationStep % runUpperBody.size()).getWidth(), runUpperBody.get(animationStep % runUpperBody.size()).getHeight(), posX - 9, posY - 33 + offset, runUpperBody.get(animationStep % runUpperBody.size()).getWidth() * facing, runUpperBody.get(animationStep % runUpperBody.size()).getHeight());
                    } else if (facing == -1) {
                        graphicsContext.drawImage(head.get(1), 0, 0, head.get(1).getWidth(), head.get(1).getHeight(), posX - 15 + 25, posY - 68 + offset, head.get(1).getWidth() * facing, head.get(1).getHeight());
                        graphicsContext.drawImage(runLowerBody.get(animationStep % runLowerBody.size()), 0, 0, runLowerBody.get(animationStep % runLowerBody.size()).getWidth(), runLowerBody.get(animationStep % runLowerBody.size()).getHeight(), posX - runLowerBody.get(animationStep % runUpperBody.size()).getWidth() / 2 + 4 + 23, posY - runLowerBody.get(animationStep % runLowerBody.size()).getHeight(), runLowerBody.get(animationStep % runLowerBody.size()).getWidth() * facing, runLowerBody.get(animationStep % runLowerBody.size()).getHeight());
                        graphicsContext.drawImage(runUpperBody.get(animationStep % runUpperBody.size()), 0, 0, runUpperBody.get(animationStep % runUpperBody.size()).getWidth(), runUpperBody.get(animationStep % runUpperBody.size()).getHeight(), posX - 9 + 17, posY - 33 + offset, runUpperBody.get(animationStep % runUpperBody.size()).getWidth() * facing, runUpperBody.get(animationStep % runUpperBody.size()).getHeight());
                    }
                    break;
                }
                break;
            }
        }
        //Draw a small dot at player position for simple debug
        double radius = 3;
        //Stoking
        graphicsContext.strokeOval(posX-radius, posY-radius, radius*2, radius*2);
        //Filling:
        graphicsContext.fillOval(posX-radius, posY-radius, radius*2, radius*2);


        posX = posXTemp;
        posY = posYTemp;

        if (key.skill1 == 1) skill1.render(graphicsContext);
        if (key.skill2 == 1) skill2.render(graphicsContext);





    }

    public int getFacing() {
        return facing;
    }

    private void drawWalkAnimation(int s) {

    }

    public void keyIn(KeyEvent keyEvent) {
        //System.out.println("Left");
        if (keyEvent.getEventType().equals(KeyEvent.KEY_PRESSED)) {
            System.out.println("Key pressed");
            switch (keyEvent.getCode()) {
                case UP -> {
                    key.up = 1;
                    break;
                }
                case DOWN -> {
                    key.down = 1;
                    break;
                }
                case LEFT -> {
                    key.left = 1;
                    break;
                }
                case RIGHT -> {
                    key.right = 1;
                    break;
                }
                case Q -> {
                    key.skill1 =1;
                    USESKILL1 = true;
                    break;
                }
                case E -> {
                    key.skill2 = 1;
                    USESKILL2= true;
                    break;
                }
            }
        }
        else {
            System.out.println("Key released");
            switch (keyEvent.getCode()) {
                case UP -> {
                    key.up = 0;
                    break;
                }
                case DOWN -> {
                    key.down = 0;
                    break;
                }
                case LEFT -> {
                    key.left = 0;
                    break;
                }
                case RIGHT -> {
                    key.right = 0;
                    break;
                }
                case Q -> {
                    key.skill1 = 0;
                    USESKILL1 = false;
                    break;
                }
                case E -> {
                    key.skill2 = 0;
                    USESKILL2 = false;
                    break;
                }

            }
        }
    }

    //Method does not control animation
    private void jump() {

    }
    //Method does not control animation
    private void left() { //Method does not control animation

    }
    //Method does not control animation
    private void right(){

    }
}
