import javafx.application.*;
import javafx.scene.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.*;
import javafx.animation.*;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.input.KeyEvent;

import java.net.*;


public class GamePackmanv2 extends Application implements EventHandler<ActionEvent> {
   private Stage stage;
   private Scene scene, gameScene;
   private StackPane root;

   private static String[] args;

   ArrayList<String> animateFrames = new ArrayList<String>();

   private final static String ICON_IMAGE1 = "Player2.gif"; 
   private final static String ICON_IMAGE2 = "Player3.gif"; 
   private final static String ICON_IMAGE3 = "Player4.gif"; 

   private final static String ICON_IMAGE = "ManCharacter.gif"; 
   private final static String ICON_IMAGE_RUNNERS = "plenkyattack.gif"; 
   private final static String EAT_BALLS = "NOM.gif"; 
   private final static String START = "EndScreen.gif"; 
   private final static String MONEY = "money.gif"; 

   private AnimationTimer timer; 

   private StaticBackgroud staticBack = null;

   private ArrayList<PacmanRacer> allRacers = new ArrayList<>();
   private ArrayList<PlenkyHunter> allHunters = new ArrayList<>();
   private ArrayList<Balls> allBalls = new ArrayList<>();
   private ArrayList<Mito> allMito = new ArrayList<>();

   boolean moveUp;
   boolean moveLeft;
   boolean moveRight;
   boolean moveDown;


   private int readjustXmid;
   private int readjustXleft;
   private int readjustXright;

   private int readjustYmid;
   private int readjustYleft;
   private int readjustYright;


   private int y2;
   private int x2;

   private int fcounter = 0;
   private int hCount = 4;


   ObjectOutputStream oos = null;

   int stop = 1;

   Image backgroundCollision = null;
   Image ballsCollision = null;

   Socket socket = new Socket();
   public static final int PORT = 12345;
   private String ipAdress = "localhost";

   public static void main(String[] _args) {
      args = _args;
      launch(args);
   }

   public void start(Stage _stage) {

      stage = _stage;
      Button playButton = new Button("CONNECT");
      playButton.setStyle(
            "-fx-text-fill: white; -fx-background-color: transparent; -fx-font-size: 30px; -fx-underline: false; -fx-font-family: Impact;");

      playButton.setOnMouseEntered(e -> playButton.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: gray; -fx-font-size: 30px; -fx-underline: true; -fx-font-family: Impact;"));
      playButton.setOnMouseExited(e -> playButton.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 30px; -fx-underline: false; -fx-font-family: Impact;"));

      playButton.setOnAction(this);

      FlowPane fp1 = new FlowPane();

      fp1.getChildren().add(playButton);

      Button quitButton = new Button("QUIT");
      quitButton.setStyle(
            "-fx-text-fill: white; -fx-background-color: transparent; -fx-font-size: 30px; -fx-underline: false; -fx-font-family: Impact;");

      quitButton.setOnMouseEntered(e -> quitButton.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: gray; -fx-font-size: 30px; -fx-underline: true; -fx-font-family: Impact;"));
      quitButton.setOnMouseExited(e -> quitButton.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 30px; -fx-underline: false; -fx-font-family: Impact;"));

      quitButton.setOnAction(this);

      FlowPane fp2 = new FlowPane();

      fp1.setAlignment(Pos.BASELINE_CENTER);
      fp2.setAlignment(Pos.BASELINE_CENTER);

      fp2.getChildren().add(quitButton);

      VBox layout = new VBox(10);
      layout.getChildren().addAll(fp1, fp2);
      layout.setAlignment(Pos.BOTTOM_CENTER);
      gameScene = new Scene(layout, 640, 320);

      Image img = new Image(START);

      BackgroundImage bI = new BackgroundImage(img, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER,
            new BackgroundSize(640, BackgroundSize.AUTO, true, true, false, true));

      layout.setBackground(new Background(bI));

      stage.setScene(gameScene);
      stage.setTitle("Pac-Man Game");
      stage.show();
   }


   public void startGame() {

      stage.setTitle("Game2D Starter");
      stage.setOnCloseRequest(
            new EventHandler<WindowEvent>() {
               public void handle(WindowEvent evt) {
                  System.exit(0);
               }
            });


      root = new StackPane();


      initializeScene();
   }


   public void initializeScene() {


      animateFrames.add(ICON_IMAGE);
      animateFrames.add(ICON_IMAGE1);
      animateFrames.add(ICON_IMAGE2);
      animateFrames.add(ICON_IMAGE3);


      Button bInfo = new Button("");
      bInfo.setStyle(
            "-fx-text-fill: white; -fx-background-color: transparent; -fx-font-size: 30px; -fx-underline: false; -fx-font-family: Impact;");


      for (int i = 0; i < 200; i++) {
         Balls b = new Balls(randomX(), randomY());
         allBalls.add(b);
      }

      PacmanRacer mainRacer = new PacmanRacer();
      allRacers.add(mainRacer);

      Mito m = new Mito();
      allMito.add(m);


      for (int i = 0; i < 4; i++) {
         PlenkyHunter pH = new PlenkyHunter();
         allHunters.add(pH);
      }


      staticBack = new StaticBackgroud();

      root.getChildren().add(staticBack);


      backgroundCollision = new Image("mape.gif");


      ballsCollision = new Image("ManCharacter.gif");


      for (int i = 0; i < allBalls.size(); i++) {
         root.getChildren().add(allBalls.get(i));

      }


      for (int i = 0; i < allRacers.size(); i++) {
         root.getChildren().add(allRacers.get(i));

      }

      for (int i = 0; i < allHunters.size(); i++) {
         root.getChildren().add(allHunters.get(i));

      }
      for (int i = 0; i < allMito.size(); i++) {
         root.getChildren().add(allMito.get(i));
      }

      root.getChildren().add(bInfo);
      bInfo.setTranslateX(150);
      bInfo.setTranslateY(0);


      timer = new AnimationTimer() {
         public void handle(long now) {
            if (fcounter % 5 == 0) {
               String nextFrame = animateFrames.get(fcounter % animateFrames.size());
               allRacers.get(0).setFrame(nextFrame);
            }
            if (allMito.get(0).update()) {
               root.getChildren().remove(allMito.get(0));
               root.getChildren().remove(allHunters.get(3));
               hCount = 3;
               bInfo.setText("Plenky bribed");
            }
            for (int i = 0; i < allBalls.size(); i++) {

               if (allBalls.get(i).update()) {
                  root.getChildren().remove(allBalls.get(i));
                  allBalls.remove(i);
               }
               if (allBalls.get(i).touchBlackPP()) {
                  root.getChildren().remove(allBalls.get(i));
                  allBalls.remove(i);
               }
            }
            for (int i = 0; i < allRacers.size(); i++) {
               try {
                  oos.writeObject("POS");
                  oos.writeObject(allRacers.get(i).getX());
                  oos.writeObject(allRacers.get(i).getY());
                  oos.flush();
                  allRacers.get(i).update();

               } catch (IOException e) {
                  e.printStackTrace();
               }

            }

            for (int i = 0; i < hCount; i++) {
               allHunters.get(i).update();
               if (allHunters.get(i).mitoIkorupcija()) {
                   this.stop();
               Platform.runLater(() -> {
                  alert(AlertType.WARNING, "You have been corupted and USKOK raded your house ", "YOU WERE COUGHT");
                  stage.close();
               });
               }
            }
            if (allBalls.size() == 0) {
               this.stop();
               Platform.runLater(() -> {
                  alert(AlertType.INFORMATION, "Congradulations you have corupted the corupted ", "YOU WON");
                  stage.close();
               });

            }
            fcounter++;

         }

      };
      timer.start();

      scene = new Scene(root, 640, 320);
      stage.setScene(scene);
      stage.show();

      scene.setOnKeyPressed(
            new EventHandler<KeyEvent>() {
               @Override
               public void handle(KeyEvent event) {
                  switch (event.getCode()) {
                     case UP:

                        moveRight = false;
                        moveLeft = false;
                        moveDown = false;

                        readjustYmid = 0;
                        readjustXmid = 15;

                        readjustYleft = 0;
                        readjustXleft = 0;

                        readjustYright = 0;
                        readjustXright = 31;

                        moveUp = true;
                        break;
                     case DOWN:
                        moveRight = false;
                        moveLeft = false;
                        moveUp = false;

                        readjustYmid = 31;
                        readjustXmid = 15;

                        readjustYleft = 31;
                        readjustXleft = 0;

                        readjustYright = 31;
                        readjustXright = 31;

                        moveDown = true;
                        break;
                     case LEFT:
                        moveRight = false;
                        moveUp = false;
                        moveDown = false;

                        readjustXmid = 0;
                        readjustYmid = 15;

                        readjustYleft = 31;
                        readjustXleft = 0;

                        readjustYright = 0;
                        readjustXright = 0;

                        moveLeft = true;
                        break;
                     case RIGHT:
                        moveUp = false;
                        moveLeft = false;
                        moveDown = false;

                        readjustXmid = 31;
                        readjustYmid = 15;

                        readjustYleft = 0;
                        readjustXleft = 31;

                        readjustYright = 31;
                        readjustXright = 31;

                        moveRight = true;
                        break;
                     case SHIFT:
                        break;
                  }
               }
            });

   }

   private class StaticBackgroud extends Pane {
      private ImageView aPicView;

      public StaticBackgroud() {
         this.aPicView = new ImageView("mape.gif");
         this.getChildren().add(this.aPicView);
      }

   }

   public int randomX() {
      int min = 21;
      int max = 600;
      Random rand = new Random();
      int x = rand.nextInt(max - min) + min;
      return x;
   }

   public int randomY() {
      int min = 21;
      int max = 270;
      Random rand = new Random();
      int y = rand.nextInt(max - min) + min;

      return y;
   }

   private class Balls extends Pane {
      private ImageView aPicView;
      private int x;
      private int y;

      public Balls(int x, int y) {
         this.x = x;
         this.y = y;
         this.aPicView = new ImageView(EAT_BALLS);
         this.getChildren().add(this.aPicView);
      }

      public void distanceReader() {
         this.aPicView.setTranslateX(this.x);
         this.aPicView.setTranslateY(this.y);
         int dx = x2 - x;
         int dy = y2 - y;
         double distance = Math.sqrt(dx * dx + dy * dy);

      }

      public boolean touchBlackPP() {
         Color lt = backgroundCollision.getPixelReader().getColor(x, y - 1);
         Color lb = backgroundCollision.getPixelReader().getColor(x, y + 32);

         Color rt = backgroundCollision.getPixelReader().getColor(x + 32, y - 1);
         Color rb = backgroundCollision.getPixelReader().getColor(x + 32, y + 32);

         if (lt.getBrightness() < 0.1 || lb.getBrightness() < 0.1 || rt.getBrightness() < 0.1
               || rb.getBrightness() < 0.1) {
            return true;
         } else {
            return false;
         }

      }

      public boolean update() {

         this.aPicView.setTranslateX(this.x);
         this.aPicView.setTranslateY(this.y);
         int dx = (x2 + 16) - (x + 16);
         int dy = (y2 + 16) - (y + 16);

         double distance = Math.sqrt(dx * dx + dy * dy);

         if (distance < 30) {

            return true;

         } else {
            return false;
         }

      }
   }

   private class Mito extends Pane {
      private ImageView aPicView;
      private int x = 310;
      private int y = 104;

      public Mito() {
         this.aPicView = new ImageView(MONEY);
         this.getChildren().add(this.aPicView);
         this.aPicView.setTranslateX(x);
         this.aPicView.setTranslateY(y);

      }

      public boolean update() {

         this.aPicView.setTranslateX(this.x);
         this.aPicView.setTranslateY(this.y);
         int dx = (x2 + 16) - (x + 16);
         int dy = (y2 + 16) - (y + 16);

         double distance = Math.sqrt(dx * dx + dy * dy);

         if (distance < 30) {

            return true;

         } else {
            return false;
         }

      }

   }

   private class PlenkyHunter extends Pane {
      private ImageView aPicView;
      Random random = new Random();
      int check = random.nextInt(4);

      private int racePosX = 310;
      private int racePosY = 104;

      public PlenkyHunter() {
         this.aPicView = new ImageView(ICON_IMAGE_RUNNERS);
         this.getChildren().add(this.aPicView);
      }


      public boolean mitoIkorupcija() {
         int dx = (x2 + 16) - (racePosX + 16);
         int dy = (y2 + 16) - (racePosY + 16);

         double distance = Math.sqrt(dx * dx + dy * dy);

         if (distance < 15) {

            return true;

         } else {
            return false;
         }

      }

      public void update() {

         this.aPicView.setTranslateX(this.racePosX);
         this.aPicView.setTranslateY(this.racePosY);


         Color colorTop = backgroundCollision.getPixelReader().getColor(racePosX + 15,
               racePosY - 1);
         Color colorBot = backgroundCollision.getPixelReader().getColor(racePosX + 15,
               racePosY + 32);
         Color colorLeft = backgroundCollision.getPixelReader().getColor(racePosX - 1,
               racePosY + 15);
         Color colorRight = backgroundCollision.getPixelReader().getColor(racePosX + 32,
               racePosY + 15);


         Color cl1 = backgroundCollision.getPixelReader().getColor(racePosX - 10,
               racePosY - 4);
         Color cl2 = backgroundCollision.getPixelReader().getColor(racePosX - 10,
               racePosY + 35);


         Color cr1 = backgroundCollision.getPixelReader().getColor(racePosX + 42,
               racePosY - 4);
         Color cr2 = backgroundCollision.getPixelReader().getColor(racePosX + 42,
               racePosY + 35);


         Color cu1 = backgroundCollision.getPixelReader().getColor(racePosX - 4,
               racePosY - 10);
         Color cu2 = backgroundCollision.getPixelReader().getColor(racePosX + 35,
               racePosY - 10);


         Color cd1 = backgroundCollision.getPixelReader().getColor(racePosX - 4,
               racePosY + 42);
         Color cd2 = backgroundCollision.getPixelReader().getColor(racePosX + 35,
               racePosY + 42);

         //
         if (check == 0) {
            if (colorTop.getBlue() > 0.8) {

               racePosY = racePosY - stop;

               if (cl1.getBlue() > 0.8 && cl2.getBlue() > 0.8 || cr1.getBlue() > 0.8 && cr2.getBlue() > 0.8) {
                  check = random.nextInt(4);
               }
            } else {
               check = random.nextInt(4);
            }
         }
         if (check == 1) {
            if (colorBot.getBlue() > 0.8) {

               racePosY = racePosY + stop;

               if (cl1.getBlue() > 0.8 && cl2.getBlue() > 0.8 || cr1.getBlue() > 0.8 && cr2.getBlue() > 0.8) {
                  check = random.nextInt(4);
               }
            } else {
               check = random.nextInt(4);
            }
         }
         if (check == 2) {
            if (colorRight.getBlue() > 0.8) {

               racePosX = racePosX + stop;

               if (cu1.getBlue() > 0.8 && cu2.getBlue() > 0.8 || cd1.getBlue() > 0.8 && cd2.getBlue() > 0.8) {
                  check = random.nextInt(4);
               }
            } else {
               check = random.nextInt(4);
            }
         }
         if (check == 3) {
            if (colorLeft.getBlue() > 0.8) {

               racePosX = racePosX - stop;
               if (cu1.getBlue() > 0.8 && cu2.getBlue() > 0.8 || cd1.getBlue() > 0.8 && cd2.getBlue() > 0.8) {
                  check = random.nextInt(4);
               }
            } else {
               check = random.nextInt(4);
            }
         }

      }

   }

   private class PacmanRacer extends Pane implements Serializable {

      private final static long serialVersionUid = 01L;

      private ImageView aPicView;
      private String slika = ICON_IMAGE;
      private int racePosX = 24;
      private int racePosY = 264;

      private int previouisX;
      private int previouisY;

      public PacmanRacer() {
         this.aPicView = new ImageView(slika);
         this.getChildren().add(this.aPicView);

      }

      public void setFrame(String nextFrame) {
         this.getChildren().remove(this.aPicView);
         slika = nextFrame;
         Image image = new Image(slika);
         this.aPicView = new ImageView(image);
         this.getChildren().add(this.aPicView);

      }

      public int getY() {
         return racePosY;
      }

      public int getX() {
         return racePosX;
      }

      public void update() {

         this.aPicView.setTranslateX(racePosX);
         this.aPicView.setTranslateY(racePosY);

         x2 = racePosX;
         y2 = racePosY;

         int speed = 2;

         Color colorMid = backgroundCollision.getPixelReader().getColor(racePosX + readjustXmid,
               racePosY + readjustYmid);
         Color colorLeft = backgroundCollision.getPixelReader().getColor(racePosX + readjustXleft,
               racePosY + readjustYleft);
         Color colorRight = backgroundCollision.getPixelReader().getColor(racePosX + readjustXright,
               racePosY + readjustYright);

         if (colorMid.getBrightness() < 0.1 || colorLeft.getBrightness() < 0.1 || colorRight.getBrightness() < 0.1) {

            racePosX = previouisX;
            racePosY = previouisY;

         } else {
            previouisX = racePosX;
            previouisY = racePosY;

         }

         if (moveUp) {

            racePosY -= speed;

         }
         if (moveLeft) {

            racePosX -= speed;

         }
         if (moveDown) {

            racePosY += speed;

         }
         if (moveRight) {

            racePosX += speed;

         }

      }
   }

   public void alert(AlertType type, String messsage, String header) {
      Alert a = new Alert(type, messsage);
      a.setHeaderText(header);
      a.showAndWait();

   }

   public boolean posCheck(int x, int y) {
      Color tl = backgroundCollision.getPixelReader().getColor(x, y);
      Color bl = backgroundCollision.getPixelReader().getColor(x, y);
      if (tl.getBrightness() < 0.1 || bl.getBrightness() < 0.1) {
         return false;

      } else {
         return true;
      }

   }

   public void resetScene() {
      gameScene = new Scene(new Pane(), 640, 320);
      stage.setScene(gameScene);
   }

   public void doConnect() {
      try {
         socket = new Socket(ipAdress, PORT);
         System.out.println("Connected to server!");
      } catch (IOException e) {
         alert(AlertType.INFORMATION, "Unable to connect to server!", "Connection failed");
      }
   }

   public void doDisconnect() {
      try {
         socket.close();
         System.out.println("Server disconnected!");
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   @Override
   public void handle(ActionEvent event) {
      Button b = (Button) event.getSource();
      switch (b.getText()) {
         case "CONNECT":
            doConnect();
            try {
               oos = new ObjectOutputStream(socket.getOutputStream());
            } catch (IOException e) {

               e.printStackTrace();
            }
            startGame();
            break;
         case "QUIT":
            try {
               if (oos != null) {

                  oos.flush();
                  socket.close();
                  oos.close();
                  Platform.exit();
               }
            } catch (IOException e) {

               e.printStackTrace();
            }
            break;
      }
   }

}