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
        String sourceField = "message";
        Configuration config = new ConfigurationImpl(Collections.singletonMap("source", sourceField));
        Context context = new ContextImpl(null, null);
        TelgrmParsingFilterTgw filter = new TelgrmParsingFilterTgw("test-id", config, context);

        Event e = new org.logstash.Event();
        TestMatchListener matchListener = new TestMatchListener();
        Object f = "16:03:25:98 S 00 00 000000 2739400000 W061 W0617065202005072030522739400000ON06240000        001  334586121                                                                                                                         N                0000000004                                                                                       002O11캐쉬백선정산        000000001000000001000000000110T11T선정산             000000000000000000000000000000000004O34이벤트포인트        0000001000O33복지포인트          0000000000O39신규복지포인트      0000467700O36OCB PP포인트        0000000000000000                                                                                                                          \n";

        e.setField(sourceField, f);
        Collection<Event> results = filter.filter(Collections.singletonList(e), matchListener);
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