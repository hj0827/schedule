package com.hj.web.login.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RouterVO {
    private String name;
    private String path;
    private String component;
    private Meta meta;
    @Data
    @AllArgsConstructor
    public class Meta{
        private String title;
        private String icon;
        private Object[] roles;
    }
    private List<RouterVO> children = new ArrayList<>();

}
