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
 * The Debug Screen Overlay.
 */
@Mixin(DebugScreenOverlay.class)
public abstract class NeoForgeDebugMixin {

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
   * Renders the text on the screen.
   *
   * @param guiGraphics GUI Graphics
   * @param ci Callback info
   */
  @Inject(method = "method_51746", at = @At("HEAD"), cancellable = true, order = 2000)
  public void drawText(final GuiGraphics guiGraphics, final CallbackInfo ci) {

    if (GeneralOptions.disableMod) {
      return;
    }

    final List<Component> leftList = DebugRenderer.newText(this.minecraft, true, this.getGameInformation(), this.getSystemInformation());
    DebugRenderer.drawLeftText(leftList, guiGraphics, this.minecraft, this.font, null);

    final List<Component> rightList = DebugRenderer.newText(this.minecraft, false, this.getGameInformation(), this.getSystemInformation());
    DebugRenderer.drawRightText(rightList, guiGraphics, this.minecraft, this.font, null);

    ci.cancel();
  }

}
