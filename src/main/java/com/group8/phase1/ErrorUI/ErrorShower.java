package com.group8.phase1.ErrorUI;

import javax.swing.*;

/**
 * The {@code ErrorShower} class provides an e asy way to display error messages.
 */
public class ErrorShower {

    /**
     * Constructs an {@code ErrorShower} object and displays an error message dialog.
     *
     * @param error the error message to be displayed
     */
    public ErrorShower(String error) {
        JOptionPane.showMessageDialog(new JFrame(), error, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
