package com.example.groza.filemanager.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.groza.filemanager.R;
import com.example.groza.filemanager.model.ListItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by groza on 12/28/2016.
 */

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemHolder>{
    private List<ListItem> listData;
    private LayoutInflater inflater;

    public ArrayList<File> getSelectedItems() {
        return (ArrayList<File>) selectedItems;
    }

    private List<File> selectedItems;
    private Pair clipboard;
    public void setItemClickCallback(ItemClickCallback itemClickCallback) {
        this.itemClickCallback = itemClickCallback;
    }


    private ItemClickCallback itemClickCallback;

    public void undoSelect() {
        for(ListItem item :listData){
            item.setChecked(false);
        }

        selectedItems.clear();
        notifyDataSetChanged();


    }
    public boolean doSelect(int position, boolean checked){
        ListItem item = listData.get(position);
        item.setChecked(checked);
        if (checked){
            selectedItems.add(item.getItem());
        }
        else{
           int index = selectedItems.indexOf(item.getItem());
            selectedItems.remove(index);
        }

        Log.d("SELECTED",selectedItems.toString());

        if(getCheckedCount()>0)
            return true;
        else
            return false;
    }

    public void setClipboard(Pair<List<ListItem>,String> clipboard) {
        this.clipboard = clipboard;
    }

    public Pair<List<File>,String> getClipboard() {
        return clipboard;
    }

    public interface ItemClickCallback{
        void onItemClick(View v, int position);
        void onDeleteClick(View v, int position);
        void onArchiveClick(View v, int position);
        void onLongClick(View v, int position);
        void onItemCheck(View v, int position, boolean checked);
    }

    public ItemAdapter(List<ListItem> listData, Context c){
        this.inflater = LayoutInflater.from(c);
        this.listData = listData;
        this.selectedItems = new ArrayList<File>();
        this.clipboard = new Pair(new ArrayList<ListItem>(),"");
    }
    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item,parent, false);
        return new ItemHolder(view);

    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        ListItem item = listData.get(position);
        if (item.getImageID()!=-1)
            holder.icon.setImageResource(item.getImageID());
        else
            holder.icon.setImageBitmap(item.getbitamp(item.getPath()));
        holder.title.setText(item.getTitle());
        holder.modification_date.setText(item.getModifiedDate());
        holder.info.setText(item.getInfo());
        Log.d("ITEM_BIND", String.valueOf(item.isChecked()));
        holder.checkBox.setChecked(item.isChecked());
        if(getCheckedCount()==0)
            holder.checkBox.setVisibility(View.GONE);

        else
            holder.checkBox.setVisibility(View.VISIBLE);
        holder.bind();
        if(item.getImageID() ==R.drawable.hdd || item.getImageID() ==R.drawable.sdcard )
            holder.deleteLayout.setVisibility(View.GONE);
        else
            holder.deleteLayout.setVisibility(View.VISIBLE);
    }


    @Override
    public int getItemCount() {
        return listData.size();
    }
    public int getCheckedCount(){

        int count = 0;
        for(ListItem item:listData){
            if(item.isChecked())
                count++;
        }
        return count;
    }
    class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private ImageView icon;
        private TextView title;
        private TextView modification_date;
        private TextView info;
        private View deleteLayout;
        private View archiveLayout;
        private CheckBox checkBox;

        private View container;
        public ItemHolder(View itemView){

            super(itemView);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox_id);
            icon =  (ImageView)itemView.findViewById(R.id.im_item_icon);
            title = (TextView) itemView.findViewById(R.id.lbl_item_title);
            modification_date = (TextView) itemView.findViewById(R.id.lbl_item_modification_date);
            info = (TextView) itemView.findViewById(R.id.lbl_item_info);

            container = itemView.findViewById(R.id.cont_item_root);
            container.setOnClickListener(this);
            container.setOnLongClickListener(this);
//            archiveLayout = itemView.findViewById(R.id.list_item_archive);
            deleteLayout = itemView.findViewById(R.id.list_item_delete);


        }
        public void bind(){
            deleteLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                        mDataSet.remove(getAdapterPosition());
//                        notifyItemRemoved(getAdapterPosition());
                    itemClickCallback.onDeleteClick(v, getAdapterPosition());
                }
            });
//            archiveLayout.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
////                        mDataSet.remove(getAdapterPosition());
////                        notifyItemRemoved(getAdapterPosition());
//                    itemClickCallback.onArchiveClick(v, getAdapterPosition());
//                }
//            });
            checkBox.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    itemClickCallback.onItemCheck(v, getAdapterPosition(),((CheckBox )checkBox).isChecked() );
                }
            });


//                textView.setText(data);
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            Log.d("ITEM_CLICK", String.valueOf(id));
            switch (id){
                case R.id.cont_item_root:
                    itemClickCallback.onItemClick(v, getAdapterPosition());
                default:
                    break;
            }
        }

        @Override
        public boolean onLongClick(View v) {
            itemClickCallback.onLongClick(v,getAdapterPosition());

            return true;
        }
    }
}
