package com.example.groza.filemanager.model;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.groza.filemanager.ui.MainActivity;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by groza on 1/7/2017.
 */

public class FileCopyer  extends AsyncTask<List<File>,Long, Boolean>{
    private final File destFolder;
    private FileSizer fSizer;
    private Long totalSize = new Long(0);
    private Long copiedSize = new Long(0);
    String size_text;
    ProgressDialog progress;
    Context ctx;
    private FileExplorer fe;


    public FileCopyer(FileExplorer fe, Context ctx, File souruce_file){
        this.ctx = ctx;
        this.destFolder = souruce_file;
        fSizer = new FileSizer();
        this.fe = fe;
    }


    @Override
    protected Boolean doInBackground(List<File>... files) {
        for(File file: files[0]){
            totalSize+=fSizer.get_size(file);
        }
        size_text = fSizer.get_size(totalSize);

        Log.d("TOTAL_SIZE", totalSize.toString());
        for(File file:files[0]){
            try{
                copyFolder(file,new File(destFolder.getPath()+File.separator + file.getName()));
            }catch(IOException e){
                e.printStackTrace();
                //error, just exit
            }
            long current_size= fSizer.get_size(file);


//            if(totalSize!=0)
//                Log.d("PROGRESS",String.valueOf((copiedSize*100)/this.totalSize));
        }
        return true;
    }

    private void copyFolder(File src, File dest)throws IOException{

            if(src.isDirectory()){

                //if directory not exists, create it
                if(!dest.exists()){
                    dest.mkdir();
//                    Log.d("COPY","Directory copied from"
//                            + src + "  to " + dest);
                }

                //list all the directory contents
                String files[] = src.list();

                for (String file : files) {
                    //construct the src and dest file structure
                    File srcFile = new File(src, file);
                    File destFile = new File(dest, file);
                    //recursive copy
                    copyFolder(srcFile,destFile);
                }

            }else{
//                Log.d("COPY","File copied from"
//                        + src + "  to " + dest);
                //if file, then copy it
                //Use bytes stream to support all file types
                InputStream in = new FileInputStream(src);
                OutputStream out = new FileOutputStream(dest);

                byte[] buffer = new byte[4096];

                int length;
                //copy the file content in bytes
                while ((length = in.read(buffer)) > 0){
                    out.write(buffer, 0, length);
                    copiedSize+=length;
                    int progress_proc = new Long((copiedSize*100)/totalSize).intValue();
                    if(progress_proc%3==0)
                        publishProgress(copiedSize);
                }

                in.close();
                out.close();
            }
    }



    @Override
    protected void onPreExecute() {
        progress = ProgressDialog.show(ctx, "", "Loading...", true);

        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Boolean success) {
        progress.dismiss();
        Log.d("COPY", "Files moved");
        fe.alfterPaste();

    }

    @Override
    protected void onProgressUpdate(Long... values) {
        progress.setMessage("Transferred " + fSizer.get_size(values[0]) + " of "+ size_text);

    }


}
