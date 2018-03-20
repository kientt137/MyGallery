package fimo.kientt.mygallery;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by kient on 3/17/2018.
 */

public class ShowImageActivity extends Activity{
    private TouchImageView showImage;
    private ImageView img_delete, img_share;
    private String path;
    private int position;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);

        showImage = findViewById(R.id.show_image);
        img_delete = findViewById(R.id.img_delete);
        img_share = findViewById(R.id.img_share);

        Intent intent = getIntent();
        path = intent.getStringExtra("IMAGE_PATH");
        position = intent.getIntExtra("POSITION_SHOW", -1);
        Bitmap bmImg = BitmapFactory.decodeFile(path);
        showImage.setImageBitmap(bmImg);

        img_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M){
                    if (ContextCompat.checkSelfPermission(ShowImageActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(ShowImageActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1235);
                    }
                    else DeleteImage(path);
                }
                else {
                    DeleteImage(path);
                }
            }
        });

        img_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap icon = BitmapFactory.decodeFile(path);
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/jpeg");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                icon.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                File f = new File(Environment.getExternalStorageDirectory() + File.separator + "temporary_file.jpg");
                try {
                    f.createNewFile();
                    FileOutputStream fo = new FileOutputStream(f);
                    fo.write(bytes.toByteArray());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                share.putExtra(Intent.EXTRA_STREAM, Uri.parse(path));
                startActivity(Intent.createChooser(share, "Share Image"));
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1235: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    DeleteImage(path);
                } else {
                    Toast.makeText(ShowImageActivity.this, "Bạn chưa cấp quyền ghi cho ứng dụng", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private void DeleteImage(final String path_img){
        new AlertDialog.Builder(ShowImageActivity.this)
                .setTitle("Chú ý")
                .setMessage("Bạn có chắc chắn muốn xóa ảnh này không?")
                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        File file = new File(path_img);
                        if(file.delete()){
//                            notifyItemRemoved(this.getLayoutPosition());
                            Intent intent = new Intent("delete-a-image");
                            intent.putExtra("POSITION_DELETE", position);
                            LocalBroadcastManager.getInstance(ShowImageActivity.this).sendBroadcast(intent);
                            Toast.makeText(ShowImageActivity.this, "Xóa thành công", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else Log.d("DELETE", "Fail");
                    }
                })
                .setNegativeButton("Không", null)
                .show();
    }
}
