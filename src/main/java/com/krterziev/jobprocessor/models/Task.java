package com.krterziev.jobprocessor.models;

import java.util.Set;

public record Task(String name, String command, Set<String> requires) {

}
