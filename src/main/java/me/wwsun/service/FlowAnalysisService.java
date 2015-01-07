package me.wwsun.service;

import com.mongodb.DB;
import com.mongodb.DBObject;
import me.wwsun.CountryDAO;
import me.wwsun.util.FileUtil;

import java.util.List;

/**
 * Created by Weiwei on 1/7/2015.
 */
public class FlowAnalysisService {
    CountryDAO countryDAO;

    public FlowAnalysisService(final DB siteDatabase) {
        countryDAO = new CountryDAO(siteDatabase);
    }

    public void getOverviewFlowMap() {
        List<DBObject> list = countryDAO.getGeoDistribution();
        FileUtil.outputAsJSON(list, "geo-full");
    }
}
