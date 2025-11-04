package org.ageuxo.numidium.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.ageuxo.numidium.element.LogicElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ElementBlockEntity extends BlockEntity {

    protected List<LogicElement> elements = new ArrayList<>();

    public ElementBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState, LogicElement... elements) {
        super(type, pos, blockState);
        this.elements.addAll(Arrays.asList(elements));
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ElementBlockEntity blockEntity) {
        for (LogicElement logicElement : blockEntity.elements) {
            logicElement.tick(level, pos, state, blockEntity);
        }
    }
}
