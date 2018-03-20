package fimo.kientt.mygallery;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by kient on 3/16/2018.
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
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
        Bitmap bmImg = BitmapFactory.decodeFile(path);
        Bitmap thumbnail = ThumbnailUtils.extractThumbnail(bmImg, 300, 300);

        holder.imageView.setImageBitmap(thumbnail);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        CheckBox checkBox;
        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_icon);
            checkBox = itemView.findViewById(R.id.checkBox);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String path = mDataset.get(getAdapterPosition());
                    Intent intent = new Intent(context, ShowImageActivity.class);
                    intent.putExtra("IMAGE_PATH", path);
                    intent.putExtra("POSITION_SHOW", getAdapterPosition());
                    context.startActivity(intent);
                }
            });
            imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    checkBox.setVisibility(View.VISIBLE);
                    return true;
                }
            });
        }
    }
}
