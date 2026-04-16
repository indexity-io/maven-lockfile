package io.github.chains_project.maven_lockfile.reporting;

import io.github.chains_project.maven_lockfile.JsonUtils;
import io.github.chains_project.maven_lockfile.data.Config;
import io.github.chains_project.maven_lockfile.data.Pom;
import io.github.chains_project.maven_lockfile.reporting.Differences.Action;
import io.github.chains_project.maven_lockfile.reporting.Differences.Difference;
import java.util.ArrayList;

public class PomDifference {

    public static Differences pomDifferences(Pom pomFromLockfile, Pom pomFromProject, Config config) {
        if (pomFromLockfile.equals(pomFromProject)) return Differences.EMPTY;

        var differences = new ArrayList<Difference>();
        var failureAction = config.isAllowPomValidationFailure() ? Action.Warn : Action.Error;

        if (Pom.COMPARE_REPOSITORY.compare(pomFromLockfile, pomFromProject) != 0) {
            differences.add(new Difference(
                    config.isAllowRepositoryValidationFailure() ? Action.Warn : failureAction,
                    "Repository details changed for parent pom(s)."));
        }

        if (Pom.COMPARE_CHECKSUM.compare(pomFromLockfile, pomFromProject) != 0) {
            differences.add(new Difference(failureAction, "Checksum mismatch in pom or parent pom(s)."));
        }

        if (Pom.COMPARE_GAV.compare(pomFromLockfile, pomFromProject) != 0) {
            differences.add(new Difference(failureAction, "Pom or parent pom(s) GAV mismatch."));
        }
        var detail = "Your lockfile pom:\n"
                + JsonUtils.toJson(pomFromLockfile)
                + "\n" + "Your project pom:\n"
                + JsonUtils.toJson(pomFromProject);
        return new Differences("Pom validation failed.", differences, detail);
    }
}
