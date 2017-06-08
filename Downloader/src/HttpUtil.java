import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class HttpUtil
{
	
	private List<String> cookies;
	
	public boolean GetPageHtml( HttpData httpData )
	{
		httpData.m_nRetries = 3;
		boolean bSuccess = false;
		
		// set up the function to retry 3 times to get the html, we see that sometimes the page likes to
		// just refuse the connection or we time out
		for( int i = 0 ; i < httpData.m_nRetries && bSuccess == false ; i++ )
		{
			httpData.ClearOutputs();
			bSuccess = GetHtml( httpData );
			if( false == bSuccess )
			{
				try
				{
					Thread.sleep( 500 );
				}
				catch (InterruptedException e)
				{
					ExceptionUtility.DoInterruptedExceptionWork( e, "Exception for VideoSiteCloudzilla.GetEmbeddedHTML" );
				}
			}
		}
		
		return bSuccess;
	}
	
	public boolean GetHtml( HttpData httpData )
	{
		HttpURLConnection httpConnection = null;

		try
		{
			httpConnection = SetUpConnection( httpData.m_urlPage );
			
			// cache the connection so that the consumer of the http util can use it
			httpData.m_httpURLConnection = httpConnection;
		
			// in case we want to debug how long the html file is
//			int nLength = httpConnection.getContentLength();
//			System.out.println( String.format( "Fetching HTML file %d bytes", nLength ) );

			// get the response code and handle a redirect
			int nResponseCode = httpConnection.getResponseCode();
//			System.out.println( "Response Code : " + nResponseCode );
			nResponseCode /= 100;
			if( 3 == nResponseCode )
			{
				// get the new location from the response headers
				Map<String,List<String>> mapHeaders = httpConnection.getHeaderFields();
				httpData.m_urlPage = ReturnURL( mapHeaders.get( "Location" ).get(0) );
//				DebugComm( httpConnection );

				// reset the number of retries as this function will exit and we'll want to be able to retry 3 more times
				httpData.m_nRetries = 3;
				return false;
			}
			
			// use this for debugging the headers
//			DebugComm( httpConnection );
			
			BufferedReader in = null;
			in = new BufferedReader( new InputStreamReader( httpConnection.getInputStream() ) );
			String inputLine;
			StringBuffer response = new StringBuffer();
			
			while( ( inputLine = in.readLine() ) != null )
			{
				response.append(inputLine);
			}
			
			in.close();
			
			// Get the response cookies
			setCookies( httpConnection.getHeaderFields().get("Set-Cookie") );
			httpData.m_aCookies = httpConnection.getHeaderFields().get("Set-Cookie");
			
			httpData.m_strPageContent = response.toString();
			FileUtility.WriteFile( "C:\\Projects\\Resources\\Downloader\\html.txt", httpData.m_strPageContent );
		}
		catch( FileNotFoundException eFNF )
		{
			HandleFNFException( eFNF, httpConnection, "Exception for HttpUtil.GetHtml" );
			return false;
        }
		catch( MalformedURLException eURL )
		{
			HandleMalformedURLException( eURL, httpConnection, "Exception for HttpUtil.GetHtml" );
			return false;
       }
		catch( IOException eIO )
		{
			HandleIOException( eIO, httpConnection, "Exception for HttpUtil.GetHtml" );
			return false;
		}		
		catch( Exception e )
		{
			HandleException( e, httpConnection, "Exception for HttpUtil.GetHtml" );
			return false;
		}
		
		return true;
	}
	
	public long GetFileSize( HttpData httpData )
	{
		long lReturn = 0;
		HttpURLConnection httpConnection = null;
		
		/****************************************************/
		// set up the connection
		/****************************************************/

		httpConnection = SetUpConnection( httpData.m_urlLink, true, httpData.m_strReferer );
	
		/****************************************************/
		// grab the file size
		/****************************************************/
		
		// check for the response code to be 200
		try
		{
			if( 200 != httpConnection.getResponseCode() )
			{
				System.out.println( httpConnection.getResponseCode() + " returned during HttpUtil.GetFileSize." );
		        httpConnection.disconnect();
//		        DumpRequestProps( httpConnection );
				DebugComm( httpConnection );
		        return -1;
			}
		}
		catch( IOException eIO )
		{
			HandleIOException( eIO, httpConnection, "Exception for HttpUtil.GetFileSize" );
		}
			
        // actually starts getting the file
		lReturn = httpConnection.getContentLength();
//        System.out.println( String.format( "The file is %d bytes long", lReturn ) );

		// use this for debugging the headers
//		DebugComm( httpConnection );
        
        httpConnection.disconnect();
			
		return lReturn; 
	}
	
	public boolean DownloadFile( DownloadInfo dlInfo )
	{
	
		boolean bReturn = false;
		HttpURLConnection httpConnection = null;
		
		try
		{
			/****************************************************/
			// set up the connection
			/****************************************************/

			URL dlURL = new URL( dlInfo.m_strURL );
			httpConnection = SetUpConnection( dlURL, true, dlInfo.m_strReferer );
		
			/****************************************************/
			// begin downloading the file
			/****************************************************/
			
            // actually starts getting the file
//            long lCompleteFileSize = httpConnection.getContentLength();
            dlInfo.m_nResponseCode = httpConnection.getResponseCode();
//            System.out.println( String.format( "The file is %d bytes long", lCompleteFileSize ) );

			// use this for debugging the headers
//			DebugComm( httpConnection );
			
            // set up the full file path that we're saving
            String strSavePath = dlInfo.m_strSavePath + dlInfo.m_strName;
//			System.out.println( String.format( "Save path is:  %s", strSavePath ) );
			
			// set up the guys needed to write out the file
            BufferedInputStream in = new BufferedInputStream(httpConnection.getInputStream());
            FileOutputStream fos = new FileOutputStream( strSavePath );
            BufferedOutputStream bout = new BufferedOutputStream( fos, 1024 );
            
            byte[] data = new byte[1024];
            long lDownloadedFileSize = 0;
            int x = 0;
            while( ( x = in.read(data, 0, 1024) ) >= 0 )
            {
            	// puts a starting line onto the console window that we'll be overwriting as the download progresses
//            	if( lDownloadedFileSize == 0 )
//            		System.out.print( String.format( "Downloaded %d bytes of %d",  lDownloadedFileSize, lCompleteFileSize ) );
            	lDownloadedFileSize += x;
//                System.out.print( String.format( "\rDownloaded %d bytes of %d",  lDownloadedFileSize, lCompleteFileSize ) );
                bout.write(data, 0, x);
                
                // for testing, we shorten the download to quickly test completing downloads
//                if( lDownloadedFileSize > 5000000 )
//                	break;
            }
            
            System.out.println("");
            
            bout.close();
            in.close();
            
            bReturn = true;
		}
		catch( FileNotFoundException eFNF )
		{
			HandleFNFException( eFNF, httpConnection, "Exception for HttpUtil.DownloadFile" );
        }
		catch( MalformedURLException eURL )
		{
			HandleMalformedURLException( eURL, httpConnection, "Exception for HttpUtil.DownloadFile" );
        }
		catch( IOException eIO )
		{
			HandleIOException( eIO, httpConnection, "Exception for HttpUtil.DownloadFile" );
		}		
		catch( Exception e )
		{
			HandleException( e, httpConnection, "Exception for HttpUtil.DownloadFile" );
		}
		
		return bReturn; 
	}
	
	public List<String> getCookies()
	{
		return cookies;
	}
	
	public void setCookies(List<String> cookies)
	{
		this.cookies = cookies;
	}
	
	
/*********************************************************************************************/
// Utility Functions
/*********************************************************************************************/
	private HttpURLConnection SetUpConnection( URL url )
	{
		return SetUpConnection( url, false, "" );
	}
	
	private HttpURLConnection SetUpConnection( URL url, boolean bIsPageItem, String strReferer )
	{
		HttpURLConnection httpConnection = null;
		
		try
		{
			httpConnection = (HttpURLConnection) url.openConnection();
			
			// default is GET
			httpConnection.setRequestMethod("GET");
			
			httpConnection.setUseCaches(false);
			
			// act like a browser
			httpConnection.setRequestProperty( "User-Agent", "Mozilla/5.0" );
			httpConnection.setRequestProperty( "Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8" );
			httpConnection.setRequestProperty( "Accept-Language", "en-US,en;q=0.5" );
			httpConnection.setRequestProperty( "Host", url.getHost() );
			if( bIsPageItem )
				httpConnection.setRequestProperty( "Referer", strReferer );
			if( cookies != null )
			{
				for( String cookie : this.cookies )
				{
					httpConnection.addRequestProperty( "Cookie", cookie.split(";", 1)[0] );
				}
			}
			
//			DumpRequestProps( httpConnection );
			
			return httpConnection;
		}
		catch( ProtocolException eProt )
		{
			HandleProtException( eProt, httpConnection, "Exception for HttpUtil.SetUpConnection" );
			return null;
		}
		catch( IOException eIO )
		{
			HandleIOException( eIO, httpConnection, "Exception for HttpUtil.SetUpConnection" );
			return null;
		}
		catch( Exception e )
		{
			HandleException( e, httpConnection, "Exception for HttpUtil.SetUpConnection" );
			return null;
		}
	}
	
	public URL ReturnURL( String strURL )
	{
		URL urlReturn = null;
		
		try
		{
			urlReturn = new URL( strURL );
		}
		catch( MalformedURLException eURL )
		{
			HandleMalformedURLException( eURL, "Exception for HttpUtil.ReturnURL" );
        }
		
		return urlReturn;
	}
		 
/*********************************************************************************************/
// Communication/HTTP Debugging
/*********************************************************************************************/
		
	private void DebugComm( HttpURLConnection httpConn )
	{
		DebugComm( httpConn, false );
	}
	
	private void DebugComm( HttpURLConnection httpConn, boolean bDumpRequestHeaders )
	{
		System.out.println( "\n\n**** DebugComm - Dumping HTTP Headers...");
		System.out.println( "**** HTTP URL:  " + httpConn.getURL() );
		if( true == bDumpRequestHeaders )
			DumpRequestProps( httpConn );
		DumpResponseHeaders( httpConn );
		System.out.println( "\n" );
	}
	
	private void DumpResponseHeaders( HttpURLConnection httpConn )
	{
		System.out.println( "\n**** Dumping Headers Fields..." );
		Map<String,List<String>> mapHeaders = httpConn.getHeaderFields();
		for (Map.Entry<String,List<String>> entry : mapHeaders.entrySet() )
		{
			String strKey = entry.getKey();
			List<String> aValues = entry.getValue();
			System.out.print( String.format( "**** %s : ", strKey ) );
			for( String strValue : aValues )
			{
				System.out.print( String.format( " %s; ", strValue ) );
			}
			System.out.println("");
		}
	}
	
	// this function can only be called before a request is made, therefore it cannot be called in DebugComm.
	// you can call this function in the SetUpConnection function to debug the request headers
	private void DumpRequestProps( HttpURLConnection httpConn )
	{
		System.out.println( "\n**** Dumping Request Properties..." );
		Map<String,List<String>> mapReqProp = httpConn.getRequestProperties();
		for (Map.Entry<String,List<String>> entry : mapReqProp.entrySet() )
		{
			String strKey = entry.getKey();
			List<String> aValues = entry.getValue();
			System.out.print( String.format( "**** %s : ", strKey ) );
			for( String strValue : aValues )
			{
				System.out.print( String.format( " %s; ", strValue ) );
			}
			System.out.println("");
		}
	}

	
/*********************************************************************************************/
// EXCEPTION HANDLING
/*********************************************************************************************/
	
	private void HandleException( Exception e, HttpURLConnection httpConn, String strMsg )
	{
		ExceptionUtility.DoExceptionWork( e, strMsg );
//		OutputResponseCode( httpConn );
	}
	
	private void HandleIOException( IOException e, HttpURLConnection httpConn, String strMsg )
	{
		ExceptionUtility.DoIOExceptionWork( e, strMsg );
//		OutputResponseCode( httpConn );
	}
	
	private void HandleProtException( ProtocolException e, HttpURLConnection httpConn, String strMsg )
	{
		ExceptionUtility.DoProtExceptionWork( e, strMsg );
//		OutputResponseCode( httpConn );
	}
	
	private void HandleMalformedURLException( MalformedURLException e, String strMsg )
	{
		ExceptionUtility.DoURLExceptionWork( e, strMsg );
	}
	
	private void HandleMalformedURLException( MalformedURLException e, HttpURLConnection httpConn, String strMsg )
	{
		ExceptionUtility.DoURLExceptionWork( e, strMsg );
//		OutputResponseCode( httpConn );
	}
	
	private void HandleFNFException( FileNotFoundException e, HttpURLConnection httpConn, String strMsg )
	{
		ExceptionUtility.DoFNFExceptionWork( e, strMsg );
//		OutputResponseCode( httpConn );
	}
	
	private void OutputResponseCode( HttpURLConnection httpConn )
	{
		if( null != httpConn )
		{
			try
			{
				System.out.println( String.format( "ResponseCode:  %d", httpConn.getResponseCode() ) );
			}
			catch( IOException eIO )
			{
				ExceptionUtility.DoIOExceptionWork( eIO, "Execption for HttpUtil.OutputResponseCode" );
			}
		}
	}
	
}
