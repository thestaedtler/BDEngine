package org.cl.engine.util;

import org.bukkit.util.Transformation;
import org.joml.*;

import java.util.function.Function;

/**
 * Represents an affine operator f:\mathbb{R}^3\to\mathbb{R}^3, where \mathbb{R}^3 is the set of all 3-tuples with real entries
 * regarded as an affine space over itself with the standard affine action.
 *
 * @author cahrypt
 */
public final class AffineTransformation implements Function<Vector3f, Vector3f> {
    private final Matrix4f transformation;

    /**
     * Uses the {@link Transformation} operation order:
     * ((Left Rotation) * (Scale) * (Right Rotation)) + (Translation)
     */
    public static AffineTransformation of(Vector3f translation, AxisAngle4f leftRotation, Vector3f scale, AxisAngle4f rightRotation) {
        return AffineTransformation.fromBukkitTransformation(new Transformation(translation, leftRotation, scale, rightRotation));
    }

    /**
     * Converts the passed {@link Transformation}
     * @param transformation the transformation
     * @return the corresponding AffineTransformation
     */
    public static AffineTransformation fromBukkitTransformation(Transformation transformation) {
        return new AffineTransformation(
                new Matrix4f()
                        .rotateAffine(transformation.getLeftRotation())
                        .scale(transformation.getScale())
                        .rotateAffine(transformation.getRightRotation())
                        .setTranslation(transformation.getTranslation())
        );
    }

    /**
     * Wraps an affine matrix
     * @param affine the matrix
     * @throws IllegalStateException the passed {@link Matrix4f} must be affine
     */
    public AffineTransformation(Matrix4f affine) {
        if (!affine.isAffine()) {
            throw new IllegalStateException("Affine matrix required!");
        }

        this.transformation = affine;
    }

    /**
     * An affine transformation can be completely described by a linear operator on Euclidean 3-space and translation.
     * The linear operator encodes scale and rotation.
     * @param linearOperator the linear operator
     * @param originTranslation the translation vector
     */
    public AffineTransformation(Matrix3f linearOperator, Vector3f originTranslation) {
        this.transformation = new Matrix4f(linearOperator)
                .setTranslation(originTranslation)
                .set(3, 3, 1);
    }

    /**
     * Apply this transformation to a vector
     * @param vector3f the function argument
     * @return the resulting vector
     */
    @Override
    public Vector3f apply(Vector3f vector3f) {
        Vector4f applied = transformation.transformAffine(new Vector4f(vector3f, 1));
        return new Vector3f(applied.x(), applied.y(), applied.z());
    }

    /**
     * Get a copy of the affine operator encoded by this object
     * @return said copy
     */
    public Matrix4f matrix() {
        return new Matrix4f(transformation);
    }
}
