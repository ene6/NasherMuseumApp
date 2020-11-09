package com.example.myapplication;

import java.util.HashMap;
import java.util.Map;

public class Users {
    Map<String, String[]> users = new HashMap<String, String[]>();

    private static Users instance;

    public static Users getInstance() {
        if (instance == null)
            instance = new Users();
        return instance;
    }

    private Users() {
    }

    private String val;

    public String getValue() {
        return val;
    }

    public String getPass() {
        return users.get(val)[0];
    }

    public String getFirst() {
        return users.get(val)[1];
    }

    public String getLast() {
        return users.get(val)[2];
    }

    public void setValue(String value) {
        this.val = value.toLowerCase();
        users.put("ayc21", new String[]{"OCEAN", "Annabelle", "Chu"});
        users.put("dipster", new String[]{"OCEAN", "Alan", "Dippy"});
        users.put("brad", new String[]{"OCEAN", "Brad", "Johnson"});
        users.put("pwk2", new String[]{"OCEAN", "Patrick", "Krivacka"});
        users.put("ms402", new String[]{"OCEAN", "Michelle", "Seymour"});
        users.put("ecr33", new String[]{"OCEAN", "Ellen", "Raimond"});
        users.put("kw108", new String[]{"OCEAN", "Kelly", "Woolbright"});
        users.put("ln35", new String[]{"OCEAN", "Lee", "Nisbet"});
        users.put("az94", new String[]{"OCEAN", "Aaron", "Zalonis"});
        users.put("bbh12", new String[]{"OCEAN", "Bryan", "Hilley"});
    }


}
