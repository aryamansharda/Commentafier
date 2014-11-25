import java.lang.*;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class Commentafier
{
	public static int charLimit = 80;
        public static String filename;
	public static String name;
	public static String userAccount;
	public static ArrayList<String> codeData;
	public static String fileHeaderBegin = "/***************"
		+ "*************************************************************\n\n";
	public static String fileHeaderClose = "\n***************"
		+"*************************************************************/";
	public static String dueDate;
	public static String classCode = "CSE 12";
	public static String quarter = "Fall 2014";
	public static String assignmentNumber;


	public Commentafier (String fileName)
	{
		Scanner input = new Scanner(System.in);
                filename = fileName; 
		name = JOptionPane.showInputDialog(null, "Please enter your full name:");               		;

		if (isFullname(name))
		{
			try
			{
				Scanner codeFile = new Scanner(new File(fileName));
				codeData = new ArrayList<String>();

				while(codeFile.hasNextLine()){
				    String line = codeFile.nextLine();

				    Scanner scanner = new Scanner(line);
				    scanner.useDelimiter("\n");

				    while(scanner.hasNextLine()){
				        codeData.add(scanner.nextLine() + "\n");
				    }
				    scanner.close();
				}
				codeFile.close();
                                
				dueDate = JOptionPane.showInputDialog(null, "Please enter the due date of this assignment: ");               		                                				
                                userAccount = JOptionPane.showInputDialog(null, "Please enter your user account: ");               		                                				
                                assignmentNumber = JOptionPane.showInputDialog(null, "What assignment number is this? ");               		                                												
				
				addFileHeader();
				addFunctionHeader();
				addVariableComments();
				addStructHeader();

				System.out.println("Would you like to recieve warning on any line that surpasses the 80 char limit?");
				System.out.print("(y)/(n) : ");
				String answer = input.nextLine();
				if(answer.toLowerCase().contains("y"))
					charLimitCheck();
				else
					System.out.println("Did not specify yes, will skip the 80 char limit check.");

				writeToFile();
				System.out.println("Operation successful. Please reopen file to see comment templates.");
			}
			catch (Exception e)
			{
				throw new RuntimeException("Error: ", e); 
			}
		}
		
	}

	public static String alignTextRight(String text)
	{
		int textLength = text.length();
		int numOfSpaces = (charLimit-textLength)-5;
		String newText = "";
		for(int i =0; i<numOfSpaces; i++)
		{
			newText = newText + " ";
		}
		newText = newText + text;
		return newText;
	}

	public static String alignTextMiddle(String text)
	{
		int textLength = text.length();
		int numOfSpaces = (charLimit-textLength)-5;
		numOfSpaces = numOfSpaces/2;
		String newText = "";
		for(int i =0; i<numOfSpaces; i++)
		{
			newText = newText + " ";
		}
		newText = newText + text;
		return newText;
	}

	public static void addFileHeader()
	{
		String fileHeaderString = "";
		fileHeaderString = fileHeaderString + fileHeaderBegin;		

		String newName = alignTextRight(name);
		fileHeaderString = fileHeaderString + newName+ "\n";
		
		String classAndQuarter = classCode + ", " + quarter;
		classAndQuarter = alignTextRight(classAndQuarter);
		fileHeaderString = fileHeaderString + classAndQuarter + "\n";
		
		String newDueDate = alignTextRight(dueDate);
		fileHeaderString = fileHeaderString + newDueDate + "\n";

		String newUserAccount = alignTextRight(userAccount);
		fileHeaderString = fileHeaderString + newUserAccount + "\n";

		String newAssignmentNumber = "Assignment "+assignmentNumber;
		newAssignmentNumber = alignTextMiddle(newAssignmentNumber);
		fileHeaderString = fileHeaderString + newAssignmentNumber + "\n\n";

		fileHeaderString = fileHeaderString + "File Name:		" + "(file name)" + "\n";
		fileHeaderString = fileHeaderString + "Description:		(enter description here)" + "\n";
		fileHeaderString = fileHeaderString + fileHeaderClose + "\n";

		String indexZero = codeData.get(0);
		indexZero = fileHeaderString + indexZero;
		codeData.set(0,indexZero);
	}

	public static void addFunctionHeader()
	{
		int pos = 0;

		for (String s : codeData)
		{
			pos++;

    		if ((s.toLowerCase().contains("()") || s.toLowerCase().contains("public") || s.toLowerCase().contains("static") || s.toLowerCase().contains(",") ||  s.toLowerCase().contains("void") ||  s.toLowerCase().contains("int"))
    			&& (s.toLowerCase().contains(";") == false && s.toLowerCase().contains("if") == false && s.toLowerCase().contains("for") == false
    			&& s.toLowerCase().contains("while") == false && s.toLowerCase().contains("//") == false))
    		{
    			String functionHeader = "\n/*---------------------------------------------------------------------------\nFunction Name:                \nPurpose:                      \nDescription:                      \nInput:                      \nOutput:                       \nResult:                      \nSide Effects:\n---------------------------------------------------------------------------*/\n";
    			codeData.set(pos - 1, functionHeader + s);    			    		
    		}
		}
	}
	public static void addStructHeader()
	{
		int pos = 0;
		String firstPart = "//============================================================================\n";
		String secondPart = "// struct (your struct name)\n"+"//\n";
		String thirdPart = "// Description: [Brief Description of your struct]\n//\n";
		String fourthPart = "// Data Fields: \n//	[field name](field Type) - [description]\n//\n";
		String fifthPart = "// Public Functions:\n//	[function name] - [functionality]\n//\n";
		
		for (String s : codeData)
		{
			pos++;

			if(s.toLowerCase().contains("struct ") 
				&& !(s.toLowerCase().contains("/")) 
				&& !(s.toLowerCase().contains(";")))
			{

    			String structHeader = firstPart+secondPart+thirdPart+fourthPart+fifthPart+firstPart;
    			codeData.set(pos - 1, structHeader + s);    			    		
    		}
		}
	}

	public static void charLimitCheck()
	{
		int pos = 0;
		for(String s : codeData)
		{
			if(s.length() > charLimit)
			{
				String halfLength = s.substring(0,s.length()/2);
				System.out.println("Line " + pos + ": " + halfLength + "..." );
			}				
			pos++;
		}
	}

	public static void addVariableComments()
	{
		int pos = 0;

		for (String s : codeData)
		{
			pos++;

    		if ((s.contains("int") || s.contains("for") || s.contains("while") | s.contains("byte") 
    			|| s.contains("short") || s.contains("long") || s.contains("float") || s.contains("double") || s.contains("boolean") || s.contains("char")) && s.contains(";"))
    		{
    			String variableHeader = "\n//TODO\n";
    			codeData.set(pos - 1, variableHeader + s);         
    		}
		}
	}

	public static boolean isFullname(String str) 
	{
		//Add dash/hyphen
 	    String expression = "^[a-zA-Z\\s]+"; 
    	return str.matches(expression);        
	}

	public static void writeToFile()
	{
                System.out.println("Here");
		try
		{
			FileWriter writer = new FileWriter( filename + "HCTree2.cpp"); 

			for(String str: codeData) {
	  			writer.write(str);
			}	
			
			writer.close();
		}
		catch (Exception e)
		{
			throw new RuntimeException("Error: ", e); 
		}

		
	}
}