package com.jeff.ac.parser;

import java.text.ParseException;

import com.jeff.ac.appMessages.ApplicationMessages;
import com.jeff.ac.model.Character;
import com.jeff.ac.model.Script;
import com.jeff.ac.model.Setting;

public class ScriptParser {

    private String mScript;

    enum DialogIdent {

        CHARACTER_NAME(22), DESCRIPTION(15), DIALOG(10), NO_DIALOG_IDENT(-1);

        private int ident;

        DialogIdent (int ident) {
            this.ident = ident;
        }

        public int getIdent () {
            return ident;
        }
    };

    private static final String REMOVE_SPEC_CHAR_REGEX = "[^\\dA-Za-z ]";

    public ScriptParser (String strScript) {
        this.setScript(strScript);
    }

    public String getScript () {
        return this.mScript;
    }

    public void setScript (String strScript) {
        this.mScript = strScript;
    }

    public Script parseScript (String scriptName) throws ParseException {
        System.out.println("* begin parseScript...");

        String[] lines = this.mScript.split("\\n");

        int lineCounter = 0;
        Script script = new Script (scriptName);

        while (lineCounter < lines.length) {

            if (!lines[lineCounter].isEmpty() &&
                    isSceneHeader(lines[lineCounter])) {
                Setting setting = new Setting();

                setting.setSettingName(getSettingName (lines[lineCounter]));
                lineCounter++;

                // Parse dialogs
                DialogIdent di;
                int currCharacterIdx = -1;
                while (lineCounter < lines.length &&
                        (((di = getDialogLineType(lines[lineCounter])) != DialogIdent.NO_DIALOG_IDENT) ||
                                (lines[lineCounter].isEmpty()))) {

                    if (lines[lineCounter].isEmpty()) {
                        lineCounter++;
                        continue;
                    }

                    String currDialogText = lines[lineCounter].substring(di.getIdent(),
                            lines[lineCounter].length());

                    switch (di) {
                    case CHARACTER_NAME:
                        currCharacterIdx = setting.getCharacterArrayIndex(currDialogText);
                        if (currCharacterIdx == -1) {
                            Character ch = new Character(currDialogText);
                            if (setting.getCharacterList().add(ch))
                                currCharacterIdx = setting.getCharacterList().size() - 1;
                            else
                                throw new ParseException (String.format(ApplicationMessages.scriptParseError, lineCounter), lineCounter);
                        }
                        break;
                    case DESCRIPTION:
                        break;
                    case DIALOG:
                        if (currCharacterIdx != -1) {
                            Character ch = setting.getCharacterList().get(currCharacterIdx);
                            String[] words = currDialogText.split(" ");
                            for (int i = 0; i < words.length; i++) {
                                String key = words[i].replaceAll(REMOVE_SPEC_CHAR_REGEX, "").toLowerCase();
                                Integer wordCount = 0;
                                if ((wordCount = ch.getWordCounts().get(key)) != null)
                                    ch.getWordCounts().put(key, wordCount + 1);
                                else
                                    ch.getWordCounts().put(key, 1);
                            }
                        }
                        else
                            throw new ParseException (String.format(ApplicationMessages.scriptParseError, lineCounter), lineCounter);
                        break;
                    default:
                        break;
                    }
                    lineCounter++;
                }
                script.getmSettingList().add(setting);
            }
            else
                lineCounter++;
        }
        System.out.println("* end parseScript...");
        return script;
    }

    private String getSettingName (String line) {
        String[] settingHeaderNames = line.split("-");

        String settingName = "";

        if (settingHeaderNames.length > 0) {
            String firstSettingName = settingHeaderNames[0];
            int indexOfLastDot = firstSettingName.lastIndexOf('.');

            settingName = firstSettingName.substring(
                    indexOfLastDot + 2,
                    firstSettingName.length());
        }

        return settingName;
    }

    private boolean isSceneHeader (String line) {
        String[] sceneHeaderPattern = {"EXT.", "INT.", "INT./EXT."};

        boolean isHeader = false;

        for (int i = 0; i < sceneHeaderPattern.length && !isHeader; i++) {
            if (line.indexOf(sceneHeaderPattern[i]) != -1)
                isHeader = true;
        }
        return isHeader;
    }

    private DialogIdent getDialogLineType (String line) {
        for (DialogIdent d : DialogIdent.values()) {
            if (d.getIdent() > 0 && d.getIdent() < line.length()) {
                boolean isInvalidIdent = false;
                for (int i = 0; i < d.getIdent() && !isInvalidIdent; i++)
                    if (line.charAt(i) != ' ') {
                        isInvalidIdent = true;
                    }

                if (!isInvalidIdent)
                    return d;
            }
        }
        return DialogIdent.NO_DIALOG_IDENT;
    }

}