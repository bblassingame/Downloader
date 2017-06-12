package com.blassingame.downloader.logger;

import java.util.LinkedList;

import com.blassingame.downloader.logger.LogMgr.eCONSOLE_LOG_LEVEL;

public class LogData
{
	
	public LogData()
	{
		m_strLogMessage = "";
		m_eLogLevel = eCONSOLE_LOG_LEVEL.eCONSOLE_LOG_LEVEL_INFO;
		m_strClassName = "";
		m_strMethodName = "";
	}

	public String m_strLogMessage;
	public eCONSOLE_LOG_LEVEL m_eLogLevel;
	public String m_strClassName;
	public String m_strMethodName;
	
}


@SuppressWarnings("serial")
class LogDataQueue extends LinkedList<LogData> {}