package ru.tinkoff.formatting;


import ru.tinkoff.formatting.watchers.FormatWatcher;

public interface FormattedTextChangeListener {
    boolean beforeFormatting(String oldValue, String newValue);

    void onTextFormatted(FormatWatcher formatter, String newFormattedText);
}