package com.brackeen.javagamebook.tilegame;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.*;
import java.util.ArrayList;
import javax.swing.ImageIcon;

import com.brackeen.javagamebook.graphics.*;
import com.brackeen.javagamebook.tilegame.sprites.*;


/**
    The ResourceManager class loads and manages tile Images and
    "host" Sprites used in the game. Game Sprites are cloned from
    "host" Sprites.
*/
public class ResourceManager {

    private ArrayList tiles;
    private int currentMap;
    private GraphicsConfiguration gc;

    // host sprites used for cloning
    private Sprite playerSprite;
    private Sprite musicSprite;
    private Sprite coinSprite;
    private Sprite goalSprite;
    private Sprite grubSprite;
    private Sprite flySprite;
    private Sprite Mugre3Sprite;

    /**
        Creates a new ResourceManager with the specified
        GraphicsConfiguration.
    */
    public ResourceManager(GraphicsConfiguration gc) {
        this.gc = gc;
        loadTileImages();
        loadCreatureSprites();
        loadPowerUpSprites();
    }


    /**
        Gets an image from the images/ directory.
    */
    public Image loadImage(String name) {
        String filename = "images/" + name;
        return new ImageIcon(filename).getImage();
    }


    public Image getMirrorImage(Image image) {
        return getScaledImage(image, -1, 1);
    }


    public Image getFlippedImage(Image image) {
        return getScaledImage(image, 1, -1);
    }


    private Image getScaledImage(Image image, float x, float y) {

        // set up the transform
        AffineTransform transform = new AffineTransform();
        transform.scale(x, y);
        transform.translate(
            (x-1) * image.getWidth(null) / 2,
            (y-1) * image.getHeight(null) / 2);

        // create a transparent (not translucent) image
        Image newImage = gc.createCompatibleImage(
            image.getWidth(null),
            image.getHeight(null),
            Transparency.BITMASK);

        // draw the transformed image
        Graphics2D g = (Graphics2D)newImage.getGraphics();
        g.drawImage(image, transform, null);
        g.dispose();

        return newImage;
    }


    public TileMap loadNextMap() {
        TileMap map = null;
        while (map == null) {
            currentMap ++ ;
            try {
                map = loadMap(
                    "maps/map" + currentMap + ".txt");
            }
            catch (IOException ex) {
                if (currentMap == 1) {
                    // no maps to load!
                    return null;
                }
                currentMap = 0;
                map = null;
            }
        }

        return map;
    }


    public TileMap reloadMap() {
        try {
            return loadMap(
                "maps/map" + currentMap + ".txt");
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }


    private TileMap loadMap(String filename)
        throws IOException
    {
        ArrayList lines = new ArrayList();
        int width = 0;
        int height = 0;

        // read every line in the text file into the list
        BufferedReader reader = new BufferedReader(
            new FileReader(filename));
        while (true) {
            String line = reader.readLine();
            // no more lines to read
            if (line == null) {
                reader.close();
                break;
            }

            // add every line except for comments
            if (!line.startsWith("#")) {
                lines.add(line);
                width = Math.max(width, line.length());
            }
        }

        // parse the lines to create a TileEngine
        height = lines.size();
        TileMap newMap = new TileMap(width, height);
        for (int y=0; y<height; y++) {
            String line = (String)lines.get(y);
            for (int x=0; x<line.length(); x++) {
                char ch = line.charAt(x);

                // check if the char represents tile A, B, C etc.
                int tile = ch - 'A';
                if (tile >= 0 && tile < tiles.size()) {
                    newMap.setTile(x, y, (Image)tiles.get(tile));
                }

                // check if the char represents a sprite
                else if (ch == 'o') {
                    addSprite(newMap, coinSprite, x, y);
                }
                else if (ch == '!') {
                    addSprite(newMap, musicSprite, x, y);
                }
                else if (ch == '*') {
                    addSprite(newMap, goalSprite, x, y);
                }
                else if (ch == '1') {
                    addSprite(newMap, grubSprite, x, y);
                }
                else if (ch == '2') {
                    addSprite(newMap, flySprite, x, y);
                }
                else if (ch == '3') {
                    addSprite(newMap, Mugre3Sprite, x, y);
                }
            }
        }

        // add the player to the map
        Sprite player = (Sprite)playerSprite.clone();
        player.setX(TileMapRenderer.tilesToPixels(3));
        player.setY(0);
        newMap.setPlayer(player);

        return newMap;
    }


    private void addSprite(TileMap map,
        Sprite hostSprite, int tileX, int tileY)
    {
        if (hostSprite != null) {
            // clone the sprite from the "host"
            Sprite sprite = (Sprite)hostSprite.clone();

            // center the sprite
            sprite.setX(
                TileMapRenderer.tilesToPixels(tileX) +
                (TileMapRenderer.tilesToPixels(1) -
                sprite.getWidth()) / 2);

            // bottom-justify the sprite
            sprite.setY(
                TileMapRenderer.tilesToPixels(tileY + 1) -
                sprite.getHeight());

            // add it to the map
            map.addSprite(sprite);
        }
    }


    // -----------------------------------------------------------
    // code for loading sprites and images
    // -----------------------------------------------------------


    public void loadTileImages() {
        // keep looking for tile A,B,C, etc. this makes it
        // easy to drop new tiles in the images/ directory
        tiles = new ArrayList();
        char ch = 'A';
        while (true) {
            String name = "tile_" + ch + ".png";
            File file = new File("images/" + name);
            if (!file.exists()) {
                break;
            }
            tiles.add(loadImage(name));
            ch++;
        }
    }


    public void loadCreatureSprites() {

        Image[][] images = new Image[6][];

        // load left-facing images
        images[0] = new Image[] {
            loadImage("player1.png"),
            loadImage("player2.png"),
            loadImage("player3.png"),
            loadImage("player4.png"),
            loadImage("Mugre1_1.png"),
            loadImage("Mugre1_2.png"),
            loadImage("Mugre1_3.png"),
            loadImage("Mugre1_4.png"),
            loadImage("Mugre2_1.png"),
            loadImage("Mugre2_2.png"),
            loadImage("Mugre2_3.png"),
            loadImage("Mugre2_4.png"),
            loadImage("Mugre3_1.png"),
            loadImage("Mugre3_2.png"),
            loadImage("Mugre3_3.png"),
            loadImage("Mugre3_4.png"),
        };

        images[1] = new Image[images[0].length];
        images[2] = new Image[images[0].length];
        images[3] = new Image[images[0].length];
        images[4] = new Image[images[0].length];
        images[5] = new Image[images[0].length];
        for (int i=0; i<images[0].length; i++) {
            // right-facing images
            images[1][i] = getMirrorImage(images[0][i]);
            // left-facing "dead" images
            images[2][i] = getFlippedImage(images[0][i]);
            // right-facing "dead" images
            images[3][i] = getFlippedImage(images[1][i]);
            
           // images[4][i] = getFlippedImage(images[1][i]);
        }

        // create creature animations
        Animation[] playerAnim = new Animation[6];
        Animation[] Mugre1Anim = new Animation[5];
        Animation[] Mugre2Anim = new Animation[5];
        Animation[] Mugre3Anim = new Animation[5];
        for (int i=0; i<5; i++) {
            playerAnim[i] = createPlayerAnim(
                images[i][0], images[i][1], images[i][2],images[i][3]);
            Mugre1Anim[i] = createMugre1Anim(
                images[i][4], images[i][5], images[i][6],images[i][7]);
            Mugre2Anim[i] = createMugre2Anim(
                images[i][8], images[i][9], images[i][10], images[i][11]);
            Mugre3Anim[i] = createMugre3Anim(
                images[i][12], images[i][13], images[i][14], images[i][15]);
        }

        // create creature sprites
        playerSprite = new Player(playerAnim[0], playerAnim[1],
            playerAnim[2], playerAnim[3]);
        flySprite = new Mugre1(Mugre1Anim[0], Mugre1Anim[1],
            Mugre1Anim[2], Mugre1Anim[3]);
        grubSprite = new Mugre2(Mugre2Anim[0], Mugre2Anim[1],
            Mugre2Anim[2], Mugre2Anim[3]);
        Mugre3Sprite = new Mugre3(Mugre3Anim[0], Mugre3Anim[1],
            Mugre3Anim[2], Mugre3Anim[3]);
    }


    private Animation createPlayerAnim(Image player1,
        Image player2, Image player3, Image player4)
    {
        Animation anim = new Animation();
        anim.addFrame(player1, 150);
        anim.addFrame(player2, 150);
        anim.addFrame(player3, 150);
        anim.addFrame(player4, 150);
        
        return anim;
    }


    private Animation createMugre1Anim(Image img1, Image img2,//Se cargan las animaciones de las mugres (enemigos)
        Image img3, Image img4)
    {
        Animation anim = new Animation();
        anim.addFrame(img1, 100);
        anim.addFrame(img2, 100);
        anim.addFrame(img3, 100);
        anim.addFrame(img4, 100);
        return anim;
    }


    private Animation createMugre2Anim(Image img1, Image img2,Image img3, Image img4) {//Se cargan las animaciones de las mugres (enemigos)
        Animation anim = new Animation();
        anim.addFrame(img1, 250);
        anim.addFrame(img2, 250);
        anim.addFrame(img3, 250);
        anim.addFrame(img4, 250);
        return anim;
    }
    
     private Animation createMugre3Anim(Image img1, Image img2,Image img3, Image img4) {//Se cargan las animaciones de las mugres (enemigos)
        Animation anim = new Animation();
        anim.addFrame(img1, 150);
        anim.addFrame(img2, 150);
        anim.addFrame(img3, 150);
        anim.addFrame(img4, 150);
        return anim;
    }


    private void loadPowerUpSprites() {//Se cargan las animaciones de los objetos a agarrar
        // create "goal" sprite
        Animation anim = new Animation();
        anim.addFrame(loadImage("burbuja1.png"), 150);
        anim.addFrame(loadImage("burbuja2.png"), 150);
        anim.addFrame(loadImage("burbuja3.png"), 150);
        anim.addFrame(loadImage("burbuja2.png"), 150);
        goalSprite = new PowerUp.Goal(anim);

        // create "star" sprite
        anim = new Animation();
        anim.addFrame(loadImage("gota1.png"), 100);
        anim.addFrame(loadImage("gota2.png"), 100);
        anim.addFrame(loadImage("gota3.png"), 100);
        anim.addFrame(loadImage("gota4.png"), 100);
        coinSprite = new PowerUp.Gota(anim);

        // create "music" sprite
        anim = new Animation();
        anim.addFrame(loadImage("jabon1.png"), 150);
        anim.addFrame(loadImage("jabon2.png"), 150);
        anim.addFrame(loadImage("jabon3.png"), 150);
        anim.addFrame(loadImage("jabon4.png"), 150);
        musicSprite = new PowerUp.Music(anim);
    }

}
