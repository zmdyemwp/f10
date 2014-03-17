package com.goldtek.gridviewtest;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class PhotoAdapter extends BaseAdapter {

	private Context c;

	static final String tag = "PhotoAdapter";

	static private int imgCount = 0;
	static private ArrayList<File> photokeys = new ArrayList<File>();
	static private ArrayMap<String, Bitmap> photomap = new ArrayMap<String, Bitmap>();
	
	public PhotoAdapter(Context context) {
		c = context;
		//RefreshFiles();
		new ImgLoader().execute();
	}

	public void ShowAll() {
		for(File f:photokeys) {
			Log.d(tag, f.toString());
		}
		Log.d(tag, "Total: "+photokeys.size());
	}	
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		//return RefreshFiles();
		return imgCount;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		if(0 <= arg0 && arg0 < imgCount) {
			return photokeys.get(arg0);
		} else {
			return null;
		}
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	
	private final int width = 220;
	private final int height = 220;
	private final int pad = 20;
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ImageView i;
		if(null == convertView) {
			i = new ImageView(c);
		} else {
			i = (ImageView)convertView;
		}

		//Creation of thumbnail of image
		try {
            i.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            i.setPadding(pad, pad, pad, pad);
            i.setLayoutParams(new GridView.LayoutParams(width, height));
			i.setImageBitmap(photomap.get(photokeys.get(position).toString()));
			Log.d(tag, String.format("getView(%d)", position));

		} catch(Throwable e) {
			//Log.d(tag, e.getLocalizedMessage());
		}

		return i;
	}

	private class ImgLoader extends AsyncTask<Void,Void,Void> {

		Bitmap CreateThumbnail(File f) {
			try {
				ExifInterface exif = new ExifInterface(f.toString());
				if(exif.hasThumbnail()) {
					Log.d(tag, "Thumbnail INSIDE the jpeg");
					byte[] thumbnail = exif.getThumbnail();
					if(null == thumbnail) {
						Log.d(tag, "CreateThumbnail()::ExifInterface.getThumbnail() return NULL");
						return null;
					}
					return Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray(thumbnail, 0, thumbnail.length)
													, width, height, true);
				}
			} catch(Throwable e) {
				Log.d(tag, e.getLocalizedMessage());
			}
			Log.d(tag, "CreateThumbnail()::"+f.toString());
			Bitmap bitmap = BitmapFactory.decodeFile(f.toString());
			if(null == bitmap) {
				Log.d(tag, "CreateThumbnail()::BitmapFactory() return NULL");
				return null;
			}
			return Bitmap.createScaledBitmap(bitmap, width, height, true);
		}

		private final String[] Image_Formats =  new String[] {"jpg", "png", "gif","jpeg"};
		private class DcimImageFileFilter implements FilenameFilter {

			@Override
			public boolean accept(File dir, String filename) {
				// TODO Auto-generated method stub
				if( !dir.toString().contains("/.")) {
					if(dir.isDirectory()) {
						return true; 
					}
					for(String ext:Image_Formats) {
						if(filename.contains(ext)) {
							Log.d(tag, String.format("\"%s\" do contain \"%s\"", filename, ext));
							return true;
						} else {
							Log.d(tag, String.format("\"%s\" do NOT contain \"%s\"", filename, ext));
						}
					}
				}
				return false;
			}
			
		}

		private static final boolean bDefPath = true;
		private void RetrieveImageFiles() {
			RetrieveImageFiles(null);
		}
		private void RetrieveImageFiles(String path) {
			try {
				File dir;
				if(null == path) {
					photokeys.clear();
					if(bDefPath) {
						dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
						Log.d(tag, "RetrieveImageFiles"+dir.toString());
					} else {
						//	Test directory
						dir = new File("/sdcard/CozyCamera");
					}
				} else {
					dir = new File(path);
				}
				File[] photolist = dir.listFiles(new DcimImageFileFilter());
				if(null != photolist) {
					for(File f:photolist) {
						if(f.isDirectory()) {
							Log.d(tag, "RetrieveImageFiles()::Subdirectory::"+f.toString());
							RetrieveImageFiles(f.toString());
						} else {
							Log.d(tag, "RetrieveImageFiles()::Image File::"+f.toString());
							photokeys.add(0,f);
						}
					}
				} else {
					Log.d(tag, "RetrieveImageFiles()::photolist is NULL");
				}
			} catch (Throwable e) {
				Log.d(tag, e.getLocalizedMessage());
			}
		}

		void UpdateImageMap() {
			if(0 == photomap.size()) {
				for(File f:photokeys) {
					Bitmap t = CreateThumbnail(f);
					if(null != t) {
						photomap.put(f.toString(), t);
						imgCount++;
						Log.d(tag, "UpdateImageMap");
						this.publishProgress();
					}
				}
			} else {
				//	TODO: check and add new image or remove old image
				boolean bchanged = false;
				for(File f:photokeys) {
					if(null == photomap.get(f.toString())) {
						Log.d(tag, "Add NEW: "+f.toString());
						photomap.put(f.toString(), CreateThumbnail(f));
						imgCount++;
						bchanged = true;
					}
				}
				Log.d(tag, String.format("%d/%d", photokeys.size(), photomap.entrySet().size()));
				for(String s:photomap.keySet()) {
					Log.d(tag, s);
					if(-1 == photokeys.indexOf(new File(s))) {
						Log.d(tag, "Remove OLD: "+s);
						//photomap.remove(obj.getKey());
						bchanged = true;
					}
				}
				if(bchanged) {
					//notifyDataSetChanged();				
				}
			}
		}

		private int RefreshFiles() {
			Log.d(tag, "RefreshFiles()");
			try {
				//	TODO: get file name list
				RetrieveImageFiles();
				//	TODO: create/modify image map
				UpdateImageMap();
			} catch(Throwable e) {
				Log.d(tag, e.getLocalizedMessage());
			}

			return photokeys.size();
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			RefreshFiles();
			return null;
		}
		
		@Override
		protected void onProgressUpdate(Void... args) {
			super.onProgressUpdate((Void)null);
			Log.d(tag, "Notify that the DATA SET is changed!");
			notifyDataSetChanged();
		}

	}
}
