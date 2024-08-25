package me.cominixo.betterf3.mixin.debugcrosshair;

import me.cominixo.betterf3.config.GeneralOptions;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Main debug crosshair removal mixin.
 */
@Mixin(value = Gui.class, priority = 1100)
public class DebugCrosshairMixin {

  @Redirect(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/DebugScreenOverlay;showDebugScreen()Z"))
  private boolean removeDebugCrosshair(final DebugScreenOverlay instance) {
    if (!GeneralOptions.disableMod && GeneralOptions.hideDebugCrosshair)
      return false;

    return instance.showDebugScreen();
  }
}
