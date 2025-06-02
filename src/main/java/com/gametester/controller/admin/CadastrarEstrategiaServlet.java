package com.gametester.controller.admin; // Ou o seu pacote de controllers

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

@WebServlet("/admin/cadastrarEstrategia") // R5: Mapeamento do servlet [cite: 2]
public class CadastrarEstrategiaServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private EstrategiaDAO estrategiaDAO;

    @Override
    public void init() {
        estrategiaDAO = new EstrategiaDAO();
    }

    // Exibe o formulário de cadastro
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        // Verifica se o usuário é administrador
        if (session == null || session.getAttribute("usuarioLogado") == null ||
                !"ADMINISTRADOR".equals(((Usuario) session.getAttribute("usuarioLogado")).getTipoPerfil())) {
            // Se não for admin, redireciona para login com mensagem de erro
            response.sendRedirect(request.getContextPath() + "/login.jsp?erro=" + java.net.URLEncoder.encode("Acesso restrito a administradores.", "UTF-8"));
            return;
        }
        // Encaminha para o JSP do formulário de cadastro de estratégia
        request.getRequestDispatcher("/WEB-INF/jsp/admin/cadastrar-estrategia.jsp").forward(request, response);
    }

    // Processa o envio do formulário
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Usuario usuarioLogado = (session != null) ? (Usuario) session.getAttribute("usuarioLogado") : null;

        // Proteção: Somente administradores podem cadastrar [cite: 2]
        if (usuarioLogado == null || !"ADMINISTRADOR".equals(usuarioLogado.getTipoPerfil())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?erro=" + java.net.URLEncoder.encode("Acesso não autorizado.", "UTF-8"));
            return;
        }

        request.setCharacterEncoding("UTF-8"); // Define o encoding para tratar caracteres especiais

        // Obtém os parâmetros do formulário
        String nome = request.getParameter("nome");
        String descricao = request.getParameter("descricao");
        String exemplosOriginal = request.getParameter("exemplos"); // Conteúdo original do campo exemplos
        String dicas = request.getParameter("dicas");
        String imagemPath = request.getParameter("imagemPath"); // Caminho da imagem vindo do formulário

        // Validação simples (campos obrigatórios: nome e descrição)
        if (nome == null || nome.trim().isEmpty() || descricao == null || descricao.trim().isEmpty()) {
            request.setAttribute("mensagemErro", "Nome e Descrição são campos obrigatórios.");
            // Repopula os campos para o usuário não perder o que digitou
            request.setAttribute("param", request.getParameterMap()); // Envia todos os parâmetros de volta
            request.getRequestDispatcher("/WEB-INF/jsp/admin/cadastrar-estrategia.jsp").forward(request, response);
            return;
        }

        Estrategia novaEstrategia = new Estrategia();
        novaEstrategia.setNome(nome.trim());
        novaEstrategia.setDescricao(descricao.trim());

        // Adaptação: Incorporar imagemPath no campo 'exemplos'
        // Os campos para estratégia são: nome, descrição, exemplos e dicas [cite: 1]
        // É possível associar imagens a uma estratégia para auxiliar nos exemplos [cite: 1]
        String exemplosFinais = exemplosOriginal != null ? exemplosOriginal.trim() : "";

        if (imagemPath != null && !imagemPath.trim().isEmpty()) {
            // Define uma convenção para como a imagem será referenciada nos exemplos.
            // Exemplo: Adicionar em uma nova linha com um marcador.
            if (!exemplosFinais.isEmpty()) {
                exemplosFinais += "\n"; // Adiciona uma nova linha se já houver exemplos
            }
            exemplosFinais += "[Imagem: " + imagemPath.trim() + "]";
            // Se preferir usar Markdown (assumindo que a visualização possa renderizar):
            // exemplosFinais += "\n![](" + imagemPath.trim() + ")";
        }

        novaEstrategia.setExemplos(exemplosFinais.isEmpty() ? null : exemplosFinais);
        novaEstrategia.setDicas(dicas != null ? dicas.trim() : null);

        // A linha original "novaEstrategia.setImagemPath(...)" é omitida
        // pois a classe Estrategia.java não possui tal método, e a imagem é incorporada nos exemplos.

        try {
            // A chamada abaixo assume que estrategiaDAO.inserirEstrategia agora declara "throws SQLException"
            // e que o ID da estratégia inserida será setado no objeto novaEstrategia dentro do método DAO.
            estrategiaDAO.inserirEstrategia(novaEstrategia);

            // Mensagem de sucesso e limpa os campos para um novo cadastro (ou redireciona)
            // O ID é acessado do objeto novaEstrategia, que deve ter sido atualizado pelo DAO.
            request.setAttribute("mensagemSucesso", "Estratégia '" + novaEstrategia.getNome() + "' cadastrada com sucesso! ID: " + novaEstrategia.getId());

            // Encaminha de volta para o formulário (limpo, para novo cadastro)
            // Para limpar o formulário, não repopule com "param" em caso de sucesso.
            // Se quiser que o formulário seja limpo, basta não setar "param".
            // Se quiser manter os dados para edição ou algo assim, seria outra lógica.
            // Para limpar efetivamente para um novo cadastro, é melhor não reenviar os "param" aqui.
            // Se houver um getParameterMap() no request.setAttribute("param", request.getParameterMap()) da falha,
            // o JSP pode estar pegando isso. Melhor limpar os atributos de erro e param se for sucesso.
            request.removeAttribute("param"); // Garante que dados antigos não sejam repopulados em caso de sucesso
            request.removeAttribute("mensagemErro");

            request.getRequestDispatcher("/WEB-INF/jsp/admin/cadastrar-estrategia.jsp").forward(request, response);
            // Alternativa: redirecionar para a lista de estratégias ou dashboard
            // response.sendRedirect(request.getContextPath() + "/admin/listarEstrategias?sucesso=" + java.net.URLEncoder.encode("Estratégia cadastrada com sucesso!", "UTF-8"));
        } catch (SQLException e) {
            e.printStackTrace(); // Importante para debug no console do servidor
            // Em caso de erro no banco, define mensagem de erro e repopula o formulário
            request.setAttribute("mensagemErro", "Erro ao salvar estratégia no banco de dados: " + e.getMessage());
            request.setAttribute("param", request.getParameterMap()); // Repopula
            request.getRequestDispatcher("/WEB-INF/jsp/admin/cadastrar-estrategia.jsp").forward(request, response);
        }
    }
}