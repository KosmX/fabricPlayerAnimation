package dev.kosmx.playerAnim.api.layered;

import dev.kosmx.playerAnim.api.TransformType;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractFadeModifier;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractModifier;
import dev.kosmx.playerAnim.core.util.Vec3f;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


/**
 * Layer to easily swap animations, add modifiers or do other sort of effects
 * Modifiers <b>affect</b> each other. For example if you put a fade modifier after a speed modifier, it will be affected by the modifier.
 *
 * @param <T>
 */
public class ModifierLayer<T extends IAnimation> implements IAnimation {

    private final List<AbstractModifier> modifiers = new ArrayList<>();
    @Nullable
    @Getter
    T animation;


    public ModifierLayer(@Nullable T animation, AbstractModifier... modifiers) {
        this.animation = animation;
        Collections.addAll(this.modifiers, modifiers);
    }

    public ModifierLayer() {
        this(null);
    }

    @Override
    public void tick() {
        for (int i = 0; i < modifiers.size(); i++) {
            if (modifiers.get(i).canRemove()) {
                removeModifier(i--);
            }
        }
        if (modifiers.size() > 0) {
            modifiers.get(0).tick();
        } else if (animation != null) animation.tick();
    }

    public void addModifier(AbstractModifier modifier, int idx) {
        modifier.setHost(this);
        modifiers.add(idx, modifier);
        this.linkModifiers();
    }

    public void addModifierBefore(AbstractModifier modifier) {
        this.addModifier(modifier, 0);
    }

    public void addModifierLast(AbstractModifier modifier) {
        this.addModifier(modifier, modifiers.size());
    }

    public void removeModifier(int idx) {
        modifiers.remove(idx);
        this.linkModifiers();
    }


    public void setAnimation(@Nullable T animation) {
        this.animation = animation;
        this.linkModifiers();
    }

    public void replaceAnimationWithFade(AbstractFadeModifier fadeModifier, @Nullable T newAnimation) {
        fadeModifier.setBeginAnimation(this.getAnimation());
        this.setAnimation(newAnimation);
    }

    public int size() {
        return modifiers.size();
    }

    protected void linkModifiers() {
        Iterator<AbstractModifier> modifierIterator = modifiers.iterator();
        if (modifierIterator.hasNext()) {
            AbstractModifier tmp = modifierIterator.next();
            while (modifierIterator.hasNext()) {
                AbstractModifier tmp2 = modifierIterator.next();
                tmp.setAnim(tmp2);
                tmp = tmp2;
            }
            tmp.setAnim(this.animation);
        }
    }


    @Override
    public boolean isActive() {
        if (modifiers.size() > 0) {
            return modifiers.get(0).isActive();
        } else if (animation != null) return animation.isActive();
        return false;
    }

    @Override
    public Vec3f get3DTransform(String modelName, TransformType type, float tickDelta, Vec3f value0) {
        if (modifiers.size() > 0) {
            return modifiers.get(0).get3DTransform(modelName, type, tickDelta, value0);
        } else if (animation != null) return animation.get3DTransform(modelName, type, tickDelta, value0);
        return value0;
    }

    @Override
    public void setupAnim(float tickDelta) {
        if (modifiers.size() > 0) {
            modifiers.get(0).setupAnim(tickDelta);
        } else if (animation != null) animation.setupAnim(tickDelta);
    }
}
