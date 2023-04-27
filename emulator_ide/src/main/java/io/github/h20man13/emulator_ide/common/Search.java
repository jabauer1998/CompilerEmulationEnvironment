package io.github.H20man13.emulator_ide.common;

public class Search {
    public static int findNextNonWhitespace(int cursorPosition, String text, SearchDirection direc){
        if(direc == SearchDirection.LEFT){
            int findBeginPosition = cursorPosition;
            while(findBeginPosition > 0){
                int nextBeginPosition = findBeginPosition - 1;
                if(nextBeginPosition < 0){
                    break;
                } else {
                    char charAtNextBeginPosition = text.charAt(nextBeginPosition);
                    if(Character.isWhitespace(charAtNextBeginPosition)){
                        findBeginPosition--;
                    } else {
                        break;
                    }
                }
            }
            return findBeginPosition;
        } else {
            int findBeginPosition = cursorPosition;
            while(findBeginPosition < text.length() - 1){
                int nextBeginPosition = findBeginPosition + 1;
                if(nextBeginPosition > text.length() - 1){
                    break;
                } else {
                    char charAtNextEndPosition = text.charAt(nextBeginPosition);
                    if(Character.isWhitespace(charAtNextEndPosition)){
                        findBeginPosition++;
                    } else {
                        break;
                    }
                }
            }
            return findBeginPosition;
        }
    }

    public static int findNextWhiteSpace(int cursorPosition, String text, SearchDirection direc){
        if(direc == SearchDirection.LEFT){
            int findBeginPosition = cursorPosition;
            while(findBeginPosition > 0){
                int nextBeginPosition = findBeginPosition - 1;
                if(nextBeginPosition < 0){
                    break;
                } else {
                    char charAtNextBeginPosition = text.charAt(nextBeginPosition);
                    if(Character.isWhitespace(charAtNextBeginPosition)){
                        break;
                    } else {
                        findBeginPosition--;
                    }
                }
            }
            return findBeginPosition;
        } else {
            int findEndPosition = cursorPosition;
            while(findEndPosition < text.length() - 1){
                int nextEndPosition = findEndPosition + 1;
                if(nextEndPosition > text.length() - 1){
                    break;
                } else {
                    char charAtNextEndPosition = text.charAt(nextEndPosition);
                    if(Character.isWhitespace(charAtNextEndPosition)){
                        break;
                    } else {
                        findEndPosition++;
                    }
                }
            }
            return findEndPosition;
        }

    }

    public enum SearchDirection{
        LEFT,
        RIGHT
    }
}
