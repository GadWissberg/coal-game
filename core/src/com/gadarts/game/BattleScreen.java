package com.gadarts.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.gadarts.engine.SkyBoxTextures;
import com.gadarts.engine.World;
import com.gadarts.engine.components.model.ModelDefinition;
import com.gadarts.engine.exceptions.GameFailureException;
import com.gadarts.engine.properties.EnemyDefinitionProperties;
import com.gadarts.engine.properties.PickupDefinitionProperties;
import com.gadarts.engine.properties.PlayerProperties;
import com.gadarts.game.hand.Hand;
import com.gadarts.game.utils.C;
import com.gadarts.game.utils.C.Enemies;
import com.gadarts.game.utils.Profiler;

public class BattleScreen implements Screen {
    private World world;
    private AssetManager assets;
    private boolean loading;
    private Stage stage;
    private Hand hand;
    private ModelDefinition bulletModelDef;
    private Model bulletHoleModel;
    private Profiler profiler;
    private boolean paused;

    @Override
    public void show() {
        world = new World();
        defineWeapons();
        world.initializePickups(C.Pickups.RADIUS, C.Pickups.PICKUP_COLOR);
        loadSkyBox();
        assets.load("enemy.txt", TextureAtlas.class);
        assets.load("textures.txt", TextureAtlas.class);
        createUI();
    }

    private void loadSkyBox() {
        assets.load("top.png", Texture.class);
        assets.load("north.png", Texture.class);
        assets.load("east.png", Texture.class);
        assets.load("south.png", Texture.class);
    }

    private void initializeWeaponHand() {
        hand = new Hand(world);
        stage.addActor(hand);
        world.getPlayerController().subscribeForPositionEvents(hand);
    }

    private void createUI() {
        OrthographicCamera camera = new OrthographicCamera(C.Resolution.UI_WORLD_WIDTH, C.Resolution.UI_WORLD_HEIGHT);
        stage = new Stage(new StretchViewport(C.Resolution.UI_WORLD_WIDTH, C.Resolution.UI_WORLD_HEIGHT, camera));
        camera.position.set(C.Resolution.UI_WORLD_WIDTH / 2, C.Resolution.UI_WORLD_HEIGHT / 2, 0);
        profiler = new Profiler(stage);
    }

    private void defineWeapons() {
        createPickupTestGun();
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();

        Material bulletMaterial = new Material(TextureAttribute.createDiffuse(new Texture(Gdx.files.internal("bullet.png"))));
        bulletMaterial.set(new ColorAttribute(ColorAttribute.Diffuse, 1f, 1f, 1f, 1f));
        MeshPartBuilder meshBuilder = modelBuilder.part("bullet", GL20.GL_TRIANGLES,
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal |
                        VertexAttributes.Usage.TextureCoordinates, bulletMaterial);
        meshBuilder.setUVRange(1, 1, 0, 0);
        float width = 2;
        float height = 0.1f;
        meshBuilder.rect(
                -width / 2, -height / 2, 0,
                width / 2, -height / 2, 0,
                width / 2, height / 2, 0,
                -width / 2, height / 2, 0,
                0, 0, 0);
        meshBuilder.setUVRange(0, 0, 1, 1);
        meshBuilder.rect(
                width / 2, -height / 2, 0,
                -width / 2, -height / 2, 0,
                -width / 2, height / 2, 0,
                width / 2, height / 2, 0,
                0, 0, 0);
        Model model = modelBuilder.end();
        bulletModelDef = new ModelDefinition(model, "bullet");
        loading = true;
    }

    private void createPickupTestGun() {
        assets = new AssetManager();
        assets.load("gun.g3db", Model.class);
        assets.load("weapon.txt", TextureAtlas.class);
    }

    private void initPlayer() {
        PlayerProperties properties = new PlayerProperties();
        initializePlayerProperties(properties);
        world.initializePlayer(properties);
        initializeWeaponHand();
    }

    private void initializePlayerProperties(PlayerProperties properties) {
        initializePlayerMovementProperties(properties);
        properties.setFov(C.Player.FOV);
        properties.setBodyAltitude(C.Player.HEAD_ALTITUDE);
    }

    private void initializePlayerMovementProperties(PlayerProperties properties) {
        properties.setMaxMovementAltitudeSpeed(C.Player.PlayerMovement.MOVEMENT_ALTITUDE_SPEED);
        properties.setMaxSpeed(C.Player.PlayerMovement.MAX_SPEED);
        properties.setMinSpeed(-C.Player.PlayerMovement.MAX_SPEED);
        properties.setMaxStepAltitude(C.Player.PlayerMovement.MAX_STEP_ALTITUDE);
        properties.setRaiseSpeed(C.Player.PlayerMovement.RAISE_SPEED);
        properties.setRadius(C.Player.PlayerMovement.RADIUS);
        properties.setJumpSpeed(C.Player.PlayerMovement.JUMP);
    }

    private void doneLoading() {
        loading = false;
        try {
            PickupDefinitionProperties gdp = new PickupDefinitionProperties();
            gdp.setModelDefinition(new ModelDefinition(assets.get("gun.g3db", Model.class), "pickup"));
            gdp.defineOnPickUp(new Runnable() {
                                   @Override
                                   public void run() {
                                       Weapons.setWeaponState(Weapons.Weapon.HAND_GUN, true);
                                       Weapons.setSelected(Weapons.Weapon.HAND_GUN);
                                       hand.init(assets.get("weapon.txt", TextureAtlas.class));
                                   }
                               }
            );
            gdp.setName(C.Pickups.GUN);
            gdp.setBodyAltitude(C.Pickups.BODY_ALTITUDE);
            gdp.setRadius(C.Pickups.RADIUS);
            world.definePickup(gdp);
            world.defineBullet(C.Bullets.GUN_BULLET, bulletModelDef, C.Bullets.BULLET_SPEED, C.Bullets.RADIUS, bulletHoleModel);
            EnemyDefinitionProperties enemyProperties = new EnemyDefinitionProperties(Enemies.ENEMY_TEST, assets.get("enemy.txt", TextureAtlas.class));
            enemyProperties.setSpeed(Enemies.SPEED);
            enemyProperties.setRadius(Enemies.RADIUS);
            enemyProperties.setFOV(Enemies.FOV);
            enemyProperties.setHP(Enemies.HP);
            enemyProperties.setPainChance(Enemies.PAIN_CHANCE);
            world.defineEnemy(enemyProperties);
            SkyBoxTextures skyBoxTextures = new SkyBoxTextures();
            skyBoxTextures.setTop(assets.get("top.png", Texture.class));
            skyBoxTextures.setEast(assets.get("east.png", Texture.class));
            skyBoxTextures.setNorth(assets.get("north.png", Texture.class));
            skyBoxTextures.setSouth(assets.get("south.png", Texture.class));
            skyBoxTextures.setWest(assets.get("east.png", Texture.class));
            world.init(C.GRAVITY, skyBoxTextures, assets.get("textures.txt", TextureAtlas.class));
            initPlayer();
            world.setInputProcessor(new InputHandler(world.getPlayerController(), hand));
        } catch (GameFailureException e) {
            stage.addActor(GameConsole.error(e.getMessage()));
            paused = true;
            e.printStackTrace();
        }
    }

    @Override
    public void render(float delta) {
        if (loading) {
            if (assets.update()) doneLoading();
        } else {
            if (!paused) {
                try {
                    world.render(delta);
                } catch (GameFailureException e) {
                    e.printStackTrace();
                }
            }
            renderUi(delta);
        }
    }

    private void renderUi(float delta) {
        profiler.update();
        stage.act(delta);
        stage.getBatch().setProjectionMatrix(stage.getCamera().combined);
        stage.getCamera().update();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
