package com.multimedia.room.upgrade;


public class Constants {


    /**
     * Can't delete temperature file.
     */
    public static final int EXCEPTION_DELETE_TMPFILE = 10;

    public static final int EXCEPTION_DOWNLOAD_IO = 7;

    /**
     */
    public static final int EXCEPTION_ILLEGAL_STATE = 3;
    /**
     * Login error
     */
    public static final int EXCEPTION_LOGIN = 8;
    /**
     */
    public static final int EXCEPTION_REBOOT_IO = 6;
    /**
     */
    public static final int EXCEPTION_REMOTE = 0;
    /**
     */
    public static final int EXCEPTION_RUNTIME = -1;
    /**
     */
    public static final int EXCEPTION_UNKOWN = -2;
    /**
     */
    public static final int EXCEPTION_VERIFY_IO = 4;
    /**
     */
    public static final int EXCEPTION_VERIFY_SECURITY = 5;
    // Prefix of update file, include temp file and final file.
    /**
     */
  
    public static final int STATE_DOWNLOADING = 4;

    public static final int STATE_ERROR = -1;

    public static final int STATE_FOUND_NEW_VERSION = 3;

    public static final int STATE_IDLE = 0;

    public static final int STATE_NO_NEW_VERSION = 2;

    public static final int STATE_REBOOTING = 8;

    public static final int STATE_SPACE_NOT_ENOUGH = 5;

    public static final int STATE_STOPPED = 9;

    public static final int STATE_VERIFING = 6;

    public static final int STATE_VERIFING_COMPLETED = 7;

    public static final int STATE_VERSION_CHECKING = 1;

    // Suffix of temperature file.
    public static final String TEMP_FILE_SUFFIX = ".tmp";

    public static final String UPGRADE_FILE_BODY = "update.zip";

    public static final String UPGRADE_KEY = "UpGradeMode";
}
