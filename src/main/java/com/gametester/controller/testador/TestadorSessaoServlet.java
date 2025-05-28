package com.gametester.controller.testador;

import com.gametester.dao.EstrategiaDAO;
import com.gametester.dao.ProjetoDAO;
import com.gametester.dao.SessaoTesteDAO;
import com.gametester.model.Estrategia;
import com.gametester.model.Projeto;
import com.gametester.model.SessaoTeste;
import com.gametester.model.Usuario;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/testador/sessoes")
public class TestadorSessaoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ProjetoDAO projetoDAO;
    private EstrategiaDAO estrategiaDAO;
    private SessaoTesteDAO sessaoTesteDAO;

    public void init() {
        projetoDAO = new ProjetoDAO();
        estrategiaDAO = new EstrategiaDAO();
        sessaoTesteDAO = new SessaoTesteDAO();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            // Poderia ser uma listagem de sessões (R9), mas R7 foca no cadastro
            action = "novo"; // Ação padrão para cadastro por enquanto
        }

        try {
            switch (action) {
                case "novo":
                    mostrarFormularioNovaSessao(request, response);
                    break;
                // Outras ações como "listarMinhasSessoes" (R9) viriam aqui
                default:
                    // Redirecionar para dashboard do testador ou uma página de erro/padrão
                    response.sendRedirect(request.getContextPath() + "/testador/dashboard.jsp");
                    break;
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        if ("salvar".equals(action)) {
            try {
                salvarNovaSessao(request, response);
            } catch (Exception e) {
                throw new ServletException(e);
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/testador/dashboard.jsp");
        }
    }

    private void mostrarFormularioNovaSessao(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        List<Projeto> listaProjetos = projetoDAO.listarTodosProjetos(); // Ou projetos do testador
        List<Estrategia> listaEstrategias = estrategiaDAO.listarTodasEstrategias();

        request.setAttribute("listaProjetos", listaProjetos);
        request.setAttribute("listaEstrategias", listaEstrategias);
        request.setAttribute("sessaoTeste", new SessaoTeste()); // Para campos do formulário
        request.setAttribute("action", "salvar");

        RequestDispatcher dispatcher = request.getRequestDispatcher("/testador/sessao/formularioSessao.jsp");
        dispatcher.forward(request, response);
    }

    private void salvarNovaSessao(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        HttpSession session = request.getSession(false);
        Usuario testadorLogado = (Usuario) session.getAttribute("usuarioLogado");

        if (testadorLogado == null || !"TESTADOR".equals(testadorLogado.getTipoPerfil())) {
            // Verificação adicional, embora o filtro deva pegar isso.
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=naoAutorizado");
            return;
        }

        try {
            int projetoId = Integer.parseInt(request.getParameter("projetoId"));
            int estrategiaId = Integer.parseInt(request.getParameter("estrategiaId"));
            int tempoSessaoMinutos = Integer.parseInt(request.getParameter("tempoSessaoMinutos"));
            String descricao = request.getParameter("descricao");

            SessaoTeste novaSessao = new SessaoTeste();
            novaSessao.setProjetoId(projetoId);
            novaSessao.setEstrategiaId(estrategiaId);
            novaSessao.setTestadorId(testadorLogado.getId()); // ID do testador logado
            novaSessao.setTempoSessaoMinutos(tempoSessaoMinutos);
            novaSessao.setDescricao(descricao);
            // Status e data_criacao são definidos no DAO

            int idNovaSessao = sessaoTesteDAO.inserirSessaoTeste(novaSessao);

            if (idNovaSessao != -1) {
                session.setAttribute("mensagemSucesso", "Sessão de teste cadastrada com sucesso! ID: " + idNovaSessao);
            } else {
                session.setAttribute("mensagemErro", "Falha ao cadastrar sessão de teste.");
            }
            // Redirecionar para o dashboard do testador ou para uma lista de suas sessões (R9)
            response.sendRedirect(request.getContextPath() + "/testador/dashboard.jsp");

        } catch (NumberFormatException e) {
            request.getSession().setAttribute("mensagemErro", "Erro ao processar dados do formulário (IDs ou tempo inválidos).");
            // Recarregar o formulário com os dados e mensagem de erro
            // (seria ideal repopular os campos, mas por simplicidade redirecionamos)
            mostrarFormularioNovaSessao(request, response); // Tenta recarregar, mas atributos de erro não persistirão via redirect
            // Melhor seria forward com erro, ou redirect para ?action=novo com erro na sessão.
            // Por agora, vamos manter o redirect para dashboard em caso de erro mais grave.
            // E exibir erro na página do formulário se for erro de conversão.
            // response.sendRedirect(request.getContextPath() + "/testador/sessoes?action=novo&error=formatoInvalido");

        } catch (Exception e) {
            request.getSession().setAttribute("mensagemErro", "Ocorreu um erro inesperado: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/testador/dashboard.jsp");
            e.printStackTrace();
        }
    }
}