package me.korbsti.soaromaac.utils;

import me.korbsti.soaromaac.Main;

public class GetType {
    Main plugin;

    public GetType(Main instance) {
        this.plugin = instance;
    }


    public String returnType(String str) {
        if (str.equals("false") || str.equals("true")) {
            return "Boolean";
        }
        try {
            double x = Double.valueOf(str);
            return "Double";
        } catch (Exception e) {
        }

        try {
            int x = Integer.valueOf(str);
            return "Integer";
        } catch (Exception e) {
        }

        return "String";
    }


}
