package com.mbrlabs.mundus.commons.assets.skybox;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@Setter
public class SkyboxMeta {
    private String top;
    private String bottom;
    private String left;
    private String right;
    private String front;
    private String back;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SkyboxMeta that = (SkyboxMeta) o;

        return new EqualsBuilder().append(top, that.top).append(bottom, that.bottom).append(left, that.left)
                .append(right, that.right).append(front, that.front).append(back, that.back).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(top).append(bottom).append(left)
                .append(right).append(front).append(back).toHashCode();
    }
}
