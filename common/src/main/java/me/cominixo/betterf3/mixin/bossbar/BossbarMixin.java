package me.cominixo.betterf3.mixin.bossbar;

import me.cominixo.betterf3.config.GeneralOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.BossHealthOverlay;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to cancel bossbar rendering during F3.
 */
@Mixin(BossHealthOverlay.class)
public class BossbarMixin {

  @Shadow @Final private Minecraft minecraft;

  /**
   * Cancels bossbar rendering during F3 if the {@link GeneralOptions#hideBossbar} setting is true.
   *
   * @param info Callback info
   */
  @Inject(at = @At("HEAD"), method = "render", cancellable = true)
  public void init(final CallbackInfo info) {
    if (GeneralOptions.hideBossbar && this.minecraft.getDebugOverlay().showDebugScreen()) {
      info.cancel();
    }
  }

}
