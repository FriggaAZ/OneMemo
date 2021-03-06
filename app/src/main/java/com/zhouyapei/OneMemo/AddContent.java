package com.zhouyapei.OneMemo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.VideoView;

public class AddContent extends Activity implements OnClickListener {

	private String val;
	private Button savebtn, deletebtn;
	private EditText ettext;
	private ImageView c_img;
	private VideoView v_video;
	private NotesDB notesDB;
	private SQLiteDatabase dbWriter;
	private File phoneFile, videoFile;
    private Uri imageUri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addcontent);
		val = getIntent().getStringExtra("flag");
		savebtn = (Button) findViewById(R.id.save);
		deletebtn = (Button) findViewById(R.id.delete);
		ettext = (EditText) findViewById(R.id.ettext);
		c_img = (ImageView) findViewById(R.id.c_img);
		v_video = (VideoView) findViewById(R.id.c_video);
		savebtn.setOnClickListener(this);
		deletebtn.setOnClickListener(this);
		notesDB = new NotesDB(this);
		dbWriter = notesDB.getWritableDatabase();
		initView();
	}

	public void initView() {
		if (val.equals("1")) { // 文字
			c_img.setVisibility(View.GONE);
			v_video.setVisibility(View.GONE);
		}
		if (val.equals("2")) {
			c_img.setVisibility(View.VISIBLE);
			v_video.setVisibility(View.GONE);
			Intent iimg = new Intent("android.media.action.IMAGE_CAPTURE");

			phoneFile = new File(getExternalCacheDir() ,"output_file1.jpg");
			try {
			    if (phoneFile.exists()){
			        phoneFile.delete();
                }
                phoneFile.createNewFile();
            }catch (IOException e){
			    e.printStackTrace();
            }
            if (Build.VERSION.SDK_INT >= 24){
			    imageUri = FileProvider.getUriForFile(AddContent.this,"com.jikexueyuan",phoneFile);
            }else {
			    imageUri = Uri.fromFile(phoneFile);
            }


			iimg.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
			startActivityForResult(iimg, 1);
		}
		if (val.equals("3")) {
			c_img.setVisibility(View.GONE);
			v_video.setVisibility(View.VISIBLE);
			Intent video = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
			videoFile = new File(Environment.getExternalStorageDirectory()
					.getAbsoluteFile() + "/" + getTime() + ".mp4");
			video.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(videoFile));
			startActivityForResult(video, 2);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.save:
			addDB();
			finish();
			break;

		case R.id.delete:
			finish();
			break;
		}
	}

	public void addDB() {
		ContentValues cv = new ContentValues();
		cv.put(NotesDB.CONTENT, ettext.getText().toString());
		cv.put(NotesDB.TIME, getTime());
		cv.put(NotesDB.PATH, phoneFile + "");
		cv.put(NotesDB.VIDEO, videoFile + "");
		dbWriter.insert(NotesDB.TABLE_NAME, null, cv);
	}

	private String getTime() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		Date curDate = new Date();
		String str = format.format(curDate);
		return str;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1) {
		    try{
			Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
			c_img.setImageBitmap(bitmap);
            }catch (FileNotFoundException e){
		        e.printStackTrace();
            }
		}
		if (requestCode == 2) {
			v_video.setVideoURI(Uri.fromFile(videoFile));
			v_video.start();
		}
	}
}
