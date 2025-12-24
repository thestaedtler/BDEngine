package org.cl.engine.collection.types;

import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.cl.engine.collection.DisplaySchematicComponent;
import org.joml.Matrix4f;

/**
 * @author cahrypt
 */
public final class BlockDisplaySchematicComponent extends DisplaySchematicComponent {
    private final BlockData blockData;

    public BlockDisplaySchematicComponent(String name, Matrix4f localTransformation, BlockData blockData) {
        super(name, localTransformation);

        this.blockData = blockData;
    }

    @Override
    public Display spawn(Location location) {
        BlockDisplay blockDisplay = location.getWorld().spawn(location, BlockDisplay.class);
        blockDisplay.setBlock(blockData);

        return blockDisplay;
    }

    public BlockData getBlockData() {
        return blockData;
    }
}
