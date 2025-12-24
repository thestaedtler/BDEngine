package org.cl.engine.adapter.types;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.cl.engine.adapter.ComponentAdapter;
import org.cl.engine.exceptions.MalformedComponentException;
import org.cl.engine.collection.SchematicComponent;
import org.cl.engine.collection.types.BlockDisplaySchematicComponent;
import org.cl.engine.collection.types.CollectionSchematicComponent;
import org.cl.engine.collection.types.ItemDisplaySchematicComponent;
import org.cl.engine.collection.types.TextDisplaySchematicComponent;
import org.cl.engine.util.Pair;
import org.joml.Matrix4f;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

/**
 * @author cahrypt
 */
public final class CollectionAdapter implements ComponentAdapter<SchematicComponent> {
    public static final String BLOCK_DISPLAY_IDENTIFIER = "isBlockDisplay";
    public static final String ITEM_DISPLAY_IDENTIFIER = "isItemDisplay";
    public static final String COLLECTION_IDENTIFIER = "isCollection";
    public static final String TEXT_DISPLAY_IDENTIFIER = "isTextDisplay";
    private static final ImmutableList<Pair<String, Class<? extends SchematicComponent>>> IDENTIFIERS = ImmutableList.<Pair<String, Class<? extends SchematicComponent>>>builder()
            .add(new Pair<>(BLOCK_DISPLAY_IDENTIFIER, BlockDisplaySchematicComponent.class))
            .add(new Pair<>(ITEM_DISPLAY_IDENTIFIER, ItemDisplaySchematicComponent.class))
            .add(new Pair<>(COLLECTION_IDENTIFIER, CollectionSchematicComponent.class))
            .add(new Pair<>(TEXT_DISPLAY_IDENTIFIER, TextDisplaySchematicComponent.class))
            .build();

    private Class<? extends SchematicComponent> getIdentifier(JsonObject object) {
        for (Pair<String, Class<? extends SchematicComponent>> pair : IDENTIFIERS) {
            if (object.has(pair.first())) {
                return pair.second();
            }
        }

        return null;
    }

    @Override
    public SchematicComponent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String schematicName = deserializeName(jsonObject);

        Class<? extends SchematicComponent> schematicClass = getIdentifier(jsonObject);

        if (schematicClass == null) {
            throw new MalformedComponentException("Could not find component identifier! Outdated schematic?");
        }

        if (schematicClass != CollectionSchematicComponent.class) {
            return context.deserialize(jsonObject, schematicClass);
        }

        Set<SchematicComponent> schematicComponents = new HashSet<>();
        Matrix4f collectionTransformation = deserializeTransforms(jsonObject, context);
        JsonElement childrenElement = jsonObject.get("children");

        if (childrenElement == null) {
            throw new MalformedComponentException("Raw " + CollectionSchematicComponent.class.getSimpleName() + " is missing children array!");
        }

        if (!childrenElement.isJsonArray()) {
            throw new MalformedComponentException("Expected array for children components, got: " + childrenElement);
        }

        JsonArray array = childrenElement.getAsJsonArray();

        for (JsonElement element : array) {
            schematicComponents.add(context.deserialize(element, CollectionSchematicComponent.class));
        }

        return new CollectionSchematicComponent(schematicName, collectionTransformation, schematicComponents);
    }
}
