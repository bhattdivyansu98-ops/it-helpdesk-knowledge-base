package com.helpdesk;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KnowledgeBaseApp extends JFrame {
    private static final String[] CATEGORIES = {"Network", "Printer", "Account", "Software", "Windows", "Other"};

    private final JTextField searchF = new JTextField(25);
    private final JComboBox<String> catC = new JComboBox<>(prepend("All", CATEGORIES));
    private final DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Title", "Category"}, 0) {
        public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);
    private final JTextArea viewer = new JTextArea();
    private final JLabel countL = new JLabel(" ");

    public KnowledgeBaseApp() {
        super("IT Help Desk Knowledge Base");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Search keyword:")); top.add(searchF);
        top.add(new JLabel("Category:")); top.add(catC);
        JButton go = new JButton("Search");
        go.addActionListener(e -> search());
        searchF.addActionListener(e -> search());
        catC.addActionListener(e -> search());
        JButton clear = new JButton("Clear");
        clear.addActionListener(e -> { searchF.setText(""); catC.setSelectedIndex(0); search(); });
        top.add(go); top.add(clear); top.add(countL);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        table.getColumnModel().getColumn(2).setMaxWidth(100);
        table.getSelectionModel().addListSelectionListener(e -> { if (!e.getValueIsAdjusting()) showSelected(); });

        viewer.setEditable(false);
        viewer.setLineWrap(true);
        viewer.setWrapStyleWord(true);
        viewer.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        viewer.setMargin(new Insets(10, 10, 10, 10));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(table), new JScrollPane(viewer));
        split.setDividerLocation(330);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton add = new JButton("Add Article");
        add.addActionListener(e -> editDialog(null));
        JButton edit = new JButton("Edit");
        edit.addActionListener(e -> { Integer id = selectedId(); if (id != null) editDialog(id); });
        JButton del = new JButton("Delete");
        del.addActionListener(e -> deleteSelected());
        bottom.add(add); bottom.add(edit); bottom.add(del);

        add(top, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
        setSize(950, 560);
        setLocationRelativeTo(null);
        search();
    }

    private static String[] prepend(String first, String[] rest) {
        String[] r = new String[rest.length + 1];
        r[0] = first;
        System.arraycopy(rest, 0, r, 1, rest.length);
        return r;
    }

    private void search() {
        StringBuilder sql = new StringBuilder("SELECT id, title, category FROM articles WHERE 1=1");
        List<Object> params = new ArrayList<>();
        String kw = searchF.getText().trim();
        if (!kw.isEmpty()) {
            // every word must match title, keywords or steps
            for (String w : kw.split("\\s+")) {
                sql.append(" AND (title LIKE ? OR keywords LIKE ? OR steps LIKE ?)");
                String like = "%" + w + "%";
                params.add(like); params.add(like); params.add(like);
            }
        }
        if (catC.getSelectedIndex() > 0) { sql.append(" AND category=?"); params.add(catC.getSelectedItem()); }
        sql.append(" ORDER BY title");
        model.setRowCount(0);
        viewer.setText("");
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) model.addRow(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3)});
            }
        } catch (SQLException ex) { error(ex); }
        countL.setText("  " + model.getRowCount() + " article(s) found");
        if (model.getRowCount() > 0) table.setRowSelectionInterval(0, 0);
    }

    private Integer selectedId() {
        int r = table.getSelectedRow();
        if (r < 0) { JOptionPane.showMessageDialog(this, "Select an article first."); return null; }
        return (Integer) model.getValueAt(r, 0);
    }

    private void showSelected() {
        int r = table.getSelectedRow();
        if (r < 0) return;
        int id = (Integer) model.getValueAt(r, 0);
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(
                "SELECT title, category, steps FROM articles WHERE id=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    viewer.setText(rs.getString(1) + "\nCategory: " + rs.getString(2)
                            + "\n\nSteps to resolve:\n" + rs.getString(3));
                    viewer.setCaretPosition(0);
                }
            }
        } catch (SQLException ex) { error(ex); }
    }

    private void editDialog(Integer id) {
        JTextField title = new JTextField(30), keywords = new JTextField(30);
        JComboBox<String> cat = new JComboBox<>(CATEGORIES);
        JTextArea steps = new JTextArea(10, 30);
        steps.setLineWrap(true); steps.setWrapStyleWord(true);
        if (id != null) {
            try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(
                    "SELECT title, category, keywords, steps FROM articles WHERE id=?")) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        title.setText(rs.getString(1)); cat.setSelectedItem(rs.getString(2));
                        keywords.setText(rs.getString(3)); steps.setText(rs.getString(4));
                    }
                }
            } catch (SQLException ex) { error(ex); return; }
        }
        JPanel p = new JPanel(new GridLayout(0, 1, 4, 4));
        p.add(new JLabel("Title")); p.add(title);
        p.add(new JLabel("Category")); p.add(cat);
        p.add(new JLabel("Keywords (space separated)")); p.add(keywords);
        p.add(new JLabel("Step-by-step solution")); p.add(new JScrollPane(steps));
        int ok = JOptionPane.showConfirmDialog(this, p, id == null ? "Add Article" : "Edit Article",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (ok != JOptionPane.OK_OPTION) return;
        if (title.getText().trim().isEmpty() || steps.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title and steps are required."); return;
        }
        String sql = id == null
                ? "INSERT INTO articles (title, category, keywords, steps) VALUES (?,?,?,?)"
                : "UPDATE articles SET title=?, category=?, keywords=?, steps=? WHERE id=?";
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, title.getText().trim());
            ps.setString(2, (String) cat.getSelectedItem());
            ps.setString(3, keywords.getText().trim());
            ps.setString(4, steps.getText().trim());
            if (id != null) ps.setInt(5, id);
            ps.executeUpdate();
        } catch (SQLException ex) { error(ex); }
        search();
    }

    private void deleteSelected() {
        Integer id = selectedId(); if (id == null) return;
        if (JOptionPane.showConfirmDialog(this, "Delete article #" + id + "?", "Confirm",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement("DELETE FROM articles WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException ex) { error(ex); }
        search();
    }

    private void error(SQLException ex) {
        JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new KnowledgeBaseApp().setVisible(true));
    }
}
