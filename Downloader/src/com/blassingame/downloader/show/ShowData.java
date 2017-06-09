package com.blassingame.downloader.show;
import java.net.MalformedURLException;
import java.net.URL;

import com.blassingame.downloader.utility.ExceptionUtility;

public class ShowData
{
	
	public void SetURL( String strUrl )
	{
		try
		{
			m_URL = new URL( strUrl );
		}
		catch( MalformedURLException e )
		{
			ExceptionUtility.DoURLExceptionWork( e, "Exception for ShowData.SetURL" );
		}
	}
		
	// show information
	public URL m_URL;
	public String m_strHtml = "";
	public String m_strName = "";
	public String m_strURLName = "";
	// used to know when we're done with a show
	public int m_nTotalSeasons = 0;
	public int m_nFinishedSeasons = 0;
	
	// downloading information
	public int m_nStartingSeason = 1;
	// use these to monitor the live downloading action
	public int m_nEpisodesCreated = 0;
	public int m_nEpisodesDownloaded = 0;
	// use these to monitor the information in the file
	public int m_nWatched = 0;
	public int m_nCompleted = 0;
	
	// children
	public Seasons m_aSeasons = new Seasons();
	
	// parent
	public ShowMgr m_parentShowMgr;

}
