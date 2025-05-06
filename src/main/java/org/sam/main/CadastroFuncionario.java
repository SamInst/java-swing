package org.sam.main;

import com.formdev.flatlaf.FlatLightLaf;
import org.sam.calendario.DatePicker;
import org.sam.ferramentas.*;
import org.sam.repository.funcionario.FuncionarioRepository;
import org.sam.repository.funcionario.FuncionarioRequest;
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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static org.sam.main.CoresApp.BRANCO;

public class CadastroFuncionario {
    private final FuncionarioRepository repositorioFuncionario;
    private final UsuarioLogado usuarioAtual;

    public CadastroFuncionario(FuncionarioRepository repositorioFuncionario, UsuarioLogado usuarioAtual) {
        this.usuarioAtual = usuarioAtual;
        this.repositorioFuncionario = repositorioFuncionario;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FlatLightLaf.setup();
            CadastroFuncionario app = new CadastroFuncionario(
                    new FuncionarioRepository(),
                    new UsuarioLogado("Usuario teste", "email@email.com"));
            app.iniciarAplicacao();
        });
    }

    public void iniciarAplicacao() {
        JFrame janelaApresentacao = new JFrame("Sistema de Cadastro de Funcionários");
        janelaApresentacao.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        janelaApresentacao.setSize(900, 500);
        GlassPanePopup.install(janelaApresentacao);

        JTable tabelaFuncionarios = criarTabelaFuncionarios();
        JScrollPane painelRolagem = new JScrollPane(tabelaFuncionarios);
        painelRolagem.setBorder(new RoundBorder(15));
        painelRolagem.setOpaque(false);
        painelRolagem.getViewport().setOpaque(false);

        JFormattedTextField campoNome = new JFormattedTextField();
        campoNome.setColumns(10);

        DatePicker seletorData = new DatePicker();
        seletorData.addDateSelectionListener(e -> {});
        seletorData.setDateSelectionAble(data -> !data.isAfter(LocalDate.now()));
        seletorData.now();

        JFormattedTextField campoDataAdmissao = new JFormattedTextField();
        campoDataAdmissao.setColumns(10);
        seletorData.setEditor(campoDataAdmissao);
        seletorData.isCloseAfterSelected();

        JFormattedTextField campoSalario = new JFormattedTextField();
        Mascara.mascaraValor(campoSalario);
        campoSalario.setColumns(8);

        JCheckBox caixaStatus = new JCheckBox();
        caixaStatus.setBackground(BRANCO);

        JButton botaoAdicionar = new JButton("Adicionar");
        botaoAdicionar.setBackground(Color.WHITE);
        botaoAdicionar.addActionListener(e -> {
            String nomeFuncionario = campoNome.getText().trim();
            String textoDataAdmissao = campoDataAdmissao.getText().trim();
            String textoSalario = campoSalario.getText().trim();

            if (nomeFuncionario.isEmpty() || textoDataAdmissao.contains("_") || textoSalario.isEmpty()) {
                MessageAlerts.getInstance().showMessage("Erro", "Preencha todos os campos", MessageAlerts.MessageType.ERROR);
                return;
            }

            LocalDateTime dataAdmissao;
            try {
                Date dataConvertida = new SimpleDateFormat("dd/MM/yyyy").parse(textoDataAdmissao);
                dataAdmissao = dataConvertida.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            } catch (ParseException ex) {
                MessageAlerts.getInstance().showMessage("Erro", "Data inválida", MessageAlerts.MessageType.ERROR);
                return;
            }

            float valorSalario;
            try {
                textoSalario = textoSalario.replace("R$", "").trim();
                valorSalario = NumberFormat.getNumberInstance(new Locale("pt","BR")).parse(textoSalario).floatValue();
            } catch (ParseException ex) {
                MessageAlerts.getInstance().showMessage("Erro", "Salário inválido", MessageAlerts.MessageType.ERROR);
                return;
            }

            boolean estaAtivo = caixaStatus.isSelected();
            FuncionarioRequest requisicaoNovoFuncionario = new FuncionarioRequest(nomeFuncionario, dataAdmissao, valorSalario, estaAtivo);

            try {
                repositorioFuncionario.adicionarFuncionario(requisicaoNovoFuncionario);
                atualizarTabelaFuncionarios(tabelaFuncionarios);
                campoNome.setText("");
                campoDataAdmissao.setText("");
                campoSalario.setText("");
                caixaStatus.setSelected(false);
                MessageAlerts.getInstance().showMessage("Sucesso", "Funcionário adicionado com sucesso", MessageAlerts.MessageType.SUCCESS);
            } catch (Exception ex) {
                MessageAlerts.getInstance().showMessage("Erro", "Falha ao adicionar funcionário: " + ex.getMessage(), MessageAlerts.MessageType.ERROR);
            }
        });

        JPanel painelCampos = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        painelCampos.setBackground(BRANCO);
        painelCampos.add(new JLabel("Nome:"));
        painelCampos.add(campoNome);
        painelCampos.add(new JLabel("Data Admissão:"));
        painelCampos.add(campoDataAdmissao);
        painelCampos.add(new JLabel("Salário:"));
        painelCampos.add(campoSalario);
        painelCampos.add(new JLabel("Status:"));
        painelCampos.add(caixaStatus);

        JPanel painelFormulario = new JPanel(new BorderLayout(5, 0));
        painelFormulario.setBackground(BRANCO);
        painelFormulario.add(painelCampos, BorderLayout.CENTER);
        painelFormulario.add(botaoAdicionar, BorderLayout.EAST);

        JPanel painelEsquerdaCabecalho = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        painelEsquerdaCabecalho.setBackground(BRANCO);
        JLabel iconeFunc = new JLabel(RedimensionarIcone.redimensionarIcone(Icones.funcionario, 24, 24));
        JLabel tituloSistema = new JLabel("Cadastro de Funcionários");
        tituloSistema.setFont(tituloSistema.getFont().deriveFont(Font.PLAIN, 16f));
        painelEsquerdaCabecalho.add(iconeFunc);
        painelEsquerdaCabecalho.add(tituloSistema);

        JPanel painelDireitaCabecalho = new JPanel();
        painelDireitaCabecalho.setLayout(new BoxLayout(painelDireitaCabecalho, BoxLayout.Y_AXIS));
        painelDireitaCabecalho.setBackground(BRANCO);
        painelDireitaCabecalho.setAlignmentY(Component.TOP_ALIGNMENT);
        painelDireitaCabecalho.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        JPanel linhaNomeUsuario = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        linhaNomeUsuario.setBackground(BRANCO);
        linhaNomeUsuario.add(new JLabel(RedimensionarIcone.redimensionarIcone(Icones.user, 16, 16)));
        linhaNomeUsuario.add(new JLabel(usuarioAtual.nome()));
        painelDireitaCabecalho.add(linhaNomeUsuario);

        JPanel linhaEmailUsuario = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        linhaEmailUsuario.setBackground(BRANCO);
        linhaEmailUsuario.add(new JLabel(RedimensionarIcone.redimensionarIcone(Icones.email, 16, 16)));
        linhaEmailUsuario.add(new JLabel(usuarioAtual.email()));
        painelDireitaCabecalho.add(linhaEmailUsuario);

        JPanel painelCabecalho = new JPanel(new BorderLayout());
        painelCabecalho.setBackground(BRANCO);
        painelCabecalho.setBorder(new EmptyBorder(0, 20, 0, 20));
        painelCabecalho.add(painelEsquerdaCabecalho, BorderLayout.WEST);
        painelCabecalho.add(painelDireitaCabecalho, BorderLayout.EAST);

        JPanel painelSuperior = new JPanel();
        painelSuperior.setLayout(new BoxLayout(painelSuperior, BoxLayout.Y_AXIS));
        painelSuperior.setBackground(BRANCO);
        painelSuperior.add(painelCabecalho);
        painelSuperior.add(Box.createVerticalStrut(20));
        painelSuperior.add(painelFormulario);

        JPanel painelPrincipal = new JPanel(new BorderLayout());
        painelPrincipal.setBackground(Color.WHITE);
        painelPrincipal.setBorder(new EmptyBorder(20, 20, 20, 20));
        painelPrincipal.add(painelSuperior, BorderLayout.NORTH);

        JPanel containerTabela = new JPanel(new BorderLayout());
        containerTabela.setOpaque(false);
        containerTabela.setBorder(new EmptyBorder(20, 0, 0, 0));
        containerTabela.add(painelRolagem, BorderLayout.CENTER);

        painelPrincipal.add(containerTabela, BorderLayout.CENTER);

        janelaApresentacao.setContentPane(painelPrincipal);
        janelaApresentacao.setLocationRelativeTo(null);
        janelaApresentacao.setVisible(true);
    }

    public JTable criarTabelaFuncionarios() {
        String[] nomesColunas = {"ID", "Nome", "Data Admissão", "Salário", "Status", "Ações"};
        DefaultTableModel modeloTabela = new DefaultTableModel(nomesColunas, 0) {
            @Override
            public Class<?> getColumnClass(int indiceColunas) {
                return switch (indiceColunas) {
                    case 0 -> Long.class;
                    case 2 -> Date.class;
                    case 3 -> Float.class;
                    case 4 -> Boolean.class;
                    default -> Object.class;
                };
            }

            @Override
            public boolean isCellEditable(int linha, int coluna) {
                return coluna == 4 || coluna == 5;
            }
        };

        JTable tabelaFuncionarios = new JTable(modeloTabela);
        atualizarTabelaFuncionarios(tabelaFuncionarios);
        estilizarTabela(tabelaFuncionarios);
        return tabelaFuncionarios;
    }

    private void estilizarTabela(JTable tabelaFuncionarios) {
        Color corSeparador = new Color(230, 230, 230);
        tabelaFuncionarios.setRowHeight(36);
        tabelaFuncionarios.setFillsViewportHeight(true);
        tabelaFuncionarios.setShowGrid(false);
        tabelaFuncionarios.setIntercellSpacing(new Dimension(0, 0));
        tabelaFuncionarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaFuncionarios.setSelectionBackground(new Color(0xF8F8FA));
        tabelaFuncionarios.setSelectionForeground(Color.DARK_GRAY);
        tabelaFuncionarios.setShowHorizontalLines(false);
        tabelaFuncionarios.setShowVerticalLines(false);
        tabelaFuncionarios.setGridColor(Color.WHITE);

        JTableHeader cabecalhoTabela = tabelaFuncionarios.getTableHeader();
        cabecalhoTabela.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tabela, Object valor, boolean selecionado, boolean temFoco, int linha, int coluna) {
                JLabel rotuloCabecalho = new JLabel(valor == null ? "" : valor.toString());
                rotuloCabecalho.setOpaque(true);
                rotuloCabecalho.setBackground(BRANCO);
                switch (coluna) {
                    case 0 -> {
                        rotuloCabecalho.setHorizontalAlignment(SwingConstants.LEFT);
                        rotuloCabecalho.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 5));
                    }
                    case 1 -> {
                        rotuloCabecalho.setHorizontalAlignment(SwingConstants.LEFT);
                        rotuloCabecalho.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 5));
                    }
                    default -> {
                        rotuloCabecalho.setHorizontalAlignment(SwingConstants.CENTER);
                        rotuloCabecalho.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
                    }
                }
                return rotuloCabecalho;
            }
        });

        TableCellRenderer renderizadorLinha = new RenderizadorLinha();
        for (int i = 0; i < tabelaFuncionarios.getColumnCount(); i++) {
            if (i == 5) {
                tabelaFuncionarios.getColumnModel().getColumn(i).setCellRenderer(new RenderizadorBotaoExcluir(corSeparador));
                tabelaFuncionarios.getColumnModel().getColumn(i).setCellEditor(new EditorBotaoExcluir(new JCheckBox(), tabelaFuncionarios, corSeparador));
            } else {
                tabelaFuncionarios.getColumnModel().getColumn(i).setCellRenderer(renderizadorLinha);
            }
        }
    }

    static class RenderizadorLinha extends DefaultTableCellRenderer {
        private final Color corSeparador = new Color(230, 230, 230);
        private final SimpleDateFormat formatoData = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        @Override
        public Component getTableCellRendererComponent(JTable tabela, Object valor, boolean selecionado, boolean temFoco, int linha, int coluna) {
            Color corFundo = selecionado ? tabela.getSelectionBackground() : Color.WHITE;

            if (valor instanceof Boolean estaAtivo) {
                JCheckBox caixaStatus = new JCheckBox();
                caixaStatus.setSelected(estaAtivo);
                caixaStatus.setHorizontalAlignment(SwingConstants.CENTER);
                caixaStatus.setOpaque(true);
                caixaStatus.setBackground(corFundo);

                JPanel painelCheckbox = new JPanel(new BorderLayout());
                painelCheckbox.setBackground(corFundo);
                painelCheckbox.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, corSeparador));
                painelCheckbox.add(caixaStatus);
                return painelCheckbox;
            }

            String valorExibicao;
            if (coluna == 3 && valor instanceof Number) {
                valorExibicao = "R$ " + FormatarFloat.format(((Number) valor).floatValue());
            } else if (valor instanceof Date) {
                valorExibicao = formatoData.format((Date) valor);
            } else {
                valorExibicao = valor == null ? "" : valor.toString();
            }

            JLabel rotuloValor = new JLabel(valorExibicao);
            rotuloValor.setOpaque(true);
            rotuloValor.setBackground(corFundo);

            if (coluna == 3) {
                rotuloValor.setForeground(new Color(0, 128, 0));
            } else {
                rotuloValor.setForeground(Color.BLACK);
            }

            rotuloValor.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, corSeparador));

            switch (coluna) {
                case 0 -> {
                    rotuloValor.setHorizontalAlignment(SwingConstants.LEFT);
                    rotuloValor.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createMatteBorder(0, 0, 1, 0, corSeparador),
                            BorderFactory.createEmptyBorder(0, 20, 0, 0)
                    ));
                }
                case 1 -> {
                    rotuloValor.setHorizontalAlignment(SwingConstants.LEFT);
                    rotuloValor.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createMatteBorder(0, 0, 1, 0, corSeparador),
                            BorderFactory.createEmptyBorder(0, 10, 0, 0)
                    ));
                }
                case 2, 3 -> rotuloValor.setHorizontalAlignment(SwingConstants.CENTER);
                default -> rotuloValor.setHorizontalAlignment(SwingConstants.LEFT);
            }

            return rotuloValor;
        }
    }

    static class RenderizadorBotaoExcluir extends JButton implements TableCellRenderer {
        RenderizadorBotaoExcluir(Color corSeparador) {
            setIcon(RedimensionarIcone.redimensionarIcone(Icones.remove, 15, 15));
            setFocusable(false);
            setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, corSeparador));
        }

        @Override
        public Component getTableCellRendererComponent(JTable tabela, Object valor, boolean selecionado, boolean temFoco, int linha, int coluna) {
            return this;
        }
    }

    class EditorBotaoExcluir extends DefaultCellEditor {
        private final JButton botaoExcluir;

        EditorBotaoExcluir(JCheckBox caixaSelecao, JTable tabela, Color corSeparador) {
            super(caixaSelecao);
            botaoExcluir = new JButton();
            botaoExcluir.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            botaoExcluir.setIcon(RedimensionarIcone.redimensionarIcone(Icones.remove, 15, 15));
            botaoExcluir.setFocusable(false);
            botaoExcluir.setToolTipText("Excluir funcionário");
            botaoExcluir.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, corSeparador));
            botaoExcluir.addActionListener(e -> {
                int linhaSelecionada = tabela.getEditingRow();
                Object nomeFuncionario = tabela.getValueAt(linhaSelecionada, 1);
                MessageAlerts.getInstance().showMessage("Confirmação de Exclusão",
                        "Tem certeza que deseja excluir o funcionário " + nomeFuncionario + "?",
                        MessageAlerts.MessageType.WARNING,
                        MessageAlerts.OK_OPTION,
                        (pc, opcao) -> {
                            if (opcao == MessageAlerts.OK_OPTION) {
                                repositorioFuncionario.removerFuncionario((Long) tabela.getValueAt(linhaSelecionada, 0));
                                ((DefaultTableModel) tabela.getModel()).removeRow(linhaSelecionada);
                            }
                        }
                );
                fireEditingStopped();
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable tabela, Object valor, boolean selecionado, int linha, int coluna) {
            return botaoExcluir;
        }

        @Override
        public Object getCellEditorValue() {
            return null;
        }
    }

    private void atualizarTabelaFuncionarios(JTable tabelaFuncionarios) {
        DefaultTableModel modeloTabela = (DefaultTableModel) tabelaFuncionarios.getModel();
        modeloTabela.setRowCount(0);

        List<FuncionarioResponse> funcionarios = repositorioFuncionario.listarFuncionarios();
        for (FuncionarioResponse funcionario : funcionarios) {
            Date dataAdmissao = Date.from(funcionario.dataAdmissao().atZone(ZoneId.systemDefault()).toInstant());
            modeloTabela.addRow(new Object[]{
                    funcionario.id(),
                    funcionario.nome(),
                    dataAdmissao,
                    funcionario.salario(),
                    funcionario.status(),
                    null
            });
        }
    }
}