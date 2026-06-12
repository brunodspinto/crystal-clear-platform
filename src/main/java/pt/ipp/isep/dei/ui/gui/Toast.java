package pt.ipp.isep.dei.ui.gui;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Window;
import javafx.util.Duration;

/**
 * Small notification popup ("toast") that slides up in the bottom-right corner
 * of the window, stays for a few seconds and fades out. It can also be closed
 * immediately with the ✕ button. Used to greet a newly approved user on their
 * first login (US02).
 */
public final class Toast {

    private static final double WIDTH = 320;
    private static final double MARGIN = 24;

    private Toast() {}

    /**
     * Shows a toast anchored to the bottom-right corner of the given window.
     *
     * @param owner   the window the toast is attached to
     * @param title   the bold title line
     * @param message the message line
     */
    public static void show(Window owner, String title, String message) {
        if (owner == null) {
            return;
        }

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: white;");

        Button close = new Button("✕");
        close.setStyle("-fx-background-color: transparent; -fx-text-fill: #d7e6f2; "
                + "-fx-font-size: 12px; -fx-cursor: hand; -fx-padding: 0 2 0 8;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        HBox header = new HBox(titleLabel, spacer, close);
        header.setAlignment(Pos.CENTER_LEFT);

        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true);
        messageLabel.setStyle("-fx-text-fill: #eaf3fa; -fx-font-size: 11.5px;");

        VBox card = new VBox(6, header, messageLabel);
        card.setPrefWidth(WIDTH);
        card.setMaxWidth(WIDTH);
        card.setStyle("-fx-background-color: linear-gradient(to bottom right, #1f6f3f, #2e9e57); "
                + "-fx-background-radius: 8; -fx-padding: 14 16 14 16; "
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.35), 12, 0.2, 0, 4);");

        Popup popup = new Popup();
        popup.setAutoFix(true);
        popup.getContent().add(card);

        close.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                popup.hide();
            }
        });

        popup.show(owner);
        repositionBottomRight(popup, owner);

        // Slide up + fade in, hold, then fade out.
        card.setTranslateY(30);
        card.setOpacity(0);

        TranslateTransition slideIn = new TranslateTransition(Duration.millis(350), card);
        slideIn.setFromY(30);
        slideIn.setToY(0);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(350), card);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        PauseTransition hold = new PauseTransition(Duration.seconds(4));

        FadeTransition fadeOut = new FadeTransition(Duration.millis(400), card);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        slideIn.play();
        SequentialTransition timeline = new SequentialTransition(fadeIn, hold, fadeOut);
        timeline.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                popup.hide();
            }
        });
        timeline.play();
    }

    private static void repositionBottomRight(Popup popup, Window owner) {
        double x = owner.getX() + owner.getWidth() - WIDTH - MARGIN;
        double y = owner.getY() + owner.getHeight() - popup.getHeight() - MARGIN;
        popup.setX(x);
        popup.setY(y);
    }
}
