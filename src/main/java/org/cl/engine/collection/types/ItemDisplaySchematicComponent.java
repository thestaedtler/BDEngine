package org.cl.engine.collection.types;

import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.cl.engine.collection.DisplaySchematicComponent;
import org.joml.Matrix4f;

/**
 * @author cahrypt
 */
public final class ItemDisplaySchematicComponent extends DisplaySchematicComponent {
    private final ItemStack itemStack;

    public ItemDisplaySchematicComponent(String name, Matrix4f localTransformation, ItemStack itemStack) {
        super(name, localTransformation);

        this.itemStack = itemStack;
    }

    @Override
    public Display spawn(Location location) {
        ItemDisplay itemDisplay = location.getWorld().spawn(location, ItemDisplay.class);
        itemDisplay.setItemStack(itemStack);

        return itemDisplay;
    }

    public ItemStack getItemStack() {
        return itemStack.clone();
    }
}
