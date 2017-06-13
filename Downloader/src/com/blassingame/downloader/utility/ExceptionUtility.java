package com.blassingame.downloader.utility;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.ini4j.InvalidFileFormatException;

import com.blassingame.downloader.logger.LogMgr;

public class ExceptionUtility
{

	public static void DoExceptionWork( Exception e, String strMsg )
	{
		String strCause = "";
		if( null != e.getCause() )
			strCause = e.getCause().toString();
		else
			strCause = "Cause Undefined";
		
		LogMgr logMgr = LogMgr.GetLogMgr();
		logMgr.LogError( "General Exception logged with cause:  " + strCause, "com.blassingame.downloader.utility.ExceptionUtility" );
		
		String strLog = "*****General Exception Log*****\r\n";
		strLog += GetTimeStamp() + "\r\n";
		strLog += "Calling Function Message:  " + strMsg + "\r\n";
		strLog += e.getMessage() + "\r\n";
		strLog += strCause + "\r\n";
		strLog += "\r\n**Begin Stack Trace**\r\n";
		StackTraceElement[] Elements = e.getStackTrace();
		for( StackTraceElement Elem : Elements )
		{
			strLog += Elem.toString() + "\r\n";
		}
		
		strLog += "\r\n\r\n";
		
		FileUtility.WriteFile( m_strLogPath, strLog );
	}
	
	public static void DoIOExceptionWork( IOException e, String strMsg )
	{
		String strCause = "";
		if( null != e.getCause() )
			strCause = e.getCause().toString();
		else
			strCause = "Cause Undefined";
		
		LogMgr logMgr = LogMgr.GetLogMgr();
		logMgr.LogError( "IO Exception logged with cause:  " + strCause, "com.blassingame.downloader.utlity.ExceptionUtlity" );
		
		String strLog = "*****IO Exception Log*****\r\n";
		strLog += GetTimeStamp() + "\r\n";
		strLog += "Calling Function Message:  " + strMsg + "\r\n";
		strLog += e.getMessage() + "\r\n";
		strLog += strCause + "\r\n";
		strLog += "\r\n**Begin Stack Trace**\r\n";
		StackTraceElement[] Elements = e.getStackTrace();
		for( StackTraceElement Elem : Elements )
		{
			strLog += Elem.toString() + "\r\n";
		}
		
		strLog += "\r\n\r\n";
		
		FileUtility.WriteFile( m_strLogPath, strLog );
	}
	
	public static void DoProtExceptionWork( ProtocolException e, String strMsg )
	{
		String strCause = "";
		if( null != e.getCause() )
			strCause = e.getCause().toString();
		else
			strCause = "Cause Undefined";
		
		LogMgr logMgr = LogMgr.GetLogMgr();
		logMgr.LogError( "Protocol Exception logged with cause:  " + strCause, "com.blassingame.downloader.utility.ExceptionUtility" );
		
		String strLog = "*****Protocol Exception Log*****\r\n";
		strLog += GetTimeStamp() + "\r\n";
		strLog += "Calling Function Message:  " + strMsg + "\r\n";
		strLog += e.getMessage() + "\r\n";
		strLog += strCause + "\r\n";
		strLog += "\r\n**Begin Stack Trace**\r\n";
		StackTraceElement[] Elements = e.getStackTrace();
		for( StackTraceElement Elem : Elements )
		{
			strLog += Elem.toString() + "\r\n";
		}
		
		strLog += "\r\n\r\n";
		
		FileUtility.WriteFile( m_strLogPath, strLog );
	}
	
	public static void DoFNFExceptionWork( FileNotFoundException e, String strMsg )
	{
		String strCause = "";
		if( null != e.getCause() )
			strCause = e.getCause().toString();
		else
			strCause = "Cause Undefined";
		
		LogMgr logMgr = LogMgr.GetLogMgr();
		logMgr.LogError( "File Not Found Exception logged with cause:  " + strCause,  "com.blassingame.downloader.utility.ExceptionUtlity" );
		
		String strLog = "*****File Not Found Exception Log*****\r\n";
		strLog += GetTimeStamp() + "\r\n";
		strLog += "Calling Function Message:  " + strMsg + "\r\n";
		strLog += e.getMessage() + "\r\n";
		strLog += strCause + "\r\n";
		strLog += "\r\n**Begin Stack Trace**\r\n";
		StackTraceElement[] Elements = e.getStackTrace();
		for( StackTraceElement Elem : Elements )
		{
			strLog += Elem.toString() + "\r\n";
		}
		
		strLog += "\r\n\r\n";
		
		FileUtility.WriteFile( m_strLogPath, strLog );
	}
	
	public static void DoURLExceptionWork( MalformedURLException e, String strMsg )
	{
		String strCause = "";
		if( null != e.getCause() )
			strCause = e.getCause().toString();
		else
			strCause = "Cause Undefined";
		
		LogMgr logMgr = LogMgr.GetLogMgr();
		logMgr.LogError( "URL Exception logged with cause:  " + strCause, "com.blassingame.downloader.utility.ExceptionUtility" );
		
		String strLog = "*****URL Exception Log*****\r\n";
		strLog += GetTimeStamp() + "\r\n";
		strLog += "Calling Function Message:  " + strMsg + "\r\n";
		strLog += e.getMessage() + "\r\n";
		strLog += strCause + "\r\n";
		strLog += "\r\n**Begin Stack Trace**\r\n";
		StackTraceElement[] Elements = e.getStackTrace();
		for( StackTraceElement Elem : Elements )
		{
			strLog += Elem.toString() + "\r\n";
		}
		
		strLog += "\r\n\r\n";
		
		FileUtility.WriteFile( m_strLogPath, strLog );
	}
	
	public static void DoInvalidFileFormatExceptionWork( InvalidFileFormatException e, String strMsg )
	{
		String strCause = "";
		if( null != e.getCause() )
			strCause = e.getCause().toString();
		else
			strCause = "Cause Undefined";
		
		LogMgr logMgr = LogMgr.GetLogMgr();
		logMgr.LogError( "Invalid File Format Exception logged with cause:  " + strCause, "com.blassingame.downloader.utility.ExceptionUtility" );
		
		String strLog = "*****Invalid File Format Exception Log*****\r\n";
		strLog += GetTimeStamp() + "\r\n";
		strLog += "Calling Function Message:  " + strMsg + "\r\n";
		strLog += e.getMessage() + "\r\n";
		strLog += strCause + "\r\n";
		strLog += "\r\n**Begin Stack Trace**\r\n";
		StackTraceElement[] Elements = e.getStackTrace();
		for( StackTraceElement Elem : Elements )
		{
			strLog += Elem.toString() + "\r\n";
		}
		
		strLog += "\r\n\r\n";
		
		FileUtility.WriteFile( m_strLogPath, strLog );
	}
	
	public static void DoInterruptedExceptionWork( InterruptedException e, String strMsg )
	{
		String strCause = "";
		if( null != e.getCause() )
			strCause = e.getCause().toString();
		else
			strCause = "Cause Undefined";
		
		LogMgr logMgr = LogMgr.GetLogMgr();
		logMgr.LogError( "Interrupt Exception logged with cause:  " + strCause, "com.blassingame.downloader.utility.ExceptionUtility" );
		
		String strLog = "*****Interrupt Exception Log*****\r\n";
		strLog += GetTimeStamp() + "\r\n";
		strLog += "Calling Function Message:  " + strMsg + "\r\n";
		strLog += e.getMessage() + "\r\n";
		strLog += strCause + "\r\n";
		strLog += "\r\n**Begin Stack Trace**\r\n";
		StackTraceElement[] Elements = e.getStackTrace();
		for( StackTraceElement Elem : Elements )
		{
			strLog += Elem.toString() + "\r\n";
		}
		
		strLog += "\r\n\r\n";
		
		FileUtility.WriteFile( m_strLogPath, strLog );
	}
	
	public static void DoNumberFormatExceptionWork( NumberFormatException e, String strMsg )
	{
		String strCause = "";
		if( null != e.getCause() )
			strCause = e.getCause().toString();
		else
			strCause = "Cause Undefined";
		
		LogMgr logMgr = LogMgr.GetLogMgr();
		logMgr.LogError( "Number Format Exception logged with cause:  " + strCause, "com.blassingame.downloader.utility.ExceptionUtility" );
		
		String strLog = "*****Number Format Exception Log*****\r\n";
		strLog += GetTimeStamp() + "\r\n";
		strLog += "Calling Function Message:  " + strMsg + "\r\n";
		strLog += e.getMessage() + "\r\n";
		strLog += strCause + "\r\n";
		strLog += "\r\n**Begin Stack Trace**\r\n";
		StackTraceElement[] Elements = e.getStackTrace();
		for( StackTraceElement Elem : Elements )
		{
			strLog += Elem.toString() + "\r\n";
		}
		
		strLog += "\r\n\r\n";
		
		FileUtility.WriteFile( m_strLogPath, strLog );
	}
	
	private static String GetTimeStamp()
	{
		return new SimpleDateFormat( "yyyy.MM.dd h:mm:ss a" ).format( new Date() );
	}
	
	static private String m_strLogPath = System.getProperty("user.home") + "\\Documents\\Downloader\\Logs\\ExceptionLogging.log";
	
}
