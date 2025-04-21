package mames1.net.mamesosu;

import mames1.net.mamesosu.object.Bot;
import mames1.net.mamesosu.object.DataBase;

public class Main {

    public static Bot bot;
    public static DataBase dataBase;

    public static void main(String[] args) {

        bot = new Bot();
        dataBase = new DataBase();
        bot.load();
    }
}