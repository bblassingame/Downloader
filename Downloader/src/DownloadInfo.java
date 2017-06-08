import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

enum eDOWNLOADSTATUS
{
	eDOWNLOADSTATUS_NONE,
	eDOWNLOADSTATUS_SUCCESS,
	eDOWNLOADSTATUS_ERROR
}

public class DownloadInfo
{
	
	public void Clone( DownloadInfo dlInfo )
	{
		this.m_strID			= dlInfo.m_strID;
		this.m_strURL			= dlInfo.m_strURL;
		this.m_strType			= dlInfo.m_strType;
		this.m_strName			= dlInfo.m_strName;
		this.m_strSavePath		= dlInfo.m_strSavePath;
		this.m_strBookmarkID	= dlInfo.m_strBookmarkID;
		this.m_strReferer		= dlInfo.m_strReferer;
		this.m_nResponseCode	= dlInfo.m_nResponseCode;
		for( String s : dlInfo.m_aCookies )
		{
			this.m_aCookies.add(s);
		}
		this.m_cbVideoSiteDLComplete = dlInfo.m_cbVideoSiteDLComplete;
	}

	public String	m_strID = UUID.randomUUID().toString();
	public String 	m_strURL = "";
	public String 	m_strType = "";
	public String 	m_strName = "";
	public String	m_strSavePath = "";
	public String	m_strBookmarkID = "";
	public String	m_strReferer = "";
	public int		m_nResponseCode = -1;
	public eDOWNLOADSTATUS m_eDownloadStatus = eDOWNLOADSTATUS.eDOWNLOADSTATUS_NONE;
	public List<String> m_aCookies = new ArrayList<String>();
	public VideoSiteCallback m_cbVideoSiteDLComplete = null;
	
}

@SuppressWarnings("serial")
class DownloadInfoArray extends ArrayList<DownloadInfo> {}
@SuppressWarnings("serial")
class DownloadInfoQueue extends LinkedList<DownloadInfo> {}