package mames1.net.mamesosu.object;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;

@Getter
public class Server {

    String privateKey;

    public Server () {
        Dotenv dotenv = Dotenv.configure().load();

        privateKey = dotenv.get("PRIVATE_KEY");
    }
}
