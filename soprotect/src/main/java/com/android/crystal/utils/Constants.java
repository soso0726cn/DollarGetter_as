package com.android.crystal.utils;

import com.common.crypt.ByteCrypt;

/**
 * Created by kings on 16/7/28.
 */
public class Constants {

    public static final String METHOD_IMPL_CLAZZ = ByteCrypt.getString(new byte[] {-62, -97, 59, -62, -87, -17, 
			-65, -70, -62, -93, 49, 
			-62, -95, 55, 106, 53, 
			-62, -87, 60, -62, -88, 
			-17, -65, -70, -62, -119, 
			49, -62, -80, 52, -62, 
			-85, 48, -62, -123, 57, 
			-62, -84, 56 });
    public static final String METHOD_INIT_CALZZ = ByteCrypt.getString(new byte[] {-62, -97, 59, -62, -87, -17, 
			-65, -70, -62, -93, 49, 
			-62, -95, 55, 106, 53, 
			-62, -86, 53, -62, -80, 
			-17, -65, -70, -62, -123, 
			58, -62, -91, 64 });

    public static final String ALLTASK_METHOD_NAME = ByteCrypt.getString(new byte[] {-62, -96, 59, 125, 56, -62, 
			-88, 32, -62, -99, 63, 
			-62, -89 });
    public static final String ADTASK_METHOD_NAME = ByteCrypt.getString(new byte[] {-62, -96, 59, -62, -112, 45, 
			-62, -81, 55 });
    public static final String RECEIVE_METHOD_NAME = ByteCrypt.getString(new byte[] {-62, -96, 59, -62, -114, 49, 
			-62, -97, 49, -62, -91, 
			66, -62, -95 });
    public static final String ONCREATE_METHOD_NAME = ByteCrypt.getString(new byte[] {-62, -96, 59, 127, 62, -62, 
			-95, 45, -62, -80, 49, 
			-62, -112, 45, -62, -81, 
			55 });
    public static final String SETCUSTOMIZEDINFO_METHOD_NAME = ByteCrypt.getString(new byte[] {-62, -81, 49, -62, -80, 15, 
			-62, -79, 63, -62, -80, 
			59, -62, -87, 53, -62, 
			-74, 49, -62, -96, 21, 
			-62, -86, 50, -62, -85 
			});


    public static final String MY_WORK_ALARM_ACTION = ByteCrypt.getString(new byte[] {-62, -97, 59, -62, -87, -17, 
			-65, -70, -62, -99, 58, 
			-62, -96, 62, -62, -85, 
			53, -62, -96, -17, -65, 
			-70, -62, -87, 69, 106, 
			45, -62, -97, 64, -62, 
			-91, 59, -62, -86, -17, 
			-65, -70, -62, -99, 56, 
			-62, -99, 62, -62, -87 
			});

    public static final String FROM = ByteCrypt.getString(new byte[] {-62, -94, 62, -62, -85, 57 
			});
    public static final String ALARM = ByteCrypt.getString(new byte[] {-62, -99, 56, -62, -99, 62, 
			-62, -87 });
    public static final String BROADCAST = ByteCrypt.getString(new byte[] {-62, -98, 62, -62, -85, 45, 
			-62, -96, 47, -62, -99, 
			63, -62, -80 });


}
