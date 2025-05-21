package prlbo.project.rpl.Controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ComboBox;

import java.util.ArrayList;
import java.util.List;

public class PieChartController {

    private int idacc;

    void setIdacc(int id) { this.idacc = id; }

    @FXML
    private ComboBox<String> cmbox;

    @FXML
    private PieChart piechart;


    @FXML
    void initialize() {

        cmbox.getItems().addAll(
                "All Task Based On Category",
                "Completion Status",
                "Completed Task Based On Category"
        );


        cmbox.setOnAction(e -> {
            piechart.getData().clear();

            DatabaseController db;
            try {
                db = new DatabaseController();
            } catch (Exception ex) {
                db = null;
            }

            String selected = cmbox.getValue();

            System.out.println("now selecting" + selected);

            switch (selected) {
                case "Completion Status":
                    int completed = db.getJumlahTask(idacc, "tugas_selesai");
                    int notcompleted = db.getJumlahTask(idacc, "tugas_tidak_selesai");

                    piechart.getData().add(new PieChart.Data("Completed", completed));
                    piechart.getData().add(new PieChart.Data("Not Completed", notcompleted));
                    break;

                case "Completed Task Based On Category":
                    ObservableList<PieChart.Data> pie = db.getCompletedTaskBasedOnCategory(idacc);

                    for (PieChart.Data p : pie) {
                        piechart.getData().add(p);
                    }
                    break;

                case "All Task Based On Category":
                    ObservableList<PieChart.Data> piex = db.getAllTaskBasedOnCategory(idacc);

                    for (PieChart.Data p : piex) {
                        piechart.getData().add(p);
                    }
                    break;
            }
        });

    }


}