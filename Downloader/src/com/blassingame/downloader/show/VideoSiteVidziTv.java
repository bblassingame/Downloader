package com.blassingame.downloader.show;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.blassingame.downloader.download.DownloadInfo;
import com.blassingame.downloader.utility.HttpData;
import com.blassingame.downloader.utility.ParseUtility;

public class VideoSiteVidziTv extends VideoSite
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
				m_videoSiteData.SetURLFrame( e.attr( "src" ) );
				return true;
			}
		}
		
		System.out.println( "Unable to get the Frame URL for VidziTv" );
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
			System.out.println( "Unable to get the Frame HTML for VidziTv" );
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
			System.out.println( "This VidziTv file was deleted..." );
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
			String strScript = e.data();	// use this so that we can stop calling e.data() so much and for the unpacking part
			if( -1 != strScript.indexOf( "vplayer" ) )
			{
				// we need to check if the script should be unpacked before we try to parse it
				if( 0 == strScript.indexOf( "eval" ) )
					strScript = UnpackScript( strScript );
				
				strEmbedURL = ParseUtility.GetJSVarData( strScript, "file", 2 );
//				System.out.println( "embed URL:  " + strEmbedURL );
				m_videoSiteData.m_strVideoLink = strEmbedURL;
				// check if we got a file name with .mp4 or .flv and exit otherwise
				return IsVideoLinkValid( m_videoSiteData.m_strVideoLink );
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
		
		System.out.println( "Unable to get the Video URL for VidziTv" );
		return false;
	}
	
//	private String UnpackScript( String strScript )
//	{
//		// this function implements the following javascript function found on the VidziTV size
//		// function(p,a,c,k,e,d){while(c--)if(k[c])p=p.replace(new RegExp('\\b'+c.toString(a)+'\\b','g'),k[c]);return p}
//		
//	    // get what's inside the eval() function
//	    EvalInfo evalInfo = new EvalInfo();
//	    evalInfo.m_strEvalFunctionAndParams = strScript;
//	    ParseUtility.GetJSEvalFunction( evalInfo );
//	    
//		if( 0 != Constants.strVIDZITV_SCRIPT.compareTo( evalInfo.m_strEvalFunctionAndParams ) )
//		{
////			System.out.println( "The JS p.a.c.k.e.d. footprint is different" );
//			if( 0 != Constants.strVIDZITV_FUNCTION.compareTo( evalInfo.m_strEvalFunction ) )
//				System.out.println( "--->The function is different, we need to account for it" );
//		}
//		
//		// clean up the script so that it conforms with java string standards and not javascript string standards
//		// we're getting rid of \' and converting to ', because we don't have to escape singles quotes in java strings
//		evalInfo.m_straEvalParams.set( 3, evalInfo.m_straEvalParams.get(3).replace( "\\'", "'" ) );
//		
//		// set the variables that we need to unpack the script
//	    String p = evalInfo.m_straEvalParams.get(0);
//	    p = p.substring(1, p.length() - 1 );
//	    int a = Integer.parseInt( evalInfo.m_straEvalParams.get(1) );
//	    int c = Integer.parseInt( evalInfo.m_straEvalParams.get(2) );
//	    String strSubs = evalInfo.m_straEvalParams.get(3);
//	    strSubs = strSubs.substring( 1, strSubs.length() );
//	    strSubs = strSubs.substring( 0, strSubs.indexOf( "'" ) );
//	    ArrayList<String> k = new ArrayList<String>( Arrays.asList( strSubs.split("\\|") ) );
//	    
//	    while( 0 < c-- )
//	    	p = p.replaceAll( "\\b" + Integer.toString(c, a) + "\\b", k.get(c) );
//	    
////	    System.out.println( p );
//
//	    return p;
//	}
	
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
		httpData.m_strReferer = m_videoSiteData.m_urlFrame.toString();
		
		m_videoSiteData.m_lFileSize = m_HttpUtil.GetFileSize( httpData );
		return false;
	}
	
}
