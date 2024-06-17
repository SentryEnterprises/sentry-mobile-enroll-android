# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html.
# For more examples on how to use CMake, see https://github.com/android/ndk-samples.

# Sets the minimum CMake version required for this project.
cmake_minimum_required(VERSION 3.22.1)

# now build app's shared lib
#set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -std=c99")

# Declares the project name. The project name can be accessed via ${ PROJECT_NAME},
# Since this is the top level CMakeLists.txt, the project name is also accessible
# with ${CMAKE_PROJECT_NAME} (both CMake variables are in-sync within the top level
# build script scope).
project("jcwkit")

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.
#
# In this top level CMakeLists.txt, ${CMAKE_PROJECT_NAME} is used to define
# the target library name; in the sub-module's CMakeLists.txt, ${PROJECT_NAME}
# is preferred for the same purpose.
#
# In order to load a library into your app from Java/Kotlin, you must call
# System.loadLibrary() and pass the name of the library defined here;
# for GameActivity/NativeActivity derived applications, the same library name must be
# used in the AndroidManifest.xml file.
add_library(
        # Specifies the name of the library.
        ${CMAKE_PROJECT_NAME}

        # Sets the library as a shared library.
        SHARED

        # List C/C++ source files with relative paths to this CMakeLists.txt.
        libsdkmain.h
        libsdkmain.c

        LibSdkWallet/bip39.h
        LibSdkWallet/hal_hmac.h
        LibSdkWallet/lib_wallet.h
        LibSdkWallet/preliminary.h
        LibSdkWallet/sha512.h

        LibSdkWallet/bip39.c
        LibSdkWallet/hal_hmac.c
        LibSdkWallet/lib_sdk_wallet.c
        LibSdkWallet/preliminary.c
        LibSdkWallet/sha512.c
        LibSdkWallet/ripemd160.c
        LibSdkWallet/sha3.c
        LibSdkWallet/sha3.h

        LibSecureChannel/aes.h
        LibSecureChannel/cmac.h
        LibSecureChannel/secure.h
        LibSecureChannel/sha.h
        LibSecureChannel/uECC.h
        LibSecureChannel/wrapper.h

        LibSecureChannel/curve-specific.h
        LibSecureChannel/platform-specific.h
        LibSecureChannel/uecc_types.h
        LibSecureChannel/uECC_vli.h

        LibSecureChannel/aes.c
        LibSecureChannel/cmac.c
        LibSecureChannel/secure.c
        LibSecureChannel/sha.c
        LibSecureChannel/uECC.c
        LibSecureChannel/wrapper.c

        LibSdkEnrollment/lib_sdk_enroll.h
        LibSdkEnrollment/lib_sdk_enroll.c

        LibSupport/BRBase58.c
        LibSupport/BRAddress.h

        LibSupport/BRBech32.c
        LibSupport/BRBase58.h

        LibSupport/BRBech32.h
        LibSupport/BRCrypto.h

        LibSupport/support.h
        LibSupport/support.c
        )

target_include_directories (
        ${CMAKE_PROJECT_NAME}

        PUBLIC

        LibSdkWallet/
        LibSecureChannel/
        LibSdkEnrollment/
        LibSupport/
)

# Specifies libraries CMake should link to your target library. You
# can link libraries from various origins, such as libraries defined in this
# build script, prebuilt third-party libraries, or Android system libraries.
target_link_libraries(${CMAKE_PROJECT_NAME}
        # List libraries link to the target library
        android
        log)

find_library( # Defines the name of the path variable that stores the
        # location of the NDK library.
        log-lib

        # Specifies the name of the NDK library that
        # CMake needs to locate.
        log )

# Links your native library against one or more other native libraries.
target_link_libraries( # Specifies the target library.
        ${CMAKE_PROJECT_NAME}

        # Links the log library to the target library.
        ${log-lib} )