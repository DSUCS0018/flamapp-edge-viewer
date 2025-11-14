cmake_minimum_required(VERSION 3.10.2)
project(flamapp_edge)

# native library
add_library(native-lib SHARED native-lib.cpp)

# where we put OpenCV inside project
set(OpenCV_DIR \/opencv)

# import prebuilt OpenCV .so for the current Android ABI
add_library(opencv_java4 SHARED IMPORTED)
set_target_properties(opencv_java4 PROPERTIES IMPORTED_LOCATION
    \/abi-\/libopencv_java4.so)

include_directories(\/include)

find_library(log-lib log)

target_link_libraries(
    native-lib
    opencv_java4
    \
)
