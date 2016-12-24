package org.toilelibre.libe.athg2sms.androidstuff.materialdesign;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Bundle;

import org.toilelibre.libe.athg2sms.R;
import org.toilelibre.libe.athg2sms.androidstuff.interactions.ConvertUI;

public class Convert extends Activity {

    @Override
    protected void onCreate (final Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        this.setContentView (R.layout.proceed);

        new ConvertUI().retryConvertOperation (this);
    }

    @TargetApi (23)
    @Override
    public void onRequestPermissionsResult (int requestCode, String [] permissions, int [] grantResults) {
        super.onRequestPermissionsResult (requestCode, permissions, grantResults);

        new ConvertUI().retryConvertOperation(this);
    }
}
