package org.cubewhy.api.entity;

import lombok.Data;

@Data
public class CrashReport {
    String trace;
    String type;
    String launchScript;
}
