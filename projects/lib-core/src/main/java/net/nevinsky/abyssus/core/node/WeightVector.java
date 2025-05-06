package net.nevinsky.abyssus.core.node;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;
import lombok.Getter;

@Getter
public class WeightVector {
    private int count;
    private float[] values;

    public WeightVector() {
        this(0, 8);
    }

    public WeightVector(int count) {
        this(count, 8);
    }

    public WeightVector(int count, int max) {
        this.count = count;
        values = new float[Math.max(count, max)];
    }

    public WeightVector set(WeightVector weights) {
        if (weights.count > values.length) {
            values = new float[weights.count];
        }
        // throw new GdxRuntimeException("WeightVector out of bound");
        this.count = weights.count;
        System.arraycopy(weights.values, 0, values, 0, weights.values.length);
        return this;
    }

    public void lerp(WeightVector value, float t) {
        if (count != value.count) {
            throw new GdxRuntimeException("WeightVector count mismatch");
        }
        for (int i = 0; i < count; i++) {
            values[i] = MathUtils.lerp(values[i], value.values[i], t);
        }
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("WeightVector(");
        for (int i = 0; i < count; i++) {
            if (i > 0) {
                s.append(", ");
            }
            s.append(values[i]);
        }
        return s + ")";
    }

    public WeightVector set() {
        this.count = 0;
        return this;
    }

    public WeightVector cpy() {
        return new WeightVector(count, values.length).set(this);
    }

    public float get(int index) {
        return values[index];
    }

    public WeightVector scl(float s) {
        for (int i = 0; i < count; i++) {
            values[i] *= s;
        }
        return this;
    }

    public WeightVector mulAdd(WeightVector w, float s) {
        for (int i = 0; i < count; i++) {
            values[i] += w.values[i] * s;
        }
        return this;
    }

}
