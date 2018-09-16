import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Thomas Baum
 */
@SuppressWarnings("unchecked")
public class Main extends Application {
    private Task rollDiceWorker;
    private ProgressBar rollDiceProgress;
    private BarChart distribution;
    private VBox root;
    private int[] data;

    @Override
    public void start(Stage window) {
        root = new VBox(5);
        root.setAlignment(Pos.CENTER);

        rollDiceWorker = rollDiceWorker(2, 1000);
        new Thread(rollDiceWorker).start();

        drawGraph();

        //Controls
        rollDiceProgress = new ProgressBar(0);

        TextField diceCountTextField = new TextField("2");
        diceCountTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d")) {
                diceCountTextField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        TextField rollCountTextField = new TextField("1000");
        rollCountTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d")) {
                rollCountTextField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        Button roll = new Button("Roll");
        roll.setOnAction(event -> {
            rollDiceWorker = rollDiceWorker(Integer.parseInt(diceCountTextField.getText()),
                    Integer.parseInt(rollCountTextField.getText()));

            rollDiceProgress.progressProperty().unbind();
            rollDiceProgress.progressProperty().bind(rollDiceWorker.progressProperty());

            new Thread(rollDiceWorker).start();

            rollDiceWorker.setOnSucceeded(event1 -> {
                rollDiceProgress.progressProperty().unbind();
                rollDiceProgress.setProgress(0);
                drawGraph();
            });
        });

        HBox controls = new HBox(5);
        controls.setAlignment(Pos.CENTER);
        controls.getChildren().addAll(diceCountTextField, rollCountTextField, roll, rollDiceProgress);

        root.getChildren().addAll(controls);

        Scene scene = new Scene(root);

        window.setScene(scene);
        window.show();
    }

    private void drawGraph() {
        root.getChildren().remove(distribution);

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Roll Results");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Frequency");

        XYChart.Series dataSeries = new XYChart.Series();

        for (int i = 0; i < data.length; i++) {
            dataSeries.getData().add(new XYChart.Data<>("" + (i + 1), data[i]));
        }

        BarChart distribution = new BarChart<>(xAxis, yAxis);
        distribution.getData().add(dataSeries);

        root.getChildren().add(0, distribution);
        this.distribution = distribution;

    }

    private Task rollDiceWorker(int diceCount, int rollCount) {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                data = new int[diceCount * 6];
                for (int i = 0; i < rollCount; i++) {
                    int total = 0;
                    for (int j = 0; j < diceCount; j++) {
                        total += ThreadLocalRandom.current().nextInt(1, 7);
                    }

                    data[total - 1] += 1;

                    updateProgress(i + 1, rollCount);
                }
                return true;
            }
        };
    }

    public static void main(String[] args) {
        launch(args);
    }
}
