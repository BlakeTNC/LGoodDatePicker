package com.lgooddatepicker.timepicker;

import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
import com.lgooddatepicker.zinternaltools.TimeMenuPanel;
import java.awt.*;
import javax.swing.border.*;
import com.lgooddatepicker.optionalusertools.PickerUtilities;
import com.lgooddatepicker.optionalusertools.TimeChangeListener;
import com.lgooddatepicker.optionalusertools.TimeVetoPolicy;
import com.lgooddatepicker.zinternaltools.CustomPopup;
import com.lgooddatepicker.zinternaltools.InternalConstants;
import com.lgooddatepicker.zinternaltools.InternalUtilities;
import com.lgooddatepicker.zinternaltools.TimeChangeEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.Instant;
import java.time.LocalTime;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * TimePicker, This class implements a time picker GUI component.
 *
 * This class supports a complete set of "default functionality" without requiring any
 * TimePickerSettings. However, the settings of a time picker can optionally be customized by
 * creating a TimePickerSettings instance and passing it to the TimePicker constructor. After the
 * time picker is constructed, the settings instance should not be changed.
 *
 * By default, the language and internationalization settings of a time picker are determined from
 * the operating system defaults using Locale.getDefault(). If desired, the locale and language can
 * be modified by passing a locale to the constructor of a TimePickerSettings instance, and passing
 * that instance to the constructor of a TimePicker.
 *
 * Automatic Time Validation: Every time picker stores its current text, and its last valid time.
 * The last valid time is returned when you call TimePicker.getTime(). If a person uses their
 * keyboard to type into the text field, any text that is not a valid time will be displayed in red,
 * any vetoed time will have a strikethrough, and any valid time will be displayed in black. Valid
 * times are automatically committed to the time picker. Any invalid or vetoed text is automatically
 * reverted to the last valid time whenever the the time picker loses focus.
 *
 * <code>
 * // Basic usage example:
 * // Create a time picker.
 * TimePicker timePicker = new TimePicker();
 * // Create a panel, and add the time picker.
 * JPanel panel = new JPanel();
 * panel.add(timePicker);
 * </code>
 */
public class TimePicker
        extends JPanel implements CustomPopup.CustomPopupCloseListener {

    /**
     * lastPopupCloseTime, This holds a timestamp that indicates when the popup menu was last
     * closed. This is used to implement a workaround for event behavior that was causing the time
     * picker class to erroneously re-open the menu when the user was clicking on the show menu
     * button in an attempt to close the previous menu.
     */
    private Instant lastPopupCloseTime = Instant.now();

    /**
     * lastValidTime, This holds the last valid time that was entered into the time picker. This
     * value is returned from the function TimePicker.getTime();
     *
     * Implementation note: After initialization, variable should never be -set- directly. Instead,
     * use the time setting function that will notify the list of timeChangeListeners each time that
     * this value is changed.
     */
    private LocalTime lastValidTime = null;

    /**
     * popup, This is the custom popup instance for this time picker. This should remain null until
     * a popup is opened. Creating a custom popup class allowed us to control the details of when
     * the popup menu should be open or closed.
     */
    private CustomPopup popup = null;

    /**
     * settings, This holds the settings instance for this time picker. Default settings are
     * generated automatically. Custom settings may optionally be supplied in the TimePicker
     * constructor.
     */
    private TimePickerSettings settings;

    /**
     * skipTextFieldChangedFunctionWhileTrue, While this is true, the function
     * "zTextFieldChangedSoIndicateIfValidAndStoreWhenValid()" will not be executed in response to
     * time text field text change events.
     */
    private boolean skipTextFieldChangedFunctionWhileTrue = false;

    /**
     * timeChangeListeners, This holds a list of time change listeners that wish to be notified
     * whenever the last valid time is changed.
     */
    private ArrayList<TimeChangeListener> timeChangeListeners = new ArrayList<>();

    /**
     * timeMenuPanel, This holds the menu panel GUI component of this time picker. This should be
     * null when the time picker menu is closed, and hold a time menu panel instance when the time
     * picker menu is opened.
     */
    private TimeMenuPanel timeMenuPanel;

    /**
     * JFormDesigner GUI components, These variables are automatically generated by JFormDesigner.
     * This section should not be modified by hand, but only modified from within the JFormDesigner
     * program.
     */
    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JTextField timeTextField;
    private JButton toggleTimeMenuButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    /**
     * Constructor with Default Values, Create a time picker instance using the default operating
     * system locale and language, and default time picker settings.
     */
    public TimePicker() {
        this(null);
    }

    /**
     * Constructor with Custom Settings, Create a time picker instance using the supplied time
     * picker settings.
     */
    public TimePicker(TimePickerSettings timePickerSettings) {
        timePickerSettings = (timePickerSettings == null)
                ? new TimePickerSettings() : timePickerSettings;
        this.settings = timePickerSettings;
        initComponents();
        // Shrink the toggle calendar button to a reasonable size.
        toggleTimeMenuButton.setText("\u25BC");
        toggleTimeMenuButton.setMargin(new java.awt.Insets(4, 4, 4, 4));
        // Set the gap size between the text field and the toggle menu button.
        int gapPixels = (settings.gapBeforeButtonPixels == null)
                ? 0 : settings.gapBeforeButtonPixels;
        setGapSize(gapPixels, ConstantSize.PIXEL);
        // Set the editability of the text field.
        // This should be done before setting the default text field background color.
        timeTextField.setEditable(settings.allowKeyboardEditing);
        // Set the text field color and font attributes to normal.
        timeTextField.setBackground(Color.white);
        timeTextField.setForeground(settings.colorTextValidTime);
        timeTextField.setFont(settings.fontValidTime);
        // Set the text field border color based on whether the date picker is editable.
        Color textFieldBorderColor = (settings.allowKeyboardEditing)
                ? InternalConstants.colorEditableTextFieldBorder
                : InternalConstants.colorNotEditableTextFieldBorder;
        timeTextField.setBorder(new CompoundBorder(
                new MatteBorder(1, 1, 1, 1, textFieldBorderColor), new EmptyBorder(1, 3, 2, 2)));

        // Add a change listener to the text field.
        zAddTextChangeListener();

        // Listen for the down arrow key, as a way to open the menu.
        timeTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.isActionKey() && e.getKeyCode() == KeyEvent.VK_DOWN) {
                    openPopup();
                    timeMenuPanel.selectFirstEntry();
                }
            }
        });
        // Make sure that the initial time is in a valid state.
        if (settings.allowEmptyTimes == false && settings.initialTime == null) {
            settings.initialTime = LocalTime.of(7, 0);
        }
        // Set the initial time from the settings.
        // Note that no time listeners can exist until after the constructor has returned.
        setTime(settings.initialTime);

        // Create a toggleTimeMenuButton listener for mouse dragging events.
        // Note: The toggleTimeMenuButton listeners should be created here in the constructor, 
        // because they do not require the timeMenuPanel to exist. These listeners are never 
        // deregistered. They will continue to exist for as long as the time picker exists.
        toggleTimeMenuButton.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent event) {
                if (timeMenuPanel != null) {
                    timeMenuPanel.mouseDraggedFromToggleButton();
                }
            }
        });

        // Create a toggleTimeMenuButton listener for mouse release events.
        toggleTimeMenuButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent event) {
                // Do nothing if the mouse was released inside the toggle button.
                Point mousePositionOnScreen = MouseInfo.getPointerInfo().getLocation();
                Rectangle toggleBoundsOnScreen = toggleTimeMenuButton.getBounds();
                toggleBoundsOnScreen.setLocation(toggleTimeMenuButton.getLocationOnScreen());
                if (toggleBoundsOnScreen.contains(mousePositionOnScreen)) {
                    return;
                }
                if (timeMenuPanel != null) {
                    timeMenuPanel.mouseReleasedFromToggleButtonOutsideButton();
                }
            }
        });
    }

    /**
     * addTimeChangeListener, This adds a time change listener to this time picker. For additional
     * details, see the TimeChangeListener class documentation.
     */
    public void addTimeChangeListener(TimeChangeListener listener) {
        timeChangeListeners.add(listener);
    }

    /**
     * clear, This will clear the time picker text. This will also clear the last valid time.
     */
    public void clear() {
        // Calling this function with null clears the time picker text and the last valid time.
        setTime(null);
    }

    /**
     * closePopup, This closes the menu popup. The popup can close itself automatically, so this
     * function does not generally need to be called programmatically.
     *
     * Notes: The popup can be automatically closed for various reasons. 1) The user may press
     * escape. 2) The popup may lose focus. 3) The window may be moved. 4) The user may toggle the
     * menu with the "toggle menu" button. 5) The user may select a time in the menu.
     */
    public void closePopup() {
        if (popup != null) {
            // The popup.hide() function handles the de-registration of the various listeners
            // associated with the popup window. This also initiates a callback to the
            // TimePicker.zEventcustomPopupWasClosed() function.
            popup.hide();
        }
    }

    /**
     * getTime, This returns the last valid time, or returns null to represent an empty time.
     *
     * If "TimePickerSettings.allowEmptyTimes" is true, then this can return null. If allow empty
     * times is false, then this can never return null.
     *
     * Note: If the automatic validation of time picker text has not yet occurred, then the the last
     * valid time may not always match the current text.
     *
     * <pre>
     * Additional Text Validation Details:
     * Whenever the current time picker text is not valid, the value returned by getTime()
     * will generally not match the time picker text. The time picker can contain invalid text
     * whenever both items (1) and (2) below are true:
     *
     * 1) The user has manually typed text that cannot be parsed by the parsing formats into a valid
     * time, or the user has typed a time that is vetoed by a current veto policy, or the user has
     * cleared (or left only whitespace) in the text when allowEmptyTimes is false.
     *
     * 2) The time picker text field has continued to have focus, and therefore the automatic
     * validation (revert/commit) process has not yet occurred.
     * </pre>
     */
    public LocalTime getTime() {
        return lastValidTime;
    }

    /**
     * getTimeChangeListeners, This returns a new ArrayList, that contains any time change listeners
     * that are registered with this TimePicker.
     */
    public ArrayList<TimeChangeListener> getTimeChangeListeners() {
        return new ArrayList<>(timeChangeListeners);
    }

    /**
     * getTimeStringOrEmptyString, This will return the last valid time as a string. If the last
     * valid time is empty, this will return an empty string ("").
     *
     * Time values will be output in one of the following ISO-8601 formats: "HH:mm", "HH:mm:ss",
     * "HH:mm:ss.SSS", "HH:mm:ss.SSSSSS", "HH:mm:ss.SSSSSSSSS".
     *
     * The format used will be the shortest that outputs the full value of the time where the
     * omitted parts are implied to be zero.
     */
    public String getTimeStringOrEmptyString() {
        LocalTime time = getTime();
        return (time == null) ? "" : time.toString();
    }

    /**
     * getTimeStringOrSuppliedString, This will return the last valid time as a string. If the last
     * valid time is empty, this will return the value of emptyTimeString.
     *
     * Time values will be output in one of the following ISO-8601 formats: "HH:mm", "HH:mm:ss",
     * "HH:mm:ss.SSS", "HH:mm:ss.SSSSSS", "HH:mm:ss.SSSSSSSSS".
     *
     * The format used will be the shortest that outputs the full value of the time where the
     * omitted parts are implied to be zero.
     */
    public String getTimeStringOrSuppliedString(String emptyTimeString) {
        LocalTime time = getTime();
        return (time == null) ? emptyTimeString : time.toString();
    }

    /**
     * getText, This returns the current text that is present in the time picker text field. This
     * text can contain anything that was written by the user. It is specifically not guaranteed to
     * contain a valid time. This should not be used to retrieve the time picker time. Instead, use
     * TimePicker.getTime() for retrieving the time.
     */
    public String getText() {
        return timeTextField.getText();
    }

    /**
     * isPopupOpen, This returns true if the time menu popup is open. This returns false if the time
     * menu popup is closed
     */
    public boolean isPopupOpen() {
        return (popup != null);
    }

    /**
     * isTextFieldValid, This returns true if, and only if, the text field contains a parsable time
     * or a valid empty string. Note that this does not guarantee that the text in the text field is
     * in a standard format. Valid times can be in any one of the parsingFormats that are accepted
     * by the time picker.
     *
     * More specifically, this returns true if: 1) the text field contains a parsable time that
     * exists, and that has not been vetoed by a current veto policy, OR 2) (allowEmptyTime == true)
     * and timeTextField.getText().trim() contains an empty string. Otherwise returns false.
     */
    public boolean isTextFieldValid() {
        return isTextValid(timeTextField.getText());
    }

    /**
     * isTextValid, This function can be used to see if the supplied text represents a "valid time"
     * according to the settings of this time picker.
     *
     * More specifically, this returns true if: 1) the text contains a parsable time that exists,
     * and that has not been vetoed by a current veto policy, OR 2) (allowEmptyTimes == true) and
     * text.trim() contains an empty string. Otherwise returns false.
     */
    public boolean isTextValid(String text) {
        // If the text is null, return false.
        if (text == null) {
            return false;
        }
        // If the text is empty, return the value of allowEmptyTimes.
        text = text.trim();
        if (text.isEmpty()) {
            return settings.allowEmptyTimes;
        }
        // Try to get a parsed time.
        LocalTime parsedTime = InternalUtilities.getParsedTimeOrNull(text,
                settings.formatForDisplayTime, settings.formatForMenuTimes,
                settings.formatsForParsing, settings.timePickerLocale);

        // If the time could not be parsed, return false.
        if (parsedTime == null) {
            return false;
        }
        // If the time is vetoed, return false.
        TimeVetoPolicy vetoPolicy = settings.vetoPolicy;
        if (InternalUtilities.isTimeVetoed(vetoPolicy, parsedTime)) {
            return false;
        }
        // The time is valid, so return true.
        return true;
    }

    /**
     * isTimeAllowed, This checks to see if the specified time is allowed by any currently set veto
     * policy, and allowed by the current setting of allowEmptyTimes.
     *
     * If allowEmptyTimes is false, and the specified time is null, then this returns false.
     *
     * If a veto policy exists, and the specified time is vetoed, then this returns false.
     *
     * If the time is not vetoed, or if empty times are allowed and the time is null, then this
     * returns true.
     */
    public boolean isTimeAllowed(LocalTime time) {
        if (time == null) {
            return settings.allowEmptyTimes;
        }
        return (!(InternalUtilities.isTimeVetoed(settings.vetoPolicy, time)));
    }

    /**
     * openPopup, This creates and shows the menu popup.
     *
     * This function creates a new menu panel and a new custom popup instance each time that it is
     * called. The associated object instances are automatically disposed and set to null when a
     * popup is closed.
     */
    public void openPopup() {
        // If this function was called programmatically, we may need to change the focus to this
        // popup.
        if (!timeTextField.hasFocus()) {
            timeTextField.requestFocusInWindow();
        }
        // Create a new time menu.
        timeMenuPanel = new TimeMenuPanel(this, settings);

        // Create a new custom popup.
        popup = new CustomPopup(timeMenuPanel, SwingUtilities.getWindowAncestor(this),
                this, settings.borderTimePopup);
        popup.setMinimumSize(new Dimension(
                this.getSize().width + 1, timeMenuPanel.getSize().height));
        //   int popupX = toggleTimeMenuButton.getLocationOnScreen().x
        //          + toggleTimeMenuButton.getBounds().width - popup.getBounds().width;
        int popupX = timeTextField.getLocationOnScreen().x;
        //     int popupY = toggleTimeMenuButton.getLocationOnScreen().y
        //              + toggleTimeMenuButton.getBounds().height - 1;
        int popupY = timeTextField.getLocationOnScreen().y
                + timeTextField.getSize().height - 1;
        popup.setLocation(popupX, popupY);
        // Show the popup and request focus.
        popup.show();
        timeMenuPanel.requestListFocus();
    }

    /**
     * removeTimeChangeListener, This removes the specified time change listener from this time
     * picker.
     */
    public void removeTimeChangeListener(TimeChangeListener listener) {
        timeChangeListeners.remove(listener);
    }

    /**
     * setGapSize, This sets the size of the gap between the time picker and the toggle menu button.
     */
    public void setGapSize(int gapSize, ConstantSize.Unit units) {
        ConstantSize gapSizeObject = new ConstantSize(gapSize, units);
        ColumnSpec columnSpec = ColumnSpec.createGap(gapSizeObject);
        FormLayout layout = (FormLayout) getLayout();
        layout.setColumnSpec(2, columnSpec);
    }

    /**
     * setText, This sets the text of the time picker text field to the supplied value. This will
     * have the same effect on the last valid time as if the user was typing into the text field. In
     * other words, it may or may not change the last valid time. This should not be used to set the
     * time of the time picker, instead use TimePicker.setTime().
     */
    public void setText(String text) {
        zInternalSetTimeTextField(text);
        timeTextField.requestFocusInWindow();
    }

    /**
     * setTime, This sets this time picker to the specified time. Times that are set from this
     * function are processed through the same validation procedures as times that are entered by
     * the user.
     *
     * More specifically:
     *
     * The "veto policy" and "allowEmptyTimes" settings are used to determine whether or not a
     * particular value is "allowed".
     *
     * Allowed values will be set in the text field, and also committed to the "last valid value".
     * Disallowed values will be set in the text field (with a disallowed indicator font), but will
     * not be committed to the "last valid value".
     *
     * A value can be checked against any current veto policy, and against the allowEmptyTimes
     * setting, by calling isTimeAllowed(). This can be used to determine (in advance), if a
     * particular value would be allowed.
     *
     * Note: If empty times are allowed, and the component does not have a veto policy, then all
     * possible values will (always) be allowed. These are the default settings of this component.
     *
     * Implementation Note: Whenever the text field changes to a valid time string, the
     * lastValidTime is also automatically set (unless the time is vetoed). This occurs through the
     * DocumentListener which is registered on the timeTextField.
     */
    public void setTime(LocalTime optionalTime) {
        // Set the text field to the supplied time, using the standard format for null, or a time.
        String standardTimeString = zGetStandardTextFieldTimeString(optionalTime);
        String textFieldString = timeTextField.getText();
        // We will only change the time, when the text of the last valid time does not match the
        // supplied time. This will prevent any registered time change listeners from receiving
        // any events unless the time actually changes.
        if ((!standardTimeString.equals(textFieldString))) {
            zInternalSetTimeTextField(standardTimeString);
        }
    }

    /**
     * toString, This will return the last valid time as a string. If the last valid time is empty,
     * this will return an empty string ("").
     *
     * Time values will be output in one of the following ISO-8601 formats: "HH:mm", "HH:mm:ss",
     * "HH:mm:ss.SSS", "HH:mm:ss.SSSSSS", "HH:mm:ss.SSSSSSSSS".
     *
     * The format used will be the shortest that outputs the full value of the time where the
     * omitted parts are implied to be zero.
     *
     * This returns the same value as getTimeStringOrEmptyString()
     */
    @Override
    public String toString() {
        return getTimeStringOrEmptyString();
    }

    /**
     * togglePopup, This creates and shows a menu popup. If the popup is already open, then this
     * will close the popup.
     *
     * This is called when the user clicks on the toggle menu button of the time picker. This
     * function does not generally need to be called programmatically.
     */
    public void togglePopup() {
        // If a popup calendar was closed in the last 200 milliseconds, then do not open a new one.
        // This is a workaround for a problem where the toggle calendar button would erroneously
        // reopen a calendar after closing one.
        if ((Instant.now().toEpochMilli() - lastPopupCloseTime.toEpochMilli()) < 200) {
            return;
        }
        openPopup();
    }

    /**
     * zAddTextChangeListener, This add a text change listener to the time text field, so that we
     * can respond to text as it is typed.
     */
    private void zAddTextChangeListener() {
        timeTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                zTextFieldChangedSoIndicateIfValidAndStoreWhenValid();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                zTextFieldChangedSoIndicateIfValidAndStoreWhenValid();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                zTextFieldChangedSoIndicateIfValidAndStoreWhenValid();
            }
        });
    }

    /**
     * zEventCustomPopupWasClosed, This is called automatically whenever the CustomPopup that is
     * associated with this time picker is closed. This should be called regardless of the type of
     * event which caused the popup to close.
     *
     * Notes: The popup can be automatically closed for various reasons. 1) The user may press
     * escape. 2) The popup may lose focus. 3) The window may be moved. 4) The user may toggle the
     * popup menu with the "toggle time menu" button. 5) The user may select a time in the popup
     * menu.
     */
    @Override
    public void zEventCustomPopupWasClosed(CustomPopup popup) {
        popup = null;
        if (timeMenuPanel != null) {
            timeMenuPanel.clearParent();
        }
        timeMenuPanel = null;
        lastPopupCloseTime = Instant.now();
    }

    /**
     * zEventTimeTextFieldFocusLostSoValidateText, This function is called whenever the time picker
     * text field loses focus. When needed, this function initiates a validation of the time picker
     * text.
     *
     * This has two possible effects: 1) If the current text is already valid and is in the standard
     * format, then this will do nothing. If the text is not valid, or if the text is not in the
     * standard format, then: 2) It will replace the invalid text in the text field with a standard
     * time field text string that matches the last valid time.
     */
    private void zEventTimeTextFieldFocusLostSoValidateText(FocusEvent e) {
        // Find out if the text field needs to be set to the last valid time or not.
        // The text field needs to be set whenever its text does not match the standard format
        // for the last valid time.
        String standardTimeString = zGetStandardTextFieldTimeString(lastValidTime);
        String textFieldString = timeTextField.getText();
        if (!standardTimeString.equals(textFieldString)) {
            // Overwrite the text field with the last valid time.
            // This will clear the text field if the last valid time is null.
            setTime(lastValidTime);
        }
    }

    /**
     * zEventToggleTimeMenuButtonMousePressed, This is called when the user clicks on the "toggle
     * time menu" button of the time picker.
     *
     * This will create a time menu panel and a popup, and display them to the user. If a time menu
     * panel is already opened, it will be closed instead.
     */
    private void zEventToggleTimeMenuButtonMousePressed(MouseEvent e) {
        togglePopup();
    }

    /**
     * zGetStandardTextFieldTimeString, This returns a string for the supplied time (or null), in
     * the standard format which could be used for displaying that time in the text field.
     */
    private String zGetStandardTextFieldTimeString(LocalTime time) {
        String standardTimeString = "";
        if (time == null) {
            return standardTimeString;
        }
        standardTimeString = time.format(settings.formatForDisplayTime);
        return standardTimeString;
    }

    /**
     * zInternalSetLastValidTimeAndNotifyListeners, This should be called whenever we need to change
     * the last valid time variable. This will store the supplied last valid time. If needed, this
     * will notify all time change listeners that the time has been changed. This does not perform
     * any other tasks besides those described here.
     */
    private void zInternalSetLastValidTimeAndNotifyListeners(LocalTime newTime) {
        LocalTime oldTime = lastValidTime;
        lastValidTime = newTime;
        if (!PickerUtilities.isSameLocalTime(oldTime, newTime)) {
            for (TimeChangeListener timeChangeListener : timeChangeListeners) {
                TimeChangeEvent timeChangeEvent = new TimeChangeEvent(this, oldTime, newTime);
                timeChangeListener.timeChanged(timeChangeEvent);
            }
        }
    }

    /**
     * zInternalSetTimeTextField, This is called whenever we need to programmatically change the
     * time text field. The purpose of this function is to make sure that text field change events
     * only occur once per programmatic text change, instead of occurring twice. The default
     * behavior is that the text change event will fire twice. (By default, it changes once to clear
     * the text, and changes once to change it to new text.)
     */
    private void zInternalSetTimeTextField(String text) {
        skipTextFieldChangedFunctionWhileTrue = true;
        if (settings.useLowercaseForDisplayTime) {
            text = text.toLowerCase(settings.timePickerLocale);
        }
        timeTextField.setText(text);
        skipTextFieldChangedFunctionWhileTrue = false;
        zTextFieldChangedSoIndicateIfValidAndStoreWhenValid();
    }

    /**
     * zTextFieldChangedSoIndicateIfValidAndStoreWhenValid, This is called whenever the text in the
     * time picker text field has changed, whether programmatically or by the user.
     *
     * This will change the font and color of the text in the text field to indicate to the user if
     * the currently text is a valid time, invalid text, or a vetoed time.
     *
     * If the current text contains a valid time, it will be stored in the variable lastValidTime.
     * Otherwise, the lastValidTime will not be changed.
     */
    private void zTextFieldChangedSoIndicateIfValidAndStoreWhenValid() {
        // Skip this function if it should not be run.
        if (skipTextFieldChangedFunctionWhileTrue) {
            return;
        }
        // Gather some variables that we will need.
        String timeText = timeTextField.getText();
        boolean textIsEmpty = timeText.trim().isEmpty();
        TimeVetoPolicy vetoPolicy = settings.vetoPolicy;
        boolean nullIsAllowed = settings.allowEmptyTimes;
        // If needed, try to get a parsed time.
        LocalTime parsedTime = null;
        if (!textIsEmpty) {
            parsedTime = InternalUtilities.getParsedTimeOrNull(timeText,
                    settings.formatForDisplayTime, settings.formatForMenuTimes,
                    settings.formatsForParsing, settings.timePickerLocale);
        }
        // Reset all atributes to normal before starting.
        timeTextField.setBackground(Color.white);
        timeTextField.setForeground(settings.colorTextValidTime);
        timeTextField.setFont(settings.fontValidTime);
        // Handle the various possibilities.
        // If the text is empty and null is allowed, leave the normal font, and
        // set lastValidTime to null.
        if (textIsEmpty && nullIsAllowed) {
            zInternalSetLastValidTimeAndNotifyListeners(null);
            // If the text is empty and null is not allowed, set a pink background, and
            // do not change the lastValidTime.
        } else if ((textIsEmpty) && (!nullIsAllowed)) {
            timeTextField.setBackground(Color.pink);
            // If the text is not valid, set a font indicator, and do not change the lastValidTime.
        } else if (parsedTime == null) {
            timeTextField.setForeground(settings.colorTextInvalidTime);
            timeTextField.setFont(settings.fontInvalidTime);
            // If the ttime is vetoed, set a font indicator, and do not change the lastValidTime.
        } else if (InternalUtilities.isTimeVetoed(vetoPolicy, parsedTime)) {
            timeTextField.setForeground(settings.colorTextVetoedTime);
            timeTextField.setFont(settings.fontVetoedTime);
        } else {
            // The time is valid, so leave the normal font, and store the last valid time.
            zInternalSetLastValidTimeAndNotifyListeners(parsedTime);
        }
    }

    /**
     * initComponents, This initializes the components of the JFormDesigner panel. This function is
     * automatically generated by JFormDesigner from the JFD form design file, and should not be
     * modified by hand. This function can be modified, if needed, by using JFormDesigner.
     */
    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        timeTextField = new JTextField();
        toggleTimeMenuButton = new JButton();

        //======== this ========
        setLayout(new FormLayout(
                "[62px,pref]:grow, pref, [26px,pref]",
                "fill:pref:grow"));

        //---- timeTextField ----
        timeTextField.setMargin(new Insets(1, 3, 2, 2));
        timeTextField.setBorder(new CompoundBorder(
                new MatteBorder(1, 1, 1, 1, new Color(122, 138, 153)),
                new EmptyBorder(1, 3, 2, 2)));
        timeTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                zEventTimeTextFieldFocusLostSoValidateText(e);
            }
        });
        add(timeTextField, CC.xy(1, 1));

        //---- toggleTimeMenuButton ----
        toggleTimeMenuButton.setText("v");
        toggleTimeMenuButton.setFocusPainted(false);
        toggleTimeMenuButton.setFocusable(false);
        toggleTimeMenuButton.setFont(new Font("Segoe UI", Font.PLAIN, 8));
        toggleTimeMenuButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                zEventToggleTimeMenuButtonMousePressed(e);
            }
        });
        add(toggleTimeMenuButton, CC.xy(3, 1));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

}
