package me.wwsun;

import com.mongodb.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Weiwei on 12/23/2014.
 */
public class KeywordDAO {
    private DBCollection keywordCollection;

    public KeywordDAO(final DB siteDatabase) {
        keywordCollection = siteDatabase.getCollection("keywords");
    }

    public List getKeywordList() {
        DBCursor cursor = keywordCollection.find();
        List outList = new ArrayList();
        while(cursor.hasNext()) {
            DBObject obj = cursor.next();
            String keyword = obj.get("keywords").toString();
            String dup = obj.get("sum").toString();
            List innerList = new ArrayList();
            //filter the low-value keywords
            if(Integer.valueOf(dup) > 9){
                innerList.add(keyword);
                innerList.add(Integer.valueOf(dup));
                outList.add(innerList.toArray());
            }
        }
        cursor.close();
        return outList;
    }

}
