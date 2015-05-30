package regex.engine;

import java.util.Stack;

public class RegExEngine extends Thread 
{
    public String regex;
    public String test;

    @Override
    public void run() 
    {
        parseStringRegexEngine();
    }
    
    public boolean parseStringRegexEngine()
    {
        Stack stack = new Stack();
        boolean characterClass = false;
        //Parse the Regular Expression
        int index = 0;
        int indexTestString = 0;
        
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
            
            //If start of character class '[' or start of group '('
            if(ch == '[' || ch == '(')
            {
                characterClass = true;
                stack.push(index);
                stack.push('[');
            }
            
            //If end of character class ']' or end of group ')'
            else if(ch == ']' || ch == ')')
            {
                if(stack.empty())
                {
                    System.err.println("Error: Unmatched parenthesis.");
                    return false;
                }
                else
                {
                    char c = (char) stack.pop();
                    if( ((ch == ']') && (c != '[')) || ((ch == ')') && (c != '(')) )
                    {
                        System.err.println("Error: Unmatched parenthesis.");
                        return false;
                    }
                    
                    int prevBracketIndex = (int) stack.pop();
                    
                    //If there are more characters left to be parsed
                    if((index+1) < regex.length())
                    {
                        //Check if there is any closure symbol onto the whole group
                        char chNext = regex.charAt(index + 1);
                        if((chNext == '*') || (chNext == '+') || (chNext == '{'))
                        {
                            index = prevBracketIndex;
                            stack.push(index);
                            stack.push('[');
                        }
                    }
                    
                    characterClass = false;
                }
            }
            
            //If character class
            else if(characterClass)
            {
                //There is only one meta character in a character class i.e. '^'
                if(ch == '^')
                {
                    
                }
                
                //If escape character is used
                else if(ch == '/')
                {
                    
                }
                
                //Treat them as normal characters
                else
                {
                    
                }
            }
            
            indexTestString++;
            index++;
        }
        
        return true;
    }
    
    public static void main(String[] args)
    {
        
    }
    
}
