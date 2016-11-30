package org.toilelibre.libe.athg2sms.business.export;

import android.content.Context;
import android.database.Cursor;

import org.toilelibre.libe.athg2sms.business.sms.Sms;
import org.toilelibre.libe.athg2sms.business.sms.SmsFinder;

import java.util.HashMap;
import java.util.Map;

public class Exporter {

    public String export(Context context, String patternName) {
        StringBuilder result = new StringBuilder();
        MessageMapper messageMapper = new MessageMapper();
        Cursor cursor = new SmsFinder().pickThemAll(context.getContentResolver());
        cursor.moveToFirst();
        for (int msgIndex = 0 ; msgIndex < cursor.getCount() ; msgIndex++){
            Map<String, Object> values = new HashMap<>();
            for (String columnName : cursor.getColumnNames()) {
                values.put(columnName, cursor.getString(cursor.getColumnIndex(columnName)));
            }
            Sms sms = new Sms(values);
            result.append(messageMapper.convert(sms, patternName));
            cursor.moveToNext();
        }
        return result.toString();
    }
}
