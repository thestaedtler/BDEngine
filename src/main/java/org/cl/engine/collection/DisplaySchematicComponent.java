package org.cl.engine.collection;

import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.cl.engine.model.ModelPart;
import org.cl.engine.model.types.Member;
import org.cl.engine.util.AffineTransformation;
import org.joml.Matrix4f;

/**
 * @author cahrypt
 */
public abstract class DisplaySchematicComponent extends SchematicComponent {
    
    public DisplaySchematicComponent(String name, Matrix4f localTransformation) {
        super(name, localTransformation);
    }

    public abstract Display spawn(Location location);

    @Override
    public ModelPart build(Location location, Matrix4f parentAffineTransformation) {
        Display display = spawn(location);
        applyTransformation(parentAffineTransformation);
        display.setTransformationMatrix(parentAffineTransformation);

        return new Member(display, new AffineTransformation(parentAffineTransformation));
    }
}
