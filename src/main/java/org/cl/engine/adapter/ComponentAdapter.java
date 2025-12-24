package org.cl.engine.adapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.cl.engine.exceptions.MalformedComponentException;
import org.cl.engine.collection.SchematicComponent;
import org.joml.Matrix4f;

/**
 * @author cahrypt
 */
public interface ComponentAdapter<T extends SchematicComponent> extends JsonDeserializer<T> {
    default String deserializeName(JsonObject jsonObject) {
        JsonElement nameElement = jsonObject.get("name");

        if (nameElement == null) {
            throw new MalformedComponentException("Component name missing");
        }

        return nameElement.getAsString();
    }

    default Matrix4f deserializeTransforms(JsonObject jsonObject, JsonDeserializationContext context) {
        JsonElement transformsElement = jsonObject.get("transforms");

        if (transformsElement == null) {
            throw new MalformedComponentException("Transforms array missing");
        }

        if (!transformsElement.isJsonArray()) {
            throw new MalformedComponentException("Expected array for 'transforms' component field, got: " + transformsElement);
        }

        try {
            return context.deserialize(transformsElement.getAsJsonArray(), Matrix4f.class);
        } catch (IndexOutOfBoundsException exception) {
            throw new MalformedComponentException("Transforms array is too small (requires 16, a 4*4 matrix)");
        }
    }
}
