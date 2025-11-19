package mames1.net.mamesosu.listener;

import mames1.net.mamesosu.server.monitor.ServerHealthMonitor;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class BotReady extends ListenerAdapter {

    // 起動時にサーバーヘルスモニタリングを開始する
    @Override
    public void onReady(@NotNull ReadyEvent e) {
        ServerHealthMonitor serverHealth = new ServerHealthMonitor();
        serverHealth.startMonitoring();
    }
}
