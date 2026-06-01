package areda.view.components;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;

public class AvatarComponent extends StackPane {
    public AvatarComponent(String name, double size) {
        this.setPrefSize(size, size);
        this.setMaxSize(size, size);
        this.setMinSize(size, size);
        this.setAlignment(Pos.CENTER);

        Circle circle = new Circle(size / 2);
        circle.getStyleClass().add("avatar-circle");

        String initials = getInitials(name);
        Label label = new Label(initials);
        label.getStyleClass().add("avatar-text");
        label.setStyle("-fx-font-size: " + (size * 0.4) + "px;");

        this.getChildren().addAll(circle, label);
    }

    private String getInitials(String name) {
        if (name == null || name.trim().isEmpty())
            return "?";
        String[] parts = name.trim().split("\\s+");
        if (parts.length >= 2) {
            return (parts[0].substring(0, 1) + parts[1].substring(0, 1)).toUpperCase();
        }
        return name.substring(0, Math.min(2, name.length())).toUpperCase();
    }
}