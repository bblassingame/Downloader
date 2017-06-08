import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

class CompareEpisodeData implements Comparator<EpisodeData>
{
    @Override
    public int compare( EpisodeData e1, EpisodeData e2 )
    {
        return Integer.compare( e1.m_nNumber, e2.m_nNumber );
    }
}

public class Season
{
	
/********************************************************************************/
//	Show  API
/********************************************************************************/
	public void Initialize()
	{
		if( true == m_SeasonData.m_bInitialized )
			return;
		
		// set up the http data for getting the season's html
		HttpData httpData = new HttpData();
		httpData.m_urlPage = m_SeasonData.m_URL;
		
		// get the season's html, return if we can't get the html
		if( false == m_HttpUtil.GetPageHtml( httpData ) )
				return;
		
		// set the season's html
		m_SeasonData.m_strHtml = httpData.m_strPageContent;
				
		// parse out the episodes and create them, returns false if we have a problem
		ParseAndCreateEpisodes();
		
		for( Episode e : m_SeasonData.m_aEpisodes )
			e.Initialize();
		
		m_SeasonData.m_bInitialized = true;
	}
	
	public void GetEpisodes()
	{
		// create more episodes if we need to
		if( GetNumWaitingEpisodes() < m_SeasonData.m_parentShow.GetNumberOfNeededEpisodes() )
		{
			ParseAndCreateEpisodes();
			
			for( Episode e : m_SeasonData.m_aEpisodes )
				e.Initialize();
		}
		
		// tell the episodes to get their video links
		for( Episode e : m_SeasonData.m_aEpisodes )
		{
			e.GetEpisodes();
		}
	}
	
	public boolean IsSeasonComplete()
	{
		// we know a season is complete when we've handled all of the episodes
		boolean bReturn = false;
		
		bReturn = m_SeasonData.m_nTotalEpisodes == m_SeasonData.m_nHandledEpisodes;
//		for( Episode e : m_SeasonData.m_aEpisodes )
//			bReturn &= !e.IsEpisodeAvailable();		// if the episode is available, the season is not complete 
				
		return bReturn;
	}
	
//	public boolean AreEpisodesAvailable()
//	{
//		boolean bReturn = true;
//		
//		bReturn = m_SeasonData.m_nTotalEpisodes == m_SeasonData.m_nHandledEpisodes;
//		
//		// if we've handled all of the episodes, we're done with the show and don't need anymore episodes
//		return !bReturn;
//	}
	
/********************************************************************************/
//	Episode API
/********************************************************************************/
	public boolean IsDownloadCompleted( int nEpisode )
	{
		String strEpisodeNumber = String.format( "E%02d", nEpisode );
		String strSeasonNumber = String.format( "S%02d", m_SeasonData.m_nNumber );
		String strSeasonEpisode = strSeasonNumber + "_" + strEpisodeNumber;
		return m_SeasonData.m_parentShow.IsDownloadCompleted( strSeasonEpisode );
	}
	
	public boolean AreMoreEpisodesNeeded()
	{
		return m_SeasonData.m_parentShow.AreMoreEpisodesNeeded();
	}
	
	public void OnDownloadComplete()
	{
		m_SeasonData.m_parentShow.OnDownloadComplete();
	}
	
	public void OnDownloadIncomplete()
	{
		m_SeasonData.m_parentShow.OnDownloadIncomplete();
	}
	
/********************************************************************************/
//	Internal Functions
/********************************************************************************/
	private boolean ParseAndCreateEpisodes()
	{
		Elements elemsEpisodes = ParseEpisodes();
		if( elemsEpisodes.size() != 0 )
		{
			CreateEpisodes( elemsEpisodes );
			return true;
		}
		else
		{
			System.out.println( "We didn't find any episode links, so that probably means the html changed" );
			return false;
		}
	}
	
	private Elements ParseEpisodes()
	{
		Elements elemsReturn = new Elements();
		
		Document doc = Jsoup.parse( m_SeasonData.m_strHtml );
		Elements elemsTableRows = doc.select( "th" );
		
		for( Element e : elemsTableRows )
		{
			// ToDo:  This is almost as flaky as the parsing for the Show.  We really to improve our parsing.
			elemsReturn.add( e.child(0).child(0) );
		}
		
		return elemsReturn;
	}
	
	private void CreateEpisodes( Elements elemsEpisodes )
	{
		// put the episode data in here so that we can sort it before creating episodes.
		ArrayList<EpisodeData> aEpisodeData = new ArrayList<EpisodeData>();
		for( Element e : elemsEpisodes )
		{
			// get the season url from the episode link
			String strURL = e.attr("href");
			
			// create and initialize the season data
			EpisodeData episodeData = new EpisodeData();
			episodeData.m_parentSeason = this;
			episodeData.SetURL( strURL );
			
			// parse out the season number since the order can be a little shaky.  Occasionally we might find the last character
			// in the URL isn't a number and we just assign something to it.  When we create the file name, we should take care of duplicates.
			String strTemp = e.ownText();
			try
			{
				episodeData.m_nNumber = Integer.parseInt( strTemp.substring( strTemp.lastIndexOf(" ") + 1, strTemp.length() ) );
			}
			catch( NumberFormatException nfException )
			{
				ExceptionUtility.DoNumberFormatExceptionWork( nfException, "We found an episode title that didn't end with a number.  We've seen this before in something like The Walking Dead with their \"Episode 0 Special Preview\" title" );
				episodeData.m_nNumber = 999;
			}
			
			aEpisodeData.add( episodeData );
		}
		
		// set the total number of episodes that are available for this season
		m_SeasonData.m_nTotalEpisodes = aEpisodeData.size();
		m_SeasonData.m_nHandledEpisodes = 0;	// reset this counter because we're about to go through all of the episodes again
		
		// sort the episode data in ascending order
		Collections.sort( aEpisodeData, new CompareEpisodeData() );
		
		for( EpisodeData episodeData : aEpisodeData )
		{
			// check if we've already downloaded the episode
			if( false == IsDownloadCompleted( episodeData.m_nNumber ) && true == AreMoreEpisodesNeeded() )
			{
				// create and initialize the season
				Episode episode = new Episode();
				episode.SetDownloadMgr( m_dlMgr );
				episode.SetEpisodeData( episodeData );
				m_SeasonData.m_aEpisodes.add( episode );
				m_SeasonData.m_parentShow.OnEpisodeCreated();
				
				// keep track of how many episodes we've handled
				m_SeasonData.m_nHandledEpisodes++;
				
				System.out.println( "Added Episode " + episodeData.m_nNumber + " for downloading" );
			}
			else
			{
				if( true == IsDownloadCompleted( episodeData.m_nNumber ) )
				{
					// keep track of how man episodes we've handled, even if we handled them previously
					m_SeasonData.m_nHandledEpisodes++;
					
					System.out.println( "Already downloaded episode " + episodeData.m_nNumber );
				}
				else if( false == AreMoreEpisodesNeeded() )
				{
					System.out.println( "We've got all the episodes we need, moving to next show" );
					break;
				}
			}
		}
	}
	
	private int GetNumWaitingEpisodes()
	{
		int nReturn = 0;
		
		for( Episode e : m_SeasonData.m_aEpisodes )
		{
			if( e.m_episodeData.m_bDownloaded == false )
				nReturn++;
		}
		
		return nReturn;
	}
	
/********************************************************************************/
//	GETTERS & SETTERS
/********************************************************************************/
	public void SetSeasonData( SeasonData seasonData )
	{
		m_SeasonData = seasonData;
	}
	
	public SeasonData GetSeasonData()
	{
		return m_SeasonData;
	}
	
	public void SetDownloadMgr( DownloadMgr dlMgr )
	{
		m_dlMgr = dlMgr;
	}
	
	public String GetSavePath()
	{
		return m_SeasonData.m_parentShow.GetSavePath();
	}
	
	public String GetLogPath()
	{
		return m_SeasonData.m_parentShow.GetLogPath();
	}
	
/********************************************************************************/
//	MEMBERS
/********************************************************************************/
	// data members
	private SeasonData m_SeasonData;
	
	// utility members
	private HttpUtil m_HttpUtil = new HttpUtil();
	private DownloadMgr m_dlMgr = null;
	
}

@SuppressWarnings("serial")
class Seasons extends ArrayList<Season>{} 