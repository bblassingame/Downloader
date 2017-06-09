package com.blassingame.downloader.show;

import java.util.LinkedList;

public class NewShowInfo
{
	
	public String m_strURLName = "";
	public String m_strShowURL = "";
	public int m_nStartingSeason = 1;
	
}

@SuppressWarnings("serial")
class NewShowInfoQueue extends LinkedList<NewShowInfo> {}
