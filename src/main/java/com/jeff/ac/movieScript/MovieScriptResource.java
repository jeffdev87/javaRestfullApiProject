package com.jeff.ac.movieScript;

import java.text.ParseException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.jeff.ac.appMessages.ApplicationMessages;
import com.jeff.ac.database.ScriptSettingsDAO;
import com.jeff.ac.exceptions.InternalServerErrorException;
import com.jeff.ac.exceptions.SQLErrorException;
import com.jeff.ac.model.Script;
import com.jeff.ac.parser.ScriptParser;

/**
 * Enum with the error types handled by this application.
 *
 */
enum ReturnCodes {

    SUCCESS(200), FORBIDDEN(403), NOT_FOUND(404), UNEXPECTED_ERROR(500);

    private int code;

    ReturnCodes (int code) {
        this.code = code;
    }

    public int getCode () {
        return code;
    }
}

/**
 * MovieSript resource.
 *
 * Responsible for receiving movie scripts and return the collected
 * information via HTTP requests.
 *
 * Currently, the available HTTP methods are: GET and POST.
 *
 * The main path of this resource is: "moviescript"
 *
 */
@Path("/moviescript")
public class MovieScriptResource {

    public String dummyScript = "nada";

    /**
     * Method handling HTTP POST requests.
     *
     * Receives the movie script in a predefined text format
     *
     * If a movie script was already received before, it should respond
     * with an error.
     *
     * Responses:
     * 2XX: Success message
     * 4XX: Forbidden message
     * 5XX: Unexpected error
     *
     * @return String that will be returned as a application/json response.
     */
    @POST
    @Path("/script/{name}")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public String doPostScript(@PathParam("name") String scriptName, String message) {
        System.out.println(ApplicationMessages.getAppLogPrefix() + "doPostScript.start");

        ScriptParser parser = new ScriptParser(message);

        Script script = null;

        /*
         * Parsing the input data
         */
        try {
            script = parser.parseScript(scriptName);

            System.out.println(ApplicationMessages.getAppLogPrefix());
            System.out.println(script.toString());
            System.out.println(ApplicationMessages.getAppLogPrefix() +
                    String.format("%d settings parsed for script %s",
                            script.getNumberOfSettings(), scriptName));
        }
        catch (ParseException ex) {
            System.out.println(ApplicationMessages.getAppLogPrefix() +
                    ApplicationMessages.scriptParseError);
            throw new InternalServerErrorException(ex.getMessage());
        }

        /*
         * Storing script data into database
         */
        if (!ScriptSettingsDAO.inserScriptObject(script))
            throw new SQLErrorException(String.format(
                    ApplicationMessages.scriptSqlErrorDuplicateScript, script.getScriptName()));

        /*
         * Preparing response
         */
        String jsonResp = String.format("{\"message\": \"%s\"}",
                ApplicationMessages.scriptAddedSuccessfully);

        System.out.println(ApplicationMessages.getAppLogPrefix() + "doPostScript.end");

        return jsonResp;
    }

    /**
     * Method handling HTTP GET requests.
     *
     * Returns information about all settings for a given script.
     *
     * The response includes the name of the settings, the list of characters
     * that appeared in each setting, and the ten top dialogue word counts for
     * each character, considering dialogues in all settings, in descending
     * order by count.
     *
     * Responses:
     * 2XX: An array of settings
     * 5XX: Unexpected error
     *
     * @param scriptId Script identifier
     * @return String that will be returned as a application/json response.
     */
    @GET
    @Path("/settings/{scriptId}")
    @Produces(MediaType.APPLICATION_JSON)
    public String doGetAllSettings(@PathParam("scriptId") int scriptId) {
        System.out.println(ApplicationMessages.getAppLogPrefix() + "doGetAllSettings.start");

        String res = ScriptSettingsDAO.getSettings(scriptId, -1);

        System.out.println(ApplicationMessages.getAppLogPrefix() + "doGetAllSettings.end");

        return res;
    }

    /**
     * Method handling HTTP GET requests.
     *
     * Returns information about the movie setting with the given id,
     * if one exists.
     *
     * The response includes the name, the list of characters that appeared in
     * the setting, and the ten top dialogue word counts for each character,
     * considering dialogues in all settings, in descending order by count.
     *
     * Responses:
     * 2XX: A movie setting
     * 4XX: Not found
     * 5XX: Unexpected error
     *
     * @param int scriptId Script identifier
     * @param int settingId Setting identifier
     * @return String that will be returned as a application/json response.
     */
    @GET
    @Path("/settings/{scriptId}/{settingId}")
    @Produces(MediaType.APPLICATION_JSON)
    public String doGetSetting(@PathParam("scriptId") int scriptId, @PathParam("settingId") int settingId) {
        System.out.println(ApplicationMessages.getAppLogPrefix() + "doGetSetting.start");

        String res = ScriptSettingsDAO.getSettings(scriptId, settingId);

        System.out.println(ApplicationMessages.getAppLogPrefix() + "doGetSetting.end");

        return res;
    }

    /**
     * Method handling HTTP GET requests.
     *
     * Returns information about all the movie characters.
     *
     * The response includes the name of the characters and the ten top
     * dialogue word counts for each character, in descending order by count.
     *
     * Responses:
     * 2XX: An array of movie characters
     * 5XX: Unexpected error
     *
     * @param int scriptId Script identifier
     * @return String that will be returned as a application/json response.
     */
    @GET
    @Path("/characters/{scriptId}")
    @Produces(MediaType.APPLICATION_JSON)
    public String doGetAllCharacters(@PathParam("scriptId") int scriptId) {
        System.out.println(ApplicationMessages.getAppLogPrefix() + "doGetAllCharacters.start");

        String res = ScriptSettingsDAO.getCharacters(scriptId, -1);

        System.out.println(ApplicationMessages.getAppLogPrefix() + "doGetAllCharacters.end");

        return res;
    }

    /**
     * Method handling HTTP GET requests.
     *
     * Returns information about the movie character with the given id,
     * if one exists.
     *
     * The response includes the name and the ten top dialogue word counts for
     * the character.
     *
     * Responses:
     * 2XX: A movie setting
     * 4XX: Not found
     * 5XX: Unexpected error
     *
     * @param int scriptId Script identifier
     * @param int characterId Character identifier
     *
     * @return String that will be returned as a application/json response.
     */
    @GET
    @Path("/characters/{scriptId}/{characterId}")
    @Produces(MediaType.APPLICATION_JSON)
    public String doGetCharacter(@PathParam("scriptId") int scriptId, @PathParam("characterId") int characterId) {
        System.out.println(ApplicationMessages.getAppLogPrefix() + "doGetCharacter.start");

        String res = ScriptSettingsDAO.getCharacters(scriptId, characterId);

        System.out.println(ApplicationMessages.getAppLogPrefix() + "doGetCharacter.end");

        return res;
    }
}
