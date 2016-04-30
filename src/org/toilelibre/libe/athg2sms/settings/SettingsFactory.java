package org.toilelibre.libe.athg2sms.settings;

import org.toilelibre.libe.athg2sms.bp.ConvertThread;
import org.toilelibre.libe.athg2sms.bp.ConvertV4;

public class SettingsFactory {

    private static SettingsV4 settingsV4 = new Settings () {
        public ConvertThread getConvertThread () {
            return new ConvertV4 ();
        }
    };

    private static Settings common = new Settings () {
        public ConvertThread getConvertThread () {
            throw new UnsupportedOperationException ();
        }
    };

    public static SettingsV4 asV4 () {
        return SettingsFactory.settingsV4;
    }

    public static SettingsCommon common () {
        return SettingsFactory.common;
    }
}
