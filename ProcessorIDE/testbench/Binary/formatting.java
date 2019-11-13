package Binary;
import java.lang.String;
public class formatting 
{
	public static String bin2Str(int input){
		String output = "";
		while(input > 0) {
			output = ((char)((input % 2) + (int)'0')) + output;
			input /= 2;
		}
		return output;
	}
	public static int str2Bin(String input){
		int output = 0;
		int length = input.length();
		for(int i = 0; i < length; i++){
			output += ((int)input.charAt(i) - (int)'0') * (int)Math.pow((double)2, (double)(length - (i + 1)));
		}
		return output;
	}
	public static String pad_str(String bin, int numpad){
		numpad = numpad - bin.length();
		for(int i = 0; i < numpad; i++){
			bin = '0' + bin;
		}
		return bin;
	}
	public static int str_2Int(String s)
	{
		int ans = 0;
		int len = s.length();
		for(int i = len - 1; i >= 0; i++)
		{
			ans += ((s.charAt(i) - '0') * Math.pow(10, (len - (i + 1))));
		}
		return ans;
	}
	public static String chop_str(String bin){
		return (bin.charAt(0) == '0') ? chop_str(bin.substring(1)) : bin;
	}
	public static void main(String[] args)
	{
		System.out.println(str2Bin("0011"));
		System.out.println(bin2Str(3));
		System.out.println(pad_str("0011", 4));
		System.out.println(chop_str("010101010101001101"));
	}
}
