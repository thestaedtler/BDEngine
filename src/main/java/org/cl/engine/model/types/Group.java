package org.cl.engine.model.types;

import org.cl.engine.model.ModelPart;
import org.cl.engine.util.AffineTransformation;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * Represents a {@link ModelPart} collection
 * @param parts the part collection
 * @param id the id of the group
 * @param originalTransformation the original transformation of the group. If this part
 *  *                               belongs to a {@link org.cl.engine.model.Model}, this is the transformation
 *  *                               this part was deserialized with.
 * @author cahrypt
 */
public record Group(List<ModelPart> parts, String id, AffineTransformation originalTransformation) implements ModelPart {
    /**
     * Gets the group with the specified id which may be a child of this group,
     * a child of its children, a child of its children's children, etc.
     * @param path the id path to the target Group which takes the same structure
     *             as a file path: <code>[grandparent, parent, child (the target)]</code>
     * @return the target group, or null if it doesn't exist or isn't a {@link Group}
     */
    public Group getDescendantGroup(String[] path) {
        Group child = getChildGroup(path[0]);

        if (child == null) {
            return null;
        }

        if (path.length == 1) {
            return child;
        }

        return child.getDescendantGroup(Arrays.copyOfRange(path, 1, path.length));
    }

    /**
     * Gets the immediate child group of this group with the specified id. Note
     * that this operation is O(n) where n denotes the number of children. Use groups effectively!
     * @param id the id of the child group to search for
     * @return the child group, or null if it doesn't exist or isn't a {@link Group}
     */
    public Group getChildGroup(String id) {
        for (ModelPart part : parts) {
            if (!(part instanceof Group group)) {
                continue;
            }

            if (group.id().equals(id)) {
                return group;
            }
        }

        return null;
    }

    /**
     * Consumes all descendants of this group that are of instance {@link Member}
     * @param memberConsumer the consumer
     */
    public void consumeMembers(Consumer<Member> memberConsumer) {
        for (ModelPart part : parts) {
            if (part instanceof Group group) {
                group.consumeMembers(memberConsumer);
                continue;
            }

            memberConsumer.accept((Member) part);
        }
    }

    @Override
    public void transform(AffineTransformation affineTransformation) {
        consumeMembers(member -> member.transform(affineTransformation));
    }

    @Override
    public void revert() {
        consumeMembers(Member::revert);
    }
}
