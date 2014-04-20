package com.halloon.android.ui.activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.halloon.android.image.ImageLoader;
import com.halloon.android.ui.fragment.FileBrowserFragment;
import com.halloon.android.ui.fragment.PublishFragment.PublishFragmentCallback;
import com.halloon.android.ui.fragment.TabMainPageFragment;
import com.halloon.android.util.Constants;

public class MainPageActivity extends BaseMultiFragmentActivity implements PublishFragmentCallback {
	
	private String imagePath;
	private CursorLoader cursorLoader;
	private Uri uri;
	private String[] proj;

	@Override
	protected Fragment onCreatePane() {
		
		System.out.println("test exit:onCreatePane");
		
		TabMainPageFragment mFragment = new TabMainPageFragment();
		mFragment.setTweetState(TabMainPageFragment.MAIN_TIMELINE_TWEET);

		return mFragment;
	}

	@Override
	public void setupFileBrowserFragment() {
		setupFragment(new FileBrowserFragment(), null);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK){
			switch(requestCode){
			case 1:
				Bundle bundle = data.getExtras();
				final Bitmap photo = (Bitmap) bundle.get("data");
				if (photo != null) {
					Intent intent = new Intent();
					intent.setAction(Constants.GET_IMAGE_PATH);
					imagePath = ImageLoader.imageSave(photo);
				}
				break;
			case 2:
				try{
					uri = data.getData();
					String[] tmp_proj = {MediaStore.Images.Media.DATA};
					proj = tmp_proj;
					getSupportLoaderManager().initLoader(0, null, new LoaderCallbacks<Cursor>(){

						@Override
						public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
							cursorLoader = new CursorLoader(MainPageActivity.this, MainPageActivity.this.uri, MainPageActivity.this.proj, null, null, null);
							return cursorLoader;
						}

						@Override
						public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
							int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
							cursor.moveToFirst();
							MainPageActivity.this.imagePath = cursor.getString(column_index);
						}

						@Override
						public void onLoaderReset(Loader<Cursor> loader) {
							MainPageActivity.this.imagePath = null;
						}
						
					});
				}catch(Exception e){
					e.printStackTrace();
				}
				break;
		}
		}
	}
	
	@Override
	public String getCapturedImagePath(){
		return imagePath;
	}

	
	
	@Override
	protected void init() {
		// TODO Auto-generated method stub
		
	}

}
