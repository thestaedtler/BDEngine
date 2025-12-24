package org.cl.engine.collection;

import org.bukkit.Location;
import org.cl.engine.model.ModelPart;
import org.joml.Matrix4f;
/**
 * @author cahrypt
 */
public abstract class SchematicComponent {
    private final String id;
    private final Matrix4f localAffineTransformation;

    public SchematicComponent(String id, Matrix4f localAffineTransformation) {
        this.id = id;
        this.localAffineTransformation = localAffineTransformation;
    }

    public abstract ModelPart build(Location location, Matrix4f parentAffineTransformation);

    protected void applyTransformation(Matrix4f affine) {
        // Global transformation applied after local
        affine.mulAffine(localAffineTransformation);
    }

    protected String id() {
        return id;
    }
}
