package com.s21m.proftaaks2maatwerk.extensions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class Bitmap {

    private android.graphics.Bitmap bitmap;

    public android.graphics.Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(android.graphics.Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap(android.graphics.Bitmap bitmap){
        this.bitmap = bitmap;
    }

    /**
     * Converts a immutable bitmap to a mutable bitmap. This operation doesn't allocates
     * more memory that there is already allocated.
     *
     * @param file - cache file to write bitmap to
     */
    public void convertToMutable(File file) throws IOException {
        //Open an RandomAccessFile
        //Make sure you have added uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
        //into AndroidManifest.xml file
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");

        // get the width and height of the source bitmap.
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        android.graphics.Bitmap.Config type = bitmap.getConfig();

        //Copy the byte to the file
        //Assume source bitmap loaded using options.inPreferredConfig = Config.ARGB_8888;
        FileChannel channel = randomAccessFile.getChannel();
        MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE, 0, bitmap.getRowBytes()*height);
        bitmap.copyPixelsToBuffer(map);
        //recycle the source bitmap, this will be no longer used.
        bitmap.recycle();
        System.gc();// try to force the bytes from the imgIn to be released

        //Create a new bitmap to load the bitmap again. Probably the memory will be available.
        bitmap = android.graphics.Bitmap.createBitmap(width, height, type);
        map.position(0);
        //load it back from temporary
        bitmap.copyPixelsFromBuffer(map);
        //close the temporary file and channel , then delete that also
        channel.close();
        randomAccessFile.close();

        // delete the temp file
        file.delete();
    }

    /**
     * Resize the bitmap to not be longer/wider than
     * the maxSize given while retaining ratio.
     *
     * @param maxSize maximum length/width
     */
    public void resize(int maxSize) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        }
        else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

        bitmap = android.graphics.Bitmap.createScaledBitmap(bitmap, width, height, true);
    }

    /**
     *
     * @param file file to save the bitmap into
     * @throws IOException when saving fails or the file was not found
     */
    public void saveToFile(File file) throws IOException {
        FileOutputStream fOut = new FileOutputStream(file);
        bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, fOut);
        fOut.flush();
        fOut.close();
    }
}
