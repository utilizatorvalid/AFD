package com.example.groza.filemanager.model;

import java.io.File;

/**
 * Created by groza on 12/28/2016.
 */

public class FileSizer {
    public String get_size(long file_length){
        String size = null;
        double bytes = file_length;
        double kilobytes = (bytes / 1024);
        double megabytes = (kilobytes / 1024);
        double gigabytes = (megabytes / 1024);
        if (gigabytes >=0.98)
            return String.format("%.2f", gigabytes) + " GB";
        if (megabytes >=0.98)
            return String.format("%.2f", megabytes) + " MB";
        if (kilobytes >=0.98)
            return String.format("%.2f", kilobytes) + " KB";
        return String.format("%.2f", bytes) + "B";
    }
    public long get_size(File file){
        if(file.isFile())
            return file.length();
        else
        {
            long count=0;
            File[] children = file.listFiles();
            for(File child: children){
                count+= get_size(child);
            }
            return count;

        }
    }
}
