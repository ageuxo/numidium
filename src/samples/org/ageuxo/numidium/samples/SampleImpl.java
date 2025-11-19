package org.ageuxo.numidium.samples;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.ageuxo.numidium.Numidium;
import org.ageuxo.numidium.block.ElementBlock;

@Mod(Numidium.MODID)
public class SampleImpl {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Numidium.MODID);

    public static final DeferredBlock<ElementBlock> SIMPLE_MACHINE_BLOCK = BLOCKS.registerBlock("simple_machine", SimpleMachineBlock::new);

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Numidium.MODID);

    @SuppressWarnings("DataFlowIssue")
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SimpleMachineBlockEntity>> SIMPLE_MACHINE_BE = BLOCK_ENTITIES.register("simple_machine",
            ()-> BlockEntityType.Builder.of(SimpleMachineBlockEntity::new, SIMPLE_MACHINE_BLOCK.get()).build(null)
    );

    public SampleImpl(IEventBus bus) {
        BLOCKS.register(bus);
        BLOCK_ENTITIES.register(bus);
    }





}
