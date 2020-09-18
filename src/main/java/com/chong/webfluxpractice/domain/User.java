package com.chong.webfluxpractice.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Document
@Builder
public class User{

    @Id
    private String userId;
    @NotNull
    private String serverName;
    @Size(min = 0, message = "gold can't be less than 0")
    private int gold;
    private List<ItemInformation> inventory;
}
