package com.chong.webfluxpractice.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document
@Builder
public class Shop {
    @Id
    private String id;
    private List<ItemInformation> itemList;
}
