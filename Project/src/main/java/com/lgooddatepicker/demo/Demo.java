package com.lgooddatepicker.demo;

import com.lgooddatepicker.datepicker.DatePicker;
import javax.swing.JButton;
import javax.swing.JFrame;
import java.awt.event.ActionEvent;
import java.time.LocalTime;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.Locale;
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import com.lgooddatepicker.datepicker.DatePickerSettings;
import com.lgooddatepicker.optionalusertools.DateChangeListener;
import com.lgooddatepicker.optionalusertools.PickerUtilities;
import com.lgooddatepicker.zinternaltools.DateChangeEvent;
import com.lgooddatepicker.zinternaltools.InternalUtilities;
import com.lgooddatepicker.zinternaltools.WrapLayout;
import com.lgooddatepicker.optionalusertools.DateVetoPolicy;
import com.lgooddatepicker.optionalusertools.DateHighlightPolicy;
import com.lgooddatepicker.optionalusertools.TimeChangeListener;
import com.lgooddatepicker.optionalusertools.TimeVetoPolicy;
import com.lgooddatepicker.datetimepicker.DateTimePicker;
import com.lgooddatepicker.optionalusertools.DateTimeChangeListener;
import com.lgooddatepicker.timepicker.TimePicker;
import com.lgooddatepicker.timepicker.TimePickerSettings;
import com.lgooddatepicker.timepicker.TimePickerSettings.TimeIncrement;
import com.lgooddatepicker.zinternaltools.DateTimeChangeEvent;
import com.lgooddatepicker.zinternaltools.TimeChangeEvent;

/**
 * Demo, This class contains a demonstration of various features of the DatePicker class.
 *
 * Optional features: Most of the DatePicker features shown in this demo are optional. The simplest
 * usage only requires creating a date picker instance and adding it to a panel or window. The
 * selected date can then be retrieved with the function datePicker.getDate().
 *
 * DatePicker Basic Usage Example:
 * <pre>
 * // Create a new date picker.
 * DatePicker datePicker = new DatePicker();
 *
 * // Add the date picker to a panel. (Or to another window container).
 * JPanel panel = new JPanel();
 * panel.add(datePicker);
 *
 * // Get the selected date.
 * LocalDate date = datePicker.getDate();
 * </pre>
 *
 * Running the demo: This is an executable demonstration. To run the demo, click "run file" (or the
 * equivalent command) for the Demo class in your IDE.
 */
public class Demo {

    // This holds our main frame.
    static JFrame frame;
    // This holds our display panel.
    static DemoPanel panel;
    // These hold date pickers.
    static DatePicker datePicker;
    static DatePicker datePicker1;
    static DatePicker datePicker2;
    // These hold time pickers.
    static TimePicker timePicker;
    static TimePicker timePicker1;
    static TimePicker timePicker2;
    // These hold DateTimePickers.
    static DateTimePicker dateTimePicker1;
    static DateTimePicker dateTimePicker2;
    static DateTimePicker dateTimePicker3;
    static DateTimePicker dateTimePicker4;
    static DateTimePicker dateTimePicker5;
    // Date pickers are placed on the rows at a set interval.
    static final int rowMultiplier = 4;

    /**
     * main, The application entry point.
     */
    public static void main(String[] args) {
        ///////////////////////////////////////////////////////////////////////////////////////////
        // If desired, set a swing look and feel here. 
        try {
            /*
            // Set a specific look and feel.
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
            
            // Set a random look and feel. 
            LookAndFeelInfo[] installedLooks = UIManager.getInstalledLookAndFeels();
            int lookIndex = (int) (Math.random() * installedLooks.length);
            UIManager.setLookAndFeel(installedLooks[lookIndex].getClassName());
            System.out.println(installedLooks[lookIndex].getClassName().toString());
             */
        } catch (Exception e) {
        }

        ///////////////////////////////////////////////////////////////////////////////////////////
        // Create a frame, a panel, and our demo buttons.
        frame = new JFrame();
        frame.setTitle("LGoodDatePicker Demo " + InternalUtilities.getProjectVersionString());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panel = new DemoPanel();
        frame.getContentPane().add(panel);
        createDemoButtons();

        ///////////////////////////////////////////////////////////////////////////////////////////
        // This section creates DatePickers, with various features.
        //
        // Create a settings variable for repeated use.
        DatePickerSettings dateSettings;
        int row = rowMultiplier;

        // Create a date picker: With default settings
        datePicker1 = new DatePicker();
        panel.panel1.add(datePicker1, getConstraints(1, (row * rowMultiplier), 1));
        panel.addLabel(panel.panel1, 1, (row++ * rowMultiplier), "Date 1, Default Settings:");

        // Create a date picker: With highlight policy.
        dateSettings = new DatePickerSettings();
        datePicker2 = new DatePicker(dateSettings);
        dateSettings.highlightPolicy = new SampleHighlightPolicy();
        panel.panel1.add(datePicker2, getConstraints(1, (row * rowMultiplier), 1));
        panel.addLabel(panel.panel1, 1, (row++ * rowMultiplier), "Date 2, Highlight Policy:");

        // Create a date picker: With veto policy.
        // Note: Veto policies can only be set after constructing the date picker.
        dateSettings = new DatePickerSettings();
        datePicker = new DatePicker(dateSettings);
        dateSettings.setVetoPolicy(new SampleDateVetoPolicy());
        panel.panel1.add(datePicker, getConstraints(1, (row * rowMultiplier), 1));
        panel.addLabel(panel.panel1, 1, (row++ * rowMultiplier), "Date 3, Veto Policy:");

        // Create a date picker: With both policies.
        // Note: Veto policies can only be set after constructing the date picker.
        dateSettings = new DatePickerSettings();
        datePicker = new DatePicker(dateSettings);
        dateSettings.highlightPolicy = new SampleHighlightPolicy();
        dateSettings.setVetoPolicy(new SampleDateVetoPolicy());
        panel.panel1.add(datePicker, getConstraints(1, (row * rowMultiplier), 1));
        panel.addLabel(panel.panel1, 1, (row++ * rowMultiplier), "Date 4, Both Policies:");

        // Create a date picker: Change first weekday.
        dateSettings = new DatePickerSettings();
        dateSettings.firstDayOfWeek = DayOfWeek.MONDAY;
        datePicker = new DatePicker(dateSettings);
        panel.panel1.add(datePicker, getConstraints(1, (row * rowMultiplier), 1));
        panel.addLabel(panel.panel1, 1, (row++ * rowMultiplier), "Date 5, Set First Day Of Week (Mon):");

        // Create a date picker: Change calendar size.
        dateSettings = new DatePickerSettings();
        dateSettings.sizeDatePanelMinimumHeight *= 1.6;
        dateSettings.sizeDatePanelMinimumWidth *= 1.6;
        datePicker = new DatePicker(dateSettings);
        panel.panel1.add(datePicker, getConstraints(1, (row * rowMultiplier), 1));
        panel.addLabel(panel.panel1, 1, (row++ * rowMultiplier), "Date 6, Change Calendar Size:");

        // Create a date picker: Custom color.
        dateSettings = new DatePickerSettings();
        dateSettings.colorBackgroundCalendarPanel = Color.green;
        dateSettings.colorBackgroundWeekdayLabels = Color.orange;
        dateSettings.colorBackgroundMonthAndYear = Color.yellow;
        dateSettings.colorBackgroundTodayAndClear = Color.yellow;
        dateSettings.colorBackgroundNavigateYearMonthButtons = Color.cyan;
        datePicker = new DatePicker(dateSettings);
        panel.panel1.add(datePicker, getConstraints(1, (row * rowMultiplier), 1));
        panel.addLabel(panel.panel1, 1, (row++ * rowMultiplier), "Date 7, Change Colors:");

        // Create a date picker: Custom date format.
        // When creating a date pattern string for BCE dates, use "u" instead of "y" for the year.
        // For more details about that, see: DatePickerSettings.formatDatesBeforeCommonEra.
        dateSettings = new DatePickerSettings();
        dateSettings.setFormatForDatesCommonEra(PickerUtilities.createFormatterFromPatternString("d MMM yyyy", dateSettings.getLocale()));
        dateSettings.setFormatForDatesBeforeCommonEra(PickerUtilities.createFormatterFromPatternString("d MMM uuuu", dateSettings.getLocale()));
        dateSettings.setInitialDateToToday();
        datePicker = new DatePicker(dateSettings);
        panel.panel1.add(datePicker, getConstraints(1, (row * rowMultiplier), 1));
        panel.addLabel(panel.panel1, 1, (row++ * rowMultiplier), "Date 8, Custom Date Format:");

        // Create a date picker: Custom font.
        dateSettings = new DatePickerSettings();
        dateSettings.fontValidDate = new Font("Monospaced", Font.ITALIC | Font.BOLD, 17);
        dateSettings.colorTextValidDate = new Color(0, 100, 0);
        dateSettings.setInitialDateToToday();
        datePicker = new DatePicker(dateSettings);
        panel.panel1.add(datePicker, getConstraints(1, (row * rowMultiplier), 1));
        panel.addLabel(panel.panel1, 1, (row++ * rowMultiplier), "Date 9, Custom Font:");

        // Create a date picker: No empty dates. (aka null)
        dateSettings = new DatePickerSettings();
        dateSettings.setAllowEmptyDates(false);
        datePicker = new DatePicker(dateSettings);
        datePicker.addDateChangeListener(new SampleDateChangeListener("datePicker10 (Disallow Empty Dates or Null), "));
        panel.panel1.add(datePicker, getConstraints(1, (row * rowMultiplier), 1));
        panel.addLabel(panel.panel1, 1, (row++ * rowMultiplier), "Date 10, Disallow Empty Dates:");

        // Create a date picker: Disallow keyboard editing.
        dateSettings = new DatePickerSettings();
        dateSettings.setAllowKeyboardEditing(false);
        dateSettings.setInitialDateToToday();
        datePicker = new DatePicker(dateSettings);
        panel.panel1.add(datePicker, getConstraints(1, (row * rowMultiplier), 1));
        panel.addLabel(panel.panel1, 1, (row++ * rowMultiplier), "Date 11, Disallow Keyboard Editing:");

        ///////////////////////////////////////////////////////////////////////////////////////////
        // This section creates TimePickers. (1 to 5)
        //
        // Create some variables for repeated use.
        TimePicker timePicker;
        TimePickerSettings timeSettings;
        row = rowMultiplier;

        // Create a time picker: With default settings
        timePicker1 = new TimePicker();
        panel.panel2.add(timePicker1, getConstraints(1, (row * rowMultiplier), 1));
        panel.addLabel(panel.panel2, 1, (row++ * rowMultiplier), "Time 1, Default Settings:");
        
        // Create a time picker: With No Buttons.
        timeSettings = new TimePickerSettings();
        timeSettings.setDisplayToggleTimeMenuButton(false);
        timeSettings.setInitialTimeToNow();
        timePicker = new TimePicker(timeSettings);
        panel.panel2.add(timePicker, getConstraints(1, (row * rowMultiplier), 1));
        panel.addLabel(panel.panel2, 1, (row++ * rowMultiplier), "Time 2, No Buttons:");
        
        // Create a time picker: With Spinner Buttons.
        timeSettings = new TimePickerSettings();
        timeSettings.setDisplayToggleTimeMenuButton(false);
        timeSettings.setDisplaySpinnerButtons(true);
        timeSettings.setInitialTimeToNow();
        timePicker = new TimePicker(timeSettings);
        panel.panel2.add(timePicker, getConstraints(1, (row * rowMultiplier), 1));
        panel.addLabel(panel.panel2, 1, (row++ * rowMultiplier), "Time 3, With Spinner Buttons:");
        
        // Create a time picker: With All Buttons.
        timeSettings = new TimePickerSettings();
        timeSettings.setDisplaySpinnerButtons(true);
        timeSettings.setInitialTimeToNow();
        timePicker = new TimePicker(timeSettings);
        panel.panel2.add(timePicker, getConstraints(1, (row * rowMultiplier), 1));
        panel.addLabel(panel.panel2, 1, (row++ * rowMultiplier), "Time 4, With All Buttons:");

        // Create a time picker: 15 minute interval, and 24 hour clock.
        timeSettings = new TimePickerSettings();
        timeSettings.use24HourClockFormat();
        timeSettings.initialTime = LocalTime.of(15, 30);
        timeSettings.generatePotentialMenuTimes(TimeIncrement.FifteenMinutes, null, null);
        timePicker = new TimePicker(timeSettings);
        panel.panel2.add(timePicker, getConstraints(1, (row * rowMultiplier), 1));
        panel.addLabel(panel.panel2, 1, (row++ * rowMultiplier), "Time 5, Interval 15 minutes, and 24 hour clock:");

        // Create a time picker: With Veto Policy.
        timeSettings = new TimePickerSettings();
        timePicker = new TimePicker(timeSettings);
        timeSettings.setVetoPolicy(new SampleTimeVetoPolicy());
        panel.panel2.add(timePicker, getConstraints(1, (row * rowMultiplier), 1));
        panel.addLabel(panel.panel2, 1, (row++ * rowMultiplier), "Time 6, With Veto Policy (Only 9a-5p allowed):");


        // Create a time picker: Localized (Chinese).
        timeSettings = new TimePickerSettings(Locale.forLanguageTag("zh"));
        timeSettings.initialTime = LocalTime.now();
        timePicker = new TimePicker(timeSettings);
        panel.panel2.add(timePicker, getConstraints(1, (row * rowMultiplier), 1));
        panel.addLabel(panel.panel2, 1, (row++ * rowMultiplier), "Time 7, Localized (to Chinese):");

        ///////////////////////////////////////////////////////////////////////////////////////////
        // This section creates DateTimePickers. (1 to 5)
        //
        // Create a DateTimePicker: Default settings
        dateTimePicker1 = new DateTimePicker();
        panel.panel2.add(dateTimePicker1, getConstraints(1, (row * rowMultiplier), 1));
        panel.addLabel(panel.panel2, 1, (row++ * rowMultiplier), "DateTimePicker 1, Default settings:");

        // Create a DateTimePicker: Disallow empty dates and times.
        dateSettings = new DatePickerSettings();
        timeSettings = new TimePickerSettings();
        dateSettings.setAllowEmptyDates(false);
        timeSettings.setAllowEmptyTimes(false);
        dateTimePicker2 = new DateTimePicker(dateSettings, timeSettings);
        panel.panel2.add(dateTimePicker2, getConstraints(1, (row * rowMultiplier), 1));
        panel.addLabel(panel.panel2, 1, (row++ * rowMultiplier), "DateTimePicker 2, Disallow empty dates and times:");

        // Create a DateTimePicker: With change listener.
        dateTimePicker3 = new DateTimePicker();
        dateTimePicker3.addDateTimeChangeListener(new SampleDateTimeChangeListener("dateTimePicker3"));
        panel.panel2.add(dateTimePicker3, getConstraints(1, (row * rowMultiplier), 1));
        panel.addLabel(panel.panel2, 1, (row++ * rowMultiplier), "DateTimePicker 3, With Change Listener:");

        ///////////////////////////////////////////////////////////////////////////////////////////
        // This section creates any remaining TimePickers.
        
        

        // Create a time picker: Disallow Empty Times.
        timeSettings = new TimePickerSettings();
        timeSettings.setAllowEmptyTimes(false);
        timePicker = new TimePicker(timeSettings);
        panel.panel2.add(timePicker, getConstraints(1, (row * rowMultiplier), 1));
        panel.addLabel(panel.panel2, 1, (row++ * rowMultiplier), "Time 8, Disallow Empty Times:");
        
        // Create a time picker: With TimeChangeListener.
        timeSettings = new TimePickerSettings();
        timePicker = new TimePicker(timeSettings);
        timePicker.addTimeChangeListener(new SampleTimeChangeListener("timePicker7"));
        panel.panel2.add(timePicker, getConstraints(1, (row * rowMultiplier), 1));
        panel.addLabel(panel.panel2, 1, (row++ * rowMultiplier), "Time 9, With a TimeChangeListener:");

        // Create a time picker: With more visible rows.
        timeSettings = new TimePickerSettings();
        timeSettings.maximumVisibleMenuRows = 20;
        timePicker = new TimePicker(timeSettings);
        panel.panel2.add(timePicker, getConstraints(1, (row * rowMultiplier), 1));
        panel.addLabel(panel.panel2, 1, (row++ * rowMultiplier), "Time 10, With 20 visible menu rows:");

        // Create a time picker: Custom Format.
        timeSettings = new TimePickerSettings();
        timeSettings.setFormatForDisplayTime(PickerUtilities.createFormatterFromPatternString("ha", timeSettings.getLocale()));
        timeSettings.setFormatForMenuTimes(timeSettings.getFormatForDisplayTime());
        timeSettings.initialTime = LocalTime.of(15, 00);
        timeSettings.generatePotentialMenuTimes(TimeIncrement.OneHour, null, null);
        timePicker = new TimePicker(timeSettings);
        panel.panel2.add(timePicker, getConstraints(1, (row * rowMultiplier), 1));
        panel.addLabel(panel.panel2, 1, (row++ * rowMultiplier), "Time 11, Custom Format:");

        // Create a time picker: Disallow Keyboard Editing.
        timeSettings = new TimePickerSettings();
        timeSettings.setAllowKeyboardEditing(false);
        timePicker = new TimePicker(timeSettings);
        panel.panel2.add(timePicker, getConstraints(1, (row * rowMultiplier), 1));
        panel.addLabel(panel.panel2, 1, (row++ * rowMultiplier), "Time 12, Disallow Keyboard Editing:");

        ///////////////////////////////////////////////////////////////////////////////////////////
        // This section creates any remaining DateTimePickers.
        // (None here at the moment.)
        ///////////////////////////////////////////////////////////////////////////////////////////
        // This section creates date pickers and labels for demonstrating the language translations.
        int rowMarker = 0;
        addLocalizedPickerAndLabel(++rowMarker, "Arabic:", "ar");
        addLocalizedPickerAndLabel(++rowMarker, "Chinese:", "zh");
        addLocalizedPickerAndLabel(++rowMarker, "Czech:", "cs");
        addLocalizedPickerAndLabel(++rowMarker, "Danish:", "da");
        addLocalizedPickerAndLabel(++rowMarker, "Dutch:", "nl");
        addLocalizedPickerAndLabel(++rowMarker, "English:", "en");
        addLocalizedPickerAndLabel(++rowMarker, "French:", "fr");
        addLocalizedPickerAndLabel(++rowMarker, "German:", "de");
        addLocalizedPickerAndLabel(++rowMarker, "Greek:", "el");
        // Note: Hindi requires a full locale specifier. 
        addLocalizedPickerAndLabel(++rowMarker, "Hindi:", "hi");
        addLocalizedPickerAndLabel(++rowMarker, "Italian:", "it");
        addLocalizedPickerAndLabel(++rowMarker, "Indonesian:", "in");
        addLocalizedPickerAndLabel(++rowMarker, "Japanese:", "ja");
        addLocalizedPickerAndLabel(++rowMarker, "Korean:", "ko");
        addLocalizedPickerAndLabel(++rowMarker, "Polish:", "pl");
        addLocalizedPickerAndLabel(++rowMarker, "Portuguese:", "pt");
        addLocalizedPickerAndLabel(++rowMarker, "Romanian:", "ro");
        addLocalizedPickerAndLabel(++rowMarker, "Russian:", "ru");
        addLocalizedPickerAndLabel(++rowMarker, "Spanish:", "es");
        addLocalizedPickerAndLabel(++rowMarker, "Swedish:", "sv");
        addLocalizedPickerAndLabel(++rowMarker, "Turkish:", "tr");
        addLocalizedPickerAndLabel(++rowMarker, "Vietnamese:", "vi");

        // Display the frame.
        frame.pack();
        frame.validate();
        int maxWidth = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().width;
        int maxHeight = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height;
        frame.setSize(maxWidth / 4 * 3, maxHeight / 8 * 7);
        frame.setLocation(maxWidth / 8, maxHeight / 16);
        frame.setVisible(true);
    }

    /**
     * getConstraints, This returns a grid bag constraints object that can be used for placing a
     * component appropriately into a grid bag layout.
     */
    private static GridBagConstraints getConstraints(int gridx, int gridy, int gridwidth) {
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.WEST;
        gc.gridx = gridx;
        gc.gridy = gridy;
        gc.gridwidth = gridwidth;
        return gc;
    }

    /**
     * addLocalizedPickerAndLabel, This creates a date picker whose locale is set to the specified
     * language. This also sets the picker to today's date, creates a label for the date picker, and
     * adds the components to the language panel.
     */
    private static void addLocalizedPickerAndLabel(int rowMarker, String labelText,
            String languageCode) {
        // Create the localized date picker and label.
        Locale locale = new Locale(languageCode);
        DatePickerSettings settings = new DatePickerSettings(locale);
        settings.setInitialDateToToday();
        DatePicker datePicker = new DatePicker(settings);
        panel.panel4.add(datePicker, getConstraints(1, (rowMarker * rowMultiplier), 1));
        panel.addLabel(panel.panel4, 1, (rowMarker * rowMultiplier), labelText);
    }

    /**
     * setTimeOneWithTimeTwoButtonClicked, This sets the time in time picker one, to whatever time
     * is currently set in time picker two.
     */
    private static void setTimeOneWithTimeTwoButtonClicked(ActionEvent e) {
        LocalTime timePicker2Time = timePicker2.getTime();
        timePicker1.setTime(timePicker2Time);
        // Display message.
        String message = "The timePicker1 value was set using the timePicker2 value!\n\n";
        String timeString = timePicker1.getTimeStringOrSuppliedString("(null)");
        String messageAddition = ("The timePicker1 value is currently set to: " + timeString + ".");
        panel.messageTextArea.setText(message + messageAddition);
    }

    /**
     * setTwoWithY2KButtonClicked, This sets the date in date picker two, to New Years Day 2000.
     */
    private static void setTwoWithY2KButtonClicked(ActionEvent e) {
        // Set date picker date.
        LocalDate dateY2K = LocalDate.of(2000, Month.JANUARY, 1);
        datePicker2.setDate(dateY2K);
        // Display message.
        String dateString = datePicker2.getDateStringOrSuppliedString("(null)");
        String message = "The datePicker2 date was set to New Years 2000!\n\n";
        message += ("The datePicker2 date is currently set to: " + dateString + ".");
        panel.messageTextArea.setText(message);
    }

    /**
     * setOneWithTwoButtonClicked, This sets the date in date picker one, to whatever date is
     * currently set in date picker two.
     */
    private static void setOneWithTwoButtonClicked(ActionEvent e) {
        // Set date from date picker 2.
        LocalDate datePicker2Date = datePicker2.getDate();
        datePicker1.setDate(datePicker2Date);
        // Display message.
        String message = "The datePicker1 date was set using the datePicker2 date!\n\n";
        message += getDatePickerOneDateText();
        panel.messageTextArea.setText(message);
    }

    /**
     * setOneWithFeb31ButtonClicked, This sets the text in date picker one, to a nonexistent date
     * (February 31st). The last valid date in a date picker is always saved. This is a programmatic
     * demonstration of what happens when the user enters invalid text.
     */
    private static void setOneWithFeb31ButtonClicked(ActionEvent e) {
        // Set date picker text.
        datePicker1.setText("February 31, 1950");
        // Display message.
        String message = "The datePicker1 text was set to: \"" + datePicker1.getText() + "\".\n";
        message += "Note: The stored date (the last valid date), did not change because"
                + " February never has 31 days.\n\n";
        message += getDatePickerOneDateText();
        panel.messageTextArea.setText(message);
    }

    /**
     * getOneAndShowButtonClicked, This retrieves and displays whatever date is currently set in
     * date picker one.
     */
    private static void getOneAndShowButtonClicked(ActionEvent e) {
        // Get and display date picker text.
        panel.messageTextArea.setText(getDatePickerOneDateText());
    }

    /**
     * clearOneAndTwoButtonClicked, This clears date picker one.
     */
    private static void clearOneAndTwoButtonClicked(ActionEvent e) {
        // Clear the date pickers.
        datePicker1.clear();
        datePicker2.clear();
        // Display message.
        String message = "The datePicker1 and datePicker2 dates were cleared!\n\n";
        message += getDatePickerOneDateText() + "\n";
        String date2String = datePicker2.getDateStringOrSuppliedString("(null)");
        message += ("The datePicker2 date is currently set to: " + date2String + ".");
        panel.messageTextArea.setText(message);
    }

    /**
     * getDatePickerOneDateText, This returns a string indicating the current date stored in date
     * picker one.
     */
    private static String getDatePickerOneDateText() {
        // Create date string for date picker 1.
        String dateString = datePicker1.getDateStringOrSuppliedString("(null)");
        return ("The datePicker1 date is currently set to: " + dateString + ".");
    }

    /**
     * createDemoButtons, This creates the buttons for the demo, adds an action listener to each
     * button, and adds each button to the display panel.
     */
    private static void createDemoButtons() {
        JPanel buttonPanel = new JPanel(new WrapLayout());
        panel.scrollPaneForButtons.setViewportView(buttonPanel);
        // Create each demo button, and add it to the panel.
        // Add an action listener to link it to its appropriate function.
        JButton showIntro = new JButton("Show Introduction Message");
        showIntro.addActionListener(e -> showIntroductionClicked(e));
        buttonPanel.add(showIntro);
        JButton setTwoWithY2K = new JButton("Set DatePicker Two with New Years Day 2000");
        setTwoWithY2K.addActionListener(e -> setTwoWithY2KButtonClicked(e));
        buttonPanel.add(setTwoWithY2K);
        JButton setDateOneWithTwo = new JButton("Set DatePicker One with the date in Two");
        setDateOneWithTwo.addActionListener(e -> setOneWithTwoButtonClicked(e));
        buttonPanel.add(setDateOneWithTwo);
        JButton setOneWithFeb31 = new JButton("Set Text in DatePicker One to February 31, 1950");
        setOneWithFeb31.addActionListener(e -> setOneWithFeb31ButtonClicked(e));
        buttonPanel.add(setOneWithFeb31);
        JButton getOneAndShow = new JButton("Get and show the date in DatePicker One");
        getOneAndShow.addActionListener(e -> getOneAndShowButtonClicked(e));
        buttonPanel.add(getOneAndShow);
        JButton clearOneAndTwo = new JButton("Clear DatePickers One and Two");
        clearOneAndTwo.addActionListener(e -> clearOneAndTwoButtonClicked(e));
        buttonPanel.add(clearOneAndTwo);
        JButton toggleButton = new JButton("Toggle DatePicker One");
        toggleButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                toggleDateOneButtonClicked();
            }
        });
        buttonPanel.add(toggleButton);
        JButton setTimeOneWithTwo = new JButton("TimePickers: Set TimePicker One with the time in Two");
        setTimeOneWithTwo.addActionListener(e -> setTimeOneWithTimeTwoButtonClicked(e));
        buttonPanel.add(setTimeOneWithTwo);
        JButton timeToggleButton = new JButton("Toggle TimePicker One");
        timeToggleButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                toggleTimeOneButtonClicked();
            }
        });
        buttonPanel.add(timeToggleButton);
    }

    /**
     * showIntroductionClicked, This displays an introduction message about the date picker.
     */
    private static void showIntroductionClicked(ActionEvent e) {
        panel.messageTextArea.setText("Interface: \nMost items in a date picker are clickable. "
                + "These include... The buttons for previous and next month, the buttons for "
                + "previous and next year, the \"today\" text, the \"clear\" text, and individual "
                + "dates. A click on the month or year label (at the top), will open a menu for "
                + "changing the month or year.\n\nGeneral features: \n* Automatic "
                + "internationalization. \n* Relatively compact source code.\n* Creating a "
                + "DatePicker requires only one line of code.\n* Open source code base.\n\n"
                + "Data types: \nThe standard Java 8 time library is used to store dates, "
                + "and they are convertible to other data types. \n(The Java 8 time package "
                + "is also called \"java.time\" or \"JSR-310\", and was developed by the author "
                + "of Joda Time.)\n\nVeto and Highlight Policies: \nThese policies are optional. "
                + "A veto policy restricts the dates that can be selected. A highlight policy "
                + "provides a visual highlight on desired dates, with optional tooltips. If today "
                + "is vetoed, the \"today\" button will be grey and disabled.\n\nDate values and "
                + "automatic validation: \nEvery date picker stores its current text, and its last "
                + "valid date. The last valid date is returned when you call DatePicker.getDate(). "
                + "If the user types into the text field, any text that is not a valid date will "
                + "be displayed in red, any vetoed date will have a strikethrough, and valid "
                + "dates will display in black. When the focus on a date picker is lost, the text "
                + "is always set to match the last valid date.\n\n\n");
        panel.messageTextArea.setCaretPosition(0);
    }

    /**
     * toggleDateOneButtonClicked, This toggles (opens or closes) date picker one.
     */
    private static void toggleDateOneButtonClicked() {
        datePicker1.togglePopup();
        String message = "The datePicker1 calendar popup is ";
        message += (datePicker1.isPopupOpen()) ? "open!" : "closed!";
        panel.messageTextArea.setText(message);
    }

    /**
     * toggleTimeOneButtonClicked, This toggles (opens or closes) time picker one.
     */
    private static void toggleTimeOneButtonClicked() {
        timePicker1.togglePopup();
        String message = "The timePicker1 menu popup is ";
        message += (timePicker1.isPopupOpen()) ? "open!" : "closed!";
        panel.messageTextArea.setText(message);
    }

    /**
     * SampleDateChangeListener, A date change listener provides a way for a class to receive
     * notifications whenever the date has changed in a DatePicker.
     */
    private static class SampleDateChangeListener implements DateChangeListener {

        /**
         * datePickerName, This holds a chosen name for the date picker that we are listening to,
         * for generating date change messages in the demo.
         */
        public String datePickerName;

        /**
         * Constructor.
         */
        private SampleDateChangeListener(String datePickerName) {
            this.datePickerName = datePickerName;
        }

        /**
         * dateChanged, This function will be called each time that the date in the applicable date
         * picker has changed. Both the old date, and the new date, are supplied in the event
         * object. Note that either parameter may contain null, which represents a cleared or empty
         * date.
         */
        @Override
        public void dateChanged(DateChangeEvent event) {
            LocalDate oldDate = event.getOldDate();
            LocalDate newDate = event.getNewDate();
            String oldDateString = PickerUtilities.localDateToString(oldDate, "(null)");
            String newDateString = PickerUtilities.localDateToString(newDate, "(null)");
            String messageStart = "\nThe date in " + datePickerName + " has changed from: ";
            String fullMessage = messageStart + oldDateString + " to: " + newDateString + ".";
            if (!panel.messageTextArea.getText().startsWith(messageStart)) {
                panel.messageTextArea.setText("");
            }
            panel.messageTextArea.append(fullMessage);
        }
    }

    /**
     * SampleDateTimeChangeListener, A DateTimeChangeListener provides a way for a class to receive
     * notifications whenever the date or time has changed in a DateTimePicker.
     */
    private static class SampleDateTimeChangeListener implements DateTimeChangeListener {

        /**
         * dateTimePickerName, This holds a chosen name for the component that we are listening to,
         * for generating time change messages in the demo.
         */
        public String dateTimePickerName;

        /**
         * Constructor.
         */
        private SampleDateTimeChangeListener(String dateTimePickerName) {
            this.dateTimePickerName = dateTimePickerName;
        }

        /**
         * dateOrTimeChanged, This function will be called whenever the in date or time in the
         * applicable DateTimePicker has changed.
         */
        @Override
        public void dateOrTimeChanged(DateTimeChangeEvent event) {
            // Report on the overall DateTimeChangeEvent.
            String messageStart = "\n\nThe LocalDateTime in " + dateTimePickerName + " has changed from: (";
            String fullMessage = messageStart + event.getOldDateTime() + ") to (" + event.getNewDateTime() + ").";
            if (!panel.messageTextArea.getText().startsWith(messageStart)) {
                panel.messageTextArea.setText("");
            }
            panel.messageTextArea.append(fullMessage);
            // Report on any DateChangeEvent, if one exists.
            DateChangeEvent dateEvent = event.getDateChangeEvent();
            if (dateEvent != null) {
                String dateChangeMessage = "\nThe DatePicker value has changed from (" + dateEvent.getOldDate()
                        + ") to (" + dateEvent.getNewDate() + ").";
                panel.messageTextArea.append(dateChangeMessage);
            }
            // Report on any TimeChangeEvent, if one exists.
            TimeChangeEvent timeEvent = event.getTimeChangeEvent();
            if (timeEvent != null) {
                String timeChangeMessage = "\nThe TimePicker value has changed from ("
                        + timeEvent.getOldTime() + ") to (" + timeEvent.getNewTime() + ").";
                panel.messageTextArea.append(timeChangeMessage);
            }
        }
    }

    /**
     * SampleDateVetoPolicy, A veto policy is a way to disallow certain dates from being selected in
     * calendar. A vetoed date cannot be selected by using the keyboard or the mouse.
     */
    private static class SampleDateVetoPolicy implements DateVetoPolicy {

        /**
         * isDateAllowed, Return true if a date should be allowed, or false if a date should be
         * vetoed.
         */
        @Override
        public boolean isDateAllowed(LocalDate date) {
            // Disallow days 7 to 11.
            if ((date.getDayOfMonth() >= 7) && (date.getDayOfMonth() <= 11)) {
                return false;
            }
            // Disallow odd numbered saturdays.
            if ((date.getDayOfWeek() == DayOfWeek.SATURDAY) && ((date.getDayOfMonth() % 2) == 1)) {
                return false;
            }
            // Allow all other days.
            return true;
        }
    }

    /**
     * SampleHighlightPolicy, A highlight policy is a way to visually highlight certain dates in the
     * calendar. These may be holidays, or weekends, or other significant dates.
     */
    private static class SampleHighlightPolicy implements DateHighlightPolicy {

        /**
         * getHighlightStringOrNull, This indicates if a date should be highlighted, or have a tool
         * tip in the calendar. Possible return values are: Return the desired tooltip text to give
         * a date a tooltip. Return an empty string to highlight a date without giving that date a
         * tooltip. Return null if a date should not be highlighted.
         */
        @Override
        public String getHighlightStringOrNull(LocalDate date) {
            // Highlight and give a tooltip to a chosen date.
            if (date.getDayOfMonth() == 25) {
                return "It's the 25th!";
            }
            // Highlight all weekend days.
            if (date.getDayOfWeek() == DayOfWeek.SATURDAY
                    || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                return "It's the weekend.";
            }
            // All other days should not be highlighted.
            return null;
        }
    }

    /**
     * SampleTimeChangeListener, A time change listener provides a way for a class to receive
     * notifications whenever the time has changed in a TimePicker.
     */
    private static class SampleTimeChangeListener implements TimeChangeListener {

        /**
         * timePickerName, This holds a chosen name for the time picker that we are listening to,
         * for generating time change messages in the demo.
         */
        public String timePickerName;

        /**
         * Constructor.
         */
        private SampleTimeChangeListener(String timePickerName) {
            this.timePickerName = timePickerName;
        }

        /**
         * timeChanged, This function will be called whenever the time in the applicable time picker
         * has changed. Note that the value may contain null, which represents a cleared or empty
         * time.
         */
        @Override
        public void timeChanged(TimeChangeEvent event) {
            LocalTime oldTime = event.getOldTime();
            LocalTime newTime = event.getNewTime();
            String oldTimeString = PickerUtilities.localTimeToString(oldTime, "(null)");
            String newTimeString = PickerUtilities.localTimeToString(newTime, "(null)");
            String messageStart = "\nThe time in " + timePickerName + " has changed from: ";
            String fullMessage = messageStart + oldTimeString + " to: " + newTimeString + ".";
            if (!panel.messageTextArea.getText().startsWith(messageStart)) {
                panel.messageTextArea.setText("");
            }
            panel.messageTextArea.append(fullMessage);
        }
    }

    /**
     * SampleTimeVetoPolicy, A veto policy is a way to disallow certain times from being selected in
     * the time picker. A vetoed time cannot be added to the time drop down menu. A vetoed time
     * cannot be selected by using the keyboard or the mouse.
     */
    private static class SampleTimeVetoPolicy implements TimeVetoPolicy {

        /**
         * isTimeAllowed, Return true if a time should be allowed, or false if a time should be
         * vetoed.
         */
        @Override
        public boolean isTimeAllowed(LocalTime time) {
            // Only allow times from 9a to 5p, inclusive.
            return PickerUtilities.isLocalTimeInRange(
                    time, LocalTime.of(9, 00), LocalTime.of(17, 00), true);
        }
    }

}
