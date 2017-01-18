package com.example.groza.filemanager.model;

import android.util.Log;

import org.apache.commons.io.DirectoryWalker;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by groza on 12/28/2016.
 */


public class FileWalker extends DirectoryWalker {
    private SimpleDateFormat date_formater = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private FileSizer fileSizer = new FileSizer();
    public FileWalker(){
        super();
    }
    public ArrayList<ListItem> get_files(File startDirectory) {
        List results = new ArrayList();
        try {
            walk(startDirectory, results);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("FILES",results.toString());
        return (ArrayList<ListItem> )results;
    }

    protected boolean handleDirectory(File directory, int depth, Collection results) {
        // level>=2 of directory  then skip
        if (depth>=2)
            return false;
        if(directory.getName().startsWith("."))
            return false;

        if(depth == 1) {
            File files[] = directory.listFiles();
            int childItems;
            if(files == null){
                childItems = 0;
            }else
                childItems= files.length;
            String modification_date =  date_formater.format(new Date(directory.lastModified()));
            ListItem listItem = new ListItem(directory, directory.getName(),
                    modification_date,
                    String.valueOf(childItems)+" items",
                    directory.getPath(),
                    -1
            );
            results.add(listItem);
        }
        return true;

    }

    protected void handleFile(File file, int depth, Collection results) {
        // delete file and add to list of deleted
//        file.delete();
        if(depth>1)
            return ;
        if(file.getName().startsWith("."))
            return ;
        String modification_date = date_formater.format(new Date(file.lastModified()));
        String info = fileSizer.get_size(file.length());
        ListItem listItem = new ListItem(file, file.getName(),modification_date,info, file.getPath(),-1);
        results.add(listItem);
    }
}
