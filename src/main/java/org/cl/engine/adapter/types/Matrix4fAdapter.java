package org.cl.engine.adapter.types;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import org.joml.Matrix4f;

import java.lang.reflect.Type;

/**
 * @author cahrypt
 */
public final class Matrix4fAdapter implements JsonDeserializer<Matrix4f> {

    private float getFloat(JsonArray array, int index) {
        return array.get(index).getAsFloat();
    }

    @Override
    public Matrix4f deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonArray transformationArray = json.getAsJsonArray();

        // column-major storage for some reason, I dunno
        return new Matrix4f(
                getFloat(transformationArray, 0), getFloat(transformationArray, 4), getFloat(transformationArray, 8), getFloat(transformationArray, 12),
                getFloat(transformationArray, 1), getFloat(transformationArray, 5), getFloat(transformationArray, 9), getFloat(transformationArray, 13),
                getFloat(transformationArray, 2), getFloat(transformationArray, 6), getFloat(transformationArray, 10), getFloat(transformationArray, 14),
                getFloat(transformationArray, 3), getFloat(transformationArray, 7), getFloat(transformationArray, 11), getFloat(transformationArray, 15)
        );
    }
}
