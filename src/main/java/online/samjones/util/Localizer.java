package online.samjones.util;

import java.text.MessageFormat;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Provides localization functionality to the application
 */
public abstract class Localizer {

    private static final MessageFormat formatter = new MessageFormat("");
    private static final ResourceBundle resourceBundle =
            ResourceBundle.getBundle("online.samjones.messages", Locale.getDefault());

    /**
     * Returns localized message given a key and any additional argument required.
     * Thanks to user waxwing of StackOverflow for solution for converting
     * <a href="https://stackoverflow.com/questions/1018750/how-to-convert-">String array to Object array</a>
     * @param args Comma separated list of arguments to substitute in template
     * @param key String key of message in properties file
     * @return String containing localized message
     */
    public static String getLocalizedMessage(String key, String args){

        //Remove all spaces from args string in case spaces added around commas
        args = args.replaceAll("\\s", "");
        String[] argsArray = args.split(",");

        //Convert to object array
        Object[] objArray = Arrays.copyOf(argsArray, argsArray.length, Object[].class);

        formatter.applyPattern(resourceBundle.getString(key));
        return formatter.format(objArray);
    }

    /**
     * Calls getLocalizedMessage with empty string parameter. Allows method to be called with key only
     * and no template arguments.
     * @param key Key of message in properties file
     * @return returns localized string
     */
    public static String getLocalizedMessage(String key){
        return getLocalizedMessage(key, "");
    }

    /**
     * Gets current locale and provides String representation depending on language.
     * @return string containing name of locale if English or French, or String that indicates no localization
     * is available.
     */
    public static String getLocale(){
        String localeCode = Locale.getDefault().toString().toLowerCase();

        if(localeCode.contains("fr")){
            return "Français";
        } else if (localeCode.contains("en")) {
            return "English";
        } else {
            return "No Localization Available";
        }
    }

    /**
     * Returns current time zone as string. This is displayed on log in screen.
     * @return string containing name of ZoneId
     */
    public static String getZone(){

        return ZoneId.systemDefault().toString().replaceAll("_", " ");
    }
}

