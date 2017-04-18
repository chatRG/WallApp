package com.wallapp.utils;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.Nullable;

import com.wallapp.R;

import java.io.File;

public class FileUtils {
    private Context context;

    public FileUtils(Context context) {
        this.context = context;
    }

    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean success = deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    @Nullable
    public File getLastModFile() {
        String dirPath = Environment
                .getExternalStorageDirectory()
                .getAbsolutePath() + File.separator + context.getString(R.string.app_name) + File.separator;
        File dir = new File(dirPath);
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            return null;
        }

        File lastModFile = files[0];
        for (int i = 1; i < files.length; i++) {
            if (lastModFile.lastModified() < files[i].lastModified()) {
                lastModFile = files[i];
            }
        }
        return lastModFile;
    }

    public void deleteCache() {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
