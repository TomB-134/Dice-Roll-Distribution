import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.concurrent.ThreadLocalRandom;

public class Main extends Application {

    private BarChart distribution;

    private int numOfRolls = 1000;
    private int diceNum = 2;
    private int[] data;

    @Override
    public void start(Stage window) {
        rollDice();
        drawGraph();

        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);
        root.getChildren().add(distribution);

        Scene scene = new Scene(root, 800, 500);

        window.setTitle("Dice Roll Distribution");
        window.setScene(scene);
        window.show();
    }

    private void rollDice() {
        data = new int[diceNum * 6];

        for (int i = 0; i < numOfRolls; i++) {
            int total = 0;
            for (int j = 0; j < diceNum; j++) {
                total += ThreadLocalRandom.current().nextInt(1, 7);
            }

            System.out.println(total);
            data[total - 1] += 1;
        }

    }

    @SuppressWarnings("unchecked")
    private void drawGraph() {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Roll Result");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Frequency");

        XYChart.Series dataSeries = new XYChart.Series();

        for (int i = 0; i < data.length; i++) {
            dataSeries.getData().add(new XYChart.Data<>("" + (i + 1), data[i]));
        }

        distribution = new BarChart<>(xAxis, yAxis);
        distribution.getData().add(dataSeries);
    }

    public static void main(String[] args) {
        launch(args);
    }

}
