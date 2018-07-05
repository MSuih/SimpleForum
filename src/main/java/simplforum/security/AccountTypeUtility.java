package simplforum.security;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/** Utility class for managing account types.
 */
public class AccountTypeUtility {
    private AccountTypeUtility() {
        throw new AssertionError("Cannot create instances of this!");
    }

    /** Ordinary registered user. */
    public static final short USER = 0;
    /** Moderator. */
    public static final short MOD = 3;
    /** Moderator with admin-level permissions. */
    public static final short SUPERMOD = 7;
    /** Administrator. */
    public static final short ADMIN = 10;

    /** Returns the string representation of a user value.
     * @param value Value which is converted to string.
     * @return String of the value.
     */
    public static String shortToString(short value) {
        switch(value){
            case USER:
                return "USER";
            case MOD:
                return "MOD";
            case SUPERMOD:
                return "SUPERMOD";
            case ADMIN:
                return "ADMIN";
            default:
                throw new IllegalArgumentException("Value " + value + "is not valid!");
        }
    }

    /** Converts a string representation to a user value.
     * @param string The string which is converted to value.
     * @return Value of the string.
     */
    public static short stringToShort(String string) {
        switch (string) {
            case "USER":
                return USER;
            case "MOD":
                return MOD;
            case "SUPERMOD":
                return SUPERMOD;
            case "ADMIN":
                return ADMIN;
            default:
                throw new IllegalArgumentException(string + "is not a valid usertype!");
        }
    }

    /** Returns a stream which contains string representation of user values.
     * The stream contains the requested value and all values below it.
     * @param value The requested value.
     * @return Stream of Strings.
     */
    public static Stream<String> shortToStream(short value) {
        switch(value){
            case USER:
                return Stream.of("USER");
            case MOD:
                return Stream.of("USER", "MOD");
            case SUPERMOD:
                return Stream.of("USER", "MOD", "SUPERMOD");
            case ADMIN:
                return Stream.of("USER", "MOD", "SUPERMOD", "ADMIN");
            default:
                throw new IllegalArgumentException("Value " + value + "is not valid!");
        }
    }

    /** Helper class for value-String pairs.
     */
    public static class Pair {
        /** String representation for this value. */
        public final String name;
        /** The user value. */
        public final short value;
        /** Creates a new instance.
         * @param name String representation of a value.
         * @param value The value.
         */
        public Pair(String name, short value) {
            this.name = name;
            this.value = value;
        }
    }

    private static final List<Pair> types = Collections.unmodifiableList(
            Arrays.asList(
                    new Pair("USER", USER),
                    new Pair("MOD", MOD),
                    new Pair("SUPERMOD", SUPERMOD),
                    new Pair("ADMIN", ADMIN)));

    /** Returns a list of possible user values.
     * @return List of a value-name pairs.
     */
    public static List<Pair> getPossibleTypes() {
        return types;
    }
}
