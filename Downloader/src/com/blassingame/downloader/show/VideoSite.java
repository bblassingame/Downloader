package com.blassingame.downloader.show;

import java.util.ArrayList;
import java.util.Arrays;

import com.blassingame.downloader.application.Constants;
import com.blassingame.downloader.download.DownloadInfo;
import com.blassingame.downloader.download.DownloadMgr;
import com.blassingame.downloader.utility.EvalInfo;
import com.blassingame.downloader.utility.FileUtility;
import com.blassingame.downloader.utility.HttpData;
import com.blassingame.downloader.utility.HttpUtil;
import com.blassingame.downloader.utility.ParseUtility;

enum eVIDEOSITESTATUS
{
	eVIDEOSITESTATUS_NOTSTARTED,
	eVIDEOSITESTATUS_COMPLETED,
	eVIDEOSITESTATUS_INCOMPLETE
}

public class VideoSite implements VideoSiteCallback
{
	
/********************************************************************************/
//	Episode API
/********************************************************************************/
	public void Initialize()
	{
		// set up the http data for getting the video site's html
		HttpData httpData = new HttpData();
		httpData.m_urlPage = m_videoSiteData.m_urlJumpSite;
		
		// get the video site's html
		m_HttpUtil.GetPageHtml( httpData );
		
		// set the video site's html
		m_videoSiteData.m_strHtmlJumpSite = httpData.m_strPageContent;
				
		// parse out the video links and create them, returns false if we have a problem
		if( false == GetVideoLink() )
			return;
		
		System.out.println( "Finished Initializing Video Link for  " + m_videoSiteData.m_strType + "  " + m_videoSiteData.m_strVideoLink + "  " + m_videoSiteData.m_lFileSize );
	}

	public boolean GetEpisode()
	{
		// start downloading the link if we found it
		return QueueDownload();
	}
	
/********************************************************************************/
//	Internal Functions
/********************************************************************************/
	protected boolean QueueDownload()
	{
		if( null != m_videoSiteData.m_strVideoLink &&  false == m_videoSiteData.m_strVideoLink.isEmpty() )
		{
			DownloadInfo dlInfo = new DownloadInfo();
			dlInfo.m_strURL = m_videoSiteData.m_strVideoLink;
			dlInfo.m_strReferer = m_videoSiteData.m_urlPrimary.toString();
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
	
	protected boolean DeletedTextFound( String strText )
	{
		boolean bReturn = false;
		bReturn |= ( -1 != strText.indexOf( "File Not Found" ) );
		bReturn |= ( -1 != strText.indexOf( "THIS FILE WAS DELETED" ) );
		bReturn |= ( -1 != strText.indexOf( "File was deleted or expired" ) );
		bReturn |= ( -1 != strText.indexOf( "The page you are looking for cannot be found" ) );
		bReturn |= ( -1 != strText.indexOf( "This video server is in maintenance" ) );
		bReturn |= ( -1 != strText.indexOf( "Try another VidBull link or refresh in few mins" ) );
		bReturn |= ( -1 != strText.indexOf( "File was deleted" ) );
		return bReturn;
	}
	
	protected boolean IsVideoLinkValid( String strVideoLink )
	{
		if( -1 == strVideoLink.indexOf( ".mp4" ) && -1 == strVideoLink.indexOf( ".flv" ) )
		{
			System.out.println( "The video link didn't have .mp4 or .flv in it so we're skipping this one." );
			return false;
		}
		return true;
	}
	
	protected String UnpackScript( String strScript )
	{
		// this function implements the following javascript function found on the VidziTV and StreaminTo sites
		// function(p,a,c,k,e,d){while(c--)if(k[c])p=p.replace(new RegExp('\\b'+c.toString(a)+'\\b','g'),k[c]);return p}
		
	    // get what's inside the eval() function
	    EvalInfo evalInfo = new EvalInfo();
	    evalInfo.m_strEvalFunctionAndParams = strScript;
	    ParseUtility.GetJSEvalFunction( evalInfo );
	    
		if( 0 != Constants.strVIDZITV_SCRIPT.compareTo( evalInfo.m_strEvalFunctionAndParams ) )
		{
//			System.out.println( "The JS p.a.c.k.e.d. footprint is different" );
			if( 0 != Constants.strVIDZITV_FUNCTION.compareTo( evalInfo.m_strEvalFunction ) )
				System.out.println( "--->The function is different, we need to account for it" );
		}
		
		// clean up the script so that it conforms with java string standards and not javascript string standards
		// we're getting rid of \' and converting to ', because we don't have to escape singles quotes in java strings
		evalInfo.m_straEvalParams.set( 3, evalInfo.m_straEvalParams.get(3).replace( "\\'", "'" ) );
		
		// set the variables that we need to unpack the script
	    String p = evalInfo.m_straEvalParams.get(0);
	    p = p.substring(1, p.length() - 1 );
	    int a = Integer.parseInt( evalInfo.m_straEvalParams.get(1) );
	    int c = Integer.parseInt( evalInfo.m_straEvalParams.get(2) );
	    String strSubs = evalInfo.m_straEvalParams.get(3);
	    strSubs = strSubs.substring( 1, strSubs.length() );
	    strSubs = strSubs.substring( 0, strSubs.indexOf( "'" ) );
	    ArrayList<String> k = new ArrayList<String>( Arrays.asList( strSubs.split("\\|") ) );
	    
	    while( 0 < c-- )
	    {
	    	if( k.get(c).isEmpty() == false )
	    		p = p.replaceAll( "\\b" + Integer.toString(c, a) + "\\b", k.get(c) );
	    }
	    
//	    System.out.println( p );

	    return p;
	}
	
/********************************************************************************/
//	CALLBACKS
/********************************************************************************/
	@Override
	public boolean OnDownloadComplete( String strStatus )
	{
		boolean bReturn = true;
		
		// update the status and notify parent
		if( strStatus.compareTo( Constants.strINI_VALUE_COMPLETED ) == 0 )
		{
			m_videoSiteData.m_eStatus = eVIDEOSITESTATUS.eVIDEOSITESTATUS_COMPLETED;
			m_videoSiteData.m_parentEpisode.OnDownloadComplete();
		}
		else
		{
			m_videoSiteData.m_eStatus = eVIDEOSITESTATUS.eVIDEOSITESTATUS_INCOMPLETE;
			m_videoSiteData.m_parentEpisode.OnDownloadIncomplete();
		}
		
//		System.out.println( "\nbefore" );
//		String[] astrSections = FileUtility.GetAllSections( System.getProperty("user.home") + "\\Documents\\Downloader\\Logs\\Parenthood.log" );
//		for( String s : astrSections )
//			System.out.println( s );
		
		String strLogPath = GetLogPath();
		String strEpisodeSection = m_videoSiteData.GenerateFileName();
		bReturn &= FileUtility.WriteIniValue( strLogPath, strEpisodeSection, Constants.strINI_KEY_DOWNLOAD, strStatus );
		bReturn &= FileUtility.WriteIniValue( strLogPath, strEpisodeSection, Constants.strINI_KEY_SITE, m_videoSiteData.m_strType );
		bReturn &= FileUtility.WriteIniValue( strLogPath, strEpisodeSection, Constants.strINI_KEY_WATCHED, Constants.strINI_VALUE_NO );
		
//		System.out.println( "\nafter" );
//		astrSections = FileUtility.GetAllSections( System.getProperty("user.home") + "\\Documents\\Downloader\\Logs\\Parenthood.log" );
//		for( String s : astrSections )
//			System.out.println( s );
		
		return bReturn;
	}
	
/********************************************************************************/
//	Overrides for Implementations of this Class
/********************************************************************************/
	protected boolean GetVideoLink()
	{
		System.out.println( "Didn't find a site for this one..." );
		return false;
	}
	
/********************************************************************************/
//	GETTERS & SETTERS
/********************************************************************************/
	public void SetVideoSiteData( VideoSiteData videoSiteData )
	{
		m_videoSiteData = videoSiteData;
	}
	
	public VideoSiteData GetSeasonData()
	{
		return m_videoSiteData;
	}
	
	protected void SetDownloadMgr( DownloadMgr dlMgr )
	{
		m_dlMgr = dlMgr;
	}
	
	public String GetSavePath()
	{
		return m_videoSiteData.m_parentEpisode.GetSavePath();
	}
	
	private String GetLogPath()
	{
		return m_videoSiteData.m_parentEpisode.GetLogPath();
	}
	
/********************************************************************************/
//	MEMBERS
/********************************************************************************/
	// data members
	protected VideoSiteData m_videoSiteData = new VideoSiteData();
	
	// utility members
	protected HttpUtil m_HttpUtil = new HttpUtil();
	protected DownloadMgr m_dlMgr = null;
	
}

@SuppressWarnings("serial")
class VideoSites extends ArrayList<VideoSite>{} 