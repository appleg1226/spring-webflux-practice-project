package com.chong.webfluxpractice.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
@Builder
public class ItemInformation{
    @Id
    private String id;
    private String name;
    private Type type;

    public static enum Type {
        EQUIP, CONSUME, OTHER
    }
}
