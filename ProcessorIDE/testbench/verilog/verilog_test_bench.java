package verilog;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import Binary.formatting;
import file.filemethods;

public class verilog_test_bench
{
	private String file_to_test;
	private LinkedList<vnode> inputs;
	private LinkedList<vnode> outputs;
	public verilog_test_bench(String file_name)
	{
		File file_to_test = new File(file_name);
		this.file_to_test = file_to_test.getAbsolutePath();
		outputs = new LinkedList
	}
	public static void filter(Iterator tokens)
	{
		Iterator begin = tokens;
		while(tokens.hasNext())
		{
			String s = ((String)tokens.next()).toLowerCase();
			if(s.equals("module"))
			{
				char c = ((String)tokens.next()).charAt(0);
				if(c == '[')
				{
					tokens.next();
				}
				continue;
			}
			else if(s.equals("input"))
			{
				char c = ((String)tokens.next()).charAt(0);
				if(c == '[')
				{
					tokens.next();
				}
				continue;
			}
			else if(s.equals("output"))
			{
				char c = ((String)tokens.next()).charAt(0);
				if(c == '[')
				{
					tokens.next();
				}
				continue;
			}
			else if(s.equals("inout"))
			{
				char c = ((String)tokens.next()).charAt(0);
				if(c == '[')
				{
					tokens.next();
				}
				continue;
			}
			else
			{
				if(tokens.equals(begin))
				{
					begin.next();
				}
				tokens.remove();
			}
		}
	}
	private static int get_size(String s)
	{
		String par1 = "";
		String par2 = "";
		int i = 1;
		while(s.charAt(i) != ':')
		{
			par1 += s.charAt(i);
			i++;
		}
		++i;
		while(s.charAt(i) != ']')
		{
			par2 += s.charAt(i);
			i++;
		}
		int p1 = formatting.str_2Int(par1);
		int p2 = formatting.str_2Int(par2);
		int sum =  p1 - p2;
		return (sum > 0) ? sum : -sum;
	}
	private void sort_data(Iterator s)
	{
		while(s.hasNext())
		{
			String ss = ((String)s.next()).toLowerCase();
			if(ss.equals("input"))
			{
				String name;
				int size;
				ss = ((String)s.next()).toLowerCase();
				if(ss.charAt(0) == '[')
				{
					
				}
				s.remove();
			}
		}
	}
	public static void main(String args[]) throws IOException
	{
		LinkedList<String> l = filemethods.tokenize("Cool_my_julios.txt");
		System.out.println(l);
		filter(l.iterator());
		System.out.println(l);
	}
}
