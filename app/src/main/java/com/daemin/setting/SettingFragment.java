package com.daemin.setting;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daemin.common.BasicFragment;
import com.daemin.enumclass.User;
import com.daemin.event.BackKeyEvent;
import com.daemin.event.ChangeFragEvent;
import com.daemin.timetable.R;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class SettingFragment extends BasicFragment implements View.OnClickListener{
    public SettingFragment() {
        super(R.layout.fragment_setting, "SettingFragment");
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);
        setLayout(root);
        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == getActivity().RESULT_OK) {
            switch(requestCode) {
                case REQUEST_IMAGE_ALBUM:
                    contentUri = data.getData();
                case REQUEST_IMAGE_CAPTURE:
                    rotatePhoto();
                    cropImage(contentUri);
                    break;
                case REQUEST_IMAGE_CROP:
                    Bundle extras = data.getExtras();
                    if(extras != null) {
                        Bitmap photo = data.getParcelableExtra("data");
                        Bitmap output = Bitmap.createBitmap(photo.getWidth(),
                                photo.getHeight(), Config.ARGB_8888);
                        Canvas canvas = new Canvas(output);
                        final int color = 0xff424242;
                        final Paint paint = new Paint();
                        final Rect rect = new Rect(0, 0, photo.getWidth(),
                                photo.getHeight());
                        final RectF rectF = new RectF(rect);
                        final float roundPx = 150;

                        paint.setAntiAlias(true);
                        canvas.drawARGB(0, 0, 0, 0);
                        paint.setColor(color);
                        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

                        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
                        canvas.drawBitmap(photo, rect, rect, paint);

                        ivProfile.setImageBitmap(output);

                        if(mCurrentPhotoPath != null) {
                            File f = new File(mCurrentPhotoPath);
                            if(f.exists()) {
                                f.delete();
                            }
                            mCurrentPhotoPath = null;
                        }
                    }
                    break;
                case REQUEST_SETTING_UNIV:
                    break;
            }
        }
    }

    private void cropImage(Uri contentUri) {
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        //indicate image type and Uri of image
        cropIntent.setDataAndType(contentUri, "image/*");
        //set crop properties
        cropIntent.putExtra("crop", "true");
        //indicate aspect of desired crop
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        //indicate output X and Y
        cropIntent.putExtra("outputX", 256);
        cropIntent.putExtra("outputY", 256);
        //retrieve data on return
        cropIntent.putExtra("return-data", true);
        startActivityForResult(cropIntent, REQUEST_IMAGE_CROP);
    }

    public Bitmap getBitmap() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inInputShareable = true;
        options.inDither=false;
        options.inTempStorage=new byte[32 * 1024];
        options.inPurgeable = true;
        options.inJustDecodeBounds = false;
        File f = new File(mCurrentPhotoPath);
        FileInputStream fs=null;
        try {
            fs = new FileInputStream(f);
        } catch (FileNotFoundException e) {
            //TODO do something intelligent
            e.printStackTrace();
        }
        Bitmap bm = null;
        try {
            if(fs!=null) bm=BitmapFactory.decodeFileDescriptor(fs.getFD(), null, options);
        } catch (IOException e) {
            //TODO do something intelligent
            e.printStackTrace();
        } finally{
            if(fs!=null) {
                try {
                    fs.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return bm;
    }

    public void rotatePhoto() {
        ExifInterface exif;
        try {
            if(mCurrentPhotoPath == null) {
                mCurrentPhotoPath = contentUri.getPath();
            }
            exif = new ExifInterface(mCurrentPhotoPath);
            int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int exifDegree = exifOrientationToDegrees(exifOrientation);
            if(exifDegree != 0) {
                Bitmap bitmap = getBitmap();
                Bitmap rotatePhoto = rotate(bitmap, exifDegree);
                saveBitmap(rotatePhoto);
            }
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public int exifOrientationToDegrees(int exifOrientation)
    {
        if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_90)
        {
            return 90;
        }
        else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_180)
        {
            return 180;
        }
        else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_270)
        {
            return 270;
        }
        return 0;
    }

    public static Bitmap rotate(Bitmap image, int degrees)
    {
        if(degrees != 0 && image != null)
        {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float)image.getWidth(), (float)image.getHeight());
            try
            {
                Bitmap b = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), m, true);
                if(image != b)
                {
                    image.recycle();
                    image = b;
                }
                image = b;
            }
            catch(OutOfMemoryError ex)
            {
                ex.printStackTrace();
            }
        }
        return image;
    }

    public void saveBitmap(Bitmap bitmap) {
        File file = new File(mCurrentPhotoPath);
        OutputStream out = null;
        try {
            out = new FileOutputStream(file);
        }
        catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        bitmap.compress(CompressFormat.JPEG, 100, out) ;
        try {
            out.close();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        bitmap.recycle();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivProfile:
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_IMAGE_ALBUM);
                break;
            case R.id.btSettingVer:
                EventBus.getDefault().post(new ChangeFragEvent(SettingVerFragment.class, getActivity().getResources().getString(R.string.setting_ver)));
                break;
            case R.id.btSettingGroup:
                EventBus.getDefault().post(new ChangeFragEvent(SettingGroupFragment.class, getActivity().getResources().getString(R.string.setting_group)));
                break;
            case R.id.btSettingInit:
                EventBus.getDefault().post(new ChangeFragEvent(SettingInitFragment.class, getActivity().getResources().getString(R.string.setting_init)));
                break;
            case R.id.btSettingCalendar:
                EventBus.getDefault().post(new ChangeFragEvent(SettingCalendarFragment.class, getActivity().getResources().getString(R.string.setting_calendar)));
                break;
            case R.id.btSettingOpenSrc:
                EventBus.getDefault().post(new ChangeFragEvent(SettingOpenSrcFragment.class, getActivity().getResources().getString(R.string.setting_opensrc)));
                break;
            case R.id.btSettingQA:
                Intent i = new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://open.kakao.com/o/sqXjIpf"));
                getActivity().startActivity(i);
                break;
            case R.id.btSettingTime:
                EventBus.getDefault().post(new ChangeFragEvent(SettingTimeFragment.class, getActivity().getResources().getString(R.string.setting_time)));
                break;
        }
    }
    public void setLayout(View root){
        EventBus.getDefault().post(new BackKeyEvent("SettingFragment",null,null));
        mCurrentPhotoPath = null;
        ivProfile = (ImageView)root.findViewById(R.id.ivProfile);
        btSettingInit = (TextView)root.findViewById(R.id.btSettingInit);
        btSettingGroup = (RelativeLayout)root.findViewById(R.id.btSettingGroup);
        btSettingOpenSrc = (RelativeLayout)root.findViewById(R.id.btSettingOpenSrc);
        btSettingCalendar = (TextView)root.findViewById(R.id.btSettingCalendar);
        btSettingQA = (TextView)root.findViewById(R.id.btSettingQA);
        btSettingTime = (TextView)root.findViewById(R.id.btSettingTime);
        //btSettingTime = (TextView)root.findViewById(R.id.btSettingTime);
        tvVer = (TextView)root.findViewById(R.id.tvVer);
        tvUnivName = (TextView)root.findViewById(R.id.tvUnivName);
        tvVer.setText("v"+ User.INFO.appVer);
        tvUnivName.setText(User.INFO.getGroupName());
        btSettingVer = (RelativeLayout)root.findViewById(R.id.btSettingVer);
        File path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        if(!path.exists()) {
            path.mkdirs();
        }
        ivProfile.setOnClickListener(this);
        btSettingVer.setOnClickListener(this);
        btSettingGroup.setOnClickListener(this);
        btSettingInit.setOnClickListener(this);
        btSettingCalendar.setOnClickListener(this);
        btSettingQA.setOnClickListener(this);
        btSettingTime.setOnClickListener(this);
        btSettingOpenSrc.setOnClickListener(this);
    }
    private ImageView ivProfile;
    private TextView btSettingInit,btSettingCalendar,btSettingQA,btSettingTime, tvVer, tvUnivName;
    private RelativeLayout btSettingVer,btSettingGroup, btSettingOpenSrc;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_ALBUM = 2;
    private static final int REQUEST_IMAGE_CROP = 3;
    private static final int REQUEST_SETTING_UNIV = 4;
    private String mCurrentPhotoPath;
    private Uri contentUri;


}