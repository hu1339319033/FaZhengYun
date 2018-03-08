package com.example.fazhengyun.model;

import com.example.fazhengyun.xdroid.event.IBus;

/**
 * Created by wanglei on 2012/2/8.
 */

public class Event {

    /**
     * 测试EventBus用到的实体
     */
    public static class TestEvent implements IBus.IEvent {

        public String name;

        public TestEvent(String name) {
            this.name = name;
        }

        @Override
        public int getTag() {
            return 10;
        }
    }

    public static class SelectLinkEvent implements IBus.IEvent{

        private String desc,url,imageurl;

        public String getImageurl() {
            return imageurl;
        }

        public String getDesc() {
            return desc;
        }

        public String getUrl() {
            return url;
        }

        /**
         *
         * @param desc 标题
         * @param url url
         */
        public SelectLinkEvent(String desc,String url,String imageurl){
            this.desc = desc;
            this.url = url;
            this.imageurl = imageurl;
        }
        @Override
        public int getTag() {
            return 0;
        }
    }

    public static class FinishSelf implements IBus.IEvent{

        @Override
        public int getTag() {
            return 0;
        }
    }

    public static class RefreshHome implements IBus.IEvent{

        @Override
        public int getTag() {
            return 0;
        }
    }

    public static class AddAdapterItem implements IBus.IEvent{

        public AddAdapterItem(ResourceInfos.Item item){
            this.item = item;
        }
        @Override
        public int getTag() {
            return 0;
        }
        private ResourceInfos.Item item;

        public ResourceInfos.Item getItem() {
            return item;
        }

        public void setItem(ResourceInfos.Item item) {
            this.item = item;
        }
    }
}
