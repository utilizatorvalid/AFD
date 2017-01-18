package com.example.groza.filemanager.ui;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.groza.filemanager.R;
import com.onegravity.rteditor.RTEditText;
import com.onegravity.rteditor.RTManager;
import com.onegravity.rteditor.RTToolbar;
import com.onegravity.rteditor.api.RTApi;
import com.onegravity.rteditor.api.RTMediaFactoryImpl;
import com.onegravity.rteditor.api.RTProxyImpl;
import com.onegravity.rteditor.api.format.RTFormat;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import static com.example.groza.filemanager.R.menu.*;

public class TxtEdit extends AppCompatActivity {
    private static final String BUNDLE_EXTRAS = "BUNDLE_EXTRAS";
    private static final String FILE_PATH = "FILE_PATH";
    private RTApi rtApi;
    private RTManager rtManager;
    private File working_file;
    private RTEditText rtEditText;
    private String start_string ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.RTE_ThemeLight);
        setContentView(R.layout.activity_txt_edit);
        Bundle extras = getIntent().getBundleExtra(BUNDLE_EXTRAS);

        working_file = new File(extras.getString(FILE_PATH));
        String file_content = null;
        try {
            file_content = FileUtils.readFileToString(working_file, "UTF-8");
            start_string = file_content;
        } catch (IOException e) {
            e.printStackTrace();
        }

        setTitle("Txt edit:"+' '+ working_file.getName());

        // create RTManager
        rtApi = new RTApi(this, new RTProxyImpl(this), new RTMediaFactoryImpl(this, true));
        rtManager = new RTManager(rtApi, savedInstanceState);

// register toolbar
        ViewGroup toolbarContainer = (ViewGroup) findViewById(R.id.rte_toolbar_container);

        RTToolbar rtToolbar0 = (RTToolbar) findViewById(R.id.rte_toolbar);
        if (rtToolbar0 != null) {
            rtManager.registerToolbar(toolbarContainer, rtToolbar0);
        }
        // register toolbar 1 (if it exists)
        RTToolbar rtToolbar1 = (RTToolbar) findViewById(R.id.rte_toolbar_character);
        if (rtToolbar1 != null) {
            rtManager.registerToolbar(toolbarContainer, rtToolbar1);
        }

        // register toolbar 2 (if it exists)
        RTToolbar rtToolbar2 = (RTToolbar) findViewById(R.id.rte_toolbar_paragraph);
        if (rtToolbar2 != null) {
            rtManager.registerToolbar(toolbarContainer, rtToolbar2);
        }

// register editor & set text
        rtEditText = (RTEditText) findViewById(R.id.rtEditText);
        rtManager.registerEditor(rtEditText, true);
        rtEditText.setRichTextEditing(true, file_content);
        Log.d("SET_TEXT",file_content);
//        rtEditText.requestFocus();

    }

    @Override
    public void onBackPressed() {
        final AppCompatActivity txtA = this;
        final String content = this.rtEditText.getText(RTFormat.HTML);
        AlertDialog.Builder alertDiBuilder = new AlertDialog.Builder(this);
        alertDiBuilder.setTitle("Cancel");
        alertDiBuilder.setMessage("Do you want to save changes?");
        alertDiBuilder.setPositiveButton("Yes",new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    FileUtils.writeStringToFile(working_file, content, "UTF-8");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast toast = Toast.makeText(getApplicationContext(), "File saved", Toast.LENGTH_SHORT);
                toast.show();
                finish();
            }
        });

        alertDiBuilder.setNegativeButton("No", new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });


        AlertDialog alertDialog = alertDiBuilder.create();
        Log.d("SAVE_FILE", "'"+content+"'<->"+"'"+start_string+"'");
        if (!content.equals(start_string))
            alertDialog.show();
        else
            finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        rtManager.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        rtManager.onDestroy(isFinishing());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.txt_edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.menu_txt_edit_save:
                String content = this.rtEditText.getText(RTFormat.HTML);
                this.start_string = content;
                try {
                    FileUtils.writeStringToFile(working_file, content, "UTF-8");
                    Toast toast = Toast.makeText(getApplicationContext(), "File saved", Toast.LENGTH_SHORT);
                    toast.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.d("FILE_EDIT",content);

        }
        return true;
    }
}
