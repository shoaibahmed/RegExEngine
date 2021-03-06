package regex.engine;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class RegExEngine extends Thread 
{
    public String regex;
    public String test;
    
    public final static String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public final static String DIGITS = "0123456789";
    public final static int UPPERCASE_A = 65;
    public final static int LOWERCASE_A = 97;
    public final static int ZERO = 48;
    
    public final static String FILE_NAME = "List.txt";
    public final static int REGEX_COMBINATIONS_LENGTH = 10;
    
    public void generateFile()
    {
        try 
        {
            // Assume default encoding.
            FileWriter fileWriter = new FileWriter(FILE_NAME);

            // Always wrap FileWriter in BufferedWriter.
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            //bufferedWriter.write("Hello there,");
            //bufferedWriter.write(" here is some text.");
            //bufferedWriter.newLine();
            
            generateCombinations(bufferedWriter);

            // Always close files.
            bufferedWriter.close();
        }
        catch(IOException ex) 
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void run() 
    {
        parseStringRegexEngine(test);
    }
    
    public boolean isNullStringAccepted()
    {
        return parseStringRegexEngine("");
    }
    
    public boolean isTestStringAccepted()
    {
        return parseStringRegexEngine(test);
    }
    
    public String getAllCharactersInBetween(char startingChar, char endingChar)
    {
        String result = "";
        
        //If number list is required
        if(Character.isDigit(startingChar))
        {
            int startingIndex = (int) startingChar - ZERO;
            int endingIndex = (int) endingChar - ZERO;
            
            // + 1 to leave the starting symbol since it was already added in the group
            for(int index = (startingIndex + 1); index <= endingIndex; index++)
            {
                result += DIGITS.charAt(index);
            }
        }
        
        //If Alphabets list is required
        else
        {
            int startingIndex, endingIndex;
            if(Character.isLowerCase(startingChar))
            {
                startingIndex = (int) startingChar - LOWERCASE_A;
                endingIndex = (int) endingChar - LOWERCASE_A;
            }
            else
            {
                startingIndex = (int) startingChar - UPPERCASE_A;
                endingIndex = (int) endingChar - UPPERCASE_A;
            }
            
            // + 1 to leave the starting symbol since it was already added in the group
            for(int index = (startingIndex + 1); index <= endingIndex; index++)
            {
                result += CHARACTERS.charAt(index);
            }
            
            if(Character.isLowerCase(startingChar))
            {
                result = result.toLowerCase();
            }
        }
        
        return result;
    }
    
    public boolean parseStringRegexEngine(String test)
    {
        //Parse the Regular Expression
        int index = 0;
        int indexTestString = 0;
        String group = "";
        
        while(index < regex.length())
        {
            char ch = regex.charAt(index);
            
            //Check if the test string starts with the specified sequence
            if((index == 0) && (ch == '^'))
            {
                char nextCh = regex.charAt(index + 1);
                
                if(nextCh == '(')
                {
                    String startString = "";
                    index++;
                    nextCh = regex.charAt(index + 1);
                    while(nextCh != ')')
                    {
                        startString += nextCh;
                        index++;
                        nextCh = regex.charAt(index + 1);
                    }
                    
                    //Check if the test string start with the specified sequence
                    if(!test.startsWith(startString))
                    {
                        return false;
                    }
                }
                else
                {
                    //Check if the test string start with the specified character
                    char testStringCh = test.charAt(indexTestString);
                    if(ch != testStringCh)
                    {
                        return false;
                    }
                }
            }
            
            //Character Class checks
            
            //If start of character class '['
            if(ch == '[')
            {                
                //Get all the elements in the character class
                index++;
                ch = regex.charAt(index);
                
                while(ch != ']')
                {
                    if(ch == '-')
                    {
                        index++;
                        ch = regex.charAt(index);
                        
                        //Check if the user used '-' for subtraction
                        if(ch == '[')
                        {
                            String subClass = "";
                            while(ch != ']')
                            {
                                subClass += ch;
                                index++;
                                ch = regex.charAt(index);
                            }
                            
                            //Remove the character class from the upper character class
                            for(int iterator = 0; iterator < subClass.length(); iterator++)
                            {
                                String replacer = "";
                                replacer += subClass.charAt(iterator);
                                group = group.replaceAll(replacer, "");
                            }
                        }
                        else
                        {
                            //Get all the character occurances in between
                            char prevChar = regex.charAt(index - 2);
                            group += getAllCharactersInBetween(prevChar, ch);
                        }
                    }
                    else
                    {
                        group += ch;
                        index++;
                        ch = regex.charAt(index);
                    }
                }
                
                //Check if there is no items remaining in the regex
                if(index == (regex.length() - 1))
                {
                    //If there is no input remaining as well
                    if(test.equals(""))
                    {
                        return false;
                    }
                    
                    //Check if there is any input remaining
                    if(indexTestString >= test.length())
                    {
                        return false;
                    }
                    
                    //Check if any single character occured
                    char testStringChar = test.charAt(indexTestString);
                    indexTestString++;
                    for(int i = 0; i < group.length(); i++)
                    {
                        if(testStringChar == group.charAt(i))
                        {
                            break;
                        }

                        else if(i == (group.length() - 1))
                        {
                            return false;
                        }
                    }
                }
                else
                {
                    //Check if there is closure symbol in the end
                    index++;
                    ch = regex.charAt(index);
                    if(ch == '*')
                    {
                        boolean finished = false;
                        while(!finished)
                        {
                            if(indexTestString < test.length())
                            {
                                char testStringChar = test.charAt(indexTestString);
                                indexTestString++;
                                for(int i = 0; i < group.length(); i++)
                                {
                                    if(testStringChar == group.charAt(i))
                                    {
                                        break;
                                    }

                                    else if(i == (group.length() - 1))
                                    {
                                        finished = true;
                                        indexTestString--;
                                        break;
                                    }
                                }
                            }
                            else
                            {
                                break;
                            }
                        }
                    }
                    else if(ch == '+')
                    {
                        int counter = 0;
                        boolean finished = false;
                        while(!finished)
                        {
                            //Check if there is input remaining
                            if(indexTestString < test.length())
                            {
                                char testStringChar = test.charAt(indexTestString);
                                indexTestString++;
                                for(int i = 0; i < group.length(); i++)
                                {
                                    if(testStringChar == group.charAt(i))
                                    {
                                        counter++;
                                        break;
                                    }

                                    else if(i == (group.length() - 1))
                                    {
                                        finished = true;
                                        indexTestString--;
                                        break;
                                    }
                                }    
                            }
                            else
                            {
                                break;
                            }
                        }

                        if(counter == 0)
                        {
                            return false;
                        }
                    }
                    else if(ch == '{')
                    {
                        String firstNum = "", secondNum = "";
                        index++;
                        char nextCh = regex.charAt(index);
                        while(nextCh != ',')
                        {
                            firstNum += regex.charAt(index);
                            index++;
                            nextCh = regex.charAt(index);
                        }

                        index++;
                        nextCh = regex.charAt(index);
                        while(nextCh != '}')
                        {
                            secondNum += regex.charAt(index);
                            index++;
                            nextCh = regex.charAt(index);
                        }

                        index++;
                        int lowerLimit = Integer.parseInt(firstNum);
                        int upperLimit = Integer.parseInt(secondNum);

                        int counter = 0;
                        boolean finished = false;
                        while(!finished)
                        {
                            //Check if there is input remaining
                            if(indexTestString < test.length())
                            {
                                char testStringChar = test.charAt(indexTestString);
                                indexTestString++;
                                for(int i = 0; i < group.length(); i++)
                                {
                                    if(testStringChar == group.charAt(i))
                                    {
                                        counter++;
                                        break;
                                    }

                                    else if(i == (group.length() - 1))
                                    {
                                        finished = true;
                                        indexTestString--;
                                        break;
                                    }
                                }
                            }
                            else
                            {
                                break;
                            }
                        }

                        if((counter < lowerLimit) || (counter > upperLimit))
                        {
                            return false;
                        }
                    }
                    else if(ch == '?')
                    {
                        if(indexTestString < test.length())
                        {
                            char testStringChar = test.charAt(indexTestString);
                            indexTestString++;
                            for(int i = 0; i < group.length(); i++)
                            {
                                if(testStringChar == group.charAt(i))
                                {
                                    break;
                                }
                                else if(i == (group.length() - 1))
                                {
                                    indexTestString--;
                                }
                            }
                        }
                    }
                    else if(ch == '$')
                    {
                        if(test.endsWith(group))
                        {
                            return true;
                        }
                        else
                        {
                            return false;
                        }
                    }
                    else
                    {
                        //Check if any single character occured
                        char testStringChar = test.charAt(indexTestString);
                        indexTestString++;
                        for(int i = 0; i < group.length(); i++)
                        {
                            if(testStringChar == group.charAt(i))
                            {
                                break;
                            }

                            else if(i == (group.length() - 1))
                            {
                                return false;
                            }
                        }

                        group = "";
                        index--;
                    }    
                }
            }
            
            //Group checks
            
            //If start of character class '['
            else if(ch == '(')
            {                
                //Get all the elements in the character class
                index++;
                ch = regex.charAt(index);
                
                while(ch != ')')
                {
                    group += ch;
                    index++;
                    ch = regex.charAt(index);
                }
                
                //Check if there is no input remaing
                if(index == regex.length())
                {
                    //Check if any single character occured
                    char testStringChar = test.charAt(indexTestString);
                    indexTestString++;
                    for(int i = 0; i < group.length(); i++)
                    {
                        if(testStringChar != group.charAt(i))
                        {
                            return false;
                        }
                    }
                }
                else
                {
                    //Check if there is closure symbol in the end
                    index++;
                    ch = regex.charAt(index);
                    int testStringStartIndex = indexTestString;
                    
                    if(ch == '*')
                    {
                        boolean finished = false;
                        while(!finished)
                        {
                            //Check if there is input remaining
                            if(indexTestString < test.length())
                            {
                                char testStringChar = test.charAt(indexTestString);
                                indexTestString++;
                                for(int i = 0; i < group.length(); i++)
                                {
                                    if(testStringChar != group.charAt(i))
                                    {
                                        finished = true;
                                        indexTestString = testStringStartIndex;
                                        break;
                                    }
                                    
                                    if((indexTestString < test.length()) && (i < (group.length() - 1)))
                                    {
                                        testStringChar = test.charAt(indexTestString);
                                        indexTestString++;
                                    }
                                }
                            }
                            else
                            {
                                break;
                            }
                        }
                    }
                    else if(ch == '+')
                    {
                        int counter = 0;
                        boolean finished = false;
                        while(!finished)
                        {
                            //Check if there is input remaining
                            if(indexTestString < test.length())
                            {
                                boolean completeMatch = true;
                                char testStringChar = test.charAt(indexTestString);
                                indexTestString++;
                                for(int i = 0; i < group.length(); i++)
                                {
                                    if(testStringChar != group.charAt(i))
                                    {
                                        finished = true;
                                        indexTestString = testStringStartIndex;
                                        completeMatch = false;
                                        break;
                                    }
                                    
                                    if((indexTestString < test.length()) && (i < (group.length() - 1)))
                                    {
                                        testStringChar = test.charAt(indexTestString);
                                        indexTestString++;
                                    }
                                } 
                                
                                if(completeMatch)
                                {
                                    testStringStartIndex = indexTestString;
                                    counter++;
                                }
                            }
                            else
                            {
                                break;
                            }
                        }

                        if(counter == 0)
                        {
                            return false;
                        }
                    }
                    else if(ch == '{')
                    {
                        String firstNum = "", secondNum = "";
                        index++;
                        char nextCh = regex.charAt(index);
                        while(nextCh != ',')
                        {
                            index++;
                            firstNum += regex.charAt(index);
                        }

                        index++;
                        nextCh = regex.charAt(index);
                        while(nextCh != '}')
                        {
                            index++;
                            secondNum += regex.charAt(index);
                        }

                        index++;
                        int lowerLimit = Integer.parseInt(firstNum);
                        int upperLimit = Integer.parseInt(secondNum);

                        int counter = 0;
                        boolean finished = false;
                        while(!finished)
                        {
                            //Check if there is input remaining
                            if(indexTestString < test.length())
                            {
                                boolean completeMatch = true;
                                char testStringChar = test.charAt(indexTestString);
                                indexTestString++;
                                for(int i = 0; i < group.length(); i++)
                                {
                                    if(testStringChar != group.charAt(i))
                                    {
                                        finished = true;
                                        indexTestString = testStringStartIndex;
                                        completeMatch = false;
                                        break;
                                    }
                                } 
                                
                                if(completeMatch)
                                {
                                    counter++;
                                }
                            }
                            else
                            {
                                break;
                            }
                        }

                        if((counter < lowerLimit) || (counter > upperLimit))
                        {
                            return false;
                        }
                    }
                    else if(ch == '?')
                    {
                        //Check if there is input remaining
                        if(indexTestString < test.length())
                        {
                            char testStringChar = test.charAt(indexTestString);
                            indexTestString++;
                            for(int i = 0; i < group.length(); i++)
                            {
                                if(testStringChar != group.charAt(i))
                                {
                                    indexTestString = testStringStartIndex;
                                    break;
                                }

                                if((indexTestString < test.length())  && (i < (group.length() - 1)))
                                {
                                    testStringChar = test.charAt(indexTestString);
                                    indexTestString++;
                                }
                            }
                        }
                    }
                    else if(ch == '$')
                    {
                        if(test.endsWith(group))
                        {
                            return true;
                        }
                        else
                        {
                            return false;
                        }
                    }
                    else
                    {
                        //Check if any single character occured
                        char testStringChar = test.charAt(indexTestString);
                        indexTestString++;
                        for(int i = 0; i < group.length(); i++)
                        {
                            if(testStringChar != group.charAt(i))
                            {
                                return false;
                            }
                        }

                        group = "";
                        index--;
                    }    
                }
                
            }
            
            //Direct character match
            else 
            {
                if(test.equals(""))
                {
                    //Check if there is any optionality
                    if(!(regex.contains("?") || regex.contains("*")))
                    {
                        return false;
                    }
                    else
                    {
                        //Check if there is any mapping
                        while(index < regex.length())
                        {
                            char regexChar = regex.charAt(index);
                            index++;
                            
                            char nextCh;
                            if(index < regex.length())
                            {
                                nextCh = regex.charAt(index);
                                if((nextCh == '?') || (nextCh == '*'))
                                {
                                    index++;
                                }
                                else
                                {
                                    return false;
                                }
                            }
                            
                            //if
                        }
                    }
                }
                else
                {
                    while((index < regex.length()) && (indexTestString < test.length()))
                    {
                        char testStringChar = test.charAt(indexTestString);
                        indexTestString++;
                        char regexChar = regex.charAt(index);
                        
                        if((regexChar == '(') || (regexChar == '['))
                        {
                            indexTestString--;
                            index--;
                            //continue;

                            break;
                        }
                        else
                        {
                            index++;
                            char closure = 'E';
                            //Check if there is closure symbol
                            if(index < regex.length())
                            {
                                char nextChar = regex.charAt(index);
                                if((nextChar == '*') || (nextChar == '+') || (nextChar == '?'))
                                {
                                    closure = nextChar;
                                    index++;
                                }
                            }

                            //Check if meta characters are used
                            if(regexChar == '.')
                            {
                                //Everything matches '.'

                                //Check if closure was used
                                if((closure != 'E') && (closure != '?'))
                                {
                                    //Everything will match with .
                                    return true;
                                }
                            }
                            else
                            {
                                if(closure == '*')
                                {
                                    boolean inputSymbolUtilized = false;
                                    boolean incremented = false;
                                    
                                    while(regexChar == testStringChar)
                                    {
                                        inputSymbolUtilized = true;
                                        if(indexTestString < test.length())
                                        {
                                            testStringChar = test.charAt(indexTestString);
                                            indexTestString++;
                                            incremented = true;
                                        }
                                        else
                                        {
                                            break;
                                        }
                                    }
                                    
                                    if(!inputSymbolUtilized)
                                    {
                                        indexTestString--;
                                    }
                                    
                                    if(incremented)
                                    {
                                        indexTestString--;
                                    }
                                }
                                else if(closure == '+')
                                {
                                    boolean symbolNotUtilized = true;
                                    boolean incremented = false;
                                    
                                    while(regexChar == testStringChar)
                                    {
                                        symbolNotUtilized = false;
                                        
                                        if(indexTestString < test.length())
                                        {
                                            testStringChar = test.charAt(indexTestString);
                                            indexTestString++;
                                            incremented = true;
                                        }
                                        else
                                        {
                                            break;
                                        }
                                    }

                                    if(symbolNotUtilized)
                                    {
                                        return false;
                                    }
                                    
                                    if(incremented)
                                    {
                                        indexTestString--;
                                    }
                                }
                                else if(closure == '?')
                                {
                                    //Check if the literals match
                                    if(regexChar != testStringChar)
                                    {
                                        indexTestString--;
                                    }
                                }
                                else
                                {
                                    //Check if the literals match
                                    if(regexChar != testStringChar)
                                    {
                                        return false;
                                    }   
                                }
                            }
                        }
                    }
                    
                    if(test.equals("") || (test.length() < indexTestString))
                    {
                        //Check if some regex symbols are remaining
                        if(index < regex.length())
                        {
                            //Take the substring of the remaining characters
                            String substr = regex.substring(index);

                            //Check if the substring contains any optionality
                            if(!(substr.contains("?") || substr.contains("*")))
                            {
                                return false;
                            }

                            char regexChar = regex.charAt(index);
                            char nextChar = '>';
                            index++;

                            if(index < regex.length())
                            {
                                nextChar = regex.charAt(index);
                                index++;
                            }

                            //Check if all the symbols remaining are optional
                            if((nextChar == '?') || (nextChar == '*'))
                            {
                                while((index < regex.length()) && (indexTestString < test.length()))
                                {
                                    if((nextChar == '?') || (nextChar == '*'))
                                    {
                                        regexChar = regex.charAt(index);
                                        index++;
                                        nextChar = regex.charAt(index);
                                        index++;
                                    }
                                    else
                                    {
                                        return false;
                                    }
                                }
                            }
                            else
                            {
                                return false;
                            }
                        }
                    }
                    
                }
                
            }
            
            group = "";
            index++;
        }
        
        if((indexTestString != test.length()))
        {
            return false;
        }
        else
        {
            return true;
        }
    }
    
    public void generateCombinations(BufferedWriter writer)
    {
        Random randomNoGenerator = new Random();
        
        boolean useOptionality = true;
        int iterator = 0;
        //for(int iterator = 0; iterator < REGEX_COMBINATIONS; iterator++)
        while(true)
        {
            useOptionality = !useOptionality;
            
            //Parse the Regular Expression
            int index = 0;
            String group = "";
            String resultantString = "";
            
            while(index < regex.length())
            {
                char ch = regex.charAt(index);

                //Check if the test string starts with the specified sequence
                if((index == 0) && (ch == '^'))
                {
                    char nextCh = regex.charAt(index + 1);

                    if(nextCh == '(')
                    {
                        String startString = "";
                        index++;
                        nextCh = regex.charAt(index + 1);
                        while(nextCh != ')')
                        {
                            startString += nextCh;
                            index++;
                            nextCh = regex.charAt(index + 1);
                        }

                        resultantString += startString;
                    }
                    else
                    {
                        resultantString += ch;
                    }
                }

                //Character Class checks

                //If start of character class '['
                if(ch == '[')
                {                
                    //Get all the elements in the character class
                    index++;
                    ch = regex.charAt(index);

                    while(ch != ']')
                    {
                        if(ch == '-')
                        {
                            index++;
                            ch = regex.charAt(index);

                            //Check if the user used '-' for subtraction
                            if(ch == '[')
                            {
                                String subClass = "";
                                while(ch != ']')
                                {
                                    subClass += ch;
                                    index++;
                                    ch = regex.charAt(index);
                                }

                                //Remove the character class from the upper character class
                                for(int iter = 0; iter < subClass.length(); iter++)
                                {
                                    String replacer = "";
                                    replacer += subClass.charAt(iter);
                                    group = group.replaceAll(replacer, "");
                                }
                            }
                            else
                            {
                                //Get all the character occurances in between
                                char prevChar = regex.charAt(index - 2);
                                group += getAllCharactersInBetween(prevChar, ch);
                            }
                        }
                        else
                        {
                            group += ch;
                            index++;
                            ch = regex.charAt(index);
                        }
                    }

                    //Check if there is no items remaining in the regex
                    if(index == (regex.length() - 1))
                    {
                        //Pick one random character from the character class
                        int randomIndex = randomNoGenerator.nextInt(group.length());
                        resultantString += group.charAt(randomIndex);
                    }
                    else
                    {
                        //Check if there is closure symbol in the end
                        index++;
                        ch = regex.charAt(index);
                        if(ch == '*')
                        {
                            for(int i = 0; i < iterator; i++)
                            {
                                //Pick one random character from the character class
                                int randomIndex = randomNoGenerator.nextInt(group.length());
                                resultantString += group.charAt(randomIndex);
                            }
                        }
                        else if(ch == '+')
                        {
                            for(int i = 0; i <= iterator; i++)
                            {
                                //Pick one random character from the character class
                                int randomIndex = randomNoGenerator.nextInt(group.length());
                                resultantString += group.charAt(randomIndex);
                            }
                        }
                        else if(ch == '{')
                        {
                            String firstNum = "", secondNum = "";
                            index++;
                            char nextCh = regex.charAt(index);
                            while(nextCh != ',')
                            {
                                firstNum += regex.charAt(index);
                                index++;
                                nextCh = regex.charAt(index);
                            }

                            index++;
                            nextCh = regex.charAt(index);
                            while(nextCh != '}')
                            {
                                secondNum += regex.charAt(index);
                                index++;
                                nextCh = regex.charAt(index);
                            }

                            index++;
                            int lowerLimit = Integer.parseInt(firstNum);
                            int upperLimit = Integer.parseInt(secondNum);

                            int randomCounter = randomNoGenerator.nextInt(upperLimit - lowerLimit) + lowerLimit;
                            for(int i = 0; i <= randomCounter; i++)
                            {
                                //Pick one random character from the character class
                                int randomIndex = randomNoGenerator.nextInt(group.length());
                                resultantString += group.charAt(randomIndex);
                            }
                        }
                        else if(ch == '?')
                        {
                            if(useOptionality)
                            {
                                //Pick one random character from the character class
                                int randomIndex = randomNoGenerator.nextInt(group.length());
                                resultantString += group.charAt(randomIndex);
                            }
                        }
                        else if(ch == '$')
                        {
                            //Pick one random character from the character class
                            int randomIndex = randomNoGenerator.nextInt(group.length());
                            resultantString += group.charAt(randomIndex);
                        }
                        else
                        {
                            //Pick one random character from the character class
                            int randomIndex = randomNoGenerator.nextInt(group.length());
                            resultantString += group.charAt(randomIndex);
                        }    
                    }
                }

                //Group checks

                //If start of character class '['
                else if(ch == '(')
                {                
                    //Get all the elements in the character class
                    index++;
                    ch = regex.charAt(index);

                    while(ch != ')')
                    {
                        group += ch;
                        index++;
                        ch = regex.charAt(index);
                    }

                    //Check if there is no input remaing
                    if(index == regex.length())
                    {
                        resultantString += group;
                    }
                    else
                    {
                        //Check if there is closure symbol in the end
                        index++;
                        ch = regex.charAt(index);

                        if(ch == '*')
                        {
                            for(int i = 0; i < iterator; i++)
                            {
                                resultantString += group;
                            }
                        }
                        else if(ch == '+')
                        {
                            for(int i = 0; i <= iterator; i++)
                            {
                                resultantString += group;
                            }
                        }
                        else if(ch == '{')
                        {
                            String firstNum = "", secondNum = "";
                            index++;
                            char nextCh = regex.charAt(index);
                            while(nextCh != ',')
                            {
                                index++;
                                firstNum += regex.charAt(index);
                            }

                            index++;
                            nextCh = regex.charAt(index);
                            while(nextCh != '}')
                            {
                                index++;
                                secondNum += regex.charAt(index);
                            }

                            index++;
                            int lowerLimit = Integer.parseInt(firstNum);
                            int upperLimit = Integer.parseInt(secondNum);

                            int randomCounter = randomNoGenerator.nextInt(upperLimit - lowerLimit) + lowerLimit;
                            for(int i = 0; i <= randomCounter; i++)
                            {
                                resultantString += group;
                            }
                        }
                        else if(ch == '?')
                        {
                            if(useOptionality)
                            {
                                resultantString += group;
                            }
                        }
                        else if(ch == '$')
                        {
                            resultantString += group;
                        }
                        else
                        {
                            resultantString += group;
                        }    
                    }

                }

                //Direct character match
                else 
                {
                    while(index < regex.length())
                    {
                        char regexChar = regex.charAt(index);
                        index++;

                        if((regexChar == '(') || (regexChar == '['))
                        {
                            index--;
                            break;
                        }
                        else
                        {
                            char closure = 'E';
                            //Check if there is closure symbol
                            if(index < regex.length())
                            {
                                char nextChar = regex.charAt(index);
                                if((nextChar == '*') || (nextChar == '+') || (nextChar == '?'))
                                {
                                    closure = nextChar;
                                    index++;
                                }
                            }

                            //Check if meta characters are used
                            if(regexChar == '.')
                            {
                                //Everything matches '.'

                                //Check if closure was used
                                if((closure != 'E') && (closure != '?'))
                                {
                                    //Anything can be added since '.' is present
                                    resultantString += '0';
                                }
                            }
                            else
                            {
                                if(closure == '*')
                                {
                                    for(int i = 0; i < iterator; i++)
                                    {
                                        resultantString += regexChar;
                                    }
                                }
                                else if(closure == '+')
                                {
                                    for(int i = 0; i <= iterator; i++)
                                    {
                                        resultantString += regexChar;
                                    }
                                }
                                else if(closure == '?')
                                {
                                    if(useOptionality)
                                    {
                                        resultantString += regexChar;
                                    }
                                }
                                else
                                {
                                    resultantString += regexChar;
                                }
                            }
                        }

                    }

                }

                group = "";
                index++;
            }
            
            try
            {
                writer.write(resultantString);
                writer.newLine();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            
            iterator++;
            
            //If the requried number of combinations are generated
            if(resultantString.length() >= REGEX_COMBINATIONS_LENGTH)
            {
                break;
            }
        }
    }
    
    public static void main(String[] args)
    {
        RegExEngine regex = new RegExEngine();
        regex.regex = "a+bc";
        regex.generateFile();
    }   
}