package com.blassingame.downloader.download;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.blassingame.downloader.application.Constants;
import com.blassingame.downloader.logger.LogMgr;

public class DownloadMgr implements Runnable
{
		
/********************************************************************************/
//	CONSTRUCTOR/DESCTRUCTOR
/********************************************************************************/
	public DownloadMgr()
	{
		m_LogMgr = LogMgr.GetLogMgr();
		
		for( int i = 0 ; i < Constants.nNUM_OF_DOWNLOAD_THREADS ; i++ )
		{
			String strThreadName = String.format( "Download Thread %d", i );
			Download thread = new Download( strThreadName );
			thread.SetDownloadMgr( this );
			m_aDownloads.add( thread );
			thread.start();
		}
	}
	
/********************************************************************************/
//	THREAD FUNCTIONS
/********************************************************************************/
	@Override
	public void run()
	{
//		System.out.println( "Beginning the Download Mgr thread..." );
		m_LogMgr.LogDebug( "Beginning the Download Mgr thread...", this );
		while( GetContinue() )
		{
//			System.out.println( "Number of Downloads left:  " + m_qDownloads.size() );

			ProcessQueues();
			Wait(250);
		}
		
		for( Download dl : m_aDownloads )
		{
			try
			{
				dl.join();
			}
			catch( InterruptedException e )
			{
				e.printStackTrace();
			}
		}
		
		// process the queue one last time to log any completed downloads
		ProcessQueues();
	}
	
	public void ShutDown()
	{
		SetContinue( false );
		
		for( Download dl : m_aDownloads )
		{
			dl.ShutDown();
		}
	}
	
/********************************************************************************/
//	CONSUMER API
/********************************************************************************/
	public synchronized void AddDownload( DownloadInfo dlInfo )
	{
		m_qWaitingDownloads.add( dlInfo );
//		System.out.println( "Download Added:  " + dlInfo.m_strName + "  " + dlInfo.m_strType + "  " + dlInfo.m_strURL + "    Num of Waiting Downloads:  " + m_qWaitingDownloads.size() );
		m_LogMgr.LogInfo( "Download Added:  " + dlInfo.m_strName + "  " + dlInfo.m_strType + "  " + dlInfo.m_strURL + "    Num of Waiting Downloads:  " + m_qWaitingDownloads.size(), this );
	}
	
/********************************************************************************/
//	DOWNLOAD API
/********************************************************************************/
	public synchronized void AddCompletedDownloadInfo( DownloadInfo dlInfo )
	{
		m_qCompletedDownloads.add( dlInfo );
//		System.out.println( "Download " + dlInfo.m_strName + "  " + dlInfo.m_strType + "  finished with status  " + dlInfo.m_eDownloadStatus + "\nNum of Waiting Downloads:  " + m_qWaitingDownloads.size() );
//		System.out.println( "Completed downloads queue size on AddCompletedDownloadInfo:  " + m_qCompletedDownloads.size() );
		m_LogMgr.LogInfo( "Download " + dlInfo.m_strName + "  " + dlInfo.m_strType + "  finished with status  " + dlInfo.m_eDownloadStatus + "\nNum of Waiting Downloads:  " + m_qWaitingDownloads.size(), this );
		m_LogMgr.LogInfo( "Completed downloads queue size on AddCompletedDownloadInfo:  " + m_qCompletedDownloads.size(), this );
	}

/********************************************************************************/
//	INTERNAL FUNCTIONS
/********************************************************************************/
	private void ProcessQueues()
	{
//		System.out.println( "\nProcessing Download Queue..." );
		m_LogMgr.LogDebug( "Processing Download Queue...", this );

		if( true == m_bContinue )
		{
			for( Download dl : m_aDownloads )
			{
				if( dl.GetDownloadState() == eDOWNLOADSTATE.eDOWNLOADSTATE_WAITING  )
				{
					if( 0 >= m_qWaitingDownloads.size() )
						return;
					
					dl.SetDownloadInfo( m_qWaitingDownloads.remove() );
				}
				else if( dl.GetDownloadState() == eDOWNLOADSTATE.eDOWNLOADSTATE_DOWNLOADING )
				{
//					System.out.println( "Download In Progress..." );
					m_LogMgr.LogDebug( "Download In Progress...", this );
				}
				else if( dl.GetDownloadState() == eDOWNLOADSTATE.eDOWNLOADSTATE_PROCESSING )
				{				
					LogCompletedDownloads();
	
					dl.SetDownloadState( eDOWNLOADSTATE.eDOWNLOADSTATE_WAITING );
				}
				else
				{
					assert( false );
//					System.out.println( "\n\n********Unknown DOWNLOADSTATE encountered....\n\n");
					m_LogMgr.LogError( "\n\n********Unknown DOWNLOADSTATE encountered....\n\n", this );
				}
			}
		}
		else
		{
			LogCompletedDownloads();
		}
	}
	
	private void LogCompletedDownloads()
	{
//		System.out.println( "Completed downloads queue size on LogCompletedDownloads before:  " + m_qCompletedDownloads.size() );
		m_LogMgr.LogInfo( "Completed downloads queue size on LogCompletedDownloads before:  " + m_qCompletedDownloads.size(), this );
		
		for( Iterator<DownloadInfo> it = m_qCompletedDownloads.iterator() ; it.hasNext() ; )
		{
			DownloadInfo dlInfo = it.next();
			
			String strStatus = "";
			if( 0 == dlInfo.m_eDownloadStatus.compareTo( eDOWNLOADSTATUS.eDOWNLOADSTATUS_SUCCESS ) )
				strStatus = Constants.strINI_VALUE_COMPLETED;
			else if( 0 == dlInfo.m_eDownloadStatus.compareTo( eDOWNLOADSTATUS.eDOWNLOADSTATUS_ERROR ) )
				strStatus = Constants.strINI_VALUE_INCOMPLETE;
			else
				System.out.println( "DownloadMgr.LogCompletedDownloads:  We didn't find the right DOWNLOAD status like we thought" );
			
			if( dlInfo.m_cbVideoSiteDLComplete.OnDownloadComplete( strStatus ) )
				it.remove();
		}
		
//		System.out.println( "Completed downloads queue size on LogCompletedDownloads after:  " + m_qCompletedDownloads.size() );
		m_LogMgr.LogInfo( "Completed downloads queue size on LogCompletedDownloads after:  " + m_qCompletedDownloads.size(), this );
	}
	
/********************************************************************************/
//	OVERRIDES
/********************************************************************************/
	public void Wait( long timeout )
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
	
	public void SetLogMgr( LogMgr logMgr )
	{
		m_LogMgr = logMgr;
	}

/********************************************************************************/
//	MEMBERS
/********************************************************************************/
	// Queues for moving downloads around as they're processed
	private DownloadInfoQueue m_qWaitingDownloads = new DownloadInfoQueue();
	private DownloadInfoQueue m_qCompletedDownloads = new DownloadInfoQueue();
	
	// Threads and the thread pool
	List<Download> m_aDownloads = new ArrayList<Download>();
	
	// local thread members
	boolean m_bContinue = true;
	
	// Log Manager pointer
	LogMgr m_LogMgr = null;
	
}
