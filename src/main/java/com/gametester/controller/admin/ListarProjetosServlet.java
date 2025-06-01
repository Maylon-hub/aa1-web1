package com.gametester.controller.admin; // Colocando em um subpacote admin

import com.gametester.dao.ProjetoDAO;
import com.gametester.model.Projeto;
import com.gametester.model.Usuario;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@WebServlet("/admin/listarProjetos")
public class ListarProjetosServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ProjetoDAO projetoDAO;

    @Override
    public void init() {
        projetoDAO = new ProjetoDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Usuario usuarioLogado = (session != null) ? (Usuario) session.getAttribute("usuarioLogado") : null;

        // Verificação de autorização (o filtro deve cuidar disso primariamente)
        if (usuarioLogado == null || !"ADMINISTRADOR".equals(usuarioLogado.getTipoPerfil())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=acessoNegado");
            return;
        }

        // Parâmetros de Ordenação
        String ordenarPor = request.getParameter("ordenarPor");
        if (ordenarPor == null || ordenarPor.isEmpty()) {
            ordenarPor = "nome"; // Ordenação padrão
        }
        String ordem = request.getParameter("ordem");
        if (ordem == null || ordem.isEmpty()) {
            ordem = "asc"; // Ordem padrão
        }

        try {
            List<Projeto> listaProjetos = projetoDAO.listarTodosProjetos();

            // Aplicar ordenação
            // O DAO já retorna ordenado por nome ASC por padrão.
            // Se a ordenação pedida for diferente, aplicamos aqui.
            if ("nome".equals(ordenarPor)) {
                listaProjetos.sort(Comparator.comparing(Projeto::getNome, String.CASE_INSENSITIVE_ORDER));
                if ("desc".equalsIgnoreCase(ordem)) {
                    Collections.reverse(listaProjetos);
                }
            } else if ("dataCriacao".equals(ordenarPor)) {
                // Verifica se dataCriacao não é null para evitar NullPointerException
                listaProjetos.sort(Comparator.comparing(Projeto::getDataCriacao, Comparator.nullsLast(Comparator.naturalOrder())));
                if ("desc".equalsIgnoreCase(ordem)) {
                    Collections.reverse(listaProjetos);
                }
            }
            // Adicionar outras lógicas de ordenação se necessário

            request.setAttribute("listaProjetos", listaProjetos);
            request.setAttribute("ordenarPorAtual", ordenarPor);
            request.setAttribute("ordemAtual", ordem);

            // Encaminhar para o JSP
            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/admin/listar-projetos.jsp");
            dispatcher.forward(request, response);

        } catch (Exception e) {
            e.printStackTrace(); // Logar a exceção
            request.setAttribute("mensagemErro", "Erro ao carregar a lista de projetos: " + e.getMessage());
            // Em caso de erro, ainda tentar mostrar a página, mas com a mensagem de erro.
            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/admin/listar-projetos.jsp");
            dispatcher.forward(request, response);
        }
    }
}