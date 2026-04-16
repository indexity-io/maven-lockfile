package io.github.chains_project.maven_lockfile.data;

import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsLast;

import io.github.chains_project.maven_lockfile.checksum.RepositoryInformation;
import java.util.Comparator;
import java.util.Objects;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;

public class Pom implements Comparable<Pom> {

    private static final Comparator<Pom> COMPARE_GAV_DETAILS =
            comparing(Pom::getGroupId).thenComparing(Pom::getArtifactId).thenComparing(Pom::getVersion);

    private static final Comparator<Pom> COMPARE_CHECKSUM_DETAILS =
            comparing(Pom::getChecksumAlgorithm).thenComparing(Pom::getChecksum);

    // Poms are either defined by their relative path or resolved from a repository by their GAV.
    // We cannot know where poms defined by their relative path will be hosted and thus their
    // resolved fields are null in this case.
    private static final Comparator<Pom> COMPARE_REPOSITORY_DETAILS = comparing(
                    Pom::getResolved, nullsLast(naturalOrder()))
            .thenComparing(Pom::getRepositoryId, nullsLast(naturalOrder()))
            .thenComparing(Pom::getRelativePath, nullsLast(naturalOrder()));

    private static final Comparator<Pom> COMPARE_ALL_DETAILS =
            COMPARE_GAV_DETAILS.thenComparing(COMPARE_CHECKSUM_DETAILS).thenComparing(COMPARE_REPOSITORY_DETAILS);

    public static final Comparator<Pom> COMPARE_ALL = compare(COMPARE_ALL_DETAILS);
    public static final Comparator<Pom> COMPARE_GAV = compare(COMPARE_GAV_DETAILS);
    public static final Comparator<Pom> COMPARE_CHECKSUM = compare(COMPARE_CHECKSUM_DETAILS);
    public static final Comparator<Pom> COMPARE_REPOSITORY = compare(COMPARE_REPOSITORY_DETAILS);

    private static Comparator<Pom> compare(Comparator<Pom> baseComparator) {
        return baseComparator.thenComparing(
                (p1, p2) -> nullsLast(compare(baseComparator)).compare(p1.getParent(), p2.getParent()));
    }

    private final GroupId groupId;
    private final ArtifactId artifactId;
    private final VersionNumber version;
    private final String relativePath;
    private final ResolvedUrl resolved;
    private final RepositoryId repositoryId;
    private final String checksumAlgorithm;
    private final String checksum;
    private final Pom parent;

    public Pom(
            GroupId groupId,
            ArtifactId artifactId,
            VersionNumber version,
            String relativePath,
            ResolvedUrl resolved,
            RepositoryId repositoryId,
            String checksumAlgorithm,
            String checksum,
            Pom parent) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.relativePath = relativePath;
        this.resolved = resolved;
        this.repositoryId = repositoryId;
        this.checksumAlgorithm = checksumAlgorithm;
        this.checksum = checksum;
        this.parent = parent;
    }

    public GroupId getGroupId() {
        return groupId;
    }

    public ArtifactId getArtifactId() {
        return artifactId;
    }

    public VersionNumber getVersion() {
        return version;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public ResolvedUrl getResolved() {
        return resolved;
    }

    public RepositoryId getRepositoryId() {
        return repositoryId;
    }

    public String getChecksumAlgorithm() {
        return checksumAlgorithm;
    }

    public String getChecksum() {
        return checksum;
    }

    public Pom getParent() {
        return parent;
    }

    @Override
    public int compareTo(Pom o) {
        return COMPARE_ALL.compare(this, o);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Pom)) {
            return false;
        }
        Pom other = (Pom) obj;
        return COMPARE_ALL.compare(this, other) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                groupId,
                artifactId,
                version,
                relativePath,
                resolved,
                repositoryId,
                checksumAlgorithm,
                checksum,
                parent);
    }
}
