package ui;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class page_ql_phim extends JFrame implements ActionListener {
    public JLabel lb_qlphim, lb_title, lb_duration, lb_genre, lb_search;
    public JTextField txt_title, txt_duration, txt_genre;
    public JRadioButton rb_all, rb_title, rb_genre;
    public ButtonGroup searchGroup;
    public JList<String> list_movies;
    public JButton btn_newMovie, btn_updateMovie, btn_deleteMovie, btn_refresh, btn_undo;
    DefaultListModel<String> movieListModel;
    public JTable tb_movies;
    private final DefaultTableModel tableModel;
    public JPanel pn_title, pn_body1, pn_body2, pn_body3, pn_footer, pn_all;

    public page_ql_phim() {
        setTitle("Quản lý phim");

        lb_qlphim = new JLabel("Quản lý phim", SwingConstants.CENTER);
        lb_qlphim.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 22));
        lb_qlphim.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        lb_title = new JLabel("Tên phim:");
        lb_duration = new JLabel("Thời lượng:");
        lb_genre = new JLabel("Thể loại:");
        lb_search = new JLabel("Tìm kiếm theo:");
        txt_title = new JTextField(10);
        txt_duration = new JTextField(10);
        txt_genre = new JTextField(10);
        rb_all = new JRadioButton("Tất cả");
        rb_title = new JRadioButton("Tên phim");
        rb_genre = new JRadioButton("Thể loại");
        searchGroup = new ButtonGroup();
        searchGroup.add(rb_all);
        searchGroup.add(rb_title);
        searchGroup.add(rb_genre);
        movieListModel = new DefaultListModel<>();
        list_movies = new JList<>(movieListModel);
        list_movies.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list_movies.setVisibleRowCount(1);
        JScrollPane listScrollPane = new JScrollPane(list_movies);
        listScrollPane.setPreferredSize(new Dimension(100, 20));
        btn_newMovie = new JButton("Thêm");
        btn_updateMovie = new JButton("Sửa");
        btn_deleteMovie = new JButton("Xóa");
        btn_refresh = new JButton("Làm mới");
        btn_undo = new JButton("Quay lại");
        tableModel = new DefaultTableModel(new String[] {"ID", "Tên phim", "Thời lượng", "Thể loại"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tb_movies = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(tb_movies);

        pn_title = new JPanel();
        pn_body1 = new JPanel();
        pn_body2 = new JPanel();
        pn_body3 = new JPanel();
        pn_footer = new JPanel();
        pn_all = new JPanel();

        pn_title.setLayout(new FlowLayout());
        pn_body1.setLayout(new FlowLayout());
        pn_body2.setLayout(new FlowLayout());
        pn_body3.setLayout(new FlowLayout());
        pn_footer.setLayout(new BorderLayout());
        pn_footer.setPreferredSize(new Dimension(800, 400));
        pn_footer.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        pn_all.setLayout(new BoxLayout(pn_all, BoxLayout.Y_AXIS));

        pn_title.add(lb_qlphim);
        pn_body1.add(lb_title);
        pn_body1.add(txt_title);
        pn_body1.add(lb_duration);
        pn_body1.add(txt_duration);
        pn_body1.add(lb_genre);
        pn_body1.add(txt_genre);
        pn_body2.add(lb_search);
        pn_body2.add(rb_all);
        pn_body2.add(rb_title);
        pn_body2.add(rb_genre);
        pn_body2.add(listScrollPane);
        pn_body3.add(btn_newMovie);
        pn_body3.add(btn_updateMovie);
        pn_body3.add(btn_deleteMovie);
        pn_body3.add(btn_refresh);
        pn_body3.add(btn_undo);
        pn_footer.add(tableScrollPane, BorderLayout.CENTER);
        pn_all.add(pn_title);
        pn_all.add(pn_body1);
        pn_all.add(pn_body2);
        pn_all.add(pn_body3);
        pn_all.add(pn_footer);
        add(pn_all);

        rb_all.addActionListener(this);
        rb_title.addActionListener(this);
        rb_genre.addActionListener(this);
        btn_refresh.addActionListener(this);
        btn_newMovie.addActionListener(this);
        btn_updateMovie.addActionListener(this);
        btn_deleteMovie.addActionListener(this);
        btn_undo.addActionListener(this);

        list_movies.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                handleListSelection();
            }
        });

        tb_movies.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                fillFieldsFromTable();
            }
        });

        rb_all.setSelected(true);
        loadAllMovies();

        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == rb_all) {
            movieListModel.clear();
            loadAllMovies();
        } else if (src == rb_title) {
            loadListByColumn("title");
        } else if (src == rb_genre) {
            loadListByColumn("genre");
        } else if (src == btn_refresh) {
            txt_title.setText("");
            txt_duration.setText("");
            txt_genre.setText("");
            rb_all.setSelected(true);
            movieListModel.clear();
            loadAllMovies();
        } else if (src == btn_newMovie) {
            handleAdd();
        } else if (src == btn_updateMovie) {
            handleUpdate();
        } else if (src == btn_deleteMovie) {
            handleDelete();
        } else if (src == btn_undo) {
            dispose();
            page_ql_menu menu = new page_ql_menu();
            menu.setVisible(true);
        }
    }

    private void handleListSelection() {
        String selected = list_movies.getSelectedValue();
        if (selected == null) {
            return;
        }
        if (rb_title.isSelected()) {
            loadMoviesByFilter("title", selected);
        } else if (rb_genre.isSelected()) {
            loadMoviesByFilter("genre", selected);
        }
    }

    private void fillFieldsFromTable() {
        int row = tb_movies.getSelectedRow();
        if (row < 0) {
            return;
        }
        txt_title.setText(String.valueOf(tableModel.getValueAt(row, 1)));
        txt_duration.setText(String.valueOf(tableModel.getValueAt(row, 2)));
        txt_genre.setText(String.valueOf(tableModel.getValueAt(row, 3)));
    }

    private void loadAllMovies() {
        String sql = "SELECT id, title, duration, genre FROM movies";
        loadTableBySql(sql, null);
    }

    private void loadMoviesByFilter(String column, String value) {
        String sql = "SELECT id, title, duration, genre FROM movies WHERE " + column + " = ?";
        loadTableBySql(sql, value);
    }

    private void loadListByColumn(String column) {
        movieListModel.clear();
        String sql = "SELECT " + column + " FROM movies ORDER BY " + column;
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String value = rs.getString(1);
                if (value != null && !value.isBlank()) {
                    movieListModel.addElement(value);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải danh sách: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadTableBySql(String sql, String param) {
        clearTable();
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (param != null) {
                ps.setString(1, param);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tableModel.addRow(new Object[] {
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getInt("duration"),
                        rs.getString("genre")
                    });
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải bảng: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearTable() {
        tableModel.setRowCount(0);
    }

    private void handleAdd() {
        String title = txt_title.getText().trim();
        String durationText = txt_duration.getText().trim();
        String genre = txt_genre.getText().trim();

        if (title.isEmpty() || durationText.isEmpty() || genre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đủ thông tin.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int duration;
        try {
            duration = Integer.parseInt(durationText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Thời lượng phải là số.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sql = "INSERT INTO movies (title, duration, genre) VALUES (?, ?, ?)";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setInt(2, duration);
            ps.setString(3, genre);
            ps.executeUpdate();
            refreshAfterCrud();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi thêm phim: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdate() {
        int row = tb_movies.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chọn một phim để sửa.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String title = txt_title.getText().trim();
        String durationText = txt_duration.getText().trim();
        String genre = txt_genre.getText().trim();

        if (title.isEmpty() || durationText.isEmpty() || genre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đủ thông tin.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int duration;
        try {
            duration = Integer.parseInt(durationText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Thời lượng phải là số.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        String sql = "UPDATE movies SET title = ?, duration = ?, genre = ? WHERE id = ?";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setInt(2, duration);
            ps.setString(3, genre);
            ps.setInt(4, id);
            ps.executeUpdate();
            refreshAfterCrud();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi sửa phim: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDelete() {
        int row = tb_movies.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chọn một phim để xóa.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa phim này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        String sql = "DELETE FROM movies WHERE id = ?";
        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            refreshAfterCrud();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi xóa phim: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshAfterCrud() {
        txt_title.setText("");
        txt_duration.setText("");
        txt_genre.setText("");
        rb_all.setSelected(true);
        movieListModel.clear();
        loadAllMovies();
    }

    public static void main(String[] args) {
        page_ql_phim pn = new page_ql_phim();
        pn.setVisible(true);
    }
}
