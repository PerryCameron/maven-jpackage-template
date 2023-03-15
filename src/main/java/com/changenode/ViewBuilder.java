package com.changenode;

import atlantafx.base.theme.PrimerLight;
import com.changenode.FxInterface.LogConstants;
import com.changenode.widgetfx.ButtonWidgets;
import com.changenode.widgetfx.MenuWidgets;
import javafx.application.Application;
import javafx.application.Platform;
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

public class ViewBuilder implements Builder<Region>, LogConstants {
    private final Model model;
    private final BiConsumer<Integer,String> log;
    private final Consumer<Void> attention;
    private final Consumer<Boolean> isDark;

    public ViewBuilder(Model model, BiConsumer<Integer,String> log, Consumer<Void> attention, Consumer<Boolean> isDark) {
        this.model = model;
        this.log = log;
        this.attention = attention;
        this.isDark = isDark;
    }

    @Override
    public Region build() {
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(setUpCenterTextArea());
        borderPane.setTop(setUpToolBars());
        borderPane.setBottom(setUpStatusBar());
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
        return borderPane;
    }

    private Node setUpCenterTextArea() {
        TextArea textArea = new TextArea();
        textArea.setWrapText(true);
        textArea.textProperty().bind(model.mainTextProperty());
        log.accept(TO_TEXT_AND_LABEL,"Try dragging one or more files and/or directories here from another application.");
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
        log.accept(TO_LABEL,"Ready.");
        out.println("Created toggle button");
        Button helloWorld = ButtonWidgets.helloWorldButton();
        helloWorld.setOnAction(event -> log.accept(TO_TEXT_AND_LABEL,"Hello World! " + java.util.Calendar.getInstance().getTime()));
        toolbar.getItems().addAll(createToggleButton(),helloWorld);
        topElements.getChildren().add(toolbar);
        return topElements;
    }

    private Node createToggleButton() {
        ToggleButton toggleDark = ButtonWidgets.createDarkButton();
        model.isDarkProperty().bindBidirectional(toggleDark.selectedProperty());
        model.isDarkProperty().addListener((observable, oldValue, isDark) -> {
            if(isDark) toggleDark.setText("Light");
            else toggleDark.setText("Dark");
            triggerCssChange(isDark);
            out.println(isDark);
        });
        return toggleDark;
    }

    private void triggerCssChange(boolean change) {
        isDark.accept(change);
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
        MenuItem newFile = MenuWidgets.menuItemOf("New", x -> log.accept(TO_TEXT_AND_LABEL,"File -> New"), KeyCode.N);
        MenuItem open = MenuWidgets.menuItemOf("Open...", x -> openFileDialog(), KeyCode.O);
        menu.getItems().addAll(newFile, open);
        if (!isMac()) {
            MenuItem quit = MenuWidgets.menuItemOf("Quit", x -> Platform.exit(), KeyCode.Q);
            menu.getItems().add(quit);
        }
        return menu;
    }

    private Menu createEditMenu() {
        Menu menu = new Menu("Edit");
        MenuItem undo = MenuWidgets.menuItemOf("Undo", x -> log.accept(TO_TEXT_AND_LABEL,"Undo"), KeyCode.Z);
        MenuItem redo = MenuWidgets.menuItemOf("Redo", x -> log.accept(TO_TEXT_AND_LABEL,"Redo"), KeyCode.R);
        SeparatorMenuItem editSeparator = new SeparatorMenuItem();
        MenuItem cut = MenuWidgets.menuItemOf("Cut", x -> log.accept(TO_TEXT_AND_LABEL,"Cut"), KeyCode.X);
        MenuItem copy = MenuWidgets.menuItemOf("Copy", x -> log.accept(TO_TEXT_AND_LABEL,"Copy"), KeyCode.C);
        MenuItem paste = MenuWidgets.menuItemOf("Paste", x -> log.accept(TO_TEXT_AND_LABEL,"Paste"), KeyCode.V);
        menu.getItems().addAll(undo, redo, editSeparator, cut, copy, paste);
        return menu;
    }

    private Menu createIntegrationMenu() {
        Menu menu = new Menu("Desktop");
        if (!isTaskbarSupported()) return menu;
        log.accept(TO_TEXT_AND_LABEL,"");
        log.accept(TO_TEXT_AND_LABEL,"Desktop integration flags for this platform include:");
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
            log.accept(TO_TEXT_AND_LABEL ," " + feature.name() + " " + getTaskbar().isSupported(feature));
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
        if (db.hasFiles()) {
            for (File file : db.getFiles()) {
                log.accept(TO_TEXT_AND_LABEL, file.getAbsolutePath());
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
            log.accept(TO_TEXT_AND_LABEL,file.getAbsolutePath());
        } else {
            log.accept(TO_TEXT_AND_LABEL,"Open File cancelled.");
        }
    }
}
