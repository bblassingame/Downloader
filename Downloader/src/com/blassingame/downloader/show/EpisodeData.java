package com.blassingame.downloader.show;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.jsoup.nodes.Element;

import com.blassingame.downloader.utility.ExceptionUtility;

public class EpisodeData
{
	
	public void SetURL( String strUrl )
	{
		try
		{
			m_URL = new URL( strUrl );
		}
		catch( MalformedURLException e )
		{
			ExceptionUtility.DoURLExceptionWork( e, "Exception for EpisodeData.SetURL" );
		}
	}
	
	public URL m_URL;
	public String m_strHtml;
	public int m_nNumber;
	public VideoSites m_aVideoSites = new VideoSites();
	public Season m_parentSeason;
	public boolean m_bInitialized = false;
	public boolean m_bEpisodeQueued = false;
	public boolean m_bDownloaded = false;

}

class CreateData
{
	public Element m_elemCreate;
	public String m_strType;
}

@SuppressWarnings("serial")
class CreateDataList extends ArrayList<CreateData>{} 