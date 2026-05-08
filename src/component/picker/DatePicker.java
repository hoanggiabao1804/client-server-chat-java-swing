package component.picker;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import component.AppContext;
import component.AppFrame;

public class DatePicker implements AppContext {
    private Container parent;
    private Dimension size = new Dimension(500, 520);

    private JPanel rootContainer;

    // Header section
    private JPanel headerContainer;
    private JPanel optionContainer;
    private String[] monthLabels = { "January", "February", "March", "April", "May", "June", "July", "August",
            "September", "October", "November", "December" };
    private JComboBox<String> monthComboBox;
    private SpinnerNumberModel yearSpinnerModel;
    private JSpinner yearSpinner;
    private JButton prevButton;
    private JButton nextButton;

    // Body section
    private JPanel bodyContainer;
    private JPanel daysInWeekContainer;
    private List<JLabel> daysInWeekLabels;
    private JPanel daysInMonthContainer;
    private List<JButton> daysInMonthButtons;
    private JButton selectedButton;

    // Footer section
    private JPanel footerContainer;
    private JButton submitButton;
    private JButton cancelButton;

    private boolean isDateRangeEnabled = false;
    private LocalDate beginDate = null;
    private LocalDate endDate = null;
    private YearMonth currentDisplayYearMonth = YearMonth.now();
    private LocalDate selectedDate = null;
    private LocalDate currentDate = LocalDate.now();
    private Color currentDayBgColor = new Color(209, 194, 239);
    private Color currentDayFgColor = new Color(105, 80, 157);
    private Color selectedDayBgColor = new Color(132, 100, 197);

    public DatePicker(Container parent) {
        this.parent = parent;

        rootContainer = new JPanel();

        // Header section
        headerContainer = new JPanel();
        optionContainer = new JPanel();
        monthComboBox = new JComboBox<>(monthLabels);
        yearSpinnerModel = new SpinnerNumberModel(Year.now().getValue(), 1, Integer.MAX_VALUE, 1);
        yearSpinner = new JSpinner(yearSpinnerModel);
        prevButton = new JButton();
        nextButton = new JButton();

        // Body section
        bodyContainer = new JPanel();
        daysInWeekContainer = new JPanel();
        daysInWeekLabels = new ArrayList<>(Arrays.asList(
                new JLabel("SUN"),
                new JLabel("MON"),
                new JLabel("TUE"),
                new JLabel("WED"),
                new JLabel("THU"),
                new JLabel("FRI"),
                new JLabel("SAT")));

        daysInMonthContainer = new JPanel();
        daysInMonthButtons = new ArrayList<>();

        daysInMonthButtons = this.calendarFilling(currentDate);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 0, 0, 0);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        GridBagConstraints gbc1 = new GridBagConstraints();
        // gbc1.insets = new Insets(2, 0, 2, 0);
        gbc1.anchor = GridBagConstraints.WEST;
        gbc1.gridwidth = GridBagConstraints.REMAINDER;

        // Footer section
        footerContainer = new JPanel();
        cancelButton = new JButton();
        submitButton = new JButton();

        // Root section
        rootContainer.setLayout(new GridBagLayout());
        rootContainer.setPreferredSize(size);
        rootContainer.add(headerContainer, gbc);
        rootContainer.add(bodyContainer, gbc);
        rootContainer.add(footerContainer, gbc);
        rootContainer.setBackground(Color.white);
        rootContainer.setBorder(BorderFactory.createLineBorder(Color.black, 2));

        // Header section
        headerContainer.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 5));
        headerContainer.setPreferredSize(new Dimension(size.width - 10, 50));
        headerContainer.add(prevButton);
        headerContainer.add(optionContainer);
        headerContainer.add(nextButton);
        // headerContainer.setBackground(Color.pink);

        optionContainer.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        optionContainer.setPreferredSize(new Dimension(size.width - 250, 40));
        optionContainer.add(monthComboBox);
        optionContainer.add(yearSpinner);

        monthComboBox.setSelectedIndex(currentDate.getMonthValue() - 1);
        monthComboBox.setPreferredSize(new Dimension(125, 30));
        monthComboBox.setFont(new Font("Consolas", Font.PLAIN, 15));
        monthComboBox.setBackground(Color.white);
        monthComboBox.setForeground(Color.black);
        monthComboBox.addActionListener(l -> {
            currentDisplayYearMonth = currentDisplayYearMonth.withMonth(monthComboBox.getSelectedIndex() + 1);
            LocalDate dateOfPrevMonth = currentDisplayYearMonth.atDay(1);

            monthComboBox.setSelectedIndex(dateOfPrevMonth.getMonthValue() - 1);
            yearSpinner.setValue(dateOfPrevMonth.getYear());

            this.daysInMonthButtons = this.calendarFilling(dateOfPrevMonth);
            daysInMonthContainer.removeAll();
            daysInMonthButtons.forEach(item -> daysInMonthContainer.add(item));
            daysInMonthContainer.revalidate();
            daysInMonthContainer.repaint();
        });

        yearSpinner.setPreferredSize(new Dimension(75, 30));
        yearSpinner.setFont(new Font("Consolas", Font.PLAIN, 15));
        yearSpinner.setBackground(Color.white);
        yearSpinner.setForeground(Color.black);
        yearSpinner.addChangeListener(l -> {
            currentDisplayYearMonth = currentDisplayYearMonth.withYear((Integer) yearSpinner.getValue());
            LocalDate dateOfPrevMonth = currentDisplayYearMonth.atDay(1);

            monthComboBox.setSelectedIndex(dateOfPrevMonth.getMonthValue() - 1);
            yearSpinner.setValue(dateOfPrevMonth.getYear());

            this.daysInMonthButtons = this.calendarFilling(dateOfPrevMonth);
            daysInMonthContainer.removeAll();
            daysInMonthButtons.forEach(item -> daysInMonthContainer.add(item));
            daysInMonthContainer.revalidate();
            daysInMonthContainer.repaint();
        });

        prevButton.setText("PREV");
        prevButton.setFocusable(false);
        prevButton.setPreferredSize(new Dimension(90, 40));
        prevButton.setFont(new Font("Consolas", Font.BOLD, 20));
        prevButton.setBackground(Color.white);
        prevButton.setForeground(Color.gray);
        prevButton.setVerticalAlignment(JButton.CENTER);
        prevButton.setHorizontalAlignment(JButton.LEFT);
        prevButton.addActionListener(l -> {
            currentDisplayYearMonth = currentDisplayYearMonth.minus(1, ChronoUnit.MONTHS);
            LocalDate dateOfPrevMonth = currentDisplayYearMonth.atDay(1);

            monthComboBox.setSelectedIndex(dateOfPrevMonth.getMonthValue() - 1);
            yearSpinner.setValue(dateOfPrevMonth.getYear());

            this.daysInMonthButtons = this.calendarFilling(dateOfPrevMonth);
            daysInMonthContainer.removeAll();
            daysInMonthButtons.forEach(item -> daysInMonthContainer.add(item));
            daysInMonthContainer.revalidate();
            daysInMonthContainer.repaint();
        });

        nextButton.setText("NEXT");
        nextButton.setFocusable(false);
        nextButton.setPreferredSize(new Dimension(90, 40));
        nextButton.setFont(new Font("Consolas", Font.BOLD, 20));
        nextButton.setBackground(Color.white);
        nextButton.setForeground(Color.gray);
        nextButton.setVerticalAlignment(JButton.CENTER);
        nextButton.setHorizontalAlignment(JButton.RIGHT);
        nextButton.addActionListener(l -> {
            currentDisplayYearMonth = currentDisplayYearMonth.plus(1, ChronoUnit.MONTHS);
            LocalDate dateOfPrevMonth = currentDisplayYearMonth.atDay(1);

            monthComboBox.setSelectedIndex(dateOfPrevMonth.getMonthValue() - 1);
            yearSpinner.setValue(dateOfPrevMonth.getYear());

            this.daysInMonthButtons = this.calendarFilling(dateOfPrevMonth);
            daysInMonthContainer.removeAll();
            daysInMonthButtons.forEach(item -> daysInMonthContainer.add(item));
            daysInMonthContainer.revalidate();
            daysInMonthContainer.repaint();
        });

        // Body section
        bodyContainer.setLayout(new GridBagLayout());
        bodyContainer.setPreferredSize(new Dimension(size.width - 10, size.height - 130));
        bodyContainer.add(daysInWeekContainer, gbc);
        bodyContainer.add(daysInMonthContainer, gbc);
        // bodyContainer.setBackground(Color.green);

        daysInWeekContainer.setLayout(new FlowLayout(FlowLayout.LEADING, 12, 5));
        daysInWeekContainer.setPreferredSize(new Dimension(size.width - 20, 50));
        daysInWeekLabels.forEach(item -> {
            item.setFont(new Font("Consolas", Font.BOLD, 20));
            item.setHorizontalAlignment(JLabel.CENTER);
            item.setVerticalAlignment(JLabel.CENTER);
            item.setPreferredSize(new Dimension(55, 40));
            item.setForeground(Color.black);
            item.setBackground(Color.white);

            daysInWeekContainer.add(item);
        });

        daysInMonthContainer.setLayout(new FlowLayout(FlowLayout.LEADING, 12, 10));
        daysInMonthContainer.setPreferredSize(new Dimension(size.width - 20, size.height - 220));

        daysInMonthButtons.forEach(item -> {
            daysInMonthContainer.add(item);
        });
        // daysInMonthContainer.setBackground(Color.blue);

        // Footer section
        footerContainer.setLayout(new FlowLayout(FlowLayout.TRAILING, 10, 5));
        footerContainer.setPreferredSize(new Dimension(size.width - 10, 50));
        footerContainer.add(cancelButton);
        footerContainer.add(submitButton);

        cancelButton.setText("CANCEL");
        cancelButton.setFocusable(false);
        cancelButton.setPreferredSize(new Dimension(120, 40));
        cancelButton.setFont(new Font("Consolas", Font.BOLD, 20));
        cancelButton.setBackground(Color.white);
        cancelButton.setForeground(Color.black);
        cancelButton.setVerticalAlignment(JButton.CENTER);

        submitButton.setText("APPLY");
        submitButton.setFocusable(false);
        submitButton.setPreferredSize(new Dimension(120, 40));
        submitButton.setFont(new Font("Consolas", Font.BOLD, 20));
        submitButton.setBackground(selectedDayBgColor);
        submitButton.setForeground(Color.white);
        submitButton.setVerticalAlignment(JButton.CENTER);
        submitButton.setEnabled(false);

    }

    private List<JButton> calendarFilling(LocalDate date) {
        List<JButton> buttons = new ArrayList<>();

        int year = 0;
        int month = 0;

        // Previous month
        year = date.getMonthValue() == 1 ? date.getYear() - 1 : date.getYear();
        month = 1 +
                (date.getMonthValue() + 10) % 12;
        int dateLengthOfPrevMonth = this.getMonthLength(
                YearMonth.of(year, month));
        LocalDate lastDateOfPrevMonth = LocalDate.of(year, month, dateLengthOfPrevMonth);
        int dayOfWeekOfPrevMonth = lastDateOfPrevMonth.getDayOfWeek().getValue() % 7;
        for (int i = 0; i <= dayOfWeekOfPrevMonth; ++i) {
            boolean isSelected = (selectedDate != null) && selectedDate.getYear() == lastDateOfPrevMonth.getYear()
                    && selectedDate.getMonthValue() == lastDateOfPrevMonth.getMonthValue()
                    && selectedDate.getDayOfMonth() == (dateLengthOfPrevMonth + i - dayOfWeekOfPrevMonth);
            JButton currentButton = this.createDayButton("" + (dateLengthOfPrevMonth + i - dayOfWeekOfPrevMonth), true,
                    false, isSelected);
            buttons
                    .add(currentButton);
        }

        // Current month
        year = date.getYear();
        month = date.getMonthValue();
        int dateLengthOfMonth = this
                .getMonthLength(YearMonth.of(year, month));

        LocalDate current = null;
        for (int i = 1; i <= dateLengthOfMonth; ++i) {
            boolean isSelected = (selectedDate != null) && selectedDate.getYear() == date.getYear()
                    && selectedDate.getMonthValue() == date.getMonthValue()
                    && selectedDate.getDayOfMonth() == i;
            boolean isToday = currentDate.getYear() == date.getYear()
                    && currentDate.getMonthValue() == date.getMonthValue()
                    && currentDate.getDayOfMonth() == i;
            current = LocalDate.of(year, month, i);
            boolean isDisabled = this.isDateRangeEnabled && ((this.beginDate != null && this.beginDate.isAfter(current))
                    || (this.endDate != null && this.endDate.isBefore(current)));
            buttons.add(this.createDayButton("" + i, isDisabled, isToday, isSelected));
        }

        // Next month

        LocalDate firstDateOfNextMonth = LocalDate
                .of(date.getMonthValue() == 12 ? date.getYear() + 1 : date.getYear(), 1 +
                        (date.getMonthValue()) % 12, 1);
        int dayOfWeekOfNextMonth = firstDateOfNextMonth.getDayOfWeek().getValue() % 7;

        int limit = (buttons.size() + (7 - dayOfWeekOfNextMonth) == 35) ? 14 : 7;
        for (int i = dayOfWeekOfNextMonth; i < limit; ++i) {
            boolean isSelected = (selectedDate != null) && selectedDate.getYear() == firstDateOfNextMonth.getYear()
                    && selectedDate.getMonthValue() == firstDateOfNextMonth.getMonthValue()
                    && selectedDate.getDayOfMonth() == (1 - dayOfWeekOfNextMonth + i);
            buttons
                    .add(this.createDayButton("" + (1 - dayOfWeekOfNextMonth + i), true, false, isSelected));
        }

        return buttons;
    }

    private JButton createDayButton(String text, boolean isDisabled, boolean isToday, boolean isSelected) {
        JButton button = new JButton();
        button.setText(text);
        button.setFocusable(false);
        button.setFont(new Font("Consolas", Font.BOLD, 15));
        if (isSelected) {
            this.selectedButton = button;
            button.setBackground(selectedDayBgColor);
            button.setForeground(Color.white);
        } else if (isToday) {
            button.setBackground(this.currentDayBgColor);
            button.setForeground(this.currentDayFgColor);
        } else {
            button.setBackground(Color.white);
            button.setForeground(Color.black);

        }

        button.setEnabled(!isDisabled);
        button.setPreferredSize(new Dimension(55, 40));
        button.setHorizontalAlignment(JButton.CENTER);
        button.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!button.isEnabled()) {
                    return;
                }

                if (!button.getBackground().equals(selectedDayBgColor)) {

                    if (selectedButton != null) {

                        int dayValue = Integer.valueOf(selectedButton.getText());

                        if (dayValue == currentDate.getDayOfMonth()) {
                            selectedButton.setBackground(currentDayBgColor);
                            selectedButton.setForeground(currentDayFgColor);
                        } else {

                            selectedButton.setBackground(Color.white);
                            selectedButton.setForeground(Color.black);
                            daysInMonthContainer.revalidate();
                            daysInMonthContainer.repaint();
                        }
                    }
                    button.setBackground(selectedDayBgColor);
                    button.setForeground(Color.white);
                    selectedButton = button;
                    submitButton.setEnabled(true);
                    selectedDate = LocalDate.of((Integer) yearSpinner.getValue(), monthComboBox.getSelectedIndex() + 1,
                            Integer.valueOf(text));
                } else {
                    if (isToday) {
                        button.setBackground(currentDayBgColor);
                        button.setForeground(currentDayFgColor);
                    } else {
                        button.setBackground(Color.white);
                        button.setForeground(Color.black);
                    }

                    selectedButton = null;
                    submitButton.setEnabled(false);
                    selectedDate = null;
                }

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (!button.isEnabled()) {
                    return;
                }

                if (!button.getBackground().equals(currentDayBgColor)
                        && !button.getBackground().equals(selectedDayBgColor)) {
                    button.setBackground(Color.lightGray);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!button.isEnabled()) {
                    return;
                }

                if (!button.getBackground().equals(selectedDayBgColor)) {
                    if (isToday) {
                        button.setBackground(currentDayBgColor);
                    } else {
                        button.setBackground(Color.white);
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

        });

        return button;
    }

    private int getMonthLength(YearMonth yearMonth) {
        return yearMonth.lengthOfMonth();
    }

    public void setCancelAction(ActionListener l) {
        this.cancelButton.addActionListener(l);
    }

    public void setSubmitAction(ActionListener l) {
        this.submitButton.addActionListener(l);
    }

    public LocalDate submit() {
        return this.selectedDate;
    }

    public void setDateRangeEnabled(LocalDate begin, LocalDate end) {
        this.isDateRangeEnabled = true;
        this.beginDate = begin;
        this.endDate = end;
    }

    public void disableDateRange() {
        this.isDateRangeEnabled = false;
        this.beginDate = null;
        this.endDate = null;
    }

    public void revalidate(LocalDate date) {
        this.selectedDate = date;
        if (date != null) {
            monthComboBox.setSelectedIndex(date.getMonthValue() - 1);
            yearSpinner.setValue(date.getYear());
            daysInMonthButtons = this.calendarFilling(date);
        } else {
            monthComboBox.setSelectedIndex(currentDate.getMonthValue() - 1);
            yearSpinner.setValue(currentDate.getYear());
            daysInMonthButtons = this.calendarFilling(currentDate);
        }

        daysInMonthContainer.removeAll();
        daysInMonthButtons.forEach(item -> daysInMonthContainer.add(item));
        daysInMonthContainer.revalidate();
        daysInMonthContainer.repaint();
    }

    @Override
    public void draw() {
        this.parent.setSize(this.getSize());
        this.parent.setMinimumSize(this.getSize());
        this.parent.add(this.rootContainer);
    }

    @Override
    public void switchContext(String newContext) {
        this.parent.remove(this.rootContainer);

        AppFrame appFrame = AppFrame.getInstance();
        AppContext context = appFrame.getContextPools().getContext(newContext);
        this.parent.setSize(context.getSize());
        this.parent.add(context.getRootComponent());
        this.parent.revalidate();
        this.parent.repaint();
    }

    @Override
    public Component getRootComponent() {
        return this.rootContainer;
    }

    @Override
    public Dimension getSize() {
        return new Dimension(this.size.width, this.size.height + 35);
    }
}
