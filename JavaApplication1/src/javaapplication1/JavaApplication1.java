package javaapplication1;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Student Grade Tracker - GUI Version (Multi-Subject, Array-based)
 * ---------------------------------------------------------------------
 * A Swing app that:
 *  - Lets the user enter a name and a score for each subject
 *  - Stores students in plain arrays (fixed max capacity)
 *  - Computes each student's own average across their subjects
 *  - Shows them in a sortable table with per-student average + grade
 *  - Calculates and displays the class average, highest, and lowest
 *    (based on each student's average, not a single score)
 *  - Supports removing a selected student
 *  - Warns if you try to add a duplicate name
 *
 * @author RAJU
 */
public class JavaApplication1 extends JFrame {

    // Subjects tracked per student - change names/count here if needed
    private static final String[] SUBJECTS = {"Maths", "Science", "English"};
    private static final int NUM_SUBJECTS = SUBJECTS.length;

    // Fixed-size arrays instead of ArrayList. MAX_STUDENTS caps how many can be added.
    private static final int MAX_STUDENTS = 50;
    private String[] names = new String[MAX_STUDENTS];
    private double[][] scores = new double[MAX_STUDENTS][NUM_SUBJECTS]; // one row per student, one column per subject
    private int studentCount = 0; // how many slots in the arrays are actually filled

    // GUI components
    private JTextField nameField;
    private JTextField[] subjectFields; // one score field per subject
    private DefaultTableModel tableModel;
    private JTable table;
    private JLabel summaryLabel;
    private DefaultTableModel subjectSummaryModel;
    private JTable subjectSummaryTable;

    public JavaApplication1() {
        setTitle("Student Grade Tracker");
        setSize(760, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ---- Top panel: input fields + buttons ----
        JPanel inputPanel = new JPanel();

        JLabel nameLabel = new JLabel("Name:*");
        nameLabel.setForeground(Color.RED.darker());
        inputPanel.add(nameLabel);
        nameField = new JTextField(10);
        inputPanel.add(nameField);

        // One labeled score field per subject
        subjectFields = new JTextField[NUM_SUBJECTS];
        for (int i = 0; i < NUM_SUBJECTS; i++) {
            JLabel subjLabel = new JLabel(SUBJECTS[i] + ":*");
            subjLabel.setForeground(Color.RED.darker());
            inputPanel.add(subjLabel);
            subjectFields[i] = new JTextField(4);
            inputPanel.add(subjectFields[i]);
        }

        JButton addButton = new JButton("Add Student");
        inputPanel.add(addButton);

        JButton removeButton = new JButton("Remove Selected");
        inputPanel.add(removeButton);

        add(inputPanel, BorderLayout.NORTH);

        // ---- Middle: table showing all students ----
        // Columns: Name, one per subject, Average, Grade
        String[] columns = new String[1 + NUM_SUBJECTS + 2];
        columns[0] = "Name";
        for (int i = 0; i < NUM_SUBJECTS; i++) {
            columns[1 + i] = SUBJECTS[i];
        }
        columns[1 + NUM_SUBJECTS] = "Average";
        columns[2 + NUM_SUBJECTS] = "Grade";

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(22);
        table.setAutoCreateRowSorter(true); // click a column header to sort

        // ---- Subject-wise summary table: one row per subject ----
        subjectSummaryModel = new DefaultTableModel(
                new Object[]{"Subject", "Average", "Highest", "Lowest"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        subjectSummaryTable = new JTable(subjectSummaryModel);
        subjectSummaryTable.setRowHeight(22);
        JScrollPane subjectSummaryScroll = new JScrollPane(subjectSummaryTable);
        subjectSummaryScroll.setBorder(BorderFactory.createTitledBorder("Subject-wise Marks Summary"));
        subjectSummaryScroll.setPreferredSize(new Dimension(0, 120));

        // Stack the main roster on top and the subject summary underneath
        JSplitPane centerSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(table), subjectSummaryScroll);
        centerSplit.setResizeWeight(0.75); // give most of the space to the roster table
        add(centerSplit, BorderLayout.CENTER);

        // ---- Bottom: summary label ----
        summaryLabel = new JLabel("Add students to see class average, highest, and lowest.");
        summaryLabel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        add(summaryLabel, BorderLayout.SOUTH);

        // ---- Button actions ----
        addButton.addActionListener(e -> addStudent());
        removeButton.addActionListener(e -> removeSelected());
    }

    // Converts a numeric average into a letter grade
    /*private char letterGrade(double average) {
        if (average >= 90) return 'A';
        if (average >= 80) return 'B';
        if (average >= 70) return 'C';
        if (average >= 60) return 'D';
        return 'F';
    }
    */

    // Converts a numeric average into the final grade
private String letterGrade(double average, double... marks) {

    // If any subject is below 35, the student fails
    for (double mark : marks) {
        if (mark < 35) {
            return "F - FAIL";
        }
    }

    // All subjects are 35 or above, so grade depends on average
    if (average >= 90) return "A+ - PASS";
    if (average >= 80) return "A - PASS";
    if (average >= 70) return "B - PASS";
    if (average >= 60) return "C - PASS";
    if (average >= 50) return "D - PASS";
    return "E - PASS";
}

// Overall grade
private String overallGrade(double[] studentScores, double average) {
    return letterGrade(average, studentScores);
}





    // The score below which a single subject counts as a fail
 
    // Computes one student's average across all their subject scores
    private double studentAverage(int studentIndex) {
        double sum = 0;
        for (int j = 0; j < NUM_SUBJECTS; j++) {
            sum += scores[studentIndex][j];
        }
        return sum / NUM_SUBJECTS;
    }

    // Runs when "Add Student" is clicked
    private void addStudent() {
        if (studentCount >= MAX_STUDENTS) {
            JOptionPane.showMessageDialog(this,
                    "Maximum of " + MAX_STUDENTS + " students reached.");
            return;
        }

      
String name = nameField.getText().trim();

if (name.isEmpty()) {
    JOptionPane.showMessageDialog(
            this,
            "Please enter a name.",
            "Required Field",
            JOptionPane.WARNING_MESSAGE);
    return;
}

// Allow letters and spaces only
if (!name.matches("[a-zA-Z ]+")) {
    JOptionPane.showMessageDialog(
            this,
            "Student name must contain letters only.",
            "Invalid Name",
            JOptionPane.WARNING_MESSAGE);
    return;
}



        // Parse and validate every subject score before saving anything
        double[] enteredScores = new double[NUM_SUBJECTS];
        for (int i = 0; i < NUM_SUBJECTS; i++) {
            String text = subjectFields[i].getText().trim();
            try {
                double value = Double.parseDouble(text);
                if (value < 0 || value > 100) {
                    JOptionPane.showMessageDialog(this,
                            SUBJECTS[i] + " score must be between 0 and 100.");
                    return;
                }
                enteredScores[i] = value;
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                        SUBJECTS[i] + " score must be a number.", "Required Field", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        // Duplicate-name check (case-insensitive)
        for (int i = 0; i < studentCount; i++) {
            if (names[i].equalsIgnoreCase(name)) {
                int choice = JOptionPane.showConfirmDialog(this,
                        "\"" + name + "\" is already in the list. Add anyway?",
                        "Duplicate Name", JOptionPane.YES_NO_OPTION);
                if (choice != JOptionPane.YES_OPTION) {
                    return;
                }
                break;
            }
        }

        names[studentCount] = name;
        scores[studentCount] = enteredScores;
        double average = studentAverage(studentCount);
        studentCount++;

        // Build the table row: Name, each subject score, Average, Grade
        Object[] row = new Object[1 + NUM_SUBJECTS + 2];
        row[0] = name;
        for (int i = 0; i < NUM_SUBJECTS; i++) {
            row[1 + i] = enteredScores[i];
        }
        row[1 + NUM_SUBJECTS] = String.format("%.2f", average);
        row[2 + NUM_SUBJECTS] = String.valueOf(overallGrade(enteredScores, average));
        tableModel.addRow(row);

        nameField.setText("");
        for (JTextField field : subjectFields) {
            field.setText("");
        }
        nameField.requestFocus();

        updateSummary();
    }

    // Runs when "Remove Selected" is clicked
    private void removeSelected() {
        int viewRow = table.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a row to remove first.");
            return;
        }
        int modelRow = table.convertRowIndexToModel(viewRow);

        // Shift everything after modelRow one slot to the left
        for (int i = modelRow; i < studentCount - 1; i++) {
            names[i] = names[i + 1];
            scores[i] = scores[i + 1];
        }
        studentCount--;
        names[studentCount] = null;
        scores[studentCount] = new double[NUM_SUBJECTS];

        tableModel.removeRow(modelRow);
        updateSummary();
    }

    // Recalculates class average, highest, and lowest (based on each student's own average)
    private void updateSummary() {
        if (studentCount == 0) {
            summaryLabel.setText("Add students to see class average, highest, and lowest.");
            return;
        }

        double total = 0;
        double highest = studentAverage(0);
        double lowest = studentAverage(0);
        String topStudent = names[0];
        String lowestStudent = names[0];

        for (int i = 0; i < studentCount; i++) {
            double avg = studentAverage(i);
            total += avg;
            if (avg > highest) {
                highest = avg;
                topStudent = names[i];
            }
            if (avg < lowest) {
                lowest = avg;
                lowestStudent = names[i];
            }
        }

        double classAverage = total / studentCount;

        summaryLabel.setText(String.format(
                "Students: %d   |   Class Average: %.2f   |   Highest: %.2f (%s)   |   Lowest: %.2f (%s)",
                studentCount, classAverage, highest, topStudent, lowest, lowestStudent
        ));

        updateSubjectSummary();
    }

    // Recalculates the per-subject average/highest/lowest table
    private void updateSubjectSummary() {
        subjectSummaryModel.setRowCount(0);

        if (studentCount == 0) {
            return;
        }

        for (int j = 0; j < NUM_SUBJECTS; j++) {
            double total = 0;
            double highest = scores[0][j];
            double lowest = scores[0][j];
            String topStudent = names[0];
            String lowestStudent = names[0];

            for (int i = 0; i < studentCount; i++) {
                double score = scores[i][j];
                total += score;
                if (score > highest) {
                    highest = score;
                    topStudent = names[i];
                }
                if (score < lowest) {
                    lowest = score;
                    lowestStudent = names[i];
                }
            }

            double average = total / studentCount;

            subjectSummaryModel.addRow(new Object[]{
                    SUBJECTS[j],
                    String.format("%.2f", average),
                    String.format("%.2f (%s)", highest, topStudent),
                    String.format("%.2f (%s)", lowest, lowestStudent)
            });
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JavaApplication1 frame = new JavaApplication1();
            frame.setVisible(true);
        });
    }
}