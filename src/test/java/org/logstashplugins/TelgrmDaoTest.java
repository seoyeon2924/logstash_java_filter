package org.logstashplugins;

import org.junit.Assert;
import org.junit.Test;

import java.util.Iterator;
import java.util.List;

public class TelgrmDaoTest {

    @Test
    public void getTelgrmTest() {
        TelgramInfoDao telgramInfoDao = new TelgramInfoDao();
        List<Telgrm> Telgrms = TelgrmParsingFilterTgw.telgrmMap.get("A400");
        Object f = "10:52:40:60 R __ __ 000000 0827000002 A400 A4004810202108271052090827000002ON0556    31441601001A              K20091757       2208160348202108271052092172010000038088=0000                                                                                                                                                                                                                                                                                                                                                                                                 1140N0000063150080000063150                                              ";
        Iterator<Telgrm> it = Telgrms.iterator();
        String telgrmString = ((String) f).substring(43);
        int telgrmLength = telgrmString.length();
        int nextIndex = 0;
        while(it.hasNext()) {
            Telgrm Telgrm = it.next();
            String stringToFill = "";
            if(nextIndex < telgrmLength) {
                stringToFill = telgrmString.substring(nextIndex, nextIndex + Telgrm.getFieldSize());
            }
            System.out.println(Telgrm.getField() + "(" + Telgrm.getFieldSize() + "): " + stringToFill);
            nextIndex += Telgrm.getFieldSize();
        }

        for(Telgrm Telgrm: Telgrms) {
            System.out.println(Telgrm);
        }
        Assert.assertEquals("organ_cd", Telgrms.get(1).getField());
    }

}
