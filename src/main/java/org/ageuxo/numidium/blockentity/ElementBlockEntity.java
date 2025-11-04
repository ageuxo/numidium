package org.ageuxo.numidium.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.ageuxo.numidium.element.LogicElement;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ElementBlockEntity extends BlockEntity {

    protected LogicElement element;

    public ElementBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState, LogicElement element) {
        super(type, pos, blockState);
        this.element = element;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ElementBlockEntity blockEntity) {
        blockEntity.element.tick(level, pos, state, blockEntity);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("element", element.saveData());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        element.loadData(tag.get("element"));
    }
}
