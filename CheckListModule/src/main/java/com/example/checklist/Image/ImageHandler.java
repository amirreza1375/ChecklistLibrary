package com.example.checklist.Image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;

import static com.example.checklist.GlobalFuncs.log;

public class ImageHandler {

    private Context context;
    private int maxSize;

    public ImageHandler(Context context,int maxSize){

        this.context = context;
        this.maxSize = maxSize;
    }





    private  String resizeAndCompressImageBeforeSend(Context context,String filePath,String fileName){
        final int MAX_IMAGE_SIZE = maxSize * 1024; // max final file size in kilobytes

        // First decode with inJustDecodeBounds=true to check dimensions of image
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath,options);

        // Calculate inSampleSize(First we are going to resize the image to 800x800 image, in order to not have a big but very low quality image.
        //resizing the image will already reduce the file size, but after resizing we will check the file size and start to compress image
        options.inSampleSize = calculateInSampleSize(options, 800, 800);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        options.inPreferredConfig= Bitmap.Config.ARGB_8888;

        Bitmap bmpPic = BitmapFactory.decodeFile(filePath,options);


        int compressQuality = 100; // quality decreasing by 5 every loop.
        int streamLength;
        do{
            ByteArrayOutputStream bmpStream = new ByteArrayOutputStream();
            Log.d("compressBitmap", "Quality: " + compressQuality);
            bmpPic.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream);
            byte[] bmpPicByteArray = bmpStream.toByteArray();
            streamLength = bmpPicByteArray.length;
            compressQuality -= 5;
            Log.d("compressBitmap", "Size: " + streamLength/1024+" kb");
        }while (streamLength >= MAX_IMAGE_SIZE);

        try {
            //save the resized and compressed file to disk cache
            Log.d("compressBitmap","cacheDir: "+context.getCacheDir());
            FileOutputStream bmpFile = new FileOutputStream(context.getCacheDir()+fileName);
            bmpPic.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpFile);
            bmpFile.flush();
            bmpFile.close();
        } catch (Exception e) {
            log(e.getMessage());
            Log.e("compressBitmap", "Error on saving file");
        }
        //return the path of resized and compressed file
        return  context.getCacheDir()+fileName;
    }



    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

        String debugTag = "MemoryInformation";
        // Image nin islenmeden onceki genislik ve yuksekligi
        final int height = options.outHeight;
        final int width = options.outWidth;
        Log.d(debugTag,"image height: "+height+ "---image width: "+ width);
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        Log.d(debugTag,"inSampleSize: "+inSampleSize);
        return inSampleSize;
    }

    public void ResizeImage(final Context context, final String filePath, final String fileName, final ImageResizerListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String path = resizeAndCompressImageBeforeSend(context,filePath,fileName);
                listener.onImageResized(path);
            }
        }).start();
    }

    public interface ImageResizerListener{
        void onImageResized(String path);
    }


//    public File imageResizer(String path){
//        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
//        Bitmap b = BitmapFactory.decodeFile(path);
//        Bitmap out = Bitmap.createScaledBitmap(b, 320, 480, false);
//
//        return savebitmap();
//    }




//    public long imageSize(String path){
//        Bitmap bitmapOrg = BitmapFactory.decodeFile(path);
//        Bitmap bitmap = bitmapOrg;
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//        byte[] imageInByte = stream.toByteArray();
//        long lengthbmp = imageInByte.length;
//        return lengthbmp;
//    }
//
//    private File savebitmap(String filename) {
//        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
//        OutputStream outStream = null;
//
//        File file = new File(filename + ".png");
//        if (file.exists()) {
//            file.delete();
//            file = new File(extStorageDirectory, filename + ".png");
//            Log.e("file exist", "" + file + ",Bitmap= " + filename);
//        }
//        try {
//            // make a new bitmap from your file
//            Bitmap bitmap = BitmapFactory.decodeFile(file.getName());
//
//            outStream = new FileOutputStream(file);
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
//            outStream.flush();
//            outStream.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        Log.e("file", "" + file);
//        return file;
//
//    }
}
