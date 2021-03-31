package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Address;
import seedu.address.model.person.Company;
import seedu.address.model.person.Email;
import seedu.address.model.person.JobTitle;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Remark;
import seedu.address.model.tag.Tag;

/**
 * Delete tags of person in the address book.
 */
public class DeleteTagCommand extends TagCommand {

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Delete tags of person(s) in the address book. "
            + "Parameters: " + DELETE_SUB_COMMAND_WORD + " INDEX ... [-t] TAG ...\n"
            + "Example: " + COMMAND_WORD + " " + DELETE_SUB_COMMAND_WORD + " 1 2 Python";

    public static final String MESSAGE_SUCCESS = "Tag(s) deleted from %1%d person: %2$s";

    private final Set<Tag> tags;
    private final List<Index> targetIndexes;
    private final boolean isShownIndex;
    private final boolean isSelectedIndex;

    /**
     * Creates an DeleteTagCommand.
     */
    private DeleteTagCommand(List<Index> targetIndexes, Set<Tag> tags, boolean isShownIndex, boolean isSelectedIndex) {
        requireAllNonNull(targetIndexes, tags, isShownIndex, isSelectedIndex);
        this.targetIndexes = targetIndexes;
        this.tags = tags;
        this.isShownIndex = isShownIndex;
        this.isSelectedIndex = isSelectedIndex;
    }

    /**
     * Creates an DeleteTagCommand that deletes the specified {@code tags} to all the shown items in the list.
     */
    public static DeleteTagCommand createWithShownIndex(Set<Tag> tags) {
        return new DeleteTagCommand(new ArrayList<>(), tags, true, false);
    }

    /**
     * Creates an DeleteTagCommand that deletes the specified {@code tags} to all the selected items in the list.
     */
    public static DeleteTagCommand createWithSelectedIndex(Set<Tag> tags) {
        return new DeleteTagCommand(new ArrayList<>(), tags, false, true);
    }

    /**
     * Creates an DeleteTagCommand that deletes the specified {@code tags} to all the items in the
     * {@code targetIndexes}.
     */
    public static DeleteTagCommand createWithTargetIndexes(List<Index> targetIndexes, Set<Tag> tags) {
        return new DeleteTagCommand(targetIndexes, tags, false, false);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (isShownIndex) {
            return deleteFromShownIndex(model);
        }
        if (isSelectedIndex) {
            return deleteFromSelectedIndex(model);
        }
        return deleteFromTargetIndexes(model);
    }

    /**
     * Delete tags from shown person from {@code Model}.
     *
     * @param model {@code Model} which the command should operate on.
     * @return feedback message of the operation result for display
     * @throws CommandException if index is invalid
     */
    private CommandResult deleteFromShownIndex(Model model) throws CommandException {
        List<Person> personList = model.getFilteredPersonList();
        if (model.getFilteredPersonList().size() == 0) {
            throw new CommandException(MESSAGE_NO_SHOWN_PERSON);
        }

        for (Person person : personList) {
            Person editedPerson = createEditedPerson(person, tags);
            model.setPerson(person, editedPerson);
        }

        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_SUCCESS, personList.size(), tags.toString()));
    }

    /**
     * Delete tags from selected person from {@code Model}.
     *
     * @param model {@code Model} which the command should operate on.
     * @return feedback message of the operation result for display
     * @throws CommandException if index is invalid
     */
    private CommandResult deleteFromSelectedIndex(Model model) throws CommandException {
        model.applySelectedPredicate();
        if (model.getFilteredPersonList().size() == 0) {
            throw new CommandException(MESSAGE_NO_SELECTED_PERSON);
        }
        return deleteFromShownIndex(model);
    }

    /**
     * Delete tags from person from {@code Model} based on the {@code targetIndexes}.
     *
     * @param model {@code Model} which the command should operate on.
     * @return feedback message of the operation result for display
     * @throws CommandException if index is invalid
     */
    private CommandResult deleteFromTargetIndexes(Model model) throws CommandException {
        List<Person> shownList = model.getFilteredPersonList();

        // Validate indexes
        for (Index targetIndex : targetIndexes) {
            if (targetIndex.getZeroBased() >= shownList.size()) {
                throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
            }
        }

        for (Index targetIndex : targetIndexes) {
            Person person = shownList.get(targetIndex.getZeroBased());
            Person editedPerson = createEditedPerson(person, tags);
            model.setPerson(person, editedPerson);
        }

        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_SUCCESS, targetIndexes.size(), tags.toString()));
    }

    /**
     * Creates and returns a {@code Person} after adding {@code tags}.
     */
    private static Person createEditedPerson(Person personToEdit, Set<Tag> tags) {
        assert personToEdit != null;

        Name updatedName = personToEdit.getName();
        Phone updatedPhone = personToEdit.getPhone();
        Email updatedEmail = personToEdit.getEmail();
        Company updatedCompany = personToEdit.getCompany();
        JobTitle updatedJobTitle = personToEdit.getJobTitle();
        Address updatedAddress = personToEdit.getAddress();
        Remark updatedRemark = personToEdit.getRemark();
        Set<Tag> updatedTags = new HashSet<>(personToEdit.getTags());
        updatedTags.removeAll(tags);

        return new Person(updatedName, updatedPhone, updatedEmail, updatedCompany, updatedJobTitle,
                updatedAddress, updatedRemark, updatedTags);
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof DeleteTagCommand // instanceof handles nulls
                && tags.equals(((DeleteTagCommand) other).tags)
                && targetIndexes.equals(((DeleteTagCommand) other).targetIndexes)
                && isShownIndex == ((DeleteTagCommand) other).isShownIndex)
                && isSelectedIndex == ((DeleteTagCommand) other).isSelectedIndex;
    }

}
