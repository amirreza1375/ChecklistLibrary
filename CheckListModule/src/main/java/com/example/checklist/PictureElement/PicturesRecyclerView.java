package com.example.checklist.PictureElement;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.checklist.R;

import java.util.ArrayList;

public class PicturesRecyclerView extends RecyclerView.Adapter<PicturesRecyclerView.ViewHolder>  {


    private Context context;
    private LinearLayout parent;
    private ArrayList<PicturePickerItemModel> models;
    private ItemClickCallBack callBack;
    private boolean FLAG_ENABLED = true ;

    public PicturesRecyclerView(Context context , LinearLayout parent, ArrayList<PicturePickerItemModel> models){

        this.context = context;
        this.parent = parent;
        this.models = models;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_picture_picker_library,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.holder = holder;
        holder.id = position;
        holder.txt.setText(models.get(position).getCategory());
        holder.cat = models.get(position).getCat_id();
        holder.cat_id = models.get(position).getId();
        holder.index = position;

        holder.img.setImageResource(R.drawable.ic_camera_alt_black_24dp);

        if (models.get(position).getPath() != null) {
            if (!models.get(position).getPath().equals("")) {
                Bitmap image = getBitmapByPath(models.get(position).getPath());
                holder.img.setImageBitmap(image);
            }
        }

        holder.img.setEnabled(models.get(position).isStatus());
    }



    @Override
    public int getItemCount() {
        return models.size();
    }

    public void setCallBack(ItemClickCallBack callBack) {
        this.callBack = callBack;
    }

    public boolean isFLAG_ENABLED() {
        return FLAG_ENABLED;
    }

    public void setFLAG_ENABLED(boolean FLAG_ENABLED) {
        this.FLAG_ENABLED = FLAG_ENABLED;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public int id;
        public ImageView img;
        public TextView txt;
        public ViewHolder holder;
        public int cat;
        public String cat_id;
        public int index;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txt = itemView.findViewById(R.id.txt);
            img = itemView.findViewById(R.id.img);

            itemView.setEnabled(FLAG_ENABLED);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callBack.onPictureItemClicked(holder,models.get(id));
                }
            });

        }
    }

    private Bitmap getBitmapByPath(String path) {
        return BitmapFactory.decodeFile(path);
    }

    public interface ItemClickCallBack{
        void onPictureItemClicked(ViewHolder holder, PicturePickerItemModel model);
    }

}