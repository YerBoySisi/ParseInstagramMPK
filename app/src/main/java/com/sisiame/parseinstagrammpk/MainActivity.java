package com.sisiame.parseinstagrammpk;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    public final String APP_TAG = "Parse Instagram";
    public String photoFileName = "photo.jpg";
    File photoFile;

    EditText etDescription;
    ImageView ivPostImg;
    Button btnTakePicture, btnSubmit, btnLogout;
    ActivityResultLauncher<Intent> cameraResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etDescription = findViewById(R.id.etDescription);
        ivPostImg = findViewById(R.id.ivPostImg);
        btnTakePicture = findViewById(R.id.btnTakePicture);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnLogout = findViewById(R.id.btnLogout);

        cameraResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        // by this point we have the camera photo on disk
                        Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                        // RESIZE BITMAP, see section below
                        // Load the taken image into a preview
                        ImageView ivPreview = ivPostImg;
                        ivPreview.setImageBitmap(takenImage);
                    } else { // Result was a failure
                        Toast.makeText(this, "Error taking picture", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        btnTakePicture.setOnClickListener(v -> onLaunchCamera());

        btnSubmit.setOnClickListener(v -> {
            if(!isValidDescription()) {
                Log.e(TAG, "Error posting: Description cannot be empty");
                Toast.makeText(MainActivity.this, "Description cannot be empty!", Toast.LENGTH_SHORT).show();
                return;
            }

            savePost(etDescription.getText().toString(), ParseUser.getCurrentUser(), photoFile);
        });

        btnLogout.setOnClickListener(v -> ParseUser.logOutInBackground(e -> {

            if(e != null) {
                Log.e(TAG, "Error signing out", e);
                Toast.makeText(MainActivity.this, "Error signing out", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.i(TAG, "Sign out successful");
            goToLoginActivity();
            Toast.makeText(MainActivity.this, "Signed out", Toast.LENGTH_SHORT).show();


        }));

        queryPosts();
    }

    private boolean isValidDescription() {
        return etDescription.getText().length() != 0;
    }

    private void savePost(String description, ParseUser user, File photoFile) {

        Post post = new Post();
        post.setDescription(description);
        post.setUser(user);
        post.setImage(new ParseFile(photoFile));

        post.saveInBackground(e -> {
            if(e != null) {
                Log.e(TAG, "Error saving post", e);
                Toast.makeText(MainActivity.this, "Error saving post", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(MainActivity.this, "Post saved successfully", Toast.LENGTH_SHORT).show();
            etDescription.setText("");
            etDescription.clearFocus();
        });

    }

    private void queryPosts() {

        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);

        /*
        can be replaced with lambda
         */
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if(e != null) {
                    Log.e(TAG, "Error loading posts", e);
                    Toast.makeText(MainActivity.this, "Error loading posts", Toast.LENGTH_SHORT).show();
                    return;
                }

                for(Post post: posts) {
                    Log.i(TAG, " \nPost: " + post.getDescription() + ",\nUsername: " + post.getUser().getUsername());
                }
            }
        });

    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(APP_TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        /*
        redundant declaration; condense lines to
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
         */
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

    public void onLaunchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(MainActivity.this, "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        cameraResultLauncher.launch(intent);

    }

    private void goToLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

}