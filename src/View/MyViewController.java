package View;

import ViewModel.MyViewModel;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.transform.Scale;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;


public class MyViewController implements Initializable, Observer {

    private MyViewModel myViewModel;
    private Maze maze;
    @FXML
    public Label lbl_player_row;
    @FXML
    public Label lbl_player_column;
    @FXML
    public MazeGenerator generator;
    @FXML
    public TextField textField_mazeRows;
    @FXML
    public TextField textField_mazeColumns;
    @FXML
    public MazeDisplayer mazeDisplayer;
    @FXML
    public Pane MainPane;
    @FXML
    Logger logger = Logger.getLogger(MyViewController.class);
    public VBox VBox;
    public javafx.scene.Node GridPane_newMaze;
    public BorderPane borderPane;
    private Stage primaryStage;
    private MediaPlayer mp; //Media player
    private boolean changedSettings = false;
    StringProperty update_player_position_row = new SimpleStringProperty();
    StringProperty update_player_position_col = new SimpleStringProperty();

    public void setMyViewModel(MyViewModel myViewModel) {
        this.myViewModel = myViewModel;
    }

    public void set_update_player_position_row(String update_player_position_row) {
        this.update_player_position_row.set(update_player_position_row);
    }

    public void set_update_player_position_col(String update_player_position_col) {
        this.update_player_position_col.set(update_player_position_col);
    }

    public void initialize(URL url, ResourceBundle resourceBundle) {
        lbl_player_row.textProperty().bind(update_player_position_row);
        lbl_player_column.textProperty().bind(update_player_position_col);

    }

    private void setMusic(String path) {
        //If there is a mediaPlayer, stop it
        if (mp != null) {
            mp.stop();
        }
        //Create a new media player using the given path
        Media m=null;
        try{
            m = new Media(getClass().getResource(path).toURI().toString());
        }
        catch (Exception e){
            System.out.println(path);
        }
        mp = new MediaPlayer(m);
        //Play the music
        mp.play();
    }

    public void generateMaze(ActionEvent actionEvent) throws FileNotFoundException {
        if(generator == null)
            generator = new MazeGenerator();
        if (changedSettings){
            myViewModel.refreshStrategies();
            changedSettings = false;
        }
        if(!textField_mazeRows.getText().matches("\\d*")){
            textField_mazeRows.setText("10");
            popAlert("Error","Numbers Only!");
        }
        int rows = Integer.valueOf(textField_mazeRows.getText());
        if(!textField_mazeColumns.getText().matches("\\d*")){
            textField_mazeColumns.setText("10");
            popAlert("Error","Numbers Only!");
        }
        int cols = Integer.valueOf(textField_mazeColumns.getText());
        myViewModel.generateMaze(rows,cols);
        mazeDisplayer.setFirstRun(true);
        mazeDisplayer.requestFocus();
        mazeDisplayer.widthProperty().bind(MainPane.widthProperty());
        mazeDisplayer.heightProperty().bind(MainPane.heightProperty());
        mazeDisplayer.drawMaze(myViewModel.getMaze(),0);
        setMusic("/Resources/mp3/backgroundMusic.mp3");
        logger.info("Maze generated");
    }

    public void stopMusic(ActionEvent actionEvent) throws FileNotFoundException {
        if (mp != null) {
            mp.pause();
            logger.info("Music stopped");
        }
    }

    public void playMusic(ActionEvent actionEvent) throws FileNotFoundException {
        if (mp != null) {
            mp.play();
            logger.info("Music playing");
        }
    }

    public void solveMaze() throws FileNotFoundException {
        myViewModel.solveMaze(mazeDisplayer.getRow_player(),mazeDisplayer.getColumn_player());
        mazeDisplayer.drawSolution(myViewModel.getSolution());
        logger.info("Solve maze is triggered");
    }

    public void mouseClicked(MouseEvent mouseEvent) {
        mazeDisplayer.requestFocus();
    }

    public void keyPressed(KeyEvent keyEvent) throws FileNotFoundException {
        myViewModel.moveCharacter(keyEvent);
        keyEvent.consume();
    }

    public  void  newGame(){
        mp.stop();
        if(generator == null)
            generator = new MazeGenerator();
        if (changedSettings){
            myViewModel.refreshStrategies();
            changedSettings = false;
        }
        try {
            int rows = Integer.valueOf(textField_mazeRows.getText());
            int columns = Integer.valueOf(textField_mazeColumns.getText());
            //  GridPane_newMaze.setDisable(false);
            myViewModel.generateMaze(rows, columns);
            mazeDisplayer.setFirstRun(true);
           // update_player_position_row = myViewModel.getMaze().getStartPosition().getRowIndex();
            mazeDisplayer.requestFocus();
            mazeDisplayer.widthProperty().bind(MainPane.widthProperty());
            mazeDisplayer.heightProperty().bind(MainPane.heightProperty());
            mazeDisplayer.drawMaze(myViewModel.getMaze(),0);
            setMusic("/Resources/mp3/backgroundMusic.mp3");
            logger.info("New Maze");
        } catch (NumberFormatException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public  void  changeSettings(){
        try{
            Stage helpStage = new Stage();
            helpStage.setTitle("Settings");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("Properties.fxml").openStream());
            Scene scene = new Scene(root, 315, 400);//possible to specify w and h
            helpStage.setScene(scene);
            helpStage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            changedSettings = true;
            helpStage.show();
            logger.info("Change settings is triggered.");
        }catch (Exception e){ }
    }

    public  void  exitGame(){
        logger.info("exit game is triggered.");
        String strExit = "Are you sure you want to exit?";
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("exit window");
        window.setMinHeight(300);
        window.setMinWidth(300);


        Label label = new Label();
        label.setText(strExit);
        Button yesButton = new Button("Yes!");
        Button noButton = new Button("No!");
        noButton.setOnAction(e -> window.close());
        yesButton.setOnAction(e ->
                myViewModel.exit());
                System.exit(0);//Platform.exit();
        VBox layout = new VBox(20);//Platform.exit();
        layout.getChildren().addAll(label, yesButton, noButton);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
        logger.info("Exit from game");

    }

    public void helpGame () {
        try{
            Stage helpStage = new Stage();
            helpStage.setTitle("Help");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("Help.fxml").openStream());
            Scene scene = new Scene(root, 515, 530);//possible to specify w and h
            helpStage.setScene(scene);
            helpStage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            helpStage.show();
        }catch (Exception e){ }

    }

    public void aboutGame () {
        try {
            Stage aboutStage = new Stage();
            aboutStage.setTitle("About");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("About.fxml").openStream());
            Scene scene = new Scene(root, 315, 400);//possible to specify w and h
            aboutStage.setScene(scene);
            aboutStage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            aboutStage.show();
        } catch (Exception e) {
        }
    }

    public void mouseScroll(ScrollEvent scrollEvent){
        if (scrollEvent.isControlDown()){
            double zoomFactor = 1.5;
            if (scrollEvent.getDeltaY() <= 0){
                zoomFactor = 1/zoomFactor;
            }

            Scale newScale = new Scale();
            newScale.setX(MainPane.getScaleX() * zoomFactor);
            newScale.setY(MainPane.getScaleY() * zoomFactor);
            newScale.setPivotX(MainPane.getScaleX());
            newScale.setPivotY(MainPane.getScaleY());
            MainPane.getTransforms().add(newScale);

        }
    }

    public void saveGame() throws IOException {
        logger.info("Save game is triggered.");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Maze Files", "*.maze")
        );
        fileChooser.setInitialFileName("My Maze To Save");
        File saveFile = fileChooser.showSaveDialog(primaryStage);
        if (saveFile != null) {
            myViewModel.saveGame(saveFile);
        }
    }

    public void loadGame() throws IOException, ClassNotFoundException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Maze Files", "*.maze"));
        File loadFile = fileChooser.showOpenDialog(primaryStage);
        logger.info("Loading game");
        if (loadFile != null) {
            myViewModel.loadGame(loadFile);
        } else {
        }
    }

    public void popAlert (String title, String message ){

        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(300);
        window.setMinHeight(300);

        Label label = new Label();
        label.setText(message);
        Button closeButton = new Button("Close this window");
        closeButton.setOnAction(e -> window.close());

        VBox layout = new VBox(20);
        layout.getChildren().addAll(label, closeButton);
        layout.setAlignment(Pos.CENTER);

        //Display window and wait for it to be closed before returning
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
    }

    public void mouseDragged(MouseEvent mouseEvent) {
        if(myViewModel.getMaze() != null) {
            int maximumSize = Math.max(myViewModel.getMaze().getMaze()[0].length, myViewModel.getMaze().getMaze().length);
            double mousePosX=mouseDraggedCheck(maximumSize,mazeDisplayer.getHeight(),
                    myViewModel.getMaze().getMaze().length,mouseEvent.getX(),mazeDisplayer.getWidth() / maximumSize,VBox.getWidth());
            double mousePosY=mouseDraggedCheck(maximumSize,mazeDisplayer.getWidth(),
                    myViewModel.getMaze().getMaze()[0].length,mouseEvent.getY(),mazeDisplayer.getHeight() / maximumSize,0);
            Move(mousePosX, mousePosY);
        }
    }

    private void Move(double mousePosX, double mousePosY) {
        if ( mousePosX == myViewModel.getColChar() && mousePosY < myViewModel.getRowChar() )
            myViewModel.moveCharacter(KeyCode.NUMPAD8);
        else if (mousePosY == myViewModel.getRowChar() && mousePosX > myViewModel.getColChar() )
            myViewModel.moveCharacter(KeyCode.NUMPAD6);
        else if ( mousePosY == myViewModel.getRowChar() && mousePosX < myViewModel.getColChar() )
            myViewModel.moveCharacter(KeyCode.NUMPAD4);
        else if (mousePosX == myViewModel.getColChar() && mousePosY > myViewModel.getRowChar()  )
            myViewModel.moveCharacter(KeyCode.NUMPAD2);
    }

    private  double mouseDraggedCheck(int maxsize, double canvasSize, int mazeSize,double mouseEvent,double temp,double extra){
        double cellSize=canvasSize/maxsize;
        double start = (canvasSize / 2 - (cellSize * mazeSize / 2)) / cellSize ;
        double mouse = (int) (((mouseEvent) - start ) / temp);
        return mouse;
    }

    public void update(Observable o, Object arg) {

        if (arg == "loaded"){
            try {
                maze = myViewModel.getMaze();
                Position startPosition = maze.getStartPosition();
                set_update_player_position_row(startPosition.getRowIndex() + "");
                set_update_player_position_col(startPosition.getColumnIndex() + "");
                mazeDisplayer.set_player_position(myViewModel.getRowChar(),myViewModel.getColChar(),0);
                mazeDisplayer.set_goal_position();
                mazeDisplayer.drawMaze(maze,0);
                mazeDisplayer.drawReachToGoal(myViewModel.reachGoal());
                logger.info("Game is loaded");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (arg == "generateMaze")
            {
                this.maze = myViewModel.getMaze();
                Position startPosition = maze.getStartPosition();
                try {
                    set_update_player_position_row(startPosition.getRowIndex() + "");
                    set_update_player_position_col(startPosition.getColumnIndex() + "");
                    mazeDisplayer.drawMaze(maze,0);
                    logger.info("Drawing maze");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
         else{
                Maze maze = myViewModel.getMaze();
                if (maze == this.maze)//Not generateMaze
                {

                    int rowChar = mazeDisplayer.getRow_player();
                    int colChar = mazeDisplayer.getColumn_player();
                    int rowFromViewModel = myViewModel.getRowChar();
                    int colFromViewModel = myViewModel.getColChar();
                    boolean isreacFromViewModel=myViewModel.reachGoal();

                    if(rowFromViewModel == rowChar && colFromViewModel == colChar)//Solve Maze
                    {
                        myViewModel.getSolution();
                    }
                    else//Update location
                    {
                        set_update_player_position_row(rowFromViewModel + "");
                        set_update_player_position_col(colFromViewModel + "");
                        try {
                            int direction = (int) arg;
                            this.mazeDisplayer.set_player_position(rowFromViewModel,colFromViewModel,direction);
                            this.mazeDisplayer.drawReachToGoal(isreacFromViewModel);
                            if (isreacFromViewModel)
                                this.setMusic("/Resources/mp3/finish.mp3");
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else//GenerateMaze
                {
                    this.maze = maze;
                    try {

                        Position startPosition = maze.getStartPosition();
                        set_update_player_position_row(startPosition.getRowIndex() + "");
                        set_update_player_position_col(startPosition.getColumnIndex() + "");
                        mazeDisplayer.drawMaze(maze,0);
                    }
                    catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

