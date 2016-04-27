package org.toilelibre.libe.athg2sms.settings;

import org.toilelibre.libe.athg2sms.bp.ConvertThread;
import org.toilelibre.libe.athg2sms.bp.ConvertV2;
import org.toilelibre.libe.athg2sms.bp.ConvertV3;
import org.toilelibre.libe.athg2sms.bp.ConvertV4;

public class SettingsFactory {


	private static SettingsV1	settingsV1	= new SettingsV1 ();
	private static SettingsV2	settingsV2	= new Settings () {
		                                       public ConvertThread getConvertThread () {
			                                       return new ConvertV2 ();
		                                       }
	                                       };
    private static SettingsV3   settingsV3  = new Settings () {
                                               public ConvertThread getConvertThread () {
                                                   return new ConvertV3 ();
                                               }
                                           };
    private static SettingsV4   settingsV4  = new Settings () {
                                               public ConvertThread getConvertThread () {
                                                   return new ConvertV4 ();
                                               }
                                           };

	private static Settings	  common	   = new Settings () {
		                                       public ConvertThread getConvertThread () {
			                                       throw new UnsupportedOperationException ();
		                                       }
	                                       };

	public static SettingsV1 asV1 () {
		return SettingsFactory.settingsV1;
	}

	public static SettingsV2 asV2 () {
		return SettingsFactory.settingsV2;
	}

	public static SettingsV3 asV3 () {
		return SettingsFactory.settingsV3;
	}
	
    public static SettingsV4 asV4 () {
        return SettingsFactory.settingsV4;
    }

	public static SettingsCommon common () {
		return SettingsFactory.common;
	}
}
