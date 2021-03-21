package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.HashMap;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import seedu.address.logic.parser.ParserUtil;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.alias.Alias;
import seedu.address.model.alias.Command;
import seedu.address.model.alias.CommandAlias;
import seedu.address.model.alias.exceptions.AliasNotFoundException;
import seedu.address.model.alias.exceptions.DuplicateAliasException;

/**
 * A map of aliases to commands that enforces uniqueness between its elements and does not allow nulls.
 * An alias is considered unique by comparing using {@code Alias#equals(Object)}.
 */
public class UniqueAliasMap implements ReadOnlyUniqueAliasMap {

    private static final String EMPTY_ALIASES = "You currently have 0 alias";

    private final ObservableMap<Alias, CommandAlias> internalList = FXCollections.observableMap(new HashMap<>());
    private final ObservableMap<Alias, CommandAlias> internalUnmodifiableList =
            FXCollections.unmodifiableObservableMap(internalList);

    public UniqueAliasMap() {}

    /**
     * Creates an UniqueAliasMap using the aliases in the {@code toBeCopied}
     */
    public UniqueAliasMap(ReadOnlyUniqueAliasMap toBeCopied) {
        this();
        resetData(toBeCopied);
    }

    /**
     * Replaces the contents of the aliases with {@code aliases}.
     * {@code aliases} must not contain duplicate aliases.
     *
     * @throws DuplicateAliasException if there are duplicate aliases in {@code aliases}.
     */
    public void setAliases(Map<Alias, CommandAlias> aliases) {
        requireAllNonNull(aliases);
        if (!aliasesAreUnique(aliases)) {
            throw new DuplicateAliasException();
        }
        internalList.clear();
        internalList.putAll(aliases);
    }

    /**
     * Resets the existing data of this {@code UniqueAliasMap} with {@code newData}.
     */
    public void resetData(ReadOnlyUniqueAliasMap newData) {
        requireNonNull(newData);

        setAliases(newData.getAliases());
    }

    /**
     * Returns true if the aliases contains an equivalent alias as the given argument.
     */
    public boolean hasAlias(Alias alias) {
        requireNonNull(alias);
        return internalList.containsKey(alias);
    }

    /**
     * Returns true if the aliases contains an equivalent command alias as the given argument.
     */
    public boolean hasCommandAlias(CommandAlias commandAlias) {
        requireNonNull(commandAlias);
        return hasAlias(commandAlias.getAlias());
    }

    /**
     * Adds a command alias to the aliases.
     * The alias must not already exist in the aliases.
     */
    public void addAlias(CommandAlias toAdd) {
        requireNonNull(toAdd);
        if (hasCommandAlias(toAdd)) {
            throw new DuplicateAliasException();
        }
        internalList.put(toAdd.getAlias(), toAdd);
    }

    /**
     * Removes the equivalent alias from the aliases.
     * The alias must exist in the aliases.
     *
     * @throws AliasNotFoundException if the alias {@code toRemove} does not exist and not found in {@code aliases}.
     */
    public void removeAlias(Alias toRemove) {
        requireNonNull(toRemove);
        if (!hasAlias(toRemove)) {
            throw new AliasNotFoundException();
        }
        internalList.remove(toRemove);
    }

    /**
     * Returns command alias mapped to alias if alias is found in aliases.
     * If alias is not found, null is returned.
     *
     * @param alias alias to search in aliases.
     */
    public CommandAlias getCommandAlias(Alias alias) {
        return internalList.get(alias);
    }

    /**
     * Returns command mapped to alias if alias is found in aliases.
     * If alias is not found, null is returned.
     *
     * @param alias alias to search in aliases.
     */
    public Command getCommand(Alias alias) {
        CommandAlias commandAlias = getCommandAlias(alias);

        if (commandAlias == null) {
            return null;
        }

        return getCommandAlias(alias).getCommand();
    }

    @Override
    public String parseAliasToCommand(String userInput) {
        Alias alias;
        try {
            alias = ParserUtil.parseAlias(userInput);
        } catch (ParseException pe) {
            return userInput;
        }

        if (!hasAlias(alias)) {
            return userInput;
        }

        return getCommand(alias).toString();
    }

    @Override
    public ObservableMap<Alias, CommandAlias> getAliases() {
        return internalUnmodifiableList;
    }

    @Override
    public String toString() {
        if (internalList.isEmpty()) {
            return EMPTY_ALIASES;
        }
        final StringBuilder builder = new StringBuilder();
        for (CommandAlias commandAlias: internalList.values()) {
            builder.append(commandAlias);
            builder.append("\n");
        }
        return builder.toString();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof UniqueAliasMap // instanceof handles nulls
                && internalList.equals(((UniqueAliasMap) other).internalList));
    }

    @Override
    public int hashCode() {
        return internalList.hashCode();
    }

    /**
     * Returns true if {@code aliases} contains only unique aliases.
     */
    private boolean aliasesAreUnique(Map<Alias, CommandAlias> aliases) {
        UniqueAliasMap checkDuplicate = new UniqueAliasMap();
        for (CommandAlias commandAlias: aliases.values()) {
            if (checkDuplicate.hasCommandAlias(commandAlias)) {
                return false;
            }
            checkDuplicate.addAlias(commandAlias);
        }
        return true;
    }

}
