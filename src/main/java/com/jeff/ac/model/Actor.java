package com.jeff.ac.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class Actor {
    private int characterId = -1;
    private String characterName = "";
    private Map<String, Integer> wordCounts = new HashMap<String, Integer>();

    public Actor(String name) {
        this.characterName = name;
    }

    public int getCharacterId() {
        return characterId;
    }

    public void setCharacterId(int characterId) {
        this.characterId = characterId;
    }

    public String getCharacterName() {
        return characterName;
    }

    public void setCharacterName(String characterName) {
        this.characterName = characterName;
    }

    public Map<String, Integer> getWordCounts() {
        return wordCounts;
    }

    public void setWordCounts(Map<String, Integer> wordCounts) {
        this.wordCounts = wordCounts;
    }

    public boolean hasDialogs() {
        return !this.wordCounts.isEmpty();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Actor) {
            if (((Actor) obj).getCharacterName().compareToIgnoreCase(this.characterName) == 0)
                return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.characterName.hashCode();
    }

    @Override
    public String toString() {
        String str = String.format("%s ;(%d)words(", this.characterName, wordCounts.size());

        Iterator<Entry<String, Integer>> it = wordCounts.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Integer> pair = it.next();
            str = str.concat(pair.getKey() + " = " + pair.getValue());
            if (it.hasNext())
                str = str.concat(",");
        }
        str = str.concat(")");

        return str;
    }
}