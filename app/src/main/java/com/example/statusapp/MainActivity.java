package com.example.statusapp;

import static android.os.Build.VERSION.SDK_INT;
import static android.webkit.MimeTypeMap.getFileExtensionFromUrl;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    GridLayoutManager MG;
    private int CAMERA_REQUEST_CODE = 1;
    private int GALLRY_REQUEST_CODE = 2;

    ArrayList<Status_Item> UsersStat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UsersStat = new ArrayList<Status_Item>();

        mRecyclerView = (RecyclerView) findViewById(R.id.rcvStatusList);
        mRecyclerView.setHasFixedSize(true);
        MG = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        RefreshRec();


    }

    // Gallery button
    public void OpenGallary(View view) {
        PicImageFromGallry();
    }

    // text button
    public void AddTxtStatus(View view) {
        Dialog();
    }

    // camera button
    public void OpenCamera(View view) {
        PicImageFromCamera();
    }

    // not set
    public void Setting(View view) {
        Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
    }

    // not set
    public void Search(View view) {
        Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show();
    }

    // select image OR video from gallery
    public void PicImageFromGallry() {
        if (checkWriteExternalPermission() && checkReadExternalPermission()) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("*/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLRY_REQUEST_CODE);
        } else {
            checkPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, 0);
            checkPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE, 1);
        }

    }

    // taking image from camera
    public void PicImageFromCamera() {
        if (checkCameraPermission()) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAMERA_REQUEST_CODE);
        } else {
            checkPermission(Manifest.permission.CAMERA, 0);
        }

    }

    //  showing dialog to insert text status
    protected void Dialog() {

        AlertDialog builder = new AlertDialog.Builder(MainActivity.this).create();
        final View customLayout = getLayoutInflater().inflate(R.layout.custom_layout, null);
        builder.setView(customLayout);
        EditText text = (EditText) customLayout.findViewById(R.id._write);
        ImageView save = customLayout.findViewById(R.id.save);
        ImageView cancel = customLayout.findViewById(R.id.cancel);
        save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                BitmapDrawable d = new BitmapDrawable();

                UsersStat.add(new Status_Item("user name", d, "", text.getText().toString(), "", "","TEXT"));
                RefreshRec();
                builder.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.dismiss();
            }
        });
        builder.show();

    }

    // refresh RecyclerView to show inserted data
    public void RefreshRec() {
        mAdapter = new Adapter(UsersStat);
        mRecyclerView.setLayoutManager(MG);
        mRecyclerView.setAdapter(mAdapter);
    }


    // start activity to get result of image or video
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = data.getData();
        if (requestCode == CAMERA_REQUEST_CODE) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            BitmapDrawable image = new BitmapDrawable(getResources(), bitmap);
            String path = getRealPathFromURI(getImageUri(MainActivity.this, bitmap));

            UsersStat.add(new Status_Item("user name", image, path, "my status from camera", "", "","IMAGE"));
            RefreshRec();
        } else {
            if (requestCode == GALLRY_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {

                String path = getPath(MainActivity.this, uri);

                if (uri.toString().contains("image")) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        BitmapDrawable image = new BitmapDrawable(getResources(), bitmap);
                        UsersStat.add(new Status_Item("user name", image, path, "my status from camera", "", "","IMAGE"));
                        RefreshRec();
                        Toast.makeText(this, path, Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else if (uri.toString().contains("video")) {

                    BitmapDrawable image = new BitmapDrawable(getResources(), VideoSample(path));
                    UsersStat.add(new Status_Item("user name", image, path, "my status from video", "", "","VIDEO"));
                    RefreshRec();

                }
            }
        }
    }

    //Get sample of video to imageview
    public Bitmap VideoSample(String path) {
        return ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.MICRO_KIND);
    }


    private boolean checkWriteExternalPermission() {
        String permission = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
        int res = MainActivity.this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    public void checkPermission(String permission, int requestCode) {
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
        } else {
            Toast.makeText(MainActivity.this, "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkReadExternalPermission() {
        String permission = android.Manifest.permission.READ_EXTERNAL_STORAGE;
        int res = MainActivity.this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    private boolean checkCameraPermission() {
        String permission = Manifest.permission.CAMERA;
        int res = MainActivity.this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    //----------------- Get path Functions ----------------------//
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    //----------------- Get path Functions End Here----------------------//


    //--------------------Get Path of Image From Camera ---------//
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        String path = "";
        if (getContentResolver() != null) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }
    //--------------------Get Path of Image From Camera End Here ---------//
}