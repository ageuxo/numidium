package org.ageuxo.numidium.element.data;

import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface ElementDataMap extends Iterable<TypedElementData<?>> {
    ElementDataMap EMPTY = new ElementDataMap() {
        @Nullable
        @Override
        public <T> T get(ElementDataType<? extends T> p_331168_) {
            return null;
        }

        @Override
        public Set<ElementDataType<?>> keySet() {
            return Set.of();
        }

        @Override
        public @NotNull Iterator<TypedElementData<?>> iterator() {
            return Collections.emptyIterator();
        }
    };
    Codec<ElementDataMap> CODEC = makeCodecFromMap(ElementDataType.VALUE_MAP_CODEC);

    static Codec<ElementDataMap> makeCodec(Codec<ElementDataType<?>> codec) {
        return makeCodecFromMap(Codec.dispatchedMap(codec, ElementDataType::codecOrThrow));
    }

    static Codec<ElementDataMap> makeCodecFromMap(Codec<Map<ElementDataType<?>, Object>> codec) {
        return codec.flatComapMap(ElementDataMap.Builder::buildFromMapTrusted, p_337448_ -> {
            int i = p_337448_.size();
            if (i == 0) {
                return DataResult.success(Reference2ObjectMaps.emptyMap());
            } else {
                Reference2ObjectMap<ElementDataType<?>, Object> reference2objectmap = new Reference2ObjectArrayMap<>(i);

                for (TypedElementData<?> TypedElementData : p_337448_) {
                    if (!TypedElementData.type().isTransient()) {
                        reference2objectmap.put(TypedElementData.type(), TypedElementData.value());
                    }
                }

                return DataResult.success(reference2objectmap);
            }
        });
    }

    static ElementDataMap composite(final ElementDataMap map1, final ElementDataMap map2) {
        return new ElementDataMap() {
            @Nullable
            @Override
            public <T> T get(ElementDataType<? extends T> p_330291_) {
                T t = map2.get(p_330291_);
                return t != null ? t : map1.get(p_330291_);
            }

            @Override
            public Set<ElementDataType<?>> keySet() {
                return Sets.union(map1.keySet(), map2.keySet());
            }
        };
    }

    static ElementDataMap.Builder builder() {
        return new ElementDataMap.Builder();
    }

    @Nullable
    <T> T get(ElementDataType<? extends T> component);

    Set<ElementDataType<?>> keySet();

    default boolean has(ElementDataType<?> component) {
        return this.get(component) != null;
    }

    default <T> T getOrDefault(ElementDataType<? extends T> component, T defaultValue) {
        T t = this.get(component);
        return t != null ? t : defaultValue;
    }

    @Nullable
    default <T> TypedElementData<T> getTyped(ElementDataType<T> component) {
        T t = this.get(component);
        return t != null ? new TypedElementData<>(component, t) : null;
    }

    @Override
    default @NotNull Iterator<TypedElementData<?>> iterator() {
        return Iterators.transform(this.keySet().iterator(), p_330954_ -> Objects.requireNonNull(this.getTyped((ElementDataType<?>)p_330954_)));
    }

    default Stream<TypedElementData<?>> stream() {
        return StreamSupport.stream(Spliterators.spliterator(this.iterator(), this.size(), Spliterator.DISTINCT | Spliterator.SIZED | Spliterator.NONNULL | Spliterator.IMMUTABLE), false);
    }

    default int size() {
        return this.keySet().size();
    }

    default boolean isEmpty() {
        return this.size() == 0;
    }

    default ElementDataMap filter(final Predicate<ElementDataType<?>> predicate) {
        return new ElementDataMap() {
            @Nullable
            @Override
            public <T> T get(ElementDataType<? extends T> p_341052_) {
                return predicate.test(p_341052_) ? ElementDataMap.this.get(p_341052_) : null;
            }

            @Override
            public Set<ElementDataType<?>> keySet() {
                return Sets.filter(ElementDataMap.this.keySet(), predicate::test);
            }
        };
    }

    class Builder{
        private final Reference2ObjectMap<ElementDataType<?>, Object> map = new Reference2ObjectArrayMap<>();

        Builder() {
        }

        public <T> ElementDataMap.Builder set(ElementDataType<T> component, @Nullable T value) {
            this.setUnchecked(component, value);
            return this;
        }

        public <T> ElementDataMap.Builder set(Supplier<ElementDataType<T>> supplier, @Nullable T value) {
            this.setUnchecked(supplier.get(), value);
            return this;
        }

        <T> void setUnchecked(ElementDataType<T> component, @Nullable Object value) {
            if (value != null) {
                this.map.put(component, value);
            } else {
                this.map.remove(component);
            }
        }

        public ElementDataMap.Builder addAll(ElementDataMap components) {
            for (TypedElementData<?> TypedElementData : components) {
                this.map.put(TypedElementData.type(), TypedElementData.value());
            }

            return this;
        }

        public ElementDataMap build() {
            return buildFromMapTrusted(this.map);
        }

        private static ElementDataMap buildFromMapTrusted(Map<ElementDataType<?>, Object> map) {
            if (map.isEmpty()) {
                return ElementDataMap.EMPTY;
            } else {
                return map.size() < 8
                        ? new ElementDataMap.Builder.SimpleMap(new Reference2ObjectArrayMap<>(map))
                        : new ElementDataMap.Builder.SimpleMap(new Reference2ObjectOpenHashMap<>(map));
            }
        }

        record SimpleMap(Reference2ObjectMap<ElementDataType<?>, Object> map) implements ElementDataMap {
            @Nullable
            @Override
            public <T> T get(ElementDataType<? extends T> p_331063_) {
                //noinspection unchecked
                return (T)this.map.get(p_331063_);
            }

            @Override
            public boolean has(ElementDataType<?> p_331343_) {
                return this.map.containsKey(p_331343_);
            }

            @Override
            public Set<ElementDataType<?>> keySet() {
                return this.map.keySet();
            }

            @Override
            public @NotNull Iterator<TypedElementData<?>> iterator() {
                return Iterators.transform(Reference2ObjectMaps.fastIterator(this.map), TypedElementData::fromEntryUnchecked);
            }

            @Override
            public int size() {
                return this.map.size();
            }

            @Override
            public String toString() {
                return this.map.toString();
            }
        }
    }
}

