//package com.example.gradproject;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.net.Uri;
//import android.os.Bundle;
//import android.provider.MediaStore;
//import android.util.Log;
//import android.view.View;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.ProgressBar;
//import android.widget.Toast;
//
//import com.bumptech.glide.Glide;
//import com.example.gradproject.RegistrationAndLogin.ActivityLogin;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.auth.UserProfileChangeRequest;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;
//import com.google.firebase.storage.UploadTask;
//
//import java.io.IOException;
//
//public class ActivityChangePic extends AppCompatActivity
//{
//    private static final int CHOOSE_IMAGE = 101;
//    ImageView profilePic;
//    EditText displayName;
//    Uri uriProfileImage;
//    ProgressBar picProgressBar;
//    String profileImageUrl;
//    private FirebaseAuth mAuth;
//
//    protected void onCreate(Bundle savedInstanceState)
//    {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_change_pic);
//
//        profilePic = findViewById(R.id.UserProfilePic);
//        displayName = findViewById(R.id.UserDisplayName);
//        picProgressBar = findViewById(R.id.picProgressBar);
//        mAuth = FirebaseAuth.getInstance();
//
//        //loadUserInformation();
//
//        profilePic.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                showImageChooser();
//            }
//        });
//
//        findViewById(R.id.SavePicAndNameBtn).setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                saveUserInformation();
//            }
//        });
//
//    }
//
////    private void loadUserInformation()
////    {
////        FirebaseUser user = mAuth.getCurrentUser();
////        uriProfileImage = user.getPhotoUrl();
////
////        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
////
////        if (user != null)
////        {
////            final StorageReference profileImageRef = FirebaseStorage.getInstance().getReference("profilepics/" + System.currentTimeMillis() + ".jpg");
////
////            if (uriProfileImage != null) {
////
////                profileImageRef.putFile(uriProfileImage)
////                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
////                        {
////                            @Override
////                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
////                            {
////                                //this is the new way to do it
////                                profileImageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>()
////                                {
////                                    @Override
////                                    public void onComplete(@NonNull Task<Uri> task) {
////                                        String profileImageUrl=task.getResult().toString();
////                                        Log.i("URL",profileImageUrl);
////                                    }
////                                });
////                            }
////                        })
////                        .addOnFailureListener(new OnFailureListener()
////                        {
////                            @Override
////                            public void onFailure(@NonNull Exception e)
////                            {
////                                picProgressBar.setVisibility(View.GONE);
////                                Toast.makeText(ActivityChangePic.this, "aaa "+e.getMessage(), Toast.LENGTH_SHORT).show();
////                            }
////                        });
////            }
////
////            if (user.getDisplayName() != null)
////            {
////                displayName.setText(user.getDisplayName());
////            }
////        }
////    }
//
//    protected void onStart()
//    {
//        super.onStart();
//
//         if(mAuth.getCurrentUser() == null)
//         {
//             finish();
//             startActivity(new Intent(this, ActivityLogin.class));
//         }
//    }
//
//    private void saveUserInformation()
//    {
//        String stringDisplayName = displayName.getText().toString();
//
//        if (stringDisplayName.isEmpty()) {
//            displayName.setError("Name required");
//            displayName.requestFocus();
//            return;
//        }
//
//        FirebaseUser user = mAuth.getCurrentUser();
//
//        if (user != null && profileImageUrl != null)
//        {
//            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
//                    .setDisplayName(stringDisplayName)
//                    .setPhotoUri(Uri.parse(profileImageUrl))
//                    .build();
//
//            user.updateProfile(profile)
//                    .addOnCompleteListener(new OnCompleteListener<Void>()
//                    {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task)
//                        {
//                            if (task.isSuccessful())
//                            {
//                                Toast.makeText(ActivityChangePic.this, "Profile Updated", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
//    {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null)
//        {
//            uriProfileImage = data.getData();
//            try
//            {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage);
//                profilePic.setImageBitmap(bitmap);
//
//                uploadImageToFirebaseStorage();
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    private void uploadImageToFirebaseStorage()
//    {
//        StorageReference profileImageRef =
//                FirebaseStorage.getInstance().getReference("profilepics/" + System.currentTimeMillis() + ".jpg");
//
//        if (uriProfileImage != null)
//        {
//            picProgressBar.setVisibility(View.VISIBLE);
//            profileImageRef.putFile(uriProfileImage)
//                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
//                    {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
//                        {
//                            picProgressBar.setVisibility(View.INVISIBLE);
//                            profileImageUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener()
//                    {
//                        @Override
//                        public void onFailure(@NonNull Exception e)
//                        {
//                            picProgressBar.setVisibility(View.INVISIBLE);
//                            Toast.makeText(ActivityChangePic.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    });
//        }
//    }
//
//    private void showImageChooser()
//    {
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), CHOOSE_IMAGE);
//    }
//}
