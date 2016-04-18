package com.jeff.ac.parser;

import java.text.ParseException;

import com.jeff.ac.appMessages.ApplicationMessages;
import com.jeff.ac.model.Actor;
import com.jeff.ac.model.Script;
import com.jeff.ac.model.Setting;

public class ScriptParser {

    private String mScript;

    enum DialogIdent {

        CHARACTER_NAME(22), DESCRIPTION(15), DIALOG(10), IS_SETTING_HEADER (0), NO_DIALOG_IDENT(-1);

        private int ident;

        DialogIdent (int ident) {
            this.ident = ident;
        }

        public int getIdent () {
            return ident;
        }
    };

    private static final String REMOVE_SPEC_CHAR_REGEX = "[^\\dA-Za-z\\- ']";

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
        System.out.println(ApplicationMessages.getAppLogPrefix() + "parseScript");

        this.mScript = this.mScript.replaceAll("\n", "#").replaceAll("\r", "");
        String[] lines = this.mScript.split("#");

        int lineCounter = 0;
        Script script = new Script (scriptName);

        while (lineCounter < lines.length) {

            if (!lines[lineCounter].isEmpty() &&
                    isSettingHeader(lines[lineCounter])) {

                String settingName = getSettingName (lines[lineCounter]);

                int settingIdx = -1;
                Setting setting = null;
                if ((settingIdx = script.getSettingArrayIndex(settingName)) != -1) {
                    setting = script.getmSettingList().get(settingIdx);
                }
                else {
                    setting = new Setting();
                    setting.setSettingName(settingName);
                }

                lineCounter++;

                // Parse dialogs
                DialogIdent di;
                int currCharacterIdx = -1;
                while (lineCounter < lines.length &&
                        (((di = getDialogLineType(lines[lineCounter])) != DialogIdent.IS_SETTING_HEADER) ||
                                (lines[lineCounter].isEmpty()))) {

                    if (lines[lineCounter].isEmpty()) {
                        lineCounter++;
                        continue;
                    }

                    String currDialogText = "";

                    if (di != DialogIdent.NO_DIALOG_IDENT)
                        currDialogText = lines[lineCounter].substring(di.getIdent(),
                                lines[lineCounter].length());

                    switch (di) {
                    case CHARACTER_NAME:
                        currCharacterIdx = setting.getCharacterArrayIndex(currDialogText);
                        if (currCharacterIdx == -1) {
                            Actor ch = new Actor(currDialogText);
                            if (setting.getCharacterList().add(ch))
                                currCharacterIdx = setting.getCharacterList().size() - 1;
                            else
                                throw new ParseException (String.format(ApplicationMessages.scriptParseError, lineCounter), lineCounter);
                        }
                        break;
                    case DESCRIPTION:
                        break;
                    case NO_DIALOG_IDENT:
                        break;
                    case DIALOG:
                        if (currCharacterIdx != -1) {
                            Actor ch = setting.getCharacterList().get(currCharacterIdx);
                            String[] words = currDialogText.split(" ");
                            for (int i = 0; i < words.length; i++) {
                                String key = words[i].replaceAll(REMOVE_SPEC_CHAR_REGEX, "").toLowerCase();

                                String[] separatedWord = key.split("-");

                                for (int j = 0; j < separatedWord.length; j++) {
                                    Integer wordCount = 0;
                                    if ((wordCount = ch.getWordCounts().get(separatedWord[j])) != null)
                                        ch.getWordCounts().put(separatedWord[j], wordCount + 1);
                                    else
                                        ch.getWordCounts().put(separatedWord[j], 1);
                                }

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
                //Adding new setting
                if (settingIdx == -1)
                    script.getmSettingList().add(setting);
            }
            else
                lineCounter++;
        }
        System.out.println(ApplicationMessages.getAppLogPrefix() + "parseScript.end");
        return script;
    }

    private String getSettingName (String line) {
        String[] settingHeaderNames = line.replace(" - ", "#").split("#");

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

    private boolean isSettingHeader (String line) {
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
                int i = 0;
                for (i = 0; i < d.getIdent() && !isInvalidIdent; i++)
                    if (line.charAt(i) != ' ') {
                        isInvalidIdent = true;
                    }

                if ((!isInvalidIdent) && line.charAt(i) != ' ')
                    return d;
            }
        }

        if (isSettingHeader(line))
            return DialogIdent.IS_SETTING_HEADER;

        return DialogIdent.NO_DIALOG_IDENT;
    }

}