package org.ageuxo.numidium;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;

@Mod(Numidium.MODID)
public class Numidium {
    public static final String MODID = "numidium";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Numidium(IEventBus modEventBus, ModContainer modContainer) {

    }

}
