import java.net.MalformedURLException;
import java.net.URL;

public class VideoSiteData
{

	public void SetURLJumpSite( String strUrl )
	{
		try
		{
			m_urlJumpSite = new URL( strUrl );
		}
		catch( MalformedURLException e )
		{
			ExceptionUtility.DoURLExceptionWork( e, "Exception for VideoSiteData.SetURLJumpSite" );
		}
	}

	public void SetURLPrimarySite( String strUrl )
	{
		try
		{
			m_urlPrimary = new URL( strUrl );
		}
		catch( MalformedURLException e )
		{
			ExceptionUtility.DoURLExceptionWork( e, "Exception for VideoSiteData.SetURLPrimarySite" );
		}
	}
	
	public void SetURLFrame( String strUrl )
	{
		try
		{
			m_urlFrame = new URL( strUrl );
		}
		catch( MalformedURLException e )
		{
			ExceptionUtility.DoURLExceptionWork( e, "Exception for VideoSiteData.SetURLFrame" );
		}
	}
	
	public void SetURLEmbedded( String strUrl )
	{
		try
		{
			m_urlEmbedded = new URL( strUrl );
		}
		catch( MalformedURLException e )
		{
			ExceptionUtility.DoURLExceptionWork( e, "Exception for VideoSiteData.SetURLEmbedded" );
		}
	}
	
	public String GenerateFileName()
	{
		String strEpisodeNumber = String.format( "%02d",m_parentEpisode.GetEpisodeData().m_nNumber );
		String strSeasonNumber = String.format( "%02d", m_parentEpisode.GetEpisodeData().m_parentSeason.GetSeasonData().m_nNumber );
		String strShow = m_parentEpisode.GetEpisodeData().m_parentSeason.GetSeasonData().m_parentShow.GetShowData().m_strName;
		
		String strFileName = strShow + "_S" + strSeasonNumber + "_E" + strEpisodeNumber;
		return strFileName;
	}

	// the data we're trying to retrieve
	public String m_strVideoLink;
	public long m_lFileSize = 0;

/********************************************************************************/
//	Data used by all video sites
/********************************************************************************/
	// This is the jump site before the actual video site
	public String m_strType;  // look at getting rid of this.  It's only for the generic OnDownloadComplete in VideoSite
	public URL m_urlJumpSite;
	public String m_strHtmlJumpSite;

/********************************************************************************/
//	Data used by video sites when we can grab the video from the jump site
//	Used By:  VODLocker
/********************************************************************************/
	// this is when we're getting frame data from the jump site
	public URL m_urlFrame;
	public String m_strHtmlFrame;
	
/********************************************************************************/
//	Data used by video sites when we have to go to the hosting site
//	Used By:  Cloudzilla
/********************************************************************************/
	// data for the primary site we're trying to get to
	public URL m_urlPrimary;
	public String m_strHtmlPrimary;
	
	// data for the embedded URL from the Primary Site
	public URL m_urlEmbedded;
	public String m_strHtmlEmbedded;

	// status info
	public eVIDEOSITESTATUS m_eStatus = eVIDEOSITESTATUS.eVIDEOSITESTATUS_NOTSTARTED;
	
	// parent
	public Episode m_parentEpisode;

}
