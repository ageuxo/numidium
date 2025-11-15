package org.ageuxo.numidium.element.data;

import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMaps;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ElementDataPatch {
    public static final ElementDataPatch EMPTY = new ElementDataPatch(Reference2ObjectMaps.emptyMap());
    public static final Codec<ElementDataPatch> CODEC = Codec.<ElementDataPatch.PatchKey, Object>dispatchedMap(ElementDataPatch.PatchKey.CODEC, ElementDataPatch.PatchKey::valueCodec)
            .xmap(map -> {
                if (map.isEmpty()) {
                    return EMPTY;
                } else {
                    Reference2ObjectMap<ElementDataType<?>, Optional<?>> reference2objectmap = new Reference2ObjectArrayMap<>(map.size());

                    for (Map.Entry<ElementDataPatch.PatchKey, ?> entry : map.entrySet()) {
                        ElementDataPatch.PatchKey ElementDatapatch$patchkey = entry.getKey();
                        if (ElementDatapatch$patchkey.removed()) {
                            reference2objectmap.put(ElementDatapatch$patchkey.type(), Optional.empty());
                        } else {
                            reference2objectmap.put(ElementDatapatch$patchkey.type(), Optional.of(entry.getValue()));
                        }
                    }

                    return new ElementDataPatch(reference2objectmap);
                }
            }, patch -> {
                Reference2ObjectMap<ElementDataPatch.PatchKey, Object> reference2objectmap = new Reference2ObjectArrayMap<>(patch.map.size());

                for (Map.Entry<ElementDataType<?>, Optional<?>> entry : Reference2ObjectMaps.fastIterable(patch.map)) {
                    ElementDataType<?> ElementDatatype = entry.getKey();
                    if (!ElementDatatype.isTransient()) {
                        Optional<?> optional = entry.getValue();
                        if (optional.isPresent()) {
                            reference2objectmap.put(new ElementDataPatch.PatchKey(ElementDatatype, false), optional.get());
                        } else {
                            reference2objectmap.put(new ElementDataPatch.PatchKey(ElementDatatype, true), Unit.INSTANCE);
                        }
                    }
                }

                return reference2objectmap;
            });
    public static final StreamCodec<RegistryFriendlyByteBuf, ElementDataPatch> STREAM_CODEC = new StreamCodec<>() {
        public ElementDataPatch decode(RegistryFriendlyByteBuf buffer) {
            int i = buffer.readVarInt();
            int j = buffer.readVarInt();
            if (i == 0 && j == 0) {
                return ElementDataPatch.EMPTY;
            } else {
                int k = i + j;
                Reference2ObjectMap<ElementDataType<?>, Optional<?>> reference2objectmap = new Reference2ObjectArrayMap<>(Math.min(k, 65536));

                for (int l = 0; l < i; l++) {
                    ElementDataType<?> ElementDatatype = ElementDataType.STREAM_CODEC.decode(buffer);
                    Object object = ElementDatatype.streamCodec().decode(buffer);
                    reference2objectmap.put(ElementDatatype, Optional.of(object));
                }

                for (int i1 = 0; i1 < j; i1++) {
                    ElementDataType<?> ElementDatatype1 = ElementDataType.STREAM_CODEC.decode(buffer);
                    reference2objectmap.put(ElementDatatype1, Optional.empty());
                }

                return new ElementDataPatch(reference2objectmap);
            }
        }

        public void encode(RegistryFriendlyByteBuf buffer, ElementDataPatch value) {
            if (value.isEmpty()) {
                buffer.writeVarInt(0);
                buffer.writeVarInt(0);
            } else {
                int i = 0;
                int j = 0;

                for (it.unimi.dsi.fastutil.objects.Reference2ObjectMap.Entry<ElementDataType<?>, Optional<?>> entry : Reference2ObjectMaps.fastIterable(
                        value.map
                )) {
                    if (entry.getValue().isPresent()) {
                        i++;
                    } else {
                        j++;
                    }
                }

                buffer.writeVarInt(i);
                buffer.writeVarInt(j);

                for (it.unimi.dsi.fastutil.objects.Reference2ObjectMap.Entry<ElementDataType<?>, Optional<?>> entry1 : Reference2ObjectMaps.fastIterable(
                        value.map
                )) {
                    Optional<?> optional = entry1.getValue();
                    if (optional.isPresent()) {
                        ElementDataType<?> ElementDatatype = entry1.getKey();
                        ElementDataType.STREAM_CODEC.encode(buffer, ElementDatatype);
                        encodeComponent(buffer, ElementDatatype, optional.get());
                    }
                }

                for (it.unimi.dsi.fastutil.objects.Reference2ObjectMap.Entry<ElementDataType<?>, Optional<?>> entry2 : Reference2ObjectMaps.fastIterable(
                        value.map
                )) {
                    if (entry2.getValue().isEmpty()) {
                        ElementDataType<?> ElementDatatype1 = entry2.getKey();
                        ElementDataType.STREAM_CODEC.encode(buffer, ElementDatatype1);
                    }
                }
            }
        }

        private static <T> void encodeComponent(RegistryFriendlyByteBuf buffer, ElementDataType<T> component, Object value) {
            //noinspection unchecked
            component.streamCodec().encode(buffer, (T) value);
        }
    };
    private static final String REMOVED_PREFIX = "!";
    final Reference2ObjectMap<ElementDataType<?>, Optional<?>> map;

    ElementDataPatch(Reference2ObjectMap<ElementDataType<?>, Optional<?>> map) {
        this.map = map;
    }

    public static ElementDataPatch.Builder builder() {
        return new ElementDataPatch.Builder();
    }

    @Nullable
    public <T> Optional<? extends T> get(ElementDataType<? extends T> component) {
        //noinspection unchecked
        return (Optional<? extends T>)this.map.get(component);
    }

    public Set<Map.Entry<ElementDataType<?>, Optional<?>>> entrySet() {
        return this.map.entrySet();
    }

    public int size() {
        return this.map.size();
    }

    public ElementDataPatch forget(Predicate<ElementDataType<?>> predicate) {
        if (this.isEmpty()) {
            return EMPTY;
        } else {
            Reference2ObjectMap<ElementDataType<?>, Optional<?>> reference2objectmap = new Reference2ObjectArrayMap<>(this.map);
            reference2objectmap.keySet().removeIf(predicate);
            return reference2objectmap.isEmpty() ? EMPTY : new ElementDataPatch(reference2objectmap);
        }
    }

    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    public ElementDataPatch.SplitResult split() {
        if (this.isEmpty()) {
            return ElementDataPatch.SplitResult.EMPTY;
        } else {
            ElementDataMap.Builder ElementDatamap$builder = ElementDataMap.builder();
            Set<ElementDataType<?>> set = Sets.newIdentityHashSet();
            this.map.forEach((type, value) -> {
                if (value.isPresent()) {
                    ElementDatamap$builder.setUnchecked((ElementDataType<?>)type, value.get());
                } else {
                    set.add(type);
                }
            });
            return new ElementDataPatch.SplitResult(ElementDatamap$builder.build(), set);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else {
            if (other instanceof ElementDataPatch ElementDatapatch && this.map.equals(ElementDatapatch.map)) {
                return true;
            }

            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.map.hashCode();
    }

    @Override
    public String toString() {
        return toString(this.map);
    }

    static String toString(Reference2ObjectMap<ElementDataType<?>, Optional<?>> map) {
        StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append('{');
        boolean flag = true;

        for (Map.Entry<ElementDataType<?>, Optional<?>> entry : Reference2ObjectMaps.fastIterable(map)) {
            if (flag) {
                flag = false;
            } else {
                stringbuilder.append(", ");
            }

            Optional<?> optional = entry.getValue();
            if (optional.isPresent()) {
                stringbuilder.append(entry.getKey());
                stringbuilder.append("=>");
                stringbuilder.append(optional.get());
            } else {
                stringbuilder.append(REMOVED_PREFIX);
                stringbuilder.append(entry.getKey());
            }
        }

        stringbuilder.append('}');
        return stringbuilder.toString();
    }

    public static class Builder {
        private final Reference2ObjectMap<ElementDataType<?>, Optional<?>> map = new Reference2ObjectArrayMap<>();

        Builder() {
        }

        public <T> ElementDataPatch.Builder set(ElementDataType<T> component, T value) {
//            net.neoforged.neoforge.common.CommonHooks.validateComponent(value); TODO maybe implement this
            this.map.put(component, Optional.of(value));
            return this;
        }

        public <T> ElementDataPatch.Builder remove(ElementDataType<T> component) {
            this.map.put(component, Optional.empty());
            return this;
        }

        public <T> ElementDataPatch.Builder set(TypedElementData<T> component) {
            return this.set(component.type(), component.value());
        }

        public ElementDataPatch build() {
            return this.map.isEmpty() ? ElementDataPatch.EMPTY : new ElementDataPatch(this.map);
        }
    }

    record PatchKey(ElementDataType<?> type, boolean removed) {
        public static final Codec<ElementDataPatch.PatchKey> CODEC = Codec.STRING
                .flatXmap(
                        p_330929_ -> {
                            boolean flag = p_330929_.startsWith(REMOVED_PREFIX);
                            if (flag) {
                                p_330929_ = p_330929_.substring(REMOVED_PREFIX.length());
                            }

                            ResourceLocation resourcelocation = ResourceLocation.tryParse(p_330929_);
                            ElementDataType<?> ElementDatatype = ElementDataTypes.REGISTRY.get(resourcelocation);
                            if (ElementDatatype == null) {
                                return DataResult.error(() -> "No component with type: '" + resourcelocation + "'");
                            } else {
                                return ElementDatatype.isTransient()
                                        ? DataResult.error(() -> "'" + resourcelocation + "' is not a persistent component")
                                        : DataResult.success(new ElementDataPatch.PatchKey(ElementDatatype, flag));
                            }
                        },
                        p_339345_ -> {
                            ElementDataType<?> ElementDatatype = p_339345_.type();
                            ResourceLocation resourcelocation = ElementDataTypes.REGISTRY.getKey(ElementDatatype);
                            return resourcelocation == null
                                    ? DataResult.error(() -> "Unregistered component: " + ElementDatatype)
                                    : DataResult.success(p_339345_.removed() ? REMOVED_PREFIX + resourcelocation : resourcelocation.toString());
                        }
                );

        public Codec<?> valueCodec() {
            return this.removed ? Codec.EMPTY.codec() : this.type.codecOrThrow();
        }
    }

    public record SplitResult(ElementDataMap added, Set<ElementDataType<?>> removed) {
        public static final ElementDataPatch.SplitResult EMPTY = new ElementDataPatch.SplitResult(ElementDataMap.EMPTY, Set.of());
    }
}
