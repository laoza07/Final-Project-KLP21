package areda.view.admin;

import areda.model.DatabaseManager;
import areda.model.Lowongan;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class AdminHomeView extends StackPane {
    private VBox mainContent;
    private StackPane overlay;

    public AdminHomeView() {
        this.setStyle("-fx-background-color: white;");
        mainContent = new VBox(30);
        mainContent.setPadding(new Insets(40));
        mainContent.setAlignment(Pos.TOP_LEFT);

        HBox header = createHeader();
        HBox kpiContainer = new HBox(30);
        kpiContainer.setAlignment(Pos.CENTER_LEFT);

        long totalLowongan = DatabaseManager.getTotalLowonganCount();
        long totalPelamar = DatabaseManager.getAllLamaran().size();
        long totalTersedia = DatabaseManager.getLowonganTersediaCount();

        VBox card1 = createKpiCard(String.valueOf(totalLowongan), "Total Lowongan", "TotalLowongan", "#3182CE");
        VBox card2 = createKpiCard(String.valueOf(totalPelamar), "Total Pelamar", "TotalPelamar", "#38A169");
        VBox card3 = createKpiCard(String.valueOf(totalTersedia), "Lowongan Tersedia", "LowonganTersedia", "#D69E2E");

        kpiContainer.getChildren().addAll(card1, card2, card3);
        mainContent.getChildren().addAll(header, kpiContainer);

        overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0,0,0,0.4);");
        overlay.setVisible(false);
        this.getChildren().addAll(mainContent, overlay);
    }

    private HBox createHeader() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Beranda");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        title.setTextFill(Color.web("#1A202C"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // LONCENG DIHAPUS - hanya logo saja
        StackPane miniLogo = new StackPane();
        miniLogo.setPrefSize(35, 35);
        miniLogo.setStyle(
                "-fx-background-color: white; -fx-background-radius: 17; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 1);");

        Label letterA = new Label("A");
        letterA.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        letterA.setTextFill(Color.web("#2D3748"));

        Circle dot = new Circle(2, Color.web("#A0522D"));
        StackPane.setAlignment(dot, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(dot, new Insets(0, 2, 4, 0));

        miniLogo.getChildren().addAll(letterA, dot);
        header.getChildren().addAll(title, spacer, miniLogo);
        return header;
    }

    private VBox createKpiCard(String number, String labelText, String cardType, String colorHex) {
        VBox card = new VBox(15);
        card.setPadding(new Insets(25));
        card.setPrefSize(220, 150);
        card.setStyle(
                "-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5); -fx-cursor: hand;");

        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label badge = new Label("📊");
        badge.setStyle("-fx-background-color: #EBF8FF; -fx-text-fill: " + colorHex
                + "; -fx-padding: 8; -fx-background-radius: 8;");
        badge.setTextFill(Color.web(colorHex));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label arrow = new Label("→");
        arrow.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        arrow.setTextFill(Color.web("#A0AEC0"));

        topRow.getChildren().addAll(badge, spacer, arrow);

        VBox dataRow = new VBox(5);

        Label numLabel = new Label(number);
        numLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        numLabel.setTextFill(Color.web("#1A202C"));

        Label descLabel = new Label(labelText);
        descLabel.setFont(Font.font("Arial", 14));
        descLabel.setTextFill(Color.web("#1A202C"));

        dataRow.getChildren().addAll(numLabel, descLabel);
        card.getChildren().addAll(topRow, dataRow);
        card.setOnMouseClicked(e -> showDetailOverlay(cardType));
        return card;
    }

    private void showDetailOverlay(String cardType) {
        VBox detailBox = new VBox(20);
        detailBox.setMaxSize(700, 500);
        detailBox.setPadding(new Insets(30));
        detailBox.setStyle("-fx-background-color: white; -fx-background-radius: 20;");

        HBox top = new HBox();
        top.setAlignment(Pos.CENTER_RIGHT);

        Button close = new Button("X");
        close.setStyle(
                "-fx-background-color: transparent; -fx-font-weight: bold; -fx-font-size: 16px; -fx-cursor: hand;");
        close.setTextFill(Color.web("#1A202C"));
        close.setOnAction(e -> hideOverlay());
        top.getChildren().add(close);

        Node content = switch (cardType) {
            case "TotalLowongan" -> createTotalLowonganDetail();
            case "TotalPelamar" -> createTotalPelamarDetail();
            case "LowonganTersedia" -> createLowonganTersediaDetail();
            default -> new Label("Unknown Card");
        };

        detailBox.getChildren().addAll(top, content);
        overlay.getChildren().clear();
        overlay.getChildren().add(detailBox);
        overlay.setVisible(true);
        mainContent.setDisable(true);
    }

    private void hideOverlay() {
        overlay.setVisible(false);
        mainContent.setDisable(false);
    }

    private VBox createTotalLowonganDetail() {
        VBox box = new VBox(20);

        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Total Lowongan");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        title.setTextFill(Color.web("#1A202C"));

        Label count = new Label(String.valueOf(DatabaseManager.getTotalLowonganCount()));
        count.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        count.setTextFill(Color.web("#3182CE"));

        header.getChildren().addAll(title, count);

        VBox list = new VBox(15);
        for (Lowongan l : DatabaseManager.getAllLowongan()) {
            String status = l.getSisaSlot() == -1 ? "Unlimited" : "Tersisa " + l.getSisaSlot() + " Slot";
            list.getChildren().add(createDetailCard(l.getJudul(), l.getDivisi(), status));
        }

        ScrollPane scroll = new ScrollPane(list);
        scroll.setFitToWidth(true);
        scroll.setStyle(
                "-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        box.getChildren().addAll(header, scroll);
        return box;
    }

    private VBox createTotalPelamarDetail() {
        VBox box = new VBox(20);

        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Total Pelamar");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        title.setTextFill(Color.web("#1A202C"));

        Label count = new Label(String.valueOf(DatabaseManager.getAllLamaran().size()));
        count.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        count.setTextFill(Color.web("#3182CE"));

        header.getChildren().addAll(title, count);

        VBox table = new VBox();
        table.setStyle(
                "-fx-border-color: #E2E8F0; -fx-border-width: 1; -fx-border-radius: 10; -fx-background-radius: 10;");

        HBox rowHeader = createPelamarRow("No.", "Nama Pelamar", "Divisi", true);
        table.getChildren().add(rowHeader);

        int i = 1;
        for (var lamaran : DatabaseManager.getAllLamaran()) {
            Lowongan l = DatabaseManager.getLowonganById(lamaran.getIdLowongan());
            String divisi = (l != null) ? l.getDivisi() : "Unknown";
            table.getChildren().add(createPelamarRow(String.valueOf(i++), lamaran.getNamaPelamar(), divisi, false));
        }

        if (i == 1)
            table.getChildren().add(createPelamarRow("", "Belum ada data pelamar", "", false));

        ScrollPane scroll = new ScrollPane(table);
        scroll.setFitToWidth(true);
        scroll.setStyle(
                "-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        box.getChildren().addAll(header, scroll);
        return box;
    }

    private VBox createLowonganTersediaDetail() {
        VBox box = new VBox(20);

        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Lowongan Tersedia");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        title.setTextFill(Color.web("#1A202C"));

        Label count = new Label(String.valueOf(DatabaseManager.getLowonganTersediaCount()));
        count.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        count.setTextFill(Color.web("#3182CE"));

        header.getChildren().addAll(title, count);

        VBox list = new VBox(15);
        for (Lowongan l : DatabaseManager.getAllLowongan()) {
            if (l.getSisaSlot() > 0 || l.getSisaSlot() == -1) {
                String status = l.getSisaSlot() == -1 ? "Unlimited" : "Tersisa " + l.getSisaSlot() + " Slot";
                list.getChildren().add(createDetailCard(l.getJudul(), l.getDivisi(), status));
            }
        }

        if (list.getChildren().isEmpty()) {
            Label empty = new Label("Tidak ada lowongan tersedia.");
            empty.setTextFill(Color.web("#1A202C"));
            list.getChildren().add(empty);
        }

        ScrollPane scroll = new ScrollPane(list);
        scroll.setFitToWidth(true);
        scroll.setStyle(
                "-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        box.getChildren().addAll(header, scroll);
        return box;
    }

    private HBox createDetailCard(String titleText, String divText, String statusText) {
        HBox card = new HBox(15);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(15, 20, 15, 20));
        card.setStyle("-fx-background-color: #EBF8FF; -fx-background-radius: 10;");

        VBox left = new VBox(5);

        Label title = new Label(titleText);
        title.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        title.setTextFill(Color.web("#1A202C"));

        Label div = new Label("Divisi: " + divText);
        div.setFont(Font.font(12));
        div.setTextFill(Color.web("#1A202C"));

        left.getChildren().addAll(title, div);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label status = new Label(statusText);
        status.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        status.setTextFill(Color.web("#2B6CB0"));

        card.getChildren().addAll(left, spacer, status);
        return card;
    }

    private HBox createPelamarRow(String no, String name, String div, boolean isHeader) {
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12, 15, 12, 15));

        if (isHeader)
            row.setStyle(
                    "-fx-background-color: #F7FAFC; -fx-border-color: #E2E8F0; -fx-border-width: 0 0 1 0; -fx-background-radius: 10 10 0 0;");
        else
            row.setStyle("-fx-background-color: white; -fx-background-radius: 0 0 10 10;");

        Label lNo = new Label(no);
        lNo.setPrefWidth(40);
        lNo.setTextFill(Color.web("#1A202C"));

        HBox nameBox = new HBox(10);
        nameBox.setAlignment(Pos.CENTER_LEFT);
        nameBox.setPrefWidth(200);

        if (!isHeader) {
            Circle avatar = new Circle(12, Color.web("#48BB78"));
            Label lName = new Label(name);
            lName.setFont(Font.font("Arial", 14));
            lName.setTextFill(Color.web("#1A202C"));
            nameBox.getChildren().addAll(avatar, lName);
        } else {
            Label lName = new Label(name);
            lName.setFont(Font.font("Arial", FontWeight.BOLD, 13));
            lName.setTextFill(Color.web("#1A202C"));
            nameBox.getChildren().add(lName);
        }

        Label lDiv = new Label(div);
        lDiv.setPrefWidth(150);
        lDiv.setTextFill(Color.web("#1A202C"));

        if (isHeader) {
            lNo.setFont(Font.font("Arial", FontWeight.BOLD, 13));
            ((Label) nameBox.getChildren().get(0)).setFont(Font.font("Arial", FontWeight.BOLD, 13));
            lDiv.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        } else {
            lNo.setFont(Font.font("Arial", 14));
            if (nameBox.getChildren().size() > 1)
                ((Label) nameBox.getChildren().get(1)).setFont(Font.font("Arial", 14));
            lDiv.setFont(Font.font("Arial", 14));
        }

        row.getChildren().addAll(lNo, nameBox, lDiv);
        return row;
    }
}