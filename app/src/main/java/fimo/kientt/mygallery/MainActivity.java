package fimo.kientt.mygallery;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private RecyclerView rvImage;
    private ArrayList<String> listImage;
    private ImageAdapter imageAdapter;
    private ImageButton btn_camera;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rvImage = findViewById(R.id.rvImage);
        listImage = new ArrayList<>();
        imageAdapter = new ImageAdapter(this, listImage);
//        imageAdapter.notifyDataSetChanged();
        if(Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1234);
            }
            else {
                UpdateListImage();
            }
        }
        else UpdateListImage();

        BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Get extra data included in the Intent
                int position_delete = intent.getIntExtra("POSITION_DELETE", -1);
                listImage.remove(position_delete);
                imageAdapter.notifyDataSetChanged();
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("delete-a-image"));
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        imageAdapter.notifyDataSetChanged();
//    }

    private void UpdateListImage(){
        listImage = getAllShownImagesPath(MainActivity.this);
        imageAdapter = new ImageAdapter(this, listImage);
        rvImage.setAdapter(imageAdapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        rvImage.setLayoutManager(gridLayoutManager);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1234: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    UpdateListImage();
                } else {
                    Toast.makeText(MainActivity.this, "Bạn chưa cấp quyền đọc cho ứng dụng", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case 1236: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 11111);
                } else {
                    Toast.makeText(MainActivity.this, "Bạn chưa cấp quyền mở CAMERA cho ứng dụng", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private ArrayList<String> getAllShownImagesPath(Activity activity) {
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        ArrayList<String> listOfAllImages = new ArrayList<String>();
        String absolutePathOfImage = null;
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = { MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME };

        cursor = activity.getContentResolver().query(uri, projection, null,
                null, null);

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);

            listOfAllImages.add(absolutePathOfImage);
            Log.d("IMAGESS", absolutePathOfImage);
        }
        return listOfAllImages;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_camera:
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M){
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1236);
                    }
                    else {
                        startActivityForResult(intent, 11111);
                    }
                }
                else startActivityForResult(intent, 11111);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111111){
            if (resultCode == RESULT_OK){
                imageAdapter.notifyDataSetChanged();
            }
        }
    }
}
