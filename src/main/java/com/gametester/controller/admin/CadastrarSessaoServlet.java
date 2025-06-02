package com.gametester.controller.testador;

import com.gametester.dao.EstrategiaDAO;
import com.gametester.dao.ProjetoDAO;
import com.gametester.dao.SessaoTesteDAO;
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
import java.util.ArrayList;
import java.util.List;

@WebServlet("/testador/cadastrarSessao")
public class CadastrarSessaoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ProjetoDAO projetoDAO;
    private EstrategiaDAO estrategiaDAO;
    private SessaoTesteDAO sessaoTesteDAO;

    @Override
    public void init() {
        projetoDAO = new ProjetoDAO();
        estrategiaDAO = new EstrategiaDAO();
        sessaoTesteDAO = new SessaoTesteDAO();
    }

    // Exibe o formulário de cadastro de sessão
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Usuario usuarioLogado = (session != null) ? (Usuario) session.getAttribute("usuarioLogado") : null;

        // Proteção: Somente Testador ou Administrador podem acessar
        if (usuarioLogado == null ||
                (!"TESTADOR".equals(usuarioLogado.getTipoPerfil()) && !"ADMINISTRADOR".equals(usuarioLogado.getTipoPerfil()))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?erro=" + java.net.URLEncoder.encode("Acesso restrito a testadores e administradores.", "UTF-8"));
            return;
        }

        // Carrega as listas necessárias para o formulário
        recarregarListasFormulario(request, usuarioLogado);

        // Limpa atributos de repopulação de valores de campos específicos do formulário
        request.removeAttribute("valorProjetoId");
        request.removeAttribute("valorEstrategiaId");
        request.removeAttribute("valorTempoSessao");
        request.removeAttribute("valorDescricaoSessao");
        // Limpa mensagens de erro/sucesso de POST anterior, se existirem no escopo do request
        request.removeAttribute("mensagemErroSessao");
        request.removeAttribute("mensagemSucessoSessao");

        request.getRequestDispatcher("/WEB-INF/jsp/testador/cadastrar-sessao.jsp").forward(request, response);
    }

    // Processa o envio do formulário de cadastro de sessão
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Usuario usuarioLogado = (session != null) ? (Usuario) session.getAttribute("usuarioLogado") : null;

        if (usuarioLogado == null ||
                (!"TESTADOR".equals(usuarioLogado.getTipoPerfil()) && !"ADMINISTRADOR".equals(usuarioLogado.getTipoPerfil()))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?erro=" + java.net.URLEncoder.encode("Acesso não autorizado.", "UTF-8"));
            return;
        }

        request.setCharacterEncoding("UTF-8");

        String projetoIdParam = request.getParameter("projetoId");
        String estrategiaIdParam = request.getParameter("estrategiaId");
        String tempoSessaoParam = request.getParameter("tempoSessao");
        String descricao = request.getParameter("descricaoSessao");

        int projetoId = 0;
        int estrategiaId = 0;
        int tempoSessao = 0;

        // Validação e conversão dos parâmetros
        try {
            if (projetoIdParam != null && !projetoIdParam.isEmpty()) projetoId = Integer.parseInt(projetoIdParam);
            if (estrategiaIdParam != null && !estrategiaIdParam.isEmpty()) estrategiaId = Integer.parseInt(estrategiaIdParam);
            if (tempoSessaoParam != null && !tempoSessaoParam.isEmpty()) tempoSessao = Integer.parseInt(tempoSessaoParam);
        } catch (NumberFormatException e) {
            request.setAttribute("mensagemErroSessao", "Valores inválidos para IDs ou tempo da sessão.");
            repopularFormularioSessao(request, projetoIdParam, estrategiaIdParam, tempoSessaoParam, descricao);
            recarregarListasFormulario(request, usuarioLogado);
            request.getRequestDispatcher("/WEB-INF/jsp/testador/cadastrar-sessao.jsp").forward(request, response);
            return;
        }

        if (projetoId == 0 || estrategiaId == 0 || tempoSessao <= 0 || descricao == null || descricao.trim().isEmpty()) {
            request.setAttribute("mensagemErroSessao", "Todos os campos são obrigatórios e o tempo da sessão deve ser positivo.");
            repopularFormularioSessao(request, projetoIdParam, estrategiaIdParam, tempoSessaoParam, descricao);
            recarregarListasFormulario(request, usuarioLogado);
            request.getRequestDispatcher("/WEB-INF/jsp/testador/cadastrar-sessao.jsp").forward(request, response);
            return;
        }

        SessaoTeste novaSessao = new SessaoTeste();
        novaSessao.setProjetoId(projetoId);
        novaSessao.setTestadorId(usuarioLogado.getId());
        novaSessao.setEstrategiaId(estrategiaId);
        novaSessao.setTempoSessaoMinutos(tempoSessao);
        novaSessao.setDescricao(descricao.trim());
        // Status "CRIADO" e dataHoraCriacao são definidos pelo DAO.

        try {
            sessaoTesteDAO.inserirSessaoTeste(novaSessao);
            request.setAttribute("mensagemSucessoSessao", "Sessão de teste '" + novaSessao.getDescricao().substring(0, Math.min(novaSessao.getDescricao().length(), 20)) + "...' cadastrada com sucesso! ID: " + novaSessao.getId());

            // Limpa os valores do formulário para o próximo cadastro
            request.removeAttribute("valorProjetoId");
            request.removeAttribute("valorEstrategiaId");
            request.removeAttribute("valorTempoSessao");
            request.removeAttribute("valorDescricaoSessao");
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("mensagemErroSessao", "Erro ao salvar sessão de teste: " + e.getMessage());
            repopularFormularioSessao(request, projetoIdParam, estrategiaIdParam, tempoSessaoParam, descricao);
        }
        // Recarrega as listas de dropdowns para o JSP em qualquer caso (sucesso ou erro, antes do forward)
        recarregarListasFormulario(request, usuarioLogado);
        request.getRequestDispatcher("/WEB-INF/jsp/testador/cadastrar-sessao.jsp").forward(request, response);
    }

    // Método auxiliar para repopular os campos do formulário com os valores submetidos em caso de erro
    private void repopularFormularioSessao(HttpServletRequest request, String projetoId, String estrategiaId, String tempo, String descricao) {
        request.setAttribute("valorProjetoId", projetoId);
        request.setAttribute("valorEstrategiaId", estrategiaId);
        request.setAttribute("valorTempoSessao", tempo);
        request.setAttribute("valorDescricaoSessao", descricao);
    }

    // Método auxiliar para carregar/recarregar as listas de Projetos e Estratégias para os dropdowns
    private void recarregarListasFormulario(HttpServletRequest request, Usuario usuarioLogado) {
        if (usuarioLogado == null) {
            System.err.println("CadastrarSessaoServlet: Usuário logado é nulo ao tentar recarregar listas.");
            request.setAttribute("mensagemErroSessao", (request.getAttribute("mensagemErroSessao") != null ? request.getAttribute("mensagemErroSessao") + "<br/>" : "") + "Erro: Informações do usuário não encontradas para carregar dados do formulário.");
            return; // Não prossegue se não houver usuário logado
        }
        try {
            // Busca apenas os projetos do usuário logado
            List<Projeto> projetos = projetoDAO.listarProjetosPorMembro(usuarioLogado.getId());
            List<Estrategia> estrategias = estrategiaDAO.listarTodasEstrategias(); // Todas as estratégias são públicas

            request.setAttribute("projetos", projetos);
            request.setAttribute("estrategias", estrategias);
        } catch (SQLException e) {
            e.printStackTrace();
            // Concatena com mensagem de erro existente, se houver
            String erroExistente = (String) request.getAttribute("mensagemErroSessao");
            request.setAttribute("mensagemErroSessao", (erroExistente != null ? erroExistente + "<br/>" : "") + "Erro crítico ao recarregar dados para o formulário: " + e.getMessage());
        }
    }
}