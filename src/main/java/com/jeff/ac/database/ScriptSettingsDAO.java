package com.jeff.ac.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.jeff.ac.appMessages.ApplicationMessages;
import com.jeff.ac.model.Script;

public class ScriptSettingsDAO {

    private static final SQLiteJDBCConnection dbConnection = SQLiteJDBCConnection.getDbCon();

    public static int getScriptAutoincrementedId (String scriptName) {
        String sqlCmd = String.format("SELECT id FROM script WHERE script_name = '%s'", scriptName);
        int resId = -1;

        try {
            ResultSet res = dbConnection.query(sqlCmd);
            resId = res.getInt("id");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return resId;
    }

    public static int getCharacterAutoincrementedId (String characterName) {
        String sqlCmd = String.format("SELECT id FROM character WHERE character_name = '%s'", characterName);
        int resId = -1;

        try {
            ResultSet res = dbConnection.query(sqlCmd);
            resId = res.getInt("id");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return resId;
    }

    public static boolean inserScript(Script script) throws SQLException {
        System.out.println("* begin insertScript...");

        try {
            dbConnection.setAutoCommit(false);

            String sqlCmd = String.format("INSERT INTO script (script_name) VALUES (\"%s\")",
                    script.getScriptName());

            dbConnection.insert(sqlCmd);

            // Read all characters and insert data into database
            for (String ch : script.getAllCharactersName()) {
                sqlCmd = String.format("INSERT INTO character (character_name) VALUES (\"%s\")", ch);
                dbConnection.insert(sqlCmd);
            }

            // Read all settings data and insert into database
            int script_id = getScriptAutoincrementedId(script.getScriptName());
            for (int i = 0; i < script.getmSettingList().size(); i++) {
                // Retrieve setting name
                String settingName = script.getmSettingList().get(i).getSettingName();

                // Retrieve all characters from this specific setting
                for (int j = 0; j < script.getmSettingList().get(i).getCharacterList().size(); j++) {
                    int characterId = getCharacterAutoincrementedId(script.getmSettingList().get(i).getCharacterList().get(j).getCharacterName());
                    sqlCmd = String.format("INSERT INTO setting (script_id, setting_name, character_id) VALUES (%d, \"%s\", %d)",
                            script_id, settingName, characterId);
                    dbConnection.insert(sqlCmd);

                    // Retrieve the word count for each character
                    Iterator<Entry<String, Integer>> it = script.getmSettingList().get(i).getCharacterList().get(j).getWordCounts().entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry<String, Integer> pair = it.next();
                        sqlCmd = String.format("INSERT INTO characterScriptWordCount (script_id, setting_name, character_id, word, counter) VALUES (%d, \"%s\", %d, \"%s\", %d)",
                                script_id, settingName, characterId, pair.getKey(), pair.getValue());
                        dbConnection.insert(sqlCmd);
                    }
                }
            }
            dbConnection.commit();
            System.out.println("Script persisted successfully!");
        }
        catch (SQLException ex) {
            dbConnection.rollBack();

            //check sql status code to determine the right error message
            throw new SQLException(String.format(ApplicationMessages.scriptSqlErrorDuplicateScript, script.getScriptName()));
        }

        System.out.println("* end insertScript...");
        return true;
    }
}
