NDK_VERSION=26.3.11579264

echo "Installing NDK ${NDK_VERSION}"
yes | $ANDROID_HOME/tools/bin/sdkmanager --install "ndk;${NDK_VERSION}"
# sdkmanager --install "ndk;${NDK_VERSION}"

export ANDROID_NDK_HOME=$ANDROID_HOME/ndk/${NDK_VERSION}
export NDK_HOME=$ANDROID_HOME/ndk/${NDK_VERSION}