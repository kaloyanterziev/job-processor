package com.krterziev.jobprocessor.transformers;

import com.krterziev.jobprocessor.models.Task;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class BashScriptTransformer {

  private final static String BASH_COMMENT = "#!/usr/bin/env bash";

  private BashScriptTransformer() {
  }

  public static InputStream transform(List<Task> tasks) {
    final StringBuilder stringBuilder = new StringBuilder(BASH_COMMENT).append("\n\n");
    for (final Task task : tasks) {
      stringBuilder.append(task.command()).append("\n");
    }
    return new ByteArrayInputStream(stringBuilder.toString().getBytes(StandardCharsets.UTF_8));
  }

}
