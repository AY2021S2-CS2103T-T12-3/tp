package seedu.address.model.person;

import org.junit.jupiter.api.Test;
import seedu.address.testutil.PersonBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FieldsContainsKeywordsPredicateTest {

    @Test
    public void equals() {
        List<String> firstPredicateKeywordList = Collections.singletonList("first");
        List<String> secondPredicateKeywordList = Arrays.asList("first", "second");

        FieldsContainsKeywordsPredicate firstPredicate = new FieldsContainsKeywordsPredicate(firstPredicateKeywordList);
        FieldsContainsKeywordsPredicate secondPredicate = new FieldsContainsKeywordsPredicate(secondPredicateKeywordList);

        // same object -> returns true
        assertEquals(firstPredicate, firstPredicate);

        // same values -> returns true
        FieldsContainsKeywordsPredicate firstPredicateCopy = new FieldsContainsKeywordsPredicate(firstPredicateKeywordList);
        assertEquals(firstPredicateCopy, firstPredicate);

        // different types -> returns false
        assertNotEquals(firstPredicate, 1);

        // null -> returns false
        assertNotEquals(firstPredicate, null);

        // different person -> returns false
        assertNotEquals(secondPredicate, firstPredicate);
    }

    @Test
    public void test_nameContainsKeywords_returnsTrue() {
        Person testPerson = new PersonBuilder().withName("Alice Bob").withPhone("12345")
                .withEmail("alice@email.com").withAddress("Main Street").withRemark("HR Manager")
                .withTags("Marketing", "Manager", "Executive").build();

        // One keyword
        FieldsContainsKeywordsPredicate predicate = new FieldsContainsKeywordsPredicate(Collections.singletonList("Alice"));
        assertTrue(predicate.test(testPerson));

        // Multiple keywords
        predicate = new FieldsContainsKeywordsPredicate(Arrays.asList("Alice", "Executive"));
        assertTrue(predicate.test(testPerson));

        // Only one matching keyword
        predicate = new FieldsContainsKeywordsPredicate(Arrays.asList("Executive", "Carol"));
        assertTrue(predicate.test(testPerson));

        // Mixed-case keywords
        predicate = new FieldsContainsKeywordsPredicate(Arrays.asList("aLIce", "bOB"));
        assertTrue(predicate.test(testPerson));

        // Similar name keywords
        predicate = new FieldsContainsKeywordsPredicate(Collections.singletonList("Manage"));
        assertTrue(predicate.test(testPerson));
    }

    @Test
    public void test_nameDoesNotContainKeywords_returnsFalse() {
        Person testPerson = new PersonBuilder().withName("Alice Gordon").withPhone("12345")
                .withEmail("alice@email.com").withAddress("Main Street").withRemark("HR Manager")
                .withTags("Marketing", "Manager", "Executive").build();

        // Zero keywords
        FieldsContainsKeywordsPredicate predicate = new FieldsContainsKeywordsPredicate(Collections.emptyList());
        assertFalse(predicate.test(testPerson));

        // Non-matching keyword
        predicate = new FieldsContainsKeywordsPredicate(Collections.singletonList("Carolina"));
        assertFalse(predicate.test(testPerson));
    }

    @Test void test_nameComparatorSort_compare() {
        Person testPerson1 = new PersonBuilder().withName("Alice Gordon").withPhone("12345")
                .withEmail("alice@email.com").withAddress("Main Street").withRemark("User Research Team Manager")
                .withTags("Marketing", "Research", "Executive").build();
        Person testPerson2 = new PersonBuilder().withName("Alice Gordon").withPhone("12345")
                .withEmail("alice@email.com").withAddress("Main Street").withRemark("Market Researcher Team Manager")
                .withTags("Marketing", "Researcher", "Executive", "Manager").build();

        // Sort by number of matched fields
        Comparator<Person> comparatorB = new FieldsContainsKeywordsPredicate(Collections.singletonList("Manager"));
        assertTrue(comparatorB.compare(testPerson1, testPerson2) > 0);
    }
}
