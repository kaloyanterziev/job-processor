package com.krterziev.jobprocessor.payload.request;

import java.util.List;

public record TasksRequest(List<TaskRequest> tasks) {

}
