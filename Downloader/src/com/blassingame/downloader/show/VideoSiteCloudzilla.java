package com.blassingame.downloader.show;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.blassingame.downloader.utility.HttpData;
import com.blassingame.downloader.utility.ParseUtility;

public class VideoSiteCloudzilla extends VideoSite
{
	
	protected boolean GetVideoLink()
	{
		if( false == GetPrimaryURL() )
			return false;
		
		if( false == GetPrimaryHTML() )
			return false;
		
		if( false == GetEmbeddedURL() )
			return false;
		
		if( false == GetEmbeddedHTML() )
			return false;
		
		// grab the script with "vurl" in it
		if( false == GetVideoLinkURL() )
			return false;
		
		GetFileSize();
		
		return true;
	}
	
	private boolean GetPrimaryURL()
	{
		Document doc = Jsoup.parse( m_videoSiteData.m_strHtmlJumpSite );
		Elements elemsAllLinks = doc.select( "a" );
		
		// get the video site link from the jump site and let's get that HTML		
		for( Element e : elemsAllLinks )
		{
			if( -1 != e.attr("href").indexOf( m_videoSiteData.m_strType ) )
			{
				m_videoSiteData.SetURLPrimarySite( e.attr( "href" ) );
				return true;
			}	
		}
		
		System.out.println( "Unable to get the Primary URL for Cloudzilla" );
		return false;
	}

	private boolean GetPrimaryHTML()
	{
		// set up the http data for getting the show's html
		HttpData httpData = new HttpData();
		httpData.m_urlPage = m_videoSiteData.m_urlPrimary;
		
		// get the show's html
		if( false == m_HttpUtil.GetPageHtml( httpData ) )
		{
			System.out.println( "Unable to get the Primary HTML for Cloudzilla" );
			return false;
		}
		
		// validate the primary host is still valid and there wasn't a redirect to a different host
		String strConnectionHost = httpData.m_httpURLConnection.getURL().getHost();
		if( 0 != m_videoSiteData.m_urlJumpSite.getHost().compareTo( strConnectionHost ) )
		{
			m_videoSiteData.m_urlPrimary = httpData.m_httpURLConnection.getURL();
		}
		
		// set the show's html
		m_videoSiteData.m_strHtmlPrimary = httpData.m_strPageContent;
		
		return true;
	}
	
	private boolean GetEmbeddedURL()
	{
		Document doc = Jsoup.parse( m_videoSiteData.m_strHtmlPrimary );
		
		// get the elements with a src attribute and find the one with embed in the source, that's the URL for the video URL
		String strEmbedURL = "";
		Elements elemsSrcs = doc.select( "[src]" );
		for( Element e : elemsSrcs )
		{
			if( -1 != e.attr("src").indexOf( "embed" ) )
			{
				strEmbedURL = m_videoSiteData.m_urlPrimary.getHost() + e.attr("src");
				break;
			}
		}
		
		// validate the url with what we're expecting by building what we expect to find
		String strExpectedURL = "";
		int nStartIndex = m_videoSiteData.m_urlPrimary.toString().lastIndexOf( "/", m_videoSiteData.m_urlPrimary.toString().length()-2 ) + 1;
		int nEndIndex = m_videoSiteData.m_urlPrimary.toString().length();
		strExpectedURL = m_videoSiteData.m_urlPrimary.toString().substring( nStartIndex, nEndIndex );
		strExpectedURL = m_videoSiteData.m_urlPrimary.getHost() + "/embed/" + strExpectedURL;
		
		if( 0 != strEmbedURL.compareTo( strExpectedURL ) )
		{
			System.out.println( "Unable to get the Embedded URL for Cloudzilla" );
			return false;
		}
		
		// make sure that "http://" is in the URL or the thing pukes
		if( -1 == strEmbedURL.indexOf( "http://" ) && -1 == strEmbedURL.indexOf( "https://" ) )
		{
			strEmbedURL = "http://" + strEmbedURL;
		}
		
		m_videoSiteData.SetURLEmbedded( strEmbedURL );
		return true;
	}
	
	private boolean GetEmbeddedHTML()
	{
		// set up the http data for getting the embedded html
		HttpData httpData = new HttpData();
		httpData.m_urlPage = m_videoSiteData.m_urlEmbedded;
		
		// get the embedded html
		if( false == m_HttpUtil.GetPageHtml( httpData ) )
		{
			System.out.println( "Unable to get the Embedded HTML for Cloudzilla" );
			return false;
		}
		
		m_videoSiteData.m_strHtmlEmbedded = httpData.m_strPageContent;
		return true;
	}
	
	private boolean GetVideoLinkURL()
	{
		Document doc = Jsoup.parse( m_videoSiteData.m_strHtmlEmbedded );
		
		// get the script tags and find the one with "vurl" in it
		Elements elemsSrcs = doc.select( "script" );
		for( Element e : elemsSrcs )
		{
			if( -1 != e.data().indexOf( "vurl" ) )
			{
				m_videoSiteData.m_strVideoLink = ParseUtility.GetJSVarData( e.data(), "vurl" );
				return true;
			}
		}
		
		
		System.out.println( "Unable to get the Video URL for Cloudzilla" );
		return false;
	}
	
	private boolean GetFileSize()
	{
		// set up the http data for the newly found video link
		HttpData httpData = new HttpData();
		httpData.m_urlLink = m_HttpUtil.ReturnURL( m_videoSiteData.m_strVideoLink );
		httpData.m_strReferer = m_videoSiteData.m_urlPrimary.toString();
		
		m_videoSiteData.m_lFileSize = m_HttpUtil.GetFileSize( httpData );
		return false;
	}

}
