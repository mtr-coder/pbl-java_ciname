package ui;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class page_nv_danhsachphim extends JFrame {
       private JTable movieTable, scheduleTable;
    private DefaultTableModel movieModel, scheduleModel;
    private JButton btnBack, btnSearch, btnShowAll;
    private JTextField txtSearch;
    private JComboBox<String> cbGenre;
    private JLabel lblScheduleTitle;

    public page_nv_danhsachphim() {
        setTitle("Quản lý danh sách phim và suất chiếu");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Bộ lọc tìm kiếm"));
        searchPanel.add(new JLabel("Tên phim:"));
        txtSearch = new JTextField(15);
        searchPanel.add(txtSearch);
        searchPanel.add(new JLabel("Thể loại:"));
        cbGenre = new JComboBox<>(new String[]{"Tất cả", "Action", "Comedy", "Horror"});
        searchPanel.add(cbGenre);
        btnSearch = new JButton("Tìm kiếm");
        searchPanel.add(btnSearch);
        btnShowAll = new JButton("Tất cả phim");
        searchPanel.add(btnShowAll);
        add(searchPanel, BorderLayout.NORTH);

        String[] movieCols = {"ID", "Tên phim", "Thời lượng (phút)", "Thể loại", "Hành động"};
        movieModel = new DefaultTableModel(movieCols, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        movieTable = new JTable(movieModel);

        JPanel pnlBottom = new JPanel(new BorderLayout());
        lblScheduleTitle = new JLabel("Chọn một phim để xem chi tiết mã ghế đã đặt");
        lblScheduleTitle.setFont(new Font("Arial", Font.BOLD, 14));
        lblScheduleTitle.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
       
        String[] schedCols = {"Giờ chiếu", "Phòng chiếu", "Danh sách ghế đã đặt"};
        scheduleModel = new DefaultTableModel(schedCols, 0);
        scheduleTable = new JTable(scheduleModel);
        
        pnlBottom.add(lblScheduleTitle, BorderLayout.NORTH);
        pnlBottom.add(new JScrollPane(scheduleTable), BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(movieTable), pnlBottom);
        splitPane.setDividerLocation(300);
        add(splitPane, BorderLayout.CENTER);

        movieTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = movieTable.getSelectedRow();
                if (row != -1) {
                    String idPhim = movieTable.getValueAt(row, 0).toString();
                    String tenPhim = movieTable.getValueAt(row, 1).toString();
                    loadSuatChieu(idPhim, tenPhim);
                }
            }
        });

        btnBack = new JButton("Menu");
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footer.add(btnBack);
        add(footer, BorderLayout.SOUTH);
        btnBack.addActionListener(e -> this.dispose());
    }

    private void loadSuatChieu(String idPhim, String tenPhim) {
        lblScheduleTitle.setText("Chi tiết ghế đã đặt - Phim: " + tenPhim.toUpperCase());
        scheduleModel.setRowCount(0);
       
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new page_nv_danhsachphim().setVisible(true));
    }
}
