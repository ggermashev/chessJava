package sample;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.Light;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Text;
import javafx.stage.Stage;


import java.awt.*;

import java.io.File;
import java.io.FileInputStream;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;


public class Main extends Application {

    void mousePressed(ImageView iv, Coord from)
    {
        //Bounds b = iv.getBoundsInLocal();
        //Bounds b = iv.getScene().getY();
        //Point2D p = new Point2D(b.getWidth()/2,b.getHeight()/2);
        from.posy = (int)iv.getScene().getY();
        from.posx = (int)iv.getScene().getX();
    }

    class Iv
    {
        ImageView image;
        int coordy;
        int coordx;
        int indy;
        int indx;
        boolean dead;

        Iv(Image img, int cy, int cx, int iy, int ix)
        {
            image = new ImageView(img);
            coordy = cy;
            coordx = cx;
            indy = iy;
            indx = ix;
            dead = false;
        }

        void setY(int y)
        {
            coordy = y;
        }

        void setX(int x)
        {
            coordx = x;
        }

        void setI(int i)
        {
            indy = i;
        }

        void setJ(int j)
        {
            indx = j;
        }

        void killed()
        {
            dead = true;
        }

    }


    boolean botTurn = true;

    Iv iv[][];

    Coord from = null;
    Coord to = null;

    //ImageView image = null;
    Iv imageFrom = null;
    Rectangle rect[][];

    Figures fig;

    void botMove(Stage primaryStage)
    {
        Atributes atr = new Atributes(-9000, null, null);
        atr = fig.getBestMove(0, null, null, 0, null, fig.field, fig.player.figs, fig.bot.figs); //два последних наоборот
        Coord move[] = new Coord[2];
        move[0] = atr.from;
        move[1] = atr.to;
        //System.out.println(" from " + move[0].posy + " " + move[0].posx + " to " + move[1].posy + " " + move[1].posx + " \n");
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 16; j++) {
                if (iv[i][j].indy == move[0].posy && iv[i][j].indx == move[0].posx && !iv[i][j].dead) {
                    Coord to = move[1];

                    Base tomove = Gamer.findFigFromPosition(move[0], fig.player.figs);
                    Base dead = Gamer.findFigFromPosition(move[1], fig.bot.figs);

                    if (dead != null) {
                        dead.dead = true;
                        for (int y = 0; y < 2; y++) {
                            for (int x = 0; x < 16; x++) {
                                if ( (iv[y][x].indy == to.posy) && iv[y][x].indx == to.posx && !iv[y][x].dead)  {
                                    iv[y][x].killed();
                                    iv[y][x].image.setVisible(false);
                                }
                            }
                        }
                    }
                    tomove.go(move[1]);
                    tomove.refreshMays();
                    fig.setField(tomove.field.field);
                    fig.printField();

                    iv[i][j].image.setY(100 + 100 * (to.posy) - 15);
                    iv[i][j].image.setX(100 * (to.posx) + 40);
                    iv[i][j].setY(100 * (to.posy) + 100);
                    iv[i][j].setX(100 * (to.posx) + 50);
                    iv[i][j].setI(iv[i][j].coordy / 100 - 1);
                    iv[i][j].setJ((iv[i][j].coordx - 50) / 100);
                    System.out.println(iv[i][j].indy + " " + iv[i][j].indx);

                    for (int y = 0; y < 2; y++)
                    {
                        for (int x = 0; x < 16; x++)
                        {
                            iv[y][x].image.setMouseTransparent(false);
                        }
                    }

                    primaryStage.show();
                    playerMove(primaryStage);
                    return;
                }
            }
        }
    }

    void playerMove(final Stage primaryStage)
    {
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 16; j++) {
                iv[i][j].image.onMouseClickedProperty().set(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        if (from == null) {
                            from = new Coord(100 * ((int) mouseEvent.getY() / 100), 100 * ((int) mouseEvent.getX() / 100) + 50);
                            for (int i = 0; i < 2; i++) {
                                for (int j = 0; j < 16; j++) {
                                    if (iv[i][j].coordy == from.posy && iv[i][j].coordx == from.posx && !iv[i][j].dead) {
                                        imageFrom = iv[i][j];
                                        if (!imageFrom.dead)
                                        {

                                            for (int y = 0; y < 2; y++)
                                            {
                                                for (int x = 0; x < 16; x++)
                                                {
                                                    iv[y][x].image.setMouseTransparent(true);
                                                }
                                            }


                                        }
                                        if (imageFrom.dead) {
                                            from = null;
                                            imageFrom = null;
                                        }
                                    }
                                }
                            }
                            //System.out.println(from.posy + " " + from.posx);
                        }
                    }
                });
            }
        }
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                rect[i][j].onMouseClickedProperty().set(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        if (from != null) {
                            to = new Coord(100 * ((int) mouseEvent.getY() / 100), 100 * ((int) mouseEvent.getX() / 100));
                            Coord moveTo = new Coord(to.posy/100 - 1, to.posx/100);
                            Base tomove = Gamer.findFigFromPosition(new Coord(imageFrom.indy, imageFrom.indx), fig.bot.figs);
                            Base dead = Gamer.findFigFromPosition(moveTo, fig.player.figs);

                            if (dead != null) {
                                dead.dead = true;
                                System.out.println(dead.pos.posy + " " + dead.pos.posx + " : " + dead.dead);
                                for (int y = 0; y < 2; y++) {
                                    for (int x = 0; x < 16; x++) {
                                        if (iv[y][x].indy == moveTo.posy && iv[y][x].indx == moveTo.posx && !iv[y][x].dead) {
                                            iv[y][x].killed();
                                            iv[y][x].image.setVisible(false);
                                        }
                                    }
                                }
                            }

                            for (int y = 0; y < 2; y++) {
                                for (int x = 0; x < 16; x++) {
                                    if (iv[y][x].coordy == to.posy && iv[y][x].coordx == to.posx && !iv[y][x].dead) {
                                        iv[y][x].killed();
                                    }
                                }
                            }

                            tomove.go(moveTo);
                            tomove.refreshMays();
                            fig.setField(tomove.field.field);
                            fig.printField();

                            imageFrom.image.setY(100 * (to.posy / 100) - 15);
                            imageFrom.image.setX(100 * (to.posx / 100) + 40);
                            imageFrom.setY(100 * (to.posy / 100));
                            imageFrom.setX(100 * (to.posx / 100) + 50);
                            imageFrom.setI(imageFrom.coordy / 100 - 1);
                            imageFrom.setJ((imageFrom.coordx - 50) / 100);
                            System.out.println(imageFrom.indy + " " + imageFrom.indx);
                            from = null;
                            imageFrom = null;
                            primaryStage.show();

                            botMove(primaryStage);
                            return;
                        }
                    }
                });
            }
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hard Chess");

        iv = new Iv[2][16];
        //ImageView iv[][] = new ImageView[2][16];
        File peshkaw = new File("C:\\Users\\ggerm\\IdeaProjects\\chessfront\\src\\images\\peshkaw.jpg");
        File peshkab = new File("C:\\Users\\ggerm\\IdeaProjects\\chessfront\\src\\images\\peshkab.png");
        File towerw = new File("C:\\Users\\ggerm\\IdeaProjects\\chessfront\\src\\images\\towerw.png");
        File towerb = new File("C:\\Users\\ggerm\\IdeaProjects\\chessfront\\src\\images\\towerb.png");
        File horsew = new File("C:\\Users\\ggerm\\IdeaProjects\\chessfront\\src\\images\\horsew.jpg");
        File horseb = new File("C:\\Users\\ggerm\\IdeaProjects\\chessfront\\src\\images\\horseb.jpg");
        File slonw = new File("C:\\Users\\ggerm\\IdeaProjects\\chessfront\\src\\images\\slonw.jpg");
        File slonb = new File("C:\\Users\\ggerm\\IdeaProjects\\chessfront\\src\\images\\slonb.png");
        File ferzw = new File("C:\\Users\\ggerm\\IdeaProjects\\chessfront\\src\\images\\ferzw.jpg");
        File ferzb = new File("C:\\Users\\ggerm\\IdeaProjects\\chessfront\\src\\images\\ferzb.jpeg");
        File kingw = new File("C:\\Users\\ggerm\\IdeaProjects\\chessfront\\src\\images\\kingw.png");
        File kingb = new File("C:\\Users\\ggerm\\IdeaProjects\\chessfront\\src\\images\\kingb.jpg");
        for (int i = 0; i < 16; i++) {

            if (i < 8) {
                iv[0][i] = new Iv(new Image(peshkaw.toURI().toURL().toString()), 200, 50 + 100 * i, 1, i);
                iv[0][i].image.setY(200);
                iv[0][i].image.setX(50 + 100 * i);
                iv[1][i] = new Iv((new Image(peshkab.toURI().toURL().toString())), 700, 50 + 100 * i, 6, i);
                iv[1][i].image.setY(700);
                iv[1][i].image.setX(50 + 100 * i);
            }
            if (i == 8) {
                iv[0][i] = new Iv((new Image(towerw.toURI().toURL().toString())), 100, 50, 0, 0);
                iv[0][i].image.setY(100);
                iv[0][i].image.setX(50);
                iv[1][i] = new Iv((new Image(towerb.toURI().toURL().toString())), 800, 50, 7, 0);
                iv[1][i].image.setY(800);
                iv[1][i].image.setX(50);
            }
            if (i == 9) {
                iv[0][i] = new Iv((new Image(towerw.toURI().toURL().toString())), 100, 750, 0, 7);
                iv[0][i].image.setY(100);
                iv[0][i].image.setX(750);
                iv[1][i] = new Iv((new Image(towerb.toURI().toURL().toString())), 800, 750, 7, 7);
                iv[1][i].image.setY(800);
                iv[1][i].image.setX(750);
            }
            if (i == 10) {
                iv[0][i] = new Iv((new Image(horsew.toURI().toURL().toString())), 100, 150, 0, 1);
                iv[0][i].image.setY(100);
                iv[0][i].image.setX(150);
                iv[1][i] = new Iv((new Image(horseb.toURI().toURL().toString())), 800, 150, 7, 1);
                iv[1][i].image.setY(800);
                iv[1][i].image.setX(150);
            }
            if (i == 11) {
                iv[0][i] = new Iv((new Image(horsew.toURI().toURL().toString())), 100, 650, 0, 6);
                iv[0][i].image.setY(100);
                iv[0][i].image.setX(650);
                iv[1][i] = new Iv((new Image(horseb.toURI().toURL().toString())), 800, 650, 7, 6);
                iv[1][i].image.setY(800);
                iv[1][i].image.setX(650);
            }
            if (i == 12) {
                iv[0][i] = new Iv((new Image(slonw.toURI().toURL().toString())), 100, 250, 0, 2);
                iv[0][i].image.setY(100);
                iv[0][i].image.setX(250);
                iv[1][i] = new Iv((new Image(slonb.toURI().toURL().toString())), 800, 250, 7, 2);
                iv[1][i].image.setY(800);
                iv[1][i].image.setX(250);
            }
            if (i == 13) {
                iv[0][i] = new Iv((new Image(slonw.toURI().toURL().toString())), 100, 550, 0, 5);
                iv[0][i].image.setY(100);
                iv[0][i].image.setX(550);
                iv[1][i] = new Iv((new Image(slonb.toURI().toURL().toString())), 800, 550, 7, 5);
                iv[1][i].image.setY(800);
                iv[1][i].image.setX(550);
            }
            if (i == 14) {
                iv[0][i] = new Iv((new Image(ferzw.toURI().toURL().toString())), 100, 350, 0, 3);
                iv[0][i].image.setY(100);
                iv[0][i].image.setX(350);
                iv[1][i] = new Iv((new Image(ferzb.toURI().toURL().toString())), 800, 350, 7, 3);
                iv[1][i].image.setY(800);
                iv[1][i].image.setX(350);
            }
            if (i == 15) {
                iv[0][i] = new Iv((new Image(kingw.toURI().toURL().toString())), 100, 450, 0, 4);
                iv[0][i].image.setY(100);
                iv[0][i].image.setX(450);
                iv[1][i] = new Iv((new Image(kingb.toURI().toURL().toString())), 800, 450, 7, 4);
                iv[1][i].image.setY(800);
                iv[1][i].image.setX(450);
            }
        }

        Group group = new Group();

        rect = new Rectangle[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                rect[i][j] = new Rectangle(0, 0, 100, 100);
                rect[i][j].setY(75 + 100 * i);
                rect[i][j].setX(25 + 100 * j);
                if ((i + j) % 2 == 1)
                    rect[i][j].setFill(Color.WHEAT);
                else
                    rect[i][j].setFill(Color.BLACK);
                group.getChildren().add(rect[i][j]);
            }
        }

        fig = new Figures();


        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 16; j++) {
                group.getChildren().add(iv[i][j].image);
            }
        }

        boolean game = true;



        if (!botTurn) {
            //System.out.println("**\n");
            playerMove(primaryStage);
            //botTurn = !botTurn;
        }
        if (botTurn) {
            //System.out.println("*\n");
            botMove(primaryStage);
            //botTurn = !botTurn;
        }


        Scene scene = new Scene(group, 1000, 1000);
        primaryStage.setScene(scene);
        primaryStage.show();

    }



    public static void main(String[] args) {
        launch(args);
    }
}
