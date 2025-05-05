package org.sam.component;

import com.formdev.flatlaf.FlatLightLaf;
import org.sam.repository.funcionario.FuncionarioRepository;
import org.sam.repository.funcionario.FuncionarioResponse;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import static org.sam.main.CoresApp.BRANCO;

public class TabelaEstilizada {

    private final FuncionarioRepository funcionarioRepository;

    public TabelaEstilizada(FuncionarioRepository funcionarioRepository) {
        this.funcionarioRepository = funcionarioRepository;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FlatLightLaf.setup();
            JFrame frame = new JFrame("Tabela Estilizada");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 400);

            TabelaEstilizada tabela = new TabelaEstilizada(new FuncionarioRepository());

            JPanel painelComPadding = new JPanel(new BorderLayout());
            painelComPadding.setBorder(new EmptyBorder(20, 20, 20, 20));
            painelComPadding.add(new JScrollPane(tabela.criarTabelaFuncionarios()), BorderLayout.CENTER);

            frame.setContentPane(painelComPadding);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    public JTable criarTabelaFuncionarios() {
        String[] colunas = {"ID", "Nome", "Data Admissão", "Salário", "Status"};

        List<FuncionarioResponse> funcionarios = listarFuncionarios();

        DefaultTableModel model = new DefaultTableModel(colunas, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return switch (columnIndex) {
                    case 0 -> Long.class;
                    case 2 -> Date.class;
                    case 3 -> Float.class;
                    case 4 -> Boolean.class;
                    default -> String.class;
                };
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };

        for (FuncionarioResponse funcionario : funcionarios) {
            Date dataAdmissao = Date.from(
                    funcionario.dataAdmissao().atZone(ZoneId.systemDefault()).toInstant()
            );

            model.addRow(new Object[]{
                    funcionario.id(),
                    funcionario.nome(),
                    dataAdmissao,
                    funcionario.salario(),
                    funcionario.status()
            });
        }

        JTable table = new JTable(model);
        estilizarTabela(table);

        return table;
    }

    public List<FuncionarioResponse> listarFuncionarios() {
        return funcionarioRepository.listarFuncionarios();
    }

    private void estilizarTabela(JTable table) {
        table.setRowHeight(36);
        table.setFillsViewportHeight(true);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(new Color(0xD0E8FF));
        table.setSelectionForeground(Color.BLACK);

        table.setShowHorizontalLines(false);
        table.setShowVerticalLines(false);
        table.setGridColor(Color.WHITE);

        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                JLabel label = new JLabel(value != null ? value.toString() : "");
                label.setOpaque(true);
                label.setBackground(BRANCO);

                switch (column) {
                    case 1 -> {
                        label.setHorizontalAlignment(SwingConstants.LEFT);
                        label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 5));
                    }
                    case 0 -> {
                        label.setHorizontalAlignment(SwingConstants.LEFT);
                        label.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
                    }
                    default -> {
                        label.setHorizontalAlignment(SwingConstants.CENTER);
                        label.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
                    }
                }

                return label;
            }
        });


        TableCellRenderer renderer = new LinhaRenderer();
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
    }

    static class LinhaRenderer extends DefaultTableCellRenderer {
        private final Color linhaSeparadora = new Color(230, 230, 230);
        private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {

            Color background = isSelected ? table.getSelectionBackground() : Color.WHITE;

            if (value instanceof Boolean booleanValue) {
                JCheckBox checkBox = new JCheckBox();
                checkBox.setSelected(booleanValue);
                checkBox.setHorizontalAlignment(SwingConstants.CENTER);
                checkBox.setOpaque(true);
                checkBox.setBackground(background);

                JPanel panel = new JPanel(new BorderLayout());
                panel.setBackground(background);
                panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, linhaSeparadora));
                panel.add(checkBox, BorderLayout.CENTER);
                return panel;
            }

            String displayValue = value instanceof Date
                    ? new SimpleDateFormat("dd/MM/yyyy HH:mm").format((Date) value)
                    : value != null ? value.toString() : "";


            JLabel label = new JLabel(displayValue);
            label.setOpaque(true);
            label.setBackground(background);
            label.setForeground(Color.BLACK);
            label.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, linhaSeparadora));

            switch (column) {

                case 0, 1 -> {
                    label.setHorizontalAlignment(SwingConstants.LEFT);
                    label.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createMatteBorder(0, 0, 1, 0, linhaSeparadora),
                            BorderFactory.createEmptyBorder(0, 10, 0, 0)
                    ));
                }
                case 2, 3 -> { // Data e Salário
                    label.setHorizontalAlignment(SwingConstants.CENTER);
                    label.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, linhaSeparadora));
                }
                default -> {
                    label.setHorizontalAlignment(SwingConstants.LEFT);
                    label.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, linhaSeparadora));
                }
            }



            return label;
        }
    }
}