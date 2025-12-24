package org.cl.engine.collection.types;

import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.cl.engine.collection.DisplaySchematicComponent;
import org.joml.Matrix4f;

/**
 * @author cahrypt
 */
public final class TextDisplaySchematicComponent extends DisplaySchematicComponent {
    public record Data(
            Component text,
            float alpha,
            Color background,
            float backgroundAlpha,
            int lineLength,
            TextDisplay.TextAlignment alignment
    ) {}

    private final Data data;

    public TextDisplaySchematicComponent(String name, Matrix4f localTransformation, Data data) {
        super(name, localTransformation);
        this.data = data;
    }

    @Override
    public Display spawn(Location location) {
        TextDisplay textDisplay = location.getWorld().spawn(location, TextDisplay.class);

        textDisplay.text(data.text());
        textDisplay.setAlignment(data.alignment());
        textDisplay.setLineWidth(data.lineLength());
        textDisplay.setBackgroundColor(data.background());
        textDisplay.setTextOpacity((byte) (data.alpha * 128));
        textDisplay.setSeeThrough(data.backgroundAlpha() == 0);

        return textDisplay;
    }

    public Data getData() {
        return data;
    }
}
