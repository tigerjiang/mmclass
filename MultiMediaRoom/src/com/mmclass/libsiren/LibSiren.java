/*****************************************************************************
 * LibSiren.java
 *****************************************************************************
 *  @author Jensen<Jensen@connect2.com.cn>
 *****************************************************************************/

/**
 * LibSiren: the native LibSiren interface
 */

package com.mmclass.libsiren;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.http.conn.util.InetAddressUtils;

import android.content.Context;
import android.util.Log;

public class LibSiren {
    private static final String TAG = "MMClass/LibSiren";

    private static LibSiren sInstance;

    /** LibSiren instance C pointer */
    private long mLibSirenInstance = 0; // Read-only, reserved for JNI

    private long mLibSatInstance = 0;// Read-only, reserved for JNI
    /** Buffer for Siren messages */
    private StringBuffer mDebugLogBuffer;
    private boolean mIsBufferingLog = false;

    /** Check in LibSiren already initialized otherwise crash */
    private boolean mIsInitialized = false;

    private static String local_addr; // the local Iddr from Android

    /* Load library before object instantiation */
    static {

        try {
            System.loadLibrary("sirenjni");
        } catch (UnsatisfiedLinkError ule) {
            Log.e(TAG, "Can't load sirenjni library: " + ule);
            // / FIXME Alert user
            System.exit(1);
        } catch (SecurityException se) {
            Log.e(TAG, "Encountered a security issue when loading sirenjni library: " + se);
            // / FIXME Alert user
            System.exit(1);
        }

        try {
            System.loadLibrary("mp3lamejni");
        } catch (UnsatisfiedLinkError ule) {
            Log.e(TAG, "Can't load mp3lamejni library: " + ule);
            // / FIXME Alert user
            System.exit(1);
        } catch (SecurityException se) {
            Log.e(TAG, "Encountered a security issue when loading mp3lamejni library: " + se);
            // / FIXME Alert user
            System.exit(1);
        }

        local_addr = getLocalIpAddress();
        Log.i(TAG, "Local Addr IP:" + local_addr);
    }

    /**
     * Singleton constructor of LibSiren Without surface and vout to create the
     * thumbnail and get information e.g. on the MediaLibraryActivity
     * 
     * @return LibSiren instance
     * @throws LibSirenException
     */
    public static LibSiren getInstance() throws LibSirenException {
        synchronized (LibSiren.class) {
            if (sInstance == null) {
                /* First call */
                sInstance = new LibSiren();
            }
        }

        return sInstance;
    }

   
    /**
     * Return an existing instance of LibSiren Call it when it is NOT important
     * that this fails
     * 
     * @return LibSiren instance OR null
     */
    public static LibSiren getExistingInstance() {
        synchronized (LibSiren.class) {
            return sInstance;
        }
    }

    /**
     * Constructor It is private because this class is a singleton.
     */
    private LibSiren() {

    }

    /**
     * Destructor: It is bad practice to rely on them, so please don't forget to
     * call destroy() before exiting.
     */
    @Override
    public void finalize() {
        if (mLibSirenInstance != 0) {
            Log.d(TAG, "LibSiren is was destroyed yet before finalize()");
            destroy();
        }
    }

    public static synchronized void restart(Context context) {
        if (sInstance != null) {
            try {
                sInstance.destroy();
                sInstance.init(context);
            } catch (LibSirenException lve) {
                Log.e(TAG, "Unable to reinit LibSiren: " + lve);
            }
        }
    }

    /**
     * Initialize the LibSiren class. This function must be called before using
     * any LibSiren functions.
     * 
     * @throws LibSirenException
     */
    public boolean init(Context context) throws LibSirenException {
        Log.v(TAG, "Initializing LibSiren");
        mDebugLogBuffer = new StringBuffer();
        if (!mIsInitialized && local_addr != null) {
            nativeInit(local_addr);
            Log.v(TAG, "LibSiren nativeInit successed");
            setEventHandler(EventHandler.getInstance());

            satnativeInit(local_addr);

            mIsInitialized = true;
        } else {
            mIsInitialized = false;
        }

        return mIsInitialized;
    }

    /**
     * Destroy this LibSiren instance
     * 
     * @note You must call it before exiting
     */
    public void destroy() {
        Log.v(TAG, "Destroying LibSiren instance");
        nativeDestroy();
        detachEventHandler();

        satnativeDestroy();
        mIsInitialized = false;
    }

    /**
     * Send a message to group all
     * 
     * @param message message to send
     */
    public void sendMessage(String message) {
//        if (mIsInitialized)
            sendMessageNative(message);
    }

    public void sendMessage(String message, String ip, String port) {
//        if (mIsInitialized)
            sendMessageToGroupNative(message, ip, port);
    }

    /*
     * the sat--siren audio tools interface
     */
    public int satJoin(String maddr, int port) {
        return Join(local_addr, maddr, port);
    }

    public void satLeave(String addr) {
        Leave(addr);
    }

    public int satSetFmt(int sampleRate, int fmt, int channel) {
        return SetFmt(sampleRate, fmt, channel);
    }

    public int satFileCastStart(String path) {
        return FileCastStart(path);
    }

    public void satFileCastPause(boolean pause) {
        FileCastPause(pause);
    }

    public void satFileCastStop() {
        FileCastStop();
    }

    public void satRecordStart(String path) {
        RecordStart(path);
    }

    public void satRecordPaused(boolean pause) {
        RecordPaused(pause);
    }

    public void satRecordStop() {
        RecordStop();
    }

    public void satnativeInit(String local_addr) {
        nativeInitSat(local_addr);
    }

    public  void startSat() {
        nativeInitSat(local_addr);
    }

    public  void destorySat() {
        nativeDestroySat();
    }
    public void satnativeDestroy() {
        nativeDestroySat();
    }

    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()
                            && InetAddressUtils.isIPv4Address(inetAddress.getHostAddress())) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("WifiPreference IpAddress", ex.toString());
        }

        return null;
    }

    public static void updateAddress(){
    	local_addr = getLocalIpAddress();
    	Log.d(TAG, "address "+local_addr);
    }
    /**
     * the mp3 encode module native interface
     */

    public void mp3LameInit(int channel, int sampleRate, int brate) {
        libmp3lameInit(channel, sampleRate, brate);
    }

    public void mp3LameDestroy() {
        libmp3lameDestroy();
    }
    
    public byte[] mp3LameFlush() {
    	return mp3LameFlush();
    }

    public byte[] mp3LameEncode(byte[] buffer, int len) {
        return libmp3lameEncode(buffer, len);
    }

    public void sendLocalVGAOut() {
        LocalVGAOut();
    }

    public void sendRemoteVGAOut() {
        RemoteVGAOut();
    }

    public int openMic() {
        return micSet(true);
    }

    public int closeMic() {
        return micSet(false);
    }

    public int getSound() {
        return soundGet();
    }

    public int setSound(int sound) {
        return soundSet(sound);
    }

    public int getMicStatus() {
        return micGet();
    }

    public int openSpeaker() {
        return speakerSet(true);
    }

    public int closeSpeaker() {
        return speakerSet(false);
    }

    public int getSpeakerStatus() {
        return speakerGet();
    }

    /**************** Native Function ****************************/
    /**
     * Initialize the LibSiren C library
     * 
     * @return a pointer to the LibSiren instance
     */
    private native void nativeInit(String local_addr) throws LibSirenException;

    /**
     * Close the LibSiren C library
     * 
     * @note mLibSirenInstance should be 0 after a call to destroy()
     */
    private native void nativeDestroy();

    /**
     * Get the LibSiren version
     * 
     * @return the LibSiren version string
     */
    public native String version();

    private native void setEventHandler(EventHandler eventHandler);

    private native void detachEventHandler();

    /**
     * send a message
     */
    private native void sendMessageNative(String message);

    private native void sendMessageToGroupNative(String message, String ip, String port);

    /**
     * the sat module native interface
     */
    private native int Join(String laddr, String maddr, int port);

    private native void Leave(String addr);

    private native int SetFmt(int sampleRate, int fmt, int channel);

    private native int FileCastStart(String path);

    private native void FileCastPause(boolean pause);

    private native void FileCastStop();

    private native int RecordStart(String path);

    private native void RecordPaused(boolean pause);

    private native void RecordStop();

    private native void nativeInitSat(String local_addr);

    private native void nativeDestroySat();

    private native void libmp3lameInit(int channel, int sampleRate, int brate);

    private native void libmp3lameDestroy();

    private native byte[] libmp3lameEncode(byte[] buffer, int len);
    
    private native byte[] libmp3lameFlush();

    private native int LocalVGAOut();

    private native int RemoteVGAOut();

    // 0 -- success
    // 1 -- fail
    private native int micSet(boolean flag);

    // 0 -- open
    // 1 -- close
    // other -- error
    private native int micGet();

    // 0 -- success
    // 1 -- fail
    private native int speakerSet(boolean flag);

    // 0 -- open
    // 1 -- close
    // other -- error
    private native int speakerGet();

    private native int soundSet(int sound);

    private native int soundGet();

}
