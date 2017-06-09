package com.blassingame.downloader.application;

public class Constants
{
	
/********************************************************************************/
//	INI FILE CONSTANTS
/********************************************************************************/
	// key: Download Value(s):  Completed
	static public String strINI_KEY_DOWNLOAD = "Download";
	static public String strINI_VALUE_COMPLETED = "Completed";
	static public String strINI_VALUE_INCOMPLETE = "Incomplete";
	
	// key: Site  Value(s): VODLocker, Cloudzilla, StreaminTo, VidziTv, TheVideoMe, VidBull
	static public String strINI_KEY_SITE = "Site";
	static public String strINI_VALUE_VODLOCKER = "VODLocker";
	static public String strINI_VALUE_CLOUDZILLA = "Cloudzilla";
	static public String strINI_VALUE_STREAMINTO = "StreaminTo";
	static public String strINI_VALUE_VIDZITV = "VidziTv";
	static public String strINI_VALUE_THEVIDEOME = "TheVideoMe";
	static public String strINI_VALUE_VIDBULL = "VidBull";
	
	// key: Watched  Value(s): No, Yes
	static public String strINI_KEY_WATCHED = "Watched";
	static public String strINI_VALUE_NO = "No";
	static public String strINI_VALUE_YES = "Yes";
	
	// these are the keys for serializing a Show
	static public String strINI_KEY_SHOWURL = "ShowURL";
	static public String strINI_KEY_SHOWURLNAME = "ShowURLName";
	static public String strINI_KEY_SHOWSTARTINGSEASON = "StartingSeason";
	
/********************************************************************************/
//	String Constants
/********************************************************************************/
	// projectfreetv url constants
	static public String strPROJFREETV_BASE_URL = "http://project-free-tv.li/";
	static public String strPROJFREETV_BASE_SERIES_URL = "http://project-free-tv.li/free/";
	
	// preferred video sites - search strings
	static public String strSITE_CLOUDZILLA = "cloudzilla.to";
	static public String strSITE_VODLOCKER = "vodlocker.com";
	static public String strSITE_STREAMINTO = "streamin.to";
	static public String strSITE_VIDZITV = "vidzi.tv";
	static public String strSITE_THEVIDEOME = "thevideo.me";
	static public String strSITE_VIDBULL = "vidbull.com";
	// preferred video sites - site types
	static public String strSITETYPE_CLOUDZILLA = "cloudzilla";
	static public String strSITETYPE_VODLOCKER = "vodlocker";
	static public String strSITETYPE_STREAMINTO = "streaminto";
	static public String strSITETYPE_VIDZITV = "vidzitv";
	static public String strSITETYPE_THEVIDEOME = "thevideoMe";
	static public String strSITETYPE_VIDBULL = "vidbull";
	
	// ParseUtility eval constants
	static public String strEVAL = "eval";
	
	// VidziTv javascript eval constants
	static public String strVIDZITV_SCRIPT = "function(p,a,c,k,e,d){while(c--)if(k[c])p=p.replace(new RegExp('\\\\b'+c.toString(a)+'\\\\b','g'),k[c]);return p}('2(\"2n\").2m({2l:[{2k:\"5://r.q.p.o/i/2j/2i/8.2h\",2g:\"5://2f.7.9/2e/7.2d\",2c:[{h:\"5://7.9/8-n.2b?2a=1\",29:\\'28\\'},{h:\"5://r.q.p.o/27/v.26\"}],25:[{h:\"5://7.9/8-n.24\",23:\"22\"}]}],21:1z,1y:\"1x\",1w:3,1v:1u,1t:1,1s:1r,1q:1p,1o:{1n:l,1m:\\'1l\\',1k:\\'1j\\',1i:20},1h:1g});g a;g f=0;g 6=0;2().1f(4(x){b(6>0)f+=x.m-6;6=x.m;b(0!=0&&f>=0){6=-1;2().1e();2().1d(l);$(\\'#1c\\').e();$(\\'d.c\\').e()}});2().1b(4(x){6=-1});2().1a(4(x){k(x)});2().19(4(){$(\\'d.c\\').e()});4 k(x){$(\\'d.c\\').18();b(a)17;a=1;$.16(\\'5://7.9/j-15/14.j?13=12&11=8&10=z-y-w-u-t\\',4(s){})}',36,96,'||jwplayer||function|http|p01265917|vidzi|ae6a0karwgfg|tv|vvplay|if|video_ad|div|show|tt1265917|var|file||cgi|doPlay|false|position|84264de8ef5394e5c1114028c0df0089|149|98|195|91|data|5714942bae6ad228d34df7d8421a3a66|1455430263||123||70|1265917|hash|file_code|ajax|op|index_dl|bin|get|return|hide|onComplete|onPlay|onSeek|play_limit_box|setFullscreen|stop|onTime|600|width|fontsize|Verdana|fontFamily|FFFFFF|color|back|captions|900|hls_maxbackbufferlength|300|hls_maxbufferlength|hls_startfromlevel|true|androidhls|hls_minbufferlength|flash|primary|338||height|thumbnails|kind|vtt|tracks|mp4|onuqjc65zy2qedz7njvb5grbajlkvpf2e6bfype2awj6epcyi56ep7j4422a|hls|type|embed|m3u8|sources|swf|nplayer|static|provider|jpg|00253|01|image|playlist|setup|vplayer'.split('|'))";
	static public String strVIDZITV_FUNCTION = "function(p,a,c,k,e,d){while(c--)if(k[c])p=p.replace(new RegExp('\\\\b'+c.toString(a)+'\\\\b','g'),k[c]);return p}";
	static public String strVIDZITV_PARAMS = "'2(\"2n\").2m({2l:[{2k:\"5://r.q.p.o/i/2j/2i/8.2h\",2g:\"5://2f.7.9/2e/7.2d\",2c:[{h:\"5://7.9/8-n.2b?2a=1\",29:\\'28\\'},{h:\"5://r.q.p.o/27/v.26\"}],25:[{h:\"5://7.9/8-n.24\",23:\"22\"}]}],21:1z,1y:\"1x\",1w:3,1v:1u,1t:1,1s:1r,1q:1p,1o:{1n:l,1m:\\'1l\\',1k:\\'1j\\',1i:20},1h:1g});g a;g f=0;g 6=0;2().1f(4(x){b(6>0)f+=x.m-6;6=x.m;b(0!=0&&f>=0){6=-1;2().1e();2().1d(l);$(\\'#1c\\').e();$(\\'d.c\\').e()}});2().1b(4(x){6=-1});2().1a(4(x){k(x)});2().19(4(){$(\\'d.c\\').e()});4 k(x){$(\\'d.c\\').18();b(a)17;a=1;$.16(\\'5://7.9/j-15/14.j?13=12&11=8&10=z-y-w-u-t\\',4(s){})}',36,96,'||jwplayer||function|http|p01265917|vidzi|ae6a0karwgfg|tv|vvplay|if|video_ad|div|show|tt1265917|var|file||cgi|doPlay|false|position|84264de8ef5394e5c1114028c0df0089|149|98|195|91|data|5714942bae6ad228d34df7d8421a3a66|1455430263||123||70|1265917|hash|file_code|ajax|op|index_dl|bin|get|return|hide|onComplete|onPlay|onSeek|play_limit_box|setFullscreen|stop|onTime|600|width|fontsize|Verdana|fontFamily|FFFFFF|color|back|captions|900|hls_maxbackbufferlength|300|hls_maxbufferlength|hls_startfromlevel|true|androidhls|hls_minbufferlength|flash|primary|338||height|thumbnails|kind|vtt|tracks|mp4|onuqjc65zy2qedz7njvb5grbajlkvpf2e6bfype2awj6epcyi56ep7j4422a|hls|type|embed|m3u8|sources|swf|nplayer|static|provider|jpg|00253|01|image|playlist|setup|vplayer'.split('|')";
	
/********************************************************************************/
//	Integer Constants
/********************************************************************************/
	static public int nNUM_OF_DOWNLOADS = 4;
	static public int nNUM_OF_DOWNLOAD_THREADS = 8;
	
}
