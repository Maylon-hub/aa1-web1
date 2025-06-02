package com.gametester.controller.admin;

import com.gametester.dao.EstrategiaDAO;
import com.gametester.dao.ProjetoDAO;
import com.gametester.dao.SessaoTesteDAO;
import com.gametester.dao.UsuarioDAO;
import com.gametester.model.Estrategia;
import com.gametester.model.Projeto;
import com.gametester.model.SessaoTeste;
import com.gametester.model.Usuario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@WebServlet("/admin/editarSessao")
public class EditarSessaoAdminServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private SessaoTesteDAO sessaoTesteDAO;
    private ProjetoDAO projetoDAO;
    private UsuarioDAO usuarioDAO;
    private EstrategiaDAO estrategiaDAO;

    @Override
    public void init() {
        sessaoTesteDAO = new SessaoTesteDAO();
        projetoDAO = new ProjetoDAO();
        usuarioDAO = new UsuarioDAO();
        estrategiaDAO = new EstrategiaDAO();
    }

    // doGet: Carrega dados da sessão e listas para o formulário de edição (seu código existente)
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioLogado") == null ||
                !"ADMINISTRADOR".equals(((Usuario) session.getAttribute("usuarioLogado")).getTipoPerfil())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?erro=" + java.net.URLEncoder.encode("Acesso restrito.", "UTF-8"));
            return;
        }

        String idParam = request.getParameter("id");
        if (idParam != null && !idParam.isEmpty()) {
            try {
                int sessaoId = Integer.parseInt(idParam);
                SessaoTeste sessaoParaEditar = sessaoTesteDAO.buscarSessaoTestePorId(sessaoId);

                if (sessaoParaEditar != null) {
                    List<Projeto> todosProjetos = projetoDAO.listarTodosProjetos();
                    List<Usuario> todosTestadores = usuarioDAO.listarTodosUsuarios();
                    List<Estrategia> todasEstrategias = estrategiaDAO.listarTodasEstrategias();

                    request.setAttribute("sessaoParaEditar", sessaoParaEditar);
                    request.setAttribute("todosProjetos", todosProjetos);
                    request.setAttribute("todosTestadores", todosTestadores);
                    request.setAttribute("todasEstrategias", todasEstrategias);

                    request.getRequestDispatcher("/WEB-INF/jsp/admin/editar-sessao-admin.jsp").forward(request, response);
                } else {
                    session.setAttribute("mensagemErroSessoesAdmin", "Sessão de teste com ID " + sessaoId + " não encontrada.");
                    response.sendRedirect(request.getContextPath() + "/admin/sessoes");
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
                session.setAttribute("mensagemErroSessoesAdmin", "ID da sessão inválido para edição.");
                response.sendRedirect(request.getContextPath() + "/admin/sessoes");
            } catch (SQLException e) {
                e.printStackTrace();
                session.setAttribute("mensagemErroSessoesAdmin", "Erro ao buscar dados para edição da sessão: " + e.getMessage());
                response.sendRedirect(request.getContextPath() + "/admin/sessoes");
            }
        } else {
            session.setAttribute("mensagemErroSessoesAdmin", "ID da sessão não fornecido para edição.");
            response.sendRedirect(request.getContextPath() + "/admin/sessoes");
        }
    }

    // doPost: Processa os dados do formulário de edição e atualiza a sessão
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Usuario adminLogado = (session != null) ? (Usuario) session.getAttribute("usuarioLogado") : null;

        if (adminLogado == null || !"ADMINISTRADOR".equals(adminLogado.getTipoPerfil())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?erro=" + java.net.URLEncoder.encode("Acesso não autorizado.", "UTF-8"));
            return;
        }

        request.setCharacterEncoding("UTF-8");

        // Obter parâmetros do formulário
        String sessaoIdParam = request.getParameter("sessaoId"); // Nome do campo oculto no formulário
        String projetoIdParam = request.getParameter("projetoId");
        String testadorIdParam = request.getParameter("testadorId");
        String estrategiaIdParam = request.getParameter("estrategiaId");
        String tempoSessaoMinutosParam = request.getParameter("tempoSessaoMinutos");
        String descricaoSessao = request.getParameter("descricaoSessao");
        String statusSessao = request.getParameter("statusSessao");
        String dataHoraInicioStr = request.getParameter("dataHoraInicio");
        String dataHoraFimStr = request.getParameter("dataHoraFim");

        SessaoTeste sessaoParaAtualizar = null; // Para usar no repopulate em caso de erro

        try {
            if (sessaoIdParam == null || sessaoIdParam.isEmpty()) {
                throw new IllegalArgumentException("ID da sessão não pode ser nulo ou vazio para atualização.");
            }
            int sessaoId = Integer.parseInt(sessaoIdParam);

            // Busca a sessão original para obter a data de criação e ter uma base
            sessaoParaAtualizar = sessaoTesteDAO.buscarSessaoTestePorId(sessaoId);
            if (sessaoParaAtualizar == null) {
                throw new IllegalArgumentException("Sessão de teste com ID " + sessaoId + " não encontrada para atualização.");
            }

            // Validações dos campos
            if (projetoIdParam == null || estrategiaIdParam == null || testadorIdParam == null ||
                    tempoSessaoMinutosParam == null || descricaoSessao == null || descricaoSessao.trim().isEmpty() ||
                    statusSessao == null || statusSessao.trim().isEmpty()) {
                throw new IllegalArgumentException("Campos obrigatórios (Projeto, Testador, Estratégia, Tempo, Descrição, Status) devem ser preenchidos.");
            }

            // Atualiza os campos do objeto SessaoTeste existente
            sessaoParaAtualizar.setProjetoId(Integer.parseInt(projetoIdParam));
            sessaoParaAtualizar.setTestadorId(Integer.parseInt(testadorIdParam));
            sessaoParaAtualizar.setEstrategiaId(Integer.parseInt(estrategiaIdParam));
            sessaoParaAtualizar.setTempoSessaoMinutos(Integer.parseInt(tempoSessaoMinutosParam));
            sessaoParaAtualizar.setDescricao(descricaoSessao.trim());
            sessaoParaAtualizar.setStatus(statusSessao);

            // Lógica para Timestamps (dataHoraInicio, dataHoraFim)
            if (dataHoraInicioStr != null && !dataHoraInicioStr.isEmpty()) {
                sessaoParaAtualizar.setDataHoraInicio(Timestamp.valueOf(dataHoraInicioStr.replace("T", " ") + ":00"));
            } else {
                sessaoParaAtualizar.setDataHoraInicio(null);
            }

            if (dataHoraFimStr != null && !dataHoraFimStr.isEmpty()) {
                sessaoParaAtualizar.setDataHoraFim(Timestamp.valueOf(dataHoraFimStr.replace("T", " ") + ":00"));
            } else {
                sessaoParaAtualizar.setDataHoraFim(null);
            }

            // Validações de consistência de status e datas
            if ("CRIADO".equals(statusSessao)) {
                sessaoParaAtualizar.setDataHoraInicio(null); // Se voltou para CRIADO, limpa início e fim
                sessaoParaAtualizar.setDataHoraFim(null);
            } else if ("EM_EXECUCAO".equals(statusSessao)) {
                if (sessaoParaAtualizar.getDataHoraInicio() == null) { // Se está em execução, precisa de data de início
                    sessaoParaAtualizar.setDataHoraInicio(new Timestamp(System.currentTimeMillis())); // Define agora se não houver
                }
                sessaoParaAtualizar.setDataHoraFim(null); // Não pode ter data de fim se está em execução
            } else if ("FINALIZADO".equals(statusSessao)) {
                if (sessaoParaAtualizar.getDataHoraInicio() == null) { // Se finalizado, precisa ter tido um início
                    throw new IllegalArgumentException("Uma sessão FINALIZADA precisa de uma Data/Hora de Início.");
                }
                if (sessaoParaAtualizar.getDataHoraFim() == null) { // Se finalizado, precisa de data de fim
                    sessaoParaAtualizar.setDataHoraFim(new Timestamp(System.currentTimeMillis())); // Define agora se não houver
                }
            }

            if (sessaoParaAtualizar.getDataHoraInicio() != null && sessaoParaAtualizar.getDataHoraFim() != null &&
                    sessaoParaAtualizar.getDataHoraFim().before(sessaoParaAtualizar.getDataHoraInicio())) {
                throw new IllegalArgumentException("A Data/Hora Fim não pode ser anterior à Data/Hora Início.");
            }

            boolean sucesso = sessaoTesteDAO.atualizarSessaoTeste(sessaoParaAtualizar);

            if (sucesso) {
                session.setAttribute("mensagemSucessoSessoesAdmin", "Sessão de teste ID " + sessaoId + " atualizada com sucesso!");
                response.sendRedirect(request.getContextPath() + "/admin/sessoes");
            } else {
                request.setAttribute("mensagemErroFormSessaoAdmin", "Não foi possível atualizar a sessão de teste. Verifique os dados ou se a sessão ainda existe.");
                repopularFormularioParaEdicao(request, sessaoParaAtualizar); // Usa o objeto já modificado
                request.getRequestDispatcher("/WEB-INF/jsp/admin/editar-sessao-admin.jsp").forward(request, response);
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
            request.setAttribute("mensagemErroFormSessaoAdmin", "Erro de formato nos IDs ou Tempo da Sessão. Verifique os valores.");
            repopularFormularioParaEdicao(request, preencherComParametrosRequest(request, sessaoIdParam));
            request.getRequestDispatcher("/WEB-INF/jsp/admin/editar-sessao-admin.jsp").forward(request, response);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            request.setAttribute("mensagemErroFormSessaoAdmin", e.getMessage());
            repopularFormularioParaEdicao(request, preencherComParametrosRequest(request, sessaoIdParam));
            request.getRequestDispatcher("/WEB-INF/jsp/admin/editar-sessao-admin.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("mensagemErroFormSessaoAdmin", "Erro de banco de dados ao atualizar sessão: " + e.getMessage());
            repopularFormularioParaEdicao(request, preencherComParametrosRequest(request, sessaoIdParam));
            request.getRequestDispatcher("/WEB-INF/jsp/admin/editar-sessao-admin.jsp").forward(request, response);
        }
    }

    // Método auxiliar para repopular o formulário em caso de erro, carregando as listas novamente.
    private void repopularFormularioParaEdicao(HttpServletRequest request, SessaoTeste sessaoComDados) {
        try {
            // Se sessaoComDados for null (ex: NumberFormatException no ID antes de buscar), não podemos usá-lo.
            // O JSP já trata sessaoParaEditar nula, mas aqui podemos apenas setar o que temos.
            request.setAttribute("sessaoParaEditar", sessaoComDados);

            // As listas são necessárias para os dropdowns
            List<Projeto> todosProjetos = projetoDAO.listarTodosProjetos();
            List<Usuario> todosTestadores = usuarioDAO.listarTodosUsuarios();
            List<Estrategia> todasEstrategias = estrategiaDAO.listarTodasEstrategias();

            request.setAttribute("todosProjetos", todosProjetos);
            request.setAttribute("todosTestadores", todosTestadores);
            request.setAttribute("todasEstrategias", todasEstrategias);

        } catch (SQLException e_repopulate) {
            e_repopulate.printStackTrace();
            String erroExistente = (String) request.getAttribute("mensagemErroFormSessaoAdmin");
            request.setAttribute("mensagemErroFormSessaoAdmin", (erroExistente != null ? erroExistente + "<br/>" : "") + "Erro crítico ao recarregar dados para o formulário de edição.");
        }
    }

    // Cria um objeto SessaoTeste com os parâmetros da requisição para repopular o form
    // em caso de erro ANTES de buscar a sessão original do banco.
    private SessaoTeste preencherComParametrosRequest(HttpServletRequest request, String sessaoIdParam) {
        SessaoTeste s = new SessaoTeste();
        try {
            if (sessaoIdParam != null && !sessaoIdParam.isEmpty()) s.setId(Integer.parseInt(sessaoIdParam));
            s.setProjetoId(Integer.parseInt(request.getParameter("projetoId")));
            s.setTestadorId(Integer.parseInt(request.getParameter("testadorId")));
            s.setEstrategiaId(Integer.parseInt(request.getParameter("estrategiaId")));
            s.setTempoSessaoMinutos(Integer.parseInt(request.getParameter("tempoSessaoMinutos")));
        } catch (Exception e) { /* Ignora erros de parse, os campos ficarão com default ou o que foi parseado */ }
        s.setDescricao(request.getParameter("descricaoSessao"));
        s.setStatus(request.getParameter("statusSessao"));
        String dataHoraInicioStr = request.getParameter("dataHoraInicio");
        String dataHoraFimStr = request.getParameter("dataHoraFim");
        try {
            if (dataHoraInicioStr != null && !dataHoraInicioStr.isEmpty()) s.setDataHoraInicio(Timestamp.valueOf(dataHoraInicioStr.replace("T", " ") + ":00"));
            if (dataHoraFimStr != null && !dataHoraFimStr.isEmpty()) s.setDataHoraFim(Timestamp.valueOf(dataHoraFimStr.replace("T", " ") + ":00"));
        } catch (Exception e) { /* Ignora erros de parse de data */ }
        return s;
    }
}