package org.logstashplugins;

import co.elastic.logstash.api.Configuration;
import co.elastic.logstash.api.Context;
import co.elastic.logstash.api.Event;
import co.elastic.logstash.api.FilterMatchListener;
import org.junit.Assert;
import org.junit.Test;
import org.logstash.plugins.ConfigurationImpl;
import org.logstash.plugins.ContextImpl;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

public class TelgrmParsingFilterTgwTest {

    @Test
    public void testJavaExampleFilter() {
        String sourceField = "A400";
        Configuration config = new ConfigurationImpl(Collections.singletonMap("source", sourceField));
        Context context = new ContextImpl(null, null);
        TelgrmParsingFilterTgw filter = new TelgrmParsingFilterTgw("test-id", config, context);

        Event e = new org.logstash.Event();
        TestMatchListener matchListener = new TestMatchListener();
        Object f = "10:52:40:60 R __ __ 000000 0827000002 A400 A4004810202100271052090027000002ON0556    31441601001A              K20091757       2208160348202108271052092172010000038088=0000                                                                                                                                                                                                                                                                                                                                                                                                 1140N0000063150080000063150           안녕하세요                  abcdabcde01002de01002de01002de01002de01002de01002de01002de01002de01002de01002de01002de01002de01002de01002de01002de01002de01002de01002de01002de01002de01002de01002de01002de01002de01002de01002de113bcde01002de01002de01002de01002defge01002defg            ";

        e.setField(sourceField, f);
        Collection<Event> results = filter.filter(Collections.singletonList(e), matchListener);

        System.out.println(e);
    }

}

class TestMatchListener implements FilterMatchListener {

    private AtomicInteger matchCount = new AtomicInteger(0);

    @Override
    public void filterMatched(Event event) {
        matchCount.incrementAndGet();
    }

    public int getMatchCount() {
        return matchCount.get();
    }
}