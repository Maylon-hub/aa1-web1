package com.gametester.controller.admin; // Mantendo o pacote que você usou

import com.gametester.dao.EstrategiaDAO;
import com.gametester.model.Estrategia;
import com.gametester.model.Usuario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/admin/editarEstrategia")
public class EditarEstrategiaServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private EstrategiaDAO estrategiaDAO;

    @Override
    public void init() {
        estrategiaDAO = new EstrategiaDAO();
    }

    // doGet: Carrega os dados da estratégia para o formulário de edição (como você já tinha)
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioLogado") == null ||
                !"ADMINISTRADOR".equals(((Usuario) session.getAttribute("usuarioLogado")).getTipoPerfil())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?erro=" + java.net.URLEncoder.encode("Acesso restrito a administradores.", "UTF-8"));
            return;
        }

        String idParam = request.getParameter("id");
        if (idParam != null && !idParam.isEmpty()) {
            try {
                int estrategiaId = Integer.parseInt(idParam);
                Estrategia estrategiaParaEditar = estrategiaDAO.buscarEstrategiaPorId(estrategiaId);

                if (estrategiaParaEditar != null) {
                    request.setAttribute("estrategia", estrategiaParaEditar);
                    request.getRequestDispatcher("/WEB-INF/jsp/admin/editar-estrategia.jsp").forward(request, response);
                } else {
                    session.setAttribute("mensagemErroGerenciamento", "Estratégia com ID " + estrategiaId + " não encontrada.");
                    response.sendRedirect(request.getContextPath() + "/admin/gerenciarEstrategias");
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
                session.setAttribute("mensagemErroGerenciamento", "ID da estratégia inválido para edição.");
                response.sendRedirect(request.getContextPath() + "/admin/gerenciarEstrategias");
            } catch (SQLException e) {
                e.printStackTrace();
                session.setAttribute("mensagemErroGerenciamento", "Erro ao buscar estratégia para edição: " + e.getMessage());
                response.sendRedirect(request.getContextPath() + "/admin/gerenciarEstrategias");
            }
        } else {
            session.setAttribute("mensagemErroGerenciamento", "ID da estratégia não fornecido para edição.");
            response.sendRedirect(request.getContextPath() + "/admin/gerenciarEstrategias");
        }
    }

    // doPost: Processa os dados do formulário de edição e atualiza a estratégia
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        // Proteção: Somente administradores podem salvar edições
        if (session == null || session.getAttribute("usuarioLogado") == null ||
                !"ADMINISTRADOR".equals(((Usuario) session.getAttribute("usuarioLogado")).getTipoPerfil())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?erro=" + java.net.URLEncoder.encode("Acesso restrito a administradores.", "UTF-8"));
            return;
        }

        request.setCharacterEncoding("UTF-8");

        // Obter os parâmetros do formulário
        String idParam = request.getParameter("id");
        String nome = request.getParameter("nome");
        String descricao = request.getParameter("descricao");
        String exemplos = request.getParameter("exemplos"); // Conteúdo como está no textarea
        String dicas = request.getParameter("dicas");
        // Se você reintroduziu um campo separado para imagemPath na edição no JSP, pegue-o aqui.
        // String imagemPathRequest = request.getParameter("imagemPath");

        int estrategiaId = 0;

        // Validação do ID
        if (idParam == null || idParam.isEmpty()) {
            session.setAttribute("mensagemErroGerenciamento", "ID da estratégia não fornecido para atualização.");
            response.sendRedirect(request.getContextPath() + "/admin/gerenciarEstrategias");
            return;
        }

        try {
            estrategiaId = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            session.setAttribute("mensagemErroGerenciamento", "ID da estratégia inválido para atualização.");
            response.sendRedirect(request.getContextPath() + "/admin/gerenciarEstrategias");
            return;
        }

        // Validação dos campos obrigatórios (nome e descrição)
        if (nome == null || nome.trim().isEmpty() || descricao == null || descricao.trim().isEmpty()) {
            request.setAttribute("mensagemErroForm", "Nome e Descrição são campos obrigatórios.");

            // Recria o objeto Estrategia com os dados submetidos (e o ID) para repopular o formulário
            Estrategia estrategiaComErro = new Estrategia();
            estrategiaComErro.setId(estrategiaId);
            estrategiaComErro.setNome(nome); // Usa os valores como vieram do form, antes do trim
            estrategiaComErro.setDescricao(descricao);
            estrategiaComErro.setExemplos(exemplos);
            estrategiaComErro.setDicas(dicas);
            request.setAttribute("estrategia", estrategiaComErro); // Passa para o JSP de edição

            request.getRequestDispatcher("/WEB-INF/jsp/admin/editar-estrategia.jsp").forward(request, response);
            return;
        }

        // Cria o objeto Estrategia com os dados atualizados
        Estrategia estrategiaAtualizada = new Estrategia();
        estrategiaAtualizada.setId(estrategiaId);
        estrategiaAtualizada.setNome(nome.trim());
        estrategiaAtualizada.setDescricao(descricao.trim());

        // Lógica para exemplos (e imagemPath, se você estiver tratando separadamente na edição)
        // Mantendo a lógica de que 'exemplos' pode conter a referência da imagem, como no cadastro.
        // Se você tivesse um campo imagemPath separado no form de edição, a lógica seria:
        // String exemplosFinais = exemplos != null ? exemplos.trim() : "";
        // if (imagemPathRequest != null && !imagemPathRequest.trim().isEmpty()) {
        //     if (!exemplosFinais.isEmpty()) { exemplosFinais += "\n"; }
        //     exemplosFinais += "[Imagem: " + imagemPathRequest.trim() + "]";
        // }
        // estrategiaAtualizada.setExemplos(exemplosFinais.isEmpty() ? null : exemplosFinais);
        // Por enquanto, simplesmente pegamos o que veio do textarea de exemplos:
        estrategiaAtualizada.setExemplos(exemplos != null ? exemplos.trim() : null);
        estrategiaAtualizada.setDicas(dicas != null ? dicas.trim() : null);

        try {
            boolean sucesso = estrategiaDAO.atualizarEstrategia(estrategiaAtualizada); // Chama o método do DAO
            if (sucesso) {
                session.setAttribute("mensagemSucessoGerenciamento", "Estratégia '" + estrategiaAtualizada.getNome() + "' (ID: " + estrategiaId + ") atualizada com sucesso!");
            } else {
                // Isso pode acontecer se o ID não existir mais no banco no momento do update (improvável se o doGet carregou)
                session.setAttribute("mensagemErroGerenciamento", "Não foi possível atualizar a estratégia com ID " + estrategiaId + ". Verifique se a estratégia ainda existe ou tente novamente.");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Loga o erro no console do servidor
            session.setAttribute("mensagemErroGerenciamento", "Erro ao atualizar estratégia no banco de dados: " + e.getMessage());
        }

        // Redireciona de volta para a página de gerenciamento de estratégias
        response.sendRedirect(request.getContextPath() + "/admin/gerenciarEstrategias");
    }
}