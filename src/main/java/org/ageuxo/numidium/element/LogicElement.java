package org.ageuxo.numidium.element;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.ageuxo.numidium.blockentity.ElementBlockEntity;

public interface LogicElement {
    void tick(Level level, BlockPos pos, BlockState state, ElementBlockEntity blockEntity);

}
