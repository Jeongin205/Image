package com.example.image;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.image.databinding.ActivityMainBinding;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity implements BottomSheetWriteFragment.BottomSheetListener {
    ActivityMainBinding binding;
    BottomSheetWriteFragment sheetWriteFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sheetWriteFragment = new BottomSheetWriteFragment();
        binding.image.setOnClickListener(view -> {
            sheetWriteFragment.show(getSupportFragmentManager(), "bottomSheet");
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClickGallery() {//저장소 읽어오는 퍼미션
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_DENIED){
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }//퍼미션이 허용되지 않아 있다면 퍼미션 허용메세지 생성 & 코드0보내기
        else{
            setGallery();//퍼미션이 허용되어 있다면 갤러리불러오기
        }
        sheetWriteFragment.dismiss();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClickCamera() {//카메라 퍼미션
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)==PackageManager.PERMISSION_DENIED){
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);//코드 1보내기
        }
        else{
            setCamera();
        }

        sheetWriteFragment.dismiss();
    }

    @Override
    public void onClickBasics() {//기본이미지 선택했을때 drawable에 있는거 불러오기
        Glide.with(getApplicationContext()).load(R.drawable.ic_profile).into(binding.image);
        Log.d("asdf", String.valueOf(Uri.parse("android.resource://com.example.image/drawable/ic_profile")));
        sheetWriteFragment.dismiss();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //퍼미션허용 메세지를 보냈을떄
        if (Build.VERSION.SDK_INT >= 23) {

            // requestPermission의 배열의 index가 아래 grantResults index와 매칭
            // 퍼미션이 승인되면
            if(grantResults.length > 0  && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                if(requestCode==0){//위에서 코드 0을 보냄
                    setGallery();
                }
                else if(requestCode==1){//코드 1
                    setCamera();
                }

            }
            // 퍼미션이 승인 거부되면
            else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                    //퍼미션 2번까진 아래코드
                    Toast.makeText(MainActivity.this, "퍼미션이 거부되었습니다. 다시 눌러 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();


                }else {
                    Toast.makeText(MainActivity.this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();
                    //2번이후론 알아서
                }
            }
            }
        }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {//코드 0번 갤러리
            if (resultCode == RESULT_OK) {
                try {
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    Bitmap img = BitmapFactory.decodeStream(in);
                    in.close();
                    Log.d("asdf", getImageUri(this, img).toString());
                    Glide.with(getApplicationContext()).load(img).into(binding.image);
                } catch (Exception e) {

                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show();
            }
        }
        if(requestCode == 1){//코드 1번 카메라
            if(resultCode == RESULT_OK){
                try{
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    Log.d("asdf", getImageUri(this, imageBitmap).toString());
                    Glide.with(getApplicationContext()).load(imageBitmap).into(binding.image);
                }catch (Exception e){

                }
            }
        }
    }
    private void setGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 0);
    }
     private void setCamera(){
         Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
         startActivityForResult(intent, 1);
     }
    public Uri getImageUri(Context ctx, Bitmap bitmap) {//비트맵 Uri로 변환
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage
                (ctx.getContentResolver(),
                        bitmap, "Temp", null);
        return Uri.parse(path);
    }
}