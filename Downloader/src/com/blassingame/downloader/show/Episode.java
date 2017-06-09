package com.blassingame.downloader.show;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.blassingame.downloader.application.Constants;
import com.blassingame.downloader.download.DownloadMgr;
import com.blassingame.downloader.utility.HttpData;
import com.blassingame.downloader.utility.HttpUtil;

class CompareVideoSite implements Comparator<VideoSite>
{
    @Override
    public int compare( VideoSite site1, VideoSite site2 )
    {
        return Long.compare( site2.m_videoSiteData.m_lFileSize, site1.m_videoSiteData.m_lFileSize );
    }
}

public class Episode
{
	
/********************************************************************************/
//	Season API
/********************************************************************************/
	public void Initialize()
	{
		if( true == m_episodeData.m_bInitialized )
			return;
		
		// set up the http data for getting the episode's html
		HttpData httpData = new HttpData();
		httpData.m_urlPage = m_episodeData.m_URL;
		
		// get the episode's html
		m_HttpUtil.GetPageHtml( httpData );
		
		// set the episode's html
		m_episodeData.m_strHtml = httpData.m_strPageContent;
				
		// parse out the video sites and create them, returns false if we have a problem
		ParseAndCreateVideoSites();
		
		for( VideoSite v : m_episodeData.m_aVideoSites )
			v.Initialize();
		
		// sort the list of sites by their size.
		Collections.sort( m_episodeData.m_aVideoSites, new CompareVideoSite() );
		
		m_episodeData.m_bInitialized = true;
		
		System.out.println( "Finished Initializing Episode " + m_episodeData.m_nNumber );
	}
	
	public void GetEpisodes()
	{
//		System.out.println( "Begin Getting Video Links for Episode " + m_episodeData.m_nNumber );
		
		// tell the seasons to get their video sites
		for( VideoSite videoSite : m_episodeData.m_aVideoSites )
		{
			// do some checking to make sure we want to queue up the download
			if( true == m_episodeData.m_parentSeason.IsDownloadCompleted( m_episodeData.m_nNumber ) )
				break;
			if( true == m_episodeData.m_bEpisodeQueued )
				break;
			
			// actually queue up the download
			if( true == videoSite.GetEpisode() )
			{
				m_episodeData.m_bEpisodeQueued = true;
				break;
			}
		}
		
//		System.out.println( "Finished Getting Video Links..." );
	}
	
	public boolean IsEpisodeAvailable()
	{
		// if the episode has been downloaded, it is not available
		return !m_episodeData.m_bDownloaded;
	}
	
/********************************************************************************/
//	VideoSite API
/********************************************************************************/
	public void OnDownloadQueued()
	{
//		m_episodeData.m_parentSeason.OnDownloadQueued();
		m_episodeData.m_bEpisodeQueued = true;
	}
	
	public void OnDownloadComplete()
	{
		m_episodeData.m_parentSeason.OnDownloadComplete();
		m_episodeData.m_bEpisodeQueued = false;
		m_episodeData.m_bDownloaded = true;
	}
	
	public void OnDownloadIncomplete()
	{
		m_episodeData.m_parentSeason.OnDownloadIncomplete();
		m_episodeData.m_bEpisodeQueued = false;
	}
	
/********************************************************************************/
//	Internal Functions
/********************************************************************************/
	private boolean ParseAndCreateVideoSites()
	{
		CreateDataList elemsVideoSite = ParseVideoSites();
		
		if( elemsVideoSite != null )
		{
			CreateVideoSites( elemsVideoSite );
			return true;
		}
		else
		{
			System.out.println( "Returned false while trying to create video site" );
			return false;
		}
	}
	
	private CreateDataList ParseVideoSites()
	{
		CreateDataList elemsReturn = new CreateDataList();
		
		Document doc = Jsoup.parse( m_episodeData.m_strHtml );
		Elements elemsAllRows = doc.select( "td" );
		
		for( Element e : elemsAllRows )
		{
			// make sure these table rows have the ID that we're looking for, these contain the episode links
//			if( 0 != e.attr( "id" ).compareTo( "t2055954" ) )
//				continue;
			
//			if( -1 != e.text().indexOf( "cloudzilla.to" ) )
//			{
//				CreateData elemTemp = new CreateData(); 
//				elemTemp.m_elemCreate = e;
//				elemTemp.m_strType = Constants.strSITETYPE_CLOUDZILLA;
//				elemsReturn.add( elemTemp );
//			}	
//			else if( -1 != e.text().indexOf( "vodlocker.com" ) )
//			{
//				CreateData elemTemp = new CreateData(); 
//				elemTemp.m_elemCreate = e;
//				elemTemp.m_strType = Constants.strSITETYPE_VODLOCKER;
//				elemsReturn.add( elemTemp );
//			}
			/*else*/ if( -1 != e.text().indexOf( "streamin.to" ) )
			{
				CreateData elemTemp = new CreateData();
				elemTemp.m_elemCreate = e;
				elemTemp.m_strType = Constants.strSITETYPE_STREAMINTO;
				elemsReturn.add( elemTemp );
			}
			else if( -1 != e.text().indexOf( "vidzi.tv" ) )
			{
				CreateData elemTemp = new CreateData();
				elemTemp.m_elemCreate = e;
				elemTemp.m_strType = Constants.strSITETYPE_VIDZITV;
				elemsReturn.add( elemTemp );
			}
//			else if( -1 != e.text().indexOf( "thevideo.me" ) )
//			{
//				CreateData elemTemp = new CreateData();
//				elemTemp.m_elemCreate = e;
//				elemTemp.m_strType = Constants.strSITETYPE_THEVIDEOME;
//				elemsReturn.add( elemTemp );
//			}
//			else if( -1 != e.text().indexOf( "vidbull.com" ) )
//			{
//				CreateData elemTemp = new CreateData();
//				elemTemp.m_elemCreate = e;
//				elemTemp.m_strType = Constants.strSITETYPE_VIDBULL;
//				elemsReturn.add( elemTemp );
//			}
		}
		
		if( elemsReturn.size() == 0 )
		{
			System.out.println( "No preferred video site found for Episode " + m_episodeData.m_nNumber + " Season " + m_episodeData.m_parentSeason.GetSeasonData().m_nNumber );
			return null;
		}
		
		return elemsReturn;
	}
	
	private void CreateVideoSites( CreateDataList elemsVideoSite )
	{
		for( CreateData createData : elemsVideoSite )
		{
			// get the season url from the episode link
			String strURL = "";
			Elements elemA = createData.m_elemCreate.select( "a" );
			if( elemA != null )
			{
				strURL = elemA.attr("href");
				// we need to look for relative urls...booo!
				if( -1 == strURL.indexOf( "http" ) )
				{
					strURL = Constants.strPROJFREETV_BASE_URL + strURL;
				}
			}
			else
			{
				System.out.println( "Didn't find an <a> tag, the page HTML may have changed" );
				continue;
			}
			
			// create and initialize the season data
			VideoSiteData videoSiteData = new VideoSiteData();
			videoSiteData.m_parentEpisode = this;
			videoSiteData.SetURLJumpSite( strURL );
			
			VideoSite videoSite = null;
			// create and initialize the video site
			if( createData.m_strType.equals( Constants.strSITETYPE_CLOUDZILLA ) )
			{
				videoSite = new VideoSiteCloudzilla();
				videoSite.SetDownloadMgr( m_dlMgr );
				videoSiteData.m_strType = Constants.strSITETYPE_CLOUDZILLA;
				videoSite.SetVideoSiteData( videoSiteData );
			}
			else if( createData.m_strType.equals( Constants.strSITETYPE_VODLOCKER ) )
			{
				videoSite = new VideoSiteVODLocker();
				videoSite.SetDownloadMgr( m_dlMgr );
				videoSiteData.m_strType = Constants.strSITETYPE_VODLOCKER;
				videoSite.SetVideoSiteData( videoSiteData );
			}
			else if( createData.m_strType.equals( Constants.strSITETYPE_STREAMINTO ) )
			{
				videoSite = new VideoSiteStreaminTo();
				videoSite.SetDownloadMgr( m_dlMgr );
				videoSiteData.m_strType = Constants.strSITETYPE_STREAMINTO;
				videoSite.SetVideoSiteData( videoSiteData );
			}
			else if( createData.m_strType.equals( Constants.strSITETYPE_VIDZITV ) )
			{
				videoSite = new VideoSiteVidziTv();
				videoSite.SetDownloadMgr( m_dlMgr );
				videoSiteData.m_strType = Constants.strSITETYPE_VIDZITV;
				videoSite.SetVideoSiteData( videoSiteData );
			}
			else if( createData.m_strType.equals( Constants.strSITETYPE_THEVIDEOME ) )
			{
				videoSite = new VideoSiteTheVideoMe();
				videoSite.SetDownloadMgr( m_dlMgr );
				videoSiteData.m_strType = Constants.strSITETYPE_THEVIDEOME;
				videoSite.SetVideoSiteData( videoSiteData );
			}
//			else if( createData.m_strType.equals( Constants.strSITETYPE_VIDBULL ) )
//			{
//				videoSite = new VideoSiteStreaminTo();
//				videoSite.SetDownloadMgr( m_dlMgr );
//				videoSiteData.m_strType = Constants.strSITETYPE_VIDBULL;
//				videoSite.SetVideoSiteData( videoSiteData );
//			}
			else
			{
				videoSite = new VideoSite();
				videoSite.SetDownloadMgr( m_dlMgr );
				videoSite.SetVideoSiteData( videoSiteData );
			}
			
			m_episodeData.m_aVideoSites.add( videoSite );
			System.out.println( "Created and added a video link" );
		}
	}
	
/********************************************************************************/
//	Utility
/********************************************************************************/
	
/********************************************************************************/
//	GETTERS & SETTERS
/********************************************************************************/
	public void SetEpisodeData( EpisodeData episodeData )
	{
		m_episodeData = episodeData;
	}
	
	public EpisodeData GetEpisodeData()
	{
		return m_episodeData;
	}
	
	public void SetDownloadMgr( DownloadMgr dlMgr )
	{
		m_dlMgr = dlMgr;
	}
	
	public String GetSavePath()
	{
		return m_episodeData.m_parentSeason.GetSavePath();
	}
	
	public String GetLogPath()
	{
		return m_episodeData.m_parentSeason.GetLogPath();
	}
	
/********************************************************************************/
//	MEMBERS
/********************************************************************************/
	// data members
	protected EpisodeData m_episodeData = new EpisodeData();
	
	// utility members
	private HttpUtil m_HttpUtil = new HttpUtil();
	protected DownloadMgr m_dlMgr = null;
	
}

@SuppressWarnings("serial")
class Episodes extends ArrayList<Episode>{} 