package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.commons.core.Messages.MESSAGE_UNKNOWN_COMMAND;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.AliasCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.FilterCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.ReadOnlyUniqueAliasMap;

/**
 * Parses user input.
 */
public class AddressBookParser {

    /**
     * Used for initial separation of command word and args.
     */
    private static final Pattern BASIC_COMMAND_FORMAT = Pattern.compile("(?<commandWord>\\S+)(?<arguments>.*)");

    /**
     * Parses user input into command for execution.
     *
     * @param userInput full user input string
     * @param aliases aliases reference for user command input to be parsed into the aliased command
     * @return the command based on the user input
     * @throws ParseException if the user input does not conform the expected format
     */
    public Command parseCommand(String userInput, ReadOnlyUniqueAliasMap aliases) throws ParseException {
        Matcher matcher = BASIC_COMMAND_FORMAT.matcher(userInput.stripLeading());
        if (!matcher.matches()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
        }

        final String alias = matcher.group("commandWord");
        String arguments = matcher.group("arguments");

        final String aliasCommand = aliases.parseAliasToCommand(alias);
        matcher = BASIC_COMMAND_FORMAT.matcher(aliasCommand.trim());
        matcher.matches();

        final String commandWord = matcher.group("commandWord");
        arguments = matcher.group("arguments") + arguments;

        switch (commandWord) {

        case AddCommand.COMMAND_WORD:
            return new AddCommandParser().parse(arguments);

        case EditCommand.COMMAND_WORD:
            return new EditCommandParser().parse(arguments);

        case DeleteCommand.COMMAND_WORD:
            return new DeleteCommandParser().parse(arguments);

        case ClearCommand.COMMAND_WORD:
            return new ClearCommand();

        case FindCommand.COMMAND_WORD:
            return new FindCommandParser().parse(arguments);

        case ListCommand.COMMAND_WORD:
            return new ListCommand();

        case FilterCommand.COMMAND_WORD:
            return new FilterCommandParser().parse(arguments);

        case ExitCommand.COMMAND_WORD:
            return new ExitCommand();

        case HelpCommand.COMMAND_WORD:
            return new HelpCommand();

        case AliasCommand.COMMAND_WORD:
            return new AliasCommandParser().parse(arguments);

        default:
            throw new ParseException(MESSAGE_UNKNOWN_COMMAND);
        }
    }

    /**
     * Returns true if a given user input is a valid command to be aliased.
     *
     * @param userInput full user input string
     */
    public boolean isValidCommandToAlias(String userInput) {
        final Matcher matcher = BASIC_COMMAND_FORMAT.matcher(userInput.trim());
        if (!matcher.matches()) {
            return false;
        }

        final String commandWord = matcher.group("commandWord");
        final String arguments = matcher.group("arguments");

        switch (commandWord) {

        case ClearCommand.COMMAND_WORD:

        case ListCommand.COMMAND_WORD:

        case ExitCommand.COMMAND_WORD:

        case HelpCommand.COMMAND_WORD:
            return true;

        case FindCommand.COMMAND_WORD:
            return new FindCommandParser().isValidCommandToAlias(arguments);

        case AddCommand.COMMAND_WORD:
            return new AddCommandParser().isValidCommandToAlias(arguments);

        case EditCommand.COMMAND_WORD:
            return new EditCommandParser().isValidCommandToAlias(arguments);

        case DeleteCommand.COMMAND_WORD:
            return new DeleteCommandParser().isValidCommandToAlias(arguments);

        case AliasCommand.COMMAND_WORD:
            return new AliasCommandParser().isValidCommandToAlias(arguments);

        default:
            return false;
        }
    }

}
