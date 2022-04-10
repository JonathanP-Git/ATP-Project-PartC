package View;

import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.AState;
import algorithms.search.MazeState;
import algorithms.search.Solution;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;

import javafx.scene.image.Image;

public class MazeDisplayer extends Canvas {
    private Maze maze;
    private int row_player =0;
    private int column_player =0;
    private int row_goal;
    private int col_goal;
    private boolean firstRun = true;
    private Solution solution = null;
    private boolean reachGoal = false;
    private int charDirection = 0;
    StringProperty imageFileNameWall = new SimpleStringProperty();
    StringProperty imageFileNamePlayer = new SimpleStringProperty();
    StringProperty imageGoalIcon = new SimpleStringProperty();
    StringProperty imageSolution= new SimpleStringProperty();
    StringProperty imageFinish= new SimpleStringProperty();
    StringProperty imageTree= new SimpleStringProperty();


    StringProperty imageBackground1 = new SimpleStringProperty();
    StringProperty imageBackground2 = new SimpleStringProperty();
    StringProperty imageBackground3= new SimpleStringProperty();
    StringProperty imageBackground4= new SimpleStringProperty();

    StringProperty imagePlayerUp = new SimpleStringProperty();
    StringProperty imagePlayerDown = new SimpleStringProperty();
    StringProperty imagePlayerLeft= new SimpleStringProperty();
    StringProperty imagePlayerRight= new SimpleStringProperty();

    public MazeDisplayer(){
        widthProperty().addListener(evt -> {
            try {
                drawMaze(0);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });
        heightProperty().addListener(evt -> {
            try {
                drawMaze(0);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });
    }


    public String getImageBackground1() {
        return imageBackground1.get();
    }

    public void setImageBackground1(String imageBackground1) {
        this.imageBackground1.set(imageBackground1);
    }

    public String getImageBackground2() {
        return imageBackground2.get();
    }


    public void setImageBackground2(String imageBackground2) {
        this.imageBackground2.set(imageBackground2);
    }

    public String getImageBackground3() {
        return imageBackground3.get();
    }


    public void setImageBackground3(String imageBackground3) {
        this.imageBackground3.set(imageBackground3);
    }

    public String getImageBackground4() {
        return imageBackground4.get();
    }


    public void setImageBackground4(String imageBackground4) {
        this.imageBackground4.set(imageBackground4);
    }


    public String getImagePlayerUp() {
        return imagePlayerUp.get();
    }

    public void setImagePlayerUp(String imagePlayerUp) {
        this.imagePlayerUp.set(imagePlayerUp);
    }

    public String getImagePlayerDown() {
        return imagePlayerDown.get();
    }

    public void setImagePlayerDown(String imagePlayerDown) {
        this.imagePlayerDown.set(imagePlayerDown);
    }

    public String getImagePlayerLeft() {
        return imagePlayerLeft.get();
    }

    public void setImagePlayerLeft(String imagePlayerLeft) {
        this.imagePlayerLeft.set(imagePlayerLeft);
    }

    public String getImagePlayerRight() {
        return imagePlayerRight.get();
    }

    public void setImagePlayerRight(String imagePlayerRight) {
        this.imagePlayerRight.set(imagePlayerRight);
    }

    public String getImageTree() {
        return imageTree.get();
    }

    public void setImageTree(String imageTree) {
        this.imageTree.set(imageTree);
    }

    public String getImageGoalIcon() {
        return imageGoalIcon.get();
    }
    public void setImageGoalIcon(String imageGoalIcon) {
        this.imageGoalIcon.set(imageGoalIcon);
    }
    public int getRow_goal() {
        return row_goal;
    }
    public int getCol_goal() {
        return col_goal;
    }
    public void set_goal_position(){
        this.row_goal = maze.getGoalPosition().getRowIndex();
        this.col_goal = maze.getGoalPosition().getColumnIndex();
    }
    public void set_player_position(int row,int col,int direction) throws FileNotFoundException {
        this.row_player = row;
        this.column_player = col;
        drawMaze(direction);
    }
    public void setFirstRun(boolean firstRun) {
        this.firstRun = firstRun;
    }

    public Maze getMaze() {
        return maze;
    }
    public String getImageFileNameWall() {
        return imageFileNameWall.get();
    }
    public String getImageFileNamePlayer() {
        return imageFileNamePlayer.get();
    }
    public void setImageFileNameWall(String imageFileNameWall) {
        this.imageFileNameWall.set(imageFileNameWall);
    }
    public void setImageFileNamePlayer(String imageFileNamePlayer) { this.imageFileNamePlayer.set(imageFileNamePlayer); }
    public String getImageSolution() { return imageSolution.get(); }
    public void setImageSolution(String imageSolution) { this.imageSolution.set(imageSolution); }

    public String getImageFinish() { return imageFinish.get(); }
    public void setImageFinish(String imageFinish) { this.imageFinish.set(imageFinish); }


    public int getRow_player() {return row_player; }
    public int getColumn_player() {
        return column_player;
    }

    public void drawMaze(Maze maze,int direction) throws FileNotFoundException {
        this.maze = maze;

        if (firstRun){
            Position startPosition = maze.getStartPosition();
            set_player_position(startPosition.getRowIndex(),startPosition.getColumnIndex(),0);
            firstRun = false;
        }
        this.reachGoal=false;
        set_goal_position();
        charDirection = direction;
        drawMaze(direction);
    }

    public void drawSolution(Solution solution) throws FileNotFoundException {
    this.solution = solution;
    drawMaze(charDirection);
    }

    public void drawReachToGoal(boolean isFinish) throws FileNotFoundException {
        this.reachGoal = isFinish;
        drawMaze(charDirection);
    }

    private void drawMaze(int direction) throws FileNotFoundException {
        if(maze != null){
            int [][] matrix = maze.getMaze();
            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            int rows = matrix.length;
            int cols = matrix[0].length;

            double cellHeight = canvasHeight / rows;
            double cellWidth = canvasWidth / cols;

            GraphicsContext graphicsContext = getGraphicsContext2D();
            //clear the canvas:
            graphicsContext.clearRect(0, 0, canvasWidth, canvasHeight);
            graphicsContext.setFill(Color.RED);
            Image grass = null;
            Image treeImage = null;
            Image solutionPathImage = null;
            Image reachToGoalImage = null;
            try{
                treeImage = new Image(new FileInputStream(getImageTree()));
                grass = new Image(new FileInputStream(getBackGround()));
                solutionPathImage = new Image(new FileInputStream(getImageSolution()));
                reachToGoalImage = new Image(new FileInputStream(getImageFinish()));


            } catch(FileNotFoundException e){
                System.out.println("File not found");
            }
            ArrayList<AState> path = null;
            if (solution != null)
                path = solution.getSolutionPath();
            graphicsContext.drawImage(grass,0,0,canvasHeight,canvasWidth);
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if(matrix[i][j] == 1){
                        //if it is a wall:
                        double w = j * cellWidth;
                        double h = i * cellHeight;
                        if (treeImage ==null){
                        graphicsContext.fillRect(w,h, cellWidth, cellHeight);
                        }
                        else{
                        graphicsContext.drawImage(treeImage,w,h,cellWidth,cellHeight);}

                    }
                    if (solution != null){
                        AState p = new MazeState(i,j);
                        if (path.contains(p)) {
                            graphicsContext.drawImage(solutionPathImage, j * cellWidth, i * cellHeight, cellWidth, cellHeight);
                        }
                    }
                    if (reachGoal !=false){
                        graphicsContext.drawImage(reachToGoalImage,40,60,450,400);
                    }

                }
            }

            if (solution != null)
                solution = null;

            charDirection = direction;
            drawPlayerAndGoal(cellHeight, cellWidth, graphicsContext,direction);
        }
    }

    private String getBackGround() {
        Random rd = new Random();
        return switch (1) {
            case 0 -> getImageBackground1();
            case 1 -> getImageBackground2();
            case 2 -> getImageBackground3();
            case 3 -> getImageBackground4();
            default -> null;
        };
    }

    private void drawPlayerAndGoal(double cellHeight, double cellWidth, GraphicsContext graphicsContext , int direction) {
        double h_player = getRow_player() * cellHeight;
        double w_player = getColumn_player() * cellWidth;
        Image playerImage = null;
        try{
        playerImage = new Image(new FileInputStream(getPlayerImage(direction)));
        }catch(FileNotFoundException e){
            System.out.println("File of player not found");
        }
        graphicsContext.drawImage(playerImage,w_player,h_player, cellWidth, cellHeight);

        double h_goal = getRow_goal() * cellHeight;
        double w_goal = getCol_goal() * cellWidth;
        Image goalImage = null;
        try{
            goalImage = new Image(new FileInputStream(getImageGoalIcon()));
        }catch(FileNotFoundException e){
            System.out.println("File of player not found");
        }
        graphicsContext.drawImage(goalImage,w_goal,h_goal, cellWidth, cellHeight);

    }

    private String getPlayerImage(int direction) {
        return switch (direction) {
            case 2 -> getImagePlayerDown(); //down
            case 4-> getImagePlayerRight(); //right
            case 3 -> getImagePlayerLeft(); //left
            case 1 -> getImagePlayerUp(); //up
            case 5 -> getImagePlayerRight(); //down
            case 6 -> getImagePlayerLeft(); //right
            case 7 -> getImagePlayerRight(); //left
            case 8 -> getImagePlayerLeft(); //up
            default -> getImagePlayerDown();
        };
    }

    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public double prefWidth(double height) {
        return getWidth();
    }

    @Override
    public double prefHeight(double width) {
        return getHeight();
    }


}
