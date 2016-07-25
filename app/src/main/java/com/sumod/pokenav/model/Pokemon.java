package com.sumod.pokenav.model;


import java.util.List;

import lombok.Data;


@Data
public class Pokemon extends BaseModel {
    Integer ndex;
    String name;
    String description;
    String imageUrl;
    List<String> types;
}
