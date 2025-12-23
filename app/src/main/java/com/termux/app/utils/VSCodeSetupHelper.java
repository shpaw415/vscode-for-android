package com.termux.app.utils;

import android.content.Context;

import com.termux.shared.logger.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Helper class to set up VS Code Server command in Termux
 */
public class VSCodeSetupHelper {

    private static final String LOG_TAG = "VSCodeSetupHelper";
    private static final String SCRIPT_NAME = "setup-vscode.sh";
    private static final String TERMUX_HOME = "/data/data/com.termux/files/home";
    private static final String BIN_DIR = TERMUX_HOME + "/.local/bin";
    
    /**
     * Install the setup script to Termux home directory
     */
    public static boolean installSetupScript(Context context) {
        try {
            // Create bin directory if it doesn't exist
            File binDir = new File(BIN_DIR);
            if (!binDir.exists()) {
                Runtime.getRuntime().exec("mkdir -p " + BIN_DIR).waitFor();
            }
            
            // Copy script from assets to bin directory
            String targetPath = BIN_DIR + "/" + SCRIPT_NAME;
            InputStream inputStream = context.getAssets().open(SCRIPT_NAME);
            FileOutputStream outputStream = new FileOutputStream(targetPath);
            
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            
            outputStream.close();
            inputStream.close();
            
            // Make script executable
            Runtime.getRuntime().exec("chmod +x " + targetPath).waitFor();
            
            // Create alias
            createAlias();
            
            Logger.logInfo(LOG_TAG, "Setup script installed to: " + targetPath);
            return true;
            
        } catch (Exception e) {
            Logger.logStackTraceWithMessage(LOG_TAG, "Error installing setup script", e);
            return false;
        }
    }
    
    /**
     * Create an alias for easy access
     */
    private static void createAlias() {
        try {
            String bashrcPath = TERMUX_HOME + "/.bashrc";
            String aliasCommand = "\n# VS Code Server alias\nalias vscode='" + BIN_DIR + "/" + SCRIPT_NAME + "'\n";
            
            // Check if alias already exists
            Process checkProcess = Runtime.getRuntime().exec(new String[]{"sh", "-c", "grep -q 'alias vscode=' " + bashrcPath});
            int exitCode = checkProcess.waitFor();
            
            if (exitCode != 0) {
                // Alias doesn't exist, add it
                Process addProcess = Runtime.getRuntime().exec(new String[]{"sh", "-c", "echo '" + aliasCommand + "' >> " + bashrcPath});
                addProcess.waitFor();
                Logger.logInfo(LOG_TAG, "Alias 'vscode' added to .bashrc");
            } else {
                Logger.logInfo(LOG_TAG, "Alias 'vscode' already exists");
            }
            
        } catch (Exception e) {
            Logger.logStackTraceWithMessage(LOG_TAG, "Error creating alias", e);
        }
    }
    
    /**
     * Check if the setup script is installed
     */
    public static boolean isSetupScriptInstalled() {
        File scriptFile = new File(BIN_DIR + "/" + SCRIPT_NAME);
        return scriptFile.exists();
    }
    
    /**
     * Run the setup script
     */
    public static boolean runSetupScript() {
        try {
            String scriptPath = BIN_DIR + "/" + SCRIPT_NAME;
            Process process = Runtime.getRuntime().exec(new String[]{"sh", scriptPath});
            
            // Read output
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                Logger.logInfo(LOG_TAG, "Script output: " + line);
            }
            
            int exitCode = process.waitFor();
            return exitCode == 0;
            
        } catch (Exception e) {
            Logger.logStackTraceWithMessage(LOG_TAG, "Error running setup script", e);
            return false;
        }
    }
    
    /**
     * Auto-install and launch code-server without user interaction
     * This runs the installation in background and opens WebView automatically
     */
    public static void autoInstallAndLaunch(Context context) {
        new Thread(() -> {
            try {
                Logger.logInfo(LOG_TAG, "Starting automatic VS Code Server installation...");
                
                // Install script if not already installed
                if (!isSetupScriptInstalled()) {
                    installSetupScript(context);
                }
                
                // Run the setup script (installs, configures, and starts code-server)
                runSetupScript();
                
                Logger.logInfo(LOG_TAG, "Auto-installation completed, WebView should open automatically");
                
            } catch (Exception e) {
                Logger.logStackTraceWithMessage(LOG_TAG, "Error in auto-install and launch", e);
            }
        }).start();
    }
}
