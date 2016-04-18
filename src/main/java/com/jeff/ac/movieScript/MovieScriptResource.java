package com.jeff.ac.movieScript;

import java.sql.SQLException;
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
     * Receives the movie script in a predefined format
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
        System.out.println("doPostScript");

        ScriptParser parser = new ScriptParser(message);

        Script script = null;

        try {
            script = parser.parseScript(scriptName);
            System.out.println(script.toString());
        }
        catch (ParseException ex) {
            System.out.println("* error while parsing input script...");
            throw new InternalServerErrorException(ex.getMessage());
        }

        try {
            ScriptSettingsDAO.inserScript(script);
        } catch (SQLException ex) {
            System.out.println("* error while persisting input script...");
            throw new SQLErrorException(ex.getMessage());
        }

        String str = String.format("{\"message\": \"%s\"}",
                ApplicationMessages.scriptAddedSuccessfully);

        return str;
    }

    /**
     * Method handling HTTP GET requests.
     *
     * Returns information about all the movie settings.
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
     * @return String that will be returned as a application/json response.
     */
    @GET
    @Path("/settings")
    //@Produces(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String doGetAllSettings() {
        System.out.println("doGetAllSettings");

        return "doGetAllSettings";
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
     * @param int Movie setting id, required, integer
     * @return String that will be returned as a application/json response.
     */
    @GET
    @Path("/settings/{id}")
    //@Produces(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String doGetSetting(@PathParam("id") long id) {
        System.out.println("doGetSetting");

        return "doGetSetting: Input: " + id;
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
     * @return String that will be returned as a application/json response.
     */
    @GET
    @Path("/characters")
    //@Produces(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String doGetAllCharacters() {
        System.out.println("doGetAllCharacters");

        return "doGetAllCharacters";
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
     * @param int Movie character id, required, integer
     * @return String that will be returned as a application/json response.
     */
    @GET
    @Path("/characters/{id}")
    //@Produces(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String doGetCharacter(@PathParam("id") long id) {
        System.out.println("doGetCharacter");

        return "doGetCharacter: Input:" + id;
    }
}
