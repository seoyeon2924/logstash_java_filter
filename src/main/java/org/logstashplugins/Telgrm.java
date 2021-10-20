package org.logstashplugins;

// FIXME LOMBOK 사용해보기

public class Telgrm {

    private String field;
    private int fieldSize;
    private String chan_con_yn;
    private String chan_con_field;


    public Telgrm(String field, int fieldSize, String chan_con_yn, String chan_con_field) {
        this.field = field;
        this.fieldSize = fieldSize;
        this.chan_con_yn = chan_con_yn;
        this.chan_con_field = chan_con_field;

    }


    @Override
    public String toString() {
        return "field:'" + field + '\'' +
                ", fieldSize:" + fieldSize +
                ", chan_con_yn:" + chan_con_yn +
                ", chan_con_field:" + chan_con_field

                ;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public int getFieldSize() {
        return fieldSize;
    }

    public void setFieldSize(int fieldSize) {
        this.fieldSize = fieldSize;
    }

    public String getChan_con_yn() {
        return chan_con_yn;
    }

    public void setChan_con_yn(String chan_con_yn) {
        this.chan_con_yn = chan_con_yn;
    }

    public String getChan_con_field() {
        return chan_con_field;
    }

    public void setChan_con_field(String chan_con_field) {
        this.chan_con_field = chan_con_field;
    }
}
