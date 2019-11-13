package Binary;
public class greycodes 
{
	//greycode functions to go from binary to greycodes
	public static String gcode(String str)
	{
		String answer = "" + str.charAt(0);
		final int len = str.length();
		for(int i = 1; i < len; i++)
		{
			answer += (str.charAt(i) != str.charAt(i - 1)) ? '1' : '0';
		}
		return answer;
	}
	
	//Uncode functions to convert from greycodes back to regular binary equivalents
	public static String uncode(String str) {
		String answer = "" + str.charAt(0);
		int size = str.length();
		for(int i = 1; i < size; i++)
		{
			answer += (str.charAt(i) != answer.charAt(i - 1)) ? '1' : '0';
		}
		return answer;
	}
	public static String next_gcode(String prev)
	{
		final int size = prev.length();
		int inter = formatting.str2Bin(formatting.chop_str(uncode(prev)));
		inter++;
		String result = gcode(formatting.bin2Str(inter));
		return formatting.pad_str(result, size);
	}
	public static void main(String args[]) {
		System.out.println(gcode("10"));
		System.out.println(uncode("11"));
		System.out.println(next_gcode("1010"));
	}
}
