package com.jeff.ac.model;

import java.util.ArrayList;
import java.util.List;

public class Setting {

    private int mSettingId = -1;
    private String mSettingName = "";
    private List<Actor> mCharacterList = new ArrayList<Actor>();

    public Setting () {
    }

    public int getSettingId() {
        return mSettingId;
    }

    public void setSettingId(int settingId) {
        this.mSettingId = settingId;
    }

    public String getSettingName() {
        return mSettingName;
    }

    public void setSettingName(String settingName) {
        this.mSettingName = settingName;
    }

    public List<Actor> getCharacterList() {
        return mCharacterList;
    }

    public boolean hasCharacterList() {
        return !this.mCharacterList.isEmpty();
    }

    public void setCharacterList(List<Actor> characterList) {
        this.mCharacterList = characterList;
    }

    public int getCharacterArrayIndex(String character) {
        for (int i = 0; i < mCharacterList.size(); i++) {
            if (mCharacterList.get(i).getCharacterName().compareToIgnoreCase(character) == 0)
                return i;
        }
        return -1;
    }

    @Override
    public String toString() {
        String setting = "id=" + mSettingId + ";name=" + mSettingName + ";characters=";

        int countCharac = 0;
        for (Actor chr : mCharacterList) {
            setting = setting.concat(chr.toString());

            if (countCharac < mCharacterList.size() - 1)
                setting = setting.concat("-");

            countCharac++;
        }

        return setting;
    }
}