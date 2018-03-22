package fimo.kientt.mygallery;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ShowImageActivity extends Activity implements View.OnClickListener{
    private TouchImageView showImage;
    private String path;
    private int position;
    private ArrayList<String> imageList;
    private ImageButton btn_left, btn_right;
    private ImageView imgDelete, imgShare, imgEdit, imgBack;
    private LinearLayout llMenu, llMenuBack;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);
        initView();
        showImage.setOnTouchImageViewListener(new TouchImageView.OnTouchImageViewListener() {
            @Override
            public void onMove() {
                if (showImage.isZoomed()){
                    btn_left.setVisibility(View.GONE);
                    btn_right.setVisibility(View.GONE);
                    llMenu.setVisibility(View.GONE);
                    llMenuBack.setVisibility(View.GONE);
                }
                else {
                    showButtonSwitch();
                    llMenu.setVisibility(View.VISIBLE);
                    llMenuBack.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void initView(){
        showImage = findViewById(R.id.show_image);
        btn_left = findViewById(R.id.btn_left);
        btn_left.setOnClickListener(this);
        btn_right = findViewById(R.id.btn_right);
        btn_right.setOnClickListener(this);
        imgDelete = findViewById(R.id.img_delete);
        imgDelete.setOnClickListener(this);
        imgShare = findViewById(R.id.img_share);
        imgShare.setOnClickListener(this);
        imgEdit = findViewById(R.id.img_edit);
        imgEdit.setOnClickListener(this);
        imgBack = findViewById(R.id.img_back);
        imgBack.setOnClickListener(this);
        llMenu = findViewById(R.id.layout_menu);
        llMenuBack = findViewById(R.id.layout_back_menu);
        Intent intent = getIntent();
        position = intent.getIntExtra("POSITION_SHOW", -1);
        Bundle bundle = intent.getExtras();
        imageList = (ArrayList<String>) bundle.getSerializable("ARRAY_IMAGE_PATH");
        path = imageList.get(position);
        Bitmap bmImg = BitmapFactory.decodeFile(path);
        showImage.setImageBitmap(bmImg);
        showButtonSwitch();
    }

    private void showButtonSwitch(){
        if (position == 0) btn_left.setVisibility(View.GONE);
        else btn_left.setVisibility(View.VISIBLE);
        if (position == imageList.size() - 1) btn_right.setVisibility(View.GONE);
        else btn_right.setVisibility(View.VISIBLE);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_left:
                if (position > 0){
                    position--;
                    path = imageList.get(position);
                    Bitmap bmImg = BitmapFactory.decodeFile(path);
                    showImage.setImageBitmap(bmImg);
                }
                showButtonSwitch();
                break;
            case R.id.btn_right:
                if (position < imageList.size() - 1){
                    position++;
                    path = imageList.get(position);
                    Bitmap bmImg = BitmapFactory.decodeFile(path);
                    showImage.setImageBitmap(bmImg);
                }
                showButtonSwitch();
                break;
            case R.id.img_delete:
                if(Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M){
                    if (ContextCompat.checkSelfPermission(ShowImageActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(ShowImageActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1235);
                    }
                    else DeleteImage(path);
                }
                else {
                    DeleteImage(path);
                }
                break;
            case R.id.img_share:
                Bitmap icon = BitmapFactory.decodeFile(path);
                try {
                    File file = new File(this.getExternalCacheDir(),"logicchip.png");
                    FileOutputStream fOut = new FileOutputStream(file);
                    icon.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                    fOut.flush();
                    fOut.close();
                    file.setReadable(true, false);
                    final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                    intent.setType("image/png");
                    startActivity(Intent.createChooser(intent, "Share image via"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.img_edit:
                final AlertDialog.Builder alert = new AlertDialog.Builder(this);
                final EditText edittext = new EditText(ShowImageActivity.this);
                final File file = new File(path);
                edittext.setText(file.getName());
                final String dir = file.getParent();
                alert.setTitle("Đổi tên ở đây á");
                alert.setView(edittext);
                alert.setPositiveButton("Lưu", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String newName = edittext.getText().toString();
                        File newFile = new File(dir, newName);
                        if(file.renameTo(newFile)){
                            Toast.makeText(ShowImageActivity.this, "Đã lưu!", Toast.LENGTH_SHORT).show();
                        }else Toast.makeText(ShowImageActivity.this, "Lỗi! Tên file có gì đó sai sai", Toast.LENGTH_SHORT).show();
                    }
                });

                alert.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    //do nothing
                    }
                });
                alert.show();
                break;
            case R.id.img_back:
                finish();
                break;
        }
    }
}
