package mames1.net.mamesosu.utils;

import java.util.ArrayList;

public abstract class Mods {

    public static String getModsToString(int n) {
        ArrayList<String> mod = new ArrayList<>();
        final String[] mods = {"NF", "EZ", "TS", "HD", "HR", "SD", "DT", "RX", "HT", "NC", "FL", "", "SO", "AP", "PF", "4K", "5K", "6K", "7K", "8K", "FD", "RD", "CM", "TG", "9K", "KC", "1K", "3K", "2K", "V2", "MR"};
        StringBuilder rMods = new StringBuilder();

        for (int i = 30; i >= 0; i--) {
            if (i != 2 && i != 11 && n >= Math.pow(2, i)) {
                switch (i) {
                    case 14 -> n -= (int) Math.pow(2, 5);
                    case 9 -> n -= (int) Math.pow(2, 6);
                }
                mod.add(mods[i]);
                n -= (int) Math.pow(2, i);
            }
        }

        for (String s : mod) {
            rMods.append(s);
        }

        if(!rMods.toString().isEmpty()) {
            return rMods.toString();
        } else {
            return "NM";
        }
    }
}
