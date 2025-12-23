# Termux VSCode-Server

> A mobile development environment: Run VS Code Server on Android using Termux, with integrated WebView access.

## Overview

This project enables you to run a full-featured [VS Code Server](https://github.com/coder/code-server) directly on your Android device using [Termux](https://termux.com/). Once the server is set up, the app launches a WebView for seamless, in-app code editing.

## Features

-   **VS Code Server on Android**: Use the power of VS Code remotely on your phone or tablet.
-   **Termux Integration**: Leverages Termux for Linux-like environment and package management.
-   **Automatic WebView Launch**: After setup, the app opens a WebView to access the running code-server instance.
-   **Touch-friendly UI**: Optimized for mobile usage.

## How It Works

1. **Setup**: The app installs ubuntu and configures code-server inside Ubuntu.
2. **Start Server**: On launch, it checks and starts the code-server if not already running.
3. **WebView Access**: Once running, a WebView is triggered to access the code-server UI at `http://127.0.0.1:8080`.
4. **Development**: Edit files, run terminals, and manage your projects just like on desktop VS Code.

## Getting Started

#### Option 1

1. **Clone this repository** into your Termux home directory.
2. **Build and install the APK** on your Android device.
3. **Launch the app** and follow the on-screen instructions to set up code-server.

#### Option 2

1. install the APK release

## Requirements

-   Android 7.0+
-   Internet connection for initial setup

## Credits

-   [Termux](https://termux.com/)
-   [code-server](https://github.com/coder/code-server)

## License

See [LICENSE.md](LICENSE.md) for details.
