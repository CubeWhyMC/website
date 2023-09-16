package org.cubewhy.api.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GameInfo implements BaseData {
    String version;
    String branch;
    String module;
}
