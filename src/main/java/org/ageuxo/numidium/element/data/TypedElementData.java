package org.ageuxo.numidium.element.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public record TypedElementData<T>(ElementDataType<T> type, T value) {
    public static final StreamCodec<RegistryFriendlyByteBuf, TypedElementData<?>> STREAM_CODEC = new StreamCodec<>() {
        public TypedElementData<?> decode(RegistryFriendlyByteBuf buffer) {
            ElementDataType<?> decoded = ElementDataType.STREAM_CODEC.decode(buffer);
            return decodeTyped(buffer, decoded);
        }

        private static <T> TypedElementData<T> decodeTyped(RegistryFriendlyByteBuf buffer, ElementDataType<T> component) {
            return new TypedElementData<>(component, component.streamCodec().decode(buffer));
        }

        public void encode(RegistryFriendlyByteBuf buffer, TypedElementData<?> value) {
            encodeCap(buffer, value);
        }

        private static <T> void encodeCap(RegistryFriendlyByteBuf buffer, TypedElementData<T> component) {
            ElementDataType.STREAM_CODEC.encode(buffer, component.type());
            component.type().streamCodec().encode(buffer, component.value());
        }
    };

    static TypedElementData<?> fromEntryUnchecked(Map.Entry<ElementDataType<?>, Object> entry) {
        return createUnchecked(entry.getKey(), entry.getValue());
    }

    public static <T> TypedElementData<T> createUnchecked(ElementDataType<T> type, Object value) {
        //noinspection unchecked
        return new TypedElementData<>(type, (T)value);
    }

    public void applyTo(PatchedElementDataMap map) {
        map.set(this.type, this.value);
    }

    public <D> DataResult<D> encodeValue(DynamicOps<D> ops) {
        Codec<T> codec = this.type.codec();
        return codec == null ? DataResult.error(() -> "Element data of type " + this.type + " is not encodable") : codec.encodeStart(ops, this.value);
    }

    @Override
    public String toString() {
        return this.type + "=>" + this.value;
    }
}
