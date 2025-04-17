package mames1.net.mamesosu.object;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Setting {

    String secretKey;
    String domain;

    // 自動キックするプレイヤー
    List<String> blacklist;

    public Setting () {
        blacklist = new ArrayList<>();
        Dotenv dotenv = Dotenv.configure().load();

        this.secretKey = dotenv.get("SECRET_KEY");
        this.domain = dotenv.get("DOMAIN");
        this.blacklist = List.of(dotenv.get("BLACKLIST").split(","));
    }
}
