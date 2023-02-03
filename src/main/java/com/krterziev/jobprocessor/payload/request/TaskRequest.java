package com.krterziev.jobprocessor.payload.request;

import java.util.List;

public record TaskRequest(String name, String command, List<String> requires) {

}
