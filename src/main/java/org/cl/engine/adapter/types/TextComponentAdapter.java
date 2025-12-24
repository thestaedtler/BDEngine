package org.cl.engine.adapter.types;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.entity.TextDisplay;
import org.cl.engine.adapter.ComponentAdapter;
import org.cl.engine.exceptions.MalformedComponentException;
import org.cl.engine.collection.types.TextDisplaySchematicComponent;

import java.lang.reflect.Type;

/**
 * @author cahrypt
 */
public final class TextComponentAdapter implements ComponentAdapter<TextDisplaySchematicComponent> {

    private JsonPrimitive getOption(JsonObject object, String name) {
        JsonElement element = object.get(name);

        if (element == null) {
            throw new MalformedComponentException("Raw " + TextDisplaySchematicComponent.class.getSimpleName() + " is missing " + name + " option");
        }

        if (!element.isJsonPrimitive()) {
            throw new MalformedComponentException("Expected " + JsonPrimitive.class.getSimpleName() + " as " + TextDisplaySchematicComponent.class.getSimpleName() + "option, got: " + element);
        }

        return element.getAsJsonPrimitive();
    }

    private float fixAlpha(float alpha) {
        return Math.max(Math.min(alpha, 1), 0);
    }

    private TextColor getTextColor(String hex) {
        TextColor color = TextColor.fromHexString(hex);

        if (color == null) {
            throw new MalformedComponentException("Invalid HEX code " + hex + " specified in " + TextDisplaySchematicComponent.class.getSimpleName() + " options");
        }

        return color;
    }

    @Override
    public TextDisplaySchematicComponent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();
        String text = deserializeName(object);
        JsonElement optionsElement = object.get("options");

        if (optionsElement == null) {
            throw new MalformedComponentException("Raw " + TextDisplaySchematicComponent.class.getSimpleName() + " missing options object!");
        }

        if (!optionsElement.isJsonObject()) {
            throw new MalformedComponentException("Expected " + JsonObject.class.getSimpleName() + " as value of options, got: " + optionsElement);
        }

        JsonObject options = optionsElement.getAsJsonObject();

        ComponentBuilder<TextComponent, TextComponent.Builder> builder = Component.text();
        builder.append(Component.text(text));
        builder.color(getTextColor(getOption(options, "color").getAsString()));

        Style.Builder styleBuilder = Style.style();

        boolean bold = getOption(options, "bold").getAsBoolean();
        boolean italic = getOption(options, "italic").getAsBoolean();
        boolean underlined = getOption(options, "underline").getAsBoolean();
        boolean strikethrough = getOption(options, "strikeThrough").getAsBoolean();
        boolean obfuscated = getOption(options, "obfuscated").getAsBoolean();
        float alpha = getOption(options, "alpha").getAsFloat();
        float bgAlpha = getOption(options, "backgroundAlpha").getAsFloat();
        int lineLength = getOption(options, "lineLength").getAsInt();
        TextDisplay.TextAlignment alignment;

        try {
            alignment = TextDisplay.TextAlignment.valueOf(getOption(options, "align").getAsString().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new MalformedComponentException("Invalid alignment specified in raw " + TextDisplaySchematicComponent.class.getSimpleName() + " options");
        }

        if (bold) {
            styleBuilder.decorate(TextDecoration.BOLD);
        }

        if (italic) {
            styleBuilder.decorate(TextDecoration.ITALIC);
        }

        if (underlined) {
            styleBuilder.decorate(TextDecoration.UNDERLINED);
        }

        if (strikethrough) {
            styleBuilder.decorate(TextDecoration.STRIKETHROUGH);
        }

        if (obfuscated) {
            styleBuilder.decorate(TextDecoration.OBFUSCATED);
        }

        builder.style(styleBuilder.build());

        String hex = getOption(options, "backgroundColor").getAsString();
        TextColor intermediate = getTextColor(hex);

        TextDisplaySchematicComponent.Data data = new TextDisplaySchematicComponent.Data(
                builder.build(),
                fixAlpha(alpha),
                Color.fromRGB(intermediate.red(), intermediate.green(), intermediate.blue()),
                fixAlpha(bgAlpha),
                lineLength,
                alignment
        );

        return new TextDisplaySchematicComponent(text, deserializeTransforms(object, context), data);
    }
}
