package org.ageuxo.numidium.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.ageuxo.numidium.element.LogicElement;
import org.ageuxo.numidium.element.data.ElementDataPatch;
import org.ageuxo.numidium.element.data.PatchedElementDataMap;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@ParametersAreNonnullByDefault
public class ElementBlockEntity extends BlockEntity {

    protected LogicElement element;
    protected PatchedElementDataMap elementData;

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
        tag.put("elementData", ElementDataPatch.CODEC.encodeStart(NbtOps.INSTANCE, elementData.asPatch()).getPartialOrThrow());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        element.loadData(tag.get("element"));
        Optional<ElementDataPatch> patch = ElementDataPatch.CODEC.parse(NbtOps.INSTANCE, tag.get("elementData")).result();
        elementData = PatchedElementDataMap.fromPatch(elementData, patch.orElse(ElementDataPatch.EMPTY));
    }
}
