package org.cl.engine.collection.types;

import org.bukkit.Location;
import org.cl.engine.collection.SchematicComponent;
import org.cl.engine.model.ModelPart;
import org.cl.engine.model.types.Group;
import org.cl.engine.util.AffineTransformation;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author cahrypt
 */
public final class CollectionSchematicComponent extends SchematicComponent {
    private final Collection<SchematicComponent> schematicComponents;

    public CollectionSchematicComponent(String name, Matrix4f localTransformation, Collection<SchematicComponent> schematicComponents) {
        super(name, localTransformation);

        this.schematicComponents = schematicComponents;
    }

    public Set<SchematicComponent> getComponents() {
        return new HashSet<>(schematicComponents);
    }

    @Override
    public ModelPart build(Location location, Matrix4f parentAffineTransformation) {
        applyTransformation(parentAffineTransformation);
        Group group = new Group(new ArrayList<>(), id(), new AffineTransformation(parentAffineTransformation));

        for (SchematicComponent schematicComponent : schematicComponents) {
            ModelPart part = schematicComponent.build(location, new Matrix4f(parentAffineTransformation));
            group.parts().add(part);
        }

        return group;
    }
}
