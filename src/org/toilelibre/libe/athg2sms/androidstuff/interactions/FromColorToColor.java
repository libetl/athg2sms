package org.toilelibre.libe.athg2sms.androidstuff.interactions;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.View;

class FromColorToColor {


    static void animate(final Context context, final View rootView, final int button, final int start, final int end) {
        final int nbSteps = 100;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), 0, nbSteps);
            colorAnimation.setDuration(3000);
            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        final int value = (Integer)animator.getAnimatedValue();

                        final int colorAccent = ContextCompat.getColor(context, start);
                        final int redAccent = ContextCompat.getColor(context, end);
                        final int decreasing = nbSteps - value;
                        final int newColor = Color.argb(255, Color.red(colorAccent) * decreasing / nbSteps + Color.red(redAccent) * value / nbSteps,
                                Color.green(colorAccent) * decreasing / nbSteps + Color.green(redAccent) * value / nbSteps,
                                Color.blue(colorAccent) * decreasing / nbSteps + Color.blue(redAccent) * value / nbSteps);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            rootView.findViewById (button).setBackgroundTintList(ColorStateList.valueOf(newColor));
                        }else {
                            rootView.findViewById (button).setBackgroundColor(newColor);
                        }
                    }
                }

            });
            colorAnimation.start();
        }
    }
}
