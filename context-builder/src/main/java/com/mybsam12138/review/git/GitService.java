package com.mybsam12138.review.git;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.Repository;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;


@Service
public class GitService {

    /**
     * Return the list of {@link DiffEntry} objects between HEAD~1 and HEAD.
     */
    public List<DiffEntry> getLatestCommitDiff(String repoPath) throws Exception {
        try (Git git = Git.open(new File(repoPath))) {
            Repository repository = git.getRepository();

            return git.diff()
                    .setOldTree(GitTreeParser.prepareTreeParser(repository, "HEAD~1"))
                    .setNewTree(GitTreeParser.prepareTreeParser(repository, "HEAD"))
                    .call();
        }
    }

    /**
     * Return the unified diff text between HEAD~1 and HEAD.
     * This is what will be embedded into the final review prompt.
     */
    public String getUnifiedDiff(String repoPath) throws Exception {
        try (Git git = Git.open(new File(repoPath))) {
            Repository repository = git.getRepository();

            var oldTreeIter = GitTreeParser.prepareTreeParser(repository, "HEAD~1");
            var newTreeIter = GitTreeParser.prepareTreeParser(repository, "HEAD");

            try (ByteArrayOutputStream out = new ByteArrayOutputStream();
                 DiffFormatter diffFormatter = new DiffFormatter(out)) {

                diffFormatter.setRepository(repository);
                diffFormatter.setDiffComparator(RawTextComparator.DEFAULT);
                diffFormatter.setDetectRenames(true);

                List<DiffEntry> entries = diffFormatter.scan(oldTreeIter, newTreeIter);
                for (DiffEntry entry : entries) {
                    diffFormatter.format(entry);
                }
                diffFormatter.flush();

                return out.toString(StandardCharsets.UTF_8);
            }
        }
    }

    /**
     * Return the relative paths of Java files changed between HEAD~1 and HEAD.
     */
    public List<String> getChangedJavaFiles(String repoPath) throws Exception {
        return getLatestCommitDiff(repoPath).stream()
                .map(DiffEntry::getNewPath)
                .filter(path -> path.endsWith(".java"))
                .toList();
    }
}