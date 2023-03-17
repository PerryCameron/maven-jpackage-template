package com.changenode;

import com.changenode.FxInterface.Log;
import com.changenode.widgetfx.ButtonWidgets;
import com.changenode.widgetfx.FileChooserWidgets;
import com.changenode.widgetfx.MenuWidgets;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
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
import java.util.function.Consumer;

import static java.awt.Desktop.getDesktop;
import static java.awt.Taskbar.getTaskbar;
import static java.awt.Taskbar.isTaskbarSupported;
import static java.lang.System.getProperty;
import static java.lang.System.out;
import static java.util.Calendar.getInstance;

public class ViewBuilder implements Builder<Region> {
    private final Model model;
    private final Consumer<Void> attention;

    public ViewBuilder(Model model, Consumer<Void> attention) {
        this.model = model;
        this.attention = attention;
    }

    @Override
    public Region build() {
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(setUpCenterTextArea());
        borderPane.setTop(setUpToolBars());
        borderPane.setBottom(setUpStatusBar());
        return borderPane;
    }

    private Node setUpCenterTextArea() {
        TextArea textArea = new TextArea();
        textArea.setWrapText(true);
        textArea.textProperty().bind(model.mainTextProperty());
        log(Log.LoggingType.BOTH,"Try dragging one or more files and/or directories here from another application.");
        textArea.setOnDragOver(event -> handleDragOver(textArea, event));
        textArea.setOnDragEntered(event -> handleDragEntered(textArea));
        textArea.setOnDragExited(event -> handleDragExited(textArea));
        textArea.setOnDragDropped(this::handleDragDropped);
        return textArea;
    }

    private Node setUpStatusBar() {
        Label statusLabel = new Label();
        statusLabel.setPadding(new Insets(5.0f, 5.0f, 5.0f, 5.0f));
        statusLabel.setMaxWidth(Double.MAX_VALUE);
        statusLabel.textProperty().bind(model.statusLabelProperty());
        return statusLabel;
    }

    private Node setUpToolBars() {
        VBox topElements = new VBox();
        topElements.getChildren().add(setUpMenuBar());
        ToolBar toolbar = new ToolBar();
        log(Log.LoggingType.STATUS_BAR,"Ready.");
        Button helloWorld = ButtonWidgets.menuButtonOf("Hello World",false);
        helloWorld.setOnAction(event -> log(Log.LoggingType.BOTH,"Hello World! " + java.util.Calendar.getInstance().getTime()));
        toolbar.getItems().addAll(createToggleButton(),helloWorld);
        topElements.getChildren().add(toolbar);
        return topElements;
    }

    private Node createToggleButton() {
        ToggleButton toggleDark = ButtonWidgets.nftToggleButtonOf("Dark", model.isDarkProperty());
        toggleDark.textProperty().bind(Bindings.createStringBinding(() ->
                toggleDark.selectedProperty().get() ? "Light" : "Dark", toggleDark.selectedProperty()));
        return toggleDark;
    }

    private Node setUpMenuBar() {
        MenuBar menuBar = new MenuBar();
        if(isMac()) menuBar.setUseSystemMenuBar(true);
        menuBar.getMenus().addAll(createFileMenu(),createEditMenu(),createIntegrationMenu(),createDebugMenu());
        return menuBar;
    }

    private Menu createDebugMenu() {
        Menu menu = new Menu("Debug");
        MenuItem findDebugLog = new MenuItem("Find Debug Log");
        findDebugLog.setOnAction(e -> getDesktop().browseFileDirectory(Fetcher.outputFile));
        MenuItem writeHelloWorldToLog = new MenuItem("Write Hello World to Log");
        writeHelloWorldToLog.setOnAction(e -> out.println("Hello World! " + getInstance().getTime()));
        menu.getItems().addAll(findDebugLog, writeHelloWorldToLog);
        return menu;
    }

    private Menu createFileMenu() {
        Menu menu = new Menu("File");
        MenuItem newFile = MenuWidgets.menuItemOf("New", x -> log(Log.LoggingType.BOTH,"File -> New"), KeyCode.N);
        MenuItem open = MenuWidgets.menuItemOf("Open...", x -> handleFileDialogue(), KeyCode.O);
        menu.getItems().addAll(newFile, open);
        if (!isMac()) menu.getItems().add(MenuWidgets.menuItemOf("Quit", x -> Platform.exit(), KeyCode.Q));
        return menu;
    }

    private Menu createEditMenu() {
        Menu menu = new Menu("Edit");
        MenuItem undo = MenuWidgets.menuItemOf("Undo", x -> log(Log.LoggingType.BOTH,"Undo"), KeyCode.Z);
        MenuItem redo = MenuWidgets.menuItemOf("Redo", x -> log(Log.LoggingType.BOTH,"Redo"), KeyCode.R);
        SeparatorMenuItem editSeparator = new SeparatorMenuItem();
        MenuItem cut = MenuWidgets.menuItemOf("Cut", x -> log(Log.LoggingType.BOTH,"Cut"), KeyCode.X);
        MenuItem copy = MenuWidgets.menuItemOf("Copy", x -> log(Log.LoggingType.BOTH,"Copy"), KeyCode.C);
        MenuItem paste = MenuWidgets.menuItemOf("Paste", x -> log(Log.LoggingType.BOTH,"Paste"), KeyCode.V);
        menu.getItems().addAll(undo, redo, editSeparator, cut, copy, paste);
        return menu;
    }

    private Menu createIntegrationMenu() {
        Menu menu = new Menu("Desktop");
        if (!isTaskbarSupported()) return menu;
        log(Log.LoggingType.BOTH,"");
        log(Log.LoggingType.BOTH,"Desktop integration flags for this platform include:");
        printTaskBarFeatures();
        setImagesToModel();
        MenuItem useCustomIcon = MenuWidgets.menuItemOf("Use Custom App Icon", x -> getTaskbar().setIconImage(model.redCircleIconProperty().get()), null);
        MenuItem useDefaultAppIcon = MenuWidgets.menuItemOf("Use Default App Icon", x -> getTaskbar().setIconImage(model.defaultIconProperty().get()), null);
        useCustomIcon.setDisable(!getTaskbar().isSupported(Taskbar.Feature.ICON_IMAGE));
        useDefaultAppIcon.setDisable(!getTaskbar().isSupported(Taskbar.Feature.ICON_IMAGE));
        MenuItem setIconBadge = MenuWidgets.menuItemOf("Set Badge", x -> getTaskbar().setIconBadge("1"), null);
        MenuItem removeIconBadge = MenuWidgets.menuItemOf("Remove Badge", x -> getTaskbar().setIconBadge(""), null);
        setIconBadge.setDisable(!getTaskbar().isSupported(Taskbar.Feature.ICON_BADGE_TEXT));
        removeIconBadge.setDisable(!getTaskbar().isSupported(Taskbar.Feature.ICON_BADGE_TEXT));
        MenuItem addProgress = MenuWidgets.menuItemOf("Add Icon Progress", x -> { addProgress(); }, KeyCode.R);
        MenuItem clearProgress = MenuWidgets.menuItemOf("Clear Icon Progress", x -> { clearProgress(); }, null);
        addProgress.setDisable(!getTaskbar().isSupported(Taskbar.Feature.PROGRESS_VALUE));
        clearProgress.setDisable(!getTaskbar().isSupported(Taskbar.Feature.PROGRESS_VALUE));
        MenuItem requestUserAttention = MenuWidgets.menuItemOf("Request User Attention (5s)", x -> attention.accept(null), null);
        requestUserAttention.setDisable(!getTaskbar().isSupported(Taskbar.Feature.USER_ATTENTION));
        menu.getItems().addAll(setIconBadge, removeIconBadge, addProgress, clearProgress, useCustomIcon, useDefaultAppIcon, requestUserAttention);
        return menu;
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
            log(Log.LoggingType.BOTH ," " + feature.name() + " " + getTaskbar().isSupported(feature));
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
        db.getFiles().stream().map(File::toPath).forEach(path -> log(Log.LoggingType.BOTH, path.toAbsolutePath().toString()));
        event.setDropCompleted(success);
        event.consume();
    }

    private void handleFileDialogue() {
        FileChooser fileChooser = FileChooserWidgets.fileChooseOf("Open File","user.home");
        File file = fileChooser.showOpenDialog(BaseApplication.getMainStage());
        if (file != null) {
            log(Log.LoggingType.BOTH,file.getAbsolutePath());
        } else {
            log(Log.LoggingType.BOTH,"Open File cancelled.");
        }
    }

    private void log(Log.LoggingType type, String message) {
        model.setLogData(new LogData(type, message));
    } //        logger.log(type,mess);

}
