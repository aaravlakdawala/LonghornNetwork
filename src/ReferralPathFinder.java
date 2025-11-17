import java.util.*;

/**
 * Finds referral paths from a starting student to any student who has worked
 * at a target company. The implementation typically relies on a
 * {@link StudentGraph} representation and priority queue search.
 */
public class ReferralPathFinder {
    /**
     * Create a finder backed by the provided student graph.
     *
     * @param graph the {@link StudentGraph} used for searches
     */
    public ReferralPathFinder(StudentGraph graph) {
        // Constructor
    }

    /**
     * Attempt to find a referral path from the given start student to any
     * student who lists {@code targetCompany} in their previous internships.
     *
     * @param start         the starting {@link UniversityStudent}
     * @param targetCompany the company name to search for
     * @return an ordered list of {@link UniversityStudent} instances forming a
     *         referral chain, or an empty list if no path exists
     */
    public List<UniversityStudent> findReferralPath(UniversityStudent start, String targetCompany) {
        // Method signature only
        return new ArrayList<>();
    }
}
