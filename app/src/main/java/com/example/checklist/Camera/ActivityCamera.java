package com.example.checklist.Camera;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.checklist.Config;
import com.example.checklist.PageGenerator.CheckListPager;
import com.example.checklist.R;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.example.checklist.GlobalFuncs.log;
import static com.example.checklist.GlobalFuncs.showToast;

/**
 * First of all sub folder should named in pictures element acrtivity
 * to put image that related to that activity in one folder
 * {sub_folder_path}
 * <p>
 * returns path in storage in onActivityResult method
 */


public class ActivityCamera extends AppCompatActivity implements View.OnClickListener
        , View.OnTouchListener, SensorEventListener {
    private static final String TAG = "ActivityCamera";

    public static String FLAG_CUSTOM_CAMERA = "com.example.checklist.Camera.ActivityCamera";

    public static String IMAGE_RESULT = "path";
    String App_Folder_Name ;
    String sub_folder_name = "Pictures";
    public static String sub_folder_path = "";

    private int zoom = 0;
    private boolean isAppClosed;

    private int minWidthRange = 600;
    private int maxWidthRange = 800;

    private float last_number_of_x = 0;
    private float last_number_of_y = 0;
    private float last_number_of_z = 0;

    private boolean editModeEnable = false;

    /************************    camera api  ************************/
    private Camera mCamera;

    private LinearLayout top_choice;
    private Boolean Camera_Status = false;

    private boolean has_flash = false;

//    private Uri imageUri;
    private LinearLayout cancel;
    private LinearLayout tick;
    private int camera_id = Camera.CameraInfo.CAMERA_FACING_BACK;


    private CameraPreview mPreview;

    private boolean isParamsSet = true;
    private List<Integer> zooms;
    private LinearLayout search_linear;
    private boolean isFromEdit = false;
    private int status_counter = 1;
    private float cr_x;
    private float cr_y;
    private Bitmap current_bm;
    private FrameLayout preview;
    private ImageView captureButton;
    private ImageView img;
    private Camera.PictureCallback mPicture;
    /***************************************************************************/
    private ImageView focus;
    private RelativeLayout root;
    private boolean FLASH_IS_ON = false;
    private ImageView capture;
    private LinearLayout comment;
    private String path;
    private ImageView flash;
    private ImageView camera_rotate;
    //    private TextView edit;
    private int max_zoom = 0;
    private String mCurrentPhotoPath;
    private LinearLayout options;
    private LinearLayout camera_hldr;
    private String comment_txt;
    private String[] permissions = {Manifest.permission.CAMERA
            , Manifest.permission.WRITE_EXTERNAL_STORAGE
            , Manifest.permission.READ_EXTERNAL_STORAGE};

    private SensorManager sm;
    private long lastUpdate;
    private int last_known_orientation = 0;

    private LinearLayout zoom_option;
    private ImageView zoom_in;
    private ImageView zoom_out;

    private int last_known_z = 0;
    private int last_known_y = 0;

    private void getImageRatio() {

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        isFromEdit = true;
        if (resultCode == RESULT_OK && requestCode == 102) {

            Bundle bundle = data.getExtras();
            if (bundle != null) {
                path = bundle.getString("path");
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                Log.i(TAG, "onActivityResult: " + path);
                img.setImageBitmap(bitmap);
                mPreview.setVisibility(View.INVISIBLE);
                img.setVisibility(View.VISIBLE);
                img.bringToFront();
            }

        } else if (requestCode == 102 && resultCode == RESULT_CANCELED) {
            Bitmap bitmap = BitmapFactory.decodeFile(getSharedPreferences(Config.sharedPreferencName, MODE_PRIVATE)
                    .getString(Config.img_path, ""));
            img.setVisibility(View.VISIBLE);
            mPreview.setVisibility(View.INVISIBLE);
            img.bringToFront();
            img.setImageBitmap(bitmap);
        }
    }

    private void setZoomIn() {
        if (zoom < max_zoom - 10) {
            zoom = zoom + 10;
            update_zoom();
        } else {
            zoom = max_zoom;
            update_zoom();
        }
    }

    private void setZoomOut() {
        if (zoom >= 10) {
            zoom = zoom - 10;
            update_zoom();
        } else {
            zoom = 0;
            update_zoom();
        }
    }

    private void update_zoom() {
        Camera.Parameters params = mCamera.getParameters();
        zooms = params.getZoomRatios();
        if (params.isZoomSupported()) {
            params.setZoom(zoom);
            mCamera.setParameters(params);
        }
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)) {
//            setZoomOut();
//        } else {
//            setZoomIn();
//        }
//        return true;
//    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
//            setContentView(R.layout.activity_camera_landscape);

            mCamera.setDisplayOrientation(0);
//            FrameLayout.LayoutParams params = new
//                    FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT
//            , FrameLayout.LayoutParams.WRAP_CONTENT);
//            params.setMargins(50,0,50,0);
////            preview.setLayoutParams(params);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
//            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
//            setContentView(R.layout.activity_camera_library);
            mCamera.setDisplayOrientation(90);
//            FrameLayout.LayoutParams params = new
//                    FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT
//                    , FrameLayout.LayoutParams.WRAP_CONTENT);
//            params.setMargins(0,60,0,80);
//            preview.setLayoutParams(params);

        }
    }


    @Override
    public void onBackPressed() {
        this.isAppClosed = false;
        if (mCamera != null) {
            mCamera.stopPreview();
        }
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_library);

/***************************************** camera api **********************/
        preview = findViewById(R.id.camera_preview);
        img = findViewById(R.id.img);
        top_choice = findViewById(R.id.top_choice);
        cancel = findViewById(R.id.cancel);
        tick = findViewById(R.id.tick);
        search_linear = findViewById(R.id.search_linear);
        flash = findViewById(R.id.flash);
        camera_rotate = findViewById(R.id.camera_rotate);
        focus = findViewById(R.id.focus);
        options = findViewById(R.id.options);
//        edit = findViewById(R.id.edit);
        root = findViewById(R.id.root);
        zoom_option = findViewById(R.id.zoom_options);
        zoom_in = findViewById(R.id.zoom_in);
        zoom_out = findViewById(R.id.zoom_out);
        cr_x = focus.getX();
        cr_x = focus.getY();
        preview.setOnTouchListener(this);
        tick.setOnClickListener(this);
        cancel.setOnClickListener(this);
        flash.setOnClickListener(this);
        options.setOnClickListener(this);
//        edit.setOnClickListener(this);
        camera_rotate.setOnClickListener(this);
        zoom_in.setOnClickListener(this);
        zoom_out.setOnClickListener(this);

        //get_permission();

        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        lastUpdate = System.currentTimeMillis();


//        if (!editModeEnable){
//            edit.setVisibility(View.GONE);
//        }

        this.App_Folder_Name = CheckListPager.appFolder;
        this.sub_folder_name = CheckListPager.picturesFolder;

        // Create an instance of Camera
        if (!isFromEdit) {
            create_camera();
        }//end of if
        else {
            img.bringToFront();
        }


        // Add a listener to the Capture button
        captureButton = findViewById(R.id.button_capture);
        captureButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Camera_Status = true;
                        search_linear.setVisibility(View.INVISIBLE);
                        camera_rotate.setVisibility(View.INVISIBLE);
                        flash.setVisibility(View.INVISIBLE);
                        zoom_option.setVisibility(View.INVISIBLE);
                        try {
                            mCamera.takePicture(null, null, mPicture);
                        } catch (Exception e) {
                            e.printStackTrace();
                            log(e.getMessage());
                        }
                    }
                }
        );
/**********************************************************************************/
    }

    @Override
    public void onDestroy() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
        }
        super.onDestroy();
    }


    /**
     * check camera orientation
     *
     * @return treu or false
     */
//    private boolean isPortrait() {
//        boolean isP = false;
//        InputStream in;
//        try {
//            in = getContentResolver().openInputStream(imageUri);
//            android.support.media.ExifInterface exifInterface = new android.support.media.ExifInterface(in);
//            int o = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
//            Log.i(TAG, "isPortrait: ooo" + o);
//
//            if (o == 6) {
//                isP = true;
//            } else if (o == 8) {
//                isP = true;
//            } else {
//                isP = false;
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            log(e.getMessage());
//        }
//        return isP;
//    }

//    public Uri getImageUri(Context inContext, Bitmap inImage) {
//        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
//        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
//        if (path != null) {
//            if (!path.equals(""))
//                return Uri.parse(path);
//            else
//                closeCamera();
//            return Uri.EMPTY;
//        } else {
//            closeCamera();
//            return Uri.EMPTY;
//        }
//    }

    private void closeCamera() {
        setResult(RESULT_CANCELED);
        finish();
    }

    /**
     * create camera surface and camera
     */
    private void create_camera() {
        try {

            mPicture = new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(final byte[] data, Camera camera) {

                    int x = last_known_orientation;
                    int y = last_known_y;
                    int z = last_known_z;

//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
                            if (mCamera != null) {
                                mCamera.stopPreview();
                                MediaPlayer mediaPlayer = MediaPlayer.create(ActivityCamera.this, R.raw.defult);
                                mediaPlayer.start();
                            }

                            current_bm = BitmapFactory.decodeByteArray(data, 0, data.length);

                            String model = getDeviceName();

                            if (camera_id == Camera.CameraInfo.CAMERA_FACING_BACK) {
                                //  if (last_known_orientation == 0) {
                                if (model.equals("LGE")){
                                    path = String.valueOf(createImageFile(rotate(current_bm, 90),x,y,z));
                                }else {
                                    path = String.valueOf(createImageFile(current_bm,x,y,z));
                                }
                            } else {
                                path = String.valueOf(createImageFile(rotate(current_bm, 270),x,y,z));
                            }

//                    Log.i(TAG, "onPictureTaken: " + last_known_orientation);

                            SharedPreferences.Editor editor = getSharedPreferences(Config.sharedPreferencName, MODE_PRIVATE).edit();
                            editor.putString(Config.img_path, path);
                            editor.apply();
//                        }
//                    }).start();

//                    imageUri = getImageUri(ActivityCamera.this, current_bm);
//                    isPortrait();
//                    getSharedPreferences(Config.sharedPreferencName, MODE_PRIVATE).getString(Config.email, "");
//                    Date today = Calendar.getInstance().getTime();
//                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");

//                    String date = dateFormat.format(today).replace("/", "-");

//                    Log.i(TAG, "onPictureTaken: " + "width = " + current_bm.getWidth() + "higth = " + current_bm.getHeight());
//                    Log.i(TAG, "onPictureTaken: " + getResources().getConfiguration().orientation);
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {

//                        }
//                    }).start();

//                camera_or(String.valueOf(createImageFile(current_bm)));
//                    int rotate_dif = 0;
//                    if (Build.MANUFACTURER.equals("HUAWEI")) {
//                        rotate_dif = 0;
//                    }

//                    Log.i(TAG, "onPictureTaken: " + last_known_orientation);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            preview.bringToFront();

                            top_choice.setVisibility(View.VISIBLE);

                            top_choice.bringToFront();
                        }
                    });


                }

            };

            mCamera = getCameraInstance(Camera.CameraInfo.CAMERA_FACING_BACK);

            // set Camera parameters

            Camera.Parameters params = mCamera.getParameters();
            max_zoom = params.getMaxZoom();
            boolean has_FLASH = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
            this.has_flash = has_FLASH;
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);
            mCamera.setDisplayOrientation(0);
            params.setRotation(90);
            if (has_FLASH) {
                params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            }
            List<Camera.Size> sizes = params.getSupportedPictureSizes();
            int cindex = getCameraSize(minWidthRange, maxWidthRange);
            params.setPictureSize(sizes.get(cindex).width, sizes.get(cindex).height);
            params.setPreviewSize(sizes.get(cindex).width, sizes.get(cindex).height);
            zooms = params.getZoomRatios();
//        params.setZoom(zooms.get(zooms.size() - 1));
            params.setZoom(zoom);
            try {
                mCamera.setParameters(params);
            } catch (Exception e) {
                log(e.getMessage());
                e.printStackTrace();
                isParamsSet = false;
                Log.i(TAG, "create_camera: " + e);
                handle_catch_camera_params(has_FLASH);
            }
            // Create our Preview view and set it as the content of our activity.
            mPreview = new CameraPreview(this, mCamera);
            preview.addView(mPreview);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                mCamera.enableShutterSound(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log(e.getMessage());
        }
    }

    private boolean isFromZero(List<Camera.Size> sizes) {

        int startIndex = 0;
        int endIndex = sizes.size() - 1;

        int startWidth = sizes.get(startIndex).width;
        int endWidth = sizes.get(endIndex).width;

        if (startWidth < endWidth) {
            return true;
        }

        return false;
    }

    public  String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
//        String model = Build.MODEL;
//        showToast(this,manufacturer);
        return manufacturer;
    }

    private int getCameraSize(int minRange, int maxRange) {

        int cindex = -1;
        boolean isFromZero;

        Camera.Parameters params = mCamera.getParameters();
        List<Camera.Size> sizes = params.getSupportedPictureSizes();

        isFromZero = isFromZero(sizes);

        for (int i = 0; i < sizes.size(); i++) {

            if (sizes.get(i).width < maxRange && sizes.get(i).width > minRange) {
                cindex = i;
                break;
            }
        }

        if (cindex == -1) {//no match found
            if (isFromZero) {
                cindex = 1;
            } else {
                cindex = sizes.size() - 2;
            }
        }

        return cindex;

    }

    /**
     * handle if user phone not have flash
     *
     * @param has_FLASH
     */
    private void handle_catch_camera_params(boolean has_FLASH) {
        try {
            Camera.Parameters paramss = mCamera.getParameters();
            paramss.setRotation(90);
            if (has_FLASH) {
                paramss.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            }
            paramss.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);
            paramss.setZoom(zoom);
            mCamera.setParameters(paramss);
            Toast.makeText(this, "parameters got error", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            log(e.getMessage());
        }
    }


    /**
     * get bitmap and create file in app folder
     *
     * @param bm
     * @return
     */

    private File createImageFile(Bitmap bm,int x,int y,int z) {
//        Log.i("ACCC", "createImageFile: x = " + last_known_orientation);
//        Log.i("ACCC", "createImageFile: y = " + last_known_y);
//        Log.i("ACCC", "createImageFile: z = " + last_known_z);
        if (z == 0) {
            if (x == 0) {
                if (bm.getHeight() < bm.getWidth()) {
//                    Toast.makeText(this, "1 -> rotate 90", Toast.LENGTH_SHORT).show();
                    bm = rotate(bm, -90);
                    String a = Build.MANUFACTURER;
                    if (Build.MANUFACTURER.toLowerCase().equals("samsung")) {
                        bm = rotate(bm, 180);
//                        Toast.makeText(this, "2 -> rotate 90", Toast.LENGTH_SHORT).show();
                    }
                }
            } else if (x == 2) {
                if (bm.getHeight() > bm.getWidth()) {
                    //its not ok
//                    Toast.makeText(this, "3 -> rotate 90", Toast.LENGTH_SHORT).show();
                    bm = rotate(bm, 90);
                }
                if (Build.MANUFACTURER.toLowerCase().equals("samsung")) {
//                    Toast.makeText(this, "4 -> rotate 90", Toast.LENGTH_SHORT).show();
                    bm = rotate(bm, 180);
                }
            } else {
                if (bm.getHeight() > bm.getWidth()) {
                    //its not ok
//                    Toast.makeText(this, "5 -> rotate 90", Toast.LENGTH_SHORT).show();
                    bm = rotate(bm, -90);
                }
            }
        } else {
            if (y == 0) {
                if (bm.getHeight() < bm.getWidth()) {
//                    Toast.makeText(this, "6 -> rotate 90", Toast.LENGTH_SHORT).show();
                    bm = rotate(bm, -90);
                }
            } else {
                if (bm.getHeight() > bm.getWidth()) {
//                    Toast.makeText(this, "7 -> rotate 90", Toast.LENGTH_SHORT).show();
                    bm = rotate(bm, -90);
                }
            }
        }

        Log.i(TAG, "createImageFile: width = " + bm.getWidth());
        Log.i(TAG, "createImageFile: height = " + bm.getHeight());
        Log.i(TAG, "createImageFile: " + bm.getByteCount());

        createMandatoryFolders();

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root
                + File.separator
                + App_Folder_Name
                + File.separator
                + sub_folder_name
                + File.separator
                + sub_folder_path);
        // myDir.mkdirs();
        String fname = System.currentTimeMillis() + "_" + x + ".jpg";
        File file = new File(myDir, fname);
        Log.i(TAG, "" + file);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            log(e.getMessage());
            Log.i(TAG, "createImageFile: " + e);
        }
        remove_image_from_pictures(fname);
        return file;
    }

    private void createMandatoryFolders() {
        File APPFOLDER = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath() + File.separator + App_Folder_Name);
        if (!APPFOLDER.exists())
            APPFOLDER.mkdirs();

        File PictureFolder = new File(APPFOLDER.getAbsolutePath()
                , sub_folder_name);
        if (!PictureFolder.exists())
            PictureFolder.mkdirs();

        File SubFolder = new File(PictureFolder.getAbsolutePath()
                , sub_folder_path);
        if (!SubFolder.exists())
            SubFolder.mkdirs();
    }

    /**
     * remove image from pictures after copiet to app folder
     *
     * @param fname
     */
    private void remove_image_from_pictures(String fname) {
        String[] pic_str = fname.split("_");
        String pic_image_str = pic_str[0];
        long pic_long = Long.parseLong(pic_image_str);
        for (int i = 0; i < 1000; i++) {
            long find_pic = pic_long - i;
            String find_pic_str = find_pic + ".jpg";
            File file = new File(Environment.getExternalStorageDirectory()
                    + File.separator
                    + "Pictures"
                    + File.separator
                    + find_pic_str);
            if (file.exists()) {
                file.delete();
                Log.i("IMAGEHANDLE", "remove_image_from_pictures: deleted");
                break;
            }
        }
    }

    /**
     * A safe way to get an instance of the Camera object.
     */
    public static Camera getCameraInstance(int camera_id) {
        Camera c = null;
        try {
            c = Camera.open(camera_id); // attempt to get a Camera instance
        } catch (Exception e) {
            e.printStackTrace();
            log(e.getMessage());
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }


    public static Bitmap rotate(Bitmap bitmap, float degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    @Override
    protected void onResume() {
        this.isAppClosed = true;
        sm.registerListener(this,
                sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        super.onResume();
//        onCreate(null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sm.unregisterListener(this);
        if (!isFromEdit) {
//            assert mCamera != null;
            if (mCamera != null) {
                mCamera.stopPreview();
//                mCamera.release();
            }
        }
        if (isAppClosed) {
            this.isAppClosed = false;
            setResult(1);
            finish();
        }
    }


    @Override
    public void onClick(View v) {

        if (v == zoom_in) {
            setZoomIn();
        }
        if (v == zoom_out) {
            setZoomOut();
        }

        if (v == cancel) {
            perfotm_cancel_pic();
        }
        if (v == tick) {
            this.isAppClosed = false;
            Intent data = new Intent();
            data.putExtra(IMAGE_RESULT, path);
            setResult(RESULT_OK, data);
            finish();
        }
        if (flash == v) {
            open_flash_status();
        }
        if (camera_rotate == v) {
            rotate_camera();
        }
//        if (v == edit) {
//            isAppClosed = false;
//            Intent i = null;
//            try {
//                i = new Intent(ActivityCamera.this
//                        , Class.forName("com.example.checklist.Camera.ActivityEdit"));
//            } catch (ClassNotFoundException e) {
//                e.printStackTrace();
//                log(e.getMessage());
//            }
//            i.putExtra("path", path);
//            startActivityForResult(i, 102);
//        }
    }

    /**
     * rotate camera if camera id changed
     */
    private void rotate_camera() {
        mCamera.stopPreview();
        if (camera_id == Camera.CameraInfo.CAMERA_FACING_BACK) {
            camera_id = Camera.CameraInfo.CAMERA_FACING_FRONT;
            flash.setVisibility(View.INVISIBLE);
            zoom_option.setVisibility(View.INVISIBLE);
            focus.setVisibility(View.INVISIBLE);
        } else {
            camera_id = Camera.CameraInfo.CAMERA_FACING_BACK;
            flash.setVisibility(View.VISIBLE);
            zoom_option.setVisibility(View.VISIBLE);
        }
        preview.removeAllViews();
        mCamera.release();
        mCamera = getCameraInstance(camera_id);
        mPreview = new CameraPreview(this, mCamera);
        preview.addView(mPreview);

        // set Camera parameters
        Camera.Parameters params = mCamera.getParameters();
        boolean has_FLASH = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        if (has_FLASH && camera_id == Camera.CameraInfo.CAMERA_FACING_BACK) {
            params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        }
//         params.setJpegThumbnailQuality(300);

        params.setJpegQuality(90);
        mCamera.setDisplayOrientation(0);
        params.setRotation(90);
        //if (!android.os.Build.MANUFACTURER.equals("HUAWEI")){

//        params.setJpegQuality(100);
        List<Camera.Size> sizes = params.getSupportedPictureSizes();
        int cindex = getCameraSize(minWidthRange, maxWidthRange);

        params.setPictureSize(sizes.get(cindex).width, sizes.get(cindex).height);
        params.setPreviewSize(sizes.get(cindex).width, sizes.get(cindex).height);


        params.setZoom(zoom);
        try {
            mCamera.setParameters(params);
        } catch (Exception e) {
            log(e.getMessage());
            handle_catch_camera_params(has_FLASH);
            e.printStackTrace();
            isParamsSet = false;
            Log.i(TAG, "rotate_camera: " + e);
        }

        mCamera.startPreview();

    }

    /**
     * set flash on/off/auto
     *
     * @param status
     */
    private void set_flash_status(int status) {
        boolean has_FLASH = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        Camera.Parameters params = mCamera.getParameters();
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);
//        params.setJpegQuality(300);
        if (has_FLASH) {
            params.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
        }
//        params.setJpegThumbnailQuality(300);

        RotateAnimation ra = new RotateAnimation(0, 360, 50.0f, 50.0f);
        ra.setDuration(200);
        ra.setRepeatCount(0);
        ra.setRepeatMode(Animation.INFINITE);
        flash.setAnimation(ra);

        switch (status) {
            case 0:
                if (has_FLASH) {
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    flash.setImageResource(R.drawable.off);
                }
                break;
            case 1:
                if (has_FLASH) {
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                    flash.setImageResource(R.drawable.on);
                }
                break;
            case 2:
                if (has_FLASH) {
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                    flash.setImageResource(R.drawable.auto);
                }
                break;
        }

//        params.setJpegQuality(100);


        List<Camera.Size> sizes = params.getSupportedPictureSizes();
        int cindex = getCameraSize(minWidthRange, maxWidthRange);

        params.setPictureSize(sizes.get(cindex).width, sizes.get(cindex).height);
        params.setPreviewSize(sizes.get(cindex).width, sizes.get(cindex).height);


        params.setZoom(zoom);
        // if (!android.os.Build.MANUFACTURER.equals("HUAWEI")){
        try {
            mCamera.setParameters(params);
        } catch (Exception e) {
            log(e.getMessage());
            handle_catch_camera_params(has_FLASH);
            isParamsSet = false;
            e.printStackTrace();
        }
        // }


        options.setVisibility(View.INVISIBLE);
        flash.setVisibility(View.VISIBLE);
        zoom_option.setVisibility(View.VISIBLE);

    }

    /**
     * handle flash
     */
    private void open_flash_status() {
        //flash.setVisibility(View.INVISIBLE);
        //options.setVisibility(View.VISIBLE);

        set_flash_status(status_counter);
        // 0 -- 2
        if (status_counter == 2) {
            status_counter = 0;
        } else {
            status_counter++;
        }


    }


    private void perfotm_cancel_pic() {
        Camera_Status = false;
        if (isFromEdit) {
            create_camera();
        }
        top_choice.setVisibility(View.INVISIBLE);
        search_linear.setVisibility(View.VISIBLE);
        search_linear.bringToFront();
        img.setVisibility(View.INVISIBLE);
        flash.bringToFront();
        options.bringToFront();
        mCamera.startPreview();
        camera_rotate.setVisibility(View.VISIBLE);
        flash.setVisibility(View.VISIBLE);
        zoom_option.setVisibility(View.VISIBLE);
        camera_rotate.bringToFront();
//        edit.setVisibility(View.INVISIBLE);

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        try {
            if (camera_id == Camera.CameraInfo.CAMERA_FACING_BACK) {


                final boolean[] IS_FOCUS_FINISHED = {false};
                int x = (int) event.getY();
                int y = (int) event.getX();

                final TranslateAnimation ta = new TranslateAnimation(cr_y, cr_x, x, y);
                ta.setDuration(1);
                ta.setFillAfter(true);
                ta.isFillEnabled();

                // focus.setAnimation(ta);

//                final Animation zoom = AnimationUtils.loadAnimation(this, R.anim.zoom_icon);
//                Animation zoom_out = AnimationUtils.loadAnimation(this, R.anim.zoom_out_icon);
                final Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
                if (IS_FOCUS_FINISHED[0]) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            shake.cancel();
                        }
                    }, 700);
                }
                if (!Camera_Status) {
                    focus.setVisibility(View.VISIBLE);
                    focus.bringToFront();
                    focus.setAnimation(shake);
                    assert mCamera != null;
                    mCamera.autoFocus(new Camera.AutoFocusCallback() {
                        @Override
                        public void onAutoFocus(boolean success, Camera camera) {
                            try {
                                focus.setVisibility(View.GONE);
                                IS_FOCUS_FINISHED[0] = true;
                                if (success) {
                                    MediaPlayer mediaPlayer = MediaPlayer.create(ActivityCamera.this, R.raw.focuss);
                                    mediaPlayer.start();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                log(e.getMessage());
                            }

                        }
                    });
                }

                cr_x = x;
                cr_y = y;
            }

        } catch (Exception e) {
            e.printStackTrace();
            log(e.getMessage());
        }
        return true;
    }

    /**
     * orientation detector by accelerometer {@link Sensor}
     *
     * @param event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(event);
        }
    }

    private int camera_orientation_z(float z) {
        if (z > 8) {
//           Log.i(TAG, "camera_orientation_z: "+1);
            return 0;//means user bend phone forward
        } else {
//           Log.i(TAG, "camera_orientation_z: "+0);
            return 0;
        }
    }

    private int camera_orientation_y(float y) {
        if (y > 0) {
//            Log.i(TAG, "camera_orientation_y: "+0);
            return 0;
        } else {
//            Log.i(TAG, "camera_orientation_y: "+-1);
            return -1;
        }
    }


    private int camera_orientation_status(float x) {
        if (4 < x) {
            //this means landscape on right
//              Log.i(TAG, "camera_orientation_status: right landscape");
            return 1;
        } else if (x < -4) {
            //this means landscape on left
//              Log.i(TAG, "camera_orientation_status: left landscape");
            return 2;
        } else {
            //this is portrait
//             Log.i(TAG, "camera_orientation_status: portrait");
            return 0;
        }
    }

    private void getAccelerometer(SensorEvent event) {
        float[] values = event.values;
        // Movement
        float x = values[0];
        float y = values[1];
        float z = values[2];

        log_check(x, y, z);

        this.last_known_y = camera_orientation_y(y);

//        Log.i(TAG, "getAccelerometer: "+z);

//        Log.i(TAG, "getAccelerometer: "+y);
//        TODO for anbdroid below 22 api
//        if (Build.VERSION.SDK_INT > 22) {//LOLIPOP version -> 22
        last_known_orientation = camera_orientation_status(x);//set landscape two modes and one portrait mode
        last_known_z = camera_orientation_z(z);
//        Log.i(TAG, "getAccelerometer: x ="+last_known_orientation);
//        Log.i(TAG, "getAccelerometer: z = "+last_known_z);
//        }else {
//            last_known_orientation = 0;//set only portrait mode
//        }

    }

    private void log_check(float x, float y, float z) {

        if ((x - last_number_of_x) > 5 || (last_number_of_x - x) > 5) {
            last_number_of_x = x;
            Log.i("CAMERA_INF", "Value of x -> " + x);
        }
        if ((y - last_number_of_y) > 5 || (last_number_of_y - y) > 5) {
            last_number_of_y = y;
            Log.i("CAMERA_INF", "Value of y -> " + y);
        }
        if ((z - last_number_of_z) > 5 || (last_number_of_z - z) > 5) {
            last_number_of_z = z;
            Log.i("CAMERA_INF", "Value of z -> " + z);
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * A basic Camera preview class
     */
    public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
        private SurfaceHolder mHolder;
        private Camera mCamera;

        public CameraPreview(Context context, Camera camera) {
            super(context);
            mCamera = camera;
            // Install a SurfaceHolder.Callback so we get notified when the
            // underlying surface is created and destroyed.
            mHolder = getHolder();
            mHolder.addCallback(this);
            // deprecated setting, but required on Android versions prior to 3.0
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        public void surfaceCreated(SurfaceHolder holder) {
            // The Surface has been created, now tell the camera where to draw the preview.
            if (!isFromEdit) {
                try {

                    Camera.Parameters parameters = mCamera.getParameters();
                    Log.i(TAG, "surfaceCreated: " + parameters);
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);
                    parameters.setJpegQuality(90);
                    int f = this.getResources().getConfiguration().orientation;
                    if (this.getResources().getConfiguration().orientation
                            != Configuration.ORIENTATION_LANDSCAPE
                            && !isFromEdit) {
                        parameters.set("orientation", "portrait");
                        List<Integer> zooms = parameters.getZoomRatios();
                        parameters.setZoom(zooms.get(zooms.size() - 1));
                        mCamera.setDisplayOrientation(90);
//                        parameters.setRotation(90);
                    }
//                parameters.setPictureSize(C,100);
                    mCamera.setPreviewDisplay(holder);
                    mCamera.startPreview();


                    // mCamera.setParameters(parameters);//commented
                } catch (IOException e) {
                    log(e.getMessage());
                    Log.d(TAG, "Error setting camera preview: " + e.getMessage());
                }
            }
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            // empty. Take care of releasing the Camera preview in your activity.
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            // If your preview can change or rotate, take care of those events here.
            // Make sure to stop the preview before resizing or reformatting it.

            if (mHolder.getSurface() == null) {
                // preview surface does not exist
                return;
            }

            // stop preview before making changes
            try {
                mCamera.stopPreview();
            } catch (Exception e) {
                log(e.getMessage());
                // ignore: tried to stop a non-existent preview
            }

            // set preview size and make any resize, rotate or
            // reformatting changes here

            // start preview with new settings
            try {
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();

            } catch (Exception e) {
                log(e.getMessage());
                Log.d(TAG, "Error starting camera preview: " + e.getMessage());
            }
        }
    }
}
