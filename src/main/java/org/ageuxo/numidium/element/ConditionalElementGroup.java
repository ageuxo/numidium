package org.ageuxo.numidium.element;

import org.ageuxo.numidium.blockentity.ElementBlockEntity;

import java.util.function.Predicate;

/**
 * ElementGroup that runs all contained Elements if its predicate returns true.
 */
public class ConditionalElementGroup extends ElementGroup {

    protected final Predicate<ElementBlockEntity> predicate;

    public ConditionalElementGroup(Predicate<ElementBlockEntity> predicate, LogicElement... elements) {
        super(elements);
        this.predicate = predicate;
    }

    @Override
    public void tick(ElementBlockEntity blockEntity) {
        if (predicate.test(blockEntity)) {
            for (LogicElement element : elements) {
                element.tick(blockEntity);
            }
        }
    }

}
