package org.ageuxo.numidium.element;

import org.ageuxo.numidium.blockentity.ElementBlockEntity;

import java.util.List;

/**
 * Group of Elements, runs in insertion order.
 */
public class ElementGroup implements LogicElement {

    protected List<LogicElement> elements;

    public ElementGroup(LogicElement... elements) {
        this.elements = List.of(elements);
    }

    @Override
    public void tick(ElementBlockEntity blockEntity) {
        for (LogicElement element : elements) {
            element.tick(blockEntity);
        }
    }

}
