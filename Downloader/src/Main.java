import java.util.Scanner;

public class Main
{
	
	public static void main(String[] args)
	{
		System.out.println( "Starting Video Download..." );
		DownloadMgr m_DownloadMgr = new DownloadMgr();
		Thread m_dlMgrThread = new Thread( m_DownloadMgr, "Download Mgr" );
		m_dlMgrThread.start();
		
	    System.out.println( "***  Welcome to the Show Downloader  ***" );
	    System.out.println( "\nYou can enter quit at any time to leave the program." );
	    
		ShowMgr m_ShowMgr = new ShowMgr();
		m_ShowMgr.SetDownloadMgr( m_DownloadMgr );
		Thread m_ShowMgrThread = new Thread( m_ShowMgr, "Show Mgr" );
		m_ShowMgrThread.start();
		
		while( false == m_bQuit ) 
		{
			NewShowInfo newShowInfo = new NewShowInfo();
		    //  get the name of the show and construct the url
		    System.out.print( "\nWhat show do you want to watch?  " );
		    if( true == GetShowNameInput( newShowInfo ) )
		    	continue;
		    
		    // get which season to start at
		    System.out.print( "Enter the starting season: " );
		    if( true == GetStartingSeason( newShowInfo ) )
		    	continue;
		    
		    System.out.println( String.format( "URL:  %s, Begin at Season:  %d", newShowInfo.m_strShowURL, newShowInfo.m_nStartingSeason ) );
		    
			m_ShowMgr.AddShow( newShowInfo );
		}
		
		m_DownloadMgr.ShutDown();
		try
		{
			m_dlMgrThread.join();
		}
		catch( InterruptedException e )
		{
			e.printStackTrace();
		}
		
		m_ShowMgr.ShutDown();
		try
		{
			m_ShowMgrThread.join();
		}
		catch( InterruptedException e )
		{
			e.printStackTrace();
		}
		
		System.exit(0);
	}
	
	static private boolean GetShowNameInput( NewShowInfo newShowInfo )
	{
		String strInput = "";
		m_Scanner.useDelimiter("\n");
		strInput += m_Scanner.next();
	    strInput = strInput.trim();	// remove the carriage return so that if the user has type quit it matches
		
	    if( 0 == strInput.compareToIgnoreCase( "quit" ) )
	    {
    		System.out.print( "Quitting the Downloader..." );
    		m_bQuit = true;
	    }
	    else if( strInput.isEmpty() )
	    
	    // condition the input for how projectfreetv formats their URLs.
	    strInput = strInput.toLowerCase();	// make the name lower case
	    strInput = strInput.replace( ' ', '-' );	// replace spaces with dashes
	    
	    newShowInfo.m_strURLName = strInput;
	    newShowInfo.m_strShowURL = Constants.strPROJFREETV_BASE_SERIES_URL + strInput;
	    
	    return m_bQuit;
	}
	
	static private boolean GetStartingSeason( NewShowInfo newShowInfo )
	{
	    String strInput = "";
	    boolean bContinue = true;
	    
	    while( true == bContinue )
	    {
		    // catch the exception in case the user quits at this point
		    try
		    {
		    	strInput = m_Scanner.next();
		    	strInput = strInput.trim();
		    	newShowInfo.m_nStartingSeason = Integer.parseInt( strInput );
		    	bContinue = false;
		    }
		    catch( NumberFormatException e )
		    {
		    	if( 0 == strInput.compareToIgnoreCase( "quit" ) )
		    	{
		    		System.out.print( "Quitting the Downloader..." );
		    		bContinue = false; // leave this while loop
		    		m_bQuit = true; // we're good to quit the app
		    	}
		    	else
		    	{
		    		System.out.println(  "That didn't seem to be a number.  \nPlease enter a number for the season that you want to start watching.  To begin watching a show from the beginning, the starting season should be '1'." );
		    		bContinue = true;
		    	}
		    }
	    }

		return m_bQuit;
	}
	
    static public Scanner m_Scanner = new Scanner( System.in );
    
    static public boolean m_bQuit = false; 

}
