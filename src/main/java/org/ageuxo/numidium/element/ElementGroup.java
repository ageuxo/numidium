package org.ageuxo.numidium.element;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
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

    @Override
    public Tag saveData() {
        CompoundTag tag = new CompoundTag();
        for (int i = 0; i < elements.size(); i++) {
            LogicElement element = elements.get(i);
            tag.put(String.valueOf(i), element.saveData());
        }

        return tag;
    }

    @Override
    public void loadData(Tag tag) {
        for (int i = 0; i < elements.size(); i++) {
            LogicElement element = elements.get(i);
            Tag subTag = ((CompoundTag) tag).get(String.valueOf(i));
            element.loadData(subTag);
        }
    }
}
