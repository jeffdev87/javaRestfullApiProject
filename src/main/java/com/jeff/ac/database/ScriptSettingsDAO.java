package com.jeff.ac.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.jeff.ac.appMessages.ApplicationMessages;
import com.jeff.ac.model.Actor;
import com.jeff.ac.model.Script;
import com.jeff.ac.model.Setting;

public class ScriptSettingsDAO {

    private static final SQLiteJDBCConnection dbConnection = SQLiteJDBCConnection.getDbCon();

    /**
     * Retrieve the auto-incremented id given to a script
     *
     * @param scriptName Script name already inserted
     * @return id
     */
    public static int getScriptAutoincrementedId (String scriptName) {
        String sqlCmd = String.format("SELECT id FROM script WHERE script_name = '%s'", scriptName);
        int resId = -1;

        try {
            ResultSet res = dbConnection.query(sqlCmd);
            resId = res.getInt("id");

        } catch (SQLException e) {
            System.out.println(ApplicationMessages.getAppLogPrefix() + e.getMessage());
        }

        return resId;
    }

    /**
     * Retrieve the auto-incremented id given to a character
     *
     * @param actorName Actor name already inserted
     * @return id
     */
    public static int getCharacterAutoincrementedId (String actorName) {
        String sqlCmd = String.format("SELECT id FROM character WHERE character_name = '%s'", actorName);
        int resId = -1;

        try {
            ResultSet res = dbConnection.query(sqlCmd);
            resId = res.getInt("id");

        } catch (SQLException e) {
            System.out.println(ApplicationMessages.getAppLogPrefix() + e.getMessage());
        }

        return resId;
    }

    /**
     * Insert script to the database
     *
     * @param scriptName Script name to be inserted
     * @return -1 if an error occur, 1 otherwise
     */
    private static int insertScript (String scriptName) {
        int insertRes = -1;

        try {
            String sqlCmd = String.format("INSERT INTO script (script_name) VALUES (\"%s\")",
                    scriptName);

            insertRes = dbConnection.insert(sqlCmd);

        } catch (SQLException e) {
            if (e.getMessage().toLowerCase().contains("unique"))
                System.out.println(ApplicationMessages.getAppLogPrefix() +
                        String.format(ApplicationMessages.scriptSqlErrorDuplicateScript, scriptName));
            else
                System.out.println(ApplicationMessages.getAppLogPrefix() + e.getMessage());
        }

        return insertRes;
    }

    /**
     * Insert actors into database.
     *
     * @param actorList List of actors to be inserted
     * @return -1 if an error occur, 1 otherwise
     */
    private static int insertCharacterList (Set<String> actorList) {
        int insertCount = 0;

        for (String ch : actorList) {
            String sqlCmd = String.format("INSERT INTO character (character_name) VALUES (\"%s\")", ch);

            try {
                insertCount+=dbConnection.insert(sqlCmd);
            }
            catch (SQLException e) {
                if (e.getMessage().toLowerCase().contains("unique"))
                    System.out.println(ApplicationMessages.getAppLogPrefix() +
                            String.format(ApplicationMessages.scriptSqlErrorDuplicateCharacter, ch));
                else
                    System.out.println(ApplicationMessages.getAppLogPrefix() + e.getMessage());
            }
        }
        return insertCount;
    }

    /**
     * Insert word count into database for each setting
     *
     * @param scriptId Id of the script
     * @param actorId Id of the actor
     * @param settingName Name of the setting
     * @param actor  Object with the dialogs of an actor in a given setting
     *
     * @return -1 if an error occur, a value greater than 1 with the number of
     * words inserted, otherwise
     */
    private static int insertCharacterScriptWordCount (int scriptId, int actorId, String settingName, Actor actor) {

        int insertCount = 0;

        Iterator<Entry<String, Integer>> it = actor.getWordCounts().entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<String, Integer> pair = it.next();

            String sqlCmd = String.format("INSERT INTO characterScriptWordCount (script_id, setting_name, character_id, word, counter) VALUES (%d, \"%s\", %d, \"%s\", %d)",
                    scriptId, settingName, actorId, pair.getKey(), pair.getValue());

            try {
                insertCount+= dbConnection.insert(sqlCmd);

            } catch (SQLException e) {
                System.out.println(ApplicationMessages.getAppLogPrefix() + e.getMessage());
            }
        }

        return insertCount;
    }

    /**
     * Insert settings dialogs into database
     *
     * @param scriptName The name of the script
     * @param settingList The list of settings
     * @return -1 if an error occur, a value greater than 1 with the number of
     * settings inserted, otherwise
     */
    private static int insertSettingsDialogs (String scriptName, List<Setting> settingList) {

        int insertCount = 0;

        int script_id = getScriptAutoincrementedId(scriptName);

        for (int i = 0; i < settingList.size(); i++) {

            String settingName = settingList.get(i).getSettingName();

            if (!settingList.get(i).hasCharacterList())
                continue;

            // Retrieve all characters with dialogs from this specific setting
            for (int j = 0; j < settingList.get(i).getCharacterList().size(); j++) {

                String characterName = settingList.get(i).getCharacterList().get(j).getCharacterName();

                int characterId = getCharacterAutoincrementedId(characterName);

                try {
                    String sqlCmd = String.format("INSERT INTO setting (script_id, setting_name, character_id) VALUES (%d, \"%s\", %d)",
                            script_id, settingName, characterId);

                    insertCount+=dbConnection.insert(sqlCmd);

                    System.out.println(String.format(ApplicationMessages.getAppLogPrefix() +
                            "Inserted setting %s with character %s.", settingName, characterName));

                } catch (SQLException ex) {
                    System.out.println(ApplicationMessages.getAppLogPrefix() + ex.getMessage());
                }

                if (!settingList.get(i).getCharacterList().get(j).hasDialogs())
                    continue;

                int nWords = insertCharacterScriptWordCount (script_id, characterId, settingName, settingList.get(i).getCharacterList().get(j));

                System.out.println(String.format(ApplicationMessages.getAppLogPrefix() +
                        "%d words inserted for character %s from setting %s.", nWords, characterName, settingName));
            }
        }

        System.out.println(ApplicationMessages.getAppLogPrefix() + "Settings persisted successfully.");

        return insertCount;
    }

    /**
     * Persist script data into database
     *
     * @param script Script object
     *
     * @return true if data persisted correctly or false, otherwise
     *
     */
    public static boolean inserScriptObject(Script script) {
        System.out.println(ApplicationMessages.getAppLogPrefix()  + "inserScriptObject");

        if (insertScript(script.getScriptName()) > 0) {
            insertCharacterList(script.getAllCharactersName());
            insertSettingsDialogs(script.getScriptName(), script.getmSettingList());
            return true;
        }

        return false;
    }
}
