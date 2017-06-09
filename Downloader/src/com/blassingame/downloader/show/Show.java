package com.blassingame.downloader.show;
import java.io.File;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.blassingame.downloader.application.Constants;
import com.blassingame.downloader.download.DownloadMgr;
import com.blassingame.downloader.utility.FileUtility;
import com.blassingame.downloader.utility.HttpData;
import com.blassingame.downloader.utility.HttpUtil;

public class Show
{
	
/********************************************************************************/
//	Show Mgr API
/********************************************************************************/
	public void Initialize()
	{
		// set up the http data for getting the show's html
		HttpData httpData = new HttpData();
		httpData.m_urlPage = m_ShowData.m_URL;
		
		// get the show's html
		if( false == m_HttpUtil.GetPageHtml( httpData ) )
			return;
		
		// set the show's html
		m_ShowData.m_strHtml = httpData.m_strPageContent;
		
		// parse the html for name
		ParseShowName();
		
		// create the save path or make sure it already exists
		CreateFolderDestinations();
		
		// check if we actually need to download any episodes
		if( 0 < UpdateNumNeededEpisodes() )
		{
		
			// parse out the seasons and create them, returns false if we have a problem
			ParseAndCreateSeasons();
			
			for( Season s:  m_ShowData.m_aSeasons )
				s.Initialize();
		}
		else
		{
			System.out.println( "Skipping " + m_ShowData.m_strName + ", we already have enough episodes for this show." );
		}
	}

	public void GetEpisodes()
	{
//		System.out.println( "Begin Getting Seasons..." );
		
		// if there are no episodes available, skip everything
		if( true == IsShowComplete() )
		{
//			System.out.println( "We have no more episodes to download for " + m_ShowData.m_strName + ", we have reached the end of the show" );
			return;
		}
		
		// we need to create the seasons if we already didn't.  this happens whenever we started the app with nothing
		if( GetNumberOfNeededEpisodes() > 0 && m_ShowData.m_aSeasons.size() == 0 )
		{
			ParseAndCreateSeasons();
			
			for( Season s: m_ShowData.m_aSeasons )
				s.Initialize();
		}
		
		// tell the seasons to get their episodes
		for( Season s : m_ShowData.m_aSeasons )
		{
			s.GetEpisodes();
		}
		
//		System.out.println( "Finished Getting Seasons" );
	}
	
	public void UpdateNumCompletedSeasons()
	{
		m_ShowData.m_nFinishedSeasons = 0;
		
		for( Season s : m_ShowData.m_aSeasons )
		{
			if( s.IsSeasonComplete() )
				m_ShowData.m_nFinishedSeasons++;
		}
	}
	
	public int UpdateNumNeededEpisodes()
	{
		// reset the counters when we read from the file
		m_ShowData.m_nCompleted = 0;
		m_ShowData.m_nWatched = 0;
		
		int nReturn = 0;
		
		String[] astrValues = FileUtility.GetAllSectionValues( GetLogPath(), Constants.strINI_KEY_DOWNLOAD );
		if( null != astrValues )
		{
			for( int i = 0 ; i < astrValues.length ; i++ )
			{
				if( 0 == astrValues[i].compareTo( Constants.strINI_VALUE_COMPLETED ) )
					m_ShowData.m_nCompleted++;
			}
		}
		
		astrValues = FileUtility.GetAllSectionValues( GetLogPath(), Constants.strINI_KEY_WATCHED );
		if( null != astrValues )
		{
			for( int i = 0 ; i < astrValues.length ; i++ )
			{
				if( 0 == astrValues[i].compareTo( Constants.strINI_VALUE_YES ) )
					m_ShowData.m_nWatched++;
			}
		}
		
		if( Constants.nNUM_OF_DOWNLOADS <= (m_ShowData.m_nCompleted - m_ShowData.m_nWatched - (m_ShowData.m_nEpisodesCreated - m_ShowData.m_nEpisodesDownloaded) ) )
			nReturn =  0;
		else
			nReturn = Constants.nNUM_OF_DOWNLOADS - (m_ShowData.m_nCompleted - m_ShowData.m_nWatched) - (m_ShowData.m_nEpisodesCreated - m_ShowData.m_nEpisodesDownloaded);
		
//		System.out.println( "UpdateNeededEpisodes:  Needed = " + nReturn + " Completed = " + m_ShowData.m_nCompleted + " Watched = " + m_ShowData.m_nWatched + " Created = " + m_ShowData.m_nEpisodesCreated + " Downloaded = " + m_ShowData.m_nEpisodesDownloaded );
		
		return nReturn;
	}
	
/********************************************************************************/
//	Season API
/********************************************************************************/
	public boolean IsDownloadCompleted( String strSeasonEpisode )
	{
		String strShowSeasonEpisode = m_ShowData.m_strName + "_" + strSeasonEpisode;
		String strDownloadValue = FileUtility.GetIniValue( GetLogPath(), strShowSeasonEpisode, Constants.strINI_KEY_DOWNLOAD );
		if( strDownloadValue == null || strDownloadValue.isEmpty() )
			return false;
		return strDownloadValue.compareTo( Constants.strINI_VALUE_COMPLETED ) == 0 ? true : false;
	}
	
	public boolean AreMoreEpisodesNeeded()
	{
		return GetNumberOfNeededEpisodes() > 0;
	}
	
	public int GetNumberOfNeededEpisodes()
	{
		int nReturn = 0;
				
		if( Constants.nNUM_OF_DOWNLOADS <= (m_ShowData.m_nCompleted - m_ShowData.m_nWatched - (m_ShowData.m_nEpisodesCreated - m_ShowData.m_nEpisodesDownloaded) ) )
			nReturn =  0;
		else
			nReturn = Constants.nNUM_OF_DOWNLOADS - (m_ShowData.m_nCompleted - m_ShowData.m_nWatched) - (m_ShowData.m_nEpisodesCreated - m_ShowData.m_nEpisodesDownloaded);
		
//		System.out.println( "AreMoreEpisodesNeeded:  Needed = " + nReturn + " Completed = " + m_ShowData.m_nCompleted + " Watched = " + m_ShowData.m_nWatched + " Created = " + m_ShowData.m_nEpisodesCreated + " Downloaded = " + m_ShowData.m_nEpisodesDownloaded );
		
		return nReturn;
	}
		
	public void OnDownloadComplete()
	{
		m_ShowData.m_nEpisodesDownloaded++;
	}
		
	public void OnDownloadIncomplete()
	{
		m_ShowData.m_nEpisodesCreated--;
	}
	
	public void OnEpisodeCreated()
	{
		m_ShowData.m_nEpisodesCreated++;
	}
	
/********************************************************************************/
//	INTERNAL FUNCTIONS
/********************************************************************************/
	private void ParseShowName()
	{
		// get the show name from the title tag.  it will be in the format "Project Free TV - <show name>"
		if( false == m_ShowData.m_strName.isEmpty() )
			return;	// we don't need to parse it out because it was already set
		
		// find the title tag
		Document doc = Jsoup.parse( m_ShowData.m_strHtml );
		Elements elemsTitle = doc.select( "title" );
		
		// if we get more than 1 match, for now we're skipping and just defaulting to "show" for the title
		if( elemsTitle.size() == 1 )
		{
			String strTemp = elemsTitle.get(0).ownText();
			m_ShowData.m_strName = strTemp.substring( strTemp.lastIndexOf( " - " ) + 3, strTemp.length() );
		}
		else
		{
			m_ShowData.m_strName = "show";
		}
	}
	
	private void CreateFolderDestinations()
	{
		String strShowSavePath = m_ShowData.m_parentShowMgr.GetSavePath() + m_ShowData.m_strName;
		File fileSavePath = new File( strShowSavePath );
		fileSavePath.mkdir();
	}
	
	private boolean ParseAndCreateSeasons()
	{
		Elements elemsSeasons = ParseSeasons();
		if( null != elemsSeasons && elemsSeasons.size() > 0 )
		{
			CreateSeasons( elemsSeasons );
			return true;
		}
		else
		{
			System.out.println( "We didn't find any of the seasons listed for " + m_ShowData.m_strName + ", it probably means the page html changed" );
			return false;
		}
	}
	
	private Elements ParseSeasons()
	{
		Elements elemsReturn = null;
		
		Document doc = Jsoup.parse( m_ShowData.m_strHtml );
		
		Elements elemsDivs = doc.select( "div" );
		for( Element e : elemsDivs )
		{
			if( 0 < e.text().indexOf( "All Seasons" ) )
			{
				// ToDo:  This is super flaky and likely to fail.  We should implement something more dynamic and stable
				Elements elemsList = elemsDivs.select( "ul" );
				elemsReturn = elemsList.get(0).children();
				break;
			}
		}
		
		return elemsReturn;
	}
	
	private void CreateSeasons( Elements elemsSeasons )
	{
		for( int i = m_ShowData.m_nStartingSeason - 1 ; i < elemsSeasons.size() ; i++ )
		{
			// get the season url from the season link
			Element elemSeason = elemsSeasons.get(i);
			String strURL = elemSeason.child(0).attr("href");
			
			// create and initialize the season data
			SeasonData seasonData = new SeasonData();
			seasonData.m_parentShow = this;
			seasonData.SetURL( strURL );
			seasonData.m_nNumber = i + 1;
			
			// create and initialize the season
			Season season = new Season();
			season.SetDownloadMgr( m_dlMgr );
			season.SetSeasonData( seasonData );
			m_ShowData.m_aSeasons.add( season );
			
			// track the total number of seasons
			m_ShowData.m_nTotalSeasons++;
			
			System.out.println( "Created and added a season..." );
		}
	}
	
	/*************************************************************************************/
	// This function is used during serialization to update the show's url if the url has changed.
	// This has happened before, so the hope is that we just have to change a couple of the base
	// urls and everything else will work and get updated from there.
	private void ValidateShowURL()
	{
		if( -1 == m_ShowData.m_URL.toString().indexOf( Constants.strPROJFREETV_BASE_URL ) )
		{
			// the show's stored url needs to be updated to the new url
			String strTemp = m_ShowData.m_URL.toString();
			m_ShowData.SetURL( Constants.strPROJFREETV_BASE_SERIES_URL + m_ShowData.m_strURLName );
			m_ShowData.m_parentShowMgr.OnShowURLUpdated();
			System.out.println( "The show " + m_ShowData.m_strName + " just had it's URL updated.\n\tOld URL:  " + strTemp + "\n\tNew URL:  " + m_ShowData.m_URL.toString() );
		}
	}
	
	private boolean IsShowComplete()
	{
		// we know a show is complete when all of the seasons are complete
		// NOTE:  we start our seasons on the season number set by the user, so we need to check all of the seasons
		boolean bReturn = true;
		
		if( m_ShowData.m_nTotalSeasons > 0 )
			bReturn = m_ShowData.m_nTotalSeasons == m_ShowData.m_nFinishedSeasons;
		else
			bReturn = false;
		
		return bReturn;
	}
	
//	private boolean AreEpisodesAvailable()
//	{
//		boolean bReturn = false;
//		
//		for( Season s : m_ShowData.m_aSeasons )
//		{
//			bReturn |= s.AreEpisodesAvailable();
//		}
//		
//		return bReturn;
//	}
	
/********************************************************************************/
//	GETTERS & SETTERS
/********************************************************************************/
	public void SetShowData( ShowData showData )
	{
		m_ShowData = showData;
	}
	
	public ShowData GetShowData()
	{
		return m_ShowData;
	}
	
	public void SetDownloadMgr( DownloadMgr dlMgr )
	{
		m_dlMgr = dlMgr;
	}
	
	public String GetSavePath()
	{
		return m_ShowData.m_parentShowMgr.GetSavePath() + m_ShowData.m_strName + "\\";
	}
	
	public String GetLogPath()
	{
		return m_ShowData.m_parentShowMgr.GetLogPath() + m_ShowData.m_strName + ".log";
	}
	
	public String GetShowURLasString()
	{
		return m_ShowData.m_URL.toString();
	}
	
/********************************************************************************/
//	SERIALIZATION
/********************************************************************************/
	public void Serialize( boolean bRead )
	{
		String strPath = m_ShowData.m_parentShowMgr.GetShowLogPath();
		
		if( true == bRead )
		{
			m_ShowData.m_strURLName = FileUtility.GetIniValue( strPath, m_ShowData.m_strName, Constants.strINI_KEY_SHOWURLNAME );
			m_ShowData.SetURL( FileUtility.GetIniValue( strPath, m_ShowData.m_strName, Constants.strINI_KEY_SHOWURL ) );
			ValidateShowURL();
			m_ShowData.m_nStartingSeason = Integer.parseInt( FileUtility.GetIniValue( strPath, m_ShowData.m_strName, Constants.strINI_KEY_SHOWSTARTINGSEASON ) );
		}
		else
		{
			FileUtility.WriteIniValue( strPath, m_ShowData.m_strName, Constants.strINI_KEY_SHOWURLNAME, m_ShowData.m_strURLName );
			FileUtility.WriteIniValue( strPath, m_ShowData.m_strName, Constants.strINI_KEY_SHOWURL, m_ShowData.m_URL.toString() );
			FileUtility.WriteIniValue( strPath, m_ShowData.m_strName, Constants.strINI_KEY_SHOWSTARTINGSEASON, Integer.toString(m_ShowData.m_nStartingSeason) );
		}
	}
	
/********************************************************************************/
//	MEMBERS
/********************************************************************************/
	// data members
	private ShowData m_ShowData;
	
	// utility members
	private HttpUtil m_HttpUtil = new HttpUtil();
	private DownloadMgr m_dlMgr = null;
	
}

@SuppressWarnings("serial")
class Shows extends ArrayList<Show>{} 