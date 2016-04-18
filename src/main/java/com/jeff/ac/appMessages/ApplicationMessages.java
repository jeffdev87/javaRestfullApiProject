package com.jeff.ac.appMessages;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ApplicationMessages {
    public static String scriptAddedSuccessfully = "Movie script successfully received";
    public static String scriptAlreadyExists = "Movie script already received";
    public static String scriptParseError = "Parse error on line %d";
    public static String scriptSqlErrorDuplicateScript = "Movie script %s already received";
    public static String scriptSqlErrorDuplicateCharacter = "Character %s already received";
    public static String scriptSqlTransactionRolledBack = "Transaction rolled back";

    public static String getAppLogPrefix () {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return String.format("APP_LOG %s: ", dateFormat.format(date));
    }
}
