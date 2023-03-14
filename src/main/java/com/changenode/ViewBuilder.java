package com.changenode;

import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;
import com.changenode.widgetfx.ButtonWidgets;
import com.changenode.widgetfx.MenuWidgets;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.util.Builder;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static java.awt.Desktop.getDesktop;
import static java.awt.Taskbar.getTaskbar;
import static java.awt.Taskbar.isTaskbarSupported;
import static java.lang.System.getProperty;
import static java.lang.System.out;
import static java.util.Calendar.getInstance;

public class ViewBuilder implements Builder<Region> {
    private final Model model;
    private final BiConsumer<String,String> log;
    private final Consumer<Void> attention;
    public ViewBuilder(Model model, BiConsumer<String,String> log, Consumer<Void> attention) {
        this.model = model;
        this.log = log;
        this.attention = attention;
    }

    @Override
    public Region build() {
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(setUpCenter());
        borderPane.setTop(setUpTop());
        borderPane.setBottom(setUpBottom());
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
        return borderPane;
    }

    private Node setUpCenter() {
        TextArea textArea = new TextArea();
        textArea.setWrapText(true);
        textArea.textProperty().bind(model.mainTextProperty());
        log.accept("both","Try dragging one or more files and/or directories here from another application.");
        textArea.setOnDragOver(event -> handleDragOver(textArea, event));
        textArea.setOnDragEntered(event -> handleDragEntered(textArea));
        textArea.setOnDragExited(event -> handleDragExited(textArea));
        textArea.setOnDragDropped(this::handleDragDropped);
        return textArea;
    }

    private Node setUpBottom() {
        Label statusLabel = new Label();
        statusLabel.setPadding(new Insets(5.0f, 5.0f, 5.0f, 5.0f));
        statusLabel.setMaxWidth(Double.MAX_VALUE);
        statusLabel.textProperty().bind(model.statusLabelProperty());
        return statusLabel;
    }

    private Node setUpTop() {
        VBox topElements = new VBox();
        MenuBar menuBar = new MenuBar();
        topElements.getChildren().add(menuBar);
        ToolBar toolbar = new ToolBar();
        createFileMenu(menuBar);
        createEditMenu(menuBar);
        createIntegrationMenu(menuBar);
        createDebugMenu(menuBar);
        log.accept("label","Ready.");
        Button toggleDark = ButtonWidgets.boundDarkButton(model.darkProperty());
        toggleDark.setOnAction(event -> toggleDark(toggleDark,model.darkProperty()));
        Button helloWorld = ButtonWidgets.helloWorldButton();
        helloWorld.setOnAction(event -> log.accept("both","Hello World! " + java.util.Calendar.getInstance().getTime()));
        toolbar.getItems().addAll(toggleDark,helloWorld);
        topElements.getChildren().add(toolbar);
        return topElements;
    }

    private void createDebugMenu(MenuBar menuBar) {
        Menu menu = new Menu("Debug");
        MenuItem findDebugLog = new MenuItem("Find Debug Log");
        findDebugLog.setOnAction(e -> getDesktop().browseFileDirectory(Fetcher.outputFile));
        MenuItem writeHelloWorldToLog = new MenuItem("Write Hello World to Log");
        writeHelloWorldToLog.setOnAction(e -> out.println("Hello World! " + getInstance().getTime()));
        menu.getItems().addAll(findDebugLog, writeHelloWorldToLog);
        menuBar.getMenus().add(menu);
    }

    private void createFileMenu(MenuBar menuBar) {
        Menu file = new Menu("File");
        MenuItem newFile = MenuWidgets.Configure("New", x -> log.accept("","File -> New"), KeyCode.N);
        MenuItem open = MenuWidgets.Configure("Open...", x -> openFileDialog(), KeyCode.O);
        file.getItems().addAll(newFile, open);
        if (!isMac()) {
            MenuItem quit = MenuWidgets.Configure("Quit", x -> Platform.exit(), KeyCode.Q);
            file.getItems().add(quit);
        } else menuBar.setUseSystemMenuBar(true);
        menuBar.getMenus().addAll(file);
    }

    private void createEditMenu(MenuBar menuBar) {
        Menu edit = new Menu("Edit");
        MenuItem undo = MenuWidgets.Configure("Undo", x -> log.accept("","Undo"), KeyCode.Z);
        MenuItem redo = MenuWidgets.Configure("Redo", x -> log.accept("","Redo"), KeyCode.R);
        SeparatorMenuItem editSeparator = new SeparatorMenuItem();
        MenuItem cut = MenuWidgets.Configure("Cut", x -> log.accept("","Cut"), KeyCode.X);
        MenuItem copy = MenuWidgets.Configure("Copy", x -> log.accept("","Copy"), KeyCode.C);
        MenuItem paste = MenuWidgets.Configure("Paste", x -> log.accept("","Paste"), KeyCode.V);
        edit.getItems().addAll(undo, redo, editSeparator, cut, copy, paste);
        menuBar.getMenus().addAll(edit);
    }

    private void createIntegrationMenu(MenuBar menuBar) {
        if (!isTaskbarSupported()) return;
        log.accept("both","");
        log.accept("both","Desktop integration flags for this platform include:");
        printTaskBarFeatures();
        setImagesToModel();
        MenuItem useCustomIcon = MenuWidgets.Configure("Use Custom App Icon", x -> getTaskbar().setIconImage(model.redCircleIconProperty().get()), null);
        MenuItem useDefaultAppIcon = MenuWidgets.Configure("Use Default App Icon", x -> getTaskbar().setIconImage(model.defaultIconProperty().get()), null);
        useCustomIcon.setDisable(!getTaskbar().isSupported(Taskbar.Feature.ICON_IMAGE));
        useDefaultAppIcon.setDisable(!getTaskbar().isSupported(Taskbar.Feature.ICON_IMAGE));
        Menu desktopIntegration = new Menu("Desktop");
        MenuItem setIconBadge = MenuWidgets.Configure("Set Badge", x -> getTaskbar().setIconBadge("1"), null);
        MenuItem removeIconBadge = MenuWidgets.Configure("Remove Badge", x -> getTaskbar().setIconBadge(""), null);
        setIconBadge.setDisable(!getTaskbar().isSupported(Taskbar.Feature.ICON_BADGE_TEXT));
        removeIconBadge.setDisable(!getTaskbar().isSupported(Taskbar.Feature.ICON_BADGE_TEXT));
        MenuItem addProgress = MenuWidgets.Configure("Add Icon Progress", x -> { addProgress(); }, KeyCode.R);
        MenuItem clearProgress = MenuWidgets.Configure("Clear Icon Progress", x -> { clearProgress(); }, null);
        addProgress.setDisable(!getTaskbar().isSupported(Taskbar.Feature.PROGRESS_VALUE));
        clearProgress.setDisable(!getTaskbar().isSupported(Taskbar.Feature.PROGRESS_VALUE));
        MenuItem requestUserAttention = MenuWidgets.Configure("Request User Attention (5s)", x -> attention.accept(null), null);
        requestUserAttention.setDisable(!getTaskbar().isSupported(Taskbar.Feature.USER_ATTENTION));
        desktopIntegration.getItems().addAll(setIconBadge, removeIconBadge, addProgress, clearProgress, useCustomIcon, useDefaultAppIcon, requestUserAttention);
        menuBar.getMenus().add(desktopIntegration);
    }

    private void clearProgress() {
        model.setCurrentIconProgress(-1);
        model.currentIconProgressProperty().setValue(model.currentIconProgressProperty().getValue() + 1);
        getTaskbar().setProgressValue(model.currentIconProgressProperty().getValue());
    }

    private void addProgress() {
        int newValue = model.currentIconProgressProperty().get() + 1;
        model.currentIconProgressProperty().set(newValue);
        if (getTaskbar().isSupported(Taskbar.Feature.PROGRESS_VALUE)) {
            getTaskbar().setProgressValue(newValue);
            out.println("Progress bar is supported");
        }
    }

    private void printTaskBarFeatures() {
        for (Taskbar.Feature feature : Taskbar.Feature.values()) {
            log.accept("both" ," " + feature.name() + " " + getTaskbar().isSupported(feature));
        }
    }

    private void setImagesToModel() {
        if (getTaskbar().isSupported(Taskbar.Feature.ICON_IMAGE)) {
            model.setDefaultIcon(setDefaultIcon());
            BufferedImage bufferedImage = setCustomIcon();
            model.redCircleIconProperty().set(bufferedImage);
        }
    }

    public static boolean isMac() {
        return getProperty("os.name").contains("Mac");
    }

    private static BufferedImage setCustomIcon() {
        BufferedImage bufferedImage = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = bufferedImage.createGraphics();
        graphics2D.setColor(java.awt.Color.red);
        graphics2D.fillOval(0, 0, 256, 256);
        graphics2D.dispose();
        return bufferedImage;
    }

    private static BufferedImage setDefaultIcon() {
        Image awtImage = getTaskbar().getIconImage();
        BufferedImage bufferedImage;
        if (awtImage instanceof BufferedImage) {
            bufferedImage = (BufferedImage) awtImage;
        } else {
            int width = awtImage.getWidth(null);
            int height = awtImage.getHeight(null);
            bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = bufferedImage.createGraphics();
            g.drawImage(awtImage, 0, 0, null);
            g.dispose();
        }
        return bufferedImage;
    }

    private void toggleDark(Button toggleDark, BooleanProperty darkProperty) {
        System.out.println("did this");
        if (darkProperty.get()) {
            // This is how to set a light style w/the default JavaFX CSS
            // scene.getRoot().setStyle("");
            Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
            toggleDark.setText("Light");
        } else {
            // This is how to set a dark style w/the default JavaFX CSS.
            // scene.getRoot().setStyle("-fx-base:#25292D;");
            Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
            toggleDark.setText("Dark");
        }
        darkProperty.set(!darkProperty.get());
    }

    private void handleDragOver(TextArea textArea, DragEvent event) {
        if (event.getGestureSource() != textArea && event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }
        event.consume();
    }

    private void handleDragEntered(TextArea textArea) {
        textArea.setBackground(new Background(new BackgroundFill(Color.CORNFLOWERBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
    }

    private void handleDragExited(TextArea textArea) {
        textArea.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
    }

    private void handleDragDropped(DragEvent event) {
        Dragboard db = event.getDragboard();
        boolean success = false;
        if (db.hasFiles()) {
            for (File file : db.getFiles()) {
                log.accept("both", file.getAbsolutePath());
            }
            success = true;
        }
        /* let the source know whether the information was successfully transferred and used */
        event.setDropCompleted(success);
        event.consume();
    }

    private void openFileDialog() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        File file = fileChooser.showOpenDialog(BaseApplication.getMainStage());
        if (file != null) {
            log.accept("both",file.getAbsolutePath());
        } else {
            log.accept("both","Open File cancelled.");
        }
    }
}
