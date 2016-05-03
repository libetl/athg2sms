package org.toilelibre.libe.athg2sms.util;

import android.annotation.TargetApi;
import android.os.PersistableBundle;
import android.service.carrier.CarrierIdentifier;
import android.service.carrier.CarrierService;
import android.telephony.CarrierConfigManager;
import android.util.Log;

@TargetApi (23)
public class Athg2SmsCarrierService extends CarrierService {

    private static final String TAG = "SampleCarrierConfigService";

    public Athg2SmsCarrierService() {
        Log.d(TAG, "Athg2SmsCarrierService created");
    }

    @Override
    public PersistableBundle onLoadConfig(CarrierIdentifier id) {
        Log.d(TAG, "Config being fetched");
        PersistableBundle config = new PersistableBundle();
        config.putBoolean(
            CarrierConfigManager.KEY_MMS_SMS_DELIVERY_REPORT_ENABLED_BOOL, true);
        return config;
    }
}