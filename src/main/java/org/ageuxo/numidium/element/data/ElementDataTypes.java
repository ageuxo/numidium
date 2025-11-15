package org.ageuxo.numidium.element.data;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.ageuxo.numidium.Numidium;
import org.jetbrains.annotations.NotNull;

import java.util.function.UnaryOperator;

public class ElementDataTypes {

    public static final ResourceKey<Registry<ElementDataType<?>>> REGISTRY_KEY = ResourceKey.createRegistryKey(Numidium.modRL("element_data_type"));
    public static final Registry<ElementDataType<?>> REGISTRY  = new RegistryBuilder<>(REGISTRY_KEY)
            .sync(true)
            .create();

    public static final ElementDataType<Integer> PROGRESS = register("progress",
            syncedPersistent(Codec.INT, ByteBufCodecs.INT)
    );
    public static final ElementDataType<ItemStack> ITEM_STACK = register("item_stack",
            syncedPersistent(ItemStack.CODEC, ItemStack.STREAM_CODEC)
    );
    public static final ElementDataType<FluidStack> FLUID_STACK = register("fluid_stack",
            syncedPersistent(FluidStack.CODEC, FluidStack.STREAM_CODEC)
    );

    private static @NotNull <T> UnaryOperator<ElementDataType.Builder<T>> syncedPersistent(Codec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
        return b -> b.persistent(codec)
                .networkSynchronized(streamCodec);
    }

    private static <T> ElementDataType<T> register(String name, UnaryOperator<ElementDataType.Builder<T>> builder) {
        return Registry.register(REGISTRY, Numidium.modRL(name), builder.apply(ElementDataType.builder()).build());
    }


}
