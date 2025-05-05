package org.sam.component;

import com.formdev.flatlaf.FlatLightLaf;
import org.sam.ferramentas.FormatarFloat;
import org.sam.ferramentas.Icones;
import org.sam.ferramentas.RedimensionarIcone;
import org.sam.ferramentas.RoundBorder;
import org.sam.repository.funcionario.FuncionarioRepository;
import org.sam.repository.funcionario.FuncionarioResponse;
import raven.alerts.MessageAlerts;
import raven.popup.GlassPanePopup;
import raven.popup.component.PopupCallbackAction;
import raven.popup.component.PopupController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
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
            GlassPanePopup.install(frame);

            TabelaEstilizada tabela = new TabelaEstilizada(new FuncionarioRepository());

            JScrollPane scrollPane = new JScrollPane(tabela.criarTabelaFuncionarios());
            scrollPane.setBorder(new RoundBorder(15));
            scrollPane.setOpaque(false);
            scrollPane.getViewport().setOpaque(false);

            JPanel painelComPadding = new JPanel(new BorderLayout());
            painelComPadding.setBackground(Color.WHITE);
            painelComPadding.setBorder(new EmptyBorder(20, 20, 20, 20));
            painelComPadding.add(scrollPane, BorderLayout.CENTER);

            frame.setContentPane(painelComPadding);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    public JTable criarTabelaFuncionarios() {
        String[] colunas = {"ID", "Nome", "Data Admissão", "Salário", "Status", "Ações"};
        List<FuncionarioResponse> funcionarios = funcionarioRepository.listarFuncionarios();

        DefaultTableModel model = new DefaultTableModel(colunas, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return switch (columnIndex) {
                    case 0 -> Long.class;
                    case 2 -> Date.class;
                    case 3 -> Float.class;
                    case 4 -> Boolean.class;
                    default -> Object.class;
                };
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4 || column == 5;
            }
        };

        for (FuncionarioResponse f : funcionarios) {
            Date dataAdmissao = Date.from(f.dataAdmissao().atZone(ZoneId.systemDefault()).toInstant());
            model.addRow(new Object[]{f.id(), f.nome(), dataAdmissao, f.salario(), f.status(), null});
        }

        JTable table = new JTable(model);
        estilizarTabela(table);
        return table;
    }

    private void estilizarTabela(JTable table) {
        Color linhaSeparadora = new Color(230, 230, 230);

        table.setRowHeight(36);
        table.setFillsViewportHeight(true);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(new Color(0xD0E8FF));
        table.setSelectionForeground(Color.DARK_GRAY);
        table.setShowHorizontalLines(false);
        table.setShowVerticalLines(false);
        table.setGridColor(Color.WHITE);

        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                JLabel label = new JLabel(value == null ? "" : value.toString());
                label.setOpaque(true);
                label.setBackground(BRANCO);
                switch (column) {
                    case 0 -> {
                        label.setHorizontalAlignment(SwingConstants.LEFT);
                        label.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
                    }
                    case 1 -> {
                        label.setHorizontalAlignment(SwingConstants.LEFT);
                        label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 5));
                    }
                    default -> {
                        label.setHorizontalAlignment(SwingConstants.CENTER);
                        label.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
                    }
                }
                return label;
            }
        });

        TableCellRenderer linhaRenderer = new LinhaRenderer();
        for (int i = 0; i < table.getColumnCount(); i++) {
            if (i == 5) {
                table.getColumnModel().getColumn(i).setCellRenderer(new BotaoRemoverRenderer(linhaSeparadora));
                table.getColumnModel().getColumn(i).setCellEditor(new BotaoRemoverEditor(new JCheckBox(), table, linhaSeparadora));
            } else {
                table.getColumnModel().getColumn(i).setCellRenderer(linhaRenderer);
            }
        }
    }

    static class LinhaRenderer extends DefaultTableCellRenderer {
        private final Color linhaSeparadora;
        private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        LinhaRenderer() {
            this.linhaSeparadora = new Color(230, 230, 230);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            Color background = isSelected ? table.getSelectionBackground() : Color.WHITE;
            if (value instanceof Boolean b) {
                JCheckBox checkBox = new JCheckBox();
                checkBox.setSelected(b);
                checkBox.setHorizontalAlignment(SwingConstants.CENTER);
                checkBox.setOpaque(true);
                checkBox.setBackground(background);
                JPanel panel = new JPanel(new BorderLayout());
                panel.setBackground(background);
                panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, linhaSeparadora));
                panel.add(checkBox, BorderLayout.CENTER);
                return panel;
            }

            String displayValue;
            if (column == 3 && value instanceof Number) {
                displayValue = "R$ " + FormatarFloat.format(((Number) value).floatValue());
            } else if (value instanceof Date) {
                displayValue = dateFormat.format((Date) value);
            } else {
                displayValue = value == null ? "" : value.toString();
            }

            JLabel label = new JLabel(displayValue);
            label.setOpaque(true);
            label.setBackground(background);
            if (column == 3) label.setForeground(new Color(0, 128, 0)); else label.setForeground(Color.BLACK);
            label.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, linhaSeparadora));

            switch (column) {
                case 0, 1 -> {
                    label.setHorizontalAlignment(SwingConstants.LEFT);
                    label.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createMatteBorder(0, 0, 1, 0, linhaSeparadora),
                            BorderFactory.createEmptyBorder(0, 10, 0, 0)
                    ));
                }
                case 2, 3 -> label.setHorizontalAlignment(SwingConstants.CENTER);
                default -> label.setHorizontalAlignment(SwingConstants.LEFT);
            }
            return label;
        }
    }

    static class BotaoRemoverRenderer extends JButton implements TableCellRenderer {
        BotaoRemoverRenderer(Color linhaSeparadora) {
            setIcon(RedimensionarIcone.redimensionarIcone(Icones.close, 15, 15));
            setFocusable(false);
            setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, linhaSeparadora));
        }
        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    class BotaoRemoverEditor extends DefaultCellEditor {
        private final JButton button;

        BotaoRemoverEditor(JCheckBox checkBox, JTable table, Color linhaSeparadora) {
            super(checkBox);
            button = new JButton();
            button.setIcon(RedimensionarIcone.redimensionarIcone(Icones.close, 15, 15));
            button.setFocusable(false);
            button.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, linhaSeparadora));
            button.addActionListener(e -> {
                int row = table.getEditingRow();
                Object nome = table.getValueAt(row, 1);
                MessageAlerts.getInstance().showMessage("Alerta de Remoção",
                        "Deseja remover funcionário " + nome + " ?",
                        MessageAlerts.MessageType.WARNING,
                        MessageAlerts.OK_OPTION,
                        (pc, i) -> {
                            if (i == MessageAlerts.OK_OPTION) {
                                funcionarioRepository.removerFuncionario((Long) table.getValueAt(row, 0));
                                ((DefaultTableModel) table.getModel()).removeRow(row);
                            }
                        });
                fireEditingStopped();
            });
        }
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            return button;
        }
        @Override
        public Object getCellEditorValue() {
            return null;
        }
    }
}
