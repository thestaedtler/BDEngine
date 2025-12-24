package org.cl.engine.model.types;

import org.bukkit.entity.Display;
import org.bukkit.util.Transformation;
import org.cl.engine.model.ModelPart;
import org.cl.engine.util.AffineTransformation;
import org.joml.Matrix4f;

/**
 * Wraps a display that can be a part of a model
 * @param display the display which is wrapped
 * @param originalTransformation the original transformation of the display. If this part
 *                               belongs to a {@link org.cl.engine.model.Model}, this is the transformation
 *                               this part was deserialized with.
 * @author cahrypt
 */
public record Member(Display display, AffineTransformation originalTransformation) implements ModelPart {
    /**
     * Returns the current transformation of the wrapped display
     * @return the current transformation of the wrapped display
     */
    public Transformation transformation() {
        return display.getTransformation();
    }

    /**
     * Returns the affine matrix representing the transformation of the wrapped display
     * @return the affine matrix representing the transformation of the wrapped display
     */
    public Matrix4f transformationMatrix() {
        return AffineTransformation.fromBukkitTransformation(transformation()).matrix();
    }

    @Override
    public void transform(AffineTransformation affineTransformation) {
        display.setTransformationMatrix(affineTransformation.matrix().mulAffine(transformationMatrix()));
    }

    @Override
    public void revert() {
        display.setTransformationMatrix(originalTransformation().matrix());
    }
}
