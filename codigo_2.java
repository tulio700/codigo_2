import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LexicalAnalyzer {
    private final List<Token> tokens;
    private String sourceCode;
    private int currentPosition;

    public LexicalAnalyzer(String sourceCode) {
        this.tokens = new ArrayList<>();
        this.sourceCode = sourceCode;
        this.currentPosition = 0;
    }

    public List<Token> analyze() {
        while (currentPosition < sourceCode.length()) {
            char currentChar = sourceCode.charAt(currentPosition);

            if (Character.isDigit(currentChar)) {
                analyzeNumber();
            } else if (Character.isLetter(currentChar)) {
                analyzeWord();
            } else if (currentChar == '"') {
                analyzeString();
            } else if (isOperator(currentChar)) {
                analyzeOperator();
            } else if (isDelimiter(currentChar)) {
                addToken(TokenType.DELIMITER, String.valueOf(currentChar));
                currentPosition++;
            } else if (Character.isWhitespace(currentChar)) {
                currentPosition++;
            } else {
                currentPosition++; // Ignoring unrecognized characters
            }
        }

        return tokens;
    }

    private void analyzeNumber() {
        StringBuilder number = new StringBuilder();
        while (currentPosition < sourceCode.length() && Character.isDigit(sourceCode.charAt(currentPosition))) {
            number.append(sourceCode.charAt(currentPosition));
            currentPosition++;
        }
        addToken(TokenType.NUMBER, number.toString());
    }

    private void analyzeWord() {
        StringBuilder word = new StringBuilder();
        while (currentPosition < sourceCode.length() &&
                (Character.isLetterOrDigit(sourceCode.charAt(currentPosition)) || sourceCode.charAt(currentPosition) == '_')) {
            word.append(sourceCode.charAt(currentPosition));
            currentPosition++;
        }
        addToken(TokenType.WORD, word.toString());
    }

    private void analyzeString() {
        currentPosition++; // Move past the initial quote
        StringBuilder string = new StringBuilder();
        while (currentPosition < sourceCode.length() && sourceCode.charAt(currentPosition) != '"') {
            string.append(sourceCode.charAt(currentPosition));
            currentPosition++;
        }
        if (currentPosition < sourceCode.length() && sourceCode.charAt(currentPosition) == '"') {
            addToken(TokenType.STRING, string.toString());
            currentPosition++; // Move past the ending quote
        } else {
            // Unclosed string, do something like throw an error or handle it accordingly
        }
    }

    private void analyzeOperator() {
        char currentChar = sourceCode.charAt(currentPosition);
        StringBuilder operator = new StringBuilder();
        operator.append(currentChar);

        // Check for multi-character operators
        while (currentPosition + 1 < sourceCode.length()) {
            String possibleOperator = operator.toString() + sourceCode.charAt(currentPosition + 1);
            if (isOperator(possibleOperator)) {
                operator.append(sourceCode.charAt(currentPosition + 1));
                currentPosition++;
            } else {
                break;
            }
        }
        addToken(TokenType.OPERATOR, operator.toString());
        currentPosition++;
    }

    private boolean isOperator(char c) {
        String[] operators = {"+", "-", "*", "/", "=", "<", ">", "!", "&", "|"};
        return new String(operators).contains(String.valueOf(c));
    }

    private boolean isOperator(String str) {
        String[] operators = {"==", "!=", "<=", ">=", "&&", "||"};
        return new String(operators).contains(str);
    }

    private boolean isDelimiter(char c) {
        return c == ';' || c == ',' || c == '(' || c == ')' || c == '{' || c == '}';
    }

    private void addToken(TokenType type, String value) {
        tokens.add(new Token(type, value));
    }
}