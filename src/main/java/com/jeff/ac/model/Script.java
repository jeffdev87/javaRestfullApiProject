package com.jeff.ac.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Script {
    private int scriptId = -1;
    private String scriptName = "";
    private List<Setting> mSettingList = new ArrayList<Setting>();

    public Script (String name) {
        this.scriptName = name;
    }

    public int getScriptId() {
        return scriptId;
    }

    public void setScriptId(int scriptId) {
        this.scriptId = scriptId;
    }

    public String getScriptName() {
        return scriptName;
    }

    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }

    public List<Setting> getmSettingList() {
        return mSettingList;
    }

    public void setmSettingList(List<Setting> mSettingList) {
        this.mSettingList = mSettingList;
    }

    public Set<String> getAllCharactersName () {
        Set<String> characterList = new HashSet<String>();

        for (int i = 0; i < mSettingList.size(); i++) {
            for (Character ch : mSettingList.get(i).getCharacterList()) {
                characterList.add(ch.getCharacterName());
            }
        }

        return characterList;
    }

    @Override
    public String toString () {
        String scriptStr = "";

        for (Setting s : mSettingList) {
            scriptStr = scriptStr.concat(s.toString() + "\n");
        }
        return scriptStr;
    }
}
