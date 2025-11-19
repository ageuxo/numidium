package org.ageuxo.numidium.samples;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.ageuxo.numidium.blockentity.ElementBlockEntity;
import org.ageuxo.numidium.element.ElementGroup;
import org.ageuxo.numidium.element.LogicElement;
import org.ageuxo.numidium.element.data.ElementDataMap;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class SimpleMachineBlockEntity extends ElementBlockEntity {

    public SimpleMachineBlockEntity(BlockPos pos, BlockState blockState) {
        this(SampleImpl.SIMPLE_MACHINE_BE.get(), pos, blockState, defineLogicElements());
    }

    public SimpleMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState, LogicElement element) {
        super(type, pos, blockState, element);
    }

    private static LogicElement defineLogicElements() {
        return new ElementGroup(

        );
    }

    @Override
    protected void defineElementData(ElementDataMap.Builder builder) {

    }

}
