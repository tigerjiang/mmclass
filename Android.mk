LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

# Use the folloing include to make our test apk.
include $(call all-makefiles-under,$(LOCAL_PATH))
