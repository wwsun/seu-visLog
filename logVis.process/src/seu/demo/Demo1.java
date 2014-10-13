package seu.demo;

import seu.url.LogUrlManager;

public class Demo1 {
	public static void main(String[] args) {
		LogUrlManager urlManager = LogUrlManager.GetInstance();
		//urlManager.FilterRequestAndResponse("mr.data.txt", "1st.txt");
		//urlManager.FilterOutsiteRequest("1st.txt", "2nd.txt");
		//urlManager.CleanOutsiteRequest("2nd.txt", "3rd.txt");
		//urlManager.getHostOfRequestAndReferer("3rd.txt", "4th.txt");
        urlManager.getOutsiteHostDistribution("4th_count.txt", "5th.csv");
	}
}
