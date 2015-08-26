ifeq ($(BUILD_MM900),true)
LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := eng#optional

LOCAL_SRC_FILES := $(call all-java-files-under, src) \

LOCAL_SHARED_LIBRARIES := sirenjni mp3lamejni
#LOCAL_STATIC_LIBRARIES := satjni
LOCAL_MODULE_PATH := $(TARGET_OUT)/app
#TARGET_PRELINK_MODULES := false
LOCAL_PACKAGE_NAME := launcher-mm
#LOCAL_MODULE_CLASS := APPS
#LOCAL_PROGUARD_FLAG_FILES := proguard.flags
LOCAL_CERTIFICATE := platform

include $(BUILD_PACKAGE)

# Use the folloing include to make our test apk.
include $(call all-makefiles-under,$(LOCAL_PATH))

endif
