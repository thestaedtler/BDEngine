package org.cl.engine.adapter.types;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.bukkit.Bukkit;
import org.bukkit.block.data.BlockData;
import org.cl.engine.adapter.ComponentAdapter;
import org.cl.engine.collection.types.BlockDisplaySchematicComponent;
import org.joml.Matrix4f;

import java.lang.reflect.Type;

/**
 * @author cahrypt
 */
public final class BlockComponentAdapter implements ComponentAdapter<BlockDisplaySchematicComponent> {

    @Override
    public BlockDisplaySchematicComponent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();

        String name = deserializeName(object);
        Matrix4f transformation = deserializeTransforms(object, context);
        BlockData blockData = Bukkit.getServer().createBlockData(name);

        return new BlockDisplaySchematicComponent(name, transformation, blockData);
    }
}
