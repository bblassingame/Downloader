import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.ini4j.*;

public class FileUtility
{
	// this is currently used for simply reading a file from disk for parsing
	static public String GetFileContents( String strFilePath )
	{
		BufferedReader br = null;
		String strFileContents = "";
		String strTemp = "";

		try
		{
			br = new BufferedReader( new FileReader( strFilePath ) );

			while ( ( strTemp = br.readLine() ) != null )
			{
				strFileContents += strTemp;
				strFileContents += "\r\n";	// append new line and carriage return for this method because it cuts it off otherwise.
			}
		}
		catch( IOException e )
		{
			e.printStackTrace();
			return "";
		}
		finally
		{
			try
			{
				if ( br != null )
					br.close();
			}
			catch( IOException ex )
			{
				ex.printStackTrace();
				return "";
			}
		}
		
		return strFileContents;		
	}
	
	// this is a simple write a string or other data to disk, not for streaming a file to disk from the internet or something
	static public boolean WriteFile( String strFilePath, String strFileContents )
	{
		try
		{
			File file = new File( strFilePath );
			
			// if file doesnt exists, then create it
			if( !file.exists() )
			{
				file.createNewFile();
			}
			
			FileWriter fw = new FileWriter( file.getAbsoluteFile(), true );
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write( strFileContents );
			bw.close();
			
//			System.out.println("Done");
			return true;
		}
		catch( IOException e )
		{
			System.out.println("----> Error writing to log file");
			e.printStackTrace();
			return false;
		}
	}
	
	static public boolean WriteIniValue( String strFilePath, String strSection, String strKey, String strValue )
	{
		boolean bReturn = false;
		
		try
		{
			File fileLog = new File( strFilePath );
			if( !fileLog.exists() )
			{
				if( !fileLog.createNewFile() )
					return false;
			}
			
			Wini iniFile = new Wini( fileLog );
			
			iniFile.put( strSection, strKey, strValue );
			
			iniFile.store();
			
			bReturn = true;
		}
		catch( InvalidFileFormatException e )
		{
			ExceptionUtility.DoInvalidFileFormatExceptionWork( e, "Exception for FileUtility.WriteIniValue" );
			bReturn = false;
		}
		catch( IOException e )
		{
			ExceptionUtility.DoIOExceptionWork( e, "Exception for FileUtility.WriteIniValue" );
			bReturn = false;
		}
		
		return bReturn;
	}
	
	static public String GetIniValue( String strFilePath, String strSection, String strKey )
	{
		String strReturn = "";
		
		try
		{
			File fileLog = new File( strFilePath );
			if( !fileLog.exists() )
			{
				if( !fileLog.createNewFile() )
					return strReturn;
			}
			
			Wini iniFile = new Wini( fileLog );
			
			strReturn = iniFile.get( strSection, strKey );
		}
		catch( InvalidFileFormatException e )
		{
			ExceptionUtility.DoInvalidFileFormatExceptionWork( e, "Exception for FileUtility.WriteIniValue" );
		}
		catch( IOException e )
		{
			ExceptionUtility.DoIOExceptionWork( e, "Exception for FileUtility.WriteIniValue" );
		}
		
		return strReturn;
	}
	
	static public String[] GetAllSections( String strFilePath )
	{
		String[] astrReturn = null;
		try
		{
			File fileLog = new File( strFilePath );
			if( !fileLog.exists() )
			{
				if( !fileLog.createNewFile() )
					return astrReturn;
			}
			
			Wini iniFile = new Wini( fileLog );
			
			Set<String> setSections = iniFile.keySet();
			astrReturn = new String[setSections.size()];
			
			Iterator<String> it = setSections.iterator();
			for( int i = 0 ; i < setSections.size() ; i++ )
				astrReturn[i] = it.next();
			
			return astrReturn;
		}
		catch( InvalidFileFormatException e )
		{
			ExceptionUtility.DoInvalidFileFormatExceptionWork( e, "Exception for FileUtility.GetAllSections" );
		}
		catch( IOException e )
		{
			ExceptionUtility.DoIOExceptionWork( e, "Exception for FileUtility.GetAllSections" );
		}
		
		return null;
	}
	
	static public String[] GetAllSectionValues( String strFilePath, String strKey )
	{
		String[] astrReturn = null;
		try
		{
			File fileLog = new File( strFilePath );
			if( !fileLog.exists() )
			{
				if( !fileLog.createNewFile() )
					return astrReturn;
			}
			
			Wini iniFile = new Wini( fileLog );
			
			Set<String> setSections = iniFile.keySet();
			
			astrReturn = new String[setSections.size()];
			Iterator<String> it = setSections.iterator();
			for( int i = 0 ; i < setSections.size() ; i++ )
			{
				String strSection = it.next();
				astrReturn[i] = iniFile.get( strSection, strKey );
			}
			
			return astrReturn;
		}
		catch( InvalidFileFormatException e )
		{
			ExceptionUtility.DoInvalidFileFormatExceptionWork( e, "Exception for FileUtility.GetAllSectionValues" );
		}
		catch( IOException e )
		{
			ExceptionUtility.DoIOExceptionWork( e, "Exception for FileUtility.GetAllSectionValues" );
		}
		
		return null;
	}
	
	static public String CreateUniqueFileName( String strFileName, String strExtension, StringArray astrFiles )
	{
		return CreateUniqueFileName( strFileName, strFileName, strExtension, astrFiles, 1 );
	}
	
	static private String CreateUniqueFileName( String strOrigName, String strNewName, String strExtension, StringArray astrFiles, int nCount )
	{		
		for( String strName : astrFiles )
		{
			// first, extract the file name extension and compare.  if they're not the same, we're good
			String strTempExt = strName.substring( strName.lastIndexOf( "." ), strName.length() );
			if( 0 != strTempExt.compareTo( strExtension ) )
				continue;
			
			// now compare the actual file names
			String strTempName = strName.substring( 0, strName.lastIndexOf( "." ) );
			if( 0 == strTempName.compareTo( strNewName ) )
			{
				strNewName = strOrigName + String.format( " (%d)",  nCount );
				return CreateUniqueFileName( strOrigName, strNewName, strExtension, astrFiles, ++nCount );
			}
		}
		
		return strNewName;
	}
	
	static public StringArray GetFilesAndFoldersInDirectory( String strDirectory )
	{
		StringArray aFiles = new StringArray();
		
		File dirParent = new File( strDirectory );
		File[] directoryListing = dirParent.listFiles();
		if( directoryListing != null )
		{
			for( File child : directoryListing )
			{
				if( !child.isDirectory() )
					aFiles.add( child.getName() );
			}
	    }

		return aFiles;
	}
	
	
	static public StringArray GetOnlyFilesInDirectory( String strDirectory )
	{
		StringArray aFiles = new StringArray();
		
		File dirParent = new File( strDirectory );
		File[] directoryListing = dirParent.listFiles();
		if( directoryListing != null )
		{
			for( File child : directoryListing )
			{
				if( !child.isDirectory() )
					aFiles.add( child.getName() );
			}
	    }

		return aFiles;
	}
	
	static public void ArchiveFile( String strOldPath, String strNewPath )
	{
		StringArray astrFiles = GetOnlyFilesInDirectory( strNewPath );
		String strOldName = strOldPath.substring( strOldPath.lastIndexOf( "\\" ) + 1, strOldPath.length() );
		String strNewName = CreateUniqueFileName( strOldName,  "", astrFiles );
		
		try
		{
			File fileOld = new File( strOldPath );
			fileOld.renameTo( new File(strNewPath + strNewName) );
		}
		catch( Exception e )
		{
			e.printStackTrace();
			System.out.println( String.format("We couldn't move a file:\n\tOld File Path:  %s\n\tNew File Path:  %s", strOldPath, strNewPath ) );
		}
	}
	
}

@SuppressWarnings("serial")
class StringArray extends ArrayList<String> {}

