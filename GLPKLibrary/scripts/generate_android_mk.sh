#!/bin/bash

# Run from <project>/scripts directory
# Capture output to file called Android.mk

# Goto the .c source file directory.
pushd ../jni >/dev/null

echo "LOCAL_PATH := \$(call my-dir)"
echo "include \$(CLEAR_VARS)"
echo "LOCAL_MODULE := glpk"


echo "LOCAL_SRC_FILES := "
echo "LOCAL_C_INCLUDES := "

# Add all .c files from top level (jni) directory
for f in ./*.c
do
    echo "LOCAL_SRC_FILES += `basename $f`"
done

# Add all .c files from subdirectories (only one level down)
for d in `ls -d */`
do
    echo "LOCAL_C_INCLUDES += \$(LOCAL_PATH)/`echo ${d%?}`"
    for f in $d*.c
    do
        echo "LOCAL_SRC_FILES += $f"
    done
done

echo "include \$(BUILD_SHARED_LIBRARY)"

# Go back to scripts directory
popd >/dev/null
