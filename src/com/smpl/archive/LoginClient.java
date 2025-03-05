package com.smpl.archive;

import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;
import com.smpl.util.watchfolder.IFileProcessor;
import com.smpl.util.watchfolder.FolderConfig;
import com.smpl.util.watchfolder.FolderMonitor;

public class LoginClient implements IFileProcessor {
    private static final Logger logger = Logger.getLogger(LoginClient.class.getName());
    private final LoginManager loginManager;
    private final ArchiveManager archiveManager;

    public LoginClient() {
        this.loginManager = new LoginManager();
        this.archiveManager = new ArchiveManager();
    }

    @Override
    public boolean processFiles(Path path, FolderConfig fc) {
        String objectName = path.getFileName().toString(); // Assuming objectName is the file name
        String token = loginManager.getToken(); // Assuming token is retrieved from LoginManager

        if (archiveManager.isFileArchived(objectName, token)) {
            System.out.println("File already archived:" +objectName);
            return true;
        } else {
            System.out.println("File not archived. Proceeding to archive:" + objectName);
            String newRequestId = archiveManager.archiveFile(objectName, token);
            if (newRequestId != null) {
                JSONObject archiveInfo;
                boolean firstLog = true; // Flag to ensure the status code is logged only once

                do {
                    archiveInfo = archiveManager.getArchiveInfo(newRequestId, token);
                    if (archiveInfo != null) {
                        if (firstLog) {
                            logger.info("GET Archive Info HTTP Status Code: 200"); // Log only once
                            firstLog = false; // Set the flag to false after logging
                        }

                        String stateName = archiveInfo.optString("stateName", "UNKNOWN");
                        int progress = archiveInfo.optInt("progress", 0);
                        System.out.println("Archive State: Progress:" + stateName + progress);

                        if ("COMPLETED".equalsIgnoreCase(stateName)) {
                            return true;
                        }
                        if ("ABORTED".equalsIgnoreCase(stateName)) {
                            System.out.println("Archive state is ABORTED.");
                            return false;
                        }

                        try {
                            Thread.sleep(500); // Poll every 0.5 seconds
                        } catch (InterruptedException ex) {
                            logger.log(Level.SEVERE, null, ex);
                            return false;
                        }
                    } else {
                        break;
                    }
                } while (true);

            } else {
                logger.log(Level.SEVERE, "Failed to get Request ID for file: {0}", objectName);
                return false;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        try {
            String configFilePath = ".\\Config\\WatcherConfig.xml";
            FolderMonitor folderMonitor = new FolderMonitor(configFilePath);
            LoginClient client = new LoginClient();
            for (FolderConfig folderConfig : folderMonitor.getFolderConfigs()) {
                folderConfig.setFileProcessor(client);
            }
//            folderMonitor.getFolderConfigs(); // Assuming processAllFiles takes an IFileProcessor
        } catch (Exception e) {
            Logger.getLogger(LoginClient.class.getName()).log(Level.SEVERE, "An error occurred while initializing the application:", e);
        }
    }

    @Override
    public void setAppParams(String params) {
        // Implement this method to handle application specific parameters if necessary
    }
}
