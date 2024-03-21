package me.cominixo.betterf3.modules;

import me.cominixo.betterf3.utils.DebugLine;
import me.cominixo.betterf3.utils.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.TextColor;

/**
 * The Help module.
 */
public class HelpModule extends BaseModule {

  /**
   * Default enabled color.
   */
  public final TextColor defaultEnabledColor = TextColor.fromLegacyFormat(ChatFormatting.GREEN);

  /**
   * Default disabled color.
   */
  public final TextColor defaultDisabledColor = TextColor.fromLegacyFormat(ChatFormatting.RED);

  /**
   * Enabled color.
   */
  public TextColor enabledColor;

  /**
   * Disabled color.
   */
  public TextColor disabledColor;

  /**
   * Instantiates a new Help module.
   */
  public HelpModule() {
    this.defaultNameColor = TextColor.fromRgb(0xfdfd96);
    this.defaultValueColor = TextColor.fromLegacyFormat(ChatFormatting.AQUA);

    this.nameColor = defaultNameColor;
    this.valueColor = defaultValueColor;
    this.enabledColor = this.defaultEnabledColor;
    this.disabledColor = this.defaultDisabledColor;

    lines.add(new DebugLine("pie_graph_new"));
    lines.add(new DebugLine("fps_tps_new"));
    lines.add(new DebugLine("ping"));
    lines.add(new DebugLine("help"));

    for (final DebugLine line : lines) {
      line.inReducedDebug = true;
    }
  }

  /**
   * Updates the Help module.
   *
   * @param client the Minecraft client
   */
  public void update(final Minecraft client) {

    final String visible = I18n.get("text.betterf3.line.visible");
    final String hidden = I18n.get("text.betterf3.line.hidden");

    // Pie Graph (F3+1)
    lines.get(0).value(client.getDebugOverlay().showProfilerChart() ? Utils.styledText(visible, this.enabledColor)
      : Utils.styledText(hidden, this.disabledColor));

    // FPS / TPS (F3+2)
    lines.get(1).value(client.getDebugOverlay().renderFpsCharts ? Utils.styledText(visible, this.enabledColor)
      : Utils.styledText(hidden, this.disabledColor));

    // Ping / Bandwidth (F3+3)
    lines.get(2).value(client.getDebugOverlay().showNetworkCharts() ? Utils.styledText(visible, this.enabledColor)
      : Utils.styledText(hidden, this.disabledColor));

    // For help
    lines.get(3).value(I18n.get("text.betterf3.line.help_press"));
  }
}
