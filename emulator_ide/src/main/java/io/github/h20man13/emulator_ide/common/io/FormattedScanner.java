package io.github.h20man13.emulator_ide.common.io;

import java.io.InputStream;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class FormattedScanner {
    private Scanner scanner;

    public FormattedScanner(InputStream Stream){
        scanner = new Scanner(Stream);
    }

    public FormattedScanner(Reader Reader){
        scanner = new Scanner(Reader);
    }

    public void close(){
        scanner.close();
    }

    public List<Object> scanf(String formatString){
        List<Object> resultList = new LinkedList<>();
        for(int i = 0; i < formatString.length(); i++){
            if(formatString.charAt(i) == '%'){
                char formatSpecifier = formatString.charAt(i + 1);
                if(formatSpecifier == 'd'){
                    resultList.add(scanner.nextLong(10));
                } else if(formatSpecifier == 's'){
                    resultList.add(scanner.nextLine());
                } else if(formatSpecifier == 'b'){
                    resultList.add(scanner.nextLong(2));
                }
            } else if(formatString.charAt(i) == '\n'){
                scanner.nextLine();
            }
        }

        return resultList;
    }

    public boolean atEof(){
        return !scanner.hasNext();
    }
}
