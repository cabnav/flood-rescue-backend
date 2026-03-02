package com.floodrescue.backend.rescue.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MissionAssignmentResponseRequest {
    /**
     * Decision of the rescuer for the assignment.
     * Expected values: ACCEPT or DECLINE.
     */
    private String decision;

    /**
     * Optional reason when the rescuer declines the assignment.
     * Required on DECLINE.
     */
    private String reason;
}

