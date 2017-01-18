package com.example.groza.filemanager.model;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.groza.filemanager.R;
import com.example.groza.filemanager.adapter.ItemAdapter;
import com.example.groza.filemanager.ui.TxtEdit;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by groza on 12/28/2016.
 */

public class FileExplorer implements ItemAdapter.ItemClickCallback {
    private static final String BUNDLE_EXTRAS = "BUNDLE_EXTRAS";
    private static final String FILE_PATH = "FILE_PATH";
    private SimpleDateFormat date_formater = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public void setCurrent_file(File current_file) {
        this.current_file = current_file;
    }

    private File current_file = null;
    private ArrayList<ListItem> roots;

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    private int depth = 0;
    private RecyclerView recView;

    public ItemAdapter getAdapter() {
        return adapter;
    }

    private ItemAdapter adapter;
    private ArrayList<ListItem> listData = new ArrayList<ListItem>();
    private FileWalker fileWalker;
    private Activity view;
    private Context context;
    private List<String> path = new LinkedList<String>();
    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        view.invalidateOptionsMenu();
    }

    private boolean selected;

    public FileExplorer(Activity v, Context c, boolean root_mode) {
        if (root_mode) {
            depth = 0;
            roots = new ArrayList<ListItem>();
            File sdCard = Environment.getExternalStorageDirectory();
            String modification_date = date_formater.format(new Date(sdCard.lastModified()));
            ListItem listItem = new ListItem(sdCard, "InternalMemory",
                    modification_date,
                    String.valueOf(sdCard.listFiles().length) + " items",
                    sdCard.getPath(),
                    R.drawable.hdd

            );
            roots.add(listItem);
            if (System.getenv("SECONDARY_STORAGE") != null) {
                File microSD = new File(System.getenv("SECONDARY_STORAGE"));
                modification_date = date_formater.format(new Date(microSD.lastModified()));
                listItem = new ListItem(microSD, microSD.getName(),
                        modification_date,
                        String.valueOf(microSD.listFiles().length) + " items",
                        microSD.getPath(),
                        R.drawable.sdcard
                );
                roots.add(listItem);

            }
        }
        this.selected =false;
        this.view = v;
        this.context = c;
        fileWalker = new FileWalker();
        listData.addAll(roots);

        recView = (RecyclerView) v.findViewById(R.id.rec_list);
        recView.setLayoutManager(new LinearLayoutManager(c));

        adapter = new ItemAdapter(listData, c);
        recView.setAdapter(adapter);
        adapter.setItemClickCallback(this);
    }

    @Override
    public void onItemClick(View v, int position) {
        ListItem listItem = listData.get(position);
        if (isSelected())
        {
            setSelected(adapter.doSelect(position, !listItem.isChecked()));
            adapter.notifyDataSetChanged();
            return;
        }

        if (listItem.getItem().isDirectory()) {
            this.setCurrent_file(listItem.getItem());
            this.depth++;
            refresh();
        } else {
            if (listItem.getItem().toString().contains(".txt")) {
                Intent i = new Intent(this.context, TxtEdit.class);
                Bundle extras = new Bundle();
                extras.putString(FILE_PATH, listItem.getItem().getAbsolutePath());
                i.putExtra(BUNDLE_EXTRAS, extras);
                this.context.startActivity(i);
            } else {
                this.open_file(listItem.getItem());

            }
        }

    }

    @Override
    public void onDeleteClick(View v, final int position) {
        final ListItem item = listData.get(position);
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);

        alertBuilder.setTitle("Do you want to delete this?");
        alertBuilder.setMessage(item.getPath());
        alertBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            deleteFile(item.getItem(),false);
            listData.remove(position);
            adapter.notifyDataSetChanged();
            }
        });

        alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();



    }

    private void deleteFile(File item, boolean quitely) {
        if(item.isDirectory()) {
            try {
                FileUtils.deleteDirectory(item);
                if(!quitely)
                     Toast.makeText(context, item.getName() + " has ben deleted", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(item.isFile()){
            try {
                FileUtils.forceDelete(item);
                if(!quitely)
                    Toast.makeText(context, item.getName() + " has ben deleted", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onArchiveClick(View v, int position) {
        ListItem item = listData.get(position);
        Toast toast = Toast.makeText(context, "Archive on " + item.getItem().getName(), Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onLongClick(View v, int position) {
        if(this.current_file != null){
        ListItem item = listData.get(position);
        if (!item.isChecked()) {
            setSelected(adapter.doSelect(position, true));
            setSelected(true);
        }
        else
           setSelected(adapter.doSelect(position,false));

        adapter.notifyDataSetChanged();
//        Toast toast = Toast.makeText(context, "Long pressed on " + item.getItem().getName(), Toast.LENGTH_SHORT);
//        toast.show();
        }

    }

    @Override
    public void onItemCheck(View v, int position, boolean checked) {
        setSelected(adapter.doSelect(position, checked));
        adapter.notifyDataSetChanged();
    }

    public void goBack() {

        if (isSelected())
        {
            this.setSelected(false);
            adapter.undoSelect();

            return;
        }

        depth--;
        if (depth == 0)
            current_file = null;

        if (this.current_file != null) {
            this.current_file = current_file.getParentFile();
            refresh();

        } else {
            this.listData.clear();
            listData.addAll(roots);
            adapter.notifyDataSetChanged();
            view.findViewById(R.id.fab_expand_menu_button).setVisibility(View.GONE);


        }


    }


    public void open_file(File url) {


        File file = url;
        Uri uri = Uri.fromFile(file);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        // Check what kind of file you are trying to open, by comparing the url with extensions.
        // When the if condition is matched, plugin sets the correct intent (mime) type,
        // so Android knew what application to use to open the file
        if (url.toString().contains(".doc") || url.toString().contains(".docx")) {
            // Word document
            intent.setDataAndType(uri, "application/msword");
        } else if (url.toString().contains(".pdf")) {
            // PDF file
            intent.setDataAndType(uri, "application/pdf");
        } else if (url.toString().contains(".ppt") || url.toString().contains(".pptx")) {
            // Powerpoint file
            intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        } else if (url.toString().contains(".xls") || url.toString().contains(".xlsx")) {
            // Excel file
            intent.setDataAndType(uri, "application/vnd.ms-excel");
        } else if (url.toString().contains(".zip") || url.toString().contains(".rar")) {
            // WAV audio file
            intent.setDataAndType(uri, "application/x-wav");
        } else if (url.toString().contains(".rtf")) {
            // RTF file
            intent.setDataAndType(uri, "application/rtf");
        } else if (url.toString().contains(".wav") || url.toString().contains(".mp3")) {
            // WAV audio file
            intent.setDataAndType(uri, "audio/x-wav");
        } else if (url.toString().contains(".gif")) {
            // GIF file
            intent.setDataAndType(uri, "image/gif");
        } else if (url.toString().contains(".jpg") || url.toString().contains(".jpeg") || url.toString().contains(".png")) {
            // JPG file
            intent.setDataAndType(uri, "image/jpeg");
        } else if (url.toString().contains(".txt")) {
            // Text file
            intent.setDataAndType(uri, "text/plain");
        } else if (url.toString().contains(".3gp") || url.toString().contains(".mpg") || url.toString().contains(".mpeg") || url.toString().contains(".mpe") || url.toString().contains(".mp4") || url.toString().contains(".avi")) {
            // Video files
            intent.setDataAndType(uri, "video/*");
        } else {
            //if you want you can also define the intent type for any other file

            //additionally use else clause below, to manage other unknown extensions
            //in this case, Android will show all applications installed on the device
            //so you can choose which application to use
            intent.setDataAndType(uri, "*/*");
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public File getCurrent_file() {
        return current_file;
    }
    public void renameFile(final File file){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Rename");

        LayoutInflater inflater= this.view.getLayoutInflater();
        final View dialog_view = inflater.inflate(R.layout.alert_new, null);
        alertDialogBuilder.setView(dialog_view);

        alertDialogBuilder.setPositiveButton("Rename", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                EditText input = (EditText) dialog_view.findViewById(R.id.create_new_item_name);
                String file_name = input.getText().toString();
                String directory = file.getParent();

                boolean isRenamed;
                if (file.isFile())
                    isRenamed = file.renameTo(new File(directory + File.separator + file_name+'.'+
                        FilenameUtils.getExtension(file.getAbsolutePath())));
                else
                    isRenamed = file.renameTo(new File(directory + File.separator + file_name));

                if(isRenamed){
                    listData.clear();
                    listData.addAll(fileWalker.get_files(current_file));
                    adapter.notifyDataSetChanged();
                }
                Toast.makeText(context,"renamed sucessful", Toast.LENGTH_SHORT).show();
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();


    }
    public void createFile() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.context);

        alertDialogBuilder.setTitle("New File");
        alertDialogBuilder.setIcon(R.drawable.ic_note_black_24dp);
        LayoutInflater inflater = this.view.getLayoutInflater();
        final View dialog_view = inflater.inflate(R.layout.alert_new, null);
        alertDialogBuilder.setView(dialog_view);
        alertDialogBuilder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                EditText input = (EditText) dialog_view.findViewById(R.id.create_new_item_name);
                String file_name = input.getText().toString();
                File newFile = new File(current_file.getAbsoluteFile() + File.separator + file_name + ".txt");

                if (newFile.exists()) {
                    Toast toast = Toast.makeText(context, "File already exists", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                try {
                    FileUtils.touch(newFile);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                Toast toast = Toast.makeText(context, newFile.getName() + " created", Toast.LENGTH_SHORT);
                toast.show();
//
                refresh();
                Intent i = new Intent(context, TxtEdit.class);
                Bundle extras = new Bundle();
                extras.putString(FILE_PATH, newFile.getAbsolutePath());
                i.putExtra(BUNDLE_EXTRAS, extras);
                context.startActivity(i);
            }
        });

        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog aler_dialog = alertDialogBuilder.create();
        aler_dialog.show();


    }

    public void createFolder() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.context);
        alertDialogBuilder.setTitle("New Folder");
        alertDialogBuilder.setIcon(R.drawable.ic_create_new_folder_black_24dp);

        LayoutInflater inflater = this.view.getLayoutInflater();
        final View dialog_view = inflater.inflate(R.layout.alert_new, null);

        alertDialogBuilder.setView(dialog_view);
        alertDialogBuilder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText input = (EditText) dialog_view.findViewById(R.id.create_new_item_name);
                String folder_name = input.getText().toString();
                File newFolder = new File(current_file.getAbsolutePath() + File.separator + folder_name);

                if (newFolder.exists()) {
                    Toast toast = Toast.makeText(context, "Folder already exists", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                try {
                    FileUtils.forceMkdir(newFolder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast toast = Toast.makeText(context, newFolder.getName() + " created", Toast.LENGTH_SHORT);
                toast.show();
                refresh();
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }


    public void handdlePaste() {
        final FileExplorer thisFE = this;
                FileCopyer task = new FileCopyer(thisFE, context, current_file);
                task.execute(adapter.getClipboard().first);



    }
    public void alfterPaste() {
        String type = adapter.getClipboard().second;
        if (type.equals("move")){
            for(File list_item : adapter.getClipboard().first){
                deleteFile(list_item,true);
            }

        }
        this.getAdapter().setClipboard(new Pair(new ArrayList<ListItem>(),"") );
        refresh();
        view.invalidateOptionsMenu();
    }
    public void refresh()
    {
        if(current_file!=null){
            listData.clear();
            listData.addAll(fileWalker.get_files(current_file));
            sortListData();
            view.findViewById(R.id.fab_expand_menu_button).setVisibility(View.VISIBLE);

            adapter.notifyDataSetChanged();
            view.setTitle(current_file.getAbsolutePath());
        }
        else
        {
           view.findViewById(R.id.fab_expand_menu_button).setVisibility(View.GONE);
            path.clear();
            path.add("/");
            String joined = "";
            for (String folder: path){
                joined+=folder + "/";
            }
            view.setTitle("/");
        }

    }

    public void sortListData(){

        Collections.sort(listData, new Comparator<ListItem>() {
            @Override
            public int compare(ListItem listItem, ListItem t1) {
                return listItem.compareTo(t1, "ext");
            }
        });
    }

    public void delete_files(ArrayList<File> selected) {
        final List<File>files = selected;

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);

        alertBuilder.setTitle("Do you want to delete this?");
        alertBuilder.setMessage(selected.toString());
        alertBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                for(File list_item : files){
                    deleteFile(list_item,true);
                }
                refresh();
            }
        });

        alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();

    }

    public void clear_clipboard() {
        adapter.setClipboard(new Pair(new ArrayList<ListItem>(),""));

    }
}
