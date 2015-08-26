
package com.multimedia.room;

import java.util.ArrayList;
import java.util.List;

public class MediaRoomDemo {

    public static ArrayList<FilesEntity> sTestFiles = new ArrayList<FilesEntity>();
    static {
        FilesEntity entity0 = new FilesEntity();
        entity0.setName("aaaaa.mp3");
//        entity0.setUrl("http://192.168.1.2:8080/Music/Pianoboy - 漫天飞雪.mp3");
        entity0.setUrl("/mnt/usbdisk/music/aaaaa.mp3");
        entity0.setType("mp3");
        sTestFiles.add(entity0);

        FilesEntity entity1 = new FilesEntity();
        entity1.setName("bbbb.mp3");
//        entity1.setUrl("http://192.168.1.2:8080/Music/Pianoboy - 安静的午后.mp3");
        entity1.setUrl("/mnt/usbdisk/music/bbbbb.mp3");
        entity0.setType("mp3");
        sTestFiles.add(entity1);

        FilesEntity entity2 = new FilesEntity();
        entity2.setName("cccc.mp3");
//        entity2.setUrl("http://192.168.1.2:8080/Music/周华健 - 忘忧草.mp3");
        entity2.setUrl("/mnt/usbdisk/music/cccc.mp3");
        entity0.setType("mp3");
        sTestFiles.add(entity2);

    }
}
