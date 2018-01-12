package org.tootto.util;

import android.content.Context;
import android.content.res.Resources;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by fred on 2018/1/8.
 */

public class Utils {
    private static Pattern PATTERN_RESOURCE_IDENTIFIER = Pattern.compile("@([\\w_]+)/([\\w_]+)");
    private static Pattern PATTERN_XML_RESOURCE_IDENTIFIER = Pattern.compile("res/xml/([\\w_]+)\\.xml");


    public static int getResId(Context context, String string){

        if (context == null || string == null)
            return 0;
        Matcher matcher = PATTERN_RESOURCE_IDENTIFIER.matcher(string);
        Resources res = context.getResources();
        if (matcher.matches())
            //@型的资源
            return res.getIdentifier(matcher.group(2), matcher.group(1), context.getPackageName());
        matcher = PATTERN_XML_RESOURCE_IDENTIFIER.matcher(string);
        if (matcher.matches())
            //路径的资源:context.getPackageName():xml/matcher.group(1)
            return res.getIdentifier(matcher.group(1), "xml", context.getPackageName());
        return 0;
    }
}
