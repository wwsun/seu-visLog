package seu.url;

public class LogUrlManager {
	
	private static final LogUrlManager urlManager = new LogUrlManager();
	
	private LogUrlManager() {
		
	}
	
	/**
	 * static factory method
	 * @return A new instance of LogUrlManager
	 */
	public static LogUrlManager GetInstance(){
		return urlManager;
	}
	
	/**
	 * 第一次过滤：过滤得到原始的 request, referer
	 * @param input		mr.data.txt	-	MapReduce后的数据集<request_hostName, dateTime, request, referer>
	 * @param output	1st.txt		-	规范化url，去掉所有的无效来源的记录
	 */
	public void FilterRequestAndResponse(String input, String output){
		LogFilter.getRequestAndReferer(input, output);
	}
	
	
	/**
	 * 第二次过滤：过滤出所有由站外进入站内的请求
	 * @param input		1st.txt
	 * @param output	2nd.txt	-	过滤得到所有站外进入站内的节点
	 */
	public void FilterOutsiteRequest(String input, String output){
		LogFilter.getOutsiteRequests(input, output);
	}
	
	/**
	 * 第三次：清洗所有的入站url，过滤所有的请求参数
	 * @param input		2nd.txt
	 * @param output	3rd.txt	-	过滤掉所有入站url的请求参数，提取入站url的主机地址
	 * 			<request_host, request, referer_host>
	 */
	public void CleanOutsiteRequest(String input, String output){
		LogFilter.cleanOutsiteRequests(input, output);
	}
	
	/**
	 * 
	 * @param input		3rd.txt
	 * @param output	4th.txt - 仅保留入站的host，和请求页面的host
     *        (request_host, referer_host)
	 */
	public void getHostOfRequestAndReferer(String input, String output){
		LogFilter.getRequestAndRefererHost(input, output);
	}

    /**
     *
     * @param input     4th_count.txt   (request_host, referer_host, dup_count)
     * @param output    5th.txt         (referer_host, dup_count, referer_domain)
     *        then      (request_host, referer_host, dup_count, referer_domain) -> JSON file
     */
    public void getOutsiteHostDistribution(String input, String output){
        LogFilter.getOutsiteHostDistribution(input, output);
    }
}
