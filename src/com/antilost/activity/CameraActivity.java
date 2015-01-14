package com.antilost.activity;



import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;





import com.antilost.util.CameraPreview;
import com.example.antiLost.R;

import android.R.integer;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Face;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
@SuppressLint("NewApi")
public class CameraActivity extends Activity{
	public static Camera mCamera;
	private CameraPreview mPreview;
    int screenWidth,screenHeight;
    public static ToneGenerator tone;
    private ImageView imageView;
    public static final int MEDIA_TYPE_IMAGE = 1;//照片模式
    public static final int MEDIA_TYPE_VIDEO = 2;//录像模式
     public int rotation;
     static String timeStamp ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		 requestWindowFeature(Window.FEATURE_NO_TITLE);
		  getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		   WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.ctext);
		// imageView=(ImageView)findViewById(R.id.imageViewp);
		screenWidth = getWindowManager().getDefaultDisplay()
				.getWidth();
		screenHeight= getWindowManager().getDefaultDisplay()
				.getHeight();
		 int rotation = getWindowManager().getDefaultDisplay()
		            .getRotation();
		
		if (checkCameraHardware(this)) {
			mCamera=getCameraInstance(rotation);
			if (mCamera!=null) {
				  setParameters(mCamera);
				 
				  mPreview = new CameraPreview(this, mCamera);
				  RelativeLayout preview = (RelativeLayout) findViewById(R.id.camera_preview);
				  preview.setOnTouchListener(new View.OnTouchListener() {
					
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						// TODO Auto-generated method stub
						if (mCamera!=null) {
							mCamera.takePicture(shutter, null, mPictureCallback);//拍照
					//	mCamera.autoFocus(afcb);
						}
						
						return false;
					}
				});
			        preview.addView(mPreview);
			}
			
		}else {	
		}
		
	}
//	public void takephoto(View view){
//		mCamera.takePicture(shutter, null, mPictureCallback);
//	}
   public static Camera getCameraInstance(int rotation){
	   Camera c=null;
	   try {
		int cameraCount=0;
		CameraInfo cameraInfo=new CameraInfo();
		cameraCount=Camera.getNumberOfCameras();
		for (int i = 0; i < cameraCount; i++) {
			Camera.getCameraInfo(i, cameraInfo);
			if (cameraInfo.facing==Camera.CameraInfo.CAMERA_FACING_FRONT) {
				
				c=Camera.open(i);
				setCameraDisplayOrientation(cameraInfo,c,rotation);
			}
		}
		
	} catch (Exception e) {
		// TODO: handle exception
		e.printStackTrace();
	}
	return c;

}
  
   
   private boolean checkCameraHardware(Context context) {
       if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
         
           return true;
       } else {
          
           return false;
   
       }
   
   }
   public static  PictureCallback mPictureCallback=new PictureCallback() {
	
	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		// TODO Auto-generated method stub
		
		final Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
		 /* Matrix matrix = new Matrix();
	       Bitmap bm = Bitmap.createBitmap(1200, 1200*bitmap.getHeight()/bitmap.getWidth(), Config.ARGB_8888);//固定所拍相片大小
	       matrix.setScale(( float ) bm.getWidth() / ( float ) bitmap.getWidth(),( float ) bm.getHeight() / ( float ) bitmap.getHeight());// 注意参数一定是float哦
	       matrix.setRotate(90);
	       Canvas canvas = new Canvas(bm);//用bm创建一个画布 可以往bm中画入东西了
	       canvas.drawBitmap(bitmap,matrix,null);  
		//saveImage(CameraActivity.this, bitmap,mCamera);
		//imageView.setImageBitmap(bitmap);*/
	
		/*File file=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
       ,"a.jpg");*/
		File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
        if (pictureFile == null){
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(data);
            fos.close();
           // MediaStore.Images.Media.insertImage(getContentResolver(),bitmap,timeStamp, ""); 
//           sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"+ Environment.getExternalStorageDirectory())));  
            camera.stopPreview();
    	    camera.startPreview();

        } catch (FileNotFoundException e) {

          e.printStackTrace();

        } catch (IOException e) {

          e.printStackTrace();

        }

    }
};
private static Uri getOutputMediaFileUri(int type){

    return Uri.fromFile(getOutputMediaFile(type));

}
@SuppressLint("SimpleDateFormat")
private static File getOutputMediaFile(int type){
    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
              Environment.DIRECTORY_PICTURES), "MyCameraApp");
    if (! mediaStorageDir.exists()){
        if (! mediaStorageDir.mkdirs()){
            Log.d("MyCameraApp", "failed to create directory");
            return null;
        }
    }
     timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date(System.currentTimeMillis()));//创建图片名字

    File mediaFile;

    if (type == MEDIA_TYPE_IMAGE){

        mediaFile = new File("/sdcard/DCIM/Camera" +File.separator+

        "IMG_"+ timeStamp + ".jpg");

    } else if(type == MEDIA_TYPE_VIDEO) {

        mediaFile = new File("/sdcard/DCIM/camera" +

        "VID_"+ timeStamp + ".mp4");
    } else {
        return null;
    }
    return mediaFile;
}
/* public static void saveImage(Context context,Bitmap bitmap,Camera camera){
	 File appDir=new File(Environment.getExternalStorageDirectory(),"MyImage");
	 if (!appDir.exists()) {
		appDir.mkdir();
	}
	 String fileName=System.currentTimeMillis()+".jpg";
	 File file=new File(appDir,fileName);
	 try {
		FileOutputStream fos=new FileOutputStream(file);
		bitmap.compress(CompressFormat.JPEG,100,fos);
		fos.flush();
		fos.close();
	    camera.stopPreview();
	    camera.startPreview();
	    bitmap.recycle();
		
		
        
	   
	} catch (Exception e) {
		// TODO: handle exception
		e.printStackTrace();
	}
	 try {
		MediaStore.Images.Media.insertImage(context.getContentResolver(),file.getAbsolutePath(),fileName,null);
		
	} catch (Exception e) {
		// TODO: handle exception
	}
	   context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,Uri.parse("file://"+Environment.getExternalStorageDirectory())));
 }*/
     
    
  public static   Camera.ShutterCallback shutter=new ShutterCallback() {
		
		@Override
		public void onShutter() {
			// TODO Auto-generated method stub
			tone=new ToneGenerator(AudioManager.STREAM_SYSTEM,ToneGenerator.MAX_VOLUME);
		    tone.startTone(ToneGenerator.TONE_PROP_BEEP);
		}
	};

    @SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public void setParameters(Camera camera){
    	  Camera.Parameters parameters = camera.getParameters();   
          //camera.setDisplayOrientation(90);
    	 // camera.autoFocus(afcb);
    	 // camera.setDisplayOrientation(90);  
    	  //parameters.setPreviewSize(screenWidth, screenHeight);
    	 // parameters.setFocusMode("auto");
    	 // parameters.setPreviewFrameRate(4);
    	
           //设置照片格式
    	  //parameters.setPictureFormat(PixelFormat.JPEG);
    	
    	    // 设置JPG照片的质量
    	 // parameters.set("jpeg-quality",85);
    	    //设置照片的大小
    	//  parameters.setPictureSize(screenWidth, screenHeight);   
    	 
    	//  camera.setParameters(parameters);   
    	  
    	  
    }
    AutoFocusCallback afcb=new AutoFocusCallback() {
		
		@Override
		public void onAutoFocus(boolean success, Camera camera) {
			// TODO Auto-generated method stub
			if (success) {
				mCamera.takePicture(shutter, null, mPictureCallback);
			}
		}
	};

	public static void setCameraDisplayOrientation(
	        CameraInfo info,Camera camera,int rotation) {
		 
	    
	     int degrees = 0;
	     switch (rotation) {
	         case Surface.ROTATION_0: degrees = 0; break;
	         case Surface.ROTATION_90: degrees = 90; break;
	         case Surface.ROTATION_180: degrees = 180; break;
	         case Surface.ROTATION_270: degrees = 270; break;
	     }

	     int result;
	     if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
	         result = (info.orientation + degrees) % 360;
	         result = (360 - result) % 360;  // compensate the mirror
	     } else {  // back-facing
	         result = (info.orientation - degrees + 360) % 360;
	     }
	     camera.setDisplayOrientation(result);
	 }
	
}