
package com.multimedia.room;


import java.io.Serializable;

public class MediaMessage  implements Serializable{
    private static final long serialVersionUID = 2520312330727845194L;
    private String mTpye;
    private String mReveiver;
    private String mMode;
    private String mCommand;
    private String mParams;
    private String mGroup;
    private boolean mIsMe;

    public String getTpye() {
        return mTpye;
    }

    public void setTpye(String mTpye) {
        this.mTpye = mTpye;
    }

    public String getReveiver() {
        return mReveiver;
    }

    public void setReveiver(String mReveiver) {
        this.mReveiver = mReveiver;
    }

    public String getMode() {
        return mMode;
    }

    public void setMode(String mMode) {
        this.mMode = mMode;
    }

    public String getCommand() {
        return mCommand;
    }

    public void setCommand(String mCommand) {
        this.mCommand = mCommand;
    }

    public String getGroup() {
        return mGroup;
    }

    public void setGroup(String group) {
        this.mGroup = group;
    }

    public String getParams() {
        return mParams;
    }

    public void setParams(String mParams) {
        this.mParams = mParams;
    }

    
    
    
    
    public boolean isMe() {
        return mIsMe;
    }

    public void setIsMe(boolean isMe) {
        this.mIsMe = isMe;
    }

    @Override
    public String toString() {
        return "MediaMessage [mTpye=" + mTpye + ", mReveiver=" + mReveiver
                + ", mMode=" + mMode + ", mGroup=" + mGroup + ", mCommand=" + mCommand
                + ", mParams="
                + mParams + "]";
    }

    public MediaMessage(String mTpye, String mReveiver, String mMode,
            String mCommand, String mGroup, String mParams) {
        super();
        this.mTpye = mTpye;
        this.mReveiver = mReveiver;
        this.mMode = mMode;
        this.mCommand = mCommand;
        this.mParams = mParams;
        this.mGroup = mGroup;
        setIsMe(CommonUtil.getSeatNo().equals(this.mReveiver)?true:false);
    }

    public MediaMessage() {
		// TODO Auto-generated constructor stub
	}
}
