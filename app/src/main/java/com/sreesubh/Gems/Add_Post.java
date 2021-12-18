package com.sreesubh.Gems;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.dhaval2404.imagepicker.ImagePicker;

public class Add_Post extends AppCompatActivity {
    ImageView imageView;
    EditText text;
    Button post;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_post);
        imageView = findViewById(R.id.post_image);
        text = findViewById(R.id.post_body);
        post = findViewById(R.id.post_upload);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.Companion.with(Add_Post.this)
                        .crop()
                        .maxResultSize(1080,1080)
                        .start();
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.home)
        {
            Intent i = new Intent(Add_Post.this,MainDashboard.class);
            startActivity(i);
            finish();
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode== Activity.RESULT_OK)
        {
            Uri img = data.getData();
            Log.d("ImageURI", String.valueOf(img));
            imageView.setImageTintList(null);
            imageView.setImageTintMode(null);
            imageView.setImageURI(img);
        }
    }
}
