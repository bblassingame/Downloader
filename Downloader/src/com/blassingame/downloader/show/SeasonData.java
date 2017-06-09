package com.blassingame.downloader.show;
import java.net.MalformedURLException;
import java.net.URL;

import com.blassingame.downloader.utility.ExceptionUtility;

public class SeasonData
{

	public void SetURL( String strUrl )
	{
		try
		{
			m_URL = new URL( strUrl );
		}
		catch( MalformedURLException e )
		{
			ExceptionUtility.DoURLExceptionWork( e, "Exception for SeasonData.SetURL" );
		}
	}
	
	public URL m_URL = null;
	public String m_strHtml = "";
	public int m_nNumber = -1;
	public Episodes m_aEpisodes = new Episodes();
	public Show m_parentShow = null;
	public boolean m_bInitialized = false;
	
	public int m_nTotalEpisodes = 0;
	public int m_nHandledEpisodes = 0;
//	public boolean m_bEpisodesRemaining = true;

}
