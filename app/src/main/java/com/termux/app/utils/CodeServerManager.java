package com.termux.app.utils;

import android.content.Context;

import com.termux.shared.logger.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

/**
 * Manager class for code-server installation and lifecycle
 */
public class CodeServerManager {

    private static final String LOG_TAG = "CodeServerManager";
    private static final int CODE_SERVER_PORT = 8080;
    
    private final Context mContext;

    public CodeServerManager(Context context) {
        mContext = context.getApplicationContext();
    }

    /**
     * Check if code-server is installed
     */
    public boolean isCodeServerInstalled() {
        try {
            Process process = Runtime.getRuntime().exec("which code-server");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            process.waitFor();
            
            boolean installed = line != null && !line.isEmpty();
            Logger.logInfo(LOG_TAG, "code-server installed: " + installed);
            return installed;
        } catch (Exception e) {
            Logger.logStackTraceWithMessage(LOG_TAG, "Error checking code-server installation", e);
            return false;
        }
    }

    /**
     * Check if code-server is currently running
     */
    public boolean isCodeServerRunning() {
        try {
            Process process = Runtime.getRuntime().exec("pgrep -f code-server");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            process.waitFor();
            
            boolean running = line != null && !line.isEmpty();
            Logger.logInfo(LOG_TAG, "code-server running: " + running);
            return running;
        } catch (Exception e) {
            Logger.logStackTraceWithMessage(LOG_TAG, "Error checking code-server status", e);
            return false;
        }
    }

    /**
     * Install code-server using pkg
     */
    public boolean installCodeServer() {
        try {
            Logger.logInfo(LOG_TAG, "Starting code-server installation...");
            
            // Run the installation command
            String installCmd = "pkg update -y && pkg upgrade -y && pkg install tur-repo code-server -y";
            Process process = Runtime.getRuntime().exec(new String[]{"sh", "-c", installCmd});
            
            // Read output
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            
            String line;
            while ((line = reader.readLine()) != null) {
                Logger.logInfo(LOG_TAG, "Install output: " + line);
            }
            
            while ((line = errorReader.readLine()) != null) {
                Logger.logError(LOG_TAG, "Install error: " + line);
            }
            
            int exitCode = process.waitFor();
            Logger.logInfo(LOG_TAG, "Installation completed with exit code: " + exitCode);
            
            return exitCode == 0 && isCodeServerInstalled();
        } catch (Exception e) {
            Logger.logStackTraceWithMessage(LOG_TAG, "Error installing code-server", e);
            return false;
        }
    }

    /**
     * Start code-server in the background
     */
    public boolean startCodeServer() {
        try {
            Logger.logInfo(LOG_TAG, "Starting code-server on port " + CODE_SERVER_PORT);
            
            // Create code-server config directory if it doesn't exist
            String configDir = getCodeServerConfigDir();
            File configDirFile = new File(configDir);
            if (!configDirFile.exists()) {
                configDirFile.mkdirs();
            }
            
            // Create a simple config file
            createCodeServerConfig(configDir);
            
            // Start code-server in background
            String startCmd = "nohup code-server --bind-addr 127.0.0.1:" + CODE_SERVER_PORT + 
                             " --auth none --disable-telemetry > /dev/null 2>&1 &";
            
            Process process = Runtime.getRuntime().exec(new String[]{"sh", "-c", startCmd});
            
            // Give it a moment to start
            Thread.sleep(1000);
            
            boolean started = isCodeServerRunning();
            Logger.logInfo(LOG_TAG, "code-server started: " + started);
            return started;
        } catch (Exception e) {
            Logger.logStackTraceWithMessage(LOG_TAG, "Error starting code-server", e);
            return false;
        }
    }

    /**
     * Stop code-server
     */
    public boolean stopCodeServer() {
        try {
            Logger.logInfo(LOG_TAG, "Stopping code-server...");
            Process process = Runtime.getRuntime().exec("pkill -f code-server");
            int exitCode = process.waitFor();
            
            boolean stopped = !isCodeServerRunning();
            Logger.logInfo(LOG_TAG, "code-server stopped: " + stopped);
            return stopped;
        } catch (Exception e) {
            Logger.logStackTraceWithMessage(LOG_TAG, "Error stopping code-server", e);
            return false;
        }
    }

    /**
     * Get the code-server URL
     */
    public String getCodeServerUrl() {
        return "http://127.0.0.1:" + CODE_SERVER_PORT;
    }

    /**
     * Get code-server config directory
     */
    private String getCodeServerConfigDir() {
        return "/data/data/com.termux/files/home/.config/code-server";
    }

    /**
     * Create code-server configuration file
     */
    private void createCodeServerConfig(String configDir) {
        try {
            String configContent = "bind-addr: 127.0.0.1:" + CODE_SERVER_PORT + "\n" +
                                  "auth: none\n" +
                                  "cert: false\n";
            
            String configPath = configDir + "/config.yaml";
            Process process = Runtime.getRuntime().exec(new String[]{
                "sh", "-c", "echo '" + configContent + "' > " + configPath
            });
            process.waitFor();
            
            Logger.logInfo(LOG_TAG, "Created config file at: " + configPath);
        } catch (Exception e) {
            Logger.logStackTraceWithMessage(LOG_TAG, "Error creating config file", e);
        }
    }

    /**
     * Get the port code-server is running on
     */
    public int getCodeServerPort() {
        return CODE_SERVER_PORT;
    }
}
