package me.googas.starbox.commands;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.NonNull;
import me.googas.commands.annotations.Free;
import me.googas.commands.annotations.Multiple;
import me.googas.commands.annotations.Parent;
import me.googas.commands.annotations.Required;
import me.googas.commands.bukkit.annotations.Command;
import me.googas.commands.bukkit.result.Result;
import me.googas.io.StarboxFile;
import me.googas.reflect.wrappers.WrappedClass;
import me.googas.reflect.wrappers.WrappedConstructor;
import me.googas.reflect.wrappers.chat.WrappedText;
import me.googas.starbox.Starbox;
import me.googas.starbox.StarboxBukkitFiles;
import me.googas.starbox.utility.Versions;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.entity.Player;

/**
 * Commands to create {@link BaseComponent} using {@link ComponentBuilder}. Javadoc warnings are
 * suppressed as commands already have a description and usage.
 */
@SuppressWarnings("JavaDoc")
public class ComponentBuilderCommands {

  @NonNull
  private static final WrappedClass<HoverEvent> HOVER_EVENT = WrappedClass.of(HoverEvent.class);

  @NonNull
  private static final WrappedConstructor<HoverEvent> HOVER_EVENT_CONSTRUCTOR =
      ComponentBuilderCommands.HOVER_EVENT.getConstructor(
          HoverEvent.Action.class, BaseComponent[].class);

  @NonNull private final Map<UUID, ComponentBuilder> builders = new HashMap<>();

  @Parent
  @Command(
      aliases = {"componentBuilder", "cb"},
      description = "Helps with the construction of Chat Components",
      permission = "starbox.component-builder")
  public Result componentBuilder(Player player) {
    return new Result(this.getBuilder(player).create());
  }

  @Command(
      aliases = "reset",
      description = "Reset the builder",
      permission = "starbox.component-builder")
  public Result reset(Player player) {
    builders.remove(player.getUniqueId());
    return new Result("&7Your builder has been reset");
  }

  @Command(
      aliases = "see",
      description = "See how a text component would look like using color codes",
      permission = "starbox.component-builder")
  public Result see(
      Player player,
      @Required(name = "text", description = "The text to test") @Multiple String text) {
    return new Result(text);
  }

  @Command(
      aliases = "space",
      description = "Appends spaces in the builder",
      permission = "starbox.component-builder")
  public Result space(
      Player player,
      @Free(
              name = "number",
              description = "The number of spaces to add",
              suggestions = {"1", "2", "3"})
          int spaces) {
    if (spaces < 1) {
      return new Result("&cNumber cannot be less than 1");
    } else {
      ComponentBuilder builder = this.getBuilder(player);
      for (int i = 0; i < spaces; i++) {
        builder.append(" ");
      }
      return new Result("&7Spaces have been appended");
    }
  }

  @Command(
      aliases = "text",
      description = "Append some text to the component and decide the format retention",
      permission = "starbox.component-builder")
  public Result text(
      Player player,
      @Required(name = "") ComponentBuilder.FormatRetention retention,
      @Required(name = "text", description = "The text to append") @Multiple String text) {
    this.getBuilder(player).append(text, retention);
    return new Result("&7Text has been appended to your builder");
  }

  @Command(
      aliases = "append",
      description = "Append some text to the component",
      permission = "starbox.component-builder")
  public Result append(
      Player player,
      @Required(name = "text", description = "The text to append") @Multiple String text) {
    this.getBuilder(player).append(text);
    return new Result("&7Text has been appended to your builder");
  }

  @Command(
      aliases = "color",
      description = "Set the color for a current part",
      permission = "starbox.component-builder")
  public Result color(
      Player player, @Required(name = "color", description = "The color to set") ChatColor color) {
    this.getBuilder(player).color(color);
    return new Result("&7Color has been applied to your builder");
  }

  @Command(
      aliases = "click",
      description = "Adds a click event to the current part",
      permission = "starbox.component-builder")
  public Result click(
      Player player,
      @Required(name = "action", description = "The action of the event") ClickEvent.Action action,
      @Multiple @Required(name = "value", description = "The value of the action for the event")
          String value) {
    this.getBuilder(player).event(new ClickEvent(action, value));
    return new Result("&7Click event has been added");
  }

  @Command(
      aliases = "hover",
      description = "Adds a hover event to the current part",
      permission = "starbox.component-builder")
  public Result hover(
      Player player,
      @Required(name = "name", description = "The name of the component to import to set as value")
          String name) {
    HoverEvent.Action action = HoverEvent.Action.SHOW_TEXT;
    StarboxFile file =
        new StarboxFile(StarboxBukkitFiles.EXPORTS, name.endsWith(".json") ? name : name + ".json");
    ComponentBuilder builder = this.importBuilder(file);
    if (file.exists()) {
      if (Versions.BUKKIT < 16) {
        this.getBuilder(player)
            .event(
                ComponentBuilderCommands.HOVER_EVENT_CONSTRUCTOR
                    .invoke(action, builder.create())
                    .handle(Starbox::severe)
                    .provide()
                    .orElseThrow(IllegalStateException::new));
      } else {
        this.getBuilder(player)
            .event(new HoverEvent(action, new WrappedText(builder.create()).getText()));
      }
      return new Result("&7Hover event has been set");
    }
    return new Result("&cFile {0} does not exist", file);
  }

  @Command(
      aliases = "export",
      description = "Export your current builder",
      permission = "starbox.component-builder")
  public Result export(
      Player player,
      @Required(name = "name", description = "The name of the exported file") String name) {
    StarboxFile file =
        new StarboxFile(StarboxBukkitFiles.EXPORTS, name.endsWith(".json") ? name : name + ".json");
    boolean exported =
        StarboxBukkitFiles.Contexts.TXT
            .write(file, ComponentSerializer.toString(this.getBuilder(player).create()), true)
            .handle(Starbox::severe)
            .provide()
            .orElse(false);
    if (exported) {
      return new Result("&7Successfully exported builder to: &b{0}", file);
    } else {
      return new Result("&7Could not export current builder");
    }
  }

  @Command(
      aliases = "import",
      description = "Import a builder",
      permission = "starbox.component-builder")
  public Result importBuilder(
      Player player,
      @Required(name = "name", description = "The name of the file to import") String name) {
    StarboxFile file =
        new StarboxFile(StarboxBukkitFiles.EXPORTS, name.endsWith(".json") ? name : name + ".json");
    if (file.exists()) {
      this.builders.put(player.getUniqueId(), this.importBuilder(file));
      return new Result("&7Successfully loaded builder");
    } else {
      return new Result("&cFile {0} does not exist", file);
    }
  }

  @NonNull
  private ComponentBuilder importBuilder(@NonNull StarboxFile file) {
    ComponentBuilder builder = new ComponentBuilder("");
    BaseComponent[] parts =
        ComponentSerializer.parse(
            file.read(StarboxBukkitFiles.Contexts.TXT)
                .handle(Starbox::severe)
                .provide()
                .orElse("[]"));
    for (BaseComponent part : parts) {
      builder.append(part.toLegacyText());
    }
    return builder;
  }

  @NonNull
  private ComponentBuilder getBuilder(@NonNull Player player) {
    return this.builders.computeIfAbsent(player.getUniqueId(), uuid -> new ComponentBuilder(""));
  }
}
