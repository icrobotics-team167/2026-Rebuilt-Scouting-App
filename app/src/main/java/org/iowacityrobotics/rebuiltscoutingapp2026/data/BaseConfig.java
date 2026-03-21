package org.iowacityrobotics.rebuiltscoutingapp2026.data;

public interface BaseConfig {
    enum DataType { NUMBER, TEXT, BOOLEAN }

    class Field {
        public int viewId;
        public String jsonKey;
        public DataType type;

        public Field(int viewId, String jsonKey, DataType type) {
            this.viewId = viewId;
            this.jsonKey = jsonKey;
            this.type = type;
        }
    }
}