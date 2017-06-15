package com.blassingame.downloader.download;
import com.blassingame.downloader.logger.LogMgr;
import com.blassingame.downloader.utility.FileUtility;
import com.blassingame.downloader.utility.HttpUtil;
import com.blassingame.downloader.utility.StringArray;

enum eDOWNLOADSTATE
{
	eDOWNLOADSTATE_WAITING,
	eDOWNLOADSTATE_DOWNLOADING,
	eDOWNLOADSTATE_PROCESSING
}

public class Download extends Thread
{
	
/********************************************************************************/
//	CONSTRUCTOR/DESCTRUCTOR
/********************************************************************************/
	public Download()
	{
		m_eDownloadState = eDOWNLOADSTATE.eDOWNLOADSTATE_WAITING;
	}
	
	public Download( String strName )
	{
		setName( strName );
		m_eDownloadState = eDOWNLOADSTATE.eDOWNLOADSTATE_WAITING;
	}
	
/********************************************************************************/
//	THREAD FUNCTIONS
/********************************************************************************/
	@Override
	public void run()
	{
		m_LogMgr.LogDebug( "Beginning a Download thread...", this );
		
		while( GetContinue() )
		{
			if( m_eDownloadState == eDOWNLOADSTATE.eDOWNLOADSTATE_WAITING )
			{
				Wait(250);
			}
			else if( m_eDownloadState == eDOWNLOADSTATE.eDOWNLOADSTATE_DOWNLOADING )
			{
				m_LogMgr.LogError( "Got to the downloading state, this shouldn't happen", this );
				DoDownload();
			}
			else if( m_eDownloadState == eDOWNLOADSTATE.eDOWNLOADSTATE_PROCESSING )
			{
				if( m_DownloadInfo != null )
					m_DownloadInfo = null;
				
				Wait(250);
			}
		}
	}
	
	public void ShutDown()
	{
		SetContinue( false );
	}
	
/********************************************************************************/
//	INTERNAL FUNCTIONS
/********************************************************************************/
	private void DoDownload()
	{
		CreateUniqueFileName();
		
		HttpUtil httpUtil = new HttpUtil();
		boolean bSuccess = false;
		bSuccess = httpUtil.DownloadFile( m_DownloadInfo );
		
		// we need to fill out this area with some status about how the download completed or finished
		if( bSuccess == true )
			m_DownloadInfo.m_eDownloadStatus = eDOWNLOADSTATUS.eDOWNLOADSTATUS_SUCCESS;
		else
			m_DownloadInfo.m_eDownloadStatus = eDOWNLOADSTATUS.eDOWNLOADSTATUS_ERROR;
		
		SetDownloadState( eDOWNLOADSTATE.eDOWNLOADSTATE_PROCESSING );
		
		m_dlMgr.AddCompletedDownloadInfo( m_DownloadInfo );
	}
	
	private void CreateUniqueFileName()
	{
		// get the files that have already been started to compare for naming
		StringArray astrFiles = FileUtility.GetOnlyFilesInDirectory( m_DownloadInfo.m_strSavePath );
		
		// ensure that the file name is unique, and append (i) to the end of the file if it's not
		// get the new base file name
		m_DownloadInfo.m_strName = FileUtility.CreateUniqueFileName( m_DownloadInfo.m_strName, m_DownloadInfo.m_strType, astrFiles );
		// append the extension for a complete file name
		m_DownloadInfo.m_strName += m_DownloadInfo.m_strType;
	}
	
//	private String RemoveSpecialChars( String strFileName )
//	{
//		// implement this later
//		return strFileName;
//	}

	
/********************************************************************************/
//	GETTERS & SETTERS
/********************************************************************************/
	public synchronized void SetDownloadMgr( DownloadMgr dlMgr )
	{
		m_dlMgr = dlMgr;
	}
	
	public synchronized void SetDownloadInfo( DownloadInfo dlInfo )
	{
		SetDownloadState( eDOWNLOADSTATE.eDOWNLOADSTATE_DOWNLOADING );
		m_DownloadInfo = dlInfo;
		m_strDownloadID = dlInfo.m_strID;
	}
	
	public synchronized String GetDownloadInfoID()
	{
		return m_strDownloadID;
	}
	
	public synchronized eDOWNLOADSTATE GetDownloadState()
	{
		return m_eDownloadState;
	}
	
	public synchronized void SetDownloadState( eDOWNLOADSTATE eState )
	{
		m_eDownloadState = eState;
	}
	
	public synchronized int GetReturnCode()
	{
		return m_DownloadInfo.m_nResponseCode;
	}
	
	public synchronized boolean GetContinue()
	{
		return m_bContinue;
	}
	
	public synchronized void SetContinue( boolean bContinue )
	{
		m_bContinue = bContinue;
	}
	
	public synchronized void SetLogMgr( LogMgr logMgr )
	{
		m_LogMgr = logMgr;
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
//	DATA MEMBERS
/********************************************************************************/
	private String m_strDownloadID;
	private DownloadInfo m_DownloadInfo = null;
	private eDOWNLOADSTATE m_eDownloadState;
	private DownloadMgr m_dlMgr;
	
	private boolean m_bContinue = true;
	
	private LogMgr m_LogMgr = null;

}
