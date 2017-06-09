package com.blassingame.downloader.utility;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class HttpData
{
	
	public void ClearOutputs()
	{
		m_strPageContent = "";
		m_strLinkContent = "";
	}
	
	/*********************************************************/
	// HttpUtil generated data
	
	public HttpURLConnection m_httpURLConnection = null;

	/*********************************************************/
	// Primary page parameters
	
	// inputs
	public URL m_urlPage;
	
	// outputs
	public String m_strPageContent;
	public List<String> m_aCookies;
	
	/*********************************************************/
	// Secondary page parameters
	
	// inputs
	public URL m_urlLink;
	public String m_strReferer;
	public int m_nRetries;
	
	// outputs
	public String m_strLinkContent;
	
}
