#!/usr/bin/env bash

./gradlew desktop:dist
java -jar './packr-all-4.0.0.jar' \
  --platform 'windows64' \
  --jdk './OpenJDK15U-jdk_x64_windows_hotspot_15.0.2_7.zip' \
  --useZgcIfSupportedOs \
  --executable 'koseihoryuen' \
  --classpath 'desktop/build/libs/desktop-1.0.jar' \
  --mainclass 'ca.nyiyui.koseihoryuen.DesktopLauncher' \
  --vmargs 'Xmx1G' \
  --resources 'assets' \
  --output 'out-windows64'
tar czvf out-windows64.tar.zst out-windows64
git lfs track 'out-windows64.tar.zst'
