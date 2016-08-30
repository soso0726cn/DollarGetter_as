package com.common.config;

/**
 * Created by leejunpeng on 2016/1/28.
 */
public class ConfigBean {

    /**
     * CHANNEL : keda08
     * INTERFACE : km
     * REVIVE_ACTION : com.sprite.magic.punctual
     * ICON : no
     * EP_MAIN_CLAZZ : aue.xos.wx.e.a
     */

    private ConfigsEntity configs;

    public void setConfigs(ConfigsEntity configs) {
        this.configs = configs;
    }

    public ConfigsEntity getConfigs() {
        return configs;
    }

    public static class ConfigsEntity {
        private String SWITCH;
        private String CHANNEL;
        private String INTERFACE;
        private String REVIVE_ACTION;
        private String ICON;
        private String EP_MAIN_CLAZZ;
        private int LOG_LEVEL;

        private String BYTECRYPT_KEY1;
        private String BYTECRYPT_KEY2;

        private String PKG_NAME;

        public String getPKG_NAME() {
            return PKG_NAME;
        }

        public void setPKG_NAME(String PKG_NAME) {
            this.PKG_NAME = PKG_NAME;
        }

        public String getSO_KEY() {
            return SO_KEY;
        }

        public void setSO_KEY(String SO_KEY) {
            this.SO_KEY = SO_KEY;
        }

        private String SO_KEY;
        private String PACKAGE_ADD_ACTION;
        private String NOTI_CLICKED_ACTION;

        public String getPACKAGE_ADD_ACTION() {
            return PACKAGE_ADD_ACTION;
        }

        public void setPACKAGE_ADD_ACTION(String PACKAGE_ADD_ACTION) {
            this.PACKAGE_ADD_ACTION = PACKAGE_ADD_ACTION;
        }

        public String getNOTI_CLICKED_ACTION() {
            return NOTI_CLICKED_ACTION;
        }

        public void setNOTI_CLICKED_ACTION(String NOTI_CLICKED_ACTION) {
            this.NOTI_CLICKED_ACTION = NOTI_CLICKED_ACTION;
        }

        public String getBYTECRYPT_KEY1() {
            return BYTECRYPT_KEY1;
        }

        public void setBYTECRYPT_KEY1(String BYTECRYPT_KEY1) {
            this.BYTECRYPT_KEY1 = BYTECRYPT_KEY1;
        }

        public String getBYTECRYPT_KEY2() {
            return BYTECRYPT_KEY2;
        }

        public void setBYTECRYPT_KEY2(String BYTECRYPT_KEY2) {
            this.BYTECRYPT_KEY2 = BYTECRYPT_KEY2;
        }

        public void setSWITCH(String SWITCH) { this.SWITCH = SWITCH; }

        public void setCHANNEL(String CHANNEL) {
            this.CHANNEL = CHANNEL;
        }

        public void setINTERFACE(String INTERFACE) {
            this.INTERFACE = INTERFACE;
        }

        public void setREVIVE_ACTION(String REVIVE_ACTION) {
            this.REVIVE_ACTION = REVIVE_ACTION;
        }

        public void setICON(String ICON) {
            this.ICON = ICON;
        }

        public void setEP_MAIN_CLAZZ(String EP_MAIN_CLAZZ) {
            this.EP_MAIN_CLAZZ = EP_MAIN_CLAZZ;
        }

        public void setLOG_LEVEL(int LOG_LEVEL) { this.LOG_LEVEL = LOG_LEVEL; }

        public String getSWITCH() { return SWITCH; }

        public String getCHANNEL() {
            return CHANNEL;
        }

        public String getINTERFACE() {
            return INTERFACE;
        }

        public String getREVIVE_ACTION() {
            return REVIVE_ACTION;
        }

        public String getICON() {
            return ICON;
        }

        public String getEP_MAIN_CLAZZ() {
            return EP_MAIN_CLAZZ;
        }

        public int getLOG_LEVEL() { return LOG_LEVEL; }

        @Override
        public String toString() {
            return "ConfigsEntity{" +
                    "SWITCH='" + SWITCH + '\'' +
                    ", CHANNEL='" + CHANNEL + '\'' +
                    ", INTERFACE='" + INTERFACE + '\'' +
                    ", REVIVE_ACTION='" + REVIVE_ACTION + '\'' +
                    ", ICON='" + ICON + '\'' +
                    ", EP_MAIN_CLAZZ='" + EP_MAIN_CLAZZ + '\'' +
                    ", LOG_LEVEL='" + LOG_LEVEL + '\'' +
                    '}';
        }
    }
}
