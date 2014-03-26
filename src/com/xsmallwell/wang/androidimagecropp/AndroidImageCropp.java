package com.xsmallwell.wang.androidimagecropp;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * 2014-03-26 23:32
 * @author wang_xiaohao wangxiaohao13@gmail.com
 * 
 */

public class AndroidImageCropp extends Activity implements OnClickListener {

	private Bitmap bitmap = null;
	private Button btSelectImage;
	private Button btCroping;
	private CutImageView croppingImage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_android_image_cropp);

		bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.find_07);

		btSelectImage = (Button) findViewById(R.id.bt_select_image);
		btSelectImage.setOnClickListener(this);

		btCroping = (Button) findViewById(R.id.bt_cropping);
		btCroping.setOnClickListener(this);

		croppingImage = (CutImageView) findViewById(R.id.cropping_image);
		croppingImage.init(bitmap);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onClick(View view) {

		int key = view.getId();
		switch (key) {
		case R.id.bt_cropping:

			Log.d("wyy", " --- --- 01  ----");
			if (null != croppingImage) {
				Log.d("wyy", " --- --- 012  ----");
				Bitmap selectBitmap = croppingImage.getSelectBitmap();
				Log.d("wyy", " --- --- 013  ----");
				if (null != selectBitmap) {
					Log.d("wyy", " --- --- 014  ----");
					croppingImage.init(selectBitmap);
					croppingImage.postInvalidate();
				}
			}
			break;
		case R.id.bt_select_image:

			break;

		default:
			break;
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (null != bitmap) {
			bitmap.recycle();
			bitmap = null;
		}
		if (null != croppingImage) {
			croppingImage.clear();
		}
	}

}
