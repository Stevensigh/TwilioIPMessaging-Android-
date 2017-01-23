package io.palliassist.palliassistmobile;

/**
 * Created by Steven on 12/5/2016.
 */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class PainLocator extends BaseActivity {

    private GestureDetector gestureDetector;
    private boolean tapped = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gestureDetector = new GestureDetector(this, new GestureListener());
        setContentView(R.layout.activity_pain_locator);

        // display body image
        ImageView bv = (ImageView) findViewById(R.id.bodyView);
        Bitmap body_bit = BitmapFactory.decodeResource(getResources(), R.drawable.body_image);
        bv.setImageBitmap(body_bit);

        // touch listener
        bv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View bv, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }

        });
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            int x = (int)e.getX();
            int y = (int)e.getY();
            if (e.getAction() == MotionEvent.ACTION_DOWN) {
                Bitmap pain_icon = BitmapFactory.decodeResource(getResources(), R.drawable.red_circle);
                ImageView pain_view = (ImageView) findViewById(R.id.pain_view);
                pain_view.setX(x-60);
                pain_view.setY(y+160);
                pain_view.requestLayout();
                pain_view.getLayoutParams().height = pain_icon.getHeight();
                pain_view.getLayoutParams().width = pain_icon.getWidth();
                pain_view.setImageBitmap(pain_icon);
                Log.d("TA",String.valueOf(pain_view.getHeight()));
                Log.d("TA",String.valueOf(pain_icon.getHeight()));
                Log.d("TA",String.valueOf(pain_view.getWidth()));
                Log.d("TA",String.valueOf(pain_icon.getWidth()));
                pain_view.bringToFront();
            }
        }

    /*
        // event when double tap occurs
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            float x = e.getX();
            float y = e.getY();

            if (!tapped) {
                ImageView pain_view = (ImageView) findViewById(R.id.pain_view);
                pain_view.setX(x);
                pain_view.setY(y);
                Bitmap pain_icon = BitmapFactory.decodeResource(getResources(), R.drawable.star_48);
                pain_view.setImageBitmap(pain_icon);
                pain_view.bringToFront();
            }

            tapped = true;

            return true;
        }
        */
    }
}