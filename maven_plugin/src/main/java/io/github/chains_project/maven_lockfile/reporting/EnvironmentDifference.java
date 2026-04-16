package io.github.chains_project.maven_lockfile.reporting;

import io.github.chains_project.maven_lockfile.data.Config;
import io.github.chains_project.maven_lockfile.data.Environment;
import java.util.ArrayList;
import java.util.Objects;

public class EnvironmentDifference {

    public static Differences environmentDifference(
            Environment envFromLockfile, Environment envFromProject, Config config) {
        if (envFromLockfile.equals(envFromProject)) return Differences.EMPTY;

        var differences = new ArrayList<Differences.Difference>();
        var failureAction =
                config.isAllowEnvironmentalValidationFailure() ? Differences.Action.Warn : Differences.Action.Error;

        if (!Objects.equals(envFromLockfile.getOsName(), envFromProject.getOsName())) {
            differences.add(new Differences.Difference(failureAction, "OS name mismatched."));
        }
        if (!Objects.equals(envFromLockfile.getMavenVersion(), envFromProject.getMavenVersion())) {
            differences.add(new Differences.Difference(failureAction, "Maven version mismatched."));
        }
        if (!Objects.equals(envFromLockfile.getJavaVersion(), envFromProject.getJavaVersion())) {
            differences.add(new Differences.Difference(failureAction, "Java version mismatched."));
        }

        var detail = "Lockfile environment: " + envFromLockfile + "\n" + "Project environment:  " + envFromProject;

        return new Differences("Lock file environment validation failed.", differences, detail);
    }
}
