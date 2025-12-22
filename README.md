# VSCode-Termux - Full Development Environment on Android

A modified version of [Termux](https://github.com/termux/termux-app) integrated with [code-server](https://github.com/coder/code-server) to provide a complete VS Code development environment running natively on Android devices.

## 📋 Overview

This project combines Termux's powerful terminal emulator with code-server to create a fully functional Visual Studio Code instance accessible through a built-in WebView. No root required, no remote server needed, no external browser necessary - everything runs locally on your device with an integrated VS Code interface.

## ✨ Features

-   **Full VS Code Experience**: Complete VS Code interface with extensions support
-   **Built-in WebView**: Integrated browser view - no need to switch to external browser
-   **Local Development**: All processing happens on your device
-   **Terminal Integration**: Built-in Termux terminal with bash, apt, pkg support
-   **Language Support**: Install compilers, interpreters, and tools via apt/pkg
-   **No Root Required**: Works on standard Android devices
-   **Portable**: Develop anywhere with just your Android device

## 🚀 Quick Start

### Prerequisites

-   Android device (Android 7.0+ recommended)
-   ~500MB free storage for base installation
-   Additional storage for development tools and projects

### Installation

1. **Download APK**

    ```bash
    # Download the latest release from GitHub releases
    # Or build from source (see Building section)
    ```

2. **Install on Device**

    ```bash
    adb install termux-app_apt-android-7-debug_x86_64.apk
    ```

    > ⚠️ **Important for Android 16+**: Always uninstall the previous version before installing a new one to avoid SELinux context issues:

    ```bash
    adb uninstall com.termux
    adb install <apk-file>
    ```

3. **Launch Termux**

    - Open the Termux app from your device
    - Wait for initial setup to complete

4. **Install Code-Server**

    ```bash
    # Update packages
    apt update && apt upgrade -y

    # Install Node.js (required for code-server)
    apt install nodejs -y

    # Install code-server
    npm install -g code-server

    # Start code-server
    code-server --bind-addr 127.0.0.1:8080
    ```

5. **Access VS Code**
    - VS Code will automatically open in the built-in WebView
    - Alternatively, manually navigate to `http://localhost:8080` in the app
    - Enter the password from `~/.config/code-server/config.yaml` if prompted

## 🛠️ Usage

### Starting Code-Server

```bash
# Start with default settings
code-server

# Start with custom port
code-server --bind-addr 127.0.0.1:3000

# Start without authentication (local development only)
code-server --bind-addr 127.0.0.1:8080 --auth none

# Run in background
nohup code-server &
```

### Installing Development Tools

```bash
# Python
apt install python -y

# Node.js and npm (already installed)
node --version
npm --version

# Git
apt install git -y

# GCC/Clang
apt install clang -y

# Java
apt install openjdk-17 -y

# PHP
apt install php -y

# Ruby
apt install ruby -y
```

### Managing Extensions

Extensions can be installed directly from the VS Code marketplace within code-server:

1. Click Extensions icon in VS Code
2. Search for desired extension
3. Click Install

Or via command line:

```bash
code-server --install-extension <extension-id>
```

## 🏗️ Building from Source

### Setup Build Environment

```bash
# Clone repository
git clone https://github.com/yourusername/vscode-termux.git
cd vscode-termux

# Install dependencies (requires Android SDK and NDK)
# See gradle.properties for required versions
```

### Build APK

```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Output location
# app/build/outputs/apk/debug/termux-app_apt-android-7-debug_x86_64.apk
```

### Configuration

Key configuration files:

-   `gradle.properties` - SDK versions and build settings
-   `app/build.gradle` - App-specific build configuration
-   `local.properties` - Local SDK paths

## 📱 Android 16 Compatibility

### Known Issue: SELinux Context Persistence

On Android 16 (SDK 35+), reinstalling the app using `adb install -r` can cause "Permission denied" errors for bash, apt, and pkg executables due to stale SELinux security contexts.

### Solution

Always perform a clean uninstall before reinstalling:

```bash
# Correct method
adb uninstall com.termux
adb install termux-app.apk

# Avoid this (causes SELinux issues)
adb install -r termux-app.apk
```

### Why This Happens

-   Android 16 enforces strict SELinux policies
-   `adb install -r` preserves old security contexts
-   Preserved contexts may block `execute_no_trans` permission
-   Clean uninstall forces Android to assign fresh, correct contexts

This issue affects all similar apps (UserLand, Termux, etc.) on Android 16.

## 🔧 Troubleshooting

### "Permission denied" errors

**Symptom**: bash, apt, or pkg commands fail with permission denied

**Solution**:

1. Uninstall the app completely
2. Reinstall (don't use `-r` flag)
3. Launch and test: `bash --version`

### Code-server won't start

**Check Node.js installation**:

```bash
node --version
npm --version
```

**Reinstall code-server**:

```bash
npm uninstall -g code-server
npm install -g code-server
```

### Can't access localhost:8080

**Check if code-server is running**:

```bash
ps aux | grep code-server
```

**Verify WebView is enabled**:

-   Restart the app
-   Check if WebView component is properly initialized
-   Try accessing via terminal first to ensure code-server is running

**Check port binding**:

```bash
netstat -tulpn | grep 8080
```

### Storage issues

**Check available space**:

```bash
df -h
```

**Clear package cache**:

```bash
apt clean
```

## 📂 Project Structure

```
vscode-termux/
├── app/                          # Main Android application
│   ├── src/main/java/           # Java/Kotlin source code
│   └── build.gradle             # App build configuration
├── terminal-emulator/           # Terminal emulator module
├── terminal-view/              # Terminal view components
├── termux-shared/              # Shared Termux utilities
├── vscode-view/                # VS Code integration components
├── gradle.properties           # Global Gradle settings
└── README.md                   # This file
```

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is based on [Termux](https://github.com/termux/termux-app), which is licensed under GPLv3.

See [LICENSE.md](LICENSE.md) for details.

## 🙏 Acknowledgments

-   [Termux](https://github.com/termux/termux-app) - The foundation of this project
-   [code-server](https://github.com/coder/code-server) - VS Code in the browser
-   [Coder](https://coder.com/) - For making code-server possible
-   Android Terminal Emulator community

## 📞 Support

-   **Issues**: [GitHub Issues](https://github.com/yourusername/vscode-termux/issues)
-   **Discussions**: [GitHub Discussions](https://github.com/yourusername/vscode-termux/discussions)
-   **Wiki**: [Project Wiki](https://github.com/yourusername/vscode-termux/wiki)

## 🗺️ Roadmap

-   [ ] Pre-configured code-server installation script
-   [ ] Auto-start code-server on app launch
-   [x] Built-in WebView for code-server (implemented)
-   [ ] Extension marketplace integration
-   [ ] Docker support in Termux
-   [ ] Multiple workspace support
-   [ ] Cloud sync capabilities
-   [ ] Split-screen: Terminal + VS Code view

## 📊 System Requirements

| Component       | Minimum                 | Recommended    |
| --------------- | ----------------------- | -------------- |
| Android Version | 7.0 (API 24)            | 10.0+ (API 29) |
| RAM             | 2GB                     | 4GB+           |
| Storage         | 2GB free                | 5GB+ free      |
| Architecture    | ARM, ARM64, x86, x86_64 | ARM64, x86_64  |

## 🎯 Use Cases

-   **Mobile Development**: Code on the go
-   **Learning**: Perfect for coding tutorials and practice
-   **Quick Fixes**: Edit code directly on your phone
-   **Remote Work**: No laptop needed for simple tasks
-   **Education**: Teach programming without requiring laptops
-   **Experimentation**: Try new languages and frameworks

## 🔐 Security Notes

-   Code-server runs locally on your device
-   Default configuration uses password authentication
-   Change default password in `~/.config/code-server/config.yaml`
-   Don't expose code-server to public networks without proper security
-   Keep packages updated: `apt update && apt upgrade`

---

**Made with ❤️ for developers who code on the go**

_Star ⭐ this repo if you find it useful!_
