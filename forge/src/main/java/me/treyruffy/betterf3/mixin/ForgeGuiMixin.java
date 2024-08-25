package me.treyruffy.betterf3.mixin;

import java.util.List;
import me.cominixo.betterf3.config.GeneralOptions;
import me.cominixo.betterf3.utils.DebugRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Forge InGame GUI Mixin.
 */
@Mixin(DebugScreenOverlay.class)
public abstract class ForgeGuiMixin {

  @Shadow @Final
  private Minecraft minecraft;
  @Shadow @Final private Font font;

  /**
   * Gets the information on the left side of the screen.
   *
   * @return the game information
   */
  @SuppressWarnings("checkstyle:MethodName")
  @Shadow protected abstract List<String> getGameInformation();

  /**
   * Gets the information on the right side of the screen.
   *
   * @return the system information
   */
  @SuppressWarnings("checkstyle:MethodName")
  @Shadow protected abstract List<String> getSystemInformation();

  /**
   * Renders the text on the left side of the screen.
   *
   * @param guiGraphics GUI Graphics
   * @param ci Callback info
   */
  @Inject(method = "drawGameInformation", at = @At("HEAD"), cancellable = true)
  public void drawLeftText(final GuiGraphics guiGraphics, final CallbackInfo ci) {

    if (GeneralOptions.disableMod) {
      return;
    }

    final List<Component> list = DebugRenderer.newText(this.minecraft, true, this.getGameInformation(), this.getSystemInformation());

    DebugRenderer.drawLeftText(list, guiGraphics, this.minecraft, this.font, null);

    ci.cancel();
  }

  /**
   * Renders the text on the right side of the screen.
   *
   * @param guiGraphics GUI Graphics
   * @param ci Callback info
   */
  @Inject(method = "drawSystemInformation", at = @At("HEAD"), cancellable = true)
  public void drawRightText(final GuiGraphics guiGraphics, final CallbackInfo ci) {

    if (GeneralOptions.disableMod) {
      return;
    }

    final List<Component> list = DebugRenderer.newText(this.minecraft, false, this.getGameInformation(), this.getSystemInformation());

    DebugRenderer.drawRightText(list, guiGraphics, this.minecraft, this.font, null);

    ci.cancel();
  }

}
