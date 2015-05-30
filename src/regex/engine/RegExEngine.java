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
                stack.push(index);
                stack.push('[');
                
                //Get all the elements in the character class
                index++;
                ch = regex.charAt(index);
                
                while(ch != ']')
                {
                    group += ch;
                    index++;
                    ch = regex.charAt(index);
                }
                
                //Check if there is no input remaing
                if(index == (regex.length() - 1))
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
                                    break;
                                }
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
                            if(indexTestString < (test.length() - 1))
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
                            if(indexTestString < (test.length() - 1))
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
            
            //Direct character match
            else 
            {
                while((index < (regex.length() - 1)) && (indexTestString < (test.length() - 1)))
                {
                    char testStringChar = test.charAt(indexTestString);
                    indexTestString++;
                    char regexChar = regex.charAt(index);
                    index++;
                    
                    if((regexChar == '(') || (regexChar == '['))
                    {
                        indexTestString--;
                        index--;
                        
                        break;
                    }
                    else
                    {
                        char closure = 'E';
                        //Check if there is closure symbol
                        if(index < (regex.length() - 1))
                        {
                            char nextChar = regex.charAt(index);
                            if((nextChar == '*') || (nextChar == '+'))
                            {
                                closure = regexChar;
                                index++;
                            }
                        }
                        
                        //Check if meta characters are used
                        if(regexChar == '.')
                        {
                            //Everything matches '.'
                            
                            //Check if closure was used
                            if(closure != 'E')
                            {
                                //Everything will match with .
                                return true;
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
            
            index++;
        }
        
        return true;
    }
    
    public static void main(String[] args)
    {
        
    }
    
}
