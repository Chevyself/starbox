package me.googas.starbox.modules.channels;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import me.googas.commands.bukkit.utils.BukkitUtils;
import me.googas.commands.bungee.utils.Components;
import me.googas.reflect.packet.PacketType;
import me.googas.reflect.wrappers.chat.WrappedChatComponent;
import me.googas.reflect.wrappers.packet.WrappedTitleAction;
import me.googas.starbox.BukkitLanguage;
import me.googas.starbox.utility.Versions;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/** A channel that is used to send data to a player. */
public class PlayerChannel implements Channel {

  @NonNull private final UUID uuid;

  /**
   * Start the channel.
   *
   * @param uuid the unique id of the player
   */
  protected PlayerChannel(@NonNull UUID uuid) {
    this.uuid = uuid;
  }

  /**
   * Get the unique id of the player.
   *
   * @return the unique id
   */
  @NonNull
  public UUID getUniqueId() {
    return uuid;
  }

  /**
   * Get the player.
   *
   * @return a {@link Optional} holding the nullable player
   */
  @NonNull
  public Optional<Player> getPlayer() {
    return Optional.ofNullable(Bukkit.getPlayer(uuid));
  }

  @Override
  @NonNull
  public PlayerChannel send(@NonNull BaseComponent... components) {
    this.getPlayer().ifPresent(player -> BukkitUtils.send(player, components));
    return this;
  }

  @Override
  @NonNull
  public PlayerChannel send(@NonNull String text) {
    this.getPlayer().ifPresent(player -> player.sendMessage(text));
    return this;
  }

  @Override
  public @NonNull PlayerChannel sendTitle(
      String title, String subtitle, int fadeIn, int stay, int fadeOut) {
    this.getPlayer()
        .ifPresent(
            player -> {
              if (Versions.BUKKIT > 11) {
                player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
              } else {
                PacketType type = PacketType.Play.ClientBound.TITLE;
                if (title != null) {
                  type.create()
                      .setField(0, WrappedTitleAction.TITLE)
                      .setField(1, WrappedChatComponent.of(Components.deserializePlain('&', title)))
                      .setField(2, -1)
                      .setField(3, -1)
                      .setField(4, -1)
                      .send(player);
                }
                if (subtitle != null) {
                  type.create()
                      .setField(0, WrappedTitleAction.SUBTITLE)
                      .setField(
                          1, WrappedChatComponent.of(Components.deserializePlain('&', subtitle)))
                      .setField(2, -1)
                      .setField(3, -1)
                      .setField(4, -1)
                      .send(player);
                }
                type.create()
                    .setField(0, WrappedTitleAction.TIMES)
                    .setField(2, fadeIn)
                    .setField(3, stay)
                    .setField(4, fadeOut)
                    .send(player);
              }
            });
    return this;
  }

  @Override
  public @NonNull Channel setTabList(String header, String bottom) {
    this.getPlayer()
        .ifPresent(
            player -> {
              if (Versions.BUKKIT < 13) {
                PacketType.Play.ClientBound.HEADER_FOOTER
                    .create()
                    .setField(
                        2,
                        header == null
                            ? null
                            : WrappedChatComponent.of(Components.deserializePlain('&', header)))
                    .setField(
                        3,
                        bottom == null
                            ? null
                            : WrappedChatComponent.of(Components.deserializePlain('&', bottom)))
                    .send(player);
              } else {
                player.setPlayerListHeaderFooter(header, bottom);
              }
            });
    return this;
  }

  @Override
  public Optional<Locale> getLocale() {
    return this.getPlayer().map(BukkitLanguage::getLocale);
  }
}
