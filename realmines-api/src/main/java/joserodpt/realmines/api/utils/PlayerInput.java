package joserodpt.realmines.api.utils;

/*
 *  ______           ____  ____
 *  | ___ \         | |  \/  (_)
 *  | |_/ /___  __ _| | .  . |_ _ __   ___  ___
 *  |    // _ \/ _` | | |\/| | | '_ \ / _ \/ __|
 *  | |\ \  __/ (_| | | |  | | | | | |  __/\__ \
 *  \_| \_\___|\__,_|_\_|  |_/_|_| |_|\___||___/
 *
 * Licensed under the MIT License
 * @author José Rodrigues © 2019-2025
 * @link https://github.com/joserodpt/RealMines
 */

import joserodpt.realmines.api.RealMinesAPI;
import joserodpt.realmines.api.config.RMLanguageConfig;
import joserodpt.realmines.api.config.TranslatableLine;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import java.util.concurrent.TimeUnit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerInput implements Listener {

    private static final Map<UUID, PlayerInput> inputs = new HashMap<>();
    private final UUID uuid;

    private final List<String> texts = Text
            .color(RMLanguageConfig.file().getStringList("System.Type-Input"));

    private final InputRunnable runGo;
    private final InputRunnable runCancel;
    private final ScheduledTask taskId;
    private boolean clearInput = true;

    public PlayerInput(final boolean clearInput, final Player p, final InputRunnable correct, final InputRunnable cancel) {
        this.uuid = p.getUniqueId();
        p.closeInventory();
        this.runGo = correct;
        this.runCancel = cancel;
        this.clearInput = clearInput;
        this.taskId = p.getScheduler().runAtFixedRate(RealMinesAPI.getInstance().getPlugin(), (ScheduledTask t) -> {
            p.sendTitle(PlayerInput.this.texts.get(0), PlayerInput.this.texts.get(1), 0, 21, 0);
        }, null, 1, 20);

        this.register();
    }

    public static Listener getListener() {
        return new Listener() {
            @EventHandler(priority = EventPriority.HIGHEST)
            public void onPlayerChat(final AsyncPlayerChatEvent event) {
                final Player p = event.getPlayer();
                final String input = event.getMessage();
                final UUID uuid = p.getUniqueId();

                if (inputs.containsKey(uuid)) {
                    event.setCancelled(true);
                    handlePlayerInput(p, input, uuid);
                }
            }
        };
    }

    private static void handlePlayerInput(final Player p, String input, final UUID uuid) {
        final PlayerInput current = inputs.get(uuid);

        if (current.clearInput) {
            input = ChatColor.stripColor(Text.color(input)).trim();
        }

        try {
            current.taskId.cancel();
            p.getScheduler().run(RealMinesAPI.getInstance().getPlugin(), (ScheduledTask t) -> p.sendTitle("", "", 0, 1, 0), null);
            current.unregister();
            String cleanInput = ChatColor.stripColor(Text.color(input));
            if (input.equalsIgnoreCase("cancel")) {
                TranslatableLine.SYSTEM_INPUT_CANCELLED.send(p);
                p.getScheduler().runDelayed(RealMinesAPI.getInstance().getPlugin(), (ScheduledTask t) -> current.runCancel.run(cleanInput), null, 3);
            } else {
                p.getScheduler().runDelayed(RealMinesAPI.getInstance().getPlugin(), (ScheduledTask t) -> current.runGo.run(cleanInput), null, 3);
            }
        } catch (final Exception e) {
            TranslatableLine.SYSTEM_ERROR_OCCURRED.send(p);
            RealMinesAPI.getInstance().getPlugin().getLogger().warning(e.getMessage());
        }
    }

    private void register() {
        inputs.put(this.uuid, this);
    }

    private void unregister() {
        inputs.remove(this.uuid);
    }

    @FunctionalInterface
    public interface InputRunnable {
        void run(String input);
    }
}
