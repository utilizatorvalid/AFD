package com.example.groza.filemanager.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.groza.filemanager.R;

import org.apache.commons.io.FilenameUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Date;

/**
 * Created by groza on 12/28/2016.
 */

public class ListItem {
    private boolean checked;
    private File item;
    private String title;
    private int imageID;

    private String modifiedDate;
    private String info;
    private String path;

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImageID() {
        return imageID;
    }

    public void setImageID(int imageID) {
        this.imageID = imageID;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public File getItem() {
        return item;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ListItem listItem = (ListItem) o;

        return item != null ? item.equals(listItem.item) : listItem.item == null;

    }

    @Override
    public int hashCode() {
        return item != null ? item.hashCode() : 0;
    }

    ListItem(File item, String title, String modifiedDate, String info, String path, int iconID){
        this.checked = false;
        this.item = item;
        this.title = title;
        this.modifiedDate = modifiedDate;
        this.info = info;
        this.path = path;
        File file = new File(path);
        if (iconID==-1)
        {
            if (file.isDirectory())
                this.imageID = R.drawable.folder;
            else{
                String extension = FilenameUtils.getExtension(file.getAbsolutePath());
                switch (extension) {
                    case "txt":
                        this.imageID = R.drawable.txt;
                        break;
                    case "mp3":

                        this.imageID = R.drawable.mp3;
                        break;
                    case "mp4":
                        this.imageID = R.drawable.mp4;
                        break;
                    case "jpg":
                        this.imageID = R.drawable.img;
                        break;
                    case "jpeg":
                        this.imageID = R.drawable.img;
                        break;
                    case "png":
                        this.imageID = R.drawable.img;
                        break;
                    case "pdf":
                        this.imageID = R.drawable.pdf;
                        break;
                    case "doc":
                        this.imageID = R.drawable.doc;
                        break;
                    case "docx":
                        this.imageID = R.drawable.doc;
                        break;
                    default:
                        this.imageID = R.drawable.default_list_item;

                }
            }
        }else
        {
            this.imageID = iconID;
        }


    }
    public Bitmap getbitamp(String path){
        Bitmap imgthumBitmap=null;
        try
        {

            final int THUMBNAIL_SIZE = 64;

            FileInputStream fis = new FileInputStream(path);
            imgthumBitmap = BitmapFactory.decodeStream(fis);

            imgthumBitmap = Bitmap.createScaledBitmap(imgthumBitmap,
                    THUMBNAIL_SIZE, THUMBNAIL_SIZE, false);

            ByteArrayOutputStream bytearroutstream = new ByteArrayOutputStream();
            imgthumBitmap.compress(Bitmap.CompressFormat.JPEG, 100,bytearroutstream);


        }
        catch(Exception ex) {

        }
        return imgthumBitmap;
    }

    public int compareTo(ListItem t1, String type) {
        if(type == "ext"){

            String s1 = this.getItem().getAbsolutePath();
            String s2 = t1.getItem().getAbsolutePath();
            final int s1Dot = s1.lastIndexOf('.');
            final int s2Dot = s2.lastIndexOf('.');
            if ((s1Dot == -1) == (s2Dot == -1)) { // both or neither
                s1 = s1.substring(s1Dot + 1);
                s2 = s2.substring(s2Dot + 1);
                return s1.compareTo(s2);
            } else if (s1Dot == -1) { // only s2 has an extension, so s1 goes first
                return -1;
            } else { // only s1 has an extension, so s1 goes second
                return 1;
            }
        }
        return 0;
    }
}
