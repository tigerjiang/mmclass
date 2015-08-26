
package com.multimedia.room;

public class Command {

    static class Modes {
        public static final String MODE_TEACH = "teach";
        public static final String MODE_GROUP = "group";
        public static final String MODE_SELF_STUDY = "study";
        public static final String MODE_EXAM = "exam";
        public static final String MODE_GLOBAL = "global";
    }

    static class COMMANDS {
        public static final String COMMAND_CLEAN = "clean";
        public static final String COMMAND_ALL_CLEAN = "allclean";
        public static final String COMMAND_GLOBAL_ALL_CALLS = "allcalls";
//        public static final String COMMAND_GLOBAL_BROADCAST = "broadcast";
        public static final String COMMAND_GLOBAL_HANDSUP = "hands_up";
        public static final String COMMAND_GLOBAL_ONLINE = "online";

        public static final String COMMAND_TEACH_DEMONSTRATION = "demonstration";
        public static final String COMMAND_TEACH_INTERCOM = "intercom";
        public static final String COMMAND_TEACH_MONITOR = "monitor";
        public static final String COMMAND_TEACH_DICTATION = "dictation";
        public static final String COMMAND_TEACH_ALLCALLS = "allcalls";
        public static final String COMMAND_TEACH_TRANSLATE = "translate";
        public static final String COMMAND_TEACH_TEST = "test";
        public static final String COMMAND_GROUP_BROADCAST = "broadcast";
        public static final String COMMAND_SETTING = "setting";
        public static final String COMMAND_UPGRADE = "upgrade";
        public static final String COMMAND_REBOOT = "reboot";
        public static final String COMMAND_CHANGE_IP = "change_ip";
        public static final String COMMAND_RECOVERY = "recovery";
        public static final String COMMAND_VERSION = "version";
        public static final String COMMAND_TEACH_AD = "adjust";
        public static final String COMMAND_TEACH_RESPONDER = "responder";
        public static final String COMMAND_EXAM_STANDARD = "standard";
        public static final String COMMAND_EXAM_DISCUSS = "discuss";
        public static final String COMMAND_EXAM_ORAL = "oral";
        public static final String COMMAND_EXAM = "exam";
        public static final String COMMAND_SELF_STUDY = "study";
        public static final String COMMAND_GROUP = "group";
        public static final String COMMAND_GROUP_DISCUSS = "discuss";
        public static final String COMMAND_GLOBAL_SYNC = "sync";
        // TODO ..
        public static final String COMMAND_GROUP_ATTEND_DISCUSS = "attend_discuss";
        public static final String COMMAND_GROUP_EXIT_DISCUSS = "exit_discuss";
        public static final String COMMAND_GROUP_ADJUST = "adjust";

        public static final String COMMAND_SELF_IP_CALL = "ip_call";
        // TODO ..
        public static final String COMMAND_SELF_REFRESH = "resource_refresh";
        public static final String COMMAND_SET_SEAT = "set_seat";
        public static final String COMMAND_CHECK_FAULT = "check_fault";

    }

    static class Params {
        public static final String INTEGRATIVE_TEACH = "integrative_teach";
        public static final String ORAL_TEACH = "oral_teach";
        public static final String SELF_STUDY = "self_study";
    }

    public static String formatMessage(String message) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        String[] params = message.split(",");
        for (int i = 0; i < params.length; i++) {
            String[] keyValue = params[i].split(":");
            String key = keyValue[0];
            String value = keyValue[1];
            sb.append("\"").append(key).append("\"").append(":")
                    .append("\"").append(value).append("\"").append(",");
        }
        sb.deleteCharAt(sb.lastIndexOf(",")).append("}");
        return sb.toString();
    }

    public static String formatSimpleMessage(String message) {
        StringBuilder sb = new StringBuilder();
        String[] params = message.split(",");
        for (int i = 0; i < params.length; i++) {
            String[] keyValue = params[i].split(":");
            String key = keyValue[0];
            String value = keyValue[1];
            sb.append("\"").append(key).append("\"").append(":")
                    .append("\"").append(value).append("\"").append(",");
        }
        sb.deleteCharAt(sb.lastIndexOf(","));
        return sb.toString();
    }

}
