package org.ageuxo.numidium.element;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
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
    public void tick(Level level, BlockPos pos, BlockState state, ElementBlockEntity blockEntity) {
        for (LogicElement element : elements) {
            element.tick(level, pos, state, blockEntity);
        }
    }
}
