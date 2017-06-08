import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class VideoSiteTheVideoMe extends VideoSite
{
	
	protected boolean GetVideoLink()
	{
		// for the VODLocker site, we try to get the video off the embedded frame on the jump site
		if( false == GetFrameURL() )
			return false;
		
		// get the html in the frame that has the URL for the video link
		if( false == GetFrameHTML() )
			return false;
		
		// Check if video is deleted
		if( true == IsDeleted() )
			return false;
		
		// finally, get the url for the video that we want to download
		if( false == GetVideoLinkURL() )
			return false;
		
		GetFileSize();
		
		return true;
	}
	
	private boolean GetFrameURL()
	{
		Document doc = Jsoup.parse( m_videoSiteData.m_strHtmlJumpSite );
		Elements elemsAllLinks = doc.select( "iframe" );
		
		// get the frame url and then we're going to load that HTML		
		for( Element e : elemsAllLinks )
		{
			if( false == e.attr("src").isEmpty() )
			{
				String strTemp = e.attr( "src" );
				
				// check for a second http:// address in the link, it's just a bad link that we need to skip and there's no vid to download
				if( 0 != strTemp.lastIndexOf( "http://" ) )
				{
					return false;
				}
				
				m_videoSiteData.SetURLFrame( strTemp );
				return true;
			}
		}
		
		if( 0 != m_videoSiteData.m_urlFrame.toString().lastIndexOf( "http:\\" ) )
		{
			System.out.println( "Found a bad frame URL for TheVideoMe, skipping..." );
			return false;
		}
		
		System.out.println( "Unable to get the Frame URL for TheVideoMe" );
		return false;
	}

	private boolean GetFrameHTML()
	{
		// set up the http data for getting the show's html
		HttpData httpData = new HttpData();
		httpData.m_urlPage = m_videoSiteData.m_urlFrame;
		
		// get the show's html
		if( false == m_HttpUtil.GetPageHtml( httpData ) )
		{
			System.out.println( "Unable to get the Frame HTML for TheVideoMe" );
			return false;
		}
		
		// validate the primary host is still valid and there wasn't a redirect to a different host
		String strConnectionHost = httpData.m_httpURLConnection.getURL().getHost();
		if( 0 != m_videoSiteData.m_urlFrame.getHost().compareTo( strConnectionHost ) )
		{
			m_videoSiteData.m_urlFrame = httpData.m_httpURLConnection.getURL();
		}
		
		// set the show's html
		m_videoSiteData.m_strHtmlFrame = httpData.m_strPageContent;
		
		return true;
	}
	
	protected boolean IsDeleted()
	{
		Document doc = Jsoup.parse( m_videoSiteData.m_strHtmlFrame );
		String strText = doc.text();
		if( true == DeletedTextFound( strText ) )
		{
			System.out.println( "This TheVideoMe file was deleted..." );
			return true;
		}
		else
		{
			return false;
		}
	}
	
	private boolean GetVideoLinkURL()
	{
		Document doc = Jsoup.parse( m_videoSiteData.m_strHtmlFrame );
		
		// get the elements with a src attribute and find the one with embed in the source, that's the URL for the video URL
		String strEmbedURL = "";
		Elements elemsScripts = doc.select( "script" );
		for( Element e : elemsScripts )
		{
			if( -1 != e.data().indexOf( "jwConfig_vars" ) )
			{
				// find the index of "480p", then back up from there to find "file"
				String strScript = e.data();
				int nStartIndex = strScript.indexOf( "480p" );
				if( -1 == nStartIndex )
					nStartIndex = strScript.indexOf( "360p" );
				nStartIndex = strScript.lastIndexOf( "file", nStartIndex ) + 5;
				strEmbedURL = ParseUtility.GetJSVarDataFromIndex( strScript, "file", nStartIndex );
//				System.out.println( "embed URL:  " + strEmbedURL );
				
				if( -1 == strEmbedURL.indexOf( "http" ) )
				{
					System.out.println( "The parse utility didn't find a valid video link" + String.format( " link = %s", strEmbedURL ) );
					return false;
				}
				else
				{
					m_videoSiteData.m_strVideoLink = strEmbedURL;
					return true;
				}
			}
		}
		
//		// validate the url with what we're expecting by building what we expect to find
//		String strExpectedURL = "";
//		int nStartIndex = m_videoSiteData.m_urlPrimary.toString().lastIndexOf( "/", m_videoSiteData.m_urlPrimary.toString().length()-2 ) + 1;
//		int nEndIndex = m_videoSiteData.m_urlPrimary.toString().length();
//		strExpectedURL = m_videoSiteData.m_urlPrimary.toString().substring( nStartIndex, nEndIndex );
//		strExpectedURL = m_videoSiteData.m_urlPrimary.getHost() + "/embed/" + strExpectedURL;
////		System.out.println( "expected URL:  " + strExpectedURL );
//		
//		if( 0 != strEmbedURL.compareTo( strExpectedURL ) )
//		{
//			System.out.println( "\n---->  We didn't find the expected URL, exiting" );
//			return "";
//		}
		
		System.out.println( "Unable to get the Video URL for TheVideoMe" );
		return false;
	}
	
	@Override
	protected boolean QueueDownload()
	{
		if( null != m_videoSiteData.m_strVideoLink &&  false == m_videoSiteData.m_strVideoLink.isEmpty() )
		{
			DownloadInfo dlInfo = new DownloadInfo();
			dlInfo.m_strURL = m_videoSiteData.m_strVideoLink;
			dlInfo.m_strReferer = m_videoSiteData.m_urlFrame.toString();
			dlInfo.m_strName = m_videoSiteData.GenerateFileName();
			dlInfo.m_strType = ".mp4";
			dlInfo.m_cbVideoSiteDLComplete = this;
			dlInfo.m_strSavePath = GetSavePath();
			
			m_dlMgr.AddDownload( dlInfo );
			
			m_videoSiteData.m_parentEpisode.OnDownloadQueued();
			
			return true;
		}
		
		return false;
	}
	
	private boolean GetFileSize()
	{
		// set up the http data for the newly found video link
		HttpData httpData = new HttpData();
		httpData.m_urlLink = m_HttpUtil.ReturnURL( m_videoSiteData.m_strVideoLink );
		httpData.m_strReferer = m_videoSiteData.m_urlJumpSite.toString();
		
		m_videoSiteData.m_lFileSize = m_HttpUtil.GetFileSize( httpData );
		return false;
	}	
	
}
