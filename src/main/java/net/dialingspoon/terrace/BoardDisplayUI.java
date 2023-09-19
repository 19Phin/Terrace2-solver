package net.dialingspoon.terrace;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.util.List;

public class BoardDisplayUI extends Application {

    private int numRows = 5; // Default number of rows
    private int numCols = 5; // Default number of columns
    private Tile[][] board;
    GridPane grid = new GridPane();
    private Button updateBoardButton; // Declare the update board button

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Board Display");

        // Create a GridPane for organizing the cells and controls
        grid.setHgap(2); // Horizontal gap between cells
        grid.setVgap(2); // Vertical gap between cells

        // Create input fields for specifying the number of rows and columns
        TextField numRowsField = new TextField(Integer.toString(numRows));
        TextField numColsField = new TextField(Integer.toString(numCols));

        // Create a button to apply changes
        Button applyButton = new Button("Apply Changes");
        applyButton.setOnAction(e -> {
            // Update numRows and numCols based on user input
            numRows = Integer.parseInt(numRowsField.getText());
            numCols = Integer.parseInt(numColsField.getText());

            // Clear the grid and recreate cells with the updated size
            grid.getChildren().clear();
            for (int row = 0; row < numRows; row++) {
                for (int col = 0; col < numCols; col++) {
                    Cell cell = new Cell();
                    // Add a mouse event handler to toggle the cell's color and handle right-clicks
                    cell.setOnMouseClicked(event -> {
                        if (event.getButton() == MouseButton.PRIMARY) {
                            cell.toggleColor();
                        } else if (event.getButton() == MouseButton.SECONDARY) {
                            cell.toggleDot();
                        }
                    });
                    grid.add(cell, col, row);
                }
            }
            grid.add(updateBoardButton, 0, numRows + 4, 3, 1); // Add the update board button
            // Make the update board button visible after applyButton is pressed
            updateBoardButton.setVisible(true);
        });

        // Create labels for input fields
        Label numRowsLabel = new Label("Number of Rows:");
        Label numColsLabel = new Label("Number of Columns:");

        // Create a button to update the board based on cell colors
        updateBoardButton = new Button("Update Board");
        updateBoardButton.setVisible(false);
        updateBoardButton.setOnAction(e -> updateBoardFromCells(primaryStage));

        // Add controls to the grid
        grid.add(numRowsLabel, 0, numRows + 1);
        grid.add(numRowsField, 1, numRows + 1);
        grid.add(numColsLabel, 0, numRows + 2);
        grid.add(numColsField, 1, numRows + 2);
        grid.add(applyButton, 0, numRows + 3, 2, 1); // Span 2 columns
        grid.add(updateBoardButton, 0, numRows + 4, 2, 1); // Add the update board button

        // Create a Scene and set it on the Stage
        Scene scene = new Scene(grid, 400, 400); // Set the desired width and height
        primaryStage.setScene(scene);

        primaryStage.show();
    }

    private void updateBoardFromCells(Stage primaryStage) {
        board = new Tile[numRows][numCols];
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                Cell cell = getCellAt(col, row);

                if (cell != null) {
                    String cellColor = cell.getCurrentColor();
                    int var;

                    // Determine var based on cell color
                    if (cellColor.equals("gray")) {
                        var = -1;
                    } else if (cellColor.equals("black")) {
                        var = 1;
                    } else { // Assume "white"
                        var = 2;
                    }

                    // Update the board with the new Tile
                    board[row][col] = new Tile(false, cell.getDisplayedNumber(), cell.getDisplayedNumberWithShift(), var, cell.getDotCount());
                }
            }
        }
        Board mainBoard = new Board(board, numRows, numCols);

        List<Board> validStates = mainBoard.generateAllStates();

        for (Board validState : validStates) {
            validState.printBoard();
            System.out.println();
        }
        ValidStatesDisplay validStatesDisplay = new ValidStatesDisplay();
        validStatesDisplay.setValidStates(validStates);

        // Launch the JavaFX application
        validStatesDisplay.start(primaryStage);
    }

    // Helper method to get the cell at a specific column and row
    private Cell getCellAt(int col, int row) {
        for (Node node : grid.getChildren()) {
            if (node instanceof Cell && GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                return (Cell) node;
            }
        }
        return null;
    }


    public static void main(String[] args) {
        launch(args);
    }

    // A simple cell class to represent each tile on the board
    private class Cell extends javafx.scene.layout.StackPane {

        private String currentColor = "gray";
        private int dotCount = -1; // Number of yellow dots added
        private int displayedNumber = 0; // Number displayed on the cell
        private int displayedNumberWithShift = 0; // Second displayed number with Shift key
        private boolean isMouseOver = false; // Track if the mouse is over the cell
        private boolean backtickPressed = false; // Track if the mouse is over the cell

        private Label label1 = new Label();
        private Label label2 = new Label();

        public Cell() {
            // Customize the cell's appearance (e.g., background color, border)
            setStyle("-fx-background-color: " + currentColor + "; -fx-border-color: gray;");
            setPrefSize(40, 40); // Set the cell's preferred size

            label1.setTextFill(Color.DARKBLUE); // Set text fill color for the first number
            label2.setTextFill(Color.DARKBLUE); // Set text fill color for the second number
            label1.setTranslateX(-10);
            label2.setTranslateX(10);

            // Add mouse event listeners
            setOnMouseEntered(event -> {isMouseOver = true;
                requestFocus();});
            setOnMouseExited(event -> isMouseOver = false);

            // Add event listeners for key presses
            setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.BACK_QUOTE) backtickPressed = true;

                if (event.getCode().isDigitKey() && isMouseOver) {
                    int newNumber = Integer.parseInt(event.getCode().getName());
                    if (backtickPressed) setDisplayedNumberWithShift(newNumber);
                    else setDisplayedNumber(newNumber);
                }
            });

            setOnKeyReleased(event -> {
                if (event.getCode() == KeyCode.BACK_QUOTE) {
                    backtickPressed = false;
                }
            });

            // Enable focus and key events
            setFocusTraversable(true);
        }

        public void toggleColor() {
            // Toggle the cell's background color between black, white, and gray
            if (currentColor.equals("gray")) {
                currentColor = "black";
            } else if (currentColor.equals("black")) {
                currentColor = "white";
            } else {
                currentColor = "gray";
            }
            setStyle("-fx-background-color: " + currentColor + "; -fx-border-color: gray;");
        }

        public void toggleDot() {
            // Toggle between adding yellow dots and clearing them
            if (dotCount < 4) {
                dotCount++;
                getChildren().clear();
                showDot();

            } else {
                // Clear all yellow dots
                getChildren().clear();
                dotCount = -1;
            }
            if (displayedNumber != 0) {
                getChildren().add(label1);
                if (displayedNumberWithShift != 0) getChildren().add(label2);
            }
        }

        public void showDot() {
            // Toggle between adding yellow dots and clearing them
            Circle purpleDot = new Circle(5, Color.PURPLE);
            purpleDot.setTranslateX(15);
            purpleDot.setTranslateY(15);
            getChildren().add(purpleDot);
            purpleDot = new Circle(5, Color.PURPLE);
            purpleDot.setTranslateX(-15);
            purpleDot.setTranslateY(15);
            getChildren().add(purpleDot);
            purpleDot = new Circle(5, Color.PURPLE);
            purpleDot.setTranslateX(15);
            purpleDot.setTranslateY(-15);
            getChildren().add(purpleDot);
            purpleDot = new Circle(5, Color.PURPLE);
            purpleDot.setTranslateX(-15);
            purpleDot.setTranslateY(-15);
            getChildren().add(purpleDot);
            // Add a yellow dot in a specific corner
            Circle yellowDot = new Circle(5, Color.YELLOW);
            if (dotCount == 1) {
                // Top-left corner
                yellowDot.setTranslateX(15);
                yellowDot.setTranslateY(-15);
                getChildren().add(yellowDot);
            } else if (dotCount == 2) {
                // Bottom-right corner
                yellowDot.setTranslateX(15); // Adjust position
                yellowDot.setTranslateY(15);
                getChildren().add(yellowDot);
            } else if (dotCount == 3) {
                // Bottom-left corner
                yellowDot.setTranslateX(-15);
                yellowDot.setTranslateY(15); // Adjust position
                getChildren().add(yellowDot);
            } else if (dotCount == 4) {
                // Top-right corner
                yellowDot.setTranslateX(-15); // Adjust position
                yellowDot.setTranslateY(-15);
                getChildren().add(yellowDot);
            }
        }

        public void setDisplayedNumber(int number) {
            // Set and display the specified number on the cell
            displayedNumber = number;
            label1.setText(Integer.toString(displayedNumber));
            getChildren().clear();
            if (displayedNumber != 0) {
                getChildren().add(label1);
                if (displayedNumberWithShift != 0) getChildren().add(label2);
            } else {
                displayedNumberWithShift = 0;
            }
            if (dotCount != -1) showDot();
        }

        public void setDisplayedNumberWithShift(int number) {
            // Set and display the second specified number on the cell with Shift
            if (displayedNumber == 0) return;
            displayedNumberWithShift = number;
            label2.setText(Integer.toString(displayedNumberWithShift));
            getChildren().clear();
            getChildren().add(label1);
            if (displayedNumberWithShift != 0) {
                getChildren().add(label2);
            }
            if (dotCount != -1) showDot();
        }

        public String getCurrentColor() {
            return currentColor;
        }

        public int getDisplayedNumber() {
            return displayedNumber;
        }

        public int getDisplayedNumberWithShift() {
            return displayedNumberWithShift;
        }

        public int getDotCount() {
            return dotCount;
        }
    }
}
