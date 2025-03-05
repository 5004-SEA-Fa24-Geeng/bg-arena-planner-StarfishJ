package student;

/**
 * Operations is an enumeration that defines the comparison operations available for
 * filtering board games in the Board Game Arena Planner.
 *
 * This enum provides a comprehensive set of comparison operators that can be used
 * to create complex filtering conditions. Each operator is represented by its
 * symbolic form and includes functionality to parse and apply the operation.
 *
 * Supported operations:
 * - GREATER_THAN (>): Tests if a value is greater than the comparison value
 * - LESS_THAN (<): Tests if a value is less than the comparison value
 * - EQUALS (==): Tests if a value equals the comparison value
 * - NOT_EQUALS (!=): Tests if a value does not equal the comparison value
 * - CONTAINS (~=): Tests if a string contains the comparison value
 * - GREATER_THAN_EQUALS (>=): Tests if a value is greater than or equal to the comparison value
 * - LESS_THAN_EQUALS (<=): Tests if a value is less than or equal to the comparison value
 *
 * The enum also provides utility methods for:
 * - Getting the operator symbol
 * - Finding the operator length in a string
 * - Parsing operators from strings
 *
 * @author Yuchen Huang
 * @version 1.0
 */
public enum Operations {

    /** Operations to use. */
    EQUALS("=="), NOT_EQUALS("!="), GREATER_THAN(">"), LESS_THAN("<"), GREATER_THAN_EQUALS(
            ">="),
    /** Operations to use. */
    LESS_THAN_EQUALS("<="), CONTAINS("~=");

    /** The operator. */
    private final String operator;

    /**
     * Constructor for the operations.
     * @param operator The operator.
     */
    Operations(String operator) {
        this.operator = operator;
    }

    /**
     * Get the operator.
     * @return The operator.
     */
    public String getOperator() {
        return operator;
    }


    /**
     * Get the operator from a string that contains it.
     * @param str The string.
     * @return The operator.
     */
    public static Operations getOperatorFromStr(String str) {
        if (str.contains(">=")) {
            return Operations.GREATER_THAN_EQUALS;
        } else if (str.contains("<=")) {
            return Operations.LESS_THAN_EQUALS;
        } else if (str.contains(">")) {
            return Operations.GREATER_THAN;
        } else if (str.contains("<")) {
            return Operations.LESS_THAN;
        } else if (str.contains("==")) {
            return Operations.EQUALS;
        } else if (str.contains("!=")) {
            return Operations.NOT_EQUALS;
        } else if (str.contains("~=")) {
            return Operations.CONTAINS;
        } else {
            return null;
        }
    }

    /**
     * Get the operator length from a string that contains it.
     * @param str The string.
     * @return int Length of operator.
     */
    public static int getOperatorLenFromStr(String str) {
        if (str.contains(">=") || str.contains("<=") || str.contains("==") || str.contains("!=")
        || str.contains("~=")) {
            return 2;
        } else if (str.contains(">") || str.contains("<")) {
            return 1;
        } else {
            return 0;
        }
    }
}
