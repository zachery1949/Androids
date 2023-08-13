cmake_minimum_required(VERSION 3.4.1)
set(CMAKE_CXX_STANDARD 11)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

#导入头文件的路径 自动查看头文件 <>  <faac.h>
include_directories(jniLibs/arm64-v8a/include/BasicUsageEnvironment/include
        jniLibs/arm64-v8a/include/groupsock/include
        jniLibs/arm64-v8a/include/liveMedia
        jniLibs/arm64-v8a/include/liveMedia/include
        jniLibs/arm64-v8a/include/UsageEnvironment/include)



# 设置liveMedia库路径
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -L${CMAKE_SOURCE_DIR}/jniLibs/arm64-v8a")

# 代码太少，不需要批量导入
add_library(
        dataPriveder
        SHARED
        nativeqwe.cpp
        live555.cpp
        queue.cpp
        Nalu.cpp
)

target_link_libraries(
        dataPriveder
        liveMedia
        BasicUsageEnvironment
        UsageEnvironment
        groupsock
        mediandk
        log)