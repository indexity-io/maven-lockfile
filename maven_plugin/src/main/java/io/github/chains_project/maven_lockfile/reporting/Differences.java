package io.github.chains_project.maven_lockfile.reporting;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.maven.plugin.MojoExecutionException;

public class Differences {

    public static final Differences EMPTY = new Differences("", List.of(), "");

    private final String message;
    private final List<Difference> differences;
    private final String detail;

    public Differences(String message, List<Difference> differences, String detail) {
        this.message = message;
        this.differences = differences;
        this.detail = detail;
    }

    public String getMessage() {
        return message;
    }

    public List<Difference> getDifferences() {
        return differences;
    }

    public String getDetail() {
        return detail;
    }

    public boolean isDifferent() {
        return !differences.isEmpty();
    }

    public void reportOrFail() throws MojoExecutionException {
        if (differences.isEmpty()) return;

        var errors =
                differences.stream().filter(d -> d.getAction() == Action.Error).collect(Collectors.toList());
        var warnings =
                differences.stream().filter(d -> d.getAction() == Action.Warn).collect(Collectors.toList());

        var sb = new StringBuilder(message);
        if (!warnings.isEmpty()) {
            sb.append("\nWarnings:");
            for (var warning : warnings) {
                sb.append("\n").append(" - ").append(warning.getMessage());
            }
        }
        if (!errors.isEmpty()) {
            sb.append("\nErrors:");
            for (var error : errors) {
                sb.append("\n").append(" - ").append(error.getMessage());
            }
        }

        if (errors.isEmpty()) {
            PluginLogManager.getLog().warn(sb.toString());
        } else {
            sb.append("\n").append(detail);
            throw new MojoExecutionException(sb.toString());
        }
    }

    public Differences combinedWith(Differences other) {
        if (!isDifferent()) return other;
        if (!other.isDifferent()) return this;

        var combinedDifferences =
                Stream.concat(differences.stream(), other.differences.stream()).collect(Collectors.toList());

        return new Differences(
                String.join("\n", message, other.message),
                combinedDifferences,
                String.join("\n", detail, other.detail));
    }

    public enum Action {
        Ignore,
        Warn,
        Error
    }

    public static class Difference {

        private final Action action;
        private final String message;

        public Difference(Action action, String message) {
            this.action = action;
            this.message = message;
        }

        public Action getAction() {
            return action;
        }

        public String getMessage() {
            return message;
        }
    }
}
