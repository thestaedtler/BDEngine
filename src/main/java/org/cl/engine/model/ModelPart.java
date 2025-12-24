package org.cl.engine.model;

import org.bukkit.util.Transformation;
import org.cl.engine.util.AffineTransformation;

/**
 * Represents a part of a model. Local scaling and translation are useless
 * for arguably all practical purposes as the locality of such operations
 * predictably results in model disfiguring.
 *
 * @author cahrypt
 */
public interface ModelPart {

    default void transform(Transformation transformation) {
        transform(AffineTransformation.fromBukkitTransformation(transformation));
    }

    void transform(AffineTransformation affineTransformation);

    /**
     * Get the transformation of which this part was spawned with.
     * Used for {@link ModelPart#revert()}.
     * @return the transformation of which this part was spawned with
     */
    AffineTransformation originalTransformation();

    /**
     * Sets the transformation to that which this part was spawned with.
     */
    void revert();
}
