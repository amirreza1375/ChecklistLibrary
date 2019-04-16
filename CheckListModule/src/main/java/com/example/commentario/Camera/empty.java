package com.example.commentario.Camera;

import android.Manifest;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.commentario.R;

import java.util.List;

public class empty extends AppCompatActivity implements View.OnClickListener
 {

    private static final String TAG = "empty";

    String App_Folder_Name = ".Operator Track - Maintenance";
    String sub_folder_name = "Pictures";
    String sub_folder_path = String.valueOf(System.currentTimeMillis());

    private int zoom = 0;

    private float last_number_of_x = 0;
    private float last_number_of_y = 0;
    private float last_number_of_z = 0;
//
//    /************************    camera api  ************************/
    private Camera mCamera;
//
    private LinearLayout top_choice;
    private Boolean Camera_Status = false;
//
    private boolean has_flash = false;

    private Uri imageUri;
    private LinearLayout cancel;
    private LinearLayout tick;
//    private int camera_id = Camera.CameraInfo.CAMERA_FACING_BACK;
//
//
////    private empty.CameraPreview mPreview;
//
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
//    private Camera.PictureCallback mPicture;
//    /***************************************************************************/
    private ImageView focus;
    private RelativeLayout root;
    private boolean FLASH_IS_ON = false;
    private ImageView capture;
    private LinearLayout comment;
    private String path;
    private ImageView flash;
    private ImageView camera_rotate;
    private TextView edit;
    private int max_zoom = 0;
    private String mCurrentPhotoPath;
    private LinearLayout options;
    private LinearLayout camera_hldr;
    private String comment_txt;
    private String[] permissions = {Manifest.permission.CAMERA
            , Manifest.permission.WRITE_EXTERNAL_STORAGE
            , Manifest.permission.READ_EXTERNAL_STORAGE};

//    private SensorManager sm;
    private long lastUpdate;
    private int last_known_orientation = 0;

    private LinearLayout zoom_option;
    private ImageView zoom_in;
    private ImageView zoom_out;

    private int last_known_z = 0;
    private int last_known_y = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_library);

//        preview = findViewById(R.id.camera_preview);
//        img = findViewById(R.id.img);
//        top_choice = findViewById(R.id.top_choice);
//        cancel = findViewById(R.id.cancel);
//        tick = findViewById(R.id.tick);
//        search_linear = findViewById(R.id.search_linear);
//        flash = findViewById(R.id.flash);
//        camera_rotate = findViewById(R.id.camera_rotate);
//        focus = findViewById(R.id.focus);
//        options = findViewById(R.id.options);
//        edit = findViewById(R.id.edit);
//        root = findViewById(R.id.root);
//        zoom_option = findViewById(R.id.zoom_options);
//        zoom_in = findViewById(R.id.zoom_in);
//        zoom_out = findViewById(R.id.zoom_out);
//        cr_x = focus.getX();
//        cr_x = focus.getY();
////        preview.setOnTouchListener(this);
//        tick.setOnClickListener(this);
//        cancel.setOnClickListener(this);
//        flash.setOnClickListener(this);
//        options.setOnClickListener(this);
//        edit.setOnClickListener(this);
//        camera_rotate.setOnClickListener(this);
//        zoom_in.setOnClickListener(this);
//        zoom_out.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

    }




//    public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
//        private SurfaceHolder mHolder;
//        private Camera mCamera;
//
//        public CameraPreview(Context context, Camera camera) {
//            super(context);
//            mCamera = camera;
//            // Install a SurfaceHolder.Callback so we get notified when the
//            // underlying surface is created and destroyed.
//            mHolder = getHolder();
//            mHolder.addCallback(this);
//            // deprecated setting, but required on Android versions prior to 3.0
//            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//        }
//
//        public void surfaceCreated(SurfaceHolder holder) {
//            // The Surface has been created, now tell the camera where to draw the preview.
//            if (! isFromEdit) {
//                try {
//
//                    Camera.Parameters parameters = mCamera.getParameters();
//                    Log.i(TAG, "surfaceCreated: " + parameters);
//                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);
//                    parameters.setJpegQuality(90);
//                    int f = this.getResources().getConfiguration().orientation;
//                    if (this.getResources().getConfiguration().orientation
//                            != Configuration.ORIENTATION_LANDSCAPE
//                            && ! isFromEdit) {
//                        parameters.set("orientation", "portrait");
//                        List<Integer> zooms = parameters.getZoomRatios();
//                        parameters.setZoom(zooms.get(zooms.size() - 1));
//                        mCamera.setDisplayOrientation(90);
////                        parameters.setRotation(90);
//                    }
////                parameters.setPictureSize(C,100);
//                    mCamera.setPreviewDisplay(holder);
//                    mCamera.startPreview();
//
//
//                    // mCamera.setParameters(parameters);//commented
//                } catch (IOException e) {
//                    Log.d(TAG, "Error setting camera preview: " + e.getMessage());
//                }
//            }
//        }
//
//        public void surfaceDestroyed(SurfaceHolder holder) {
//            // empty. Take care of releasing the Camera preview in your activity.
//        }
//
//        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
//            // If your preview can change or rotate, take care of those events here.
//            // Make sure to stop the preview before resizing or reformatting it.
//
//            if (mHolder.getSurface() == null) {
//                // preview surface does not exist
//                return;
//            }
//
//            // stop preview before making changes
//            try {
//                mCamera.stopPreview();
//            } catch (Exception e) {
//                // ignore: tried to stop a non-existent preview
//            }
//
//            // set preview size and make any resize, rotate or
//            // reformatting changes here
//
//            // start preview with new settings
//            try {
//                mCamera.setPreviewDisplay(mHolder);
//                mCamera.startPreview();
//
//            } catch (Exception e) {
//                Log.d(TAG, "Error starting camera preview: " + e.getMessage());
//            }
//        }
//    }

}
