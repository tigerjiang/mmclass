
package com.multimedia.room;

import android.util.Log;

import com.mmclass.libsiren.LibSiren;

public class CommandManager {

    private static CommandManager sIntance = new CommandManager();
    private static LibSiren mLibSiren;
    private static String mSeatNo;
    public static boolean sIsInitSat = true;

    private CommandManager() {

    }

    public synchronized CommandManager getInstance() {
        if (sIntance == null) {
            sIntance = new CommandManager();
        }
        return sIntance;
    }

    public static void SetSeatNo(String seatNo) {
        mSeatNo = seatNo;
    }

    public static void SetLibSiren(LibSiren libSiren) {
        mLibSiren = libSiren;
    }

    public static void sendSetSeatMessage() {
        String setSeatMsg = "type:" + CommonUtil.getSeatNo() + "," + "receiver:all" + "," + "mode:"
                + Command.Modes.MODE_GLOBAL + "," + "command:"
                + Command.COMMANDS.COMMAND_SET_SEAT + "," + "group:null" + "," + "param:null";

        mLibSiren.sendMessage(Command.formatMessage(setSeatMsg));
    }

    public static void sendSetOnlineMessage() {
        String setOnlineMsg = "type:" + mSeatNo + "," + "receiver:teacher" + "," + "mode:"
                + Command.Modes.MODE_GLOBAL + "," + "command:"
                + Command.COMMANDS.COMMAND_GLOBAL_ONLINE + "," + "group:null" + "," + "param:null";
        mLibSiren.sendMessage(Command.formatMessage(setOnlineMsg));
    }


    public static void sendSetSyncMessage() {
        String setSyncMsg = "type:" + mSeatNo + "," + "receiver:teacher" + "," + "mode:"
                + Command.Modes.MODE_GLOBAL + "," + "command:"
                + Command.COMMANDS.COMMAND_GLOBAL_SYNC + "," + "group:null" + "," + "param:null";
        mLibSiren.sendMessage(Command.formatMessage(setSyncMsg));
    }

    public static void sendSetHandUpMessage() {
        
        String setHandUpMsg = "type:" + mSeatNo + "," + "receiver:teacher" + "," + "mode:"
                + Command.Modes.MODE_GLOBAL + "," + "command:"
                + Command.COMMANDS.COMMAND_GLOBAL_HANDSUP + "," + "group:null" + "," + "param:on";
        mLibSiren.sendMessage(Command.formatMessage(setHandUpMsg));
    }

    public static void sendCancelHandUpMessage() {
        String setHandUpMsg = "type:" + mSeatNo + "," + "receiver:teacher" + "," + "mode:"
                + Command.Modes.MODE_GLOBAL + "," + "command:"
                + Command.COMMANDS.COMMAND_GLOBAL_HANDSUP + "," + "group:null" + "," + "param:off";
        mLibSiren.sendMessage(Command.formatMessage(setHandUpMsg));
    }
    // demonstration
    public static void setDemonstration(String addr) {
        mLibSiren.satJoin(addr, 6000);
    }

    // intercom
    public static void setIntercom(String addr) {
        mLibSiren.satJoin(addr, 6000);
    }

    // monitor
    public static void setMonitor(String addr) {
        mLibSiren.satJoin(addr, 6000);
    }

    // dictation
    public static void sendDictationMessage(String content) {
        
        String dictationMsg = "type:" + mSeatNo + "," + "receiver:all" + "," + "mode:"
                + Command.Modes.MODE_GLOBAL + "," + "command:"
                + Command.COMMANDS.COMMAND_TEACH_DICTATION + "," + "group:null" + "," + "param:"+content+ " ";
        mLibSiren.sendMessage(Command.formatMessage(dictationMsg));
    }

    // test
    public static void sendTestMessage(String content) {
        
        
        String testMsg = "type:" + mSeatNo + "," + "receiver:all" + "," + "mode:"
                + Command.Modes.MODE_GLOBAL + "," + "command:"
                + Command.COMMANDS.COMMAND_TEACH_TEST + "," + "group:null" + "," + "param:"+content;
        
        mLibSiren.sendMessage(Command.formatMessage(testMsg));
    }

    // translate
    public static void setTranslate(String addr) {
        Log.d("join", "join group "+addr);
        mLibSiren.satJoin(addr, 6000);
    }

    // group
    // translate
    public static void joinGroup(String addr) {
        Log.d("join", "join group "+addr);
        mLibSiren.satJoin(addr, 6000);
    }

    
    public static void leaveGroup(String addr){
        Log.d("leave", "leave group "+addr);
        mLibSiren.satLeave(addr);
    }
    // exam
    public static void sendStandardExamMessage(String content) {
        String examMsg = "{"
                +"\""+ "type" + "\"" + ":" + "\"" + mSeatNo + "\"" 
                + "," 
                +"\""+ "receiver" + "\"" + ":" + "\"" + "all" + "\"" 
                + "," 
                +"\""+ "mode" + "\"" + ":" + "\"" + Command.Modes.MODE_GLOBAL + "\"" 
                + "," 
                + "\""  + "command" + "\"" + ":" + "\"" + Command.COMMANDS.COMMAND_EXAM_STANDARD + "\""
                + ","
                +"\""+ "group" + "\"" + ":" + "\"" + "all" + "\"" 
                + ","
                + "\"" + "param" + "\"" + ":" + "\"" + content + "\""
                +"}";
        mLibSiren.sendMessage(examMsg);
    }

    // exam
    public static void sendIPcallMessage(String receiver) {

        String IpCallMsg = "type:cmd" + "," + "receiver:" + receiver + "," + "mode:"
                + Command.Modes.MODE_SELF_STUDY + "," + "command:"
                + Command.COMMANDS.COMMAND_SELF_IP_CALL + "," + "group:g100" + "," + "param:"
                + CommonUtil.getSeatNo();

        mLibSiren.sendMessage(Command.formatMessage(IpCallMsg));
    }

    // record
    public static void mp3LameInit(int channel, int sampleRate, int brate) {
        mLibSiren.mp3LameInit(channel, sampleRate, brate);
    }

    // record
    public static void mp3LameDestroy() {
        mLibSiren.mp3LameDestroy();
    }
    
    // record
    public static byte[] mp3LameFlush() {
       return  mLibSiren.mp3LameFlush();
    }
    

    public static byte[] mp3LameEncode(byte[] buffer, int len) {
        return mLibSiren.mp3LameEncode(buffer, len);
    }
    
    public static void sendLocalVGAOut(){
        mLibSiren.sendLocalVGAOut();
    }
    
    public static void sendRemoteVGAOut(){
        mLibSiren.sendRemoteVGAOut();
    }
    
    
    public static void setSound(int sound){
        mLibSiren.setSound(sound);
    }
    
    public static int getSound(){
       return  mLibSiren.getSound();
    }
    
    public static void startSat(){
        if (mLibSiren != null) {
            sIsInitSat = true;
            Log.d("lisiren", "startSat()");
            mLibSiren.startSat();
        }
    }
    
    public static void destorySat(){
        if (mLibSiren != null) {
            sIsInitSat = false;
            Log.d("lisiren", "destorySat()");
            mLibSiren.destorySat();
        }
    }
    
    
    public static void openMic(){
        Log.d("lisiren", "openMic()");
        mLibSiren.openMic();
    }
    
    public static void closeMic(){
        Log.d("lisiren", "closeMic()");
        mLibSiren.closeMic();
    }
    
    
    public static void openSpeaker(){
        mLibSiren.openSpeaker();
    }
    
    public static void closeSpeaker(){
        mLibSiren.closeSpeaker();
    }
}
