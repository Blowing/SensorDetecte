package com.blowing.sensordetecte.util;

import android.os.Environment;

import java.io.File;
import java.io.IOException;

/**
 * Created by wujie
 * on 2019/11/8/008.
 */
public class FileUtil {



    private static final String soundFilePath =
            Environment.getExternalStorageDirectory().getPath()+"/sensorSound";

    static {
        File srcFile = new File(soundFilePath);
        if (!srcFile.exists()) {
            srcFile.mkdirs();
        }
    }

    public static File createFile(String fileName) {


        File soundFile = new File(soundFilePath+"/" + fileName);
        if (soundFile.exists()) {
            soundFile.delete();
        }
        try {
            soundFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  soundFile;

    }

}
