package file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

public class filemethods 
{
	public static boolean is_file(String input)
	{
		File f = new File(input);
		return f.exists();
	}
	public static void create_file(String input) throws IOException
	{
		File f = new File(input);
		f.createNewFile();
	}
	public static LinkedList<String> tokenize(String input) throws IOException
	{
		LinkedList<String> tokens = new LinkedList<String>();
		if(is_file(input))
		{
			FileReader fr = new FileReader(input);
			BufferedReader br = new BufferedReader(fr);
			int c;
			String s = "";
			while((c = br.read()) != -1)
			{
				char ch = (char)c;
				if(ch == ' ' || ch == '\n' || ch == ';')
				{
					if(!s.equals(" ") && !s.equals(""))
					{
						tokens.add(s);
						s = "";
					}
				}
				else if(ch >= 'a' && ch <= 'z' || ch > 'A' && ch <= 'Z' || ch >= '0' || ch <= '9' || ch == ':' || ch == '[' || ch == ']')
				{
					s += ch;
				}
			}
		}
		else
		{
			System.err.println("Error: File named " + input.toString() +  " not found.");
		}
		return tokens;
	}
	public static void main(String args[]) throws IOException
	{
		String s = "Cool_my_julios.txt";
		System.out.println(is_file(s));
		create_file(s);
		System.out.println(is_file(s));
	}
}
