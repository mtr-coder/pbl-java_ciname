package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

public class page_ql_thongke extends JFrame {

    private JTable tableMovie;
    private JTable tableDaily;
    private JTable tableShow;
    private DBContext db = new DBContext(); // Sử dụng class DBContext của bạn

    public void UI() {
        setLayout(new BorderLayout(10, 10));

        // --- VÙNG TRÊN ---
        JPanel topPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        // Bảng 1: Thống kê theo Phim
        tableMovie = createTable(new String[]{"Tên Phim", "Số Vé", "Doanh Thu"});
        JPanel moviePanel = new JPanel(new BorderLayout());
        moviePanel.setBorder(BorderFactory.createTitledBorder("Thống kê theo từng phim (Toàn thời gian)"));
        moviePanel.add(new JScrollPane(tableMovie), BorderLayout.CENTER);

        // Bảng 2: Thống kê theo Ngày
        tableDaily = createTable(new String[]{"Ngày", "Tổng Số Vé", "Tổng Tiền"});
        JPanel dailyPanel = new JPanel(new BorderLayout());
        dailyPanel.setBorder(BorderFactory.createTitledBorder("Tổng doanh thu theo ngày"));
        dailyPanel.add(new JScrollPane(tableDaily), BorderLayout.CENTER);

        topPanel.add(moviePanel);
        topPanel.add(dailyPanel);

        // --- VÙNG DƯỚI ---
        tableShow = createTable(new String[]{"Mã Suất", "Tên Phim", "Ngày Chiếu", "Giờ Chiếu", "Phòng", "Doanh Thu"});
        JPanel showtimePanel = new JPanel(new BorderLayout());
        showtimePanel.setBorder(BorderFactory.createTitledBorder("Danh sách chi tiết các suất chiếu"));
        showtimePanel.setPreferredSize(new Dimension(1100, 350));
        showtimePanel.add(new JScrollPane(tableShow), BorderLayout.CENTER);

        add(topPanel, BorderLayout.CENTER);
        add(showtimePanel, BorderLayout.SOUTH);

        // --- SỰ KIỆN ---
        tableMovie.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tableMovie.getSelectedRow();
                if (row != -1) {
                    String movieName = tableMovie.getValueAt(row, 0).toString();
                    loadShowtimeData(movieName);
                }
            }
        });

        // Load dữ liệu ban đầu cho 2 bảng trên
        loadMovieStatistics();
        loadDailyStatistics();

        setSize(1100, 750);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // 1. Load thống kê doanh thu từng phim
    private void loadMovieStatistics() {
        DefaultTableModel model = (DefaultTableModel) tableMovie.getModel();
        model.setRowCount(0);
        String sql = "SELECT m.title, COUNT(t.id) as so_ve, SUM(t.price) as doanh_thu " +
                     "FROM movies m " +
                     "JOIN showtimes s ON m.id = s.movie_id " +
                     "LEFT JOIN tickets t ON s.id = t.showtime_id " +
                     "GROUP BY m.title";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("title"),
                    rs.getInt("so_ve"),
                    String.format("%,.0f VND", rs.getDouble("doanh_thu"))
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    // 2. Load thống kê doanh thu theo ngày
    private void loadDailyStatistics() {
        DefaultTableModel model = (DefaultTableModel) tableDaily.getModel();
        model.setRowCount(0);
        String sql = "SELECT CAST(start_time AS DATE) as ngay, COUNT(t.id) as so_ve, SUM(t.price) as doanh_thu " +
                     "FROM showtimes s " +
                     "LEFT JOIN tickets t ON s.id = t.showtime_id " +
                     "GROUP BY CAST(start_time AS DATE) " +
                     "ORDER BY ngay DESC";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getDate("ngay"),
                    rs.getInt("so_ve"),
                    String.format("%,.0f VND", rs.getDouble("doanh_thu"))
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    // 3. Load chi tiết suất chiếu khi click vào phim
    private void loadShowtimeData(String movieName) {
        DefaultTableModel model = (DefaultTableModel) tableShow.getModel();
        model.setRowCount(0);
        String sql = "SELECT s.id, m.title, CAST(s.start_time AS DATE) as ngay, " +
                     "CONVERT(VARCHAR(5), s.start_time, 108) as gio, r.name, SUM(t.price) as doanh_thu " +
                     "FROM showtimes s " +
                     "JOIN movies m ON s.movie_id = m.id " +
                     "JOIN rooms r ON s.room_id = r.id " +
                     "LEFT JOIN tickets t ON s.id = t.showtime_id " +
                     "WHERE m.title = ? " +
                     "GROUP BY s.id, m.title, s.start_time, r.name";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, movieName);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getDate("ngay"),
                    rs.getString("gio"),
                    rs.getString("name"),
                    String.format("%,.0f VND", rs.getDouble("doanh_thu"))
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private JTable createTable(String[] columns) {
        return new JTable(new DefaultTableModel(columns, 0)) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
    }

    public page_ql_thongke(String st) {
        super(st);
        UI();
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        new page_ql_thongke("Quản Lý Thống Kê Doanh Thu");
    }
}