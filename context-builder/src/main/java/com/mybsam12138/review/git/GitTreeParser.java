package com.mybsam12138.review.git;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

public class GitTreeParser {

    public static CanonicalTreeParser prepareTreeParser(Repository repository, String ref) throws Exception {

        ObjectId objectId = repository.resolve(ref);

        try (ObjectReader reader = repository.newObjectReader()) {

            CanonicalTreeParser treeParser = new CanonicalTreeParser();

            treeParser.reset(reader, repository.resolve(ref + "^{tree}"));

            return treeParser;
        }
    }
}