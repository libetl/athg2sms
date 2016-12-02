package org.toilelibre.libe.athg2sms.business.export;


import org.toilelibre.libe.athg2sms.androidstuff.ContextHolder;
import org.toilelibre.libe.athg2sms.androidstuff.CursorHolder;
import org.toilelibre.libe.athg2sms.androidstuff.SmsFinder;
import org.toilelibre.libe.athg2sms.business.sms.Sms;

import java.util.HashMap;
import java.util.Map;

public class Exporter {

    public String export(ContextHolder<?> context, String patternName) {
        StringBuilder result = new StringBuilder();
        MessageMapper messageMapper = new MessageMapper();
        CursorHolder<?> cursor = new SmsFinder().pickThemAll(context);
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
