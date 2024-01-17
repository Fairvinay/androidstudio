package com.jwell.suite.model;

import android.graphics.Bitmap;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class ImageTaken {

    public Bitmap imageTaken ;
    private String blankImage="data:image/gif;base64,R0lGODlhAQABAIAAAP7//wAAACH5BAAAAAAALAAAAAABAAEAAAICRAEAOw==";
    public ImageTaken(Bitmap imageTaken) {
        this.imageTaken = imageTaken;
    }

    public Bitmap getImageTaken() {
        return imageTaken;
    }

    public void setImageTaken(Bitmap imageTaken) {
        this.imageTaken = imageTaken;
    }

    @Override
    public String toString() {
        String convertImage = blankImage;
        try {
            if( imageTaken != null) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                imageTaken.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();

                convertImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
                return "ImageTaken{" +
                        "imageTaken=" + convertImage +
                        '}';
            }

        }catch(Exception er){
              return "ImageTaken{" +
                        "imageTaken=" + blankImage+
                        '}';
        }

        return "ImageTaken{" +
                "imageTaken=" + convertImage+
                '}';
    }
}
