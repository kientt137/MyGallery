package fimo.kientt.mygallery;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by kient on 3/16/2018.
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> implements Serializable {
    private Context context;
    ArrayList<String> mDataset;

    public ImageAdapter(Context context, ArrayList<String> myDataset) {
        this.context = context;
        this.mDataset = myDataset;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String path = mDataset.get(position);
        File file = new File(path);
        if (file.exists()) {
            Bitmap bmImg = BitmapFactory.decodeFile(path);
            Bitmap thumbnail = ThumbnailUtils.extractThumbnail(bmImg, 400, 400);
//        holder.imageView.setImageBitmap(BitmapFactory.(getResources(), R.id.myimage, 100, 100));
            holder.imageView.setImageBitmap(thumbnail);
        }
        else {
//            mDataset.remove(position);
//            position--;
            Log.d("kien.tt", String.valueOf(position));
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
//        CheckBox checkBox;
        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_icon);
//            checkBox = itemView.findViewById(R.id.checkBox);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    String path = mDataset.get(getAdapterPosition());
                    Intent intent = new Intent(context, ShowImageActivity.class);
//                    intent.putExtra("IMAGE_PATH", path);
                    intent.putExtra("POSITION_SHOW", getAdapterPosition());

                    Bundle bundle = new Bundle();
                    bundle.putSerializable("ARRAY_IMAGE_PATH", mDataset);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            });
//            imageView.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View view) {
//                    checkBox.setVisibility(View.VISIBLE);
//                    return true;
//                }
//            });
        }
    }
}
