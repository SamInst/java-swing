package org.sam.component;

import com.formdev.flatlaf.FlatLightLaf;
import org.sam.calendario.DatePicker;
import org.sam.ferramentas.*;
import org.sam.repository.funcionario.FuncionarioRepository;
import org.sam.repository.funcionario.FuncionarioResponse;
import raven.alerts.MessageAlerts;
import raven.popup.GlassPanePopup;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static org.sam.main.CoresApp.BRANCO;

public class TabelaEstilizada {
    private final FuncionarioRepository funcionarioRepository;

    public TabelaEstilizada(FuncionarioRepository funcionarioRepository) {
        this.funcionarioRepository = funcionarioRepository;
    }

    public static void main(String[] args) throws ParseException {
        SwingUtilities.invokeLater(() -> {
            FlatLightLaf.setup();
            JFrame frame = new JFrame("Tabela Estilizada");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(900, 500);
            GlassPanePopup.install(frame);

            TabelaEstilizada TT = new TabelaEstilizada(new FuncionarioRepository());
            JTable table = TT.criarTabelaFuncionarios();

            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setBorder(new RoundBorder(15));
            scrollPane.setOpaque(false);
            scrollPane.getViewport().setOpaque(false);

//            MaskFormatter dateMask = new MaskFormatter("##/##/####");
//            dateMask.setPlaceholderCharacter('_');

            JFormattedTextField ftfNome = new JFormattedTextField();
            ftfNome.setColumns(10);

            DatePicker datePicker = new DatePicker();
            datePicker.addDateSelectionListener(e -> {});
            datePicker.setDateSelectionAble(d -> !d.isAfter(LocalDate.now()));
            datePicker.now();
            JFormattedTextField ftfData = new JFormattedTextField();
            ftfData.setColumns(10);
            datePicker.setEditor(ftfData);

            JFormattedTextField ftfSalario = new JFormattedTextField();
            Mascara.mascaraValor(ftfSalario);
            ftfSalario.setColumns(8);

            JCheckBox cbStatus = new JCheckBox();
            cbStatus.setBackground(BRANCO);

            JButton btnAdd = new JButton("Adicionar");
            btnAdd.setBackground(Color.WHITE);
            btnAdd.addActionListener(e -> {
                String nome = ftfNome.getText().trim();
                String dataText = ftfData.getText().trim();
                String salText = ftfSalario.getText().trim();
                if (nome.isEmpty() || dataText.contains("_") || salText.isEmpty()) {
                    MessageAlerts.getInstance().showMessage("Erro", "Preencha todos os campos", MessageAlerts.MessageType.ERROR);
                    return;
                }
                Date data = null;
                try { data = new SimpleDateFormat("dd/MM/yyyy").parse(dataText); } catch (ParseException ex) {}
                float salario = 0;
                try { salario = NumberFormat.getNumberInstance(new Locale("pt","BR")).parse(salText).floatValue(); } catch (ParseException ex) {}
                boolean status = cbStatus.isSelected();
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                model.addRow(new Object[]{model.getRowCount()+1, nome, data, salario, status, null});
                ftfNome.setText("");
                ftfData.setText("");
                ftfSalario.setText("");
                cbStatus.setSelected(false);
            });

            JPanel fieldsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
            fieldsPanel.setBackground(BRANCO);
            fieldsPanel.add(new JLabel("Nome:")); fieldsPanel.add(ftfNome);
            fieldsPanel.add(new JLabel("Data Admissão:")); fieldsPanel.add(ftfData);
            fieldsPanel.add(new JLabel("Salário:")); fieldsPanel.add(ftfSalario);
            fieldsPanel.add(new JLabel("Status:")); fieldsPanel.add(cbStatus);

            JPanel formPanel = new JPanel(new BorderLayout(10, 0));
            formPanel.setBackground(BRANCO);
            formPanel.add(fieldsPanel, BorderLayout.CENTER);
            formPanel.add(btnAdd, BorderLayout.EAST);

            JPanel container = new JPanel(new BorderLayout());
            container.setBackground(Color.WHITE);
            container.setBorder(new EmptyBorder(20, 20, 20, 20));
            container.add(formPanel, BorderLayout.NORTH);

            JPanel tableWrapper = new JPanel(new BorderLayout());
            tableWrapper.setOpaque(false);
            tableWrapper.setBorder(new EmptyBorder(20, 0, 0, 0));
            tableWrapper.add(scrollPane, BorderLayout.CENTER);

            container.add(tableWrapper, BorderLayout.CENTER);

            frame.setContentPane(container);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    public JTable criarTabelaFuncionarios() {
        String[] colunas = {"ID", "Nome", "Data Admissão", "Salário", "Status", "Ações"};
        List<FuncionarioResponse> funcionarios = funcionarioRepository.listarFuncionarios();
        DefaultTableModel model = new DefaultTableModel(colunas, 0) {
            @Override public Class<?> getColumnClass(int i) {
                return switch (i) {
                    case 0 -> Long.class;
                    case 2 -> Date.class;
                    case 3 -> Float.class;
                    case 4 -> Boolean.class;
                    default -> Object.class;
                };
            }
            @Override public boolean isCellEditable(int r, int c) {
                return c == 4 || c == 5;
            }
        };
        for (FuncionarioResponse f: funcionarios) {
            Date d = Date.from(f.dataAdmissao().atZone(ZoneId.systemDefault()).toInstant());
            model.addRow(new Object[]{f.id(), f.nome(), d, f.salario(), f.status(), null});
        }
        JTable table = new JTable(model);
        estilizarTabela(table);
        return table;
    }

    private void estilizarTabela(JTable table) {
        Color sep = new Color(230,230,230);
        table.setRowHeight(36);
        table.setFillsViewportHeight(true);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0,0));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(new Color(0xD0EFF));
        table.setSelectionForeground(Color.DARK_GRAY);
        table.setShowHorizontalLines(false);
        table.setShowVerticalLines(false);
        table.setGridColor(Color.WHITE);

        JTableHeader h = table.getTableHeader();
        h.setDefaultRenderer(new DefaultTableCellRenderer(){
            @Override public Component getTableCellRendererComponent(JTable t,Object v,boolean s,boolean f,int r,int c){
                JLabel L=new JLabel(v==null?"":v.toString());
                L.setOpaque(true);L.setBackground(BRANCO);
                switch(c){
                    case 0->{L.setHorizontalAlignment(SwingConstants.LEFT);L.setBorder(BorderFactory.createEmptyBorder(10,5,10,5));}
                    case 1->{L.setHorizontalAlignment(SwingConstants.LEFT);L.setBorder(BorderFactory.createEmptyBorder(10,10,10,5));}
                    default->{L.setHorizontalAlignment(SwingConstants.CENTER);L.setBorder(BorderFactory.createEmptyBorder(10,5,10,5));}
                }
                return L;
            }
        });

        TableCellRenderer lr = new LinhaRenderer();
        for(int i=0;i<table.getColumnCount();i++){
            if(i==5){
                table.getColumnModel().getColumn(i).setCellRenderer(new BotaoRemoverRenderer(sep));
                table.getColumnModel().getColumn(i).setCellEditor(new BotaoRemoverEditor(new JCheckBox(), table, sep));
            } else {
                table.getColumnModel().getColumn(i).setCellRenderer(lr);
            }
        }
    }

    static class LinhaRenderer extends DefaultTableCellRenderer {
        private final Color sep=new Color(230,230,230);
        private final SimpleDateFormat df=new SimpleDateFormat("dd/MM/yyyy HH:mm");
        @Override public Component getTableCellRendererComponent(JTable t,Object v,boolean s,boolean f,int r,int c){
            Color bg=s?t.getSelectionBackground():Color.WHITE;
            if(v instanceof Boolean b){
                JCheckBox cb=new JCheckBox();
                cb.setSelected(b);
                cb.setHorizontalAlignment(SwingConstants.CENTER);
                cb.setOpaque(true);
                cb.setBackground(bg);
                JPanel p=new JPanel(new BorderLayout());
                p.setBackground(bg);
                p.setBorder(BorderFactory.createMatteBorder(0,0,1,0,sep));
                p.add(cb);
                return p;
            }
            String dv;
            if(c==3&&v instanceof Number) dv="R$ "+FormatarFloat.format(((Number)v).floatValue());
            else if(v instanceof Date) dv=df.format((Date)v);
            else dv=v==null?"":v.toString();
            JLabel L=new JLabel(dv);
            L.setOpaque(true);L.setBackground(bg);
            if(c==3)L.setForeground(new Color(0,128,0));else L.setForeground(Color.BLACK);
            L.setBorder(BorderFactory.createMatteBorder(0,0,1,0,sep));
            switch(c){
                case 0->L.setHorizontalAlignment(SwingConstants.LEFT);
                case 1->{
                    L.setHorizontalAlignment(SwingConstants.LEFT);
                    L.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createMatteBorder(0,0,1,0,sep),
                            BorderFactory.createEmptyBorder(0,10,0,0)
                    ));
                }
                case 2,3->L.setHorizontalAlignment(SwingConstants.CENTER);
                default->L.setHorizontalAlignment(SwingConstants.LEFT);
            }
            return L;
        }
    }

    static class BotaoRemoverRenderer extends JButton implements TableCellRenderer {
        BotaoRemoverRenderer(Color sep){
            setIcon(RedimensionarIcone.redimensionarIcone(Icones.remove,15,15));
            setFocusable(false);
            setBorder(BorderFactory.createMatteBorder(0,0,1,0,sep));
        }
        @Override public Component getTableCellRendererComponent(JTable t,Object v,boolean s,boolean f,int r,int c){
            return this;
        }
    }

    class BotaoRemoverEditor extends DefaultCellEditor {
        private final JButton button;
        BotaoRemoverEditor(JCheckBox cb,JTable t,Color sep){
            super(cb);
            button=new JButton();
            button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            button.setIcon(RedimensionarIcone.redimensionarIcone(Icones.remove,15,15));
            button.setFocusable(false);
            button.setToolTipText("Remover funcionário");
            button.setBorder(BorderFactory.createMatteBorder(0,0,1,0,sep));
            button.addActionListener(e->{
                int row=t.getEditingRow();
                Object nome=t.getValueAt(row,1);
                MessageAlerts.getInstance().showMessage("Alerta de Remoção",
                        "Deseja remover funcionário "+nome+" ?",
                        MessageAlerts.MessageType.WARNING,
                        MessageAlerts.OK_OPTION,
                        (pc,i)->{
                            if(i==MessageAlerts.OK_OPTION){
                                funcionarioRepository.removerFuncionario((Long)t.getValueAt(row,0));
                                ((DefaultTableModel)t.getModel()).removeRow(row);
                            }
                        }
                );
                fireEditingStopped();
            });
        }
        @Override public Component getTableCellEditorComponent(JTable t,Object v,boolean s,int r,int c){return button;}
        @Override public Object getCellEditorValue(){return null;}
    }
}
