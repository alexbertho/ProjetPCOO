package io.github.Wasnowl.screens;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.CircleMapObject;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import io.github.Wasnowl.managers.MusicManager;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.Wasnowl.GameMain;
import io.github.Wasnowl.entities.*;
import io.github.Wasnowl.managers.WaveManager;
import io.github.Wasnowl.managers.ProjectileManager;
import io.github.Wasnowl.builders.TowerBuilder;
import io.github.Wasnowl.managers.CurrencyManager;
import io.github.Wasnowl.entities.TowerType;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class GameScreen extends ScreenAdapter {
    private final GameMain game;
    private String mapPath = "maps/BeginningFields.tmx";
    private static final int DEFAULT_WINDOW_WIDTH = 1280;
    private static final int DEFAULT_WINDOW_HEIGHT = 720;
    private static final String PAUSE_TITLE = "Pause";
    private static final String OPTIONS_TITLE = "Options";
    private static final String BLUR_VERTEX_SHADER =
        "attribute vec4 a_position;\n" +
        "attribute vec4 a_color;\n" +
        "attribute vec2 a_texCoord0;\n" +
        "uniform mat4 u_projTrans;\n" +
        "varying vec4 v_color;\n" +
        "varying vec2 v_texCoords;\n" +
        "void main() {\n" +
        "    v_color = a_color;\n" +
        "    v_texCoords = a_texCoord0;\n" +
        "    gl_Position = u_projTrans * a_position;\n" +
        "}\n";
    private static final String BLUR_FRAGMENT_SHADER =
        "#ifdef GL_ES\n" +
        "precision mediump float;\n" +
        "#endif\n" +
        "varying vec4 v_color;\n" +
        "varying vec2 v_texCoords;\n" +
        "uniform sampler2D u_texture;\n" +
        "uniform vec2 u_texelSize;\n" +
        "void main() {\n" +
        "    vec4 sum = vec4(0.0);\n" +
        "    sum += texture2D(u_texture, v_texCoords) * 0.16;\n" +
        "    sum += texture2D(u_texture, v_texCoords + vec2(u_texelSize.x, 0.0)) * 0.12;\n" +
        "    sum += texture2D(u_texture, v_texCoords - vec2(u_texelSize.x, 0.0)) * 0.12;\n" +
        "    sum += texture2D(u_texture, v_texCoords + vec2(0.0, u_texelSize.y)) * 0.12;\n" +
        "    sum += texture2D(u_texture, v_texCoords - vec2(0.0, u_texelSize.y)) * 0.12;\n" +
        "    sum += texture2D(u_texture, v_texCoords + u_texelSize) * 0.09;\n" +
        "    sum += texture2D(u_texture, v_texCoords - u_texelSize) * 0.09;\n" +
        "    sum += texture2D(u_texture, v_texCoords + vec2(u_texelSize.x, -u_texelSize.y)) * 0.09;\n" +
        "    sum += texture2D(u_texture, v_texCoords + vec2(-u_texelSize.x, u_texelSize.y)) * 0.09;\n" +
        "    gl_FragColor = v_color * sum;\n" +
        "}\n";


    private OrthographicCamera camera;
    private OrthographicCamera screenCamera;
    private Viewport viewport;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private SpriteBatch batch;
    private int mapPixelWidth;
    private int mapPixelHeight;
    private Array<Rectangle> collisionRects;
    private FrameBuffer backgroundBuffer;
    private TextureRegion backgroundRegion;
    private ShaderProgram blurShader;
    // UI
    private Stage uiStage;
    private Skin uiSkin;
    private Actor towerMenuWindow;
    private boolean showTowerMenu = false;
    private boolean placingPreview = false;
    private TowerType previewTowerType = null;
    private Vector2 previewPosition = new Vector2();
    private ShapeRenderer shapeRenderer;
    private com.badlogic.gdx.InputMultiplexer inputMultiplexer;
    private BitmapFont hudFont;
    private Label balanceLabel;
    private Label costLabel;
    private TextButton nextWaveButton;
    private TextButton towersButton;
    private Table hudTable;
    private Table towerButtonTable;
    private Table bottomRightTable;
    private Window pauseWindow;
    private Window pauseOptionsWindow;
    private boolean paused = false;

    private PlayerTower player;
    private Array<Tower> towers;
    private Array<Enemy> enemies;
    private Array<Projectile> projectiles;
    private ProjectileManager projectileManager;
    private CurrencyManager currencyManager;

    private WaveManager waveManager;


    public GameScreen(GameMain game) {
        this.game = game;
    }

    public GameScreen(GameMain game, String mapPath) {
        this.game = game;
        this.mapPath = mapPath;
    }

    @Override
    public void show() {

        batch = new SpriteBatch();

        // Initialisation du joueur

        // Initialisation des listes (doit précéder la création du joueur/tour)
        towers = new Array<>();
        enemies = new Array<>();
        projectiles = new Array<>();
        projectileManager = new ProjectileManager(projectiles);

        Array<Portal> portals = new Array<>();
        // Exemple : portal large sur la bordure inférieure (y proche de 0)
        portals.add(new Portal(new Rectangle(350, 0, 100, 16), "combat/SideBoss", Portal.Type.COMBAT));
        // Exemple de portal menant à une autre map top-down
        portals.add(new Portal(new Rectangle(750, 550, 50, 50), "maps/NextLevel.tmx", Portal.Type.MAP));

        player = new PlayerTower(100, 100, 150, 1f, enemies, projectiles, portals, game);
        // Currency manager
        currencyManager = new CurrencyManager(50); // monnaie de départ
        waveManager = new WaveManager(enemies, currencyManager);

        // ShapeRenderer for preview
        shapeRenderer = new ShapeRenderer();

        // Charger les sprites de projectiles (si présents) depuis assets/projectiles/
        try {
            com.badlogic.gdx.graphics.Texture tSimple = null;
            com.badlogic.gdx.graphics.Texture tAoe = null;
            com.badlogic.gdx.graphics.Texture tRic = null;
            if (Gdx.files.internal("projectiles/simple.png").exists()) {
                tSimple = new com.badlogic.gdx.graphics.Texture(Gdx.files.internal("projectiles/simple.png"));
            }
            if (Gdx.files.internal("projectiles/aoe.png").exists()) {
                tAoe = new com.badlogic.gdx.graphics.Texture(Gdx.files.internal("projectiles/aoe.png"));
            }
            if (Gdx.files.internal("projectiles/ricochet.png").exists()) {
                tRic = new com.badlogic.gdx.graphics.Texture(Gdx.files.internal("projectiles/ricochet.png"));
            }

            float frameDuration = 0.12f;
            java.util.function.BiConsumer<com.badlogic.gdx.graphics.Texture, io.github.Wasnowl.entities.ProjectileType> buildAnim = (tex, ptype) -> {
                if (tex == null) return;
                int frameCount = 2;
                int fw = tex.getWidth() / frameCount;
                int fh = tex.getHeight();
                com.badlogic.gdx.graphics.g2d.TextureRegion[][] tmp = com.badlogic.gdx.graphics.g2d.TextureRegion.split(tex, fw, fh);
                com.badlogic.gdx.utils.Array<com.badlogic.gdx.graphics.g2d.TextureRegion> frames = new com.badlogic.gdx.utils.Array<>();
                for (int i = 0; i < frameCount; i++) frames.add(tmp[0][i]);
                com.badlogic.gdx.graphics.g2d.Animation<com.badlogic.gdx.graphics.g2d.TextureRegion> anim = new com.badlogic.gdx.graphics.g2d.Animation<>(frameDuration, frames, com.badlogic.gdx.graphics.g2d.Animation.PlayMode.LOOP);
                io.github.Wasnowl.managers.ProjectileAssetManager.getInstance().setProjectileAnimation(ptype, anim);
            };

            // SIMPLE et le joueur
            buildAnim.accept(tSimple, io.github.Wasnowl.entities.ProjectileType.SIMPLE);
            // AOE
            buildAnim.accept(tAoe, io.github.Wasnowl.entities.ProjectileType.AOE);
            buildAnim.accept(tAoe, io.github.Wasnowl.entities.ProjectileType.AOE_STRONG);
            // RICOCHET
            buildAnim.accept(tRic, io.github.Wasnowl.entities.ProjectileType.RICOCHET);

        } catch (Exception e) {
            Gdx.app.log("ASSETS", "Failed to load projectile sprites: " + e.getMessage());
        }

        // Charger la carte depuis assets/maps/
        map = new TmxMapLoader().load(mapPath);
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1f);
        configureViewportForMap();
        collisionRects = buildCollisionRects();
        player.setCollisionRects(collisionRects);
        player.setWorldBounds(mapPixelWidth, mapPixelHeight);
        int tileWidth = map.getProperties().get("tilewidth", Integer.class);
        int tileHeight = map.getProperties().get("tileheight", Integer.class);
        float step = Math.min(tileWidth, tileHeight);
        float searchRadius = Math.max(mapPixelWidth, mapPixelHeight);
        player.setPositionSafe(mapPixelWidth * 0.5f, mapPixelHeight * 0.5f, searchRadius, step);
        setupBackgroundBlur();

        // UI stage for tower menu (use same aspect ratio as the map)
        uiSkin = new Skin(Gdx.files.internal("uiskin.json"));
        uiStage = new Stage(new FitViewport(mapPixelWidth, mapPixelHeight));
        uiStage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        // HUD font and labels
        hudFont = new BitmapFont(Gdx.files.internal("default.fnt"));
        balanceLabel = new Label("Gold: " + currencyManager.getBalance(), new Label.LabelStyle(hudFont, Color.WHITE));
        costLabel = new Label("", new Label.LabelStyle(hudFont, Color.YELLOW));

        // Top-left HUD table
        hudTable = new Table();
        hudTable.setFillParent(true);
        hudTable.top().left();
        hudTable.add(balanceLabel).pad(6).row();
        hudTable.add(costLabel).pad(6);
        uiStage.addActor(hudTable);

        // Bottom-center button to open tower menu
        towerButtonTable = new Table();
        towerButtonTable.setFillParent(true);
        uiStage.addActor(towerButtonTable);

        towersButton = new TextButton("Towers", uiSkin);
        towerButtonTable.bottom().add(towersButton).padBottom(8);

        // Tower selection window (created when needed)
        towersButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (paused) {
                    return;
                }
                toggleTowerMenu();
            }
        });

        // Bottom-right Next Wave button
        nextWaveButton = new TextButton("Next Wave", uiSkin);
        nextWaveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (waveManager != null && waveManager.isWaveFinished()) {
                    waveManager.startNextWave();
                }
            }
        });
        bottomRightTable = new Table();
        bottomRightTable.setFillParent(true);
        bottomRightTable.bottom().right();
        bottomRightTable.add(nextWaveButton).pad(8);
        uiStage.addActor(bottomRightTable);

        inputMultiplexer = new com.badlogic.gdx.InputMultiplexer();
        inputMultiplexer.addProcessor(uiStage);
        inputMultiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (paused) {
                    return false;
                }
                // Left click placement
                if (placingPreview && button == com.badlogic.gdx.Input.Buttons.LEFT) {
                    Vector3 tmp = new Vector3(screenX, screenY, 0);
                    viewport.unproject(tmp);
                    float wx = tmp.x;
                    float wy = tmp.y;
                    // simple overlap check: don't place if too close to existing tower
                    boolean overlap = false;
                    for (Tower t : towers) {
                        if (t.getPosition().dst(wx, wy) < 32f) { overlap = true; break; }
                    }
                    if (!overlap && previewTowerType != null && currencyManager.canAfford(previewTowerType.getCost())) {
                        currencyManager.spend(previewTowerType.getCost());
                        balanceLabel.setText("Gold: " + currencyManager.getBalance());
                        Tower placed = new TowerBuilder(wx, wy)
                            .withRange(previewTowerType.getProjectileType().isAOE() ? 180f : 200f)
                            .withFireRate(previewTowerType.getProjectileType().isAOE() ? 1f : 1.5f)
                            .withTowerType(previewTowerType)
                            .withEnemies(enemies)
                            .withProjectiles(projectiles)
                            .build();
                        towers.add(placed);
                    }
                    // exit placement mode
                    placingPreview = false;
                    previewTowerType = null;
                    costLabel.setText("");
                    return true;
                }
                return false;
            }

            @Override
            public boolean keyDown(int keycode) {
                if (keycode == com.badlogic.gdx.Input.Keys.ESCAPE) {
                    togglePause();
                    return true;
                }
                return false;
            }
        });

        Gdx.input.setInputProcessor(inputMultiplexer);

        // Callback pour mettre à jour l'UI quand l'argent change
        waveManager.setOnMoneyChanged(() -> {
            balanceLabel.setText("Gold: " + currencyManager.getBalance());
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderBackgroundBlur();
        if (!paused) {
            handleCameraInput(delta);
        }

        viewport.apply();
        camera.update();
        mapRenderer.setView(camera);
        mapRenderer.render();

        // Rendre les object layers (buildings, trees, props, etc.)
        renderMapObjectLayer(batch);

        if (!paused) {
            // Update
            if (waveManager != null) {
                waveManager.update(delta);
                // Activer/désactiver le bouton Next Wave selon l'état de la vague
                if (nextWaveButton != null) {
                    nextWaveButton.setDisabled(!waveManager.isWaveFinished());
                }
            }
            player.update(delta);
            for (Tower t : towers) t.update(delta);
            projectileManager.update(delta);  // Utiliser le ProjectileManager
        }

        // Render
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        player.render(batch);
        for (Tower t : towers) t.render(batch);
        for (Enemy e : enemies) e.render(batch);
        projectileManager.render(batch);  // Utiliser le ProjectileManager
        batch.end();

        // Projectiles are now rendered by their Sprite/Animation via ProjectileManager

        // Render preview (using ShapeRenderer)
        if (!paused && placingPreview && previewTowerType != null) {
            Vector3 mouse = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            viewport.unproject(mouse);
            previewPosition.set(mouse.x, mouse.y);

            batch.begin();
            // optionally draw ghost with lower alpha via shape renderer
            batch.end();

            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            Color c = previewTowerType == TowerType.AOE ? new Color(1f, 0.5f, 0.2f, 0.5f) : new Color(0.2f, 0.8f, 0.2f, 0.5f);
            shapeRenderer.setColor(c);
            float w = 32f, h = 32f;
            shapeRenderer.rect(previewPosition.x - w/2f, previewPosition.y - h/2f, w, h);
            shapeRenderer.end();
        }

        // Draw UI on top
        if (uiStage != null) {
            uiStage.act(delta);
            uiStage.draw();
        }
    }

    private void handleCameraInput(float delta) {
        float speed = 200 * delta;
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.LEFT)) camera.position.x -= speed;
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.RIGHT)) camera.position.x += speed;
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.UP)) camera.position.y += speed;
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.DOWN)) camera.position.y -= speed;
        // Centrer la caméra sur le joueur si ESPACE est pressé
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.SPACE)) {
            camera.position.x = player.getPosition().x;
            camera.position.y = player.getPosition().y;
        }
    }

    @Override
    public void resize(int width, int height) {
        if (viewport != null) viewport.update(width, height);
        if (uiStage != null) uiStage.getViewport().update(width, height, true);
        if (screenCamera != null) screenCamera.setToOrtho(false, width, height);
        centerWindow(pauseWindow);
        centerWindow(pauseOptionsWindow);
    }

    @Override
    public void dispose() {
        map.dispose();
        mapRenderer.dispose();
        batch.dispose();
        if (uiStage != null) uiStage.dispose();
        if (uiSkin != null) uiSkin.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (hudFont != null) hudFont.dispose();
        if (backgroundBuffer != null) backgroundBuffer.dispose();
        if (blurShader != null) blurShader.dispose();
    }

    private void toggleTowerMenu() {
        if (showTowerMenu) {
            if (towerMenuWindow != null) towerMenuWindow.remove();
            towerMenuWindow = null;
            showTowerMenu = false;
            return;
        }

        // create menu window
        Window window = new Window("Choose Tower", uiSkin);
        window.defaults().pad(6);

        TextButton btnSimple = new TextButton("Simple (" + TowerType.SIMPLE.getCost() + ")", uiSkin);
        TextButton btnAOE = new TextButton("AOE (" + TowerType.AOE.getCost() + ")", uiSkin);
        TextButton btnCleave = new TextButton("Cleave (" + TowerType.RICOCHET.getCost() + ")", uiSkin);
        TextButton btnClose = new TextButton("Close", uiSkin);

        window.add(btnSimple).row();
        window.add(btnAOE).row();
        window.add(btnCleave).row();
        window.add(btnClose).row();
        window.pack();
        window.setPosition(uiStage.getViewport().getWorldWidth()/2f - window.getWidth()/2f, 60);

        btnSimple.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                previewTowerType = TowerType.SIMPLE;
                placingPreview = true;
                costLabel.setText("Cost: " + previewTowerType.getCost());
                toggleTowerMenu();
            }
        });
        btnSimple.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                costLabel.setText("Cost: " + TowerType.SIMPLE.getCost());
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (!placingPreview) costLabel.setText("");
            }
        });

        btnAOE.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                previewTowerType = TowerType.AOE;
                placingPreview = true;
                costLabel.setText("Cost: " + previewTowerType.getCost());
                toggleTowerMenu();
            }
        });
        btnAOE.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                costLabel.setText("Cost: " + TowerType.AOE.getCost());
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (!placingPreview) costLabel.setText("");
            }
        });

        btnCleave.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                previewTowerType = TowerType.RICOCHET;
                placingPreview = true;
                costLabel.setText("Cost: " + previewTowerType.getCost());
                toggleTowerMenu();
            }
        });
        btnCleave.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                costLabel.setText("Cost: " + TowerType.RICOCHET.getCost());
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (!placingPreview) costLabel.setText("");
            }
        });

        btnClose.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toggleTowerMenu();
            }
        });

        uiStage.addActor(window);
        towerMenuWindow = window;
        showTowerMenu = true;
    }

    private void renderMapObjectLayer(SpriteBatch batch) {
        if (map == null) return;

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // Parcourir tous les layers pour trouver les object layers
        for (com.badlogic.gdx.maps.MapLayer mapLayer : map.getLayers()) {
            // Vérifier si c'est un layer sans tuiles (object layer)
            if (!(mapLayer instanceof com.badlogic.gdx.maps.tiled.TiledMapTileLayer)) {
                // Accéder aux objets du layer
                com.badlogic.gdx.maps.MapObjects objects = mapLayer.getObjects();
                if (objects != null) {
                    for (com.badlogic.gdx.maps.MapObject obj : objects) {
                        // Vérifier s'il y a un GID (global tile id)
                        Object gidObj = obj.getProperties().get("gid");
                        if (gidObj != null) {
                            int gid = ((Number) gidObj).intValue();

                            // Chercher le tile avec ce gid dans les tilesets
                            com.badlogic.gdx.maps.tiled.TiledMapTile tile = map.getTileSets().getTile(gid);
                            if (tile != null && tile.getTextureRegion() != null) {
                                com.badlogic.gdx.graphics.g2d.TextureRegion region = tile.getTextureRegion();
                                float x = ((Number) obj.getProperties().get("x")).floatValue();
                                float y = ((Number) obj.getProperties().get("y")).floatValue();
                                batch.draw(region, x, y);
                            }
                        }
                    }
                }
            }
        }

        batch.end();
    }

    private void configureViewportForMap() {
        int mapWidth = map.getProperties().get("width", Integer.class);
        int mapHeight = map.getProperties().get("height", Integer.class);
        int tileWidth = map.getProperties().get("tilewidth", Integer.class);
        int tileHeight = map.getProperties().get("tileheight", Integer.class);

        mapPixelWidth = mapWidth * tileWidth;
        mapPixelHeight = mapHeight * tileHeight;

        camera = new OrthographicCamera();
        viewport = new FitViewport(mapPixelWidth, mapPixelHeight, camera);

        if (!Gdx.graphics.isFullscreen()) {
            int windowWidth = Math.max(DEFAULT_WINDOW_WIDTH, mapPixelWidth);
            int windowHeight = Math.max(DEFAULT_WINDOW_HEIGHT, mapPixelHeight);
            Gdx.graphics.setWindowedMode(windowWidth, windowHeight);
        }
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }

    private void setupBackgroundBlur() {
        if (mapPixelWidth <= 0 || mapPixelHeight <= 0) {
            return;
        }

        backgroundBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, mapPixelWidth, mapPixelHeight, false);
        backgroundBuffer.begin();
        Gdx.gl.glViewport(0, 0, mapPixelWidth, mapPixelHeight);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        OrthographicCamera bgCamera = new OrthographicCamera();
        bgCamera.setToOrtho(false, mapPixelWidth, mapPixelHeight);
        mapRenderer.setView(bgCamera);
        mapRenderer.render();
        backgroundBuffer.end();

        backgroundRegion = new TextureRegion(backgroundBuffer.getColorBufferTexture());
        backgroundRegion.flip(false, true);

        blurShader = new ShaderProgram(BLUR_VERTEX_SHADER, BLUR_FRAGMENT_SHADER);
        if (!blurShader.isCompiled()) {
            Gdx.app.log("SHADER", "Blur shader compile error: " + blurShader.getLog());
            blurShader = null;
        }

        screenCamera = new OrthographicCamera();
        screenCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    private void renderBackgroundBlur() {
        if (backgroundRegion == null || blurShader == null) {
            return;
        }

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        Gdx.gl.glViewport(0, 0, (int) screenWidth, (int) screenHeight);
        screenCamera.setToOrtho(false, screenWidth, screenHeight);
        screenCamera.update();

        batch.setProjectionMatrix(screenCamera.combined);
        batch.setShader(blurShader);
        batch.begin();
        blurShader.setUniformf(
            "u_texelSize",
            1f / backgroundRegion.getTexture().getWidth(),
            1f / backgroundRegion.getTexture().getHeight()
        );

        float scale = Math.max(screenWidth / mapPixelWidth, screenHeight / mapPixelHeight);
        float drawWidth = mapPixelWidth * scale;
        float drawHeight = mapPixelHeight * scale;
        float drawX = (screenWidth - drawWidth) / 2f;
        float drawY = (screenHeight - drawHeight) / 2f;
        batch.draw(backgroundRegion, drawX, drawY, drawWidth, drawHeight);
        batch.end();
        batch.setShader(null);
    }

    private Array<Rectangle> buildCollisionRects() {
        Array<Rectangle> rects = new Array<>();
        int tileWidth = map.getProperties().get("tilewidth", Integer.class);
        int tileHeight = map.getProperties().get("tileheight", Integer.class);

        for (MapLayer layer : map.getLayers()) {
            if (layer instanceof TiledMapTileLayer) {
                TiledMapTileLayer tileLayer = (TiledMapTileLayer) layer;
                for (int x = 0; x < tileLayer.getWidth(); x++) {
                    for (int y = 0; y < tileLayer.getHeight(); y++) {
                        TiledMapTileLayer.Cell cell = tileLayer.getCell(x, y);
                        if (cell == null || cell.getTile() == null) {
                            continue;
                        }
                        addTileCollisionRects(cell.getTile(), x * tileWidth, y * tileHeight, rects);
                    }
                }
            } else {
                MapObjects objects = layer.getObjects();
                if (objects == null) {
                    continue;
                }
                for (MapObject object : objects) {
                    addObjectCollisionRects(object, rects);
                }
            }
        }

        return rects;
    }

    private void addObjectCollisionRects(MapObject object, Array<Rectangle> rects) {
        Object gidObj = object.getProperties().get("gid");
        if (gidObj instanceof Number) {
            int gid = ((Number) gidObj).intValue();
            TiledMapTile tile = map.getTileSets().getTile(gid);
            if (tile != null && tile.getObjects() != null && tile.getObjects().getCount() > 0) {
                float objX = getFloatProperty(object, "x");
                float objY = getFloatProperty(object, "y");
                addTileCollisionRects(tile, objX, objY, rects);
            }
            return;
        }

        Rectangle bounds = getObjectBounds(object);
        if (bounds != null && bounds.width > 0f && bounds.height > 0f) {
            rects.add(new Rectangle(bounds));
        }
    }

    private void addTileCollisionRects(TiledMapTile tile, float baseX, float baseY, Array<Rectangle> rects) {
        MapObjects objects = tile.getObjects();
        if (objects == null || objects.getCount() == 0) {
            return;
        }
        for (MapObject object : objects) {
            Rectangle bounds = getObjectBounds(object);
            if (bounds == null || bounds.width <= 0f || bounds.height <= 0f) {
                continue;
            }
            rects.add(new Rectangle(baseX + bounds.x, baseY + bounds.y, bounds.width, bounds.height));
        }
    }

    private float getFloatProperty(MapObject object, String key) {
        Object value = object.getProperties().get(key);
        if (value instanceof Number) {
            return ((Number) value).floatValue();
        }
        return 0f;
    }

    private Rectangle getObjectBounds(MapObject object) {
        if (object instanceof RectangleMapObject) {
            return new Rectangle(((RectangleMapObject) object).getRectangle());
        }
        if (object instanceof PolygonMapObject) {
            return ((PolygonMapObject) object).getPolygon().getBoundingRectangle();
        }
        if (object instanceof PolylineMapObject) {
            return ((PolylineMapObject) object).getPolyline().getBoundingRectangle();
        }
        if (object instanceof EllipseMapObject) {
            com.badlogic.gdx.math.Ellipse ellipse = ((EllipseMapObject) object).getEllipse();
            return new Rectangle(ellipse.x, ellipse.y, ellipse.width, ellipse.height);
        }
        if (object instanceof CircleMapObject) {
            com.badlogic.gdx.math.Circle circle = ((CircleMapObject) object).getCircle();
            float size = circle.radius * 2f;
            return new Rectangle(circle.x - circle.radius, circle.y - circle.radius, size, size);
        }
        if (object instanceof TextureMapObject) {
            TextureMapObject textureObject = (TextureMapObject) object;
            float width = textureObject.getTextureRegion() != null ? textureObject.getTextureRegion().getRegionWidth() : 0f;
            float height = textureObject.getTextureRegion() != null ? textureObject.getTextureRegion().getRegionHeight() : 0f;
            if (width > 0f && height > 0f) {
                return new Rectangle(textureObject.getX(), textureObject.getY(), width, height);
            }
        }
        return null;
    }

    private void togglePause() {
        paused = !paused;
        if (paused) {
            if (placingPreview) {
                placingPreview = false;
                previewTowerType = null;
                costLabel.setText("");
            }
            if (showTowerMenu) {
                toggleTowerMenu();
            }
            showPauseWindow();
            setGameplayUiVisible(false);
        } else {
            hidePauseWindows();
            setGameplayUiVisible(true);
        }
    }

    private void showPauseWindow() {
        if (pauseWindow != null) {
            pauseWindow.setVisible(true);
            return;
        }

        pauseWindow = new Window(PAUSE_TITLE, uiSkin);
        pauseWindow.defaults().pad(6);

        TextButton resumeButton = new TextButton("Reprendre", uiSkin);
        TextButton optionsButton = new TextButton(OPTIONS_TITLE, uiSkin);

        resumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                togglePause();
            }
        });

        optionsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                togglePauseOptionsWindow();
            }
        });

        pauseWindow.add(resumeButton).width(200).row();
        pauseWindow.add(optionsButton).width(200).row();
        pauseWindow.pack();
        centerWindow(pauseWindow);
        uiStage.addActor(pauseWindow);
    }

    private void hidePauseWindows() {
        if (pauseOptionsWindow != null) {
            pauseOptionsWindow.remove();
            pauseOptionsWindow = null;
        }
        if (pauseWindow != null) {
            pauseWindow.remove();
            pauseWindow = null;
        }
    }

    private void togglePauseOptionsWindow() {
        if (pauseOptionsWindow != null) {
            pauseOptionsWindow.remove();
            pauseOptionsWindow = null;
            return;
        }

        pauseOptionsWindow = new Window(OPTIONS_TITLE, uiSkin);
        pauseOptionsWindow.defaults().pad(6);

        Label volumeLabel = new Label("Volume", uiSkin);
        Slider volumeSlider = new Slider(
            MusicManager.MIN_VOLUME_DB,
            MusicManager.MAX_VOLUME_DB,
            1f,
            false,
            uiSkin
        );
        if (game.getMusicManager() != null) {
            volumeSlider.setValue(game.getMusicManager().getVolumeDb());
        }

        volumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (game.getMusicManager() != null) {
                    game.getMusicManager().setVolumeDb(volumeSlider.getValue());
                }
            }
        });

        TextButton closeButton = new TextButton("Fermer", uiSkin);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                togglePauseOptionsWindow();
            }
        });

        pauseOptionsWindow.add(volumeLabel);
        pauseOptionsWindow.add(volumeSlider).width(220);
        pauseOptionsWindow.row();
        pauseOptionsWindow.add(closeButton).colspan(2).padTop(6);
        pauseOptionsWindow.pack();
        centerWindow(pauseOptionsWindow);
        uiStage.addActor(pauseOptionsWindow);
    }

    private void setGameplayUiVisible(boolean visible) {
        if (hudTable != null) hudTable.setVisible(visible);
        if (towerButtonTable != null) towerButtonTable.setVisible(visible);
        if (bottomRightTable != null) bottomRightTable.setVisible(visible);
        if (nextWaveButton != null) nextWaveButton.setDisabled(!visible);
        if (towersButton != null) towersButton.setDisabled(!visible);
    }

    private void centerWindow(Window window) {
        if (window == null || uiStage == null) {
            return;
        }
        window.setPosition(
            uiStage.getViewport().getWorldWidth() / 2f - window.getWidth() / 2f,
            uiStage.getViewport().getWorldHeight() / 2f - window.getHeight() / 2f
        );
    }
}
