package br.com.jonyfs;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.channels.FileLock;
import java.util.Properties;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

@Slf4j
@SpringBootApplication
public class SpringBootDesktopApplication extends Application {

    public static final String APPLICATION_NAME = "Spring Boot Desktop Demo";

    final Properties properties = new Properties();

    // one icon location is shared between the application tray icon and task bar icon.
    // you could also use multiple icons to allow for clean display of tray icons on hi-dpi devices.
    private static final String iconImageLoc
            = "http://icons.iconarchive.com/icons/custom-icon-design/flatastic-11/16/Laptop-wifi-icon.png";

    private static final String iconImageConfiguracao
            = "http://icons.iconarchive.com/icons/seanau/server/32/Server-setting-icon.png";

    // application stage is stored so that it can be shown and hidden based on system tray icon operations.
    private Stage stage;

    public static ConfigurableApplicationContext applicationContext;

    @Override
    public void start(Stage stage) throws Exception {
        testingDirectory();
        if (!isFileshipAlreadyRunning()) {
            LOGGER.warn(APPLICATION_NAME + " is already running!");
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(APPLICATION_NAME);
            alert.setHeaderText("Attention!");
            alert.setContentText(APPLICATION_NAME + " is already running!");
            alert.showAndWait();
            Platform.exit();
            System.exit(0);
        }

        LOGGER.info(APPLICATION_NAME + " is starting...");
        // stores a reference to the stage.
        this.stage = stage;

        // instructs the javafx system not to exit implicitly when the last application window is shut.
        Platform.setImplicitExit(false);

        javax.swing.SwingUtilities.invokeLater(this::addAppToTray);
        LOGGER.info(APPLICATION_NAME + " started.");
    }


    public void openRootUrl() {
        open("/");
    }

    public void openAPIUrl() {
        open("/api");
    }

    public void open(String path) {

        try {
            Environment environment = applicationContext.getBean(Environment.class);
            openUrl("http://" + InetAddress.getLocalHost().getHostAddress() + ":" + environment.getProperty("local.server.port") + path);
        } catch (UnknownHostException ex) {
            LOGGER.error("Fail to open path {}", path, ex);
        }
    }

    public void openUrl(String url) {
        try {
            if (Desktop.isDesktopSupported()) {
                LOGGER.info("Opening {}...", url);
                Desktop.getDesktop().browse(new URI(url));
            }
        } catch (IOException | URISyntaxException ex) {
            LOGGER.error("Fail to open url {}", url, ex);
        }
    }


    private void addAppToTray() {
        try {
            // ensure awt toolkit is initialized.
            java.awt.Toolkit.getDefaultToolkit();

            // app requires system tray support, just exit if there is no support.
            if (!java.awt.SystemTray.isSupported()) {
                System.out.println("No system tray support, application exiting.");
                Platform.exit();
            }

            // set up a system tray icon.
            java.awt.SystemTray tray = java.awt.SystemTray.getSystemTray();
            URL imageLoc = new URL(
                    iconImageLoc
            );
            java.awt.Image image = ImageIO.read(imageLoc);
            java.awt.TrayIcon trayIcon = new java.awt.TrayIcon(image);

            trayIcon.setToolTip(APPLICATION_NAME);

            trayIcon.addActionListener(event -> Platform.runLater(this::showStage));

            java.awt.MenuItem openRootUrl = new java.awt.MenuItem("Open");
            openRootUrl.addActionListener(event -> Platform.runLater(this::openRootUrl));

            java.awt.MenuItem openApiUrl = new java.awt.MenuItem("API Rest Browser");
            openApiUrl.addActionListener(event -> Platform.runLater(this::openAPIUrl));

            java.awt.MenuItem exitItem = new java.awt.MenuItem("Exit");
            exitItem.addActionListener(event -> {
                //notificationTimer.cancel();
                ((ConfigurableApplicationContext) applicationContext).registerShutdownHook();
                Platform.exit();
                tray.remove(trayIcon);
                System.exit(0);
            });

            // setup the popup menu for the application.
            final java.awt.PopupMenu popup = new java.awt.PopupMenu();
            popup.add(openRootUrl);
            popup.add(openApiUrl);
            popup.addSeparator();
            popup.add(exitItem);
            trayIcon.setPopupMenu(popup);

            // add the application tray icon to the system tray.
            tray.add(trayIcon);
        } catch (java.awt.AWTException | IOException ex) {
            LOGGER.error("Fail!", ex);
        }
    }

    /**
     * Shows the application stage and ensures that it is brought ot the front
     * of all stages.
     */
    private void showStage() {
        if (stage != null) {
            stage.show();
            stage.toFront();
        }
    }

    private static boolean isFileshipAlreadyRunning() {
        // socket concept is shown at http://www.rbgrn.net/content/43-java-single-application-instance
        // but this one is really great
        try {
            final File file = new File(System.getProperty("user.home") + "/.spring-boot-desktop/spring-boot-desktop.lck");
            final RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            final FileLock fileLock = randomAccessFile.getChannel().tryLock();
            if (fileLock != null) {
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    @Override
                    public void run() {
                        try {
                            LOGGER.info("releasing locking file {}: ", file);
                            fileLock.release();
                            randomAccessFile.close();
                            file.delete();
                            LOGGER.info("locking file {} released", file);
                        } catch (IOException e) {
                            LOGGER.error("Unable to remove lock file: " + file, e);
                        }
                    }
                });
                return true;
            }
        } catch (IOException e) {
            LOGGER.error("Unable to remove lock file", e);
        }
        return false;
    }

    private void testingDirectory() {
        File diretorio = new File(System.getProperty("user.home") + "/.spring-boot-desktop");
        if (!diretorio.exists()) {
            LOGGER.info("Creating folder {}", diretorio.getAbsolutePath());
            diretorio.mkdir();
        }

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        LOGGER.info("Starting Spring Boot...");
        applicationContext = new SpringApplicationBuilder(SpringBootDesktopApplication.class)
                .headless(false).run(args);
        LOGGER.info("Spring Boot started.");
        launch(args);

    }

}
