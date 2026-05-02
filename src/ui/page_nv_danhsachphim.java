package ui;

import javax.swing.*;
import java.awt.event.ActionEvent; 
import java.awt.event.ActionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.Vector;

public class page_nv_danhsachphim extends JFrame implements ActionListener {
    private JTable movieTable, scheduleTable;
    private DefaultTableModel movieModel, scheduleModel;
    private JButton btnBack, btnSearch, btnShowAll;
    private JTextField txtSearch;
    private JComboBox<String> cbGenre;
    private JLabel lblScheduleTitle;
    private DBContext dbContext = new DBContext();

   public void GUI() {
        setTitle("Quản lý danh sách phim và suất chiếu");
        setSize(1100, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Bộ lọc tìm kiếm"));
        
        JLabel lb1 = new JLabel("Tên phim:");
        txtSearch = new JTextField(15);
        JLabel lb2 = new JLabel("Thể loại:");
        cbGenre = new JComboBox<>(new String[]{"Tất cả", "Action", "Comedy", "Horror", "Drama", "Sci-Fi"});
        btnSearch = new JButton("Tìm kiếm");
        btnShowAll = new JButton("Tất cả phim");
        
        searchPanel.add(lb1);
        searchPanel.add(txtSearch);
        searchPanel.add(lb2);
        searchPanel.add(cbGenre);
        searchPanel.add(btnSearch);
        searchPanel.add(btnShowAll);

        String[] movieCols = {"ID", "Tên phim", "Thời lượng (phút)", "Thể loại"};
        movieModel = new DefaultTableModel(movieCols, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        movieTable = new JTable(movieModel);

        JPanel pnlBottom = new JPanel(new BorderLayout());
        lblScheduleTitle = new JLabel("Chọn một phim từ danh sách trên để xem chi tiết ghế");
        lblScheduleTitle.setFont(new Font("Arial", Font.BOLD, 14));
        lblScheduleTitle.setForeground(Color.BLUE);
        lblScheduleTitle.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        String[] schedCols = {"Giờ chiếu", "Phòng chiếu", "Danh sách ghế đã đặt"};
        scheduleModel = new DefaultTableModel(schedCols, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        scheduleTable = new JTable(scheduleModel);

        pnlBottom.add(lblScheduleTitle, BorderLayout.NORTH);
        pnlBottom.add(new JScrollPane(scheduleTable), BorderLayout.CENTER);
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(movieTable), pnlBottom);
        splitPane.setDividerLocation(300);

        btnBack = new JButton("Quay lại Menu");
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footer.add(btnBack);

        add(searchPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);

        btnSearch.addActionListener(this);
        btnBack.addActionListener(this);
        btnShowAll.addActionListener(this);

        movieTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = movieTable.getSelectedRow();
                if (row != -1) {
                    String idPhim = movieTable.getValueAt(row, 0).toString();
                    String tenPhim = movieTable.getValueAt(row, 1).toString();
                    loadSuatChieu(idPhim, tenPhim);
                }
            }
        }
    );
    setVisible(true);
    }

  public void actionPerformed(ActionEvent e) {
    if(e.getSource() == btnSearch) {
        String keyword = txtSearch.getText().trim();
        String genre = (String) cbGenre.getSelectedItem();
        loadMovies(keyword, genre);
    } else if(e.getSource() == btnShowAll) {
        txtSearch.setText("");
        cbGenre.setSelectedIndex(0);
        loadMovies(null, "Tất cả");
    }else if(e.getSource() == btnBack) {
        this.dispose();
        new page_nv_menu().setVisible(true); 
    }
  }
    private void loadMovies(String keyword, String genre) {
        movieModel.setRowCount(0);
        String sql = "SELECT * FROM movies WHERE 1=1";
        if (keyword != null && !keyword.isEmpty()) {
            sql += " AND title LIKE ?";
        }
        if (genre != null && !genre.equals("Tất cả")) {
            sql += " AND genre = ?";
        }
        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            int paramIdx = 1;
            if (keyword != null && !keyword.isEmpty()) {
                ps.setString(paramIdx++, "%" + keyword + "%");
            }
            if (genre != null && !genre.equals("Tất cả")) {
                ps.setString(paramIdx++, genre);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                movieModel.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getInt("duration"),
                    rs.getString("genre")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "ERROR! " + e.getMessage());
        }
    }
    private void loadSuatChieu(String idPhim, String tenPhim) {
        lblScheduleTitle.setText("Chi tiết ghế đã đặt - Phim: " + tenPhim.toUpperCase());
        scheduleModel.setRowCount(0);
        String sql = "SELECT st.start_time, r.name as room_name, " +
                     "STRING_AGG(s.seat_number, ', ') as booked_seats " +
                     "FROM showtimes st " +
                     "JOIN rooms r ON st.room_id = r.id " +
                     "LEFT JOIN tickets t ON st.id = t.showtime_id " +
                     "LEFT JOIN seats s ON t.seat_id = s.id " +
                     "WHERE st.movie_id = ? " +
                     "GROUP BY st.start_time, r.name";

        try (Connection conn = dbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(idPhim));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String seats = rs.getString("booked_seats");
                scheduleModel.addRow(new Object[]{
                    rs.getTimestamp("start_time"),
                    rs.getString("room_name"),
                    (seats != null) ? seats : "Chưa có ghế đặt"
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "ERROR! " + e.getMessage());
        }
    }

    public page_nv_danhsachphim(String st) {
        super(st);
        GUI();
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
      new page_nv_danhsachphim("Danh sach phim");
    }
}
