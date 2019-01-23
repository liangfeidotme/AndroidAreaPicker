package me.liangfei.areapicker.sample.pojo;

import java.util.List;

public class ResponseData {
    public List<Branch> branches;

    public static final class Branch {
        public String query;
        public String text;
    }
}
