package com.example.myapplication.moodle;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class HandleImage {
    // Uploads an image (Bitmap) to Firebase Storage
    public static void LoadImageFile(Bitmap bitmap, final Context context, String mid) {
        final ProgressDialog p; // Progress dialog to show loading status
        FirebaseStorage storage = FirebaseStorage.getInstance(); // Get an instance of FirebaseStorage

        // Reference to the storage path where the image will be stored, using 'mid' as the image identifier
        StorageReference storageReference = storage.getReferenceFromUrl("gs://textmaster-123.firebasestorage.app").child((mid));

        // Convert the bitmap to a byte array
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream); // Compress bitmap as PNG
        byte[] data = outputStream.toByteArray(); // Convert to byte array

        // Show a progress dialog while uploading
        p = ProgressDialog.show(context, "Write your title", "Loading, please wait...", true);
        p.setCancelable(true); // Allow user to cancel the upload
        p.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL); // Set progress bar style
        p.setMessage("Loading...");
        p.show();

        // Start uploading the image to Firebase
        UploadTask uploadTask = storageReference.putBytes(data);

        // Handle upload failure
        uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        exception.printStackTrace(); // Print error details
                        p.dismiss(); // Dismiss progress dialog on failure
                    }
                })
                // Handle successful upload
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        p.dismiss(); // Dismiss progress dialog on success
                    }
                });
    }

    // Downloads an image from Firebase Storage and displays it in an ImageView
    public static void DownLoadImage(final ImageView imageView, final Context context, String mid) {
        FirebaseStorage storage = FirebaseStorage.getInstance(); // Get an instance of FirebaseStorage
        final long ONE_MEGABYTE = 4096 * 4096; // Define max size for the image download (1MB)

        // Reference to the storage path where the image is stored, using 'mid' as the image identifier
        StorageReference storageReference = storage.getReferenceFromUrl("gs://textmaster-123.firebasestorage.app").child((mid));

        // Download the image as a byte array
        storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Convert the byte array into a Bitmap and display it in the ImageView
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                if(bitmap != null) {
                    imageView.setImageBitmap(bitmap); // Set the downloaded image into the ImageView
                }
            }
        });
    }
}
