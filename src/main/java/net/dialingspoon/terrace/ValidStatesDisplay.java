package net.dialingspoon.terrace;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.List;

public class ValidStatesDisplay extends Application {

    private List<Board> validStates; // Assuming you have a List of valid board states

    private int currentBoardIndex = 0; // Index of the currently displayed board
    private GridPane grid = new GridPane();// Create a GridPane to display the board
    // Create buttons to navigate between boards
    private Button prevButton = new Button("Previous");
    private Button nextButton = new Button("Next");
    private Button doneButton = new Button("Done");
    private Text pageNumberText = new Text();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Valid States Display");

        // Add event handlers for the navigation buttons
        prevButton.setOnAction(e -> showPreviousBoard());
        nextButton.setOnAction(e -> showNextBoard());
        doneButton.setOnAction(e -> reset(primaryStage));

        // Create a Scene and set it on the Stage
        Scene scene = new Scene(grid, 400, 400); // Set the desired width and height
        primaryStage.setScene(scene);

        // Show the initial board
        showBoard(0);

        primaryStage.show();
    }

    // Method to display the board at the specified index
    private void showBoard(int index) {
        if (index >= 0 && index < validStates.size()) {
            Board board = validStates.get(index);

            // Clear the grid
            grid.getChildren().clear();

            // Assuming numRows and numCols are defined for the Board class
            for (int row = 0; row < board.getNumRows(); row++) {
                for (int col = 0; col < board.getNumCols(); col++) {
                    boolean isOn = board.getTile(row, col).isOn();

                    // Create a cell representing the on/off state
                    Cell cell = new Cell(isOn);

                    // Add the cell to the grid
                    grid.add(cell, col, row);
                }
            }
            grid.add(prevButton, 0, validStates.get(0).getNumRows()+1, 2, 1);
            grid.add(nextButton, 2, validStates.get(0).getNumRows()+1, 1, 1);
            grid.add(doneButton, 0, validStates.get(0).getNumRows()+2, 10, 1);
            grid.add(new Text(index+1+"/"+validStates.size()), 3, validStates.get(0).getNumRows()+1, 10, 1);
            currentBoardIndex = index;
        }
    }

    // Method to show the previous board
    private void showPreviousBoard() {
        if (currentBoardIndex > 0) {
            showBoard(currentBoardIndex - 1);
        }
    }

    private void reset(Stage primaryStage) {
        BoardDisplayUI boardDisplayUI = new BoardDisplayUI();
        boardDisplayUI.start(primaryStage);
    }

    // Method to show the next board
    private void showNextBoard() {
        if (currentBoardIndex < validStates.size() - 1) {
            showBoard(currentBoardIndex + 1);
        }
    }

    public void setValidStates(List<Board> validStates) {
        this.validStates = validStates;
    }

    private class Cell extends StackPane {
        public Cell(boolean isOn) {
            // Set the cell's size, background color, and border
            setPrefSize(40, 40);
            setBackground(new Background(new BackgroundFill(
                    isOn ? Color.BLACK : Color.WHITE, // Set the background color based on isOn state
                    CornerRadii.EMPTY,
                    Insets.EMPTY
            )));
            setBorder(new Border(new BorderStroke(
                    Color.GRAY,
                    BorderStrokeStyle.SOLID,
                    CornerRadii.EMPTY,
                    BorderWidths.DEFAULT
            )));
        }
    }
}