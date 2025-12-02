package mames1.net.mamesosu;

import mames1.net.mamesosu.object.Bot;
import mames1.net.mamesosu.object.Cache;

public class Main {

    public static Bot bot;
    public static Cache cache;

    public static void main(String[] args) {
        bot = new Bot();
        cache = new Cache();
    }
}