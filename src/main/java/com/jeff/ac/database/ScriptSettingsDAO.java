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
     * Retrieve the auto-incremented id of a given model
     *
     * @param table Table name
     * @param collumn Table's columns
     * @param value Value of the field
     *
     * @return id
     */
    public static int getAutoincrementedId (String table, String column, String value) {
        String sqlCmd = String.format("SELECT id FROM %s WHERE %s = \"%s\"", table, column, value);
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

        int scriptId = getAutoincrementedId("script", "script_name", scriptName);

        for (int i = 0; i < settingList.size(); i++) {

            String settingName = settingList.get(i).getSettingName();

            try {
                String sqlCmd = String.format("INSERT INTO setting (setting_name) VALUES (\"%s\")", settingName);

                insertCount+=dbConnection.insert(sqlCmd);

                System.out.println(String.format(ApplicationMessages.getAppLogPrefix() +
                        "Inserted setting %s.", settingName));

            } catch (SQLException ex) {
                System.out.println(ApplicationMessages.getAppLogPrefix() + ex.getMessage());
            }

            if (!settingList.get(i).hasCharacterList())
                continue;

            int settingId = getAutoincrementedId("setting", "setting_name", settingName);

            // Retrieve all characters with dialogs from this specific setting
            for (int j = 0; j < settingList.get(i).getCharacterList().size(); j++) {

                String characterName = settingList.get(i).getCharacterList().get(j).getCharacterName();
                int characterId = getAutoincrementedId("character", "character_name", characterName);

                try {
                    String sqlCmd = String.format("INSERT INTO settingCharacter (script_id, setting_id, character_id) VALUES (%d, %d, %d)",
                            scriptId, settingId, characterId);

                    insertCount+=dbConnection.insert(sqlCmd);

                    System.out.println(String.format(ApplicationMessages.getAppLogPrefix() +
                            "Inserted setting %s with character %s.", settingName, characterName));

                } catch (SQLException ex) {
                    System.out.println(ApplicationMessages.getAppLogPrefix() + ex.getMessage());
                }

                if (!settingList.get(i).getCharacterList().get(j).hasDialogs())
                    continue;

                int nWords = insertCharacterScriptWordCount (scriptId, characterId, settingName, settingList.get(i).getCharacterList().get(j));

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

    public static String getSettings (int scriptId, int settingId) {

        String whereClause = String.format("WHERE script_id = %d ", scriptId);
        if (settingId != -1)
            whereClause+=String.format(" AND setting_id = \"%s\" ", settingId);

        String sqlCmd = String.format(
                "SELECT script_id, setting_id, setting_name, character_id, character_name "
                        + "FROM settingCharacter sc JOIN character c ON c.id = sc.character_id "
                        + "JOIN setting s ON s.id = sc.setting_id "
                        + "%s "
                        + "ORDER BY script_id, setting_name",
                        whereClause);

        String jsonRes = "[]";

        if (settingId != -1)
            jsonRes = String.format("{ \"message\": \"Movie setting with id %d not found\"}",
                    settingId);

        try {
            ResultSet res = dbConnection.query(sqlCmd);
            boolean hasResult = false;

            if (res.isBeforeFirst()) {
                jsonRes = "[";
                hasResult = true;
            }

            int currSettingId = -1, i = 0;
            String settingJsonObj = "";

            while (res.next()) {
                if (res.getInt("setting_id") != currSettingId) {
                    if (!settingJsonObj.isEmpty())
                        jsonRes = jsonRes.concat(settingJsonObj + "]},");

                    settingJsonObj = "";

                    currSettingId = res.getInt("setting_id");
                    String currSetting = res.getString("setting_name");

                    settingJsonObj+= String.format("{\"id\":%d, \"name\":\"%s\",", currSettingId, currSetting);
                    settingJsonObj+= "\"characters\":[";
                    ++i;
                }
                else {
                    settingJsonObj+=",";
                }

                String characterJsonObj = String.format("{\"id\":%d, \"name\":\"%s\",",
                        res.getInt("character_id"), res.getString("character_name"));

                characterJsonObj+=getTopWordCount(scriptId, res.getInt("character_id"), 10);
                characterJsonObj+="}";

                settingJsonObj+=characterJsonObj;
            }
            if (hasResult)
                jsonRes+=settingJsonObj+"]}]";

            System.out.println(ApplicationMessages.getAppLogPrefix() +
                    String.format(ApplicationMessages.scriptSqlResultSetSizeSettings, i));

        } catch (SQLException e) {
            System.out.println(ApplicationMessages.getAppLogPrefix() + e.getMessage());
        }

        return jsonRes;
    }

    public static String getCharacters (int scriptId, int characterId) {

        String whereClause = String.format("WHERE script_id = %d ", scriptId);
        if (characterId != -1)
            whereClause+=String.format(" AND character_id = \"%s\" ", characterId);

        String sqlCmd = String.format(
                "SELECT DISTINCT script_id, character_id, character_name "
                        + "FROM settingCharacter sc JOIN character c ON c.id = sc.character_id "
                        + "%s "
                        + "ORDER BY character_name",
                        whereClause);

        String jsonRes = "[]";

        if (characterId != -1)
            jsonRes = String.format("{ \"message\": \"Movie character with id %d not found\"}",
                    characterId);

        try {
            ResultSet res = dbConnection.query(sqlCmd);

            boolean hasResult = res.isBeforeFirst();

            if (hasResult) {
                jsonRes = "[";
            }

            String settingJsonObj = "";
            int i = 0;
            while (res.next()) {
                String characterJsonObj = String.format("{\"id\":%d, \"name\":\"%s\",",
                        res.getInt("character_id"), res.getString("character_name"));

                characterJsonObj+=getTopWordCount(scriptId, res.getInt("character_id"), 10);
                characterJsonObj+="},";

                jsonRes+=characterJsonObj;

                i++;
            }
            if (i > 0)
                jsonRes = jsonRes.substring(0, jsonRes.length() - 1);

            if (hasResult)
                jsonRes+=settingJsonObj+"]";

            System.out.println(ApplicationMessages.getAppLogPrefix() +
                    String.format(ApplicationMessages.scriptSqlResultSetSizeCharacters, i));

        } catch (SQLException e) {
            System.out.println(ApplicationMessages.getAppLogPrefix() + e.getMessage());
        }

        return jsonRes;
    }

    /**
     * Select the top words in dialogs for a given character in
     * a specific script.
     *
     * @param scriptId The id of the script
     * @param actorId The id of the character
     * @param limit The limit of results.
     *
     * @return String with an array of word/count values.
     */
    public static String getTopWordCount (int scriptId, int actorId, int limit) {

        String sqlCmd = String.format(
                "SELECT script_id, word, sum(counter) as sumCounter "
                        + "FROM characterScriptWordCount "
                        + "where script_id = %d AND character_id = %d "
                        + "GROUP BY script_id, character_id, word "
                        + "ORDER BY sum(counter) DESC "
                        + "LIMIT %d", scriptId, actorId, limit);

        String jsonWordCounts = "\"wordCounts\":[]";

        try {
            ResultSet res = dbConnection.query(sqlCmd);

            if (res.isBeforeFirst())
                jsonWordCounts = "\"wordCounts\":";

            String wordc = "";
            int i = 0;
            while (res.next()) {
                if (res.isFirst())
                    jsonWordCounts+="[";

                wordc = String.format("{\"word\":\"%s\", \"count\":%d}",
                        res.getString("word"),
                        res.getInt("sumCounter"));

                jsonWordCounts+= wordc + ",";

                i++;
            }

            // Remove last comma
            if (i > 0)
                jsonWordCounts = jsonWordCounts.substring(0, jsonWordCounts.length() - 1);

            jsonWordCounts+="]";

        } catch (SQLException e) {
            System.out.println(ApplicationMessages.getAppLogPrefix() + e.getMessage());
        }

        return jsonWordCounts;
    }
}
