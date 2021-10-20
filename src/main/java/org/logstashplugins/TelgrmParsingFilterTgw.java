package org.logstashplugins;

import co.elastic.logstash.api.Configuration;
import co.elastic.logstash.api.Context;
import co.elastic.logstash.api.Event;
import co.elastic.logstash.api.Filter;
import co.elastic.logstash.api.FilterMatchListener;
import co.elastic.logstash.api.LogstashPlugin;
import co.elastic.logstash.api.PluginConfigSpec;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.logging.Logger;


// class name must match plugin name
@LogstashPlugin(name = "telgrm_parsing_filter_tgw")
public class TelgrmParsingFilterTgw implements Filter {

    public static final PluginConfigSpec<String> SOURCE_CONFIG =
            PluginConfigSpec.stringSetting("source", "message");
    private final static Logger LOG = Logger.getGlobal();
    public static Map<String, List<Telgrm>> telgrmMap = new HashMap<>();
    private String id;
    private String sourceField;
    private TelgramDao telgramInfoDao;


    public TelgrmParsingFilterTgw(String id, Configuration config, Context context) {
        // constructors should validate configuration options
        this.id = id;
        this.sourceField = config.get(SOURCE_CONFIG);
        this.telgramInfoDao = new TelgramDao();
    }

    @Override
    public Collection<Event> filter(Collection<Event> events, FilterMatchListener matchListener) {
        for (Event e : events) {
            Object f = e.getField(sourceField);
            if (f instanceof String) {

                String telgrmNo = ((String) f).substring(38, 42);
                String telgrmString = ((String) f).substring(43);

                byte[] telgrmByte = new byte[0];

                try {
                    telgrmByte = telgrmString.getBytes("euc-kr");
                } catch (UnsupportedEncodingException unsupportedEncodingException) {
                    unsupportedEncodingException.printStackTrace();
                }

                if (telgrmMap == null) {
                    telgramInfoDao.getTelgrm();
                }

                List<Telgrm> Telgrms = telgrmMap.get(telgrmNo);
                if (Telgrms == null || Telgrms.size() == 0) {
                    return events;
                }

                ListIterator<Telgrm> it = Telgrms.listIterator();

                int telgrmLength = telgrmString.length();
                int nextIndex = 0;
                int cnt = 0;
                while (it.hasNext()) {
                    Telgrm Telgrm = it.next();

                    //System.out.println("Telgrm: " + Telgrm);

                    int begin = nextIndex;
                    if (nextIndex >= telgrmLength) {
                        break;
                    }
                    int end = nextIndex + Telgrm.getFieldSize();
                    if (end >= telgrmLength) {
                        end = telgrmLength;
                    }

                    String nextField = "";
                    byte[] nextFieldByte = Arrays.copyOfRange(telgrmByte, begin, end);
                    try {
                        nextField = new String(nextFieldByte, "euc-kr");
                    } catch (UnsupportedEncodingException unsupportedEncodingException) {
                        unsupportedEncodingException.printStackTrace();
                    }

                    e.setField(Telgrm.getField(), nextField);
                    nextIndex += Telgrm.getFieldSize();

                    // fixme 여기서 부터 코드가 아파 ... 반복 처리 필요한 전문인지
                    if (Telgrm.getChan_con_yn() != null && Telgrm.getChan_con_yn()
                            .equals("Y")) { // sub이 존재

                        begin = nextIndex;

                        //몇번 반복을 돌려야 하는지 찾기
                        int grid_cnt = Integer.parseInt(nextField);
                        Map<String, Integer> subTelgrm = new HashMap<>();

                        // 반복을 돌릴 Map 생성
                        while (true) {
                            if (!it.hasNext()) {
                                break;
                            }
                            Telgrm = it.next();
                            if (Telgrm.getChan_con_field() == null) {
                                break;
                            } else {
                                subTelgrm.put(Telgrm.getField(), Telgrm.getFieldSize());
                            }
                        }
                        it.previous(); // 반복문 빠져나간 다음을 위해 ,

                        // map 생성 후 grid_cnt 만큼 반복하기
                        int name = 1;
                        while (grid_cnt > 0) {
                            for (Map.Entry<String, Integer> entry : subTelgrm.entrySet()) {

                                end = begin + entry.getValue();
                                nextFieldByte = Arrays.copyOfRange(telgrmByte, begin, end);

                                try {
                                    nextField = new String(nextFieldByte, "euc-kr");
                                } catch (UnsupportedEncodingException unsupportedEncodingException) {
                                    unsupportedEncodingException.printStackTrace();
                                }
                                begin += entry.getValue();
                                e.setField("SUB_" + entry.getKey() + "_" + name, nextField);
                            }
                            grid_cnt--;
                            name++;
                        }

                        // 반복 돌린만큼 nextIndex 계산 해주기
                        nextIndex = end;
                    }
                }

            }

            matchListener.filterMatched(e);
        }

        return events;
    }

    @Override
    public Collection<PluginConfigSpec<?>> configSchema() {
        // should return a list of all configuration options for this plugin
        return Collections.singletonList(SOURCE_CONFIG);
    }

    @Override
    public String getId() {
        return this.id;
    }
}
