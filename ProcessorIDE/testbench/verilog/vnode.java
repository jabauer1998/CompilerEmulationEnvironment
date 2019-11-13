package verilog;

public class vnode 
{
	private String name;
	private int size;
	public vnode(String name, int size)
	{
		this.name = name;
		this.size = size;
	}
	public String get_name()
	{
		return name;
	}
	public int get_size()
	{
		return size;
	}
}
