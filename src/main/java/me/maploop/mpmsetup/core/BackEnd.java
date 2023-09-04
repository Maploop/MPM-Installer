package me.maploop.mpmsetup.core;

import javafx.scene.control.Alert;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class BackEnd
{
      public static final String authToken = "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";
      public static String releaseID = "INVALID";

      public static String fetchDownloadURL() {
            String urlS = "https://api.github.com/repos/" + "Maploop" + "/" + "MaploopPasswordManager" + "/releases";
            try {
                  org.json.JSONArray releaseList = new JSONArray(get(urlS));
                  JSONObject latestRelease = (JSONObject) releaseList.get(releaseList.length() - 1);

                  JSONArray assets = (JSONArray) latestRelease.get("assets");
                  String downloadURL = "";
                  String relId = "";
                  for (Object o : assets) {
                        JSONObject asset = (JSONObject) o;
                        if (asset.get("name").toString().endsWith(".zip")) {
                              downloadURL = asset.get("url").toString();

                              String[] split = downloadURL.split("/");
                              relId = split[split.length - 1];
                              break;
                        }
                  }

                  System.out.println("Found Download Link of RELEASE: " + relId);
                  releaseID = relId;
                  return downloadURL;


            } catch (Exception e) {
                  e.printStackTrace();
                  System.out.println("Could not find download link for this app.");
                  return "Nothing!";
            }
      }

      public static String get(String urlS) {
            return get(urlS, new ArrayList<>());
      }

      public static String get(String urlS, List<Map.Entry<String, String>> headers) {
            try {
                  URL url = new URL(urlS);
                  URLConnection connection = url.openConnection();
                  connection.addRequestProperty("User-Agent", "Mozilla/5.0");
                  connection.addRequestProperty("Authorization", "token " + authToken);

                  for (Map.Entry<String, String> entry : headers)
                        connection.addRequestProperty(entry.getKey(), entry.getValue());

                  return new Scanner(connection.getInputStream()).useDelimiter("\\A").next();

            } catch (Exception e) {
                  e.printStackTrace();
                  Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
                  alert.show();
            }

            return null;
      }

      public static String getDownloadFolder() {
            return new File(getLocalAppData().getPath() + File.separator + "MaploopPasswordManager" + File.separator + "temp").getPath();
      }

      public static String getInstallPath() {
            return new File(getLocalAppData().getPath() + File.separator + "MaploopPasswordManager" + File.separator + "Main").getPath();
      }

      public static File getLocalAppData() {
            return new File(System.getenv("LOCALAPPDATA"));
      }
}
