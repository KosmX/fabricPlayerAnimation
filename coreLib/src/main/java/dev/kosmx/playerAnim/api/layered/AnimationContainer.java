package dev.kosmx.playerAnim.api.layered;

import dev.kosmx.playerAnim.api.TransformType;
import dev.kosmx.playerAnim.core.util.Vec3f;

/**
 * A container to make swapping animation object easier
 * It will clone the behaviour of the held animation
 *
 * you can put endless AnimationContainer into each other
 * @param <T> Nullable animation
 */
public class AnimationContainer<T extends IAnimation> implements IAnimation {
    protected T anim;

    public AnimationContainer(T anim) {
        this.anim = anim;
    }

    public AnimationContainer() {
        this.anim = null;
    }

    public void setAnim(T newAnim) {
        this.anim = newAnim;
    }

    public T getAnim() {
        return this.anim;
    }

    @Override
    public boolean isActive() {
        return anim != null && anim.isActive();
    }

    @Override
    public void tick() {
        if (anim != null) anim.tick();
    }

    @Override
    public Vec3f get3DTransform(String modelName, TransformType type, float tickDelta, Vec3f value0) {
        return anim == null ? value0 : anim.get3DTransform(modelName, type, tickDelta, value0);
    }

    @Override
    public void setupAnim(float tickDelta) {
        if (this.anim != null) this.anim.setupAnim(tickDelta);
    }
}
