package com.blassingame.downloader.utility;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

import com.blassingame.downloader.application.Constants;
import com.blassingame.downloader.download.DownloadInfo;

public class ParseUtility
{
	
	static public String GetJSVarData( String strScript, String strVar )
	{
		return GetJSVarData( strScript, strVar, 1 );
	}
	
	static public String GetJSVarData( String strScript, String strVar, int nOccurance )
	{
		// TODO:  check for duplicates before sending it back
		// if a duplicate is found, return the first found value
		
		// make sure the var is in this script, otherwise get out
		if( -1 == strScript.indexOf( strVar ) )
		{
			System.out.println( String.format( "ERROR\tGetJSVarData: \"%s\" doesn't exist in this script", strVar ) );
			return "ERROR:  NOT FOUND";
		}
		
		// get the index after the occurrence of the variable we're looking for
		int nStartIndex = 0;
		String strValue = "";
		String strChar = "";
		for( int i = 0 ; i < nOccurance ; i++ )
		{
			nStartIndex = strScript.indexOf( strVar, nStartIndex ) + strVar.length();
		}
		
		if( -1 == nStartIndex )
		{
			System.out.println( String.format( "ERROR\tGetJSVarData: Didn't find the %d occurance of \"%s\"", nOccurance + 1, strVar ) );
			return "ERROR:  NOT FOUND";
		}
		
		// find out of the variable is in double- or single-quotes and set that as our character
		int nSingleIndex = strScript.indexOf( "\'", nStartIndex );
		int nDoubleIndex = strScript.indexOf( "\"", nStartIndex );
		if( -1 != nSingleIndex && nSingleIndex < nDoubleIndex )
			strChar = "\'";
		else if( nDoubleIndex != -1 )
			strChar = "\"";
		else
		{
			System.out.println( String.format( "ERROR\tGetJSVarData: Didn't find a single- or double-quote after our variable instance", strVar ) );
			return "ERROR:  NOT FOUND";
		}

		// get the variable value
		int nEndIndex = -1;
		if( -1 != nStartIndex )
		{
			nStartIndex = strScript.indexOf( strChar, nStartIndex ) + 1;
			nEndIndex = strScript.indexOf( strChar, nStartIndex );
			strValue = strScript.substring( nStartIndex, nEndIndex );
//			System.out.println( String.format( "Var %s = %s",  strVar, strValue ) );
		}
		else
		{
			System.out.println( String.format( "ERROR\tGetJSVarData: Didn't find %s...", strVar ) );
			strValue = "ERROR:  NOT FOUND";
		}
		
		return strValue;
	}
	
	static public String GetJSVarDataFromIndex( String strScript, String strVar, int nStartIndex )
	{
		// TODO:  check for duplicates before sending it back
		// if a duplicate is found, return the first found value
		
		// make sure the var is in this script, otherwise get out
//		if( -1 == strScript.indexOf( strVar, nStartIndex ) )
//		{
//			System.out.println( String.format( "ERROR\tGetJSVarData: \"%s\" doesn't exist in this script", strVar ) );
//			return "ERROR:  NOT FOUND";
//		}
		
//		// get the index after the occurrence of the variable we're looking for
//		int nStartIndex = 0;
//		String strValue = "";
//		String strChar = "";
//		for( int i = 0 ; i < nOccurance ; i++ )
//		{
//			nStartIndex = strScript.indexOf( strVar, nStartIndex ) + strVar.length();
//		}
//		
//		if( -1 == nStartIndex )
//		{
//			System.out.println( String.format( "ERROR\tGetJSVarData: Didn't find the %d occurance of \"%s\"", nOccurance + 1, strVar ) );
//			return "ERROR:  NOT FOUND";
//		}

		// find out of the variable is in double- or single-quotes and set that as our character
		String strChar = "";
		String strValue = "";
		int nSingleIndex = strScript.indexOf( "\'", nStartIndex );
		int nDoubleIndex = strScript.indexOf( "\"", nStartIndex );
		if( -1 != nSingleIndex && nSingleIndex < nDoubleIndex )
			strChar = "\'";
		else if( nDoubleIndex != -1 )
			strChar = "\"";
		else
		{
			System.out.println( String.format( "ERROR\tGetJSVarData: Didn't find a single- or double-quote after our variable instance", strVar ) );
			return "ERROR:  NOT FOUND";
		}

		// get the variable value
		int nEndIndex = -1;
		if( -1 != nStartIndex )
		{
			nStartIndex = strScript.indexOf( strChar, nStartIndex ) + 1;
			nEndIndex = strScript.indexOf( strChar, nStartIndex );
			strValue = strScript.substring( nStartIndex, nEndIndex );
//			System.out.println( String.format( "Var %s = %s",  strVar, strValue ) );
		}
		else
		{
			System.out.println( String.format( "ERROR\tGetJSVarData: Didn't find %s...", strVar ) );
			strValue = "ERROR:  NOT FOUND";
		}
		
		return strValue;
	}

	static public String GetJsonVarData( String strJson, String strVar )
	{
		// TODO:  check for duplicates before sending it back
		// if a duplicate is found, return the first found value
		
		if( -1 == strJson.indexOf( strVar ) )
			return "ERROR:  NOT FOUND";

		String strValue = "";
		int nStartIndex = strJson.indexOf( strVar ) + strVar.length();
		int nEndIndex = -1;
		if( -1 != nStartIndex )
		{
			nStartIndex = strJson.indexOf( "\"", nStartIndex ) + 1;
			nEndIndex = strJson.indexOf( "\"", nStartIndex );
			strValue = strJson.substring( nStartIndex,  nEndIndex );
//			System.out.println( String.format( "Var %s = %s",  strVar, strValue ) );
		}
		else
		{
			System.out.println( String.format( "ERROR\tGetJsonVarData: Didn't find %s...", strVar ) );
			strValue = "ERROR:  NOT FOUND";
		}
		
		return strValue;
	}
	
	
	/********************************************************************************************************/
	//		Parses out the function and parameters passed to that function to allow us to evaluate a javascript function.
	//		This is currently used to unpack a packed, inline javascript function with the following footprint: 
	//		eval( <function>( <function_params> ) )
	//
	static public boolean GetJSEvalFunction( EvalInfo evalInfo )
	{
	    /********************************************************************************/
		// strip off the "eval()" portion and get the inside that we want to evaluate
	    int nIndexStart = evalInfo.m_strEvalFunctionAndParams.indexOf( Constants.strEVAL ) + Constants.strEVAL.length();
	    nIndexStart = evalInfo.m_strEvalFunctionAndParams.indexOf( "(", nIndexStart ) + 1;	// locate the first open parenthesis, the start of the function
	    int nIndexEnd = evalInfo.m_strEvalFunctionAndParams.lastIndexOf( ")" );
	    String strFunctionAndParams = evalInfo.m_strEvalFunctionAndParams.substring( nIndexStart, nIndexEnd );
	    
	    /********************************************************************************/
	    // get the function we're going to evaluate
	    nIndexStart = 0;
	    int nBraceCount = -1;
    	for( int i = 0 ; i < strFunctionAndParams.length() && nBraceCount != 0 ; i++ )
    	{
    		if( strFunctionAndParams.charAt(i) == '{' )
    		{
    			if( nBraceCount == -1 )
    				nBraceCount = 1;
    			else
    				nBraceCount++;
    		}
    		else if( strFunctionAndParams.charAt(i) == '}' )
    		{
    			if( nBraceCount == -1 )
    			{
    				System.out.println( "VideoSite::EvaluateScript  Didn't find well formatted javascript, exiting function" );
    				return false;
    			}
    			else
    			{
    				nBraceCount--;
    				if( nBraceCount == 0 )
    					nIndexEnd = i + 1;
    			}
    		}
	    }
	    
	    evalInfo.m_strEvalFunction = strFunctionAndParams.substring( nIndexStart, nIndexEnd );
	    
	    /********************************************************************************/
	    // parse out the parameters that get passed in into an array so that we can pass them into the script generation engine
	    String strParamsPortion = strFunctionAndParams.substring( nIndexEnd + 1, strFunctionAndParams.length() - 1 );	// strips off parentheses that encapsulate the params
	    
	    // set up the counters that we'll use to make sure we find the end of each variable
	    nIndexStart = 0;
	    nIndexEnd = 0;
	    boolean bSingleQuote = false;
	    
	    // loop over the string and grab each variable and put it into the string array
	    
	    for( int i = 0 ; i < strParamsPortion.length() ; i++ )
	    {
	    	char c = strParamsPortion.charAt(i);
	    	switch(c)
	    	{
	    		case '\'':
	    			bSingleQuote = !bSingleQuote;
	    			break;
	    		case ',':
	    			if( bSingleQuote )
	    			{
	    				break;
	    			}
	    			else
	    			{
	    				nIndexEnd = i;
	    				evalInfo.m_straEvalParams.add( strParamsPortion.substring( nIndexStart, nIndexEnd ).trim() );
	    				nIndexStart = nIndexEnd + 1;
	    			}
	    			break;
		    	default:
		    		break;
	    	}
	    	
	    	if( i == strParamsPortion.length() - 1 )
	    	{
	    		nIndexEnd = strParamsPortion.length();
	    		evalInfo.m_straEvalParams.add( strParamsPortion.substring( nIndexStart, nIndexEnd ) );
	    	}
	    }
	    
	    return true;
	}

	static public DownloadInfo ContainsVideoLink( String strContent )
	{
		Document doc = Jsoup.parse( strContent );
		
		DownloadInfo dataLink = new DownloadInfo();
		
		DownloadInfo dataHtml = SearchMimeType( doc );
		DownloadInfo dataScript = SearchScripts( doc );
		DownloadInfo dataVid = SearchVid( doc );
		
		if( !dataHtml.m_strURL.isEmpty() )
			dataLink = dataHtml;
		else if( !dataScript.m_strURL.isEmpty() )
			dataLink = dataScript;
		else if( !dataVid.m_strURL.isEmpty() )
			dataLink = dataVid;
		
		return dataLink;
	}
	
	static private DownloadInfo SearchMimeType( Document doc )
	{
		DownloadInfo ReturnData = new DownloadInfo();
		
		// find .H261 links
		Elements elemsH261 = doc.select( "[type=video/H261]" );
		for( Element e : elemsH261 )
		{
			//System.out.println( e.attr( "abs:src" ) );
			System.out.println( "H261 source type found");
			ReturnData.m_strURL = e.attr( "abs:src" );
			ReturnData.m_strType = ".h261";
			return ReturnData;
		}
		
		// find .H263 links
		Elements elemsH263 = doc.select( "[type=video/H263]" );
		for( Element e : elemsH263 )
		{
			//System.out.println( e.attr( "abs:src" ) );
			System.out.println( "H263 source type found");
			ReturnData.m_strURL = e.attr( "abs:src" );
			ReturnData.m_strType = ".H263";
			return ReturnData;
		}
		
		// find .H264 links
		Elements elemsH264 = doc.select( "[type=video/H264]" );
		for( Element e : elemsH264 )
		{
			//System.out.println( e.attr( "abs:src" ) );
			System.out.println( "H264 source type found");
			ReturnData.m_strURL = e.attr( "abs:src" );
			ReturnData.m_strType = ".H264";
			return ReturnData;
		}
		
		// find .flv links
		Elements elemsFLV = doc.select( "[type=video/flv]" );
		for( Element e : elemsFLV )
		{
			//System.out.println( e.attr( "abs:src" ) );
			System.out.println( "flv source type found");
			ReturnData.m_strURL = e.attr( "abs:src" );
			ReturnData.m_strType = ".flv";
			return ReturnData;
		}
		
		// find .mp4 links
		Elements elemsMP4 = doc.select( "[type=video/mp4]" );
		for( Element e : elemsMP4 )
		{
			//System.out.println( e.attr( "abs:src" ) );
			System.out.println( "mp4 source type found");
			ReturnData.m_strURL = e.attr( "abs:src" );
			ReturnData.m_strType = ".mp4";
			return ReturnData;
		}
		
		// find .webm links
		Elements elemsWebm = doc.select( "[type=video/webm]" );
		for( Element e : elemsWebm )
		{
			//System.out.println( e.attr( "abs:src" ) );
			System.out.println( "webm source type found");
			ReturnData.m_strURL = e.attr( "abs:src" );
			ReturnData.m_strType = ".webm";
			return ReturnData;
		}
		
		// find .ogv links
		Elements elemsOgg = doc.select( "[type=video/ogg]" );
		for( Element e : elemsOgg )
		{
			//System.out.println( e.attr( "abs:src" ) );
			System.out.println( "ogg source type found");
			ReturnData.m_strURL = e.attr( "abs:src" );
			ReturnData.m_strType = ".ogv";
			return ReturnData;
		}
		
		return ReturnData;		
	}
	
	static private DownloadInfo SearchScripts( Document doc )
	{
		DownloadInfo dataScripts = new DownloadInfo();
		
		Elements eScripts = doc.select( "script" );
		
		for( Element eScript : eScripts )
		{
			if( !eScript.data().isEmpty() )
			{
				int nIndex = eScript.data().indexOf( ".flv" );
				if( -1 != nIndex )
				{
					System.out.println( "Found a .flv link" );
				}
				
				nIndex = eScript.data().indexOf( ".mp4" );
				if( -1 != nIndex )
				{
					System.out.println( "Found a .mp4 link" );
				}
			}
		}
		
		return dataScripts;
	}
	
	static private DownloadInfo SearchVid( Document doc )
	{
		DownloadInfo dataScripts = new DownloadInfo();
		
		Elements eScripts = doc.select( "video" );
		
		for( Element eScript : eScripts )
		{
			if( !eScript.data().isEmpty() )
			{
				int nIndex = eScript.data().indexOf( ".flv" );
				if( -1 != nIndex )
				{
					System.out.println( "Found a video .flv link" );
				}
				
				nIndex = eScript.data().indexOf( ".mp4" );
				if( -1 != nIndex )
				{
					System.out.println( "Found a video .mp4 link" );
				}
			}
			else
			{
				System.out.println( "Found a video tag, but not sure what's here" );
			}
		}
		
		return dataScripts;
	}
	
}
