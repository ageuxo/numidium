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
import org.ageuxo.numidium.element.data.ElementDataMap;
import org.ageuxo.numidium.element.data.ElementDataPatch;
import org.ageuxo.numidium.element.data.ElementDataType;
import org.ageuxo.numidium.element.data.PatchedElementDataMap;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@ParametersAreNonnullByDefault
public abstract class ElementBlockEntity extends BlockEntity {

    protected LogicElement element;
    protected PatchedElementDataMap elementData;

    public ElementBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState, LogicElement element) {
        super(type, pos, blockState);
        this.element = element;
    }

    protected ElementDataMap createElementDataPrototype() {
        ElementDataMap.Builder builder = ElementDataMap.builder();
        defineElementData(builder);
        return builder.build();
    }

    /**
     * Override this to define the types of ElementData available to the LogicElements in this BlockEntity
     */
    protected abstract void defineElementData(ElementDataMap.Builder builder);

    public static void tick(Level level, BlockPos pos, BlockState state, ElementBlockEntity blockEntity) {
        blockEntity.element.tick(level, pos, state, blockEntity);
    }

    /**
     * Get value assigned to type in this BlockEntity's ElementDataMap
     * @return value of this type
     */
    public <T> T get(ElementDataType<T> type) {
        return elementData.get(type);
    }

    /**
     * Set value assigned to type in this BlockEntity's ElementDataMap
     * @return previous value of this type
     */
    public <T> T set(ElementDataType<T> type, @Nullable T value) {
        return elementData.set(type, value);
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
        elementData = PatchedElementDataMap.fromPatch(createElementDataPrototype(), patch.orElse(ElementDataPatch.EMPTY));
    }
}
