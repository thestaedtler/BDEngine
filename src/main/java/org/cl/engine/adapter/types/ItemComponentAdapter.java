package org.cl.engine.adapter.types;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerTextures;
import org.cl.engine.adapter.ComponentAdapter;
import org.cl.engine.exceptions.MalformedComponentException;
import org.cl.engine.collection.types.ItemDisplaySchematicComponent;
import org.joml.Matrix4f;

import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Base64;
import java.util.UUID;

/**
 * @author cahrypt
 */
public final class ItemComponentAdapter implements ComponentAdapter<ItemDisplaySchematicComponent> {
    private static final UUID ARBITRARY = UUID.fromString("f6393cb7-aad3-4e52-bcec-962545f31805");

    private void trySkullData(ItemStack itemStack, JsonObject object) {
        JsonElement tagHeadElement = object.get("tagHead");

        if (tagHeadElement == null) {
            return;
        }

        if (!tagHeadElement.isJsonObject()) {
            throw new MalformedComponentException("Expected a " + JsonObject.class.getSimpleName() + " as value of tagHead, got: " + tagHeadElement);
        }

        JsonObject tagHeadObj = tagHeadElement.getAsJsonObject();
        JsonElement valueElement = tagHeadObj.get("Value");

        if (valueElement == null) {
            return;
        }

        if (!valueElement.isJsonPrimitive()) {
            throw new MalformedComponentException("Expected a " + JsonPrimitive.class.getSimpleName() + " as value of tagHead.Value, got: " + valueElement);
        }

        String value = valueElement.getAsString();

        if (value.isEmpty()) {
            return;
        }

        String rawURL;

        try {
            JsonObject dataObject = JsonParser.parseString(new String(Base64.getDecoder().decode(value))).getAsJsonObject();
            rawURL = dataObject.get("textures").getAsJsonObject().get("SKIN").getAsJsonObject().get("url").getAsString();
        } catch (Exception exception) {
            throw new MalformedComponentException("Error deserializing tagHead.Value! " +
                    "If you didn't manually change the Base64 value in the file, then this is probably an issue with the player head on BDEngine",
                    exception);
        }

        itemStack.editMeta(SkullMeta.class, meta -> {
            PlayerProfile profile = Bukkit.createProfile(ARBITRARY);
            PlayerTextures playerTextures = profile.getTextures();

            try {
                playerTextures.setSkin(URI.create(rawURL).toURL());
            } catch (MalformedURLException exception) {
                throw new MalformedComponentException("Malformed skin texture URL: " + exception.getMessage());
            }

            profile.setTextures(playerTextures);
            meta.setPlayerProfile(profile);
        });
    }

    @Override
    public ItemDisplaySchematicComponent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();

        String name = deserializeName(object);
        Matrix4f transformation = deserializeTransforms(object, context);

        int endIndex = name.indexOf("[");
        Material material = Material.getMaterial(name.substring(0, endIndex == -1 ? 0 : endIndex).toUpperCase());

        if (material == null) {
            throw new MalformedComponentException("Material specified for " + ItemDisplaySchematicComponent.class.getSimpleName() + " '" + name + "' doesn't exist!");
        }

        ItemStack itemStack = new ItemStack(material);

        // Skull Meta
        trySkullData(itemStack, object);

        return new ItemDisplaySchematicComponent(name, transformation, itemStack);
    }
}
