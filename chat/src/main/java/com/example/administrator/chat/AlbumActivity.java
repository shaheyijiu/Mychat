package com.example.administrator.chat;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;


import java.util.ArrayList;

import adapter.AlbumAdapter;

/**
 * Created by Administrator on 2016/3/10.
 */
public class AlbumActivity extends Activity {
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择

    private int images[] = {R.drawable.head,R.drawable.head,R.drawable.head,R.drawable.head,R.drawable.head,
            R.drawable.head,R.drawable.head,R.drawable.head,R.drawable.head,R.drawable.head,
            R.drawable.head,R.drawable.head,R.drawable.head,R.drawable.head,R.drawable.head,
            R.drawable.head,R.drawable.head,R.drawable.head,R.drawable.head,R.drawable.head,
            R.drawable.head,R.drawable.head,R.drawable.head,R.drawable.head,R.drawable.head};

    private AlbumAdapter adapter;
    private ArrayList<Integer> imageList = new ArrayList<>();
    private ArrayList<Bitmap> bitmaps = new ArrayList<>();
    private ArrayList<String> pathList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        initList();
        GridView gridView = (GridView)findViewById(R.id.grid);
        Button bt_upload = (Button)findViewById(R.id.button_upload);
        bt_upload.setVisibility(View.VISIBLE);

        adapter = new AlbumAdapter(this,R.layout.row_album,pathList);
        gridView.setAdapter(adapter);

    }
    private void initList(){
        for (int i = 0;i < images.length;i++){
            imageList.add(images[i]);
        }
    }

    public void upload(View view){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap bitmap = null;
        if (requestCode == PHOTO_REQUEST_GALLERY) {
            if (data != null){
                Uri uri = data.getData();
                Cursor cursor = getContentResolver().query(uri, null, null,
                        null, null);
                if (cursor != null){
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex("_data");
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    cursor = null;
                    bitmap = BitmapFactory.decodeFile(picturePath);
                    pathList.add(picturePath);
                }
                if (bitmap == null){
                    System.out.println("图片为空");
                } else {
                    System.out.println("图片不为空");
                }
                bitmaps.add(bitmap);
                adapter.notifyDataSetChanged();

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // 封装请求Gallery的intent
    public static Intent getPhotoPickIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        intent.setType("image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 80);
        intent.putExtra("outputY", 80);
        intent.putExtra("return-data", true);
        return intent;
    }
}
