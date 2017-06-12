package com.blassingame.downloader.show;
import java.util.Iterator;

import com.blassingame.downloader.application.Constants;
import com.blassingame.downloader.download.DownloadMgr;
import com.blassingame.downloader.logger.LogMgr;
import com.blassingame.downloader.utility.FileUtility;

public class ShowMgr implements Runnable
{
	
	public ShowMgr()
	{
		m_LogMgr = LogMgr.GetLogMgr();
	}
	
/********************************************************************************/
//	THREAD FUNCTIONS
/********************************************************************************/
	@Override
	public void run()
	{
//		System.out.println( "Beginning the Show Mgr thread..." );
		m_LogMgr.LogDebug( "Beginning the Show Mgr thread...", this );
		
		Initialize();
		
		while( GetContinue() )
		{
			ProcessQueue();
			Wait(250);
		}
	}
	
	public void ShutDown()
	{
		SetContinue( false );
	}
	
/********************************************************************************/
//	CONSUMER API
/********************************************************************************/
	public synchronized void AddShow( NewShowInfo newShowInfo )
	{
		m_qNewShowInfoQueue.add( newShowInfo );
	}
	
/********************************************************************************/
//	SHOW API
/********************************************************************************/
	public void OnShowURLUpdated()
	{
		m_bReserialize = true;
	}
	
/********************************************************************************/
//	INTERNAL FUNCTIONS
/********************************************************************************/
	private void Initialize()
	{
		String[] astrSections = FileUtility.GetAllSections( GetShowLogPath() );
		
		for( int i = 0 ; i < astrSections.length ; i++ )
		{
			// set up the show data for the show we're trying to download
			ShowData showData = new ShowData();
			showData.m_strName = astrSections[i];
			showData.m_parentShowMgr = this;
			
			// create the show
			Show show = new Show();
			show.SetDownloadMgr( m_dlMgr );
			
			// set the data on the show page
			show.SetShowData( showData );
			m_aShows.add( show );
			show.Serialize( true );	// read in data from the log file
			show.Initialize();
		}
		
		// we updated the show urls, so we need to save them back out
		if( true == m_bReserialize )
		{
			for( Show show : m_aShows )
				show.Serialize( false );
			
			m_bReserialize = false;
		}
	}
	
	private void ProcessQueue()
	{
		for( Iterator<NewShowInfo> it = m_qNewShowInfoQueue.iterator() ; it.hasNext() ; )
		{
			NewShowInfo newShowInfo = it.next();
			
			// first check if we already have this show
			for( Show s : m_aShows )
			{
				if( 0 == s.GetShowURLasString().compareToIgnoreCase( newShowInfo.m_strShowURL ) )
				{
					System.out.println( "We are already downloading that show\n" );
					it.remove();
					return;
				}
			}
			
			// set up the show data for the show we're trying to download
			ShowData showData = new ShowData();
			showData.m_parentShowMgr = this;
			showData.m_nStartingSeason = newShowInfo.m_nStartingSeason;
			showData.SetURL( newShowInfo.m_strShowURL );
			
			// create the show
			Show show = new Show();
			show.SetDownloadMgr( m_dlMgr );
			
			// set the data on the show page
			show.SetShowData( showData );
			m_aShows.add( show );
			show.Initialize();
			show.Serialize( false );	// write out the show after we've added it
			
			System.out.println( "Added show with URL " + newShowInfo.m_strShowURL );
			
			it.remove();
		}
		
		// update the number of needed episodes we need
		for( Show show : m_aShows )
		{
			show.UpdateNumCompletedSeasons();
			show.UpdateNumNeededEpisodes();
		}
		
		// get the episodes that we need to get
		GetEpisodes();
	}
	
	private void GetEpisodes()
	{
		for( Show s : m_aShows )
			s.GetEpisodes();
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

	public void SetDownloadMgr( DownloadMgr dlMgr )
	{
		m_dlMgr = dlMgr;
	}
	
	public void SetLogMgr( LogMgr logMgr )
	{
		m_LogMgr = logMgr;
	}
	
	public String GetLogPath()
	{
		return Constants.strLOG_PATH;
	}
	
	public String GetShowLogPath()
	{
		return Constants.strLOG_PATH + "Shows.log";
	}
	
	public String GetSavePath()
	{
		return m_strSavePath;
	}
	
	
/********************************************************************************/
//	MEMBERS
/********************************************************************************/
	// file paths
//	private String m_strLogPath = System.getProperty("user.home") + "\\Documents\\Downloader\\Logs\\";
	private String m_strSavePath = System.getProperty("user.home") + "\\Documents\\Downloader\\Shows\\";
	
	// show members
	NewShowInfoQueue m_qNewShowInfoQueue = new NewShowInfoQueue();
	Shows m_aShows = new Shows();
	
	boolean m_bReserialize = false;
	
	// utility pointers
	private DownloadMgr m_dlMgr = null;
	private LogMgr m_LogMgr = null;
	
	// local thread members
	boolean m_bContinue = true;

}

