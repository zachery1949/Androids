#CMakeLists固定开头
cmake_minimum_required(VERSION 3.4.1)
# 引入子目录的CmakeList.txt rmtp静态库
add_subdirectory(src/main/cpp/librtmp)

#导入头文件的路径 自动查看头文件 <>  <faac.h>
include_directories(src/main/cpp/librtmp)
# 设置x264库路径
#set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -L${CMAKE_SOURCE_DIR}/src/main/cpp/libx264/libs/armeabi-v7a")
#批量导入c文件
file(GLOB bzip_src src/main/cpp/bzip2/*.c)

#导入头文件的路径 自动查看头文件 <>  <faac.h>
include_directories(src/main/cpp/bzip2)

        #使用指定的源文件向工程中添加一个库。
add_library( # Sets the name of the library.
             native-lib

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             src/main/cpp/native-lib.cpp
             src/main/cpp/BufferArea.cpp
             ${bzip_src}
             src/main/cpp/bspatch.c)






find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )



target_link_libraries( # Specifies the target library.
                       native-lib
                        rtmp
                       # Links the target library to the log library
                        android
                       # included in the NDK.
                       ${log-lib}
                       )

