package com.microservice.apiserver.request;

/**
 * Created by cjl on 2018/4/25.
 */
public class AnnotationOriginTextRequest {

        private String id;
        private String text;

//    public AnnotationOriginTextRequest(String id, String text) {
//        this.id = id;
//        this.text = text;
//    }

    public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
}
