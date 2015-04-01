package com.mayying.tileMapGame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.mayying.tileMapGame.GameWorld;


/**
 * Created by May on 17/3/2015.
 */


public class SideBar {

    private Stage stage;
    private Label timer, scoreboard, descriptionImg, descriptionText;
    private Skin skin;
    private GameWorld world;
    private ImageButton buttonA, buttonB, sound, question, close;
    private OrthographicCamera hudCamera;
    private final Rectangle screenBound;
    private LabelStyle labelStyle;
    //  public static Touchpad touchpad;

    volatile static int timeLeft = 1;
    private int numOfPowerUp = 0;

    private float gameTime = 1 * 60 + 30;
    private int min, sec;

    SideBar(GameWorld world) {
        this.world = world;
        hudCamera = new OrthographicCamera();
        min = 1;
        sec = 30;
        screenBound = new Rectangle(GameWorld.screenBound.getX() + GameWorld.screenBound.getWidth() + GameWorld.TILE_WIDTH,
                0, GameWorld.TILE_WIDTH * 3,
                GameWorld.TILE_HEIGHT * 10);
        labelStyle = new Label.LabelStyle();
    }

    public void create() {
        stage = new Stage(new ExtendViewport(Play.V_WIDTH, Play.V_HEIGHT, hudCamera));
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
//
        inputMultiplexer.addProcessor(stage);
        inputMultiplexer.addProcessor(world.getDirectionGestureDetector());
        Gdx.input.setInputProcessor(inputMultiplexer);

//        Gdx.input.setInputProcessor(stage);

        TextureAtlas buttonAtlas = new TextureAtlas(Gdx.files.internal("skin/skin.txt"));
        skin = new Skin(Gdx.files.internal("skin/gameSkin.json"), buttonAtlas);

        // container for all UI widgets
        Table table = new Table(skin);
        table.setFillParent(true);
        table.setBounds(0, 0, Play.V_WIDTH, Play.V_HEIGHT);
        table.align(Align.top);
        // table.setDebug(true);
        // tableBtm.setDebug(true);

        timer = new Label("Time Left\n" + min + " : " + sec, skin, "timer");
        timer.setAlignment(Align.center);

        sound = new ImageButton(skin, "sound");
        question = new ImageButton(skin, "question");
        close = new ImageButton(skin, "close");
        close.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        scoreboard = new Label("Score Board", skin, "scoreboard");

        descriptionImg = new Label("", skin, "description");
        descriptionImg.setAlignment(Align.bottom);
        descriptionText = new Label("", skin, "description");
        descriptionText.setWrap(true);
        descriptionText.setAlignment(Align.top);
        // descriptionText.setAlignment(Align.center);
        descriptionText.setFontScale(0.75f);

        world.getMyTouchPad().getTouchPad().setPosition(0, 0);

        buttonA = new ImageButton(skin);
        buttonB = new ImageButton(skin);

        Table subTable = new Table();
        subTable.add(buttonA).right().expandX().expandY().width(140).height(140).center().row();
        subTable.add(buttonB).left().expandX().width(140).height(140).row();

        Table descriptionTable = new Table();
        descriptionTable.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture("skin/skinSquare280x210.png"))));
        descriptionTable.add(descriptionImg).padTop(40).width(90).height(90).row();
        descriptionTable.add(descriptionText).expandY().width(150).height(140).top().center();
        // descriptionTable.setDebug(true);

        // putting stuff together
        //table.align(Align.center);
        table.add(timer).top().left().expandX().padTop(10).height(140).width(210);
        table.add(sound).top();
        table.add(question).top();
        table.add(close).top().row();

        table.add(scoreboard).left().expandX().height(280).width(210);
        table.add(descriptionTable).fill().colspan(3).row();

        table.add(world.getMyTouchPad().getTouchPad()).left().expandY().width(210);
        table.add(subTable).fill().colspan(3);

        stage.addActor(table);

        buttonA.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if(!buttonA.isDisabled()) {
                    buttonA.setDisabled(true);
                    buttonA.setChecked(true);
                    numOfPowerUp--;
                }
//                world.getPlayer().spacePressed();
            }
        });

    }

    public void render(float delta) {
        stage.act(delta);
        stage.draw();
        gameTime -= delta;
        int minutes = (int) Math.floor(gameTime / 60.0f);
        int seconds = (int) (gameTime - minutes * 60.0f);
        timeLeft = minutes * 60 + seconds;
        timer.setText("Time Left\n" + String.format("%02d : %02d", minutes, seconds));
//        Gdx.app.log(world.getPlayer().canPickPowerUp() + " canPickPowerUp() ", world.pickUpPowerUp() + " pickUpPowerUp()");
        if (world.pickUpPowerUp()) {
            numOfPowerUp++;
            descriptionText.setText(world.getPowerUp().getName() + "\n" + world.getPowerUp().getDescription());
            labelStyle.background = skin.getDrawable(world.getPowerUp().getFilename());
            labelStyle.font = new BitmapFont(Gdx.files.internal("font/black.fnt"));
            descriptionImg.setStyle(labelStyle);

            ImageButtonStyle imageButtonStyle = new ImageButtonStyle();
            imageButtonStyle.imageUp = skin.getDrawable(world.getPowerUp().getFilenameBtn());
            Gdx.app.log(numOfPowerUp + "", world.getPowerUp().getFilenameBtn() + "");
            imageButtonStyle.imageChecked = skin.getDrawable("skinRound140x140");
            Gdx.app.log(numOfPowerUp + " numOfPowerUp ", "");
            if (numOfPowerUp == 1) {
                Gdx.app.log(buttonA.isDisabled()
                        + " buttonA.isDisabled()", buttonA.isChecked() + " buttonA.isChecked()");
                buttonA.setDisabled(false);
                buttonA.setChecked(false);
                buttonA.setStyle(imageButtonStyle);

            } else if (numOfPowerUp == 2) {
                buttonB.setStyle(imageButtonStyle);
            }


        }
    }

    public void dispose() {
        stage.dispose();
    }


}

