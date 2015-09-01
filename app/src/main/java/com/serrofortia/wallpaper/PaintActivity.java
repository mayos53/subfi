/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.serrofortia.wallpaper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.serrofortia.wallpaper.controller.BaseTaskListener;
import com.serrofortia.wallpaper.controller.SetWallpaperTask;
import com.serrofortia.wallpaper.network.model.requests.SetWallpaperRequest;
import com.serrofortia.wallpaper.network.model.responses.BaseResponse;
import com.serrofortia.wallpaper.network.model.responses.SetWallpaperResponse;
import com.serrofortia.wallpaper.ui.components.AlertEditText;
import com.serrofortia.wallpaper.ui.components.AlertEditText.ButtonListener;
import com.serrofortia.wallpaper.ui.components.AlertLoading;
import com.serrofortia.wallpaper.ui.components.ColorPickerView;
import com.serrofortia.wallpaper.ui.components.ColorPickerView.OnColorChangedListener;
import com.serrofortia.wallpaper.util.Loger;
import com.serrofortia.wallpaper.util.Tools;

public class PaintActivity extends BaseActivity
        implements OnColorChangedListener {

    protected final String TAG = this.getClass().getSimpleName();

    public final static String EXTRA_PICTURE_URI = "EXTRA_PICTURE_URI";

    private WallpaperApp mApp;

    View view;


    private Paint mPaint;
    private int mColor = -1;
    //	    private MaskFilter  mEmboss;
//	    private MaskFilter  mBlur;
    private List<PathPaint> paths;
    private Path mPath;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mBitmapPaint;

    private Uri pictureUri;
    private String picturePath;

    private int groupId;

    private Drawable drawableEdit;
    private Drawable drawableUndo;

    private int mStatusBarHeight;
    private int actionBarHeight;


    private AlertLoading alertLoading;


    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApp = (WallpaperApp) getApplication();
        pictureUri = (Uri) getIntent().getExtras().get(EXTRA_PICTURE_URI);
        groupId = getIntent().getIntExtra(GroupActivity.EXTRA_GROUP_ID, 0);


        RelativeLayout relativeLayout = new RelativeLayout(this);
        relativeLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

//        String[] filePathColumn = { MediaStore.Images.Media.DATA };
//
//		Cursor cursor = getContentResolver().query(pictureUri,
//				filePathColumn, null, null, null);
//		cursor.moveToFirst();
//
//		int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        // picturePath = cursor.getString(columnIndex);
        //cursor.close();

        picturePath = Tools.uriToFilename(this, pictureUri);

        Loger.d(TAG, "picturePath : " + picturePath);


        mStatusBarHeight = getStatusBarHeight();
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }

        Bitmap bitmap = scaleImage(pictureUri, picturePath);
        view = new MyView(this, bitmap);
        // view.setBackground(new BitmapDrawable(getResources(),bmImg));


        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(mColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(15);

        relativeLayout.addView(view);

        ColorPickerView palette = new ColorPickerView(this);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        layoutParams.setMargins(0, (int) Tools.convertDpToPixel(30, this), (int) Tools.convertDpToPixel(15, this), 0);
        relativeLayout.addView(palette, layoutParams);


        ImageView imageView = (ImageView) palette.findViewById(R.id.bg_palette_edit);
        drawableEdit = imageView.getDrawable();

        imageView = (ImageView) palette.findViewById(R.id.bg_palette_undo);
        drawableUndo = imageView.getDrawable();
        palette.setColorChangeListener(this);

        setContentView(relativeLayout);


        alertLoading = new AlertLoading(getContentView(), this);


//        ColorPickerView pickerView = (ColorPickerView)palette.findViewById(R.id.paint);
//        pickerView.setColorChangeListener(this);

        palette.findViewById(R.id.palette_undo).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mPath != null) {
                    mPath = null;
                }
                if (paths.size() > 0) {
                    paths.remove(paths.size() - 1);
                    PathPaint previous = null;
                    if (paths.size() > 1) {
                        previous = paths.get(paths.size() - 2);
                    } else if (paths.size() > 0) {
                        previous = paths.get(paths.size() - 1);
                    }
                    if (previous != null) {
                        ColorFilter filter = new LightingColorFilter(previous.color, previous.color);
                        drawableUndo.setColorFilter(filter);
                    }

                }
                view.invalidate();

            }
        });


    }


    private Bitmap scaleImage(Uri photoUri, String picturePath) {

        InputStream is = null;

        try {


            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Config.RGB_565;
            options.inDither = true;

            //	is = getContentResolver().openInputStream(photoUri);

            mBitmap = BitmapFactory.decodeFile(picturePath, options);


            //int rotatedWidth, rotatedHeight;
            int orientation = getOrientation(picturePath);

//        if (orientation == 90 || orientation == 270) {
//            rotatedWidth = dbo.outHeight;
//            rotatedHeight = dbo.outWidth;
//        } else {
//            rotatedWidth = dbo.outWidth;
//            rotatedHeight = dbo.outHeight;
//        }

            Point dim = Tools.getScreenDimensions(this);
        
        /*
         * if the orientation is not 0 (or -1, which means we don't know), we
         * have to do a rotation.
         */
            if (orientation > 0) {


                mBitmap = Bitmap.createScaledBitmap(mBitmap, dim.y - actionBarHeight - mStatusBarHeight, dim.x, true);

                Matrix matrix = new Matrix();
                matrix.postRotate(orientation);

                mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);


            } else {

                mBitmap = Bitmap.createScaledBitmap(mBitmap, dim.x, dim.y - actionBarHeight - mStatusBarHeight, true);

            }
        } catch (Exception e) {
            Loger.e(TAG, "Error in processing bitmap", e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                    Loger.e(TAG, "", e);

                }
            }
        }

        return mBitmap;

    }

//    public int getOrientation(Context context, Uri photoUri) {
//        /* it's on the external media. */
//        Cursor cursor = context.getContentResolver().query(photoUri,
//                new String[]{MediaStore.Images.ImageColumns.ORIENTATION}, null, null, null);
//
//        if (cursor.getCount() != 1) {
//            return -1;
//        }
//
//        cursor.moveToFirst();
//        return cursor.getInt(0);
//    }

    public int getOrientation(String filepath) {// YOUR MEDIA PATH AS STRING
        int degree = 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filepath);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        if (exif != null) {
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
            if (orientation != -1) {
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }

            }
        }
        return degree;
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;

    }

    public void colorChanged(int color) {
        //mPaint.setColor(color);


        ColorFilter filter = new LightingColorFilter(color, color);
        drawableEdit.setColorFilter(filter);
        if (mColor == -1) {
            drawableUndo.setColorFilter(filter);
        }
        mColor = color;
    }

    @SuppressLint("NewApi")
    public class MyView extends View {

        private static final float MINP = 0.25f;
        private static final float MAXP = 0.75f;
        private int actionBarHeight;


        public MyView(Context c, Bitmap bitmap) {
            super(c);
            mBitmap = bitmap;
            mPath = new Path();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
            paths = new ArrayList<PathPaint>();

            // Calculate ActionBar height
            TypedValue tv = new TypedValue();
            if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
                actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
            }
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);

            mCanvas = new Canvas();

        }


        @Override
        protected void onDraw(Canvas canvas) {
            //canvas.drawColor(0xFFAAAAAA);

            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);

            for (int i = 0; i < paths.size(); i++) {
                mPaint.setColor(paths.get(i).color);
                canvas.drawPath(paths.get(i).path, mPaint);
            }
            if (mPath != null) {
                mPaint.setColor(mColor);
                canvas.drawPath(mPath, mPaint);
            }


        }

        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 4;

        private void touch_start(float x, float y) {
            mPath = new Path();
            mPath.moveTo(x, y);
            mX = x;
            mY = y;
        }

        private void touch_move(float x, float y) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
                mX = x;
                mY = y;
            }
        }

        private void touch_up() {
            mPath.lineTo(mX, mY);
            // commit the path to our offscreen

            PathPaint pp = new PathPaint();
            pp.color = mColor;
            pp.path = mPath;

            if (paths != null && paths.size() > 0) {
                PathPaint previous = paths.get(paths.size() - 1);
                ColorFilter filter = new LightingColorFilter(previous.color, previous.color);
                drawableUndo.setColorFilter(filter);
            }

            paths.add(pp);

            //mCanvas.drawPath(mPath, mPaint);
            // kill this so we don't double draw

            //mPath.reset();
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_move(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    touch_up();
                    invalidate();
                    break;
            }
            return true;
        }
    }


    private String saveBitmap(Bitmap bitmap, String originalPath) {

        File edited = null;
        try {
            String originalName = new File(originalPath).getName();
            File originalDirectory = new File(new File(originalPath).getParent());

            int indexExtension = originalName.lastIndexOf(".");

            edited = new File(originalDirectory, originalName.substring(0, indexExtension + 1) + "_edited" + originalName.substring(indexExtension));


            FileOutputStream fOut = new FileOutputStream(edited);

            bitmap.compress(Bitmap.CompressFormat.PNG, 85, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            Loger.e(TAG, "Error in saving bitmap", e);
        }
        if (edited != null) {
            return edited.getAbsolutePath();
        }

        return null;
    }

    class PathPaint {
        public Path path;
        public int color;
    }

    @Override
    public View getTitleLayout() {
        ViewGroup view = (ViewGroup) getLayoutInflater().inflate(R.layout.title_paint, null);
        return view;
    }

    @Override
    public boolean onActionBarItemSelected(View action_item) {
        switch (action_item.getId()) {
            case R.id.btnOkWallpaper:
            /*
			 AlertDialog.Builder alert = new AlertDialog.Builder(PaintActivity.this);

				alert.setTitle(R.string.add_wallpaper_title);
				alert.setMessage(R.string.add_wallpaper_message);

				// Set an EditText view to get user input
				final EditText input = new EditText(PaintActivity.this);
				alert.setView(input);

				alert.setNeutralButton("Ok",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								String title = input.getText().toString();
									if(Tools.isEmpty(title)){
										title="";
									}
									sendWallpaper(title);
									
								}
				});
			
				
				alert.show();
				
				
					*/
                AlertEditText.showMessage(getContentView(), this, R.string.add_wallpaper_message, new ButtonListener() {
                    public void onClick(String text) {
                        if (Tools.isEmpty(text)) {
                            text = "";
                        }
                        sendWallpaper(text);
                    }
                });

                return true;

            case R.id.group_back:
                finish();
                return true;
        }
        return super.onActionBarItemSelected(action_item);
    }


    @SuppressWarnings("unchecked")
    private void sendWallpaper(final String title) {

        new AsyncTask() {
            protected void onPreExecute() {
                super.onPreExecute();
                alertLoading.show();
            }

            ;

            @Override
            protected Object doInBackground(Object... params) {
                Canvas canvas = new Canvas(mBitmap);
                //canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);

                for (int i = 0; i < paths.size(); i++) {
                    mPaint.setColor(paths.get(i).color);
                    canvas.drawPath(paths.get(i).path, mPaint);
                }


                drawCaption(canvas, mApp.getGroupById(groupId).name);

                String editedPath = saveBitmap(mBitmap, picturePath);
                return editedPath;

            }


            @Override
            protected void onPostExecute(Object editedPath) {
                final SetWallpaperRequest request = new SetWallpaperRequest();
                request.path = (String) editedPath;
                request.groupId = groupId;
                request.user_id = mApp.getUserDetails().id;
                request.title = title;
                SetWallpaperTask task = new SetWallpaperTask(PaintActivity.this, new BaseTaskListener<SetWallpaperResponse>() {

                    @Override
                    public void onSuccess(SetWallpaperResponse response) {
                        //delete picture
                        Tools.deleteFile(picturePath);
                        mApp.updateGroup(response.group);
                        setResult(RESULT_OK);

                        finish();

                    }

                    @Override
                    public void onFailed(BaseResponse response) {
                        alertLoading.hide();
                    }
                });
                task.execute(request);

            }

            ;
        }.execute();

    }

    private void drawCaption(Canvas canvas, String text) {

        Paint paint = new Paint();
        paint.setTextSize(30);
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);

        int textBoxWidth = bounds.width();

        int offset = (int) (paint.getTextSize() / 2);

        int x = canvas.getWidth() - textBoxWidth - offset;
        int y = canvas.getHeight() - offset;
        Loger.d(TAG, "x : " + x + " y: " + y);

        int right = x + textBoxWidth;
        int bottom = (int) (y - paint.getTextSize());

        long red = 0;
        long green = 0;
        long blue = 0;

        int count = 0;

        for (int i = x; i < right; i += 2) {
            for (int j = bottom; j < y; j += 2) {
                int pixel = mBitmap.getPixel(i, j);
                red += Color.red(pixel);
                green += Color.green(pixel);
                blue += Color.blue(pixel);
                count++;

            }
        }


        red = (int) ((double) red / count);
        green = (int) ((double) green / count);
        blue = (int) ((double) blue / count);

        Loger.d(TAG, "red : " + red + " green: " + green + " blue: " + blue);

        int alpha = 255;
        // Loger.d(TAG, "red : "+red +" green: "+ green+" blue: "+blue);
        int inverseColor = Color.argb(alpha, alpha - (int) red, alpha - (int) green, alpha - (int) blue);


        paint.setColor(inverseColor);
        canvas.drawText(text, x, y, paint);
    }
}


 