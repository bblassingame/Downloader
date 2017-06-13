package com.blassingame.downloader.logger;

import java.util.Iterator;

import com.blassingame.downloader.application.Constants;
import com.blassingame.downloader.utility.FileUtility;

public class LogMgr implements Runnable
{
	
	private LogMgr()
	{
	}

/********************************************************************************/
//	THREAD FUNCTIONS
/********************************************************************************/
	@Override
	public void run()
	{
		LogDebug( "Beginning the LogMgr thread...", this );
		
		while( GetContinue() )
		{
			ProcessQueues();
			Wait(250);
		}
		
		// process the queue one last time to log any completed downloads
		ProcessQueues();
	}
	
	public void ShutDown()
	{
		SetContinue( false );
	}

/********************************************************************************/
//	CONSUMER API
/********************************************************************************/
	public static LogMgr GetLogMgr()
	{
		return m_sLogMgr;
	}
	
	public void LogMessage( String strMessage, Object obj, eCONSOLE_LOG_LEVEL eLogLevel )
	{
		LogData logData = new LogData();
		logData.m_strLogMessage = strMessage;
		logData.m_eLogLevel = eLogLevel;
		logData.m_strClassName = obj.getClass().getSimpleName();
		m_qLogData.add( logData );
	}
	
	public void LogMessage( String strMessage, String strClassName, eCONSOLE_LOG_LEVEL eLogLevel )
	{
		LogData logData = new LogData();
		logData.m_strLogMessage = strMessage;
		logData.m_eLogLevel = eLogLevel;
		logData.m_strClassName = strClassName;
		m_qLogData.add( logData );
	}
	
	// this is the lowest level of debugging where everything will be output to the console
	public synchronized void LogAll( String strMessage, Object obj )
	{
		LogData logData = new LogData();
		logData.m_strLogMessage = strMessage;
		logData.m_eLogLevel = eCONSOLE_LOG_LEVEL.eCONSOLE_LOG_LEVEL_ALL;
		logData.m_strClassName = obj.getClass().getSimpleName();
		m_qLogData.add( logData );
	}
	
	public synchronized void LogAll( String strMessage, String strClassName )
	{
		LogData logData = new LogData();
		logData.m_strLogMessage = strMessage;
		logData.m_eLogLevel = eCONSOLE_LOG_LEVEL.eCONSOLE_LOG_LEVEL_ALL;
		logData.m_strClassName = strClassName;
		m_qLogData.add( logData );
	}
	
	// this level of logging should log stuff that's pertinent to what we've recently done
	// but still want to have some visibility in.
	public synchronized void LogDebug( String strMessage, Object obj )
	{
		LogData logData = new LogData();
		logData.m_strLogMessage = strMessage;
		logData.m_eLogLevel = eCONSOLE_LOG_LEVEL.eCONSOLE_LOG_LEVEL_DEBUG;
		logData.m_strClassName = obj.getClass().getSimpleName();
		m_qLogData.add( logData );
	}
	
	public synchronized void LogDebug( String strMessage, String strClassName )
	{
		LogData logData = new LogData();
		logData.m_strLogMessage = strMessage;
		logData.m_eLogLevel = eCONSOLE_LOG_LEVEL.eCONSOLE_LOG_LEVEL_DEBUG;
		logData.m_strClassName = strClassName;
		m_qLogData.add( logData );
	}
	
	// this should be used to make sure that we see exceptions that we're throwing so that we
	// can take action to correct them
	public synchronized void LogError( String strMessage, Object obj )
	{
		LogData logData = new LogData();
		logData.m_strLogMessage = strMessage;
		logData.m_eLogLevel = eCONSOLE_LOG_LEVEL.eCONSOLE_LOG_LEVEL_ERROR;
		logData.m_strClassName = obj.getClass().getSimpleName();
		m_qLogData.add( logData );
	}
	
	public synchronized void LogError( String strMessage, String strClassName )
	{
		LogData logData = new LogData();
		logData.m_strLogMessage = strMessage;
		logData.m_eLogLevel = eCONSOLE_LOG_LEVEL.eCONSOLE_LOG_LEVEL_ERROR;
		logData.m_strClassName = strClassName;
		m_qLogData.add( logData );
	}
	
	// this is really for what you're immediately working on right now.
	public synchronized void LogInfo( String strMessage, Object obj )
	{
		LogData logData = new LogData();
		logData.m_strLogMessage = strMessage;
		logData.m_eLogLevel = eCONSOLE_LOG_LEVEL.eCONSOLE_LOG_LEVEL_INFO;
		logData.m_strClassName = obj.getClass().getSimpleName();
		m_qLogData.add( logData );
	}
	
	public synchronized void LogInfo( String strMessage, String strClassName )
	{
		LogData logData = new LogData();
		logData.m_strLogMessage = strMessage;
		logData.m_eLogLevel = eCONSOLE_LOG_LEVEL.eCONSOLE_LOG_LEVEL_ERROR;
		logData.m_strClassName = strClassName;
		m_qLogData.add( logData );
	}
	
/********************************************************************************/
//	INTERNAL FUNCTIONS
/********************************************************************************/
	private synchronized void ProcessQueues()
	{
		LogAll( "\nProcessing Log Queue...", this );

		for( Iterator<LogData> it = m_qLogData.iterator() ; it.hasNext() ; )
		{
			LogData logData = it.next();
			
			String strOutput = ComposeMessage( logData );

			// log it to the console according to the log level we've got
			if( logData.m_eLogLevel.m_nLogLevel <= m_eConsoleLoggingLevel.m_nLogLevel )
			{
				System.out.println( strOutput );
				FileUtility.WriteFile( m_strLogFilePath, strOutput );
			}
			
			// finally, remove the log data from the queue
			it.remove();
		}
	}
	
	private String ComposeMessage( LogData logData )
	{
		StringBuffer strBuffer = new StringBuffer();
		
		if( !logData.m_strClassName.isEmpty() )
			strBuffer.append( logData.m_strClassName + "." );
		else
			strBuffer.append( "<class_name>." );
		
		if( !logData.m_strMethodName.isEmpty() )
			strBuffer.append( logData.m_strMethodName + "  " );
		else
			strBuffer.append( "<method_name>  ");
		
		if( !logData.m_strLogMessage.isEmpty() )
		{
			strBuffer.append( logData.m_strLogMessage );
		}
		else
			strBuffer.append( "<message>" );				
		
		if( strBuffer.lastIndexOf("\n") != logData.m_strLogMessage.length() - 1 )
			strBuffer.append( "\n" );

		return strBuffer.toString();
	}
	
/********************************************************************************/
//	OVERRIDES
/********************************************************************************/
	private void Wait( long timeout )
	{
		try
		{
			Thread.sleep( 250 );
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
	
/********************************************************************************/
//	GETTERS/SETTERS
/********************************************************************************/
	public synchronized boolean GetContinue()
	{
		return m_bContinue;
	}
	
	public synchronized void SetContinue( boolean bContinue )
	{
		m_bContinue = bContinue;
	}
	
	public synchronized void SetConsoleLogLevel( eCONSOLE_LOG_LEVEL eLogLevel )
	{
		m_eConsoleLoggingLevel = eLogLevel;
	}

/********************************************************************************/
//	MEMBERS
/*******************************************************************************/
	private static LogMgr m_sLogMgr = new LogMgr();
	private eCONSOLE_LOG_LEVEL m_eConsoleLoggingLevel = eCONSOLE_LOG_LEVEL.eCONSOLE_LOG_LEVEL_NONE;
	private String m_strLogFilePath = Constants.strLOG_PATH + "DownloaderLog.txt";
	
	public enum eCONSOLE_LOG_LEVEL
	{
		eCONSOLE_LOG_LEVEL_NONE(0),
		eCONSOLE_LOG_LEVEL_INFO(1),
		eCONSOLE_LOG_LEVEL_ERROR(2),
		eCONSOLE_LOG_LEVEL_DEBUG(3),
		eCONSOLE_LOG_LEVEL_ALL(4);
		
		eCONSOLE_LOG_LEVEL( int nValue )
		{
			m_nLogLevel = nValue;
		}
		
		public int m_nLogLevel;
	};
	
	// Queues for moving downloads around as they're processed
	private LogDataQueue m_qLogData = new LogDataQueue();
	
	// local thread members
	boolean m_bContinue = true;
	
}
