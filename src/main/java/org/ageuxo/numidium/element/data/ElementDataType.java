package org.ageuxo.numidium.element.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.Util;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.EncoderCache;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;

public interface ElementDataType<T> {
    EncoderCache ENCODER_CACHE = new EncoderCache(512);
    Codec<ElementDataType<?>> CODEC = Codec.lazyInitialized(ElementDataTypes.REGISTRY::byNameCodec);
    StreamCodec<RegistryFriendlyByteBuf, ElementDataType<?>> STREAM_CODEC = StreamCodec.recursive(
            p_330812_ -> ByteBufCodecs.registry(ElementDataTypes.REGISTRY_KEY)
    );
    Codec<ElementDataType<?>> PERSISTENT_CODEC = CODEC.validate(
            p_337456_ -> p_337456_.isTransient()
                    ? DataResult.error(() -> "Encountered transient type " + ElementDataTypes.REGISTRY.getKey(p_337456_))
                    : DataResult.success(p_337456_)
    );
    Codec<Map<ElementDataType<?>, Object>> VALUE_MAP_CODEC = Codec.dispatchedMap(PERSISTENT_CODEC, ElementDataType::codecOrThrow);

    static <T> ElementDataType.Builder<T> builder() {
        return new ElementDataType.Builder<>();
    }

    @Nullable
    Codec<T> codec();

    default Codec<T> codecOrThrow() {
        Codec<T> codec = this.codec();
        if (codec == null) {
            throw new IllegalStateException(this + " is not a persistent type");
        } else {
            return codec;
        }
    }

    default boolean isTransient() {
        return this.codec() == null;
    }

    StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec();

    class Builder<T> {
        @Nullable
        private Codec<T> codec;
        @Nullable
        private StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec;
        private boolean cacheEncoding;

        public ElementDataType.Builder<T> persistent(Codec<T> codec) {
            this.codec = codec;
            return this;
        }

        public ElementDataType.Builder<T> networkSynchronized(StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
            this.streamCodec = streamCodec;
            return this;
        }

        public ElementDataType.Builder<T> cacheEncoding() {
            this.cacheEncoding = true;
            return this;
        }

        public ElementDataType<T> build() {
            StreamCodec<? super RegistryFriendlyByteBuf, T> streamcodec = Objects.requireNonNullElseGet(
                    this.streamCodec, () -> ByteBufCodecs.fromCodecWithRegistries(Objects.requireNonNull(this.codec, "Missing Codec for type"))
            );
            Codec<T> codec = this.cacheEncoding && this.codec != null ? ElementDataType.ENCODER_CACHE.wrap(this.codec) : this.codec;
            return new ElementDataType.Builder.SimpleType<>(codec, streamcodec);
        }

        static class SimpleType<T> implements ElementDataType<T> {
            @Nullable
            private final Codec<T> codec;
            private final StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec;

            SimpleType(@Nullable Codec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
                this.codec = codec;
                this.streamCodec = streamCodec;
            }

            @Nullable
            @Override
            public Codec<T> codec() {
                return this.codec;
            }

            @Override
            public StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec() {
                return this.streamCodec;
            }

            @Override
            public String toString() {
                return Util.getRegisteredName(ElementDataTypes.REGISTRY, this);
            }
        }
    }
}
