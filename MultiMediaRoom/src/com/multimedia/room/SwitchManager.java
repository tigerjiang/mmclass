
package com.multimedia.room;

import java.util.ArrayList;
import java.util.List;

public class SwitchManager {

    private static SwitchManager sSwitchManager = new SwitchManager();
    //hands up switch
    private static List<ISwitch> mHandUpSwitchList = new ArrayList<ISwitch>();
    // dictation switch
    private static List<ISwitch> mDictationSwitchList = new ArrayList<ISwitch>();
    
    private static List<ISwitch> mTestSwitchList = new ArrayList<ISwitch>();
    
    // dictation switch
    private static List<ISwitch> mTranslateSwitchList = new ArrayList<ISwitch>();

    // exam switch
    private static List<ISwitch> mExamSwitchList = new ArrayList<ISwitch>();
    
    
    private SwitchManager() {

    }

    public static SwitchManager getInstance() {
        if (sSwitchManager == null) {
            return new SwitchManager();
        } else {
            return sSwitchManager;
        }
    }

    public void registerHandUpSwitch(ISwitch switch1) {
        mHandUpSwitchList.add(switch1);
    }

    public void unregisterHandUpSwitch(ISwitch switch1) {
        mHandUpSwitchList.remove(switch1);
    }
    public void registerDictationSwitch(ISwitch switch1) {
        mDictationSwitchList.add(switch1);
    }

    public void unregisterDictationSwitch(ISwitch switch1) {
        mDictationSwitchList.remove(switch1);
    }
    
    
    
    public void registerTestSwitch(ISwitch switch1) {
        mTestSwitchList.add(switch1);
    }

    public void unregisterTestSwitch(ISwitch switch1) {
        mTestSwitchList.remove(switch1);
    }
    public void registerTranslateSwitch(ISwitch switch1) {
        mTranslateSwitchList.add(switch1);
    }

    public void unregisterTranslateSwitch(ISwitch switch1) {
        mTranslateSwitchList.remove(switch1);
    }
    
    public void registerExamSwitch(ISwitch switch1) {
        mExamSwitchList.add(switch1);
    }

    public void unregisterExamSwitch(ISwitch switch1) {
        mExamSwitchList.remove(switch1);
    }
    
    public List<ISwitch> getSwitchs() {
        return mHandUpSwitchList;
    }

    public void notifyHandUpSwitchStatus(boolean isOn) {
        for (ISwitch is : mHandUpSwitchList) {
            if (isOn) {
                is.switchOn();
            } else {
                is.switchOff();
            }
        }
    }
    
    
    public void notifyExamSwitchStatus(boolean isOn) {
        for (ISwitch is : mExamSwitchList) {
            if (isOn) {
                is.switchOn();
            } else {
                is.switchOff();
            }
        }
    }
    
    
    public void notifyDictationSwitchStatus(boolean isOn) {
        for (ISwitch is : mDictationSwitchList) {
            if (isOn) {
                is.switchOn();
            } else {
                is.switchOff();
            }
        }
    }
    
    public void notifyTranslateSwitchStatus(boolean isOn) {
        for (ISwitch is : mTranslateSwitchList) {
            if (isOn) {
                is.switchOn();
            } else {
                is.switchOff();
            }
        }
    }
    
    public void notifyTestSwitchStatus(boolean isOn) {
        for (ISwitch is : mTestSwitchList) {
            if (isOn) {
                is.switchOn();
            } else {
                is.switchOff();
            }
        }
    }
}
