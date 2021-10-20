package com.mrjoshuat.coppergolem;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;

public interface OxidizableBlockCallback {
    Event<OxidizableBlockCallback> EVENT = EventFactory.createArrayBacked(OxidizableBlockCallback.class,
            (listeners) -> () -> {
                for (OxidizableBlockCallback listener : listeners) {
                    ActionResult result = listener.randomTick();

                    if(result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            });

    ActionResult randomTick();
}
