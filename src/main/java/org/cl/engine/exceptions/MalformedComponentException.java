package org.cl.engine.exceptions;

import com.google.gson.JsonParseException;

/**
 * Thrown when there is an error deserializing a {@link org.cl.engine.collection.SchematicComponent} induced
 * by bad json.
 */
public class MalformedComponentException extends JsonParseException {
    public MalformedComponentException(String message) {
        super(message);
    }

    public MalformedComponentException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
