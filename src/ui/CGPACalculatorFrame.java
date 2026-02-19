package ui;

import model.Course;
import model.GradeUtils;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Main application frame for the CGPA Calculator.
 * Features a tabbed pane where each tab is a semester containing a course
 * table.
 */
public class CGPACalculatorFrame extends JFrame {

    private JTabbedPane tabbedPane;
    private JLabel cgpaLabel;
    private int semesterCount = 0;

    // Stores courses for each semester (index = tab index)
    private final List<List<Course>> allSemesters = new ArrayList<>();
    // Stores table models for each semester
    private final List<DefaultTableModel> tableModels = new ArrayList<>();
    // Stores GPA labels for each semester
    private final List<JLabel> gpaLabels = new ArrayList<>();

    private static final Color PRIMARY = new Color(25, 118, 210);
    private static final Color PRIMARY_DARK = new Color(21, 101, 180);
    private static final Color BG = new Color(245, 245, 248);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(33, 33, 33);
    private static final Color TEXT_SECONDARY = new Color(117, 117, 117);
    private static final Color ACCENT = new Color(56, 142, 60);
    private static final Color DANGER = new Color(211, 47, 47);

    public CGPACalculatorFrame() {
        setTitle("CGPA Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(750, 580);
        setMinimumSize(new Dimension(650, 480));
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);

        setLayout(new BorderLayout(0, 0));

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);

        // Add the first semester by default
        addSemester();
    }

    // ─────────────────────────── Header ───────────────────────────

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY);
        header.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        JLabel titleLabel = new JLabel("CGPA Calculator");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);

        cgpaLabel = new JLabel("CGPA: 0.00");
        cgpaLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        cgpaLabel.setForeground(new Color(255, 255, 255, 230));

        header.add(titleLabel, BorderLayout.WEST);
        header.add(cgpaLabel, BorderLayout.EAST);

        return header;
    }

    // ─────────────────────────── Center ───────────────────────────

    private JPanel createCenterPanel() {
        JPanel center = new JPanel(new BorderLayout(0, 10));
        center.setBackground(BG);
        center.setBorder(BorderFactory.createEmptyBorder(12, 16, 16, 16));

        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabbedPane.setBackground(BG);

        // Toolbar with Add/Remove semester buttons
        JPanel toolbar = createToolbar();

        center.add(toolbar, BorderLayout.NORTH);
        center.add(tabbedPane, BorderLayout.CENTER);

        return center;
    }

    private JPanel createToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toolbar.setBackground(BG);

        JButton addSemBtn = createStyledButton("+ Add Semester", PRIMARY);
        addSemBtn.addActionListener(e -> addSemester());

        JButton removeSemBtn = createStyledButton("- Remove Semester", DANGER);
        removeSemBtn.addActionListener(e -> removeCurrentSemester());

        toolbar.add(addSemBtn);
        toolbar.add(removeSemBtn);

        return toolbar;
    }

    // ───────────────────── Semester Tab Panel ─────────────────────

    private JPanel createSemesterPanel(int semesterIndex) {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(BG);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // ── Table ──
        String[] columns = { "Course Name", "Credit Hours", "Grade" };
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return true; // all cells editable
            }
        };
        tableModels.add(model);

        JTable table = new JTable(model);
        table.setRowHeight(32);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(232, 234, 240));
        table.getTableHeader().setForeground(TEXT_PRIMARY);
        table.setSelectionBackground(new Color(187, 222, 251));
        table.setGridColor(new Color(224, 224, 224));
        table.setShowGrid(true);

        // Center-align Credits and Grade columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);

        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(280);
        table.getColumnModel().getColumn(1).setPreferredWidth(120);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);

        // Credit Hours column: combo box editor (1–6)
        JComboBox<Integer> creditCombo = new JComboBox<>();
        for (int c : GradeUtils.CREDIT_OPTIONS)
            creditCombo.addItem(c);
        creditCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        TableColumn creditCol = table.getColumnModel().getColumn(1);
        creditCol.setCellEditor(new DefaultCellEditor(creditCombo));

        // Grade column: combo box editor (A–F)
        JComboBox<String> gradeCombo = new JComboBox<>(GradeUtils.GRADES);
        gradeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        TableColumn gradeCol = table.getColumnModel().getColumn(2);
        gradeCol.setCellEditor(new DefaultCellEditor(gradeCombo));

        // Listen for table changes to recalculate GPA/CGPA
        model.addTableModelListener(e -> {
            syncCoursesFromTable(semesterIndex);
            refreshAllGPAs();
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        scrollPane.getViewport().setBackground(CARD_BG);

        // ── Bottom bar: buttons + GPA ──
        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.setBackground(BG);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        buttonPanel.setBackground(BG);

        JButton addBtn = createStyledButton("+ Add Course", ACCENT);
        addBtn.addActionListener(e -> {
            model.addRow(new Object[] { "", GradeUtils.CREDIT_OPTIONS[0], GradeUtils.GRADES[0] });
        });

        JButton removeBtn = createStyledButton("Remove Selected", DANGER);
        removeBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                model.removeRow(row);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Please select a course to remove.",
                        "No Selection", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        buttonPanel.add(addBtn);
        buttonPanel.add(removeBtn);

        JLabel gpaLabel = new JLabel("Semester GPA: 0.00  ");
        gpaLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        gpaLabel.setForeground(PRIMARY_DARK);
        gpaLabels.add(gpaLabel);

        bottomBar.add(buttonPanel, BorderLayout.WEST);
        bottomBar.add(gpaLabel, BorderLayout.EAST);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomBar, BorderLayout.SOUTH);

        return panel;
    }

    // ───────────────────── Semester Management ─────────────────────

    private void addSemester() {
        semesterCount++;
        List<Course> courses = new ArrayList<>();
        allSemesters.add(courses);

        int index = allSemesters.size() - 1;
        JPanel semPanel = createSemesterPanel(index);
        tabbedPane.addTab("Semester " + semesterCount, semPanel);
        tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
    }

    private void removeCurrentSemester() {
        int selected = tabbedPane.getSelectedIndex();
        if (selected < 0)
            return;

        if (tabbedPane.getTabCount() <= 1) {
            JOptionPane.showMessageDialog(this,
                    "You must have at least one semester.",
                    "Cannot Remove", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Remove \"" + tabbedPane.getTitleAt(selected) + "\" and all its courses?",
                "Confirm Removal", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            tabbedPane.removeTabAt(selected);
            allSemesters.remove(selected);
            tableModels.remove(selected);
            gpaLabels.remove(selected);
            refreshAllGPAs();
        }
    }

    // ──────────────────── Data Sync & Calculation ────────────────────

    /**
     * Reads the table model at the given semester index and rebuilds
     * the corresponding course list.
     */
    private void syncCoursesFromTable(int semesterIndex) {
        if (semesterIndex < 0 || semesterIndex >= tableModels.size())
            return;

        DefaultTableModel model = tableModels.get(semesterIndex);
        List<Course> courses = new ArrayList<>();

        for (int r = 0; r < model.getRowCount(); r++) {
            String name = model.getValueAt(r, 0) != null ? model.getValueAt(r, 0).toString() : "";
            int credits;
            try {
                credits = Integer.parseInt(model.getValueAt(r, 1).toString());
            } catch (Exception e) {
                credits = 1;
            }
            String grade = model.getValueAt(r, 2) != null ? model.getValueAt(r, 2).toString() : "F";
            courses.add(new Course(name, credits, grade));
        }

        allSemesters.set(semesterIndex, courses);
    }

    /**
     * Recalculates and updates all semester GPA labels and the overall CGPA label.
     */
    private void refreshAllGPAs() {
        for (int i = 0; i < allSemesters.size(); i++) {
            double gpa = GradeUtils.calculateGPA(allSemesters.get(i));
            if (i < gpaLabels.size()) {
                gpaLabels.get(i).setText(String.format("Semester GPA: %.2f  ", gpa));
            }
        }

        double cgpa = GradeUtils.calculateCGPA(allSemesters);
        cgpaLabel.setText(String.format("CGPA: %.2f", cgpa));
    }

    // ──────────────────────── UI Helpers ──────────────────────────

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

        // Hover effect
        Color hoverColor = bgColor.darker();
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }
}
