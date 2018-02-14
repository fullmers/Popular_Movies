package com.amiculous.popularmoviesi.utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by sarah on 14/02/2018.
 */

public class ImageUtils {
    private static final String TAG = ImageUtils.class.getSimpleName();

    public static final String IMAGE_DIR = "imageDir";

    //http://www.codexpedia.com/android/android-download-and-save-image-through-picasso/
    public static Target picassoImageTarget(final Context context, final String imageName) {
        ContextWrapper cw = new ContextWrapper(context);
        final File directory = cw.getDir(IMAGE_DIR, Context.MODE_PRIVATE); // path to /data/data/yourapp/app_imageDir
        return new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                //saveBitmap(context,bitmap,imageName);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final File myImageFile = new File(directory, imageName); // Create image file
                        FileOutputStream fos = null;
                        try {
                            fos = new FileOutputStream(myImageFile);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                fos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        Log.i("image", "image saved to >>>" + myImageFile.getAbsolutePath());

                    }
                }).start();

            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
            }
            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                if (placeHolderDrawable != null) {}
            }
        };
    }

    public static String getMoviePosterFileName(String movieTitle) {
        //Remove all chars that are not allowed in filename
        //https://en.wikipedia.org/wiki/Filename
        String fileName = movieTitle.replace("/","");
        fileName = fileName.replace("\\","");
        fileName = fileName.replace("?","");
        fileName = fileName.replace("%","");
        fileName = fileName.replace("*","");
        fileName = fileName.replace(":","");
        fileName = fileName.replace("|","");
        fileName = fileName.replace("\"","");
        fileName = fileName.replace("<","");
        fileName = fileName.replace(">","");
        fileName = fileName.replace(".","");

        fileName = fileName.replace(" ","_");

        fileName = fileName + ".png";

        return fileName;
    }

    public static File getImageFile(Context context, String fileName) {
        ContextWrapper cw = new ContextWrapper(context.getApplicationContext());
        File directory = cw.getDir(IMAGE_DIR, Context.MODE_PRIVATE);
        return new File(directory, fileName);
    }

    public static boolean deleteImageFile(Context context, String fileName) {
        ContextWrapper cw = new ContextWrapper(context.getApplicationContext());
        File directory = cw.getDir(IMAGE_DIR, Context.MODE_PRIVATE);
        File myImageFile = new File(directory, fileName);
        return myImageFile.delete();
    }

    // https://stackoverflow.com/questions/19978100/how-to-save-bitmap-on-internal-storage-download-from-internet
    public static void saveBitmap(Context context, Bitmap b, String picName){
        FileOutputStream fos;
        try {
            fos = context.openFileOutput(picName, Context.MODE_PRIVATE);
            String dir = context.getFilesDir().getPath();
            Log.d(TAG,"saved in directory:" + dir);

            b.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        }
        catch (FileNotFoundException e) {
            Log.d(TAG, "file not found");
            e.printStackTrace();
        }
        catch (IOException e) {
            Log.d(TAG, "io exception");
            e.printStackTrace();
        }
    }
}
