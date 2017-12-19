package org.tootto.adapter;

import android.text.Spanned;
import android.text.SpannedString;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import org.tootto.util.HtmlUtils;

import java.lang.reflect.Type;

/**
 * Created by fred on 2017/12/19.
 */

public class SpannedTypeAdapter implements JsonDeserializer<Spanned> {
    @Override
    public Spanned deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        String string = json.getAsString();
        if (string != null) {
            return HtmlUtils.fromHtml(string);
        } else {
            return new SpannedString("");
        }
    }
}
