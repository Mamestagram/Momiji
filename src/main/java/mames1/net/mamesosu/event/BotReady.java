package mames1.net.mamesosu.event;

import mames1.net.mamesosu.server.monitor.ServerHealth;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class BotReady extends ListenerAdapter {

    // 起動時にサーバーヘルスモニタリングを開始する
    @Override
    public void onReady(@NotNull ReadyEvent e) {
        ServerHealth serverHealth = new ServerHealth();
        serverHealth.startMonitoring();
    }
}
