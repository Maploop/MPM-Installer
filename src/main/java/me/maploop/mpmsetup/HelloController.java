package me.maploop.mpmsetup;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import me.maploop.mpmsetup.core.BackEnd;
import me.maploop.mpmsetup.core.Unzipper;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.ResourceBundle;

public class HelloController implements Initializable
{
      @FXML private Button closeButton;
      @FXML private Button buttonInstall;
      @FXML private ProgressBar installationProgress;
      @FXML private Label statusLabel;

      public void onCloseButtonClick() {
            System.exit(0);
      }

      public void onInstallButtonClick() {
            System.out.println("Installation started...");
            buttonInstall.setDisable(true);
            buttonInstall.setText("Starting...");

            System.out.println("Checking for installation path and download file path...");
            if (!new File(BackEnd.getDownloadFolder()).exists()) {
                  new File(BackEnd.getDownloadFolder()).mkdirs();
                  System.out.println("Created temp folder.");
            } else {
                  new File(BackEnd.getDownloadFolder()).delete();
                  System.out.println("Deleted old temp Folder;");
                  new File(BackEnd.getDownloadFolder()).mkdirs();
            }
            if (!new File(BackEnd.getInstallPath()).exists()) {
                  new File(BackEnd.getInstallPath()).mkdirs();
                  System.out.println("Created Main folder.");
            } else {
                  new File(BackEnd.getInstallPath()).delete();
                  System.out.println("Deleted old main Folder;");
                  new File(BackEnd.getInstallPath()).mkdirs();
            }



            new Thread(() -> {
                  String downloadURL = BackEnd.fetchDownloadURL();

                  buttonInstall.setVisible(false);
                  installationProgress.setVisible(true);
                  Platform.runLater(() -> statusLabel.setText("Initializing..."));
                  statusLabel.setVisible(true);

                  new Thread(() -> {
                        try {
                              URL url = new URL(downloadURL);
                              URLConnection connection = url.openConnection();
                              connection.addRequestProperty("User-Agent", "Mozilla/5.0");
                              connection.addRequestProperty("Authorization", "token " + BackEnd.authToken);
                              connection.addRequestProperty("Accept", "application/octet-stream");

                              String downloadFile = BackEnd.getDownloadFolder() + File.separator + "PWM-Build" + BackEnd.releaseID + ".zip";
                              InputStream is = connection.getInputStream();
                              OutputStream fs = new FileOutputStream(downloadFile);

                              long length = connection.getContentLength();

                              final byte[] data = new byte[1024];
                              int count;
                              while ((count = is.read(data, 0, 1024)) != 1) {
                                    if (count == -1) {
                                          finishDownload(new File(downloadFile));
                                          break;
                                    }
                                    fs.write(data, 0, count);
                                    double prc = (double) (count * 100) / length;

                                    System.out.println("Downloaded: " + (prc * 100) + "%");
                                    Platform.runLater(() -> installationProgress.setProgress(prc));
                                    Platform.runLater(() -> statusLabel.setText("Downloading..."));
                              }
                        } catch (Exception e) {
                              Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, e.getMessage()).show());
                              e.printStackTrace();
                        }
                  }, "Download-Thread").start();
            }).start();



            // Start download and show progress


      }

      public void finishDownload(File downloadedFile) {
            Platform.runLater(() -> installationProgress.setProgress(-1));
            Platform.runLater(() -> statusLabel.setText("Unzipping..."));
            new Thread(() -> {
                  try {
                        new Unzipper().unzip(downloadedFile.getPath(), BackEnd.getDownloadFolder() + File.separator + "unzipped");
                        Platform.runLater(() -> statusLabel.setText("Copying files..."));

                  } catch (IOException e) {
                        Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, Arrays.toString(e.getStackTrace())).show());
                        e.printStackTrace();
                  }
            }).start();
      }

      @Override
      public void initialize(URL url, ResourceBundle resourceBundle) {
            installationProgress.setVisible(false);
            statusLabel.setVisible(false);
      }

      /**
       * Do Not Touch
       */
      public void minimize() {
            HelloApplication.mainStage.setIconified(true);
      }


}