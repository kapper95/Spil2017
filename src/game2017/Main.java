package game2017;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Main extends Application {

	public static final int size = 20;
	public static final int scene_height = size * 20 + 100;
	public static final int scene_width = size * 20 + 200;

	public static Image image_floor;
	public static Image image_wall;
	public static Image hero_right, hero_left, hero_up, hero_down;

	public static Player me;
	public static List<Player> players = new ArrayList<>();
	public static String playerIP[] = { "192.168.0.25", "192.168.0.29", "192.168.0.28" };
	private static Socket clientSocket;

	private Label[][] fields;
	private TextArea scoreList;

	private String[] board = { // 20x20
			"wwwwwwwwwwwwwwwwwwww", "w        ww        w", "w w  w  www w  w  ww", "w w  w   ww w  w  ww",
			"w  w               w", "w w w w w w w  w  ww", "w w     www w  w  ww", "w w     w w w  w  ww",
			"w   w w  w  w  w   w", "w     w  w  w  w   w", "w ww ww        w  ww", "w  w w    w    w  ww",
			"w        ww w  w  ww", "w         w w  w  ww", "w        w     w  ww", "w  w              ww",
			"w  w www  w w  ww ww", "w w      ww w     ww", "w   w   ww  w      w", "wwwwwwwwwwwwwwwwwwww" };

	// -------------------------------------------
	// | Maze: (0,0) | Score: (1,0) |
	// |-----------------------------------------|
	// | boardGrid (0,1) | scorelist |
	// | | (1,1) |
	// -------------------------------------------

	@Override
	public void start(Stage primaryStage) {
		try {
			clientSocket = new Socket("192.168.0.25", 6789);

			GridPane grid = new GridPane();
			grid.setHgap(10);
			grid.setVgap(10);
			grid.setPadding(new Insets(0, 10, 0, 10));

			Text mazeLabel = new Text("Maze:");
			mazeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));

			Text scoreLabel = new Text("Score:");
			scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));

			scoreList = new TextArea();

			GridPane boardGrid = new GridPane();

			image_wall = new Image(this.getClass().getResourceAsStream("Image/wall4.png"), size, size, false, false);
			image_floor = new Image(this.getClass().getResourceAsStream("Image/floor1.png"), size, size, false, false);

			hero_right = new Image(this.getClass().getResourceAsStream("Image/heroRight.png"), size, size, false,
					false);
			hero_left = new Image(this.getClass().getResourceAsStream("Image/heroLeft.png"), size, size, false, false);
			hero_up = new Image(this.getClass().getResourceAsStream("Image/heroUp.png"), size, size, false, false);
			hero_down = new Image(this.getClass().getResourceAsStream("Image/heroDown.png"), size, size, false, false);

			fields = new Label[20][20];
			for (int j = 0; j < 20; j++) {
				for (int i = 0; i < 20; i++) {
					switch (board[j].charAt(i)) {
					case 'w':
						fields[i][j] = new Label("", new ImageView(image_wall));
						break;
					case ' ':
						fields[i][j] = new Label("", new ImageView(image_floor));
						break;
					default:
						throw new Exception("Illegal field value: " + board[j].charAt(i));
					}
					boardGrid.add(fields[i][j], i, j);
				}
			}
			scoreList.setEditable(false);

			grid.add(mazeLabel, 0, 0);
			grid.add(scoreLabel, 1, 0);
			grid.add(boardGrid, 0, 1);
			grid.add(scoreList, 1, 1);

			Scene scene = new Scene(grid, scene_width, scene_height);
			primaryStage.setScene(scene);
			primaryStage.show();

			try {

				DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());

				scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
					switch (event.getCode()) {
					case UP:
						try {
							outToServer.writeBytes(me.getXpos() + " " + me.getYpos() + " " + me.name + " " + 0 + " "
									+ -1 + " " + "up" + "\n");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
					case DOWN:
						try {
							outToServer.writeBytes(me.getXpos() + " " + me.getYpos() + " " + me.name + " " + 0 + " " + 1
									+ " " + "down" + "\n");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						// this.playerMoved(0, +1, "down");
						break;
					case LEFT:
						try {
							outToServer.writeBytes(me.getXpos() + " " + me.getYpos() + " " + me.name + " " + -1 + " "
									+ 0 + " " + "left" + "\n");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						// this.playerMoved(-1, 0, "left");
						break;
					case RIGHT:
						try {
							outToServer.writeBytes(me.getXpos() + " " + me.getYpos() + " " + me.name + " " + 1 + " " + 0
									+ " " + "right" + "\n");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						// this.playerMoved(+1, 0, "right");
						break;
					default:
						break;
					}
				});

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// Setting up standard players

			me = new Player("Allan", 10, 10, "up");
			players.add(me);
			fields[10][10].setGraphic(new ImageView(hero_up));

			scoreList.setText(this.getScoreList());

			Update update = new Update(clientSocket, me.name, this);
			update.start();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void playerMoved(String name, int delta_x, int delta_y, String direction) {
		Player me = null;
		for (Player p : players) {
			if (p.name.equals(name)) {
				me = p;
			}
		}
		me.direction = direction;
		int x = me.getXpos(), y = me.getYpos();

		if (board[y + delta_y].charAt(x + delta_x) == 'w') {
			me.addPoints(-1);
		} else {
			Player p = this.getPlayerAt(x + delta_x, y + delta_y);
			if (p != null) {
				me.addPoints(10);
				p.addPoints(-10);
			} else {
				me.addPoints(1);

				fields[x][y].setGraphic(new ImageView(image_floor));
				x += delta_x;
				y += delta_y;

				if (direction.equals("right")) {
					fields[x][y].setGraphic(new ImageView(hero_right));
				}
				;
				if (direction.equals("left")) {
					fields[x][y].setGraphic(new ImageView(hero_left));
				}
				;
				if (direction.equals("up")) {
					fields[x][y].setGraphic(new ImageView(hero_up));
				}
				;
				if (direction.equals("down")) {
					fields[x][y].setGraphic(new ImageView(hero_down));
				}
				;

				me.setXpos(x);
				me.setYpos(y);
			}
		}
		scoreList.setText(this.getScoreList());

	}

	public String getScoreList() {
		StringBuffer b = new StringBuffer(100);
		for (Player p : players) {
			b.append(p + "\r\n");
		}
		return b.toString();
	}

	public Player getPlayerAt(int x, int y) {
		for (Player p : players) {
			if (p.getXpos() == x && p.getYpos() == y) {
				return p;
			}
		}
		return null;
	}

	public void spawnPlayer(int x, int y) {

		fields[x][y].setGraphic(new ImageView(hero_up));

	}

	public static void main(String[] args) {
		Application.launch(args);
	}
}
