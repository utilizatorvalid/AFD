package com.example.groza.filemanager.ui;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.groza.filemanager.R;
import com.example.groza.filemanager.adapter.ItemAdapter;
import com.example.groza.filemanager.model.FileExplorer;
import com.example.groza.filemanager.model.FileWalker;
import com.example.groza.filemanager.model.ListItem;
import com.getbase.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    FileExplorer fe;
    final String CURRENT_PATH = "CURRENT_PATH";
    final String CLIPBOARD_FILES = "CLIPBOARD_FILES";
    final String CLIPBOARD_TYPE = "CLIPBOARD_TYPE";
    final String DEPTH = "DEPTH";

    @Override
    public void onBackPressed() {
        Log.d("BACK",String.valueOf(fe.getCurrent_file()));
        if (fe.getCurrent_file() == null) {
            super.onBackPressed();
        } else
        {

            fe.goBack();
        }
        fe.refresh();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            fe = new FileExplorer(this, this,  true );
            fe.refresh();
        } else {

              fe = new FileExplorer(this, this,  true );

              String current_path = savedInstanceState.getString(CURRENT_PATH);
                if(current_path != null){
                    fe.setCurrent_file(new File(current_path));
                    fe.setDepth(savedInstanceState.getInt(DEPTH));
                    fe.refresh();
                }
            List<String> files_paths = savedInstanceState.getStringArrayList(CLIPBOARD_FILES);
            String clipboard_type = savedInstanceState.getString(CLIPBOARD_TYPE);
            if(files_paths!=null){
                  List<File> files = new ArrayList<File>();
                  for(String path : files_paths){
                      files.add(new File(path));
                  }

                  fe.getAdapter().setClipboard(new Pair(files,clipboard_type));
                fe.refresh();
              }

//            fe = (FileExplorer)savedInstanceState.getParcelable(MAIN_ACTIVITY_FE);
//            fe.refresh();
            invalidateOptionsMenu();
        }

        final FloatingActionButton newFileAction = (FloatingActionButton)findViewById(R.id.new_file);
        newFileAction.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                fe.createFile();


            }
        });
        final FloatingActionButton newFolderAction = (FloatingActionButton)findViewById(R.id.new_folder);
        newFolderAction.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                fe.createFolder();

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        ArrayList<File> selected = (ArrayList<File>) fe.getAdapter().getSelectedItems().clone();
        switch (id) {
            case R.id.copy_option:

                fe.getAdapter().setClipboard(new Pair(selected,"copy"));
                fe.getAdapter().undoSelect();
                fe.setSelected(false);
                break;
            case R.id.move_option:
                fe.getAdapter().setClipboard(new Pair(selected,"move"));
                fe.getAdapter().undoSelect();
                fe.setSelected(false);
                break;
            case R.id.rename_option:
                File listItem = fe.getAdapter().getSelectedItems().get(0);
                fe.renameFile(listItem);
                fe.getAdapter().undoSelect();

                fe.setSelected(false);
                fe.refresh();

                break;
            case R.id.delete_option:
                fe.delete_files(selected);
                fe.refresh();

            case R.id.paste_option:
                fe.handdlePaste();
                break;

            case R.id.refresh_option:
                fe.refresh();
                break;
            case R.id.abort_copy:
                fe.clear_clipboard();
                invalidateOptionsMenu();
                break;

            default:


        }
        Log.d("CLIPBOARD", fe.getAdapter().getClipboard().first.toString()+" "+fe.getAdapter().getClipboard().second);
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {

        if (fe.isSelected()) {
            menu.findItem(R.id.move_option).setVisible(true);
            menu.findItem(R.id.copy_option).setVisible(true);
            menu.findItem(R.id.delete_option).setVisible(true);
            if (fe.getAdapter().getCheckedCount()==1)
                menu.findItem(R.id.rename_option).setVisible(true);
        }
        else
        {
            Pair<List<File>,String> clipboard = fe.getAdapter().getClipboard();
            if(clipboard.first.size()>0) {
                menu.findItem(R.id.paste_option).setVisible(true);
                menu.findItem(R.id.abort_copy).setVisible(true);
            }
            else {
                menu.findItem(R.id.paste_option).setVisible(false);
                menu.findItem(R.id.abort_copy).setVisible(false);

            }
            menu.findItem(R.id.move_option).setVisible(false);
            menu.findItem(R.id.copy_option).setVisible(false);
            menu.findItem(R.id.rename_option).setVisible(false);
            menu.findItem(R.id.delete_option).setVisible(false);


        }
        return super.onPrepareOptionsPanel(view, menu);
    }

    @Override
    public void onSaveInstanceState(Bundle saveInstanceState) {
        super.onSaveInstanceState(saveInstanceState);
        Log.d("MAIN_ACTIVITY_FE", "MainActivity: onSaveInstanceState");
        if (fe.getCurrent_file()!=null)
            saveInstanceState.putString(CURRENT_PATH, fe.getCurrent_file().getAbsolutePath());
        Pair<List<File>,String> clipboard = fe.getAdapter().getClipboard();
        List<String> files_to_copy =new ArrayList<String>();
        if(clipboard.first.size()>0)
        {
            for(File file:clipboard.first){
                files_to_copy.add(file.getAbsolutePath());
            }
            saveInstanceState.putStringArrayList(CLIPBOARD_FILES, (ArrayList<String>) files_to_copy);
            saveInstanceState.putString(CLIPBOARD_TYPE, clipboard.second);

        }
        saveInstanceState.putInt(DEPTH, fe.getDepth());

    }


}
