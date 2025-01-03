
# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.22.1)

# Declares and names the project.

project("jnijson")

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

add_library( # Sets the name of the library.
        jnijson

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        jnijson.cpp)

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

##依赖json
set(JSON_CPP_SOURCE_DIR ${PROJECT_SOURCE_DIR}/json)

set(PUBLIC_HEADERS
        ${JSON_CPP_SOURCE_DIR}/config.h
        ${JSON_CPP_SOURCE_DIR}/forwards.h
        ${JSON_CPP_SOURCE_DIR}/json_features.h
        ${JSON_CPP_SOURCE_DIR}/value.h
        ${JSON_CPP_SOURCE_DIR}/reader.h
        ${JSON_CPP_SOURCE_DIR}/version.h
        ${JSON_CPP_SOURCE_DIR}/writer.h
        ${JSON_CPP_SOURCE_DIR}/assertions.h
        )

source_group("Public API" FILES ${PUBLIC_HEADERS})

set(JSON_CPP_DIR ${PROJECT_SOURCE_DIR}/lib_json)

set(JSONCPP_SOURCES
        ${JSON_CPP_DIR}/json_tool.h
        ${JSON_CPP_DIR}/json_reader.cpp
        ${JSON_CPP_DIR}/json_valueiterator.inl
        ${JSON_CPP_DIR}/json_value.cpp
        ${JSON_CPP_DIR}/json_writer.cpp
        )

add_library( # Sets the name of the library.
        jsoncpp

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        ${PUBLIC_HEADERS}
        ${JSONCPP_SOURCES})

#引用so库
#add_library(
#        jsoncpp
#
#        SHARED
#
#        IMPORTED
#)
#

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
        jnijson
        jsoncpp

        # Links the target library to the log library
        # included in the NDK.
        ${log-lib})
