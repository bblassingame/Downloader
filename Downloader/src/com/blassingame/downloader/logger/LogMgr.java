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
		LogMgr logMgr = m_LogMgr;
		
		if( null == logMgr )
		{
			synchronized( lock )
			{
				logMgr = m_LogMgr;
				if( null == logMgr )
				{
					logMgr = new LogMgr();
					m_LogMgr = logMgr;
				}
			}
		}
		
		return logMgr;
	}
	
	public void LogMessage( String strMessage, Object obj, eCONSOLE_LOG_LEVEL eLogLevel )
	{
		LogMessage( strMessage, obj, eLogLevel, false );
	}
	
	public void LogMessage( String strMessage, Object obj, eCONSOLE_LOG_LEVEL eLogLevel, boolean bConsoleOnly )
	{
		LogData logData = new LogData();
		logData.m_strConsoleMessage = strMessage;
		logData.m_eLogLevel = eLogLevel;
		logData.m_strClassName = obj.getClass().getSimpleName();
		logData.m_bConsoleOnly = bConsoleOnly;
		synchronized( lock )
		{
			m_qLogData.add( logData );
		}
	}
	
	public void LogMessage( String strMessage, String strClassName, eCONSOLE_LOG_LEVEL eLogLevel )
	{
		LogMessage( strMessage, strClassName, eLogLevel, false );
	}
	
	public void LogMessage( String strMessage, String strClassName, eCONSOLE_LOG_LEVEL eLogLevel, boolean bConsoleOnly )
	{
		LogData logData = new LogData();
		logData.m_strConsoleMessage = strMessage;
		logData.m_eLogLevel = eLogLevel;
		logData.m_strClassName = strClassName;
		synchronized( lock )
		{
			m_qLogData.add( logData );
		}
	}
	
	// this is the lowest level of debugging where everything will be output to the console
	public void LogAll( String strMessage, Object obj )
	{
		LogAll( strMessage, obj, false );
	}
	
	public void LogAll( String strMessage, Object obj, boolean bConsoleOnly )
	{
		LogData logData = new LogData();
		logData.m_strConsoleMessage = strMessage;
		logData.m_eLogLevel = eCONSOLE_LOG_LEVEL.eCONSOLE_LOG_LEVEL_ALL;
		logData.m_strClassName = obj.getClass().getSimpleName();
		logData.m_bConsoleOnly = bConsoleOnly;
		synchronized( lock )
		{
			m_qLogData.add( logData );
		}
	}
	
	public void LogAll( String strMessage, String strClassName )
	{
		LogAll( strMessage, strClassName, false );
	}
	
	public void LogAll( String strMessage, String strClassName, boolean bConsoleOnly )
	{
		LogData logData = new LogData();
		logData.m_strConsoleMessage = strMessage;
		logData.m_eLogLevel = eCONSOLE_LOG_LEVEL.eCONSOLE_LOG_LEVEL_ALL;
		logData.m_strClassName = strClassName;
		logData.m_bConsoleOnly = bConsoleOnly;
		synchronized( lock )
		{
			m_qLogData.add( logData );
		}
	}
	
	// this level of logging should log stuff that's pertinent to what we've recently done
	// but still want to have some visibility in.
	public void LogDebug( String strMessage, Object obj )
	{
		LogDebug( strMessage, obj, false );
	}
	
	public void LogDebug( String strMessage, Object obj, boolean bConsoleOnly )
	{
		LogData logData = new LogData();
		logData.m_strConsoleMessage = strMessage;
		logData.m_eLogLevel = eCONSOLE_LOG_LEVEL.eCONSOLE_LOG_LEVEL_DEBUG;
		logData.m_strClassName = obj.getClass().getSimpleName();
		logData.m_bConsoleOnly = bConsoleOnly;
		synchronized( lock )
		{
			m_qLogData.add( logData );
		}
	}
	
	public void LogDebug( String strMessage, String strClassName )
	{
		LogDebug( strMessage, strClassName, false );
	}
	
	public void LogDebug( String strMessage, String strClassName, boolean bConsoleOnly )
	{
		LogData logData = new LogData();
		logData.m_strConsoleMessage = strMessage;
		logData.m_eLogLevel = eCONSOLE_LOG_LEVEL.eCONSOLE_LOG_LEVEL_DEBUG;
		logData.m_strClassName = strClassName;
		logData.m_bConsoleOnly = bConsoleOnly;
		synchronized( lock )
		{
			m_qLogData.add( logData );
		}
	}
	
	// this should be used to make sure that we see exceptions that we're throwing so that we
	// can take action to correct them
	public void LogError( String strMessage, Object obj )
	{
		LogError( strMessage, obj, false );
	}
	
	public void LogError( String strMessage, Object obj, boolean bConsoleOnly )
	{
		LogData logData = new LogData();
		logData.m_strConsoleMessage = strMessage;
		logData.m_eLogLevel = eCONSOLE_LOG_LEVEL.eCONSOLE_LOG_LEVEL_ERROR;
		logData.m_strClassName = obj.getClass().getSimpleName();
		logData.m_bConsoleOnly = bConsoleOnly;
		synchronized( lock )
		{
			m_qLogData.add( logData );
		}
	}
	
	public void LogError( String strMessage, String strClassName )
	{
		LogError( strMessage, strClassName, false );
	}
	
	public void LogError( String strMessage, String strClassName, boolean bConsoleOnly )
	{
		LogData logData = new LogData();
		logData.m_strConsoleMessage = strMessage;
		logData.m_eLogLevel = eCONSOLE_LOG_LEVEL.eCONSOLE_LOG_LEVEL_ERROR;
		logData.m_strClassName = strClassName;
		logData.m_bConsoleOnly = bConsoleOnly;
		synchronized( lock )
		{
			m_qLogData.add( logData );
		}
	}
	
	// this is really for what you're immediately working on right now.
	public void LogInfo( String strMessage, Object obj )
	{
		LogInfo( strMessage, obj, false );
	}
	
	public void LogInfo( String strMessage, Object obj, boolean bConsoleOnly )
	{
		LogData logData = new LogData();
		logData.m_strConsoleMessage = strMessage;
		logData.m_eLogLevel = eCONSOLE_LOG_LEVEL.eCONSOLE_LOG_LEVEL_INFO;
		logData.m_strClassName = obj.getClass().getSimpleName();
		logData.m_bConsoleOnly = bConsoleOnly;
		synchronized( lock )
		{
			m_qLogData.add( logData );
		}
	}
	
	public void LogInfo( String strMessage, String strClassName )
	{
		LogInfo( strMessage, strClassName, false );
	}
	
	public void LogInfo( String strMessage, String strClassName, boolean bConsoleOnly )
	{
		LogData logData = new LogData();
		logData.m_strConsoleMessage = strMessage;
		logData.m_eLogLevel = eCONSOLE_LOG_LEVEL.eCONSOLE_LOG_LEVEL_ERROR;
		logData.m_strClassName = strClassName;
		logData.m_bConsoleOnly = bConsoleOnly;
		synchronized( lock )
		{
			m_qLogData.add( logData );
		}
	}
	
/********************************************************************************/
//	INTERNAL FUNCTIONS
/********************************************************************************/
	private void ProcessQueues()
	{
		LogAll( "Processing Log Queue...", this, true );

		// copy the message queue to a local queue to be processed.  That way the lock
		// doesn't take a long time and we don't have anything building up behind it.
		LogDataQueue qLocalQueue = new LogDataQueue();
		synchronized( lock )
		{
			qLocalQueue.addAll( m_qLogData );
			m_qLogData.clear();
		}
		
		// process the local message queue that we created earlier.
		for( Iterator<LogData> it = qLocalQueue.iterator() ; it.hasNext() ; )
		{
			LogData logData = it.next();
			
			ComposeMessage( logData );

			// log it to the console according to the log level we've got
			if( logData.m_eLogLevel.m_nLogLevel <= m_eConsoleLoggingLevel.m_nLogLevel )
				System.out.println( logData.m_strConsoleMessage );

			if( true != logData.m_bConsoleOnly )
				FileUtility.WriteFile( m_strLogFilePath, logData.m_strFileMessage );
			
			// finally, remove the log data from the queue
			it.remove();
		}
	}
	
	private void ComposeMessage( LogData logData )
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
		
		if( !logData.m_strConsoleMessage.isEmpty() )
		{
			strBuffer.append( logData.m_strConsoleMessage );
		}
		else
			strBuffer.append( "<message>" );
		
		logData.m_strFileMessage = logData.m_strConsoleMessage;
		if( logData.m_strFileMessage.lastIndexOf("\n") != logData.m_strFileMessage.length() - 1 )
			logData.m_strFileMessage += "\n";
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
	public boolean GetContinue()
	{
		return m_bContinue;
	}
	
	public void SetContinue( boolean bContinue )
	{
		synchronized( lock )
		{
			m_bContinue = bContinue;
		}
	}
	
	public void SetConsoleLogLevel( eCONSOLE_LOG_LEVEL eLogLevel )
	{
		synchronized( lock )
		{
			m_eConsoleLoggingLevel = eLogLevel;
		}
	}

/********************************************************************************/
//	MEMBERS
/*******************************************************************************/
	private static final Object lock = new Object();
	private static volatile LogMgr m_LogMgr;
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
